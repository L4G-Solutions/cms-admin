package com.andromeda.cms.admin.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.ArticleTextEditor;
import com.andromeda.cms.model.DocumentUpload;
import com.andromeda.cms.model.ImageWithDescription;
import com.andromeda.cms.model.PhotoBulkUpload;
import com.andromeda.cms.model.StoryGeographicLocation;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiImage;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiTag;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.model.StrapiResponse.StrapiMetadata;

public class StrapiCjUtils {
	
	public static StrapiPhotoGallery getStrapiCjPhotoGalleryForCMS(HashMap<String, Object> attrs) {
		StrapiPhotoGallery strapiPhotoGallery = new StrapiPhotoGallery();

		PhotoBulkUpload photoBulkUpload = null;
		StoryGeographicLocation storyGeographicLocation;
		List<StrapiTag> strapiTags;
		String contentType = null;

		if (attrs.containsKey("id"))
			strapiPhotoGallery.setId((int) attrs.get("id"));

		/*
		 * if(attrs.containsKey("publishedAt")) { String publishedAtStr = (String)
		 * attrs.get("publishedAt"); if(publishedAtStr != null) { Timestamp ts =
		 * convertStringToTsTz(publishedAtStr); strapiPhotoGallery.setPublishedAt(ts);
		 * String publishedAtSm = convertTsTzToIST(publishedAtStr);
		 * strapiPhotoGallery.setPublishedAtSm(publishedAtSm); } }
		 */

		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiPhotoGallery.setCreatedAt(ts);
			strapiPhotoGallery.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiPhotoGallery.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiPhotoGallery.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiPhotoGallery.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("prioritiseInPhotoLanding")) {
			if (attrs.get("prioritiseInPhotoLanding") == null)
				strapiPhotoGallery.setPrioritiseInPhotoLanding(false);
			else
				strapiPhotoGallery.setPrioritiseInPhotoLanding((Boolean) attrs.get("prioritiseInPhotoLanding"));
		}

		if (attrs.containsKey("metaTitle"))
			strapiPhotoGallery.setMetaTitle((String) attrs.get("metaTitle"));
		if (attrs.containsKey("metaDescription"))
			strapiPhotoGallery.setMetaDescription((String) attrs.get("metaDescription"));
		if (attrs.containsKey("headline"))
		{
			String headline = (String) attrs.get("headline");
			strapiPhotoGallery.setHeadline(headline.replace("\"", ""));
		}

		/*
		 * if(attrs.containsKey("englishTitle"))
		 * strapiPhotoGallery.setEnglishTitle((String) attrs.get("englishTitle"));
		 */
		if (attrs.containsKey("englishTitle")) {
			String englishTitle = (String) attrs.get("englishTitle");
			strapiPhotoGallery.setEnglishTitle(englishTitle);
			// generate seoSlug from englishHeadline
			String updatedEnglishTitle = englishTitle.trim().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
			updatedEnglishTitle = updatedEnglishTitle.replaceAll("\\s+", "-");
			strapiPhotoGallery.setSeoSlug(updatedEnglishTitle);
		}

		if (attrs.containsKey("summary"))
			strapiPhotoGallery.setSummary((String) attrs.get("summary"));

		if (attrs.containsKey("newsKeywords"))
			strapiPhotoGallery.setNewsKeywords((String) attrs.get("newsKeywords"));

		/*
		 * if(attrs.containsKey("seoSlug")) strapiPhotoGallery.setSeoSlug((String)
		 * attrs.get("seoSlug"));
		 */

		if (attrs.containsKey("contentType")) {
			contentType = (String) attrs.get("contentType");
			strapiPhotoGallery.setContentType(contentType);
		}

		if (attrs.containsKey("abnStoryId"))
			strapiPhotoGallery.setAbnStoryId((String) attrs.get("abnStoryId"));

		if (attrs.containsKey("location")) {
			storyGeographicLocation = getStoryGeographicLocation(attrs.get("location"));
			if (storyGeographicLocation != null)
				strapiPhotoGallery.setPhotoLocation(storyGeographicLocation);
		}

		if (contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_IMAGE_WITH_DESCRIPTION)
				&& attrs.containsKey("photoWithDescription")) {
			List<ImageWithDescription> iwds = new ArrayList<ImageWithDescription>();

			List<HashMap<String, Object>> imageWithDescriptionHmLs = (List<HashMap<String, Object>>) attrs
					.get("photoWithDescription");

			int id = 0;
			String photoDescription = null;
			StrapiImage si = null;

			for (HashMap<String, Object> imageWithDescriptionHm : imageWithDescriptionHmLs) {
				ImageWithDescription imageWithDescription = new ImageWithDescription();
				if (imageWithDescriptionHm.containsKey("id")) {
					id = (int) imageWithDescriptionHm.get("id");
					imageWithDescription.setId(id);
				}
				if (imageWithDescriptionHm.containsKey("photoDescription")) {
					photoDescription = (String) imageWithDescriptionHm.get("photoDescription");
					imageWithDescription.setPhotoDescription(photoDescription);
				}
				if (imageWithDescriptionHm.containsKey("photo")) {
					HashMap<String, Object> imageAttrs = (HashMap<String, Object>) imageWithDescriptionHm.get("photo");
					si = getCjStrapiImage(imageAttrs);
					imageWithDescription.setStrapiImage(si);
				}

				iwds.add(imageWithDescription);
				if (si != null
						&& (strapiPhotoGallery.getImageUrl() == null || strapiPhotoGallery.getImageUrl().isEmpty())) {
					strapiPhotoGallery.setImageUrl(si.getUrl());
					strapiPhotoGallery.setImageCaption(photoDescription);
					strapiPhotoGallery.setThumbnailPrimaryImageUrl(si.getThumbnailImageUrl());
					strapiPhotoGallery.setImageWidth(si.getWidth());
					strapiPhotoGallery.setImageHeight(si.getHeight());
				}

			}
			strapiPhotoGallery.setImageWithDescription(iwds);
		}
		
		if (contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_BULK_IMAGE_UPLOAD)
				&& attrs.containsKey("photoBulkUpload")) 
		{
			photoBulkUpload = new PhotoBulkUpload();
			HashMap<String, Object> photoBulkUploadHm = (HashMap<String, Object>) attrs.get("photoBulkUpload");
			if(photoBulkUploadHm != null)
			{
				if (photoBulkUploadHm.containsKey("id")) {
					int id = (int) photoBulkUploadHm.get("id");
					photoBulkUpload.setId(id);
				}

				if (photoBulkUploadHm.containsKey("primaryImage")) {
					HashMap<String, Object> primaryImageSr = (HashMap<String, Object>) photoBulkUploadHm
							.get("primaryImage");
					StrapiImage strapiPrimaryImage = getCjStrapiImage(primaryImageSr);
					strapiPhotoGallery.setImageUrl(strapiPrimaryImage.getUrl());
					strapiPhotoGallery.setImageCaption(strapiPrimaryImage.getCaption());
					strapiPhotoGallery.setThumbnailPrimaryImageUrl(strapiPrimaryImage.getThumbnailImageUrl());
					strapiPhotoGallery.setImageWidth(strapiPrimaryImage.getWidth());
					strapiPhotoGallery.setImageHeight(strapiPrimaryImage.getHeight());
					photoBulkUpload.setPrimaryImage(strapiPrimaryImage);
				}

				if (photoBulkUploadHm.containsKey("photoBulkUpload")) {
					List<StrapiImage> photoBulkUploadImages = new ArrayList<>();
					HashMap<String, Object> attrs1 = (HashMap<String, Object>) photoBulkUploadHm.get("photoBulkUpload");
					StrapiResponseArray photoBulkUploadHmAttrs = getStrapiDataAttributesArray(attrs1);
					List<Entry> entries = photoBulkUploadHmAttrs.getEntries();
					for (Entry entry : entries) {
						HashMap<String, Object> bulkImageUploadHm = entry.getAttributes();
						StrapiImage si = getCjStrapiImage(bulkImageUploadHm);
						photoBulkUploadImages.add(si);
					}
					photoBulkUpload.setPhotoBulkUpload(photoBulkUploadImages);
				}
				strapiPhotoGallery.setPhotoBulkUpload(photoBulkUpload);
			}

		}

		if (attrs.containsKey("photoSlider"))
		{
			List<HashMap<String, String>> photoSliderArray = (List<HashMap<String, String>>) attrs.get("photoSlider");
			StrapiImage primaryImage = null;
			if(photoSliderArray != null && !photoSliderArray.isEmpty())
			{
				photoBulkUpload = new PhotoBulkUpload();
				List<StrapiImage> photoBulkUploadImages = new ArrayList<>();
				for (HashMap<String, String> photoSliderHm : photoSliderArray) {
					if(primaryImage == null)
					{
						String imageUrl = photoSliderHm.get("imageURL");
						imageUrl = StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME+ imageUrl;
						primaryImage = new StrapiImage();
						primaryImage.setUrl(imageUrl);
						photoBulkUpload.setPrimaryImage(primaryImage);
						strapiPhotoGallery.setImageUrl(primaryImage.getUrl());
					}
					else
					{
					String imageUrl = photoSliderHm.get("imageURL");
					imageUrl = StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME+ imageUrl;
					StrapiImage otherImage = new StrapiImage();
					otherImage.setCaption(String.valueOf(photoSliderHm.get("id")));
					otherImage.setUrl(imageUrl);
					photoBulkUploadImages.add(otherImage);
					}
				}
				photoBulkUpload.setPhotoBulkUpload(photoBulkUploadImages);
				strapiPhotoGallery.setPhotoBulkUpload(photoBulkUpload);
			}
			
		}

		if (attrs.containsKey("subCategory")) {
			// StrapiSubCategory subCategory = getStrapiSubCategory((HashMap<String,
			// Object>) attrs.get("subCategory"));
			String subCategoryStr = (String) attrs.get("subCategory");
			String subCatIdChar = (String) subCategoryStr.substring(subCategoryStr.length() - 2,
					subCategoryStr.length());
			StrapiSubCategory strapiSubCategory = new StrapiSubCategory();
			strapiSubCategory.setId(Integer.parseInt(subCatIdChar));
			strapiPhotoGallery.setSubCategory(strapiSubCategory);
		}

		if (attrs.containsKey("tags")) {
			strapiTags = new ArrayList<>();
			List<Object> tagsLs = (List<Object>) attrs.get("tags");

			for (Object tagObj : tagsLs) {
				StrapiTag tag = getStrapiTag((HashMap<String, Object>) tagObj);
				strapiTags.add(tag);
			}
			strapiPhotoGallery.setTags(strapiTags);
		}
		strapiPhotoGallery.setPublished(true);
		
		if (attrs.containsKey("newUrl"))
		{
			String newUrl = (String) attrs.get("newUrl");
			if(newUrl != null && !newUrl.isEmpty())
				strapiPhotoGallery.setUrl((String) attrs.get("newUrl"));
		}
		// for Migrated data
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiPhotoGallery.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			//updatedAt = publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiPhotoGallery.setPublishedAt(ts);
						strapiPhotoGallery.setUpdatedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiPhotoGallery.setPublishedAtSm(publishedAtSm);
						strapiPhotoGallery.setUpdatedAtSm(publishedAtSm);
					}
				}
			}
		}
		
		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			strapiPhotoGallery.setLocale(locale);
		}
				
		return strapiPhotoGallery;
	}
	
	public static StrapiPhotoGallery getStrapiCjPhotoGallery(HashMap<String, Object> attrs) {
		StrapiPhotoGallery strapiPhotoGallery = new StrapiPhotoGallery();

		PhotoBulkUpload photoBulkUpload;
		StoryGeographicLocation storyGeographicLocation;
		List<StrapiTag> strapiTags;
		String contentType = null;

		if (attrs.containsKey("id"))
			strapiPhotoGallery.setId((int) attrs.get("id"));
		


		/*
		 * if(attrs.containsKey("publishedAt")) { String publishedAtStr = (String)
		 * attrs.get("publishedAt"); if(publishedAtStr != null) { Timestamp ts =
		 * convertStringToTsTz(publishedAtStr); strapiPhotoGallery.setPublishedAt(ts);
		 * String publishedAtSm = convertTsTzToIST(publishedAtStr);
		 * strapiPhotoGallery.setPublishedAtSm(publishedAtSm); } }
		 */

		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiPhotoGallery.setCreatedAt(ts);
			strapiPhotoGallery.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiPhotoGallery.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiPhotoGallery.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiPhotoGallery.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("prioritiseInPhotoLanding")) {
			if (attrs.get("prioritiseInPhotoLanding") == null)
				strapiPhotoGallery.setPrioritiseInPhotoLanding(false);
			else
				strapiPhotoGallery.setPrioritiseInPhotoLanding((Boolean) attrs.get("prioritiseInPhotoLanding"));
		}

		if (attrs.containsKey("metaTitle"))
			strapiPhotoGallery.setMetaTitle((String) attrs.get("metaTitle"));
		if (attrs.containsKey("metaDescription"))
			strapiPhotoGallery.setMetaDescription((String) attrs.get("metaDescription"));
		if (attrs.containsKey("headline"))
			{
			String headline = (String) attrs.get("headline");
			strapiPhotoGallery.setHeadline(headline.replace("\"", ""));
			}

		/*
		 * if(attrs.containsKey("englishTitle"))
		 * strapiPhotoGallery.setEnglishTitle((String) attrs.get("englishTitle"));
		 */
		if (attrs.containsKey("englishTitle")) {
			String englishTitle = (String) attrs.get("englishTitle");
			strapiPhotoGallery.setEnglishTitle(englishTitle);
			// generate seoSlug from englishHeadline
			String updatedEnglishTitle = englishTitle.trim().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
			updatedEnglishTitle = updatedEnglishTitle.replaceAll("\\s+", "-");
			strapiPhotoGallery.setSeoSlug(updatedEnglishTitle);
		}

		if (attrs.containsKey("summary"))
			strapiPhotoGallery.setSummary((String) attrs.get("summary"));

		if (attrs.containsKey("newsKeywords"))
			strapiPhotoGallery.setNewsKeywords((String) attrs.get("newsKeywords"));

		/*
		 * if(attrs.containsKey("seoSlug")) strapiPhotoGallery.setSeoSlug((String)
		 * attrs.get("seoSlug"));
		 */

		if (attrs.containsKey("contentType")) {
			contentType = (String) attrs.get("contentType");
			strapiPhotoGallery.setContentType(contentType);
		}

		if (attrs.containsKey("abnStoryId"))
			strapiPhotoGallery.setAbnStoryId((String) attrs.get("abnStoryId"));

		if (attrs.containsKey("location")) {
			storyGeographicLocation = getStoryGeographicLocation(attrs.get("location"));
			if (storyGeographicLocation != null)
				strapiPhotoGallery.setPhotoLocation(storyGeographicLocation);
		}

		if (contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_IMAGE_WITH_DESCRIPTION)
				&& attrs.containsKey("photoWithDescription")) {
			List<ImageWithDescription> iwds = new ArrayList<ImageWithDescription>();

			List<HashMap<String, Object>> imageWithDescriptionHmLs = (List<HashMap<String, Object>>) attrs
					.get("photoWithDescription");

			int id = 0;
			String photoDescription = null;
			StrapiImage si = null;

			for (HashMap<String, Object> imageWithDescriptionHm : imageWithDescriptionHmLs) {
				ImageWithDescription imageWithDescription = new ImageWithDescription();
				if (imageWithDescriptionHm.containsKey("id")) {
					id = (int) imageWithDescriptionHm.get("id");
					imageWithDescription.setId(id);
				}
				if (imageWithDescriptionHm.containsKey("photoDescription")) {
					photoDescription = (String) imageWithDescriptionHm.get("photoDescription");
					imageWithDescription.setPhotoDescription(photoDescription);
				}
				if (imageWithDescriptionHm.containsKey("photo")) {
					HashMap<String, Object> imageAttrs = (HashMap<String, Object>) imageWithDescriptionHm.get("photo");
					si = getCjStrapiImage(imageAttrs);
					imageWithDescription.setStrapiImage(si);
				}

				iwds.add(imageWithDescription);
				if (si != null
						&& (strapiPhotoGallery.getImageUrl() == null || strapiPhotoGallery.getImageUrl().isEmpty())) {
					strapiPhotoGallery.setImageUrl(si.getUrl());
					strapiPhotoGallery.setImageCaption(photoDescription);
					strapiPhotoGallery.setThumbnailPrimaryImageUrl(si.getThumbnailImageUrl());
					strapiPhotoGallery.setImageWidth(si.getWidth());
					strapiPhotoGallery.setImageHeight(si.getHeight());
				}

			}
			strapiPhotoGallery.setImageWithDescription(iwds);
		}

		if (contentType.equalsIgnoreCase(StrapiConstants.CONTENT_TYPE_BULK_IMAGE_UPLOAD)
				&& attrs.containsKey("photoBulkUpload")) {
			photoBulkUpload = new PhotoBulkUpload();
			HashMap<String, Object> photoBulkUploadHm = (HashMap<String, Object>) attrs.get("photoBulkUpload");
			if (photoBulkUploadHm != null)
			{
				if (photoBulkUploadHm.containsKey("id")) {
					int id = (int) photoBulkUploadHm.get("id");
					photoBulkUpload.setId(id);
				}

				if (photoBulkUploadHm.containsKey("primaryImage")) {
					HashMap<String, Object> primaryImageSr = (HashMap<String, Object>) photoBulkUploadHm
							.get("primaryImage");
					StrapiImage strapiPrimaryImage = getCjStrapiImage(primaryImageSr);
					strapiPhotoGallery.setImageUrl(strapiPrimaryImage.getUrl());
					strapiPhotoGallery.setImageCaption(strapiPrimaryImage.getCaption());
					strapiPhotoGallery.setThumbnailPrimaryImageUrl(strapiPrimaryImage.getThumbnailImageUrl());
					strapiPhotoGallery.setImageWidth(strapiPrimaryImage.getWidth());
					strapiPhotoGallery.setImageHeight(strapiPrimaryImage.getHeight());
					photoBulkUpload.setPrimaryImage(strapiPrimaryImage);
				}

				if (photoBulkUploadHm.containsKey("photoBulkUpload")) {
					List<StrapiImage> photoBulkUploadImages = new ArrayList<>();
					List<Object> photoBulkUploadHmLs = (List<Object>) photoBulkUploadHm	.get("photoBulkUpload");
					for (Object bulkImageUploadHm : photoBulkUploadHmLs) {
						StrapiImage si = getCjStrapiImage((HashMap<String, Object>) bulkImageUploadHm);
						photoBulkUploadImages.add(si);
					}
					photoBulkUpload.setPhotoBulkUpload(photoBulkUploadImages);
				}
				strapiPhotoGallery.setPhotoBulkUpload(photoBulkUpload);
			}
		}


		if (attrs.containsKey("subCategory")) {
			// StrapiSubCategory subCategory = getStrapiSubCategory((HashMap<String,
			// Object>) attrs.get("subCategory"));
			String subCategoryStr = (String) attrs.get("subCategory");
			String subCatIdChar = (String) subCategoryStr.substring(subCategoryStr.length() - 2,
					subCategoryStr.length());
			StrapiSubCategory strapiSubCategory = new StrapiSubCategory();
			strapiSubCategory.setId(Integer.parseInt(subCatIdChar));
			strapiPhotoGallery.setSubCategory(strapiSubCategory);
		}
		
		if (attrs.containsKey("newUrl"))
		{
			String newUrl = (String) attrs.get("newUrl");
			if(newUrl != null && !newUrl.isEmpty())
				strapiPhotoGallery.setUrl((String) attrs.get("newUrl"));
		}
		
		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			strapiPhotoGallery.setLocale(locale);
		}
			
		// for Migrated data
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiPhotoGallery.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			//updatedAt = publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiPhotoGallery.setPublishedAt(ts);
						strapiPhotoGallery.setUpdatedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiPhotoGallery.setPublishedAtSm(publishedAtSm);
						strapiPhotoGallery.setUpdatedAtSm(publishedAtSm);
					}
				}
			}
		}

		if (attrs.containsKey("tags")) {
			strapiTags = new ArrayList<>();
			List<Object> tagsLs = (List<Object>) attrs.get("tags");

			for (Object tagObj : tagsLs) {
				StrapiTag tag = getStrapiTag((HashMap<String, Object>) tagObj);
				strapiTags.add(tag);
			}
			strapiPhotoGallery.setTags(strapiTags);
		}
		return strapiPhotoGallery;
	}
	
	public static StrapiArticle getStrapiCjArticle(HashMap<String, Object> attrs) {
		// Entry e = strapiResponse.getEntry();
		// HashMap<String, Object> attrs = strapiResponse.getAttributes();

		StrapiArticle strapiArticle = new StrapiArticle();
		List<ArticleTextEditor> articleTextEditors;
		ArticleTextEditor firstArticleTextEditor = null;
		StoryGeographicLocation storyGeographicLocation;
		StrapiCategory primaryCategory;
		StrapiSubCategory primarySubCategory;
		List<StrapiCategory> secondaryCategories;
		List<StrapiTag> strapiTags;
		List<StrapiSubCategory> secondarySubCategories;

		if (attrs.containsKey("id"))
			strapiArticle.setId((int) attrs.get("id"));

		if (attrs.containsKey("prioritiseInLatestNews"))
			strapiArticle.setPrioritiseInLatestNews((Boolean) attrs.get("prioritiseInLatestNews"));
		if (attrs.containsKey("prioritiseInPrimarySection"))
			strapiArticle.setPrioritiseInPrimarySection((Boolean) attrs.get("prioritiseInPrimarySection"));
		if (attrs.containsKey("displayModifiedDate"))
			strapiArticle.setDisplayModifiedDate((Boolean) attrs.get("displayModifiedDate"));
		/*
		 * if(attrs.containsKey("publishedAt")) { String publishedAtStr = (String)
		 * attrs.get("publishedAt"); if(publishedAtStr != null) { //publishedAtStr =
		 * (String) attrs.get("updatedAt"); Timestamp ts =
		 * convertStringToTsTz(publishedAtStr); strapiArticle.setPublishedAt(ts); String
		 * publishedAtSm = convertTsTzToIST(publishedAtStr);
		 * strapiArticle.setPublishedAtSm(publishedAtSm); }
		 * 
		 * }
		 */
		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiArticle.setCreatedAt(ts);
			strapiArticle.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiArticle.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiArticle.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiArticle.setUpdatedAtSm(updatedAtSm);
		}
		if (attrs.containsKey("metaTitle"))
			strapiArticle.setMetaTitle((String) attrs.get("metaTitle"));
		if (attrs.containsKey("metaDescription"))
			strapiArticle.setMetaDescription((String) attrs.get("metaDescription"));
		if (attrs.containsKey("primaryImage")) {
			HashMap<String, Object> primaryImageSr = (HashMap<String, Object>) attrs.get("primaryImage");
			StrapiImage strapiPrimaryImage = getCjStrapiImage(primaryImageSr);
			strapiArticle.setImageUrl(strapiPrimaryImage.getUrl());
			// strapiArticle.setImageCaption(strapiPrimaryImage.getCaption());
			strapiArticle.setSmallPrimaryImageUrl(strapiPrimaryImage.getSmallImageUrl());
			strapiArticle.setMediumPrimaryImageUrl(strapiPrimaryImage.getMediumImageUrl());
			strapiArticle.setLargePrimaryImageUrl(strapiPrimaryImage.getLargeImageUrl());
			strapiArticle.setThumbnailPrimaryImageUrl(strapiPrimaryImage.getThumbnailImageUrl());
		}

		if (attrs.containsKey("primaryImageDescription")) {
			strapiArticle.setImageCaption((String) attrs.get("primaryImageDescription"));
		}

		if (attrs.containsKey("headline"))
		{
			String headline = (String) attrs.get("headline");
			strapiArticle.setHeadline(headline.replace("\"", ""));
		}

		if (attrs.containsKey("englishTitle")) {
			String englishTitle = (String) attrs.get("englishTitle");
			strapiArticle.setEnglishTitle(englishTitle);
			// generate seoSlug from englishHeadline
			String updatedEnglishTitle = englishTitle.trim().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
			updatedEnglishTitle = updatedEnglishTitle.replaceAll("\\s+", "-");
			strapiArticle.setSeoSlug(updatedEnglishTitle);
		}

		if (attrs.containsKey("summary"))
			strapiArticle.setSummary((String) attrs.get("summary"));

		if (attrs.containsKey("shortHeadline"))
			strapiArticle.setShortHeadline((String) attrs.get("shortHeadline"));

		if (attrs.containsKey("newsKeywords"))
			strapiArticle.setNewsKeywords((String) attrs.get("newsKeywords"));

		/*
		 * if(attrs.containsKey("seoSlug")) strapiArticle.setSeoSlug((String)
		 * attrs.get("seoSlug"));
		 */
		
		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			strapiArticle.setLocale(locale);
		}
		

		if (attrs.containsKey("headline"))
		{
			String headline = (String) attrs.get("headline");
			strapiArticle.setHeadline(headline.replace("\"", ""));
		}

		if (attrs.containsKey("contentType"))
			strapiArticle.setContentType((String) attrs.get("contentType"));

		if (attrs.containsKey("abnStoryId"))
			strapiArticle.setAbnStoryId((String) attrs.get("abnStoryId"));

		if (attrs.containsKey("author"))
			strapiArticle.setAuthor((String) attrs.get("author"));

		if (attrs.containsKey("articleText")) {
			articleTextEditors = getArticleTextEditors(attrs.get("articleText"));
			firstArticleTextEditor = articleTextEditors.get(0);
			strapiArticle.setArticleTextEditors(articleTextEditors);
		}
		
		if (attrs.containsKey("newUrl"))
		{
			String newUrl = (String) attrs.get("newUrl");
			if(newUrl != null && !newUrl.isEmpty())
				strapiArticle.setUrl((String) attrs.get("newUrl"));
		}
		
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiArticle.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiArticle.setPublishedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiArticle.setPublishedAtSm(publishedAtSm);
					}
				}
				
				if(firstArticleTextEditor != null)
				{
				// primaryImage = Image in the first Article Text Editor
				String primaryImageUrl  = firstArticleTextEditor.getImageUrl();
				String primaryImageCaption = firstArticleTextEditor.getImageDescription();
				if(primaryImageUrl != null)
				{
					strapiArticle.setImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME +  primaryImageUrl);
					strapiArticle.setSmallPrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setMediumPrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setLargePrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setThumbnailPrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
				}
				strapiArticle.setImageCaption(primaryImageCaption);
				}
			}
			
			
		}

		if (attrs.containsKey("location")) {
			storyGeographicLocation = getStoryGeographicLocation(attrs.get("location"));
			if (storyGeographicLocation != null)
				strapiArticle.setStoryGeographicLocation(storyGeographicLocation);
		}

		if (attrs.containsKey("primaryCategory")) {
			primaryCategory = getStrapiCategory((HashMap<String, Object>) attrs.get("primaryCategory"));
			strapiArticle.setPrimaryCategory(primaryCategory);
		}

		/*
		 * if(attrs.containsKey("secondaryCategories")) { secondaryCategories = new
		 * ArrayList<>(); HashMap<String, Object> secondaryCategoriesHm =
		 * (HashMap<String, Object>) attrs.get("secondaryCategories");
		 * List<HashMap<String, Object>> secondaryCategoriesList = (List<HashMap<String,
		 * Object>>) secondaryCategoriesHm.get("data"); for (HashMap<String, Object>
		 * entry : secondaryCategoriesList) { StrapiCategory secondaryCategory =
		 * getStrapiCategory(entry); secondaryCategories.add(secondaryCategory); }
		 * strapiArticle.setSecondaryCategories(secondaryCategories); }
		 */

		if (attrs.containsKey("secondaryCategories")) {
			secondaryCategories = new ArrayList<>();
			System.out.println(attrs.get("secondaryCategories").getClass());
			List<Object> secondaryCategoriesLs = (List<Object>) attrs.get("secondaryCategories");
			for (Object secondaryCategoriesObj : secondaryCategoriesLs) {
				StrapiCategory secondaryCategory = getStrapiCategory((HashMap<String, Object>) secondaryCategoriesObj);
				secondaryCategories.add(secondaryCategory);
			}

			strapiArticle.setSecondaryCategories(secondaryCategories);
		}

		if (attrs.containsKey("primarySubCategory")) {
			primarySubCategory = getStrapiSubCategory((HashMap<String, Object>) attrs.get("primarySubCategory"));
			strapiArticle.setPrimarySubCategory(primarySubCategory);
		}
		/*
		 * if(attrs.containsKey("secondarySubCategories")) { secondaryCategories = new
		 * ArrayList<>(); HashMap<String, Object> secondaryCategoriesHm =
		 * (HashMap<String, Object>) attrs.get("secondaryCategories");
		 * List<HashMap<String, Object>> secondaryCategoriesList = (List<HashMap<String,
		 * Object>>) secondaryCategoriesHm.get("data"); for (HashMap<String, Object>
		 * entry : secondaryCategoriesList) { StrapiCategory secondaryCategory =
		 * getStrapiCategory(entry); secondaryCategories.add(secondaryCategory); }
		 * strapiArticle.setSecondaryCategories(secondaryCategories); }
		 */

		if (attrs.containsKey("secondarySubCategories")) {
			secondaryCategories = new ArrayList<>();
			List<Object> secondaryCategoriesLs = (List<Object>) attrs.get("secondaryCategories");

			for (Object secondaryCategoryObj : secondaryCategoriesLs) {
				StrapiCategory secondaryCategory = getStrapiCategory((HashMap<String, Object>) secondaryCategoryObj);
				secondaryCategories.add(secondaryCategory);
			}

			strapiArticle.setSecondaryCategories(secondaryCategories);
			strapiArticle.setSecondaryCategories(secondaryCategories);
		}

		/*
		 * if(attrs.containsKey("tags")) { strapiTags = new ArrayList<>();
		 * HashMap<String, Object> tagsHm = (HashMap<String, Object>) attrs.get("tags");
		 * StrapiResponseArray strapiResponseArrayObj =
		 * getStrapiDataAttributesArray(tagsHm); for (Entry entry :
		 * strapiResponseArrayObj.getEntries()) { StrapiTag tag =
		 * getStrapiTag(entry.getAttributes()); strapiTags.add(tag); }
		 * strapiArticle.setTags(strapiTags); }
		 */

		if (attrs.containsKey("tags")) {
			strapiTags = new ArrayList<>();
			List<Object> tagsLs = (List<Object>) attrs.get("tags");

			for (Object tagObj : tagsLs) {
				StrapiTag tag = getStrapiTag((HashMap<String, Object>) tagObj);
				strapiTags.add(tag);
			}
			strapiArticle.setTags(strapiTags);
		}

		if (attrs.containsKey("referenceArticles")) {
			List<StrapiArticle> relatedArticles = new ArrayList<>();
			List<Object> articlesLs = (List<Object>) attrs.get("referenceArticles");

			for (Object articleStr : articlesLs) {
				StrapiArticle relatedArticle = getStrapiCjArticle((HashMap<String, Object>) articleStr);
				relatedArticles.add(relatedArticle);
			}

			strapiArticle.setArticles(relatedArticles);
		}

		return strapiArticle;

	}
	
	public static StrapiArticle getStrapiCjArticleFromCMS(HashMap<String, Object> attrs) {
		StrapiArticle strapiArticle = new StrapiArticle();

		strapiArticle.setPublished(true); // set published=true for this article

		List<ArticleTextEditor> articleTextEditors;
		ArticleTextEditor firstArticleTextEditor = null;
		StoryGeographicLocation storyGeographicLocation;
		StrapiCategory primaryCategory;
		StrapiSubCategory primarySubCategory;
		List<StrapiCategory> secondaryCategories;
		List<StrapiTag> strapiTags;
		List<StrapiSubCategory> secondarySubCategories;

		if (attrs.containsKey("id"))
			strapiArticle.setId((int) attrs.get("id"));
		if (attrs.containsKey("newUrl"))
		{
			String newUrl = (String) attrs.get("newUrl");
			if(newUrl != null && !newUrl.isEmpty())
				strapiArticle.setUrl((String) attrs.get("newUrl"));
		}

		if (attrs.containsKey("prioritiseInLatestNews"))
			strapiArticle.setPrioritiseInLatestNews((Boolean) attrs.get("prioritiseInLatestNews"));
		if (attrs.containsKey("prioritiseInPrimarySection"))
			strapiArticle.setPrioritiseInPrimarySection((Boolean) attrs.get("prioritiseInPrimarySection"));
		if (attrs.containsKey("displayModifiedDate"))
			strapiArticle.setDisplayModifiedDate((Boolean) attrs.get("displayModifiedDate"));

		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiArticle.setCreatedAt(ts);
			strapiArticle.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiArticle.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiArticle.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiArticle.setUpdatedAtSm(updatedAtSm);
		}
		if (attrs.containsKey("metaTitle"))
			strapiArticle.setMetaTitle((String) attrs.get("metaTitle"));
		if (attrs.containsKey("metaDescription"))
			strapiArticle.setMetaDescription((String) attrs.get("metaDescription"));
		
		if (attrs.containsKey("primaryImage")) {
			HashMap<String, Object> primaryImageSr = (HashMap<String, Object>) attrs.get("primaryImage");
			StrapiImage strapiPrimaryImage = getCjStrapiImage(primaryImageSr);
			strapiArticle.setImageUrl(strapiPrimaryImage.getUrl());
			// strapiArticle.setImageCaption(strapiPrimaryImage.getCaption());
			strapiArticle.setSmallPrimaryImageUrl(strapiPrimaryImage.getSmallImageUrl());
			strapiArticle.setMediumPrimaryImageUrl(strapiPrimaryImage.getMediumImageUrl());
			strapiArticle.setLargePrimaryImageUrl(strapiPrimaryImage.getLargeImageUrl());
			strapiArticle.setThumbnailPrimaryImageUrl(strapiPrimaryImage.getThumbnailImageUrl());
		}

		if (attrs.containsKey("primaryImageDescription")) {
			String pid = (String) attrs.get("primaryImageDescription");
			strapiArticle.setImageCaption(pid);
		}

		if (attrs.containsKey("headline"))
		{
			String headline = (String) attrs.get("headline");
			strapiArticle.setHeadline(headline.replace("\"", ""));
		}

		if (attrs.containsKey("englishTitle")) {
			String englishTitle = (String) attrs.get("englishTitle");
			strapiArticle.setEnglishTitle(englishTitle);
			// generate seoSlug from englishHeadline
			String updatedEnglishTitle = englishTitle.trim().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
			updatedEnglishTitle = updatedEnglishTitle.replaceAll("\\s+", "-");
			strapiArticle.setSeoSlug(updatedEnglishTitle);
		}

		if (attrs.containsKey("summary"))
			strapiArticle.setSummary((String) attrs.get("summary"));

		if (attrs.containsKey("shortHeadline"))
			strapiArticle.setShortHeadline((String) attrs.get("shortHeadline"));

		if (attrs.containsKey("newsKeywords"))
			strapiArticle.setNewsKeywords((String) attrs.get("newsKeywords"));

		/*if (attrs.containsKey("seoSlug"))
			strapiArticle.setSeoSlug((String) attrs.get("seoSlug"));*/

		if (attrs.containsKey("headline"))
		{
			String headline = (String) attrs.get("headline");
			strapiArticle.setHeadline(headline.replace("\"", ""));
		}

		if (attrs.containsKey("contentType"))
			strapiArticle.setContentType((String) attrs.get("contentType"));

		if (attrs.containsKey("speedNews"))
			strapiArticle.setSpeedNews((boolean) attrs.get("speedNews"));

		if (attrs.containsKey("articleText")) {
			articleTextEditors = getArticleTextEditors(attrs.get("articleText"));
			firstArticleTextEditor = articleTextEditors.get(0);
			strapiArticle.setArticleTextEditors(articleTextEditors);
		}
		
		// for Migrated data
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiArticle.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			//updatedAt = publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiArticle.setPublishedAt(ts);
						strapiArticle.setUpdatedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiArticle.setPublishedAtSm(publishedAtSm);
						strapiArticle.setUpdatedAtSm(publishedAtSm);
					}
				}
				
				if(firstArticleTextEditor != null)
				{
				// primaryImage = Image in the first Article Text Editor
				String primaryImageUrl  = firstArticleTextEditor.getImageUrl();
				String primaryImageCaption = firstArticleTextEditor.getImageDescription();
				strapiArticle.setImageCaption(primaryImageCaption);
				if(primaryImageUrl != null)
					{
					strapiArticle.setImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME +  primaryImageUrl);
					strapiArticle.setSmallPrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setMediumPrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setLargePrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setThumbnailPrimaryImageUrl(StrapiConstants.CHITRAJYOTHY_MEDIA_DOMAIN_NAME + primaryImageUrl);
					}
				}
			}
		}

		if (attrs.containsKey("author"))
			strapiArticle.setAuthor((String) attrs.get("author"));

		

		if (attrs.containsKey("location")) {
			storyGeographicLocation = getStoryGeographicLocation(attrs.get("location"));
			if (storyGeographicLocation != null)
				strapiArticle.setStoryGeographicLocation(storyGeographicLocation);
		}

		if (attrs.containsKey("primaryCategory")) {
			primaryCategory = getStrapiCategory((HashMap<String, Object>) attrs.get("primaryCategory"));
			strapiArticle.setPrimaryCategory(primaryCategory);
		}

		if (attrs.containsKey("secondaryCategories")) {
			secondaryCategories = new ArrayList<>();
			HashMap<String, Object> secondaryCategoriesHm = (HashMap<String, Object>) attrs.get("secondaryCategories");
			List<HashMap<String, Object>> secondaryCategoriesList = (List<HashMap<String, Object>>) secondaryCategoriesHm
					.get("data");
			for (HashMap<String, Object> entry : secondaryCategoriesList) {
				StrapiCategory secondaryCategory = getStrapiCategory(entry);
				secondaryCategories.add(secondaryCategory);
			}
			strapiArticle.setSecondaryCategories(secondaryCategories);
		}

		if (attrs.containsKey("primarySubCategory")) {
			primarySubCategory = getStrapiSubCategory((HashMap<String, Object>) attrs.get("primarySubCategory"));
			strapiArticle.setPrimarySubCategory(primarySubCategory);
		}

		if (attrs.containsKey("secondarySubCategories")) {
			secondaryCategories = new ArrayList<>();
			HashMap<String, Object> secondaryCategoriesHm = (HashMap<String, Object>) attrs.get("secondaryCategories");
			List<HashMap<String, Object>> secondaryCategoriesList = (List<HashMap<String, Object>>) secondaryCategoriesHm
					.get("data");
			for (HashMap<String, Object> entry : secondaryCategoriesList) {
				StrapiCategory secondaryCategory = getStrapiCategory(entry);
				secondaryCategories.add(secondaryCategory);
			}
			strapiArticle.setSecondaryCategories(secondaryCategories);
		}
		
		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			strapiArticle.setLocale(locale);
		}

		if (attrs.containsKey("tags")) {
			strapiTags = new ArrayList<>();
			HashMap<String, Object> tagsHm = (HashMap<String, Object>) attrs.get("tags");
			StrapiResponseArray strapiResponseArrayObj = getStrapiDataAttributesArray(tagsHm);
			for (Entry entry : strapiResponseArrayObj.getEntries()) {
				StrapiTag tag = getStrapiTag(entry.getAttributes());
				strapiTags.add(tag);
			}
			strapiArticle.setTags(strapiTags);
		}

		return strapiArticle;
	}
	
	private static StrapiTag getStrapiTag(HashMap<String, Object> attrs) {
		String tagId;
		String name;

		StrapiTag strapiTag = new StrapiTag();
		// StrapiResponse strapiResponseObj = getStrapiDataAttributes(object);
		// HashMap<String, Object> attrs = strapiResponseObj.getEntry().getAttributes();

		if (attrs.containsKey("name")) {
			name = (String) attrs.get("name");
			strapiTag.setName(name.trim());
			strapiTag.setUrl("/tag/" + name.trim().toLowerCase().replace(" ", "-"));
		}

		if (attrs.containsKey("tagId")) {
			tagId = (String) attrs.get("tagId");
			strapiTag.setTagId(tagId);
		}

		return strapiTag;
	}

	public static StrapiSubCategory getStrapiSubCategory(HashMap<String, Object> object) {
		String subCategoryId;
		String name;
		String description;
		String metaTitle;
		String keywords;
		String teluguLabel;
		String seoSlug;
		String metaDescription;

		StrapiSubCategory strapiTestSubCategory = new StrapiSubCategory();
		StrapiResponse strapiResponseObj = getStrapiDataAttributes(object);
		if (strapiResponseObj.getEntry() != null) {
			int id = strapiResponseObj.getEntry().getId();
			strapiTestSubCategory.setId(id);

			HashMap<String, Object> attrs = strapiResponseObj.getEntry().getAttributes();

			if (attrs.containsKey("subCategoryId")) {
				subCategoryId = (String) attrs.get("subCategoryId");
				strapiTestSubCategory.setSubCategoryId(subCategoryId);
			}
			if (attrs.containsKey("name")) {
				name = (String) attrs.get("name");
				strapiTestSubCategory.setName(name);
				seoSlug = name.trim().toLowerCase().replace(" ", "-");
				strapiTestSubCategory.setSeoSlug(seoSlug);
			}
			if (attrs.containsKey("category")) {
				HashMap<String, Object> categoryHm = (HashMap<String, Object>) attrs.get("category");
				StrapiCategory parentCategory = getStrapiCategory(categoryHm);
				strapiTestSubCategory.setCategory(parentCategory);
			}
			else if (attrs.containsKey("categories")) {
				HashMap<String, Object> categoryHm = (HashMap<String, Object>) attrs.get("categories");
				StrapiCategory parentCategory = getStrapiCategory(categoryHm);
				strapiTestSubCategory.setCategory(parentCategory);
			}
			if (attrs.containsKey("description")) {
				description = (String) attrs.get("description");
				strapiTestSubCategory.setDescription(description);
			}
			if (attrs.containsKey("metaTitle")) {
				metaTitle = (String) attrs.get("metaTitle");
				strapiTestSubCategory.setMetaTitle(metaTitle);
			}
			if (attrs.containsKey("metaDescription")) {
				metaDescription = (String) attrs.get("metaDescription");
				strapiTestSubCategory.setMetaDescription(metaDescription);
			}
			if (attrs.containsKey("keywords")) {
				keywords = (String) attrs.get("keywords");
				strapiTestSubCategory.setKeywords(keywords);
			}
			if (attrs.containsKey("teluguLabel")) {
				teluguLabel = (String) attrs.get("teluguLabel");
				strapiTestSubCategory.setTeluguLabel(teluguLabel);
			}
			if (attrs.containsKey("seoSlug")) {
				seoSlug = (String) attrs.get("seoSlug");
				strapiTestSubCategory.setSeoSlug(seoSlug);
			}
		}
		return strapiTestSubCategory;
	}

	private static Timestamp convertStringToTsTz(String publishedAtStr) {
		OffsetDateTime odt = OffsetDateTime.parse(publishedAtStr);
		Instant instant = odt.toInstant();
		java.sql.Timestamp ts = java.sql.Timestamp.from(instant);
		return ts;
	}

	private static StoryGeographicLocation getStoryGeographicLocation(Object object) {
		StoryGeographicLocation storyGeographicLocation = new StoryGeographicLocation();
		HashMap<String, String> storyGeographicLocationHm = (HashMap<String, String>) object;
		if (object != null) {
			if (storyGeographicLocationHm.containsKey("latitude")) {
				String latitude = storyGeographicLocationHm.get("latitude");
				storyGeographicLocation.setLatitude(latitude);
			}
			if (storyGeographicLocationHm.containsKey("longitude")) {
				String longitude = storyGeographicLocationHm.get("longitude");
				storyGeographicLocation.setLongitide(longitude);
			}
		}

		return storyGeographicLocation;
	}

	private static List<ArticleTextEditor> getArticleTextEditors(Object object) {
		List<ArticleTextEditor> returnList = new ArrayList<>();
		List<Object> articleTextEditorList = (List<Object>) object;
		DocumentUpload documentUpload;

		for (Object articleTextEditorObj : articleTextEditorList) {
			HashMap<String, Object> articleTextEditorHm = (HashMap<String, Object>) articleTextEditorObj;
			ArticleTextEditor articleTextEditor = new ArticleTextEditor();
			if (articleTextEditorHm.containsKey("id"))
				articleTextEditor.setId((int) articleTextEditorHm.get("id"));
			if (articleTextEditorHm.containsKey("articleText"))
				articleTextEditor.setArticleText((String) articleTextEditorHm.get("articleText"));
			if (articleTextEditorHm.containsKey("videoType"))
				articleTextEditor.setVideoType((String) articleTextEditorHm.get("videoType"));
			if (articleTextEditorHm.containsKey("videoURL"))
				articleTextEditor.setVideoUrl((String) articleTextEditorHm.get("videoURL"));
			if (articleTextEditorHm.containsKey("imageType"))
				articleTextEditor.setImageType((String) articleTextEditorHm.get("imageType"));
			if (articleTextEditorHm.containsKey("imageURL"))
				articleTextEditor.setImageUrl((String) articleTextEditorHm.get("imageURL"));
			if (articleTextEditorHm.containsKey("imageDescription"))
				articleTextEditor.setImageDescription((String) articleTextEditorHm.get("imageDescription"));
			if (articleTextEditorHm.containsKey("documentUpload")) {
				HashMap<String, Object> documentUploadHm = (HashMap<String, Object>) articleTextEditorHm
						.get("documentUpload");
				documentUpload = getStrapiDocumentUpload(documentUploadHm);
				articleTextEditor.setDocumentUpload(documentUpload);
			}
			if (articleTextEditorHm.containsKey("contentImage")) {
				HashMap<String, Object> primaryImageSr = (HashMap<String, Object>) articleTextEditorHm
						.get("contentImage");
				StrapiImage contentImage = getCjStrapiImage(primaryImageSr);
				articleTextEditor.setContentImage(contentImage);
			}
			if (articleTextEditorHm.containsKey("timestamp")) {

				String timestampStr = (String) articleTextEditorHm.get("timestamp");
				Timestamp ts = convertStringToTsTz(timestampStr);
				articleTextEditor.setTimestamp(ts);
				/*
				 * String publishedAtSm = convertTsTzToIST(createdAtStr);
				 * strapiArticle.setPublishedAtSm(publishedAtSm);
				 */
			}

			if (articleTextEditorHm.containsKey("articles")) {
				List<StrapiArticle> relatedArticles = new ArrayList<>();
				List<Object> articlesLs = (List<Object>) articleTextEditorHm.get("articles");

				for (Object articleStr : articlesLs) {
					StrapiArticle relatedArticle = getStrapiCjArticle((HashMap<String, Object>) articleStr);
					relatedArticles.add(relatedArticle);
				}

				articleTextEditor.setArticles(relatedArticles);
			}

			returnList.add(articleTextEditor);
		}

		return returnList;
	}

	private static DocumentUpload getStrapiDocumentUpload(HashMap<String, Object> documentUploadHm) {

		DocumentUpload documentUpload = new DocumentUpload();

		if (documentUploadHm != null) {
			StrapiResponse strapiResponseObj = getStrapiDataAttributes(documentUploadHm);
			Entry e = strapiResponseObj.getEntry();
			if (e != null) {
				HashMap<String, Object> documentUploadAtrrs = strapiResponseObj.getEntry().getAttributes();
				int id = e.getId();
				documentUpload.setId(id);

				if (documentUploadAtrrs.containsKey("name")) {
					documentUpload.setName((String) documentUploadAtrrs.get("name"));
				}

				if (documentUploadAtrrs.containsKey("alternativeText")) {
					documentUpload.setAlternativeText((String) documentUploadAtrrs.get("alternativeText"));
				}

				if (documentUploadAtrrs.containsKey("caption")) {
					documentUpload.setCaption((String) documentUploadAtrrs.get("caption"));
				}
				if (documentUploadAtrrs.containsKey("url")) {
					documentUpload.setUrl((String) documentUploadAtrrs.get("url"));
				}
			}
		}
		return documentUpload;
	}

	public static StrapiCategory getStrapiCategory(HashMap<String, Object> object) {
		String categoryId;
		String name;
		String description;
		String metaTitle;
		String metaDescription;
		String keywords;
		String teluguLabel;
		String seoSlug;

		StrapiCategory strapiTestCategory = new StrapiCategory();
		StrapiResponse strapiResponseObj = getStrapiDataAttributes(object);
		Entry entry = strapiResponseObj.getEntry();

		if (entry != null) {
			if (strapiResponseObj.getEntry().getId() != null) {
				int id = strapiResponseObj.getEntry().getId();
				strapiTestCategory.setId(id);
			}

			HashMap<String, Object> attrs = strapiResponseObj.getEntry().getAttributes();
			if (attrs.containsKey("categoryId")) {
				categoryId = (String) attrs.get("categoryId");
				strapiTestCategory.setCategoryId(categoryId);
			}

			if (attrs.containsKey("name")) {
				name = (String) attrs.get("name");
				strapiTestCategory.setName(name);
				seoSlug = name.trim().toLowerCase().replace(" ", "-");
				strapiTestCategory.setSeoSlug(seoSlug);
			}

			if (attrs.containsKey("description")) {
				description = (String) attrs.get("description");
				strapiTestCategory.setDescription(description);
			}

			if (attrs.containsKey("metaTitle")) {
				metaTitle = (String) attrs.get("metaTitle");
				strapiTestCategory.setMetaTitle(metaTitle);
			}
			
			if (attrs.containsKey("metaDescription")) {
				metaDescription = (String) attrs.get("metaDescription");
				strapiTestCategory.setMetaDescription(metaDescription);
			}

			if (attrs.containsKey("keywords")) {
				keywords = (String) attrs.get("keywords");
				strapiTestCategory.setKeywords(keywords);
			}

			if (attrs.containsKey("teluguLabel")) {
				teluguLabel = (String) attrs.get("teluguLabel");
				strapiTestCategory.setTeluguLabel(teluguLabel);
			}

			if (attrs.containsKey("subCategories")) {
				HashMap<String, Object> subCategoriesHm = (HashMap<String, Object>) attrs.get("subCategories");
				ArrayList<StrapiSubCategory> subCategories = new ArrayList<>();

				List<HashMap<String, Object>> secondaryCategoriesList = (List<HashMap<String, Object>>) subCategoriesHm
						.get("data");
				for (HashMap<String, Object> entrySc : secondaryCategoriesList) {
					StrapiSubCategory secondaryCategory = getStrapiSubCategory(entrySc);
					subCategories.add(secondaryCategory);
				}
				strapiTestCategory.setSubCategories(subCategories);
			}
		}

		return strapiTestCategory;
	}
	public static StrapiImage getCjStrapiImage(HashMap<String, Object> primaryImageAtrrsHm) {
		StrapiImage strapiImage = new StrapiImage();
		if (primaryImageAtrrsHm != null) {

			StrapiResponse strapiResponseObj = getStrapiDataAttributes(primaryImageAtrrsHm);
			Entry e = strapiResponseObj.getEntry();
			if (e != null) {
				HashMap<String, Object> primaryImageAtrrs = strapiResponseObj.getEntry().getAttributes();

				if (primaryImageAtrrs.containsKey("id")) {
					strapiImage.setId((int) primaryImageAtrrs.get("id"));
				}
				if (primaryImageAtrrs.containsKey("name")) {
					strapiImage.setName((String) primaryImageAtrrs.get("name"));
				}

				if (primaryImageAtrrs.containsKey("alternativeText")) {
					strapiImage.setAlternativeText((String) primaryImageAtrrs.get("alternativeText"));
				}

				if (primaryImageAtrrs.containsKey("url")) {
					String url = (String) primaryImageAtrrs.get("url");
					url = url.replace("media-abn.s3.ap-south-1.amazonaws.com", "media.chitrajyothy.com");
					strapiImage.setUrl(url);
				}

				if (primaryImageAtrrs.containsKey("caption")) {

					strapiImage.setCaption((String) primaryImageAtrrs.get("caption"));
				}

				if (primaryImageAtrrs.containsKey("ext")) {
					strapiImage.setExt((String) primaryImageAtrrs.get("ext"));
				}

				if (primaryImageAtrrs.containsKey("width")) {
					strapiImage.setWidth((int) primaryImageAtrrs.get("width"));
				}

				if (primaryImageAtrrs.containsKey("height")) {
					strapiImage.setHeight((int) primaryImageAtrrs.get("height"));
				}

				if (primaryImageAtrrs.containsKey("formats")) {
					HashMap<String, Object> primaryImageFormats = (HashMap<String, Object>) primaryImageAtrrs
							.get("formats");
					HashMap<String, Object> smallImageFormats;
					HashMap<String, Object> mediumImageFormats;
					HashMap<String, Object> thumbnailImageFormats;
					HashMap<String, Object> largeImageFormats;
					if (primaryImageFormats != null) {
						if (primaryImageFormats.containsKey("small")) {
							smallImageFormats = (HashMap<String, Object>) primaryImageFormats.get("small");
							if (smallImageFormats.containsKey("url")) {
								String smallImageUrl = (String) smallImageFormats.get("url");
								strapiImage.setSmallImageUrl(smallImageUrl);
							}

						}

						if (primaryImageFormats.containsKey("medium")) {
							mediumImageFormats = (HashMap<String, Object>) primaryImageFormats.get("medium");
							if (mediumImageFormats.containsKey("url")) {
								String mediumImageUrl = (String) mediumImageFormats.get("url");
								strapiImage.setMediumImageUrl(mediumImageUrl);
							}
						}

						if (primaryImageFormats.containsKey("thumbnail")) {
							thumbnailImageFormats = (HashMap<String, Object>) primaryImageFormats.get("thumbnail");
							if (thumbnailImageFormats.containsKey("url")) {
								String thumbnailImageUrl = (String) thumbnailImageFormats.get("url");
								strapiImage.setThumbnailImageUrl(thumbnailImageUrl);
							}
						}

						if (primaryImageFormats.containsKey("large")) {
							largeImageFormats = (HashMap<String, Object>) primaryImageFormats.get("large");
							if (largeImageFormats.containsKey("url")) {
								String largeImageUrl = (String) largeImageFormats.get("url");
								strapiImage.setLargeImageUrl(largeImageUrl);
							}
						}
					}

				}
			}

		}
		return strapiImage;
	}

	private static StrapiResponseArray getStrapiDataAttributesArray(HashMap<String, Object> attrsHm) {
		StrapiResponseArray strapiResponseArrayObj = new StrapiResponseArray();
		if (attrsHm.containsKey("meta"))
			strapiResponseArrayObj.setStrapiMeta((StrapiMetadata) attrsHm.get("meta"));

		if (attrsHm.containsKey("data")) {
			List<HashMap<String, Object>> dataObjects = (List<HashMap<String, Object>>) attrsHm.get("data");
			List<Entry> entries = new ArrayList<>();
			for (HashMap<String, Object> data : dataObjects) {
				Entry entry = new Entry();
				if (data.containsKey("id"))
					;
				entry.setId((Integer) data.get("id"));
				if (data.containsKey("attributes"))
					entry.setAttributes((HashMap<String, Object>) data.get("attributes"));
				entries.add(entry);
			}
			strapiResponseArrayObj.setEntries(entries);
		}
		return strapiResponseArrayObj;
	}

	private static StrapiResponse getStrapiDataAttributes(HashMap<String, Object> attrsHm) {
		StrapiResponse strapiResponseObj = new StrapiResponse();
		if (attrsHm != null) {
			if (attrsHm.containsKey("meta"))
				strapiResponseObj.setStrapiMeta((StrapiMetadata) attrsHm.get("meta"));

			Entry entry = new Entry();
			if (attrsHm.containsKey("data")) {
				HashMap<String, Object> data = (HashMap<String, Object>) attrsHm.get("data");
				if (data != null) {
					if (data.containsKey("id"))
						;
					entry.setId((Integer) data.get("id"));
					if (data.containsKey("attributes"))
						entry.setAttributes((HashMap<String, Object>) data.get("attributes"));
					strapiResponseObj.setEntry(entry);
				}

			} else {
				if (attrsHm.containsKey("id"))
					;
				entry.setId((Integer) attrsHm.get("id"));
				if (attrsHm.containsKey("attributes")) {
					entry.setAttributes((HashMap<String, Object>) attrsHm.get("attributes"));
				}
				strapiResponseObj.setEntry(entry);
			}

			if (strapiResponseObj.getEntry() != null) {
				if (strapiResponseObj.getEntry().getAttributes() == null) {
					entry.setAttributes(attrsHm);
				}
			}

		}

		return strapiResponseObj;
	}

	public static HashMap<String, Object> getAttributes(StrapiResponse strapiResponse) {
		Entry e = strapiResponse.getEntry();
		HashMap<String, Object> attrs = e.getAttributes();
		return attrs;
	}
	
	public static String convertTsTzToIST(String tsTz) {
		Instant timestamp = Instant.parse(tsTz);
		ZonedDateTime zonedDateTimeKolkata = timestamp.atZone(ZoneId.of("+05:30"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
		String formattedString = zonedDateTimeKolkata.format(formatter);
		return formattedString;
	}

	public static String convertTsToIST(Timestamp ts) {
		Instant timestamp = Instant.ofEpochMilli(ts.getTime());
		ZonedDateTime zonedDateTimeKolkata = timestamp.atZone(ZoneId.of("+05:30"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
		String formattedString = zonedDateTimeKolkata.format(formatter);
		return formattedString;
	}

	public static int getYearFromTimestamp(Timestamp ts) {
		long timestamp = ts.getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return cal.get(Calendar.YEAR);
	}

	public static int getDifferenceInDates(Date secondDate, Date firstDate) {
		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return (int) diff;

	}

	public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);

		while (calendar.getTime().before(enddate)) {
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DATE, 1);
		}
		return dates;
	}

	public static Date formatDate(String dateStr) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static int getYearFromDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(date);
		long timestamp = date.getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR);
		return year;
	}
	

}
