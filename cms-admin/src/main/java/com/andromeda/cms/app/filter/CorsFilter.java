package com.andromeda.cms.app.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Filter to add XSS attributes to response headers.
 * 
 * @author Prakash K
 * @date 2020-09-14
 *
 */
@Component
public class CorsFilter implements Filter
{
	/**
	 * Constructor definition
	 */
	public CorsFilter()
	{

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletResponse response = (HttpServletResponse) res;

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "HEAD, GET, PUT, POST, PATCH, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Accept, X-Requested-With, remember-me");
		// response.setHeader("X-Frame-Options", "DENY");
		// response.setHeader("X-XSS-Protection", "1; mode=block");
		// response.setHeader("X-Content-Type-Options", "nosniff");

		// Need to check the following
		// response.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors
		// 'self'; reflected-xss 'block'");

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig)
	{

	}

	@Override
	public void destroy()
	{

	}
}