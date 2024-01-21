package net.greeta.stock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.common.domain.valueobject.OrderStatus;
import net.greeta.stock.customer.CustomerTestHelper;
import net.greeta.stock.helper.CalculationHelper;
import net.greeta.stock.helper.RetryHelper;
import net.greeta.stock.order.OrderClient;
import net.greeta.stock.order.OrderTestHelper;
import net.greeta.stock.payment.Payment2Client;
import net.greeta.stock.payment.PaymentClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = {
        "classpath:application.yml"
})
@Slf4j
public class PaymentProcessingConcurrencyE2eTest extends E2eTest {

    @Autowired
    private CustomerTestHelper customerTestHelper;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private Payment2Client payment2Client;

    @Autowired
    private OrderTestHelper orderTestHelper;

    @Test
    @SneakyThrows
    void createParallelOrdersThenStockIsZeroTest() {
        CreateCustomerResponse customer = customerTestHelper.createCustomerWithBalance("test", BigDecimal.valueOf(20.0));
        String customerId = customer.getCustomerId().toString();

        // Start the clock
        long start = Instant.now().toEpochMilli();

        int numberOfOrders = 15;
        List<CompletableFuture<CreateOrderResponse>> createdOrders = new ArrayList<>();
        // Kick of multiple, asynchronous lookups
        for (int i = 0; i < numberOfOrders; i++) {
            CompletableFuture<CreateOrderResponse> orderSummary = orderTestHelper.createAsyncOrder(customerId, BigDecimal.valueOf(2.0), i);
            createdOrders.add(orderSummary);
        }

        int numberOfDepositUpdates = 5;
        List<CompletableFuture<CreateOrderResponse>> addedDeposits = new ArrayList<>();
        // Kick of multiple, asynchronous lookups
        for (int i = 0; i < numberOfDepositUpdates; i++) {
            CompletableFuture<CreateOrderResponse> addDepositResult = orderTestHelper.createAsyncDeposit(customerId, BigDecimal.valueOf(2.0), i);
            addedDeposits.add(addDepositResult);
        }

        // Wait until they are all done
        CompletableFuture.allOf(createdOrders.toArray(new CompletableFuture[0])).join();
        CompletableFuture.allOf(addedDeposits.toArray(new CompletableFuture[0])).join();

        for (CompletableFuture<CreateOrderResponse> orderFuture: createdOrders) {
            CreateOrderResponse order = orderFuture.get();
            assertNotNull(order.getOrderTrackingId());
            assertEquals(OrderStatus.PENDING, order.getOrderStatus());
            log.info("--> " + order.getOrderTrackingId());
            Boolean orderPaid =  RetryHelper.retry(() -> {
                var test = orderClient.getOrderByTrackingId(order.getOrderTrackingId());
                return test.getOrderStatus() == OrderStatus.PAID;
            });
            assertTrue(orderPaid);
        }

        for (CompletableFuture<CreateOrderResponse> addDepositResultFuture: addedDeposits) {
            CreateOrderResponse addDepositResult = addDepositResultFuture.get();
            assertNotNull(addDepositResult);
            assertEquals(OrderStatus.PENDING, addDepositResult.getOrderStatus());
            log.info("--> " + addDepositResult.getOrderTrackingId());
            Boolean depositPaid =  RetryHelper.retry(() -> {
                var test = orderClient.getOrderByTrackingId(addDepositResult.getOrderTrackingId());
                return test.getOrderStatus() == OrderStatus.PAID;
            });
            assertTrue(depositPaid);
            log.info("Add Deposit --> " + addDepositResult.getOrderTrackingId());
        }

        log.info("Elapsed time: " + (Instant.now().toEpochMilli() - start));

        AtomicInteger paymentClientHash = new AtomicInteger(0);
        Boolean balanceReducedToZero = RetryHelper.retry(() -> {
            var test = paymentClientHash.getAndIncrement() % 2 == 0
                    ? paymentClient.getCustomerAccount(customerId)
                    : payment2Client.getCustomerAccount(customerId);
            return CalculationHelper.isEqualToZero(test.getBalance());
        });
        assertTrue(balanceReducedToZero);

        //Check that the next order is rejected because the stock is zero
        CreateOrderResponse rejectedOrder = orderTestHelper.createOrder(customerId, BigDecimal.valueOf(1.0));
        assertNotNull(rejectedOrder.getOrderTrackingId());
        assertEquals(OrderStatus.PENDING, rejectedOrder.getOrderStatus());
        Boolean orderRejected =  RetryHelper.retry(() -> {
            var test = orderClient.getOrderByTrackingId(rejectedOrder.getOrderTrackingId());
            return test.getOrderStatus() == OrderStatus.CANCELLED;
        });
        assertTrue(orderRejected);
    }


}

