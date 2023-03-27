package com.andromeda.cms.admin.service;

import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.StrapiCategoryService;

public class StaticPageTest {

	public static void main(String[] args) throws Exception 
	{
		DataGeneratorService dataGeneratorService = TestContextUtils.getDataGeneratorService();
		StrapiCategoryService strapiCategoryService = TestContextUtils.getStrapiCategoryService();
		
		List<StrapiCategory> categoryList =  strapiCategoryService.getAllCategories();
		String ftlFileName = "404.ftl";
		String htmlFileName = "404.html";
		dataGeneratorService.generateStaticPage(ftlFileName, htmlFileName, categoryList);

	}

}
