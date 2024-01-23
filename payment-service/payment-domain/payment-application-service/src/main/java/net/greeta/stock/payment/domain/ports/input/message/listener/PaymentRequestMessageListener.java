package net.greeta.stock.payment.domain.ports.input.message.listener;

import net.greeta.stock.payment.domain.dto.PaymentRequest;

public interface PaymentRequestMessageListener {

    void completePayment(PaymentRequest paymentRequest);

    void cancelPayment(PaymentRequest paymentRequest);
}
