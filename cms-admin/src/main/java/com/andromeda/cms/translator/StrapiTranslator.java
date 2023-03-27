package com.andromeda.cms.translator;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.ArticleTextEditor;
import com.andromeda.cms.model.Cartoon;
import com.andromeda.cms.model.Category;
import com.andromeda.cms.model.DocumentUpload;
import com.andromeda.cms.model.Horoscope;
import com.andromeda.cms.model.ImageWithDescription;
import com.andromeda.cms.model.PhotoBulkUpload;
import com.andromeda.cms.model.PhotoGallery;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCartoon;
import com.andromeda.cms.model.StrapiHoroscope;
import com.andromeda.cms.model.StrapiImage;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiTag;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.commons.util.JsonUtils;

@Service
public class StrapiTranslator 
{
	@Value("${domain.name}")
	private static String domainName;
	
	@Autowired
	private ArticleService articleService;
	
	public void setArticleService(ArticleService as)
	{
		articleService = as;
	}
	
	public static Cartoon translateCartoon(StrapiCartoon strapiCartoon)
	{
		Cartoon cartoon = new Cartoon();
		StrapiImage image = strapiCartoon.getImage();
		
		cartoon.setId(strapiCartoon.getId());
		cartoon.setCartoonId(strapiCartoon.getCartoonId());
		cartoon.setPublished(strapiCartoon.getPublished());
		cartoon.setCreatedAt(strapiCartoon.getCreatedAt());
		cartoon.setUpdatedAt(strapiCartoon.getUpdatedAt());
		cartoon.setUpdatedAtSm(strapiCartoon.getUpdatedAtSm());
		cartoon.setPublishedAt(strapiCartoon.getPublishedAt());
		cartoon.setPublishedAtSm(strapiCartoon.getPublishedAtSm());
		cartoon.setEnglishTitle(strapiCartoon.getEnglishTitle());
		cartoon.setNewsKeywords(strapiCartoon.getNewsKeywords());
		
		if(image != null)
		{
			cartoon.setImageCaption(image.getCaption());
			cartoon.setImageUrl(image.getUrl());
			cartoon.setImageHeight(image.getHeight());
			cartoon.setImageWidth(image.getWidth());
			cartoon.setImageAlternativeText(image.getAlternativeText());
		}
		
		StrapiCategory primaryCategory = strapiCartoon.getCategory();
		cartoon.setPrimaryCategoryId(primaryCategory.getId());
		cartoon.setPrimaryCategoryName(primaryCategory.getName());
		cartoon.setPrimaryCategorySeoSlug(primaryCategory.getSeoSlug());
		cartoon.setPrimaryCategoryTeluguLabel(primaryCategory.getTeluguLabel());
		String pcUrl = getCategoryUrl(primaryCategory);
		cartoon.setPrimaryCategoryUrl(pcUrl);
		
		if(strapiCartoon.getAbnStoryId() != null && !strapiCartoon.getAbnStoryId().isEmpty())
		{
			String abnStoryId = strapiCartoon.getAbnStoryId();
			cartoon.setAbnStoryId(abnStoryId);
			cartoon.setImageUrl(strapiCartoon.getImageURL());
		}
		
		String tagsList = "";
		List<StrapiTag> strapiTags = strapiCartoon.getTags();
		if(strapiTags != null)
		{
			for (StrapiTag strapiTag : strapiTags) {
				tagsList  = tagsList + strapiTag.getName() +","; 
			}
			if(tagsList.length() > 0)
				tagsList = tagsList.substring(0, tagsList.length()-1);
			else
				tagsList = "Telugu News";
			cartoon.setTags(tagsList);
		}
		else
			cartoon.setTags("Telugu News");

		return cartoon;
	}
	
	public static Horoscope translateHoroscope(StrapiHoroscope strapiHoroscope)
	{
		Horoscope horoscope = new Horoscope();
		
	 
		horoscope.setId(strapiHoroscope.getId());
		horoscope.setCreatedAt(strapiHoroscope.getCreatedAt());
		horoscope.setEnglishTitle(strapiHoroscope.getEnglishTitle());
		horoscope.setHoroscopeId(strapiHoroscope.getHoroscopeId());
		horoscope.setHoroscopeType(strapiHoroscope.getHoroscopeType());
		horoscope.setNewsKeywords(strapiHoroscope.getKeywords());
		horoscope.setPublishedAt(strapiHoroscope.getPublishedAt());
		horoscope.setPublishedAtSm(strapiHoroscope.getPublishedAtSm());
		horoscope.setTitle(strapiHoroscope.getTitle());
		horoscope.setUpdatedAt(strapiHoroscope.getUpdatedAt());
		horoscope.setUpdatedAtSm(strapiHoroscope.getUpdatedAtSm());
		horoscope.setStartDate(strapiHoroscope.getStartDate());
		horoscope.setEndDate(strapiHoroscope.getEndDate());
		horoscope.setLocale(strapiHoroscope.getLocale());
		horoscope.setSource(strapiHoroscope.getSource());
		horoscope.setPublished(strapiHoroscope.getPublished());
		
		horoscope.setMeshamAries(strapiHoroscope.getMeshamAries());
		horoscope.setVrushabamTaurus(strapiHoroscope.getVrushabamTaurus());
		horoscope.setMithunamGemini(strapiHoroscope.getMithunamGemini());
		horoscope.setKarkatakamCancer(strapiHoroscope.getKarkatakamCancer());
		horoscope.setSimhaLeo(strapiHoroscope.getSimhaLeo());
		horoscope.setKanyaVirgo(strapiHoroscope.getKanyaVirgo());
		horoscope.setTulaLibra(strapiHoroscope.getTulaLibra());
		horoscope.setVruschikamScorpio(strapiHoroscope.getVruschikamScorpio());
		horoscope.setDhanassuSagittarius(strapiHoroscope.getDhanassuSagittarius());
		horoscope.setMakaramCapricorn(strapiHoroscope.getMakaramCapricorn());
		horoscope.setKumbhamAquarius(strapiHoroscope.getKumbhamAquarius());
		horoscope.setMeenamPisces(strapiHoroscope.getMeenamPisces());
		
		horoscope.setPanchangam(strapiHoroscope.getPanchangam());
		
		StrapiCategory primaryCategory = strapiHoroscope.getCategory();
		horoscope.setPrimaryCategoryId(primaryCategory.getId());
		horoscope.setPrimaryCategoryName(primaryCategory.getName());
		horoscope.setPrimaryCategorySeoSlug(primaryCategory.getSeoSlug());
		horoscope.setPrimaryCategoryTeluguLabel(primaryCategory.getTeluguLabel());
		String pcUrl = getCategoryUrl(primaryCategory);
		horoscope.setPrimaryCategoryUrl(pcUrl);
		
		String tagsList = "";
		List<StrapiTag> strapiTags = strapiHoroscope.getTags();
		if(strapiTags != null)
		{
			for (StrapiTag strapiTag : strapiTags) {
				tagsList  = tagsList + strapiTag.getName() +","; 
			}
			if(tagsList.length() > 0)
				tagsList = tagsList.substring(0, tagsList.length()-1);
			else
				tagsList = "Telugu News";
			horoscope.setTags(tagsList);
		}
		else
			horoscope.setTags("Telugu News");

		return horoscope;
	}
	
	/**
	 * Translates StrapiPhotoGallery(nested) to PhotoGallery 
	 * @param strapiPhotoGallery
	 * @return
	 */
	public static PhotoGallery translatePhotoGallery(StrapiPhotoGallery strapiPhotoGallery) 
	{
		PhotoGallery photoGallery = new PhotoGallery();
		photoGallery.setId(strapiPhotoGallery.getId());
		photoGallery.setAbnStoryId(strapiPhotoGallery.getAbnStoryId());
		photoGallery.setPublished(strapiPhotoGallery.getPublished());
		photoGallery.setPublishedAtSm(strapiPhotoGallery.getPublishedAtSm());
		photoGallery.setSource(strapiPhotoGallery.getSource());
		photoGallery.setHeadline(strapiPhotoGallery.getHeadline());
		photoGallery.setEnglishTitle(strapiPhotoGallery.getEnglishTitle());
		photoGallery.setContentType(strapiPhotoGallery.getContentType());
		photoGallery.setPrioritiseInPhotoLanding(strapiPhotoGallery.getPrioritiseInPhotoLanding());
		
		photoGallery.setImageCaption(strapiPhotoGallery.getImageCaption());
		photoGallery.setThumbnailPrimaryImageUrl(strapiPhotoGallery.getThumbnailPrimaryImageUrl());
		photoGallery.setImageHeight(strapiPhotoGallery.getImageHeight());
		photoGallery.setImageWidth(strapiPhotoGallery.getImageWidth());
		photoGallery.setImageUrl(strapiPhotoGallery.getImageUrl());
		
		PhotoBulkUpload photoBulkUpload = strapiPhotoGallery.getPhotoBulkUpload();
		List<HashMap<String, String>> bulkImagesList = new ArrayList<>();
		
		if(photoBulkUpload != null)
		{
			List<StrapiImage> bulkImages = photoBulkUpload.getPhotoBulkUpload();
			if(bulkImages != null)
			{
				
				for (StrapiImage strapiImage : bulkImages) 
				{
					HashMap<String, String> imagesHm = new HashMap<>();
					String imageCaption = strapiImage.getCaption();
					String imageUrl = strapiImage.getUrl();
					imagesHm.put("caption", imageCaption);
					imagesHm.put("url", imageUrl);
					bulkImagesList.add(imagesHm);
				}
				photoGallery.setBulkImageUpload(JsonUtils.toString(bulkImagesList));
			}
		}
		
		
		List<ImageWithDescription> iwds = strapiPhotoGallery.getImageWithDescription();
		if(iwds != null)
		{
			List<HashMap<String, String>> imageWithDescList = new ArrayList<>();
			for (ImageWithDescription iwd: iwds) 
			{
				HashMap<String, String> imageHm = new HashMap<>();
				String pDesc = iwd.getPhotoDescription();
				imageHm.put("photoDescription", pDesc);
				
				StrapiImage si = iwd.getStrapiImage();
				if(si != null)
				{
					String pCaption = si.getCaption();
					imageHm.put("caption", pCaption);
					String pUrl = si.getUrl();
					imageHm.put("url", pUrl);
				}
				imageWithDescList.add(imageHm);
			}
			photoGallery.setImageWithDescription(JsonUtils.toString(imageWithDescList));
		}
		
		StrapiCategory primaryCategory = strapiPhotoGallery.getCategory();
		photoGallery.setPrimaryCategoryId(primaryCategory.getId());
		photoGallery.setPrimaryCategoryName(primaryCategory.getName());
		photoGallery.setPrimaryCategorySeoSlug(primaryCategory.getSeoSlug());
		photoGallery.setPrimaryCategoryTeluguLabel(primaryCategory.getTeluguLabel());
		String pcUrl = getCategoryUrl(primaryCategory);
		photoGallery.setPrimaryCategoryUrl(pcUrl);
		
		StrapiSubCategory subCategory = strapiPhotoGallery.getSubCategory();
		boolean contains = checkCategoryContainsSubCategory(primaryCategory, subCategory);

		if(contains)
		{
			photoGallery.setPrimarySubCategoryId(subCategory.getId());
			photoGallery.setPrimarySubCategoryName(subCategory.getName());
			photoGallery.setPrimarySubCategorySeoSlug(subCategory.getSeoSlug());
			photoGallery.setPrimarySubCategoryTeluguLabel(subCategory.getTeluguLabel());
			String pscUrl = getSubCategoryUrl(primaryCategory, subCategory);
			photoGallery.setPrimarySubCategoryUrl(pscUrl);
		}
		
		photoGallery.setPublishedAt(strapiPhotoGallery.getPublishedAt());
		Timestamp publishedTs = strapiPhotoGallery.getPublishedAt();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(publishedTs.getTime());
		int year = cal.get(Calendar.YEAR);
		photoGallery.setPublishedYear(year);
		photoGallery.setCreatedAt(strapiPhotoGallery.getCreatedAt());
		photoGallery.setUpdatedAt(strapiPhotoGallery.getUpdatedAt());
		photoGallery.setUpdatedAtSm(strapiPhotoGallery.getUpdatedAtSm());
		photoGallery.setNewsKeywords(strapiPhotoGallery.getNewsKeywords());
		photoGallery.setSeoSlug(strapiPhotoGallery.getSeoSlug());
		photoGallery.setSummary(strapiPhotoGallery.getSummary());
		
		String tagsList = "";
		String tagUrlsList = "";
		
		List<StrapiTag> strapiTags = strapiPhotoGallery.getTags();
		if(strapiTags != null)
		{
			for (StrapiTag strapiTag : strapiTags) {
				tagsList  = tagsList + strapiTag.getName() +","; 
				tagUrlsList = tagUrlsList + strapiTag.getUrl() +",";
			}
			if(tagsList.length() > 0 && tagUrlsList.length() > 0)
				{
				tagsList = tagsList.substring(0, tagsList.length()-1);
				tagUrlsList = tagUrlsList.substring(0, tagUrlsList.length()-1);
				}
			else
			{
				tagsList = "Telugu News";
				tagUrlsList = tagsList.trim().toLowerCase().replace(" ", "-");
			}
				
			photoGallery.setTags(tagsList);
			photoGallery.setTagUrls(tagUrlsList);
		}
		else
			{
			tagsList = "Telugu News";
			tagUrlsList = tagsList.trim().toLowerCase().replace(" ", "-");
			photoGallery.setTags(tagsList);
			photoGallery.setTagUrls(tagUrlsList);
			}
		
		if(strapiPhotoGallery.getAuthor() == null || strapiPhotoGallery.getAuthor().isEmpty())
			photoGallery.setAuthor("ABN");
		else
			photoGallery.setAuthor(strapiPhotoGallery.getAuthor());
		
		String url = null;
		String ampUrl = null;
		//Old migrated articles
		if(strapiPhotoGallery.getAbnStoryId() != null && !strapiPhotoGallery.getAbnStoryId().isEmpty())
		{
			url = "/" + strapiPhotoGallery.getUrl();
			ampUrl = url + "/amp";
		}
		else
		{
			url = getPhotoUrl(strapiPhotoGallery);
			ampUrl = getPhotoAmpUrl(strapiPhotoGallery);
		}
		
		if(url != null)
			photoGallery.setUrl(url);
		if(ampUrl != null)
			photoGallery.setAmpUrl(ampUrl);
		
		photoGallery.setMetaTitle(strapiPhotoGallery.getMetaTitle());
		photoGallery.setMetaDescription(strapiPhotoGallery.getMetaDescription());
		photoGallery.setLocale(strapiPhotoGallery.getLocale());
		photoGallery.setPhotoLocation(JsonUtils.toString(strapiPhotoGallery.getPhotoLocation()));
		
		return photoGallery;
	}
	
	/**
	 * 
	 * @param strapiArticle
	 * @return
	 */
	
	public static Article translateArticle(StrapiArticle strapiArticle)
	{
		Article article = new Article();
		HashMap<String, String> photosHm = new HashMap<>();
		LinkedHashMap<String, String> videosHm = new LinkedHashMap<>();
		
		article.setId(strapiArticle.getId());
		article.setAbnStoryId(strapiArticle.getAbnStoryId());
		article.setPublished(strapiArticle.getPublished());
		article.setSource(strapiArticle.getSource());
		String contentType = strapiArticle.getContentType();
		article.setContentType(strapiArticle.getContentType());
		List<ArticleTextEditor> articleTextEditors = strapiArticle.getArticleTextEditors();
		String articleText = "";
		String ampArticleText = "";
		Set<String> ampHeaders = new HashSet<>();
		
		String relatedArticleIdsList = "";
		if(articleTextEditors != null)
		{
			for (int i=0; i < articleTextEditors.size(); i++) 
			{
				ArticleTextEditor articleTextEditor = articleTextEditors.get(i);
				String videoType = articleTextEditor.getVideoType();
				String videoUrl = articleTextEditor.getVideoUrl();
				String ampVideoUrl = articleTextEditor.getVideoUrl(); // video Url for amp pages
				
				if(videoType != null && videoUrl != null)
				{
					videoUrl = videoUrl.replace("watch?v=", "embed/");
					ampVideoUrl = ampVideoUrl.replace("watch?v=", "embed/");
					videosHm.put(videoType, videoUrl);
				}
				DocumentUpload du =articleTextEditor.getDocumentUpload(); 
				String duUrl = null;
				if(du != null)
					duUrl = du.getUrl();
				
				String articleTEditorText = articleTextEditor.getArticleText();
				String ampArticleTEditorText = articleTextEditor.getArticleText();
				
				if(articleTEditorText != null)
				{
					articleTEditorText = articleTEditorText.replace("<img", "<img class=\"lazy\" loading=\"lazy\"")
							.replace("rel=\"noopener noreferrer nofollow\"", "")
							.replace("target=\"\"", "");
					if(i > 0)
						articleText = articleText +  "<br/>";
					articleText = articleText + articleTEditorText ;
				}
				
				if(ampArticleTEditorText != null)
				{
					ampArticleTEditorText = ampArticleTEditorText.replaceAll("(<img[^>]+>)", "<amp-img  width=\"1.33\" height=\"1\" layout=\"responsive\" $1</amp-img>")
							.replaceAll("<img ", "")
							.replaceAll("</img>", "");
					
					if(i > 0)
						ampArticleText = ampArticleText +  "<br/>";
					ampArticleText = ampArticleText + ampArticleTEditorText ;
				}
				
				if(duUrl != null || videoUrl != null)
				{
					
					if(duUrl != null )
						{
						articleText = articleText + "<br> <embed src=\""+ du.getUrl() +"\" type=\"application/pdf\" width=\"100%\" height=\"500px\">"+du.getCaption()+"</embed>"
													+  "<br/>" ;
						//articleText = articleText + "<iframe src=\"" + du.getUrl()   + "#view=fit\"> "+ du.getCaption()  + "</iframe>";
						
						ampArticleText = ampArticleText + "<amp-google-document-embed"
								+ "  src=\" " + du.getUrl() + "\""
								+ "  width=\"420px\""
								+ "  height=\"500px\""
								+ "  layout=\"responsive\">"
								+ "</amp-google-document-embed>";
						ampHeaders.add("amp-google-document-embed");
						
						}
					if(videoUrl != null && !(contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_VIDEO) && i == 0))
					{
						String videoId = ampVideoUrl.substring(ampVideoUrl.lastIndexOf("/")+1);
						switch (videoType) {
							case "Youtube": 
											videoUrl = videoUrl.replace("watch?v=", "embed/");
											articleText = articleText + "<iframe width=\"100%\" height=\"415\" "
													+ "src=\" "+ videoUrl + "\" "
													+ "title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; "
													+ "autoplay; clipboard-write; encrypted-media; gyroscope; "
													+ "picture-in-picture\" allowfullscreen loading=\"lazy\"></iframe>";
											
											ampArticleText = ampArticleText + "<amp-youtube"
													+ "  data-videoid=\"" + videoId + "\""
													+ "  layout=\"responsive\""
													+ "  width=\"480\""
													+ "  height=\"270\""
													+ "></amp-youtube>";
											ampHeaders.add("amp-youtube");
											break;
						
																	
							case "Facebook" : 
											articleText = articleText + " <iframe width=\"100%\" height=\"615\" "
																	+ "	src=\" " + videoUrl + "\" "
																	+ "	style=\"border:none;overflow:hidden\" scrolling=\"no\" "
																	+ "	frameborder=\"0\" allowfullscreen=\"true\" allow=\"autoplay; "
																	+ "	clipboard-write; encrypted-media; picture-in-picture; "
																	+ "	web-share\" allowFullScreen=\"true\" loading=\"lazy\"></iframe>";
												
											ampArticleText = ampArticleText + "<amp-facebook width=\"552\" height=\"310\""
														+ "    layout=\"responsive\""
														+ "    data-href= \"" + videoUrl + "\">" 
														+ "</amp-facebook>";
											ampHeaders.add("amp-facebook");
											break;
							
								
							  case "Twitter" : 
								  			articleText = articleText + "<iframe width=\"100%\" height='800px' "
							  								+ "src='https://twitframe.com/show?url=" +  videoUrl +"' "
							  								+ "frameborder='0' allow='autoplay' allowfullscreen='true' "
							  								+ "scrolling='yes' style='width:100%; display: block;' loading=\"lazy\"></iframe>";
							  								
											ampArticleText = ampArticleText + "<amp-twitter"
											  		+ "  width=\"375\""
											  		+ "  height=\"472\""
											  		+ "  layout=\"responsive\""
											  		+ "  data-tweetid=\""+ videoId +"\">"
											  		+ "</amp-twitter>";
											ampHeaders.add("amp-twitter");
											break;
			  								
							  
							  case "Instagram" : 
								  			articleText = articleText + "<iframe width=\"100%\" height='500px' "
															  		+ "	src='"+ videoUrl + "' "
															  		+ "frameborder='0' allow='autoplay; encrypted-media' allowfullscreen='true' "
															  		+ "style='width:100%; display: block;' loading=\"lazy\"></iframe>";
							  					
								  			ampArticleText = ampArticleText + "<amp-instagram"
												  		+ "  data-shortcode=\"" + videoId + "\""
												  		+ "  data-captioned"
												  		+ "  width=\"400\""
												  		+ "  height=\"400\""
												  		+ "  layout=\"responsive\">"
												  		+ "</amp-instagram>";
								  			ampHeaders.add("amp-instagram");
											break;

							 	
							  case "Vimeo" : articleText = articleText + "<iframe width=\"100%\" src=\" " + videoUrl + "\" "
																	+ "height=\"415\" frameborder=\"0\" "
																	+ "allow=\"autoplay; fullscreen; picture-in-picture\" "
																	+ "allowfullscreen loading=\"lazy\"></iframe>";
																	break;
																	
							  case "Dailymotion" : 
								  			articleText = articleText + "<iframe width=\"100%\"  src=\" " + videoUrl + "\" "
																	+ "height=\"415\" frameborder=\"0\" "
																	+ "allow=\"autoplay; fullscreen; picture-in-picture\" "
																	+ "allowfullscreen frameborder='0' loading=\"lazy\"></iframe>";
																	
																	
											ampArticleText = ampArticleText + "<amp-dailymotion"
															+ "  data-videoid=\"" + videoId + "\""
															+ "  layout=\"responsive\""
															+ "  width=\"480\""
															+ "  height=\"270\">"
															+ "</amp-dailymotion>";
											ampHeaders.add("amp-dailymotion");
											break;	

							  default: break;
							}
					}
				}
				
			
				List<StrapiArticle> articles = articleTextEditor.getArticles();
				if(articles != null && articles.size() > 0)
				{
					for (StrapiArticle sarticle : articles) 
					{
						
						if(sarticle.getImageCaption()!= null && sarticle.getImageUrl() != null)
						{
							String imageCaption = sarticle.getImageCaption();
							String imageUrl = sarticle.getImageUrl();
							if(imageCaption != null || imageUrl != null)
								photosHm.put(imageCaption, imageUrl);
						}
					}
				}

				
				/*List<StrapiArticle> relatedArticles = articleTextEditor.getArticles();
				if(relatedArticles != null && relatedArticles.size() > 0)
				{
					for (StrapiArticle relatedArticle : relatedArticles) 
					{
						relatedArticleIdsList = relatedArticleIdsList + relatedArticle.getId() + ",";
					}
				}*/
			}
		}

		List<StrapiArticle> relatedArticles = strapiArticle.getArticles();
		if(relatedArticles != null && relatedArticles.size() > 0)
		{
			for (StrapiArticle relatedArticle : relatedArticles) 
			{
				relatedArticleIdsList = relatedArticleIdsList + relatedArticle.getId() + ",";
			}
		}
		
		if(relatedArticleIdsList.length() > 0)
			relatedArticleIdsList = relatedArticleIdsList.substring(0, relatedArticleIdsList.length()-1);
		
		article.setLocale(strapiArticle.getLocale());
		article.setRelatedArticles(relatedArticleIdsList);
		article.setVideos(JsonUtils.toString(videosHm));
		article.setPhotos(JsonUtils.toString(photosHm));
		article.setArticleText(articleText);
		article.setAmpArticleText(ampArticleText);
		article.setAmpHeaders(ampHeaders);
		if(strapiArticle.getAuthor() == null || strapiArticle.getAuthor().isEmpty())
			article.setAuthor("ABN");
		else
			article.setAuthor(strapiArticle.getAuthor());
		String englishHeadline = strapiArticle.getEnglishTitle();
		article.setEnglishTitle(englishHeadline);
		article.setHeadline(strapiArticle.getHeadline());
		article.setSeoSlug(strapiArticle.getSeoSlug().trim());
		
		if(strapiArticle.getImageUrl() == null || strapiArticle.getImageUrl().isEmpty())
		{
			article.setImageUrl(StrapiConstants.RESOURCE_DOMAIN_NAME + "/images/defaultImg.jpeg");
		}
		else
		{
			article.setImageUrl(strapiArticle.getImageUrl());
		}
		
		article.setImageCaption(strapiArticle.getImageCaption());
		article.setImageMediumUrl(strapiArticle.getMediumPrimaryImageUrl());
		article.setImageSmallUrl(strapiArticle.getSmallPrimaryImageUrl());
		article.setImageThumbUrl(strapiArticle.getThumbnailPrimaryImageUrl());
		article.setImageWidth(strapiArticle.getImageWidth());
		article.setImageHeight(strapiArticle.getImageHeight());
		article.setMetaTitle(strapiArticle.getMetaTitle());
		article.setNewsKeywords(strapiArticle.getNewsKeywords());
		
		StrapiCategory primaryCategory = strapiArticle.getPrimaryCategory();
		article.setPrimaryCategoryId(primaryCategory.getId());
		article.setPrimaryCategoryName(primaryCategory.getName());
		article.setPrimaryCategorySeoSlug(primaryCategory.getSeoSlug());
		article.setPrimaryCategoryTeluguLabel(primaryCategory.getTeluguLabel());
		String pcUrl = getCategoryUrl(primaryCategory);
		article.setPrimaryCategoryUrl(pcUrl);
		
		
		StrapiSubCategory subCategory = strapiArticle.getPrimarySubCategory();
		boolean contains = checkCategoryContainsSubCategory(primaryCategory, subCategory);
		if(contains)
		{
			article.setPrimarySubCategoryId(subCategory.getId());
			article.setPrimarySubCategoryName(subCategory.getName());
			article.setPrimarySubCategorySeoSlug(subCategory.getSeoSlug());
			article.setPrimarySubCategoryTeluguLabel(subCategory.getTeluguLabel());
			String pscUrl = getSubCategoryUrl(primaryCategory, subCategory);
			article.setPrimarySubCategoryUrl(pscUrl);
		}
		
		article.setLocale(strapiArticle.getLocale());
		article.setPublishedAt(strapiArticle.getPublishedAt());
		article.setPublishedAtSm(strapiArticle.getPublishedAtSm());
		Timestamp publishedTs = strapiArticle.getPublishedAt();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(publishedTs.getTime());
		int year = cal.get(Calendar.YEAR);
		article.setPublishedYear(year);
		article.setCreatedAt(strapiArticle.getCreatedAt());
		article.setUpdatedAt(strapiArticle.getUpdatedAt());
		article.setUpdatedAtSm(strapiArticle.getUpdatedAtSm());
		
		article.setSpeedNews(strapiArticle.isSpeedNews());
		article.setShortHeadline(strapiArticle.getShortHeadline());
		article.setSummary(strapiArticle.getSummary());
		
		String tagsList = "";
		String tagUrlsList = "";
		List<StrapiTag> strapiTags = strapiArticle.getTags();
		if(strapiTags != null)
		{
			for (StrapiTag strapiTag : strapiTags) {
				tagsList  = tagsList + strapiTag.getName() +","; 
				tagUrlsList = tagUrlsList + strapiTag.getUrl() +",";
			}
			if(tagsList.length() > 0 && tagUrlsList.length() > 0)
				{
				tagsList = tagsList.substring(0, tagsList.length()-1);
				tagUrlsList = tagUrlsList.substring(0, tagUrlsList.length()-1);
				}
			else
				{
				tagsList = "Telugu News";
				tagUrlsList = "/tag/" + tagsList.trim().toLowerCase().replace(" ", "-");
				}
				
			article.setTags(tagsList);
			article.setTagUrls(tagUrlsList);
		}
		else
		{
			tagsList = "Telugu News";
			tagUrlsList = "/tag/" + tagsList.trim().toLowerCase().replace(" ", "-");
			article.setTags(tagsList);
			article.setTagUrls(tagUrlsList);
		}
		
		String url = null;
		String ampUrl = null;
		//Old migrated articles
		if(strapiArticle.getAbnStoryId() != null && !strapiArticle.getAbnStoryId().isEmpty())
		{
			url = "/" + strapiArticle.getUrl();
			ampUrl = url + "/amp";
		}
		else // New articles
		{
		    url = getArticleUrl(strapiArticle);
		    ampUrl = getArticleAmpUrl(strapiArticle);
		}
		if(url != null)
			article.setUrl(url);
		if(ampUrl != null)
			article.setAmpUrl(ampUrl);
		
		article.setPrioritiseInLatestNews(strapiArticle.isPrioritiseInLatestNews());
		article.setPrioritiseInPrimarySection(strapiArticle.isPrioritiseInPrimarySection());
		article.setDisplayModifiedDate(strapiArticle.isDisplayModifiedDate());
		if(article.isDisplayModifiedDate() == null)
		{
			article.setDisplayModifiedDate(false);
			
		}
		return article;
	}
		
	public static String getPhotoUrl(StrapiPhotoGallery strapiPhotoGallery) {
			String url = null;
			StrapiCategory primaryCategory = strapiPhotoGallery.getCategory();
			
			long timestamp = strapiPhotoGallery.getPublishedAt().getTime();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timestamp);
			int year = cal.get(Calendar.YEAR);
			
			StrapiSubCategory primarySubCategory = strapiPhotoGallery.getSubCategory();
			boolean contains = checkCategoryContainsSubCategory(primaryCategory, primarySubCategory);

			if(contains && primaryCategory.getSeoSlug() != null && primarySubCategory.getSeoSlug() != null)
				url = "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + primarySubCategory.getSeoSlug() + "/" + 
						strapiPhotoGallery.getSeoSlug() + "-" + strapiPhotoGallery.getId() +".html";
			else if(primaryCategory.getSeoSlug() != null)
				url = "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + 	strapiPhotoGallery.getSeoSlug() +
						"-" + strapiPhotoGallery.getId() + ".html";
			
			return url;
		}
	
	public static String getPhotoAmpUrl(StrapiPhotoGallery strapiPhotoGallery) {
		String url = null;
		StrapiCategory primaryCategory = strapiPhotoGallery.getCategory();
		
		long timestamp = strapiPhotoGallery.getPublishedAt().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR);
		
		StrapiSubCategory primarySubCategory = strapiPhotoGallery.getSubCategory();
		
		boolean contains = checkCategoryContainsSubCategory(primaryCategory, primarySubCategory);
		
		if(contains && primaryCategory.getSeoSlug() != null && primarySubCategory.getSeoSlug() != null)
			url =  "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + primarySubCategory.getSeoSlug() + "/" + 
					strapiPhotoGallery.getSeoSlug() + "-" + strapiPhotoGallery.getId() +".html/amp";
		else if(primaryCategory.getSeoSlug() != null)
			url =  "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + 	strapiPhotoGallery.getSeoSlug() +
					"-" + strapiPhotoGallery.getId() + ".html/amp";
		
		return url;
	}
	
	public static boolean checkCategoryContainsSubCategory(StrapiCategory primaryCategory, StrapiSubCategory primarySubCategory)
	{
		boolean contains = false;	
		
		Category c = translateCategory(primaryCategory);
		if(c.getSubCategories() != null)
		{
			String subCategoriesStr = c.getSubCategories();
			String[] subCategories = subCategoriesStr.split(",");
			int primarySubCategoryId = primarySubCategory.getId();
			contains = Arrays.asList(subCategories).contains(String.valueOf(primarySubCategoryId));
		}
		
		return contains;
	}
	
	public static String getArticleUrl(StrapiArticle strapiArticle) {
		String url = null;
		StrapiCategory primaryCategory = strapiArticle.getPrimaryCategory();
				
		long timestamp = strapiArticle.getPublishedAt().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR);
		
		boolean contains = false;
		StrapiSubCategory primarySubCategory = strapiArticle.getPrimarySubCategory();
		
		contains = checkCategoryContainsSubCategory(primaryCategory, primarySubCategory);
				
		if(contains && primaryCategory.getSeoSlug() != null && primarySubCategory.getSeoSlug() != null)
			url =  "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + primarySubCategory.getSeoSlug() + "/" + 
					strapiArticle.getSeoSlug() + "-" + strapiArticle.getId() +".html";
		else if(primaryCategory.getSeoSlug() != null)
			url =  "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + 	strapiArticle.getSeoSlug() +
					"-" + strapiArticle.getId() + ".html";
		
		return url;
	}
	
	public static String getArticleAmpUrl(StrapiArticle strapiArticle) {
		String url = null;
		StrapiCategory primaryCategory = strapiArticle.getPrimaryCategory();
		
		long timestamp = strapiArticle.getPublishedAt().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR);
		
		boolean contains = false;
		StrapiSubCategory primarySubCategory = strapiArticle.getPrimarySubCategory();
		
		contains = checkCategoryContainsSubCategory(primaryCategory, primarySubCategory);
		
		if(contains && primaryCategory.getSeoSlug() != null && primarySubCategory.getSeoSlug() != null)
			url =  "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + primarySubCategory.getSeoSlug() + "/" + 
					strapiArticle.getSeoSlug() + "-" + strapiArticle.getId() +".html/amp";
		else if(primaryCategory.getSeoSlug() != null)
			url =  "/" + year + "/" + primaryCategory.getSeoSlug() + "/" + 	strapiArticle.getSeoSlug() +
					"-" + strapiArticle.getId() + ".html/amp";
		
		return url;
	}
	
	
	
	public static Category translateCategory(StrapiCategory strapiCategory)
	{
		Category category = new Category();
		
		category.setId(strapiCategory.getId());
		category.setCategoryId(strapiCategory.getCategoryId());
		category.setDescription(strapiCategory.getDescription());
		category.setKeywords(strapiCategory.getKeywords());
		category.setMetaTitle(strapiCategory.getMetaTitle());
		category.setMetaDescription(strapiCategory.getMetaDescription());
		category.setName(strapiCategory.getName());
		category.setSeoSlug(strapiCategory.getSeoSlug());
		category.setTeluguLabel(strapiCategory.getTeluguLabel());
		
		String subCategoryIds = "";
		List<StrapiSubCategory> subCategories = strapiCategory.getSubCategories();
		if(subCategories != null && !subCategories.isEmpty())
		{
			for (StrapiSubCategory strapiTestSubCategory : subCategories) {
				subCategoryIds = subCategoryIds + strapiTestSubCategory.getId() + ",";
			}
			if(subCategoryIds.length() > 0)
				subCategoryIds = subCategoryIds.substring(0, subCategoryIds.length()-1); // remove the last comma
			category.setSubCategories(subCategoryIds);	
		}
		String url = getCategoryUrl(strapiCategory);
		category.setUrl(url);
		return category;
	}
	
	public static String getCategoryUrl(StrapiCategory strapiTestCategory) 
	{
		String url = null;
		String seoSlug = strapiTestCategory.getSeoSlug();
		
		if(seoSlug != null)
			url =  "/" + seoSlug ;
			
		return url;
	}
	
	public static String getCategoryMUrl(StrapiCategory strapiTestCategory) 
	{
		String url = null;
		String seoSlug = strapiTestCategory.getSeoSlug();
		
		if(seoSlug != null)
			url =  "/" + seoSlug ;
			
		return url;
	}
	public static SubCategory translateSubCategory(StrapiCategory strapiCategory, StrapiSubCategory strapiSubCategory)
	{
		SubCategory subCategory = new SubCategory();
		if(strapiSubCategory != null)
		{
			subCategory.setId(strapiSubCategory.getId());
			subCategory.setSubCategoryId(strapiSubCategory.getSubCategoryId());
			if(strapiSubCategory.getCategory() != null)
			{
				subCategory.setCategoryId(strapiSubCategory.getCategory().getId());
			}
			subCategory.setDescription(strapiSubCategory.getDescription());
			subCategory.setKeywords(strapiSubCategory.getKeywords());
			subCategory.setMetaTitle(strapiSubCategory.getMetaTitle());
			subCategory.setName(strapiSubCategory.getName());
			subCategory.setSeoSlug(strapiSubCategory.getSeoSlug());
			subCategory.setTeluguLabel(strapiSubCategory.getTeluguLabel());
			String url = getSubCategoryUrl(strapiCategory,strapiSubCategory);
			subCategory.setUrl(url);
		}
		return subCategory;
	}
	
	public static String getSubCategoryUrl( StrapiCategory strapiCategory, StrapiSubCategory strapiSubCategory) {
		String url = null;
		//StrapiCategory primaryCategory = strapiSubCategory.getCategory();
		
		if(strapiCategory.getCategoryId() != null && strapiSubCategory.getSubCategoryId() != null)
			url = "/" + strapiCategory.getSeoSlug() + "/" + strapiSubCategory.getSeoSlug();
		return url;
	}
	
	public static String getSubCategoryMUrl( StrapiCategory strapiCategory, StrapiSubCategory strapiSubCategory) {
		String url = null;
		//StrapiCategory primaryCategory = strapiSubCategory.getCategory();
		
		if(strapiCategory.getCategoryId() != null && strapiSubCategory.getSubCategoryId() != null)
			url =  "/" + strapiCategory.getSeoSlug() + "/" + strapiSubCategory.getSeoSlug();
		return url;
	}

	
}
