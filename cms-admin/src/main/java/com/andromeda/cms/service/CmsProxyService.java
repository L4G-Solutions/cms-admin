package com.andromeda.cms.service;

import java.util.List;
import com.andromeda.cms.dao.ValueCmsProxyRepository;

public class CmsProxyService
{

	private ValueCmsProxyRepository repository;
	private ValueCmsProxyRepository backupRepository;


	public void setRepository(ValueCmsProxyRepository repository)
	{
		this.repository = repository;
	}
	
	public void setBackupRepository(ValueCmsProxyRepository backupRepository)
	{
		this.backupRepository = backupRepository;
	}

	public void save(String key, String data)
	{
		repository.save(key, data);
	}

	public String get(String key) throws Exception
	{
		return repository.get(key);
	}

	public void delete(String key)
	{
		repository.delete(key);
	}

	public void deleteAll(String key)
	{
		repository.delete(key);
		backupRepository.delete(key);
	}
	
	public void saveToList(String key, List<String> data)
	{
		repository.addAllToList(key, data);
	}
	
	public void leftPushToList(String key, String data)
	{
		repository.addToList(key, data);
	}
	
	public List<String> getList(String key, int start, int end)
	{
		return repository.getList(key, start, end);
	}
	
	public String getFromList(String key, int index)
	{
		return repository.get(key, index);
	}
	
	public void removeFromList(String key)
	{
		repository.removeFromList(key);
	}
	
	public Long getListLength(String key)
	{
		return repository.getListLength(key);
	}
	
	public void checkAndPop(String key, Long length)
	{
		Long existingLength = repository.getListLength(key);
		
		while(existingLength > length)
		{
			repository.removeFromList(key);
		}
	}
}
