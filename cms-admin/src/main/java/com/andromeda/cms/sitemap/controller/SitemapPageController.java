package com.andromeda.cms.sitemap.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.sitemap.service.SitemapPageService;

@RestController
public class SitemapPageController {
	
	@Autowired
	SitemapPageService sitemapPageService;
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/news-sitemap", method = { RequestMethod.GET })
	public void createOrUpdateNewsSitemap()
	{
		sitemapPageService.createOrUpdateNewsSitemap();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/post-sitemap", method = { RequestMethod.GET })
	public void createPostSitemap()
	{
		sitemapPageService.createPostSitemap();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/post-sitemap/{dateStr}", method = { RequestMethod.GET })
	public void createPostSitemapByDate(@PathVariable String dateStr)
	{
		Date utilDate = StrapiUtils.formatDate(dateStr);
		java.sql.Date date = new java.sql.Date(utilDate.getTime());
		sitemapPageService.createPostSitemapByDate(date);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/post-sitemap/from/{fromDateStr}/to/{toDateStr}", method = { RequestMethod.GET })
	public void createPostSitemapBtwDates(@PathVariable String fromDateStr, @PathVariable String toDateStr)
	{
		sitemapPageService.createPostSitemapBtwDates(fromDateStr, toDateStr);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/gallery-sitemap/{publishedYear}", method = { RequestMethod.GET })
	public void createOrUpdateGallerySitemap(@PathVariable Integer publishedYear)
	{
		sitemapPageService.createOrUpdateGallerySitemap(publishedYear);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/video-sitemap/{publishedYear}", method = { RequestMethod.GET })
	public void createOrUpdateVideoSitemap(@PathVariable Integer publishedYear)
	{
		sitemapPageService.createOrUpdateVideoSitemap(publishedYear);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/category-sitemap", method = { RequestMethod.GET })
	public void createOrUpdateCategorySitemap()
	{
		sitemapPageService.createOrUpdateCategorySitemap();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/sitemap-index", method = { RequestMethod.GET })
	public void createOrUpdateSitemapIndex()
	{
		sitemapPageService.createOrUpdateSitemapIndex();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/sitemap/page-sitemap", method = { RequestMethod.GET })
	public void createOrUpdatePageSitemap()
	{
		sitemapPageService.createOrUpdatePageSitemap();
	}
	
	
}
