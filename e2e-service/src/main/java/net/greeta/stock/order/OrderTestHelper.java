package net.greeta.stock.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.CreateOrderCommand;
import net.greeta.stock.common.domain.dto.CreateOrderResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderTestHelper {

    private final OrderClient orderClient;

    private final Order2Client order2Client;

    private final Order3Client order3Client;

    @Async
    public CompletableFuture<CreateOrderResponse> createAsyncOrder(String customerId, BigDecimal amount, int hash) {
        log.info("Creating order with amount {} for customer {}", amount, customerId);
        CreateOrderCommand order = new CreateOrderCommand(UUID.fromString(customerId), amount);
        return CompletableFuture.completedFuture(createOrder(order, hash));
    }

    @Async
    public CompletableFuture<CreateOrderResponse> createAsyncDeposit(String customerId, BigDecimal amount, int hash) {
        log.info("Creating deposit order with amount {} for customer {}", amount, customerId);
        CreateOrderCommand order = new CreateOrderCommand(UUID.fromString(customerId), amount);
        return CompletableFuture.completedFuture(createDepositOrder(order, hash));
    }

    public CreateOrderResponse createOrder(String customerId, BigDecimal amount) {
        log.info("Creating order with amount {} for customer {}", amount, customerId);
        CreateOrderCommand order = new CreateOrderCommand(UUID.fromString(customerId), amount);
        CreateOrderResponse orderSummary = orderClient.createOrder(order);
        return orderSummary;
    }

    private CreateOrderResponse createOrder(CreateOrderCommand order, int hash) {
        if (hash % 3 == 0) {
            return orderClient.createOrder(order);
        } else if (hash % 3 == 1) {
            return order2Client.createOrder(order);
        } else {
            return order3Client.createOrder(order);
        }
    }

    private CreateOrderResponse createDepositOrder(CreateOrderCommand order, int hash) {
        if (hash % 3 == 0) {
            return orderClient.depositOrder(order);
        } else if (hash % 3 == 1) {
            return order2Client.depositOrder(order);
        } else {
            return order3Client.depositOrder(order);
        }
    }

}
