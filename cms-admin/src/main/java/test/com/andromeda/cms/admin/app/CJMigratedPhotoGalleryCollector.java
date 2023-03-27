package test.com.andromeda.cms.admin.app;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.service.CJCategoryService;
import com.andromeda.cms.service.CJDataGeneratorService;
import com.andromeda.cms.service.CJPhotoGalleryService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.cms.sitemap.service.CJSitemapPageService;
import com.andromeda.commons.util.PropertiesUtils;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class CJMigratedPhotoGalleryCollector {
	public static void main(String[] args) throws Exception 
	{
		StrapiCategory cjPhotoGalleryCategory = null;
		HashMap<Integer, StrapiSubCategory> subCategoryHm = new HashMap<>();
		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
		int startIndex = Integer.valueOf(prop.getProperty("startIndex"));
		int endIndex = Integer.valueOf(prop.getProperty("endIndex"));
		
		CJPhotoGalleryService photoGalleryService = TestContextUtils.getCjPhotoGalleryService();
		StrapiContentService strapiContentService = TestContextUtils.getStrapiContentService();
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		CJCategoryService categoryService = TestContextUtils.getCjCategoryService();
		CJSitemapPageService sitemapPageService = TestContextUtils.getCjSitemapPageService();
		FeedService feedService = TestContextUtils.getFeedService();
		feedService.init();

		CJDataGeneratorService dataGeneratorService = TestContextUtils.getCjDataGeneratorService();
		List<CJCategory> categories = categoryService.getAll();

		StrapiWebhooksService strapiWebhooksService = TestContextUtils.getStrapiWebhooksService();
		strapiWebhooksService.init();
		List<StrapiCategory> strapiCategories = strapiCategoryService.getByCategoryName(StrapiConstants.STRAPI_CJ_CATEGORY_PHOTOGALLERY);
		cjPhotoGalleryCategory = strapiCategories.get(0);
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		List<StrapiSubCategory> photoSubCategories =  strapiCategoryService.getSubCategories(cjPhotoGalleryCategory.getId());
		for (StrapiSubCategory strapiSubCategory : photoSubCategories) 
		{
			strapiSubCategory.setCategory(cjPhotoGalleryCategory);
			subCategoryHm.put(strapiSubCategory.getId(), strapiSubCategory);
		}
		
		
		strapiWebhooksService.initCj();
		
		for(int i = startIndex ; i<= endIndex; i++)
		{
			String url = "http://cms.andhrajyothy.com/api/cj-photogalleries-2022s/" + i 
					+ "?populate[0]=photoSlider"
					+ "&populate[2]=photoSlider.imageURL";
			System.out.println(url);
			StrapiResponse sr = strapiContentService.getArticle(url);
			Entry entry = sr.getEntry();
			HashMap<String, Object> attrs = entry.getAttributes();
		
			StrapiPhotoGallery spg = strapiArticleService.getCjPhotoFromCMS(entry);
			if(spg != null)
			{
				spg.setCategory(cjPhotoGalleryCategory);
				
				//int subCategoryId = spg.getSubCategory().getId();
				//StrapiSubCategory subCat =	subCategoryHm.get(subCategoryId);
				//spg.setSubCategory(subCat);
				strapiWebhooksService.onCjPhotoCreateCMS(spg);
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
