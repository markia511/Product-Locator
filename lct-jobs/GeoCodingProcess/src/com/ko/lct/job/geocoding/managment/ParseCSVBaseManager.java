package com.ko.lct.job.geocoding.managment;

import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_BEV_CAT_PRD_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_DELIVERY_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_MAP_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.UPDATE_MAP_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.LKP_FIND_NAME_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_BEV_CAT_PRD_LNK_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_INIT_ADDRESS_ID_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_INIT_OUTLET_ID;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_MAP_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_UNIQUE_MAP_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.UPDATE_DELIVERY_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_DUMMY_LOOKUP_QUERY;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.Dao.GeocodingQuery;
import com.ko.lct.job.geocoding.businessobjects.GenericValueObject;
import com.ko.lct.job.geocoding.businessobjects.MappingType;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;

public abstract class ParseCSVBaseManager extends AbstractManager {
    protected static final int LOOKUP_BEVERAGE_CATEGORY = 1;
    protected static final int LOOKUP_BRAND = 2;
    protected static final int LOOKUP_FLAVOR = 3;
    protected static final int LOOKUP_PRIMARY_CONTAINER = 4;
    protected static final int LOOKUP_SECONDARY_PACKAGE = 5;
    protected static final int LOOKUP_TRADE_SUB_CHANNEL = 6;
    protected static final int LOOKUP_PRODUCT = 7;

    protected static final String ACTIVE_INDEX = "1";
    protected static final String UNMAPPED_INDEX = "0";

    protected static final int BATCH_SIZE = 5000;
    private static final int MERGE_SIZE = 500;

    protected static int startRow = 0;
    protected static int processArraySize = 100000;

    protected static int newAddrCount = 0;

    protected static int outletIdSeq = 0;
    protected static int addrIdSeq = 0;
    protected static int productPackageIdSeq = 0;
    protected static int delivIdSeq = 0;

    private static java.sql.Date[] mergeDeliveryParams1 = new java.sql.Date[MERGE_SIZE];
    private static int[] mergeDeliveryParams2 = new int[MERGE_SIZE];
    private static int[] mergeDeliveryParams3 = new int[MERGE_SIZE];
    private static int[] mergeDeliveryParamsIds = new int[MERGE_SIZE];
    private static int mergeDeliveryParamIndex = 0;

    private static PreparedStatement getMaxDelivIdStmt;
    private static PreparedStatement deliverySelectAllStmt;
    private static PreparedStatement deliveryInsertStmt;
    private static PreparedStatement deliveryUpdateStmt;

    private static PreparedStatement insertBevCatPrdStmt;

    private static PreparedStatement mapInsertStmt;
    private static PreparedStatement mapUpdateStmt;
    private static PreparedStatement mapSelectStmt; 
    private static CallableStatement insertDummyLookupStmt;    
    private static PreparedStatement lkpFindNameStmt;

    private static HashMap<String, Set<String>> bevCatPrdLnkMap = new HashMap<String, Set<String>>();

    /*
     * private static Map<String, String> productLookupMap = new HashMap<String, String>(); private static Map<String, String> brandLookupMap = new HashMap<String,
     * String>(); private static Map<String, String> flavorLookupMap = new HashMap<String, String>();
     */
    private static Map<String, String> primContToShrtCodeLookupMap = new HashMap<String, String>();
    private static Map<String, String> secPkgToShrtCodeLookupMap = new HashMap<String, String>();

    protected static void initBasePreparedStatements(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	getMaxDelivIdStmt = connectionBroker.getNewPreparedStatement(GeocodingQuery.SELECT_DELIV_SEQUENCE, schemaName);
	updateMaxDelivId();
	deliveryInsertStmt = connectionBroker.getNewPreparedStatement(INSERT_DELIVERY_QUERY, schemaName);
	deliverySelectAllStmt = connectionBroker.getNewPreparedStatement(GeocodingQuery.genDeliverySelectAllSql(MERGE_SIZE), schemaName);
	deliveryUpdateStmt = connectionBroker.getNewPreparedStatement(UPDATE_DELIVERY_QUERY, schemaName);

	insertBevCatPrdStmt = connectionBroker.getNewPreparedStatement(INSERT_BEV_CAT_PRD_QUERY, schemaName);
	initBevCatPrdLnkMap(connectionBroker, schemaName);

	mapInsertStmt = connectionBroker.getNewPreparedStatement(INSERT_MAP_QUERY, schemaName);
	mapUpdateStmt = connectionBroker.getNewPreparedStatement(UPDATE_MAP_QUERY, schemaName);
	mapSelectStmt = connectionBroker.getNewPreparedStatement(SELECT_UNIQUE_MAP_QUERY, schemaName);
	insertDummyLookupStmt = connectionBroker.getNewCallableStatement(INSERT_DUMMY_LOOKUP_QUERY, schemaName);
	lkpFindNameStmt = connectionBroker.getNewPreparedStatement(LKP_FIND_NAME_QUERY, schemaName);
    }

    protected static void initOutletIdSeq(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_INIT_OUTLET_ID, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		if (rs.next()) {
		    outletIdSeq = rs.getInt(1);
		}
	    } finally {
		rs.close();
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
    }

    protected static void initAddrIdSeq(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_INIT_ADDRESS_ID_QUERY, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		if (rs.next()) {
		    addrIdSeq = rs.getInt(1);
		}
		newAddrCount = 0;
	    } finally {
		rs.close();
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
    }

    protected static void initProductPackageIdSeq(JdbcConnectionBroker connectionBroker, String schemaName)
	    throws SQLException {
	PreparedStatement stmt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.SELECT_PRODUCT_PACKAGE_SEQUENCE, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		if (rs.next()) {
		    productPackageIdSeq = rs.getInt(1);
		}
	    } finally {
		rs.close();
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
    }

    protected static void closeMaxDelivIdStmt(JdbcConnectionBroker connectionBroker) {
	connectionBroker.closeStatement(getMaxDelivIdStmt);
    }

    protected static void updateMaxDelivId() throws SQLException {
	ResultSet rs = getMaxDelivIdStmt.executeQuery();
	try {
	    if (rs.next()) {
		delivIdSeq = rs.getInt(1);
	    }
	} finally {
	    rs.close();
	}
    }

    protected static void addMergeDeliveryRow(java.sql.Date delivDate, int outletId, int productPackageId) throws SQLException {
	// search same outlet with same product/package

	for (int i = 0; i < mergeDeliveryParamIndex; i++) {
	    if (mergeDeliveryParams2[i] == outletId
		    && mergeDeliveryParams3[i] == productPackageId) {
		if (delivDate.compareTo(mergeDeliveryParams1[i]) > 0) {
		    mergeDeliveryParams1[i] = delivDate;
		    return;
		}
		return;
	    }
	}

	mergeDeliveryParamsIds[mergeDeliveryParamIndex] = -1;
	mergeDeliveryParams1[mergeDeliveryParamIndex] = delivDate;
	mergeDeliveryParams2[mergeDeliveryParamIndex] = outletId;
	mergeDeliveryParams3[mergeDeliveryParamIndex] = productPackageId;
	mergeDeliveryParamIndex++;
	if (mergeDeliveryParamIndex >= MERGE_SIZE) {
	    executeMergeDeliveryStmt();
	}
    }

    protected static void executeMergeDeliveryStmt() throws SQLException {
	int paramIndex = 0;
	for (int i = 0; i < mergeDeliveryParamIndex; i++) {
	    deliverySelectAllStmt.setInt(++paramIndex, mergeDeliveryParams2[i]);
	    deliverySelectAllStmt.setInt(++paramIndex, mergeDeliveryParams3[i]);
	}
	ResultSet rs = deliverySelectAllStmt.executeQuery();
	try {
	    while (rs.next()) {
		int delivId = rs.getInt(1);
		Timestamp delivDt = rs.getTimestamp(2);
		int outletId = rs.getInt(3);
		int prdPkgId = rs.getInt(4);
		for (int i = 0; i < mergeDeliveryParamIndex; i++) {
		    if (mergeDeliveryParams2[i] == outletId && mergeDeliveryParams3[i] == prdPkgId) {
			if (mergeDeliveryParams1[i].compareTo(delivDt) > 0) {
			    mergeDeliveryParamsIds[i] = delivId;
			}
			else {
			    mergeDeliveryParamsIds[i] = 0;
			}
			break;
		    }
		}
	    }
	} finally {
	    rs.close();
	}

	paramIndex = 0;
	boolean isInsert = false;
	boolean isUpdate = false;
	for (int i = 0; i < mergeDeliveryParamIndex; i++) {
	    if (mergeDeliveryParamsIds[i] < 0) {
		deliveryInsertStmt.setInt(1, ++delivIdSeq);
		deliveryInsertStmt.setDate(2, mergeDeliveryParams1[i]);
		deliveryInsertStmt.setInt(3, mergeDeliveryParams2[i]);
		deliveryInsertStmt.setInt(4, mergeDeliveryParams3[i]);
		deliveryInsertStmt.addBatch();
		isInsert = true;
	    }
	    else if (mergeDeliveryParamsIds[i] > 0) {
		deliveryUpdateStmt.setDate(1, mergeDeliveryParams1[i]);
		deliveryUpdateStmt.setInt(2, mergeDeliveryParamsIds[i]);
		deliveryUpdateStmt.setDate(3, mergeDeliveryParams1[i]);
		deliveryUpdateStmt.addBatch();
		isUpdate = true;
	    }
	}
	if (isInsert) {
	    deliveryInsertStmt.executeBatch();
	}
	if (isUpdate) {
	    deliveryUpdateStmt.executeBatch();
	}

	mergeDeliveryParamIndex = 0;
    }

    protected static void flushMergeDelivery(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	if (mergeDeliveryParamIndex > 0 && mergeDeliveryParamIndex < MERGE_SIZE) {
	    deliverySelectAllStmt = connectionBroker.getNewPreparedStatement(GeocodingQuery.genDeliverySelectAllSql(mergeDeliveryParamIndex), schemaName);
	    executeMergeDeliveryStmt();
	}
    }

    protected static void initBevCatPrdLnkMap(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_BEV_CAT_PRD_LNK_QUERY, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    String oldBevCatCode = null;
	    Set<String> prdSet = null;
	    while (rs.next()) {
		String bevCatCode = rs.getString("BEV_CAT_CD");
		if (prdSet == null || !bevCatCode.equals(oldBevCatCode)) {
		    prdSet = new HashSet<String>();
		    bevCatPrdLnkMap.put(bevCatCode, prdSet);
		    oldBevCatCode = bevCatCode;
		}
		prdSet.add(rs.getString("PRD_CD"));
	    }
	} finally {
	    stmt.close();
	}
    }

    protected static void checkBevCatPrdLnk(String bevCatCode, String productCode) throws SQLException {
	Set<String> prdSet = bevCatPrdLnkMap.get(bevCatCode);
	if (prdSet == null || !prdSet.contains(productCode)) {
	    insertBevCatPrdMap(bevCatCode, productCode);
	}
    }

    protected static void insertBevCatPrdMap(String beverageCategoryCode, String productCode) throws SQLException {
	insertBevCatPrdStmt.setString(1, beverageCategoryCode);
	insertBevCatPrdStmt.setString(2, productCode);
	insertBevCatPrdStmt.executeUpdate();
	Set<String> prdSet = bevCatPrdLnkMap.get(beverageCategoryCode);
	if (prdSet == null) {
	    prdSet = new HashSet<String>();
	    bevCatPrdLnkMap.put(beverageCategoryCode, prdSet);
	}
	prdSet.add(productCode);
    }

    protected static void loadLookupMap(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	/*
	 * PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_LOOKUP_QUERY, schemaName); try { stmt.setInt(1, LOOKUP_BRAND); ResultSet rs =
	 * stmt.executeQuery(); try { while (rs.next()) { productLookupMap.put(rs.getString("LKP_CD"), rs.getString("NAME")); } } finally { rs.close(); }
	 * 
	 * stmt.setInt(1, LOOKUP_FLAVOR); rs = stmt.executeQuery(); try { while (rs.next()) { flavorLookupMap.put(rs.getString("LKP_CD"), rs.getString("NAME")); } }
	 * finally { rs.close(); }
	 * 
	 * } finally { stmt.close(); }
	 */

	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_MAP_QUERY, schemaName);
	try {
	    stmt.setInt(1, LOOKUP_PRIMARY_CONTAINER);
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    primContToShrtCodeLookupMap.put(rs.getString("SRC_VAL"), rs.getString("LKP_CD"));
		}
	    } finally {
		rs.close();
	    }

	    stmt.setInt(1, LOOKUP_SECONDARY_PACKAGE);
	    rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    secPkgToShrtCodeLookupMap.put(rs.getString("SRC_VAL"), rs.getString("LKP_CD"));
		}
	    } finally {
		rs.close();
	    }

	} finally {
	    stmt.close();
	}

    }

    protected static void initConvertedMaps(JdbcConnectionBroker connectionBroker, String schemaName,
	    MappingType mappingType, Map<String, GenericValueObject> convertedMap) throws SQLException {
	String key;
	int paramIndex = 0;
	PreparedStatement initConvertedBrandStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.GET_CONVERTED_GENERIC, schemaName);
	try {
	    initConvertedBrandStmnt.setInt(++paramIndex, mappingType.getValue());
	    ResultSet rs = initConvertedBrandStmnt.executeQuery();
	    try {
		while (rs.next()) {
		    key = rs.getString("SRC_VAL");
		    if (key != null) {
			convertedMap.put(normalizeMapSourceValue(key), 
				new GenericValueObject(rs.getString("LKP_CD"), rs.getString("NAME")));
		    }
		}
	    } finally {
		rs.close();
	    }
	} finally {
	    initConvertedBrandStmnt.close();
	}
    }

    protected static String getPrimaryContainerShrtCode(String primaryContainerName) {
	return primContToShrtCodeLookupMap.get(normalizeMapSourceValue(primaryContainerName));
    }

    protected static String getSecondaryPackageShrtCode(String secondaryPackageName) {
	return secPkgToShrtCodeLookupMap.get(normalizeMapSourceValue(secondaryPackageName));
    }

    private static String extractSecPkgSizeFromName(String secondaryPackageName) {
	if (secondaryPackageName == null) {
	    return null;
	}
	int i = secondaryPackageName.length() - 1;
	while (i >= 0 && "0123456789".indexOf(secondaryPackageName.charAt(i)) < 0) {
	    i--;
	}
	if (i < 0) {
	    return null;
	}
	int j = i;

	while (i >= 0 && "0123456789".indexOf(secondaryPackageName.charAt(i)) >= 0) {
	    i--;
	}
	return secondaryPackageName.substring(i + 1, j + 1);
    }

    protected static String getSecondaryPackageShrtCodeBySize(String secondaryPackageName) throws SQLException {
	String size = extractSecPkgSizeFromName(secondaryPackageName);
	if (size == null) {
	    return null;
	}
	String secPkgShtrCode = null;
	lkpFindNameStmt.setInt(1, LOOKUP_SECONDARY_PACKAGE);
	lkpFindNameStmt.setString(2, size);
	ResultSet rs = lkpFindNameStmt.executeQuery();
	try {
	    if (rs.next()) {
		secPkgShtrCode = rs.getString("LKP_CD");
		secPkgToShrtCodeLookupMap.put(secondaryPackageName, secPkgShtrCode);
	    }
	} finally {
	    rs.close();
	}
	return secPkgShtrCode;
    }

    protected static String addUnmappedValue(int lookupTypeId, String mapSourceValue) throws SQLException {
	
	String dummyLookupCode = null;
	int paramIndex = 0;

	mapSelectStmt.setInt(++paramIndex, lookupTypeId);
	mapSelectStmt.setString(++paramIndex, mapSourceValue);
	ResultSet rs = mapSelectStmt.executeQuery();
	
	try {
	    if(rs.next()) {	
    	        if(lookupTypeId != LOOKUP_TRADE_SUB_CHANNEL) {
        	    	dummyLookupCode = rs.getString(1);
        	    	if(dummyLookupCode == null) {
        	    	    dummyLookupCode = addDummyLookup(lookupTypeId, mapSourceValue);
        	    	}  
    	        }
    	
    	    	paramIndex = 0;
    	    	mapUpdateStmt.setString(++paramIndex, mapSourceValue);
    	    	mapUpdateStmt.setString(++paramIndex, UNMAPPED_INDEX);
    	    	mapUpdateStmt.setInt(++paramIndex, lookupTypeId);
    	    	mapUpdateStmt.setString(++paramIndex, dummyLookupCode);
    	    	mapUpdateStmt.executeUpdate();    	    	
	    } else {
                if(lookupTypeId != LOOKUP_TRADE_SUB_CHANNEL) {
        	    	dummyLookupCode = addDummyLookup(lookupTypeId, mapSourceValue);
                }
    	
    	    	paramIndex = 0;
    	    	mapInsertStmt.setInt(++paramIndex, lookupTypeId);
    	    	mapInsertStmt.setString(++paramIndex, dummyLookupCode);
    	    	mapInsertStmt.setString(++paramIndex, mapSourceValue);
    	    	mapInsertStmt.setString(++paramIndex, UNMAPPED_INDEX);
    	    	mapInsertStmt.executeUpdate();
	    }
	} finally {
	    rs.close();
	}
	return dummyLookupCode;
    }

    protected static String addDummyLookup(int lookupTypeId, String mapSourceValue) throws SQLException {	
	int paramIndex = 0;
	String dummyLookupCode = null;
    	insertDummyLookupStmt.setInt(++paramIndex, lookupTypeId);
    	insertDummyLookupStmt.setString(++paramIndex, mapSourceValue);
    	insertDummyLookupStmt.registerOutParameter(++paramIndex, Types.NVARCHAR);
    	insertDummyLookupStmt.executeUpdate();
    	dummyLookupCode = insertDummyLookupStmt.getString(paramIndex);
	return dummyLookupCode;
    }

    protected static Double getBaseSize(String primaryContainerName) {
	String newPrimaryContainerName = primaryContainerName.trim().toUpperCase();
	String vol = "";
	int i = 0;
	while (i < newPrimaryContainerName.length() && "0123456789.".indexOf(newPrimaryContainerName.charAt(i)) >= 0) {
	    vol += newPrimaryContainerName.charAt(i);
	    i++;
	}
	if (vol.isEmpty()) {
	    return null;
	}
	double d = Double.parseDouble(vol);
	if (newPrimaryContainerName.indexOf("-LTR") > 0) {
	    d = d * 33.8140225589;
	}
	else if (newPrimaryContainerName.indexOf("-ML") > 0) {
	    d = d * 0.0338140227;
	}
	else if (newPrimaryContainerName.indexOf("-GAL") > 0) {
	    d = d * 128;
	}
	return Double.valueOf(d);
    }

    protected static String normalizeMapSourceValue(String mapSourceValue) {
	if (mapSourceValue == null) {
	    return null;
	}
	StringBuffer sb = new StringBuffer(mapSourceValue);
	int i;
	while ((i = sb.indexOf(" ")) >= 0) { // Unicode Space
	    sb.replace(i, i+1, " ");
	}
	
	while ((i = sb.indexOf("  ")) >= 0) {
	    sb.delete(i, i + 1);
	}
	
	return sb.toString().toUpperCase();
    }

}
