package com.andromeda.cms.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xhtmlrenderer.util.IOUtil;

import com.andromeda.cms.model.Feed;
import com.andromeda.cms.model.FeedMessage;
import com.andromeda.commons.util.FileNDirUtils;
import com.andromeda.commons.util.RestClient;

import ch.qos.logback.core.util.FileUtil;

@Service
public class AbnRssReadService
{
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String AUTHOR = "author";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";
	static final String GUID = "guid";

	private String baseUrl;

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public String getRssFeeds()
	{
		String rssXmlContent = null;
		try
		{
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0,
					new StringHttpMessageConverter(StandardCharsets.UTF_8));
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

			ResponseEntity<String> response =
					restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);
			rssXmlContent = response.getBody();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return rssXmlContent;
	}

	public Feed readFeed(String rssXmlContent)
	{
		Feed feed = null;
		try
		{
			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new ByteArrayInputStream(rssXmlContent.getBytes());
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// read the XML document
			while (eventReader.hasNext())
			{
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement())
				{
					String localPart = event.asStartElement().getName().getLocalPart();
					switch (localPart)
					{
						case ITEM:
							if (isFeedHeader)
							{
								isFeedHeader = false;
								feed = new Feed(title, link, description, language, copyright,
										pubdate);
							}
							event = eventReader.nextEvent();
							break;
						case TITLE:
							title = getCharacterData(event, eventReader);
							break;
						case DESCRIPTION:
							description = getCharacterData(event, eventReader);
							break;
						case LINK:
							link = getCharacterData(event, eventReader);
							break;
						case GUID:
							guid = getCharacterData(event, eventReader);
							break;
						case LANGUAGE:
							language = getCharacterData(event, eventReader);
							break;
						case AUTHOR:
							author = getCharacterData(event, eventReader);
							break;
						case PUB_DATE:
							pubdate = getCharacterData(event, eventReader);
							break;
						case COPYRIGHT:
							copyright = getCharacterData(event, eventReader);
							break;
					}
				}
				else if (event.isEndElement())
				{
					if (event.asEndElement().getName().getLocalPart() == (ITEM))
					{
						FeedMessage message = new FeedMessage();
						message.setAuthor(author);
						message.setDescription(description);
						message.setGuid(guid);
						message.setLink(link);
						message.setTitle(title);
						feed.getMessages().add(message);
						event = eventReader.nextEvent();
						continue;
					}
				}
			}
		}
		catch (XMLStreamException e)
		{
			throw new RuntimeException(e);
		}
		return feed;
	}

	private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
			throws XMLStreamException
	{
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters)
		{
			result = event.asCharacters().getData();
		}
		return result;
	}

	public void saveContent(String rssXmlContent)
	{
		InputStream targetStream = new ByteArrayInputStream(rssXmlContent.getBytes());
		BOMInputStream bomInputStream = new BOMInputStream(targetStream);
		try
		{
			String targetContent = org.apache.commons.io.IOUtils.toString(bomInputStream, "UTF-8");
			FileNDirUtils.writeToFile(targetContent, "rssDemo.xml");

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String takeOffBOM(InputStream inputStream) throws IOException
	{
		BOMInputStream bomInputStream = new BOMInputStream(inputStream);
		return org.apache.commons.io.IOUtils.toString(bomInputStream, "UTF-8");
	}

}
