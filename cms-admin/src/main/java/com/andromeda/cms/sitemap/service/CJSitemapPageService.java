package com.andromeda.cms.sitemap.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.service.CJArticleService;
import com.andromeda.cms.service.CJCategoryService;
import com.andromeda.cms.service.CJDataGeneratorService;
import com.andromeda.cms.service.CJPhotoGalleryService;
import com.andromeda.cms.sitemap.dao.CJSitemapDateDao;
import com.andromeda.cms.sitemap.dao.CJSitemapLocationDao;
import com.andromeda.cms.sitemap.dao.CJSitemapPostLocationDao;
import com.andromeda.cms.sitemap.model.CJSitemapDate;
import com.andromeda.cms.sitemap.model.CJSitemapLocation;
import com.andromeda.cms.sitemap.model.CJSitemapPostLocation;


@Service
public class CJSitemapPageService {
	@Autowired
	CJDataGeneratorService cjDataGeneratorService;
	
	@Autowired
	CJArticleService cjArticleService;
	
	@Autowired
	CJPhotoGalleryService cjPhotoGalleryService;
	
	@Autowired
	CJCategoryService cjCategoryService;
	
	@Autowired
	CJSitemapDateDao cjSitemapDateDao;
	
	@Autowired
	CJSitemapLocationDao cjSitemapLocationDao;
	
	@Autowired
	CJSitemapPostLocationDao cjSitemapPostLocationDao;
	
	public void setCjDataGeneratorService(CJDataGeneratorService cjDataGeneratorService)
	{
		this.cjDataGeneratorService = cjDataGeneratorService;
	}
	
	public void setCjArticleService(CJArticleService cjArticleService)
	{
		this.cjArticleService = cjArticleService;
	}
	
	public void setCjPhotoGalleryService(CJPhotoGalleryService cjPhotoGalleryService)
	{
		this.cjPhotoGalleryService = cjPhotoGalleryService;
	}
	
	public void setCjCategoryService(CJCategoryService cjCategoryService)
	{
		this.cjCategoryService = cjCategoryService;
	}
	
	public void setCjSitemapDateDao(CJSitemapDateDao cjSitemapDateDao)
	{
		this.cjSitemapDateDao = cjSitemapDateDao;
	}
	
	public void setCjSitemapLocationDao(CJSitemapLocationDao cjSitemapLocationDao)
	{
		this.cjSitemapLocationDao = cjSitemapLocationDao;
	}
	
	/**
	 * function to create or update the news-sitemap.xml
	 * Generally called after creation of a new article
	 * @param sitemapArticle
	 */
	public void createOrUpdateCjNewsSitemap()
	{
		
		List<CJArticle> latestArticles = cjArticleService.getLatestArticlesWithoutPriority(null, StrapiConstants.SITEMAP_NEWS_INDEX_PAGE_LIMIT);
		String xmlFileName = cjDataGeneratorService.generateNewSitemap(latestArticles);
		if(xmlFileName != null && latestArticles != null)
		{
			cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
		}
	}
	
	public void createCjPostSitemapBtwDates(String fromDateStr, String toDateStr) 
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
			createCjPostSitemapByDateWithoutIndexUpdate(sqlDate);
		}
		
		for (Integer year : years) {
			createOrUpdateCjSitemapPostIndex(year);
		}
		
		createOrUpdateCjSitemapIndex();
	}

	/**
	 * function to create post-sitemap-YYYY-MM-DD.xml
	 */
	public void createCjPostSitemap() 
	{
		List<CJSitemapDate> existingSitemapDates = cjSitemapDateDao.getAll();
		
		//if there are no existing post sitemaps
		if((existingSitemapDates == null) || existingSitemapDates.isEmpty())
		{
			List<Date> dates = cjArticleService.getDistinctPublishDates();
			for (Date date : dates) 
			{
				createCjPostSitemapByDate(date);
			}
			
		}
		else
		{
			CJSitemapDate latestSitemapDate = cjSitemapDateDao.getLatestDate();
			Date latestArticleDate = cjArticleService.getMaxArticlePublishDate();
			List<Date> dates =  StrapiUtils.getDaysBetweenDates(latestSitemapDate.getDate(), latestArticleDate);
			for (Date date : dates) {
				createCjPostSitemapByDate(date);
			}
		}
	}
	
	public  void createCjPostSitemapByDate(Date date)
	{
		List<CJArticle> sitemapArticles = cjArticleService.getByPublishedDate(date);
		HashMap<String, String> fileNames = cjDataGeneratorService.generatePostSitemap(sitemapArticles, date);
		String xmlFileName = fileNames.get("xmlFileName");
		String postFolder = fileNames.get("postFolder");
		
		int year = StrapiUtils.getYearFromDate(date);
		
		if(xmlFileName != null  && postFolder != null && sitemapArticles != null)
		{
			cjSitemapDateDao.add(new CJSitemapDate(date));
			cjSitemapPostLocationDao.add(new CJSitemapPostLocation( year, StrapiConstants.SITEMAP_FOLDER_S3 +  "/" +xmlFileName));
			cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/" + postFolder + ".xml" , StrapiConstants.SITEMAP_TYPE_INDEX));
		}
		
		createOrUpdateCjSitemapPostIndex(year);
		createOrUpdateCjSitemapIndex();
	}
	
	public  void createCjPostSitemapByDateWithoutIndexUpdate(Date date)
	{
		List<CJArticle> sitemapArticles = cjArticleService.getByPublishedDate(date);
		HashMap<String, String> fileNames = cjDataGeneratorService.generatePostSitemap(sitemapArticles, date);
		if(fileNames != null)
		{
			String xmlFileName = fileNames.get("xmlFileName");
			String postFolder = fileNames.get("postFolder");
			
			int year = StrapiUtils.getYearFromDate(date);
			
			if(xmlFileName != null  && postFolder != null && sitemapArticles != null)
			{
				cjSitemapDateDao.add(new CJSitemapDate(date));
				cjSitemapPostLocationDao.add(new CJSitemapPostLocation( year, StrapiConstants.SITEMAP_FOLDER_S3 +  "/" +xmlFileName));
				cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/" + postFolder + ".xml" , StrapiConstants.SITEMAP_TYPE_INDEX));
			}
		}
	}
	
	
	/**
	 * function to create or update the gallery-sitemap.xml
	 * Generally called after creation of a new article of content type =Photo gallery
	 * @param photoGallery 
	 * @param sitemapArticle
	 */
	
	public void createOrUpdateCjGallerySitemap(int publishedYear) {
		
		List<CJPhotoGallery> allPhotos =  cjPhotoGalleryService.getByPublishedYear(publishedYear);;
		String xmlFileName = cjDataGeneratorService.generateGallerySitemap(publishedYear, allPhotos);
		if(xmlFileName != null && allPhotos != null)
			cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));

	}

	/**
	 * function to create or update the video-sitemap.xml
	 * Generally called after creation of a new article of content type =Video gallery
	 * @param sitemapArticle 
	 * @param sitemapArticle
	 */
	public void createOrUpdateCjVideoSitemapVOld(int publishedYear) 
	{
		String contentType = "Video Gallery";
		List<CJArticle> allVideos =  cjArticleService.getVideosByPublishedYear(contentType, publishedYear);;
		String xmlFileName = cjDataGeneratorService.generateVideoSitemap(publishedYear,  1, allVideos);
		if(xmlFileName != null && allVideos != null)
			cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
	}
	
	/**
	 * function to create or update the video-sitemap.xml
	 * Generally called after creation of a new article of content type =Video gallery
	 * @param sitemapArticle 
	 * @param sitemapArticle
	 */
	public void createOrUpdateCjVideoSitemap(int publishedYear) 
	{
		String contentType = "Video Gallery";
		List<CJArticle> allVideos =  cjArticleService.getVideosByPublishedYear(contentType, publishedYear);
		
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
			List<CJArticle> videosSubList = allVideos.subList(i, counter);
			String xmlFileName = cjDataGeneratorService.generateVideoSitemap(publishedYear, fileCounter , videosSubList);
			if(xmlFileName != null)
				cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
			
			fileCounter ++ ;
			i = counter;
		}
		
		createOrUpdateCjSitemapIndex();
	}
	
	public void createOrUpdateCjCategorySitemap() {
		List<CJCategory> categories = cjCategoryService.getAll();
		String xmlFileName = cjDataGeneratorService.generateCategorySitemap(categories);
		if(xmlFileName != null && categories != null)
			cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + "/"+ xmlFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
	}

	public void createOrUpdateCjSitemapIndex() {
		String outputFileName = "sitemap-index.xml";
		List<CJSitemapLocation> sitemapLocations = cjSitemapLocationDao.getByType(StrapiConstants.SITEMAP_TYPE_INDEX);
		cjDataGeneratorService.generateSitemapIndexXml("sitemap-index.ftl", outputFileName, sitemapLocations);
		
	}
	
	public void createOrUpdateCjSitemapPostIndex(int year) {
		String outputFileName = "post-sitemap-" + year + ".xml";
		List<CJSitemapPostLocation> sitemapPostLocations = cjSitemapPostLocationDao.getByYear(year);
		cjDataGeneratorService.generatePostSitemapXml("sitemap-index.ftl", outputFileName, sitemapPostLocations);
		
	}

	public void createOrUpdateCjPageSitemap() {
		String outputFileName = "page-sitemap.xml";
		List<CJSitemapLocation> sitemapPageLocations = cjSitemapLocationDao.getByType(StrapiConstants.SITEMAP_TYPE_PAGE);
		cjDataGeneratorService.generateSitemapIndexXml("page-sitemap.ftl", outputFileName, sitemapPageLocations);
		cjSitemapLocationDao.add(new CJSitemapLocation(StrapiConstants.SITEMAP_FOLDER_S3 + File.separator+ outputFileName, StrapiConstants.SITEMAP_TYPE_INDEX));
		createOrUpdateCjSitemapIndex();
	}
	

	public void updateCjPostSitemapByDate(Date publishedDate) {
		CJSitemapDate sitemapDate = cjSitemapDateDao.getByDate(publishedDate);
		if(sitemapDate != null)
		{
			createCjPostSitemapByDate(publishedDate);
		}
	}
}
