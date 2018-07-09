package org.ufla.dcc.jmsnewsl.service;

import java.io.IOException;

import javax.mail.MessagingException;

import org.ufla.dcc.jmsnewsl.domain.User;


public interface MailerService {
	
	void sendEmail(User user) throws MessagingException, IOException;
	
	Boolean sendEmailToAll() throws MessagingException, IOException;
	
}
