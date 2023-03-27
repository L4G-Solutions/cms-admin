package test.com.andromeda.cms.admin.service;

import com.andromeda.cms.collector.StrapiCategoryCollector;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class StrapiCategoryCollectorTest
{
	public static void main(String args[]) throws Exception
	{
		StrapiCategoryCollectorTest strapiCategoryCollectorTest = new StrapiCategoryCollectorTest();
		strapiCategoryCollectorTest.addCategoriesFromExcel();
	}

	private void collectCategories() throws Exception 
	{
		StrapiCategoryCollector strapiCategoryCollector = TestContextUtils.getStrapiCategoryCollector();
		strapiCategoryCollector.collectCategoriesFromFile();
	}
	
	private void addCategoriesFromExcel() throws Exception
	{
		StrapiCategoryCollector strapiCategoryCollector = TestContextUtils.getStrapiCategoryCollector();
		strapiCategoryCollector.addCategoriesFromFile();
	}
}
