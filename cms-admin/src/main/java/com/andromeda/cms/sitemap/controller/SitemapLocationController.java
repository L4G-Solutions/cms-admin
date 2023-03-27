package com.andromeda.cms.sitemap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.sitemap.service.SitemapLocationService;

//@RestController
public class SitemapLocationController 
{
	@Autowired
	SitemapLocationService sitemapLocationService;
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/index", method = { RequestMethod.GET })
	public void createSitemapIndex()
	{
		//sitemapLocationService.createSitemapIndex();
	}
}
