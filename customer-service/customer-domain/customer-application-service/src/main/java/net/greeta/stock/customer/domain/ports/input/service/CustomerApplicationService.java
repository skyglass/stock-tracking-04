package net.greeta.stock.customer.domain.ports.input.service;

import net.greeta.stock.customer.domain.create.CreateCustomerCommand;
import net.greeta.stock.customer.domain.create.CreateCustomerResponse;

import jakarta.validation.Valid;

public interface CustomerApplicationService {

    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);

}
