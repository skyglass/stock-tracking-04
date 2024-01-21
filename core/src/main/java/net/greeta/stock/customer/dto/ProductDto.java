package net.greeta.stock.customer.dto;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class ProductDto {
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;
}
