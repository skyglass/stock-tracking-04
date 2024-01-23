package net.greeta.stock.payment.domain.mapper;

import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.common.domain.valueobject.Money;
import net.greeta.stock.common.domain.valueobject.OrderId;
import net.greeta.stock.common.messaging.dto.CustomerModel;
import net.greeta.stock.payment.domain.entity.CreditEntry;
import net.greeta.stock.payment.domain.entity.Payment;
import net.greeta.stock.payment.domain.event.PaymentEvent;
import net.greeta.stock.payment.domain.dto.PaymentRequest;
import net.greeta.stock.common.domain.event.payload.PaymentOrderEventPayload;
import net.greeta.stock.payment.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }

    public PaymentOrderEventPayload paymentEventToOrderEventPayload(PaymentEvent paymentEvent) {
        return PaymentOrderEventPayload.builder()
                .paymentId(paymentEvent.getPayment().getId().getValue().toString())
                .customerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
                .orderId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .price(paymentEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentEvent.getCreatedAt())
                .paymentStatus(paymentEvent.getPayment().getPaymentStatus().name())
                .failureMessages(paymentEvent.getFailureMessages())
                .build();
    }

    public PaymentOrderEventPayload paymentToOrderEventPayload(Payment payment) {
        return PaymentOrderEventPayload.builder()
                .paymentId(payment.getId().getValue().toString())
                .customerId(payment.getCustomerId().getValue().toString())
                .orderId(payment.getOrderId().getValue().toString())
                .price(payment.getPrice().getAmount())
                .createdAt(payment.getCreatedAt())
                .paymentStatus(payment.getPaymentStatus().name())
                .failureMessages(new ArrayList<>())
                .build();
    }

    public CreditEntry customerModelToCreditEntry(CustomerModel customerModel) {
        return CreditEntry.builder()
                .creditEntryId(new CreditEntryId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.fromString(customerModel.getId())))
                .totalCreditAmount(new Money(BigDecimal.ZERO))
                .build();
    }
}
