package net.greeta.stock.common.domain.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class CustomerAccountDto {
	private String customerId;
	private BigDecimal balance;
}
