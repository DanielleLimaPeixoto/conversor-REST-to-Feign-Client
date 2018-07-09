package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

//import com.intercomm.AuthenticateInterface;
//import com.intercomm.CustomerInterface;
//import com.intercomm.ProductInterface;

@Controller
public class SaleController {

//	@Autowired
//	private CustomerInterface customer;

//	@Autowired
//	private ProductInterface product;
	
//	@Autowired
//	private AuthenticateInterface auth;
	
	@Autowired
	private RestTemplate restTemplate;


	@RequestMapping("/login")
	String login() throws Exception {
		return "index";
	}

	@RequestMapping("/service")
	String service() throws Exception {
		return "sale";
	}

	/**
	 * 
	 * Retorna os produtos atraves do product feign como uma string JSON
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProducts", method = RequestMethod.GET)
	public @ResponseBody String getProducts() {
//		String produtos = product.getAllProduct();
		String produtos = restTemplate.getForObject("http://localhost:9092/getProducts", String.class);
		return produtos;
	}
	@RequestMapping(value = "/getProductsRest", method = RequestMethod.GET)
	public @ResponseBody String getProductsRest() {
		String produtos = restTemplate.getForObject("http://localhost:9092/getProducts", String.class);
		return produtos;
	}
	
	@RequestMapping(value = { "/sale" }, method = RequestMethod.GET)
	ModelAndView index2( ) {
		ModelAndView modelAndView = new ModelAndView();
		
		return modelAndView;
	}

	@RequestMapping("/")
	String index() throws Exception {
		return "sale";
	}

	@RequestMapping("*")
	String home() {
		return "falback";
	}

	@RequestMapping("/venda/getCustomerCpf/{cpf}")
	@ResponseBody
	public String getClient(@PathVariable("cpf") String cpf) {
		if (!cpf.isEmpty()) {
//			String clientJson = customer.getCustomerCpf(cpf);
			String clientJson = restTemplate.getForObject("http://localhost:9093/customer/getCustomerCpf/" + cpf, String.class);
			if (clientJson.isEmpty()) {
				return null;
			} else {
				return clientJson;
			}
		} else {
			return null;
		}

	}
	
	@RequestMapping("/venda/getProduct/{id}")
	@ResponseBody
	public String getProduct(@PathVariable("id") String id) {
//		return product.getProduct(id);
		return restTemplate.getForObject("http://localhost:9092/product/getProduct/"+ id, String.class);
	}
	
//	@RequestMapping(value= "autenticacao/{user}/{password}", method = RequestMethod.GET)
//	@ResponseBody
//	public String getAuthenticationRest(@PathVariable String user, @PathVariable String password) {
//		System.out.println("user: " + user + "password:  " + password);
//		String response = restTemplate.getForObject("http://localhost:9094/api/authenticate/"+user+"/"+password, String.class);
//		return response;
//	}
	
	@RequestMapping("/venda/autenticacao/{user}/{senha}")
	@ResponseBody
	public String getAuthentication(@PathVariable("user") String user, @PathVariable("senha") String senha) {
		String response = restTemplate.postForObject(
				"http://localhost:9094/api/authenticate?username=" + user + "&password=" + senha, null, String.class);
		return response;

	}
	@RequestMapping("/atualizaProducts/{qnt}/{id}")
	@ResponseBody
	public String productUpdate(@PathVariable("qnt") int qnt, @PathVariable("id") String id){
		String response = restTemplate.postForObject(
				"http://localhost:9092/updateProduct/" + qnt + "/" + id, null, String.class);
		return response;
	}
}
