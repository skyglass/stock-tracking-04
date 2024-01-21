package net.greeta.stock;

import lombok.SneakyThrows;
import net.greeta.stock.order.OrderTestDataService;
import net.greeta.stock.customer.CustomerTestDataService;
import net.greeta.stock.payment.PaymentTestDataService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class E2eTest {

    @Autowired
    private OrderTestDataService orderTestDataService;

    @Autowired
    private CustomerTestDataService customerTestDataService;

    @Autowired
    private PaymentTestDataService paymentTestDataService;

    @BeforeEach
    @SneakyThrows
    void cleanup() {
        customerTestDataService.resetDatabase();
        orderTestDataService.resetDatabase();
        paymentTestDataService.resetDatabase();
    }
}
