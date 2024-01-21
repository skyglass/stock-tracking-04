package net.greeta.stock.customer;

import net.greeta.stock.common.domain.dto.CreateCustomerCommand;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer")
public interface CustomerClient {

    @PostMapping("/")
    public CreateCustomerResponse create(@RequestBody CreateCustomerCommand customer);
}