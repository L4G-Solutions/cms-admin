package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiCjUtils;
import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.ArticleTextEditor;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCartoon;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.commons.util.JsonUtils;
import com.andromeda.cms.model.StrapiResponse.Entry;

@Service
public class StrapiArticleService 
{

	//@Autowired
	private StrapiContentService strapiContentService;
	
	public void setStrapiContentService(StrapiContentService strapiContentService)
	{
		this.strapiContentService = strapiContentService;
	}
		
	
	public StrapiResponseArray getArticles(String url) 
	{
		return strapiContentService.getArticles(url);
	}
	
	public StrapiArticle getArticle(String url) 
	{
		StrapiResponse sr = strapiContentService.getArticle(url);
		Entry entry = sr.getEntry();
		StrapiArticle sa = new StrapiArticle();
		System.out.println(JsonUtils.toString(sr));
		sa = StrapiUtils.getStrapiArticle(entry.getAttributes());
		if(sa.getId() == 0)
			sa.setId(entry.getId());
		return sa;
	}
	
	public StrapiArticle getArticleFromCMS(Entry entry) 
	{
		//StrapiResponse sr = strapiContentService.getArticle(url);
		//Entry entry = sr.getEntry();
		StrapiArticle sa = StrapiUtils.getStrapiArticleFromCMS(entry.getAttributes());

		if(sa != null && sa.getId() == 0)
			sa.setId(entry.getId());
		return sa;
	}
	
	public StrapiArticle getCjArticleFromCMS(Entry entry) 
	{
		//StrapiResponse sr = strapiContentService.getArticle(url);
		//Entry entry = sr.getEntry();
		StrapiArticle sa = StrapiCjUtils.getStrapiCjArticleFromCMS(entry.getAttributes());

		if(sa != null && sa.getId() == 0)
			sa.setId(entry.getId());
		return sa;
	}
	
	public StrapiPhotoGallery getPhotoFromCMS(Entry entry) 
	{
		//StrapiResponse sr = strapiContentService.getArticle(url);
		//Entry entry = sr.getEntry();
		StrapiPhotoGallery spg = StrapiUtils.getStrapiPhotoGalleryForCMS(entry.getAttributes());

		if(spg != null && spg.getId() == 0)
			spg.setId(entry.getId());
		return spg;
	}
	
	public StrapiPhotoGallery getCjPhotoFromCMS(Entry entry) 
	{
		//StrapiResponse sr = strapiContentService.getArticle(url);
		//Entry entry = sr.getEntry();
		StrapiPhotoGallery spg = StrapiCjUtils.getStrapiCjPhotoGalleryForCMS(entry.getAttributes());

		if(spg != null && spg.getId() == 0)
			spg.setId(entry.getId());
		return spg;
	}
	
	public StrapiCartoon getCartoonFromCMS(Entry entry) 
	{
		//StrapiResponse sr = strapiContentService.getArticle(url);
		//Entry entry = sr.getEntry();
		StrapiCartoon sc = StrapiUtils.getStrapiCartoonForCMS(entry.getAttributes());

		if(sc != null && sc.getId() == 0)
			sc.setId(entry.getId());
		return sc;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<StrapiArticle> getLatestByPrimaryCategory(String primaryCategoryId)
	{
		List<StrapiArticle> primaryCategoryArticles = new ArrayList<>();
		if(primaryCategoryId != null)
		{
			StrapiResponseArray strapiResponseArray = strapiContentService.getLatestByPrimaryCategory(primaryCategoryId);
			List<Entry> entries = strapiResponseArray.getEntries();
			for (Entry entry : entries) 
			{
				StrapiArticle strapiArticle =  StrapiUtils.getStrapiArticle(entry.getAttributes());
				primaryCategoryArticles.add(strapiArticle);
			}
			
		}
		return primaryCategoryArticles;
	}
	
	
		
	public List<StrapiArticle> getLatestByPrimarySubCategory(Integer primarySubCategoryId)
	{
		List<StrapiArticle> primarySubCategoryArticles = new ArrayList<>();
		if(primarySubCategoryId != null)
		{
			StrapiResponseArray strapiResponseArray = strapiContentService.getLatestByPrimarySubCategory(primarySubCategoryId);
			List<Entry> entries = strapiResponseArray.getEntries();
			for (Entry entry : entries) 
			{
				StrapiArticle strapiArticle =  StrapiUtils.getStrapiArticle(entry.getAttributes());
				primarySubCategoryArticles.add(strapiArticle);
			}
			
		}
		return primarySubCategoryArticles;
	}
	
	public List<StrapiArticle> getRelatedArticles(Integer articleId)
	{
		List<StrapiArticle> relatedArticles = new ArrayList<>();
		if(articleId != null)
		{
			StrapiResponseArray strapiResponseArray = strapiContentService.getRelatedArticles(articleId);
			List<Entry> entries = strapiResponseArray.getEntries();
			for (Entry entry : entries) 
			{
				StrapiArticle strapiArticle =  StrapiUtils.getStrapiArticle(entry.getAttributes());
				List<ArticleTextEditor> articleTextEditors =  strapiArticle.getArticleTextEditors();
				for (ArticleTextEditor articleTextEditor : articleTextEditors) 
				{
					relatedArticles.addAll(articleTextEditor.getArticles());
				}
			}
		}
		return relatedArticles;
	}
}
