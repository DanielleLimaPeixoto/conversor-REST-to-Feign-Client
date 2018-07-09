package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DAO.ProductDAO;
import entities.Product;

@Service
public class ProductService {
	@Autowired
	private ProductDAO productDAO;
	protected Logger logger = Logger.getLogger(ProductService.class.getName());
	public ProductService(){
		
	}
	
	public void saveProduct(Product p){
		try {
			productDAO.save(p);
		
			logger.info("Produto criado com Sucesso!");

		} catch (ArrayIndexOutOfBoundsException e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
	
	public Product findProductByID(String id) {

		try {
			Product c = productDAO.findById(id);
			
			return c;

		} catch (ArrayIndexOutOfBoundsException e) {

			throw new UnsupportedOperationException("Not supported yet.");

		}
	}
	
	public List<Product> getAllProducts(){
		List<Product> products = new ArrayList();
		return productDAO.findAll().stream().filter(p -> p.getNumber() > 0).collect(Collectors.toList());
	}
	
	public String  getProductById(String id){
		Product product;
		product = productDAO.findById(id);
		return product.getName();
		
	}
	
	public Product findOneProduct(String id){
		return productDAO.findOne(id);
	}
	
	
}
