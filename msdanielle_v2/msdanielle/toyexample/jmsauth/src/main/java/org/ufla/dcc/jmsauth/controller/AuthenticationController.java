package org.ufla.dcc.jmsauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.ufla.dcc.jmsauth.domain.User;
import org.ufla.dcc.jmsauth.service.AuthenticateService;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
	
	@Autowired
	private AuthenticateService authenticateService;
	
	@RequestMapping("/authenticate")
	@ResponseBody
	public Boolean authentication(@RequestParam("username") String username, @RequestParam("password") String password) {
		return authenticateService.authenticate(username, password);
    }
	
	@PostMapping("/user/save")
	@ResponseBody
	public User saveUser(@RequestBody User user) {
		return authenticateService.saveUser(user);
    }

}
