package com.andromeda.cms.admin.util;

import java.beans.PropertyVetoException;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.andromeda.cms.collector.ATStoryCollector;
import com.andromeda.cms.controller.CategoryController;
import com.andromeda.cms.dao.ATStoryDao;
import com.andromeda.cms.dao.ArticleDao;
import com.andromeda.cms.dao.CJArticleDao;
import com.andromeda.cms.dao.CJCategoryDao;
import com.andromeda.cms.dao.CJSubCategoryDao;
import com.andromeda.cms.dao.CartoonDao;
import com.andromeda.cms.dao.CategoryDao;
import com.andromeda.cms.dao.HoroscopeDao;
import com.andromeda.cms.dao.SubCategoryDao;
import com.andromeda.cms.defs.StrapiConstants;
import com.andromeda.cms.service.ATStoryService;
import com.andromeda.cms.service.ArticleService;
import com.andromeda.cms.service.CJArticleService;
import com.andromeda.cms.service.CJCategoryService;
import com.andromeda.cms.service.CJDataGeneratorService;
import com.andromeda.cms.service.CJSubCategoryService;
import com.andromeda.cms.service.CartoonService;
import com.andromeda.cms.service.CategoryService;
import com.andromeda.cms.service.CmsProxyService;
import com.andromeda.cms.service.DataGeneratorService;
import com.andromeda.cms.service.HoroscopeService;
import com.andromeda.cms.service.StrapiArticleService;
import com.andromeda.cms.service.StrapiCategoryService;
import com.andromeda.cms.service.StrapiContentService;
import com.andromeda.cms.service.StrapiSubCategoryService;
import com.andromeda.cms.service.StrapiWebhooksService;
import com.andromeda.cms.service.SubCategoryService;
import com.andromeda.cms.sitemap.service.SitemapPageService;
import com.andromeda.cms.translator.ModelTranslator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author Prakash K
 * @date 2020-03-31
 *
 */
public class TestContextUtils
{
	private static StrapiContentService strapiReadService = null;

	private static DataSource msServerDataSource = null;
	private static DataSource h2MemDataSource = null;
	private static DataSource h2FileDataSource = null;

	private static SqlSessionFactory msServerSqlSessionFactory = null;
	private static SqlSessionFactory h2MemSqlSessionFactory = null;
	private static SqlSessionFactory h2FileSqlSessionFactory = null;

	private static SqlSessionTemplate msServerSqlSessionTemplate = null;

	private static ATStoryDao aTStoryDao;
	private static ATStoryService aTStoryService;
	private static ATStoryCollector aTStoryCollector = null;
	private static ModelTranslator modelTranslator = null;
	private static StrapiContentService strapiContentService = null;
	private static StrapiArticleService strapiArticleService = null;
	private static StrapiCategoryService strapiCategoryService = null;
	private static StrapiSubCategoryService strapiSubCategoryService = null;
	private static StrapiWebhooksService strapiWebhooksService = null;
	
	public static CJArticleDao cjArticleDao = null;
	public static CJCategoryDao cjCategoryDao = null;
	public static CJSubCategoryDao cjSubCategoryDao = null;
	
	public static ArticleDao sitemapArticleDao = null;
	public static CategoryDao sitemapCategoryDao = null;
	public static SubCategoryDao sitemapSubCategoryDao = null;
	public static CartoonDao sitemapCartoonDao = null;
	public static HoroscopeDao sitemapHoroscopeDao = null;
	
	public static CJArticleService cjArticleService = null;
	public static CJCategoryService cjCategoryService = null;
	public static CJSubCategoryService cjSubCategoryService = null;
	
	public static ArticleService sitemapArticleService = null;
	public static CategoryService sitemapCategoryService = null;
	public static SubCategoryService sitemapSubCategoryService = null;
	public static CartoonService sitemapCartoonService = null;
	public static HoroscopeService sitemapHoroscopeService = null;
	
	public static DataGeneratorService dataGeneratorService = null;
	public static CJDataGeneratorService cjDataGeneratorService = null;
	
	public static DataSource getMsServerDataSource() throws Exception
	{
		if (msServerDataSource == null)
		{
			String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			// String dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource";
			String url = "jdbc:sqlserver://13.235.73.77\\AJNNEWS";
			String username = "SA";
			String password = "L4G@2022";
			String poolName = "pg-skills-pool";

			HikariConfig config = new HikariConfig();
			// config.setDataSourceClassName(dataSourceClassName);
			config.setDriverClassName(driverClassName);
			config.setJdbcUrl(url);
			config.setUsername(username);
			config.setPassword(password);
			config.setPoolName(poolName);
			config.setMaximumPoolSize(10);

			HikariDataSource dataSource = new HikariDataSource(config);
			msServerDataSource = dataSource;
		}

		return msServerDataSource;
	}
	

	public static SqlSessionFactory getMsSqlServerSessionFactory() throws Exception
	{
		if (msServerSqlSessionFactory == null)
		{
			String path = "src/main/resources/MsSqlServerMapConfig.xml";
			Resource configLocation = new FileSystemResource(path);

			SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
			sqlSessionFactoryBean.setDataSource(getMsServerDataSource());
			sqlSessionFactoryBean.setConfigLocation(configLocation);

			msServerSqlSessionFactory = sqlSessionFactoryBean.getObject();
		}

		return msServerSqlSessionFactory;
	}

	public static SqlSessionTemplate getMsSqlServerSqlSessionTemplate() throws Exception
	{
		if (msServerSqlSessionTemplate == null)
		{
			msServerSqlSessionTemplate = new SqlSessionTemplate(getMsSqlServerSessionFactory());
		}

		return msServerSqlSessionTemplate;
	}
	
	
	public static DataSource postgresDataSource() throws PropertyVetoException
	{
		String driverClassName = "org.postgresql.Driver";
		String url = "jdbc:postgresql://65.0.129.47:5432/abndemo";
		String username = "abn";
		String password = "LbgkGVs8Uhgfx3J3";
		String poolName = "pg-skills-pool";

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setPoolName(poolName);
		config.setMaximumPoolSize(10);

		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}

	
	public static SqlSessionFactory postgresSqlSessionFactory() throws Exception
	{
		String configLocation = "sqlMapConfig.xml";
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(postgresDataSource());
		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(configLocation));

		return sqlSessionFactoryBean.getObject();
	}


	public static SqlSessionTemplate postgresSqlSessionTemplate() throws Exception
	{
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(postgresSqlSessionFactory());

		return sqlSessionTemplate;
	}
	
	public static ArticleDao getSitemapArticleDao() throws Exception
	{
		if(sitemapArticleDao == null)
		{
			sitemapArticleDao = new ArticleDao();
			sitemapArticleDao.setSqlSessionFactory(postgresSqlSessionFactory());
			sitemapArticleDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return sitemapArticleDao;
	}
	
	public static CJArticleDao getCjArticleDao() throws Exception
	{
		if(cjArticleDao == null)
		{
			cjArticleDao = new CJArticleDao();
			cjArticleDao.setSqlSessionFactory(postgresSqlSessionFactory());
			cjArticleDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return cjArticleDao;
	}
	
	public static CategoryDao getSitemapCategoryDao() throws Exception
	{
		if(sitemapCategoryDao == null)
		{
			sitemapCategoryDao = new CategoryDao();
			sitemapCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
			sitemapCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return sitemapCategoryDao;
	}
	
	public static CJCategoryDao getCjCategoryDao() throws Exception
	{
		if(cjCategoryDao == null)
		{
			cjCategoryDao = new CJCategoryDao();
			cjCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
			cjCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return cjCategoryDao;
	}
	
	public static ArticleService getSitemapArticleService() throws Exception
	{
		if(sitemapArticleService == null)
		{
			sitemapArticleService = new ArticleService();
			sitemapArticleService.setSitemapArticleDao(getSitemapArticleDao());
			sitemapArticleService.setSitemapCategoryDao(getSitemapCategoryDao());
			sitemapArticleService.setSitemapSubCategoryDao(getSitemapSubCategoryDao());
			sitemapArticleService.setDataGeneratorService(getDataGeneratorService());
			sitemapArticleService.setCmsProxyService(getCmsProxyService());
		}
		
		return sitemapArticleService;
	}
	
	public static CJArticleService getCJArticleService() throws Exception
	{
		if(cjArticleService == null)
		{
			cjArticleService = new CJArticleService();
			cjArticleService.setCjArticleDao(getCjArticleDao());
			cjArticleService.setCjCategoryDao(getCjCategoryDao());
			cjArticleService.setCjSubCategoryDao(getCjSubCategoryDao());
			cjArticleService.setCjDataGeneratorService(getCjDataGeneratorService());
			cjArticleService.setCmsProxyService(getCmsProxyService());
		}
		
		return cjArticleService;
	}
	
	public static CategoryService getSitemapCategoryService() throws Exception
	{
		if(sitemapCategoryService == null)
		{
			sitemapCategoryService = new CategoryService();
			sitemapCategoryService.setSitemapCategoryDao(getSitemapCategoryDao());
			sitemapCategoryService.setDataGeneratorService(getDataGeneratorService());
		}
		
		return sitemapCategoryService;
	}
	
	public static CJCategoryService getCjCategoryService() throws Exception
	{
		if(cjCategoryService == null)
		{
			cjCategoryService = new CJCategoryService();
			cjCategoryService.setCjCategoryDao(getCjCategoryDao());
		}
		
		return cjCategoryService;
	}

	public static DataGeneratorService getDataGeneratorService() throws IOException 
	{
		if(dataGeneratorService == null)
		{
			dataGeneratorService = new DataGeneratorService();
			dataGeneratorService.init();
		}
		return dataGeneratorService;
	}
	
	public static CJDataGeneratorService getCjDataGeneratorService() throws IOException 
	{
		if(cjDataGeneratorService == null)
		{
			cjDataGeneratorService = new CJDataGeneratorService();
			cjDataGeneratorService.init();
		}
		return cjDataGeneratorService;
	}


	public static ATStoryDao getATStoryDao() throws Exception
	{
		if (aTStoryDao == null)
		{
			aTStoryDao = new ATStoryDao();
			aTStoryDao.setSqlSessionFactory(getMsSqlServerSessionFactory());
			aTStoryDao.setSqlSessionTemplate(getMsSqlServerSqlSessionTemplate());
		}
		return aTStoryDao;
	}

	public static ATStoryService getATStoryService() throws Exception
	{
		if (aTStoryService == null)
		{
			aTStoryService = new ATStoryService();
			aTStoryService.setATStoryDao(getATStoryDao());
		}
		return aTStoryService;
	}

	public static ATStoryCollector getATStoryCollector() throws Exception
	{
		if (aTStoryCollector == null)
		{
			aTStoryCollector = new ATStoryCollector();
			aTStoryCollector.setATStoryService(getATStoryService());
		}
		return aTStoryCollector;
	}

	public static ModelTranslator getModelTranslator()
	{
		if (modelTranslator == null)
		{
			modelTranslator = new ModelTranslator();
		}
		return modelTranslator;
	}

	public static StrapiContentService getStrapiContentService() throws Exception
	{
		if (strapiContentService == null)
		{
			strapiContentService = new StrapiContentService();
			strapiContentService.init();
			strapiContentService.setBaseUrl(StrapiConstants.STRAPI_BASE_URL);
		}
		return strapiContentService;
	}
	
	public static StrapiArticleService getStrapiArticleService() throws Exception
	{
		if(strapiArticleService == null)
		{
			strapiArticleService = new StrapiArticleService();
			strapiArticleService.setStrapiContentService(getStrapiContentService());
		}
		
		return strapiArticleService;
	}
	
	public static StrapiCategoryService getStrapiCategoryService() throws Exception
	{
		if(strapiCategoryService == null)
		{
			strapiCategoryService = new StrapiCategoryService();
			strapiCategoryService.setStrapiContentService(getStrapiContentService());
		}
		
		return strapiCategoryService;
	}
	
	public static StrapiSubCategoryService getStrapiSubCategoryService() throws Exception
	{
		if(strapiSubCategoryService == null)
		{
			strapiSubCategoryService = new StrapiSubCategoryService();
			strapiSubCategoryService.setStrapiContentService(getStrapiContentService());
		}
		
		return strapiSubCategoryService;
	}


	public static StrapiWebhooksService getStrapiWebhooksService() throws Exception {
		
		if(strapiWebhooksService == null)
		{
			strapiWebhooksService.setCmsProxyService(getCmsProxyService());
			strapiWebhooksService.setSitemapArticleService(getSitemapArticleService());
			strapiWebhooksService.setStrapiArticleService(getStrapiArticleService());
			strapiWebhooksService.setSitemapPageService(getSitemapPageService());
			strapiWebhooksService.setStrapiCategoryService(getStrapiCategoryService());
		}
		return strapiWebhooksService;
	}


	private static SitemapPageService getSitemapPageService() {
		// TODO Auto-generated method stub
		return null;
	}


	private static CmsProxyService getCmsProxyService() {
		// TODO Auto-generated method stub
		return null;
	}

	public static SubCategoryDao getSitemapSubCategoryDao() throws Exception
	{
		if(sitemapSubCategoryDao == null)
		{
			sitemapSubCategoryDao = new SubCategoryDao();
			sitemapSubCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
			sitemapSubCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return sitemapSubCategoryDao;
	}
	
	public static CJSubCategoryDao getCjSubCategoryDao() throws Exception
	{
		if(cjSubCategoryDao == null)
		{
			cjSubCategoryDao = new CJSubCategoryDao();
			cjSubCategoryDao.setSqlSessionFactory(postgresSqlSessionFactory());
			cjSubCategoryDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return cjSubCategoryDao;
	}

	public static SubCategoryService getSitemapSubCategoryService() throws Exception 
	{
		if(sitemapSubCategoryService == null)
		{
			sitemapSubCategoryService = new SubCategoryService();
			sitemapSubCategoryService.setSitemapSubCategoryDao(getSitemapSubCategoryDao());
			
		}
		return sitemapSubCategoryService;
	}
	
	public static CJSubCategoryService getCjSubCategoryService() throws Exception 
	{
		if(cjSubCategoryService == null)
		{
			cjSubCategoryService = new CJSubCategoryService();
			cjSubCategoryService.setCjSubCategoryDao(getCjSubCategoryDao());
			
		}
		return cjSubCategoryService;
	}
	
	public static CartoonDao getSitemapCartoonDao() throws Exception
	{
		if(sitemapCartoonDao == null)
		{
			sitemapCartoonDao = new CartoonDao();
			sitemapCartoonDao.setSqlSessionFactory(postgresSqlSessionFactory());
			sitemapCartoonDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return sitemapCartoonDao;
	}
	
	public static CartoonService getSitemapCartoonService() throws Exception
	{
		if(sitemapCartoonService == null)
		{
			sitemapCartoonService = new CartoonService();
			sitemapCartoonService.setSitemapCartoonDao(getSitemapCartoonDao());
			
		}
		return sitemapCartoonService;
	}
	
	public static HoroscopeDao getSitemapHoroscopeDao() throws Exception
	{
		if(sitemapHoroscopeDao == null)
		{
			sitemapHoroscopeDao = new HoroscopeDao();
			sitemapHoroscopeDao.setSqlSessionFactory(postgresSqlSessionFactory());
			sitemapHoroscopeDao.setSqlSessionTemplate(postgresSqlSessionTemplate());
		}
		
		return sitemapHoroscopeDao;
	}
	
	public static HoroscopeService getSitemapHoroscopeService() throws Exception
	{
		if(sitemapHoroscopeService == null)
		{
			sitemapHoroscopeService = new HoroscopeService();
			sitemapHoroscopeService.setSitemapHoroscopeDao(getSitemapHoroscopeDao());
		}
		
		return sitemapHoroscopeService;
	}

}
