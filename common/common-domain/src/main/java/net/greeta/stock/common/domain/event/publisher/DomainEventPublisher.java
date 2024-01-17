package net.greeta.stock.common.domain.event.publisher;

import net.greeta.stock.common.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
