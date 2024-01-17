package net.greeta.stock.domain.ports.input.message.listener.customer;

import net.greeta.stock.domain.dto.message.CustomerModel;

public interface CustomerMessageListener {

    void customerCreated(CustomerModel customerModel);
}
