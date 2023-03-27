package com.andromeda.commons.model;

import java.io.Serializable;

import com.andromeda.commons.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author Prakash K
 * @date 29-Aug-2015
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BaseModel implements Serializable
{
	@Override
	public String toString()
	{
		return JsonUtils.toString(this);
	}
}
