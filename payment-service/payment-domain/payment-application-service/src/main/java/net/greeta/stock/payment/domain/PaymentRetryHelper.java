package net.greeta.stock.payment.domain;

import lombok.RequiredArgsConstructor;
import net.greeta.stock.payment.domain.dto.PaymentRequest;
import net.greeta.stock.payment.domain.exception.CreditHistoryInvalidStateException;
import net.greeta.stock.payment.domain.exception.PaymentNotEnoughCreditException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRetryHelper {

    private final PaymentRequestHelper paymentRequestHelper;

    @Retryable(retryFor = {DataAccessException.class,
            PaymentNotEnoughCreditException.class,
            CreditHistoryInvalidStateException.class},
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public boolean persistPayment(PaymentRequest paymentRequest) {
        return paymentRequestHelper.persistPayment(paymentRequest);
    }

    @Retryable(retryFor = {DataAccessException.class},
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public boolean persistCancelPayment(PaymentRequest paymentRequest) {
        return paymentRequestHelper.persistCancelPayment(paymentRequest);
    }

}
