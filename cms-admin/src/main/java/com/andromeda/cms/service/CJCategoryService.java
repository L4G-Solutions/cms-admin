package com.andromeda.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.CJCategoryDao;
import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.model.CJCategory;


@Service
public class CJCategoryService {
	@Autowired
	CJCategoryDao cjCategoryDao;
	
	
	@Autowired
	CJSubCategoryDao cjSubCategoryDao;
	

	public void setCjCategoryDao(CJCategoryDao cjCategoryDao)
	{
		this.cjCategoryDao = cjCategoryDao;
	}
	
	
	
	public void addBatch(List<CJCategory> cjCategories)
	{
		for (CJCategory cjCategory : cjCategories) 
		{
			addOrUpdate(cjCategory);
		}
	}
	
	public void add(CJCategory cjCategory)
	{
		cjCategoryDao.add(cjCategory);
	}
	
	public void addOrUpdate(CJCategory cjCategory)
	{
		CJCategory existingCjCategory = cjCategoryDao.getById(cjCategory.getId());
		if(existingCjCategory  != null)
		{
			cjCategoryDao.update(cjCategory);
		}
		else
		{
			cjCategoryDao.add(cjCategory);
		}
	}
	
	
	public CJCategory getById(int id)
	{
		return cjCategoryDao.getById(id);
	}
	
	public List<CJCategory> getAll()
	{
		return cjCategoryDao.getAll();
	}

}
