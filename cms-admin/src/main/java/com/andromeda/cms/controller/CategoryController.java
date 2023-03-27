package com.andromeda.cms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.service.CategoryService;

@RestController
public class CategoryController 
{
	@Autowired
	CategoryService sitemapCategoryService;
	
	@ResponseBody
	@RequestMapping(value = "/cms/all-categories", method = { RequestMethod.GET })
	public List<Category> getAll()
	{
		return sitemapCategoryService.getAll();
	}
}
