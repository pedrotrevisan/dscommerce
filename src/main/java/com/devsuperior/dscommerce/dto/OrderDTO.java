package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

	private Long id;
	private Instant moment;
	private OrderStatus status;
	private UserDTO client;

	@NotEmpty(message = "Deve ter pelo menos um item")
	@Valid
	private List<OrderItemDTO> items = new ArrayList<>();

	public OrderDTO() {}

	public OrderDTO(Order entity) {
		id = entity.getId();
		moment = entity.getMoment();
		status = entity.getStatus();
		client = new UserDTO(entity.getClient());
		entity.getItems().forEach(item -> items.add(new OrderItemDTO(item)));
	}

	public Long getId() { return id; }
	public Instant getMoment() { return moment; }
	public OrderStatus getStatus() { return status; }
	public UserDTO getClient() { return client; }
	public List<OrderItemDTO> getItems() { return items; }

	public Double getTotal() {
		return items.stream().mapToDouble(OrderItemDTO::getSubTotal).sum();
	}
}
