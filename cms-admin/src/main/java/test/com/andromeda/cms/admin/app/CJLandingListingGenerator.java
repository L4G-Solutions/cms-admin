package test.com.andromeda.cms.admin.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.service.CJArticleService;
import com.andromeda.cms.service.CJCategoryService;
import com.andromeda.cms.service.CJPhotoGalleryService;
import com.andromeda.cms.service.CJSubCategoryService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.commons.util.PropertiesUtils;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class CJLandingListingGenerator {
	public static void main(String[] args) throws Exception {
		StrapiCategory rkCategory = null;
		StrapiCategory ottCategory = null;
		StrapiCategory photoCategory = null;
		StrapiCategory videoCategory = null;

		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
	

		CJArticleService cjArticleService = TestContextUtils.getCjArticleService();
		CJPhotoGalleryService photoGalleryService = TestContextUtils.getCjPhotoGalleryService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		
		CJCategoryService categoryService = TestContextUtils.getCjCategoryService();
		CJSubCategoryService subCategoryService = TestContextUtils.getCjSubCategoryService();
		FeedService feedService = TestContextUtils.getFeedService();
		feedService.init();
		feedService.initCj();

		HashMap<Integer, StrapiCategory> categoryMap = new HashMap<>();
		HashMap<String, StrapiCategory> categoryNameMap = new HashMap<>();
		HashMap<Integer, List<StrapiSubCategory>> subCategoryMap = new HashMap<>();
		
		List<StrapiCategory> categoryList = strapiCategoryService.getAllCjCategoriesWithMetaDesc();
		
		for (StrapiCategory strapiCategory : categoryList) 
		{
			int strapiCategoryId = strapiCategory.getId();
			categoryMap.put(strapiCategoryId, strapiCategory);
			categoryNameMap.put(strapiCategory.getName(), strapiCategory);
		}
		
		List<CJCategory> categories = categoryService.getAll();

		StrapiWebhooksService strapiWebhooksService = TestContextUtils.getStrapiWebhooksService();
		strapiWebhooksService.init();
		strapiWebhooksService.initCj();
		List<StrapiCategory> strapiCategories ;

		rkCategory = categoryNameMap.get(StrapiConstants.STRAPI_CJ_CATEGORY_OPEN_HEART);
		photoCategory = categoryNameMap.get(StrapiConstants.STRAPI_CJ_CATEGORY_PHOTOGALLERY);
		ottCategory = categoryNameMap.get(StrapiConstants.STRAPI_CJ_CATEGORY_OTT);
			
		videoCategory = categoryNameMap.get(StrapiConstants.STRAPI_CATEGORY_VIDEOGALLERY);

		System.out.println("Save Latest Articles To Redis()");
		List<CJArticle> latestNews = cjArticleService
				.saveLatestArticlesOnCreatedToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		cjArticleService.generateLatestArticleListingPage(latestNews, rkCategory, ottCategory, categoryList);

		System.out.println("Save Priority Articles to Redis");
		cjArticleService.savePriorityArticlesToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		
		boolean rkFlag ;
		
		for (CJCategory category : categories) 
		{
			if(category.getId()==rkCategory.getId())
				rkFlag = true;
			else
				rkFlag = false;
			
			List<CJSubCategory> subcategories = new ArrayList<>();
			String subCatsStrs = category.getSubCategories();
			
			if (category.getId() == photoCategory.getId()) {
				photoGalleryService.saveLatestPhotosToRedis();
				photoGalleryService.savePhotoCategoryRelatedArticlesToRedisForCMS(category);
				photoGalleryService.generateLandingAndListingPageForCMS(category, categoryList);
			}
			else  {
				if (category.getId() == videoCategory.getId())
					cjArticleService.saveLatestVideosToRedis(StrapiConstants.CONTENT_TYPE_VIDEO);
				cjArticleService.saveCategoryRelatedArticlesToRedisForCMS(category, rkFlag);
				cjArticleService.generateLandingAndListingPageForCMS(category, rkCategory, ottCategory ,categoryList);
			}
		}
		
		strapiWebhooksService.generateCjHomePage();
	}
}
