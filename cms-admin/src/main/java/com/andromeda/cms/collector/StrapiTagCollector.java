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
import com.andromeda.cms.model.StrapiTag;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.service.StrapiSubCategoryService;
import com.andromeda.cms.service.StrapiTagService;
import com.andromeda.commons.util.FileNDirUtils;

public class StrapiTagCollector {
	private static Logger logger = LoggerFactory.getLogger(StrapiTagCollector.class);
	private static String BASE_DIR;
	private static final String FILE_EXTENSION = "csv";
	private static final int TOTAL_PARTS = 2;
	
	@Autowired
	private StrapiTagService strapiTagService;
	
	public void setStrapiTagService(StrapiTagService strapiTagService)
	{
		this.strapiTagService = strapiTagService;
	}
	
	public void collectTagsFromFile() throws Exception
	{
		FileReader reader=new FileReader("config.properties");  
		Properties p=new Properties();  
	    p.load(reader);  
	    BASE_DIR = p.getProperty("tags.data.dir.path");
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
					logger.info("Importing Tags from " + fileName);
					System.out.println("Importing file: " + completeFileName);
					List<String> lines = FileNDirUtils.getFileContentAsLines(completeFileName);
					if ((!CollectionUtils.isEmpty(lines)) && (lines.size() > 1))
					{
						collectTags(lines);
					}
				}
			}
		}
	}
	
	private void collectTags(List<String> lines) throws Exception 
	{
		for (int i = 1; i < lines.size(); i++)
		{
			String[] tagParts = getParts(lines.get(i));
			StrapiTag strapiTag = getStrapiTag(tagParts);
			//StrapiCreateRequest strapiTestCategory = JsonUtils.deserialize(categoryStr, StrapiCreateRequest.class);
			//HashMap<String, String> strapiTestCategory = JsonUtils.deserialize(categoryStr, HashMap.class);
			StrapiResponse strapiResponse = strapiTagService.addStrapiTag(strapiTag);
		}
		
	}
	
	private StrapiTag getStrapiTag(String[] tagParts) 
	{
		StrapiTag strapiTag = new StrapiTag();
		String name = tagParts[0];
		String tagId = tagParts[1];
		
		if(!StringUtils.isEmpty(name))
		{
			String modifiedTagName = name.toLowerCase().replace(" ", "-");
			strapiTag.setName(modifiedTagName);
		}
			
		if(!StringUtils.isEmpty(tagId))
			strapiTag.setTagId(tagId);

		return strapiTag;
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
}
