package com.andromeda.cms.collector;

import java.util.List;

import com.andromeda.cms.model.ATStory;
import com.andromeda.cms.service.ATStoryService;

public class ATStoryCollector
{

	public ATStoryService aTStoryService;

	public void setATStoryService(ATStoryService aTStoryService)
	{
		this.aTStoryService = aTStoryService;
	}

	public List<ATStory> collectData()
	{
		return aTStoryService.getAll();
	}

}
