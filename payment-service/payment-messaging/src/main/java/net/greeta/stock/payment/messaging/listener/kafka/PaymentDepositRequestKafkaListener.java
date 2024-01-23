package net.greeta.stock.payment.messaging.listener.kafka;

import debezium.order.payment_deposit_outbox.Envelope;
import debezium.order.payment_deposit_outbox.Value;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.event.payload.OrderPaymentEventPayload;
import net.greeta.stock.common.messaging.DebeziumOp;
import net.greeta.stock.kafka.consumer.KafkaConsumer;
import net.greeta.stock.kafka.order.avro.model.PaymentOrderStatus;
import net.greeta.stock.kafka.producer.KafkaMessageHelper;
import net.greeta.stock.payment.domain.exception.PaymentApplicationServiceException;
import net.greeta.stock.payment.domain.exception.PaymentNotFoundException;
import net.greeta.stock.payment.domain.ports.input.message.listener.PaymentRequestMessageListener;
import net.greeta.stock.payment.messaging.mapper.PaymentDepositMessagingDataMapper;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class PaymentDepositRequestKafkaListener implements KafkaConsumer<Envelope> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentDepositMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentDepositRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentDepositMessagingDataMapper paymentMessagingDataMapper,
                                       KafkaMessageHelper kafkaMessageHelper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-deposit-consumer-group-id}",
            topics = "${payment-service.payment-deposit-request-topic-name}",
            groupId = "${kafka-consumer-config.payment-deposit-consumer-group-id}")
    public void receive(@Payload List<Envelope> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment deposit requests received!",
                messages.stream().filter(message -> message.getBefore() == null &&
                        DebeziumOp.CREATE.getValue().equals(message.getOp())).toList().size());

        messages.forEach(message -> {

            if (message.getBefore() == null && DebeziumOp.CREATE.getValue().equals(message.getOp())) {
                log.info("Incoming message in PaymentDepositRequestKafkaListener: {}", message);
                Value paymentRequestAvroModel = message.getAfter();
                OrderPaymentEventPayload orderPaymentEventPayload =
                        kafkaMessageHelper.getOrderEventPayload(paymentRequestAvroModel.getPayload(), OrderPaymentEventPayload.class);
                try {
                    if (PaymentOrderStatus.PENDING.name().equals(orderPaymentEventPayload.getPaymentOrderStatus())) {
                        log.info("Processing payment deposit for order id: {}", orderPaymentEventPayload.getOrderId());
                        paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                                .paymentRequestAvroModelToPaymentRequest(orderPaymentEventPayload, paymentRequestAvroModel));
                    }
                } catch (DataAccessException e) {
                    SQLException sqlException = (SQLException) e.getRootCause();
                    if (sqlException != null && sqlException.getSQLState() != null &&
                            PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                        //NO-OP for unique constraint exception
                        log.error("Caught unique constraint exception with sql state: {} " +
                                        "in PaymentDepositRequestKafkaListener for order id: {}",
                                sqlException.getSQLState(), orderPaymentEventPayload.getOrderId());
                    } else {
                        throw new PaymentApplicationServiceException("Throwing DataAccessException in" +
                                " PaymentDepositRequestKafkaListener: " + e.getMessage(), e);
                    }
                } catch (PaymentNotFoundException e) {
                    //NO-OP for PaymentNotFoundException
                    log.error("No payment deposit found for order id: {}", orderPaymentEventPayload.getOrderId());
                }
            }
        });

    }
}

