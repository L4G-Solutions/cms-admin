package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import com.andromeda.cms.feed.service.FeedService;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.PhotoGalleryService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.SubCategoryService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.commons.util.PropertiesUtils;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class ListDates {
	
	
	public static void main(String[] args) throws Exception
	{
		PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
	

		TestContextUtils.getStrapiContentService();
		TestContextUtils.getSitemapArticleService();
		TestContextUtils.getPhotoGalleryService();
		TestContextUtils.getStrapiArticleService();
		TestContextUtils.getStrapiCategoryService();
		
		TestContextUtils.getSitemapCategoryService();
		TestContextUtils.getSitemapSubCategoryService();
		SitemapPageService sitemapPageService = TestContextUtils.getSitemapPageService();
		FeedService feedService = TestContextUtils.getFeedService();
		feedService.init();
		List<Date> publishedDates = getDatesBetweenUsingJava7(new Date(2022, 1, 1), new Date(2022, 1, 31));
		sitemapPageService.createPostSitemapByDate(publishedDates.get(0));

	}
	
	public static List getDatesBetweenUsingJava7(Date startDate, Date endDate) {
	List datesInRange = new ArrayList<>();
	  Calendar calendar = getCalendarWithoutTime(startDate);
	  Calendar endCalendar = getCalendarWithoutTime(endDate);

	  while (calendar.before(endCalendar)) {
	    Date result = calendar.getTime();
	    datesInRange.add(result);
	    calendar.add(Calendar.DATE, 1);
	  }

	  return datesInRange;
	}

	private static Calendar getCalendarWithoutTime(Date date) {
	  Calendar calendar = new GregorianCalendar();
	  calendar.setTime(date);
	  calendar.set(Calendar.HOUR, 0);
	  calendar.set(Calendar.HOUR_OF_DAY, 0);
	  calendar.set(Calendar.MINUTE, 0);
	  calendar.set(Calendar.SECOND, 0);
	  calendar.set(Calendar.MILLISECOND, 0);
	  return calendar;
	}

}
