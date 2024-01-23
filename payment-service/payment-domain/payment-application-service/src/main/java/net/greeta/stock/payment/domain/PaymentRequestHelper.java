package net.greeta.stock.payment.domain;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.valueobject.CustomerId;
import net.greeta.stock.common.domain.valueobject.PaymentStatus;
import net.greeta.stock.outbox.OutboxStatus;
import net.greeta.stock.payment.domain.dto.PaymentRequest;
import net.greeta.stock.payment.domain.entity.CreditEntry;
import net.greeta.stock.payment.domain.entity.CreditHistory;
import net.greeta.stock.payment.domain.entity.Payment;
import net.greeta.stock.payment.domain.event.PaymentEvent;
import net.greeta.stock.payment.domain.exception.PaymentApplicationServiceException;
import net.greeta.stock.payment.domain.exception.PaymentNotFoundException;
import net.greeta.stock.payment.domain.mapper.PaymentDataMapper;
import net.greeta.stock.payment.domain.outbox.model.OrderOutboxMessage;
import net.greeta.stock.payment.domain.outbox.scheduler.OrderOutboxHelper;
import net.greeta.stock.payment.domain.ports.output.repository.CreditEntryRepository;
import net.greeta.stock.payment.domain.ports.output.repository.CreditHistoryRepository;
import net.greeta.stock.payment.domain.ports.output.repository.PaymentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderOutboxHelper orderOutboxHelper;

    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                OrderOutboxHelper orderOutboxHelper) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Transactional
    public boolean persistPayment(PaymentRequest paymentRequest) {
        if (isOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info("An outbox message with saga id: {} is already saved to database!",
                    paymentRequest.getSagaId());
            return true;
        }

        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent =
                paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages);

        return persistIfSucceeded(paymentRequest, payment, creditEntry, creditHistories, failureMessages, paymentEvent);
    }

    @Transactional
    public boolean persistCancelPayment(PaymentRequest paymentRequest) {
        if (isOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.FAILED)) {
            log.info("An outbox message with saga id: {} is already saved to database!",
                    paymentRequest.getSagaId());
            return true;
        }

        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        payment.initializePayment();
        payment.updateStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentToOrderEventPayload(payment),
                payment.getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));

        return true;

    }

    private boolean persistIfSucceeded(PaymentRequest paymentRequest, Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages, PaymentEvent paymentEvent) {

        if (!failureMessages.isEmpty()) {
            String message = StringUtils.join(failureMessages, "; ");
            log.error("Failure message: {}", message);
            throw new PaymentApplicationServiceException(message);
        }

        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));

        return true;
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " +
                    customerId.getValue());
        }
        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()) {
            log.error("Could not find credit history for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit history for customer: " +
                    customerId.getValue());
        }
        return creditHistories.get();
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private boolean isOutboxMessageProcessedForPayment(PaymentRequest paymentRequest,
                                                       PaymentStatus paymentStatus) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                        UUID.fromString(paymentRequest.getSagaId()),
                        paymentStatus);
        if (orderOutboxMessage.isPresent()) {
            return true;
        }
        return false;
    }

}
