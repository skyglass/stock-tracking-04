package net.greeta.stock.payment.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.kafka.consumer.KafkaConsumer;
import net.greeta.stock.kafka.order.avro.model.CustomerAvroModel;
import net.greeta.stock.payment.domain.ports.input.message.listener.customer.CustomerPaymentMessageListener;
import net.greeta.stock.payment.messaging.mapper.PaymentMessagingDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CustomerPaymentKafkaListener implements KafkaConsumer<CustomerAvroModel> {

    private final CustomerPaymentMessageListener customerMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public CustomerPaymentKafkaListener(CustomerPaymentMessageListener customerMessageListener,
                                        PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.customerMessageListener = customerMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.customer-group-id}", topics = "${payment-service.customer-topic-name}")
    public void receive(@Payload List<CustomerAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of customer create messages received with keys {}, partitions {} and offsets {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(customerAvroModel ->
                customerMessageListener.customerCreated(paymentMessagingDataMapper
                        .customerAvroModeltoCustomerModel(customerAvroModel)));
    }
}
