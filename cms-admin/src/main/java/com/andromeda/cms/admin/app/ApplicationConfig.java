package com.andromeda.cms.admin.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CJArticleDao;
import com.andromeda.cms.dao.CJCategoryDao;
import com.andromeda.cms.dao.CJLiveBlogDao;
import com.andromeda.cms.dao.CJLiveBlogRedirectionUrlDao;
import com.andromeda.cms.dao.CJPhotoGalleryDao;
import com.andromeda.cms.dao.CJRedirectionUrlDao;
import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.dao.CartoonDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.HoroscopeDao;
import com.andromeda.cms.dao.PhotoGalleryDao;
import com.andromeda.cms.dao.RedirectionUrlDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.dao.ValueCmsProxyRepository;
import com.andromeda.cms.service.CmsProxyService;
import com.andromeda.cms.sitemap.dao.CJSitemapDateDao;
import com.andromeda.cms.sitemap.dao.CJSitemapLocationDao;
import com.andromeda.cms.sitemap.dao.CJSitemapPostLocationDao;
import com.andromeda.cms.sitemap.dao.SitemapDateDao;
import com.andromeda.cms.sitemap.dao.SitemapLocationDao;
import com.andromeda.cms.sitemap.dao.SitemapPostLocationDao;
import com.andromeda.migration.dao.ArticleStatusDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



/**
 * Application configurations.
 * 
 * @author Prakash K
 * @date 2020-09-14
 *
 */
@Configuration
public class ApplicationConfig
{
	@Value("${database.redis.hostname}")
	private String redisHostName;

	@Value("${database.redis.port}")
	private int redisPort;




	@Primary
	@Bean
	JedisConnectionFactory jedisConnectionFactory0()
	{
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(redisHostName);
		configuration.setPort(redisPort);
		configuration.setDatabase(0);

		return new JedisConnectionFactory(configuration);
	}

	@Bean
	JedisConnectionFactory jedisConnectionFactory1()
	{
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(redisHostName);
		configuration.setPort(redisPort);
		configuration.setDatabase(1);

		return new JedisConnectionFactory(configuration);
	}
	
	

	@Bean
	public StringRedisSerializer stringRedisSerializer()
	{
		return new StringRedisSerializer();
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate0()
	{
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory0());
		// For Strings
		redisTemplate.setKeySerializer(stringRedisSerializer());
		redisTemplate.setHashKeySerializer(stringRedisSerializer());
		redisTemplate.setValueSerializer(stringRedisSerializer());
		redisTemplate.setHashValueSerializer(stringRedisSerializer());

		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate()
	{
		return redisTemplate0();
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate1()
	{
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory1());
		// For Strings
		redisTemplate.setKeySerializer(stringRedisSerializer());
		redisTemplate.setHashKeySerializer(stringRedisSerializer());
		redisTemplate.setValueSerializer(stringRedisSerializer());
		redisTemplate.setHashValueSerializer(stringRedisSerializer());

		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}


	
	/*@Bean
	@Primary
	public DataSource postgresDataSource()
	{
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName("org.postgresql.Driver");
		String url = String.format("jdbc:postgresql://10.1.15.219:5432/abndemo");
		//String url = String.format("jdbc:postgresql://43.204.83.217:5432/abndemo");
		dataSource.setUrl(url);
		dataSource.setUsername("abn");
		dataSource.setPassword("LbgkGVs8Uhgfx3J3");
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("socketTimeout", "10");
		dataSource.setConnectionProperties(connectionProperties);

		return dataSource;
	}*/
	
	@Bean
	@Primary
	public DataSource postgresDataSource()
	{
		String driverClassName = "org.postgresql.Driver";
		String url = "jdbc:postgresql://10.1.15.219:5432/abndemo";
		String username = "abn";
		String password = "LbgkGVs8Uhgfx3J3";
		String poolName = "pg-cms-pool";

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setPoolName(poolName);
		config.setMaximumPoolSize(25);

		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}

	@Bean
	@Primary
	public SqlSessionFactory postgresSqlSessionFactory() throws Exception
	{
		String configLocation = "sqlMapConfig.xml";
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(postgresDataSource());
		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(configLocation));

		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate postgresSqlSessionTemplate() throws Exception
	{
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(postgresSqlSessionFactory());
		return sqlSessionTemplate;
	}
	
	@Bean
	public ArticleDao sitemapArticleDao() throws Exception
	{
		ArticleDao sitemapArticleDao = new ArticleDao();
		sitemapArticleDao.setSqlSessionFactory(postgresSqlSessionFactory());
		sitemapArticleDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return sitemapArticleDao;
	}
	
	@Bean
	public CJArticleDao cjArticleDao() throws Exception
	{
		CJArticleDao cjArticleDao = new CJArticleDao();
		cjArticleDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjArticleDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjArticleDao;
	}
	
	@Bean
	public CJLiveBlogDao cjLiveBlogDao() throws Exception
	{
		CJLiveBlogDao cjLiveBlogDao = new CJLiveBlogDao();
		cjLiveBlogDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjLiveBlogDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjLiveBlogDao;
	}
	
	@Bean
	public RedirectionUrlDao redirectionUrlDao() throws Exception
	{
		RedirectionUrlDao redirectionUrlDao = new RedirectionUrlDao();
		redirectionUrlDao.setSqlSessionFactory(postgresSqlSessionFactory());
		redirectionUrlDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return redirectionUrlDao;
	}
	
	@Bean
	public CJRedirectionUrlDao cjRedirectionUrlDao() throws Exception
	{
		CJRedirectionUrlDao cjRedirectionUrlDao = new CJRedirectionUrlDao();
		cjRedirectionUrlDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjRedirectionUrlDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjRedirectionUrlDao;
	}
	
	@Bean
	public CJLiveBlogRedirectionUrlDao cjLiveBlogRedirectionUrlDao() throws Exception
	{
		CJLiveBlogRedirectionUrlDao cjLiveBlogRedirectionUrlDao = new CJLiveBlogRedirectionUrlDao();
		cjLiveBlogRedirectionUrlDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjLiveBlogRedirectionUrlDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjLiveBlogRedirectionUrlDao;
	}
	
	@Bean
	public ArticleStatusDao articleStatusDao() throws Exception
	{
		ArticleStatusDao articleStatusDao = new ArticleStatusDao();
		articleStatusDao.setSqlSessionFactory(postgresSqlSessionFactory());
		articleStatusDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return articleStatusDao;
	}
	
	@Bean
	public SitemapDateDao sitemapDateDao() throws Exception
	{
		SitemapDateDao sitemapDateDao = new SitemapDateDao();
		sitemapDateDao.setSqlSessionFactory(postgresSqlSessionFactory());
		sitemapDateDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return sitemapDateDao;
	}
	
	@Bean
	public CJSitemapDateDao cjSitemapDateDao() throws Exception
	{
		CJSitemapDateDao cjSitemapDateDao = new CJSitemapDateDao();
		cjSitemapDateDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjSitemapDateDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjSitemapDateDao;
	}
	
	@Bean
	public CJPhotoGalleryDao cjPhotoGalleryDao() throws Exception
	{
		CJPhotoGalleryDao cjPhotoGalleryDao = new CJPhotoGalleryDao();
		cjPhotoGalleryDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjPhotoGalleryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjPhotoGalleryDao;
	}
	
	@Bean
	public PhotoGalleryDao photoGalleryDao() throws Exception
	{
		PhotoGalleryDao photoGalleryDao = new PhotoGalleryDao();
		photoGalleryDao.setSqlSessionFactory(postgresSqlSessionFactory());
		photoGalleryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return photoGalleryDao;
	}
	
	@Bean
	public CategoryDao sitemapCategoryDao() throws Exception
	{
		CategoryDao sitemapCategoryDao = new CategoryDao();
		sitemapCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
		sitemapCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return sitemapCategoryDao;
	}
	
	@Bean
	public CJCategoryDao cjCategoryDao() throws Exception
	{
		CJCategoryDao cjCategoryDao = new CJCategoryDao();
		cjCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjCategoryDao;
	}
	
	@Bean
	public SubCategoryDao sitemapSubCategoryDao() throws Exception
	{
		SubCategoryDao sitemapSubCategoryDao = new SubCategoryDao();
		sitemapSubCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
		sitemapSubCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return sitemapSubCategoryDao;
	}
	
	@Bean
	public CJSubCategoryDao cjSubCategoryDao() throws Exception
	{
		CJSubCategoryDao cjSubCategoryDao = new CJSubCategoryDao();
		cjSubCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjSubCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjSubCategoryDao;
	}
	
	@Bean
	public CartoonDao cartoonDao() throws Exception
	{
		CartoonDao cartoonDao = new CartoonDao();
		cartoonDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cartoonDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cartoonDao;
	}
	
	@Bean
	public HoroscopeDao horoscopeDao() throws Exception
	{
		HoroscopeDao horoscopeDao = new HoroscopeDao();
		horoscopeDao.setSqlSessionFactory(postgresSqlSessionFactory());
		horoscopeDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return horoscopeDao;
	}
	
	@Bean
	public SitemapLocationDao sitemapLocationDao() throws Exception
	{
		SitemapLocationDao sitemapLocationDao = new SitemapLocationDao();
		sitemapLocationDao.setSqlSessionFactory(postgresSqlSessionFactory());
		sitemapLocationDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return sitemapLocationDao;
	}
	
	@Bean
	public CJSitemapLocationDao cjSitemapLocationDao() throws Exception
	{
		CJSitemapLocationDao cjSitemapLocationDao = new CJSitemapLocationDao();
		cjSitemapLocationDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjSitemapLocationDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjSitemapLocationDao;
	}
	
	@Bean
	public SitemapPostLocationDao sitemapPostLocationDao() throws Exception
	{
		SitemapPostLocationDao sitemapPostLocationDao = new SitemapPostLocationDao();
		sitemapPostLocationDao.setSqlSessionFactory(postgresSqlSessionFactory());
		sitemapPostLocationDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return sitemapPostLocationDao;
	}

	@Bean
	public CJSitemapPostLocationDao cjSitemapPostLocationDao() throws Exception
	{
		CJSitemapPostLocationDao cjSitemapPostLocationDao = new CJSitemapPostLocationDao();
		cjSitemapPostLocationDao.setSqlSessionFactory(postgresSqlSessionFactory());
		cjSitemapPostLocationDao.setSqlSessionTemplate(postgresSqlSessionTemplate());

		return cjSitemapPostLocationDao;
	}

	@Bean
	public ValueCmsProxyRepository repository()
	{
		ValueCmsProxyRepository repository = new ValueCmsProxyRepository();
		repository.setRedisTemplate(redisTemplate0());
		repository.setListOps(redisTemplate0().opsForList());
		return repository;
	}
	
	@Bean
	public ValueCmsProxyRepository backupRepository()
	{
		ValueCmsProxyRepository repository = new ValueCmsProxyRepository();
		repository.setRedisTemplate(redisTemplate1());

		return repository;
	}


	@Bean
	public CmsProxyService cmsProxyService()
	{
		CmsProxyService service = new CmsProxyService();
		service.setRepository(repository());
		service.setBackupRepository(backupRepository());
		return service;
	}
}
