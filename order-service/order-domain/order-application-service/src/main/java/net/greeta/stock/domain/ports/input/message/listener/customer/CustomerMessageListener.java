package net.greeta.stock.domain.ports.input.message.listener.customer;

import net.greeta.stock.common.messaging.dto.CustomerModel;

public interface CustomerMessageListener {

    void customerCreated(CustomerModel customerModel);
}
