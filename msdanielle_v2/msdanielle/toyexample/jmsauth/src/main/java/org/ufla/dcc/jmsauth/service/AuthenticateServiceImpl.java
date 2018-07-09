package org.ufla.dcc.jmsauth.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ufla.dcc.jmsauth.domain.User;
import org.ufla.dcc.jmsauth.repository.UserRepository;

@Service
public class AuthenticateServiceImpl implements AuthenticateService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional
	public Boolean authenticate(String username, String password) {
		User user = userRepository.findByUsername(username);
		return user != null && user.getPassword().equals(password);
	}

	@Override
	@Transactional
	public User saveUser(User user) {
		return userRepository.save(user);
	}

}
