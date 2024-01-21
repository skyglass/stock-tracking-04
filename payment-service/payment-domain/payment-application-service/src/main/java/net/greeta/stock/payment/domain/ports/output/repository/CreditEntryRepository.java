package net.greeta.stock.payment.domain.ports.output.repository;

import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.payment.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);

    void detach(CustomerId customerId);
}