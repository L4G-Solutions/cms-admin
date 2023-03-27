package com.andromeda.cms.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.andromeda.cms.collector.ATStoryCollector;
import com.andromeda.cms.model.ATStory;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiTestArticle;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.translator.ModelTranslator;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class CollectorTest
{

	public static void main(String args[]) throws Exception
	{
		CollectorTest c = new CollectorTest();
		c.collectData();
	}

	public void collectData() throws Exception
	{
		collectATStories(); // Done
	}

	private void collectATStories() throws Exception
	{
		ATStoryCollector asc = TestContextUtils.getATStoryCollector();
		ModelTranslator modelTranslator = TestContextUtils.getModelTranslator();
		StrapiContentService strapiContentService = TestContextUtils.getStrapiContentService();

		List<ATStory> storyList = asc.collectData();
		List<StrapiTestArticle> strapiArticleList = new ArrayList<StrapiTestArticle>();
		for (ATStory atStory : storyList)
		{
			StrapiTestArticle sp = modelTranslator.translateAtStoryToStarpiArticle(atStory);
			//StrapiResponseArray sr = strapiContentService.postArticle(sp);
			strapiArticleList.add(sp);
		}
	}

}
