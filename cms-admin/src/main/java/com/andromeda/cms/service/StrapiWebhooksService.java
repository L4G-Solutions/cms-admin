package com.andromeda.cms.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJLiveBlog;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.Cartoon;
import com.andromeda.cms.model.HomePageAd;
import com.andromeda.cms.model.Horoscope;
import com.andromeda.cms.model.ElectionVote;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.model.RankingDashboard;
import com.andromeda.cms.model.RankingItem;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCartoon;
import com.andromeda.cms.model.StrapiHoroscope;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.sitemap.service.CJSitemapPageService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.cms.translator.StrapiCjTranslator;
import com.andromeda.cms.translator.StrapiTranslator;
import com.andromeda.commons.util.JsonUtils;

@Service
public class StrapiWebhooksService {

	public static List<StrapiCategory> categoryList;

	public static List<StrapiSubCategory> subCategoryList;

	public static List<StrapiCategory> cjCategoryList;

	public static List<StrapiSubCategory> cjSubCategoryList;

	public static HashMap<String, StrapiCategory> categoryNameHm = new HashMap<>();

	public static HashMap<String, StrapiCategory> cjCategoryNameHm = new HashMap<>();

	public static HashMap<Integer, StrapiCategory> categoryIdHm = new HashMap<>();

	public static HashMap<Integer, StrapiCategory> cjCategoryIdHm = new HashMap<>();

	public static HashMap<Integer, StrapiSubCategory> subcategoryIdHm = new HashMap<>();

	public static HashMap<String, StrapiSubCategory> subcategoryNameHm = new HashMap<>();

	public static HashMap<Integer, StrapiSubCategory> cjSubcategoryIdHm = new HashMap<>();

	public static HashMap<String, StrapiSubCategory> cjSubcategoryNameHm = new HashMap<>();

	public static StrapiCategory rKCategory;

	public static StrapiCategory photoCategory;

	public static StrapiCategory cartoonCategory;

	public static StrapiCategory horoscopeCategory;

	public static StrapiSubCategory abnVideosSubcategory;

	public static StrapiCategory cjPhotoCategory;

	public static StrapiCategory cjRkCategory;

	public static StrapiCategory cjOttCategory;

	public static StrapiSubCategory cjTrailersSubcategory;

	@Autowired
	public ArticleService sitemapArticleService;

	@Autowired
	public CJArticleService cjArticleService;
	
	@Autowired
	public CJLiveBlogService cjLiveBlogService;

	@Autowired
	public PhotoGalleryService photoGalleryService;

	@Autowired
	public CJPhotoGalleryService cjPhotoGalleryService;

	@Autowired
	public CartoonService cartoonService;

	@Autowired
	public HoroscopeService sitemapHoroscopeService;

	@Autowired
	public StrapiArticleService strapiArticleService;

	@Autowired
	public StrapiCategoryService strapiCategoryService;

	@Autowired
	public StrapiSubCategoryService strapiSubCategoryService;

	@Autowired
	public DataGeneratorService dataGeneratorService;

	@Autowired
	public CJDataGeneratorService cjDataGeneratorService;

	@Autowired
	public CmsProxyService cmsProxyService;

	@Autowired
	private SitemapPageService sitemapPageService;

	@Autowired
	private CJSitemapPageService cjSitemapPageService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private HomepageAdService homepageAdService;

	public void setSitemapArticleService(ArticleService sitemapArticleService) {
		this.sitemapArticleService = sitemapArticleService;
	}

	public void setCjArticleService(CJArticleService cjArticleService) {
		this.cjArticleService = cjArticleService;
	}

	public void setStrapiArticleService(StrapiArticleService strapiArticleService) {
		this.strapiArticleService = strapiArticleService;
	}

	public void setStrapiCategoryService(StrapiCategoryService strapiCategoryService) {
		this.strapiCategoryService = strapiCategoryService;
	}

	public void setStrapiSubCategoryService(StrapiSubCategoryService strapiSubCategoryService) {
		this.strapiSubCategoryService = strapiSubCategoryService;
	}

	public void setCmsProxyService(CmsProxyService cmsProxyService) {
		this.cmsProxyService = cmsProxyService;
	}

	public void setSitemapPageService(SitemapPageService sitemapPageService) {
		this.sitemapPageService = sitemapPageService;
	}

	public void setDataGeneratorService(DataGeneratorService dataGeneratorService) {
		this.dataGeneratorService = dataGeneratorService;
	}

	public void setCjDataGeneratorService(CJDataGeneratorService cjDataGeneratorService) {
		this.cjDataGeneratorService = cjDataGeneratorService;
	}



	public void setPhotoGalleryService(PhotoGalleryService photoGalleryService) {
		this.photoGalleryService = photoGalleryService;
	}

	public void setCjPhotoGalleryService(CJPhotoGalleryService cjPhotoGalleryService) {
		this.cjPhotoGalleryService = cjPhotoGalleryService;
	}

	public void setCartoonService(CartoonService cartoonService) {
		this.cartoonService = cartoonService;
	}

	public void setHoroscopeService(HoroscopeService horoscopeService) {
		this.sitemapHoroscopeService = horoscopeService;
	}

	public void setFeedService(FeedService feedService) {
		this.feedService = feedService;
	}

	@PostConstruct
	public void init() throws IOException {
		dataGeneratorService.configureFreemarker();
		categoryList = strapiCategoryService.getAllCategories();
		subCategoryList = strapiSubCategoryService.getAllSubCategories();

		String categoryIdsList = "";
		List<String> categoryStrList = new ArrayList<>();
		for (StrapiCategory strapiCategory : categoryList) {
			categoryNameHm.put(strapiCategory.getName(), strapiCategory);
			categoryIdHm.put(strapiCategory.getId(), strapiCategory);
			String categoryStr = JsonUtils.toString(strapiCategory);
			categoryStrList.add(categoryStr);
			categoryIdsList = categoryIdsList + strapiCategory.getId() + ",";
		}
		cmsProxyService.delete("allCategories");
		cmsProxyService.saveToList("allCategories", categoryStrList);

		if (categoryIdsList.length() > 0)
			categoryIdsList = categoryIdsList.substring(0, categoryIdsList.length() - 1);

		cmsProxyService.delete("categoryIdsList");
		cmsProxyService.save("categoryIdsList", categoryIdsList);

		rKCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_OPEN_HEART);
		photoCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
		cartoonCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_CARTOON);
		horoscopeCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_HOROSCOPE);

		for (StrapiSubCategory strapiSubCategory : subCategoryList) {
			subcategoryIdHm.put(strapiSubCategory.getId(), strapiSubCategory);
			subcategoryNameHm.put(strapiSubCategory.getName(), strapiSubCategory);
		}

		abnVideosSubcategory = subcategoryNameHm.get(StrapiConstants.STRAPI_SUBCATEGORY_ABN_VIDEOS);

		sitemapPageService.createOrUpdatePageSitemap();
		sitemapPageService.createOrUpdateSitemapIndex();
		sitemapPageService.createOrUpdateCategorySitemap();

		feedService.createMainFeedPage();

	}

	@PostConstruct
	public void initCj() throws Exception {
		cjDataGeneratorService.configureFreemarker();
		cjCategoryList = strapiCategoryService.getAllCjCategoriesWithMetaDesc();
		cjSubCategoryList = strapiSubCategoryService.getAllCjSubCategories();

		String categoryIdsList = "";
		List<String> categoryStrList = new ArrayList<>();
		for (StrapiCategory strapiCategory : cjCategoryList) {
			cjCategoryNameHm.put(strapiCategory.getName(), strapiCategory);
			cjCategoryIdHm.put(strapiCategory.getId(), strapiCategory);
			String categoryStr = JsonUtils.toString(strapiCategory);
			categoryStrList.add(categoryStr);
			categoryIdsList = categoryIdsList + strapiCategory.getId() + ",";
		}
		cmsProxyService.delete("cj_allCategories");
		cmsProxyService.saveToList("cj_allCategories", categoryStrList);

		if (categoryIdsList.length() > 0)
			categoryIdsList = categoryIdsList.substring(0, categoryIdsList.length() - 1);
		cmsProxyService.delete("cj_categoryIdsList");
		cmsProxyService.save("cj_categoryIdsList", categoryIdsList);

		cjPhotoCategory = cjCategoryNameHm.get(StrapiConstants.STRAPI_CJ_CATEGORY_PHOTOGALLERY);
		cjRkCategory = cjCategoryNameHm.get(StrapiConstants.STRAPI_CJ_CATEGORY_OPEN_HEART);
		cjOttCategory = cjCategoryNameHm.get(StrapiConstants.STRAPI_CJ_CATEGORY_OTT);

		for (StrapiSubCategory strapiSubCategory : cjSubCategoryList) {
			cjSubcategoryIdHm.put(strapiSubCategory.getId(), strapiSubCategory);
			cjSubcategoryNameHm.put(strapiSubCategory.getName(), strapiSubCategory);
		}

		cjTrailersSubcategory = cjSubcategoryNameHm.get(StrapiConstants.STRAPI_CJ_SUBCATEGORY_TRAILERS);
		
		  cjSitemapPageService.createOrUpdateCjPageSitemap();
		  cjSitemapPageService.createOrUpdateCjSitemapIndex();
		  cjSitemapPageService.createOrUpdateCjCategorySitemap();
		  
		  feedService.createCjMainFeedPage();
		 

	}

	public void onCartoonCreate(StrapiCartoon strapiCartoon) {
		if (strapiCartoon != null) {
			dataGeneratorService.clearInvalidationUrlList();
			
			StrapiCategory strapiCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_CARTOON);
			strapiCartoon.setCategory(strapiCategory);
			boolean updateFlag = false;
			Cartoon sitemapCartoon = StrapiTranslator.translateCartoon(strapiCartoon);
			updateFlag = cartoonService.addOrUpdate(sitemapCartoon);

			cartoonService.saveCategoryRelatedCartoonsOnCreatedtoRedis(sitemapCartoon);
			System.out.println("Save Latest Cartoons To Redis()");
			cartoonService.saveLatestCartoonsOnCreatedToRedis(); // to be used in cms-proxy for Category Landing & Listing pages

			cartoonService.generateCartoonPage(sitemapCartoon.getPrimaryCategoryId(), categoryList, updateFlag);
		}

	}

	public void onHoroscopeCreate(StrapiHoroscope strapiHoroscope) {
		if (strapiHoroscope != null) {
			StrapiCategory strapiCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_HOROSCOPE);
			// if(strapiCategories != null && strapiCategories.size() > 0)
			strapiHoroscope.setCategory(strapiCategory);
			boolean updateFlag = false;
			Horoscope sitemapHoroscope = StrapiTranslator.translateHoroscope(strapiHoroscope);
			updateFlag = sitemapHoroscopeService.addOrUpdate(sitemapHoroscope);
			sitemapHoroscopeService.generateHoroscopePage(sitemapHoroscope.getPrimaryCategoryId(), categoryList,
					updateFlag);
		}

	}

	public void onPhotoCreateCMS(StrapiPhotoGallery strapiPhotoGallery) throws Exception {
		if (strapiPhotoGallery != null) {
			StrapiCategory photoStrapiCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
			strapiPhotoGallery.setCategory(photoStrapiCategory);

			int scId = strapiPhotoGallery.getSubCategory().getId();
			StrapiSubCategory subCat = subcategoryIdHm.get(scId);
			strapiPhotoGallery.setSubCategory(subCat);

			System.out.println("Translating Photo ");

			PhotoGallery photoGallery = StrapiTranslator.translatePhotoGallery(strapiPhotoGallery);

			photoGalleryService.addOrUpdate(photoGallery);

			System.out.println("Generate Photo Detail Page");
			photoGalleryService.generatePhotoDetailPageForCMS(photoGallery, categoryList);

		}
	}

	public void onCjPhotoCreateCMS(StrapiPhotoGallery strapiPhotoGallery) throws Exception {
		if (strapiPhotoGallery != null) {
			StrapiCategory cjPhotoStrapiCategory = cjCategoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
			strapiPhotoGallery.setCategory(cjPhotoStrapiCategory);

			/*
			 * int scId = strapiPhotoGallery.getSubCategory().getId(); StrapiSubCategory
			 * cjSubCat = cjSubcategoryIdHm.get(scId);
			 * strapiPhotoGallery.setSubCategory(cjSubCat);
			 */

			System.out.println("Translating CJ Photo ");

			CJPhotoGallery cjPhotoGallery = StrapiCjTranslator.translatePhotoGallery(strapiPhotoGallery);

			cjPhotoGalleryService.addOrUpdate(cjPhotoGallery);

			System.out.println("Generate CJ Photo Detail Page");
			cjPhotoGalleryService.generatePhotoDetailPageForCMS(cjPhotoGallery, cjCategoryList);
		}
	}

	public void onPhotoCreate(StrapiPhotoGallery strapiPhotoGallery) throws Exception {
		boolean updateFlag = false;

		StrapiCategory photoStrapiCategory = categoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
		strapiPhotoGallery.setCategory(photoStrapiCategory);

		int scId = strapiPhotoGallery.getSubCategory().getId();
		StrapiSubCategory subCat = subcategoryIdHm.get(scId);
		strapiPhotoGallery.setSubCategory(subCat);

		if (strapiPhotoGallery != null) {
			System.out.println("Translating Photo ");
			PhotoGallery photoGallery = StrapiTranslator.translatePhotoGallery(strapiPhotoGallery);
			PhotoGallery existingPhotoGallery = photoGalleryService.getByIdWOPublished(photoGallery.getId());
			if (existingPhotoGallery != null)
				updateFlag = true;

			System.out.println("Generate Photo Detail Page");
			if (photoGallery.getAbnStoryId() != null && !photoGallery.getAbnStoryId().isEmpty()) {
				photoGalleryService.generatePhotoDetailPageForCMS(photoGallery, categoryList);
			} else {
				photoGalleryService.generatePhotoDetailPage(photoGallery, categoryList, updateFlag);
			}

			System.out.println("Add or Update database");
			photoGalleryService.addOrUpdate(photoGallery);

			System.out.println("Save Category related Photos to Redis");
			photoGalleryService.saveCategoryRelatedPhotosOnCreatedToRedis(photoGallery); // to be used in cms-proxy for Category
																				// Landing & Listing pages

			System.out.println("Generate Landing and Listing page");
			photoGalleryService.generateLandingAndListingPage(photoGallery, categoryList);

			System.out.println("Save LatestPhotos To Redis()");
			photoGalleryService.saveLatestPhotosOnCreatedToRedis();
			
			System.out.println("Create Invalidations");
			dataGeneratorService.invalidate();

			System.out.println("Create Or Update Gallery Sitemap");

			Timestamp publishedAt = photoGallery.getPublishedAt();
			int year = StrapiUtils.getYearFromTimestamp(publishedAt);
			sitemapPageService.createOrUpdateGallerySitemap(year);

			// Update feeds
			System.out.println("Create Or Update Category Feed");
			feedService.createPhotoGalleryCategoryFeedPage(photoGallery.getPrimaryCategoryId());
			int subCategoryId = photoGallery.getPrimarySubCategoryId();
			if (subCategoryId != 0) {
				System.out.println("Create Or Update SubCategory Feed");
				feedService.createPhotoGallerySubCategoryFeedPage(photoGallery.getPrimarySubCategoryId());
			}

			System.out.println("Create Or Update Main Feed");
			feedService.createMainFeedPage();

		}
	}

	public void onCjPhotoCreate(StrapiPhotoGallery strapiPhotoGallery) throws Exception {
		boolean updateFlag = false;

		StrapiCategory photoCjStrapiCategory = cjCategoryNameHm.get(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
		strapiPhotoGallery.setCategory(photoCjStrapiCategory);

		CJPhotoGallery existingCjPhoto = cjPhotoGalleryService.getByIdWOPublished(strapiPhotoGallery.getId());
		if (existingCjPhoto != null)
			updateFlag = true;

		if (strapiPhotoGallery != null) {
			System.out.println("Translating CJ Photo ");
			CJPhotoGallery cjPhotoGallery = StrapiCjTranslator.translatePhotoGallery(strapiPhotoGallery);

			System.out.println("Generate CJ Photo Detail Page");
			if (cjPhotoGallery.getAbnStoryId() != null && !cjPhotoGallery.getAbnStoryId().isEmpty()) {
				cjPhotoGalleryService.generatePhotoDetailPageForCMS(cjPhotoGallery, cjCategoryList);
			} else {
				cjPhotoGalleryService.generatePhotoDetailPage(cjPhotoGallery, cjCategoryList, updateFlag);
			}

			System.out.println("Updating DB with CJ Photo Gallery");
			updateFlag = cjPhotoGalleryService.addOrUpdate(cjPhotoGallery);

			System.out.println("Save CJ Category related photos to Redis");
			cjPhotoGalleryService.saveCategoryRelatedPhotosOnCreatedToRedis(cjPhotoGallery); // to be used in cms-proxy for
																					// Category Landing & Listing pages

			System.out.println("Generate CJ Landing and Lisiting page");
			cjPhotoGalleryService.generateLandingAndListingPage(cjPhotoGallery, cjCategoryList);

			System.out.println("Save CJ Latest Photos To Redis()");
			cjPhotoGalleryService.saveLatestPhotosOnCreatedToRedis();
			
			System.out.println("Create Invalidations");
			cjDataGeneratorService.invalidate();
			
			System.out.println("Create Or Update CJ NewsSitemap");

			Timestamp publishedAt = cjPhotoGallery.getPublishedAt();
			int year = StrapiUtils.getYearFromTimestamp(publishedAt);
			cjSitemapPageService.createOrUpdateCjGallerySitemap(year);

			// Update feeds
			feedService.createCjPhotoGalleryCategoryFeedPage(cjPhotoGallery.getPrimaryCategoryId());
			int subCategoryId = cjPhotoGallery.getPrimarySubCategoryId();
			if (subCategoryId != 0)
				feedService.createCjPhotoGallerySubCategoryFeedPage(cjPhotoGallery.getPrimarySubCategoryId());
			feedService.createCjMainFeedPage();

		}
	}

	public boolean onArticleCreateForCMS(StrapiArticle strapiArticle) throws Exception {
		boolean status = true;
		boolean updateFlag = false;
		try {
			if (strapiArticle != null) {
				int cId = strapiArticle.getPrimaryCategory().getId();
				StrapiCategory pc = categoryIdHm.get(cId);
				strapiArticle.setPrimaryCategory(pc);
				System.out.println("Translating Article ");
				Article sitemapArticle = StrapiTranslator.translateArticle(strapiArticle);
				updateFlag = sitemapArticleService.addOrUpdate(sitemapArticle);

				String contentType = sitemapArticle.getContentType();
				if (contentType != null) {
					if (contentType.equalsIgnoreCase("article")
							&& sitemapArticle.getPrimaryCategoryId() != rKCategory.getId()) {
						System.out.println("Generate Article Page");
						sitemapArticleService.generateArticlePage(sitemapArticle, categoryList, updateFlag);
					} else if (contentType.equalsIgnoreCase("article")
							&& sitemapArticle.getPrimaryCategoryId() == rKCategory.getId()) {
						System.out.println("Generate RK Article Page");
						sitemapArticleService.generateArticlePage(sitemapArticle, categoryList, updateFlag);
						sitemapArticleService.generateRKPage(sitemapArticle.getPrimaryCategoryId(), categoryList,
								updateFlag);
					} else if (contentType.equalsIgnoreCase("video gallery")) {
						sitemapArticleService.generateVideoDetailPage(sitemapArticle, categoryList, updateFlag);
					}
				}
			}
			return status;
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
			return status;
		}
	}

	public boolean onCjArticleCreateForCMS(StrapiArticle strapiArticle) throws Exception {
		boolean status = true;
		boolean updateFlag = false;
		try {
			if (strapiArticle != null) {
				int cId = strapiArticle.getPrimaryCategory().getId();
				StrapiCategory pc = cjCategoryIdHm.get(cId);
				strapiArticle.setPrimaryCategory(pc);
				System.out.println("Translating CJ Article ");
				CJArticle cjArticle = StrapiCjTranslator.translateArticle(strapiArticle);

				System.out.println("Updating Db with CJ Article ");
				updateFlag = cjArticleService.addOrUpdate(cjArticle);

				String contentType = cjArticle.getContentType();
				if (contentType != null) {
					if (contentType.equalsIgnoreCase("article")) {
						System.out.println("Generate CJ Article Page");
						cjArticleService.generateArticlePage(cjArticle, cjCategoryList, updateFlag);
					} else if (contentType.equalsIgnoreCase("video gallery")) {
						cjArticleService.generateVideoDetailPage(cjArticle, cjCategoryList, updateFlag);
					}
				}
			}
			return status;
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
			return status;
		}
	}

	public void onArticleCreate(StrapiArticle strapiArticle) throws Exception {
		if (strapiArticle != null) {
			int id = strapiArticle.getId();
			boolean updateFlag = false;
			int cId = strapiArticle.getPrimaryCategory().getId();
			StrapiCategory pc = categoryIdHm.get(cId);
			strapiArticle.setPrimaryCategory(pc);
			System.out.println("Translating Article ");
			Article sitemapArticle = StrapiTranslator.translateArticle(strapiArticle);
			Article existingArticle = sitemapArticleService.getByIdWOPublished(sitemapArticle.getId());
			if (existingArticle != null)
				updateFlag = true;

			String contentType = strapiArticle.getContentType();
			if (contentType != null) {
				if (contentType.equalsIgnoreCase("article")
						&& sitemapArticle.getPrimaryCategoryId() != rKCategory.getId()) {
					//System.out.println("Generate JSON for id " + id);
					//sitemapArticleService.generateJSON(sitemapArticle);
					
					System.out.println("Generate Article Page " + id);
					sitemapArticleService.generateArticlePage(sitemapArticle, categoryList, updateFlag);

					System.out.println("Add or Update Database " + id);
					sitemapArticleService.addOrUpdate(sitemapArticle);

					System.out.println("Save LatestArticles To Redis");
					List<Article> latestNews = sitemapArticleService.saveLatestArticlesOnCreatedToRedis(contentType);

					System.out.println("Generate Latest Article Listing Page " + id);
					sitemapArticleService.generateLatestArticleListingPage(latestNews, categoryList);

					System.out.println("Save Priority Articles To Redis()");
					sitemapArticleService.savePriorityArticlesToRedis(contentType);
					
					System.out.println("Generate Speed News JSON pages");
					sitemapArticleService.generateSpeedNewsJsons();

					System.out.println("Generate Priority articles Page");
					List<Article> priorityNews = sitemapArticleService.getPriorityArticles();
					sitemapArticleService.generatePriorityArticleListingPage(priorityNews, categoryList);

					System.out.println("Save Category Articles To Redis");
					sitemapArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(sitemapArticle, false); // to be used in
																										// cms-proxy for
																										// Category
																										// Landing &
																										// Listing pages

					System.out.println("Generate Landing and Listing Page " + id);
					sitemapArticleService.generateLandingAndListingPage(sitemapArticle, categoryList);

					System.out.println("Generate Tag Page");
					sitemapArticleService.generateTagPage(sitemapArticle, categoryList);
					
					System.out.println("Creating Invalidations ");
					dataGeneratorService.invalidate();
					
					System.out.println("Create Or Update NewsSitemap");
					sitemapPageService.createOrUpdateNewsSitemap();

				} 
				else if (contentType.equalsIgnoreCase("article")
						&& sitemapArticle.getPrimaryCategoryId() == rKCategory.getId()) {
					System.out.println("Generate RK Article Page");
					sitemapArticleService.generateArticlePage(sitemapArticle, categoryList, updateFlag);

					System.out.println("Add or Update Database");
					sitemapArticleService.addOrUpdate(sitemapArticle);

					System.out.println("Save Category Articles To Redis ");
					sitemapArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(sitemapArticle, true); // to be used in
																									// cms-proxy for
																									// Category Landing
																									// & Listing pages

					System.out.println("Generate Listing and RK Page");
					sitemapArticleService.generateListingPage(sitemapArticle, categoryList, false); //pcListing = false here
					sitemapArticleService.generateRKPage(sitemapArticle.getPrimaryCategoryId(), categoryList,
							updateFlag);
					
					System.out.println("Creating Invalidations ");
					dataGeneratorService.invalidate();
					
					System.out.println("Create Or Update NewsSitemap");
					sitemapPageService.createOrUpdateNewsSitemap();
				} 
				else if (contentType.equalsIgnoreCase("video gallery")) 
				{
					System.out.println("Generate Video Article Page");
					sitemapArticleService.generateVideoDetailPage(sitemapArticle, categoryList, updateFlag);

					System.out.println("Add or Update Database");
					sitemapArticleService.addOrUpdate(sitemapArticle);

					System.out.println("Save Latest Videos To Redis");
					sitemapArticleService.saveLatestVideosOnCreatedToRedis(contentType);

					System.out.println("Save Category Articles To Redis ");
					sitemapArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(sitemapArticle, false); // to be used in
																										// cms-proxy for
																										// Category
																										// Landing &
																										// Listing pages

					System.out.println("Generate Landing and Listing Page");
					sitemapArticleService.generateLandingAndListingPage(sitemapArticle, categoryList);
					
					System.out.println("Creating Invalidations ");
					dataGeneratorService.invalidate();

					Timestamp publishedAt = sitemapArticle.getPublishedAt();
					int year = StrapiUtils.getYearFromTimestamp(publishedAt);

					System.out.println("Create Or Update Video Sitemap");
					sitemapPageService.createOrUpdateVideoSitemap(year);
				}
			}

			System.out.println("Create Or Update Category Feed");
			feedService.createCategoryFeedPage(sitemapArticle.getPrimaryCategoryId());
			int subCategoryId = sitemapArticle.getPrimarySubCategoryId();
			if (subCategoryId != 0) {
				System.out.println("Create Or Update SubCategory Feed");
				feedService.createSubCategoryFeedPage(sitemapArticle.getPrimarySubCategoryId());

			}

			System.out.println("Create Or Update Main Feed");
			feedService.createMainFeedPage();
		}

	}

	public void onCjArticleCreate(StrapiArticle strapiArticle) throws Exception {
		if (strapiArticle != null) {
			boolean updateFlag = false;
			int cId = strapiArticle.getPrimaryCategory().getId();
			StrapiCategory pc = cjCategoryIdHm.get(cId);
			strapiArticle.setPrimaryCategory(pc);
			System.out.println("Translating Article ");
			CJArticle cjArticle = StrapiCjTranslator.translateArticle(strapiArticle);
			CJArticle existingCjArticle = cjArticleService.getByIdWOPublished(cjArticle.getId());
			if (existingCjArticle != null) {
				updateFlag = true;
			}

			String contentType = strapiArticle.getContentType();
			if (contentType != null) {
				if (contentType.equalsIgnoreCase("article")) {
					System.out.println("Generate Article Page " + cjArticle.getId() + " Update Flag " + updateFlag);
					cjArticleService.generateArticlePage(cjArticle, cjCategoryList, updateFlag);

					System.out.println("Add or Update Database with CJ Article " + cjArticle.getId());
					updateFlag = cjArticleService.addOrUpdate(cjArticle);

					System.out.println("Save CJ Latest Articles To Redis()");
					List<CJArticle> latestNews = cjArticleService.saveLatestArticlesOnCreatedToRedis(contentType);

					System.out.println("Generate CJ Latest Article Page");
					cjArticleService.generateLatestArticleListingPage(latestNews, cjRkCategory, cjOttCategory,
							cjCategoryList);

					System.out.println("Save CJ priority articles to Redis");
					cjArticleService.savePriorityArticlesToRedis(contentType);

					System.out.println("Generate CJ priority articles Page");
					List<CJArticle> priorityNews = cjArticleService.getPriorityArticles();
					cjArticleService.generatePriorityArticleListingPage(priorityNews, cjRkCategory, cjOttCategory,
							cjCategoryList);

					System.out.println("Save Category related CJ articles to Redis");
					cjArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(cjArticle); // to be used in cms-proxy
																							// for Category Landing &
																							// Listing pages

					System.out.println("Generate CJ Landing and Listing page");
					cjArticleService.generateLandingAndListingPage(cjArticle, cjRkCategory, cjOttCategory,
							cjCategoryList);

					System.out.println("Generate CJ Tag Page");
					cjArticleService.generateTagPage(cjArticle, cjRkCategory, cjOttCategory, cjCategoryList);
					
					System.out.println("Creating Invalidations ");
					cjDataGeneratorService.invalidate();

					System.out.println("Create Or Update CJ NewsSitemap");
					cjSitemapPageService.createOrUpdateCjNewsSitemap();

				}
				
				else if (contentType.equalsIgnoreCase("video gallery")) {
					System.out.println("Generate CJ Video Detail Page");
					cjArticleService.generateVideoDetailPage(cjArticle, cjCategoryList, updateFlag);

					System.out.println("Add or Update Database with CJ Article");
					updateFlag = cjArticleService.addOrUpdate(cjArticle);

					System.out.println("Save CJ Latest Videos To Redis()");
					cjArticleService.saveLatestVideosToRedis(contentType);

					System.out.println("Save CJ Category related articles to Redis");
					cjArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(cjArticle); // to be used in cms-proxy
																							// for Category Landing &
																							// Listing pages

					System.out.println("Generate CJ Landing and Listing page");
					cjArticleService.generateLandingAndListingPage(cjArticle, cjRkCategory, cjOttCategory,
							cjCategoryList);
					
					System.out.println("Creating Invalidations ");
					cjDataGeneratorService.invalidate();
					
					Timestamp publishedAt = cjArticle.getPublishedAt();
					int year = StrapiUtils.getYearFromTimestamp(publishedAt);
					System.out.println("Create Or Update CJ Video Sitemap");
					cjSitemapPageService.createOrUpdateCjVideoSitemap(year);
				}
			}

			System.out.println("Create Or Update CJ Category Feed Page");
			feedService.createCjCategoryFeedPage(cjArticle.getPrimaryCategoryId());
			int subCategoryId = cjArticle.getPrimarySubCategoryId();
			if (subCategoryId != 0) {
				System.out.println("Create Or Update CJ SubCategory Feed Page");
				feedService.createCjSubCategoryFeedPage(cjArticle.getPrimarySubCategoryId());
			}

			System.out.println("Create Or Update CJ Main Feed Page");
			feedService.createCjMainFeedPage();
		}

	}
	
	public void onCjLiveBlogCreate(StrapiArticle strapiArticle) throws Exception {
		if (strapiArticle != null) {
			boolean updateFlag = false;
			int cId = strapiArticle.getPrimaryCategory().getId();
			StrapiCategory pc = cjCategoryIdHm.get(cId);
			strapiArticle.setPrimaryCategory(pc);
			System.out.println("Translating LiveBlog ");
			CJLiveBlog cjLiveBlog = StrapiCjTranslator.translateLiveBlog(strapiArticle);
			CJLiveBlog existingLiveBlog = cjLiveBlogService.getByIdWOPublished(cjLiveBlog.getId());
			if (existingLiveBlog != null) {
				updateFlag = true;
			}

			String contentType = strapiArticle.getContentType();
				if (contentType.equalsIgnoreCase("article")) 
				{
					System.out.println("Generate LiveBlog Page " + cjLiveBlog.getId() + " Update Flag " + updateFlag);
					cjLiveBlogService.generateLiveBlogPage(cjLiveBlog, categoryList, updateFlag);

					System.out.println("Add or Update Database with CJ LiveBlog ");
					updateFlag = cjLiveBlogService.addOrUpdate(cjLiveBlog);

					System.out.println("Save CJ Latest Articles To Redis()");
					List<CJArticle> latestNews = cjArticleService.saveLatestArticlesOnCreatedToRedis(contentType);

					System.out.println("Generate CJ Latest Article Page");
					cjArticleService.generateLatestArticleListingPage(latestNews, cjRkCategory, cjOttCategory,
							cjCategoryList);

					System.out.println("Save CJ priority articles to Redis");
					cjArticleService.savePriorityArticlesToRedis(contentType);

					System.out.println("Generate CJ priority articles Page");
					List<CJArticle> priorityNews = cjArticleService.getPriorityArticles();
					cjArticleService.generatePriorityArticleListingPage(priorityNews, cjRkCategory, cjOttCategory,
							cjCategoryList);

					System.out.println("Save Category related CJ articles to Redis");
					CJArticle cja = new CJArticle();
					cja.setContentType(cjLiveBlog.getContentType());
					cja.setPrimaryCategoryId(cjLiveBlog.getPrimaryCategoryId());
					cja.setPrimarySubCategoryId(cjLiveBlog.getPrimarySubCategoryId());
					cja.setTags(cjLiveBlog.getTags());
					cja.setTagUrls(cjLiveBlog.getTagUrls());
					cjArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(cja); // to be used in cms-proxy
																							// for Category Landing &
																							// Listing pages

					System.out.println("Generate CJ Landing and Listing page");
					cjArticleService.generateLandingAndListingPage(cja, cjRkCategory, cjOttCategory,
							cjCategoryList);

					System.out.println("Generate CJ Tag Page");
					cjArticleService.generateTagPage(cja, cjRkCategory, cjOttCategory, cjCategoryList);
					
					System.out.println("Creating Invalidations ");
					cjDataGeneratorService.invalidate();

					System.out.println("Create Or Update CJ NewsSitemap");
					cjSitemapPageService.createOrUpdateCjNewsSitemap();
	
			}

			System.out.println("Create Or Update CJ Category Feed Page");
			feedService.createCjCategoryFeedPage(cjLiveBlog.getPrimaryCategoryId());
			int subCategoryId = cjLiveBlog.getPrimarySubCategoryId();
			if (subCategoryId != 0) {
				System.out.println("Create Or Update CJ SubCategory Feed Page");
				feedService.createCjSubCategoryFeedPage(cjLiveBlog.getPrimarySubCategoryId());
			}

			System.out.println("Create Or Update CJ Main Feed Page");
			feedService.createCjMainFeedPage();
		}

	}

	public void onArticleUnPublish(StrapiArticle strapiArticle, boolean deleteFlag) throws IOException {
		int id = strapiArticle.getId();
		Article article = sitemapArticleService.getByIdWOPublished(id);
		String url = null;
		if (article != null) {
			url = article.getUrl();
			article.setPublished(false);
			article.setDeleted(deleteFlag);
			sitemapArticleService.update(article);
		}

		// Update Redis lists - remove article from redis
		sitemapArticleService.saveLatestArticlesOnCreatedToRedis(StrapiConstants.STRAPI_MODEL_ARTICLE);
		sitemapArticleService.savePriorityArticlesToRedis(StrapiConstants.STRAPI_MODEL_ARTICLE);
		sitemapArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(article, false);

		// Update the CDNs correspondingly
		sitemapArticleService.generateLandingAndListingPage(article, categoryList);
		List<Article> latestArticles = sitemapArticleService.getLatestArticles(StrapiConstants.STRAPI_MODEL_ARTICLE,
				StrapiConstants.ABN_LISTING_PAGE_LIMIT);
		sitemapArticleService.generateLatestArticleListingPage(latestArticles, categoryList);
		
		if (url != null && deleteFlag) {
			System.out.println("Delete article at URL " + url);
			dataGeneratorService.deleteFileWithUrl(url);
		}

		System.out.println("Creating Invalidations ");
		dataGeneratorService.invalidate();
		
		// Update the sitemaps
		sitemapPageService.createOrUpdateNewsSitemap();
		Timestamp publishedAt = article.getPublishedAt();
		java.sql.Date publishedDate = new java.sql.Date(publishedAt.getTime());
		sitemapPageService.updatePostSitemapByDate(publishedDate);

		// Update the feeds
		feedService.createCategoryFeedPage(article.getPrimaryCategoryId());
		int subCategoryId = article.getPrimarySubCategoryId();
		if (subCategoryId != 0)
			feedService.createSubCategoryFeedPage(article.getPrimarySubCategoryId());
		feedService.createMainFeedPage();

		

	}

	public void onCjArticleUnPublish(StrapiArticle strapiArticle, boolean deleteFlag) throws IOException {
		int id = strapiArticle.getId();
		CJArticle cjArticle = cjArticleService.getByIdWOPublished(id);
		String url = null;
		if (cjArticle != null) {
			url = cjArticle.getUrl();
			cjArticle.setPublished(false);
			cjArticle.setDeleted(deleteFlag);
			cjArticleService.update(cjArticle);
		}

		// Update Redis lists - remove article from redis
		cjArticleService.saveLatestArticlesOnCreatedToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		cjArticleService.savePriorityArticlesToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		cjArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(cjArticle);

		// Update the CDNs correspondingly
		cjArticleService.generateLandingAndListingPage(cjArticle, cjRkCategory, cjOttCategory, cjCategoryList);
		List<CJArticle> latestArticles = cjArticleService.getLatestArticlesOnCreated(StrapiConstants.CONTENT_TYPE_ARTICLE,
				StrapiConstants.ABN_LISTING_PAGE_LIMIT);
		cjArticleService.generateLatestArticleListingPage(latestArticles, cjRkCategory, cjOttCategory, cjCategoryList);
		
		if (url != null && deleteFlag) {
			System.out.println("Delete article at URL " + url);
			cjDataGeneratorService.deleteFileWithUrl(url);
		}
		
		System.out.println("Creating Invalidations ");
		dataGeneratorService.invalidate();
		
		// Update the sitemaps
		cjSitemapPageService.createOrUpdateCjNewsSitemap();
		Timestamp publishedAt = cjArticle.getPublishedAt();
		java.sql.Date publishedDate = new java.sql.Date(publishedAt.getTime());
		cjSitemapPageService.updateCjPostSitemapByDate(publishedDate);

		// Update the feeds
		feedService.createCjCategoryFeedPage(cjArticle.getPrimaryCategoryId());
		int subCategoryId = cjArticle.getPrimarySubCategoryId();
		if (subCategoryId != 0)
			feedService.createCjSubCategoryFeedPage(cjArticle.getPrimarySubCategoryId());
		feedService.createCjMainFeedPage();

	}
	

	public void onCjLiveBlogUnPublish(StrapiArticle strapiArticle, boolean deleteFlag) throws IOException {
		int id = strapiArticle.getId();
		CJLiveBlog cjLiveBlog = cjLiveBlogService.getByIdWOPublished(id);
		String url = null;
		if (cjLiveBlog != null) {
			url = cjLiveBlog.getUrl();
			cjLiveBlog.setPublished(false);
			cjLiveBlog.setDeleted(deleteFlag);
			cjLiveBlogService.update(cjLiveBlog);
		}

		// Update Redis lists - remove article from redis
		cjArticleService.saveLatestArticlesOnCreatedToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		cjArticleService.savePriorityArticlesToRedis(StrapiConstants.CONTENT_TYPE_ARTICLE);
		CJArticle cja = new CJArticle();
		cja.setContentType(cjLiveBlog.getContentType());
		cja.setPrimaryCategoryId(cjLiveBlog.getPrimaryCategoryId());
		cja.setPrimarySubCategoryId(cjLiveBlog.getPrimarySubCategoryId());
		cja.setTags(cjLiveBlog.getTags());
		cja.setTagUrls(cjLiveBlog.getTagUrls());
		cjArticleService.saveCategoryRelatedArticlesOnCreatedToRedis(cja);

		// Update the CDNs correspondingly
		cjArticleService.generateLandingAndListingPage(cja, cjRkCategory, cjOttCategory, cjCategoryList);
		List<CJArticle> latestArticles = cjArticleService.getLatestArticlesOnCreated(StrapiConstants.CONTENT_TYPE_ARTICLE,
				StrapiConstants.ABN_LISTING_PAGE_LIMIT);
		cjArticleService.generateLatestArticleListingPage(latestArticles, cjRkCategory, cjOttCategory, cjCategoryList);
		
		if (url != null && deleteFlag) {
			System.out.println("Delete article at URL " + url);
			cjDataGeneratorService.deleteFileWithUrl(url);
		}
		
		System.out.println("Creating Invalidations ");
		dataGeneratorService.invalidate();
		
		// Update the sitemaps
		cjSitemapPageService.createOrUpdateCjNewsSitemap();
		Timestamp publishedAt = cja.getPublishedAt();
		java.sql.Date publishedDate = new java.sql.Date(publishedAt.getTime());
		cjSitemapPageService.updateCjPostSitemapByDate(publishedDate);

		// Update the feeds
		feedService.createCjCategoryFeedPage(cja.getPrimaryCategoryId());
		int subCategoryId = cja.getPrimarySubCategoryId();
		if (subCategoryId != 0)
			feedService.createCjSubCategoryFeedPage(cja.getPrimarySubCategoryId());
		feedService.createCjMainFeedPage();

	}

	public void onPhotoUnpublish(StrapiPhotoGallery strapiPhotoGallery, boolean deleteFlag) throws IOException {
		int id = strapiPhotoGallery.getId();
		PhotoGallery photoGallery = photoGalleryService.getByIdWOPublished(id);
		if (photoGallery != null) {
			String url = photoGallery.getUrl();
			photoGallery.setPublished(false);
			photoGallery.setDeleted(deleteFlag);
			photoGalleryService.update(photoGallery);

			photoGalleryService.saveCategoryRelatedPhotosOnCreatedToRedis(photoGallery); // to be used in cms-proxy for Category
																				// Landing & Listing pages
			photoGalleryService.generateLandingAndListingPage(photoGallery, categoryList);

			System.out.println("Save LatestPhotos To Redis()");
			photoGalleryService.saveLatestPhotosOnCreatedToRedis();
			
			if (url != null && deleteFlag) {
				System.out.println("Delete article at URL " + url);
				dataGeneratorService.deleteFileWithUrl(url);
			}
			
			System.out.println("Creating Invalidations ");
			dataGeneratorService.invalidate();

			System.out.println("Create Or Update NewsSitemap");

			Timestamp publishedAt = photoGallery.getPublishedAt();
			int year = StrapiUtils.getYearFromTimestamp(publishedAt);
			sitemapPageService.createOrUpdateGallerySitemap(year);

			// Update feeds
			feedService.createPhotoGalleryCategoryFeedPage(photoGallery.getPrimaryCategoryId());
			int subCategoryId = photoGallery.getPrimarySubCategoryId();
			if (subCategoryId != 0)
				feedService.createPhotoGallerySubCategoryFeedPage(photoGallery.getPrimarySubCategoryId());
			feedService.createMainFeedPage();

		
		}
	}

	public void onCjPhotoUnpublish(StrapiPhotoGallery strapiPhotoGallery, boolean deleteFlag) throws IOException {
		int id = strapiPhotoGallery.getId();
		CJPhotoGallery cjPhotoGallery = cjPhotoGalleryService.getByIdWOPublished(id);
		if (cjPhotoGallery != null) {
			String url = cjPhotoGallery.getUrl();
			cjPhotoGallery.setPublished(false);
			cjPhotoGallery.setDeleted(deleteFlag);
			cjPhotoGalleryService.update(cjPhotoGallery);

			cjPhotoGalleryService.saveCategoryRelatedPhotosToRedis(cjPhotoGallery); // to be used in cms-proxy for
																					// Category Landing & Listing pages
			cjPhotoGalleryService.generateLandingAndListingPage(cjPhotoGallery, cjCategoryList);

			System.out.println("Save Latest CJ Photos To Redis");
			cjPhotoGalleryService.saveLatestPhotosToRedis();
			
			if (url != null && deleteFlag) {
				System.out.println("Delete CJ article at URL " + url);
				cjDataGeneratorService.deleteFileWithUrl(url);
			}
			
			System.out.println("Creating Invalidations ");
			dataGeneratorService.invalidate();
			
			System.out.println("Create Or Update CJ NewsSitemap");

			Timestamp publishedAt = cjPhotoGallery.getPublishedAt();
			int year = StrapiUtils.getYearFromTimestamp(publishedAt);
			cjSitemapPageService.createOrUpdateCjGallerySitemap(year);

			// Update feeds
			feedService.createCjPhotoGalleryCategoryFeedPage(cjPhotoGallery.getPrimaryCategoryId());
			int subCategoryId = cjPhotoGallery.getPrimarySubCategoryId();
			if (subCategoryId != 0)
				feedService.createCjPhotoGallerySubCategoryFeedPage(cjPhotoGallery.getPrimarySubCategoryId());
			feedService.createCjMainFeedPage();

			
		}
	}

	public void onHoroscopeUnpublish(StrapiHoroscope strapiHoroscope, boolean deleteFlag) {
		boolean updateFlag = true;
		int id = strapiHoroscope.getId();
		Horoscope horoscope = sitemapHoroscopeService.getById(id);
		if (horoscope != null) {
			horoscope.setPublished(false);
			horoscope.setDeleted(deleteFlag);
			sitemapHoroscopeService.update(horoscope);
			sitemapHoroscopeService.generateHoroscopePage(horoscope.getPrimaryCategoryId(), categoryList, updateFlag);
			
			System.out.println("Creating Invalidations ");
			dataGeneratorService.invalidate();
		}

	}

	public void onCartoonUnpublish(StrapiCartoon strapiCartoon, boolean deleteFlag) {
		boolean updateFlag = true;
		int id = strapiCartoon.getId();
		Cartoon cartoon = cartoonService.getById(id);
		if (cartoon != null) {
			cartoon.setPublished(false);
			cartoon.setDeleted(deleteFlag);
			cartoonService.update(cartoon);
			cartoonService.generateCartoonPage(cartoon.getPrimaryCategoryId(), categoryList, updateFlag);
			
			System.out.println("Creating Invalidations ");
			dataGeneratorService.invalidate();
		}

	}

	public void generateHomepadeAd(HomePageAd homePageAd) {
		homepageAdService.generateAd(homePageAd);
		boolean published = homePageAd.isPublished();
		int publishedInt = published ? 1 : 0;
		cmsProxyService.save("enableHomepageAd", String.valueOf(publishedInt));
	}

	public void generateCjHomePage() throws Exception {
		String cjCategoryIdsList = cmsProxyService.get("cj_categoryIdsList");
		String[] cjCategoryArray = cjCategoryIdsList.split(",");

		List<CJArticle> latestCjArticles = new ArrayList<>();
		latestCjArticles = cjArticleService.getLatestArticlesFromCMS();

		List<CJArticle> latestCjVideos = new ArrayList<>();
		latestCjVideos = cjArticleService.getLatestVideosFromCMS();

		List<CJArticle> priorityCjArticles = new ArrayList<>();
		priorityCjArticles = cjArticleService.getPriorityArticlesFromCMS();
		//List<Object> rankingArticles = cjArticleService.getRankingArticles();

		List<CJArticle> trailerCjArticles = cjArticleService.getLatestBySubCategory(
				StrapiConstants.CONTENT_TYPE_VIDEO, cjTrailersSubcategory.getCategory().getId(),
				cjTrailersSubcategory.getId(), StrapiConstants.LIMIT, null);

		List<CJPhotoGallery> latestCjPhotos = new ArrayList<>();

		HashMap<String, List<CJArticle>> cjCategoryRelatedArticles = new HashMap<>();

		for (String cjCategoryStr : cjCategoryArray) {
			String key = null;
			List<CJArticle> cjCategoryArticles = new ArrayList<>();
			int cjCategoryInt = Integer.parseInt(cjCategoryStr);

			if (cjCategoryInt == cjPhotoCategory.getId()) {

				latestCjPhotos = cjPhotoGalleryService.getLatestPhotosFromRedis(StrapiConstants.LIMIT);
			} else {

				key = "cj_cat_" + cjCategoryStr;
				List<String> cjArticlesStrList = cmsProxyService.getList(key, 0, StrapiConstants.LIMIT);
				if (!cjArticlesStrList.isEmpty()) {
					CJArticle cjArticle;
					for (String cjStr : cjArticlesStrList) {
						cjArticle = JsonUtils.deserialize(cjStr, CJArticle.class);
						cjCategoryArticles.add(cjArticle);
					}
					cjCategoryRelatedArticles.put(key, cjCategoryArticles);
				}

			}
		}
		String ehd = cmsProxyService.get("enableCJHomepageAd");
		int enableCjHomepageAd = 0;
		if (ehd != null)
			enableCjHomepageAd = Integer.parseInt(ehd);
		System.out.println("Generate CJ Home Page");
		cjDataGeneratorService.generateHomePage(latestCjArticles, latestCjVideos, latestCjPhotos, priorityCjArticles,
				trailerCjArticles, cjCategoryRelatedArticles, enableCjHomepageAd, cjCategoryList);
	}

	public void generateHomePage() throws Exception {
		String categoryIdsList = cmsProxyService.get("categoryIdsList");
		String[] categoryArray = categoryIdsList.split(",");

		List<Article> latestArticles = new ArrayList<>();
		latestArticles = sitemapArticleService.getLatestArticlesFromCMS();

		List<Article> latestVideos = new ArrayList<>();
		latestVideos = sitemapArticleService.getLatestVideosFromCMS();

		List<Object> priorityArticles = new ArrayList<>();
		//priorityArticles = sitemapArticleService.getPriorityArticlesFromCMS();
		priorityArticles = sitemapArticleService.getRankingArticles();
		
		List<Article> tagRelatedArticles = new ArrayList<>();
		tagRelatedArticles = sitemapArticleService.getTagRelatedArticles(StrapiConstants.HOMEPAGE_TAG);
		
		List<Article> abnVideosArticles = new ArrayList<>();
		abnVideosArticles = sitemapArticleService.getLatestBySubCategoryWithoutPriority(StrapiConstants.CONTENT_TYPE_VIDEO,
				abnVideosSubcategory.getCategory().getId(), abnVideosSubcategory.getId(), StrapiConstants.LIMIT, 0);

		List<PhotoGallery> latestPhotos = new ArrayList<>();
		List<Cartoon> cartoonList = new ArrayList<>();

		HashMap<String, List<Article>> categoryRelatedArticles = new HashMap<>();

		for (String categoryStr : categoryArray) {
			String key = null;
			List<Article> categoryArticles = new ArrayList<>();
			int categoryInt = Integer.parseInt(categoryStr);

			if (categoryInt == photoCategory.getId()) {

				latestPhotos = photoGalleryService.getLatestPhotosFromRedis(StrapiConstants.LIMIT);
			}

			else if (categoryInt == cartoonCategory.getId())
				cartoonList = cartoonService.getLatestCartoonsFromRedis(StrapiConstants.LIMIT);

			else if (categoryInt == horoscopeCategory.getId()) {
				sitemapHoroscopeService.getLatestHoroscopes(StrapiConstants.LIMIT);
			} else {

				key = "cat_" + categoryStr;
				List<String> articlesStrList = cmsProxyService.getList(key, 0, StrapiConstants.LIMIT);
				if (!articlesStrList.isEmpty()) {
					Article sitemapArticle;
					for (String saStr : articlesStrList) {
						sitemapArticle = JsonUtils.deserialize(saStr, Article.class);
						categoryArticles.add(sitemapArticle);
					}
					categoryRelatedArticles.put(key, categoryArticles);
				}

			}
		}
		String ehd = cmsProxyService.get("enableHomepageAd");
		int enableHomepageAd = 0;
		if (ehd != null)
			enableHomepageAd = Integer.parseInt(ehd);
		dataGeneratorService.generateHomePage(latestArticles, latestVideos, latestPhotos, priorityArticles, tagRelatedArticles, abnVideosArticles, cartoonList,
				categoryRelatedArticles, enableHomepageAd, categoryList);
	}

	public void generateElectionVoteJson(ElectionVote electionVote, String model) {
		cmsProxyService.save(model, JsonUtils.toString(electionVote));
		dataGeneratorService.generateElectionVoteJson(electionVote, model);

	}

	public List<Object> saveRankingDashboardItems(RankingDashboard rankingDashboard) {
		List<RankingItem> rankingItems = rankingDashboard.getRankingItems();
		
		List<String> allIdsList = new ArrayList<>();
		
		List<Long> abnAvIds = new ArrayList<>();
		List<Article> abnAvArticles = new ArrayList<>();
		HashMap<String, Article> abnAvMap = new HashMap<>();
				
		List<Long> abnPhotoIds = new ArrayList<>();
		List<PhotoGallery> abnPhotos = new ArrayList<>();
		HashMap<String, PhotoGallery> abnPhotoMap = new HashMap<>();

				
		List<Long> cjAvIds = new ArrayList<>();
		List<CJArticle> cjAvArticles = new ArrayList<>();
		HashMap<String, CJArticle> cjAvMap = new HashMap<>();

				
		List<Long> cjPhotoIds = new ArrayList<>();
		List<CJPhotoGallery> cjPhotos = new ArrayList<>();
		HashMap<String, CJPhotoGallery> cjPhotoMap = new HashMap<>();

		
		List<Object> topItems = new ArrayList<>();
		
		for (RankingItem rankingItem : rankingItems) {
			long id = rankingItem.getStoryId();
			if(rankingItem.getStoryType().equalsIgnoreCase(StrapiConstants.RANKING_ITEM_STORY_TYPE_ARTICLE))
			{
				if(rankingItem.getWebsite().equalsIgnoreCase(StrapiConstants.RANKING_ITEM_WEBSITE_ABN))
				{
					allIdsList.add("abn_av_" + id);
					abnAvIds.add(id);
				}
				else if(rankingItem.getWebsite().equalsIgnoreCase(StrapiConstants.RANKING_ITEM_WEBSITE_CJ))
				{
					allIdsList.add("cj_av_" + id);
					cjAvIds.add(id);
				} 
			}
			else if(rankingItem.getStoryType().equalsIgnoreCase(StrapiConstants.RANKING_ITEM_STORY_TYPE_PHOTO))
			{
				if(rankingItem.getWebsite().equalsIgnoreCase(StrapiConstants.RANKING_ITEM_WEBSITE_ABN))
				{
					allIdsList.add("abn_photo_" + id);
					abnPhotoIds.add(id);
				}
				else if(rankingItem.getWebsite().equalsIgnoreCase(StrapiConstants.RANKING_ITEM_WEBSITE_CJ))
				{
					allIdsList.add("cj_photo_" + id);
					cjPhotoIds.add(id);
				} 
			}
		}
		
		abnAvArticles = sitemapArticleService.getByIds(abnAvIds);
		for (Article abnAvArticle : abnAvArticles) {
			String key = "abn_av_" + abnAvArticle.getId();
			abnAvMap.put(key, abnAvArticle);
		}
		
		abnPhotos = photoGalleryService.getByIds(abnPhotoIds);
		for (PhotoGallery abnPhoto : abnPhotos) {
			String key = "abn_photo_" + abnPhoto.getId();
			abnPhotoMap.put(key, abnPhoto);
		}
		
		cjAvArticles = cjArticleService.getByIds(cjAvIds);
		for (CJArticle cjAvArticle : cjAvArticles) {
			String key = "cj_av_" + cjAvArticle.getId();
			cjAvMap.put(key, cjAvArticle);
		}
		
		cjPhotos = cjPhotoGalleryService.getByIds(cjPhotoIds);
		for (CJPhotoGallery cjPhoto : cjPhotos) {
			String key = "cj_photo_" + cjPhoto.getId();
			cjPhotoMap.put(key, cjPhoto);
		}
		
		for (String rankingItemId : allIdsList) {
			if(abnAvMap.containsKey(rankingItemId))
				{
				Article a = abnAvMap.get(rankingItemId);
				a.setWebsite("ABN");
				topItems.add(a);
				}
			else if(cjAvMap.containsKey(rankingItemId))
				{
				CJArticle cja = cjAvMap.get(rankingItemId);
				cja.setWebsite("CJ");
				topItems.add(cja);
				}
			else if(abnPhotoMap.containsKey(rankingItemId))
				{
				PhotoGallery pg = abnPhotoMap.get(rankingItemId);
				pg.setWebsite("ABN");
				topItems.add(pg);
				}
			else if(cjPhotoMap.containsKey(rankingItemId))
				{
				CJPhotoGallery cjPg = cjPhotoMap.get(rankingItemId);
				cjPg.setWebsite("CJ");
				topItems.add(cjPg);
				}
		}
		List<String> values = new ArrayList<>();
		if(topItems != null && !topItems.isEmpty())
		{
			cmsProxyService.delete("rankingArticles");
			for (Object sa : topItems) {
				values.add(JsonUtils.toString(sa));
			}
			cmsProxyService.saveToList("rankingArticles", values);
		}
		return topItems;
	}

}
