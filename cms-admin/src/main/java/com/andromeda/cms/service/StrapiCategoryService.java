package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.commons.util.JsonUtils;
import com.andromeda.commons.util.StringUtils;

@Service
public class StrapiCategoryService 
{
	@Autowired
	private StrapiContentService strapiContentService;
	
	public void setStrapiContentService(StrapiContentService strapiContentService)
	{
		this.strapiContentService = strapiContentService;
	}
	
	public List<StrapiCategory> getAllCategories()
	{
		List<StrapiCategory> allCategories = new ArrayList<>();
		StrapiResponseArray strapiResponseArray = null;
		strapiResponseArray = strapiContentService.getAllCategories();
		if(strapiResponseArray != null)
		{
			List<Entry> entries = strapiResponseArray.getEntries();
			for (Entry entry : entries) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", entry.getId());
				hm.put("attributes", entry.getAttributes());
				StrapiCategory strapiTestCategory = StrapiUtils.getStrapiCategory(hm);
				allCategories.add(strapiTestCategory);
			}
		}
		return allCategories;
	}
	
	public List<StrapiSubCategory> getSubCategories(Integer categoryId)
	{
		List<StrapiSubCategory> subCategories = new ArrayList<>();
		StrapiResponse strapiResponse = strapiContentService.getSubCategories(categoryId);
		if(strapiResponse != null)
		{
			Entry entry = strapiResponse.getEntry();
			HashMap<String, Object> subCategoriesObj = (HashMap<String, Object>) entry.getAttributes().get("subCategories");
			List<HashMap<String, Object>> subCategoriesList = (List<HashMap<String, Object>>) subCategoriesObj.get("data");
			for (HashMap<String, Object> subCategoryObj : subCategoriesList) 
			{
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", subCategoryObj.get("id"));
				hm.put("attributes", subCategoryObj.get("attributes"));
				StrapiSubCategory strapiTestSubCategory= StrapiUtils.getStrapiSubCategory(hm);
				subCategories.add(strapiTestSubCategory);
			}
			
		}
		return subCategories;
	}
	
	public StrapiResponseArray getByCategoryId(String categoryId)
	{
		StrapiResponseArray strapiResponseArray = null;
		if(!StringUtils.isEmpty(categoryId))
		{
			strapiResponseArray = strapiContentService.getCategoryById(categoryId);
		}
		
		return strapiResponseArray;
	}
	
	public List<StrapiCategory> getById(int id)
	{
		StrapiResponseArray strapiResponseArray = null;
		List<StrapiCategory> allCategories = new ArrayList<>();
		if(id!=0)
		{
			strapiResponseArray = strapiContentService.getCategoryById(id);
			if(strapiResponseArray != null)
			{
				List<Entry> entries = strapiResponseArray.getEntries();
				for (Entry entry : entries) {
					HashMap<String, Object> hm = new HashMap<>();
					hm.put("id", entry.getId());
					hm.put("attributes", entry.getAttributes());
					StrapiCategory strapiTestCategory = StrapiUtils.getStrapiCategory(hm);
					allCategories.add(strapiTestCategory);
				}
			}
		}
		
		return allCategories;
	}
	
	public List<StrapiCategory> getByCategoryName(String categoryName)
	{
		StrapiResponseArray strapiResponseArray = null;
		List<StrapiCategory> allCategories = new ArrayList<>();
		if(!StringUtils.isEmpty(categoryName))
		{
			strapiResponseArray = strapiContentService.getCategoryByName(categoryName);
			if(strapiResponseArray != null)
			{
				List<Entry> entries = strapiResponseArray.getEntries();
				for (Entry entry : entries) {
					HashMap<String, Object> hm = new HashMap<>();
					hm.put("id", entry.getId());
					hm.put("attributes", entry.getAttributes());
					StrapiCategory strapiTestCategory = StrapiUtils.getStrapiCategory(hm);
					allCategories.add(strapiTestCategory);
				}
			}
		}
		return allCategories;
	}
	
	public List<StrapiCategory> getAllCategoriesWithMetaDesc() throws Exception
	{
		StrapiResponseArray strapiResponseArray = null;
		strapiResponseArray = strapiContentService.getAllCategoriesWithMetaDesc();
		List<StrapiCategory> allCategories = new ArrayList<>();
		if(strapiResponseArray != null)
		{
			List<Entry> entries = strapiResponseArray.getEntries();
			for (Entry entry : entries) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", entry.getId());
				hm.put("attributes", entry.getAttributes());
				StrapiCategory strapiTestCategory = StrapiUtils.getStrapiCategory(hm);
				allCategories.add(strapiTestCategory);
			}
		}
		return allCategories;
	}
	
	public List<StrapiCategory> getAllCjCategoriesWithMetaDesc() throws Exception
	{
		StrapiResponseArray strapiResponseArray = null;
		strapiResponseArray = strapiContentService.getAllCjCategoriesWithMetaDesc();
		List<StrapiCategory> allCategories = new ArrayList<>();
		if(strapiResponseArray != null)
		{
			List<Entry> entries = strapiResponseArray.getEntries();
			for (Entry entry : entries) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", entry.getId());
				hm.put("attributes", entry.getAttributes());
				StrapiCategory strapiTestCategory = StrapiUtils.getStrapiCategory(hm);
				allCategories.add(strapiTestCategory);
			}
		}
		return allCategories;
	}
}
