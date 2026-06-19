package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.OrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class OrderItemDTO {

	@NotNull(message = "Campo requerido")
	private Long productId;

	private String name;
	private Double price;

	@NotNull(message = "Campo requerido")
	@PositiveOrZero(message = "Quantidade deve ser maior ou igual a zero")
	private Integer quantity;

	private String imgUrl;

	public OrderItemDTO() {}

	public OrderItemDTO(OrderItem entity) {
		productId = entity.getProduct().getId();
		name = entity.getProduct().getName();
		price = entity.getPrice();
		quantity = entity.getQuantity();
		imgUrl = entity.getProduct().getImgUrl();
	}

	public Long getProductId() { return productId; }
	public void setProductId(Long productId) { this.productId = productId; }
	public String getName() { return name; }
	public Double getPrice() { return price; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public String getImgUrl() { return imgUrl; }

	public Double getSubTotal() { return price * quantity; }
}
