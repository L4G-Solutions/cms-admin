package com.andromeda.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

/**
 * 
 * @author Prakash K
 * @date 2020-09-18
 *
 */
public class ZipUtils
{
	public static String compress(String content) throws IOException
	{

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION, true);
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(stream, compresser);
		deflaterOutputStream.write(content.getBytes());
		deflaterOutputStream.close();
		byte[] output = stream.toByteArray();
		String encoded = Base64Utils.encode(output);

		return encoded;
	}

	public static String decompress(String content) throws IOException
	{
		byte[] bytes = Base64Utils.decode(content);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Inflater decompresser = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(stream, decompresser);
		inflaterOutputStream.write(bytes);
		inflaterOutputStream.close();
		String value = stream.toString();

		return value;
	}
}
