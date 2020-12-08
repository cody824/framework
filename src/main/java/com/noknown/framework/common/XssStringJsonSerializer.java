package com.noknown.framework.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;

/**
 * @author guodong
 * @date 2020/12/7
 */
public class XssStringJsonSerializer extends JsonSerializer<String> {
	@Override
	public Class<String> handledType() {
		return String.class;
	}

	@Override
	public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		if (s != null) {
			String encodedValue = StringEscapeUtils.escapeHtml(s);
			jsonGenerator.writeString(encodedValue);
		}
	}
}
