package net.greeta.stock.domain.ports.input.service;

import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import net.greeta.stock.domain.dto.track.TrackOrderQuery;
import net.greeta.stock.common.domain.dto.TrackOrderResponse;

import jakarta.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    CreateOrderResponse depositOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
