package net.greeta.stock.payment.domain.ports.output.message.publisher;

import net.greeta.stock.outbox.OutboxStatus;
import net.greeta.stock.payment.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponseMessagePublisher {
    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
