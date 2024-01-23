package net.greeta.stock.payment.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@EnableJpaRepositories(basePackages = "net.greeta.stock.payment.dataaccess")
@EntityScan(basePackages = "net.greeta.stock.payment.dataaccess")
@SpringBootApplication(scanBasePackages = "net.greeta.stock")
@EnableRetry
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
