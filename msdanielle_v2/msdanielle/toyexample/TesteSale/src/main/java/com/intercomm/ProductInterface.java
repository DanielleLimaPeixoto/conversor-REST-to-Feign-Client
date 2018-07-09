package com.intercomm;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="MsProduct", configuration = FeignConfiguration.class)
public interface ProductInterface {

	@RequestMapping( method = RequestMethod.GET, value="/product/getProduct/{id}")
	public String getProduct(@PathVariable("id") String id);
	
	@RequestMapping( method = RequestMethod.GET, value="/getProducts")
	public String getAllProduct();
	
	@RequestMapping( method = RequestMethod.POST, value="/venda/{id}/{qnt}")
	public String updateStock(@PathVariable("id") String[] id, @PathVariable("qnt") String[] qnt);
}
