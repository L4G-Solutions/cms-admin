package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CartoonDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.HoroscopeDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.Horoscope;
import com.andromeda.cms.model.StrapiCategory;

@Service
public class HoroscopeService 
{
	@Autowired
	HoroscopeDao horoscopeDao;
	
	@Autowired
	private DataGeneratorService dataGeneratorService;
	
	@Autowired
	private CmsProxyService cmsProxyService;
	
	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private SubCategoryDao ssitemapSubCategoryDao;
	
	public void setSitemapHoroscopeDao(HoroscopeDao horoscopeDao)
	{
		this.horoscopeDao = horoscopeDao;
	}
	
	public boolean addOrUpdate(Horoscope horoscope) 
	{
		boolean updateFlag = false;
		Horoscope existingHoroscope = horoscopeDao.getByIdWOPublished(horoscope.getId());
		if(existingHoroscope == null)
		{
			add(horoscope);
		}
		else
		{
			updateFlag = true;
			update(horoscope);
		}
		
		return updateFlag;
	}
	
	public void update(Horoscope horoscope) {
		horoscopeDao.update(horoscope);
		
	}

	public void add(Horoscope sitemapHoroscope)
	{
		horoscopeDao.add(sitemapHoroscope);
	}
	
	public List<Horoscope> getLatestHoroscopes(int limit)
	{
		return horoscopeDao.getLatestHoroscopes(limit);
	}

	public void generateHoroscopePage(int primaryCategoryId,List<StrapiCategory> categoryList, boolean updateFlag) 
	{
		HashMap<String, Horoscope> latestHoroscopes = new HashMap<>();
		Category primaryCategory = categoryDao.getById(primaryCategoryId);
		List<Horoscope> latestHoroscopesList = horoscopeDao.getLatestHoroscopes(StrapiConstants.HOROSCOPE_LATEST_LIMIT);
		for (Horoscope horoscope : latestHoroscopesList) {
			String type = horoscope.getHoroscopeType();
			if(type.equalsIgnoreCase(StrapiConstants.HOROSCOPETYPE_DAILY))
			{
				latestHoroscopes.put("daily", horoscope);
			}
			if(type.equalsIgnoreCase(StrapiConstants.HOROSCOPETYPE_WEEKLY))
			{
				latestHoroscopes.put("weekly", horoscope);
			}
			if(type.equalsIgnoreCase(StrapiConstants.HOROSCOPETYPE_MONTHLY))
			{
				latestHoroscopes.put("monthly", horoscope);
			}
			if(type.equalsIgnoreCase(StrapiConstants.HOROSCOPETYPE_YEARLY_BY_BIRTHDATE))
			{
				latestHoroscopes.put("yearlyByBirthDate", horoscope);
			}
			if(type.equalsIgnoreCase(StrapiConstants.HOROSCOPETYPE_YEARLY_BY_BIRTHSTAR))
			{
				latestHoroscopes.put("yearlyByBirthStar", horoscope);
			}
			if(type.equalsIgnoreCase(StrapiConstants.HOROSCOPETYPE_PANCHANGAM))
			{
				latestHoroscopes.put("panchangam", horoscope);
			}
		}
		dataGeneratorService.generateHoroscope(primaryCategory, categoryList,latestHoroscopes, updateFlag);
		
	}

	public Horoscope getById(int id) {
		return horoscopeDao.getById(id);
		
	}
}
