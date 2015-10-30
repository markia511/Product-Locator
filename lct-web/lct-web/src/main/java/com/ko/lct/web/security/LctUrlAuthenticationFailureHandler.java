package com.ko.lct.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.ko.lct.common.uri.LctUriUtils;

public class LctUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private String credentialsExpiredUrl;

    public LctUrlAuthenticationFailureHandler(String defaultFailureUrl, String credentialsExpiredUrl) {
	super(defaultFailureUrl);
	this.credentialsExpiredUrl = credentialsExpiredUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	    AuthenticationException exception) throws IOException, ServletException {
	logger.error("Authentication Failure", exception);
	if (exception instanceof CredentialsExpiredException) {
	    UriComponents uriComponents = UriComponentsBuilder.fromUriString(
		    this.credentialsExpiredUrl + "{userName}").build();
	    String userName = LctUriUtils.escapeParam(request.getParameter("j_username"));
	    String uri = uriComponents.expand(userName).encode().toUriString();
	    getRedirectStrategy().sendRedirect(request, response, uri);
	}
	else {
	    super.onAuthenticationFailure(request, response, exception);
	}
    }

}
