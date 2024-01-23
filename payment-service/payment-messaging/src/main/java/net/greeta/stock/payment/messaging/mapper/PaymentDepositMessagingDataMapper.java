package net.greeta.stock.payment.messaging.mapper;

import debezium.order.payment_deposit_outbox.Value;
import net.greeta.stock.common.domain.event.payload.OrderPaymentEventPayload;
import net.greeta.stock.common.domain.valueobject.PaymentOrderStatus;
import net.greeta.stock.payment.domain.dto.PaymentRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentDepositMessagingDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(OrderPaymentEventPayload orderPaymentEventPayload,
                                                                  Value paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(orderPaymentEventPayload.getCustomerId())
                .orderId(orderPaymentEventPayload.getOrderId())
                .price(orderPaymentEventPayload.getPrice())
                .createdAt(Instant.parse(paymentRequestAvroModel.getCreatedAt()))
                .paymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .build();
    }
}
