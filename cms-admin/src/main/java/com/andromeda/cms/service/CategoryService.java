package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.SubCategory;

@Service
public class CategoryService 
{
	@Autowired
	CategoryDao sitemapCategoryDao;
	
	@Autowired
	ArticleDao sitemapArticleDao;
	
	@Autowired
	SubCategoryDao sitemapSubCategoryDao;
	
	@Autowired
	private DataGeneratorService dataGeneratorService;

	public void setSitemapCategoryDao(CategoryDao sitemapCategoryDao)
	{
		this.sitemapCategoryDao = sitemapCategoryDao;
	}
	
	public void setDataGeneratorService(DataGeneratorService dataGeneratorService)
	{
		this.dataGeneratorService = dataGeneratorService;
	}
	
	
	
	public void addBatch(List<Category> sitemapCategories)
	{
		for (Category sitemapCategory : sitemapCategories) 
		{
			addOrUpdate(sitemapCategory);
		}
	}
	
	public void add(Category sitemapCategory)
	{
		sitemapCategoryDao.add(sitemapCategory);
	}
	
	public void addOrUpdate(Category sitemapCategory)
	{
		Category existingSitemapCategory = sitemapCategoryDao.getById(sitemapCategory.getId());
		if(existingSitemapCategory != null)
		{
			sitemapCategoryDao.update(sitemapCategory);
		}
		else
		{
			sitemapCategoryDao.add(sitemapCategory);
		}
		
	}
	
	
	public Category getById(int id)
	{
		return sitemapCategoryDao.getById(id);
	}
	
	public List<Category> getAll()
	{
		return sitemapCategoryDao.getAll();
	}
}
