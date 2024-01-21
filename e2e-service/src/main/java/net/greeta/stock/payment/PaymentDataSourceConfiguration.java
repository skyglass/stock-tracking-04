package net.greeta.stock.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PaymentDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.payment")
    public DataSourceProperties paymentDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.payment.hikari")
    public DataSource paymentDataSource() {
        return paymentDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate paymentJdbcTemplate(@Qualifier("paymentDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
