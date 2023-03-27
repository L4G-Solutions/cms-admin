package com.andromeda.cms.translator;

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

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.ArticleTextEditor;
import com.andromeda.cms.model.CJArticle;
import com.andromeda.cms.model.CJCategory;
import com.andromeda.cms.model.CJLiveBlog;
import com.andromeda.cms.model.CJPhotoGallery;
import com.andromeda.cms.model.CJSubCategory;
import com.andromeda.cms.model.DocumentUpload;
import com.andromeda.cms.model.ImageWithDescription;
import com.andromeda.cms.model.PhotoBulkUpload;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiImage;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiTag;
import com.andromeda.cms.service.CJArticleService;
import com.andromeda.commons.util.JsonUtils;


public class StrapiCjTranslator {

		@Autowired
		private CJArticleService cjarticleService;
		
		public void setArticleService(CJArticleService cjas)
		{
			cjarticleService = cjas;
		}
		
				
		/**
		 * Translates StrapiPhotoGallery(nested) to PhotoGallery 
		 * @param strapiPhotoGallery
		 * @return
		 */
		public static CJPhotoGallery translatePhotoGallery(StrapiPhotoGallery strapiPhotoGallery) 
		{
			CJPhotoGallery photoGallery = new CJPhotoGallery();
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
		
		public static CJLiveBlog translateLiveBlog(StrapiArticle strapiArticle)
		{
			CJLiveBlog liveBlog = new CJLiveBlog();
			HashMap<String, String> photosHm = new HashMap<>();
			LinkedHashMap<String, String> videosHm = new LinkedHashMap<>();
			
			liveBlog.setId(strapiArticle.getId());
			liveBlog.setAbnStoryId(strapiArticle.getAbnStoryId());
			liveBlog.setPublished(strapiArticle.getPublished());
			liveBlog.setSource(strapiArticle.getSource());
			String contentType = strapiArticle.getContentType();
			liveBlog.setContentType(strapiArticle.getContentType());
			List<ArticleTextEditor> articleTextEditors = strapiArticle.getArticleTextEditors();
			//String articleText = "";
			//String ampArticleText = "";
			Set<String> ampHeaders = new HashSet<>();
			
			List<ArticleTextEditor> ateList = new ArrayList<>();

			
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
						
					}
					if(ampArticleTEditorText != null)
					{
						ampArticleTEditorText = ampArticleTEditorText.replaceAll("(<img[^>]+>)", "<amp-img  width=\"1.33\" height=\"1\" layout=\"responsive\" $1</amp-img>")
								.replaceAll("<img ", "")
								.replaceAll("</img>", "");
						
						
					}
					
					if(duUrl != null || videoUrl != null)
					{
						
						if(duUrl != null )
						{
							articleTEditorText = articleTEditorText + "<br> <embed src=\""+ du.getUrl() +"\" type=\"application/pdf\" width=\"100%\" height=\"500px\">"+du.getCaption()+"</embed>"
									+  "<br/>" ;
							
							ampArticleTEditorText = ampArticleTEditorText + "<amp-google-document-embed"
									+ "  src=\" " + du.getUrl() + "\""
									+ "  width=\"100%\""
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
											articleTEditorText = articleTEditorText + "<iframe width=\"100%\" height=\"415\" "
													+ "src=\" "+ videoUrl + "\" "
													+ "title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; "
													+ "autoplay; clipboard-write; encrypted-media; gyroscope; "
													+ "picture-in-picture\" allowfullscreen loading=\"lazy\"></iframe>";
											
											ampArticleTEditorText = ampArticleTEditorText + "<amp-youtube"
													+ "  data-videoid=\"" + videoId + "\""
													+ "  layout=\"responsive\""
													+ "  width=\"480\""
													+ "  height=\"270\""
													+ "></amp-youtube>";
											ampHeaders.add("amp-youtube");
											break;
							
																		
								case "Facebook" : articleTEditorText = articleTEditorText + " <iframe width=\"100%\" height=\"615\" "
																		+ "	src=\" " + videoUrl + "\" "
																		+ "	style=\"border:none;overflow:hidden\" scrolling=\"no\" "
																		+ "	frameborder=\"0\" allowfullscreen=\"true\" allow=\"autoplay; "
																		+ "	clipboard-write; encrypted-media; picture-in-picture; "
																		+ "	web-share\" allowFullScreen=\"true\" loading=\"lazy\"></iframe>";
								
								ampArticleTEditorText = ampArticleTEditorText + "<amp-facebook width=\"552\" height=\"310\""
																		+ "    layout=\"responsive\""
																		+ "    data-href= \"" + videoUrl + "\">" 
																		+ "</amp-facebook>";
													ampHeaders.add("amp-facebook");
													break;
								
								
								
								  case "Twitter" : articleTEditorText = articleTEditorText + "<iframe width=\"100%\" height='500px' "
								  								+ "src='https://twitframe.com/show?url=" +  videoUrl +"' "
								  								+ "frameborder='0' allow='autoplay' allowfullscreen='true' "
								  								+ "scrolling='yes' style='width:100%; display: block;' loading=\"lazy\"></iframe>";
												  
								  					ampArticleTEditorText = ampArticleTEditorText + "<amp-twitter"
													  		+ "  width=\"375\""
													  		+ "  height=\"472\""
													  		+ "  layout=\"responsive\""
													  		+ "  data-tweetid=\""+ videoId +"\">"
													  		+ "</amp-twitter>";
								  					ampHeaders.add("amp-twitter");
								  					break;
										  								
								  
								  case "Instagram" : articleTEditorText = articleTEditorText + "<iframe width=\"100%\" height='500px' "
																  		+ "	src='"+ videoUrl + "' "
																  		+ "frameborder='0' allow='autoplay; encrypted-media' allowfullscreen='true' "
																  		+ "style='width:100%; display: block;' loading=\"lazy\"></iframe>";
								  										
								  					ampArticleTEditorText = ampArticleTEditorText + "<amp-instagram"
													  		+ "  data-shortcode=\"" + videoId + "\""
													  		+ "  data-captioned"
													  		+ "  width=\"400\""
													  		+ "  height=\"400\""
													  		+ "  layout=\"responsive\">"
													  		+ "</amp-instagram>";
			  										ampHeaders.add("amp-instagram");
													break;
								 	
								  case "Vimeo" : articleTEditorText = articleTEditorText + "<iframe width=\"100%\" src=\" " + videoUrl + "\" "
																		+ "height=\"415\" frameborder=\"0\" "
																		+ "allow=\"autoplay; fullscreen; picture-in-picture\" "
																		+ "allowfullscreen loading=\"lazy\"></iframe>";
																		break;
																		
								  case "Dailymotion" : articleTEditorText = articleTEditorText + "<iframe width=\"100%\"  src=\" " + videoUrl + "\" "
																		+ "height=\"415\" frameborder=\"0\" "
																		+ "allow=\"autoplay; fullscreen; picture-in-picture\" "
																		+ "allowfullscreen frameborder='0' loading=\"lazy\"></iframe>";
								  						ampArticleTEditorText = ampArticleTEditorText + "<amp-dailymotion"
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

					articleTextEditor.setArticleText(articleTEditorText);
					articleTextEditor.setAmpArticleText(ampArticleTEditorText);
					ateList.add(articleTextEditor);
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
			
			liveBlog.setLocale(strapiArticle.getLocale());
			liveBlog.setRelatedArticles(relatedArticleIdsList);
			liveBlog.setVideos(JsonUtils.toString(videosHm));
			liveBlog.setPhotos(JsonUtils.toString(photosHm));
			liveBlog.setArticleText(JsonUtils.toString(ateList));
			//liveBlog.setAmpArticleText(ampArticleText);
			liveBlog.setAmpHeaders(ampHeaders);
			if(strapiArticle.getAuthor() == null || strapiArticle.getAuthor().isEmpty())
				liveBlog.setAuthor("ABN");
			else
				liveBlog.setAuthor(strapiArticle.getAuthor());
			String englishHeadline = strapiArticle.getEnglishTitle();
			liveBlog.setEnglishTitle(englishHeadline);
			liveBlog.setHeadline(strapiArticle.getHeadline());
			liveBlog.setSeoSlug(strapiArticle.getSeoSlug().trim());
			
			if(strapiArticle.getImageUrl() == null || strapiArticle.getImageUrl().isEmpty())
			{
				liveBlog.setImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + "/images/defaultImg.jpeg");
			}
			else
			{
				liveBlog.setImageUrl(strapiArticle.getImageUrl());
			}
			
			liveBlog.setImageCaption(strapiArticle.getImageCaption());
			liveBlog.setImageMediumUrl(strapiArticle.getMediumPrimaryImageUrl());
			liveBlog.setImageSmallUrl(strapiArticle.getSmallPrimaryImageUrl());
			liveBlog.setImageThumbUrl(strapiArticle.getThumbnailPrimaryImageUrl());
			liveBlog.setImageWidth(strapiArticle.getImageWidth());
			liveBlog.setImageHeight(strapiArticle.getImageHeight());
			liveBlog.setMetaTitle(strapiArticle.getMetaTitle());
			liveBlog.setNewsKeywords(strapiArticle.getNewsKeywords());
			
			StrapiCategory primaryCategory = strapiArticle.getPrimaryCategory();
			liveBlog.setPrimaryCategoryId(primaryCategory.getId());
			liveBlog.setPrimaryCategoryName(primaryCategory.getName());
			liveBlog.setPrimaryCategorySeoSlug(primaryCategory.getSeoSlug());
			liveBlog.setPrimaryCategoryTeluguLabel(primaryCategory.getTeluguLabel());
			String pcUrl = getCategoryUrl(primaryCategory);
			liveBlog.setPrimaryCategoryUrl(pcUrl);
			
			
			StrapiSubCategory subCategory = strapiArticle.getPrimarySubCategory();
			boolean contains = checkCategoryContainsSubCategory(primaryCategory, subCategory);
			if(contains)
			{
				liveBlog.setPrimarySubCategoryId(subCategory.getId());
				liveBlog.setPrimarySubCategoryName(subCategory.getName());
				liveBlog.setPrimarySubCategorySeoSlug(subCategory.getSeoSlug());
				liveBlog.setPrimarySubCategoryTeluguLabel(subCategory.getTeluguLabel());
				String pscUrl = getSubCategoryUrl(primaryCategory, subCategory);
				liveBlog.setPrimarySubCategoryUrl(pscUrl);
			}
			
		
			liveBlog.setPublishedAt(strapiArticle.getPublishedAt());
			liveBlog.setPublishedAtSm(strapiArticle.getPublishedAtSm());
			Timestamp publishedTs = strapiArticle.getPublishedAt();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(publishedTs.getTime());
			int year = cal.get(Calendar.YEAR);
			liveBlog.setPublishedYear(year);
			liveBlog.setCreatedAt(strapiArticle.getCreatedAt());
			liveBlog.setUpdatedAt(strapiArticle.getUpdatedAt());
			liveBlog.setUpdatedAtSm(strapiArticle.getUpdatedAtSm());
			
			liveBlog.setHideAuthorName(strapiArticle.isSpeedNews());
			liveBlog.setShortHeadline(strapiArticle.getShortHeadline());
			liveBlog.setSummary(strapiArticle.getSummary());
			
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
					
				liveBlog.setTags(tagsList);
				liveBlog.setTagUrls(tagUrlsList);
			}
			else
			{
				tagsList = "Telugu News";
				tagUrlsList = "/tag/" + tagsList.trim().toLowerCase().replace(" ", "-");
				liveBlog.setTags(tagsList);
				liveBlog.setTagUrls(tagUrlsList);
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
				liveBlog.setUrl(url);
			if(ampUrl != null)
				liveBlog.setAmpUrl(ampUrl);
			
			liveBlog.setPrioritiseInLatestNews(strapiArticle.isPrioritiseInLatestNews());
			liveBlog.setPrioritiseInPrimarySection(strapiArticle.isPrioritiseInPrimarySection());
			liveBlog.setDisplayModifiedDate(strapiArticle.isDisplayModifiedDate());
			if(liveBlog.isDisplayModifiedDate() == null)
			{
				liveBlog.setDisplayModifiedDate(false);
				
			}
			return liveBlog;
		}
		
		/**
		 * 
		 * @param strapiArticle
		 * @return
		 */
		
		public static CJArticle translateArticle(StrapiArticle strapiArticle)
		{
			CJArticle article = new CJArticle();
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
						/*articleText = articleText + "<br> <embed src=\""+ du.getUrl() +"\" type=\"application/pdf\" width=\"100%\" height=\"100%\">"+du.getCaption()+"</embed>"
													+  "<br/>" ;*/
						articleText = articleText + "<iframe src=\"" + du.getUrl()   + "#view=fit\"> "+ du.getCaption()  + "</iframe>";

						
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
							
																		
								case "Facebook" : articleText = articleText + " <iframe width=\"100%\" height=\"615\" "
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
								
								
								
								  case "Twitter" : articleText = articleText + "<iframe width=\"100%\" height='500px' "
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
										  								
								  
								  case "Instagram" : articleText = articleText + "<iframe width=\"100%\" height='500px' "
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
																		
								  case "Dailymotion" : articleText = articleText + "<iframe width=\"100%\"  src=\" " + videoUrl + "\" "
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
				article.setImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + "/images/defaultImg.jpeg");
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
			
			CJCategory c = translateCjCategory(primaryCategory);
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
		
		
		
		public static CJCategory translateCjCategory(StrapiCategory strapiCategory)
		{
			CJCategory category = new CJCategory();
			
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
		public static CJSubCategory translateSubCategory(StrapiCategory strapiCategory, StrapiSubCategory strapiSubCategory)
		{
			CJSubCategory subCategory = new CJSubCategory();
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
			
			if(strapiCategory != null && strapiSubCategory != null)
				url = "/" + strapiCategory.getSeoSlug() + "/" + strapiSubCategory.getSeoSlug();
			return url;
		}
		
		public static String getSubCategoryMUrl( StrapiCategory strapiCategory, StrapiSubCategory strapiSubCategory) {
			String url = null;
			//StrapiCategory primaryCategory = strapiSubCategory.getCategory();
			
			if(strapiCategory != null && strapiSubCategory != null)
				url =  "/" + strapiCategory.getSeoSlug() + "/" + strapiSubCategory.getSeoSlug();
			return url;
		}

}
