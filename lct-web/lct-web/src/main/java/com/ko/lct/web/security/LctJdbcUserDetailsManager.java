package com.ko.lct.web.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class LctJdbcUserDetailsManager extends JdbcDaoImpl {
    protected final Log logger = LogFactory.getLog(getClass());

    private static final String PASSWORD_EXPIRATIONS_DAYS = "90";
    private static final String USERS_BY_USER_NAME_QUERY = "select u.NAME as username, u.PASSWORD, u.ENABLED, case when sysdate - greatest(u.CREATE_DT, u.MODIFY_DT) > "
	    + PASSWORD_EXPIRATIONS_DAYS +
	    " then 0 else 1 end as CREDENTIALS_NON_EXPIRED from T_USER u where u.NAME = ?";
    private static final String AUTHORITIES_BY_USERNAME_QUERY = "select u.NAME as username, ROLE_NAME as authority from T_USER u where u.NAME = ?";

    private LoginAttemptService loginAttemptService;

    public LctJdbcUserDetailsManager(LoginAttemptService loginAttemptService) {
	super();
	this.loginAttemptService = loginAttemptService;
	this.setUsersByUsernameQuery(USERS_BY_USER_NAME_QUERY);
	this.setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl#loadUsersByUsername(java.lang.String)
     */
    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
	return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[] { username }, new RowMapper<UserDetails>() {
	    @Override
	    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		String username = rs.getString(1);
		String password = rs.getString(2);
		boolean enabled = rs.getBoolean(3);
		boolean credentialsNonExpired = rs.getBoolean(4);
		return new User(username, password, enabled, true, credentialsNonExpired, true, AuthorityUtils.NO_AUTHORITIES);
	    }

	});
    }

    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
	String returnUsername = userFromUserQuery.getUsername();

	if (!isUsernameBasedPrimaryKey()) {
	    returnUsername = username;
	}

	boolean isUserLocked = this.loginAttemptService.isUserLocked(returnUsername);
	if (isUserLocked) {
	    logger.info("User Locked: " + returnUsername);
	}
	return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
		true, userFromUserQuery.isCredentialsNonExpired(), !isUserLocked, combinedAuthorities);
    }

}
