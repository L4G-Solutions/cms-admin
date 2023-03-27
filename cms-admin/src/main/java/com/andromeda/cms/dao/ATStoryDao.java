package com.andromeda.cms.dao;

import java.util.List;

import com.andromeda.cms.model.ATStory;
import com.andromeda.commons.dao.BaseDAO;

public class ATStoryDao extends BaseDAO
{
	public List<ATStory> getAll()
	{
		return sqlSessionTemplate.selectList("com.andromeda.cms.proxy.model.ATStory.GetAll");
	}

	public ATStory getById(int storyId)
	{
		return sqlSessionTemplate.selectOne("com.andromeda.cms.proxy.model.ATStory.GetById", storyId);
	}
}
