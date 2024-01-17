package net.greeta.stock.payment.domain.valueobject;

import net.greeta.stock.common.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
    public CreditEntryId(UUID value) {
        super(value);
    }
}
