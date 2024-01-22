package net.greeta.stock.payment.application.rest;

import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.CustomerAccountDto;
import net.greeta.stock.payment.domain.ports.input.service.PaymentApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @GetMapping("/{customerId}")
    public CustomerAccountDto getCustomerAccount(@PathVariable String customerId) {
        return paymentApplicationService.getCustomerAccount(customerId);
    }

}
