package com.andromeda.cms.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.andromeda.cms.admin.util.StrapiCjUtils;
import com.andromeda.cms.admin.util.StrapiUtils;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.ElectionVote;
import com.andromeda.cms.model.HomePageAd;
import com.andromeda.cms.model.RankingDashboard;
import com.andromeda.cms.model.ElectionVote;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCartoon;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiHoroscope;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiWebhookEvent;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiSubCategoryService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.commons.util.HttpUtils;
import com.andromeda.commons.util.JsonUtils;

@RestController
public class StrapiWebhooksController
{
	 private static final Logger logger  = LogManager.getLogger(StrapiWebhooksController.class.getName());

	@Autowired
	StrapiWebhooksService strapiWebhooksService;
	
	@Autowired
	StrapiArticleService strapiArticleService;
	
	@Autowired
	StrapiCategoryService strapiCategoryService;
	
	@Autowired
	StrapiSubCategoryService strapiSubCategoryService;
	

	@ResponseBody
	@RequestMapping(value = "/cms-admin/api", method = { RequestMethod.POST })
	public void onStrapiArticleEvent(@RequestBody String strapiEventStr, HttpServletRequest request) throws Exception
	{
		logger.debug("Webhook event " + strapiEventStr);
		System.out.println("StrapiWebhookEvent " + strapiEventStr);
		StrapiWebhookEvent strapiWebhookEvent =
				JsonUtils.deserialize(strapiEventStr, StrapiWebhookEvent.class);
		Timestamp eventCreatedAt = strapiWebhookEvent.getCreatedAt();
		String event = strapiWebhookEvent.getEvent();
		String model = strapiWebhookEvent.getModel();
		HashMap<String, Object> attrs = strapiWebhookEvent.getEntry();
		String source = HttpUtils.getClientAddress(request);
	
		if(event.equalsIgnoreCase(StrapiConstants.STRAPI_EVENT_PUBLISH))
		{
			Boolean published = true;
			if(model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_ARTICLE) || model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_MAINARTICLE))
			{
				StrapiArticle strapiArticle = StrapiUtils.getStrapiArticle(attrs);
				strapiArticle.setSource(source);
				strapiArticle.setPublished(published);
				if (strapiArticle.getPrimaryCategory() != null) {
					strapiWebhooksService.onArticleCreate(strapiArticle);
				}
				strapiWebhooksService.generateHomePage();
			}

			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_PHOTOGALLERY)) {
				StrapiPhotoGallery strapiPhotoGallery = StrapiUtils.getStrapiPhotoGallery(attrs);
				strapiPhotoGallery.setSource(source);
				strapiPhotoGallery.setPublished(published);
				strapiPhotoGallery.setSource(source);

				strapiWebhooksService.onPhotoCreate(strapiPhotoGallery);

				strapiWebhooksService.generateHomePage();
			}
			
			else if(model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_HOROSCOPE))
			{
				StrapiHoroscope strapiHoroscope = StrapiUtils.getStrapiHoroscope(strapiWebhookEvent);
				strapiHoroscope.setSource(source);
				strapiHoroscope.setPublished(published);
				strapiWebhooksService.onHoroscopeCreate(strapiHoroscope);

				strapiWebhooksService.generateHomePage();
			}
			
			else if(model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CARTOON))
			{
				StrapiCartoon strapiCartoon = StrapiUtils.getStrapiCartoon(strapiWebhookEvent);
				strapiCartoon.setSource(source);
				strapiCartoon.setPublished(published);
				List<StrapiCategory> strapiCategories = strapiCategoryService
						.getByCategoryName(StrapiConstants.STRAPI_CATEGORY_CARTOON);
				if (strapiCategories != null && strapiCategories.size() > 0)
					strapiCartoon.setCategory(strapiCategories.get(0));
				strapiWebhooksService.onCartoonCreate(strapiCartoon);

				strapiWebhooksService.generateHomePage();
			}

			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_HOMEPAGEAD)) {
				HomePageAd homePageAd = StrapiUtils.getHomepageAd(strapiWebhookEvent);
				homePageAd.setPublished(true);
				strapiWebhooksService.generateHomepadeAd(homePageAd);
			}

			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CJ_ARTICLE)) {
				StrapiArticle strapiArticle = StrapiCjUtils.getStrapiCjArticle(attrs);
				strapiArticle.setSource(source);
				strapiArticle.setPublished(true);
				if (strapiArticle.getPrimaryCategory() != null) {
					strapiWebhooksService.onCjArticleCreate(strapiArticle);
				}
				strapiWebhooksService.generateCjHomePage();
			}

			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CJ_PHOTOGALLERY)) {
				StrapiPhotoGallery strapiPhotoGallery = StrapiCjUtils.getStrapiCjPhotoGallery(attrs);
				strapiPhotoGallery.setSource(source);
				strapiPhotoGallery.setPublished(true);
				strapiPhotoGallery.setSource(source);

				strapiWebhooksService.onCjPhotoCreate(strapiPhotoGallery);

				strapiWebhooksService.generateCjHomePage();
			} 
			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CJ_LIVEBLOG)) {
				StrapiArticle strapiArticle = StrapiCjUtils.getStrapiCjArticle(attrs);
				strapiArticle.setSource(source);
				strapiArticle.setPublished(true);
				if (strapiArticle.getPrimaryCategory() != null) {
					strapiWebhooksService.onCjLiveBlogCreate(strapiArticle);
				}
				strapiWebhooksService.generateCjHomePage();
			}
			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_MEGHALAYA_ELECTION_VOTE)
					|| model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_NAGALAND_ELECTION_VOTE)
					|| model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_TRIPURA_ELECTION_VOTE)) 
			{
				ElectionVote electionVote = StrapiUtils.getElectionVote(strapiWebhookEvent);
				electionVote.setPublished(true);
				strapiWebhooksService.generateElectionVoteJson(electionVote, model);
				strapiWebhooksService.generateHomePage();
			}
			else if(model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_RANKING_DASHBOARD))
			{
				RankingDashboard rankingDashboard = StrapiUtils.getRankingDashboard(strapiWebhookEvent);
				strapiWebhooksService.saveRankingDashboardItems(rankingDashboard);
				strapiWebhooksService.generateHomePage();
				strapiWebhooksService.generateCjHomePage();
			}
		} else if (event.equalsIgnoreCase(StrapiConstants.STRAPI_EVENT_UNPUBLISH)
				|| event.equalsIgnoreCase(StrapiConstants.STRAPI_EVENT_DELETE)) {
			boolean deleteFlag = event.equalsIgnoreCase(StrapiConstants.STRAPI_EVENT_DELETE);
			if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_ARTICLE)) {
				System.out.println("Article UnPublished !!");
				StrapiArticle strapiArticle = StrapiUtils.getStrapiArticle(strapiWebhookEvent);
				strapiWebhooksService.onArticleUnPublish(strapiArticle, deleteFlag);

				strapiWebhooksService.generateHomePage();
			} else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_PHOTOGALLERY)) {
				System.out.println("Photo UnPublished !!");
				StrapiPhotoGallery strapiPhotoGallery = StrapiUtils
						.getStrapiPhotoGallery(strapiWebhookEvent.getEntry());
				strapiWebhooksService.onPhotoUnpublish(strapiPhotoGallery, deleteFlag);

				strapiWebhooksService.generateHomePage();
			} else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_HOROSCOPE)) {
				System.out.println("Horoscope UnPublished !!");
				StrapiHoroscope strapiHoroscope = StrapiUtils.getStrapiHoroscope(strapiWebhookEvent);
				strapiWebhooksService.onHoroscopeUnpublish(strapiHoroscope, deleteFlag);

				strapiWebhooksService.generateHomePage();
			} else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CARTOON)) {
				System.out.println("Cartoon UnPublished !!");
				StrapiCartoon strapiCartoon = StrapiUtils.getStrapiCartoon(strapiWebhookEvent);
				strapiWebhooksService.onCartoonUnpublish(strapiCartoon, deleteFlag);

				strapiWebhooksService.generateHomePage();
			} else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_HOMEPAGEAD)) {
				System.out.println("Homepage Ad UnPublished !!");
				HomePageAd homePageAd = StrapiUtils.getHomepageAd(strapiWebhookEvent);
				homePageAd.setPublished(false);
				strapiWebhooksService.generateHomepadeAd(homePageAd);

				strapiWebhooksService.generateHomePage();
			}

			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CJ_ARTICLE)) {
				System.out.println("CJ Article UnPublished !!");
				StrapiArticle strapiArticle = StrapiUtils.getStrapiArticle(strapiWebhookEvent);
				strapiWebhooksService.onCjArticleUnPublish(strapiArticle, deleteFlag);

				strapiWebhooksService.generateCjHomePage();
			}else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CJ_LIVEBLOG)) {
				System.out.println("CJ LiveBlog UnPublished !!");
				StrapiArticle strapiArticle = StrapiUtils.getStrapiArticle(strapiWebhookEvent);
				strapiWebhooksService.onCjLiveBlogUnPublish(strapiArticle, deleteFlag);

				strapiWebhooksService.generateCjHomePage();
			} 
			else if (model.equalsIgnoreCase(StrapiConstants.STRAPI_MODEL_CJ_PHOTOGALLERY)) {
				System.out.println("Photo UnPublished !!");
				StrapiPhotoGallery strapiPhotoGallery = StrapiUtils
						.getStrapiPhotoGallery(strapiWebhookEvent.getEntry());
				strapiWebhooksService.onPhotoUnpublish(strapiPhotoGallery, deleteFlag);

				strapiWebhooksService.generateCjHomePage();
			}
		}

	}

	@ResponseBody
	@RequestMapping(value = "/cms/admin/article_created_for_test", method = { RequestMethod.POST })
	public void onStrapiArticleEventTest() throws Exception {
		String url = "http://3.108.187.218:1337/api/articles/5?populate[1]=articleTextEditor&populate[2]=articleTextEditor.contentImage&populate[3]=articleTextEditor.articles&populate[4]=primaryImage&populate[5]=primaryCategory&populate[6]=secondaryCategories&populate[0]=PhotoGallery&populate[7]=storyGeographicLocation&populate[8]=primarySubCategory&populate[9]=secondarySubCategories";
		StrapiArticle strapiArticle = strapiArticleService.getArticle(url);
		strapiWebhooksService.onArticleCreate(strapiArticle);
	}

	@ResponseBody
	@RequestMapping(value = "/api/to be called/on create", method = { RequestMethod.POST })
	public void onPostCreate() throws Exception {
		// strapiWebhooksService.onPostCreate();
	}

	@ResponseBody
	@RequestMapping(value = "/api/to be called/on update", method = { RequestMethod.POST })
	public void onPostUpdate() throws Exception {
		// strapiWebhooksService.onPostUpdate();
	}

	@ResponseBody
	@RequestMapping(value = "/api/to be called/on publish", method = { RequestMethod.POST })
	public void onPostPublish() throws Exception {
		// strapiWebhooksService.onPostPublish();
	}

}
