package net.greeta.stock.payment;

import net.greeta.stock.common.domain.dto.CustomerAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment2")
public interface Payment2Client {

    @GetMapping("/{customerId}")
    public CustomerAccountDto getCustomerAccount(@PathVariable String customerId);
}
