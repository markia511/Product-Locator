package com.ko.lct.web.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.ko.lct.common.bean.ReportAPIUsage;
import com.ko.lct.common.bean.ReportQuariesPerBrand;
import com.ko.lct.common.util.LocatorConstants;

@Service
public class ReportDao extends JdbcDaoSupport {
    private static final String REPORT_QUERIES_PER_BRAND_SQL =
	    "select prd_rqst.BRND_NM as BRND_NM, \n" +
		    "count(*) as TOTAL_RQST, \n" +
		    "sum(PKG_RQST) as SPEC_PKG_RQST \n" +
		    "from (select brnd.BRND_CD, brnd.BRND_NM, rqst.RQST_ID, \n" +
		    "case when exists(select 1 \n" +
		    "from T_RQST_DTL rqst_dtl_pkg \n" +
		    "where rqst_dtl_pkg.RQST_ID = rqst.RQST_ID \n" +
		    "and rqst_dtl_pkg.RQST_PRM_TYPE_ID in (10, 11) \n " +
		    "and rqst_dtl_pkg.VALUE is not null) then 1 \n" +
		    "else 0 \n" +
		    "end as PKG_RQST \n" +
		    "from (select distinct " +
		    "             prd.BRND_CD, " +
		    "             (select NAME from T_LKP lkp where lkp.LKP_TYPE_ID = " + LocatorConstants.LOOKUP_BRAND + " and lkp.LKP_CD = prd.BRND_CD) as BRND_NM \n" +
		    "from T_PRD prd) brnd, \n" +
		    "T_RQST rqst, \n" +
		    "T_RQST_DTL rqst_dtl \n" +
		    "where rqst_dtl.RQST_ID = rqst.RQST_ID \n" +
		    "and rqst_dtl.VALUE = brnd.BRND_CD \n" +
		    "and rqst_dtl.RQST_PRM_TYPE_ID = 8 \n" +
		    "and rqst.RQST_DT >= ? \n" +
		    "and trunc(rqst.RQST_DT) <= ?) prd_rqst \n" +
		    "group by prd_rqst.BRND_NM \n" +
		    "order by 2 desc, 3 desc, 1";

    private static final String REPORT_API_USAGE_SQL =
	    "select client.CLIENT_ID as CLIENT_KEY,\n" +
		    "client.NAME as CLIENT_NAME, \n" +
		    "count(*) as QUERIES_CNT \n" +
		    "from T_RQST rqst, \n" +
		    "T_CLIENT client \n" +
		    "where client.CLIENT_ID = rqst.CLIENT_ID \n" +
		    "and rqst.RQST_DT >= ? \n" +
		    "and trunc(rqst.RQST_DT) <= ? \n" +
		    "group by client.CLIENT_ID, client.NAME \n" +
		    "order by 3 desc \n";

    @Autowired
    public ReportDao(DataSource dataSource) {
	super.setDataSource(dataSource);
    }

    public List<ReportAPIUsage> getReportAPIUsage(long dateFrom, long dateTo) {

	ArrayList<Object> parameters = new ArrayList<Object>();
	parameters.add(new Date(dateFrom));
	parameters.add(new Date(dateTo));
	List<ReportAPIUsage> reportAPIUsage = new ArrayList<ReportAPIUsage>();

	reportAPIUsage = getJdbcTemplate().query(REPORT_API_USAGE_SQL,
		parameters.toArray(),
		new ReportAPIUsageExtractor(reportAPIUsage));

	return reportAPIUsage;
    }

    public List<ReportQuariesPerBrand> getReportQuariesPerBrand(long dateFrom, long dateTo) {
	ArrayList<Object> parameters = new ArrayList<Object>();
	parameters.add(new Date(dateFrom));
	parameters.add(new Date(dateTo));
	List<ReportQuariesPerBrand> reportQPList = new ArrayList<ReportQuariesPerBrand>();

	reportQPList = getJdbcTemplate().query(REPORT_QUERIES_PER_BRAND_SQL,
		parameters.toArray(),
		new ReportQuariesPerBrandExtractor(reportQPList));

	return reportQPList;
    }

    class ReportAPIUsageExtractor implements ResultSetExtractor<List<ReportAPIUsage>> {

	private List<ReportAPIUsage> reportAUList;

	public ReportAPIUsageExtractor(List<ReportAPIUsage> reportAUList) {
	    this.reportAUList = reportAUList;
	}

	@Override
	public List<ReportAPIUsage> extractData(ResultSet rs) throws SQLException, DataAccessException {
	    ReportAPIUsage reportAU;

	    while (rs.next()) {
		reportAU = new ReportAPIUsage();
		reportAU.setClientKey(rs.getString("CLIENT_KEY"));
		reportAU.setClientName(rs.getString("CLIENT_NAME"));
		reportAU.setCountOfQueries(rs.getInt("QUERIES_CNT"));
		this.reportAUList.add(reportAU);
	    }
	    return this.reportAUList;
	}
    }

    class ReportQuariesPerBrandExtractor implements ResultSetExtractor<List<ReportQuariesPerBrand>> {

	private List<ReportQuariesPerBrand> reportQPList;

	public ReportQuariesPerBrandExtractor(List<ReportQuariesPerBrand> reportQPList) {
	    this.reportQPList = reportQPList;
	}

	@Override
	public List<ReportQuariesPerBrand> extractData(ResultSet rs) throws SQLException, DataAccessException {
	    ReportQuariesPerBrand reportQP;

	    while (rs.next()) {
		reportQP = new ReportQuariesPerBrand();

		reportQP.setBrandName(rs.getString("BRND_NM"));
		reportQP.setSpecificPackage(rs.getInt("SPEC_PKG_RQST"));
		reportQP.setTotal(rs.getInt("TOTAL_RQST"));
		reportQP.setAnyPackage(reportQP.getTotal() - reportQP.getSpecificPackage());
		this.reportQPList.add(reportQP);
	    }
	    return this.reportQPList;
	}
    }
}
