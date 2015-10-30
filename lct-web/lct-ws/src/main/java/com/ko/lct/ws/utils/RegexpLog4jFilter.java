package com.ko.lct.ws.utils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class RegexpLog4jFilter extends Filter {

    private Pattern pattern;
    
    public String getPattern() {
	return pattern == null ? null : pattern.pattern();
    }
    
    public void setPattern(String pattern) {
	try {
	    this.pattern = Pattern.compile(pattern);
	} catch(PatternSyntaxException ex) {
	    this.pattern = null;
	    throw ex;
	}
    }
    
    @Override
    public int decide(LoggingEvent event) {
	if(pattern == null)
	    return Filter.NEUTRAL;
	if(pattern.matcher(event.getMessage().toString()).matches())
	    return Filter.ACCEPT;
	else
	    return Filter.DENY;
    }
}
