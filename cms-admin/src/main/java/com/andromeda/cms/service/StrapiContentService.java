package com.andromeda.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.stereotype.Service;

import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.StrapiCreateRequest;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.model.StrapiResponse.StrapiMetadata;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiTag;
import com.andromeda.cms.model.StrapiTestArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.commons.util.JsonUtils;
import com.andromeda.commons.util.RestClient;
import com.andromeda.commons.util.StringUtils;

import test.com.andromeda.cms.admin.util.TestUtils;

@Service
public class StrapiContentService
{

	private String baseUrl;
	private RestClient restClient;
	private static String jwtToken;

	@PostConstruct
	public void init() throws Exception
	{
		restClient = new RestClient();
		//restClient.init();
		authenticate();
		restClient.init(jwtToken);
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}
	
	public void setJwtToken(String jwtToken)
	{
		this.jwtToken = jwtToken;
	}
	
	public void authenticate() throws Exception
	{
		String credentials = TestUtils.getStrapiCredentials();
		//String responseJson = restClient.post(StrapiConstants.STRAPI_AUTH_URL, credentials, String.class);
		//HashMap<String, String> responseHm =  JsonUtils.deserialize(responseJson, HashMap.class);
		jwtToken = credentials;
	}

	public StrapiResponseArray getArticles(String url)
	{
		String postContent = null;
		StrapiResponseArray strapiResponse = null;
		try
		{
			postContent = restClient.get(url);
			strapiResponse = JsonUtils.deserialize(postContent, StrapiResponseArray.class);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return strapiResponse;
	}
	
	public StrapiResponse getArticle(String url)
	{
		String articleContent = null;
		StrapiResponse strapiResponse = null;
		try
		{
			articleContent = restClient.get(url);
			strapiResponse = JsonUtils.deserialize(articleContent, StrapiResponse.class);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return strapiResponse;
	}
	
	public StrapiResponseArray getLatestArticles()
	{
		String articles = null;
		StrapiResponseArray strapiResponseArray = null;
		try
		{
			articles = restClient.get(StrapiConstants.STRAPI_LATEST_ARTICLE_URL);
			strapiResponseArray = JsonUtils.deserialize(articles, StrapiResponseArray.class);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return strapiResponseArray;
	}

	/*public StrapiResponse postArticle(StrapiArticle strapiArticle)
	{
		restClient.post(baseUrl, strapiArticle, StrapiResponse.class);
	}*/
	
	public StrapiResponse postCategory(StrapiCategory strapiTestCategory) throws Exception
	{
		StrapiResponse strapiResponse = null;
		if(strapiTestCategory != null)
		{
			StrapiCreateRequest strapiCreateRequest = new StrapiCreateRequest();
			strapiCreateRequest.setData(strapiTestCategory);
			String strapiCreateRequestStr = JsonUtils.toString(strapiCreateRequest);
			String strapiResponseStr = restClient.post(StrapiConstants.STRAPI_CATEGORY_URL, strapiCreateRequestStr, String.class);
			strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponse.class);
		}
		return strapiResponse;
	}
	
	
	public StrapiResponseArray getCategoryById(String categoryId)
	{
		StrapiResponseArray strapiResponseArray = null;
		if(!StringUtils.isEmpty(categoryId))
		{
			String url = String.format(StrapiConstants.STRAPI_CATEGORY_FILTER_URL, "categoryId", categoryId);
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return strapiResponseArray;
	}
	
	public StrapiResponseArray getCategoryById(int id)
	{
		StrapiResponseArray strapiResponseArray = null;
		if(id!= 0)
		{
			String url = String.format(StrapiConstants.STRAPI_CATEGORY_FILTER_URL, "id", id);
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return strapiResponseArray;
	}
	
		
	public StrapiResponseArray getCategoryByName(String categoryName)
	{
		StrapiResponseArray strapiResponseArray = null;
		if(!StringUtils.isEmpty(categoryName))
		{
			String url = String.format(StrapiConstants.STRAPI_CATEGORY_FILTER_URL, "name", categoryName);
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return strapiResponseArray;
	}

	public StrapiResponse postSubCategory(StrapiSubCategory strapiTestSubCategory) throws Exception 
	{
		StrapiResponse strapiResponse = null;
		if(strapiTestSubCategory != null)
		{
			
			StrapiCreateRequest strapiCreateRequest = new StrapiCreateRequest();
			strapiCreateRequest.setData(strapiTestSubCategory);
			String strapiCreateRequestStr = JsonUtils.toString(strapiCreateRequest);
			String strapiResponseStr = restClient.post(StrapiConstants.STRAPI_SUBCATEGORY_URL, strapiCreateRequestStr, String.class);
			strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponse.class);
		}
		return strapiResponse;
	}
	
	public StrapiResponse postTag(StrapiTag strapiTag) throws Exception
	{
		StrapiResponse strapiResponse = null;
		if(strapiTag != null)
		{
			StrapiCreateRequest strapiCreateRequest = new StrapiCreateRequest();
			strapiCreateRequest.setData(strapiTag);
			String strapiCreateRequestStr = JsonUtils.toString(strapiCreateRequest);
			String strapiResponseStr = restClient.post(StrapiConstants.STRAPI_TAG_URL, strapiCreateRequestStr, String.class);
			strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponse.class);
		}
		
		return strapiResponse;
	}

	public StrapiResponseArray getAllCategories() 
	{
		StrapiResponseArray strapiResponseArray = null;
		String url = StrapiConstants.STRAPI_DETAILED_CATEGORY_URL;
		try 
		{
			String strapiResponseStr = restClient.get(url);
			strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return strapiResponseArray;
	}
	
	public StrapiResponseArray getAllSubCategories() 
	{
		StrapiResponseArray strapiResponseArray = null;
		List<Entry> responseEntries = new ArrayList<>();
		String url = String.format(StrapiConstants.STRAPI_DETAILED_SUBCATEGORY_URL, 0);
		try 
		{
			String strapiResponseStr = restClient.get(url);
			strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
		return strapiResponseArray;
	}
	
	public StrapiResponseArray getAllCJSubCategories() 
	{
		StrapiResponseArray strapiResponseArray = null;
		List<Entry> responseEntries = new ArrayList<>();
		String url = String.format(StrapiConstants.STRAPI_DETAILED_CJ_SUBCATEGORY_URL, 0);
		try 
		{
			String strapiResponseStr = restClient.get(url);
			strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
		return strapiResponseArray;
	}

	public StrapiResponse getSubCategories(Integer categoryId) 
	{
		StrapiResponse strapiResponse = null;
		String url = String.format(StrapiConstants.STRAPI_SUBCATEGORY_BY_CATEGORY_URL, categoryId);
		if(categoryId != null)
		{
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponse.class);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return strapiResponse;
	}
	
	public StrapiResponse getSubCategoryById(Integer subCategoryId) 
	{
		StrapiResponse strapiResponse = null;
		String url = String.format(StrapiConstants.STRAPI_SUBCATEGORY_BY_ID_URL, subCategoryId);
		if(subCategoryId != null)
		{
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponse.class);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return strapiResponse;
	}

	public StrapiResponseArray getLatestByPrimarySubCategory(Integer primarySubCategoryId) 
	{
		StrapiResponseArray strapiResponse = null;
		String url = String.format(StrapiConstants.STRAPI_PRIMARY_SUB_CATEGORY_RELATED_ARTICLE_URL, primarySubCategoryId);
		if(primarySubCategoryId != null)
		{
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return strapiResponse;
	}
	
	public StrapiResponseArray getLatestByPrimaryCategory(String primaryCategoryId) 
	{
		StrapiResponseArray strapiResponse = null;
		String url = String.format(StrapiConstants.STRAPI_PRIMARY_CATEGORY_RELATED_ARTICLE_URL, primaryCategoryId);
		if(primaryCategoryId != null)
		{
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return strapiResponse;
	}

	public StrapiResponseArray getRelatedArticles(Integer articleId) {
		StrapiResponseArray strapiResponse = null;
		String url = String.format(StrapiConstants.STRAPI_RELATED_ARTICLE_URL, articleId);
		if(articleId != null)
		{
			String strapiResponseStr = restClient.get(url);
			try {
				strapiResponse = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return strapiResponse;
	}

	public void updateSubCategory(StrapiSubCategory strapiTestSubCategory) throws Exception 
	{
		StrapiResponse strapiResponse = null;
		if(strapiTestSubCategory != null)
		{
			StrapiCreateRequest strapiCreateRequest = new StrapiCreateRequest();
			strapiCreateRequest.setData(strapiTestSubCategory);
			String strapiCreateRequestStr = JsonUtils.toString(strapiCreateRequest);
			String strapiSubCategoryFindByIdUrl = String.format(StrapiConstants.STRAPI_SUBCATEGORY_FILTER_URL, "subCategoryId" , strapiTestSubCategory.getSubCategoryId());
			String responseStr = restClient.get(strapiSubCategoryFindByIdUrl);
			StrapiResponseArray strapiCategoryObj = JsonUtils.deserialize(responseStr, StrapiResponseArray.class);
			if(strapiCategoryObj.getEntries().size()>0)
			{
				int strapiCategoryId = strapiCategoryObj.getEntries().get(0).getId();
				String strapiUpdateUrl = String.format(StrapiConstants.STRAPI_SUBCATEGORY_UPDATE_URL, strapiCategoryId);
				restClient.put(strapiUpdateUrl, strapiCreateRequestStr);
			}
			
		}

	}

	public StrapiResponseArray getAllCategoriesWithMetaDesc() {
		StrapiResponseArray strapiResponseArray = null;
		String url = StrapiConstants.STRAPI_DETAILED_CATEGORY_URL;
		try 
		{
			String strapiResponseStr = restClient.get(url);
			strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return strapiResponseArray;
	}
	
	public StrapiResponseArray getAllCjCategoriesWithMetaDesc() {
		StrapiResponseArray strapiResponseArray = null;
		String url = StrapiConstants.STRAPI_DETAILED_CJ_CATEGORY_URL;
		try 
		{
			String strapiResponseStr = restClient.get(url);
			strapiResponseArray = JsonUtils.deserialize(strapiResponseStr, StrapiResponseArray.class);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return strapiResponseArray;
	}
	
	
}
