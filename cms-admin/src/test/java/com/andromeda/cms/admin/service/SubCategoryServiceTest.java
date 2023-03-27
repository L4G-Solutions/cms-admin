package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiSubCategoryService;
import com.andromeda.cms.service.SubCategoryService;
import com.andromeda.cms.translator.StrapiTranslator;

public class SubCategoryServiceTest 
{
	public static void main(String args[]) throws Exception
	{
		SubCategoryServiceTest s = new SubCategoryServiceTest();
		s.addAllStrapiSubCategories();
	}

	private void addAllStrapiSubCategories() throws Exception 
	{
		SubCategoryService sitemapSubCategoryService = TestContextUtils.getSitemapSubCategoryService();
		StrapiSubCategoryService strapiSubCategoryService = TestContextUtils.getStrapiSubCategoryService();
		List<StrapiSubCategory> allStrapiSubCategories = strapiSubCategoryService.getAllSubCategories();
		
		List<SubCategory> sitemapSubCategories = new ArrayList<>();
		
		for (StrapiSubCategory strapiTestSubCategory : allStrapiSubCategories) {
			StrapiCategory strapiCategory = strapiTestSubCategory.getCategory();
			SubCategory sitemapSubCategory =  StrapiTranslator.translateSubCategory(strapiCategory, strapiTestSubCategory);
			sitemapSubCategories.add(sitemapSubCategory);
		}
		
		
		for (SubCategory subCategory : sitemapSubCategories) {
			SubCategory existingSubCategory =  sitemapSubCategoryService.getById(subCategory.getId());
			if(existingSubCategory == null)
			{
				sitemapSubCategoryService.add(subCategory);
			}
			else
			{
				sitemapSubCategoryService.update(subCategory);
			}
		}
		
	}
}
