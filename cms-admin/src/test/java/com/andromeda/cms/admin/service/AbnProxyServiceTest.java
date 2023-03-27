package com.andromeda.cms.admin.service;

import com.andromeda.cms.model.Feed;
import com.andromeda.cms.model.FeedMessage;
import com.andromeda.cms.service.AbnRssReadService;

public class AbnProxyServiceTest
{

	public static void main(String[] args)
	{
		AbnRssReadService arrs = new AbnRssReadService();
		arrs.setBaseUrl("https://rss.andhrajyothy.com/news/Telangana?SupId=0&SubId=44");
		String feedStr = arrs.getRssFeeds();
		Feed feed = arrs.readFeed(feedStr);
		for (FeedMessage message : feed.getMessages())
		{
			System.out.println(message);
		}

	}

}
