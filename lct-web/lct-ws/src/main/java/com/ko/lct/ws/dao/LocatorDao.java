package com.ko.lct.ws.dao;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ko.lct.common.bean.Address;
import com.ko.lct.common.bean.BeverageCategories;
import com.ko.lct.common.bean.BeverageCategory;
import com.ko.lct.common.bean.Brand;
import com.ko.lct.common.bean.Brands;
import com.ko.lct.common.bean.BusinessType;
import com.ko.lct.common.bean.BusinessTypes;
import com.ko.lct.common.bean.Countries;
import com.ko.lct.common.bean.Country;
import com.ko.lct.common.bean.DistanceUnits;
import com.ko.lct.common.bean.Flavor;
import com.ko.lct.common.bean.Flavors;
import com.ko.lct.common.bean.Location;
import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.Package;
import com.ko.lct.common.bean.Packages;
import com.ko.lct.common.bean.PhysicalState;
import com.ko.lct.common.bean.PhysicalStates;
import com.ko.lct.common.bean.PrimaryContainer;
import com.ko.lct.common.bean.PrimaryContainers;
import com.ko.lct.common.bean.Prod;
import com.ko.lct.common.bean.Product;
import com.ko.lct.common.bean.ProductPackageType;
import com.ko.lct.common.bean.ProductPackageTypes;
import com.ko.lct.common.bean.Products;
import com.ko.lct.common.bean.SearchRequest;
import com.ko.lct.common.bean.SearchRequestV2;
import com.ko.lct.common.bean.SecondaryPackage;
import com.ko.lct.common.bean.SecondaryPackages;
import com.ko.lct.common.bean.SortColumnEnum;
import com.ko.lct.common.bean.State;
import com.ko.lct.common.bean.States;
import com.ko.lct.common.bean.SubTradeChannel;
import com.ko.lct.common.bean.TradeChannel;
import com.ko.lct.common.bean.TradeChannels;
import com.ko.lct.common.util.LocatorConstants;
import com.ko.lct.common.util.LocatorCrypt;
import com.ko.lct.ws.bean.OutletWrapper;
import com.ko.lct.ws.bean.ProductPackageWrapper;
import com.ko.lct.ws.bean.ServiceTimer;
import com.ko.lct.ws.exception.InvalidSignatureException;

@Service
public class LocatorDao extends JdbcDaoSupport {
    // @SuppressWarnings("hiding")
    // private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ALL_SELECTED_VALUE_NAME = "*";

    /*
    private static final double EARTH_RADIUS_KM = 6371.0007900;
    private static final double EARTH_RADIUS_MI = 3958.756356627297;
    */

    /*
     * private static final double DIST_CORR_COEFF1_KM = 71396.0132504180629; private static final double DIST_CORR_COEFF2_KM = 0.000014006361776421;
     * 
     * private static final double DIST_CORR_COEFF1_MI = 44341.1616292968962; private static final double DIST_CORR_COEFF2_MI = 0.000022552349936888;
     */

    private static final String CONDITION_PARAM = "{condition}";
    private static final String KOSHER_PRODUCT_DESC = "Kosher";

    private static final int RQST_TYPE_ID_SEARCH = 1;

    private static final int RQST_PRM_TYPE_ID_LOCALE = 1;
    private static final int RQST_PRM_TYPE_ID_LATITUDE = 2;
    private static final int RQST_PRM_TYPE_ID_LONGITUDE = 3;
    private static final int RQST_PRM_TYPE_ID_DISTANCE = 4;
    private static final int RQST_PRM_TYPE_ID_DISTANCEUNIT = 5;
    private static final int RQST_PRM_TYPE_ID_BEVERAGECATEGORYCODE = 6;
    private static final int RQST_PRM_TYPE_ID_PRODUCTTYPECODE = 7;
    private static final int RQST_PRM_TYPE_ID_BRANDCODE = 8;
    private static final int RQST_PRM_TYPE_ID_FLAVORCODE = 9;
    private static final int RQST_PRM_TYPE_ID_PRIMARYCONTAINERCODE = 10;
    private static final int RQST_PRM_TYPE_ID_SECONDARYPACKAGECODE = 11;
    private static final int RQST_PRM_TYPE_ID_BUSINESSTYPECODE = 12;
    private static final int RQST_PRM_TYPE_ID_PHYSICALSTATE = 13;
    private static final int RQST_PRM_TYPE_ID_TRADECHANNELCODE = 14;
    private static final int RQST_PRM_TYPE_ID_SUBTRADECHANNELCODE = 15;
    private static final int RQST_PRM_TYPE_ID_OUTLETNAME = 16;
    private static final int RQST_PRM_TYPE_ID_INCLUDEFOODSERVICE = 17;
    private static final int RQST_PRM_TYPE_ID_KOSHERPRODUCTONLY = 18;
    private static final int RQST_PRM_TYPE_ID_PAGENUMBER = 19;
    private static final int RQST_PRM_TYPE_ID_RECORDSCOUNT = 20;
    private static final int RQST_PRM_TYPE_ID_PRODUCTCODE = 21;
    private static final String STATE_COUNTRY_DELIMETER = "_";
    private static final String STATES_RESOURCE_BUNDLE_NAME = "states";

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
    
    private static final String OUTLET_PRD_PKG_ID_PARAMS = "{outelt_ids}";
    private static final String SQL_PARAM = "?";

    private static final String SEARCH_PRD_PKG_SQL =
	    "select distinct \n" +
		    "                deliv.OUTLET_ID, \n" +
		    "                prd.PRD_CD, \n" +
		    "                (select NAME from T_LKP lkp where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_PRODUCT
		    + " and lkp.LKP_CD = prd.PRD_CD) as PRD_NM, \n" +
		    "                prd.BRND_CD, \n" +
		    "                (select NAME from T_LKP lkp where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_BRAND
		    + " and lkp.LKP_CD = prd.BRND_CD) as BRND_NM, \n" +
		    "                prd.FLVR_CD, \n" +
		    "                (select NAME from T_LKP lkp where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_FLAVOR
		    + " and lkp.LKP_CD = prd.FLVR_CD) as FLVR_NM, \n" +
		    "                bev_cat.BEV_CAT_CD, \n" +
		    "                bev_cat.NAME as BEV_CAT_NM, \n" +
		    /*"                pkg.PKG_PRIM_CD, \n" +
		    "                pkg.PKG_SECN_CD, \n" +*/
		    "                pkg.CAT_NM, \n" +
		    "                pkg.PRIM_SHRT_CD, \n" +
		    "                (select NAME from T_LKP lkp where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_PRIMARY_CONTAINER
		    + " and lkp.LKP_CD = pkg.PRIM_SHRT_CD) as PRIM_SHRT_NM, \n" +
		    "                pkg.PRIM_SIZE, \n" +
		    "                pkg.SECN_SHRT_CD, \n" +
		    "                (select NAME from T_LKP lkp where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_SECONDARY_PACKAGE
		    + " and lkp.LKP_CD = pkg.SECN_SHRT_CD) as SECN_SHRT_NM, \n" +
		    "                prd_pkg.BPP_CD, \n" +
		    "                prd_pkg.BPP_NM, \n" +
		    "                prd_pkg.PHYS_ST_CD, \n" +
		    "                prd_pkg.PHYS_ST_NM \n" +
		    "           from T_DELIV deliv, \n" +
		    "                T_PRD_PKG prd_pkg, \n" +
		    "                T_PRD prd, \n" +
		    "                T_BEV_CAT bev_cat, \n" +
		    "                T_BEV_CAT_PRD bev_cat_prd, \n" +
		    "                T_PKG pkg, \n" +
		    "                T_PRD_PKG_TYPE prd_pkg_type \n" +
		    "          where deliv.OUTLET_ID              in (" + OUTLET_PRD_PKG_ID_PARAMS + ") \n" +
		    "            and prd_pkg.PRD_PKG_ID           = deliv.PRD_PKG_ID \n" +
		    "            and prd.PRD_CD                   = prd_pkg.PRD_CD \n" +
		    "            and pkg.PKG_PRIM_CD              = prd_pkg.PKG_PRIM_CD \n" +
		    "            and pkg.PKG_SECN_CD              = prd_pkg.PKG_SECN_CD \n" +
		    "            and prd_pkg_type.PRD_PKG_TYPE_ID = prd_pkg.PRD_PKG_TYPE_ID \n" +
		    "            and bev_cat_prd.PRD_CD(+) = prd.PRD_CD \n" +
		    "            and bev_cat.BEV_CAT_CD(+) = bev_cat_prd.BEV_CAT_CD \n" +
		    // "            and deliv.DELIV_DT               > add_months(sysdate, -"
		    // + EXPIRATION_MONTHS + ") \n" +
		    CONDITION_PARAM +
		    "          order by deliv.OUTLET_ID, \n" +
		    "                   PRD_NM, \n" +
		    "                   PRIM_SHRT_NM, \n" +
		    "                   SECN_SHRT_NM, \n" +
		    "                   prd.PRD_CD";
    		/*
		    "                   pkg.PKG_PRIM_CD, \n" +
		    "                   pkg.PKG_SECN_CD";
		    */

    private static final String BEVERAGE_CAT_SQL =
	    "select distinct \n" +
		    "       b.BEV_CAT_CD, \n" +
		    "       lkp.NAME \n" +
		    "  from T_BEV_CAT b, \n" +
		    "	    T_LKP lkp \n" +
		    " where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_BEVERAGE_CATEGORY + "\n" +
		    "	and lkp.LKP_CD      = b.BEV_CAT_CD \n" +
		    " order by NAME \n";

    private static final String BEVERAGE_BRAND_SQL =
	    "select distinct \n" +
		    "       prd.BRND_CD, \n" +
		    "       lkp.NAME as BRND_NM \n" +
		    "  from T_PRD prd, \n" +
		    "	    T_LKP lkp \n" +
		    " where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_BRAND + "\n" +
		    "	and lkp.LKP_CD      = prd.BRND_CD \n" +
		    " order by BRND_NM \n";

    private static final String BEVERAGE_BRAND_BY_BEV_CAT_SQL =
	    "select distinct \n" +
		    "       prd.BRND_CD, \n" +
		    "       lkp.NAME as BRND_NM \n" +
		    "  from T_PRD prd, \n" +
		    "       T_BEV_CAT_PRD bev_cat_prd, \n" +
		    "	    T_LKP lkp \n" +
		    " where prd.PRD_CD = bev_cat_prd.PRD_CD \n" +
		    "   and bev_cat_prd.BEV_CAT_CD = ? \n" +
		    "   and lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_BRAND + "\n" +
		    "	and lkp.LKP_CD 	    = prd.BRND_CD \n" +
		    " order by BRND_NM \n";

    private static final String FLAVORS_SQL =
	    "select distinct " +
		    "       FLVR_CD, \n" +
		    "       lkp.NAME as FLVR_NM \n" +
		    "  from T_PRD prd, \n" +
		    "	    T_LKP lkp \n" +
		    " where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_FLAVOR + "\n" +
		    "	and lkp.LKP_CD 	    = prd.FLVR_CD \n";

    private static final String FLAVORS_BRND_SQL =
	    "select distinct " +
		    "       FLVR_CD, \n" +
		    "       lkp.NAME as FLVR_NM \n" +
		    "  from T_PRD prd, \n" +
		    "	    T_LKP lkp \n" +
		    " where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_FLAVOR + "\n" +
		    "	and lkp.LKP_CD 	    = prd.FLVR_CD" +
		    "   and prd.BRND_CD     = ? \n";

    private static final String FLAVORS_BEV_CAT_SQL =
	    "select distinct " +
		    "       FLVR_CD, \n" +
		    "       lkp.NAME as FLVR_NM \n" +
		    "  from T_PRD prd, \n" +
		    "	    T_LKP lkp, \n" +
		    "	    T_BEV_CAT_PRD bev_cat_prd \n" +
		    " where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_FLAVOR + "\n" +
		    "	and lkp.LKP_CD 	    = prd.FLVR_CD \n" +
		    "   and bev_cat_prd.PRD_CD     = prd.PRD_CD \n" +
		    "   and bev_cat_prd.BEV_CAT_CD = ?";

    private static final String FLAVORS_BRND_BEV_CAT_SQL =
	    "select distinct " +
		    "       FLVR_CD, \n" +
		    "       lkp.NAME as FLVR_NM \n" +
		    "  from T_PRD prd, \n" +
		    "	    T_LKP lkp, \n" +
		    "	    T_BEV_CAT_PRD bev_cat_prd \n" +
		    " where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_FLAVOR + "\n" +
		    "	and lkp.LKP_CD 	    = prd.FLVR_CD \n" +
		    "   and bev_cat_prd.PRD_CD     = prd.PRD_CD \n" +
		    "   and bev_cat_prd.BEV_CAT_CD = ? \n" +
		    "   and prd.BRND_CD 	   = ?";

    private static final String PRODUCTS_SQL =
	    "select distinct " +
		    "	    prd.PRD_CD, \n" +
		    "	    prd_lkp.NAME  as PRD_NM, \n" +
		    "	    prd.BRND_CD, \n" +
		    "       brnd_lkp.NAME as BRND_NM, \n" +
		    "	    prd.FLVR_CD, \n" +
		    "       flvr_lkp.NAME as FLVR_NM, \n" +
		    "	    bev_cat.BEV_CAT_CD, \n" +
		    "       bev_cat.NAME  as BEV_CAT_NM \n" +
		    "  from T_PRD prd, \n" +
		    "       T_BEV_CAT bev_cat, \n" +
		    "       T_BEV_CAT_PRD bev_cat_prd," +
		    "	    T_LKP prd_lkp," +
		    "	    T_LKP brnd_lkp," +
		    "	    T_LKP flvr_lkp \n" +
		    " where bev_cat_prd.PRD_CD(+) = prd.PRD_CD \n" +
		    "   and bev_cat.BEV_CAT_CD(+) = bev_cat_prd.BEV_CAT_CD \n" +
		    "   and prd_lkp.LKP_TYPE_ID  = " + LocatorConstants.LOOKUP_PRODUCT + "\n" +
		    "   and prd_lkp.LKP_CD       = prd.PRD_CD \n" +
		    "   and brnd_lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_BRAND + "\n" +
		    "   and brnd_lkp.LKP_CD      = prd.BRND_CD \n" +
		    "   and flvr_lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_FLAVOR + "\n" +
		    "	and flvr_lkp.LKP_CD 	 = prd.FLVR_CD \n" +
		    "   and exists (select 1 from T_PRD_PKG p where p.PRD_CD = prd.PRD_CD) \n";

    private static final String TRADE_CHANNELS_SQL =
	    "select chnl.CHNL_ID, \n" +
		    "       chnl.NAME as CHNL_NM, \n" +
		    "       chnl.FOOD_SRVC_IND, \n" +
		    "       sb_chnl.SB_CHNL_ID, \n" +
		    "       lkp.NAME as SB_CHNL_NM \n" +
		    "  from T_CHNL chnl, \n" +
		    "       T_SB_CHNL sb_chnl, \n" +
		    "	    T_LKP lkp \n" +
		    " where sb_chnl.CHNL_ID = chnl.CHNL_ID \n" +
		    "   and lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_TRADE_SUB_CHANNEL + "\n" +
		    "	and lkp.LKP_CD 	    = sb_chnl.SB_CHNL_ID \n" +
		    " order by CHNL_NM, \n" +
		    "          SB_CHNL_NM";

    private static final String PACKAGES_SQL =
	    "select distinct \n" +
		    "	    pkg.CAT_NM, \n" +
		    "	    pkg.PRIM_SHRT_CD, \n" +
		    "       prim_lkp.NAME as PRIM_SHRT_NM, \n" +
		    "	    pkg.PRIM_SIZE, \n" +
		    "       pkg.SECN_SHRT_CD, \n" +
		    "       secn_lkp.NAME as SECN_SHRT_NM, \n" +
		    "	    prd_pkg.BRND_CD, \n" +
		    "	    prd_pkg.PRD_CD \n" +
		    "  from T_PKG pkg, \n" +
		    "	    T_PRD_PKG prd_pkg," +
		    "	    T_LKP prim_lkp," +
		    "       T_LKP secn_lkp \n" +
		    " where prd_pkg.PKG_PRIM_CD  = pkg.PKG_PRIM_CD \n" +
		    "   and prd_pkg.PKG_SECN_CD  = pkg.PKG_SECN_CD" +
		    "   and prim_lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_PRIMARY_CONTAINER + 
		    "   and prim_lkp.LKP_CD 	 = pkg.PRIM_SHRT_CD \n" +
		    "   and secn_lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_SECONDARY_PACKAGE + 
		    " 	and secn_lkp.LKP_CD 	 = pkg.SECN_SHRT_CD";

    private static final String PRIMARY_CONTAINERS_SQL =
	    "select distinct \n" +
		    "       pkg.PRIM_SHRT_CD, \n" +
		    "       lkp.NAME as PRIM_SHRT_NM, \n" +
		    "	    pkg.PRIM_SIZE \n" +
		    "  from T_PKG pkg, T_PRD_PKG prd_pkg, T_PRD prd, T_LKP lkp \n" +
		    " where prd_pkg.PKG_PRIM_CD = pkg.PKG_PRIM_CD \n" +
		    "   and prd_pkg.PKG_SECN_CD = pkg.PKG_SECN_CD \n" +
		    "   and prd.PRD_CD 		= prd_pkg.PRD_CD \n" +
		    "   and lkp.LKP_TYPE_ID 	= " + LocatorConstants.LOOKUP_PRIMARY_CONTAINER + "\n" +
		    "	and lkp.LKP_CD 	    	= pkg.PRIM_SHRT_CD \n";
    
    private static final String PRIMARY_CONTAINERS_SQL_ORDER = " order by PRIM_SHRT_NM";

    private static final String SECONDARY_PACKAGES_SQL =
	    "select distinct \n" +
		    "       pkg.SECN_SHRT_CD, \n" +
		    "       lkp.NAME as SECN_SHRT_NM \n" +
		    "  from T_PKG pkg, T_PRD_PKG prd_pkg, T_PRD prd, T_LKP lkp \n" +
		    " where prd_pkg.PKG_PRIM_CD = pkg.PKG_PRIM_CD \n" +
		    "   and prd_pkg.PKG_SECN_CD = pkg.PKG_SECN_CD \n" +
		    "   and prd.PRD_CD 		= prd_pkg.PRD_CD \n" +
		    "   and lkp.LKP_TYPE_ID 	= " + LocatorConstants.LOOKUP_SECONDARY_PACKAGE + "\n" +
		    "	and lkp.LKP_CD      	= pkg.SECN_SHRT_CD ";
    
    private static final String SECONDARY_PACKAGES_SQL_ORDER = " order by SECN_SHRT_NM";

    private static final String PRODUCT_PACKAGE_TYPES_SQL =
	    "select distinct " +
		    "	    PRD_PKG_TYPE_ID, \n" +
		    "	    NAME \n" +
		    "  from T_PRD_PKG_TYPE \n" +
		    " order by NAME";

    private static final String BUSINESS_TYPES_SQL =
	    "select distinct " +
		    "	    BSNS_TYPE_ID, \n" +
		    "	    NAME \n" +
		    "  from T_BSNS_TYPE \n" +
		    " order by NAME";

    private static final String GET_CLIENT_KEY_SQL =
	    "select KEY from T_CLIENT where CLIENT_ID = ?";

    private static final String GET_RQST_SEQUENCE_SQL =
	    "select Q_RQST_ID.nextval from DUAL";

    private static final String INSERT_RQST_SQL =
	    "insert into T_RQST (RQST_ID, RQST_DT, RQST_TYPE_ID, CLIENT_ID, OUTLET_CNT)" +
		    " values (?, systimestamp, ?, ?, null)";

    private static final String INSERT_RQST_DTL_SQL =
	    "insert into T_RQST_DTL (RQST_DTL_ID, RQST_ID, RQST_PRM_TYPE_ID, VALUE)" +
		    " values (Q_RQST_DTL_ID.nextval, ?, ?, ?)";

    private static final String UPDATE_RQST_OUTLET_CNT_SQL =
	    "update T_RQST set OUTLET_CNT = ? where RQST_ID = ?";

    @Autowired
    public LocatorDao(DataSource dataSource) {
	super();
	super.setDataSource(dataSource);
    }

    // @Transactional(readOnly = true)
    public Countries getCountries() throws DataAccessException {
	List<Country> countryList = getJdbcTemplate().query("select CTRY_CD, NAME from T_CTRY order by NAME",
		new ParameterizedBeanPropertyRowMapper<Country>() {

		    @Override
		    public Country mapRow(ResultSet rs, int rowNumber) throws SQLException {

			Country country = new Country();
			country.setCode(rs.getString("CTRY_CD"));
			country.setName(rs.getString("NAME"));
			return country;
		    }

		});
	Countries countries = new Countries();
	countries.getCountryList().addAll(countryList);
	return countries;
    }

    public BeverageCategories getBeverageCategories() throws DataAccessException {
	List<BeverageCategory> beverageCategoryList = getJdbcTemplate().query(BEVERAGE_CAT_SQL,
		new ParameterizedBeanPropertyRowMapper<BeverageCategory>() {

		    @Override
		    public BeverageCategory mapRow(ResultSet rs, int rowNumber) throws SQLException {

			BeverageCategory beverageCategory = new BeverageCategory();
			beverageCategory.setCode(rs.getString("BEV_CAT_CD"));
			beverageCategory.setName(rs.getString("NAME"));
			return beverageCategory;
		    }

		});
	BeverageCategories beverageCategories = new BeverageCategories();
	beverageCategories.getBeverageCategory().addAll(beverageCategoryList);
	return beverageCategories;
    }

    public Brands getBrands(String beverageCategoryCode) throws DataAccessException {
	beverageCategoryCode = getSelectedValue(beverageCategoryCode);
	RowMapper<Brand> rowMapper = new RowMapper<Brand>() {
	    @Override
	    public Brand mapRow(ResultSet rs, int rowNum) throws SQLException {
		Brand brand = new Brand();
		brand.setCode(rs.getString("BRND_CD"));
		brand.setName(rs.getString("BRND_NM"));
		return brand;

	    }

	};

	List<Brand> beverageBrandList;
	if (beverageCategoryCode == null) {
	    beverageBrandList = getJdbcTemplate().query(BEVERAGE_BRAND_SQL, rowMapper);
	}
	else {
	    beverageBrandList = getJdbcTemplate().query(BEVERAGE_BRAND_BY_BEV_CAT_SQL, rowMapper, beverageCategoryCode);
	}
	Brands brands = new Brands();
	brands.getBrand().addAll(beverageBrandList);
	return brands;
    }

    public Flavors getFlavors(String beverageCategoryCode, String beverageBrandCode) throws DataAccessException {
	beverageCategoryCode = getSelectedValue(beverageCategoryCode);
	beverageBrandCode = getSelectedValue(beverageBrandCode);

	String sql;
	Object[] params;
	if (beverageCategoryCode == null && beverageBrandCode == null) {
	    sql = FLAVORS_SQL;
	    params = new Object[0];
	}
	else if (beverageCategoryCode != null && beverageBrandCode == null) {
	    sql = FLAVORS_BEV_CAT_SQL;
	    params = new Object[] { beverageCategoryCode };
	}
	else if (beverageCategoryCode == null && beverageBrandCode != null) {
	    sql = FLAVORS_BRND_SQL;
	    params = new Object[] { beverageBrandCode };
	}
	else {
	    sql = FLAVORS_BRND_BEV_CAT_SQL;
	    params = new Object[] { beverageCategoryCode, beverageBrandCode };
	}

	List<Flavor> beverageFlavorList = getJdbcTemplate().query(sql + " order by FLVR_NM", params, new RowMapper<Flavor>() {

	    @Override
	    public Flavor mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Flavor flavor = new Flavor();
		flavor.setCode(rs.getString("FLVR_CD"));
		flavor.setName(rs.getString("FLVR_NM"));
		return flavor;
	    }

	});
	Flavors flavors = new Flavors();
	flavors.getFlavor().addAll(beverageFlavorList);
	return flavors;
    }

    public Products getProducts() {
	List<Product> productList = getJdbcTemplate().query(PRODUCTS_SQL, new ResultSetExtractor<List<Product>>() {

	    @Override
	    public List<Product> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<Product> retValue = new ArrayList<Product>();
		String oldPrdCd = null;
		Product product = null;
		while (rs.next()) {
		    String prdCd = rs.getString("PRD_CD");
		    if (product == null || !prdCd.equals(oldPrdCd)) {
			product = new Product();
			Prod prod = new Prod();
			prod.setCode(prdCd);
			prod.setName(rs.getString("PRD_NM"));
			product.setProd(prod);

			Brand brand = new Brand();
			brand.setCode(rs.getString("BRND_CD"));
			brand.setName(rs.getString("BRND_NM"));
			product.setBrand(brand);

			Flavor flavor = new Flavor();
			flavor.setCode(rs.getString("FLVR_CD"));
			flavor.setName(rs.getString("FLVR_NM"));
			product.setFlavor(flavor);

			final String bevCatCd = rs.getString("BEV_CAT_CD");
			final String bevCatNm = rs.getString("BEV_CAT_NM");
			if (bevCatCd != null && bevCatNm != null) {
			    BeverageCategory beverageCategory = new BeverageCategory();
			    beverageCategory.setCode(bevCatCd);
			    beverageCategory.setName(bevCatNm);
			    product.getBeverageCategory().add(beverageCategory);
			}

			oldPrdCd = prdCd;

			retValue.add(product);
		    }
		    else {
			BeverageCategory beverageCategory = new BeverageCategory();
			beverageCategory.setCode(rs.getString("BEV_CAT_CD"));
			beverageCategory.setName(rs.getString("BEV_CAT_NM"));
			product.getBeverageCategory().add(beverageCategory);
		    }
		}

		return retValue;
	    }

	});
	Products products = new Products();
	products.getProduct().addAll(productList);
	return products;
    }

    public Packages getPackages() {
	List<Package> packageList = getJdbcTemplate().query(PACKAGES_SQL, new RowMapper<Package>() {

	    @Override
	    public Package mapRow(ResultSet rs, int rowNumber) throws SQLException {

		Package pkg = new Package();
		pkg.setPackageCategory(rs.getString("CAT_NM"));

		PrimaryContainer primaryContainer = new PrimaryContainer();

		primaryContainer.setCode(rs.getString("PRIM_SHRT_CD"));
		primaryContainer.setName(rs.getString("PRIM_SHRT_NM"));


		double baseSize = rs.getDouble("PRIM_SIZE");
		if (!rs.wasNull()) {
		    primaryContainer.setSize(Double.valueOf(baseSize));
		}

		pkg.setPrimaryContainer(primaryContainer);

		SecondaryPackage secondaryPackage = new SecondaryPackage();
		secondaryPackage.setCode(rs.getString("SECN_SHRT_CD"));
		secondaryPackage.setName(rs.getString("SECN_SHRT_NM"));

		pkg.setSecondaryPackage(secondaryPackage);

		pkg.setBrandCode(rs.getString("BRND_CD"));
		pkg.setProductCode(rs.getString("PRD_CD"));

		return pkg;
	    }

	});
	Packages packages = new Packages();
	packages.getPackage().addAll(packageList);
	return packages;
    }

    public PrimaryContainers getPrimaryContainers(String brandCode, String flavorCode) {
	return getPrimaryContainers(brandCode, flavorCode, null);
    }

    public PrimaryContainers getPrimaryContainers(String brandCode, String flavorCode, String productCode) {
	StringBuffer sb = new StringBuffer(PRIMARY_CONTAINERS_SQL);
	ArrayList<Object> parameters = new ArrayList<Object>();
	addSelectCondition(sb, parameters, "prd.BRND_CD", brandCode);
	addSelectCondition(sb, parameters, "prd.FLVR_CD", flavorCode);
	addSelectCondition(sb, parameters, "prd_pkg.PRD_CD", productCode);
	sb.append(PRIMARY_CONTAINERS_SQL_ORDER);
	List<PrimaryContainer> primaryContainerList = getJdbcTemplate().query(sb.toString(), new ParameterizedBeanPropertyRowMapper<PrimaryContainer>() {

	    @Override
	    public PrimaryContainer mapRow(ResultSet rs, int rowNumber) throws SQLException {

		PrimaryContainer primaryContainer = new PrimaryContainer();
		primaryContainer.setCode(rs.getString("PRIM_SHRT_CD"));
		primaryContainer.setName(rs.getString("PRIM_SHRT_NM"));
		double baseSize = rs.getDouble("PRIM_SIZE");
		if (!rs.wasNull()) {
		    primaryContainer.setSize(Double.valueOf(baseSize));
		}
		return primaryContainer;
	    }

	},
		parameters.toArray());
	PrimaryContainers primaryContainers = new PrimaryContainers();
	primaryContainers.getPrimaryContainer().addAll(primaryContainerList);
	return primaryContainers;
    }

    public SecondaryPackages getSecondaryPackages(String brandCode, String flavorCode, String primaryContainerCode) {
	StringBuffer sb = new StringBuffer(SECONDARY_PACKAGES_SQL);
	ArrayList<Object> parameters = new ArrayList<Object>();
	addSelectCondition(sb, parameters, "prd.BRND_CD", brandCode);
	addSelectCondition(sb, parameters, "prd.FLVR_CD", flavorCode);
	addSelectCondition(sb, parameters, "pkg.PRIM_SHRT_CD", primaryContainerCode);
	sb.append(SECONDARY_PACKAGES_SQL_ORDER);
	List<SecondaryPackage> secondaryPackagesList = getJdbcTemplate().query(sb.toString(), new ParameterizedBeanPropertyRowMapper<SecondaryPackage>() {

	    @Override
	    public SecondaryPackage mapRow(ResultSet rs, int rowNumber) throws SQLException {

		SecondaryPackage secondaryPackage = new SecondaryPackage();
		secondaryPackage.setCode(rs.getString("SECN_SHRT_CD"));
		secondaryPackage.setName(rs.getString("SECN_SHRT_NM"));
		return secondaryPackage;
	    }

	},
		parameters.toArray());
	SecondaryPackages secondaryPackages = new SecondaryPackages();
	secondaryPackages.getSecondaryPackage().addAll(secondaryPackagesList);
	return secondaryPackages;
    }

    public ProductPackageTypes getProductPackageTypes() {
	List<ProductPackageType> productPackageTypeList = getJdbcTemplate().query(PRODUCT_PACKAGE_TYPES_SQL,
		new ParameterizedBeanPropertyRowMapper<ProductPackageType>() {

		    @Override
		    public ProductPackageType mapRow(ResultSet rs, int rowNumber) throws SQLException {

			ProductPackageType productPackageType = new ProductPackageType();
			productPackageType.setId(rs.getInt("PRD_PKG_TYPE_ID"));
			productPackageType.setName(rs.getString("NAME"));
			return productPackageType;
		    }

		});
	ProductPackageTypes productPackageTypes = new ProductPackageTypes();
	productPackageTypes.getProductPackageTypes().addAll(productPackageTypeList);
	return productPackageTypes;
    }

    public BusinessTypes getBusinessTypes() {
	List<BusinessType> businessTypeList = getJdbcTemplate().query(BUSINESS_TYPES_SQL,
		new ParameterizedBeanPropertyRowMapper<BusinessType>() {

		    @Override
		    public BusinessType mapRow(ResultSet rs, int rowNumber) throws SQLException {

			BusinessType businessType = new BusinessType();
			businessType.setId(rs.getInt("BSNS_TYPE_ID"));
			businessType.setName(rs.getString("NAME"));
			return businessType;
		    }

		});
	BusinessTypes businessTypes = new BusinessTypes();
	businessTypes.getBusinessType().addAll(businessTypeList);
	return businessTypes;
    }

    public States getStates() {

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	ResourceBundle resourceBundle = ResourceBundle.getBundle(STATES_RESOURCE_BUNDLE_NAME,
		Locale.getDefault(), classLoader);
	Enumeration<String> keys = resourceBundle.getKeys();

	List<State> stateList = new ArrayList<State>();
	Countries countries = getCountries();
	Map<String, Country> countriesMap = new HashMap<String, Country>();
	String key;
	String value;
	State state;
	Country country;
	while (keys.hasMoreElements()) {
	    key = keys.nextElement();
	    value = (String) resourceBundle.getObject(key);
	    String[] splitKey = key.split(STATE_COUNTRY_DELIMETER);
	    if (splitKey.length > 1) {
		state = new State();
		state.setCode(splitKey[1]);
		state.setName(value);
		country = countriesMap.get(splitKey[0]);
		if (country == null) {
		    for (Country countryTemp : countries.getCountryList()) {
			if (countryTemp.getCode().equals(splitKey[0])) {
			    countriesMap.put(countryTemp.getCode(), countryTemp);
			    state.setCountry(countryTemp);
			    break;
			}
		    }
		} else {
		    state.setCountry(country);
		}
		stateList.add(state);
	    }
	}
	Collections.sort(stateList, new Comparator<State>() {
	    @Override
	    public int compare(State o1, State o2) {
		return o1.getCode().compareTo(o2.getCode());
	    }
	});
	States states = new States();
	states.getState().addAll(stateList);
	return states;
    }

    public PhysicalStates getPhysicalStates() {
	List<PhysicalState> physicalStateList = getJdbcTemplate().query(
		"select distinct PHYS_ST_CD, PHYS_ST_NM from T_PRD_PKG where PHYS_ST_CD is not null order by PHYS_ST_NM",
		new ParameterizedBeanPropertyRowMapper<PhysicalState>() {

		    @Override
		    public PhysicalState mapRow(ResultSet rs, int rowNumber) throws SQLException {

			PhysicalState physicalState = new PhysicalState();
			physicalState.setCode(rs.getString("PHYS_ST_CD"));
			physicalState.setName(rs.getString("PHYS_ST_NM"));
			return physicalState;
		    }

		});
	PhysicalStates physicalStates = new PhysicalStates();
	physicalStates.getPhysicalState().addAll(physicalStateList);
	return physicalStates;
    }

    public TradeChannels getTradeChannels() {
	TradeChannels tradeChannels = new TradeChannels();
	getJdbcTemplate().query(TRADE_CHANNELS_SQL, new TradeChannelsExtractor(tradeChannels));
	return tradeChannels;
    }

    private static String getSelectedValue(String value) {
	return (LocatorConstants.SELECTED_VALUE_ALL.equals(value) || ALL_SELECTED_VALUE_NAME.equals(value))
		? null : value;
    }

    private static String getUrlValue(String value) {
	return ALL_SELECTED_VALUE_NAME.equals(value) ? null : value;
    }

    public Locations getLocation(SearchRequest searchRequest) throws InvalidSignatureException {

	if (searchRequest.getClientId() == null ||
		searchRequest.getClientId() == null ||
		searchRequest.getSignature() == null ||
		searchRequest.getSignature().isEmpty()) {
	    throw new InvalidSignatureException();
	}

	final JdbcTemplate jdbcTemplate = getJdbcTemplate();
	// jdbcTemplate.execute("alter session set SQL_TRACE=true"); // FIXME

	final double latitude = searchRequest.getLatitude();
	final double longitude = searchRequest.getLongitude();
	ServiceTimer timer = new ServiceTimer();
	String clientKeyGuid = getClientKey(searchRequest.getClientId());
	String calculatedSignature;
	try {
	    calculatedSignature = LocatorCrypt.getSignature(searchRequest.getClientId(), clientKeyGuid, latitude, longitude, searchRequest.getDistance());
	} catch (InvalidKeyException e) {
	    throw new InvalidSignatureException();
	} catch (NoSuchAlgorithmException e) {
	    throw new InvalidSignatureException();
	} catch (IllegalStateException e) {
	    throw new InvalidSignatureException();
	} catch (UnsupportedEncodingException e) {
	    throw new InvalidSignatureException();
	}

	if (!searchRequest.getSignature().equals(calculatedSignature)) {
	    throw new InvalidSignatureException();
	}
	// logger.info("Getting signature completed in " +
	// timer.getCurrentDurationTime() + " ms");
	int rqstId = logRequest(jdbcTemplate, searchRequest);

	int distance = searchRequest.getDistance();
	if (distance > 1000) {
	    distance = 1000; // Maximum Value of distance is 1000
	}
	
	timer.start();
	ArrayList<Object> outletParameters = new ArrayList<Object>();
	addSelectParam(outletParameters, Double.valueOf(searchRequest.getLatitude()));
	addSelectParam(outletParameters, Double.valueOf(searchRequest.getLongitude()));
	addSelectParam(outletParameters, Integer.valueOf(distance));
	addSelectParam(outletParameters, (searchRequest.getDistanceUnits() == DistanceUnits.mi) ? Integer.valueOf(0) : Integer.valueOf(1));
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
	addSelectParam(outletParameters, searchRequest.isIncludeFoodService() ? Integer.valueOf(1) : Integer.valueOf(0));
	addSelectParam(outletParameters, searchRequest.isKosherProductOnly() ? Integer.valueOf(1) : Integer.valueOf(0));
	addSelectParam(outletParameters, Integer.valueOf(searchRequest.getPageNumber()));
	addSelectParam(outletParameters, Integer.valueOf(searchRequest.getRecordsCount()));
	addSelectParam(outletParameters, searchRequest.getSortColumnEnum().toString());
	addSelectParam(outletParameters, searchRequest.getSortOrder());
	
	
	String sql = SEARCH_OUTLET_SQL;
	// logQuery(sql, outletParameters);
	Locations locations = new Locations();
	
	List<Location> locationList = jdbcTemplate.query(sql,
		outletParameters.toArray(), new LocationsRowMapper(locations));

	logger.info("Getting outlets completed in " +
	timer.getCurrentDurationTime() + " ms");
	
	timer.start();
	
	ArrayList<Object> productParameters = new ArrayList<Object>();
	StringBuffer productPackageCondition = new StringBuffer();

	boolean isBevCat = addSelectCondition(productPackageCondition, productParameters, "bev_cat_prd.BEV_CAT_CD", searchRequest.getBeverageCategoryCode());
	boolean isPrdPkg = addSelectCondition(productPackageCondition, productParameters, "prd_pkg.PRD_PKG_TYPE_ID", searchRequest.getProductTypeCode()) || isBevCat;

	boolean isPkg = addSelectCondition(productPackageCondition, productParameters, "pkg.SECN_SHRT_CD", searchRequest.getSecondaryPackageCode());
	isPkg = addSelectCondition(productPackageCondition, productParameters, "pkg.PRIM_SHRT_CD", searchRequest.getPrimaryContainerCode()) || isPkg;

	if (searchRequest instanceof SearchRequestV2) {
	    isPrdPkg = addSelectCondition(productPackageCondition, productParameters, "prd_pkg.PRD_CD", ((SearchRequestV2) searchRequest).getProductCode()) || isPrdPkg;
	}

	boolean isPrd = addSelectCondition(productPackageCondition, productParameters, "prd.BRND_CD", searchRequest.getBrandCode());
	isPrd = addSelectCondition(productPackageCondition, productParameters, "prd.FLVR_CD", searchRequest.getFlavorCode()) || isPrd;

	if (searchRequest.isKosherProductOnly()) {
	    isPrdPkg = addLikeCondition(productPackageCondition, productParameters, "prd_pkg.BPP_NM", KOSHER_PRODUCT_DESC) || isPrdPkg;
	}
	isPrdPkg = addSelectCondition(productPackageCondition, productParameters, "prd_pkg.PHYS_ST_CD", searchRequest.getPhysicalStateCode()) || isPrdPkg;

	StringBuffer outletProductTables = new StringBuffer();
	StringBuffer outletProductRelt = new StringBuffer();

	if (isPrdPkg || isPrd || isPkg || isBevCat) {
	    // Add Product Package
	    outletProductTables.append("    ,T_PRD_PKG prd_pkg \n");
	    outletProductRelt.append(" and prd_pkg.PRD_PKG_ID           = deliv.PRD_PKG_ID \n");
	}
	if (isPrd) {
	    // Add Product
	    outletProductTables.append(" ,T_PRD prd \n");
	    outletProductRelt.append(" and prd.PRD_CD              = prd_pkg.PRD_CD \n");
	}
	if (isPkg) {
	    // Add Package
	    outletProductTables.append(" ,T_PKG pkg \n");
	    outletProductRelt.append(" and pkg.PKG_PRIM_CD              = prd_pkg.PKG_PRIM_CD \n");
	    outletProductRelt.append(" and pkg.PKG_SECN_CD              = prd_pkg.PKG_SECN_CD \n");
	}
	if (isBevCat) {
	    outletProductTables.append("    ,T_BEV_CAT_PRD bev_cat_prd \n");
	    outletProductRelt.append(" and bev_cat_prd.PRD_CD           = prd_pkg.PRD_CD \n");
	}


	if (locationList != null && !locationList.isEmpty()) {
	    // Oracle returns sorted by distance, name
	    // Need sorting by rounded distance, name
	    if (searchRequest.getSortColumnEnum() == SortColumnEnum.DISTANCE) {
		int order = "DESC".equalsIgnoreCase(searchRequest.getSortOrder()) ? -1 : 1;
		Collections.sort(locationList, new LocationComparator(order));
	    }
	    timer.start();
	    String outletPrdPkgParams = SQL_PARAM;
	    productParameters.add(0, Integer.valueOf(((OutletWrapper) locationList.get(0).getOutlet()).getOutletId()));
	    for (int i = 1; i < locationList.size(); i++) {
		outletPrdPkgParams += ("," + SQL_PARAM);
		productParameters.add(i, Integer.valueOf(((OutletWrapper) locationList.get(i).getOutlet()).getOutletId()));
	    }
	    sql = getConditionQuery(SEARCH_PRD_PKG_SQL, productPackageCondition.toString());
	    sql = getConditionOrderQuery(sql, new String[][] { { OUTLET_PRD_PKG_ID_PARAMS, outletPrdPkgParams } });
	    List<ProductPackageWrapper> productPackages = jdbcTemplate.query(sql, productParameters.toArray(),
		    new ProductPackageExtractor());
	    for (ProductPackageWrapper productPackage : productPackages) {
		for (Location location : locationList) {
		    if (((OutletWrapper) location.getOutlet()).getOutletId() == productPackage.getOutletId()) {
			location.getProductPackage().add(productPackage);
			break;
		    }
		}
	    }
	    logger.info("Getting product packages of outlets completed in " +
	    timer.getCurrentDurationTime() + " ms");
	}

	locations.getLocation().addAll(locationList);
	locations.setDistanceUnit(searchRequest.getDistanceUnits());
	locations.setPageNumber(searchRequest.getPageNumber());
	updateRqstCnt(jdbcTemplate, rqstId, locations.getRecordsCount());
	return locations;
    }

    
    private static void addSelectParam(ArrayList<Object> params, Object selectValue) {
	if (selectValue != null && !selectValue.equals(ALL_SELECTED_VALUE_NAME)) {
	    params.add(selectValue);
	}
	else {
	    params.add(null);
	}
    }
    
    private static boolean addSelectCondition(StringBuffer sql, List<Object> parameters, String columnName, String selectValue) {
	boolean retValue = false;
	if (selectValue != null && !selectValue.equals(ALL_SELECTED_VALUE_NAME)) {
	    sql.append(" and ");
	    String[] results = selectValue.split(",");
	    retValue = true;
	    if (results.length < 2) {
		sql.append(columnName).append(" = ? \n");
		parameters.add(selectValue);
	    }
	    else {
		sql.append(columnName).append(" in (");
		for (int i = 0; i < results.length; i++) {
		    if (i > 0) {
			sql.append(",");
		    }
		    sql.append("?");
		    parameters.add(results[i]);
		}
		sql.append(") \n");
	    }
	}
	return retValue;
    }
    
    private static boolean addLikeCondition(StringBuffer sql, List<Object> parameters, String columnName, String likeValue) {
	boolean retValue = false;
	if (likeValue != null && !likeValue.equals(ALL_SELECTED_VALUE_NAME)) {
	    StringBuffer sb = new StringBuffer();
	    String[] results = likeValue.toLowerCase().split(",");
	    for (int i = 0; i < results.length; i++) {
		String searchValue = results[i];
		if (searchValue != null) {
		    searchValue = searchValue.trim();
		}
		if (searchValue != null && !searchValue.isEmpty()) {
		    retValue = true;
		    if (i > 0) {
			sb.append(" or ");
		    }
		    if (searchValue.indexOf('%') >= 0 || searchValue.indexOf('_') >= 0) {
			sb.append("instr(lower(").append(columnName).append("), ?) > 0");
			parameters.add(searchValue);
		    }
		    else {
			sb.append("lower(").append(columnName).append(") like ?");
			parameters.add(getFormattedLikeValue(searchValue));
		    }

		    sb.append("\n");
		}
	    }
	    if (sb.length() > 0) {
		sql.append(" and (");
		sql.append(sb);
		sql.append(") \n");
	    }

	}
	return retValue;
    }

    private static String getFormattedLikeValue(String likeValue) {
	String[] res = likeValue.split(" ");
	String formattedLikeValue = "%";
	if (res.length > 1) {
	    for (String param : res) {
		if (!param.isEmpty()) {
		    formattedLikeValue += (param + "%");
		}
	    }
	} else {
	    formattedLikeValue += (likeValue + "%");
	}
	return formattedLikeValue;
    }

    static String getConditionQuery(String query, String condition) {
	return query.replace(CONDITION_PARAM, condition);
    }

    static String getConditionOrderQuery(String query, String[][] replaceArr) {
	for (String[] pair : replaceArr) {
	    query = query.replace(pair[0], pair[1]);
	}
	return query;
    }

    class LocationsRowMapper extends ParameterizedBeanPropertyRowMapper<Location> {

	private Locations locations;

	public LocationsRowMapper(Locations locations) {
	    super();
	    this.locations = locations;
	}

	@Override
	public Location mapRow(ResultSet rs, int rowNumber) throws SQLException {

	    this.locations.setRecordsCount(rs.getInt("ROW_COUNT")); // Only the last record contains correct value of the records count

	    Location location = new Location();
	    location.setDistance(rs.getDouble("DIST"));

	    OutletWrapper outlet = new OutletWrapper();
	    int outletId = rs.getInt("OUTLET_ID");
	    outlet.setOutletId(outletId);
	    outlet.setTdlCode(rs.getString("TDL_CD"));
	    outlet.setNameId1(rs.getString("NM_ID1"));
	    outlet.setNameId2(rs.getString("NM_ID2"));
	    outlet.setChainName(rs.getString("CHN_NM"));
	    outlet.setName(rs.getString("OUTLET_NM"));
	    outlet.setPhoneNumber(rs.getString("PHNE_NBR"));
	    location.setOutlet(outlet);

	    int geoLevel = rs.getInt("GEO_LVL");

	    Address address = new Address();
	    address.setCountryCode(rs.getString("CTRY_CD"));
	    address.setState(rs.getString("STATE"));
	    address.setCity(rs.getString("CITY"));
	    address.setPostalCode(rs.getString("PSTL_CD"));
	    address.setAddressLine1(rs.getString("ADDR_LINE_1"));
	    address.setAddressLine2(rs.getString("ADDR_LINE_2"));
	    if (geoLevel < 6) {
		address.setFormattedAddress(
			(address.getAddressLine1() == null ? "" : address.getAddressLine1() + ", ") +
				address.getCity() + ", " + address.getState() + " " +
				address.getPostalCode() + ", " + address.getCountryCode());
	    } else {
		address.setFormattedAddress(rs.getString("FRMT_ADDR"));
	    }
	    address.setLatitude(rs.getDouble("LATITUDE"));
	    address.setLongitude(rs.getDouble("LONGITUDE"));
	    outlet.setAddress(address);

	    TradeChannel tradeChannel = new TradeChannel();
	    tradeChannel.setCode(rs.getString("CHNL_ID"));
	    tradeChannel.setName(rs.getString("CHNL_NM"));
	    tradeChannel.setFoodServiceInd(rs.getInt("FOOD_SRVC_IND") > 0);
	    outlet.setTradeChannel(tradeChannel);

	    SubTradeChannel subTradeChannel = new SubTradeChannel();
	    subTradeChannel.setCode(rs.getString("SB_CHNL_ID"));
	    subTradeChannel.setName(rs.getString("SB_CHNL_NM"));
	    outlet.setSubTradeChannel(subTradeChannel);

	    return location;
	}

    }

    class ProductPackageExtractor implements ResultSetExtractor<List<ProductPackageWrapper>> {

	@Override
	public List<ProductPackageWrapper> extractData(ResultSet rs) throws SQLException, DataAccessException {
	    ArrayList<ProductPackageWrapper> retValue = new ArrayList<ProductPackageWrapper>();
	    int oldOutletId = -1;
	    String oldPrdCd = null;
	    String oldPkgPrimCd = null;
	    String oldPkgSecnCd = null;
	    String oldBppCd = null;
	    String oldPhysStCd = null;
	    ProductPackageWrapper productPackage = null;
	    while (rs.next()) {
		int outletId = rs.getInt("OUTLET_ID");
		String prdCd = rs.getString("PRD_CD");
		String pkgPrimCd = rs.getString("PRIM_SHRT_CD");
		String pkgSecnCd = rs.getString("SECN_SHRT_CD");
		String bppCd = rs.getString("BPP_CD");
		String physStCd = rs.getString("PHYS_ST_CD");
		if (productPackage == null ||
			outletId != oldOutletId ||
			!prdCd.equals(oldPrdCd) ||
			!pkgPrimCd.equals(oldPkgPrimCd) ||
			!pkgSecnCd.equals(oldPkgSecnCd) ||
			(bppCd == null && oldBppCd != null) ||
			(bppCd != null && !bppCd.equals(oldBppCd)) ||
			(physStCd == null && oldPhysStCd != null) ||
			(physStCd != null && !physStCd.equals(oldPhysStCd))) {
		    productPackage = new ProductPackageWrapper();
		    Product product = new Product();
		    Prod prod = new Prod();
		    prod.setCode(prdCd);
		    prod.setName(rs.getString("PRD_NM"));
		    product.setProd(prod);
		    Brand brand = new Brand();
		    final String brandCode = rs.getString("BRND_CD");
		    brand.setCode(brandCode);
		    brand.setName(rs.getString("BRND_NM"));
		    product.setBrand(brand);
		    Flavor flavor = new Flavor();
		    flavor.setCode(rs.getString("FLVR_CD"));
		    flavor.setName(rs.getString("FLVR_NM"));
		    product.setFlavor(flavor);

		    BeverageCategory beverageCategory = new BeverageCategory();
		    beverageCategory.setCode(rs.getString("BEV_CAT_CD"));
		    beverageCategory.setName(rs.getString("BEV_CAT_NM"));
		    product.getBeverageCategory().add(beverageCategory);

		    productPackage.setProduct(product);

		    com.ko.lct.common.bean.Package pkg = new com.ko.lct.common.bean.Package();
		    pkg.setPackageCategory(rs.getString("CAT_NM"));
		    PrimaryContainer primaryContainer = new PrimaryContainer();
		    primaryContainer.setCode(pkgPrimCd);
		    primaryContainer.setName(rs.getString("PRIM_SHRT_NM"));
		    double baseSize = rs.getDouble("PRIM_SIZE");
		    if (!rs.wasNull()) {
			primaryContainer.setSize(Double.valueOf(baseSize));
		    }

		    pkg.setPrimaryContainer(primaryContainer);
		    SecondaryPackage secondaryPackage = new SecondaryPackage();
		    secondaryPackage.setCode(pkgSecnCd);
		    secondaryPackage.setName(rs.getString("SECN_SHRT_NM"));

		    pkg.setSecondaryPackage(secondaryPackage);
		    pkg.setBrandCode(brandCode);
		    pkg.setProductCode(prdCd);
		    productPackage.setPackage(pkg);

		    productPackage.setBppCode(bppCd);
		    productPackage.setBppName(rs.getString("BPP_NM"));

		    productPackage.setPhysicalStateCode(physStCd);
		    productPackage.setPhysicalStateName(rs.getString("PHYS_ST_NM"));

		    productPackage.setOutletId(outletId);

		    retValue.add(productPackage);

		    oldOutletId = outletId;
		    oldPrdCd = prdCd;
		    oldPkgPrimCd = pkgPrimCd;
		    oldPkgSecnCd = pkgSecnCd;
		    oldBppCd = bppCd;
		    oldPhysStCd = physStCd;
		}
		else {
		    BeverageCategory beverageCategory = new BeverageCategory();
		    beverageCategory.setCode(rs.getString("BEV_CAT_CD"));
		    beverageCategory.setName(rs.getString("BEV_CAT_NM"));
		    productPackage.getProduct().getBeverageCategory().add(beverageCategory);
		}
	    }
	    return retValue;
	}
    }

    class TradeChannelsExtractor implements ResultSetExtractor<TradeChannels> {

	private TradeChannels tradeChannels;
	private TradeChannel tradeChannel = null;

	public TradeChannelsExtractor(TradeChannels tradeChannels) {
	    this.tradeChannels = tradeChannels;
	}

	@Override
	public TradeChannels extractData(ResultSet rs) throws SQLException, DataAccessException {
	    while (rs.next()) {
		final String tradeChannelCode = rs.getString("CHNL_ID");
		if (tradeChannel == null || !tradeChannel.getCode().equals(tradeChannelCode)) {
		    tradeChannel = new TradeChannel();
		    tradeChannel.setSubTradeChannel(new ArrayList<SubTradeChannel>());
		    tradeChannel.setCode(tradeChannelCode);
		    tradeChannel.setName(rs.getString("CHNL_NM"));
		    tradeChannel.setFoodServiceInd(rs.getInt("FOOD_SRVC_IND") > 0);
		    tradeChannels.getTradeChannels().add(tradeChannel);
		}
		SubTradeChannel subTradeChannel = new SubTradeChannel();
		subTradeChannel.setCode(rs.getString("SB_CHNL_ID"));
		subTradeChannel.setName(rs.getString("SB_CHNL_NM"));
		tradeChannel.getSubTradeChannel().add(subTradeChannel);
	    }
	    return this.tradeChannels;
	}
    }

    protected String getClientKey(String clientId) {
	return getJdbcTemplate().queryForObject(GET_CLIENT_KEY_SQL, new Object[] { clientId }, String.class);
    }

    @Transactional
    private static int logRequest(JdbcTemplate jdbcTemplate, final SearchRequest searchRequest) {
	int rqstId = jdbcTemplate.queryForObject(GET_RQST_SEQUENCE_SQL, Integer.class).intValue();
	jdbcTemplate.update(INSERT_RQST_SQL,
		Integer.valueOf(rqstId),
		Integer.valueOf(RQST_TYPE_ID_SEARCH),
		searchRequest.getClientId());

	ArrayList<Object[]> params = new ArrayList<Object[]>();

	addParam(params, rqstId, RQST_PRM_TYPE_ID_LOCALE, searchRequest.getLocale());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_LATITUDE, searchRequest.getLatitude());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_LONGITUDE, searchRequest.getLongitude());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_DISTANCE, searchRequest.getDistance());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_DISTANCEUNIT, searchRequest.getDistanceUnits().toString());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_BEVERAGECATEGORYCODE, searchRequest.getBeverageCategoryCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_PRODUCTTYPECODE, searchRequest.getProductTypeCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_BRANDCODE, searchRequest.getBrandCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_FLAVORCODE, searchRequest.getFlavorCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_PRIMARYCONTAINERCODE, searchRequest.getPrimaryContainerCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_SECONDARYPACKAGECODE, searchRequest.getSecondaryPackageCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_BUSINESSTYPECODE, searchRequest.getBusinessTypeCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_PHYSICALSTATE, searchRequest.getPhysicalStateCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_TRADECHANNELCODE, searchRequest.getTradeChannelCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_SUBTRADECHANNELCODE, searchRequest.getSubTradeChannelCode());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_OUTLETNAME, searchRequest.getOutletName());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_INCLUDEFOODSERVICE, searchRequest.isIncludeFoodService() ? "1" : "0");
	addParam(params, rqstId, RQST_PRM_TYPE_ID_KOSHERPRODUCTONLY, searchRequest.isKosherProductOnly() ? "1" : "0");
	addParam(params, rqstId, RQST_PRM_TYPE_ID_PAGENUMBER, searchRequest.getPageNumber());
	addParam(params, rqstId, RQST_PRM_TYPE_ID_RECORDSCOUNT, searchRequest.getRecordsCount());
	if (searchRequest instanceof SearchRequestV2) {
	    addParam(params, rqstId, RQST_PRM_TYPE_ID_PRODUCTCODE, ((SearchRequestV2) searchRequest).getProductCode());
	}

	jdbcTemplate.batchUpdate(INSERT_RQST_DTL_SQL, params);

	return rqstId;
    }

    private static void updateRqstCnt(JdbcTemplate jdbcTemplate, int rqstId, int outletsCount) {
	jdbcTemplate.update(UPDATE_RQST_OUTLET_CNT_SQL, Integer.valueOf(outletsCount), Integer.valueOf(rqstId));
    }

    private static void addParam(final List<Object[]> params, final int rqstId, final int paramCode, final String value) {
	String val = getSelectedValue(value);
	if (val != null) {
	    params.add(new Object[] { Integer.valueOf(rqstId), Integer.valueOf(paramCode), value });
	}
    }

    private static void addParam(final List<Object[]> params, final int rqstId, final int paramCode, final double value) {
	params.add(new Object[] { Integer.valueOf(rqstId), Integer.valueOf(paramCode), Double.valueOf(value) });
    }

    private static void addParam(final List<Object[]> params, final int rqstId, final int paramCode, final int value) {
	params.add(new Object[] { Integer.valueOf(rqstId), Integer.valueOf(paramCode), Integer.valueOf(value) });
    }

    private static class LocationComparator implements Comparator<Location> {
	int order;

	LocationComparator(int order) {
	    this.order = order;
	}

	@Override
	public int compare(Location l1, Location l2) {
	    if (l1.getDistance() == l2.getDistance()) {
		return l1.getOutlet().getChainName().compareTo(l2.getOutlet().getChainName());
	    }
	    return order * (l1.getDistance() < l2.getDistance() ? -1 : 1);
	}
    }

    /*
    private void logQuery(final String sql, final ArrayList<Object> parameters) {
	if (logger.isDebugEnabled()) {
	    StringBuffer sb = new StringBuffer(sql);
	    for (Object param : parameters) {
		final int i = sb.indexOf("?");
		if (i >= 0) {
		    if (param == null) {
			sb.replace(i, i + 1, " null ");
		    } else {
			final String p = (param instanceof
				String) ? '\'' + param.toString() + '\'' : param.toString();
			sb.replace(i, i + 1, p);
		    }
		}
	    }
	    logger.debug(sb.toString());
	}
    }
    */

}
