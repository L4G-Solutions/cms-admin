package com.andromeda.cms.sitemap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.sitemap.dao.CJSitemapLocationDao;
import com.andromeda.cms.sitemap.model.CJSitemapLocation;


@Service
public class CJSitemapLocationService {
	@Autowired
	private CJSitemapLocationDao cjSitemapLocationDao;
	

	public void setSitemapLocationDao(CJSitemapLocationDao cjSitemapLocationDao)
	{
		this.cjSitemapLocationDao = cjSitemapLocationDao;
	}
	
	
	public List<CJSitemapLocation> getAll()
	{
		return cjSitemapLocationDao.getAll();
	}
}
