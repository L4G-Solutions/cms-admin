package com.andromeda.cms.admin.util;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.andromeda.cms.dao.ValueCmsProxyRepository;
import com.andromeda.cms.service.CmsProxyService;

public class RedisTestContextUtils
{
	private static StringRedisSerializer stringRedisSerializer;
	private static JedisConnectionFactory jedisConnectionFactory;
	private static RedisTemplate<String, String> redisTemplate;
	// private static HashCmsProxyRepository hashCmsProxyRepository;
	private static ValueCmsProxyRepository repository;
	private static CmsProxyService cmsProxyService;

	public static StringRedisSerializer getStringRedisSerializer()
	{
		if (stringRedisSerializer == null)
		{
			stringRedisSerializer = new StringRedisSerializer();
		}

		return stringRedisSerializer;
	}

	public static JedisConnectionFactory getJedisConnectionFactory()
	{
		if (jedisConnectionFactory == null)
		{
			RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
			configuration.setHostName("43.204.83.217");
			configuration.setPort(6379);
			configuration.setDatabase(1);

			return new JedisConnectionFactory(configuration);
		}

		return jedisConnectionFactory;
	}

	public static RedisTemplate<String, String> getRedisTemplate()
	{
		if (redisTemplate == null)
		{
			redisTemplate = new RedisTemplate<String, String>();
			redisTemplate.setConnectionFactory(getJedisConnectionFactory());
			redisTemplate.setKeySerializer(getStringRedisSerializer());
			redisTemplate.setHashKeySerializer(getStringRedisSerializer());
			redisTemplate.setValueSerializer(getStringRedisSerializer());
			redisTemplate.setHashValueSerializer(getStringRedisSerializer());
			redisTemplate.afterPropertiesSet();
		}

		return redisTemplate;
	}

	// public static HashCmsProxyRepository getHashCmsProxyRepository()
	// {
	// if (hashCmsProxyRepository == null)
	// {
	// hashCmsProxyRepository = new HashCmsProxyRepository();
	// hashCmsProxyRepository.setRedisTemplate(getRedisTemplate());
	// }
	//
	// return hashCmsProxyRepository;
	// }

	public static ValueCmsProxyRepository getRepository()
	{
		if (repository == null)
		{
			repository = new ValueCmsProxyRepository();
			repository.setRedisTemplate(getRedisTemplate());
		}

		return repository;
	}

	public static CmsProxyService getCmsProxyService()
	{
		if (cmsProxyService == null)
		{
			cmsProxyService = new CmsProxyService();
			// cmsProxyService.setHashCmsProxyRepository(getHashCmsProxyRepository());
			cmsProxyService.setRepository(getRepository());
		}

		return cmsProxyService;
	}
}
