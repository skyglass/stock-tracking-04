package net.greeta.stock.payment.domain;

import net.greeta.stock.payment.domain.dto.PaymentRequest;
import net.greeta.stock.payment.domain.exception.PaymentApplicationServiceException;
import net.greeta.stock.payment.domain.exception.PaymentNotEnoughCreditException;
import net.greeta.stock.payment.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;

    private static final int MAX_EXECUTION = 20;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper) {
        this.paymentRequestHelper = paymentRequestHelper;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        processPayment(paymentRequestHelper::persistPayment, paymentRequest, "completePayment");
    }

    private void processPayment(Function<PaymentRequest, Boolean> func, PaymentRequest paymentRequest, String methodName) {
        int execution = 1;
        boolean result;
        do {
            log.info("Executing {} operation for {} time for order id {}", methodName, execution, paymentRequest.getOrderId());
            try {
                result = func.apply(paymentRequest);
                execution++;
            } catch (ConcurrencyFailureException | PaymentNotEnoughCreditException e) {
                String exceptionName = e instanceof  OptimisticLockingFailureException ? "OptimisticLockingFailureException" : "PaymentNotEnoughCreditException";
                log.error("Caught {} exception in {} with message {}!. Retrying for order id {}!",
                        exceptionName, methodName, e.getMessage(), paymentRequest.getOrderId());
                result = false;
            } catch (Exception e) {
                log.error("Caught exception: {} {}", e.getMessage(), e.getClass());
                throw e;
            }
        } while(!result && execution < MAX_EXECUTION);

        if (!result) {
            throw new PaymentApplicationServiceException("Could not complete " + methodName + " operation. Throwing exception!");
        }
    }

}
