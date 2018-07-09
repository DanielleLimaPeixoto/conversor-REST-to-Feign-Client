package com.intercomm;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="MsAutenticacao")
public interface AuthenticateInterface {
	@RequestMapping( method = RequestMethod.GET, value="autenticate/{name}/{password}")
	public String verifyAuthentication(@PathVariable("name") String name, @PathVariable("password") String password );

}
