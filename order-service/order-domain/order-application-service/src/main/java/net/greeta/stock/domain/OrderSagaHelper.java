package net.greeta.stock.domain;

import net.greeta.stock.common.domain.valueobject.OrderId;
import net.greeta.stock.common.domain.valueobject.OrderStatus;
import net.greeta.stock.domain.entity.Order;
import net.greeta.stock.domain.exception.OrderNotFoundException;
import net.greeta.stock.domain.ports.output.repository.OrderRepository;
import net.greeta.stock.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    Order findOrder(String orderId) {
        Optional<Order> orderResponse = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderResponse.isEmpty()) {
            log.error("Order with id: {} could not be found!", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " could not be found!");
        }
        return orderResponse.get();
    }

    void saveOrder(Order order) {
        orderRepository.save(order);
    }

    SagaStatus orderStatusToSagaStatus(OrderStatus orderStatus) {
        switch (orderStatus) {
            case PAID:
                return SagaStatus.SUCCEEDED;
            case CANCELLED:
                return SagaStatus.FAILED;
            default:
                return SagaStatus.STARTED;
        }
    }
}
