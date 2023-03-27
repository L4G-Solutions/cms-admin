package com.andromeda.cms.admin.service;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiWebhooksService;

public class StrapiWebhooksServiceTest {

	public static void main(String[] args) throws Exception 
	{
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		String url = "http://3.108.187.218:1337/api/articles/5?populate[1]=articleTextEditor&populate[2]=articleTextEditor.contentImage&populate[3]=articleTextEditor.articles&populate[4]=primaryImage&populate[5]=primaryCategory&populate[6]=secondaryCategories&populate[0]=PhotoGallery&populate[7]=storyGeographicLocation&populate[8]=primarySubCategory&populate[9]=secondarySubCategories";
		StrapiArticle sr = strapiArticleService.getArticle(url);
		
		StrapiWebhooksService strapiWebhooksService = TestContextUtils.getStrapiWebhooksService();

	}

}
