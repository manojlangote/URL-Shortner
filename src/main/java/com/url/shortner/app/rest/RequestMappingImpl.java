package com.url.shortner.app.rest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.url.shortner.app.service.URLShortnerServiceImpl;

@Controller
public class RequestMappingImpl {

	
	@GetMapping("/")
	public String getHomePage() {
		return "index.html";
	}
	
	
	@GetMapping("/shorten")
	@ResponseBody
	public JSONObject getShortURL(@RequestHeader("inputUrl")String inputUrl) {
		return URLShortnerServiceImpl.shortenURL(inputUrl, 8);
	}
	
	ApplicationContextAware applicationContextAware() throws BeanCreationException{
		return null;
	}
}
