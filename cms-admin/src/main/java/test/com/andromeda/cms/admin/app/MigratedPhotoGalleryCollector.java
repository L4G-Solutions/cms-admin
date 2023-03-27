package test.com.andromeda.cms.admin.app;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.PhotoGalleryService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.commons.util.PropertiesUtils;
import com.andromeda.migration.service.ArticleStatusService;

import test.com.andromeda.cms.admin.util.TestContextUtils;


public class MigratedPhotoGalleryCollector {

	public static void main(String[] args) throws Exception 
	{
		StrapiCategory photoGalleryCategory = null;
		HashMap<Integer, StrapiSubCategory> subCategoryHm = new HashMap<>();
		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
		int startIndex = Integer.valueOf(prop.getProperty("startIndex"));
		int endIndex = Integer.valueOf(prop.getProperty("endIndex"));
		
		PhotoGalleryService photoGalleryService = TestContextUtils.getPhotoGalleryService();
		StrapiContentService strapiContentService = TestContextUtils.getStrapiContentService();
		ArticleService sitemapArticleService = TestContextUtils.getSitemapArticleService();
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		CategoryService categoryService = TestContextUtils.getSitemapCategoryService();
		SitemapPageService sitemapPageService = TestContextUtils.getSitemapPageService();
		ArticleStatusService articleStatusService = TestContextUtils.getArticleStatusService();
		FeedService feedService = TestContextUtils.getFeedService();
		feedService.init();

		DataGeneratorService dataGeneratorService = TestContextUtils.getDataGeneratorService();
		List<Category> categories = categoryService.getAll();

		StrapiWebhooksService strapiWebhooksService = TestContextUtils.getStrapiWebhooksService();
		strapiWebhooksService.init();
		List<StrapiCategory> strapiCategories = strapiCategoryService.getByCategoryName(StrapiConstants.STRAPI_CATEGORY_PHOTOGALLERY);
		photoGalleryCategory = strapiCategories.get(0);
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		List<StrapiSubCategory> photoSubCategories =  strapiCategoryService.getSubCategories(photoGalleryCategory.getId());
		for (StrapiSubCategory strapiSubCategory : photoSubCategories) 
		{
			strapiSubCategory.setCategory(photoGalleryCategory);
			subCategoryHm.put(strapiSubCategory.getId(), strapiSubCategory);
		}
		
		
		strapiWebhooksService.init();
		String tableName = null;
		for(int i = startIndex ; i<= endIndex; i++)
		{
			//int i = Integer.parseInt(idStr.trim());
			if(i < 1602)
				tableName = "mainphotogalleries";
			else
				tableName = "photogalleries";
			String url = "http://3.108.187.218:1337/api/" + tableName+"/" + i 
					+ "?populate[0]=photoSlider"
					+ "&populate[2]=photoSlider.imageURL";
			System.out.println(url);
			StrapiResponse sr = strapiContentService.getArticle(url);
			Entry entry = sr.getEntry();
			HashMap<String, Object> attrs = entry.getAttributes();
		
			StrapiPhotoGallery spg = strapiArticleService.getPhotoFromCMS(entry);
			if(spg != null)
			{
				spg.setCategory(photoGalleryCategory);
				int subCategoryId = spg.getSubCategory().getId();
				StrapiSubCategory subCat =	subCategoryHm.get(subCategoryId);
				spg.setSubCategory(subCat);
				strapiWebhooksService.onPhotoCreateCMS(spg);
			}
			else
			{
				System.out.println("Photo Gallery object null");
			}
		}
		System.out.println("Save LatestPhotos To Redis()");
		photoGalleryService.saveLatestPhotosToRedis();

	}

}
