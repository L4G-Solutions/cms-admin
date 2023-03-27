package com.andromeda.cms.admin.util;

import com.andromeda.commons.util.FileNDirUtils;


public class TestUtils {
	
	public static String getStrapiCredentials()
	{
		String fileName = "src/test/data/strapi_credentials.json";
		//String fileName = "data/coursera_config/coursera_l4g.json";
		String content = FileNDirUtils.getFileString(fileName);
		System.out.println(content);
		return content;
	}

}
