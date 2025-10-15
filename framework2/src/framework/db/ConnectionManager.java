/*
 * @(#)ConnectionManager.java
 * 데이타베이스 컨넥션을 관리하는 클래스
 */
package framework.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionManager {
	private static Map<String, DataSource> _dsMap = new HashMap<String, DataSource>();
	private static Log _logger = LogFactory.getLog(framework.db.ConnectionManager.class);
	private List<DBStatement> _stmtList = null;
	private String _dsName = null;
	private Object _caller = null;
	private Connection _connection = null;

	public ConnectionManager(String dsName, Object caller) {
		this._dsName = dsName;
		this._caller = caller;
		if (_stmtList == null) {
			_stmtList = new ArrayList<DBStatement>();
		}
		if (dsName != null) {
			try {
				if (_dsMap.get(dsName) == null) {
					InitialContext ctx = new InitialContext();
					DataSource ds = (DataSource) ctx.lookup(dsName);
					_dsMap.put(dsName, ds);
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	public SQLPreparedStatement createPrepareStatement(String sql) throws SQLException {
		SQLPreparedStatement pstmt = new SQLPreparedStatement(sql, this, _caller);
		_stmtList.add(pstmt);
		return pstmt;
	}

	public SQLBatchPreparedStatement createBatchPrepareStatement(String sql) throws SQLException {
		SQLBatchPreparedStatement pstmt = new SQLBatchPreparedStatement(sql, this, _caller);
		_stmtList.add(pstmt);
		return pstmt;
	}

	public SQLStatement createStatement(String sql) throws SQLException {
		SQLStatement stmt = new SQLStatement(sql, this, _caller);
		_stmtList.add(stmt);
		return stmt;
	}

	public SQLCallableStatement createCallableStatement(String sql) throws SQLException {
		SQLCallableStatement cstmt = new SQLCallableStatement(sql, this, _caller);
		_stmtList.add(cstmt);
		return cstmt;
	}

	public SQLBatchStatement createBatchStatement() throws SQLException {
		SQLBatchStatement bstmt = new SQLBatchStatement(this, _caller);
		_stmtList.add(bstmt);
		return bstmt;
	}

	public void commit() throws SQLException {
		getConnection().commit();
	}

	public void connect() throws Exception {
		setConnection(_dsMap.get(_dsName).getConnection());
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("DB연결 성공!: " + _dsName);
		}
	}

	public void connect(String jdbcDriver, String url, String userID, String userPW) throws Exception {
		try {
			DriverManager.registerDriver((Driver) Class.forName(jdbcDriver).newInstance());
			setConnection(DriverManager.getConnection(url, userID, userPW));
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("DB연결 성공!: " + url);
			}
		} catch (UnsupportedClassVersionError ucve) {
			getLogger().debug("오류: JVM 버전이 호환되지 않습니다. Java 8 이상을 사용해야 합니다.");
			ucve.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			getLogger().debug("오류: 제공된 JDBC 드라이버를 찾을 수 없습니다. 드라이버 클래스를 확인하세요.");
			cnfe.printStackTrace();
		} catch (SQLException se) {
			getLogger().debug("오류: 데이터베이스 연결에 실패했습니다. URL, 사용자명 및 비밀번호를 확인하세요.");
			se.printStackTrace();
		} catch (Exception e) {
			getLogger().debug("예기치 못한 오류가 발생했습니다: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return this._connection;
	}

	public void setConnection(Connection conn) {
		this._connection = conn;
	}

	public void release() {
		if (_stmtList != null) {
			for (DBStatement stmt : _stmtList) {
				try {
					stmt.close();
				} catch (Exception e) {
					getLogger().error("DBStatement Release error!", e);
				}
			}
		}
		if (getConnection() != null) {
			try {
				getConnection().rollback();
			} catch (Exception e) {
				getLogger().error("Connection rollback error!", e);
			}
			try {
				getConnection().close();
			} catch (Exception e) {
				getLogger().error("Connection close error!", e);
			}
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("DB연결 종료!: " + _dsName);
			}
		} else {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@CONNECTION IS NULL");
			}
		}
	}

	public void rollback() {
		try {
			getConnection().rollback();
		} catch (SQLException e) {
		}
	}

	public void setAutoCommit(boolean isAuto) {
		try {
			getConnection().setAutoCommit(isAuto);
		} catch (SQLException e) {
		}
	}

	private Log getLogger() {
		return ConnectionManager._logger;
	}
}