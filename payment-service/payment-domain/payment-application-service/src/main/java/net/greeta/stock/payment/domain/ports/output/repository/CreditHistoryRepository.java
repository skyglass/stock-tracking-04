package net.greeta.stock.payment.domain.ports.output.repository;

import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.payment.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
