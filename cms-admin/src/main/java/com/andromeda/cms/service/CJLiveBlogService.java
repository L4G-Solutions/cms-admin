package com.andromeda.cms.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CJLiveBlogDao;
import com.andromeda.cms.dao.CJLiveBlogRedirectionUrlDao;
import com.andromeda.cms.dao.CJCategoryDao;
import com.andromeda.cms.dao.CJLiveBlogDao;
import com.andromeda.cms.dao.CJPhotoGalleryDao;
import com.andromeda.cms.dao.CJRedirectionUrlDao;
import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.CJLiveBlog;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.CJRedirectionUrl;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.commons.util.JsonUtils;

@Service
public class CJLiveBlogService {
		private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		
		@Autowired
		private CJLiveBlogDao cjLiveBlogDao;
		
		@Autowired
		private CJPhotoGalleryDao cjPhotoGalleryDao;
		
		@Autowired
		private CJCategoryDao cjCategoryDao;
		
		@Autowired
		private CJSubCategoryDao cjSubCategoryDao;
		
		@Autowired
		private CJDataGeneratorService dataGeneratorService;
		
		@Autowired
		private CmsProxyService cmsProxyService;
		
		@Autowired
		CJRedirectionUrlDao cjRedirectionUrlDao;
		
		@Autowired
		CJLiveBlogRedirectionUrlDao cjLiveBlogRedirectionUrlDao;

		public void setCjLiveBlogDao(CJLiveBlogDao cjLiveBlogDao)
		{
			this.cjLiveBlogDao = cjLiveBlogDao;
		}
		
		public void setCjPhotoGalleryDao(CJPhotoGalleryDao cjPhotoGalleryDao)
		{
			this.cjPhotoGalleryDao = cjPhotoGalleryDao;
		}
		
		public void setCjCategoryDao(CJCategoryDao cjCategoryDao)
		{
			this.cjCategoryDao = cjCategoryDao;
		}
		
		public void setCjSubCategoryDao(CJSubCategoryDao cjSubCategoryDao)
		{
			this.cjSubCategoryDao = cjSubCategoryDao;
		}
		
		public void setCjDataGeneratorService(CJDataGeneratorService chitrajyothyDataGeneratorService)
		{
			this.dataGeneratorService = chitrajyothyDataGeneratorService;
		}
		
		public void setCmsProxyService(CmsProxyService cmsProxyService)
		{
			this.cmsProxyService = cmsProxyService;
		}

				
		public void generateLiveBlogPage(CJLiveBlog cjLiveBlog, List<StrapiCategory> categoryList, boolean updateFlag)
		{
			String relatedArticlesStr = cjLiveBlog.getRelatedArticles();
			String[] relatedArticleIds = relatedArticlesStr.split(",");
			List<CJLiveBlog> relatedArticles = new ArrayList<>();
			
			if(relatedArticleIds.length > 0)
			{
				for (String relatedArticleId : relatedArticleIds) 
				{
					if(relatedArticleId.length() > 0)
					{
						CJLiveBlog sa = cjLiveBlogDao.getById(Integer.parseInt(relatedArticleId));
						relatedArticles.add(sa);
					}
				}
			}
			dataGeneratorService.generateLiveBlog(cjLiveBlog,categoryList, relatedArticles, updateFlag);
			dataGeneratorService.generateAmpLiveBlog(cjLiveBlog,categoryList, relatedArticles, updateFlag);

		}
		

		public List<CJLiveBlog> getVideosByPublishedYear(String contentType, int publishedYear) {
			return cjLiveBlogDao.getByPublishedYear(contentType, publishedYear);
		}

		

		
		public boolean addOrUpdate(CJLiveBlog cjLiveBlog) 
		{
			boolean updateFlag = false;
			CJLiveBlog existingCJLiveBlog = cjLiveBlogDao.getByIdWOPublished(cjLiveBlog.getId());
			
			
			if(existingCJLiveBlog == null)
			{
				System.out.println("Adding CJ LiveBlog " + JsonUtils.toString(cjLiveBlog));
				add(cjLiveBlog);
			}
			else
			{
				// if there is a clash of published Year between the child tables then DELETE the existing record,then ADD
				//else only UPDATE
				updateFlag = true;
				System.out.println("Updating article " + JsonUtils.toString(existingCJLiveBlog));
				if(existingCJLiveBlog.getPublishedYear() != cjLiveBlog.getPublishedYear())
				{
					cjLiveBlogDao.deleteById(existingCJLiveBlog.getId());
					add(cjLiveBlog);
				}
				else
				{
					update(cjLiveBlog);
					//if old seoslug != new seoslug
					if(!existingCJLiveBlog.getSeoSlug().equals(cjLiveBlog.getSeoSlug()))
					{
						int id = existingCJLiveBlog.getId();
						cjLiveBlogRedirectionUrlDao.add(new CJRedirectionUrl(id, existingCJLiveBlog.getUrl().trim(), existingCJLiveBlog.getAmpUrl().trim(), existingCJLiveBlog.getPublishedYear()));
						List<CJRedirectionUrl> redirectionUrls = cjLiveBlogRedirectionUrlDao.getById(id);
						for (CJRedirectionUrl redirectionUrl : redirectionUrls) {
							dataGeneratorService.generateLiveBlogRedirectionPage(cjLiveBlog, redirectionUrl);
						}
					}
				}
			}
			
			return updateFlag;
		}
		
		public void add(CJLiveBlog sitemapArticle)
		{
			cjLiveBlogDao.add(sitemapArticle);
			//dataGeneratorService.generateArticle(sitemapArticle, categoryList);
		}
		
		public void update(CJLiveBlog sitemapArticle)
		{
			cjLiveBlogDao.update(sitemapArticle);
			//dataGeneratorService.generateArticle(sitemapArticle, categoryList);
		}

		
		public List<CJLiveBlog> getLatestArticles(String contentType, Integer limit) 
		{
			return cjLiveBlogDao.getLatestArticles(contentType, limit);
		}
		
		public List<CJLiveBlog> getLatestArticlesOnCreated(String contentType, Integer limit) 
		{
			return cjLiveBlogDao.getLatestArticlesOnCreated(contentType, limit);
		}
		
		public List<CJLiveBlog> getLatestArticlesWithoutPriority(String contentType, Integer limit)
		{
			return cjLiveBlogDao.getLatestArticlesWithoutPriority(contentType, limit);
		}
		

		public List<CJLiveBlog> getSubCategoryRelatedArticles(int subCategoryId) 
		{
			return cjLiveBlogDao.getSubCategoryRelatedArticles(subCategoryId);
		}
		
		public List<CJLiveBlog> getLatestBySubCategory(String contentType,Integer categoryId,Integer subCategoryId, Integer limit, Integer offset) 
		{
			return cjLiveBlogDao.getLatestBySubCategoryId(contentType, categoryId, subCategoryId,  limit,  offset);
		}
		
		public List<CJLiveBlog> getLatestBySubCategoryWithoutPriority(String contentType,int categoryId,int subCategoryId, int limit, int offset) 
		{
			return cjLiveBlogDao.getLatestBySubCategoryIdWithoutPriority(contentType, categoryId, subCategoryId,  limit,  offset);
		}
		
		public List<CJLiveBlog> getLatestBySubCategory(HashMap<String, String> params) 
		{
			Integer limit = StrapiConstants.LIMIT;
			Integer subCategoryId = null;
			Integer categoryId = null;
			Integer offset = null;
			String contentType = null;
			if(params.containsKey("subCategoryId"))
			{
				subCategoryId = Integer.parseInt(params.get("subCategoryId"));
			}
			if(params.containsKey("categoryId"))
			{
				categoryId = Integer.parseInt(params.get("categoryId"));
			}
			
			if(params.containsKey("contentType"))
				limit = Integer.parseInt(params.get("contentType"));			
			if(params.containsKey("limit"))
				limit = Integer.parseInt(params.get("limit"));
			if(params.containsKey("offset"))
				offset = Integer.parseInt(params.get("offset"));
			return cjLiveBlogDao.getLatestBySubCategoryId(contentType,categoryId,subCategoryId, limit, offset);
		}
		
		public List<CJLiveBlog> getCategoryRelatedArticles(Integer subCategoryId) 
		{
			return cjLiveBlogDao.getByCategoryId(subCategoryId);
		}
		
		public List<CJLiveBlog> getLatestByCategoryIdWithoutPriority(Integer categoryId, Integer limit) 
		{
			return cjLiveBlogDao.getLatestByCategoryIdWithoutPriority(categoryId, limit, null);
		}
		
		public List<CJLiveBlog> getLatestByCategory(Integer categoryId, Integer limit) 
		{
			return cjLiveBlogDao.getLatestByCategoryId(categoryId, limit, null);
		}

		public CJLiveBlog getById(int id) {
			return cjLiveBlogDao.getById(id);
		}
		
		public List<CJLiveBlog> getByIds(List<Long> ids) {
			return cjLiveBlogDao.getByIds(ids);
		}
		
		public CJLiveBlog getByIdWOPublished(int id) {
			return cjLiveBlogDao.getByIdWOPublished(id);
		}
		
		/*public Article getByAbnStoryId(String abnStoryId) {
			return sitemapArticleDao.getByAbnStoryId(abnStoryId);
		}*/

		public List<Date> getDistinctPublishDates() {
			
			return cjLiveBlogDao.getDistinctPublishDates();
		}
		
		public List<Integer> getDistinctPublishYears() 
		{
			return cjLiveBlogDao.getDistinctPublishYears();
		}
		
		public Date getMaxArticlePublishDate() 
		{
			return cjLiveBlogDao.getMaxArticlePublishDate();
		}

		public List<CJLiveBlog> getByPublishedDate(Date date) {
			
			return cjLiveBlogDao.getByPublishedDate(date);
		}



		public List<CJLiveBlog> getVideos() {
			return cjLiveBlogDao.getVideos(null);
		}



		


		
		public List<CJLiveBlog> getLatestVideosFromCMS() throws Exception {
			List<String> articlesStrList = cmsProxyService.getList("cj_latestVideos", 0, StrapiConstants.LIMIT);
			List<CJLiveBlog> latestVideos = new ArrayList<>();
			if(!articlesStrList.isEmpty())
			{
				CJLiveBlog chitrajyothyArticle;
				for (String cjaStr : articlesStrList) 
				{
					chitrajyothyArticle = JsonUtils.deserialize(cjaStr, CJLiveBlog.class);
					latestVideos.add(chitrajyothyArticle);
				}
			}
			return latestVideos;
		}
		
		
		public List<CJLiveBlog> getLatestArticlesFromCMS() throws Exception 
		{
			List<String> articlesStrList = cmsProxyService.getList("cj_latestArticles", 0, 10);
			List<CJLiveBlog> latestArticles = new ArrayList<>();
			if(!articlesStrList.isEmpty())
			{
				CJLiveBlog chitrajyothyArticle;
				for (String cjaStr : articlesStrList) 
				{
					chitrajyothyArticle = JsonUtils.deserialize(cjaStr, CJLiveBlog.class);
					latestArticles.add(chitrajyothyArticle);
				}
			}
			return latestArticles;
		}
		
		public List<CJLiveBlog> getPriorityArticlesFromCMS() throws Exception {
			List<String> articlesStrList = cmsProxyService.getList("cj_priorityArticles", 0, StrapiConstants.LIMIT);
			List<CJLiveBlog>priorityArticles = new ArrayList<>();
			if(!articlesStrList.isEmpty())
			{
				CJLiveBlog chitrajyothyArticle;
				for (String cjaStr : articlesStrList) 
				{
					chitrajyothyArticle = JsonUtils.deserialize(cjaStr, CJLiveBlog.class);
					priorityArticles.add(chitrajyothyArticle);
				}
			}
			return priorityArticles;
		}
		
}
