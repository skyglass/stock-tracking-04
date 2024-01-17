package net.greeta.stock.customer.domain.ports.output.message.publisher;

import net.greeta.stock.customer.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);

}