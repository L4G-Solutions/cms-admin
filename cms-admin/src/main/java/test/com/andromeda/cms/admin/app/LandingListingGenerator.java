package test.com.andromeda.cms.admin.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.PhotoGalleryService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.cms.service.SubCategoryService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.commons.util.FileNDirUtils;
import com.andromeda.commons.util.PropertiesUtils;
import com.andromeda.migration.service.ArticleStatusService;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class LandingListingGenerator {
	
	public static void main(String[] args) throws Exception {
		StrapiCategory rKCategory = null;
		StrapiCategory photoCategory = null;
		StrapiCategory videoCategory = null;

		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
	

		StrapiContentService strapiContentService = TestContextUtils.getStrapiContentService();
		ArticleService sitemapArticleService = TestContextUtils.getSitemapArticleService();
		PhotoGalleryService photoGalleryService = TestContextUtils.getPhotoGalleryService();
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		
		CategoryService categoryService = TestContextUtils.getSitemapCategoryService();
		SubCategoryService subCategoryService = TestContextUtils.getSitemapSubCategoryService();
		FeedService feedService = TestContextUtils.getFeedService();
		feedService.init();

		HashMap<Integer, StrapiCategory> categoryMap = new HashMap<>();
		HashMap<Integer, List<StrapiSubCategory>> subCategoryMap = new HashMap<>();
		
		List<StrapiCategory> categoryList = strapiCategoryService.getAllCategories();
		
		for (StrapiCategory strapiCategory : categoryList) 
		{
			int strapiCategoryId = strapiCategory.getId();
			categoryMap.put(strapiCategoryId, strapiCategory);
		}
		
		List<Category> categories = categoryService.getAll();

		StrapiWebhooksService strapiWebhooksService = TestContextUtils.getStrapiWebhooksService();
		strapiWebhooksService.init();
		
		List<StrapiCategory> strapiCategories = strapiCategoryService.getByCategoryName("Open Heart");
		if (strapiCategories != null && strapiCategories.size() > 0)
			rKCategory = strapiCategories.get(0);
		
		strapiCategories = strapiCategoryService.getByCategoryName(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
		if (strapiCategories != null && strapiCategories.size() > 0)
			photoCategory = strapiCategories.get(0);
			
		strapiCategories = strapiCategoryService.getByCategoryName(StrapiConstants.STRAPI_CATEGORY_VIDEOGALLERY);
		if (strapiCategories != null && strapiCategories.size() > 0)
			videoCategory = strapiCategories.get(0);

		System.out.println("Save LatestArticles To Redis()");
		List<Article> latestNews = sitemapArticleService
				.saveLatestArticlesOnCreatedToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		sitemapArticleService.generateLatestArticleListingPage(latestNews, categoryList);

		System.out.println("Generate Priority page");
		sitemapArticleService.savePriorityArticlesToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		
		boolean rkFlag ;
		
		for (Category category : categories) 
		{
			if(category.getId()==rKCategory.getId())
				rkFlag = true;
			else
				rkFlag = false;
			
			List<SubCategory> subcategories = new ArrayList<>();
			String subCatsStrs = category.getSubCategories();
			String[] subCatsStrArray = subCatsStrs.split(",");
			for (String s : subCatsStrArray)
			{
				if(!s.isEmpty())
				{
					SubCategory sc = subCategoryService.getById(Integer.valueOf(s));
					subcategories.add(sc);
				}
			}
			
			if (category.getId() == rKCategory.getId()) 
			{
				sitemapArticleService.saveCategoryRelatedArticlesToRedisForCMS(category, rkFlag);
				sitemapArticleService.generateRKPage(category.getId(), categoryList, false);
				for (SubCategory subCategory : subcategories) 
				{
					sitemapArticleService.generateListingPageForCMS(category, subCategory, categoryList, false);

				}
			}
			else if (category.getId() == photoCategory.getId()) {
				photoGalleryService.saveLatestPhotosOnCreatedToRedis();
				photoGalleryService.savePhotoCategoryRelatedArticlesToRedisForCMS(category);
				photoGalleryService.generateLandingAndListingPageForCMS(category, categoryList);
			}
			else  {
				if (category.getId() == videoCategory.getId())
					sitemapArticleService.saveLatestVideosToRedis(StrapiConstants.CONTENT_TYPE_VIDEO);
				sitemapArticleService.saveCategoryRelatedArticlesToRedisForCMS(category, rkFlag);
				sitemapArticleService.generateLandingAndListingPageForCMS(category, categoryList);
			}
		}
		
	 
	}

}
