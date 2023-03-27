package com.andromeda.cms.admin.service;

import com.andromeda.cms.service.CmsProxyService;

import test.com.andromeda.cms.admin.util.RedisTestContextUtils;

class CmsProxyServiceTest
{
	public static void main(String args[]) throws Exception
	{
		CmsProxyService cmsProxyService = RedisTestContextUtils.getCmsProxyService();
		cmsProxyService.save("key9", "Sample Data1");
		cmsProxyService.save("key10", "Sample Data2");

		String result = cmsProxyService.get("key9");
		System.out.println("Result: " + result);

		result = cmsProxyService.get("key10");
		System.out.println("Result: " + result);
		
		result = cmsProxyService.get("key3");
		System.out.println("Result: " + result);
	}
}
