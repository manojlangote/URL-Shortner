package com.url.shortner.app.rest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.url.shortner.app.service.URLShortnerServiceImpl;

@Controller
public class RequestMappingImpl {

	private static String appDomain;

	@Value("${my-app-domain}")
	public void setAppDomain(String domain) {
		appDomain = domain;
	}

	public static String getAppDomain() {
		return appDomain;
	}

	@GetMapping("/")
	public String getHomePage(Model model) {
		model.addAttribute("apiBaseUrl", appDomain);
		return "index.html";
	}

	@GetMapping("/shorten")
	@ResponseBody
	public JSONObject getShortURL(@RequestHeader("inputUrl") String inputUrl, @RequestHeader("customUrlData") String customInput) {
		return URLShortnerServiceImpl.shortenURL(inputUrl,customInput, 8);
	}

	@GetMapping("/{shortUrl}")
	public String submitForm(@PathVariable("shortUrl") String shortUrl) {
		return "redirect:" + URLShortnerServiceImpl.getURLFromDB(shortUrl);
	}

	ApplicationContextAware applicationContextAware() throws BeanCreationException {
		return null;
	}
}
