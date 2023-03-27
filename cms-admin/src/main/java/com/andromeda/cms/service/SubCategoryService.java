package com.andromeda.cms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.SubCategory;

@Service
public class SubCategoryService 
{
	@Autowired
	SubCategoryDao sitemapSubCategoryDao;
	
	public void setSitemapSubCategoryDao(SubCategoryDao sitemapSubCategoryDao)
	{
		this.sitemapSubCategoryDao = sitemapSubCategoryDao;
	}
	
	public void batchAdd(List<SubCategory> sitemapSubCategories)
	{
		for (SubCategory sitemapSubCategory : sitemapSubCategories) 
		{
			add(sitemapSubCategory);
		}
	}
	
	public void batchUpdate(List<SubCategory> sitemapSubCategories)
	{
		for (SubCategory sitemapSubCategory : sitemapSubCategories) 
		{
			update(sitemapSubCategory);
		}
	}
	
	public void update(SubCategory sitemapSubCategory)
	{
		sitemapSubCategoryDao.update(sitemapSubCategory);
	}
	
	public void add(SubCategory sitemapSubCategory)
	{
		sitemapSubCategoryDao.add(sitemapSubCategory);
	}
	
	public SubCategory getById(int id)
	{
		
		return sitemapSubCategoryDao.getById(id);
	}
	
	public List<SubCategory> getAll()
	{
		return sitemapSubCategoryDao.getAll();
	}
}
