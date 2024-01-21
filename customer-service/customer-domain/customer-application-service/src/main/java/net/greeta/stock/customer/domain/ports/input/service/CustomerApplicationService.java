package net.greeta.stock.customer.domain.ports.input.service;

import net.greeta.stock.common.domain.dto.CreateCustomerCommand;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;

import jakarta.validation.Valid;

public interface CustomerApplicationService {

    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);

}
