package net.greeta.stock.domain;

import net.greeta.stock.domain.entity.Order;
import net.greeta.stock.domain.event.OrderCancelledEvent;
import net.greeta.stock.domain.event.OrderCreatedEvent;
import net.greeta.stock.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order);

    OrderPaidEvent payOrder(Order order);

    void cancelOrder(Order order, List<String> failureMessages);
}
