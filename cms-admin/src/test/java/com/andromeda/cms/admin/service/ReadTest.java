package com.andromeda.cms.admin.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.andromeda.cms.model.Feed;
import com.andromeda.cms.model.FeedMessage;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.RSSFeedParser;
import com.andromeda.commons.util.JsonUtils;
import com.lowagie.text.DocumentException;

public class ReadTest
{
	public static void main(String[] args) throws Exception
	{
		DataGeneratorService c = new DataGeneratorService();
		c.configureFreemarker();
		
		RSSFeedParser parser =
				new RSSFeedParser("https://rss.andhrajyothy.com/news/Telangana?SupId=0&SubId=44");
		Feed feed = parser.readFeed();
		for (FeedMessage message : feed.getMessages())
		{
			System.out.println(message);
		}
		c.generateHtmlforFeed(feed);

	}
}
