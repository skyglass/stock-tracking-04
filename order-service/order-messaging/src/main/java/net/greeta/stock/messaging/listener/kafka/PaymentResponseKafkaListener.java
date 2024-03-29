package net.greeta.stock.messaging.listener.kafka;

import net.greeta.stock.common.messaging.DebeziumOp;
import debezium.payment.order_outbox.Envelope;
import debezium.payment.order_outbox.Value;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.event.payload.PaymentOrderEventPayload;
import net.greeta.stock.domain.exception.OrderNotFoundException;
import net.greeta.stock.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import net.greeta.stock.kafka.consumer.KafkaConsumer;
import net.greeta.stock.kafka.order.avro.model.PaymentStatus;
import net.greeta.stock.kafka.producer.KafkaMessageHelper;
import net.greeta.stock.messaging.mapper.OrderMessagingDataMapper;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<Envelope> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                        OrderMessagingDataMapper orderMessagingDataMapper,
                                        KafkaMessageHelper kafkaMessageHelper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<Envelope> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received!",
                messages.stream().filter(message -> message.getBefore() == null &&
                        DebeziumOp.CREATE.getValue().equals(message.getOp())).toList().size());

        messages.forEach(avroModel -> {
            if (avroModel.getBefore() == null && DebeziumOp.CREATE.getValue().equals(avroModel.getOp())) {
                log.info("Incoming message in PaymentResponseKafkaListener: {}", avroModel);
                Value paymentResponseAvroModel = avroModel.getAfter();
                PaymentOrderEventPayload paymentOrderEventPayload =
                        kafkaMessageHelper.getOrderEventPayload(paymentResponseAvroModel.getPayload(), PaymentOrderEventPayload.class);
                try {
                    if (PaymentStatus.COMPLETED.name().equals(paymentOrderEventPayload.getPaymentStatus())) {
                        log.info("Processing successful payment for order id: {}", paymentOrderEventPayload.getOrderId());
                        paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper
                                .paymentResponseAvroModelToPaymentResponse(paymentOrderEventPayload, paymentResponseAvroModel));
                    } else if (PaymentStatus.FAILED.name().equals(paymentOrderEventPayload.getPaymentStatus())) {
                        log.info("Processing unsuccessful payment for order id: {}", paymentOrderEventPayload.getOrderId());
                        paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper
                                .paymentResponseAvroModelToPaymentResponse(paymentOrderEventPayload, paymentResponseAvroModel));
                    }
                } catch (OptimisticLockingFailureException e) {
                    //NO-OP for optimistic lock. This means another thread finished the work, do not throw error to prevent reading the data from kafka again!
                    log.error("Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                            paymentOrderEventPayload.getOrderId());
                } catch (OrderNotFoundException e) {
                    //NO-OP for OrderNotFoundException
                    log.error("No order found for order id: {}", paymentOrderEventPayload.getOrderId());
                } catch (DataAccessException e) {
                    SQLException sqlException = (SQLException) e.getRootCause();
                    if (sqlException != null && sqlException.getSQLState() != null &&
                            PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                        //NO-OP for unique constraint exception
                        log.error("Caught unique constraint exception with sql state: {} " +
                                        "in PaymentResponseKafkaListener for order id: {}",
                                sqlException.getSQLState(), paymentOrderEventPayload.getOrderId());
                    }
                }
            }
        });
    }
}
