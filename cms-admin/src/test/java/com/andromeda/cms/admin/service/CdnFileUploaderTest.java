package com.andromeda.cms.admin.service;

import java.io.IOException;

import com.andromeda.cms.admin.util.TestContextUtils;
import com.andromeda.cms.service.DataGeneratorService;

public class CdnFileUploaderTest {

	public static void main(String[] args) throws IOException {
		DataGeneratorService dataGeneratorService = TestContextUtils.getDataGeneratorService();
		dataGeneratorService.uploadFile("app-ads.txt", "output/app-ads.txt");
	}

}
