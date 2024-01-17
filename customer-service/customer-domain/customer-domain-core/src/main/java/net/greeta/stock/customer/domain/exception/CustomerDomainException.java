package net.greeta.stock.customer.domain.exception;

import net.greeta.stock.common.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {

    public CustomerDomainException(String message) {
        super(message);
    }
}
