package com.ko.lct.job.geocoding.Dao;

import com.ko.lct.job.geocoding.utilities.GeocodingConstants;

public class GeocodingQuery {

    public static final String SELECT_ADRESS_QUERY = "select ADDR_ID from %s.T_ADDR where STATE = ? AND CITY=? AND ADDR_LINE_1 = ?";
    public static final String SELECT_ADRESS_WITHOUT_ADDR_LINE_1_QUERY = "select ADDR_ID from %s.T_ADDR where STATE = ? AND CITY=? AND ADDR_LINE_1 is null";
    public static final String INSERT_ADRESS_QUERY = "insert into %s.T_ADDR(ADDR_ID, STATE, CITY, PSTL_CD, ADDR_LINE_1, ADDR_LINE_2, CTRY_CD, CREATED_DT) VALUES(?,?,?,?,?,?,?,?)";
    public static final String SELECT_INIT_ADDRESS_ID_QUERY = "select max(ADDR_ID) from %s.T_ADDR";

    public static final String IF_EMPTY_CHECK_QUERY = "SELECT CTRY_CD FROM %s.T_CTRY where NAME = ?";
    public static final String INSERT_COUNTRY_QUERY = "insert into %s.T_CTRY(CTRY_CD,NAME) VALUES(?,?)";
    public static final String INSERT_CHANNEL_ORG_QUERY = "insert into %s.T_CHNL_ORG (CHNL_ORG_ID,NAME) values(?,?)";
    public static final String INSERT_CHANNEL_QUERY = "insert into %s.T_CHNL (CHNL_ID,NAME,CHNL_ORG_ID,FOOD_SRVC_IND) values(?,?,?,?)";
    public static final String INSERT_SB_CHANNEL_QUERY = "insert into %s.T_SB_CHNL (SB_CHNL_ID,NAME,CHNL_ID) values(?,?,?)";
    public static final String SELECT_CHANNEL_ORG_QUERY = "select CHNL_ORG_ID from %s.T_CHNL_ORG where NAME=?";
    public static final String SELECT_CHANNEL_QUERY = "select CHNL_ID from %s.T_CHNL where CHNL_ID=?";
    public static final String SELECT_SB_CHANNEL_QUERY = "select SB_CHNL_ID from %s.T_SB_CHNL where SB_CHNL_ID=?";
    public static final String INSERT_OUTLET_QUERY = "insert into %s.T_OUTLET (OUTLET_ID,TDL_CD,TDL_GRP_CD, NM_ID1, NM_ID2, CHN_NM, NAME, ADDR_ID, SB_CHNL_ID, PHNE_NBR,CREATED_DT, BSNS_TYPE_ID, FOOD_SRVC_IND) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String SELECT_OUTLET_QUERY = "select OUTLET_ID, o.ADDR_ID, STATE, CITY, ADDR_LINE_1 from %s.T_OUTLET o, %s.T_ADDR a where a.ADDR_ID = o.ADDR_ID and o.NM_ID1 = ? and o.NM_ID2 = ?";
    public static final String SELECT_OUTLET_BY_ALL_COLUMN_QUERY = "select OUTLET_ID from %s.T_OUTLET o where o.NM_ID1 = ? and lower(o.CHN_NM) = lower(?) and TDL_CD = ? and ADDR_ID = ? and SB_CHNL_ID = ? and BSNS_TYPE_ID = ?";
    public static final String SELECT_INIT_OUTLET_ID = "select max(OUTLET_ID) from %s.T_OUTLET";
    public static final String UPDATE_OUTLET_ADDR_ID = "update %s.T_OUTLET set ADDR_ID = ? where OUTLET_ID = ?";
    public static final String INSERT_BEV_CATEGORY_QUERY = "insert into %s.T_BEV_CAT (BEV_CAT_CD,NAME) VALUES(?,?)";
    public static final String SELECT_BEV_CATEGORY_QUERY = "select BEV_CAT_CD from %s.T_BEV_CAT where BEV_CAT_CD=?";
    public static final String INSERT_BEV_CAT_PRD_QUERY = "insert into %s.T_BEV_CAT_PRD (BEV_CAT_CD, PRD_CD) values (?, ?)";
    public static final String INSERT_PRODUCT_QUERY = "insert into %s.T_PRD (PRD_CD, BRND_CD, FLVR_CD) values (?,?,?)";
    public static final String SELECT_PRODUCT_CODE_QUERY = "select PRD_CD from %s.T_PRD where PRD_CD = ?";
    public static final String SELECT_PRODUCT_PACKAGE_BY_PRD_CODE_QUERY = "select BRND_CD, FLVR_CD from %s.T_PRD_PKG where PRD_CD = ?";
    public static final String UPDATE_PRODUCT_QUERY = "update %s.T_PRD set BRND_CD = ?, FLVR_CD = ? where PRD_CD = ?";
    public static final String UPDATE_PRODUCT_PACKAGE_QUERY = "update %s.T_PRD_PKG set BRND_CD = ?, FLVR_CD = ? where PRD_CD = ?";
    public static final String INSERT_PACKAGE_QUERY = "insert into %s.T_PKG (PKG_PRIM_CD,PKG_SECN_CD,CAT_NM, PRIM_SHRT_CD, PRIM_SIZE, SECN_SHRT_CD) values (?,?,?,?,?,?)";
    public static final String INSERT_PRODUCT_PCG_TYPE_QUERY = "insert into %s.T_PRD_PKG_TYPE (PRD_PKG_TYPE_ID,NAME) VALUES(?,?)";
    public static final String SELECT_PRODUCT_PCG_TYPE_QUERY = "select PRD_PKG_TYPE_ID from %s.T_PRD_PKG_TYPE where PRD_PKG_TYPE_ID = ? AND NAME = ?";
    public static final String INSERT_PRODUCT_TYPE_QUERY = "insert into %s.T_PRD_TYPE (PRD_TYPE_ID,NAME) VALUES(?,?)";
    public static final String INSERT_PRODUCT_PKG_QUERY = "insert into %s.T_PRD_PKG (PRD_PKG_ID, PRD_CD, PKG_PRIM_CD, PKG_SECN_CD, BPP_CD, BPP_NM, PHYS_ST_CD, PHYS_ST_NM, PRD_PKG_TYPE_ID, BRND_CD, FLVR_CD) values (?,?,?,?,?,?,?,?,?,?,?)";
    public static final String INSERT_LOG_QUERY = "insert into %s.T_LOG (LOG_ID,DT,STTS_CD,MSG) VALUES(Q_LOG_ID.nextval,?,?,?)";
    public static final String SELECT_COUNTRY_CODES_QUERY = "select CTRY_CD, NAME from %s.T_CTRY";
    public static final String INSERT_BUSINESS_TYPE_QUERY = "insert into %s.T_BSNS_TYPE (BSNS_TYPE_ID, NAME) values(?, ?)";
    public static final String SELECT_BUSINESS_TYPE_QUERY = "select BSNS_TYPE_ID, NAME from %s.T_BSNS_TYPE where BSNS_TYPE_ID = ?";

    public static final String SELECT_DELIVERY_QUERY = "select DELIV_ID, DELIV_DT from %s.T_DELIV where OUTLET_ID = ? and PRD_PKG_ID = ?";
    public static final String INSERT_DELIVERY_QUERY = "insert into %s.T_DELIV (DELIV_ID,DELIV_DT,OUTLET_ID,PRD_PKG_ID,CREATED_DT) values (?,?,?,?,sysdate)";
    public static final String UPDATE_DELIVERY_QUERY = "update %s.T_DELIV set DELIV_DT = ?, MODIFIED_DT = sysdate WHERE DELIV_ID = ? and DELIV_DT < ?";

    public static final String GET_ADDRESS_LIST =
	    " select a.ADDR_ID,		\n" +
		    "        a.CTRY_CD,		\n" +
		    "        a.STATE,		\n" +
		    "        a.CITY,		\n" +
		    "        a.PSTL_CD,		\n" +
		    "        a.ADDR_LINE_1,	\n" +
		    "        a.ADDR_LINE_2,	\n" +
		    "        (select o.NAME 	\n" +
		    "           from %s.T_OUTLET o	\n" +
		    "          where o.ADDR_ID = a.ADDR_ID	\n" +
		    "            and rownum < 2 \n" +
		    "        ) as OUTLET_NAME   \n" +
		    "  from %s.T_ADDR a  \n" +
		    " where exists (select 1  \n" +
		    "                 from %s.T_OUTLET o \n" +
		    "                where o.ADDR_ID = a.ADDR_ID)    \n" +
		    "                  and a.GEO_DT is null \n" +
		    "   and rownum < (? - nvl((select count(*) from %s.T_ADDR where GEO_DT >= trunc(sysdate)),0)) \n";

    public static final String GET_ZIP_LIST =
	    " select distinct a.PSTL_CD		\n" +
		    "  from %s.T_ADDR a  \n" +
		    " where a.geo_lvl is null and latitude is null and longitude is null\n" +
		    "   and rownum < (? - nvl((select count(*) from %s.T_ADDR where GEO_DT >= trunc(sysdate)),0)) \n";

    public static final String UPDATE_ADDRESS_GEOCODE =
	    "	 update %s.T_ADDR \n" +
		    "		set FRMT_ADDR	= ?, \n" +
		    "	 		LATITUDE 	= ?, \n" +
		    "			LONGITUDE 	= ?, \n" +
		    "	 		GEO_LVL 	= ?, \n" +
		    "	 		GEO_DT 		= sysdate, \n" +
		    "	 		MODIFIED_DT = sysdate \n" +
		    "	  where ADDR_ID 	= ? \n";

    public static final String UPDATE_LOCATION_BY_ZIP =
	    "	 update %s.T_ADDR \n" +
		    "		set LATITUDE 	= ?, \n" +
		    "			LONGITUDE 	= ?, \n" +
		    "	 		MODIFIED_DT = sysdate \n" +
		    "	  where PSTL_CD   = ? \n" +
		    "		and upper(?) = upper (state)\n" +
		    "       and LATITUDE  is null \n" +
		    "       and LONGITUDE is null \n" +
		    "       and GEO_DT   is null \n";

    public static final String REMOVE_OBSOLETE_DELIVERY_DATA =
	    "delete from %s.T_DELIV \n" + "" +
		    " where DELIV_ID in (select DELIV_ID \n" +
		    "                      from %s.T_DELIV deliv \n" +
		    // "                     where deliv.DELIV_DT < (last_day(trunc(add_months(sysdate,-4)))+1) \n" +
		    "                     where deliv.DELIV_DT < trunc(add_months(sysdate,-3)) \n" +
		    // we can't delete more than 5000 in one transaction
		    "                       and rownum <= " + GeocodingConstants.MAX_DELETE_ITEMS_COUNT + ")"; 

    public static final String REMOVE_OBSOLETE_OUTLET_DATA =
	    "delete from %s.T_OUTLET o \n" + "" +
		    " where not exists (select 1 \n" +
		    "                      from %s.T_DELIV deliv \n" +
		    "                     where deliv.OUTLET_ID = o.OUTLET_ID ) \n" +
		    // we can't delete more than 5000 in one transaction
		    "   and rownum <= " + GeocodingConstants.MAX_DELETE_ITEMS_COUNT; 

    public static final String REMOVE_DIGITAL_NOISE =
	    "delete \n" +
		    "  from %s.T_DELIV  \n" +
		    " where PRD_PKG_ID in (select PRD_PKG_ID \n" +
		    "                        from %s.T_PRD_PKG prd_pkg,  \n" +
		    "                             (select PRD_CD \n" +
		    "                                from (select prd_pkg.PRD_CD,  count(*) as CNT \n" +
		    "                                        from %s.T_DELIV deliv, %s.T_PRD_PKG prd_pkg \n" +
		    "                                       where prd_pkg.PRD_PKG_ID = deliv.PRD_PKG_ID \n" +
		    "                                       group by prd_pkg.PRD_CD \n" +
		    "                                     ) where CNT < 10) p \n" +
		    "                       where p.PRD_CD = prd_pkg.PRD_CD \n" +
		    "                     ) \n" +
		    "   and DELIV_DT < add_months(sysdate,-1)";

    public static final String REMOVE_OBSOLETE_PRODUCT_PACKAGE_DATA =
	    " delete from %s.T_PRD_PKG p where not exists (select 1 from %s.T_DELIV d where d.PRD_PKG_ID = p.PRD_PKG_ID) \n";

    public static final String UPDATE_OUTLET_LOCATION =
	    "update %s.T_OUTLET \n" +
		    "   set LATITUDE = ?, LONGITUDE = ?, MODIFIED_DT = sysdate \n" +
		    " where ADDR_ID = ?";
    
    public static final String UPDATE_OUTLET_EMPTY_LOCATION =
	    "update T_OUTLET outlet \n" +
		    "   set (LATITUDE, LONGITUDE) = (select LATITUDE, LONGITUDE \n" +
		    "                                  from T_ADDR addr \n" +
		    "                                 where addr.ADDR_ID = outlet.ADDR_ID) \n" +
		    " where (outlet.LATITUDE is null or outlet.LONGITUDE is null) \n" +
		    "   and exists(select 1 \n" +
		    "                from T_ADDR addr \n" +
		    "               where addr.ADDR_ID = outlet.ADDR_ID \n" +
		    "                 and addr.LATITUDE is not null \n" +
		    "                 and addr.LONGITUDE is not null)";	    

    public static final String SELECT_PRODUCT_PACKAGE_SEQUENCE = "select max(PRD_PKG_ID) from %s.T_PRD_PKG";
    public static final String SELECT_DELIV_SEQUENCE = "select max(DELIV_ID) from %s.T_DELIV";

    public static final String SELECT_ALL_PRODUCT_PACKAGE_QUERY =
	    "select PRD_CD, PKG_PRIM_CD, PKG_SECN_CD, BPP_CD, PHYS_ST_CD, PRD_PKG_TYPE_ID, PRD_PKG_ID from %s.T_PRD_PKG order by PRD_CD, PKG_PRIM_CD, PKG_SECN_CD, BPP_CD, PHYS_ST_CD, PRD_PKG_TYPE_ID";

    public static final String SELECT_ALL_CONTAINER_QUERY =
	    "select PKG_SECN_CD, PKG_PRIM_CD from %s.T_PKG order by PKG_SECN_CD, PKG_SECN_CD";

    public static final String INSERT_DUMMY_LOOKUP_QUERY =
	    "begin \n"
		    +
		    "insert into T_LKP (LKP_TYPE_ID, LKP_CD, NAME, ACTV_IND) values (?, (select 'ZZZ' || trim(to_char(nvl(max(to_number(substr(LKP_CD, 4))),0)+1,'0000')) from T_LKP where substr(LKP_CD,1,3) = 'ZZZ'), ?, 0) \n"
		    +
		    "returning LKP_CD into ?; \n" +
		    "end; ";

    public static final String INSERT_LOOKUP_QUERY =
	    "insert into T_LKP (LKP_TYPE_ID, LKP_CD, NAME, ACTV_IND) values (?, ?, ?, ?)";

    public static final String SELECT_LOOKUP_QUERY =
	    "select LKP_CD, NAME from %s.T_LKP where LKP_TYPE_ID = ? ";

    public static final String SELECT_MAP_QUERY =
	    "select upper(replace(replace(replace(m.SRC_VAL, ' ', ' '), '  ', ' '), '  ', ' ')) as SRC_VAL, m.LKP_CD \n" +
		    "  from %s.T_MAP m \n" +
		    " where m.LKP_TYPE_ID = ? \n" +
		    "   and substr(m.LKP_CD,1,3) = 'ZZZ' \n" +
		    "   and length(m.LKP_CD) = 7 \n" +
		    "   and MAP_IND = 1 \n" +
		    " order by m.SRC_VAL";

    public static final String INSERT_MAP_QUERY =
	    "insert into %s.T_MAP (LKP_TYPE_ID, LKP_CD, SRC_VAL, MAP_IND) values (?, ?, ?, ?)";

    public static final String UPDATE_MAP_QUERY =
	    "update %s.T_MAP set SRC_VAL = ?, MAP_IND = ? where LKP_TYPE_ID = ? and LKP_CD = ? ";

    public static final String SELECT_UNIQUE_MAP_QUERY =
	    "select LKP_CD from %s.T_MAP where LKP_TYPE_ID = ? and SRC_VAL = ?";

    public static final String LKP_FIND_NAME_QUERY =
	    "select LKP_CD from T_LKP where LKP_TYPE_ID = ? and NAME = ?";

    public static final String SELECT_BEV_CAT_PRD_LNK_QUERY =
	    "select BEV_CAT_CD, PRD_CD from T_BEV_CAT_PRD order by BEV_CAT_CD";

    /* Nielson queries */
    public static final String NIELSON_SELECT_PRODUCT_PKG_QUERY = "select PRD_PKG_ID from %s.T_PRD_PKG where PRD_CD = ? and PKG_PRIM_CD = ? and PKG_SECN_CD = ? and BPP_CD is null and PHYS_ST_CD is null and PRD_PKG_TYPE_ID = ?";
    public static final String NIELSON_INSERT_PRODUCT_PKG_QUERY = "insert into %s.T_PRD_PKG (PRD_PKG_ID, PRD_CD, PKG_PRIM_CD, PKG_SECN_CD, PRD_PKG_TYPE_ID, BRND_CD, FLVR_CD) VALUES(?,?,?,?,?,?,?)";
    public static final String NIELSON_SELECT_PACKAGE_QUERY = "select PKG_PRIM_CD from %s.T_PKG where PKG_PRIM_CD = ? and PKG_SECN_CD = ?";
    public static final String NIELSON_INSERT_PACKAGE_QUERY = "insert into %s.T_PKG (PKG_PRIM_CD,PKG_SECN_CD,PRIM_SHRT_CD,SECN_SHRT_CD,PRIM_SIZE) VALUES(?,?,?,?,?)";
    public static final String NIELSON_SELECT_OUTLET_QUERY = "select OUTLET_ID, BSNS_TYPE_ID, SB_CHNL_ID from %s.T_OUTLET where TDL_CD = ? and CHN_NM = ? ";
    public static final String NIELSON_SELECT_OUTLET_BY_ID_QUERY = "select OUTLET_ID from %s.T_OUTLET where NM_ID1 = ? and NM_ID2 = ? ";
    public static final String NIELSON_INSERT_OUTLET_QUERY = "insert into %s.T_OUTLET (OUTLET_ID, TDL_CD, NM_ID1, NM_ID2, CHN_NM, NAME, ADDR_ID, SB_CHNL_ID, CREATED_DT, BSNS_TYPE_ID, FOOD_SRVC_IND, LATITUDE, LONGITUDE) VALUES(?,?,?,?,?,?,?,?,sysdate,?,?,?,?)";
    public static final String NIELSON_SELECT_ADDRESS_QUERY = "select ADDR_ID from %s.T_ADDR where (LATITUDE = ? and LONGITUDE = ?) or (ADDR_LINE_1 = ? and CITY = ? and PSTL_CD = ?)";
    public static final String NIELSON_INSERT_ADDRESS_QUERY = "insert into %s.T_ADDR(ADDR_ID, CITY, PSTL_CD, ADDR_LINE_1, CREATED_DT) VALUES(?,?,?,?,sysdate)";
    public static final String NIELSON_INSERT_GEOCODED_ADDRESS_QUERY = "insert into %s.T_ADDR(ADDR_ID, CITY, PSTL_CD, ADDR_LINE_1, FRMT_ADDR, LATITUDE, LONGITUDE, GEO_DT, GEO_LVL, CREATED_DT) VALUES(?,?,?,?,?,?,?,sysdate,?,sysdate)";
    public static final String NIELSON_INSERT_PRODUCT_QUERY = "insert into %s.T_PRD (PRD_CD, BRND_CD, FLVR_CD) values (?,?,?)";
    // public static final String NIELSON_SELECT_PRODUCT_QUERY = "select 1 from T_PRD where PRD_CD = ?";
    public static final String NIELSON_CHECK_IMPROVING_ADDRESS_QUERY = "select a.ADDR_ID from %s.T_ADDR a, %s.T_OUTLET o where o.ADDR_ID = a.ADDR_ID and a.GEO_LVL < 5 and o.OUTLET_ID = ? ";
    public static final String NIELSON_IMPROVE_ADDRESS_QUERY = "update T_ADDR set LATITUDE = ?, LONGITUDE = ?, FRMT_ADDR = ?, GEO_LVL = ?, MODIFIED_DT = sysdate where ADDR_ID = ? ";
    public static final String NIELSON_IMPROVE_OUTLET_ADDRESS_QUERY = "update T_OUTLET set LATITUDE = ?, LONGITUDE = ? where ADDR_ID = ? ";
    public static final String NIELSON_SELECT_PRODUCT_BRAND_FLAVOR_QUERY = "select PRD_CD, BRND_CD, FLVR_CD from %s.T_PRD order by PRD_CD";

    public static final String GET_CONVERTED_GENERIC =
	    "select lk.LKP_CD, \n" +
		    "       lk.NAME, \n" +
		    "       upper(replace(replace(replace(m.SRC_VAL, ' ', ' '), '  ', ' '), '  ', ' ')) as SRC_VAL \n" +
		    "  from %s.T_LKP lk, \n" +
		    "       %s.T_MAP m \n" +
		    " where lk.LKP_TYPE_ID = m.LKP_TYPE_ID \n" +
		    "   and m.LKP_CD 	   = lk.LKP_CD \n" +
		    "   and lk.LKP_TYPE_ID = ? \n" +
		    " order by LKP_CD ";
    public static final String GET_LOOKUP_BY_MAP_TYPE = " select LKP_CD, NAME from %s.T_LKP where LKP_TYPE_ID = ? ";
    public static final String GET_SUB_CHANNEL_MAPPING = " select SB_CHNL_ID, CHNL_ID from %s.T_SB_CHNL";
    public static final String SELECT_GEOCODED_SQL =
	    "select outlet.TDL_CD, \n" +
		    "       outlet.TDL_GRP_CD, \n" +
		    "       outlet.NM_ID1, \n" +
		    "       outlet.NM_ID2, \n" +
		    "       outlet.CHN_NM, \n" +
		    "       outlet.NAME, \n" +
		    "       outlet.SB_CHNL_ID, \n" +
		    "       outlet.PHNE_NBR, \n" +
		    "       outlet.BSNS_TYPE_ID, \n" +
		    "       outlet.FOOD_SRVC_IND, \n" +
		    "       addr.CTRY_CD, \n" +
		    "       addr.STATE, \n" +
		    "       addr.CITY, \n" +
		    "       addr.PSTL_CD, \n" +
		    "       addr.ADDR_LINE_1, \n" +
		    "       addr.ADDR_LINE_2, \n" +
		    "       addr.FRMT_ADDR, \n" +
		    "       addr.LATITUDE, \n" +
		    "       addr.LONGITUDE, \n" +
		    "       to_char(addr.GEO_DT, 'MM/dd/yyyy') as GEO_DT, \n" +
		    "       addr.GEO_LVL \n" +
		    "  from %s.T_OUTLET outlet, \n" +
		    "       %s.T_ADDR addr \n" +
		    " where outlet.ADDR_ID = addr.ADDR_ID \n" +
		    "   and addr.ADDR_ID = ? \n" +
		    " order by outlet.OUTLET_ID";

    public static String genDeliverySelectAllSql(int rowCount) {
	final String condition = " or (OUTLET_ID = ? and PRD_PKG_ID = ?) ";
	StringBuilder sb = new StringBuilder("select DELIV_ID, DELIV_DT, OUTLET_ID, PRD_PKG_ID from %s.T_DELIV where (OUTLET_ID = ? and PRD_PKG_ID = ?)");
	for (int i = 1; i < rowCount; i++) {
	    sb.append(condition);
	}
	return sb.toString();
    }
}
