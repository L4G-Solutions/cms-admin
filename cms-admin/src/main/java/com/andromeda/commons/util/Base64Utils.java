package com.andromeda.commons.util;

import java.util.Base64;

/**
 * 
 * @author Prakash K
 * @date 29-Aug-2015
 *
 */
public class Base64Utils
{
	/**
	 * 
	 * @param source
	 * @return
	 */
	public static String encode(byte[] source)
	{
		return Base64.getEncoder().encodeToString(source);
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	public static byte[] decode(String source)
	{
		return Base64.getDecoder().decode(source);
	}
}
