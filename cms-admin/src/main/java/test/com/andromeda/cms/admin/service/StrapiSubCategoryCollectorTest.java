package test.com.andromeda.cms.admin.service;

import com.andromeda.cms.collector.StrapiCategoryCollector;
import com.andromeda.cms.collector.StrapiSubCategoryCollector;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class StrapiSubCategoryCollectorTest {
	public static void main(String args[]) throws Exception
	{
		StrapiSubCategoryCollectorTest s = new StrapiSubCategoryCollectorTest();
		s.collectSubCategories();
	}

	private void collectSubCategories() throws Exception {
		StrapiSubCategoryCollector strapiCategoryCollector = TestContextUtils.getStrapiSubCategoryCollector();

		strapiCategoryCollector.addCategoriesFromExcelFile();
		
	}
	
	private void updateSubCategories() throws Exception
	{
		StrapiSubCategoryCollector strapiSubCategoryCollector = TestContextUtils.getStrapiSubCategoryCollector();
		strapiSubCategoryCollector.updateCategoriesFromFile();
	}
	
}
