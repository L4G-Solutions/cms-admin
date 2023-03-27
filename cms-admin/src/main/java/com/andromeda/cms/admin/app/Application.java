package com.andromeda.cms.admin.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application main class to start Spring Boot application.
 * 
 * @author Prakash K
 * @date 2020-09-14
 *
 */
@ComponentScan(basePackages = { "com.andromeda.cms" })
@SpringBootApplication
(
		exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class,
				TransactionAutoConfiguration.class, RedisAutoConfiguration.class, GsonAutoConfiguration.class })
public class Application
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
}
