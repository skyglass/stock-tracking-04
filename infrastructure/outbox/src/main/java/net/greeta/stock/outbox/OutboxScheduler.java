package net.greeta.stock.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
