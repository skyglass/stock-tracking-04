package net.greeta.stock.service.application.rest;

import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.domain.dto.track.TrackOrderQuery;
import net.greeta.stock.common.domain.dto.TrackOrderResponse;
import net.greeta.stock.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {} with amount: {}", createOrderCommand.getCustomerId(),
                createOrderCommand.getAmount().toString());
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order created with tracking id: {}", createOrderResponse.getOrderTrackingId());
        return ResponseEntity.ok(createOrderResponse);
    }

    @PostMapping("/deposit")
    public ResponseEntity<CreateOrderResponse> depositOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating deposit order for customer: {} with amount: {}", createOrderCommand.getCustomerId(),
                createOrderCommand.getAmount().toString());
        CreateOrderResponse createOrderResponse = orderApplicationService.depositOrder(createOrderCommand);
        log.info("Order created with tracking id: {}", createOrderResponse.getOrderTrackingId());
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId) {
       TrackOrderResponse trackOrderResponse =
               orderApplicationService.trackOrder(TrackOrderQuery.builder().orderTrackingId(trackingId).build());
       log.info("Returning order status with tracking id: {}", trackOrderResponse.getOrderTrackingId());
       return  ResponseEntity.ok(trackOrderResponse);
    }
}
