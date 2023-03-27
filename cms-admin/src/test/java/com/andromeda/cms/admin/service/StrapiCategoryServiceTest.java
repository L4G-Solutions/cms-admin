package com.andromeda.cms.admin.service;

import java.util.List;

import com.andromeda.cms.collector.StrapiCategoryCollector;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.service.StrapiCategoryService;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class StrapiCategoryServiceTest
{
	public static void main(String args[]) throws Exception
	{
		StrapiCategoryServiceTest strapiCategoryServiceTest = new StrapiCategoryServiceTest();
		//strapiCategoryServiceTest.collectCategories();
		//strapiCategoryServiceTest.getCategories();
		strapiCategoryServiceTest.getSubCategoriesByCategoryId(5);
	}

	private void getCategories() throws Exception 
	{
		StrapiCategoryService strapiCategoryService = com.andromeda.cms.admin.util.TestContextUtils.getStrapiCategoryService();
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		System.out.println(categoryList);
	}
	
	private void getSubCategoriesByCategoryId(Integer categoryId) throws Exception
	{
		StrapiCategoryService strapiCategoryService = com.andromeda.cms.admin.util.TestContextUtils.getStrapiCategoryService();
		List<StrapiSubCategory> subCategories =  strapiCategoryService.getSubCategories(categoryId);
		System.out.println(subCategories);
	}

	private void collectCategories() throws Exception 
	{
		StrapiCategoryCollector strapiCategoryCollector = TestContextUtils.getStrapiCategoryCollector();
		strapiCategoryCollector.collectCategoriesFromFile();
		
	}
}
