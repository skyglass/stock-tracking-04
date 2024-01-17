package net.greeta.stock.domain.ports.output.message.publisher.restaurantapproval;

import net.greeta.stock.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import net.greeta.stock.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface RestaurantApprovalRequestMessagePublisher {

    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);
}
