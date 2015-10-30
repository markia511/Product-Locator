package com.ko.lct.job.caching;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ko.lct.job.caching.bean.SearchRequest;
import com.ko.lct.job.caching.dao.SearchDAO;
import com.ko.lct.job.caching.utilities.CachingDataProperties;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;

public class CachingData {
    
    private static final long CACHING_TIME = 20000;
    
    /**
     * @param args
     * @throws ApplicationException
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws SQLException, ApplicationException, IOException, InterruptedException {
	JdbcConnectionBroker connectionBroker = new JdbcConnectionBroker();
	try {
	    connectionBroker.connect(CachingDataProperties.getDbUrlParam(), CachingDataProperties.getUserName(), 
		    CachingDataProperties.getPassword());
	    processCachingData(connectionBroker);
	} finally {
	    connectionBroker.cleanup();
	}
    }
    
    private static void processCachingData(JdbcConnectionBroker connectionBroker) 
	    throws ApplicationException, SQLException, InterruptedException {
	SearchDAO searchDAO = new SearchDAO(CachingDataProperties.getSchema());
	long cachingTime = CachingDataProperties.getCachingTime();
	if(cachingTime == 0) {
	    cachingTime = CACHING_TIME;
	}
	while (true) {
	    List<Integer> requestIdList = searchDAO.getRequestIdList(connectionBroker);
	    for(Integer requestId : requestIdList) {
		SearchRequest searchRequest = searchDAO.getRandomRequest(connectionBroker, requestId);
        	searchDAO.casheLocation(searchRequest, connectionBroker);
        	Thread.sleep(cachingTime);
	    }
	}
    }
}
