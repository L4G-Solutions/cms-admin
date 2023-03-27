package com.andromeda.cms.sitemap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.sitemap.dao.SitemapLocationDao;
import com.andromeda.cms.sitemap.model.SitemapLocation;

@Service
public class SitemapLocationService 
{
	@Autowired
	private SitemapLocationDao sitemapLocationDao;
	
	@Autowired
	private DataGeneratorService dataGeneratorService;
	
	public void setSitemapLocationDao(SitemapLocationDao sitemapLocationDao)
	{
		this.sitemapLocationDao = sitemapLocationDao;
	}
	
	
	public List<SitemapLocation> getAll()
	{
		return sitemapLocationDao.getAll();
	}



}
