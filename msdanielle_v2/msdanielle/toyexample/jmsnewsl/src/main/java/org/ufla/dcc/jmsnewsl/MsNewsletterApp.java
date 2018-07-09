package org.ufla.dcc.jmsnewsl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.ufla.dcc.jmsnewsl.domain")
@ComponentScan("org.ufla.dcc.jmsnewsl")
@EnableJpaRepositories("org.ufla.dcc.jmsnewsl.repository")
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class MsNewsletterApp {

	public static void main(String[] args) {
		SpringApplication.run(MsNewsletterApp.class, args);
	}

}