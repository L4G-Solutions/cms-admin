package com.andromeda.cms.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CJCategoryDao;
import com.andromeda.cms.dao.CJPhotoGalleryDao;
import com.andromeda.cms.dao.CJRedirectionUrlDao;
import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.CJRedirectionUrl;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.commons.util.JsonUtils;

@Service
public class CJPhotoGalleryService {
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	
	@Autowired
	private CJPhotoGalleryDao cjPhotoGalleryDao;
	
	@Autowired
	private CJCategoryDao cjCategoryDao;
	
	@Autowired
	private CJSubCategoryDao cjSubCategoryDao;
	
	@Autowired
	private CJDataGeneratorService cjDataGeneratorService;
	
	@Autowired
	private CmsProxyService cmsProxyService;
	
	@Autowired
	CJRedirectionUrlDao redirectionUrlDao;
	
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
	
	
	public void setCjDataGeneratorService(CJDataGeneratorService cjDataGeneratorService)
	{
		this.cjDataGeneratorService = cjDataGeneratorService;
	}
	
	public void setCmsProxyService(CmsProxyService cmsProxyService)
	{
		this.cmsProxyService = cmsProxyService;
	}
	
	public void generatePhotoDetailPage(CJPhotoGallery chitrajyothyPhotoGallery, List<StrapiCategory> categoryList, boolean updateFlag)
	{
		cjDataGeneratorService.generatePhotoDetailPage(chitrajyothyPhotoGallery,categoryList, updateFlag);
		cjDataGeneratorService.generatePhotoDetailAmpPage(chitrajyothyPhotoGallery,categoryList,updateFlag );
	}
	
	public void generatePhotoDetailPageForCMS(CJPhotoGallery photoGallery, List<StrapiCategory> categoryList)
	{
		cjDataGeneratorService.generatePhotoDetailPageForCMS(photoGallery,categoryList);
		cjDataGeneratorService.generatePhotoDetailAmpPageForCMS(photoGallery,categoryList);
	}
	
	public synchronized void generateLandingAndListingPage(CJPhotoGallery chitrajyothyPhotoGallery, List<StrapiCategory> categoryList)
	{
		//String contentType = chitrajyothyPhotoGallery.getContentType();
		
		int categoryId = chitrajyothyPhotoGallery.getPrimaryCategoryId();
		CJCategory primaryCategory = cjCategoryDao.getById(categoryId);

		/*int subCategoryId = chitrajyothyPhotoGallery.getPrimarySubCategoryId();
		CJSubCategory primarySubCategory = cjSubCategoryDao.getById(subCategoryId);
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(subCategoryId));
		
		HashMap<CJSubCategory, List<CJPhotoGallery>> dataHashMap = new HashMap<>();
		if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
		{
			for (String subCategoryIdStr : subCategories) 
			{
				if(!subCategoryIdStr.equals(""))
				{
					CJSubCategory subCategory = cjSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
					List<CJPhotoGallery> photos = cjPhotoGalleryDao.getLatestBySubCategoryId(categoryId, Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
					if(photos!= null && photos.size()>0)
					{
						dataHashMap.put(subCategory, photos);
					}
				}
			}
		}
		
		
		if(contentType != null)
		{
			if(subCategories != null  && subCategories.length > 0 && !subCategoriesStr.equals(""))
			{	
				System.out.println("Photo Landing and Listing");
				List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.REDIS_DATA_LIMIT.intValue());
				cjDataGeneratorService.generatePhotoLanding(primaryCategory, categoryList, latestPhotos,dataHashMap);
				if(contains)
				{
					generateListingPage(chitrajyothyPhotoGallery, categoryList, primaryCategory, primarySubCategory);
				}
					
			}
			else*/
			{
				generateListingPage(chitrajyothyPhotoGallery, categoryList, primaryCategory, null);
			}
		//}
		
		
	}
	
	public synchronized void generateLandingAndListingPageForCMS(CJCategory cjPhotoCategory, List<StrapiCategory> categoryList)
	{
		generateListingPage(null, categoryList, cjPhotoCategory, null);
	}
	
	public synchronized void generateLandingAndListingPageForCMS_WithLanding(CJCategory cjPhotoCategory, List<StrapiCategory> categoryList)
	{
		
		int categoryId = cjPhotoCategory.getId();

		
		String subCategoriesStr = cjPhotoCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		
		HashMap<CJSubCategory, List<CJPhotoGallery>> dataHashMap = new HashMap<>();
		if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
		{
			for (String subCategoryIdStr : subCategories) 
			{
				if(!subCategoryIdStr.equals(""))
				{
					CJSubCategory subCategory = cjSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
					List<CJPhotoGallery> photos = cjPhotoGalleryDao.getLatestBySubCategoryId(categoryId, Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
					if(photos!= null && photos.size()>0)
					{
						dataHashMap.put(subCategory, photos);
					}
					
					generateListingPage(null, categoryList, cjPhotoCategory, subCategory);
				}
			}
		}
		
			if(subCategories != null  && subCategories.length > 0 && !subCategoriesStr.equals(""))
			{	
				System.out.println("Photo Landing and Listing");
				List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.REDIS_DATA_LIMIT.intValue());
				cjDataGeneratorService.generatePhotoLanding(cjPhotoCategory, categoryList, latestPhotos,dataHashMap);

			}
		
	}
	
	private synchronized void generateListingPage(CJPhotoGallery photoGallery, List<StrapiCategory> categoryList, CJCategory category, CJSubCategory subCategory) 
	{
		int categoryId = category.getId();
		//Category category = sitemapCategoryDao.getById(categoryId);
		
		//int subCategoryId = subCategory.getId();
		//SubCategory subCategory = sitemapSubCategoryDao.getById(subCategoryId);
		
		List<CJPhotoGallery> photos = null;
		/*if( subCategory != null && subCategoryId != 0)
		{
			photos = cjPhotoGalleryDao.getLatestBySubCategoryId(categoryId, subCategoryId, StrapiConstants.ABN_LISTING_PAGE_LIMIT, null);
		}
		else*/
		{
			photos = cjPhotoGalleryDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.CHITRAJYOTHY_PHOTO_LISTING_TOTAL_LIMIT, null);
		}
		
		cjDataGeneratorService.generatePhotoListing(category, null, categoryList, photos);
	}
	
	/**
	 * Insert Latest  Articles to Redis Database
	 */
	/*public void saveLatestArticlesToRedis()
	{
		List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.LIMIT);
		List<String> values = new ArrayList<>();
		if(latestPhotos != null && !latestPhotos.isEmpty())
		{
			cmsProxyService.delete("cj_latestPhotos");
			for (CJPhotoGallery pg : latestPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList("latestPhotos", values);
		}
		
	}*/
	

	/**
	 * Insert Latest Primary Category related articles to Redis Database
	 */
	public void savePhotoCategoryRelatedArticlesToRedis(CJPhotoGallery photoGallery)
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoGallery.getPrimaryCategoryId();
		CJCategory primaryCategory = cjCategoryDao.getById(photoPrimaryCategoryId);
		
		Integer photoPrimarySubCategoryId = photoGallery.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(photoPrimarySubCategoryId));
		
		List<CJPhotoGallery> catRelatedPhotos =  cjPhotoGalleryDao.getLatestByCategoryId(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cj_cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (CJPhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			System.out.println("saveCategoryRelatedArticlesToRedis " + redisCatKey + ":" + JsonUtils.toString(values));
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		
		List<String> subcatValues = new ArrayList<>();
		if(contains && photoPrimarySubCategoryId != null && photoPrimarySubCategoryId != 0)
		{
			String redisSubCatKey = "cj_cat_" + photoPrimarySubCategoryId + "_" + photoGallery.getPrimarySubCategoryId();
			List<CJPhotoGallery> subCatRelatedArticles = cjPhotoGalleryDao.getLatestBySubCategoryId(photoPrimaryCategoryId,photoPrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);

			if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
			{
				cmsProxyService.delete(redisSubCatKey);
				for (CJPhotoGallery pg : subCatRelatedArticles) 
				{
					subcatValues.add(JsonUtils.toString(pg));
				}
				cmsProxyService.saveToList(redisSubCatKey, subcatValues);
			}
		}
	}

	/**
	 * Insert Latest Primary Category related articles to Redis Database
	 */
	public void savePhotoCategoryRelatedArticlesToRedisForCMS(CJCategory photoCategory)
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoCategory.getId();
				
		String subCategoriesStr = photoCategory.getSubCategories();
		
				
		List<CJPhotoGallery> catRelatedPhotos =  cjPhotoGalleryDao.getLatestByCategoryIdOnCreated(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (CJPhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			System.out.println("saveCategoryRelatedArticlesToRedis " + redisCatKey + ":" + JsonUtils.toString(values));
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		List<String> subcatValues = new ArrayList<>();
		if(subCategoriesStr != null)
		{
			String[] subCategories = subCategoriesStr.split(",");
			for (String subCategoryIdStr : subCategories) 
			{
				if( !subCategoryIdStr.isEmpty() && subCategoryIdStr != "")
				{
					Integer sitemapArticlePrimarySubCategoryId = Integer.valueOf(subCategoryIdStr);
					String redisSubCatKey = "cat_" + photoPrimaryCategoryId + "_" + sitemapArticlePrimarySubCategoryId;
					List<CJPhotoGallery> subCatRelatedPhotos = cjPhotoGalleryDao.getLatestBySubCategoryIdOnCreated(photoPrimaryCategoryId,sitemapArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
					if(subCatRelatedPhotos != null && !subCatRelatedPhotos.isEmpty())
					{
						cmsProxyService.delete(redisSubCatKey);
						for (CJPhotoGallery pg : subCatRelatedPhotos) 
						{
							subcatValues.add(JsonUtils.toString(pg));
						}
						cmsProxyService.saveToList(redisSubCatKey, subcatValues);
					}
				}
			}
		}
		
	}
	
	
	public boolean addOrUpdate(CJPhotoGallery photoGallery) 
	{
		boolean updateFlag = false;
		CJPhotoGallery existingPhotoGallery = cjPhotoGalleryDao.getByIdWOPublished(photoGallery.getId());
		if(existingPhotoGallery == null)
		{
			System.out.println("Adding Photo Gallery Object " + JsonUtils.toString(photoGallery));
			add(photoGallery);
		}
		else
		{
			updateFlag = true;
			System.out.println("Updating Photo Gallery Object " + JsonUtils.toString(photoGallery));
			if(existingPhotoGallery.getPublishedYear() != photoGallery.getPublishedYear())
			{
				cjPhotoGalleryDao.deleteById(existingPhotoGallery.getId());
				add(photoGallery);
			}
			else
			{
				update(photoGallery);
				//if old seoslug != new seoslug
				if(!existingPhotoGallery.getSeoSlug().equals(photoGallery.getSeoSlug()))
				{
					int id = existingPhotoGallery.getId();
					redirectionUrlDao.add(new CJRedirectionUrl(id, existingPhotoGallery.getUrl(), existingPhotoGallery.getAmpUrl(), existingPhotoGallery.getPublishedYear()));
					List<CJRedirectionUrl> redirectionUrls = redirectionUrlDao.getById(id);
					for (CJRedirectionUrl redirectionUrl : redirectionUrls) {
						cjDataGeneratorService.generateRedirectionPage(photoGallery, redirectionUrl);
					}
				}
			}
		}
		return updateFlag;
	}
	

	
	public List<CJPhotoGallery> getByPublishedYear(int publishedYear) 
	{
		return cjPhotoGalleryDao.getByPublishedYear(publishedYear);
	}
	
	public void add(CJPhotoGallery photoGallery)
	{
		cjPhotoGalleryDao.add(photoGallery);
	}
	
	public void update(CJPhotoGallery photoGallery)
	{
		cjPhotoGalleryDao.update(photoGallery);
	}

	
	public List<CJPhotoGallery> getLatestPhotos(Integer limit) 
	{
		return cjPhotoGalleryDao.getLatestPhotos(limit);
	}
	

	
	public List<CJPhotoGallery> getLatestBySubCategory(HashMap<String, String> params) 
	{
		Integer limit = StrapiConstants.LIMIT;
		Integer subCategoryId = null;
		Integer categoryId = null;
		Integer offset = null;
		if(params.containsKey("subCategoryId"))
		{
			subCategoryId = Integer.parseInt(params.get("subCategoryId"));
		}
		
		if(params.containsKey("categoryId"))
		{
			categoryId = Integer.parseInt(params.get("categoryId"));
		}
			
		if(params.containsKey("limit"))
			limit = Integer.parseInt(params.get("limit"));
		if(params.containsKey("offset"))
			offset = Integer.parseInt(params.get("offset"));
		return cjPhotoGalleryDao.getLatestBySubCategoryId(categoryId, subCategoryId, limit, offset);
	}
	

	
	public List<CJPhotoGallery> getLatestByCategory(Integer categoryId, Integer limit) 
	{
		return cjPhotoGalleryDao.getLatestByCategoryId(categoryId, limit, null);
	}

	public CJPhotoGallery getById(int id) {
		return cjPhotoGalleryDao.getById(id);
	}
	
	public CJPhotoGallery getByIdWOPublished(int id) {
		return cjPhotoGalleryDao.getByIdWOPublished(id);
	}

	public void saveCategoryRelatedPhotosToRedis(CJPhotoGallery photoGallery) 
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoGallery.getPrimaryCategoryId();
		CJCategory primaryCategory = cjCategoryDao.getById(photoPrimaryCategoryId);
		
		/*Integer photoSubCategoryId = photoGallery.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(photoSubCategoryId));*/
		
		List<CJPhotoGallery> catRelatedPhotos =  cjPhotoGalleryDao.getLatestByCategoryId(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cj_cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (CJPhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		/*List<String> subcatValues = new ArrayList<>();
		if(contains && photoPrimaryCategoryId != null && photoSubCategoryId != 0)
		{
			String redisSubCatKey = "cj_cat_" + photoPrimaryCategoryId + "_" + photoSubCategoryId;
			List<CJPhotoGallery> subCatRelatedPhotos = cjPhotoGalleryDao.getLatestBySubCategoryId(photoPrimaryCategoryId, photoSubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);

			if(subCatRelatedPhotos != null && !subCatRelatedPhotos.isEmpty())
			{
				cmsProxyService.delete(redisSubCatKey);
				for (CJPhotoGallery pg : subCatRelatedPhotos) 
				{
					subcatValues.add(JsonUtils.toString(pg));
				}
				cmsProxyService.saveToList(redisSubCatKey, subcatValues);
			}
		}*/
		
	}

	
	public void saveCategoryRelatedPhotosOnCreatedToRedis(CJPhotoGallery photoGallery) 
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoGallery.getPrimaryCategoryId();
		CJCategory primaryCategory = cjCategoryDao.getById(photoPrimaryCategoryId);
		
	
		List<CJPhotoGallery> catRelatedPhotos =  cjPhotoGalleryDao.getLatestByCategoryId(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cj_cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (CJPhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		/*List<String> subcatValues = new ArrayList<>();
		if(contains && photoPrimaryCategoryId != null && photoSubCategoryId != 0)
		{
			String redisSubCatKey = "cj_cat_" + photoPrimaryCategoryId + "_" + photoSubCategoryId;
			List<CJPhotoGallery> subCatRelatedPhotos = cjPhotoGalleryDao.getLatestBySubCategoryId(photoPrimaryCategoryId, photoSubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);

			if(subCatRelatedPhotos != null && !subCatRelatedPhotos.isEmpty())
			{
				cmsProxyService.delete(redisSubCatKey);
				for (CJPhotoGallery pg : subCatRelatedPhotos) 
				{
					subcatValues.add(JsonUtils.toString(pg));
				}
				cmsProxyService.saveToList(redisSubCatKey, subcatValues);
			}
		}*/
		
	}
	
	public void saveLatestPhotosToRedis() 
	{
		List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotos(StrapiConstants.REDIS_DATA_LIMIT.intValue());
		List<String> values = new ArrayList<>();
		if(latestPhotos != null && !latestPhotos.isEmpty())
		{
			cmsProxyService.delete("cj_latestPhotos");
			for (CJPhotoGallery pg : latestPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList("cj_latestPhotos", values);
		}
		
	}
	
	public void saveLatestPhotosOnCreatedToRedis() 
	{
		List<CJPhotoGallery> latestPhotos = cjPhotoGalleryDao.getLatestPhotosOnCreated(StrapiConstants.REDIS_DATA_LIMIT.intValue());
		List<String> values = new ArrayList<>();
		if(latestPhotos != null && !latestPhotos.isEmpty())
		{
			cmsProxyService.delete("cj_latestPhotos");
			for (CJPhotoGallery pg : latestPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList("cj_latestPhotos", values);
		}
		
	}
	


	public List<CJPhotoGallery> getLatestPhotosFromRedis(Integer limit) throws Exception {
		List<String> photosStrList = cmsProxyService.getList("cj_latestPhotos", 0, limit);
		List<CJPhotoGallery> latestPhotos = new ArrayList<>();
		if(!photosStrList.isEmpty())
		{
			CJPhotoGallery photo;
			for (String cjpgStr : photosStrList) 
			{
				photo = JsonUtils.deserialize(cjpgStr, CJPhotoGallery.class);
				latestPhotos.add(photo);
			}
		}
		return latestPhotos;
	}

	public List<CJPhotoGallery> getLatestPhotosWithoutPriority(int primaryCategoryId, Integer feedLimit) {
		return cjPhotoGalleryDao.getLatestPhotosWithoutPriority(primaryCategoryId, feedLimit);
	}

	public List<CJPhotoGallery> getLatestPhotosBySubCategoryWithoutPriority(int categoryId,
			int subCategoryId, Integer limit, int offset) {
		
		return cjPhotoGalleryDao.getLatestPhotosBySubCategoryWithoutPriority(categoryId, subCategoryId,  limit,  offset);
	}

	public List<CJPhotoGallery> getByIds(List<Long> ids) {
		return cjPhotoGalleryDao.getByIds(ids);
	}

}
