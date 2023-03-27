package test.com.andromeda.cms.admin.service;

import com.andromeda.cms.collector.StrapiTagCollector;

import test.com.andromeda.cms.admin.util.TestContextUtils;

public class StrapiTagServiceTest {

	public static void main(String[] args) throws Exception {
		StrapiTagServiceTest strapiTagServiceTest = new StrapiTagServiceTest();
		strapiTagServiceTest.collectTags();

	}

	private void collectTags() throws Exception {
		StrapiTagCollector strapiTagCollector = TestContextUtils.getStrapiTagCollector();
		strapiTagCollector.collectTagsFromFile();
		
	}

}
