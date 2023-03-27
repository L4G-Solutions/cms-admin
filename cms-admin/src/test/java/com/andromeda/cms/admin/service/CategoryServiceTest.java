package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.translator.StrapiTranslator;

public class CategoryServiceTest 
{
	public static void main(String args[]) throws Exception
	{
		CategoryServiceTest s = new CategoryServiceTest();
		s.addAllStrapiCategories();
	}

	private void addAllStrapiCategories() throws Exception 
	{
		CategoryService sitemapCategoryService = TestContextUtils.getSitemapCategoryService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		List<StrapiCategory> allStrapiCategories = strapiCategoryService.getAllCategoriesWithMetaDesc();
		
		List<Category> sitemapCategories = new ArrayList<>();
		
		for (StrapiCategory strapiTestCategory : allStrapiCategories) {
			Category sitemapCategory =  StrapiTranslator.translateCategory(strapiTestCategory);
			sitemapCategories.add(sitemapCategory);
		}
		
		sitemapCategoryService.addBatch(sitemapCategories);
		
		
	}
}
