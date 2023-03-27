package com.andromeda.commons.util;

/**
 * 
 * @author Prakash K
 * @date 2020-10-17
 *
 */
public class ThreadUtils
{
	public static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			// Do nothing
		}
	}
}
