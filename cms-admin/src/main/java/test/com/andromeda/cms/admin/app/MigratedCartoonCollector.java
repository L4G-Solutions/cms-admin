package test.com.andromeda.cms.admin.app;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiCartoon;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.commons.util.PropertiesUtils;
import com.andromeda.migration.service.ArticleStatusService;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class MigratedCartoonCollector 
{
	public static void main(String[] args) throws Exception 
	{
		StrapiCategory cartoonCategory = null;
		HashMap<Integer, StrapiSubCategory> subCategoryHm = new HashMap<>();
		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
		int startIndex = Integer.valueOf(prop.getProperty("startIndex"));
		int endIndex = Integer.valueOf(prop.getProperty("endIndex"));
		
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
		List<StrapiCategory> strapiCategories = strapiCategoryService.getByCategoryName(StrapiConstants.STRAPI_CATEGORY_CARTOON);
		cartoonCategory = strapiCategories.get(0);
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		/*List<StrapiSubCategory> photoSubCategories =  strapiCategoryService.getSubCategories(photoGalleryCategory.getId());
		for (StrapiSubCategory strapiSubCategory : photoSubCategories) 
		{
			strapiSubCategory.setCategory(photoGalleryCategory);
			subCategoryHm.put(strapiSubCategory.getId(), strapiSubCategory);
		}*/
		
		
		strapiWebhooksService.init();
		
		for(int i = startIndex ; i<= endIndex; i++)
		{
			String url = "http://3.108.187.218:1337/api/cartoons/" + i 
					+ "?populate=*";
			System.out.println(url);
			StrapiResponse sr = strapiContentService.getArticle(url);
			if(sr != null)
			{
				try 
				{
				Entry entry = sr.getEntry();
				HashMap<String, Object> attrs = entry.getAttributes();
			
				StrapiCartoon sc = strapiArticleService.getCartoonFromCMS(entry);
					if(sc != null)
					{
						sc.setCategory(cartoonCategory);
						strapiWebhooksService.onCartoonCreate(sc);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Photo Gallery object null");
			}
			
		}

	}
}
