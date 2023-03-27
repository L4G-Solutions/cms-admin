package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.CmsProxyService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.translator.StrapiTranslator;
import test.com.andromeda.cms.admin.util.RedisTestContextUtils;

public class ArticleServiceTest 
{
	public static void main(String args[]) throws Exception
	{
		ArticleService sitemapArticleService = TestContextUtils.getSitemapArticleService();
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		CategoryService sitemapCategoryService = TestContextUtils.getSitemapCategoryService();
		CategoryDao sitemapCategoryDao = TestContextUtils.getSitemapCategoryDao();
		DataGeneratorService dataGeneratorService = TestContextUtils.getDataGeneratorService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		CmsProxyService cmsProxyService = RedisTestContextUtils.getCmsProxyService();
		sitemapArticleService.setCmsProxyService(RedisTestContextUtils.getCmsProxyService());
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		
		//for(int i = 55 ; i<= 90; i++)
		{
			String url = "http://3.108.187.218:1337/api/articles/" + 83 + "?populate[1]=articleTextEditor&populate[2]=articleTextEditor.contentImage&populate[3]=articleTextEditor.articles&populate[5]=primaryImage&populate[6]=primaryCategory&populate[7]=secondaryCategories&populate[0]=PhotoGallery&populate[8]=storyGeographicLocation&populate[9]=primarySubCategory&populate[10]=secondarySubCategories&populate[4]=articleTextEditor.articles.contentImage&populate[11]=tags&populate[12]=articleTextEditor.documentUpload";
			System.out.println(url);
			StrapiArticle sr = strapiArticleService.getArticle(url);
			Article sitemapArticle =  StrapiTranslator.translateArticle(sr);
			if(sitemapArticle.isDisplayModifiedDate() == null)
			{
				sitemapArticle.setDisplayModifiedDate(false);
			}
			sitemapArticleService.addOrUpdate(sitemapArticle);
			
			//sitemapArticleService.generateLandingPage(sitemapArticle, categoryList);
		
			
			sitemapArticleService.saveLatestArticlesToRedis("article");

			
			String relatedArticlesStr = sitemapArticle.getRelatedArticles();
			String[] relatedArticleIds = relatedArticlesStr.split(",");
			List<Article> relatedArticles = new ArrayList<>();
			if(relatedArticleIds.length > 0)
			{
				for (String relatedArticleId : relatedArticleIds) 
				{
					if(relatedArticleId.length() > 0)
					{
						Article sa = sitemapArticleService.getById(Integer.parseInt(relatedArticleId));
						relatedArticles.add(sa);
					}
				}
			}
			sitemapArticleService.saveCategoryRelatedArticlesToRedis(sitemapArticle,false);
			dataGeneratorService.generateArticle(sitemapArticle,categoryList, relatedArticles, false);
		}
		

	}
}
