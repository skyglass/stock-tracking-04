package net.greeta.stock.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.common.domain.valueobject.OrderId;
import net.greeta.stock.common.domain.valueobject.OrderStatus;
import net.greeta.stock.common.domain.valueobject.PaymentOrderStatus;
import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.domain.entity.Customer;
import net.greeta.stock.domain.entity.Order;
import net.greeta.stock.domain.exception.OrderDomainException;
import net.greeta.stock.domain.mapper.OrderDataMapper;
import net.greeta.stock.common.domain.event.payload.OrderPaymentEventPayload;
import net.greeta.stock.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import net.greeta.stock.domain.ports.input.service.OrderApplicationService;
import net.greeta.stock.domain.ports.output.repository.CustomerRepository;
import net.greeta.stock.domain.ports.output.repository.OrderRepository;
import net.greeta.stock.domain.ports.output.repository.PaymentOutboxRepository;
import net.greeta.stock.outbox.OutboxStatus;
import net.greeta.stock.saga.SagaStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static net.greeta.stock.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .amount(PRICE)
                .build();


        Customer customer = new Customer(new CustomerId(CUSTOMER_ID));

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class))).thenReturn(getOrderPaymentOutboxMessage());
    }

    @Test
    public void testCreateOrder() {
       CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
       assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
       assertEquals("Order created successfully", createOrderResponse.getMessage());
       assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    private OrderPaymentOutboxMessage getOrderPaymentOutboxMessage() {
        OrderPaymentEventPayload orderPaymentEventPayload = OrderPaymentEventPayload.builder()
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(ZonedDateTime.now())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();

        return OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(SAGA_ID)
                .createdAt(ZonedDateTime.now())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderPaymentEventPayload))
                .orderStatus(OrderStatus.PENDING)
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .version(0)
                .build();
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload);
        } catch (JsonProcessingException e) {
            throw new OrderDomainException("Cannot create OrderPaymentEventPayload object!");
        }
    }

}
