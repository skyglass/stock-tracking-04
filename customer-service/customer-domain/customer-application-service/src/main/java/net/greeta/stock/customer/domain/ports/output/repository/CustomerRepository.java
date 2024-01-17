package net.greeta.stock.customer.domain.ports.output.repository;

import net.greeta.stock.customer.domain.entity.Customer;

public interface CustomerRepository {

    Customer createCustomer(Customer customer);
}
