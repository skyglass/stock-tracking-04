package net.greeta.stock.domain.ports.output.repository;

import net.greeta.stock.common.domain.valueobject.OrderId;
import net.greeta.stock.domain.entity.Order;
import net.greeta.stock.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    Optional<Order> findByTrackingId(TrackingId trackingId);
}
