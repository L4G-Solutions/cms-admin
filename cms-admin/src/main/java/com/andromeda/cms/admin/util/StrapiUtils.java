package com.andromeda.cms.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.model.AppJson;
import com.andromeda.cms.model.Article;
import com.andromeda.cms.model.ArticleTextEditor;
import com.andromeda.cms.model.DocumentUpload;
import com.andromeda.cms.model.HomePageAd;
import com.andromeda.cms.model.ImageWithDescription;
import com.andromeda.cms.model.ElectionVote;
import com.andromeda.cms.model.PhotoBulkUpload;
import com.andromeda.cms.model.RankingDashboard;
import com.andromeda.cms.model.RankingItem;
import com.andromeda.cms.model.StoryGeographicLocation;
import com.andromeda.cms.model.StrapiArticle;
import com.andromeda.cms.model.StrapiCartoon;
import com.andromeda.cms.model.StrapiHoroscope;
import com.andromeda.cms.model.StrapiImage;
import com.andromeda.cms.model.StrapiPhotoGallery;
import com.andromeda.cms.model.StrapiResponse;
import com.andromeda.cms.model.StrapiResponseArray;
import com.andromeda.cms.model.StrapiTag;
import com.andromeda.cms.model.StrapiCategory;
import com.andromeda.cms.model.StrapiSubCategory;
import com.andromeda.cms.model.StrapiWebhookEvent;
import com.andromeda.cms.model.SubCategory;
import com.andromeda.commons.model.BaseModel;
import com.andromeda.cms.model.StrapiResponse.Entry;
import com.andromeda.cms.model.StrapiResponse.StrapiMetadata;

/**
 * Utility class to transalte StrapiResponse to respective Objects
 * 
 * @author Chaithanya
 *
 */
public class StrapiUtils {

	/**
	 * Translate StrapiWebhookEvent to StrapiArticle
	 * 
	 * @param strapiResponse
	 * @return
	 */
	public static StrapiArticle getStrapiArticle(StrapiWebhookEvent strapiWebhookEvent) {
		Timestamp eventCreatedAt = strapiWebhookEvent.getCreatedAt();
		Object entry = strapiWebhookEvent.getEntry();
		HashMap<String, Object> attrs = (HashMap<String, Object>) entry;
		StrapiArticle strapiArticle = getStrapiArticle(attrs);
		return strapiArticle;
	}

	public static StrapiCartoon getStrapiCartoon(StrapiWebhookEvent strapiWebhookEvent) {
		Object entry = strapiWebhookEvent.getEntry();
		HashMap<String, Object> attrs = (HashMap<String, Object>) entry;
		StrapiCartoon strapiCartoon = getStrapiCartoon(attrs);
		return strapiCartoon;
	}

	public static StrapiHoroscope getStrapiHoroscope(StrapiWebhookEvent strapiWebhookEvent) throws Exception {
		Object entry = strapiWebhookEvent.getEntry();
		HashMap<String, Object> attrs = (HashMap<String, Object>) entry;
		StrapiHoroscope strapiHoroscope = getStrapiHoroscope(attrs);
		return strapiHoroscope;
	}

	public static HomePageAd getHomepageAd(StrapiWebhookEvent strapiWebhookEvent) {
		Object entry = strapiWebhookEvent.getEntry();
		HashMap<String, Object> attrs = (HashMap<String, Object>) entry;
		HomePageAd homepageAd = getHomepageAd(attrs);
		return homepageAd;
	}

	public static HashMap<String, String> getStrapiMetadata(StrapiMetadata strapiMetadata) {
		HashMap<String, String> metadata = new HashMap<>();
		HashMap<String, String> pagination = strapiMetadata.getMeta();

		if (pagination != null) {
			if (pagination.containsKey("page")) {
				metadata.put("page", pagination.get("page"));
			}
			if (pagination.containsKey("pageSize")) {
				metadata.put("pageSize", pagination.get("pageSize"));
			}
			if (pagination.containsKey("pageCount")) {
				metadata.put("pageCount", pagination.get("pageCount"));
			}
			if (pagination.containsKey("start")) {
				metadata.put("start", pagination.get("start"));
			}
			if (pagination.containsKey("limit")) {
				metadata.put("limit", pagination.get("limit"));
			}
			if (pagination.containsKey("total")) {
				metadata.put("total", pagination.get("total"));
			}

		}

		return metadata;
	}

	private static StrapiHoroscope getStrapiHoroscope(HashMap<String, Object> attrs) throws Exception {
		StrapiHoroscope strapiHoroscope = new StrapiHoroscope();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		int id;
		String horoscopeId;
		String title;
		String englishTitle;
		List<StrapiTag> tags;
		String horoscopeType;
		String keywords;

		if (attrs.containsKey("Mesham_Aries")) {
			strapiHoroscope.setMeshamAries((String) attrs.get("Mesham_Aries"));
			;
		}
		if (attrs.containsKey("Vrushabam_Taurus")) {
			strapiHoroscope.setVrushabamTaurus((String) attrs.get("Vrushabam_Taurus"));
			;
		}
		if (attrs.containsKey("Mithunam_Gemini")) {
			strapiHoroscope.setMithunamGemini((String) attrs.get("Mithunam_Gemini"));
			;
		}
		if (attrs.containsKey("Karkatakam_Cancer")) {
			strapiHoroscope.setKarkatakamCancer((String) attrs.get("Karkatakam_Cancer"));
		}
		if (attrs.containsKey("Simha_Leo")) {
			strapiHoroscope.setSimhaLeo((String) attrs.get("Simha_Leo"));
		}
		if (attrs.containsKey("Kanya_Virgo")) {
			strapiHoroscope.setKanyaVirgo(((String) attrs.get("Kanya_Virgo")));
			;
		}
		if (attrs.containsKey("Tula_Libra")) {
			strapiHoroscope.setTulaLibra((String) attrs.get("Tula_Libra"));
		}
		if (attrs.containsKey("Vruschikam_Scorpio")) {
			strapiHoroscope.setVruschikamScorpio((String) attrs.get("Vruschikam_Scorpio"));
		}
		if (attrs.containsKey("Dhanassu_Sagittarius")) {
			strapiHoroscope.setDhanassuSagittarius((String) attrs.get("Dhanassu_Sagittarius"));
		}
		if (attrs.containsKey("Makaram_Capricorn")) {
			strapiHoroscope.setMakaramCapricorn((String) attrs.get("Makaram_Capricorn"));
		}
		if (attrs.containsKey("Kumbham_Aquarius")) {
			strapiHoroscope.setKumbhamAquarius((String) attrs.get("Kumbham_Aquarius"));
		}
		if (attrs.containsKey("Meenam_Pisces")) {
			strapiHoroscope.setMeenamPisces((String) attrs.get("Meenam_Pisces"));
		}
		if (attrs.containsKey("panchangam")) {
			strapiHoroscope.setPanchangam((String) attrs.get("panchangam"));
		}

		if (attrs.containsKey("id")) {
			strapiHoroscope.setId((int) attrs.get("id"));
		}
		if (attrs.containsKey("locale")) {
			strapiHoroscope.setLocale((String) attrs.get("locale"));
		}

		/*
		 * if(attrs.containsKey("publishedAt")) { String publishedAtStr = (String)
		 * attrs.get("publishedAt"); if(publishedAtStr != null) { //publishedAtStr =
		 * (String) attrs.get("updatedAt"); Timestamp ts =
		 * convertStringToTsTz(publishedAtStr); strapiHoroscope.setPublishedAt(ts);
		 * String publishedAtSm = convertTsTzToIST(publishedAtStr);
		 * strapiHoroscope.setPublishedAtSm(publishedAtSm); } }
		 */
		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiHoroscope.setCreatedAt(ts);
			strapiHoroscope.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiHoroscope.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiHoroscope.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiHoroscope.setUpdatedAtSm(updatedAtSm);
		}
		if (attrs.containsKey("startDate")) {
			String startDateStr = (String) attrs.get("startDate");
			Date date = formatter.parse(startDateStr);
			strapiHoroscope.setStartDate(date);
		}
		if (attrs.containsKey("endDate")) {
			String endDateStr = (String) attrs.get("endDate");
			Date date = formatter.parse(endDateStr);
			strapiHoroscope.setEndDate(date);
		}

		if (attrs.containsKey("horoscopeId")) {
			horoscopeId = (String) attrs.get("horoscopeId");
			strapiHoroscope.setHoroscopeId(horoscopeId);
		}

		if (attrs.containsKey("title")) {
			title = (String) attrs.get("title");
			strapiHoroscope.setTitle(title);
		}

		if (attrs.containsKey("horoscopeType")) {
			horoscopeType = (String) attrs.get("horoscopeType");
			strapiHoroscope.setHoroscopeType(horoscopeType);
		}

		if (attrs.containsKey("englishTitle")) {
			englishTitle = (String) attrs.get("englishTitle");
			strapiHoroscope.setEnglishTitle(englishTitle);
		}

		if (attrs.containsKey("keywords")) {
			keywords = (String) attrs.get("keywords");
			strapiHoroscope.setKeywords(keywords);
		}

		if (attrs.containsKey("tags")) {
			tags = new ArrayList<>();
			List<Object> tagsLs = (List<Object>) attrs.get("tags");

			for (Object tagObj : tagsLs) {
				StrapiTag tag = getStrapiTag((HashMap<String, Object>) tagObj);
				tags.add(tag);
			}
			strapiHoroscope.setTags(tags);
		}

		return strapiHoroscope;
	}
	
	public static StrapiCartoon getStrapiCartoonForCMS(HashMap<String, Object> attrs) {
		StrapiCartoon strapiCartoon = new StrapiCartoon();

		String cartoonId;
		String title;
		String englishTitle;
		StrapiImage image;
		String keywords;
		List<StrapiTag> tags;

		strapiCartoon.setPublished(true);
		
		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiCartoon.setCreatedAt(ts);
			strapiCartoon.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiCartoon.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiCartoon.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiCartoon.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("cartoonId")) {
			cartoonId = (String) attrs.get("cartoonId");
			strapiCartoon.setCartoonId(cartoonId);
		}
		
		if (attrs.containsKey("abnStoryId")) {
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiCartoon.setAbnStoryId(abnStoryId);
		}

		if (attrs.containsKey("id")) {
			int id = (int) attrs.get("id");
			strapiCartoon.setId(id);
		}

		if (attrs.containsKey("title")) {
			title = (String) attrs.get("title");
			strapiCartoon.setTitle(title);
		}

		if (attrs.containsKey("englishTitle")) {
			englishTitle = (String) attrs.get("englishTitle");
			strapiCartoon.setEnglishTitle(englishTitle);
		}

		if (attrs.containsKey("keywords")) {
			keywords = (String) attrs.get("keywords");
			strapiCartoon.setNewsKeywords(keywords);
		}
		if (attrs.containsKey("image")) {
			image = getStrapiImage((HashMap<String, Object>) attrs.get("image"));
			strapiCartoon.setImage(image);
		}
		
		if (attrs.containsKey("imageUrl")) {
			String imageUrl = StrapiConstants.MEDIA_DOMAIN_NAME  + (String) attrs.get("imageUrl");
			strapiCartoon.setImageURL(imageUrl);
		}
		
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiCartoon.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			//updatedAt = publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiCartoon.setPublishedAt(ts);
						strapiCartoon.setUpdatedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiCartoon.setPublishedAtSm(publishedAtSm);
						strapiCartoon.setUpdatedAtSm(publishedAtSm);
					}
				}
			}
		}
		
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiCartoon.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			//updatedAt = publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiCartoon.setPublishedAt(ts);
						strapiCartoon.setUpdatedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiCartoon.setPublishedAtSm(publishedAtSm);
						strapiCartoon.setUpdatedAtSm(publishedAtSm);
					}
				}
			}
		}
		
		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			strapiCartoon.setLocale(locale);
		}
		
		if (attrs.containsKey("tags")) {
			List strapiTags = new ArrayList<>();
			HashMap<String, Object> tagsHm = (HashMap<String, Object>) attrs.get("tags");
			StrapiResponseArray strapiResponseArrayObj = getStrapiDataAttributesArray(tagsHm);
			for (Entry entry : strapiResponseArrayObj.getEntries()) {
				StrapiTag tag = getStrapiTag(entry.getAttributes());
				strapiTags.add(tag);
			}
			strapiCartoon.setTags(strapiTags);
		}


		return strapiCartoon;
	}

	public static StrapiCartoon getStrapiCartoon(HashMap<String, Object> attrs) {
		StrapiCartoon strapiCartoon = new StrapiCartoon();

		String cartoonId;
		String title;
		String englishTitle;
		StrapiImage image;
		String keywords;
		List<StrapiTag> tags;

		/*
		 * if(attrs.containsKey("publishedAt")) { String publishedAtStr = (String)
		 * attrs.get("publishedAt"); if(publishedAtStr != null) { Timestamp ts =
		 * convertStringToTsTz(publishedAtStr); strapiCartoon.setPublishedAt(ts); String
		 * publishedAtSm = convertTsTzToIST(publishedAtStr);
		 * strapiCartoon.setPublishedAtSm(publishedAtSm); } }
		 */
		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			strapiCartoon.setCreatedAt(ts);
			strapiCartoon.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			strapiCartoon.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			strapiCartoon.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			strapiCartoon.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("cartoonId")) {
			cartoonId = (String) attrs.get("cartoonId");
			strapiCartoon.setCartoonId(cartoonId);
		}
		
		if (attrs.containsKey("abnStoryId")) {
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiCartoon.setAbnStoryId(abnStoryId);
		}

		if (attrs.containsKey("id")) {
			int id = (int) attrs.get("id");
			strapiCartoon.setId(id);
		}

		if (attrs.containsKey("title")) {
			title = (String) attrs.get("title");
			strapiCartoon.setTitle(title);
		}

		if (attrs.containsKey("englishTitle")) {
			englishTitle = (String) attrs.get("englishTitle");
			strapiCartoon.setEnglishTitle(englishTitle);
		}
		
		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			strapiCartoon.setLocale(locale);
		}

		if (attrs.containsKey("keywords")) {
			keywords = (String) attrs.get("keywords");
			strapiCartoon.setNewsKeywords(keywords);
		}
		if (attrs.containsKey("image")) {
			image = getStrapiImage((HashMap<String, Object>) attrs.get("image"));
			strapiCartoon.setImage(image);
		}
		
		if (attrs.containsKey("imageUrl")) {
			String imageURL = (String) attrs.get("imageUrl");
			strapiCartoon.setImageURL(imageURL);
		}
		
		if (attrs.containsKey("abnStoryId")) 
		{
			String abnStoryId = (String) attrs.get("abnStoryId");
			strapiCartoon.setAbnStoryId(abnStoryId);
			
			// publishedAt = Old publishedAt
			//updatedAt = publishedAt
			if (abnStoryId != null && !abnStoryId.isEmpty()) 
			{
				if (attrs.containsKey("publishedAt")) {
					String publishedAtStr = (String) attrs.get("publishedAt");
					if (publishedAtStr != null) {
						Timestamp ts = convertStringToTsTz(publishedAtStr);
						strapiCartoon.setPublishedAt(ts);
						strapiCartoon.setUpdatedAt(ts);
						String publishedAtSm = convertTsTzToIST(publishedAtStr);
						strapiCartoon.setPublishedAtSm(publishedAtSm);
						strapiCartoon.setUpdatedAtSm(publishedAtSm);
					}
				}
			}
		}
		
		if (attrs.containsKey("tags")) {
			tags = new ArrayList<>();
			List<Object> tagsLs = (List<Object>) attrs.get("tags");
			for (Object tagObj : tagsLs) {
				StrapiTag tag = getStrapiTag((HashMap<String, Object>) tagObj);
				tags.add(tag);
			}
			strapiCartoon.setTags(tags);
		}

		return strapiCartoon;
	}
	
	public static StrapiPhotoGallery getStrapiPhotoGalleryForCMS(HashMap<String, Object> attrs) {
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
					si = getStrapiImage(imageAttrs);
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

			if (photoBulkUploadHm.containsKey("id")) {
				int id = (int) photoBulkUploadHm.get("id");
				photoBulkUpload.setId(id);
			}

			if (photoBulkUploadHm.containsKey("primaryImage")) {
				HashMap<String, Object> primaryImageSr = (HashMap<String, Object>) photoBulkUploadHm
						.get("primaryImage");
				StrapiImage strapiPrimaryImage = getStrapiImage(primaryImageSr);
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
					StrapiImage si = getStrapiImage(bulkImageUploadHm);
					photoBulkUploadImages.add(si);
				}
				photoBulkUpload.setPhotoBulkUpload(photoBulkUploadImages);
			}
			strapiPhotoGallery.setPhotoBulkUpload(photoBulkUpload);
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
						imageUrl = StrapiConstants.MEDIA_DOMAIN_NAME+ imageUrl;
						primaryImage = new StrapiImage();
						primaryImage.setUrl(imageUrl);
						photoBulkUpload.setPrimaryImage(primaryImage);
						strapiPhotoGallery.setImageUrl(primaryImage.getUrl());
					}
					else
					{
					String imageUrl = photoSliderHm.get("imageURL");
					imageUrl = StrapiConstants.MEDIA_DOMAIN_NAME+ imageUrl;
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
	
	

	
	public static StrapiPhotoGallery getStrapiPhotoGallery(HashMap<String, Object> attrs) {
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
					si = getStrapiImage(imageAttrs);
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
					StrapiImage strapiPrimaryImage = getStrapiImage(primaryImageSr);
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
						StrapiImage si = getStrapiImage((HashMap<String, Object>) bulkImageUploadHm);
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
	
	

	public static HomePageAd getHomepageAd(HashMap<String, Object> attrs) {
		HomePageAd homePageAd = new HomePageAd();

		StrapiImage image;

		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			homePageAd.setCreatedAt(ts);
			homePageAd.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			homePageAd.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			homePageAd.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			homePageAd.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("id")) {
			int id = (int) attrs.get("id");
			homePageAd.setId(id);
		}

		if (attrs.containsKey("AdUrl")) {
			String adUrl = (String) attrs.get("AdUrl");
			homePageAd.setAdUrl(adUrl);
		}

		if (attrs.containsKey("Timer")) {
			int timer = (int) attrs.get("Timer");
			homePageAd.setTimer(timer);
		}

		if (attrs.containsKey("HtmlCode")) {
			String htmlCode = (String) attrs.get("HtmlCode");
			homePageAd.setHtmlCode(htmlCode);
		}

		if (attrs.containsKey("locale")) {
			String locale = (String) attrs.get("locale");
			homePageAd.setLocale(locale);
		}

		if (attrs.containsKey("EnableHomepageAd")) {
			Boolean enableHomepageAd = (Boolean) attrs.get("EnableHomepageAd");
			homePageAd.setEnableHomepageAd(enableHomepageAd);
		}
		if (attrs.containsKey("AdImage")) {
			image = getStrapiImage((HashMap<String, Object>) attrs.get("AdImage"));
			homePageAd.setAdImage(image);
		}

		return homePageAd;
	}
	
	

	public static StrapiArticle getStrapiArticleFromCMS(HashMap<String, Object> attrs) {
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
			StrapiImage strapiPrimaryImage = getStrapiImage(primaryImageSr);
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
			{
				if(attrs.get("speedNews") != null)
					{
					Boolean speedNews = (boolean) attrs.get("speedNews");
					if(speedNews != null)
						strapiArticle.setSpeedNews(speedNews);
					}
			}
		
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
					strapiArticle.setImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME +  primaryImageUrl);
					strapiArticle.setSmallPrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setMediumPrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setLargePrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setThumbnailPrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
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

	/**
	 * Translate Strapi Response to StrapiArticle
	 * 
	 * @param strapiResponse
	 * @return
	 */
	public static StrapiArticle getStrapiArticle(HashMap<String, Object> attrs) {
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
		if (attrs.containsKey("speedNews"))
		{
			if(attrs.get("speedNews") != null)
				{
				Boolean speedNews = (boolean) attrs.get("speedNews");
				if(speedNews != null)
					strapiArticle.setSpeedNews(speedNews);
				}
			
		}
			
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
			StrapiImage strapiPrimaryImage = getStrapiImage(primaryImageSr);
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
		

		if (attrs.containsKey("contentType"))
			strapiArticle.setContentType((String) attrs.get("contentType"));

		if (attrs.containsKey("abnStoryId"))
			strapiArticle.setAbnStoryId((String) attrs.get("abnStoryId"));

		if (attrs.containsKey("author"))
			strapiArticle.setAuthor((String) attrs.get("author"));

		if (attrs.containsKey("articleText")) {
			articleTextEditors = getArticleTextEditors(attrs.get("articleText"));
			if(articleTextEditors != null && articleTextEditors.size() > 0)
			{
				firstArticleTextEditor = articleTextEditors.get(0);
				strapiArticle.setArticleTextEditors(articleTextEditors);
			}
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
					strapiArticle.setImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME +  primaryImageUrl);
					strapiArticle.setSmallPrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setMediumPrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setLargePrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
					strapiArticle.setThumbnailPrimaryImageUrl(StrapiConstants.MEDIA_DOMAIN_NAME + primaryImageUrl);
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
				StrapiArticle relatedArticle = getStrapiArticle((HashMap<String, Object>) articleStr);
				relatedArticles.add(relatedArticle);
			}

			strapiArticle.setArticles(relatedArticles);
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
				StrapiImage contentImage = getStrapiImage(primaryImageSr);
				articleTextEditor.setContentImage(contentImage);
			}
			/*
			 * if(articleTextEditorHm.containsKey("articles")) { List<StrapiArticle>
			 * relatedArticles = new ArrayList<>(); HashMap<String, Object> articlesHm =
			 * (HashMap<String, Object>) articleTextEditorHm.get("articles");
			 * StrapiResponseArray strapiResponseArrayObj =
			 * getStrapiDataAttributesArray(articlesHm); for (Entry entry :
			 * strapiResponseArrayObj.getEntries()) { StrapiArticle relatedArticle =
			 * getStrapiArticle(entry.getAttributes()); relatedArticles.add(relatedArticle);
			 * 
			 * } articleTextEditor.setArticles(relatedArticles); }
			 */

			if (articleTextEditorHm.containsKey("articles")) {
				List<StrapiArticle> relatedArticles = new ArrayList<>();
				List<Object> articlesLs = (List<Object>) articleTextEditorHm.get("articles");

				for (Object articleStr : articlesLs) {
					StrapiArticle relatedArticle = getStrapiArticle((HashMap<String, Object>) articleStr);
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

	public static StrapiImage getStrapiImage(HashMap<String, Object> primaryImageAtrrsHm) {
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
					url = url.replace("media-abn.s3.ap-south-1.amazonaws.com", "media.andhrajyothy.com");
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

	public static List<String> getLinesFromExcel(String fileLocation) throws Exception {
		FileInputStream file = new FileInputStream(new File(fileLocation));
		Workbook workbook = new XSSFWorkbook(fileLocation);
		Sheet sheet = workbook.getSheetAt(0);
		List<String> allLines = new ArrayList<>();
		for (Row row : sheet) {
			String rowString = "";
			for (Cell cell : row) {
				rowString += cell.getRichStringCellValue().getString() + ",";
			}
			rowString = rowString.substring(0, rowString.length() - 1);
			allLines.add(rowString);
		}

		return allLines;

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
	
	public static RankingDashboard getRankingDashboard(StrapiWebhookEvent strapiWebhookEvent) {
		Object entry = strapiWebhookEvent.getEntry();
		HashMap<String, Object> attrs = (HashMap<String, Object>) entry;
		RankingDashboard rankingDashboard = getRankingDashboard(attrs);
		return rankingDashboard;
	}

	private static RankingDashboard getRankingDashboard(HashMap<String, Object> attrs) {
		RankingDashboard rankingDashboard = new RankingDashboard();
		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			rankingDashboard.setCreatedAt(ts);
			rankingDashboard.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			rankingDashboard.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			rankingDashboard.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			rankingDashboard.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("id")) {
			int id = (int) attrs.get("id");
			rankingDashboard.setId(id);
		}
		
		if (attrs.containsKey("TopNewsRanking")) {
			List<RankingItem> topNewsRankingItems = new ArrayList<>();
			topNewsRankingItems = getTopNewsRankingItems(attrs.get("TopNewsRanking"));
			rankingDashboard.setRankingItems(topNewsRankingItems);
		}

		return rankingDashboard;
	}

	private static List<RankingItem> getTopNewsRankingItems(Object object) {
		List<RankingItem> returnList = new ArrayList<>();
		List<Object> rankingItems = (List<Object>) object;


		for (Object rankingItemObj : rankingItems) {
			HashMap<String, Object> rankingItemHm = (HashMap<String, Object>) rankingItemObj;
			RankingItem rankingItem = new RankingItem();
			if (rankingItemHm.containsKey("id"))
				rankingItem.setId(((int)rankingItemHm.get("id")));
			if (rankingItemHm.containsKey("storyId"))
				rankingItem.setStoryId(Long.parseLong((String) rankingItemHm.get("storyId")));
			if (rankingItemHm.containsKey("storyType"))
				rankingItem.setStoryType((String)rankingItemHm.get("storyType"));
			if (rankingItemHm.containsKey("website"))
				rankingItem.setWebsite((String)rankingItemHm.get("website"));
			
			returnList.add(rankingItem);
		}	
				
		return returnList;
	}

	public static ElectionVote getElectionVote(StrapiWebhookEvent strapiWebhookEvent) {
		Object entry = strapiWebhookEvent.getEntry();
		HashMap<String, Object> attrs = (HashMap<String, Object>) entry;
		ElectionVote munugodeElectionVote = getElectionVote(attrs);
		return munugodeElectionVote;
	}

	private static ElectionVote getElectionVote(HashMap<String, Object> attrs) {
		ElectionVote munugodeElectionVote = new ElectionVote();


		if (attrs.containsKey("createdAt")) {
			String createdAtStr = (String) attrs.get("createdAt");
			Timestamp ts = convertStringToTsTz(createdAtStr);
			munugodeElectionVote.setCreatedAt(ts);
			munugodeElectionVote.setPublishedAt(ts);
			String publishedAtSm = convertTsTzToIST(createdAtStr);
			munugodeElectionVote.setPublishedAtSm(publishedAtSm);
		}
		if (attrs.containsKey("updatedAt")) {
			String updatedAtStr = (String) attrs.get("updatedAt");
			Timestamp ts = convertStringToTsTz(updatedAtStr);
			munugodeElectionVote.setUpdatedAt(ts);
			String updatedAtSm = convertTsTzToIST(updatedAtStr);
			munugodeElectionVote.setUpdatedAtSm(updatedAtSm);
		}

		if (attrs.containsKey("id")) {
			int id = (int) attrs.get("id");
			munugodeElectionVote.setId(id);
		}

		if (attrs.containsKey("RoundsCompleted")) {
			int roundsCompleted = (int) attrs.get("RoundsCompleted");
			munugodeElectionVote.setRoundsCompleted(roundsCompleted);
		}

		if (attrs.containsKey("votes")) {
			HashMap<String, Integer> votesHm = (HashMap<String, Integer>) attrs.get("votes");
			HashMap<String, Integer> votes = new HashMap<>();

			for (String key : votesHm.keySet()) {
				if(!key.equalsIgnoreCase("id"))
				{
					//String valueStr = votesHm.get(key);
					Integer value = votesHm.get(key);
					votes.put(key, value);
				}
			}
			
			munugodeElectionVote.setVotes(votes);
		}

		return munugodeElectionVote;
	}
	
	public static AppJson generateJSON(Object obj, String modelType) {
		AppJson modelJson = new AppJson();
		modelJson.setVersion("1.0");
		modelJson.setModelType(modelType);
		modelJson.setModel(obj);
		
		return modelJson;
	}
}
