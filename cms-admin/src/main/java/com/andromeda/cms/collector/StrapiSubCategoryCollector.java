package com.andromeda.cms.collector;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiSubCategoryService;
import com.andromeda.commons.util.FileNDirUtils;

public class StrapiSubCategoryCollector {
	private static Logger logger = LoggerFactory.getLogger(StrapiCategoryCollector.class);
	private static String BASE_DIR;
	private static final String FILE_EXTENSION = "csv";
	private static final int TOTAL_PARTS = 3;
	
	@Autowired
	private StrapiSubCategoryService strapiSubCategoryService;
	
	@Autowired
	private StrapiCategoryService strapiCategoryService;
	
	public void setStrapiSubCategoryService(StrapiSubCategoryService strapiSubCategoryService)
	{
		this.strapiSubCategoryService = strapiSubCategoryService;
	}
	
	public void setStrapiCategoryService(StrapiCategoryService strapiCategoryService)
	{
		this.strapiCategoryService = strapiCategoryService;
	}
	
	public void collectSubCategoriesFromFile() throws Exception
	{
		FileReader reader=new FileReader("config.properties");  
		Properties p=new Properties();  
	    p.load(reader);  
	    BASE_DIR = p.getProperty("subcategory.data.dir.path");
		List<String> fileNames = FileNDirUtils.getFileNamesList(BASE_DIR, FILE_EXTENSION);
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
						collectSubCategories(lines);
					}
				}
			}
		}
	}
	
	private void collectSubCategories(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] subCategoryParts = getParts(lines.get(i));
			StrapiSubCategory strapiTestCategory = getStrapiTestSubCategory(subCategoryParts);
			//StrapiCreateRequest strapiTestCategory = JsonUtils.deserialize(categoryStr, StrapiCreateRequest.class);
			//HashMap<String, String> strapiTestCategory = JsonUtils.deserialize(categoryStr, HashMap.class);
			StrapiResponse strapiResponse = strapiSubCategoryService.postSubCategory(strapiTestCategory);
		}
		
	}
	
	private StrapiSubCategory getStrapiTestSubCategory(String[] categoryParts) {
		int strapiParentId = 0;
		
		StrapiSubCategory strapiTestSubCategory = new StrapiSubCategory();
		String categoryName = categoryParts[0];
		String categoryId = categoryParts[1];
		String parentCategoryId = categoryParts[2];
		String description = categoryParts[3];
		String metaTitle = categoryParts[4];
		String keywords = categoryParts[5];
		String teluguLabel = categoryParts[6];
		
		if(!StringUtils.isEmpty(categoryId))
			strapiTestSubCategory.setSubCategoryId(categoryId);
		if(!StringUtils.isEmpty(categoryName))
		{
			strapiTestSubCategory.setName(categoryName);
			strapiTestSubCategory.setSeoSlug(categoryName.trim().toLowerCase().replace(" ", "-"));
		}
					
		if(!StringUtils.isEmpty(parentCategoryId))
		{
			StrapiResponseArray strapiResponseArray  = strapiCategoryService.getByCategoryId(parentCategoryId);
			List<Entry> entries = strapiResponseArray.getEntries();
			if(entries.size() > 0)
			{
				Entry entry = entries.get(0);
				if(entry.getId() != null)
				{
					strapiParentId = entry.getId();
					StrapiCategory sc= StrapiUtils.getStrapiCategory(entry.getAttributes());
					if(sc.getId() == 0)
						sc.setId(strapiParentId);
					strapiTestSubCategory.setCategory(sc);
				}
			}
				
		}
		
		strapiTestSubCategory.setDescription(description);
		strapiTestSubCategory.setMetaTitle(metaTitle);
		strapiTestSubCategory.setKeywords(keywords);
		strapiTestSubCategory.setTeluguLabel(teluguLabel);
		return strapiTestSubCategory;
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


	public void updateCategoriesFromFile() throws Exception 
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
						
						//List<String> lines = FileNDirUtils.getFileContentAsLines(completeFileName);
						List<String> lines = StrapiUtils.getLinesFromExcel(completeFileName);
						if ((!CollectionUtils.isEmpty(lines)) && (lines.size() > 1))
						{
							updateSubCategories(lines);
						}
					}
				}
			}
		
	}
	
	public void addCategoriesFromExcelFile() throws Exception 
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
					
					//List<String> lines = FileNDirUtils.getFileContentAsLines(completeFileName);
					List<String> lines = StrapiUtils.getLinesFromExcel(completeFileName);
					if ((!CollectionUtils.isEmpty(lines)) && (lines.size() > 1))
					{
						addSubCategories(lines);
					}
				}
			}
		}
	
}

	private void updateSubCategories(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] categoryParts = getParts(lines.get(i));
			StrapiSubCategory strapiTestSubCategory = getStrapiTestSubCategory(categoryParts);
			strapiSubCategoryService.updateSubCategory(strapiTestSubCategory);
		}
		
	}
	
	private void addSubCategories(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] categoryParts = getParts(lines.get(i));
			StrapiSubCategory strapiTestSubCategory = getStrapiTestSubCategory(categoryParts);
			strapiTestSubCategory.setId(i);
			strapiSubCategoryService.postSubCategory(strapiTestSubCategory);
		}
		
	}
}
