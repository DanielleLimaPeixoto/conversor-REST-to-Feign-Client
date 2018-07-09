package org.ufla.dcc.jmsnewsl.service;

import static org.ufla.dcc.jmsnewsl.config.resources.RESOURCES.CONFIRM_EMAIL_RESOURCE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.ufla.dcc.jmsnewsl.domain.User;
import org.ufla.dcc.jmsnewsl.repository.UserRepository;

@Service
public class MailerServiceImpl implements MailerService {
	
    private static final String CONFIRM_EMAIL_SUBJECT = "ToyExample - Publish";
    private static final String FROM_EMAIL = "toyexample72@gmail.com";
    private static final String FROM_NAME = "ToyExample | Microservices Technology";
    private static final String EMAIL_ENCODING = "utf-8";
    private static final String EMAIL_CONTENT_TYPE = "text/html; charset=UTF-8";

    @Autowired
    private JavaMailSender javaMailSender;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Autowired
    private UserRepository userRepository;
    
    private void sendMail(String fromEmail, String fromName, String to, String subject, String contentHtml) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, EMAIL_ENCODING);
        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        mimeMessage.setContent(contentHtml, EMAIL_CONTENT_TYPE);
        javaMailSender.send(mimeMessage);
    }
    
	@Override
	public void sendEmail(User user) throws MessagingException, IOException {
		System.out.println("ENVIANDO EMAIL para " + user.getEmail() + "...");
        Resource resource = resourceLoader.getResource(CONFIRM_EMAIL_RESOURCE);
        byte[] encodedHtmlEmailContent = new byte[(int) resource.contentLength()];
        resource.getInputStream().read(encodedHtmlEmailContent);
        String htmlEmailContent = new String(encodedHtmlEmailContent);
        sendMail(FROM_EMAIL, FROM_NAME, user.getEmail(), CONFIRM_EMAIL_SUBJECT, MessageFormat.format(htmlEmailContent, user.getCpf()));
        System.out.println("EMAIL ENVIADO com sucesso");
		
	}

	@Override
	@Transactional
	public Boolean sendEmailToAll() throws MessagingException, IOException {
		Iterable<User> users = userRepository.findAll();
		for (User user : users) {
			sendEmail(user);
		}
		return true;
	}

}
