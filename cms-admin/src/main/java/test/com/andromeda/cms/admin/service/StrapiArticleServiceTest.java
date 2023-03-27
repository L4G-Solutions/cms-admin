package test.com.andromeda.cms.admin.service;

import java.util.HashMap;
import java.util.List;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.commons.util.JsonUtils;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class StrapiArticleServiceTest {
	
	public static void main(String args[]) throws Exception
	{
		StrapiArticleService strapiArticleService = TestContextUtils.getStrapiArticleService();
		StrapiArticle sr = strapiArticleService.getArticle("http://3.108.187.218:1337/api/articles/5?populate[1]=articleTextEditor&populate[2]=articleTextEditor.contentImage&populate[3]=articleTextEditor.articles&populate[5]=primaryImage&populate[6]=primaryCategory&populate[7]=secondaryCategories&populate[0]=PhotoGallery&populate[8]=storyGeographicLocation&populate[9]=primarySubCategory&populate[10]=secondarySubCategories&populate[4]=articleTextEditor.articles.contentImage&populate[11]=tags");
				List<StrapiArticle> latestArticles =  strapiArticleService.getLatestByPrimarySubCategory(6);
		//StrapiArticle sr = strapiArticleService.getPost("http://3.108.187.218:1337/api/articles/5?populate=secondaryCategories");		
		System.out.println(latestArticles);
	}
	
	

}
