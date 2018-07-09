package org.ufla.dcc.jmsnewsl.controller;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.ufla.dcc.jmsnewsl.domain.User;
import org.ufla.dcc.jmsnewsl.service.MailerService;
import org.ufla.dcc.jmsnewsl.service.UserService;

@RestController
@RequestMapping("/api")
public class NewsletterController {
	
	@Autowired
	private MailerService mailerService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/newsletter/subscribe")
	@ResponseBody
	public Boolean subscribe(@RequestBody User user) {
		User userBD = userService.save(user);
		return userBD != null;
    }
	
	@GetMapping("/newsletter/publish")
	@ResponseBody
	public Boolean publish() {
		try {
			return mailerService.sendEmailToAll();
		} catch (MessagingException | IOException e) {
			return false;
		}
    }

}
