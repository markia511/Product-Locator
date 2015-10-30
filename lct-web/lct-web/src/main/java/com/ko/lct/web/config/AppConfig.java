package com.ko.lct.web.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ko.lct.web.dao.ReportDao;
import com.ko.lct.web.security.LctDaoAuthenticationProvider;
import com.ko.lct.web.security.LctJdbcUserDetailsManager;
import com.ko.lct.web.security.LctUrlAuthenticationFailureHandler;
import com.ko.lct.web.security.LoginAttemptService;

@Configuration
@EnableTransactionManagement
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
// @EnableGlobalMethodSecurity(prePostEnabled=true, jsr250Enabled=true, securedEnabled=true, proxyTargetClass=true)
public class AppConfig extends WebSecurityConfigurerAdapter {
    protected final Log logger = LogFactory.getLog(getClass());
    // private static final String CONNECTION_CACHE_NAME = "lct-web-connection-cache"; 

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(lctAuthenticationProvider());
	// .jdbcAuthentication()
	// .dataSource(dataSource())
	// .usersByUsernameQuery()
	// .authoritiesByUsernameQuery()
	// .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
		.antMatchers("/",
			"/resources/images/*.png", 
			"/resources/images/*.jpg", 
			"/resources/images/*.gif", 
			"/resources/scripts/*.js",
			"/resources/styles/*.css", 
			"/resources/styles/*.htc", 
			"/resources/styles/images/*.png", 
			"/resources/xls/*.xls").permitAll()
		.antMatchers(
			"/mapping", 
			"/get_generic_list", 
			"/get_mapped_values", 
			"/get_unmapped_values", 
			"/map_value", 
			"/unmap_value", 
			"/add_generic_value",
			"/update_generic_value", 
			"/remove_generic_value", 
			"/report", 
			"/runReport").hasAnyRole("ADMIN","USER")
		.antMatchers("/user*").hasRole("ADMIN")
		.antMatchers("/resources/**").denyAll()
		.anyRequest().permitAll()
		.and()
		.csrf()
		.and()
		.exceptionHandling()
		.and()
		.formLogin()
		.loginPage("/login")
		.failureHandler(authenticationFailureHandler())
		.loginProcessingUrl("/security_check")
		.usernameParameter("j_username").passwordParameter("j_password").permitAll()
		.and()
		.portMapper().http(80).mapsTo(443)
		.http(8080).mapsTo(8443)
		.http(9080).mapsTo(9443)
		.and()
		.logout().invalidateHttpSession(true) // .logoutUrl("/logout").logoutSuccessUrl("/login")
		.and()
		.requiresChannel()
		.antMatchers("/mapping",
			"/get_generic_list",
			"/get_mapped_values",
			"/get_unmapped_values",
			"/map_value",
			"/unmap_value",
			"/add_generic_value",
			"/update_generic_value",
			"/remove_generic_value",
			"/report",
			"/runReport",
			"/password_expired",
			"/user*").requiresSecure()
		.and()
		.httpBasic();
    }

    @Bean
    public DataSource dataSource() throws Exception {
	/*
	String url = System.getProperty("JDBC_CONNECTION_STRING");
	if (url == null) {
	*/
	    this.logger.info("Trying to use JNDI to find DataSource");
	    Context ctx = new InitialContext();
	    DataSource ods = (DataSource) ctx.lookup("java:comp/env/jdbc/LctDB");
	    this.logger.info("JNDI DS used.");
	    return ods;
	/*    
	} else {
	    String userName = System.getProperty("RDS_USERNAME");
	    String password = System.getProperty("RDS_PASSWORD");

	    this.logger.info("JDBC URL:[" + url + "] with USER:[" + userName + "]");
	    OracleDataSource ods = new OracleDataSource();
	    ods.setURL(url);
	    ods.setUser(userName);
	    ods.setPassword(password);
	    ods.setConnectionCachingEnabled(true);
	    ods.setConnectionCacheName(CONNECTION_CACHE_NAME);
	    Properties cacheProps = new Properties();
	    cacheProps.setProperty("MinLimit", "1");
	    cacheProps.setProperty("MaxLimit", "4");
	    cacheProps.setProperty("InitialLimit", "1");
	    cacheProps.setProperty("ConnectionWaitTimeout", "5");
	    cacheProps.setProperty("ValidateConnection", "true");

	    ods.setConnectionCacheProperties(cacheProps);
	    this.logger.info("Custom DS used.");
	    return ods;

	}
	*/
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
	DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource());
	return dataSourceTransactionManager;
    }

    @Bean
    public ReportDao reportDao() throws Exception {
	return new ReportDao(dataSource());
    }

    @Bean
    public MessageDigestPasswordEncoder passwordEncoder() {
	return new MessageDigestPasswordEncoder("SHA-256");

    }

    @Bean
    public LctDaoAuthenticationProvider lctAuthenticationProvider() throws Exception {
	LctDaoAuthenticationProvider retValue = new LctDaoAuthenticationProvider(loginAttemptService());
	retValue.setHideUserNotFoundExceptions(false);
	retValue.setUserDetailsService(lctUserDetailsManager());
	retValue.setPasswordEncoder(passwordEncoder());
	return retValue;
    }

    @Bean
    public JdbcDaoImpl lctUserDetailsManager() throws Exception {
	JdbcDaoImpl retValue = new LctJdbcUserDetailsManager(loginAttemptService());
	retValue.setDataSource(dataSource());
	return retValue;
    }

    @Bean
    public LoginAttemptService loginAttemptService() {
	return new LoginAttemptService();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
	LctUrlAuthenticationFailureHandler retValue = new LctUrlAuthenticationFailureHandler("/login?error=true", "/password_expired/");
	return retValue;
    }

}
