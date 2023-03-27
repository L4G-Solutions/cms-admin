package com.andromeda.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andromeda.cms.model.HomePageAd;

@Service
public class HomepageAdService {
	
	@Autowired
	private DataGeneratorService dataGeneratorService;

	public void generateAd(HomePageAd homePageAd) 
	{
		dataGeneratorService.generateAd(homePageAd);
		
	}

}
