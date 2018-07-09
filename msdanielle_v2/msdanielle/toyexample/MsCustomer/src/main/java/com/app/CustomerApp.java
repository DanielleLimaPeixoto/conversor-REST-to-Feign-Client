package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.entities")
@ComponentScan({"com.controller"})
@EnableJpaRepositories("com.DAO")



@EnableEurekaClient
@EnableDiscoveryClient
@EnableAutoConfiguration
@EnableFeignClients(basePackages = {"com.intercomm", "com.controller"})
@SpringBootApplication(scanBasePackages={"com.intercomm","com.DAO","com.entities","com.controller"})
public class CustomerApp {
	
	public static void main(String[] args) {
		SpringApplication.run(CustomerApp.class,args);

	}



}
