package com.andromeda.cms.admin.service;

import java.util.HashMap;
import java.util.List;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.commons.util.JsonUtils;


public class StrapiArticleServiceTest {
	
	public static void main(String args[]) throws Exception
	{
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		StrapiContentService strapiContentService = TestContextUtils.getStrapiContentService();
		//String responseJson = strapiContentService.authenticate();
		//HashMap<String, String> responseHm =  JsonUtils.deserialize(responseJson, HashMap.class);
		//String token = responseHm.get("jwt");
		//strapiContentService.setJwtToken(token);
		String url = "http://3.108.187.218:1337/api/articles/6?populate[1]=articleTextEditor&populate[2]=articleTextEditor.contentImage&populate[3]=articleTextEditor.articles&populate[4]=primaryImage&populate[5]=primaryCategory&populate[6]=secondaryCategories&populate[0]=PhotoGallery&populate[7]=storyGeographicLocation&populate[8]=primarySubCategory&populate[9]=secondarySubCategories";
		StrapiArticle sr = strapiArticleService.getArticle(url);
		System.out.println(sr);
	}
	
	

}
