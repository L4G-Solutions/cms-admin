package com.andromeda.cms.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.AppJson;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJLiveBlog;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.CJRedirectionUrl;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.Feed;
import com.andromeda.cms.model.HomePageAd;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.sitemap.model.CJSitemapLocation;
import com.andromeda.cms.sitemap.model.CJSitemapPostLocation;
import com.andromeda.commons.util.FileNDirUtils;
import com.andromeda.commons.util.JsonUtils;
import com.andromeda.commons.util.PropertiesUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

@Service
public class CJDataGeneratorService {
		private static final Logger logger = LoggerFactory.getLogger(DataGeneratorService.class);

		@Autowired
		CJDataUploader dataUploader;
		
		List<String> invalidationUrls = new ArrayList<>();		
		
		private Configuration cfg;
		private DataWriter dataWriter;

		@PostConstruct
		public void init() throws IOException
		{
			configureFreemarker();
			dataWriter = new DataWriter();
			dataUploader = new CJDataUploader();
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
			String templatesDir = prop.getProperty("chitrajyothy.templates.dir");
			System.out.println("Chitrajyothy Template directory " + templatesDir);
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

		public void generateSitemapIndexXml(String ftlFileName, String outputFile, List<CJSitemapLocation> chitrajyothySitemapLocations) 
		{
			if (chitrajyothySitemapLocations.size() != 0)
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
						root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
					    root.put( "sitemapLocations", chitrajyothySitemapLocations );
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
		
		public void generateArticle(CJArticle chitrajyothyArticle, List<StrapiCategory> categoryList, List<CJArticle> relatedArticles, boolean updateFlag) 
		{
			if (chitrajyothyArticle != null)
			{
				try
				{
					Template template = cfg.getTemplate("article.ftl");
					if(template != null)
						{
						String articleFileName = chitrajyothyArticle.getUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + articleFileName;
						
						long timestamp = chitrajyothyArticle.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", ""); 
						else
							uploadFileName = articleFileName ;
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "article", chitrajyothyArticle );
						    root.put("primaryCategoryList", categoryList);
						    root.put("relatedArticles", relatedArticles);
							template.process(root, fileWriter);
						}
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						AppJson articleJson = StrapiUtils.generateJSON(chitrajyothyArticle, StrapiConstants.STRAPI_MODEL_ARTICLE );
						String appUploadFilename = generateAppJson(chitrajyothyArticle.getId(), chitrajyothyArticle.getUrl(), articleJson);
						
						
						if(updateFlag)
						{
							invalidationUrls.add(uploadFileName + "*");
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
		
		public void generateLiveBlog(CJLiveBlog cjLiveBlog, List<StrapiCategory> categoryList, List<CJLiveBlog> relatedArticles, boolean updateFlag) 
		{
			if (cjLiveBlog != null)
			{
				try
				{
					Template template = cfg.getTemplate("article.ftl");
					if(template != null)
						{
						String articleFileName = cjLiveBlog.getUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + articleFileName;
						
						long timestamp = cjLiveBlog.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", ""); 
						else
							uploadFileName = articleFileName ;
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "article", cjLiveBlog );
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
							invalidationUrls.add(uploadFileName + "*");
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
		
		public void generateArticleForCMS(CJArticle chitrajyothyArticle, List<StrapiCategory> categoryList, List<CJArticle> relatedArticles) 
		{
			if (chitrajyothyArticle != null)
			{
				try
				{
					Template template = cfg.getTemplate("article.ftl");
					if(template != null)
						{
						String articleFileName = chitrajyothyArticle.getUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + articleFileName;
						
						long timestamp = chitrajyothyArticle.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", ""); 
						else
							uploadFileName = articleFileName ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "article", chitrajyothyArticle );
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
		
		public void generateAmpLiveBlog(CJLiveBlog cjLiveBlog, List<StrapiCategory> categoryList, List<CJLiveBlog> relatedArticles, boolean updateFlag) 
		{
			if (cjLiveBlog != null)
			{
				try
				{
					Template template = cfg.getTemplate("article-amp.ftl");
					if(template != null)
						{
						String articleFileName = cjLiveBlog.getAmpUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + "amp/article-" + cjLiveBlog.getId() + ".html";
						
						long timestamp = cjLiveBlog.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						
						
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", ""); 
						else
							uploadFileName = articleFileName ;
						
						String articleText = cjLiveBlog.getArticleText();
						String newArticleText = articleText.replace("iframe", "amp-iframe")
								.replace("width=\"100%\"", "width=\"300\"").replace("allowfullscreen=\"true\"", "")
								.replace("allowfullscreen='true'", "")
								.replace("allowfullscreen=\"false\"", "")
								.replace("allowfullscreen='false'", "")
								.replace("start=\"0\"", "");
						cjLiveBlog.setArticleText(newArticleText);
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "article", cjLiveBlog );
						    root.put("primaryCategoryList", categoryList);
						    root.put("relatedArticles", relatedArticles);
							template.process(root, fileWriter);
						}
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						invalidationUrls.add(uploadFileName + "*");
						FileNDirUtils.deleteFile(htmlFileName);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		public void generateAmpArticle(CJArticle chitrajyothyArticle, List<StrapiCategory> categoryList, List<CJArticle> relatedArticles, boolean updateFlag) 
		{
			if (chitrajyothyArticle != null)
			{
				try
				{
					Template template = cfg.getTemplate("article-amp.ftl");
					if(template != null)
						{
						String articleFileName = chitrajyothyArticle.getAmpUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + "amp/article-" + chitrajyothyArticle.getId() + ".html";
						
						long timestamp = chitrajyothyArticle.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						
						
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", ""); 
						else
							uploadFileName = articleFileName ;
						
						String articleText = chitrajyothyArticle.getArticleText();
						String newArticleText = articleText.replace("iframe", "amp-iframe")
								.replace("width=\"100%\"", "width=\"300\"").replace("allowfullscreen=\"true\"", "")
								.replace("allowfullscreen='true'", "")
								.replace("allowfullscreen=\"false\"", "")
								.replace("allowfullscreen='false'", "")
								.replace("start=\"0\"", "");
						chitrajyothyArticle.setArticleText(newArticleText);
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "article", chitrajyothyArticle );
						    root.put("primaryCategoryList", categoryList);
						    root.put("relatedArticles", relatedArticles);
							template.process(root, fileWriter);
						}
						finally
						{
							fileWriter.close();
						}
						
						dataUploader.uploadFilekeyName(uploadFileName, htmlFileName);
						invalidationUrls.add(uploadFileName + "*");
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

		public String generateNewSitemap(List<CJArticle> latestArticles) {
			if(! latestArticles.isEmpty())
			{
				try
				{
					Template template = cfg.getTemplate("news-sitemap.ftl");
					if(template != null)
						{
						String xmlFileName = "news-sitemap.xml"; 
						String uploadFileName = "sitemaps/" + xmlFileName;
						String fullXmlFileName = "chitrajyothy-output" + File.separator +  "sitemaps" + File.separator +  xmlFileName;
						createFile(fullXmlFileName);
						Writer fileWriter = new FileWriter(fullXmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
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

		public HashMap<String, String> generatePostSitemap(List<CJArticle> chitrajyothyArticles, Date date) {
			if((chitrajyothyArticles != null) && (! chitrajyothyArticles.isEmpty()) && (date != null))
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
						String fullXmlFileName = "chitrajyothy-output" +"/" +  "sitemaps" + File.separator +  xmlFileName;
						createFile(fullXmlFileName);
						Writer fileWriter = new FileWriter(fullXmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
						    root.put("sitemapArticles", chitrajyothyArticles);
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

		public void generateCategoryLanding(CJCategory primaryCategory, List<StrapiCategory> categoryList,
				List<CJArticle> latestCategoryArticles, List<CJPhotoGallery> latestPhotos, HashMap<CJSubCategory, List<CJArticle>> dataHashMap) {
			if(primaryCategory != null)
			//if((primaryCategory != null) && (latestCategoryArticles != null) && (!latestCategoryArticles.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("category-landing.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX_CJ,primaryCategory.getSeoSlug()); 
						
						
						String htmlFileName = "chitrajyothy-output" + File.separator + primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
						    root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put("dataHashMap", dataHashMap);
						    root.put("latestPhotos", latestPhotos);
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
						
						invalidationUrls.add(uploadFileName);
						
						FileNDirUtils.deleteFile(htmlFileName);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		
		}

		public String generateGallerySitemap(int publishedYear, List<CJPhotoGallery> chitrajyothyArticles) {
			if((chitrajyothyArticles != null) && (! chitrajyothyArticles.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("gallery-sitemap.ftl");
					if(template != null)
						{
						String xmlFileName = "gallery-sitemap-" + publishedYear + ".xml";
						String uploadFileName = "sitemaps/" + xmlFileName;
						String fullXmlFileName = "chitrajyothy-output" + File.separator + "sitemaps"+  File.separator + xmlFileName;
						createFile(fullXmlFileName);
						Writer fileWriter = new FileWriter(fullXmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
						    root.put("sitemapArticles", chitrajyothyArticles);
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

		public String generateVideoSitemap(int publishedYear, int fileCounter, List<CJArticle> videos) 
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
						String fullXmlFileName = "chitrajyothy-output" + File.separator + "sitemaps"+ File.separator + xmlFileName;
						createFile(fullXmlFileName);
						Writer fileWriter = new FileWriter(fullXmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
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
						String htmlFileName = "chitrajyothy-output"   + File.separator + htmlFName;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME);
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME);
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
				List<CJArticle> chitrajyothyArticles, List<CJPhotoGallery> latestPhotos, List<CJArticle> rkArticles, List<CJArticle> ottArticles) {
			if ((tag != null) && (chitrajyothyArticles != null) && (!chitrajyothyArticles.isEmpty())) {
				String tagUrlWS = tagUrl;
				if(tagUrl.startsWith("/"))
					tagUrlWS = tagUrl.replaceFirst("/", "");
					
				try {
					Template template = cfg.getTemplate("tag-listing.ftl");
					if (template != null) {

						String uploadFileName = null;
						int totalSize = chitrajyothyArticles.size();

						for (int i = 0,	pageNo = 1; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++) {
							if (i == 0) {
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_CJ, tagUrlWS);
							} else {
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ,
											tagUrlWS, pageNo);
							}

							int limit = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT;
							List<CJArticle> rangedArticles = null;
							if (limit < totalSize)
								rangedArticles = chitrajyothyArticles.subList(i, limit);
							else
								rangedArticles = chitrajyothyArticles.subList(i, totalSize);

							String htmlFileName = "chitrajyothy-output" + File.separator + tag + "-" +pageNo	+ "-Listing.html";
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
								root.put("domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME);
								root.put("mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME);
								root.put("resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME);
								root.put("articlesList", rangedArticles);
								root.put("latestPhotos", latestPhotos);
							    root.put("rkNewsList", rkArticles);
							    root.put("ottArticles", ottArticles);
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
						  
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void generateCategoryListing(CJCategory category,CJSubCategory subCategory, List<StrapiCategory> categoryList, List<CJArticle> chitrajyothyArticles, List<CJPhotoGallery> latestPhotos, List<CJArticle> rkArticles, List<CJArticle> ottArticles) {
			if((category != null) && (chitrajyothyArticles != null) && (! chitrajyothyArticles.isEmpty()))
			{
				String invalidationUrl = null;				try
				{
					Template template = cfg.getTemplate("category-listing.ftl");
					if(template != null)
						{
						
						String uploadFileName  = null;
						int totalSize = chitrajyothyArticles.size();
						
						for (int i = 0, pageNo=1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
						{
							if(i == 0)
							{
								if(subCategory != null)
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_INVALIDATION, category.getSeoSlug(), subCategory.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_CJ, category.getSeoSlug(), subCategory.getSeoSlug()); 
									}
								else
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_INVALIDATION, category.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_CJ, category.getSeoSlug()); 
									}
							}
							else
							{
								if(subCategory != null)
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_PAGE_CJ, category.getSeoSlug(), subCategory.getSeoSlug(), pageNo); 
								else
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ, category.getSeoSlug(), pageNo); 
							}
							
							int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
							List<CJArticle> rangedArticles = null;
							if(limit < totalSize)
								rangedArticles =  chitrajyothyArticles.subList(i, limit);
							else
								rangedArticles =  chitrajyothyArticles.subList(i, totalSize);
							
							String htmlFileName = "chitrajyothy-output" + File.separator + category.getSeoSlug() + "-categoryListing.html";
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
							    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							    root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
							    root.put("articlesList", rangedArticles);
							    root.put("latestPhotos", latestPhotos);
							    root.put("rkNewsList", rkArticles);
							    root.put("ottArticles", ottArticles);
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
							
						}
						
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		public void generatePhotoDetailPageForCMS(CJPhotoGallery chitrajyothyPhotoGallery, List<StrapiCategory> categoryList) 
		{
			if (chitrajyothyPhotoGallery != null)
			{
				try
				{
					Template template = cfg.getTemplate("photo-detail.ftl");
					if(template != null)
						{
						String photoFileName = chitrajyothyPhotoGallery.getUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + "photo-" + chitrajyothyPhotoGallery.getId() + ".html";
						
						long timestamp = chitrajyothyPhotoGallery.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						if(photoFileName.startsWith("/"))
							uploadFileName = photoFileName.replaceFirst("/", ""); 
						else
							uploadFileName = photoFileName ;
						
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "photoGallery", chitrajyothyPhotoGallery );
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

		public void generatePhotoDetailPage(CJPhotoGallery chitrajyothyPhotoGallery, List<StrapiCategory> categoryList, boolean updateFlag) 
		{
			if (chitrajyothyPhotoGallery != null)
			{
				try
				{
					Template template = cfg.getTemplate("photo-detail.ftl");
					if(template != null)
						{
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + "photo-" + chitrajyothyPhotoGallery.getId() + ".html";

						long timestamp = chitrajyothyPhotoGallery.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						
						String uploadFileName = null;
						String photoFileName = chitrajyothyPhotoGallery.getUrl();
						if(photoFileName.startsWith("/"))
							uploadFileName = photoFileName.replaceFirst("/", ""); 
						else
							uploadFileName = photoFileName ;
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "photoGallery", chitrajyothyPhotoGallery );
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
							invalidationUrls.add(uploadFileName + "*");
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
		
		public void generatePhotoDetailAmpPage(CJPhotoGallery chitrajyothyPhotoGallery, List<StrapiCategory> categoryList, boolean updateFlag) 
		{
			if (chitrajyothyPhotoGallery != null)
			{
				try
				{
					Template template = cfg.getTemplate("photo-detail-amp.ftl");
					if(template != null)
						{
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator +  "amp/photo - " + chitrajyothyPhotoGallery.getId() + ".html";
						
						long timestamp = chitrajyothyPhotoGallery.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;

						String photoFileName = chitrajyothyPhotoGallery.getAmpUrl();
						if(photoFileName.startsWith("/"))
							uploadFileName = photoFileName.replaceFirst("/", ""); 
						else
							uploadFileName = photoFileName ;
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "photoGallery", chitrajyothyPhotoGallery );
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
		
		public void generatePhotoDetailAmpPageForCMS(CJPhotoGallery chitrajyothyPhotoGallery, List<StrapiCategory> categoryList) 
		{
			if (chitrajyothyPhotoGallery != null)
			{
				try
				{
					Template template = cfg.getTemplate("photo-detail-amp.ftl");
					if(template != null)
						{
						String photoFileName = chitrajyothyPhotoGallery.getAmpUrl();
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator +  "amp/photo - " + chitrajyothyPhotoGallery.getId() + ".html";
						
						long timestamp = chitrajyothyPhotoGallery.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						if(photoFileName.startsWith("/"))
							uploadFileName = photoFileName.replaceFirst("/", "");
						else
							uploadFileName = photoFileName ;
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "photoGallery", chitrajyothyPhotoGallery );
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

		public void generatePhotoLanding(CJCategory primaryCategory, List<StrapiCategory> categoryList, List<CJPhotoGallery> latestPhotos ,
				HashMap<CJSubCategory, List<CJPhotoGallery>> dataHashMap) {
			if((primaryCategory != null) && (latestPhotos != null) && (!latestPhotos.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("photo-landing.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX_CJ,primaryCategory.getSeoSlug()); 
						
						String htmlFileName = "chitrajyothy-output" +File.separator +primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put("domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
						    root.put("resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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
						
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
						
						FileNDirUtils.deleteFile(htmlFileName);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			
		}

		public void generatePhotoListing(CJCategory category, CJSubCategory subCategory, List<StrapiCategory> categoryList,
				List<CJPhotoGallery> photos) {
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
						
						for (int i = 0, pageNo=1 ; i < totalSize; i = i + StrapiConstants.CHITRAJYOTHY_PHOTO_LISTING_PAGE_LIMIT, pageNo++)
						{
							if(i == 0)
							{
								if(subCategory != null)
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_CJ, category.getSeoSlug(), subCategory.getSeoSlug()); 
								else
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_CJ, category.getSeoSlug()); 
							}
							else
							{
								if(subCategory != null)
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_INVALIDATION, category.getSeoSlug(), subCategory.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_PAGE_CJ, category.getSeoSlug(), subCategory.getSeoSlug(), pageNo); 
									}
								else
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_INVALIDATION, category.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ, category.getSeoSlug(), pageNo); 
									}
							}
							
							int limit = i+ StrapiConstants.CHITRAJYOTHY_PHOTO_LISTING_PAGE_LIMIT;
							List<CJPhotoGallery> rangedPhotos = null;
							if(limit < totalSize)
								rangedPhotos =  photos.subList(i, limit);
							else
								rangedPhotos =  photos.subList(i, totalSize);
							
							String htmlFileName = "chitrajyothy-output" + File.separator + category.getSeoSlug() + "-categoryListing.html";
							createFile(htmlFileName);
							Writer fileWriter = new FileWriter(htmlFileName);
							int mod = totalSize % StrapiConstants.CHITRAJYOTHY_PHOTO_LISTING_PAGE_LIMIT ;
							int totalPages;
							if(mod == 0)
								totalPages = totalSize/StrapiConstants.CHITRAJYOTHY_PHOTO_LISTING_PAGE_LIMIT;
							else
								totalPages = totalSize/StrapiConstants.CHITRAJYOTHY_PHOTO_LISTING_PAGE_LIMIT + 1;

							try
							{
								Map<String, Object> root = new HashMap<String, Object>();
							    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							    root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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
						}
						
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}

		

		public void generateVideoDetail(CJArticle chitrajyothyArticle, String primaryVideoType, String primaryVideoUrl,
				List<StrapiCategory> categoryList, List<CJArticle> relatedArticles, boolean updateFlag) {
			if (chitrajyothyArticle != null)
			{
				try
				{
					Template template = cfg.getTemplate("video-detail.ftl");
					if(template != null)
						{
						//String articleFileName = chitrajyothyArticle.getSeoSlug()+"-"+ chitrajyothyArticle.getId() + ".html";
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator +  chitrajyothyArticle.getId() + ".html";

						long timestamp = chitrajyothyArticle.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						String articleFileName = chitrajyothyArticle.getUrl();
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", "");
						else
							uploadFileName = articleFileName ;
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "videoGallery", chitrajyothyArticle );
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
							invalidationUrls.add(uploadFileName + "*");
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
		
		public void generateVideoDetailAmp(CJArticle chitrajyothyArticle, String primaryVideoType, String primaryVideoUrl,
				List<StrapiCategory> categoryList, List<CJArticle> relatedArticles, boolean updateFlag) {
			if (chitrajyothyArticle != null)
			{
				try
				{
					Template template = cfg.getTemplate("video-detail-amp.ftl");
					if(template != null)
						{
						String htmlFileName = "chitrajyothy-output" + File.separator + "articles"  + File.separator + "/amp/video-" + chitrajyothyArticle.getId() + ".html";
						
						long timestamp = chitrajyothyArticle.getPublishedAt().getTime();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timestamp);
						int year = cal.get(Calendar.YEAR);
						String uploadFileName = null;
						String articleFileName = chitrajyothyArticle.getAmpUrl();
						if(articleFileName.startsWith("/"))
							uploadFileName = articleFileName.replaceFirst("/", "");
						else
							uploadFileName = articleFileName ;
						
						String articleText = chitrajyothyArticle.getArticleText();
						String newArticleText = articleText.replace("iframe", "amp-iframe")
								.replace("width=\"100%\"", "width=\"300\"").replace("allowfullscreen=\"true\"", "")
								.replace("allowfullscreen=\"false\"", "")
								.replace("start=\"0\"", "");
						chitrajyothyArticle.setArticleText(newArticleText);
						
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "videoGallery", chitrajyothyArticle );
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


		public void generateVideoLanding(CJCategory primaryCategory, List<StrapiCategory> categoryList,
				List<CJArticle> latestCategoryArticles, List<CJPhotoGallery> latestPhotos, HashMap<CJSubCategory, List<CJArticle>> dataHashMap) {
			if((primaryCategory != null) && (latestCategoryArticles != null) && (!latestCategoryArticles.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("video-landing.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX_CJ, primaryCategory.getSeoSlug()); 
						
						
						String htmlFileName = "chitrajyothy-output"  + File.separator + primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
						    root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
						    root.put("dataHashMap", dataHashMap);
						    root.put("latestPhotos", latestPhotos);
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
						
						invalidationUrls.add(uploadFileName);
						//dataUploader.invalidateFiles(invalidationUrls);
						
						FileNDirUtils.deleteFile(htmlFileName);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}

		public void generateVideoListing(CJCategory category, CJSubCategory subCategory, List<StrapiCategory> categoryList,
				List<CJArticle> chitrajyothyArticles, List<CJPhotoGallery> latestPhotos, List<CJArticle> rkArticles, List<CJArticle> ottArticles) {
			if((category != null) && (chitrajyothyArticles != null) && (! chitrajyothyArticles.isEmpty()))
			{
				String invalidationUrl = null;
				try
				{
					Template template = cfg.getTemplate("video-listing.ftl");
					if(template != null)
						{
						
						String uploadFileName  = null;
						
						int totalSize = chitrajyothyArticles.size();
						
						for (int i = 0, pageNo = 1 ; i < totalSize; i = i + StrapiConstants.ABN_LISTING_PAGE_LIMIT, pageNo++)
						{
							if(i == 0)
							{
								if(subCategory != null)
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_INVALIDATION, category.getSeoSlug(), subCategory.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_CJ, category.getSeoSlug(), subCategory.getSeoSlug()); 
									}
								else
									{
									invalidationUrl = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_INVALIDATION, category.getSeoSlug()) + "*"; 
									invalidationUrls.add(invalidationUrl);
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_CJ, category.getSeoSlug()); 
									}
							}
							else
							{
								if(subCategory != null)
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITH_SC_PAGE_CJ, category.getSeoSlug(), subCategory.getSeoSlug(), pageNo); 
								else
									uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ, category.getSeoSlug(), pageNo); 
							}
							
							int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
							List<CJArticle> rangedArticles = null;
							if(limit < totalSize)
								rangedArticles =  chitrajyothyArticles.subList(i, limit);
							else
								rangedArticles =  chitrajyothyArticles.subList(i, totalSize);
							
							String htmlFileName = "chitrajyothy-output" + File.separator + category.getSeoSlug() + "-categoryListing.html";
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
							    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							    root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
							    root.put("articlesList", rangedArticles);
							    root.put("latestPhotos", latestPhotos);
							    root.put("rkNewsList", rkArticles);
							    root.put("ottArticles", ottArticles);
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
						}
						
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		

		public String generateCategorySitemap(List<CJCategory> categories) 
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
						String fullXmlFileName = "chitrajyothy-output" + File.separator + "sitemaps"+ File.separator + xmlFileName;
						createFile(fullXmlFileName);
						Writer fileWriter = new FileWriter(fullXmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
						    root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
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
		
		
		
		public void generatePriorityArticleListingPage(List<CJArticle> priorityArticles, List<CJPhotoGallery> latestPhotos, List<CJArticle> rkArticles, List<CJArticle> ottArticles, List<StrapiCategory> categoryList) {
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
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_CJ, "top-news");
								invalidationFileName = uploadFileName + "*";
								invalidationUrls.add(invalidationFileName);
							}
							else
							{
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ, "top-news", pageNo); 
							}
							int limit = i+ StrapiConstants.ABN_LISTING_PAGE_LIMIT;
							List<CJArticle> rangedArticles = null;
							if(limit < totalSize)
								rangedArticles =  priorityArticles.subList(i, limit);
							else
								rangedArticles =  priorityArticles.subList(i, totalSize);
							
							String htmlFileName = "chitrajyothy-output" + File.separator + "top-news.html";
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
								root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
								root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
								root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
							    root.put("articlesList", rangedArticles);
								root.put("latestPhotos", latestPhotos);
							    root.put("rkNewsList", rkArticles);
							    root.put("ottArticles", ottArticles);
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
						
						//dataUploader.invalidateFiles(invalidationUrls);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}

		public void generateLatestArticleListingPage(List<CJArticle> latestArticles, List<CJPhotoGallery> latestPhotos, List<CJArticle> rkArticles, List<CJArticle> ottArticles, List<StrapiCategory> categoryList) {
			if((latestArticles != null) && (! latestArticles.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("latest-news.ftl");
					if(template != null)
						{
						
						String uploadFileName  = null;
						
						int totalSize = latestArticles.size();
						
						for (int i = 0, pageNo = 1 ; i < totalSize; i = i + StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_PAGE_LIMIT, pageNo++)
						{
							if(i == 0)
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_CJ, "latest-news");
							else
							{
								uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LISTING_WITHOUT_SC_PAGE_CJ, "latest-news", pageNo); 
							}
							int limit = i+ StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_PAGE_LIMIT;
							List<CJArticle> rangedArticles = null;
							if(limit < totalSize)
								rangedArticles =  latestArticles.subList(i, limit);
							else
								rangedArticles =  latestArticles.subList(i, totalSize);
							
							String htmlFileName = "chitrajyothy-output" + File.separator + "latest-news.html";
							createFile(htmlFileName);
							Writer fileWriter = new FileWriter(htmlFileName);
							int mod = totalSize % StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_PAGE_LIMIT ;
							int totalPages;
							if(mod == 0)
								totalPages = totalSize/StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_PAGE_LIMIT;
							else
								totalPages = totalSize/StrapiConstants.CHITRAJYOTHY_LATEST_LISTING_PAGE_LIMIT + 1;
							try
							{
								Map<String, Object> root = new HashMap<String, Object>();
								 root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
								    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
								    root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
							    root.put("articlesList", rangedArticles);
								root.put("latestPhotos", latestPhotos);
							    root.put("rkNewsList", rkArticles);
							    root.put("ottArticles", ottArticles);
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

							AppJson latestCJArticlesJson = StrapiUtils.generateJSON( rangedArticles, StrapiConstants.MODEL_LATEST_ARTICLE_LISTING);
							String url =  generateAppJson(null, uploadFileName, latestCJArticlesJson);
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

		public void generateLiveTV(CJCategory primaryCategory, List<StrapiCategory> categoryList,
				HashMap<CJSubCategory, List<CJArticle>> dataHashMap) {
			if((primaryCategory != null) && (dataHashMap != null) && (!dataHashMap.isEmpty()))
			{
				try
				{
					Template template = cfg.getTemplate("livetv.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX_CJ, "live-tv"); 
						
						
						String htmlFileName = "chitrajyothy-output" + primaryCategory.getSeoSlug() + "-categoryLanding.html" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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
						String uploadFileName = String.format(StrapiConstants.S3_CATEGORY_LANDING_PREFIX_CJ, StrapiConstants.ADS_FOLDER_S3); 
						
						
						String htmlFileName = "chitrajyothy-output" + File.separator + "homePageAd.html" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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

		public void generateMainFeedPage(List<CJArticle> feedArticles, HashMap<String, CJCategory> categoryMap,
				HashMap<String, CJSubCategory> subCategoryMap) {
			if(feedArticles != null) 
			{
				try
				{
					Template template = cfg.getTemplate("feed.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT, StrapiConstants.FEEDS_FOLDER_S3); 
						
						
						String htmlFileName = "chitrajyothy-output" + File.separator + "feed.xml" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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

		public void generateCategoryFeedPage(List<CJArticle> feedArticles, CJCategory primaryCategory,
				HashMap<String, CJCategory> categoryMap, HashMap<String, CJSubCategory> subCategoryMap) {
			if(feedArticles != null) 
			{
				try
				{
					Template template = cfg.getTemplate("feed_category.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L1, StrapiConstants.FEEDS_FOLDER_S3, primaryCategory.getSeoSlug()); 
						
						
						String htmlFileName = "chitrajyothy-output" + File.separator + "feed_category_" + primaryCategory.getId()+".xml" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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

		public void generateSubCategoryFeedPage(List<CJArticle> feedArticles, CJCategory primaryCategory,CJSubCategory primarySubCategory,
				HashMap<String, CJCategory> categoryMap, HashMap<String, CJSubCategory> subCategoryMap) {
			if(feedArticles != null) 
			{
				try
				{
					Template template = cfg.getTemplate("feed_subcategory.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L2, StrapiConstants.FEEDS_FOLDER_S3, primaryCategory.getSeoSlug() , primarySubCategory.getSeoSlug()); 
						
						
						String htmlFileName = "output" + File.separator + "feed_sub_category_" + primarySubCategory.getId()+".xml" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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

		public void generateHomePage(List<CJArticle> latestArticles, List<CJArticle> latestVideos,
				List<CJPhotoGallery> latestPhotos, List<CJArticle> rankingArticles, List<CJArticle> trailerCjArticles, HashMap<String, 
				List<CJArticle>> categoryRelatedArticles, int enableHomepageAd, List<StrapiCategory> categoryList) 
		{
			try
			{
				Template template = cfg.getTemplate("index.ftl");
				
				if(template != null)
					{
					String uploadFileName = "index.html";
					
					String htmlFileName = "chitrajyothy-output" + File.separator + "home.html";
					createFile(htmlFileName);
					Writer fileWriter = new FileWriter(htmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
						root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
						root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
					    root.put("latestArticles", latestArticles);
					    root.put("latestVideos", latestVideos);
					    root.put("latestPhotos", latestPhotos);
					    root.put("trailers", trailerCjArticles);
					    root.put("priorityArticles", rankingArticles);
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
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}

		public void generatePhotoGalleryCategoryFeedPage(List<CJPhotoGallery> feedPhotos, CJCategory primaryCategory,
				HashMap<String, CJCategory> categoryMap, HashMap<String, CJSubCategory> subCategoryMap) {
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
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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

		public void generatePhotoGallerySubCategoryFeedPage(List<CJPhotoGallery> feedPhotos, CJCategory category,
				CJSubCategory primarySubCategory, HashMap<String, CJCategory> categoryMap,
				HashMap<String, CJSubCategory> subCategoryMap) {
			if(feedPhotos != null) 
			{
				try
				{
					Template template = cfg.getTemplate("feed_subcategory.ftl");
					
					if(template != null)
						{
						String uploadFileName = String.format(StrapiConstants.FEEDS_PAGE_FORMAT_L2, StrapiConstants.FEEDS_FOLDER_S3, category.getSeoSlug() , primarySubCategory.getSeoSlug()); 
						
						
						String htmlFileName = "chitrajyothy-output" + File.separator + "feed_Sub_category_" + primarySubCategory.getId()+".xml" ;
						createFile(htmlFileName);
						Writer fileWriter = new FileWriter(htmlFileName);
						try
						{
							Map<String, Object> root = new HashMap<String, Object>();
							root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
							root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
							root.put( "resourceDomain", StrapiConstants.RESOURCE_CHITRAJYOTHY_DOMAIN_NAME );
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
				List<CJSitemapPostLocation> chitrajyothySitemapPostLocations) {
			if (chitrajyothySitemapPostLocations.size() != 0)
			{
				try
				{
					Template template = cfg.getTemplate(ftlFileName);
					String xmlFileName = outputFileName;
					String uploadFileName = "sitemaps/" + xmlFileName;
					String fullXmlFileName = "chitrajyothy-output" + File.separator + "sitemaps"  + File.separator +  xmlFileName;
					createFile(fullXmlFileName);
					Writer fileWriter = new FileWriter(fullXmlFileName);
					try
					{
						Map<String, Object> root = new HashMap<String, Object>();
						root.put( "domainName", StrapiConstants.CHITRAJYOTHY_DOMAIN_NAME );
					    root.put( "mDomainName", StrapiConstants.CHITRAJYOTHY_M_DOMAIN_NAME );
					    root.put( "sitemapLocations", chitrajyothySitemapPostLocations );
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
		
		
		public void generateLiveBlogRedirectionPage(CJLiveBlog cjLiveBlog, CJRedirectionUrl redirectionUrl) {
			if (cjLiveBlog != null && redirectionUrl != null) {
				try {
					Template template = cfg.getTemplate("redirect.ftl");

					String uploadFileName = redirectionUrl.getUrl();
					String uploadAmpFileName = redirectionUrl.getAmpUrl();
					if (cjLiveBlog.getUrl().startsWith("/"))
						uploadFileName = redirectionUrl.getUrl().replaceFirst("/", "");
					if (cjLiveBlog.getAmpUrl().startsWith("/"))
						uploadAmpFileName = redirectionUrl.getAmpUrl().replaceFirst("/", "");

					String fullHtmlmlFileName = "output" + File.separator + "articles"  + File.separator + cjLiveBlog.getId()+ ".html";
					String fullHtmlmlAmpFileName = "output" + File.separator + "articles"  + File.separator + cjLiveBlog.getId()+ "-amp.html";
					createFile(fullHtmlmlFileName);
					createFile(fullHtmlmlAmpFileName);
					Writer fileWriter = new FileWriter(fullHtmlmlFileName);
					Writer fileWriterAmp = new FileWriter(fullHtmlmlAmpFileName);
					try {
						Map<String, Object> root = new HashMap<String, Object>();
						Map<String, Object> rootAmp = new HashMap<String, Object>();
						root.put("redirectToUrl", cjLiveBlog.getUrl());
						rootAmp.put("redirectToUrl", cjLiveBlog.getAmpUrl());
						template.process(root, fileWriter);
						template.process(rootAmp, fileWriterAmp);
					} finally {
						fileWriter.close();
						fileWriterAmp.close();
					}
					dataUploader.uploadFilekeyNameWithRedirection(uploadFileName, fullHtmlmlFileName, cjLiveBlog.getUrl());
					dataUploader.uploadFilekeyNameWithRedirection(uploadAmpFileName, fullHtmlmlAmpFileName, cjLiveBlog.getAmpUrl());
					FileNDirUtils.deleteFile(fullHtmlmlFileName);
					FileNDirUtils.deleteFile(fullHtmlmlAmpFileName);

					List<String> invalidationUrls = new ArrayList<>();
					invalidationUrls.add(uploadFileName +"*");
					dataUploader.invalidateFiles(invalidationUrls);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}



		
		public void generateRedirectionPage(CJArticle chitrajyothyArticle, CJRedirectionUrl redirectionUrl) {
			if (chitrajyothyArticle != null && redirectionUrl != null) {
				try {
					Template template = cfg.getTemplate("redirect.ftl");

					String uploadFileName = redirectionUrl.getUrl();
					String uploadAmpFileName = redirectionUrl.getAmpUrl();
					if (chitrajyothyArticle.getUrl().startsWith("/"))
						uploadFileName = redirectionUrl.getUrl().replaceFirst("/", "");
					if (chitrajyothyArticle.getAmpUrl().startsWith("/"))
						uploadAmpFileName = redirectionUrl.getAmpUrl().replaceFirst("/", "");

					String fullHtmlmlFileName = "output" + File.separator + "articles"  + File.separator + chitrajyothyArticle.getId()+ ".html";
					String fullHtmlmlAmpFileName = "output" + File.separator + "articles"  + File.separator + chitrajyothyArticle.getId()+ "-amp.html";
					createFile(fullHtmlmlFileName);
					createFile(fullHtmlmlAmpFileName);
					Writer fileWriter = new FileWriter(fullHtmlmlFileName);
					Writer fileWriterAmp = new FileWriter(fullHtmlmlAmpFileName);
					try {
						Map<String, Object> root = new HashMap<String, Object>();
						Map<String, Object> rootAmp = new HashMap<String, Object>();
						root.put("redirectToUrl", chitrajyothyArticle.getUrl());
						rootAmp.put("redirectToUrl", chitrajyothyArticle.getAmpUrl());
						template.process(root, fileWriter);
						template.process(rootAmp, fileWriterAmp);
					} finally {
						fileWriter.close();
						fileWriterAmp.close();
					}
					dataUploader.uploadFilekeyNameWithRedirection(uploadFileName, fullHtmlmlFileName, chitrajyothyArticle.getUrl());
					dataUploader.uploadFilekeyNameWithRedirection(uploadAmpFileName, fullHtmlmlAmpFileName, chitrajyothyArticle.getAmpUrl());
					FileNDirUtils.deleteFile(fullHtmlmlFileName);
					FileNDirUtils.deleteFile(fullHtmlmlAmpFileName);

					List<String> invalidationUrls = new ArrayList<>();
					invalidationUrls.add(uploadFileName +"*");
					dataUploader.invalidateFiles(invalidationUrls);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		public void generateRedirectionPage(CJPhotoGallery chitrajyothyPhotoGallery, CJRedirectionUrl redirectionUrl) {
			if (chitrajyothyPhotoGallery != null && redirectionUrl != null) {
				try {
					Template template = cfg.getTemplate("redirect.ftl");

					String uploadFileName = redirectionUrl.getUrl();
					String uploadAmpFileName = redirectionUrl.getAmpUrl();
					if (chitrajyothyPhotoGallery.getUrl().startsWith("/"))
						uploadFileName = redirectionUrl.getUrl().replaceFirst("/", "");
					if (chitrajyothyPhotoGallery.getAmpUrl().startsWith("/"))
						uploadAmpFileName = redirectionUrl.getAmpUrl().replaceFirst("/", "");

					String fullHtmlmlFileName = "output" + File.separator + "articles"  + File.separator + chitrajyothyPhotoGallery.getId()+ ".html";
					String fullHtmlmlAmpFileName = "output" + File.separator + "articles"  + File.separator + chitrajyothyPhotoGallery.getId()+ "-amp.html";
					createFile(fullHtmlmlFileName);
					createFile(fullHtmlmlAmpFileName);
					Writer fileWriter = new FileWriter(fullHtmlmlFileName);
					Writer fileWriterAmp = new FileWriter(fullHtmlmlAmpFileName);
					try {
						Map<String, Object> root = new HashMap<String, Object>();
						Map<String, Object> rootAmp = new HashMap<String, Object>();
						root.put("redirectToUrl", chitrajyothyPhotoGallery.getUrl());
						rootAmp.put("redirectToUrl", chitrajyothyPhotoGallery.getAmpUrl());
						template.process(root, fileWriter);
						template.process(rootAmp, fileWriterAmp);
					} finally {
						fileWriter.close();
						fileWriterAmp.close();
					}
					dataUploader.uploadFilekeyNameWithRedirection(uploadFileName, fullHtmlmlFileName, chitrajyothyPhotoGallery.getUrl());
					dataUploader.uploadFilekeyNameWithRedirection(uploadAmpFileName, fullHtmlmlAmpFileName, chitrajyothyPhotoGallery.getAmpUrl());
					FileNDirUtils.deleteFile(fullHtmlmlFileName);
					invalidationUrls.add(uploadFileName + "*");
					dataUploader.invalidateFiles(invalidationUrls);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}

		public void deleteFileWithUrl(String url) throws IOException {
		System.out.println("Delete article at URL " + url);
		dataUploader.deleteFilekeyName(StrapiConstants.CHITRAJYOTHY_S3_BUCKET_NAME, url);
		invalidationUrls.add(url + "*");
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


}
