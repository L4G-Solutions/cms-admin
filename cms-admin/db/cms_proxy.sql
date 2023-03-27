
CREATE SCHEMA IF NOT EXISTS cms_proxy;

DROP TABLE IF EXISTS cms_proxy.sitemap_locations ;
CREATE TABLE cms_proxy.sitemap_locations
(
	id SERIAL PRIMARY KEY,
	url CHARACTER VARYING NOT NULL UNIQUE,
	type CHARACTER VARYING		
);

DROP TABLE IF EXISTS cms_proxy.cj_sitemap_locations ;
CREATE TABLE cms_proxy.cj_sitemap_locations
(
	id SERIAL PRIMARY KEY,
	url CHARACTER VARYING NOT NULL UNIQUE,
	type CHARACTER VARYING		
);

DROP TABLE IF EXISTS cms_proxy.sitemap_post_locations ;
CREATE TABLE cms_proxy.sitemap_post_locations
(
	id SERIAL PRIMARY KEY,
	url CHARACTER VARYING NOT NULL UNIQUE,
	year int		
);

DROP TABLE IF EXISTS cms_proxy.cj_sitemap_post_locations ;
CREATE TABLE cms_proxy.cj_sitemap_post_locations
(
	id SERIAL PRIMARY KEY,
	url CHARACTER VARYING NOT NULL UNIQUE,
	year int		
);


DROP TABLE IF EXISTS cms_proxy.redirection_urls CASCADE;
CREATE TABLE cms_proxy.redirection_urls
(
	id INTEGER,
	url CHARACTER VARYING UNIQUE,
	ampUrl CHARACTER VARYING UNIQUE,
	year int		
);

DROP TABLE IF EXISTS cms_proxy.cj_redirection_urls CASCADE;
CREATE TABLE cms_proxy.cj_redirection_urls
(
	id INTEGER,
	url CHARACTER VARYING UNIQUE,
	ampUrl CHARACTER VARYING UNIQUE,
	year int		
);

DROP TABLE IF EXISTS cms_proxy.photoGallery ;
CREATE TABLE cms_proxy.photoGallery
(
	id INTEGER PRIMARY KEY,
	source CHARACTER VARYING,
	url CHARACTER VARYING,
	ampUrl CHARACTER VARYING,
	abnStoryId CHARACTER VARYING ,
	author CHARACTER VARYING,
	publishedYear INTEGER NOT NULL,
	publishedAt TIMESTAMP WITH TIME ZONE NOT NULL,
	createdAt TIMESTAMP WITH TIME ZONE,
	updatedAt TIMESTAMP WITH TIME ZONE,
	headline CHARACTER VARYING NOT NULL,
	englishTitle CHARACTER VARYING NOT NULL,
	summary CHARACTER VARYING NOT NULL,
	published BOOLEAN NOT NULL DEFAULT TRUE,
	primaryCategoryId INTEGER NOT NULL,
	primaryCategoryName CHARACTER VARYING NOT NULL,
	primaryCategoryTeluguLabel CHARACTER VARYING NOT NULL,
	primaryCategorySeoSlug CHARACTER VARYING NOT NULL,
	primaryCategoryUrl CHARACTER VARYING NOT NULL,
	primarySubCategoryId INTEGER,
	primarySubCategoryName CHARACTER VARYING,
	primarySubCategoryTeluguLabel CHARACTER VARYING,
	primarySubCategorySeoSlug CHARACTER VARYING,
	primarySubCategoryUrl CHARACTER VARYING,
	seoSlug CHARACTER VARYING NOT NULL,
	newsKeywords CHARACTER VARYING NOT NULL,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	tags CHARACTER VARYING,
	tagUrls CHARACTER VARYING,
	contentType CHARACTER VARYING NOT NULL,
	bulkImageUpload CHARACTER VARYING,
	imageWithDescription CHARACTER VARYING,
	photoLocation CHARACTER VARYING,
	locale CHARACTER VARYING,
	imageCaption CHARACTER VARYING,
	imageUrl CHARACTER VARYING,
	imageWidth CHARACTER VARYING,
	imageHeight CHARACTER VARYING,
	thumbnailPrimaryImageUrl CHARACTER VARYING,
	prioritiseInPhotoLanding BOOLEAN DEFAULT true,
	publishedAtSm CHARACTER VARYING,
	updatedAtSm CHARACTER VARYING,
	deleted BOOLEAN DEFAULT FALSE
);


DROP TABLE IF EXISTS cms_proxy.cj_photoGallery ;
CREATE TABLE cms_proxy.cj_photoGallery
(
	id INTEGER PRIMARY KEY,
	source CHARACTER VARYING,
	url CHARACTER VARYING,
	ampUrl CHARACTER VARYING,
	abnStoryId CHARACTER VARYING ,
	author CHARACTER VARYING,
	publishedYear INTEGER NOT NULL,
	publishedAt TIMESTAMP WITH TIME ZONE NOT NULL,
	createdAt TIMESTAMP WITH TIME ZONE,
	updatedAt TIMESTAMP WITH TIME ZONE,
	headline CHARACTER VARYING NOT NULL,
	englishTitle CHARACTER VARYING NOT NULL,
	summary CHARACTER VARYING NOT NULL,
	published BOOLEAN NOT NULL DEFAULT TRUE,
	primaryCategoryId INTEGER NOT NULL,
	primaryCategoryTeluguLabel CHARACTER VARYING NOT NULL,
	primaryCategorySeoSlug CHARACTER VARYING NOT NULL,
	primaryCategoryUrl CHARACTER VARYING NOT NULL,
	primarySubCategoryId INTEGER,
	primarySubCategoryTeluguLabel CHARACTER VARYING,
	primarySubCategorySeoSlug CHARACTER VARYING,
	primarySubCategoryUrl CHARACTER VARYING,
	seoSlug CHARACTER VARYING NOT NULL,
	newsKeywords CHARACTER VARYING NOT NULL,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	tags CHARACTER VARYING,
	tagUrls CHARACTER VARYING,
	contentType CHARACTER VARYING NOT NULL,
	bulkImageUpload CHARACTER VARYING,
	imageWithDescription CHARACTER VARYING,
	photoLocation CHARACTER VARYING,
	locale CHARACTER VARYING,
	imageCaption CHARACTER VARYING,
	imageUrl CHARACTER VARYING,
	imageWidth CHARACTER VARYING,
	imageHeight CHARACTER VARYING,
	thumbnailPrimaryImageUrl CHARACTER VARYING,
	prioritiseInPhotoLanding BOOLEAN DEFAULT true,
	publishedAtSm CHARACTER VARYING,
	updatedAtSm CHARACTER VARYING,
	deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX cms_proxy_cj_photogallery_published_at_idx ON cms_proxy.cj_photogallery (publishedAt);


DROP TABLE IF EXISTS cms_proxy.articles ;
CREATE TABLE cms_proxy.articles
(
	id INTEGER PRIMARY KEY,
	source CHARACTER VARYING,
	url CHARACTER VARYING,
	ampUrl CHARACTER VARYING,
	abnStoryId CHARACTER VARYING ,
	author CHARACTER VARYING,
	publishedYear INTEGER NOT NULL,
	publishedAt TIMESTAMP WITH TIME ZONE,
	createdAt TIMESTAMP WITH TIME ZONE,
	updatedAt TIMESTAMP WITH TIME ZONE,
	headline CHARACTER VARYING NOT NULL,
	shortHeadline CHARACTER VARYING,
	articleText CHARACTER VARYING,
	englishTitle CHARACTER VARYING NOT NULL,
	summary CHARACTER VARYING NOT NULL,
	imageUrl CHARACTER VARYING,
	imageMediumUrl CHARACTER VARYING,
	imageSmallUrl CHARACTER VARYING,
	imageThumbUrl CHARACTER VARYING,
	imageCaption CHARACTER VARYING,
	imageWidth INTEGER,
	imageHeight INTEGER,
	primaryCategoryId INTEGER NOT NULL,
	primaryCategoryTeluguLabel CHARACTER VARYING NOT NULL,
	primaryCategorySeoSlug CHARACTER VARYING NOT NULL,
	primaryCategoryUrl CHARACTER VARYING NOT NULL,
	primaryCategoryName CHARACTER VARYING NOT NULL,
	primarySubCategoryId INTEGER,
	primarySubCategoryTeluguLabel CHARACTER VARYING,
	primarySubCategorySeoSlug CHARACTER VARYING,
	primarySubCategoryUrl CHARACTER VARYING,
	primarySubCategoryName CHARACTER VARYING,
	seoSlug CHARACTER VARYING NOT NULL,
	newsKeywords CHARACTER VARYING NOT NULL,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	tags CHARACTER VARYING,
	tagUrls CHARACTER VARYING,
	contentType CHARACTER VARYING NOT NULL,
	photos JSONB,
	videos JSONB,
	relatedArticles CHARACTER VARYING,
	prioritiseInLatestNews BOOLEAN,
	prioritiseInPrimarySection BOOLEAN,
	displayModifiedDate BOOLEAN NOT NULL DEFAULT FALSE,
	published BOOLEAN NOT NULL DEFAULT TRUE,
	publishedAtSm CHARACTER VARYING,
	updatedAtSm CHARACTER VARYING,
	deleted BOOLEAN DEFAULT FALSE,
	hideAuthorName BOOLEAN DEFAULT FALSE,
	locale CHARACTER VARYING
);

CREATE INDEX cms_proxy_articles_published_at_idx ON cms_proxy.articles (publishedAt);

DROP TABLE IF EXISTS cms_proxy.cj_articles CASCADE;
CREATE TABLE cms_proxy.cj_articles
(
	id INTEGER PRIMARY KEY,
	source CHARACTER VARYING,
	url CHARACTER VARYING,
	ampUrl CHARACTER VARYING,
	abnStoryId CHARACTER VARYING ,
	author CHARACTER VARYING,
	publishedYear INTEGER NOT NULL,
	publishedAt TIMESTAMP WITH TIME ZONE,
	createdAt TIMESTAMP WITH TIME ZONE,
	updatedAt TIMESTAMP WITH TIME ZONE,
	headline CHARACTER VARYING NOT NULL,
	shortHeadline CHARACTER VARYING,
	articleText CHARACTER VARYING,
	englishTitle CHARACTER VARYING NOT NULL,
	summary CHARACTER VARYING NOT NULL,
	imageUrl CHARACTER VARYING,
	imageMediumUrl CHARACTER VARYING,
	imageSmallUrl CHARACTER VARYING,
	imageThumbUrl CHARACTER VARYING,
	imageCaption CHARACTER VARYING,
	imageWidth INTEGER,
	imageHeight INTEGER,
	primaryCategoryId INTEGER NOT NULL,
	primaryCategoryTeluguLabel CHARACTER VARYING,
	primaryCategorySeoSlug CHARACTER VARYING NOT NULL,
	primaryCategoryUrl CHARACTER VARYING NOT NULL,
	primaryCategoryName CHARACTER VARYING NOT NULL,
	primarySubCategoryId INTEGER,
	primarySubCategoryTeluguLabel CHARACTER VARYING,
	primarySubCategorySeoSlug CHARACTER VARYING,
	primarySubCategoryUrl CHARACTER VARYING,
	primarySubCategoryName CHARACTER VARYING,
	seoSlug CHARACTER VARYING NOT NULL,
	newsKeywords CHARACTER VARYING NOT NULL,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	tags CHARACTER VARYING,
	tagUrls CHARACTER VARYING,
	contentType CHARACTER VARYING NOT NULL,
	photos JSONB,
	videos JSONB,
	relatedArticles CHARACTER VARYING,
	prioritiseInLatestNews BOOLEAN,
	prioritiseInPrimarySection BOOLEAN,
	displayModifiedDate BOOLEAN NOT NULL DEFAULT FALSE,
	published BOOLEAN NOT NULL DEFAULT TRUE,
	publishedAtSm CHARACTER VARYING,
	updatedAtSm CHARACTER VARYING,
	deleted BOOLEAN DEFAULT FALSE,
	hideAuthorName BOOLEAN DEFAULT FALSE,
	locale CHARACTER VARYING
);

CREATE INDEX cms_proxy_articles_cj_published_at_idx ON cms_proxy.cj_articles (publishedAt);

DROP TABLE IF EXISTS cms_proxy.categories;
CREATE TABLE cms_proxy.categories
	(
	id INTEGER PRIMARY KEY,
	categoryId CHARACTER VARYING UNIQUE,
	name CHARACTER VARYING,
	description CHARACTER VARYING,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	keywords CHARACTER VARYING,
	teluguLabel CHARACTER VARYING,
	seoSlug CHARACTER VARYING,
	subCategories CHARACTER VARYING,
	url CHARACTER VARYING
	);

DROP TABLE IF EXISTS cms_proxy.cj_categories;
CREATE TABLE cms_proxy.cj_categories
	(
	id INTEGER PRIMARY KEY,
	categoryId CHARACTER VARYING UNIQUE,
	name CHARACTER VARYING,
	description CHARACTER VARYING,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	keywords CHARACTER VARYING,
	teluguLabel CHARACTER VARYING,
	seoSlug CHARACTER VARYING,
	subCategories CHARACTER VARYING,
	url CHARACTER VARYING
	);



DROP TABLE IF EXISTS cms_proxy.subcategories;
CREATE TABLE cms_proxy.subcategories
	(
	id INTEGER PRIMARY KEY,
	subCategoryId CHARACTER VARYING UNIQUE,
	name CHARACTER VARYING,
	description CHARACTER VARYING,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	keywords CHARACTER VARYING,
	teluguLabel CHARACTER VARYING,
	seoSlug CHARACTER VARYING,
	categoryId INTEGER NOT NULL,
	url CHARACTER VARYING
	);

DROP TABLE IF EXISTS cms_proxy.cj_subcategories;
CREATE TABLE cms_proxy.cj_subcategories
	(
	id INTEGER PRIMARY KEY,
	subCategoryId CHARACTER VARYING UNIQUE,
	name CHARACTER VARYING,
	description CHARACTER VARYING,
	metaTitle CHARACTER VARYING,
	metaDescription CHARACTER VARYING,
	keywords CHARACTER VARYING,
	teluguLabel CHARACTER VARYING,
	seoSlug CHARACTER VARYING,
	categoryId INTEGER NOT NULL,
	url CHARACTER VARYING
	);



DROP TABLE IF EXISTS cms_proxy.sitemap_dates;
CREATE TABLE cms_proxy.sitemap_dates
	(
	date DATE
	);
	
DROP TABLE IF EXISTS cms_proxy.cj_sitemap_dates;
CREATE TABLE cms_proxy.cj_sitemap_dates
	(
	date DATE
	);	

	
DROP TABLE IF EXISTS cms_proxy.cartoons ;
CREATE TABLE cms_proxy.cartoons
(
	id INTEGER PRIMARY KEY,
	catoonId CHARACTER VARYING,
	abnStoryId CHARACTER VARYING,
	publishedAt TIMESTAMP WITH TIME ZONE,
	createdAt TIMESTAMP WITH TIME ZONE,
	updatedAt TIMESTAMP WITH TIME ZONE,
	title CHARACTER VARYING,
	englishTitle CHARACTER VARYING,
	imageUrl CHARACTER VARYING,
	imageCaption CHARACTER VARYING,
	imageAlternativeText CHARACTER VARYING,
	imageWidth INTEGER,
	imageHeight INTEGER,
	newsKeywords CHARACTER VARYING,
	tags CHARACTER VARYING,
	published BOOLEAN,
	locale CHARACTER VARYING,
	source CHARACTER VARYING,
	primaryCategoryId INTEGER NOT NULL,
	primaryCategoryTeluguLabel CHARACTER VARYING NOT NULL,
	primaryCategorySeoSlug CHARACTER VARYING NOT NULL,
	primaryCategoryUrl CHARACTER VARYING NOT NULL,
	publishedAtSm CHARACTER VARYING,
	updatedAtSm CHARACTER VARYING,
	deleted BOOLEAN DEFAULT FALSE
);	

CREATE INDEX cms_proxy_cartoons_published_at_idx ON cms_proxy.cartoons (publishedAt);

DROP TABLE IF EXISTS cms_proxy.horoscopes ;
CREATE TABLE cms_proxy.horoscopes
(
	id INTEGER PRIMARY KEY,
	horoscopeId CHARACTER VARYING,
	publishedAt TIMESTAMP WITH TIME ZONE,
	createdAt TIMESTAMP WITH TIME ZONE,
	updatedAt TIMESTAMP WITH TIME ZONE,
	title CHARACTER VARYING,
	englishTitle CHARACTER VARYING,
	horoscopeType CHARACTER VARYING,
	newsKeywords CHARACTER VARYING,
	tags CHARACTER VARYING,
	locale CHARACTER VARYING,
	meshamAries CHARACTER VARYING,
	vrushabamTaurus CHARACTER VARYING,
	mithunamGemini CHARACTER VARYING,
	karkatakamCancer CHARACTER VARYING,
	simhaLeo CHARACTER VARYING,
	kanyaVirgo CHARACTER VARYING,
	tulaLibra CHARACTER VARYING,
	vruschikamScorpio CHARACTER VARYING,
	dhanassuSagittarius CHARACTER VARYING,
	makaramCapricorn CHARACTER VARYING,
	kumbhamAquarius CHARACTER VARYING,
	meenamPisces CHARACTER VARYING,
	panchangam CHARACTER VARYING,
	startDate DATE,
	endDate DATE, 
	published BOOLEAN,
	source CHARACTER VARYING,
	primaryCategoryId INTEGER NOT NULL,
	primaryCategoryTeluguLabel CHARACTER VARYING NOT NULL,
	primaryCategorySeoSlug CHARACTER VARYING NOT NULL,
	primaryCategoryUrl CHARACTER VARYING NOT NULL,
	publishedAtSm CHARACTER VARYING,
	updatedAtSm CHARACTER VARYING,
	deleted BOOLEAN DEFAULT FALSE
);	

CREATE INDEX cms_proxy_horoscopes_published_at_idx ON cms_proxy.horoscopes (publishedAt);

CREATE SCHEMA migration;   		
CREATE TABLE migration.articles_status
(
        id INTEGER PRIMARY KEY,
        abnStoryId CHARACTER VARYING,
        status BOOLEAN
); 

--------------------------------------------------------------------------

--------------------------------------------------------------------------
insert into cms_proxy.sitemap_locations (url, type) values ('live-tv', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('cartoonarchive', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('astrology', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('astrology/yearly-horoscope', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('astrology/daily-horoscope', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('astrology/weekly-date-horoscope', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('astrology/weekly-star-horoscope', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('open-heart', 'page');
insert into cms_proxy.sitemap_locations (url, type) values ('live-tv', 'page');


UPDATE cms_proxy.categories
SET
name = 'Technology',
teluguLabel = '',
seoslug='technology'
url = '/technology',
subcategories=''
WHERE id = 5
