package com.andromeda.commons.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestClient
{
	public static final int CONNECTION_TIMEOUT = 5 * 60 * 1000; // 20000; // 20 seconds
	public static final int READ_TIMEOUT = 5 * 60 * 1000; // 60000; // 30 seconds

	private RestTemplate restTemplate = null;

	public void init()
	{
		try
		{
			restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0,
					new StringHttpMessageConverter(StandardCharsets.UTF_8));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	private void init(SimpleClientHttpRequestFactory requestFactory)
	{
		MediaType plainTextType = new MediaType("text", "plain");
		MediaType jsonType = new MediaType("application", "json");

		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(plainTextType);
		supportedMediaTypes.add(jsonType);

		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
				new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(mappingJackson2HttpMessageConverter);

		restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(messageConverters);
		restTemplate.setRequestFactory(requestFactory);
	}
	
	public void init(final String bearer)
	{
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory()
		{
			@Override
			protected void prepareConnection(HttpURLConnection connection, String httpMethod)
					throws IOException
			{
				super.prepareConnection(connection, httpMethod);
				String acceptHeaderValue = "application/json";
				connection.setRequestProperty("Accept", acceptHeaderValue);

				if (bearer != null)
				{
					connection.setRequestProperty("Authorization", "Bearer " + bearer);
				}

				connection.setConnectTimeout(CONNECTION_TIMEOUT);
				connection.setReadTimeout(READ_TIMEOUT);
			}
		};

		init(requestFactory);
	}

	public String get(String url)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<String> response =
				restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}
	
	public String get(String url, String accessToken)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+accessToken);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<String> response =
				restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}
	
	public <T> T post(String url, String objJson, Class<T> responseType)
	{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		/*headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		*/
	    HttpEntity<String> request = 
			      new HttpEntity<String>(objJson, headers);
		T obj = restTemplate.postForObject(url, request, responseType);
		System.out.println("Data: " + obj);
		return obj;
	}
	
	public void put(String url, String objJson)
	{
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		/*headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		*/
	    HttpEntity<String> request = 
			      new HttpEntity<String>(objJson, headers);
		restTemplate.put(url, request);;

	}
	
	
	public <T> void put(String url, T t)
	{
		restTemplate.put(url, t);
	}

	public void delete(String url)
	{
		restTemplate.delete(url);
	}

}
