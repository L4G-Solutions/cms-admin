package test.com.andromeda.cms.admin.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.cms.service.SubCategoryService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.cms.translator.StrapiTranslator;
import com.andromeda.commons.util.FileNDirUtils;
import com.andromeda.commons.util.PropertiesUtils;
import com.andromeda.migration.model.ArticleStatus;
import com.andromeda.migration.service.ArticleStatusService;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class MigratedArticleCollector {

	public static void main(String[] args) throws Exception {
		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
		int startIndex = Integer.valueOf(prop.getProperty("startIndex"));
		int endIndex = Integer.valueOf(prop.getProperty("endIndex"));
		
		//String inputFileName = prop.getProperty("inputFileName");
		//List<String> idStrList =  FileNDirUtils.getFileContentAsLines(inputFileName);

		StrapiContentService strapiContentService = TestContextUtils.getStrapiContentService();
		ArticleService sitemapArticleService = TestContextUtils.getSitemapArticleService();
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		
		CategoryService categoryService = TestContextUtils.getSitemapCategoryService();
		SubCategoryService subCategoryService = TestContextUtils.getSitemapSubCategoryService();
		SitemapPageService sitemapPageService = TestContextUtils.getSitemapPageService();
		ArticleStatusService articleStatusService = TestContextUtils.getArticleStatusService();
		FeedService feedService = TestContextUtils.getFeedService();
		feedService.init();

		HashMap<Integer, StrapiCategory> categoryMap = new HashMap<>();
		HashMap<Integer, List<StrapiSubCategory>> subCategoryMap = new HashMap<>();
		
		List<StrapiCategory> categoryList = strapiCategoryService.getAllCategories();
		
		for (StrapiCategory strapiCategory : categoryList) 
		{
			int strapiCategoryId = strapiCategory.getId();
			categoryMap.put(strapiCategoryId, strapiCategory);
			
			Category c = categoryService.getById(strapiCategoryId);
			
		}
		
		List<Category> categories = categoryService.getAll();

		StrapiWebhooksService strapiWebhooksService = TestContextUtils.getStrapiWebhooksService();
		strapiWebhooksService.init();
		List<StrapiCategory> strapiCategories = strapiCategoryService.getByCategoryName("Open Heart");
		
		String tableName = null;
		String fileName = "output" + File.separator + "failed.csv";
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
		FileNDirUtils.createDir(fileName);
		
		//for (String idStr : idStrList) 
		for (int i = startIndex; i <= endIndex; i++) 
		{
			//int i = Integer.parseInt(idStr.trim());
			if(i < 930002)
				tableName = "mainarticles";
			else
				tableName = "articles";
			
			String url = "https://cms.andhrajyothy.com/api/"+ tableName +"/" + i
					+ "?populate[1]=primaryCategory&populate[5]=articleTextpopulate[7]=articleText.documentUpload&populate[6]=articleText.articleText&populate[8]=primaryImage&populate[11]=location&populate[2]=primarySubCategory&populate[3]=secondaryCategories&populate[4]=secondarySubCategories&populate[10]=tags&populate[9]=referenceArticles";
			StrapiResponse sr = strapiContentService.getArticle(url);
			boolean status = false;
			if (sr != null) {
				try {
					Entry entry = sr.getEntry();
					HashMap<String, Object> attrs = entry.getAttributes();
					String contentType = (String) attrs.get("contentType");
					if (contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_ARTICLE) || contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_VIDEO)) {
						StrapiArticle sa = strapiArticleService.getArticleFromCMS(entry);
						if (sa != null) {
							int cId = sa.getPrimaryCategory().getId();
							sa.setPrimaryCategory(categoryMap.get(cId));
							status = strapiWebhooksService.onArticleCreateForCMS(sa);
						}
					} else {
						System.out.println("Article Id :" + i + "is not of content type Article");
					}
				} catch (Exception e) {
					int id = sr.getEntry().getId();
					articleStatusService.add(new ArticleStatus(id, null , status));
					writer.write(sr.toString());
					e.printStackTrace();
				}
			} else {
				System.out.println("Article Id :" + i + "does not exist");
			}
		}
		writer.close();
	}


}
