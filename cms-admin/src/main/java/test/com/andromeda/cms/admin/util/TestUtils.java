package test.com.andromeda.cms.admin.util;

import com.andromeda.commons.util.FileNDirUtils;


public class TestUtils {
	
	public static String getStrapiCredentials()
	{
		//String fileName = "src/test/data/strapi/strapi_credentials.json";
		String fileName = "data/strapi/strapi_credentials.json";
		String content = FileNDirUtils.getFileString(fileName);
		System.out.println(content);
		return content;
	}

}
