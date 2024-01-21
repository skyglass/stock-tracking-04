package net.greeta.stock.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = { "net.greeta.stock.customer.dataaccess"})
@EntityScan(basePackages = { "net.greeta.stock.customer.dataaccess"})
@SpringBootApplication(scanBasePackages = "net.greeta.stock")
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
