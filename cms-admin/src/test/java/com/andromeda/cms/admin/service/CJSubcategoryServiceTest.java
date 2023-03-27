package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.service.CJSubCategoryService;
import com.andromeda.cms.service.StrapiSubCategoryService;
import com.andromeda.cms.translator.StrapiCjTranslator;

public class CJSubcategoryServiceTest {
	public static void main(String args[]) throws Exception
	{
		CJSubcategoryServiceTest s = new CJSubcategoryServiceTest();
		s.addAllStrapiCJSubCategories();
	}

	private void addAllStrapiCJSubCategories() throws Exception 
	{
		CJSubCategoryService cjSubcategoryService = TestContextUtils.getCjSubCategoryService();
		StrapiSubCategoryService strapiSubCategoryService = TestContextUtils.getStrapiSubCategoryService();
		List<StrapiSubCategory> allStrapiSubCategories = strapiSubCategoryService.getAllCjSubCategories();
		
		List<CJSubCategory> cjSubCategories = new ArrayList<>();
		
		for (StrapiSubCategory strapiTestSubCategory : allStrapiSubCategories) {
			StrapiCategory strapiCategory = strapiTestSubCategory.getCategory();
			CJSubCategory sitemapSubCategory =  StrapiCjTranslator.translateSubCategory(strapiCategory, strapiTestSubCategory);
			cjSubCategories.add(sitemapSubCategory);
		}
		

		
		for (CJSubCategory subCategory : cjSubCategories) {
			CJSubCategory existingSubCategory =  cjSubcategoryService.getById(subCategory.getId());
			if(existingSubCategory == null)
			{
				cjSubcategoryService.add(subCategory);
			}
			else
			{
				cjSubcategoryService.update(subCategory);
			}
		}
		
	}
}
