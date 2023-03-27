package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CartoonDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.Cartoon;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.Horoscope;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.commons.util.JsonUtils;

@Service
public class CartoonService 
{
	@Autowired 
	CartoonDao sitemapCartoonDao;
	
	@Autowired
	private CategoryDao sitemapCategoryDao;
	
	@Autowired
	private DataGeneratorService dataGeneratorService;
	
	@Autowired
	private CmsProxyService cmsProxyService;
	
	
	public void setSitemapCategoryDao(CategoryDao sitemapCategoryDao) {
		this.sitemapCategoryDao = sitemapCategoryDao;
	}


	public void setDataGeneratorService(DataGeneratorService dataGeneratorService) {
		this.dataGeneratorService = dataGeneratorService;
	}


	public void setCmsProxyService(CmsProxyService cmsProxyService) {
		this.cmsProxyService = cmsProxyService;
	}



	
	public void setSitemapCartoonDao(CartoonDao sitemapCartoonDao)
	{
		this.sitemapCartoonDao = sitemapCartoonDao;
	}
	
	
	public boolean addOrUpdate(Cartoon sitemapCartoon) 
	{
		boolean updateFlag = false;
		Cartoon existingSitemapCartoon = sitemapCartoonDao.getByIdWOPublished(sitemapCartoon.getId());
		if(existingSitemapCartoon == null)
		{
			add(sitemapCartoon);
		}
		else
		{
			updateFlag = true;
			update(sitemapCartoon);
		}
		
		return updateFlag;
	}
	
	
	public void update(Cartoon sitemapCartoon) {
		sitemapCartoonDao.update(sitemapCartoon);
		
	}

	public void add(Cartoon sitemapCartoon) {
		sitemapCartoonDao.add(sitemapCartoon);

	}

	public void generateCartoonPage(int primaryCategoryId, List<StrapiCategory> categoryList, boolean updateFlag) {
		Category primaryCategory = sitemapCategoryDao.getById(primaryCategoryId);

		List<Cartoon> latestCartoons = sitemapCartoonDao.getLatestCartoons(StrapiConstants.CARTOON_DATA_LIMIT, null);
		dataGeneratorService.generateCartoonPage(primaryCategory, categoryList, latestCartoons, updateFlag);
		
	}

	
	public void saveLatestCartoonsOnCreatedToRedis() {
		List<Cartoon> latestCartoons =  sitemapCartoonDao.getLatestCartoonsOnCreated(StrapiConstants.CARTOON_DATA_LIMIT.intValue(), null);
				List<String> values = new ArrayList<>();
		
		if(latestCartoons != null && !latestCartoons.isEmpty())
		{
			
			cmsProxyService.delete("latestCartoons");
			
			for (Cartoon ct : latestCartoons) {
				values.add(JsonUtils.toString(ct));
			}
			cmsProxyService.saveToList("latestCartoons", values);
		}
		
	}

	public void saveLatestCartoonsToRedis() {
		List<Cartoon> latestCartoons =  sitemapCartoonDao.getLatestCartoons(StrapiConstants.CARTOON_DATA_LIMIT.intValue(), null);
				List<String> values = new ArrayList<>();
		
		if(latestCartoons != null && !latestCartoons.isEmpty())
		{
			
			cmsProxyService.delete("latestCartoons");
			
			for (Cartoon ct : latestCartoons) {
				values.add(JsonUtils.toString(ct));
			}
			cmsProxyService.saveToList("latestCartoons", values);
		}
		
	}

	
	public void saveCategoryRelatedCartoonsOnCreatedtoRedis(Cartoon sitemapCartoon)
	{
	Integer cartoonPrimaryCategoryId = sitemapCartoon.getPrimaryCategoryId();
				
		List<Cartoon> catRelatedCartoons =  sitemapCartoonDao.getLatestCartoonsOnCreated(StrapiConstants.REDIS_CARTOON_DATA_LIMIT.intValue(), null);
				List<String> values = new ArrayList<>();
		
		if(catRelatedCartoons != null && !catRelatedCartoons.isEmpty())
		{
			String redisCatKey = "cat_" + cartoonPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (Cartoon ct : catRelatedCartoons) {
				values.add(JsonUtils.toString(ct));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}// TODO Auto-generated method stub
		
	}

	public void saveCategoryRelatedCartoonstoRedis(Cartoon sitemapCartoon)
	{
	Integer cartoonPrimaryCategoryId = sitemapCartoon.getPrimaryCategoryId();
				
		List<Cartoon> catRelatedCartoons =  sitemapCartoonDao.getLatestCartoons(StrapiConstants.REDIS_CARTOON_DATA_LIMIT.intValue(), null);
				List<String> values = new ArrayList<>();
		
		if(catRelatedCartoons != null && !catRelatedCartoons.isEmpty())
		{
			String redisCatKey = "cat_" + cartoonPrimaryCategoryId;
			cmsProxyService.delete(redisCatKey);
			
			for (Cartoon ct : catRelatedCartoons) {
				values.add(JsonUtils.toString(ct));
			}
			cmsProxyService.saveToList(redisCatKey, values);
		}// TODO Auto-generated method stub
		
	}


	public Cartoon getById(int id) {
			return sitemapCartoonDao.getById(id);
			
		}


	public List<Cartoon> getLatestCartoonsFromRedis(Integer limit) throws Exception {
		List<String> cartoonsStrList = cmsProxyService.getList("latestCartoons", 0, StrapiConstants.CARTOON_DATA_LIMIT);
		List<Cartoon> latestCartoons = new ArrayList<>();
		if(!cartoonsStrList.isEmpty())
		{
			Cartoon cartoon;
			for (String saStr : cartoonsStrList) 
			{
				cartoon = JsonUtils.deserialize(saStr, Cartoon.class);
				latestCartoons.add(cartoon);
			}
		}
		return latestCartoons;
	}
	
	
}
