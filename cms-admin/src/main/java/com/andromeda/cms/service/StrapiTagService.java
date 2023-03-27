package com.andromeda.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiTag;

@Service
public class StrapiTagService {
	//@Autowired
	private StrapiContentService strapiContentService;
	
	public void setStrapiContentService(StrapiContentService strapiContentService)
	{
		this.strapiContentService = strapiContentService;
	}
	
	public StrapiResponse addStrapiTag(StrapiTag strapiTag) throws Exception
	{
		return strapiContentService.postTag(strapiTag);
	}
}
