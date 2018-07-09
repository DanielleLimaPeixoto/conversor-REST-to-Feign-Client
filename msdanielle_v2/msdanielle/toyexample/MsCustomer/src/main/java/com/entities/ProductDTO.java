package com.entities;

import javax.validation.constraints.NotNull;

public class ProductDTO {
	private String id;
	
	private String name;
	
    private String description;
	@NotNull
    private int number;
	@NotNull
	private double value;
	public ProductDTO(String id, String name, String description, int number, double value) {
	
		this.id = id;
		this.name = name;
		this.description = description;
		this.number = number;
		this.value = value;
	}
}
