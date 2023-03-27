package com.andromeda.cms.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class DataWriter
{
	
	public String writeFeedInHtml(Configuration c, Object feed)
	{
		if (feed != null)
		{
			try
			{
				Template template = c.getTemplate("feedTemplate.ftl");
				String htmlFile = "output" + File.separator +  "feedOutput.html";
				createFile(htmlFile);
				Writer fileWriter = new FileWriter(htmlFile);
				try
				{
					template.process(feed, fileWriter);
				}
				finally
				{
					fileWriter.close();
				}
				return htmlFile;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}



	private static void createFile(String htmlFile)
	{
		File htmlFileName = new File(htmlFile);
		if (!htmlFileName.exists())
			try
			{
				htmlFileName.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
}
