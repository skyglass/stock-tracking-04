package net.greeta.stock.payment.domain.exception;

import net.greeta.stock.common.domain.exception.DomainException;

public class CreditHistoryInvalidStateException extends DomainException {

    public CreditHistoryInvalidStateException(String message) {
        super(message);
    }
}
