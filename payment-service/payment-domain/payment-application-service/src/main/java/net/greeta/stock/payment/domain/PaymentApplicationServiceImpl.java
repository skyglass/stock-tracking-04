package net.greeta.stock.payment.domain;


import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.CustomerAccountDto;
import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.payment.domain.ports.input.service.PaymentApplicationService;
import net.greeta.stock.payment.domain.ports.output.repository.CreditEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Slf4j
@Validated
@Service
class PaymentApplicationServiceImpl implements PaymentApplicationService {

    @Autowired
    private CreditEntryRepository creditEntryRepository;

    @Override
    public CustomerAccountDto getCustomerAccount(String customerId) {
        var creditEntry = creditEntryRepository.findByCustomerId(
                new CustomerId(UUID.fromString(customerId))).orElse(null);
        if (creditEntry == null) {
            throw new IllegalArgumentException(String.format("Credit Entry for customer id = %s not found", customerId));
        }
        return new CustomerAccountDto(customerId, creditEntry.getTotalCreditAmount().getAmount());
    }
}
