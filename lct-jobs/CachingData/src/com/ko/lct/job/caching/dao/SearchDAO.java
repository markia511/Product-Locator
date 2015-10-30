package com.ko.lct.job.caching.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ko.lct.job.caching.bean.DistanceUnits;
import com.ko.lct.job.caching.bean.SearchRequest;
import com.ko.lct.job.caching.bean.SearchRequestV2;
import com.ko.lct.job.caching.bean.ServiceTimer;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;

public class SearchDAO {

    private static final Logger logger = Logger.getLogger(SearchDAO.class.getName());

    private static final String ALL_SELECTED_VALUE_NAME = "*";

    private static final String SEARCH_OUTLET_SQL =
	    "select \n" +
		    "       ROW_NUM, \n" +
		    "       ROW_COUNT, \n" +
		    "       DIST, \n" +
		    "       OUTLET_ID, \n" +
		    "       ADDR_ID, \n" +
		    "       TDL_CD, \n" +
		    "       NM_ID1, \n" +
		    "       NM_ID2, \n" +
		    "       CHN_NM, \n" +
		    "       OUTLET_NM, \n" +
		    "       PHNE_NBR, \n" +
		    "       SB_CHNL_ID, \n" +
		    "       LATITUDE, \n" +
		    "       LONGITUDE, \n" +
		    "       SB_CHNL_NM, \n" +
		    "       CHNL_ID, \n" +
		    "       CHNL_NM, \n" +
		    "       FOOD_SRVC_IND, \n" +
		    "       CTRY_CD, \n" +
		    "       STATE, \n" +
		    "       CITY, \n" +
		    "       PSTL_CD, \n" +
		    "       ADDR_LINE_1, \n" +
		    "       ADDR_LINE_2, \n" +
		    "       FRMT_ADDR, \n" +
		    "       GEO_LVL \n" +
		    "  from TABLE(LCT_PKG.OUTLET_DELIV_LIST(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)) \n";

    private static final int LAST_REQUESTS_COUNT = 100;

    private static final String GET_REQUEST_ID_LIST_QUERY =
	    "select RQST_ID \n" +
		    "  from (select * \n" +
		    " 	       from T_RQST \n" +
		    "	      where OUTLET_CNT > 0 \n" +
		    "	      order by RQST_ID desc \n" +
		    "	    )  \n" +
		    " where rownum < " + LAST_REQUESTS_COUNT + "\n" +
		    " order by OUTLET_CNT desc \n";

    private static final String GET_REQUEST_PARAM_QUERY =
	    "select rd.RQST_ID,    \n" +
		    "      t.NAME as TYPE_NAME, \n" +
		    "      rd.VALUE \n" +
		    " from T_RQST_PRM_TYPE t, \n" +
		    "      T_RQST_DTL rd \n" +
		    "where t.RQST_PRM_TYPE_ID = rd.RQST_PRM_TYPE_ID \n" +
		    "  and rd.RQST_ID = ? \n" +
		    "order by rd.RQST_PRM_TYPE_ID \n";

    private static final String REQUEST_ID = "RQST_ID";
    private static final String REQUEST_PARAM_TYPE = "TYPE_NAME";
    private static final String REQUEST_PARAM_VALUE = "VALUE";
    private static final String BOOLEAN_YES = "1";

    private static enum ParamTypeEnum {
	locale,
	latitude,
	longitude,
	distance,
	distanceUnit,
	beverageCategoryCode,
	productTypeCode,
	brandCode,
	flavorCode,
	productCode,
	primaryContainerCode,
	secondaryPackageCode,
	businessTypeCode,
	physicalState,
	tradeChannelCode,
	subTradeChannelCode,
	outletName,
	includeFoodService,
	kosherProductOnly,
	pageNumber,
	recordsCount
    }

    private String schema;

    public SearchDAO(String schema) {
	this.schema = schema;
    }

    public void casheLocation(SearchRequest searchRequest, JdbcConnectionBroker connectionBroker) throws SQLException, InterruptedException {

	logger.info("Request [latitude=" + searchRequest.getLatitude()
		+ ", longitude=" + searchRequest.getLongitude()
		+ ", distance=" + searchRequest.getDistance()
		+ " " + searchRequest.getDistanceUnit() + "]");

	double distance = searchRequest.getDistance();
	if (distance > 1000) {
	    distance = 1000; // Maximum Value of distance is 1000
	}

	ArrayList<Object> outletParameters = new ArrayList<Object>();
	addSelectParam(outletParameters, Double.valueOf(searchRequest.getLatitude()));
	addSelectParam(outletParameters, Double.valueOf(searchRequest.getLongitude()));
	addSelectParam(outletParameters, Double.valueOf(searchRequest.getDistance()));
	addSelectParam(outletParameters,
		Integer.valueOf((searchRequest.getDistanceUnits() == DistanceUnits.mi) ? 0 : 1));
	addSelectParam(outletParameters, searchRequest.getBeverageCategoryCode());
	addSelectParam(outletParameters, searchRequest.getProductTypeCode());
	if (searchRequest instanceof SearchRequestV2) {
	    addSelectParam(outletParameters, ((SearchRequestV2) searchRequest).getProductCode());
	}
	else {
	    addSelectParam(outletParameters, null);
	}
	addSelectParam(outletParameters, searchRequest.getBrandCode());
	addSelectParam(outletParameters, searchRequest.getFlavorCode());
	addSelectParam(outletParameters, searchRequest.getPrimaryContainerCode());
	addSelectParam(outletParameters, searchRequest.getSecondaryPackageCode());
	addSelectParam(outletParameters, searchRequest.getBusinessTypeCode());
	addSelectParam(outletParameters, searchRequest.getPhysicalStateCode());
	addSelectParam(outletParameters, searchRequest.getTradeChannelCode());
	addSelectParam(outletParameters, searchRequest.getSubTradeChannelCode());
	addSelectParam(outletParameters, searchRequest.getOutletName());
	addSelectParam(outletParameters, Integer.valueOf(searchRequest.isIncludeFoodService() ? 1 : 0));
	addSelectParam(outletParameters, Integer.valueOf(searchRequest.isKosherProductOnly() ? 1 : 0));
	addSelectParam(outletParameters, Integer.valueOf(searchRequest.getPageNumber()));
	addSelectParam(outletParameters, Integer.valueOf(searchRequest.getRecordsCount()));
	addSelectParam(outletParameters,
		searchRequest.getSortColumnEnum() == null ? null : searchRequest.getSortColumnEnum().toString());
	addSelectParam(outletParameters, searchRequest.getSortOrder());

	ServiceTimer timer = new ServiceTimer();
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SEARCH_OUTLET_SQL, schema);
	try {
	    int paramInd = 0;
	    for (Object param : outletParameters) {
		if (param == null) {
		    stmt.setNull(++paramInd, Types.VARCHAR);
		} else if (param instanceof Double) {
		    stmt.setDouble(++paramInd, ((Double) param).doubleValue());
		} else if (param instanceof Integer) {
		    stmt.setInt(++paramInd, ((Integer) param).intValue());
		} else {
		    stmt.setString(++paramInd, String.valueOf(param));
		}
	    }
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    Thread.sleep(1);
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    stmt.close();
	}
	logger.info("Getting outlets completed in " + timer.getCurrentDurationTime() + " ms");
    }

    private static void addSelectParam(ArrayList<Object> params, Object selectValue) {
	if (selectValue != null && !selectValue.equals(ALL_SELECTED_VALUE_NAME)) {
	    params.add(selectValue);
	}
	else {
	    params.add(null);
	}
    }

    static String getConditionOrderQuery(String query, String[][] replaceArr) {
	String tempQuery = query;
	for (String[] pair : replaceArr) {
	    tempQuery = tempQuery.replace(pair[0], pair[1]);
	}
	return tempQuery;
    }

    public List<Integer> getRequestIdList(JdbcConnectionBroker connectionBroker) throws SQLException {
	List<Integer> requestIdList = new ArrayList<Integer>();
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(GET_REQUEST_ID_LIST_QUERY, schema);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    requestIdList.add(Integer.valueOf(rs.getInt(REQUEST_ID)));
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    stmt.close();
	}
	return requestIdList;
    }

    public SearchRequest getRandomRequest(JdbcConnectionBroker connectionBroker, Integer requestId) throws SQLException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(GET_REQUEST_PARAM_QUERY, schema);
	SearchRequest request = new SearchRequest();
	try {
	    stmt.setInt(1, requestId.intValue());
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    retrieveRequestParam(request, rs.getString(REQUEST_PARAM_TYPE), rs.getString(REQUEST_PARAM_VALUE));
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    stmt.close();
	}
	return request;
    }

    private static void retrieveRequestParam(SearchRequest request, String paramType, String paramValue) {
	String value = paramValue;
	if (value == null)
	    value = ALL_SELECTED_VALUE_NAME;

	if (paramType == null)
	    return;

	ParamTypeEnum paramTypeEnum = ParamTypeEnum.valueOf(paramType);

	switch (paramTypeEnum) {
	case beverageCategoryCode:
	    request.setBeverageCategoryCode(paramValue);
	    break;
	case brandCode:
	    request.setBrandCode(paramValue);
	    break;
	case businessTypeCode:
	    request.setBusinessTypeCode(paramValue);
	    break;
	case distance:
	    request.setDistance(Integer.parseInt(paramValue));
	    break;
	case distanceUnit:
	    request.setDistanceUnit(paramValue);
	    break;
	case flavorCode:
	    request.setFlavorCode(paramValue);
	    break;
	case includeFoodService:
	    request.setIncludeFoodService(BOOLEAN_YES.equals(paramValue));
	    break;
	case kosherProductOnly:
	    request.setKosherProductOnly(BOOLEAN_YES.equals(paramValue));
	    break;
	case latitude:
	    request.setLatitude(Double.parseDouble(paramValue));
	    break;
	case locale:
	    request.setLocale(paramValue);
	    break;
	case longitude:
	    request.setLongitude(Double.parseDouble(paramValue));
	    break;
	case outletName:
	    request.setOutletName(paramValue);
	    break;
	case pageNumber:
	    request.setPageNumber(Integer.parseInt(paramValue));
	    break;
	case physicalState:
	    request.setPhysicalStateCode(paramValue);
	    break;
	case primaryContainerCode:
	    request.setPrimaryContainerCode(paramValue);
	    break;
	case productTypeCode:
	    request.setProductTypeCode(paramValue);
	    break;
	case recordsCount:
	    request.setRecordsCount(Integer.parseInt(paramValue));
	    break;
	case secondaryPackageCode:
	    request.setSecondaryPackageCode(paramValue);
	    break;
	case subTradeChannelCode:
	    request.setSubTradeChannelCode(paramValue);
	    break;
	case tradeChannelCode:
	    request.setTradeChannelCode(paramValue);
	    break;
	case productCode:
	    if (request instanceof SearchRequestV2)
		((SearchRequestV2) request).setProductCode(paramValue);
	    break;
	default:
	    break;
	}
    }

    private static void closeResultSet(ResultSet rs) {
	JdbcConnectionBroker.closeResultSet(rs);
    }

}
