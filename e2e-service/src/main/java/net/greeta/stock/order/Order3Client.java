package net.greeta.stock.order;

import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order3")
public interface Order3Client {

    @PostMapping("/")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderCommand order);

    @PostMapping("/deposit")
    public CreateOrderResponse depositOrder(@RequestBody CreateOrderCommand order);
}
