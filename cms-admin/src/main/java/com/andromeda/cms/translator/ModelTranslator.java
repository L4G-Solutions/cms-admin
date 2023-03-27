package com.andromeda.cms.translator;

import com.andromeda.cms.model.ATStory;
import com.andromeda.cms.model.StrapiTestArticle;

public class ModelTranslator
{
	public StrapiTestArticle translateAtStoryToStarpiArticle(ATStory aTStory)
	{
		StrapiTestArticle strapiArticle = null;
		if (aTStory != null)
		{
			strapiArticle = new StrapiTestArticle();
			strapiArticle.setId(aTStory.getStoryId());
			strapiArticle.setHeading(aTStory.getEnglishTitle());
		}
		return strapiArticle;
	}
}
