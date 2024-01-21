package net.greeta.stock.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CustomerDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.customer")
    public DataSourceProperties customerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.customer.hikari")
    public DataSource customerDataSource() {
        return customerDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate customerJdbcTemplate(@Qualifier("customerDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
