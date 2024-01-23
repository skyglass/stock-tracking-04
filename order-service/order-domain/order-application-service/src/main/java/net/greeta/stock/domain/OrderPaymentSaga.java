package net.greeta.stock.domain;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.valueobject.OrderId;
import net.greeta.stock.common.domain.valueobject.OrderStatus;
import net.greeta.stock.common.domain.valueobject.PaymentStatus;
import net.greeta.stock.domain.dto.message.PaymentResponse;
import net.greeta.stock.domain.entity.Order;
import net.greeta.stock.domain.event.OrderPaidEvent;
import net.greeta.stock.domain.exception.OrderNotFoundException;
import net.greeta.stock.domain.mapper.OrderDataMapper;
import net.greeta.stock.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import net.greeta.stock.domain.outbox.scheduler.payment.PaymentDepositOutboxHelper;
import net.greeta.stock.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import net.greeta.stock.domain.ports.output.repository.OrderRepository;
import net.greeta.stock.saga.SagaStatus;
import net.greeta.stock.saga.SagaStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static net.greeta.stock.common.domain.DomainConstants.UTC;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentDepositOutboxHelper paymentDepositOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            OrderRepository orderRepository,
                            PaymentOutboxHelper paymentOutboxHelper,
                            PaymentDepositOutboxHelper paymentDepositOutboxHelper,
                            OrderSagaHelper orderSagaHelper,
                            OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.paymentDepositOutboxHelper = paymentDepositOutboxHelper;
        this.orderSagaHelper = orderSagaHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                isDepositPayment(paymentResponse)
                ?
                paymentDepositOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        SagaStatus.STARTED)
                :
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        SagaStatus.STARTED);

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        OrderPaidEvent domainEvent = completePaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        if (isDepositPayment(paymentResponse)) {
            paymentDepositOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                    domainEvent.getOrder().getOrderStatus(), sagaStatus));
        } else {
            paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                    domainEvent.getOrder().getOrderStatus(), sagaStatus));
        }

        log.info("Order with id: {} is paid", domainEvent.getOrder().getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                isDepositPayment(paymentResponse) ?
                        paymentDepositOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                                UUID.fromString(paymentResponse.getSagaId()),
                                getCurrentSagaStatus(paymentResponse.getPaymentStatus()))
                        :
                        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        getCurrentSagaStatus(paymentResponse.getPaymentStatus()));

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already roll backed!", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        Order order = rollbackPaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        if (isDepositPayment(paymentResponse)) {
            paymentDepositOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                    order.getOrderStatus(), sagaStatus));
        } else {
            paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                    order.getOrderStatus(), sagaStatus));
        }

        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private Order findOrder(String orderId) {
        Optional<Order> orderResponse = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderResponse.isEmpty()) {
            log.error("Order with id: {} could not be found!", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " could not be found!");
        }
        return orderResponse.get();
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage
                                                                             orderPaymentOutboxMessage,
                                                                     OrderStatus
                                                                             orderStatus,
                                                                     SagaStatus
                                                                             sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = findOrder(paymentResponse.getOrderId());
        OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
        orderRepository.save(order);
        return domainEvent;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[] { SagaStatus.STARTED };
            case FAILED -> new SagaStatus[] { SagaStatus.STARTED };
        };
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderRepository.save(order);
        return order;
    }

    private boolean isDepositPayment(PaymentResponse paymentResponse) {
        return paymentResponse.getPrice().signum() < 0;
    }
}
