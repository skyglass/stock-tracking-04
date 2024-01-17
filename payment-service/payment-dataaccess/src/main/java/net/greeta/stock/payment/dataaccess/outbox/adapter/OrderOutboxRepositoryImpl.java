package net.greeta.stock.payment.dataaccess.outbox.adapter;

import net.greeta.stock.common.domain.valueobject.PaymentStatus;
import net.greeta.stock.outbox.OutboxStatus;
import net.greeta.stock.payment.dataaccess.outbox.exception.OrderOutboxNotFoundException;
import net.greeta.stock.payment.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import net.greeta.stock.payment.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import net.greeta.stock.payment.domain.outbox.model.OrderOutboxMessage;
import net.greeta.stock.payment.domain.ports.output.repository.OrderOutboxRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    public OrderOutboxRepositoryImpl(OrderOutboxJpaRepository orderOutboxJpaRepository,
                                     OrderOutboxDataAccessMapper orderOutboxDataAccessMapper) {
        this.orderOutboxJpaRepository = orderOutboxJpaRepository;
        this.orderOutboxDataAccessMapper = orderOutboxDataAccessMapper;
    }

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderPaymentOutboxMessage) {
        return orderOutboxDataAccessMapper
                .orderOutboxEntityToOrderOutboxMessage(orderOutboxJpaRepository
                        .save(orderOutboxDataAccessMapper
                                .orderOutboxMessageToOutboxEntity(orderPaymentOutboxMessage)));
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        return Optional.of(orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus)
                .orElseThrow(() -> new OrderOutboxNotFoundException("Outbox object " + outboxStatus.toString() +
                        " cannot be found for saga type " + sagaType))
                .stream()
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String sagaType,
                                                                            UUID sagaId,
                                                                            PaymentStatus paymentStatus,
                                                                            OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(sagaType, sagaId,
                        paymentStatus, outboxStatus)
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus);
    }
}
