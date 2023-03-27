package com.andromeda.cms.sitemap.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.sitemap.service.CJSitemapPageService;


@RestController
public class CJSitemapPageController {
	@Autowired
	CJSitemapPageService cjSitemapPageService;
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/news-sitemap", method = { RequestMethod.GET })
	public void createOrUpdateCjNewsSitemap()
	{
		cjSitemapPageService.createOrUpdateCjNewsSitemap();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/post-sitemap", method = { RequestMethod.GET })
	public void createCjPostSitemap()
	{
		cjSitemapPageService.createCjPostSitemap();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/post-sitemap/{dateStr}", method = { RequestMethod.GET })
	public void createCjPostSitemapByDate(@PathVariable String dateStr)
	{
		Date utilDate = StrapiUtils.formatDate(dateStr);
		java.sql.Date date = new java.sql.Date(utilDate.getTime());
		cjSitemapPageService.createCjPostSitemapByDate(date);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/post-sitemap/from/{fromDateStr}/to/{toDateStr}", method = { RequestMethod.GET })
	public void createCjPostSitemapBtwDates(@PathVariable String fromDateStr, @PathVariable String toDateStr)
	{
		cjSitemapPageService.createCjPostSitemapBtwDates(fromDateStr, toDateStr);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/gallery-sitemap/{publishedYear}", method = { RequestMethod.GET })
	public void createOrUpdateCjGallerySitemap(@PathVariable Integer publishedYear)
	{
		cjSitemapPageService.createOrUpdateCjGallerySitemap(publishedYear);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/video-sitemap/{publishedYear}", method = { RequestMethod.GET })
	public void createOrUpdateCjVideoSitemap(@PathVariable Integer publishedYear)
	{
		cjSitemapPageService.createOrUpdateCjVideoSitemap(publishedYear);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/category-sitemap", method = { RequestMethod.GET })
	public void createOrUpdateCategorySitemap()
	{
		cjSitemapPageService.createOrUpdateCjCategorySitemap();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/sitemap-index", method = { RequestMethod.GET })
	public void createOrUpdateSitemapIndex()
	{
		cjSitemapPageService.createOrUpdateCjSitemapIndex();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/cj/sitemap/page-sitemap", method = { RequestMethod.GET })
	public void createOrUpdatePageSitemap()
	{
		cjSitemapPageService.createOrUpdateCjPageSitemap();
	}
}
