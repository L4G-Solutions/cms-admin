package com.andromeda.cms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.model.Article;
import com.andromeda.cms.service.ArticleService;

@RestController
public class ArticleController 
{
	@Autowired
	ArticleService sitemapArticleService;
	
	/*@ResponseBody
	@RequestMapping(value = "/cms/articles/related-articles/{subCategoryId}", method = { RequestMethod.GET })
	public List<Article> getRelatedArticles(@PathVariable int subCategoryId)
	{
		return sitemapArticleService.getSubCategoryRelatedArticles(subCategoryId);
	}*/
	
	/*@ResponseBody
	@RequestMapping(value = "/cms/sitemap/index", method = { RequestMethod.GET })
	public void updateNewsSitemap(SitemapArticle newSitemapArticle)
	{
		//sitemapArticleService.updateNewsSitemap(newSitemapArticle);
	}*/
	
	
}
