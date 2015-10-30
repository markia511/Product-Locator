package com.ko.lct.web.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;

public class LctDaoAuthenticationProvider extends DaoAuthenticationProvider {
    protected final Log logger = LogFactory.getLog(getClass());

    private LoginAttemptService loginAttemptService;

    public LctDaoAuthenticationProvider(LoginAttemptService loginAttemptService) {
	super();
	this.loginAttemptService = loginAttemptService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	Authentication retValue = null;

	String userName = (authentication.getPrincipal() == null) ? null : authentication.getName();
	try {
	    logger.info("User Name: " + userName);
	    retValue = super.authenticate(authentication);
	    this.loginAttemptService.successfilLogin(userName);
	} catch (BadCredentialsException ex) {
	    logger.error("Bad Credentials: " + userName, ex);
	    this.loginAttemptService.failedLogin(userName);
	    throw ex;
	}
	return retValue;
    }

    @Override
    public UserDetailsChecker getPostAuthenticationChecks() {
	return super.getPostAuthenticationChecks();
    }

}
