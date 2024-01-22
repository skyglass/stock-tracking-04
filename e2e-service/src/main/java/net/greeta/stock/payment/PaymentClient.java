package net.greeta.stock.payment;

import net.greeta.stock.common.domain.dto.CustomerAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment")
public interface PaymentClient {

    @GetMapping("/{customerId}")
    public CustomerAccountDto getCustomerAccount(@PathVariable String customerId);

}