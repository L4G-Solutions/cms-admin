package com.andromeda.cms.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.PhotoGalleryDao;
import com.andromeda.cms.dao.RedirectionUrlDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.model.RedirectionUrl;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.commons.util.JsonUtils;

@Service
public class PhotoGalleryService 
{
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	
	@Autowired
	private PhotoGalleryDao photoGalleryDao;
	
	@Autowired
	private CategoryDao sitemapCategoryDao;
	
	@Autowired
	private SubCategoryDao sitemapSubCategoryDao;
	
	@Autowired
	private SitemapPageService sitemapPageService;
	
	@Autowired
	private DataGeneratorService dataGeneratorService;
	
	@Autowired
	private CmsProxyService cmsProxyService;
	
	@Autowired
	RedirectionUrlDao redirectionUrlDao;
	
	public void setPhotoGalleryDao(PhotoGalleryDao photoGalleryDao)
	{
		this.photoGalleryDao = photoGalleryDao;
	}
	
	public void setCategoryDao(CategoryDao sitemapCategoryDao)
	{
		this.sitemapCategoryDao = sitemapCategoryDao;
	}
	
	public void setSubCategoryDao(SubCategoryDao sitemapSubCategoryDao)
	{
		this.sitemapSubCategoryDao = sitemapSubCategoryDao;
	}
	
	public void setSitemapPageService(SitemapPageService sitemapPageService)
	{
		this.sitemapPageService = sitemapPageService;
	}
	
	public void setDataGeneratorService(DataGeneratorService dataGeneratorService)
	{
		this.dataGeneratorService = dataGeneratorService;
	}
	
	public void setCmsProxyService(CmsProxyService cmsProxyService)
	{
		this.cmsProxyService = cmsProxyService;
	}
	
	public void generatePhotoDetailPage(PhotoGallery photoGallery, List<StrapiCategory> categoryList, boolean updateFlag)
	{
		dataGeneratorService.generatePhotoDetailPage(photoGallery,categoryList, updateFlag);
		dataGeneratorService.generatePhotoDetailAmpPage(photoGallery,categoryList,updateFlag );
	}
	
	public void generatePhotoDetailPageForCMS(PhotoGallery photoGallery, List<StrapiCategory> categoryList)
	{
		dataGeneratorService.generatePhotoDetailPageForCMS(photoGallery,categoryList);
		dataGeneratorService.generatePhotoDetailAmpPageForCMS(photoGallery,categoryList);
	}
	
	public synchronized void generateLandingAndListingPage(PhotoGallery photoGallery, List<StrapiCategory> categoryList)
	{
		String contentType = photoGallery.getContentType();
		
		int categoryId = photoGallery.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(categoryId);

		int subCategoryId = photoGallery.getPrimarySubCategoryId();
		SubCategory primarySubCategory = sitemapSubCategoryDao.getById(subCategoryId);
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(subCategoryId));
		
		HashMap<SubCategory, List<PhotoGallery>> dataHashMap = new HashMap<>();
		if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
		{
			for (String subCategoryIdStr : subCategories) 
			{
				if(!subCategoryIdStr.equals(""))
				{
					SubCategory subCategory = sitemapSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
					List<PhotoGallery> photos = photoGalleryDao.getLatestBySubCategoryIdOnCreated(categoryId, Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
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
				List<PhotoGallery> latestPhotos = photoGalleryDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
				dataGeneratorService.generatePhotoLanding(primaryCategory, categoryList, latestPhotos,dataHashMap);
				if(contains)
				{
					generateListingPage(photoGallery, categoryList, primaryCategory, primarySubCategory, false);
				}
				
				//copy of PhotoGallery with primary sub category set to null
				//to be used for L1 category listing page generation
				PhotoGallery photogalleryWithPscRemoved = photoGallery;
				photogalleryWithPscRemoved.setPrimarySubCategoryId(0);
				photogalleryWithPscRemoved.setPrimarySubCategoryName(null);
				photogalleryWithPscRemoved.setPrimarySubCategorySeoSlug(null);
				photogalleryWithPscRemoved.setPrimarySubCategoryTeluguLabel(null);
				photogalleryWithPscRemoved.setPrimarySubCategoryUrl(null);
				generateListingPage(photogalleryWithPscRemoved, categoryList, primaryCategory ,null, true);
					
			}
			else
			{
				generateListingPage(photoGallery, categoryList, primaryCategory, primarySubCategory, false);
			}
		}
		
		
	}
	
	public synchronized void generateLandingAndListingPageForCMS(Category photoCategory, List<StrapiCategory> categoryList)
	{
		
		int categoryId = photoCategory.getId();
		String subCategoriesStr = photoCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		
		HashMap<SubCategory, List<PhotoGallery>> dataHashMap = new HashMap<>();
		if(!subCategoriesStr.equals("") && subCategories != null && subCategories.length > 0)
		{
			for (String subCategoryIdStr : subCategories) 
			{
				if(!subCategoryIdStr.equals(""))
				{
					SubCategory subCategory = sitemapSubCategoryDao.getById(Integer.parseInt(subCategoryIdStr));
					List<PhotoGallery> photos = photoGalleryDao.getLatestBySubCategoryIdOnCreated(categoryId, Integer.parseInt(subCategoryIdStr), StrapiConstants.LIMIT, null);
					if(photos!= null && photos.size()>0)
					{
						dataHashMap.put(subCategory, photos);
					}
					
					generateListingPage(null, categoryList, photoCategory, subCategory, false);
				}
			}
		}
		
			if(subCategories != null  && subCategories.length > 0 && !subCategoriesStr.equals(""))
			{	
				System.out.println("Photo Landing and Listing");
				//List<PhotoGallery> latestPhotos = photoGalleryDao.getLatestPhotos(StrapiConstants.REDIS_DATA_LIMIT.intValue());
				List<PhotoGallery> latestPhotos = photoGalleryDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
				generateListingPage(null, categoryList, photoCategory, null, true);
				dataGeneratorService.generatePhotoLanding(photoCategory, categoryList, latestPhotos,dataHashMap);

			}
		
	}
	
	private synchronized void generateListingPage(PhotoGallery photoGallery, List<StrapiCategory> categoryList, Category category, SubCategory subCategory, boolean pcListing) 
	{
		int categoryId = category.getId();
		//Category category = sitemapCategoryDao.getById(categoryId);
		
		
		//SubCategory subCategory = sitemapSubCategoryDao.getById(subCategoryId);
		
		List<PhotoGallery> photos = null;
		if( subCategory != null)
		{
			int subCategoryId = subCategory.getId();
			photos = photoGalleryDao.getLatestBySubCategoryIdOnCreated(categoryId, subCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
		}
		else
		{
			photos = photoGalleryDao.getLatestByCategoryIdOnCreated(categoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
		}
		
		dataGeneratorService.generatePhotoListing(category, subCategory, categoryList, photos, pcListing);
	}
	
		

	/**
	 * Insert Latest Primary Category related articles to Redis Database
	 */
	public void savePhotoCategoryRelatedArticlesToRedis(PhotoGallery photoGallery)
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoGallery.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(photoPrimaryCategoryId);
		
		Integer photoPrimarySubCategoryId = photoGallery.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(photoPrimarySubCategoryId));
		
		List<PhotoGallery> catRelatedPhotos =  photoGalleryDao.getLatestByCategoryId(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (PhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			System.out.println("saveCategoryRelatedArticlesToRedis " + redisCatKey + ":" + JsonUtils.toString(values));
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		
		List<String> subcatValues = new ArrayList<>();
		if(contains && photoPrimarySubCategoryId != null && photoPrimarySubCategoryId != 0)
		{
			String redisSubCatKey = "cat_" + photoPrimarySubCategoryId + "_" + photoGallery.getPrimarySubCategoryId();
			List<PhotoGallery> subCatRelatedArticles = photoGalleryDao.getLatestBySubCategoryId(photoPrimaryCategoryId,photoPrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);

			if(subCatRelatedArticles != null && !subCatRelatedArticles.isEmpty())
			{
				cmsProxyService.delete(redisSubCatKey);
				for (PhotoGallery pg : subCatRelatedArticles) 
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
	public void savePhotoCategoryRelatedArticlesToRedisForCMS(Category photoCategory)
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoCategory.getId();
				
		String subCategoriesStr = photoCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
				
		List<PhotoGallery> catRelatedPhotos =  photoGalleryDao.getLatestByCategoryId(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (PhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			System.out.println("saveCategoryRelatedArticlesToRedis " + redisCatKey + ":" + JsonUtils.toString(values));
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		List<String> subcatValues = new ArrayList<>();
		for (String subCategoryIdStr : subCategories) 
		{
			if( !subCategoryIdStr.isEmpty() && subCategoryIdStr != "")
			{
				Integer sitemapArticlePrimarySubCategoryId = Integer.valueOf(subCategoryIdStr);
				String redisSubCatKey = "cat_" + photoPrimaryCategoryId + "_" + sitemapArticlePrimarySubCategoryId;
				List<PhotoGallery> subCatRelatedPhotos = photoGalleryDao.getLatestBySubCategoryId(photoPrimaryCategoryId,sitemapArticlePrimarySubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);
				if(subCatRelatedPhotos != null && !subCatRelatedPhotos.isEmpty())
				{
					cmsProxyService.delete(redisSubCatKey);
					for (PhotoGallery pg : subCatRelatedPhotos) 
					{
						subcatValues.add(JsonUtils.toString(pg));
					}
					cmsProxyService.saveToList(redisSubCatKey, subcatValues);
				}
			}
		}
	}
	
	
	public boolean addOrUpdate(PhotoGallery photoGallery) 
	{
		boolean updateFlag = false;
		PhotoGallery existingPhotoGallery = photoGalleryDao.getByIdWOPublished(photoGallery.getId());
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
				photoGalleryDao.deleteById(existingPhotoGallery.getId());
				add(photoGallery);
			}
			else
			{
				update(photoGallery);
				//if old seoslug != new seoslug
				if(!existingPhotoGallery.getSeoSlug().equals(photoGallery.getSeoSlug()))
				{
					int id = existingPhotoGallery.getId();
					redirectionUrlDao.add(new RedirectionUrl(id, existingPhotoGallery.getUrl(), existingPhotoGallery.getAmpUrl(), existingPhotoGallery.getPublishedYear()));
					List<RedirectionUrl> redirectionUrls = redirectionUrlDao.getById(id);
					for (RedirectionUrl redirectionUrl : redirectionUrls) {
						dataGeneratorService.generateRedirectionPage(photoGallery, redirectionUrl);
					}
				}
			}
		}
		return updateFlag;
	}
	

	public List<PhotoGallery> getAll() {
		return photoGalleryDao.getAll();
	}
	
	public List<PhotoGallery> getByPublishedYear(int publishedYear) 
	{
		return photoGalleryDao.getByPublishedYear(publishedYear);
	}
	
	public void add(PhotoGallery photoGallery)
	{
		photoGalleryDao.add(photoGallery);
	}
	
	public void update(PhotoGallery photoGallery)
	{
		photoGalleryDao.update(photoGallery);
	}

	
	public List<PhotoGallery> getLatestPhotos(Integer limit) 
	{
		return photoGalleryDao.getLatestPhotos(limit);
	}
	
	public List<PhotoGallery> getSubCategoryRelatedPhotos(int subCategoryId) 
	{
		return photoGalleryDao.getSubCategoryRelatedPhotos(subCategoryId);
	}
	
	public List<PhotoGallery> getLatestBySubCategory(HashMap<String, String> params) 
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
		return photoGalleryDao.getLatestBySubCategoryId(categoryId, subCategoryId, limit, offset);
	}
	
	public List<PhotoGallery> getCategoryRelatedArticles(Integer subCategoryId) 
	{
		return photoGalleryDao.getByCategoryId(subCategoryId);
	}
	
	public List<PhotoGallery> getLatestByCategory(Integer categoryId, Integer limit) 
	{
		return photoGalleryDao.getLatestByCategoryId(categoryId, limit, null);
	}

	public PhotoGallery getById(int id) {
		return photoGalleryDao.getById(id);
	}
	
	public PhotoGallery getByIdWOPublished(int id) {
		return photoGalleryDao.getByIdWOPublished(id);
	}
	
	
	
	public void saveCategoryRelatedPhotosOnCreatedToRedis(PhotoGallery photoGallery) 
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoGallery.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(photoPrimaryCategoryId);
		
		Integer photoSubCategoryId = photoGallery.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(photoSubCategoryId));
		
		List<PhotoGallery> catRelatedPhotos =  photoGalleryDao.getLatestByCategoryIdOnCreated(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (PhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		List<String> subcatValues = new ArrayList<>();
		if(contains && photoPrimaryCategoryId != null && photoSubCategoryId != 0)
		{
			String redisSubCatKey = "cat_" + photoPrimaryCategoryId + "_" + photoSubCategoryId;
			List<PhotoGallery> subCatRelatedPhotos = photoGalleryDao.getLatestBySubCategoryIdOnCreated(photoPrimaryCategoryId, photoSubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);

			if(subCatRelatedPhotos != null && !subCatRelatedPhotos.isEmpty())
			{
				cmsProxyService.delete(redisSubCatKey);
				for (PhotoGallery pg : subCatRelatedPhotos) 
				{
					subcatValues.add(JsonUtils.toString(pg));
				}
				cmsProxyService.saveToList(redisSubCatKey, subcatValues);
			}
		}
		
	}
	
	public void saveCategoryRelatedPhotosToRedis(PhotoGallery photoGallery) 
	{
		// Insert Latest Primary Category related articles to Redis Database
		Integer photoPrimaryCategoryId = photoGallery.getPrimaryCategoryId();
		Category primaryCategory = sitemapCategoryDao.getById(photoPrimaryCategoryId);
		
		Integer photoSubCategoryId = photoGallery.getPrimarySubCategoryId();
		
		String subCategoriesStr = primaryCategory.getSubCategories();
		String[] subCategories = subCategoriesStr.split(",");
		
		boolean contains = Arrays.asList(subCategories).contains(String.valueOf(photoSubCategoryId));
		
		List<PhotoGallery> catRelatedPhotos =  photoGalleryDao.getLatestByCategoryId(photoPrimaryCategoryId, StrapiConstants.LIMIT, null);
		List<String> values = new ArrayList<>();
		
		if(catRelatedPhotos != null && !catRelatedPhotos.isEmpty())
		{
			String redisCatKey = "cat_" + photoPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (PhotoGallery pg : catRelatedPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}
		
		List<String> subcatValues = new ArrayList<>();
		if(contains && photoPrimaryCategoryId != null && photoSubCategoryId != 0)
		{
			String redisSubCatKey = "cat_" + photoPrimaryCategoryId + "_" + photoSubCategoryId;
			List<PhotoGallery> subCatRelatedPhotos = photoGalleryDao.getLatestBySubCategoryId(photoPrimaryCategoryId, photoSubCategoryId, StrapiConstants.REDIS_DATA_LIMIT.intValue(), null);

			if(subCatRelatedPhotos != null && !subCatRelatedPhotos.isEmpty())
			{
				cmsProxyService.delete(redisSubCatKey);
				for (PhotoGallery pg : subCatRelatedPhotos) 
				{
					subcatValues.add(JsonUtils.toString(pg));
				}
				cmsProxyService.saveToList(redisSubCatKey, subcatValues);
			}
		}
		
	}

	public void saveLatestPhotosToRedis() 
	{
		List<PhotoGallery> latestPhotos = photoGalleryDao.getLatestPhotos(StrapiConstants.REDIS_DATA_LIMIT.intValue());
		List<String> values = new ArrayList<>();
		if(latestPhotos != null && !latestPhotos.isEmpty())
		{
			cmsProxyService.delete("latestPhotos");
			for (PhotoGallery pg : latestPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList("latestPhotos", values);
		}
		
	}
	
	/**
	 * Insert Latest  Photos to Redis Database as per descending order of Created Time
	 */
	
	public void saveLatestPhotosOnCreatedToRedis() 
	{
		List<PhotoGallery> latestPhotos = photoGalleryDao.getLatestPhotosOnCreated(StrapiConstants.REDIS_DATA_LIMIT.intValue());
		List<String> values = new ArrayList<>();
		if(latestPhotos != null && !latestPhotos.isEmpty())
		{
			cmsProxyService.delete("latestPhotos");
			for (PhotoGallery pg : latestPhotos) {
				values.add(JsonUtils.toString(pg));
			}
			cmsProxyService.saveToList("latestPhotos", values);
		}
		
	}
	

	public void createOrUpdateGallerySitemap(int year) {
		sitemapPageService.createOrUpdateGallerySitemap(year);
		
	}

	public List<PhotoGallery> getLatestPhotosFromRedis(Integer limit) throws Exception {
		List<String> photosStrList = cmsProxyService.getList("latestPhotos", 0, limit);
		List<PhotoGallery> latestPhotos = new ArrayList<>();
		if(!photosStrList.isEmpty())
		{
			PhotoGallery photo;
			for (String saStr : photosStrList) 
			{
				photo = JsonUtils.deserialize(saStr, PhotoGallery.class);
				latestPhotos.add(photo);
			}
		}
		return latestPhotos;
	}

	public List<PhotoGallery> getLatestPhotosWithoutPriority(int primaryCategoryId, Integer feedLimit) {
		return photoGalleryDao.getLatestPhotosWithoutPriority(primaryCategoryId, feedLimit);
	}

	public List<PhotoGallery> getLatestPhotosBySubCategoryWithoutPriority(int categoryId,
			int subCategoryId, Integer limit, int offset) {
		
		return photoGalleryDao.getLatestPhotosBySubCategoryWithoutPriority(categoryId, subCategoryId,  limit,  offset);
	}

	public List<PhotoGallery> getByIds(List<Long> ids) {
		
		return photoGalleryDao.getByIds(ids);
	}



	
}
