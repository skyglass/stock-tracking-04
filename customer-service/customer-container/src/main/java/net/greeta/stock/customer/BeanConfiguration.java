package net.greeta.stock.customer;

import net.greeta.stock.customer.domain.CustomerDomainService;
import net.greeta.stock.customer.domain.CustomerDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CustomerDomainService customerDomainService() {
        return new CustomerDomainServiceImpl();
    }
}
