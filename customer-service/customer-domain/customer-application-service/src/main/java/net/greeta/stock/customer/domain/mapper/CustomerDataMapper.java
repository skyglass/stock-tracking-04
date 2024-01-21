package net.greeta.stock.customer.domain.mapper;

import net.greeta.stock.common.domain.dto.CreateCustomerCommand;
import net.greeta.stock.common.domain.dto.CreateCustomerResponse;
import net.greeta.stock.customer.domain.entity.Customer;
import net.greeta.stock.common.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

    public Customer createCustomerCommandToCustomer(CreateCustomerCommand createCustomerCommand) {
        return new Customer(new CustomerId(createCustomerCommand.getCustomerId()),
                createCustomerCommand.getUsername(),
                createCustomerCommand.getFirstName(),
                createCustomerCommand.getLastName());
    }

    public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
        return new CreateCustomerResponse(customer.getId().getValue(), message);
    }
}
