package com.entities;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="customer")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@NotNull
	private String name;
	@NotNull
    private String cpf;
	@NotNull
    private String email;
//	@NotNull
//	private List<ProductDTO	>products;
	//
    
	public Customer(int id, String name, String cpf, String email) {
		this.id = id;
		this.name = name;
		this.cpf = cpf;				
		this.email = email;
	}
	 
	
	public Customer(int id){
		this.id = id;
	}
	public Customer(String name, String cpf, String email) {
		
		this.name = name;
		this.cpf = cpf;				
		this.email = email;
	}
	public Customer(String name, String cpf) {			
		this.name = name;
		this.cpf = cpf;	
	}
	public Customer(){}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
    public String toString() {
        return String.format(
                "Cliente[id=%d, name='%s', cpf='%s' , email='%s']",
                
                id, name, cpf, email);
    }

   
}
