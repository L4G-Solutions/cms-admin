package com.andromeda.cms.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.AppJson;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Cartoon;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.Feed;
import com.andromeda.cms.model.HomePageAd;
import com.andromeda.cms.model.Horoscope;
import com.andromeda.cms.model.ElectionVote;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.model.RedirectionUrl;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.sitemap.model.SitemapLocation;
import com.andromeda.cms.sitemap.model.SitemapPostLocation;
import com.andromeda.commons.model.BaseModel;
import com.andromeda.commons.util.FileNDirUtils;
import com.andromeda.commons.util.JsonUtils;
import com.andromeda.commons.util.PropertiesUtils;
import com.lowagie.text.DocumentException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

@Service
public class DataGeneratorService
{
	private static final Logger logger = LoggerFactory.getLogger(DataGeneratorService.class);

	@Autowired
	DataUploader dataUploader;
	
	@Value("${templates.dir}")
	private static String templatesDir;
	
	@Value("${domain.name}")
	private static String DOMAIN_NAME;
	
	private Configuration cfg;
	private DataWriter dataWriter;
	
	List<String> invalidationUrls = new ArrayList<>();

	@PostConstruct
	public void init() throws IOException
	{
		configureFreemarker();
		dataWriter = new DataWriter();
		dataUploader = new DataUploader();
	}
	
	/**
	 * function to clear the invalidationUrls list
	 * 
	 */
	public void clearInvalidationUrlList()
	{
		invalidationUrls.clear();
	}
	
	public void invalidate()
	{
		dataUploader.invalidateFiles(invalidationUrls);
		invalidationUrls.clear();
	}
	
	public String generateHtmlforFeed(Feed feed) throws Exception
	{
		String htmlFileName = dataWriter.writeFeedInHtml(cfg, feed);
		return htmlFileName;
	}

	public void configureFreemarker() throws IOException
	{
		// 1. Configure FreeMarker
		//
		// You should do this ONLY ONCE, when your application starts,
		// then reuse the same Configuration object elsewhere.
		
		//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
		Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
		String templatesDir = prop.getProperty("templates.dir"); 
		System.out.println("Freemarker Template directory " + templatesDir);
		cfg = new Configuration();
		cfg.setAPIBuiltinEnabled(true);

		// Where do we load the templates from:
		cfg.setClassForTemplateLoading(DataGeneratorService.class, templatesDir);
		cfg.setDirectoryForTemplateLoading(new File (templatesDir));
		// Some other recommended settings:
		cfg.setIncompatibleImprovements(new Version(2, 3, 20));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	public void generateSitemapIndexXml(String ftlFileName, String outputFile, List<SitemapLocation> sitemapLocations) 
	{
		if (sitemapLocations.size() != 0)
		{
			try
			{
				Template template = cfg.getTemplate(ftlFileName);
				String xmlFileName = outputFile;
				String uploadFileName = "sitemaps/" + xmlFileName;
				String fullXmlFileName = "output" + File.separator + "sitemaps"  + File.separator +  xmlFileName;
				createFile(fullXmlFileName);
				Writer fileWriter = new FileWriter(fullXmlFileName);
				try
				{
					Map<String, Object> root = new HashMap<String, Object>();
					root.put( "domainName", StrapiConstants.DOMAIN_NAME );
				    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
				    root.put( "sitemapLocations", sitemapLocations );
					template.process(root, fileWriter);
				}
				finally
				{
					fileWriter.close();
				}
				dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void generateArticle(Article sitemapArticle, List<StrapiCategory> categoryList, List<Article> relatedArticles, boolean updateFlag) 
	{
		if (sitemapArticle != null)
		{
			try
			{
				Template template = cfg.getTemplate("article.ftl");
				if(template != null)
					{
					String articleFileName = sitemapArticle.getSeoSlug()+"-"+ sitemapArticle.getId() + ".html";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + articleFileName;
					
					long timestamp = sitemapArticle.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(sitemapArticle.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), sitemapArticle.getPrimarySubCategorySeoSlug(), articleFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), articleFileName); 
					
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "article", sitemapArticle );
					    root.put("primaryCategoryList", categoryList);
					    root.put("relatedArticles", relatedArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					
					AppJson articleJson = StrapiUtils.generateJSON(sitemapArticle, StrapiConstants.STRAPI_MODEL_ARTICLE );
					String appUploadFilename = generateAppJson(sitemapArticle.getId(), sitemapArticle.getUrl(), articleJson);
					
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						invalidationUrls.add(appUploadFilename);
					}
					
					
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	public void generateArticleForCMS(Article sitemapArticle, List<StrapiCategory> categoryList, List<Article> relatedArticles) 
	{
		if (sitemapArticle != null)
		{
			try
			{
				Template template = cfg.getTemplate("article.ftl");
				if(template != null)
					{
					String articleFileName = sitemapArticle.getSeoSlug()+"-"+ sitemapArticle.getId() + ".html";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + articleFileName;
					
					long timestamp = sitemapArticle.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(sitemapArticle.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), sitemapArticle.getPrimarySubCategorySeoSlug(), articleFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), articleFileName); 
					
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "article", sitemapArticle );
					    root.put("primaryCategoryList", categoryList);
					    root.put("relatedArticles", relatedArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	public void generateAmpArticle(Article sitemapArticle, List<StrapiCategory> categoryList, List<Article> relatedArticles, boolean updateFlag) 
	{
		if (sitemapArticle != null)
		{
			try
			{
				Template template = cfg.getTemplate("article-amp.ftl");
				if(template != null)
					{
					String articleFileName = sitemapArticle.getSeoSlug()+"-"+ sitemapArticle.getId() + ".html/amp";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + "amp/article-" + sitemapArticle.getId() + ".html";
					
					long timestamp = sitemapArticle.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(sitemapArticle.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), sitemapArticle.getPrimarySubCategorySeoSlug(), articleFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), articleFileName); 
					
					String articleText = sitemapArticle.getArticleText();
					String newArticleText = articleText.replace("iframe", "amp-iframe")
							.replace("width=\"100%\"", "width=\"300\"").replace("allowfullscreen=\"true\"", "")
							.replace("allowfullscreen=\"false\"", "")
							.replace("start=\"0\"", "")
							.replace ("!important", "")
							.replace("target=\"\" rel=\"noopener noreferrer nofollow\"", "");
					sitemapArticle.setArticleText(newArticleText);
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "article", sitemapArticle );
					    root.put("primaryCategoryList", categoryList);
					    root.put("relatedArticles", relatedArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
					}
					
					
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	private static void createFile(String htmlFile)
	{
		File htmlFileName = new File(htmlFile);
		htmlFileName.getParentFile().mkdirs();
		if (!htmlFileName.exists())
			try
			{
				htmlFileName.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	public String generateNewSitemap(List<Article> latestArticles) {
		if(! latestArticles.isEmpty())
		{
			try
			{
				Template template = cfg.getTemplate("news-sitemap.ftl");
				if(template != null)
					{
					String xmlFileName = "news-sitemap.xml"; 
					String uploadFileName = "sitemaps/" + xmlFileName;
					String fullXmlFileName = "output" + File.separator +  "sitemaps" + File.separator +  xmlFileName;
					createFile(fullXmlFileName);
					Writer fileWriter = new FileWriter(fullXmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put("sitemapArticles", latestArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
					return xmlFileName;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public HashMap<String, String> generatePostSitemap(List<Article> sitemapArticles, Date date) {
		if((sitemapArticles != null) && (! sitemapArticles.isEmpty()) && (date != null))
		{
			HashMap<String, String> fileNames = new HashMap<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdf.format(date);

			int year = StrapiUtils.getYearFromDate(date);

			try
			{
				Template template = cfg.getTemplate("post-sitemap.ftl");
				if(template != null)
					{
					String postFolderName = "post-sitemap-"+ year ;
					String xmlFileName = postFolderName + "/"+ "post-sitemap-" + dateStr + ".xml";
					String uploadFileName = "sitemaps" + "/"  +xmlFileName;
					String fullXmlFileName = "output" +"/" +  "sitemaps" + File.separator +  xmlFileName;
					createFile(fullXmlFileName);
					Writer fileWriter = new FileWriter(fullXmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put("sitemapArticles", sitemapArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					fileNames.put("postFolder", postFolderName);
					fileNames.put("xmlFileName", xmlFileName);
					dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
					return fileNames;
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return null;
		
	}

	public void generateCategoryLanding(Category primaryCategory, List<StrapiCategory> categoryList,
			List<Article> latestCategoryArticles, HashMap<SubCategory, List<Article>> dataHashMap) {
		
		if((primaryCategory != null) && (latestCategoryArticles != null) && (!latestCategoryArticles.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("category-landing.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX,primaryCategory.getSeoSlug());

					String invalidationUrl = null;
					invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LANDING_INVALIDATION, primaryCategory.getSeoSlug()) + "*"; 
					invalidationUrls.add(invalidationUrl);
					
					String htmlFileName = "output" + File.separator + primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					Map<String, Object> root = new HashMap<String, Object>();

					try
					{
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("dataHashMap", dataHashMap);
					    root.put("primaryCategory", primaryCategory);
					    root.put("primaryCategoryList", categoryList);
					    root.put("latestCategoryArticles", latestCategoryArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);

					AppJson landingAppJson = StrapiUtils.generateJSON( latestCategoryArticles.subList(0, 10), StrapiConstants.MODEL_AV_CATEGORY_LANDING);
					String jsonlandingPageUrl =  generateAppJson(null, uploadFileName, landingAppJson);
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	
	}

	public String generateGallerySitemap(int publishedYear, List<PhotoGallery> sitemapArticles) {
		if((sitemapArticles != null) && (! sitemapArticles.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("gallery-sitemap.ftl");
				if(template != null)
					{
					String xmlFileName = "gallery-sitemap-" + publishedYear + ".xml";
					String uploadFileName = "sitemaps/" + xmlFileName;
					String fullXmlFileName = "output" + File.separator + "sitemaps"+  File.separator + xmlFileName;
					createFile(fullXmlFileName);
					Writer fileWriter = new FileWriter(fullXmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put("sitemapArticles", sitemapArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
					return xmlFileName;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public String generateVideoSitemap(int publishedYear, int fileCounter, List<Article> videos) 
	{
		if((videos != null) && (! videos.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("video-sitemap.ftl");
				if(template != null)
					{
					String xmlFileName = "video-sitemap-" + publishedYear + "-"+ fileCounter +".xml";
					String uploadFileName = "sitemaps/" + xmlFileName;
					String fullXmlFileName = "output" + File.separator + "sitemaps"+ File.separator + xmlFileName;
					createFile(fullXmlFileName);
					Writer fileWriter = new FileWriter(fullXmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put("videos", videos);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
					return xmlFileName;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public void generateStaticPage(String ftlFileName, String htmlFName,List<StrapiCategory> categoryList) {
		if (categoryList != null && categoryList.size() > 0)
		{
			try
			{
				Template template = cfg.getTemplate(ftlFileName);
				if(template != null)
					{
					String htmlFileName = "output"   + File.separator + htmlFName;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME);
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME);
					    root.put( "primaryCategoryList", categoryList );
  						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(htmlFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void generateTagListing(String tag, String tagUrl, List<StrapiCategory> categoryList,
			List<Article> sitemapArticles) {
		if ((tag != null) && (sitemapArticles != null) && (!sitemapArticles.isEmpty())) {
			String tagUrlWS = tagUrl;
			if(tagUrl.startsWith("/"))
				tagUrlWS = tagUrl.replaceFirst("/", "");
				
			try {
				Template template = cfg.getTemplate("tag-listing.ftl");
				if (template != null) {

					String uploadFileName = null;
					int totalSize = sitemapArticles.size();

					for (int i = 0,	pageNo = 1; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++) {
						if (i == 0) {
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, tagUrlWS);
						} else {
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE,
										tagUrlWS, pageNo);
						}

						int limit = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT;
						List<Article> rangedArticles = null;
						if (limit < totalSize)
							rangedArticles = sitemapArticles.subList(i, limit);
						else
							rangedArticles = sitemapArticles.subList(i, totalSize);

						String htmlFileName = "output" + File.separator + tag + "-" +pageNo	+ "-Listing.html";
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						int mod = totalSize % 10;
						int totalPages;
						if (mod == 0)
							totalPages = totalSize / 10;
						else
							totalPages = totalSize / 10 + 1;
						try {
							Map<String, Object> root = new HashMap<String, Object>();
							root.put("domainName", StrapiConstants.DOMAIN_NAME);
							root.put("mDomainName", StrapiConstants.M_DOMAIN_NAME);
							root.put("resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME);
							root.put("articlesList", rangedArticles);
							root.put("tagUrl", tagUrl);
							root.put("tag", tag);
							root.put("primaryCategoryList", categoryList);
							root.put("pageNo", pageNo);
							root.put("totalPages", totalPages);
							template.process(root, fileWriter);
						}

						finally {
							fileWriter.close();
						}

						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						FileNDirUtils.deleteFile(htmlFileName);
					}
					  invalidationUrls.add(tagUrlWS + "*");
					 /* if(!invalidationUrls.isEmpty()) {
					  dataUploader.invalidateFiles(invalidationUrls); }*/
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void generateCategoryListing(Category category,SubCategory subCategory, List<StrapiCategory> categoryList, List<Article> sitemapArticles, boolean pcListing) {
		if((category != null) && (sitemapArticles != null) && (! sitemapArticles.isEmpty()))
		{
			String invalidationUrl = null;
			try
			{
				Template template = cfg.getTemplate("category-listing.ftl");
				if(template != null)
					{
					
					String uploadFileName  = null;
					int totalSize = sitemapArticles.size();
					
					for (int i = 0, pageNo=1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
					{
						List<Article> rangedArticles = null;
						if(i == 0 && pcListing == false)
						{
								if(subCategory != null)
								{
								invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_INVALIDATION, category.getSeoSlug(), subCategory.getSeoSlug()) + "*"; 
								invalidationUrls.add(invalidationUrl);
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC, category.getSeoSlug(), subCategory.getSeoSlug());
								}
								else
								{
								invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_INVALIDATION, category.getSeoSlug()) + "*"; 
								invalidationUrls.add(invalidationUrl);
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, category.getSeoSlug());
						
								}
						}
						else if (i == 0 && pcListing == true)
						{
							// create primaryCategory/page/1.json for Primary Category Listing page
							/*rangedArticles =  sitemapArticles.subList(0, StrapiConstants.ABN_LISTING_PAGE_LIMIT);
							uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE, category.getSeoSlug(), 1); 
							AppJson listingAppJson = StrapiUtils.generateJSON( rangedArticles, StrapiConstants.MODEL_CATEGORY_LISTING);
							String jsonListingPageUrl =  generateAppJson(null, uploadFileName, listingAppJson);*/
							continue;
						}
						else
						{
							if(subCategory != null)
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_PAGE, category.getSeoSlug(), subCategory.getSeoSlug(), pageNo); 
							else
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE, category.getSeoSlug(), pageNo); 
						}
						
						int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
						
						if(limit < totalSize)
							rangedArticles =  sitemapArticles.subList(i, limit);
						else
							rangedArticles =  sitemapArticles.subList(i, totalSize);
						
						String htmlFileName = "output" + File.separator + category.getSeoSlug() + "-categoryListing.html";
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						int mod = totalSize % 10 ;
						int totalPages;
						if(mod == 0)
							totalPages = totalSize/10;
						else
							totalPages = totalSize/10 + 1;
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
						    root.put("articlesList", rangedArticles);
						    root.put("primarySubCategory", subCategory);
						    root.put("primaryCategory", category);
						    root.put("primaryCategoryList", categoryList);
						    root.put("pageNo", pageNo);
						    root.put("totalPages", totalPages);
							template.process(root, fileWriter);
						}
						
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						FileNDirUtils.deleteFile(htmlFileName);
						AppJson listingAppJson = StrapiUtils.generateJSON( rangedArticles, StrapiConstants.MODEL_AV_CATEGORY_LISTING);
						String jsonListingPageUrl =  generateAppJson(null, uploadFileName, listingAppJson);
					}
					
						//dataUploader.invalidateFiles(invalidationUrls);

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void generatePhotoDetailPageForCMS(PhotoGallery photoGallery, List<StrapiCategory> categoryList) 
	{
		if (photoGallery != null)
		{
			try
			{
				Template template = cfg.getTemplate("photo-detail.ftl");
				if(template != null)
					{
					String photoFileName = photoGallery.getUrl();
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + "photo-" + photoGallery.getId() + ".html";
					
					long timestamp = photoGallery.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(photoFileName.startsWith("/"))
						uploadFileName = photoFileName.replaceFirst("/", ""); 
					
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "photoGallery", photoGallery );
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void generatePhotoDetailPage(PhotoGallery photoGallery, List<StrapiCategory> categoryList, boolean updateFlag) 
	{
		if (photoGallery != null)
		{
			try
			{
				Template template = cfg.getTemplate("photo-detail.ftl");
				if(template != null)
					{
					String photoFileName = photoGallery.getSeoSlug()+"-"+ photoGallery.getId() + ".html";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + photoFileName;
					
					long timestamp = photoGallery.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(photoGallery.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,photoGallery.getPrimaryCategorySeoSlug(), photoGallery.getPrimarySubCategorySeoSlug(), photoFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,photoGallery.getPrimaryCategorySeoSlug(), photoFileName); 
					
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "photoGallery", photoGallery );
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					
					AppJson photoJson = StrapiUtils.generateJSON(photoGallery, StrapiConstants.STRAPI_MODEL_PHOTOGALLERY);
					String appUploadFilename = generateAppJson(photoGallery.getId(), photoGallery.getUrl(), photoJson);
					
					
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						// dataUploader.invalidateFiles(invalidationUrls);
					}
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void generatePhotoDetailAmpPage(PhotoGallery photoGallery, List<StrapiCategory> categoryList, boolean updateFlag) 
	{
		if (photoGallery != null)
		{
			try
			{
				Template template = cfg.getTemplate("photo-detail-amp.ftl");
				if(template != null)
					{
					String photoFileName = photoGallery.getSeoSlug()+"-"+ photoGallery.getId() + ".html/amp";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator +  "amp/photo - " + photoGallery.getId() + ".html";
					
					long timestamp = photoGallery.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(photoGallery.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,photoGallery.getPrimaryCategorySeoSlug(), photoGallery.getPrimarySubCategorySeoSlug(), photoFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,photoGallery.getPrimaryCategorySeoSlug(), photoFileName); 
					
					
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "photoGallery", photoGallery );
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
					}
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void generatePhotoDetailAmpPageForCMS(PhotoGallery photoGallery, List<StrapiCategory> categoryList) 
	{
		if (photoGallery != null)
		{
			try
			{
				Template template = cfg.getTemplate("photo-detail-amp.ftl");
				if(template != null)
					{
					String photoFileName = photoGallery.getUrl() + "/amp";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator +  "amp/photo - " + photoGallery.getId() + ".html";
					
					long timestamp = photoGallery.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(photoFileName.startsWith("/"))
						uploadFileName = photoFileName.replaceFirst("/", "");
				
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put( "photoGallery", photoGallery );
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generatePhotoLanding(Category primaryCategory, List<StrapiCategory> categoryList, List<PhotoGallery> latestPhotos ,
			HashMap<SubCategory, List<PhotoGallery>> dataHashMap) {
		if((primaryCategory != null) && (latestPhotos != null) && (!latestPhotos.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("photo-landing.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX,primaryCategory.getSeoSlug()); 
					String invalidationUrl = null;
					invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LANDING_INVALIDATION, primaryCategory.getSeoSlug()) + "*"; 
					invalidationUrls.add(invalidationUrl);
					
					String htmlFileName = "output" +File.separator +primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put("domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put("resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("latestPhotos", latestPhotos);
					    root.put("dataHashMap", dataHashMap);
					    root.put("primaryCategory", primaryCategory);
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					AppJson landingAppJson = StrapiUtils.generateJSON( latestPhotos.subList(0, 10), StrapiConstants.MODEL_PHOTO_CATEGORY_LANDING);
					String jsonlandingPageUrl =  generateAppJson(null, uploadFileName, landingAppJson);
					
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
	}

	public void generatePhotoListing(Category category, SubCategory subCategory, List<StrapiCategory> categoryList,
			List<PhotoGallery> photos, boolean pcListing) {
		if((category != null) && (photos != null) && (! photos.isEmpty()))
		{
			String invalidationUrl = null;
			try
			{
				Template template = cfg.getTemplate("photo-listing.ftl");
				if(template != null)
					{
					
					String uploadFileName  = null;
					int totalSize = photos.size();
					
					for (int i = 0, pageNo=1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
					{
						if(i == 0 && pcListing == false)
						{
							
								if(subCategory != null)
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_INVALIDATION, category.getSeoSlug(), subCategory.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC, category.getSeoSlug(), subCategory.getSeoSlug()); 
									}
								else
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_INVALIDATION, category.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, category.getSeoSlug());
								}
						}
						else if (i == 0 && pcListing == true)
						{
							continue;
						}
						else
						{
							if(subCategory != null)
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_PAGE, category.getSeoSlug(), subCategory.getSeoSlug(), pageNo); 
							else
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE, category.getSeoSlug(), pageNo); 
						}
						
						int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
						List<PhotoGallery> rangedPhotos = null;
						if(limit < totalSize)
							rangedPhotos =  photos.subList(i, limit);
						else
							rangedPhotos =  photos.subList(i, totalSize);
						
						String htmlFileName = "output" + File.separator + category.getSeoSlug() + "-categoryListing.html";
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						int mod = totalSize % 10 ;
						int totalPages;
						if(mod == 0)
							totalPages = totalSize/10;
						else
							totalPages = totalSize/10 + 1;

						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
						    root.put("photoGalleryList", rangedPhotos);
						    root.put("primarySubCategory", subCategory);
						    root.put("primaryCategory", category);
						    root.put("primaryCategoryList", categoryList);
						    root.put("pageNo", pageNo);
						    root.put("totalPages", totalPages);
							template.process(root, fileWriter);
						}
						
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						FileNDirUtils.deleteFile(htmlFileName);
						AppJson listingAppJson = StrapiUtils.generateJSON( rangedPhotos, StrapiConstants.MODEL_PHOTO_CATEGORY_LISTING);
						String jsonListingPageUrl =  generateAppJson(null, uploadFileName, listingAppJson);
					}
					
					//dataUploader.invalidateFiles(invalidationUrls);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	

	public void generateVideoDetail(Article sitemapArticle, String primaryVideoType, String primaryVideoUrl,
			List<StrapiCategory> categoryList, List<Article> relatedArticles, boolean updateFlag) {
		if (sitemapArticle != null)
		{
			try
			{
				Template template = cfg.getTemplate("video-detail.ftl");
				if(template != null)
					{
					String articleFileName = sitemapArticle.getSeoSlug()+"-"+ sitemapArticle.getId() + ".html";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + articleFileName;
					
					long timestamp = sitemapArticle.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(sitemapArticle.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), sitemapArticle.getPrimarySubCategorySeoSlug(), articleFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), articleFileName); 
					
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
						root.put( "videoGallery", sitemapArticle );
						root.put("primaryVideoType", primaryVideoType);
						root.put("primaryVideoUrl", primaryVideoUrl);
					    root.put("primaryCategoryList", categoryList);
					    root.put("relatedArticles", relatedArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					AppJson articleJson = StrapiUtils.generateJSON(sitemapArticle, StrapiConstants.STRAPI_MODEL_ARTICLE );
					String appUploadFilename = generateAppJson(sitemapArticle.getId(), sitemapArticle.getUrl(), articleJson);
					
					
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
					}
						
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void generateVideoDetailAmp(Article sitemapArticle, String primaryVideoType, String primaryVideoUrl,
			List<StrapiCategory> categoryList, List<Article> relatedArticles, boolean updateFlag) {
		if (sitemapArticle != null)
		{
			try
			{
				Template template = cfg.getTemplate("video-detail-amp.ftl");
				if(template != null)
					{
					String articleFileName = sitemapArticle.getSeoSlug()+"-"+ sitemapArticle.getId() + ".html/amp";
					String htmlFileName = "output" + File.separator + "articles"  + File.separator + "/amp/video-" + sitemapArticle.getId() + ".html";
					
					long timestamp = sitemapArticle.getPublishedAt().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(timestamp);
					int year = cal.get(Calendar.YEAR);
					String uploadFileName = null;
					if(sitemapArticle.getPrimarySubCategorySeoSlug() != null)
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITH_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), sitemapArticle.getPrimarySubCategorySeoSlug(), articleFileName); 
					else
						uploadFileName = String.format(StrapiConstants.S3_ARTICLE_PREFIX_WITHOUT_SC, year,sitemapArticle.getPrimaryCategorySeoSlug(), articleFileName); 
					
					
					String articleText = sitemapArticle.getArticleText();
					String newArticleText = articleText.replace("iframe", "amp-iframe")
							.replace("width=\"100%\"", "width=\"300\"").replace("allowfullscreen=\"true\"", "")
							.replace("allowfullscreen=\"false\"", "")
							.replace("start=\"0\"", "");
					sitemapArticle.setArticleText(newArticleText);
					
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
						root.put( "videoGallery", sitemapArticle );
						root.put("primaryVideoType", primaryVideoType);
						root.put("primaryVideoUrl", primaryVideoUrl);
					    root.put("primaryCategoryList", categoryList);
					    root.put("relatedArticles", relatedArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
					}
					
					
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		
	}


	public void generateVideoLanding(Category primaryCategory, List<StrapiCategory> categoryList,
			List<Article> latestCategoryArticles, HashMap<SubCategory, List<Article>> dataHashMap) {
		if((primaryCategory != null) && (latestCategoryArticles != null) && (!latestCategoryArticles.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("video-landing.ftl");
				String invalidationUrl = null;
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX,primaryCategory.getSeoSlug()); 
					invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LANDING_INVALIDATION, primaryCategory.getSeoSlug()) + "*";
					
					String htmlFileName = "output"  + File.separator + primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					Map<String, Object> root = new HashMap<String, Object>();
					try
					{
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("dataHashMap", dataHashMap);
					    root.put("primaryCategory", primaryCategory);
					    root.put("primaryCategoryList", categoryList);
					    root.put("latestCategoryArticles", latestCategoryArticles);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					
					invalidationUrls.add(invalidationUrl);
					AppJson landingAppJson = StrapiUtils.generateJSON( latestCategoryArticles.subList(0, 10), StrapiConstants.MODEL_AV_CATEGORY_LANDING);
					String jsonlandingPageUrl =  generateAppJson(null, uploadFileName, landingAppJson);
					
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateVideoListing(Category category, SubCategory subCategory, List<StrapiCategory> categoryList,
			List<Article> sitemapArticles, boolean pcListing) {
		if((category != null) && (sitemapArticles != null) && (! sitemapArticles.isEmpty()))
		{
			String invalidationUrl = null;
			try
			{
				Template template = cfg.getTemplate("video-listing.ftl");
				if(template != null)
					{
					
					String uploadFileName  = null;
					
					int totalSize = sitemapArticles.size();
					
					for (int i = 0, pageNo = 1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
					{
						if(i == 0 && pcListing == false)
						{
							if(subCategory != null)
								{
								invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_INVALIDATION, category.getSeoSlug(), subCategory.getSeoSlug()) + "*"; 
								invalidationUrls.add(invalidationUrl);
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC, category.getSeoSlug(), subCategory.getSeoSlug()); 
								}
							else
								{
								invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_INVALIDATION, category.getSeoSlug()) + "*"; 
								invalidationUrls.add(invalidationUrl);
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, category.getSeoSlug()); 
								}
						
						}
						else if (i == 0 && pcListing == true)
						{
							continue;
						}
						else
						{
							if(subCategory != null)
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_PAGE, category.getSeoSlug(), subCategory.getSeoSlug(), pageNo); 
							else
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE, category.getSeoSlug(), pageNo); 
						}
						
						int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
						List<Article> rangedArticles = null;
						if(limit < totalSize)
							rangedArticles =  sitemapArticles.subList(i, limit);
						else
							rangedArticles =  sitemapArticles.subList(i, totalSize);
						
						String htmlFileName = "output" + File.separator + category.getSeoSlug() + "-categoryListing.html";
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						int mod = totalSize % 10 ;
						int totalPages;
						if(mod == 0)
							totalPages = totalSize/10;
						else
							totalPages = totalSize/10 + 1;
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
						    root.put("articlesList", rangedArticles);
						    root.put("primarySubCategory", subCategory);
						    root.put("primaryCategory", category);
						    root.put("primaryCategoryList", categoryList);
						    root.put("totalPages", totalPages);
						    root.put("pageNo", pageNo);
							template.process(root, fileWriter);
						}
						
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						FileNDirUtils.deleteFile(htmlFileName);
						AppJson listingAppJson = StrapiUtils.generateJSON( rangedArticles, StrapiConstants.MODEL_AV_CATEGORY_LISTING);
						String jsonListingPageUrl =  generateAppJson(null, uploadFileName, listingAppJson);
					}
					//dataUploader.invalidateFiles(invalidationUrls);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateHoroscope(Category category, List<StrapiCategory> categoryList,HashMap<String, Horoscope> latestHoroscopes, boolean updateFlag) 
	{
		if(latestHoroscopes != null && !latestHoroscopes.isEmpty())
		{
			try
			{
				Template template = cfg.getTemplate("horoscope.ftl");
				if(template != null)
					{
					
					String uploadFileName  = null;
					
					uploadFileName = "astrology"; 
					String htmlFileName = "output" + File.separator  +"astrology.html";
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("primaryCategory", category);
					    root.put("latestHoroscopes", latestHoroscopes);
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
						
					finally
					{
						fileWriter.close();
					}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);

						if(updateFlag)
						{
							invalidationUrls.add(uploadFileName);
							//dataUploader.invalidateFiles(invalidationUrls);
						}
						
						FileNDirUtils.deleteFile(htmlFileName);
					}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void generateCartoonPage(Category category, List<StrapiCategory> categoryList, List<Cartoon> latestCartoons, boolean updateFlag) {
		try
		{
			Template template = cfg.getTemplate("cartoon.ftl");
			if(template != null)
				{
				
				String uploadFileName  = null;
				
				uploadFileName = "cartoonarchive"; 
				String htmlFileName = "output" + File.separator  +"cartoon.html";
				createFile(htmlFileName);
				Writer fileWriter = new FileWriter(htmlFileName);
				try
				{
					Map<String, Object> root = new HashMap<String, Object>();
				    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
				    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
				    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
				    root.put("primaryCategory", category);
				    root.put("latestCartoons", latestCartoons);
				    root.put("primaryCategoryList", categoryList);
					template.process(root, fileWriter);
				}
					
				finally
				{
					fileWriter.close();
				}
					
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
					}
					
					FileNDirUtils.deleteFile(htmlFileName);
					AppJson latestCartoonsJson = StrapiUtils.generateJSON( latestCartoons, StrapiConstants.MODEL_LATEST_ARTICLE_LISTING);
					String url =  generateAppJson(null, uploadFileName, latestCartoonsJson);
				}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void generateRKPage(Category category, List<StrapiCategory> categoryList, List<Article> latestCategoryArticles, boolean updateFlag) {
		if (categoryList != null && categoryList.size() > 0)
		{
			try
			{
				Template template = cfg.getTemplate("rk.ftl");
				if(template != null)
					{
					String htmlFileName = "output"   + File.separator + "rk.html";
					String uploadFileName = "open-heart";
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("primaryCategory", category);
					    root.put( "primaryCategoryList", categoryList );
					    root.put( "latestCategoryArticles", latestCategoryArticles);
  						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					
					if(updateFlag)
					{
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
					}
						
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public String generateCategorySitemap(List<Category> categories) 
	{
		if((categories != null) && (! categories.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("category-sitemap.ftl");
				if(template != null)
					{
					String xmlFileName = "category-sitemap.xml";
					String uploadFileName = "sitemaps/" + xmlFileName;
					String fullXmlFileName = "output" + File.separator + "sitemaps"+ File.separator + xmlFileName;
					createFile(fullXmlFileName);
					Writer fileWriter = new FileWriter(fullXmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
					    root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					    root.put("categories", categories);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
					return xmlFileName;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public void generateLatestArticleListingPage(List<Article> latestArticles, List<StrapiCategory> categoryList) {
		if((latestArticles != null) && (! latestArticles.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("latest-news.ftl");
				if(template != null)
					{
					
					String uploadFileName  = null;
					
					int totalSize = latestArticles.size();
					
					for (int i = 0, pageNo = 1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
					{
						if(i == 0)
							uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, "latest-news");
						else
						{
							uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE, "latest-news", pageNo); 
						}
						int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
						List<Article> rangedArticles = null;
						if(limit < totalSize)
							rangedArticles =  latestArticles.subList(i, limit);
						else
							rangedArticles =  latestArticles.subList(i, totalSize);
						
						String htmlFileName = "output" + File.separator + "latest-news.html";
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						int mod = totalSize % 10 ;
						int totalPages;
						if(mod == 0)
							totalPages = totalSize/10;
						else
							totalPages = totalSize/10 + 1;
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						    root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
						    root.put("articlesList", rangedArticles);
						    root.put("primaryCategoryList", categoryList);
						    root.put("pageNo", pageNo);
						    root.put("totalPages", totalPages);
							template.process(root, fileWriter);
						}
						
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						FileNDirUtils.deleteFile(htmlFileName);
						AppJson listingAppJson = StrapiUtils.generateJSON( rangedArticles, StrapiConstants.MODEL_LATEST_ARTICLE_LISTING);
						String jsonListingPageUrl =  generateAppJson(null, uploadFileName, listingAppJson);
					
					}
					
					invalidationUrls.add("latest-news*");
					//dataUploader.invalidateFiles(invalidationUrls);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void generateSpeedNewsListingJson(List<Article> articles) {
		if((articles != null) && (! articles.isEmpty()))
		{
			try
			{
					String uploadFileName  = null;
					
					int totalSize = articles.size();
					
					for (int i = 0, pageNo = 1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
					{
						if(i == 0)
							uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, "speed-news");
						else
						{
							uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE, "speed-news", pageNo); 
						}
						int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
						List<Article> rangedArticles = null;
						if(limit < totalSize)
							rangedArticles =  articles.subList(i, limit);
						else
							rangedArticles =  articles.subList(i, totalSize);
						
						AppJson listingAppJson = StrapiUtils.generateJSON( rangedArticles, StrapiConstants.MODEL_LATEST_ARTICLE_LISTING);
						String jsonListingPageUrl =  generateAppJson(null, uploadFileName, listingAppJson);
					}
					invalidationUrls.add("speed-news*");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateLiveTV(Category primaryCategory, List<StrapiCategory> categoryList,
			HashMap<SubCategory, List<Article>> dataHashMap) {
		if((primaryCategory != null) && (dataHashMap != null) && (!dataHashMap.isEmpty()))
		{
			try
			{
				Template template = cfg.getTemplate("livetv.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX, "live-tv"); 
					
					
					String htmlFileName = "output"  + File.separator + primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("dataHashMap", dataHashMap);
					    root.put("primaryCategory", primaryCategory);
					    root.put("primaryCategoryList", categoryList);
						template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateAd(HomePageAd homePageAd) 
	{
		if(homePageAd != null) 
		{
			try
			{
				Template template = cfg.getTemplate("abnads.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX, StrapiConstants.ADS_FOLDER_S3); 
					
					
					String htmlFileName = "output" + File.separator + "homePageAd.html" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("homePageAd", homePageAd);
					    template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void uploadFile(String uploadFileName, String htmlFileName) 
	{
		try 
		{
			dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}

	public void generateMainFeedPage(List<Article> feedArticles, HashMap<String, Category> categoryMap,
			HashMap<String, SubCategory> subCategoryMap) {
		if(feedArticles != null) 
		{
			try
			{
				Template template = cfg.getTemplate("feed.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT, StrapiConstants.FEEDS_FOLDER_S3); 
					
					
					String htmlFileName = "output" + File.separator + "feed.xml" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("feedArticles", feedArticles);
					    root.put("categoryMap", categoryMap);
					    root.put("subCategoryMap", subCategoryMap);
					    template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateCategoryFeedPage(List<Article> feedArticles, Category primaryCategory,
			HashMap<String, Category> categoryMap, HashMap<String, SubCategory> subCategoryMap) {
		if(feedArticles != null) 
		{
			try
			{
				Template template = cfg.getTemplate("feed_category.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L1, StrapiConstants.FEEDS_FOLDER_S3, primaryCategory.getSeoSlug()); 
					
					
					String htmlFileName = "output" + File.separator + "feed_category_" + primaryCategory.getId()+".xml" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("feedArticles", feedArticles);
					    root.put("category", primaryCategory);
					    root.put("categoryMap", categoryMap);
					    root.put("subCategoryMap", subCategoryMap);
					    template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateSubCategoryFeedPage(List<Article> feedArticles, Category primaryCategory,SubCategory primarySubCategory,
			HashMap<String, Category> categoryMap, HashMap<String, SubCategory> subCategoryMap) {
		if(feedArticles != null) 
		{
			try
			{
				Template template = cfg.getTemplate("feed_subcategory.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L2, StrapiConstants.FEEDS_FOLDER_S3, primaryCategory.getSeoSlug() , primarySubCategory.getSeoSlug()); 
					
					
					String htmlFileName = "output" + File.separator + "feed_Sub_category_" + primarySubCategory.getId()+".xml" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("feedArticles", feedArticles);
					    root.put("category", primaryCategory);
					    root.put("subcategory", primarySubCategory);
					    root.put("categoryMap", categoryMap);
					    root.put("subCategoryMap", subCategoryMap);
					    template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generateHomePage(List<Article> latestArticles, List<Article> latestVideos,
			List<PhotoGallery> latestPhotos, List<Object> priorityArticles, List<Article> tagRelatedArticles, List<Article> abnVideosArticles,  List<Cartoon> cartoonList,
			HashMap<String, List<Article>> categoryRelatedArticles, int enableHomepageAd, List<StrapiCategory> categoryList) 
	{
		try
		{
			Template template = cfg.getTemplate("index.ftl");
			
			if(template != null)
				{
				String uploadFileName = "index.html";
				
				String htmlFileName = "output" + File.separator + "home.html";
				createFile(htmlFileName);
				Writer fileWriter = new FileWriter(htmlFileName);
				Map<String, Object> root = new HashMap<String, Object>();
				try
				{
					
					root.put( "domainName", StrapiConstants.DOMAIN_NAME );
					root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
					root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
				    root.put("latestArticles", latestArticles);
				    root.put("latestVideos", latestVideos);
				    root.put("latestPhotos", latestPhotos);
				    root.put("priorityArticles", priorityArticles);
				    root.put("tagRelatedArticles", tagRelatedArticles);
				    root.put("abnVideos", abnVideosArticles);
				    root.put("cartoonList", cartoonList);
				    root.put("dataHashMap", categoryRelatedArticles);
				    root.put("enableHomepageAd", enableHomepageAd);
				    root.put("primaryCategoryList", categoryList);
				    template.process(root, fileWriter);
				}
				finally
				{
					fileWriter.close();
				}
				dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
				List<String> ls = new ArrayList<>();
				ls.add("/index.html");
				dataUploader.invalidateFiles(ls);
				FileNDirUtils.deleteFile(htmlFileName);

				AppJson priorityArticlesJson = StrapiUtils.generateJSON( priorityArticles, StrapiConstants.MODEL_AV_CATEGORY_LISTING);
				String url =  generateAppJson(null, "priority-articles", priorityArticlesJson);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void generatePhotoGalleryCategoryFeedPage(List<PhotoGallery> feedPhotos, Category primaryCategory,
			HashMap<String, Category> categoryMap, HashMap<String, SubCategory> subCategoryMap) {
		if(feedPhotos != null) 
		{
			try
			{
				Template template = cfg.getTemplate("feed_category.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L1, StrapiConstants.FEEDS_FOLDER_S3, primaryCategory.getSeoSlug()); 
					
					
					String htmlFileName = "output" + File.separator + "feed_category_" + primaryCategory.getId()+".xml" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("feedArticles", feedPhotos);
					    root.put("category", primaryCategory);
					    root.put("categoryMap", categoryMap);
					    root.put("subCategoryMap", subCategoryMap);
					    template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generatePhotoGallerySubCategoryFeedPage(List<PhotoGallery> feedPhotos, Category category,
			SubCategory primarySubCategory, HashMap<String, Category> categoryMap,
			HashMap<String, SubCategory> subCategoryMap) {
		if(feedPhotos != null) 
		{
			try
			{
				Template template = cfg.getTemplate("feed_subcategory.ftl");
				
				if(template != null)
					{
					String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L2, StrapiConstants.FEEDS_FOLDER_S3, category.getSeoSlug() , primarySubCategory.getSeoSlug()); 
					
					
					String htmlFileName = "output" + File.separator + "feed_Sub_category_" + primarySubCategory.getId()+".xml" ;
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
					    root.put("feedArticles", feedPhotos);
					    root.put("category", category);
					    root.put("subcategory", primarySubCategory);
					    root.put("categoryMap", categoryMap);
					    root.put("subCategoryMap", subCategoryMap);
					    template.process(root, fileWriter);
					}
					finally
					{
						fileWriter.close();
					}
					dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
					FileNDirUtils.deleteFile(htmlFileName);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void generatePostSitemapXml(String ftlFileName, String outputFileName,
			List<SitemapPostLocation> sitemapPostLocations) {
		if (sitemapPostLocations.size() != 0)
		{
			try
			{
				Template template = cfg.getTemplate(ftlFileName);
				String xmlFileName = outputFileName;
				String uploadFileName = "sitemaps/" + xmlFileName;
				String fullXmlFileName = "output" + File.separator + "sitemaps"  + File.separator +  xmlFileName;
				createFile(fullXmlFileName);
				Writer fileWriter = new FileWriter(fullXmlFileName);
				try
				{
					Map<String, Object> root = new HashMap<String, Object>();
					root.put( "domainName", StrapiConstants.DOMAIN_NAME );
				    root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
				    root.put( "sitemapLocations", sitemapPostLocations );
					template.process(root, fileWriter);
				}
				finally
				{
					fileWriter.close();
				}
				dataUploader.uploadFilekeyName(uploadFileName, fullXmlFileName);
				FileNDirUtils.deleteFile(fullXmlFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	public void generateElectionVoteJson(ElectionVote electionVote, String model) {
		if (electionVote != null) {
			try {
				String uploadFileName = model + ".json";
				String fullFileName = "output" + File.separator + uploadFileName;
				createFile(fullFileName);
				Writer fileWriter = new FileWriter(fullFileName);
				try {
					fileWriter.write(JsonUtils.toString(electionVote));
				} finally {
					fileWriter.close();
				}
				dataUploader.uploadFilekeyName(uploadFileName, fullFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String generateAppJson(Integer id, String url, AppJson appJson) 
	{
		if (appJson != null) {
			try {
				if (url.startsWith("/"))
					url = url.replaceFirst("/", "");
				
				int index = 0;
				String uploadFileName = null;
				
				if(id != null)
					{
					index = url.lastIndexOf("/");
					uploadFileName = url.substring(0, index+1) + id + ".json";
					}
				else
					{
					uploadFileName = url + ".json";
					}

				String fullFileName = "output" + File.separator + uploadFileName;
				createFile(fullFileName);
				Writer fileWriter = new FileWriter(fullFileName);
				try {
					fileWriter.write(JsonUtils.toString(appJson));
				} finally {
					fileWriter.close();
				}
				dataUploader.uploadFilekeyName(uploadFileName, fullFileName);
				FileNDirUtils.deleteFile(fullFileName);
				
				return uploadFileName;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public void generateRedirectionPageOld(Article sitemapArticle, RedirectionUrl redirectionUrl) {
		if (sitemapArticle != null && redirectionUrl != null) {
			try {
				Template template = cfg.getTemplate("redirect.ftl");

				String uploadFileName = redirectionUrl.getUrl();
				String uploadAmpFileName = redirectionUrl.getAmpUrl();
				if (sitemapArticle.getUrl().startsWith("/"))
					uploadFileName = redirectionUrl.getUrl().replaceFirst("/", "");
				if (sitemapArticle.getAmpUrl().startsWith("/"))
					uploadAmpFileName = redirectionUrl.getAmpUrl().replaceFirst("/", "");

				String fullHtmlmlFileName = "output" + File.separator + "articles"  + File.separator + sitemapArticle.getId()+ ".html";
				String fullHtmlmlAmpFileName = "output" + File.separator + "articles"  + File.separator + sitemapArticle.getId()+ "-amp.html";
				createFile(fullHtmlmlFileName);
				createFile(fullHtmlmlAmpFileName);
				Writer fileWriter = new FileWriter(fullHtmlmlFileName);
				Writer fileWriterAmp = new FileWriter(fullHtmlmlAmpFileName);
				try {
					Map<String, Object> root = new HashMap<String, Object>();
					Map<String, Object> rootAmp = new HashMap<String, Object>();
					root.put("redirectToUrl", sitemapArticle.getUrl());
					rootAmp.put("redirectToUrl", sitemapArticle.getAmpUrl());
					template.process(root, fileWriter);
					template.process(rootAmp, fileWriterAmp);
				} finally {
					fileWriter.close();
					fileWriterAmp.close();
				}
				dataUploader.uploadFilekeyName(uploadFileName, fullHtmlmlFileName);
				dataUploader.uploadFilekeyName(uploadAmpFileName, fullHtmlmlAmpFileName);
				FileNDirUtils.deleteFile(fullHtmlmlFileName);
				FileNDirUtils.deleteFile(fullHtmlmlAmpFileName);

				List<String> invalidationUrls = new ArrayList<>();
				invalidationUrls.add(uploadFileName);
				dataUploader.invalidateFiles(invalidationUrls);
				invalidationUrls = new ArrayList<>();
				invalidationUrls.add(uploadAmpFileName);
				dataUploader.invalidateFiles(invalidationUrls);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void generateRedirectionPage(Article sitemapArticle, RedirectionUrl redirectionUrl) {
		if (sitemapArticle != null && redirectionUrl != null) {
			try {
				Template template = cfg.getTemplate("redirect.ftl");

				String uploadFileName = redirectionUrl.getUrl();
				String uploadAmpFileName = redirectionUrl.getAmpUrl();
				if (sitemapArticle.getUrl().startsWith("/"))
					uploadFileName = redirectionUrl.getUrl().replaceFirst("/", "");
				if (sitemapArticle.getAmpUrl().startsWith("/"))
					uploadAmpFileName = redirectionUrl.getAmpUrl().replaceFirst("/", "");

				String fullHtmlmlFileName = "output" + File.separator + "articles"  + File.separator + sitemapArticle.getId()+ ".html";
				String fullHtmlmlAmpFileName = "output" + File.separator + "articles"  + File.separator + sitemapArticle.getId()+ "-amp.html";
				createFile(fullHtmlmlFileName);
				createFile(fullHtmlmlAmpFileName);
				Writer fileWriter = new FileWriter(fullHtmlmlFileName);
				Writer fileWriterAmp = new FileWriter(fullHtmlmlAmpFileName);
				try {
					Map<String, Object> root = new HashMap<String, Object>();
					Map<String, Object> rootAmp = new HashMap<String, Object>();
					root.put("redirectToUrl", sitemapArticle.getUrl());
					rootAmp.put("redirectToUrl", sitemapArticle.getAmpUrl());
					template.process(root, fileWriter);
					template.process(rootAmp, fileWriterAmp);
				} finally {
					fileWriter.close();
					fileWriterAmp.close();
				}
				dataUploader.uploadFilekeyNameWithRedirection(uploadFileName, fullHtmlmlFileName, sitemapArticle.getUrl());
				dataUploader.uploadFilekeyNameWithRedirection(uploadAmpFileName, fullHtmlmlAmpFileName, sitemapArticle.getAmpUrl());
				FileNDirUtils.deleteFile(fullHtmlmlFileName);
				FileNDirUtils.deleteFile(fullHtmlmlAmpFileName);

				invalidationUrls.add(uploadFileName +"*");
				//dataUploader.invalidateFiles(invalidationUrls);

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void generateRedirectionPage(PhotoGallery photoGallery, RedirectionUrl redirectionUrl) {
		if (photoGallery != null && redirectionUrl != null) {
			try {
				Template template = cfg.getTemplate("redirect.ftl");

				String uploadFileName = redirectionUrl.getUrl();
				String uploadAmpFileName = redirectionUrl.getAmpUrl();
				if (photoGallery.getUrl().startsWith("/"))
					uploadFileName = redirectionUrl.getUrl().replaceFirst("/", "");
				if (photoGallery.getAmpUrl().startsWith("/"))
					uploadAmpFileName = redirectionUrl.getAmpUrl().replaceFirst("/", "");

				String fullHtmlmlFileName = "output" + File.separator + "articles"  + File.separator + photoGallery.getId()+ ".html";
				String fullHtmlmlAmpFileName = "output" + File.separator + "articles"  + File.separator + photoGallery.getId()+ "-amp.html";
				createFile(fullHtmlmlFileName);
				createFile(fullHtmlmlAmpFileName);
				Writer fileWriter = new FileWriter(fullHtmlmlFileName);
				Writer fileWriterAmp = new FileWriter(fullHtmlmlAmpFileName);
				try {
					Map<String, Object> root = new HashMap<String, Object>();
					Map<String, Object> rootAmp = new HashMap<String, Object>();
					root.put("redirectToUrl", photoGallery.getUrl());
					rootAmp.put("redirectToUrl", photoGallery.getAmpUrl());
					template.process(root, fileWriter);
					template.process(rootAmp, fileWriterAmp);
				} finally {
					fileWriter.close();
					fileWriterAmp.close();
				}
				dataUploader.uploadFilekeyNameWithRedirection(uploadFileName, fullHtmlmlFileName, photoGallery.getUrl());
				dataUploader.uploadFilekeyNameWithRedirection(uploadAmpFileName, fullHtmlmlAmpFileName, photoGallery.getAmpUrl());
				FileNDirUtils.deleteFile(fullHtmlmlFileName);
				
				invalidationUrls.add(uploadFileName + "*");
				//dataUploader.invalidateFiles(invalidationUrls);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public void generatePriorityArticleListingPage(List<Article> priorityArticles, List<StrapiCategory> categoryList) {
			if((priorityArticles != null) && (! priorityArticles.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("top-news.ftl");
					if(template != null)
						{
						
						String uploadFileName  = null;
						String invalidationFileName = null;
						
						int totalSize = priorityArticles.size();
						
						for (int i = 0, pageNo = 1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
						{
							if(i == 0)
							{
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC, "top-news");
								invalidationFileName = uploadFileName + "*";
							}
							else
							{
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ, "top-news", pageNo); 
							}
							int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
							List<Article> rangedArticles = null;
							if(limit < totalSize)
								rangedArticles =  priorityArticles.subList(i, limit);
							else
								rangedArticles =  priorityArticles.subList(i, totalSize);
							
							String htmlFileName = "output" + File.separator + "top-news.html";
							createFile(htmlFileName);
							Writer fileWriter = new FileWriter(htmlFileName);
							int mod = totalSize % 10 ;
							int totalPages;
							if(mod == 0)
								totalPages = totalSize/10;
							else
								totalPages = totalSize/10 + 1;
							try
							{
								Map<String, Object> root = new HashMap<String, Object>();
								root.put( "domainName", StrapiConstants.DOMAIN_NAME );
								root.put( "mDomainName", StrapiConstants.M_DOMAIN_NAME );
								root.put( "resourceDomain", StrapiConstants.RESOURCE_DOMAIN_NAME );
							    root.put("articlesList", rangedArticles);
							    root.put("primaryCategoryList", categoryList);
							    root.put("pageNo", pageNo);
							    root.put("totalPages", totalPages);
								template.process(root, fileWriter);
							}
							
							finally
							{
								fileWriter.close();
							}
							
							dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
							FileNDirUtils.deleteFile(htmlFileName);
						}
						
						invalidationUrls.add("top-news*");
						//dataUploader.invalidateFiles(invalidationUrls);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		
	}

	public void deleteFileWithUrl(String url) throws IOException {
		System.out.println("Delete article at URL " + url);
		dataUploader.deleteFilekeyName(StrapiConstants.S3_BUCKET_NAME, url);
		invalidationUrls.add(url + "*");
	}

}
