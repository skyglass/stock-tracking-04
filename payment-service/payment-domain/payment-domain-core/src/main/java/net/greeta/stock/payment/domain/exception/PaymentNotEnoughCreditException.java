package net.greeta.stock.payment.domain.exception;

public class PaymentNotEnoughCreditException extends IllegalArgumentException {

    public PaymentNotEnoughCreditException(String customerId) {
        super(String.format("Insufficient credit amount in account for customer %s", customerId));
    }
}
