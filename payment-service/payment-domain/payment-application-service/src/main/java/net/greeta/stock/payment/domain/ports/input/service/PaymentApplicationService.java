package net.greeta.stock.payment.domain.ports.input.service;

import jakarta.validation.Valid;
import net.greeta.stock.common.domain.dto.CreateCustomerCommand;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;
import net.greeta.stock.common.domain.dto.CustomerAccountDto;

public interface PaymentApplicationService {

    CustomerAccountDto getCustomerAccount(String customerId);

}
