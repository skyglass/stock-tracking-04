package net.greeta.stock.customer.domain;

import net.greeta.stock.customer.domain.entity.Customer;
import net.greeta.stock.customer.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

    CustomerCreatedEvent validateAndInitiateCustomer(Customer customer);

}
