package com.andromeda.cms.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CJArticleDao;
import com.andromeda.cms.dao.CJCategoryDao;
import com.andromeda.cms.dao.CJPhotoGalleryDao;
import com.andromeda.cms.dao.CJRedirectionUrlDao;
import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.CJRedirectionUrl;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.commons.util.JsonUtils;

@Service
public class CJArticleService {
		private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		
		@Autowired
		private CJArticleDao cjArticleDao;
		
		@Autowired
		private CJPhotoGalleryDao cjPhotoGalleryDao;
		
		@Autowired
		private CJCategoryDao cjCategoryDao;
		
		@Autowired
		private CJSubCategoryDao cjSubCategoryDao;
		
		@Autowired
		private CJDataGeneratorService dataGeneratorService;
		
		@Autowired
		private CmsProxyService cmsProxyService;
		
		@Autowired
		CJRedirectionUrlDao cjRedirectionUrlDao;

		public void setCjArticleDao(CJArticleDao cjArticleDao)
		{
			this.cjArticleDao = cjArticleDao;
		}
		
		public void setCjPhotoGalleryDao(CJPhotoGalleryDao cjPhotoGalleryDao)
		{
			this.cjPhotoGalleryDao = cjPhotoGalleryDao;
		}
		
		public void setCjCategoryDao(CJCategoryDao cjCategoryDao)
		{
			this.cjCategoryDao = cjCategoryDao;
		}
		
		public void setCjSubCategoryDao(CJSubCategoryDao cjSubCategoryDao)
		{
			this.cjSubCategoryDao = cjSubCategoryDao;
		}
		
		public void setCjDataGeneratorService(CJDataGeneratorService chitrajyothyDataGeneratorService)
		{
			this.dataGeneratorService = chitrajyothyDataGeneratorService;
		}
		
		public void setCmsProxyService(CmsProxyService cmsProxyService)
		{
			this.cmsProxyService = cmsProxyService;
		}

		public void generateLatestArticleListingPage(List<CJArticle> latestChitraArticles, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory, List<StrapiCategory> categoryList)
		{
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.LIMIT);
			List<CJArticle> rkArticles = cjArticleDao.getLatestByCategoryId(cjRkCategory.getId(), StrapiConstants.LIMIT, null);
			List<CJArticle> ottArticles = cjArticleDao.getLatestByCategoryId(cjOttCategory.getId(), StrapiConstants.LIMIT, null);

			dataGeneratorService.generateLatestArticleListingPage(latestChitraArticles,latestPhotos, rkArticles, ottArticles ,categoryList);
		}
		
		
		public void generatePriorityArticleListingPage(List<CJArticle> priorityChitraArticles, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory, List<StrapiCategory> categoryList)
		{
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.LIMIT);
			List<CJArticle> rkArticles = cjArticleDao.getLatestByCategoryId(cjRkCategory.getId(), StrapiConstants.LIMIT, null);
			List<CJArticle> ottArticles = cjArticleDao.getLatestByCategoryId(cjOttCategory.getId(), StrapiConstants.LIMIT, null);

			dataGeneratorService.generatePriorityArticleListingPage(priorityChitraArticles,latestPhotos, rkArticles, ottArticles ,categoryList);
		}
		
		public void generateArticlePage(CJArticle chitrajyothyArticle, List<StrapiCategory> categoryList, boolean updateFlag)
		{
			String relatedArticlesStr = chitrajyothyArticle.getRelatedArticles();
			String[] relatedArticleIds = relatedArticlesStr.split(",");
			List<CJArticle> relatedArticles = new ArrayList<>();
			
			if(relatedArticleIds.length > 0)
			{
				for (String relatedArticleId : relatedArticleIds) 
				{
					if(relatedArticleId.length() > 0)
					{
						CJArticle sa = cjArticleDao.getById(Integer.parseInt(relatedArticleId));
						relatedArticles.add(sa);
					}
				}
			}
			dataGeneratorService.generateArticle(chitrajyothyArticle,categoryList, relatedArticles, updateFlag);
			dataGeneratorService.generateAmpArticle(chitrajyothyArticle,categoryList, relatedArticles, updateFlag);
		}
		
		
		public synchronized void generateLandingAndListingPageForCMS(CJCategory primaryCategory, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory,List<StrapiCategory> categoryList)
		{
			int categoryId = primaryCategory.getId();
			List<CJArticle> latestCategoryArticles = cjArticleDao.getLatestByCategoryId(categoryId, StrapiConstants.LIMIT, null);
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.LIMIT);

			CJCategory videoCategory = cjCategoryDao.getByName(StrapiConstants.STRAPI_CATEGORY_VIDEOGALLERY);
			
			String subCategoriesStr = primaryCategory.getSubCategories();
			String[] subCategories = null;
			if(subCategoriesStr != null)
				subCategories = subCategoriesStr.split(",");
						
			HashMap<CJSubCategory, List<CJArticle>> dataHashMap = new HashMap<>();
			if(subCategoriesStr != null && subCategories != null)
			{
				for (String subCategoryIdStr : subCategories) 
				{
					if(!subCategoryIdStr.equals(""))
					{
						CJSubCategory subCategory = cjSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
						List<CJArticle> chitrajyothyArticles = cjArticleDao.getLatestBySubCategoryId(null,categoryId,Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
						if(chitrajyothyArticles!= null && chitrajyothyArticles.size()>0)
						{
							dataHashMap.put(subCategory, chitrajyothyArticles);
						}
						
						generateListingPageForCMS(primaryCategory, cjRkCategory, cjOttCategory,subCategory, categoryList);
					}
				}
				if(categoryId != videoCategory.getId())
				{
					System.out.println("Category Landing and Listing");
					dataGeneratorService.generateCategoryLanding(primaryCategory, categoryList, latestCategoryArticles,latestPhotos,dataHashMap);
				}
				else if(categoryId == videoCategory.getId())
				{
					System.out.println("Video Landing and Listing");
					dataGeneratorService.generateVideoLanding(primaryCategory, categoryList, latestCategoryArticles,latestPhotos, dataHashMap);
					//dataGeneratorService.generateLiveTV(primaryCategory, categoryList, dataHashMap);
				}
				
			}
			
			else
				generateListingPageForCMS(primaryCategory, cjRkCategory, cjOttCategory, null, categoryList);

		}
		
		public synchronized void generateLandingAndListingPage(CJArticle chitrajyothyArticle, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory ,List<StrapiCategory> categoryList)
		{
			String contentType = chitrajyothyArticle.getContentType();
			
			int categoryId = chitrajyothyArticle.getPrimaryCategoryId();
			CJCategory primaryCategory = cjCategoryDao.getById(categoryId);
			List<CJArticle> latestCategoryArticles = cjArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.LIMIT, null);
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotosOnCreated(StrapiConstants.LIMIT);
			int subCategoryId = chitrajyothyArticle.getPrimarySubCategoryId();
			
			String subCategoriesStr = primaryCategory.getSubCategories();
			String[] subCategories = null;
			if(subCategoriesStr != null)
			{
				subCategories = subCategoriesStr.split(",");
				
				boolean contains = Arrays.asList(subCategories).contains(String.valueOf(subCategoryId));
				
				HashMap<CJSubCategory, List<CJArticle>> dataHashMap = new HashMap<>();
				if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
				{
					for (String subCategoryIdStr : subCategories) 
					{
						if(!subCategoryIdStr.equals(""))
						{
							CJSubCategory subCategory = cjSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
							List<CJArticle> chitrajyothyArticles = cjArticleDao.getLatestBySubCategoryIdOnCreated(contentType, categoryId,Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
							if(chitrajyothyArticles!= null && chitrajyothyArticles.size()>0)
							{
								dataHashMap.put(subCategory, chitrajyothyArticles);
							}
						}
					}
				}
				
				if(subCategories != null  && subCategories.length > 0 && !subCategoriesStr.equals(""))
				{	
					if(contentType.equalsIgnoreCase("article"))
					{
						System.out.println("Chitrajyothy Category Landing and Listing");
						dataGeneratorService.generateCategoryLanding(primaryCategory, categoryList, latestCategoryArticles,latestPhotos,dataHashMap);
					}
					else if(contentType.equalsIgnoreCase("video gallery"))
					{
						System.out.println("Chitrajyothy Video Landing and Listing");
						dataGeneratorService.generateVideoLanding(primaryCategory, categoryList, latestCategoryArticles,latestPhotos,dataHashMap);
					}
					
					if(contains)
					{
						generateListingPage(chitrajyothyArticle, cjRkCategory, cjOttCategory ,categoryList);
					}
				}
			}
			else
			{
				generateListingPage(chitrajyothyArticle, cjRkCategory , cjOttCategory, categoryList);
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
		public synchronized void generateListingPage(CJArticle chitrajyothyArticle, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory, List<StrapiCategory> categoryList) 
		{
			String contentType = chitrajyothyArticle.getContentType();
			int categoryId = chitrajyothyArticle.getPrimaryCategoryId();
			CJCategory category = cjCategoryDao.getById(categoryId);
			
			int subCategoryId = chitrajyothyArticle.getPrimarySubCategoryId();
			CJSubCategory subCategory = cjSubCategoryDao.getById(subCategoryId);
			
			List<CJArticle> chitrajyothyArticles = null;
			if( subCategory != null && subCategoryId != 0)
			{
				chitrajyothyArticles = cjArticleDao.getLatestBySubCategoryIdOnCreated(contentType, categoryId, subCategoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
			}
			else
			{
				chitrajyothyArticles = cjArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
			}
			
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotosOnCreated(StrapiConstants.LIMIT);
			List<CJArticle> rkArticles = cjArticleDao.getLatestByCategoryIdOnCreated(cjRkCategory.getId(), StrapiConstants.LIMIT, null);
			List<CJArticle> ottArticles = cjArticleDao.getLatestByCategoryIdOnCreated(cjOttCategory.getId(), StrapiConstants.LIMIT, null);

			if(contentType.equalsIgnoreCase("article"))
				dataGeneratorService.generateCategoryListing(category, subCategory, categoryList, chitrajyothyArticles, latestPhotos, rkArticles, ottArticles);
			else if(contentType.equalsIgnoreCase("video gallery"))
				dataGeneratorService.generateVideoListing(category, subCategory, categoryList, chitrajyothyArticles, latestPhotos, rkArticles, ottArticles);
		}
		
		public synchronized void generateListingPageForCMS(CJCategory category, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory,CJSubCategory subCategory, List<StrapiCategory> categoryList) 
		{
			int categoryId = category.getId();
			CJCategory videoCategory = cjCategoryDao.getByName(StrapiConstants.STRAPI_CATEGORY_VIDEOGALLERY);

			List<CJArticle> chitrajyothyArticles = null;
			if( subCategory != null)
			{
				int subCategoryId = subCategory.getId();
				chitrajyothyArticles = cjArticleDao.getLatestBySubCategoryIdOnCreated(null, categoryId,subCategoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
			}
			else
			{
				chitrajyothyArticles = cjArticleDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.STRAPI_PAGINATION_LIMIT, null);
			}
			
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotosOnCreated(StrapiConstants.LIMIT);
			List<CJArticle> rkArticles = cjArticleDao.getLatestByCategoryIdOnCreated(cjRkCategory.getId(), StrapiConstants.LIMIT, null);
			List<CJArticle> ottArticles = cjArticleDao.getLatestByCategoryIdOnCreated(cjOttCategory.getId(), StrapiConstants.LIMIT, null);

			
			if(categoryId != videoCategory.getId() )
				dataGeneratorService.generateCategoryListing(category, subCategory, categoryList, chitrajyothyArticles, latestPhotos, rkArticles, ottArticles);
			else if(categoryId == videoCategory.getId())
				dataGeneratorService.generateVideoListing(category, subCategory, categoryList, chitrajyothyArticles, latestPhotos, rkArticles, ottArticles);
		}
		
		/**
		 * Insert Latest  Articles to Redis Database
		 */
		public List<CJArticle> saveLatestArticlesToRedis(String contentType)
		{
			List<CJArticle> latestArticles = cjArticleDao.getLatestArticles(contentType, StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_TOTAL_LIMIT);
			List<String> values = new ArrayList<>();
			if(latestArticles != null && !latestArticles.isEmpty())
			{
				cmsProxyService.delete("cj_latestArticles");
				for (CJArticle cja : latestArticles) {
					values.add(JsonUtils.toString(cja));
				}
				cmsProxyService.saveToList("cj_latestArticles", values);
			}
			return latestArticles;
		}
		
		/**
		 * Insert Latest  Articles to Redis Database
		 */
		public List<CJArticle> saveLatestArticlesOnCreatedToRedis(String contentType)
		{
			List<CJArticle> latestArticles = cjArticleDao.getLatestArticlesOnCreated(contentType, StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_TOTAL_LIMIT);
			List<String> values = new ArrayList<>();
			if(latestArticles != null && !latestArticles.isEmpty())
			{
				cmsProxyService.delete("cj_latestArticles");
				for (CJArticle cja : latestArticles) {
					values.add(JsonUtils.toString(cja));
				}
				cmsProxyService.saveToList("cj_latestArticles", values);
			}
			return latestArticles;
		}
		
		/**
		 * Insert Priority  Articles to Redis Database
		 */
		public void savePriorityArticlesToRedis(String contentType)
		{
			List<CJArticle> priorityArticles = cjArticleDao.getLatestPriorityArticles(StrapiConstants.LIMIT);
			List<String> values = new ArrayList<>();
			if(priorityArticles != null && !priorityArticles.isEmpty())
			{
				cmsProxyService.delete("cj_priorityArticles");
				for (CJArticle cja : priorityArticles) {
					values.add(JsonUtils.toString(cja));
				}
				cmsProxyService.saveToList("cj_priorityArticles", values);
			}
			
		}
		
		/**
		 * Get Priority  Articles 
		 */
		public  List<CJArticle> getPriorityArticles()
		{
			List<CJArticle> priorityArticles = cjArticleDao.getLatestPriorityArticles(StrapiConstants.STRAPI_PAGINATION_LIMIT);
			return priorityArticles;
		}
		
		
		/**
		 * Insert Latest  Videos to Redis Database
		 */
		public void saveLatestVideosToRedis(String contentType)
		{
			List<CJArticle> latestVideos = cjArticleDao.getLatestArticles(contentType, StrapiConstants.REDIS_DATA_LIMIT.intValue());
			if(latestVideos != null && !latestVideos.isEmpty())
			{
				cmsProxyService.delete("cj_latestVideos");
				List<String> values = new ArrayList<>();
				for (CJArticle cja : latestVideos) {
					values.add(JsonUtils.toString(cja));
				}
				
				cmsProxyService.saveToList("cj_latestVideos", values);
			}
			
		}
		
		/**
		 * Insert Latest  Videos to Redis Database 
		 * in descending order of Created Time
		 */
		public void saveLatestVideosToRedisOnCreated(String contentType)
		{
			List<CJArticle> latestVideos = cjArticleDao.getLatestArticlesOnCreated(contentType, StrapiConstants.REDIS_DATA_LIMIT.intValue());
			if(latestVideos != null && !latestVideos.isEmpty())
			{
				cmsProxyService.delete("cj_latestVideos");
				List<String> values = new ArrayList<>();
				for (CJArticle cja : latestVideos) {
					values.add(JsonUtils.toString(cja));
				}
				
				cmsProxyService.saveToList("cj_latestVideos", values);
			}
			
		}
		
		public boolean checkCategoryContainsSubCategory(CJCategory primaryCategory, CJSubCategory primarySubCategory)
		{
			String subCategoriesStr = primaryCategory.getSubCategories();
			String[] subCategories = subCategoriesStr.split(",");
			int primarySubCategoryId = primarySubCategory.getId();
			boolean contains = Arrays.asList(subCategories).contains(String.valueOf(primarySubCategoryId));
			return contains;
		}
		
		public void saveCategoryRelatedArticlesToRedisForCMS(CJCategory category, boolean rkFlag)
		{
			Integer cjArticlePrimaryCategoryId = category.getId();
			List<CJArticle> catRelatedArticles =  cjArticleDao.getLatestByCategoryId(cjArticlePrimaryCategoryId, StrapiConstants.LIMIT, null);
			List<String> values = new ArrayList<>();
			
			if(catRelatedArticles != null && !catRelatedArticles.isEmpty())
			{
				String redisCatKey = "cj_cat_" + cjArticlePrimaryCategoryId;
				cmsProxyService.delete(redisCatKey);
				for (CJArticle cja : catRelatedArticles) {
					values.add(JsonUtils.toString(cja));
				}
				cmsProxyService.saveToList(redisCatKey, values);
			}
			
			List<String> subcatValues = new ArrayList<>();
			String subCategoriesStr = category.getSubCategories();
			if(subCategoriesStr != null)
			{
				String[] subCategories = subCategoriesStr.split(",");
				for (String subCategoryIdStr : subCategories) 
				{
					if( !subCategoryIdStr.isEmpty() && subCategoryIdStr != "")
					{
						Integer cjArticlePrimarySubCategoryId = Integer.valueOf(subCategoryIdStr);
						String redisSubCatKey = "cj_cat_" + cjArticlePrimaryCategoryId + "_" + cjArticlePrimarySubCategoryId;
						List<CJArticle> subCatRelatedArticles = cjArticleDao.getLatestBySubCategoryId(null,cjArticlePrimaryCategoryId,cjArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
						if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
						{
							cmsProxyService.delete(redisSubCatKey);
							for (CJArticle sa : subCatRelatedArticles) 
							{
								subcatValues.add(JsonUtils.toString(sa));
							}
							cmsProxyService.saveToList(redisSubCatKey, subcatValues);
						}
					}
					
				}
			}
	}
		
		
		/**
		 * Insert Latest Primary Category related articles to Redis Database
		 * in Descending order of Updated time
		 */
		public void saveCategoryRelatedArticlesToRedis(CJArticle cjArticle, boolean rkFlag)
		{
			// Insert Latest Primary Category related articles to Redis Database
			String contentType = cjArticle.getContentType();
			Integer cjArticlePrimaryCategoryId = cjArticle.getPrimaryCategoryId();
			CJCategory primaryCategory = cjCategoryDao.getById(cjArticlePrimaryCategoryId);
			Integer cjArticlePrimarySubCategoryId = cjArticle.getPrimarySubCategoryId();
			
			String subCategoriesStr = primaryCategory.getSubCategories();

			
			List<CJArticle> catRelatedArticles =  cjArticleDao.getLatestByCategoryId(cjArticlePrimaryCategoryId, StrapiConstants.LIMIT, null);
			List<String> values = new ArrayList<>();
			
			if(catRelatedArticles != null && !catRelatedArticles.isEmpty())
			{
				String redisCatKey = "cj_cat_" + cjArticlePrimaryCategoryId;
				cmsProxyService.delete(redisCatKey);
				for (CJArticle sa : catRelatedArticles) {
					values.add(JsonUtils.toString(sa));
				}
				cmsProxyService.saveToList(redisCatKey, values);
			}
			
			if(subCategoriesStr != null)
			{
				String[] subCategories = subCategoriesStr.split(",");
				
				boolean contains = Arrays.asList(subCategories).contains(String.valueOf(cjArticlePrimarySubCategoryId));
				
				List<String> subcatValues = new ArrayList<>();
				if(contains && cjArticlePrimaryCategoryId != null && cjArticlePrimarySubCategoryId != 0)
				{
					String redisSubCatKey = "cj_cat_" + cjArticlePrimaryCategoryId + "_" + cjArticlePrimarySubCategoryId;
					List<CJArticle> subCatRelatedArticles = cjArticleDao.getLatestBySubCategoryId(contentType, cjArticlePrimaryCategoryId, cjArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
					if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
					{
						cmsProxyService.delete(redisSubCatKey);
						for (CJArticle sa : subCatRelatedArticles) 
						{
							subcatValues.add(JsonUtils.toString(sa));
						}
						cmsProxyService.saveToList(redisSubCatKey, subcatValues);
					}
				}
			}

		}
		
		/**
		 * Insert Latest Primary Category related articles to Redis Database
		 * in Descending order of Created time
		 */
		public void saveCategoryRelatedArticlesOnCreatedToRedis(CJArticle cjArticle)
		{
			// Insert Latest Primary Category related articles to Redis Database
			String contentType = cjArticle.getContentType();
			Integer cjArticlePrimaryCategoryId = cjArticle.getPrimaryCategoryId();
			CJCategory primaryCategory = cjCategoryDao.getById(cjArticlePrimaryCategoryId);
			Integer cjArticlePrimarySubCategoryId = cjArticle.getPrimarySubCategoryId();
			
			String subCategoriesStr = primaryCategory.getSubCategories();

			
			List<CJArticle> catRelatedArticles =  cjArticleDao.getLatestByCategoryIdOnCreated(cjArticlePrimaryCategoryId, StrapiConstants.LIMIT, null);
			List<String> values = new ArrayList<>();
			
			if(catRelatedArticles != null && !catRelatedArticles.isEmpty())
			{
				String redisCatKey = "cj_cat_" + cjArticlePrimaryCategoryId;
				cmsProxyService.delete(redisCatKey);
				for (CJArticle sa : catRelatedArticles) {
					values.add(JsonUtils.toString(sa));
				}
				cmsProxyService.saveToList(redisCatKey, values);
			}
			
			if(subCategoriesStr != null)
			{
				String[] subCategories = subCategoriesStr.split(",");
				
				boolean contains = Arrays.asList(subCategories).contains(String.valueOf(cjArticlePrimarySubCategoryId));
				
				List<String> subcatValues = new ArrayList<>();
				if(contains && cjArticlePrimaryCategoryId != null && cjArticlePrimarySubCategoryId != 0)
				{
					String redisSubCatKey = "cj_cat_" + cjArticlePrimaryCategoryId + "_" + cjArticlePrimarySubCategoryId;
					List<CJArticle> subCatRelatedArticles = cjArticleDao.getLatestBySubCategoryIdOnCreated(contentType, cjArticlePrimaryCategoryId, cjArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
					if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
					{
						cmsProxyService.delete(redisSubCatKey);
						for (CJArticle sa : subCatRelatedArticles) 
						{
							subcatValues.add(JsonUtils.toString(sa));
						}
						cmsProxyService.saveToList(redisSubCatKey, subcatValues);
					}
				}
			}

		}

		public List<CJArticle> getVideosByPublishedYear(String contentType, int publishedYear) {
			return cjArticleDao.getByPublishedYear(contentType, publishedYear);
		}

		

		
		public boolean addOrUpdate(CJArticle chitrajyothyArticle) 
		{
			boolean updateFlag = false;
			CJArticle existingCJArticle = cjArticleDao.getByIdWOPublished(chitrajyothyArticle.getId());
			// if there is a clash of published Year between the child tables then DELETE the existing record,then ADD
			//else only UPDATE
			
			if(existingCJArticle == null)
			{
				System.out.println("Adding article " + JsonUtils.toString(chitrajyothyArticle));
				add(chitrajyothyArticle);
			}
			else
			{
				updateFlag = true;
				System.out.println("Updating article " + JsonUtils.toString(existingCJArticle));
				if(existingCJArticle.getPublishedYear() != chitrajyothyArticle.getPublishedYear())
				{
					cjArticleDao.deleteById(existingCJArticle.getId());
					add(chitrajyothyArticle);
				}
				else
				{
					update(chitrajyothyArticle);
					//if old seoslug != new seoslug
					if(!existingCJArticle.getSeoSlug().equals(chitrajyothyArticle.getSeoSlug()))
					{
						int id = existingCJArticle.getId();
						cjRedirectionUrlDao.add(new CJRedirectionUrl(id, existingCJArticle.getUrl().trim(), existingCJArticle.getAmpUrl().trim(), existingCJArticle.getPublishedYear()));
						List<CJRedirectionUrl> redirectionUrls = cjRedirectionUrlDao.getById(id);
						for (CJRedirectionUrl redirectionUrl : redirectionUrls) {
							dataGeneratorService.generateRedirectionPage(chitrajyothyArticle, redirectionUrl);
						}
					}
				}
			}
			
			return updateFlag;
		}
		
		public void add(CJArticle sitemapArticle)
		{
			cjArticleDao.add(sitemapArticle);
			//dataGeneratorService.generateArticle(sitemapArticle, categoryList);
		}
		
		public void update(CJArticle sitemapArticle)
		{
			cjArticleDao.update(sitemapArticle);
			//dataGeneratorService.generateArticle(sitemapArticle, categoryList);
		}

		
		public List<CJArticle> getLatestArticles(String contentType, Integer limit) 
		{
			return cjArticleDao.getLatestArticles(contentType, limit);
		}
		
		public List<CJArticle> getLatestArticlesOnCreated(String contentType, Integer limit) 
		{
			return cjArticleDao.getLatestArticlesOnCreated(contentType, limit);
		}
		
		public List<CJArticle> getLatestArticlesWithoutPriority(String contentType, Integer limit)
		{
			return cjArticleDao.getLatestArticlesWithoutPriority(contentType, limit);
		}
		
		public List<CJArticle> getLatestPhotos() 
		{
			return cjArticleDao.getPhotos(StrapiConstants.LIMIT);
		}

		public List<CJArticle> getSubCategoryRelatedArticles(int subCategoryId) 
		{
			return cjArticleDao.getSubCategoryRelatedArticles(subCategoryId);
		}
		
		public List<CJArticle> getLatestBySubCategory(String contentType,Integer categoryId,Integer subCategoryId, Integer limit, Integer offset) 
		{
			return cjArticleDao.getLatestBySubCategoryId(contentType, categoryId, subCategoryId,  limit,  offset);
		}
		
		public List<CJArticle> getLatestBySubCategoryWithoutPriority(String contentType,int categoryId,int subCategoryId, int limit, int offset) 
		{
			return cjArticleDao.getLatestBySubCategoryIdWithoutPriority(contentType, categoryId, subCategoryId,  limit,  offset);
		}
		
		public List<CJArticle> getLatestBySubCategory(HashMap<String, String> params) 
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
			return cjArticleDao.getLatestBySubCategoryId(contentType,categoryId,subCategoryId, limit, offset);
		}
		
		public List<CJArticle> getCategoryRelatedArticles(Integer subCategoryId) 
		{
			return cjArticleDao.getByCategoryId(subCategoryId);
		}
		
		public List<CJArticle> getLatestByCategoryIdWithoutPriority(Integer categoryId, Integer limit) 
		{
			return cjArticleDao.getLatestByCategoryIdWithoutPriority(categoryId, limit, null);
		}
		
		public List<CJArticle> getLatestByCategory(Integer categoryId, Integer limit) 
		{
			return cjArticleDao.getLatestByCategoryId(categoryId, limit, null);
		}

		public CJArticle getById(int id) {
			return cjArticleDao.getById(id);
		}
		
		public List<CJArticle> getByIds(List<Long> ids) {
			return cjArticleDao.getByIds(ids);
		}
		
		public CJArticle getByIdWOPublished(int id) {
			return cjArticleDao.getByIdWOPublished(id);
		}
		
		/*public Article getByAbnStoryId(String abnStoryId) {
			return sitemapArticleDao.getByAbnStoryId(abnStoryId);
		}*/

		public List<Date> getDistinctPublishDates() {
			
			return cjArticleDao.getDistinctPublishDates();
		}
		
		public List<Integer> getDistinctPublishYears() 
		{
			return cjArticleDao.getDistinctPublishYears();
		}
		
		public Date getMaxArticlePublishDate() 
		{
			return cjArticleDao.getMaxArticlePublishDate();
		}

		public List<CJArticle> getByPublishedDate(Date date) {
			
			return cjArticleDao.getByPublishedDate(date);
		}



		public List<CJArticle> getVideos() {
			return cjArticleDao.getVideos(null);
		}


		public void generateVideoDetailPage(CJArticle chitrajyothyArticle, List<StrapiCategory> categoryList, boolean updateFlag) throws Exception {
			String relatedArticlesStr = chitrajyothyArticle.getRelatedArticles();
			String[] relatedArticleIds = relatedArticlesStr.split(",");
			List<CJArticle> relatedArticles = new ArrayList<>();
			
			if(relatedArticleIds.length > 0)
			{
				for (String relatedArticleId : relatedArticleIds) 
				{
					if(relatedArticleId.length() > 0)
					{
						CJArticle cja = cjArticleDao.getById(Integer.parseInt(relatedArticleId));
						relatedArticles.add(cja);
					}
				}
			}
			
			String videos = chitrajyothyArticle.getVideos();
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
			dataGeneratorService.generateVideoDetail(chitrajyothyArticle,primaryVideoType, primaryVideoUrl, categoryList, relatedArticles, updateFlag);
			dataGeneratorService.generateVideoDetailAmp(chitrajyothyArticle,primaryVideoType, primaryVideoUrl, categoryList, relatedArticles, updateFlag);

		}

		
		public void generateTagPage(CJArticle chitrajyothyArticle, StrapiCategory cjRkCategory, StrapiCategory cjOttCategory, List<StrapiCategory> categoryList) {
			List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.LIMIT);
			List<CJArticle> rkArticles = cjArticleDao.getLatestByCategoryId(cjRkCategory.getId(), StrapiConstants.LIMIT, null);
			List<CJArticle> ottArticles = cjArticleDao.getLatestByCategoryId(cjOttCategory.getId(), StrapiConstants.LIMIT, null);

			String tagsStr = chitrajyothyArticle.getTags();
			String[] tags = tagsStr.split(",");
			
			String tagUrlsStr = chitrajyothyArticle.getTagUrls();
			String[] tagUrls = tagUrlsStr.split(",");
			
			for (int i=0; i< tags.length; i++) {
				List<CJArticle> tagRelatedArticles = cjArticleDao.getTagRelatedArticles(tags[i], StrapiConstants.STRAPI_PAGINATION_LIMIT);
				if(tagRelatedArticles!= null && tagRelatedArticles.size() > 0)
				{
					dataGeneratorService.generateTagListing(tags[i], tagUrls[i], categoryList, tagRelatedArticles, latestPhotos, rkArticles, ottArticles);
				}
			}
		}


		
		public List<CJArticle> getLatestVideosFromCMS() throws Exception {
			List<String> articlesStrList = cmsProxyService.getList("cj_latestVideos", 0, StrapiConstants.LIMIT);
			List<CJArticle> latestVideos = new ArrayList<>();
			if(!articlesStrList.isEmpty())
			{
				CJArticle chitrajyothyArticle;
				for (String cjaStr : articlesStrList) 
				{
					chitrajyothyArticle = JsonUtils.deserialize(cjaStr, CJArticle.class);
					latestVideos.add(chitrajyothyArticle);
				}
			}
			return latestVideos;
		}
		
		
		public List<CJArticle> getLatestArticlesFromCMS() throws Exception 
		{
			List<String> articlesStrList = cmsProxyService.getList("cj_latestArticles", 0, 10);
			List<CJArticle> latestArticles = new ArrayList<>();
			if(!articlesStrList.isEmpty())
			{
				CJArticle chitrajyothyArticle;
				for (String cjaStr : articlesStrList) 
				{
					chitrajyothyArticle = JsonUtils.deserialize(cjaStr, CJArticle.class);
					latestArticles.add(chitrajyothyArticle);
				}
			}
			return latestArticles;
		}
		
		public List<CJArticle> getPriorityArticlesFromCMS() throws Exception {
			List<String> articlesStrList = cmsProxyService.getList("cj_priorityArticles", 0, StrapiConstants.LIMIT);
			List<CJArticle>priorityArticles = new ArrayList<>();
			if(!articlesStrList.isEmpty())
			{
				CJArticle chitrajyothyArticle;
				for (String cjaStr : articlesStrList) 
				{
					chitrajyothyArticle = JsonUtils.deserialize(cjaStr, CJArticle.class);
					priorityArticles.add(chitrajyothyArticle);
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


}
