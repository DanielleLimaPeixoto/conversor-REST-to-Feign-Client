package org.ufla.dcc.jmsauth.service;

import org.ufla.dcc.jmsauth.domain.User;

public interface AuthenticateService {
	
	Boolean authenticate(String username, String password);
	
	User saveUser(User user);

}
