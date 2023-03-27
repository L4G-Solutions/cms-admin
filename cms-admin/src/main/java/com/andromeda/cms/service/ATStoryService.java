package com.andromeda.cms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.andromeda.cms.dao.ATStoryDao;
import com.andromeda.cms.model.ATStory;

@Service
public class ATStoryService
{
	private ATStoryDao aTStoryDao;

	public void setATStoryDao(ATStoryDao aTStoryDao)
	{
		this.aTStoryDao = aTStoryDao;
	}

	public List<ATStory> getAll()
	{
		return aTStoryDao.getAll();
	}

	public ATStory getById(int storyId)
	{
		return aTStoryDao.getById(storyId);
	}
}
