package net.greeta.stock.payment.domain;

import net.greeta.stock.payment.domain.entity.CreditEntry;
import net.greeta.stock.payment.domain.entity.Payment;
import net.greeta.stock.payment.domain.event.PaymentEvent;
import net.greeta.stock.payment.domain.entity.CreditHistory;
import net.greeta.stock.payment.domain.exception.PaymentNotEnoughCreditException;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages) throws PaymentNotEnoughCreditException;
}
