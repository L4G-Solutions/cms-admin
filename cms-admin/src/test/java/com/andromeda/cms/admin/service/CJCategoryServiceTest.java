package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.CJCategoryService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.translator.StrapiCjTranslator;
import com.andromeda.cms.translator.StrapiTranslator;

public class CJCategoryServiceTest {
	public static void main(String args[]) throws Exception
	{
		CJCategoryServiceTest s = new CJCategoryServiceTest();
		s.addAllStrapiCjCategories();
	}
	
	private void addAllStrapiCjCategories() throws Exception 
	{
		CJCategoryService sitemapCategoryService = TestContextUtils.getCjCategoryService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		List<StrapiCategory> allStrapiCategories = strapiCategoryService.getAllCjCategoriesWithMetaDesc();
		
		List<CJCategory> cjCategories = new ArrayList<>();
		
		for (StrapiCategory strapiTestCategory : allStrapiCategories) {
			CJCategory cjCategory =  StrapiCjTranslator.translateCjCategory(strapiTestCategory);
			cjCategories.add(cjCategory);
		}
		
		sitemapCategoryService.addBatch(cjCategories);
		
		
	}
}
