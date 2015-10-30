package com.ko.lct.ws.utils;

import java.io.UnsupportedEncodingException;

import org.springframework.web.util.UriUtils;

public class ParameterParser {
	public static String[] parseParameter(String parameter) throws UnsupportedEncodingException {
		if (parameter == null) {
			return null;
		}
		String[] retValue = parameter.split(",");
		for (int i = 0; i < retValue.length; i++) {
			final String value = retValue[i];
			if (value.contains("%")) {
				retValue[i] = UriUtils.decode(value, "UTF-8");
			}
		}
		return retValue;
	}

}
