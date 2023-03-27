package com.andromeda.cms.admin.service;

import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CmsProxyService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.translator.StrapiTranslator;
import test.com.andromeda.cms.admin.util.RedisTestContextUtils;

public class CategoryLandingListingTest {

	public static void main(String[] args) throws Exception {
		ArticleService sitemapArticleService = TestContextUtils.getSitemapArticleService();
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		
		DataGeneratorService dataGeneratorService = TestContextUtils.getDataGeneratorService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		CmsProxyService cmsProxyService = RedisTestContextUtils.getCmsProxyService();
		sitemapArticleService.setCmsProxyService(RedisTestContextUtils.getCmsProxyService());
		
		String url = "http://3.108.187.218:1337/api/articles/6?populate[1]=articleTextEditor&populate[2]=articleTextEditor.contentImage&populate[3]=articleTextEditor.articles&populate[5]=primaryImage&populate[6]=primaryCategory&populate[7]=secondaryCategories&populate[0]=PhotoGallery&populate[8]=storyGeographicLocation&populate[9]=primarySubCategory&populate[10]=secondarySubCategories&populate[4]=articleTextEditor.articles.contentImage&populate[11]=tags";
		StrapiArticle sr = strapiArticleService.getArticle(url);
		
		Article sitemapArticle =  StrapiTranslator.translateArticle(sr);
		sitemapArticleService.addOrUpdate(sitemapArticle);
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		sitemapArticleService.saveCategoryRelatedArticlesToRedis(sitemapArticle, false);
		sitemapArticleService.generateLandingAndListingPage(sitemapArticle, categoryList);
		//sitemapArticleService.generateCategoryListingPage(sitemapArticle, categoryList);
	}

}
