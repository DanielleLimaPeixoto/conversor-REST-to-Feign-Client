package com.intercomm;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value="MsNewsLetter")
public interface NewsLetterInterface {
	
	@RequestMapping( method = RequestMethod.GET, value="/newsLetter/autheticate")
	String authenticate(@PathVariable("password") String id, @PathVariable("user") String user );
	
	

}
