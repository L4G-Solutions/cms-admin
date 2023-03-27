package com.andromeda.cms.sitemap.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.dao.ValueCmsProxyRepository;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.PhotoGalleryService;
import com.andromeda.cms.sitemap.dao.SitemapDateDao;
import com.andromeda.cms.sitemap.dao.SitemapLocationDao;
import com.andromeda.cms.sitemap.dao.SitemapPostLocationDao;
import com.andromeda.cms.sitemap.model.SitemapDate;
import com.andromeda.cms.sitemap.model.SitemapLocation;
import com.andromeda.cms.sitemap.model.SitemapPostLocation;

@Service
public class SitemapPageService 
{
	
	@Autowired
	DataGeneratorService dataGeneratorService;
	
	@Autowired
	ArticleService sitemapArticleService;
	
	@Autowired
	PhotoGalleryService photoGalleryService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	SitemapDateDao sitemapDateDao;
	
	@Autowired
	SitemapLocationDao sitemapLocationDao;
	
	@Autowired
	SitemapPostLocationDao sitemapPostLocationDao;
	
	public void setDataGeneratorService(DataGeneratorService dataGeneratorService)
	{
		this.dataGeneratorService = dataGeneratorService;
	}
	
	public void setArticleService(ArticleService sitemapArticleService)
	{
		this.sitemapArticleService = sitemapArticleService;
	}
	
	public void setPhotoGalleryService(PhotoGalleryService photoGalleryService)
	{
		this.photoGalleryService = photoGalleryService;
	}
	
	public void setCategoryService(CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
	
	public void setSitemapDateDao(SitemapDateDao sitemapDateDao)
	{
		this.sitemapDateDao = sitemapDateDao;
	}
	
	public void setSitemapLocationDao(SitemapLocationDao sitemapLocationDao)
	{
		this.sitemapLocationDao = sitemapLocationDao;
	}
	
	/**
	 * function to create or update the news-sitemap.xml
	 * Generally called after creation of a new article
	 * @param sitemapArticle
	 */
	public void createOrUpdateNewsSitemap()
	{
		
		List<Article> latestArticles =  sitemapArticleService.getLatestArticlesWithoutPriority(null, StrapiConstants.SITEMAP_NEWS_INDEX_PAGE_LIMIT);
		String xmlFileName = dataGeneratorService.generateNewSitemap(latestArticles);
		if(xmlFileName != null && latestArticles != null)
		{
			sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
		}
	}
	
	public void createPostSitemapBtwDates(String fromDateStr, String toDateStr) 
	{
		Set<Integer> years = new HashSet<>();
		
		Date utilDate1 = StrapiUtils.formatDate(fromDateStr);
		Date utilDate2 = StrapiUtils.formatDate(toDateStr);
		
		List<Date> dates =  StrapiUtils.getDaysBetweenDates(utilDate1, utilDate2);
		
		for (Date date : dates) 
		{
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			int year = StrapiUtils.getYearFromDate(sqlDate);
			years.add(year);
			createPostSitemapByDateWithoutIndexUpdate(sqlDate);
		}
		
		for (Integer year : years) {
			createOrUpdateSitemapPostIndex(year);
		}
		
		createOrUpdateSitemapIndex();
	}

	/**
	 * function to create post-sitemap-YYYY-MM-DD.xml
	 */
	public void createPostSitemap() 
	{
		List<SitemapDate> existingSitemapDates = sitemapDateDao.getAll();
		
		//if there are no existing post sitemaps
		if((existingSitemapDates == null) || existingSitemapDates.isEmpty())
		{
			List<Date> dates = sitemapArticleService.getDistinctPublishDates();
			for (Date date : dates) 
			{
				createPostSitemapByDate(date);
			}
			
		}
		else
		{
			SitemapDate latestSitemapDate = sitemapDateDao.getLatestDate();
			Date latestArticleDate = sitemapArticleService.getMaxArticlePublishDate();
			List<Date> dates =  StrapiUtils.getDaysBetweenDates(latestSitemapDate.getDate(), latestArticleDate);
			for (Date date : dates) {
				createPostSitemapByDate(date);
			}
		}
	}
	
	public  void createPostSitemapByDate(Date date)
	{
		List<Article> sitemapArticles = sitemapArticleService.getByPublishedDate(date);
		HashMap<String, String> fileNames = dataGeneratorService.generatePostSitemap(sitemapArticles, date);
		String xmlFileName = fileNames.get("xmlFileName");
		String postFolder = fileNames.get("postFolder");
		
		int year = StrapiUtils.getYearFromDate(date);
		
		if(xmlFileName != null  && postFolder != null && sitemapArticles != null)
		{
			sitemapDateDao.add(new SitemapDate(date));
			sitemapPostLocationDao.add(new SitemapPostLocation( year, StrapiConstants.SITEMAP_FOLDER_S3 +  "/" +xmlFileName));
			sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/" + postFolder + ".xml" , StrapiConstants.SITEMAP_TYPE_INDEX));
		}
		
		createOrUpdateSitemapPostIndex(year);
		createOrUpdateSitemapIndex();
	}
	
	public  void createPostSitemapByDateWithoutIndexUpdate(Date date)
	{
		List<Article> sitemapArticles = sitemapArticleService.getByPublishedDate(date);
		HashMap<String, String> fileNames = dataGeneratorService.generatePostSitemap(sitemapArticles, date);
		if(fileNames != null)
		{
			String xmlFileName = fileNames.get("xmlFileName");
			String postFolder = fileNames.get("postFolder");
			
			int year = StrapiUtils.getYearFromDate(date);
			
			if(xmlFileName != null  && postFolder != null && sitemapArticles != null)
			{
				sitemapDateDao.add(new SitemapDate(date));
				sitemapPostLocationDao.add(new SitemapPostLocation( year, StrapiConstants.SITEMAP_FOLDER_S3 +  "/" +xmlFileName));
				sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/" + postFolder + ".xml" , StrapiConstants.SITEMAP_TYPE_INDEX));
			}
		}
	}
	
	
	/**
	 * function to create or update the gallery-sitemap.xml
	 * Generally called after creation of a new article of content type =Photo gallery
	 * @param photoGallery 
	 * @param sitemapArticle
	 */
	
	public void createOrUpdateGallerySitemap(int publishedYear) {
		
		List<PhotoGallery> allPhotos =  photoGalleryService.getByPublishedYear(publishedYear);;
		String xmlFileName = dataGeneratorService.generateGallerySitemap(publishedYear, allPhotos);
		if(xmlFileName != null && allPhotos != null)
			sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));

	}

	/**
	 * function to create or update the video-sitemap.xml
	 * Generally called after creation of a new article of content type =Video gallery
	 * @param sitemapArticle 
	 * @param sitemapArticle
	 */
	public void createOrUpdateVideoSitemapVOld(int publishedYear) 
	{
		String contentType = "Video Gallery";
		List<Article> allVideos =  sitemapArticleService.getVideosByPublishedYear(contentType, publishedYear);;
		String xmlFileName = dataGeneratorService.generateVideoSitemap(publishedYear,  1, allVideos);
		if(xmlFileName != null && allVideos != null)
			sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
	}
	
	/**
	 * function to create or update the video-sitemap.xml
	 * Generally called after creation of a new article of content type =Video gallery
	 * @param sitemapArticle 
	 * @param sitemapArticle
	 */
	public void createOrUpdateVideoSitemap(int publishedYear) 
	{
		String contentType = "Video Gallery";
		List<Article> allVideos =  sitemapArticleService.getVideosByPublishedYear(contentType, publishedYear);
		
		int vsSize = allVideos.size();
		int i = 0;
		int fileCounter = 1;
		int videoSitemapLimit = 10000;
		int counter = 0;
		while (i < vsSize)
		{
			counter = i + videoSitemapLimit ;
			if(counter >= vsSize)
				counter = vsSize ;
			List<Article> videosSubList = allVideos.subList(i, counter);
			String xmlFileName = dataGeneratorService.generateVideoSitemap(publishedYear, fileCounter , videosSubList);
			if(xmlFileName != null)
				sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
			
			fileCounter ++ ;
			i = counter;
		}
		
		createOrUpdateSitemapIndex();
	}
	
	public void createOrUpdateCategorySitemap() {
		List<Category> categories = categoryService.getAll();
		String xmlFileName = dataGeneratorService.generateCategorySitemap(categories);
		if(xmlFileName != null && categories != null)
			sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
	}

	public void createOrUpdateSitemapIndex() {
		String outputFileName = "sitemap-index.xml";
		List<SitemapLocation> sitemapLocations = sitemapLocationDao.getByType(StrapiConstants.SITEMAP_TYPE_INDEX);
		dataGeneratorService.generateSitemapIndexXml("sitemap-index.ftl", outputFileName, sitemapLocations);
		
	}
	
	public void createOrUpdateSitemapPostIndex(int year) {
		String outputFileName = "post-sitemap-" + year + ".xml";
		List<SitemapPostLocation> sitemapPostLocations = sitemapPostLocationDao.getByYear(year);
		dataGeneratorService.generatePostSitemapXml("sitemap-index.ftl", outputFileName, sitemapPostLocations);
		
	}

	public void createOrUpdatePageSitemap() {
		String outputFileName = "page-sitemap.xml";
		List<SitemapLocation> sitemapPageLocations = sitemapLocationDao.getByType(StrapiConstants.SITEMAP_TYPE_PAGE);
		dataGeneratorService.generateSitemapIndexXml("page-sitemap.ftl", outputFileName, sitemapPageLocations);
		sitemapLocationDao.add(new SitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + File.separator+ outputFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
		createOrUpdateSitemapIndex();
	}
	

	public void updatePostSitemapByDate(Date publishedDate) {
		SitemapDate sitemapDate = sitemapDateDao.getByDate(publishedDate);
		if(sitemapDate != null)
		{
			createPostSitemapByDate(publishedDate);
		}
	}
}
