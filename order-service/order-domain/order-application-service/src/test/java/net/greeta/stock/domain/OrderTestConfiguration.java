package net.greeta.stock.domain;

import net.greeta.stock.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import net.greeta.stock.domain.ports.output.repository.CustomerRepository;
import net.greeta.stock.domain.ports.output.repository.OrderRepository;
import net.greeta.stock.domain.ports.output.repository.PaymentOutboxRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

;

@SpringBootApplication(scanBasePackages = "net.greeta.stock")
public class OrderTestConfiguration {

    @Bean
    public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
        return Mockito.mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public PaymentOutboxRepository paymentOutboxRepository() {
        return Mockito.mock(PaymentOutboxRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
