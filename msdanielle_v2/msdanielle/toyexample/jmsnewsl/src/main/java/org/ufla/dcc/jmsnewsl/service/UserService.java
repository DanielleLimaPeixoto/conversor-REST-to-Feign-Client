package org.ufla.dcc.jmsnewsl.service;

import org.ufla.dcc.jmsnewsl.domain.User;

public interface UserService {
	
	User save(User user);
	
	Iterable<User> findAll();

}
