package org.ufla.dcc.jmsauth.repository;

import org.springframework.data.repository.CrudRepository;
import org.ufla.dcc.jmsauth.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByUsername(String username);

}
