package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.User;

import java.time.LocalDate;

public class UserDTO {

	private Long id;
	private String name;
	private String email;
	private String phone;
	private LocalDate birthDate;

	public UserDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		phone = entity.getPhone();
		birthDate = entity.getBirthDate();
	}

	public Long getId() { return id; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getPhone() { return phone; }
	public LocalDate getBirthDate() { return birthDate; }
}
