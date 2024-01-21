package net.greeta.stock.order;

import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.common.domain.dto.TrackOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "order")
public interface OrderClient {

    @PostMapping("/")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderCommand order);

    @PostMapping("/deposit")
    public CreateOrderResponse depositOrder(@RequestBody CreateOrderCommand order);

    @GetMapping("/{trackingId}")
    public TrackOrderResponse getOrderByTrackingId(@PathVariable UUID trackingId);
}
