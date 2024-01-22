package net.greeta.stock.payment.domain.ports.input.message.listener.customer;

import net.greeta.stock.common.messaging.dto.CustomerModel;

public interface CustomerPaymentMessageListener {

    void customerCreated(CustomerModel customerModel);
}
