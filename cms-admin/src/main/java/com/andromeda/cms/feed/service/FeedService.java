package com.andromeda.cms.feed.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CJArticleService;
import com.andromeda.cms.service.CJCategoryService;
import com.andromeda.cms.service.CJDataGeneratorService;
import com.andromeda.cms.service.CJPhotoGalleryService;
import com.andromeda.cms.service.CJSubCategoryService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.PhotoGalleryService;
import com.andromeda.cms.service.SubCategoryService;

@Service
public class FeedService 
{
	@Autowired
	public ArticleService articleService;
	
	@Autowired
	public CJArticleService cjArticleService;
	
	@Autowired
	public PhotoGalleryService photoGalleryService;
	
	@Autowired
	public CJPhotoGalleryService cjPhotoGalleryService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	CJCategoryService cjCategoryService;
	
	@Autowired
	SubCategoryService subCategoryService;
	
	@Autowired
	CJSubCategoryService cjSubCategoryService;
	
	@Autowired
	DataGeneratorService dataGeneratorService;
	
	@Autowired
	CJDataGeneratorService cjDataGeneratorService;
	
	public void setArticleService(ArticleService articleService)
	{
		this.articleService = articleService;
	}
	
	public void setCjArticleService(CJArticleService articleService)
	{
		this.cjArticleService = cjArticleService;
	}
	
	public void setCategoryService(CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
	
	public void setCjCategoryService(CJCategoryService cjCategoryService)
	{
		this.cjCategoryService = cjCategoryService;
	}
	
	public void setSubCategoryService(SubCategoryService subCategoryService)
	{
		this.subCategoryService = subCategoryService;
	}
	
	public void setCjSubCategoryService(CJSubCategoryService cjSubCategoryService)
	{
		this.cjSubCategoryService = cjSubCategoryService;
	}
	
	public void setDataGeneratorService(DataGeneratorService dataGeneratorService)
	{
		this.dataGeneratorService = dataGeneratorService;
	}
	
	public void setCjDataGeneratorService(CJDataGeneratorService cjDataGeneratorService)
	{
		this.cjDataGeneratorService = cjDataGeneratorService;
	}
	
	public List<Category> categoryList;
	public HashMap<String,Category> categoryMap = new HashMap<>();
	
	public List<CJCategory> cjCategoryList;
	public HashMap<String,CJCategory> cjCategoryMap = new HashMap<>();
	
	public List<SubCategory> subCategoryList;
	public HashMap<String,SubCategory> subCategoryMap = new HashMap<>();
	
	public List<CJSubCategory> cjSubCategoryList;
	public HashMap<String,CJSubCategory> cjSubCategoryMap = new HashMap<>();
	
	@PostConstruct
	public  void init()
	{
		categoryList = categoryService.getAll();
		for (Category category : categoryList) {
			categoryMap.put(String.valueOf(category.getId()), category);
		}
		
		subCategoryList =subCategoryService.getAll();
		for (SubCategory subCategory : subCategoryList) {
			subCategoryMap.put(String.valueOf(subCategory.getId()), subCategory);
		}
	}
	
	@PostConstruct
	public  void initCj()
	{
		cjCategoryList = cjCategoryService.getAll();
		for (CJCategory cjCategory : cjCategoryList) {
			cjCategoryMap.put(String.valueOf(cjCategory.getId()), cjCategory);
		}
		
		cjSubCategoryList =cjSubCategoryService.getAll();
		for (CJSubCategory cjSubCategory : cjSubCategoryList) {
			cjSubCategoryMap.put(String.valueOf(cjSubCategory.getId()), cjSubCategory);
		}
	}

	public void createMainFeedPage() {
		List<Article> feedArticles = articleService.getLatestArticlesWithoutPriority(null, StrapiConstants.FEED_LIMIT);
		dataGeneratorService.generateMainFeedPage(feedArticles, categoryMap, subCategoryMap);
	}
	
	public void createCjMainFeedPage() {
		List<CJArticle> feedCjArticles = cjArticleService.getLatestArticlesWithoutPriority(null, StrapiConstants.FEED_LIMIT);
		cjDataGeneratorService.generateMainFeedPage(feedCjArticles, cjCategoryMap, cjSubCategoryMap);
	}

	public void createCategoryFeedPage(int categoryId) {
		Category primaryCategory = categoryMap.get(String.valueOf(categoryId));
		List<Article> feedArticles = articleService.getLatestByCategoryIdWithoutPriority(categoryId, StrapiConstants.FEED_LIMIT);

		dataGeneratorService.generateCategoryFeedPage(feedArticles, primaryCategory, categoryMap, subCategoryMap);
		
	}
	
	public void createCjCategoryFeedPage(int cjCategoryId) {
		CJCategory primaryCategory = cjCategoryMap.get(String.valueOf(cjCategoryId));
		List<CJArticle> cjFeedArticles = cjArticleService.getLatestByCategoryIdWithoutPriority(cjCategoryId, StrapiConstants.FEED_LIMIT);

		cjDataGeneratorService.generateCategoryFeedPage(cjFeedArticles, primaryCategory, cjCategoryMap, cjSubCategoryMap);
		
	}

	public void createSubCategoryFeedPage(int subCategoryId) {
		SubCategory primarySubCategory = subCategoryMap.get(String.valueOf(subCategoryId));
		int categoryId = primarySubCategory.getCategoryId();
		Category category = categoryMap.get(String.valueOf(categoryId));
		List<Article> feedArticles = articleService.getLatestBySubCategoryWithoutPriority(null, categoryId, subCategoryId, StrapiConstants.FEED_LIMIT, 0);

		dataGeneratorService.generateSubCategoryFeedPage(feedArticles, category,primarySubCategory, categoryMap, subCategoryMap);
		
	}
	
	public void createCjSubCategoryFeedPage(int cjSubCategoryId) {
		CJSubCategory primarySubCategory = cjSubCategoryMap.get(String.valueOf(cjSubCategoryId));
		int categoryId = primarySubCategory.getCategoryId();
		CJCategory cjCategory = cjCategoryMap.get(String.valueOf(categoryId));
		List<CJArticle> cjFeedArticles = cjArticleService.getLatestBySubCategoryWithoutPriority(null, categoryId, cjSubCategoryId, StrapiConstants.FEED_LIMIT, 0);

		cjDataGeneratorService.generateSubCategoryFeedPage(cjFeedArticles, cjCategory,primarySubCategory, cjCategoryMap, cjSubCategoryMap);
		
	}

	public void createAllCategoryFeedPages() {
		for (Category category : categoryList) {
			List<Article> feedArticles = articleService.getLatestByCategoryIdWithoutPriority(category.getId(), StrapiConstants.FEED_LIMIT);
			dataGeneratorService.generateCategoryFeedPage(feedArticles, category, categoryMap, subCategoryMap);
		}
		
	}
	
	public void createAllCjCategoryFeedPages() {
		for (CJCategory cjCategory : cjCategoryList) {
			List<CJArticle> cjFeedArticles = cjArticleService.getLatestByCategoryIdWithoutPriority(cjCategory.getId(), StrapiConstants.FEED_LIMIT);
			cjDataGeneratorService.generateCategoryFeedPage(cjFeedArticles, cjCategory, cjCategoryMap, cjSubCategoryMap);
		}
		
	}

	public void createAllSubCategoryFeedPages() {
		for (SubCategory subCategory : subCategoryList) {
			int subCategoryId = subCategory.getId();
			int categoryId = subCategory.getCategoryId();
			Category category = categoryMap.get(String.valueOf(categoryId));
			List<Article> feedArticles = articleService.getLatestBySubCategoryWithoutPriority(StrapiConstants.STRAPI_MODEL_ARTICLE, categoryId, subCategoryId, StrapiConstants.FEED_LIMIT, 0);
			dataGeneratorService.generateSubCategoryFeedPage(feedArticles, category,subCategory, categoryMap, subCategoryMap);
		}
		
	}
	
	public void createAllCjSubCategoryFeedPages() {
		for (CJSubCategory cjSubCategory : cjSubCategoryList) {
			int cjSubCategoryId = cjSubCategory.getId();
			int cjCategoryId = cjSubCategory.getCategoryId();
			CJCategory cjCategory = cjCategoryMap.get(String.valueOf(cjCategoryId));
			List<CJArticle> cjFeedArticles = cjArticleService.getLatestBySubCategoryWithoutPriority(StrapiConstants.STRAPI_MODEL_ARTICLE, cjCategoryId, cjSubCategoryId, StrapiConstants.FEED_LIMIT, 0);
			cjDataGeneratorService.generateSubCategoryFeedPage(cjFeedArticles, cjCategory, cjSubCategory, cjCategoryMap, cjSubCategoryMap);
		}
		
	}

	public void createPhotoGalleryCategoryFeedPage(int primaryCategoryId) {
		List<PhotoGallery> feedPhotos = photoGalleryService.getLatestPhotosWithoutPriority(primaryCategoryId, StrapiConstants.FEED_LIMIT);
		Category primaryCategory = categoryMap.get(String.valueOf(primaryCategoryId));
		dataGeneratorService.generatePhotoGalleryCategoryFeedPage(feedPhotos, primaryCategory, categoryMap, subCategoryMap);
	}
	
	public void createCjPhotoGalleryCategoryFeedPage(int primaryCategoryId) {
		List<CJPhotoGallery> cjFeedPhotos = cjPhotoGalleryService.getLatestPhotosWithoutPriority(primaryCategoryId, StrapiConstants.FEED_LIMIT);
		CJCategory primaryCategory = cjCategoryMap.get(String.valueOf(primaryCategoryId));
		cjDataGeneratorService.generatePhotoGalleryCategoryFeedPage(cjFeedPhotos, primaryCategory, cjCategoryMap, cjSubCategoryMap);
	}

	
	public void createPhotoGallerySubCategoryFeedPage(int subCategoryId) {
		SubCategory primarySubCategory = subCategoryMap.get(String.valueOf(subCategoryId));
		int categoryId = primarySubCategory.getCategoryId();
		Category category = categoryMap.get(String.valueOf(categoryId));
		List<PhotoGallery> feedPhotos = photoGalleryService.getLatestPhotosBySubCategoryWithoutPriority(categoryId, subCategoryId, StrapiConstants.FEED_LIMIT, 0);

		dataGeneratorService.generatePhotoGallerySubCategoryFeedPage(feedPhotos, category,primarySubCategory, categoryMap, subCategoryMap);
		
	}

	public void createCjPhotoGallerySubCategoryFeedPage(int subCategoryId) {
		CJSubCategory primarySubCategory = cjSubCategoryMap.get(String.valueOf(subCategoryId));
		int cjCategoryId = primarySubCategory.getCategoryId();
		CJCategory category = cjCategoryMap.get(String.valueOf(cjCategoryId));
		List<CJPhotoGallery> cjFeedPhotos = cjPhotoGalleryService.getLatestPhotosBySubCategoryWithoutPriority(cjCategoryId, subCategoryId, StrapiConstants.FEED_LIMIT, 0);

		cjDataGeneratorService.generatePhotoGallerySubCategoryFeedPage(cjFeedPhotos, category,primarySubCategory, cjCategoryMap, cjSubCategoryMap);
		
	}
	

}
