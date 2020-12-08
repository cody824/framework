package com.noknown.framework.common.web.filter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author guodong
 * @date 2020/12/7
 */
public class XssAndSqlHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private HttpServletRequest request;

	public XssAndSqlHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	@Override
	public String getParameter(String name)
	{
		String value = request.getParameter(name);
		if (!StringUtils.isEmpty(value))
		{
			value = StringEscapeUtils.escapeHtml(value);
		}
		return value;
	}


	@Override
	public String[] getParameterValues(String name)
	{
		String[] parameterValues = super.getParameterValues(name);
		if (parameterValues == null)
		{
			return null;
		}
		for (int i = 0; i < parameterValues.length; ++i)
		{
			String value = parameterValues[i];
			parameterValues[i] = StringEscapeUtils.escapeHtml(value);
		}
		return parameterValues;
	}
}
