package com.andromeda.cms.collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.model.StrapiCreateRequest;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.commons.util.FileNDirUtils;
import com.andromeda.commons.util.JsonUtils;


public class StrapiCategoryCollector 
{
	private static Logger logger = LoggerFactory.getLogger(StrapiCategoryCollector.class);
	private static String BASE_DIR;
	private static final String FILE_EXTENSION = "csv";
	private static final int TOTAL_PARTS = 2;
	
	@Autowired
	private StrapiCategoryService strapiCategoryService;
	
	@Autowired
	private StrapiContentService strapiContentService;
	
	public void setStrapiContentService(StrapiContentService strapiContentService)
	{
		this.strapiContentService = strapiContentService;
	}
	
	public void collectCategoriesFromFile() throws Exception
	{
		FileReader reader=new FileReader("config.properties");  
		Properties p=new Properties();  
	    p.load(reader);  
	    BASE_DIR = p.getProperty("data.dir.path");
		List<String> fileNames = FileNDirUtils.getFileNamesList(BASE_DIR, "xlsx");
		logger.info("fileNames: " + fileNames);
		System.out.println("fileNames: " + fileNames);
		if (!CollectionUtils.isEmpty(fileNames))
		{
			for (String fileName : fileNames)
			{
				String completeFileName = BASE_DIR + "/" + fileName;
				File file = new File(completeFileName);
				if (file.exists())
				{
					logger.info("Importing data from " + fileName);
					System.out.println("Importing file: " + completeFileName);
					List<String> lines = FileNDirUtils.getFileContentAsLines(completeFileName);
					if ((!CollectionUtils.isEmpty(lines)) && (lines.size() > 1))
					{
						collectCategories(lines);
					}
				}
			}
		}
	}

	private void collectCategories(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] categoryParts = getParts(lines.get(i));
			StrapiCategory strapiTestCategory = getStrapiTestCategory(categoryParts);
			StrapiResponse strapiResponse = strapiContentService.postCategory(strapiTestCategory);
		}
		
	}

	private StrapiCategory getStrapiTestCategory(String[] categoryParts) {
		StrapiCategory strapiTestCategory = new StrapiCategory();
		String categoryId = categoryParts[0];
		String categoryName = categoryParts[1];
		String teluguLabel = categoryParts[2];
		
		if(!StringUtils.isEmpty(categoryId))
			strapiTestCategory.setCategoryId(categoryId);
		if(!StringUtils.isEmpty(categoryName))
			strapiTestCategory.setName(categoryName);
		if(!StringUtils.isEmpty(teluguLabel))
			strapiTestCategory.setTeluguLabel(teluguLabel);
		return strapiTestCategory;
	}

	private String[] getParts(String line)
	{
		String[] parts = null;
		if (!StringUtils.isEmpty(line))
		{
			String tempLine = line.trim() + ",#";
			parts = tempLine.split(",");
			if (parts.length < TOTAL_PARTS)
			{
				parts = null;
			}
		}

		return parts;
	}

	public void addCategoriesFromFile() throws Exception 
	{
		FileReader reader=new FileReader("config.properties");  
		Properties p=new Properties();  
	    p.load(reader);  
	    BASE_DIR = p.getProperty("data.dir.path");
		List<String> fileNames = FileNDirUtils.getFileNamesList(BASE_DIR, "xlsx");
		logger.info("fileNames: " + fileNames);
		System.out.println("fileNames: " + fileNames);
		if (!CollectionUtils.isEmpty(fileNames))
		{
			for (String fileName : fileNames)
			{
				String completeFileName = BASE_DIR + "/" + fileName;
				File file = new File(completeFileName);
				/*BufferedReader br;
				br = new BufferedReader(new InputStreamReader(new FileInputStream(
		                completeFileName), "ISO-8859-1"));
		        String line; 
		        while ((line = br.readLine()) != null) 
		        {
		        	PrintStream ps = new PrintStream(System.out, true, "UTF-8");
		        	ps.print(line + "\n");
		        }
		        br.close();*/
				if (file.exists())
				{
					logger.info("Importing data from " + fileName);
					System.out.println("Importing file: " + completeFileName);
					
					//List<String> lines = FileNDirUtils.getFileContentAsLines(completeFileName);
					List<String> lines = StrapiUtils.getLinesFromExcel(completeFileName);
					if ((!CollectionUtils.isEmpty(lines)) && (lines.size() > 1))
					{
						addCategories(lines);
					}
				}
			}
		}
		
	}
	
	private void addCategories(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] categoryParts = getParts(lines.get(i));
			StrapiCategory strapiTestCategory = getStrapiTestCategory(categoryParts);
			strapiContentService.postCategory(strapiTestCategory);
		}
		
	}

	private void updateCategories(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] categoryParts = getParts(lines.get(i));
			StrapiCategory strapiTestCategory = getStrapiTestCategory(categoryParts);
			//strapiContentService.updateCategory(strapiTestCategory);
		}
		
	}


}
