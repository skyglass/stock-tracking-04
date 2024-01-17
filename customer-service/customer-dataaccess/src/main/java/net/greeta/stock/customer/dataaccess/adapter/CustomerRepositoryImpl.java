package net.greeta.stock.customer.dataaccess.adapter;

import net.greeta.stock.customer.dataaccess.mapper.CustomerDataAccessMapper;
import net.greeta.stock.customer.dataaccess.repository.CustomerJpaRepository;
import net.greeta.stock.customer.domain.ports.output.repository.CustomerRepository;
import net.greeta.stock.customer.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    private final CustomerDataAccessMapper customerDataAccessMapper;

    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository,
                                  CustomerDataAccessMapper customerDataAccessMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerDataAccessMapper = customerDataAccessMapper;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(customerDataAccessMapper.customerToCustomerEntity(customer)));
    }
}
