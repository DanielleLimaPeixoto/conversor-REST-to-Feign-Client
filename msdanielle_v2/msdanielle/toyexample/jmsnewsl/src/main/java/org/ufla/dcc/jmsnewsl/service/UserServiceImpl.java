package org.ufla.dcc.jmsnewsl.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ufla.dcc.jmsnewsl.domain.User;
import org.ufla.dcc.jmsnewsl.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}
	
	
	

}
