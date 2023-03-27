package com.andromeda.cms.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.service.CmsProxyService;

/**
 * 
 * @author Prakash K
 * @date 2020-09-14
 *
 */
@RestController
public class CmsProxyController
{
	@Autowired
	private CmsProxyService cmsProxyService;

	@ResponseBody
	@RequestMapping(value = "/content", method = { RequestMethod.GET })
	public String get() throws Exception
	{
		return getData("/content/");
	}

	@ResponseBody
	@RequestMapping(value = "/content/**/*", method = { RequestMethod.GET })
	public String get(HttpServletRequest request) throws Exception
	{
		return getData(request.getRequestURI());
	}

	private String getData(String path) throws Exception
	{
		String uri = path.replaceFirst("/content", "");
		long startTime = System.currentTimeMillis();
		String value = cmsProxyService.get(uri);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.printf("| %dms | %db | %s\n", totalTime, value.length(), uri);
		return value;
	}

	@ResponseBody
	@RequestMapping(value = "/delkey/{key}", method = { RequestMethod.GET })
	public String delKey(@PathVariable String key) throws Exception
	{
		cmsProxyService.delete(key);
		return "Deleted from primary cache - /" + key;
	}

	@ResponseBody
	@RequestMapping(value = "/delall/{key}", method = { RequestMethod.GET })
	public String delAll(@PathVariable String key) throws Exception
	{
		cmsProxyService.deleteAll(key);
		return "Deleted from primary and secondary cache - /" + key;
	}
}
