package net.greeta.stock.payment.dataaccess.creditentry.adapter;

import jakarta.persistence.EntityManager;
import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.payment.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import net.greeta.stock.payment.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import net.greeta.stock.payment.domain.entity.CreditEntry;
import net.greeta.stock.payment.domain.ports.output.repository.CreditEntryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    private final EntityManager entityManager;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     CreditEntryDataAccessMapper creditEntryDataAccessMapper,
                                     EntityManager entityManager) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
        this.entityManager = entityManager;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper
                .creditEntryEntityToCreditEntry(creditEntryJpaRepository
                        .save(creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository
                .findByCustomerId(customerId.getValue())
                .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
    }

    @Override
    public void detach(CustomerId customerId) {
        entityManager.detach(creditEntryJpaRepository.findByCustomerId(customerId.getValue()).orElseThrow());
    }
}
