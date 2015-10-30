package com.ko.lct.ws.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ko.lct.ws.dao.LocatorDao;

@Configuration
@EnableTransactionManagement
public class WsAppConfig {
    protected final Log logger = LogFactory.getLog(getClass());
    // private static final String CONNECTION_CACHE_NAME = "lct-ws-connection-cache";

    @Bean
    public DataSource dataSource() throws Exception {
	/*
	String url = System.getProperty("JDBC_CONNECTION_STRING");
	if (url == null) {
	*/
	    this.logger.info("Trying to use JNDI to find DataSource");
	    Context ctx = new InitialContext();
	    DataSource ods = (DataSource) ctx
		    .lookup("java:comp/env/jdbc/LctDB");
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
	    cacheProps.setProperty("MaxLimit", "7");
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
	DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(
		dataSource());
	return dataSourceTransactionManager;
    }

    @Bean
    public LocatorDao locatorDao() throws Exception {
	return new LocatorDao(dataSource());
    }

}
