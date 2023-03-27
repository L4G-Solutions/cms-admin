package com.andromeda.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.model.CJSubCategory;

@Service
public class CJSubCategoryService {
	@Autowired
	CJSubCategoryDao cjSubCategoryDao;
	
	public void setCjSubCategoryDao(CJSubCategoryDao cjSubCategoryDao)
	{
		this.cjSubCategoryDao = cjSubCategoryDao;
	}
	
	public void batchAdd(List<CJSubCategory> cjSubCategories)
	{
		for (CJSubCategory cjSubCategory : cjSubCategories) 
		{
			add(cjSubCategory);
		}
	}
	
	public void batchUpdate(List<CJSubCategory> cjSubCategories)
	{
		for (CJSubCategory cjSubCategory : cjSubCategories) 
		{
			update(cjSubCategory);
		}
	}
	
	public void update(CJSubCategory cjSubCategory)
	{
		cjSubCategoryDao.update(cjSubCategory);
	}
	
	public void add(CJSubCategory sitemapSubCategory)
	{
		cjSubCategoryDao.add(sitemapSubCategory);
	}
	
	public CJSubCategory getById(int id)
	{
		
		return cjSubCategoryDao.getById(id);
	}
	
	public List<CJSubCategory> getAll()
	{
		return cjSubCategoryDao.getAll();
	}

}
