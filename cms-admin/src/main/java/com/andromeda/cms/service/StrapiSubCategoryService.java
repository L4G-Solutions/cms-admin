package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;

@Service
public class StrapiSubCategoryService 
{
	@Autowired
	private StrapiContentService strapiContentService;
	
	public void setStrapiContentService(StrapiContentService strapiContentService)
	{
		this.strapiContentService = strapiContentService;
	}
	
	public List<StrapiSubCategory> getAllSubCategories()
	{
		List<StrapiSubCategory> allSubCategories = new ArrayList<>();
		StrapiResponseArray strapiResponse = null;
		strapiResponse = strapiContentService.getAllSubCategories();
		if(strapiResponse != null)
		{
			List<Entry> entries = strapiResponse.getEntries();
			for (Entry entry : entries) 
			{
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", entry.getId());
				hm.put("attributes", entry.getAttributes());
				StrapiSubCategory strapiTestSubCategory = StrapiUtils.getStrapiSubCategory(hm);
				allSubCategories.add(strapiTestSubCategory);
			}
		}
		return allSubCategories;
	}
	
	public List<StrapiSubCategory> getAllCjSubCategories()
	{
		List<StrapiSubCategory> allCjSubCategories = new ArrayList<>();
		StrapiResponseArray strapiResponse = null;
		strapiResponse = strapiContentService.getAllCJSubCategories();
		if(strapiResponse != null)
		{
			List<Entry> entries = strapiResponse.getEntries();
			for (Entry entry : entries) 
			{
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", entry.getId());
				hm.put("attributes", entry.getAttributes());
				StrapiSubCategory strapiTestSubCategory = StrapiUtils.getStrapiSubCategory(hm);
				allCjSubCategories.add(strapiTestSubCategory);
			}
		}
		return allCjSubCategories;
	}
	
	
	public StrapiSubCategory getSubCategoryById(int subCategoryId)
	{
		StrapiSubCategory strapiTestSubCategory = null;
		StrapiResponse strapiResponse = null;
		strapiResponse = strapiContentService.getSubCategoryById(subCategoryId);
		if(strapiResponse != null)
		{
			Entry entry = strapiResponse.getEntry();
			HashMap<String, Object> hm = new HashMap<>();
			hm.put("id", entry.getId());
			hm.put("attributes", entry.getAttributes());
			strapiTestSubCategory = StrapiUtils.getStrapiSubCategory(hm);
		}
		return strapiTestSubCategory;
	}
	
	public StrapiResponse postSubCategory(StrapiSubCategory strapiTestSubCategory) throws Exception
	{
		StrapiResponse strapiResponse = null;
		if(strapiTestSubCategory != null)
		{
			strapiResponse = strapiContentService.postSubCategory(strapiTestSubCategory);
		}
		
		return strapiResponse;
	}

	public void updateSubCategory(StrapiSubCategory strapiTestSubCategory) throws Exception
	{
		if(strapiTestSubCategory != null)
		{
			strapiContentService.updateSubCategory(strapiTestSubCategory);
		}

		
	}
}
