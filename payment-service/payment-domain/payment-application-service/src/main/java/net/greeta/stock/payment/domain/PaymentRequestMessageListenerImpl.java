package net.greeta.stock.payment.domain;

import net.greeta.stock.payment.domain.dto.PaymentRequest;
import net.greeta.stock.payment.domain.exception.PaymentApplicationServiceException;
import net.greeta.stock.payment.domain.exception.PaymentNotEnoughCreditException;
import net.greeta.stock.payment.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private static final String CANCEL_PAYMENT_METHOD = "cancelPayment";

    private final PaymentRetryHelper paymentRetryHelper;

    public PaymentRequestMessageListenerImpl(PaymentRetryHelper paymentRetryHelper) {
        this.paymentRetryHelper = paymentRetryHelper;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        processPayment(paymentRetryHelper::persistPayment, paymentRequest, "completePayment");
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        processPayment(paymentRetryHelper::persistCancelPayment, paymentRequest, CANCEL_PAYMENT_METHOD);
    }

    private void processPayment(Function<PaymentRequest, Boolean> func, PaymentRequest paymentRequest, String methodName) {
        log.info("Executing {} operation for order id {}", methodName, paymentRequest.getOrderId());
        try {
            func.apply(paymentRequest);
        } catch (Exception e) {
            log.error("Caught exception: {} {}", e.getMessage(), e.getClass());
            if (CANCEL_PAYMENT_METHOD.equals(methodName)) {
                throw new PaymentApplicationServiceException("Could not complete " + methodName
                        + " operation. Throwing exception!: " + e.getClass() + ": " + e.getMessage());
            }
            log.info("Cancelling payment for order id: {}", paymentRequest.getOrderId());
            cancelPayment(paymentRequest);
        }
    }

}
