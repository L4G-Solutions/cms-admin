package com.andromeda.cms.app.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Prakash K
 * @date 2020-10-19
 *
 */
//@Component
public class RequestMappingFilter extends OncePerRequestFilter
{
	private static final String[] paths =
			{ "/actuator", "/delkey", "/delall", "/stats", "/favicon" };

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException
	{
		String uri = request.getRequestURI();
		if (!StringUtils.isEmpty(uri))
		{
			boolean found = false;
			for (String path : paths)
			{
				if (uri.startsWith(path))
				{
					found = true;
					break;
				}
			}

			if (!found)
			{
				uri = "/content" + uri;
			}
		}
		else
		{
			uri = "/content";
		}

		request.getRequestDispatcher(uri).forward(request, response);
	}
}
