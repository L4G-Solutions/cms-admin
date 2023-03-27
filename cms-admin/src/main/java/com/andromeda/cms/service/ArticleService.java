package com.andromeda.cms.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.standard.expression.Each;

import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.RedirectionUrlDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.AppJson;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.RedirectionUrl;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.commons.util.JsonUtils;



@Service
public class ArticleService 
{
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	
	@Autowired
	private ArticleDao sitemapArticleDao;
	
	@Autowired
	private CategoryDao sitemapCategoryDao;
	
	@Autowired
	private SubCategoryDao sitemapSubCategoryDao;
	
	@Autowired
	private DataGeneratorService dataGeneratorService;
	
	@Autowired
	private CmsProxyService cmsProxyService;
	
	@Autowired
	RedirectionUrlDao redirectionUrlDao;

	public void setSitemapArticleDao(ArticleDao sitemapArticleDao)
	{
		this.sitemapArticleDao = sitemapArticleDao;
	}
	
	public void setSitemapCategoryDao(CategoryDao sitemapCategoryDao)
	{
		this.sitemapCategoryDao = sitemapCategoryDao;
	}
	
	public void setSitemapSubCategoryDao(SubCategoryDao sitemapSubCategoryDao)
	{
		this.sitemapSubCategoryDao = sitemapSubCategoryDao;
	}
	
	public void setDataGeneratorService(DataGeneratorService dataGeneratorService)
	{
		this.dataGeneratorService = dataGeneratorService;
	}
	
	public void setCmsProxyService(CmsProxyService cmsProxyService)
	{
		this.cmsProxyService = cmsProxyService;
	}

	public void generateLatestArticleListingPage(List<Article> latestArticles, List<StrapiCategory> categoryList)
	{
		dataGeneratorService.generateLatestArticleListingPage(latestArticles,categoryList);
	}
	
	public void generateArticlePage(Article sitemapArticle, List<StrapiCategory> categoryList, boolean updateFlag)
	{
		String relatedArticlesStr = sitemapArticle.getRelatedArticles();
		String[] relatedArticleIds = relatedArticlesStr.split(",");
		List<Article> relatedArticles = new ArrayList<>();
		
		if(relatedArticleIds.length > 0)
		{
			for (String relatedArticleId : relatedArticleIds) 
			{
				if(relatedArticleId.length() > 0)
				{
					Article sa = sitemapArticleDao.getById(Integer.parseInt(relatedArticleId));
					relatedArticles.add(sa);
				}
			}
		}
		dataGeneratorService.generateArticle(sitemapArticle,categoryList, relatedArticles, updateFlag);
		dataGeneratorService.generateAmpArticle(sitemapArticle,categoryList, relatedArticles, updateFlag);
	}
	
	
	public synchronized void generateLandingAndListingPageForCMS(Category primaryCategory, List<StrapiCategory> categoryList)
	{
		int categoryId = primaryCategory.getId();
		List<Article> latestCategoryArticles = sitemapArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.LIMIT, null);
		Category videoCategory = sitemapCategoryDao.getByName(StrapiConstants.STRAPI_CATEGORY_VIDEOGALLERY);

		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		
		HashMap<SubCategory, List<Article>> dataHashMap = new HashMap<>();
		if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
		{
			for (String subCategoryIdStr : subCategories) 
			{
				if(!subCategoryIdStr.equals(""))
				{
					SubCategory subCategory = sitemapSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
					List<Article> sitemapArticles = sitemapArticleDao.getLatestBySubCategoryIdOnCreated(null,categoryId,Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
					if(sitemapArticles!= null && sitemapArticles.size()>0)
					{
						dataHashMap.put(subCategory, sitemapArticles);
					}
					
					generateListingPageForCMS(primaryCategory, subCategory, categoryList, false);
				}
			}
			if(categoryId != videoCategory.getId())
			{
				System.out.println("Category Landing and Listing");
				generateListingPageForCMS(primaryCategory, null, categoryList, true);
				dataGeneratorService.generateCategoryLanding(primaryCategory, categoryList, latestCategoryArticles,dataHashMap);
			}
			else if(categoryId == videoCategory.getId())
			{
				System.out.println("Video Landing and Listing");
				generateListingPageForCMS(primaryCategory, null, categoryList, true);
				dataGeneratorService.generateVideoLanding(primaryCategory, categoryList, latestCategoryArticles,dataHashMap);
				dataGeneratorService.generateLiveTV(primaryCategory, categoryList, dataHashMap);
			}
			
		}
		
		else
			generateListingPageForCMS(primaryCategory, null, categoryList, false);

	}
	
	public synchronized void generateLandingAndListingPage(Article sitemapArticle, List<StrapiCategory> categoryList)
	{
		// boolean pcListing = false ; // flag to generate L1(primary category) listing pages
		String contentType = sitemapArticle.getContentType();
		
		int categoryId = sitemapArticle.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(categoryId);
		List<Article> latestCategoryArticles = sitemapArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.LIMIT, null);
		

		int subCategoryId = sitemapArticle.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = null;
		if(subCategoriesStr != null)
		{
			subCategories = subCategoriesStr.split(",");
			
			boolean contains = Arrays.asList(subCategories).contains(String.valueOf(subCategoryId));
			
			HashMap<SubCategory, List<Article>> dataHashMap = new HashMap<>();
			if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
			{
				for (String subCategoryIdStr : subCategories) 
				{
					if(!subCategoryIdStr.equals(""))
					{
						SubCategory subCategory = sitemapSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
						List<Article> sitemapArticles = sitemapArticleDao.getLatestBySubCategoryIdOnCreated(contentType, categoryId,Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
						if(sitemapArticles!= null && sitemapArticles.size()>0)
						{
							dataHashMap.put(subCategory, sitemapArticles);
						}
					}
				}
			}
			
			if(subCategories != null  && subCategories.length > 0 && !subCategoriesStr.equals(""))
			{
				if(contentType.equalsIgnoreCase("article"))
				{
					System.out.println("Category Landing and Listing " + primaryCategory.getUrl());
					dataGeneratorService.generateCategoryLanding(primaryCategory, categoryList, latestCategoryArticles,dataHashMap);
				} 
				else if(contentType.equalsIgnoreCase("video gallery"))
				{
					System.out.println("Video Landing and Listing " + primaryCategory.getUrl());
					dataGeneratorService.generateVideoLanding(primaryCategory, categoryList, latestCategoryArticles,dataHashMap);
					dataGeneratorService.generateLiveTV(primaryCategory, categoryList, dataHashMap);
				}
				
				if(contains)
				{
					generateListingPage(sitemapArticle, categoryList, false);
				}
				
				//copy of article with primary sub category set to null
				//to be used for L1 category listing page generation
				Article articleWithPscRemoved = sitemapArticle;
				articleWithPscRemoved.setPrimarySubCategoryId(0);
				articleWithPscRemoved.setPrimarySubCategoryName(null);
				articleWithPscRemoved.setPrimarySubCategorySeoSlug(null);
				articleWithPscRemoved.setPrimarySubCategoryTeluguLabel(null);
				articleWithPscRemoved.setPrimarySubCategoryUrl(null);
				generateListingPage(articleWithPscRemoved, categoryList, true);
			}
		}
		else
		{
			generateListingPage(sitemapArticle, categoryList, false);
		}
		
	}
	
	
	/**
	 * Generate listing pages for Article i.e. 
	 * 1) Article 
	 * 2)Video Gallery
	 * 
	 * @param sitemapArticle
	 * @param categoryList
	 */
	public synchronized void generateListingPage(Article sitemapArticle, List<StrapiCategory> categoryList, boolean pcListing) 
	{
		String contentType = sitemapArticle.getContentType();
		int categoryId = sitemapArticle.getPrimaryCategoryId();
		Category category = sitemapCategoryDao.getById(categoryId);
		
		int subCategoryId = sitemapArticle.getPrimarySubCategoryId();
		SubCategory subCategory = null;
		if(subCategoryId != 0 )
			subCategory = sitemapSubCategoryDao.getById(subCategoryId);
		
		List<Article> sitemapArticles = null;
		if( subCategory != null && subCategoryId != 0)
		{
			System.out.println("Category Listing " + subCategory.getUrl());
			sitemapArticles = sitemapArticleDao.getLatestBySubCategoryIdOnCreated(contentType, categoryId, subCategoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
		}
		else
		{
			System.out.println("Category Listing " + category.getUrl());
			sitemapArticles = sitemapArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
		}
		
		if(contentType.equalsIgnoreCase("article"))
			dataGeneratorService.generateCategoryListing(category, subCategory, categoryList, sitemapArticles, pcListing);
		else if(contentType.equalsIgnoreCase("video gallery"))
			dataGeneratorService.generateVideoListing(category, subCategory, categoryList, sitemapArticles, pcListing);
	}
	
	public synchronized void generateListingPageForCMS(Category category, SubCategory subCategory, List<StrapiCategory> categoryList, boolean pcListing) 
	{
		int categoryId = category.getId();
		Category videoCategory = sitemapCategoryDao.getByName(StrapiConstants.STRAPI_CATEGORY_VIDEOGALLERY);
		
		List<Article> sitemapArticles = null;
		if( subCategory != null)
		{
			int subCategoryId = subCategory.getId();
			sitemapArticles = sitemapArticleDao.getLatestBySubCategoryIdOnCreated(null, categoryId,subCategoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
		}
		else
		{
			sitemapArticles = sitemapArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
		}
		
		if(categoryId != videoCategory.getId() )
			{
			dataGeneratorService.generateCategoryListing(category, subCategory, categoryList, sitemapArticles, pcListing);
			}
		else if(categoryId == videoCategory.getId())
			{
			dataGeneratorService.generateVideoListing(category, subCategory, categoryList, sitemapArticles, pcListing);
			}
	}
	
	/**
	 * Insert Latest  Articles to Redis Database
	 */
	public List<Article> saveLatestArticlesToRedis(String contentType)
	{
		List<Article> latestArticles = sitemapArticleDao.getLatestArticles(contentType, StrapiConstants.STRAPI_PAGINATION_LIMIT);
		List<String> values = new ArrayList<>();
		if(latestArticles != null && !latestArticles.isEmpty())
		{
			cmsProxyService.delete("latestArticles");
			for (Article sa : latestArticles) {
				values.add(JsonUtils.toString(sa));
			}
			cmsProxyService.saveToList("latestArticles", values);
		}
		return latestArticles;
	}
	
	
	/**
	 * Insert Latest  Articles to Redis Database
	 */
	public List<Article> saveLatestArticlesOnCreatedToRedis(String contentType)
	{
		List<Article> latestArticles = sitemapArticleDao.getLatestArticlesOnCreated(contentType, StrapiConstants.STRAPI_PAGINATION_LIMIT);
		List<String> values = new ArrayList<>();
		if(latestArticles != null && !latestArticles.isEmpty())
		{
			cmsProxyService.delete("latestArticles");
			for (Article sa : latestArticles) {
				values.add(JsonUtils.toString(sa));
			}
			cmsProxyService.saveToList("latestArticles", values);
		}
		return latestArticles;
	}
	
	/**
	 * Insert Priority  Articles to Redis Database
	 */
	public void savePriorityArticlesToRedis(String contentType)
	{
		List<Article> priorityArticles = sitemapArticleDao.getLatestPriorityArticles(StrapiConstants.LIMIT);
		List<String> values = new ArrayList<>();
		if(priorityArticles != null && !priorityArticles.isEmpty())
		{
			cmsProxyService.delete("priorityArticles");
			for (Article sa : priorityArticles) {
				values.add(JsonUtils.toString(sa));
			}
			cmsProxyService.saveToList("priorityArticles", values);
		}
		
	}

	/**
	 * Get Priority  Articles 
	 */
	public  List<Article> getPriorityArticles()
	{
		List<Article> priorityArticles = sitemapArticleDao.getLatestPriorityArticles(StrapiConstants.STRAPI_PAGINATION_LIMIT);
		return priorityArticles;
	}
	
	/**
	 * Get Priority  Articles 
	 */
	public  void generateSpeedNewsJsons()
	{
		List<Article> speedNewsArticles = sitemapArticleDao.getSpeedNewsArticles(StrapiConstants.STRAPI_PAGINATION_LIMIT);
		dataGeneratorService.generateSpeedNewsListingJson(speedNewsArticles);
	}
	
	
	/**
	 * Insert Latest  Photos to Redis Database
	 */
	/*public void saveLatestPhotosToRedis()
	{
		List<Article> latestArticles = sitemapArticleDao.getPhotos(StrapiConstants.ABN_LISTING_PAGE_LIMIT);
		if(latestArticles != null && !latestArticles.isEmpty())
		{
			cmsProxyService.delete("latestPhotos");
			List<String> values = new ArrayList<>();
			for (Article sa : latestArticles) {
				values.add(JsonUtils.toString(sa));
			}
		
			cmsProxyService.saveToList("latestPhotos", values);
		}
		
	}*/
	
	
	/**
	 * Insert Latest  Videos to Redis Database as per descending order of Created Time
	 */
	public void saveLatestVideosOnCreatedToRedis(String contentType)
	{
		List<Article> latestVideos = sitemapArticleDao.getLatestArticlesOnCreated(contentType, StrapiConstants.REDIS_DATA_LIMIT.intValue());
		if(latestVideos != null && !latestVideos.isEmpty())
		{
			cmsProxyService.delete("latestVideos");
			List<String> values = new ArrayList<>();
			for (Article sa : latestVideos) {
				values.add(JsonUtils.toString(sa));
			}
			
			cmsProxyService.saveToList("latestVideos", values);
		}
		
	}
	
	/**
	 * Insert Latest  Videos to Redis Database
	 */
	public void saveLatestVideosToRedis(String contentType)
	{
		List<Article> latestVideos = sitemapArticleDao.getLatestArticles(contentType, StrapiConstants.REDIS_DATA_LIMIT.intValue());
		if(latestVideos != null && !latestVideos.isEmpty())
		{
			cmsProxyService.delete("latestVideos");
			List<String> values = new ArrayList<>();
			for (Article sa : latestVideos) {
				values.add(JsonUtils.toString(sa));
			}
			
			cmsProxyService.saveToList("latestVideos", values);
		}
		
	}
	
	public boolean checkCategoryContainsSubCategory(Category primaryCategory, SubCategory primarySubCategory)
	{
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		int primarySubCategoryId = primarySubCategory.getId();
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(primarySubCategoryId));
		return contains;
	}
	
	/**
	 * Function to be used during article regeneration
	 * Insert Latest Primary Category related articles to Redis Database in Descending order of
	 * Created Time
	 */
	
	public void saveCategoryRelatedArticlesToRedisForCMS(Category category, boolean rkFlag)
	{
		Integer sitemapArticlePrimaryCategoryId = category.getId();
		List<Article> catRelatedArticles =  sitemapArticleDao.getLatestByCategoryIdOnCreated(sitemapArticlePrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedArticles != null && !catRelatedArticles.isEmpty())
		{
			String redisCatKey = "cat_" + sitemapArticlePrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			for (Article sa : catRelatedArticles) {
				values.add(JsonUtils.toString(sa));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		
		List<String> subcatValues = new ArrayList<>();
		String subCategoriesStr = category.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		for (String subCategoryIdStr : subCategories) 
		{
			if( !subCategoryIdStr.isEmpty() && subCategoryIdStr != "")
			{
				Integer sitemapArticlePrimarySubCategoryId = Integer.valueOf(subCategoryIdStr);
				String redisSubCatKey = "cat_" + sitemapArticlePrimaryCategoryId + "_" + sitemapArticlePrimarySubCategoryId;
				List<Article> subCatRelatedArticles = sitemapArticleDao.getLatestBySubCategoryIdOnCreated(null,sitemapArticlePrimaryCategoryId,sitemapArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
				if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
				{
					cmsProxyService.delete(redisSubCatKey);
					for (Article sa : subCatRelatedArticles) 
					{
						subcatValues.add(JsonUtils.toString(sa));
					}
					cmsProxyService.saveToList(redisSubCatKey, subcatValues);
				}
			}
			
		}
		
		if(rkFlag)
		{
			char c;
			for(c = 'a'; c <= 'z'; ++c)
			{
			List<Article> articles = sitemapArticleDao.getRkArticlesWithLetter(category.getId(), String.valueOf(c), StrapiConstants.RK_LIMIT);
			String redisRKKey = "RK_"+  c;
			cmsProxyService.delete(redisRKKey);
			List<String> rkArticles = new ArrayList<>();
			if(articles != null && !articles.isEmpty())
			{
				for (Article article : articles) {
					rkArticles.add(JsonUtils.toString(article));
				}
				cmsProxyService.saveToList(redisRKKey, rkArticles);
			}
			
		}
	}
		
}
	
	
	/**
	 * Insert Latest Primary Category related articles to Redis Database in Descending order of
	 * Updated Time
	 */
	public void saveCategoryRelatedArticlesToRedis(Article sitemapArticle, boolean rkFlag)
	{
		// Insert Latest Primary Category related articles to Redis Database
		String contentType = sitemapArticle.getContentType();
		Integer sitemapArticlePrimaryCategoryId = sitemapArticle.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(sitemapArticlePrimaryCategoryId);
		Integer sitemapArticlePrimarySubCategoryId = sitemapArticle.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();

		
			List<Article> catRelatedArticles =  sitemapArticleDao.getLatestByCategoryId(sitemapArticlePrimaryCategoryId, StrapiConstants.LIMIT, null);
			List<String> values = new ArrayList<>();
			
			if(catRelatedArticles != null && !catRelatedArticles.isEmpty())
			{
				String redisCatKey = "cat_" + sitemapArticlePrimaryCategoryId;
				cmsProxyService.delete(redisCatKey);
				for (Article sa : catRelatedArticles) {
					values.add(JsonUtils.toString(sa));
				}
				cmsProxyService.saveToList(redisCatKey, values);
			}
		if(subCategoriesStr != null)
		{
			String[] subCategories = subCategoriesStr.split(",");
			
			boolean contains = Arrays.asList(subCategories).contains(String.valueOf(sitemapArticlePrimarySubCategoryId));
			
			List<String> subcatValues = new ArrayList<>();
			if(contains && sitemapArticlePrimarySubCategoryId != null && sitemapArticlePrimarySubCategoryId != 0)
			{
				String redisSubCatKey = "cat_" + sitemapArticlePrimaryCategoryId + "_" + sitemapArticlePrimarySubCategoryId;
				List<Article> subCatRelatedArticles = sitemapArticleDao.getLatestBySubCategoryId(contentType,sitemapArticlePrimaryCategoryId,sitemapArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
				if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
				{
					cmsProxyService.delete(redisSubCatKey);
					for (Article sa : subCatRelatedArticles) 
					{
						subcatValues.add(JsonUtils.toString(sa));
					}
					cmsProxyService.saveToList(redisSubCatKey, subcatValues);
				}
			}
		}

		
		
		
		if(rkFlag)
		{
			char seoSlugFirstLetter = sitemapArticle.getSeoSlug().charAt(0);
			String redisRKKey = "RK_"+  seoSlugFirstLetter;
			long listLength = cmsProxyService.getListLength(redisRKKey);
			while(listLength >= StrapiConstants.RK_LIMIT)
			{
				cmsProxyService.removeFromList(redisRKKey);
			}
			cmsProxyService.leftPushToList(redisRKKey, JsonUtils.toString(sitemapArticle));
		}
	}

	public List<Article> getVideosByPublishedYear(String contentType, int publishedYear) {
		return sitemapArticleDao.getByPublishedYear(contentType, publishedYear);
	}

	
	/**
	 * Insert Latest Primary Category related articles to Redis Database in Descending order of
	 * Created Time
	 */
	public void saveCategoryRelatedArticlesOnCreatedToRedis(Article sitemapArticle, boolean rkFlag)
	{
		// Insert Latest Primary Category related articles to Redis Database
		String contentType = sitemapArticle.getContentType();
		Integer sitemapArticlePrimaryCategoryId = sitemapArticle.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(sitemapArticlePrimaryCategoryId);
		Integer sitemapArticlePrimarySubCategoryId = sitemapArticle.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();

		
			List<Article> catRelatedArticles =  sitemapArticleDao.getLatestByCategoryIdOnCreated(sitemapArticlePrimaryCategoryId, StrapiConstants.LIMIT, null);
			List<String> values = new ArrayList<>();
			
			if(catRelatedArticles != null && !catRelatedArticles.isEmpty())
			{
				String redisCatKey = "cat_" + sitemapArticlePrimaryCategoryId;
				cmsProxyService.delete(redisCatKey);
				for (Article sa : catRelatedArticles) {
					values.add(JsonUtils.toString(sa));
				}
				cmsProxyService.saveToList(redisCatKey, values);
			}
		if(subCategoriesStr != null)
		{
			String[] subCategories = subCategoriesStr.split(",");
			
			boolean contains = Arrays.asList(subCategories).contains(String.valueOf(sitemapArticlePrimarySubCategoryId));
			
			List<String> subcatValues = new ArrayList<>();
			if(contains && sitemapArticlePrimarySubCategoryId != null && sitemapArticlePrimarySubCategoryId != 0)
			{
				String redisSubCatKey = "cat_" + sitemapArticlePrimaryCategoryId + "_" + sitemapArticlePrimarySubCategoryId;
				List<Article> subCatRelatedArticles = sitemapArticleDao.getLatestBySubCategoryIdOnCreated(contentType,sitemapArticlePrimaryCategoryId,sitemapArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
				if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
				{
					cmsProxyService.delete(redisSubCatKey);
					for (Article sa : subCatRelatedArticles) 
					{
						subcatValues.add(JsonUtils.toString(sa));
					}
					cmsProxyService.saveToList(redisSubCatKey, subcatValues);
				}
			}
		}

		
		
		
		if(rkFlag)
		{
			char seoSlugFirstLetter = sitemapArticle.getSeoSlug().charAt(0);
			String redisRKKey = "RK_"+  seoSlugFirstLetter;
			long listLength = cmsProxyService.getListLength(redisRKKey);
			while(listLength >= StrapiConstants.RK_LIMIT)
			{
				cmsProxyService.removeFromList(redisRKKey);
			}
			cmsProxyService.leftPushToList(redisRKKey, JsonUtils.toString(sitemapArticle));
		}
	}

	
	
	
	public List<Article> getAll()
	{
		return sitemapArticleDao.getAll();
	}
	
	public boolean addOrUpdate(Article sitemapArticle) 
	{
		boolean updateFlag = false;
		Article existingSitemapArticle = sitemapArticleDao.getByIdWOPublished(sitemapArticle.getId());
		// if there is a clash of published Year between the child tables then DELETE the existing record,then ADD
		//else only UPDATE
		
		if(existingSitemapArticle == null)
		{
			add(sitemapArticle);
		}
		else
		{
			updateFlag = true;
			if(existingSitemapArticle.getPublishedYear() != sitemapArticle.getPublishedYear())
			{
				sitemapArticleDao.deleteById(existingSitemapArticle.getId());
				add(sitemapArticle);
			}
			else
			{
				update(sitemapArticle);
				//if old seoslug != new seoslug
				if(!existingSitemapArticle.getSeoSlug().equals(sitemapArticle.getSeoSlug()))
				{
					int id = existingSitemapArticle.getId();
					redirectionUrlDao.add(new RedirectionUrl(id, existingSitemapArticle.getUrl().trim(), existingSitemapArticle.getAmpUrl().trim(), existingSitemapArticle.getPublishedYear()));
					List<RedirectionUrl> redirectionUrls = redirectionUrlDao.getById(id);
					for (RedirectionUrl redirectionUrl : redirectionUrls) {
						dataGeneratorService.generateRedirectionPage(sitemapArticle, redirectionUrl);
					}
				}
			}
		}
		
		return updateFlag;
	}
	
	public void add(Article sitemapArticle)
	{
		sitemapArticleDao.add(sitemapArticle);
		//dataGeneratorService.generateArticle(sitemapArticle, categoryList);
	}
	
	public void update(Article sitemapArticle)
	{
		sitemapArticleDao.update(sitemapArticle);
		//dataGeneratorService.generateArticle(sitemapArticle, categoryList);
	}

	
	public List<Article> getLatestArticles(String contentType, Integer limit) 
	{
		return sitemapArticleDao.getLatestArticles(contentType, limit);
	}
	
	public List<Article> getLatestArticlesWithoutPriority(String contentType, Integer limit)
	{
		return sitemapArticleDao.getLatestArticlesWithoutPriority(contentType, limit);
	}
	
	public List<Article> getLatestPhotos() 
	{
		return sitemapArticleDao.getPhotos(StrapiConstants.LIMIT);
	}

	public List<Article> getSubCategoryRelatedArticles(int subCategoryId) 
	{
		return sitemapArticleDao.getSubCategoryRelatedArticles(subCategoryId);
	}
	
	public List<Article> getLatestBySubCategory(String contentType,int categoryId,int subCategoryId, int limit, int offset) 
	{
		return sitemapArticleDao.getLatestBySubCategoryId(contentType, categoryId, subCategoryId,  limit,  offset);
	}
	
	public List<Article> getLatestBySubCategoryWithoutPriority(String contentType,int categoryId,int subCategoryId, int limit, int offset) 
	{
		return sitemapArticleDao.getLatestBySubCategoryIdWithoutPriority(contentType, categoryId, subCategoryId,  limit,  offset);
	}
	
	public List<Article> getLatestBySubCategory(HashMap<String, String> params) 
	{
		Integer limit = StrapiConstants.LIMIT;
		Integer subCategoryId = null;
		Integer categoryId = null;
		Integer offset = null;
		String contentType = null;
		if(params.containsKey("subCategoryId"))
		{
			subCategoryId = Integer.parseInt(params.get("subCategoryId"));
		}
		if(params.containsKey("categoryId"))
		{
			categoryId = Integer.parseInt(params.get("categoryId"));
		}
		
		if(params.containsKey("contentType"))
			limit = Integer.parseInt(params.get("contentType"));			
		if(params.containsKey("limit"))
			limit = Integer.parseInt(params.get("limit"));
		if(params.containsKey("offset"))
			offset = Integer.parseInt(params.get("offset"));
		return sitemapArticleDao.getLatestBySubCategoryId(contentType,categoryId,subCategoryId, limit, offset);
	}
	
	public List<Article> getCategoryRelatedArticles(Integer subCategoryId) 
	{
		return sitemapArticleDao.getByCategoryId(subCategoryId);
	}
	
	public List<Article> getLatestByCategoryIdWithoutPriority(Integer categoryId, Integer limit) 
	{
		return sitemapArticleDao.getLatestByCategoryIdWithoutPriority(categoryId, limit, null);
	}
	
	public List<Article> getLatestByCategory(Integer categoryId, Integer limit) 
	{
		return sitemapArticleDao.getLatestByCategoryId(categoryId, limit, null);
	}
	
	public List<Article> getByIds(List<Long> ids) {
		return sitemapArticleDao.getByIds(ids);
	}

	public Article getById(int id) {
		return sitemapArticleDao.getById(id);
	}
	
	public Article getByIdWOPublished(int id) {
		return sitemapArticleDao.getByIdWOPublished(id);
	}
	
	/*public Article getByAbnStoryId(String abnStoryId) {
		return sitemapArticleDao.getByAbnStoryId(abnStoryId);
	}*/

	public List<Date> getDistinctPublishDates() {
		
		return sitemapArticleDao.getDistinctPublishDates();
	}
	
	public List<Integer> getDistinctPublishYears() 
	{
		return sitemapArticleDao.getDistinctPublishYears();
	}
	
	public Date getMaxArticlePublishDate() 
	{
		return sitemapArticleDao.getMaxArticlePublishDate();
	}

	public List<Article> getByPublishedDate(Date date) {
		
		return sitemapArticleDao.getByPublishedDate(date);
	}

	public List<Article> getPhotos() {
		return sitemapArticleDao.getPhotos(null);
	}

	public List<Article> getVideos() {
		return sitemapArticleDao.getVideos(null);
	}


	public void generateVideoDetailPage(Article sitemapArticle, List<StrapiCategory> categoryList, boolean updateFlag) throws Exception {
		String relatedArticlesStr = sitemapArticle.getRelatedArticles();
		String[] relatedArticleIds = relatedArticlesStr.split(",");
		List<Article> relatedArticles = new ArrayList<>();
		
		if(relatedArticleIds.length > 0)
		{
			for (String relatedArticleId : relatedArticleIds) 
			{
				if(relatedArticleId.length() > 0)
				{
					Article sa = sitemapArticleDao.getById(Integer.parseInt(relatedArticleId));
					relatedArticles.add(sa);
				}
			}
		}
		
		String videos = sitemapArticle.getVideos();
		LinkedHashMap<String, String> videosHm  = JsonUtils.deserialize(videos, LinkedHashMap.class);
		String primaryVideoType = null;
		String primaryVideoUrl = null;
		if(!videosHm.entrySet().isEmpty()  && videosHm.entrySet().iterator().next() != null)
		{
			Entry<String, String> hmEntry =  videosHm.entrySet().iterator().next();
			primaryVideoType = hmEntry.getKey();
			primaryVideoUrl = hmEntry.getValue();
		}
		else
		{
			primaryVideoType = "Youtube";
			primaryVideoUrl = "https://www.youtube.com/embed/zwES4--H1aM";
		}
		dataGeneratorService.generateVideoDetail(sitemapArticle,primaryVideoType, primaryVideoUrl, categoryList, relatedArticles, updateFlag);
		dataGeneratorService.generateVideoDetailAmp(sitemapArticle,primaryVideoType, primaryVideoUrl, categoryList, relatedArticles, updateFlag);

	}

	public void generateRKPage(int primaryCategoryId, List<StrapiCategory> categoryList, boolean updateFlag) {
		Category primaryCategory = sitemapCategoryDao.getById(primaryCategoryId);
		List<Article> latestCategoryArticles = sitemapArticleDao.getLatestByCategoryIdOnCreated(primaryCategoryId, StrapiConstants.LIMIT, null);
		dataGeneratorService.generateRKPage(primaryCategory, categoryList, latestCategoryArticles, updateFlag);
		
	}
	
	public void generateTagPage(Article sitemapArticle, List<StrapiCategory> categoryList) {
		String tagsStr = sitemapArticle.getTags();
		String[] tags = tagsStr.split(",");
		
		String tagUrlsStr = sitemapArticle.getTagUrls();
		String[] tagUrls = tagUrlsStr.split(",");
		
		for (int i=0; i< tags.length; i++) {
			List<Article> tagRelatedArticles = sitemapArticleDao.getTagRelatedArticles(tags[i], StrapiConstants.STRAPI_PAGINATION_LIMIT);
			if(tagRelatedArticles!= null && tagRelatedArticles.size() > 0)
			{
				dataGeneratorService.generateTagListing(tags[i], tagUrls[i], categoryList, tagRelatedArticles);
			}
		}
	}
	
	public List<Article> getTagRelatedArticles(String tag)
	{
		List<Article> tagRelatedArticles = sitemapArticleDao.getTagRelatedArticles(tag, StrapiConstants.STRAPI_PAGINATION_LIMIT);
		return tagRelatedArticles;
	}

	public List<Article> getCategoryRelatedArticlesFromRedis(Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Article> getLatestVideosFromCMS() throws Exception {
		List<String> articlesStrList = cmsProxyService.getList("latestVideos", 0, StrapiConstants.LIMIT);
		List<Article> latestVideos = new ArrayList<>();
		if(!articlesStrList.isEmpty())
		{
			Article sitemapArticle;
			for (String saStr : articlesStrList) 
			{
				sitemapArticle = JsonUtils.deserialize(saStr, Article.class);
				latestVideos.add(sitemapArticle);
			}
		}
		return latestVideos;
	}
	
	
	public List<Article> getLatestArticlesFromCMS() throws Exception 
	{
		List<String> articlesStrList = cmsProxyService.getList("latestArticles", 0, 10);
		List<Article> latestArticles = new ArrayList<>();
		if(!articlesStrList.isEmpty())
		{
			Article sitemapArticle;
			for (String saStr : articlesStrList) 
			{
				sitemapArticle = JsonUtils.deserialize(saStr, Article.class);
				latestArticles.add(sitemapArticle);
			}
		}
		return latestArticles;
	}
	
	public List<Article> getPriorityArticlesFromCMS() throws Exception {
		List<String> articlesStrList = cmsProxyService.getList("priorityArticles", 0, StrapiConstants.LIMIT);
		List<Article> priorityArticles = new ArrayList<>();
		if(!articlesStrList.isEmpty())
		{
			Article sitemapArticle;
			for (String saStr : articlesStrList) 
			{
				sitemapArticle = JsonUtils.deserialize(saStr, Article.class);
				priorityArticles.add(sitemapArticle);
			}
		}
		return priorityArticles;
	}
	
	public List<Object> getRankingArticles() throws Exception {
		List<String> rankingArticlesStrList = cmsProxyService.getList("rankingArticles", 0, StrapiConstants.LIMIT);
		List<Object> rankingArticles = new ArrayList<>();
		if(!rankingArticlesStrList.isEmpty())
		{
			Object ra;
			for (String saStr : rankingArticlesStrList) 
			{
				ra = JsonUtils.deserialize(saStr, Object.class);
				rankingArticles.add(ra);
			}
		}
		return rankingArticles;
	}

	public void generatePriorityArticleListingPage(List<Article> priorityArticles,  List<StrapiCategory> categoryList)
	{
		dataGeneratorService.generatePriorityArticleListingPage(priorityArticles,categoryList);
	}

	public void generateJSON(Article sitemapArticle) {
		AppJson articleJson = new AppJson();
		articleJson.setVersion("1.0");
		articleJson.setModelType(StrapiConstants.STRAPI_MODEL_ARTICLE);
		articleJson.setModel(sitemapArticle);
		
		dataGeneratorService.generateAppJson(sitemapArticle.getId(), sitemapArticle.getUrl(), articleJson);
	}

	
}
