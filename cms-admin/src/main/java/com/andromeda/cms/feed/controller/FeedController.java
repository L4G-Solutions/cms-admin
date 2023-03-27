package com.andromeda.cms.feed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.sitemap.service.SitemapLocationService;

@RestController
public class FeedController 
{
	@Autowired
	FeedService feedService;
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/main-feed", method = { RequestMethod.GET })
	public void createMainFeedPage()
	{
		feedService.createMainFeedPage();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/category/{categoryId}", method = { RequestMethod.GET })
	public void createCategoryFeedPage(@PathVariable int categoryId)
	{
		feedService.createCategoryFeedPage(categoryId);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/photoGallery/{categoryId}", method = { RequestMethod.GET })
	public void createPhotoGalleryFeedPage(@PathVariable int categoryId)
	{
		feedService.createPhotoGalleryCategoryFeedPage(categoryId);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/all-categories", method = { RequestMethod.GET })
	public void createAllCategoryFeedPages()
	{
		feedService.createAllCategoryFeedPages();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/all-subCategories", method = { RequestMethod.GET })
	public void createAllSubCategoryFeedPages()
	{
		feedService.createAllSubCategoryFeedPages();
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/photoGallery/subCategory/{subCategoryId}", method = { RequestMethod.GET })
	public void createPhotoGallerySubCategoryFeedPage(@PathVariable int subCategoryId)
	{
		feedService.createPhotoGallerySubCategoryFeedPage(subCategoryId);
	}
	
	@ResponseBody
	@RequestMapping(value = "/cms/feed/subCategory/{subCategoryId}", method = { RequestMethod.GET })
	public void createSubCategoryFeedPage(@PathVariable int subCategoryId)
	{
		feedService.createSubCategoryFeedPage(subCategoryId);
	}
}
