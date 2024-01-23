package net.greeta.stock.dataaccess.outbox.payment.adapter;

import net.greeta.stock.dataaccess.outbox.payment.exception.PaymentOutboxNotFoundException;
import net.greeta.stock.dataaccess.outbox.payment.mapper.PaymentDepositOutboxDataAccessMapper;
import net.greeta.stock.dataaccess.outbox.payment.mapper.PaymentOutboxDataAccessMapper;
import net.greeta.stock.dataaccess.outbox.payment.repository.PaymentDepositOutboxJpaRepository;
import net.greeta.stock.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import net.greeta.stock.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import net.greeta.stock.domain.ports.output.repository.PaymentDepositOutboxRepository;
import net.greeta.stock.domain.ports.output.repository.PaymentOutboxRepository;
import net.greeta.stock.outbox.OutboxStatus;
import net.greeta.stock.saga.SagaStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PaymentDepositOutboxRepositoryImpl implements PaymentDepositOutboxRepository {

    private final PaymentDepositOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentDepositOutboxDataAccessMapper paymentOutboxDataAccessMapper;

    public PaymentDepositOutboxRepositoryImpl(PaymentDepositOutboxJpaRepository paymentOutboxJpaRepository,
                                       PaymentDepositOutboxDataAccessMapper paymentOutboxDataAccessMapper) {
        this.paymentOutboxJpaRepository = paymentOutboxJpaRepository;
        this.paymentOutboxDataAccessMapper = paymentOutboxDataAccessMapper;
    }

    @Override
    public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
        return paymentOutboxDataAccessMapper
                .paymentOutboxEntityToOrderPaymentOutboxMessage(paymentOutboxJpaRepository
                        .save(paymentOutboxDataAccessMapper
                                .orderPaymentOutboxMessageToOutboxEntity(orderPaymentOutboxMessage)));
    }

    @Override
    public Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType,
                                                                                            OutboxStatus outboxStatus,
                                                                                            SagaStatus... sagaStatus) {
        return Optional.of(paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType,
                        outboxStatus,
                        Arrays.asList(sagaStatus))
                .orElseThrow(() -> new PaymentOutboxNotFoundException("Payment outbox object " +
                        "could not be found for saga type " + sagaType))
                .stream()
                .map(paymentOutboxDataAccessMapper::paymentOutboxEntityToOrderPaymentOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                                UUID sagaId,
                                                                                SagaStatus... sagaStatus) {
        return paymentOutboxJpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
                .map(paymentOutboxDataAccessMapper::paymentOutboxEntityToOrderPaymentOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                Arrays.asList(sagaStatus));
    }
}
