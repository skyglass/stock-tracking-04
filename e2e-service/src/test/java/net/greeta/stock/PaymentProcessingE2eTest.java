package net.greeta.stock;

import net.greeta.stock.common.domain.dto.CreateCustomerCommand;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;
import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.common.domain.valueobject.OrderStatus;
import net.greeta.stock.customer.CustomerClient;
import net.greeta.stock.customer.CustomerTestHelper;
import net.greeta.stock.helper.CalculationHelper;
import net.greeta.stock.helper.RetryHelper;
import net.greeta.stock.order.OrderClient;
import net.greeta.stock.payment.PaymentClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = {
        "classpath:application.yml"
})
public class PaymentProcessingE2eTest extends E2eTest {

    @Autowired
    private CustomerTestHelper customerTestHelper;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private PaymentClient paymentClient;


    @Test
    void createOrderThenCustomerAccountBalanceReducedTest() {
        CreateCustomerResponse customer = customerTestHelper.createCustomerWithBalance("test", BigDecimal.valueOf(5.0));
        String customerId = customer.getCustomerId().toString();

        CreateOrderCommand order = new CreateOrderCommand(customer.getCustomerId(), BigDecimal.valueOf(2.0));
        CreateOrderResponse orderSummary = orderClient.createOrder(order);
        assertNotNull(orderSummary.getOrderTrackingId());
        assertEquals(OrderStatus.PENDING, orderSummary.getOrderStatus());

        Boolean orderPaid =  RetryHelper.retry(() -> {
            var test = orderClient.getOrderByTrackingId(orderSummary.getOrderTrackingId());
            return test.getOrderStatus() == OrderStatus.PAID;
        });
        assertTrue(orderPaid);

        Boolean balanceReducedTo3 = RetryHelper.retry(() -> {
            var test = paymentClient.getCustomerAccount(customerId);
            return CalculationHelper.equalsToScale2(test.getBalance(), BigDecimal.valueOf(3.0));
        });
        assertTrue(balanceReducedTo3);
    }


}
