package com.url.shortner.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.url.shortner.app.db.RedisDBServiceImpl;

@Configuration
public class AppConfig {

	@Bean
	public RedisDBServiceImpl getRedisDBServiceImpl() {
		return new RedisDBServiceImpl();
	}
}
