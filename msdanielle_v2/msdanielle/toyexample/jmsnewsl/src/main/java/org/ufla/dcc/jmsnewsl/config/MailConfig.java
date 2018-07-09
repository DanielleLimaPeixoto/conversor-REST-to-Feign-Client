package org.ufla.dcc.jmsnewsl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.ufla.dcc.jmsnewsl.config.properties.MAIL_GMAIL.*;
import static org.ufla.dcc.jmsnewsl.config.resources.RESOURCES.MAIL_GMAIL_RESOURCE;

@Configuration
@PropertySource(MAIL_GMAIL_RESOURCE)
public class MailConfig {

    @Autowired
    private Environment env;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty(MAIL_SMTP_HOST));
        mailSender.setPort(env.getProperty(MAIL_SMTP_PORT, Integer.class));
        mailSender.setUsername(env.getProperty(MAIL_SMTP_USERNAME));
        mailSender.setPassword(env.getProperty(MAIL_SMTP_PASSWORD));

        Properties props = new Properties();
        props.put(MAIL_TRANSPORT_PROTOCOL, env.getProperty(MAIL_TRANSPORT_PROTOCOL));
        props.put(MAIL_SMTP_AUTH, env.getProperty(MAIL_SMTP_AUTH, Boolean.class));
        props.put(MAIL_SMTP_STARTTLS_ENABLE, env.getProperty(MAIL_SMTP_STARTTLS_ENABLE, Boolean.class));
        props.put(MAIL_SMTP_STARTTLS_REQUIRED, env.getProperty(MAIL_SMTP_STARTTLS_REQUIRED, Boolean.class));
        props.put(MAIL_SMTP_CONNECTIONTIMEOUT, env.getProperty(MAIL_SMTP_CONNECTIONTIMEOUT, Integer.class));

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

}

