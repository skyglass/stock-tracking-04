package net.greeta.stock.payment.domain;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.messaging.dto.CustomerModel;
import net.greeta.stock.payment.domain.entity.CreditEntry;
import net.greeta.stock.payment.domain.exception.PaymentDomainException;
import net.greeta.stock.payment.domain.mapper.PaymentDataMapper;
import net.greeta.stock.payment.domain.ports.input.message.listener.customer.CustomerPaymentMessageListener;
import net.greeta.stock.payment.domain.ports.output.repository.CreditEntryRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerPaymentMessageListenerImpl implements CustomerPaymentMessageListener {

    private final CreditEntryRepository creditEntryRepository;
    private final PaymentDataMapper paymentDataMapper;

    public CustomerPaymentMessageListenerImpl(CreditEntryRepository creditEntryRepository, PaymentDataMapper paymentDataMapper) {
        this.creditEntryRepository = creditEntryRepository;
        this.paymentDataMapper = paymentDataMapper;
    }

    @Override
    public void customerCreated(CustomerModel customerModel) {
        CreditEntry creditEntry = creditEntryRepository.save(paymentDataMapper.customerModelToCreditEntry(customerModel));
        if (creditEntry == null) {
            log.error("CreditEntry could not be created in payment database with id: {}", creditEntry.getId());
            throw new PaymentDomainException("CreditEntry could not be created in payment database with id " +
                    creditEntry.getId());
        }
        log.info("CreditEntry is created in payment database with id: {}", creditEntry.getId());
    }
}
