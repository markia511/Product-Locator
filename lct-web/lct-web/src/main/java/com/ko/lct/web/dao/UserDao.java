package com.ko.lct.web.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ko.lct.web.bean.User;
import com.ko.lct.web.bean.UserRole;

@Service
public class UserDao extends JdbcDaoSupport {

    private static final String PASSWORD_HISTORY_AMOUNT = "13";

    private static final String GET_USER_LIST_SQL =
	    "select u.NAME, u.ROLE_NAME, u.ENABLED, u.CREATE_DT, u.MODIFY_DT from T_USER u order by u.NAME";

    private static final String DELETE_USER_SQL =
	    "delete from T_USER where NAME = ?";

    private static final String ADD_USER_SQL =
	    "insert into T_USER(NAME, PASSWORD, ROLE_NAME, ENABLED, CREATE_DT, MODIFY_DT) values (?, ?, ?, ?, sysdate, sysdate)";

    private static final String TEST_USER_EXISTS_SQL =
	    "select count(*) from T_USER where NAME = ?";

    private static final String GET_USER_SQL =
	    "select u.NAME, u.ROLE_NAME, u.ENABLED, u.CREATE_DT, u.MODIFY_DT from T_USER u where u.NAME = ?";

    private static final String UPDATE_USER_PASSWORD_SQL =
	    "update T_USER set PASSWORD = ?, MODIFY_DT = sysdate where NAME = ?";

    private static final String UPDATE_USER_PASSWORD_ROLE_SQL =
	    "update T_USER set PASSWORD = ?, ROLE_NAME = ?, ENABLED = ?, MODIFY_DT = sysdate where NAME = ?";

    private static final String UPDATE_USER_ROLE_SQL =
	    "update T_USER set ROLE_NAME = ?, ENABLED = ?, MODIFY_DT = sysdate where NAME = ?";

    private static final String DELETE_PSW_HIST_SQL =
	    "delete  \n" +
		    "  from T_PSW_HIST psw_hist \n" +
		    " where psw_hist.ROWID in (select ROW_ID \n" +
		    "                            from (select row_number() over (partition by NAME order by CREATE_DT desc) as ROW_NUM, \n" +
		    "                                          ROWID as ROW_ID \n" +
		    "                                     from T_PSW_HIST \n" +
		    "                                 ) \n" +
		    "                           where ROW_NUM > " + PASSWORD_HISTORY_AMOUNT + ")";

    private static final String TEST_PASSWORD_IN_HISTORY_SQL =
	    "select count(*) as CNT "
		    + "  from T_PSW_HIST "
		    + " where NAME = ? and PASSWORD = ?";

    private static final String ADD_PASSWORD_TO_HISTORY_SQL =
	    "insert into T_PSW_HIST (NAME, PASSWORD, CREATE_DT)"
		    + " values (?, ?, systimestamp)";

    @Autowired
    public void init(DataSource dataSource) {
	super.setDataSource(dataSource);
    }

    public List<User> getUserList() {
	return getJdbcTemplate().query(GET_USER_LIST_SQL, new UserRowMapper());
    }

    public boolean deleteUser(String name) {
	return getJdbcTemplate().update(DELETE_USER_SQL, name) > 0;
    }

    public boolean addUser(String name, String password, UserRole role, boolean enabled) {
	boolean retValue = (getJdbcTemplate().update(ADD_USER_SQL,
		new Object[] { name, password, role.name(), enabled ? Integer.valueOf(1) : Integer.valueOf(0) },
		new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER }) > 0);
	if (retValue) {
	    addPasswordToHistory(name, password);
	}
	return retValue;
    }

    public boolean isUserExists(String userName) {
	Integer usersCount = getJdbcTemplate().queryForObject(TEST_USER_EXISTS_SQL,
		new Object[] { userName }, Integer.class);
	return (usersCount != null && usersCount.intValue() > 0);
    }

    public boolean isPasswordInHistory(String userName, String password) {
	Integer cnt = getJdbcTemplate().queryForObject(TEST_PASSWORD_IN_HISTORY_SQL,
		new Object[] { userName, password },
		new int[] { Types.VARCHAR, Types.VARCHAR }, Integer.class);
	return (cnt != null && cnt.intValue() > 0);
    }

    public boolean addPasswordToHistory(String userName, String password) {
	boolean retValue = (getJdbcTemplate().update(ADD_PASSWORD_TO_HISTORY_SQL, new Object[] { userName, password }, new int[] { Types.VARCHAR, Types.VARCHAR }) > 0);
	deleteOldPasswordsFromHistory();
	return retValue;
    }

    protected boolean deleteOldPasswordsFromHistory() {
	return getJdbcTemplate().update(DELETE_PSW_HIST_SQL) > 0;
    }

    public User getUser(String userName) {
	try {
	    return getJdbcTemplate().queryForObject(GET_USER_SQL,
		    new Object[] { userName },
		    new int[] { Types.VARCHAR },
		    new UserRowMapper());
	} catch (EmptyResultDataAccessException ex) {
	    return null;
	}
    }

    public boolean updateUserPassword(String name, String password) {
	boolean retValue = getJdbcTemplate().update(UPDATE_USER_PASSWORD_SQL,
		new Object[] { password, name },
		new int[] { Types.VARCHAR, Types.VARCHAR }) > 0;
	if (retValue) {
	    addPasswordToHistory(name, password);
	}
	return retValue;
    }

    public boolean updateUser(String name, String password, UserRole role, boolean enabled) {
	if (StringUtils.hasText(password)) {
	    boolean retValue = getJdbcTemplate().update(UPDATE_USER_PASSWORD_ROLE_SQL,
		    new Object[] { password, role.name(), enabled ? Integer.valueOf(1) : Integer.valueOf(0), name },
		    new int[] { Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR }) > 0;
	    if (retValue) {
		addPasswordToHistory(name, password);
	    }
	    return retValue;
	}
	else {
	    return getJdbcTemplate().update(UPDATE_USER_ROLE_SQL,
		    new Object[] { role.name(), enabled ? Integer.valueOf(1) : Integer.valueOf(0), name },
		    new int[] { Types.VARCHAR, Types.INTEGER, Types.VARCHAR }) > 0;
	}
    }

    private class UserRowMapper implements RowMapper<User> {
	public UserRowMapper() {
	    super();
	}

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
	    User retValue = new User();
	    retValue.setUserName(rs.getString("NAME"));
	    retValue.setRole(UserRole.parse(rs.getString("ROLE_NAME")));
	    retValue.setEnabled(rs.getInt("ENABLED") == 1);
	    retValue.setCreateDt(rs.getTimestamp("CREATE_DT"));
	    retValue.setModifyDt(rs.getTimestamp("MODIFY_DT"));
	    return retValue;
	}

    }

}