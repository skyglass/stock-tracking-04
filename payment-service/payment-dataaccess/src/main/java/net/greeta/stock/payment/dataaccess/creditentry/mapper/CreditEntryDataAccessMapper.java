package net.greeta.stock.payment.dataaccess.creditentry.mapper;

import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.common.domain.valueobject.Money;
import net.greeta.stock.payment.dataaccess.creditentry.entity.CreditEntryEntity;
import net.greeta.stock.payment.domain.entity.CreditEntry;
import net.greeta.stock.payment.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder()
                .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .version(creditEntryEntity.getVersion())
                .build();
    }

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .version(creditEntry.getVersion())
                .build();
    }

}

