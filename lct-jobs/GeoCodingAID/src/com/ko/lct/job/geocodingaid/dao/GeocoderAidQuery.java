package com.ko.lct.job.geocodingaid.dao;

public class GeocoderAidQuery {

    public static final String GET_NEW_ADDR_PRV_ID =
	    "select nvl(max(ADDR_PRV_ID),0)+1 as NEW_ADDR_PRV_ID from %s.T_ADDR_PRV";

    public static final String FIND_ADDR_PRV =
	    "select ADDR_PRV_ID," +
		    "       BTLR_DLVR_PNT_NM," +
		    "       ADDR_LINE_2," +
		    "       PSTL_CD," +
		    "       CTRY_CD," +
		    "       AREA_CD," +
		    "       PHN_NO" +
		    "  from %s.T_ADDR_PRV" +
		    " where OWNERSHIP_ID = ?" +
		    "   and BTLR_DLVR_PNT_NO = ?" +
		    "   and STATE = ?" +
		    "   and CITY = ?" +
		    "   and ADDR_LINE_1 = ?";

    public static final String INSERT_ADDR =
	    "insert into %s.T_ADDR_PRV( \n" +
		    "  ADDR_PRV_ID, \n" +
		    "  OWNERSHIP_ID, \n" +
		    "  BTLR_DLVR_PNT_NO, \n" +
		    "  BTLR_DLVR_PNT_NM, \n" +
		    "  ADDR_LINE_1, \n" +
		    "  ADDR_LINE_2, \n" +
		    "  CITY, \n" +
		    "  STATE, \n" +
		    "  PSTL_CD, \n" +
		    "  CTRY_CD, \n" +
		    "  AREA_CD, \n" +
		    "  PHN_NO, \n" +
		    "  FRMT_ADDR, \n" +
		    "  LATITUDE, \n" +
		    "  LONGITUDE, \n" +
		    "  GEO_DT, \n" +
		    "  GEO_LVL, \n" +
		    "  CREATED_DT, \n" +
		    "  UNLOAD_IND \n" +
		    ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null, null, null, 0, sysdate, 0)";

    public static final String INSERT_ADDR_PRV_ROW =
	    "insert into %s.T_ADDR_PRV( \n" +
		    "  ADDR_PRV_ID, \n" +
		    "  OWNERSHIP_ID, \n" +
		    "  BTLR_DLVR_PNT_NO, \n" +
		    "  BTLR_DLVR_PNT_NM, \n" +
		    "  ADDR_LINE_1, \n" +
		    "  ADDR_LINE_2, \n" +
		    "  CITY, \n" +
		    "  STATE, \n" +
		    "  PSTL_CD, \n" +
		    "  CTRY_CD, \n" +
		    "  AREA_CD, \n" +
		    "  PHN_NO, \n" +
		    "  FRMT_ADDR, \n" +
		    "  LATITUDE, \n" +
		    "  LONGITUDE, \n" +
		    "  GEO_DT, \n" +
		    "  GEO_LVL, \n" +
		    "  CREATED_DT, \n" +
		    "  UNLOAD_IND \n" +
		    ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, 1)";
    
    public static final String UPDATE_ADDR =
	    "update %s.T_ADDR_PRV \n" +
		    "   set BTLR_DLVR_PNT_NM = ?, \n" +
		    "       ADDR_LINE_2 = ?, \n" +
		    "       PSTL_CD = ?, \n" +
		    "       CTRY_CD = ?, \n" +
		    "       AREA_CD = ?, \n" +
		    "       PHN_NO = ?, \n" +
		    "       CREATED_DT = sysdate, \n" +
		    "       UNLOAD_IND = 0 \n" +
		    " where ADDR_PRV_ID = ?";
    
    public static final String UPDATE_GEOCODING_ADDRESS_BY_MAIN_ADDRESS = 
	    "	update %s.T_ADDR_PRV a \n" +
	    "	   set (FRMT_ADDR, LATITUDE, LONGITUDE, GEO_DT, GEO_LVL, UNLOAD_IND)  =  \n" +
	    "	       (select b.FRMT_ADDR, b.LATITUDE, b.LONGITUDE, sysdate, GEO_LVL, 0 \n" +
	    "	          from %s.T_ADDR b \n" +
	    "		 where b.GEO_DT is not null \n" +
	    "	           and a.CITY  = b.CITY \n" +
	    "	           and a.STATE = b.STATE \n" +
	    "	           and a.ADDR_LINE_1 = b.ADDR_LINE_1 \n" +
	    "              and rownum = 1) \n" +
	    "	 where a.GEO_DT is null \n" +
	    "	   and exists (select 1 \n" +
	    "			 from %s.T_ADDR b \n" +
	    "			where b.GEO_DT is not null \n" +
	    "	              	  and a.CITY  = b.CITY \n" +
	    "	              	  and a.STATE = b.STATE \n" +
	    "	              	  and a.ADDR_LINE_1 = b.ADDR_LINE_1) \n" +
	    "	   and rownum <= ?  \n";
    
    public static final String UPDATE_GEOCODING_ADDRESS_BY_PRIVACY_ADDRESS = 
	    "	update %s.T_ADDR_PRV a \n" +
	    "	   set (FRMT_ADDR, LATITUDE, LONGITUDE, GEO_DT, GEO_LVL, UNLOAD_IND)  =  \n" +
	    "	       (select b.FRMT_ADDR, b.LATITUDE, b.LONGITUDE, sysdate, GEO_LVL, 0 \n" +
	    "	          from %s.T_ADDR_PRV b \n" +
	    "		 where b.GEO_DT is not null \n" +
	    "	           and a.CITY  = b.CITY \n" +
	    "	           and a.STATE = b.STATE \n" +
	    "	           and a.ADDR_LINE_1 = b.ADDR_LINE_1 \n" +
	    "              and rownum = 1) \n" +
	    "	 where a.GEO_DT is null \n" +
	    "	   and exists (select 1 \n" +
	    "			 from %s.T_ADDR_PRV b \n" +
	    "			where b.GEO_DT is not null \n" +
	    "	              	  and a.CITY  = b.CITY \n" +
	    "	              	  and a.STATE = b.STATE \n" +
	    "	              	  and a.ADDR_LINE_1 = b.ADDR_LINE_1 \n" +
	    "              	  and rownum = 1) \n" +
	    "	   and rownum <= ?  \n";

    public static final String GET_NON_GEOCODED_PRIVACY_ADDRESS_LIST =
	    " select ADDR_PRV_ID,	\n" +
            "        CTRY_CD,		\n" +
            "        STATE,		\n" +
            "        CITY,		\n" +
            "        PSTL_CD,		\n" +
	    "        ADDR_LINE_1,	\n" +
            "        ADDR_LINE_2,	\n" +
	    "        BTLR_DLVR_PNT_NM 	\n" +            
	    "   from (select ADDR_PRV_ID, \n" +      
            "       	     CTRY_CD,	  \n" +
            "        	     STATE,	  \n" +
            "        	     CITY,	  \n" +
            "        	     PSTL_CD,	  \n" +
	    "        	     trim(replace(replace(replace(ADDR_LINE_1,'!', ' '), '#', ' '), '*', ' ')) as ADDR_LINE_1, \n" +
            "        	     ADDR_LINE_2,	\n" +
	    "        	     BTLR_DLVR_PNT_NM 	\n" +           
	    "  		from %s.T_ADDR_PRV a  \n" +
	    " 	       where a.GEO_DT is null \n" +
	    " 	       order by ADDR_LINE_1, CITY, STATE) \n" + 
	    "  where ADDR_LINE_1 is not null \n" +
	    "    and rownum < ? \n";
    
    public static final String SELECT_GEOCODED_SQL =
	    " select a.OWNERSHIP_ID,	\n" +
	    "        a.BTLR_DLVR_PNT_NO, \n" +
	    "        a.BTLR_DLVR_PNT_NM, \n" +
            "        a.CTRY_CD,		\n" +
            "        a.STATE,		\n" +
            "        a.CITY,		\n" +
            "        a.PSTL_CD,		\n" +
	    "        a.ADDR_LINE_1,	\n" +
            "        a.ADDR_LINE_2,	\n" +
	    "        a.AREA_CD, 	\n" +
	    "        a.PHN_NO, 		\n" +
	    "        a.FRMT_ADDR, 	\n" +
	    "        a.LATITUDE, 	\n" +
	    "        a.LONGITUDE, 	\n" +
	    "        to_char(a.GEO_DT, 'MM/dd/yyyy') as GEO_DT, \n" +
	    "        a.GEO_LVL 		\n" +
	    "   from %s.T_ADDR_PRV a 	\n" +
	    "  where a.ADDR_PRV_ID = ? 	\n";

    public static final String UPDATE_GEOCODED_PRIVACY_ADDRESS =
	    "	 update %s.T_ADDR_PRV \n" + 
	    "	    set FRMT_ADDR   = ?, \n" +
	    "	 	LATITUDE    = ?, \n" +
	    "		LONGITUDE   = ?, \n" +
	    " 		GEO_LVL     = ?, \n" +
	    "	 	GEO_DT 	    = sysdate, \n" +
	    "       	UNLOAD_IND  = 0 \n" +
	    "	  where ADDR_PRV_ID = ? \n";
    
    public static final String GET_UNLOAD_GEOCODED_ADDRESS = 
	    " select OWNERSHIP_ID,	\n" +
	    "        BTLR_DLVR_PNT_NO, 	\n" +
	    "        BTLR_DLVR_PNT_NM, 	\n" +
	    "        ADDR_LINE_1,	\n" +
            "        ADDR_LINE_2,	\n" +
            "        CITY,		\n" +
            "        STATE,		\n" +
            "        PSTL_CD,		\n" +
            "        CTRY_CD,		\n" +
	    "        AREA_CD, 		\n" +
	    "        PHN_NO, 		\n" +
	    "        FRMT_ADDR, 	\n" +
	    "        LATITUDE, 		\n" +
	    "        LONGITUDE, 	\n" +
	    "        to_char(GEO_DT, 'yyyy-MM-dd') as GEO_DT, \n" +
	    "        GEO_LVL 		\n" +
	    "   from %s.T_ADDR_PRV 	\n" +
	    "  where UNLOAD_IND = 0   	\n"
//	    +
//	    "  order by ADDR_PRV_ID     \n"
	    ;

    public static final String IS_EXIST_UNLOAD_GEOCODED_ADDRESS = 
	    "select 1 \n" +
            "  from %s.T_ADDR_PRV 	\n" +
            " where GEO_DT is not null 	\n" +
            "   and UNLOAD_IND = 0   	\n" +
	    "   and rownum = 1   	\n";
    
    public static final String UPDATE_UNLOAD_GEOCODED_ADDRESS = 
	    " update %s.T_ADDR_PRV  \n" + 
	    "    set UNLOAD_IND = 1 \n" +  
	    "  where UNLOAD_IND = 0 \n" +
	    "	 and rownum <= ? \n";
    
}
