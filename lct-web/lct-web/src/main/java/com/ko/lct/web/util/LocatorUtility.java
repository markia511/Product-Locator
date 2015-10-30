package com.ko.lct.web.util;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import com.ko.lct.common.util.LocatorConstants;
import com.ko.lct.web.dao.UserDao;
import com.ko.lct.web.repository.LocatorCache;
import com.ko.lct.web.security.LctDaoAuthenticationProvider;

@Component
public class LocatorUtility {
    private static final Logger logger = LoggerFactory.getLogger(LocatorUtility.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private LocatorCache locatorCache;

    @Autowired
    private LctDaoAuthenticationProvider lctAuthenticationProvider;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    public org.springframework.security.core.userdetails.User getUserData() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	if (authentication != null) {
	    Object principal = authentication.getPrincipal();
	    if (principal instanceof org.springframework.security.core.userdetails.User) {
		org.springframework.security.core.userdetails.User userData = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
		return userData;
	    }
	}
	return null;
    }

    public Authentication authenticateUser(String userName, String password) {
	Authentication authentication = null;
	try {
	    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userName,
		    password);
	    synchronized (this.lctAuthenticationProvider) {
		UserDetailsChecker userDetailsChecker = this.lctAuthenticationProvider.getPostAuthenticationChecks();
		try {
		    this.lctAuthenticationProvider.setPostAuthenticationChecks(new UserDetailsChecker() {
			@Override
			public void check(UserDetails toCheck) {
			    // Do nothing
			}
		    });
		    authentication = this.lctAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
		} finally {
		    this.lctAuthenticationProvider.setPostAuthenticationChecks(userDetailsChecker);
		}
	    }
	} catch (Exception ex) {
	    // do nothing
	    logger.error("Change password error - authentication failure", ex);
	}
	return authentication;
    }

    public boolean isAdminAuthority(HttpSession session) {
	org.springframework.security.core.userdetails.User userData = getUserData();
	if (userData != null) {
	    Collection<GrantedAuthority> grantedAuthorities = userData.getAuthorities();
	    if (grantedAuthorities != null) {
		for (GrantedAuthority authority : grantedAuthorities) {
		    if (ROLE_ADMIN.equalsIgnoreCase(authority.getAuthority())) {
			return true;
		    }
		}
	    }

	}
	return false;
    }

    public boolean isAdminOrUserAuthority(HttpSession session) {
	org.springframework.security.core.userdetails.User userData = getUserData();
	if (userData != null) {
	    Collection<GrantedAuthority> grantedAuthorities = userData.getAuthorities();
	    if (grantedAuthorities != null) {
		for (GrantedAuthority authority : grantedAuthorities) {
		    if (ROLE_ADMIN.equalsIgnoreCase(authority.getAuthority()) || ROLE_USER.equalsIgnoreCase(authority.getAuthority())) {
			return true;
		    }
		}
	    }

	}
	return false;
    }

    public void clearCache(int mappingType) {
	switch (mappingType) {
	case LocatorConstants.LOOKUP_BEVERAGE_CATEGORY:
	case LocatorConstants.LOOKUP_BRAND:
	case LocatorConstants.LOOKUP_FLAVOR:
	case LocatorConstants.LOOKUP_PRODUCT:
	    locatorCache.clearProductsCache();
	    locatorCache.clearPackagesCache();
	    break;
	case LocatorConstants.LOOKUP_PRIMARY_CONTAINER:
	case LocatorConstants.LOOKUP_SECONDARY_PACKAGE:
	    locatorCache.clearPackagesCache();
	    break;
	case LocatorConstants.LOOKUP_TRADE_SUB_CHANNEL:
	    locatorCache.clearTradeSubChannelsCache();
	    break;
	default:
	    break;
	}
    }

}
