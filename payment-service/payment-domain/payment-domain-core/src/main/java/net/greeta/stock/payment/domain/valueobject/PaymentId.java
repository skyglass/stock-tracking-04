package net.greeta.stock.payment.domain.valueobject;

import net.greeta.stock.common.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
