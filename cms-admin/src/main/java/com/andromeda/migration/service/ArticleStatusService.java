package com.andromeda.migration.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.migration.dao.ArticleStatusDao;
import com.andromeda.migration.model.ArticleStatus;

@Service
public class ArticleStatusService 
{
	@Autowired
	ArticleStatusDao articleStatusDao;
	
	public void setArticleStatusDao(ArticleStatusDao articleStatusDao)
	{
		this.articleStatusDao = articleStatusDao;
	}
	
	public List<ArticleStatus> getAll()
	{
		return articleStatusDao.getAll();
	}
	
	public int add(ArticleStatus articleStatus) 
	{
		return articleStatusDao.add(articleStatus);
	}
	
	public ArticleStatus getById(int id)
	{
		return articleStatusDao.getById(id);
	}
	
}
