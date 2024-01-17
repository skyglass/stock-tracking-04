package net.greeta.stock.domain.ports.input.service;

import net.greeta.stock.domain.dto.create.CreateOrderCommand;
import net.greeta.stock.domain.dto.create.CreateOrderResponse;
import net.greeta.stock.domain.dto.track.TrackOrderQuery;
import net.greeta.stock.domain.dto.track.TrackOrderResponse;

import jakarta.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
