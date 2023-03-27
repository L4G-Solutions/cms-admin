package test.com.andromeda.cms.admin.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.andromeda.cms.dao.ValueCmsProxyRepository;
import com.andromeda.cms.service.CmsProxyService;
import com.andromeda.commons.util.PropertiesUtils;

public class RedisTestContextUtils
{
	private static StringRedisSerializer stringRedisSerializer;
	private static JedisConnectionFactory jedisConnectionFactory;
	private static RedisTemplate<String, String> redisTemplate;
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

	public static JedisConnectionFactory getJedisConnectionFactory() throws IOException
	{
		if (jedisConnectionFactory == null)
		{
			Properties prop = PropertiesUtils.readPropertiesFile("application.properties");
			//Properties prop = PropertiesUtils.readPropertiesFile("src/main/resources/application.properties");
			String redisHostName = prop.getProperty("database.redis.hostname");
			String redisPort = prop.getProperty("database.redis.port");
			RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
			configuration.setHostName(redisHostName);
			configuration.setPort( Integer.valueOf(redisPort));
			configuration.setDatabase(0);

			return new JedisConnectionFactory(configuration);
		}

		return jedisConnectionFactory;
	}

	public static RedisTemplate<String, String> getRedisTemplate() throws IOException
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


	public static ValueCmsProxyRepository getRepository() throws IOException
	{
		if (repository == null)
		{
			repository = new ValueCmsProxyRepository();
			repository.setRedisTemplate(getRedisTemplate());
			repository.setListOps(getRedisTemplate().opsForList());
		}

		return repository;
	}

	public static CmsProxyService getCmsProxyService() throws Exception
	{
		if (cmsProxyService == null)
		{
			cmsProxyService = new CmsProxyService();
			cmsProxyService.setRepository(getRepository());
		}

		return cmsProxyService;
	}
}
