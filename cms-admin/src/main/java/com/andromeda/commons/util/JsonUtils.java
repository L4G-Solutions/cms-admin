package com.andromeda.commons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Prakash K
 * @date 29-Aug-2015
 *
 */
public class JsonUtils
{
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 
	 * @param object
	 * @return
	 */
	public static String toString(Object object)
	{
		String str = null;

		try
		{
			str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		}
		catch (JsonProcessingException e)
		{
			str = "" + object;
		}

		return str;
	}

	public static <T> T deserialize(String content, Class<T> klass) throws Exception
	{
		return mapper.readValue(content, klass);
	}

	public static <T> T deserialize(String content, TypeReference<T> typeReference) throws Exception
	{
		return mapper.reader().forType(new TypeReference<T>()
		{
		}).readValue(content);
		// return mapper.readValue(content, typeReference);
	}
}
