package DAO;


import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import entities.Product;

@Repository
@Transactional
public interface ProductDAO extends CrudRepository<Product,String>{
	
	public Product save(Product p);
	public List<Product> findAll();	
	public Product findById(String id);

	
	
}
