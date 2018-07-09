package com.app;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Controller
public class InvocationServiceCEP {
	
	@RequestMapping("/getCep")
	@ResponseBody
	public String getCep(){
		
		String getCustomer = "https://viacep.com.br/ws/37160000/json/"; 
        try{
            URL url = new URL(getCustomer);
            HttpURLConnection requestCustomer = (HttpURLConnection) url.openConnection();
            requestCustomer.connect();             
            JsonParser jp = new JsonParser();          
            JsonElement getCustomerResult = jp.parse(new InputStreamReader((InputStream) requestCustomer.getContent()));
     
            return getCustomerResult.toString();            
            
        }catch (Exception e){
            System.out.println("" + e.getMessage());
            e.printStackTrace();
            return "Erro:" + e.getMessage();
        }       		
	}
}
