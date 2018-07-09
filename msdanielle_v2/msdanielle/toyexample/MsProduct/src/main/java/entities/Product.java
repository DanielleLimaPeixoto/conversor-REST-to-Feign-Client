package entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
@Entity
@Table(name="product")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	@NotNull
	private String name;
	@NotNull
    private String description;
	@NotNull
    private int number;
	@NotNull
	private double value;
	public Product(String id, String name, String description, int number, double value) {
	
		this.id = id;
		this.name = name;
		this.description = description;
		this.number = number;
		this.value = value;
	}
	public Product(){}
	public Product( String name, String description, int number, double value) {
		
		
		this.name = name;
		this.description = description;
		this.number = number;
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	
}
