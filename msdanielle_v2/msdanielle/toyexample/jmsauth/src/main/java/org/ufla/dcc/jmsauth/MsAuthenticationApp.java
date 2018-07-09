package org.ufla.dcc.jmsauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.ufla.dcc.jmsauth.domain")
@ComponentScan("org.ufla.dcc.jmsauth")
@EnableJpaRepositories("org.ufla.dcc.jmsauth.repository")
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class MsAuthenticationApp {

	public static void main(String[] args) {
		SpringApplication.run(MsAuthenticationApp.class, args);
	}

}