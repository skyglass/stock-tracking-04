package net.greeta.stock.dataaccess.outbox.payment.mapper;

import net.greeta.stock.dataaccess.outbox.payment.entity.PaymentDepositOutboxEntity;
import net.greeta.stock.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import net.greeta.stock.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class PaymentDepositOutboxDataAccessMapper {

    public PaymentDepositOutboxEntity orderPaymentOutboxMessageToOutboxEntity(OrderPaymentOutboxMessage
                                                                               orderPaymentOutboxMessage) {
        return PaymentDepositOutboxEntity.builder()
                .id(orderPaymentOutboxMessage.getId())
                .sagaId(orderPaymentOutboxMessage.getSagaId())
                .createdAt(orderPaymentOutboxMessage.getCreatedAt())
                .type(orderPaymentOutboxMessage.getType())
                .payload(orderPaymentOutboxMessage.getPayload())
                .orderStatus(orderPaymentOutboxMessage.getOrderStatus())
                .sagaStatus(orderPaymentOutboxMessage.getSagaStatus())
                .outboxStatus(orderPaymentOutboxMessage.getOutboxStatus())
                .version(orderPaymentOutboxMessage.getVersion())
                .build();
    }

    public OrderPaymentOutboxMessage paymentOutboxEntityToOrderPaymentOutboxMessage(PaymentDepositOutboxEntity
                                                                                            paymentOutboxEntity) {
        return OrderPaymentOutboxMessage.builder()
                .id(paymentOutboxEntity.getId())
                .sagaId(paymentOutboxEntity.getSagaId())
                .createdAt(paymentOutboxEntity.getCreatedAt())
                .type(paymentOutboxEntity.getType())
                .payload(paymentOutboxEntity.getPayload())
                .orderStatus(paymentOutboxEntity.getOrderStatus())
                .sagaStatus(paymentOutboxEntity.getSagaStatus())
                .outboxStatus(paymentOutboxEntity.getOutboxStatus())
                .version(paymentOutboxEntity.getVersion())
                .build();
    }

}
