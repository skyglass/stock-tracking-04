package net.greeta.stock.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.CreateCustomerCommand;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;
import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.common.domain.valueobject.OrderStatus;
import net.greeta.stock.customer.CustomerClient;
import net.greeta.stock.helper.CalculationHelper;
import net.greeta.stock.helper.RetryHelper;
import net.greeta.stock.order.OrderClient;
import net.greeta.stock.payment.PaymentClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerTestHelper {

    private final OrderClient orderClient;

    private final CustomerClient customerClient;

    private final PaymentClient paymentClient;

    public CreateCustomerResponse createCustomerWithBalance(String username, BigDecimal balance) {
        CreateCustomerCommand createCustomer = new CreateCustomerCommand(UUID.randomUUID(), username, username, username);
        CreateCustomerResponse customer = customerClient.create(createCustomer);
        assertNotNull(customer);
        assertNotNull(customer.getCustomerId());
        String customerId = customer.getCustomerId().toString();

        Boolean checkZeroBalance = RetryHelper.retry(() -> {
            var test = paymentClient.getCustomerAccount(customerId);
            return CalculationHelper.equals(test.getBalance(), BigDecimal.ZERO);
        });
        assertTrue(checkZeroBalance);

        CreateOrderCommand deposit = new CreateOrderCommand(customer.getCustomerId(), balance);
        final CreateOrderResponse depositSummary = orderClient.depositOrder(deposit);
        assertNotNull(depositSummary.getOrderTrackingId());
        assertEquals(OrderStatus.PENDING, depositSummary.getOrderStatus());

        Boolean depositPaid =  RetryHelper.retry(() -> {
            var test = orderClient.getOrderByTrackingId(depositSummary.getOrderTrackingId());
            return test.getOrderStatus() == OrderStatus.PAID;
        });
        assertTrue(depositPaid);

        Boolean checkBalance = RetryHelper.retry(() -> {
            var test = paymentClient.getCustomerAccount(customerId);
            return CalculationHelper.equals(test.getBalance(), balance);
        });
        assertTrue(checkBalance);

        return customer;
    }

}
