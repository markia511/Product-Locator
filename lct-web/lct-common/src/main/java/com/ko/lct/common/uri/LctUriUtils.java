package com.ko.lct.common.uri;

public class LctUriUtils {
    public static final String escapeParam(String value) {
	if (value != null) {
	    return value.replace(".", "%2E").replace("\\", "%5C").replace("/", "%2F").replace(";", "%3B");
	}
	return value;
    }

    public static final String unEscapeParam(String value) {
	if (value != null) {
	    return value.replace("%2E", ".").replace("%5C", "\\").replace("%2F", "/").replace("%3B", ";");
	}
	return value;
    }

}
