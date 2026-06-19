package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.Product;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO {

	private Long id;

	@NotBlank(message = "Campo requerido")
	@Size(min = 3, max = 80, message = "Nome deve ter entre 3 e 80 caracteres")
	private String name;

	@NotBlank(message = "Campo requerido")
	@Size(min = 10, message = "Descrição deve ter no mínimo 10 caracteres")
	private String description;

	@NotNull(message = "Campo requerido")
	@Positive(message = "Preço deve ser positivo")
	private Double price;

	private String imgUrl;

	@NotEmpty(message = "Deve ter pelo menos uma categoria")
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO() {}

	public ProductDTO(Product entity) {
		id = entity.getId();
		name = entity.getName();
		description = entity.getDescription();
		price = entity.getPrice();
		imgUrl = entity.getImgUrl();
		entity.getCategories().forEach(cat -> categories.add(new CategoryDTO(cat)));
	}

	public Long getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public Double getPrice() { return price; }
	public void setPrice(Double price) { this.price = price; }
	public String getImgUrl() { return imgUrl; }
	public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
	public List<CategoryDTO> getCategories() { return categories; }
}
