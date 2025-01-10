/*
 * @(#)SQLCallableStatement.java
 * Prepared Statement ï¿½ï¿½ ï¿½Ì¿ï¿½ï¿½Ï±ï¿½ ï¿½ï¿½ï¿½ï¿½ ï¿½ï¿½Ã¼
 */
package framework.db;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import oracle.jdbc.OracleTypes;

public class SQLCallableStatement extends DBStatement {
	private String _sql;
	private ConnectionManager _connMgr = null;
	private CallableStatement _cstmt = null;
	private RecordSet _rs = null;
	private int _upCnt = 0;
	private List<Object> _param = new ArrayList<Object>();
	private Object _caller = null;
	private int _retCode = 0;
	private String _retMessage = "";

	public SQLCallableStatement(String sql, ConnectionManager connMgr, Object caller) {
		this._sql = sql;
		this._connMgr = connMgr;
		this._caller = caller;
	}

	@Override
	public void close() throws SQLException {
		try {
			if (_cstmt != null) {
				_cstmt.close();
				_cstmt = null;
			}
			clearParam();
		} catch (SQLException e) {
			getLogger().error("close Error!");
			throw e;
		}
	}

	public void clearParam() {
		this._param = new ArrayList<Object>();
	}

	public RecordSet executeQuery() throws SQLException {
		return executeQuery(0, 0);
	}

	public RecordSet executeQuery(int currPage, int pageSize) throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return null;
		}
		try {
			CallableStatement cstmt = getCallableStatement();
			if (getParamSize() > 0) {
				for (int i = 1; i <= getParamSize(); i++) {
					Object param = getObject(i - 1);
					if (param == null || "".equals(param)) {
						cstmt.setNull(i, java.sql.Types.VARCHAR);
					} else if (param instanceof java.util.Date) {
						java.util.Date d = (java.util.Date) param;
						cstmt.setObject(i, new java.sql.Timestamp(d.getTime()));
					} else {
						cstmt.setObject(i, param);
					}
				}
			}

			// Get database metadata
			DatabaseMetaData metaData = _connMgr.getConnection().getMetaData();

			// Retrieve database product name
			String dbmsName = metaData.getDatabaseProductName();
			String dbmsVersion = metaData.getDatabaseProductVersion();

			getLogger().debug("Database Product Name: " + dbmsName + "\t Version: " + dbmsVersion);

			if (getLogger().isDebugEnabled()) {

				StringBuilder log = new StringBuilder();
				log.append("@Sql Start (P_STATEMENT) FetchSize : " + cstmt.getFetchSize() + " Caller : " + _caller.getClass().getName() + "\n");
				log.append("@Sql Command: \n" + getQueryString());
				getLogger().debug(log.toString());
			}

			if (StringUtils.contains(dbmsName, "Oracle")) {
				cstmt.registerOutParameter(getParamSize() + 1, OracleTypes.CURSOR); /* select °á°ú. */
				cstmt.registerOutParameter(getParamSize() + 2, OracleTypes.INTEGER);
				cstmt.registerOutParameter(getParamSize() + 3, OracleTypes.VARCHAR);
			} else {
				cstmt.registerOutParameter(getParamSize() + 1, Types.INTEGER);
				cstmt.registerOutParameter(getParamSize() + 2, Types.VARCHAR);
			}


			if (StringUtils.contains(dbmsName, "Oracle")) {
				cstmt.executeQuery();
				_retCode = Integer.parseInt(cstmt.getObject(getParamSize() + 2).toString());
				_retMessage = cstmt.getObject(getParamSize() + 3).toString();

				if (0 <= (Integer) cstmt.getObject(getParamSize() + 2)) {
					_rs = new RecordSet((ResultSet) cstmt.getObject(getParamSize() + 1), currPage, pageSize);
				} else {
					_rs = null;
					throw new SQLException("ErrorCode:" + (Integer) cstmt.getObject(getParamSize() + 2) + "\nErrorMessage:" + cstmt.getObject(getParamSize() + 3));
				}

			} else {
				_rs = new RecordSet(cstmt.executeQuery(), currPage, pageSize);
				_retCode = Integer.parseInt(cstmt.getObject(getParamSize() + 1).toString());
				_retMessage = cstmt.getObject(getParamSize() + 2).toString();

				while (cstmt.getMoreResults()) {
					_rs = new RecordSet(cstmt.getResultSet(), currPage, pageSize);
				}

				if (_rs == null) {
					throw new SQLException("ErrorCode:" + (Integer) cstmt.getObject(getParamSize() + 1) + "\nErrorMessage:" + cstmt.getObject(getParamSize() + 2));
				}
			}

			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@Sql End (P_STATEMENT)");
			}
		} catch (SQLException e) {
			getLogger().error("executeQuery Error!");
			throw new SQLException(e.getMessage() + "\nSQL : " + getQueryString());
		}
		return _rs;
	}

	public RecordSet executeQuery(String sql) throws SQLException {
		setSQL(sql);
		return executeQuery(0, 0);
	}

	public RecordSet executeQuery(String sql, int currPage, int pageSize) throws SQLException {
		setSQL(sql);
		return executeQuery(currPage, pageSize);
	}

	public int executeUpdate() throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return 0;
		}
		try {
			CallableStatement cstmt = getCallableStatement();
			if (getParamSize() > 0) {
				for (int i = 1; i <= getParamSize(); i++) {
					Object param = getObject(i - 1);
					if (param == null || "".equals(param)) {
						cstmt.setNull(i, java.sql.Types.VARCHAR);
					} else if (param instanceof CharSequence) {
						cstmt.setString(i, param.toString());
					} else if (param instanceof byte[]) {
						int size = ((byte[]) param).length;
						if (size > 0) {
							InputStream is = new ByteArrayInputStream((byte[]) param);
							cstmt.setBinaryStream(i, is, size);
						} else {
							cstmt.setBinaryStream(i, null, 0);
						}
					} else if (param instanceof java.util.Date) {
						java.util.Date d = (java.util.Date) param;
						cstmt.setObject(i, new java.sql.Timestamp(d.getTime()));
					} else {
						cstmt.setObject(i, param);
					}
				}
			}

			if (getLogger().isDebugEnabled()) {
				StringBuilder log = new StringBuilder();
				log.append("@Sql Start (P_STATEMENT) FetchSize : " + cstmt.getFetchSize() + " Caller : " + _caller.getClass().getName() + "\n");
				log.append("@Sql Command: \n" + getQueryString() + "\n");
				log.append("@Param Size: " + getParamSize());
				getLogger().debug(log.toString());
			}

			cstmt.registerOutParameter(getParamSize() + 1, OracleTypes.INTEGER);
			cstmt.registerOutParameter(getParamSize() + 2, OracleTypes.VARCHAR);

			_upCnt = cstmt.executeUpdate();
			_retCode = Integer.parseInt(cstmt.getObject(getParamSize() + 1).toString());
			_retMessage = cstmt.getObject(getParamSize() + 2).toString();

			if (null == cstmt.getObject(getParamSize() + 1)) {
				_upCnt = -1;
				_retCode = -1;
				_retMessage = "Out parameter return code°¡ NULL °ªÀÔ´Ï´Ù.";
				throw new SQLException("ErrorCode:-1 \nErrorMessage:Out parameter return code°¡ NULL °ªÀÔ´Ï´Ù.");
			} else if (0 > (Integer) cstmt.getObject(getParamSize() + 1)) {
				throw new SQLException("ErrorCode:" + (Integer) cstmt.getObject(getParamSize() + 1) + "\nErrorMessage:" + cstmt.getObject(getParamSize() + 2));
			}

			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@Sql End (P_STATEMENT)");
			}

		} catch (SQLException e) {
			_upCnt = -1;
			getLogger().error("executeUpdate Error!");
			throw new SQLException(e.getMessage() + "\nSQL : " + getQueryString());
		}
		return _upCnt;
	}

	public int executeUpdate(String sql) throws Throwable {
		setSQL(sql);
		return executeUpdate();
	}

	public Object getObject(int idx) {
		return this._param.get(idx);
	}

	public Object[] getParams() {
		if (_param == null)
			return null;
		return _param.toArray();
	}

	public int getParamSize() {
		return _param.size();
	}

	protected CallableStatement getCallableStatement() throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return null;
		}
		try {
			if (_cstmt == null) {
				_cstmt = _connMgr.getConnection().prepareCall(getSQL(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				_cstmt.setFetchSize(100);
			}
		} catch (SQLException e) {
			getLogger().error("getPrepareStatment Error!");
			throw e;
		}
		return _cstmt;
	}

	public RecordSet getRecordSet() {
		return this._rs;
	}

	public String getSQL() {
		return this._sql;
	}

	public int getRetCode() {
		return this._retCode;
	}

	public String getRetMessage() {
		return this._retMessage;
	}

	public String getString(int idx) {
		return (String) getObject(idx);
	}

	public int getUpdateCount() {
		return this._upCnt;
	}

	public void set(Object[] obj) {
		clearParam();
		for (int i = 0; i < obj.length; i++) {
			set(i, obj[i]);
		}
	}

	public void set(int idx, double value) {
		set(idx, Double.valueOf(value));
	}

	public void set(int idx, int value) {
		set(idx, Integer.valueOf(value));
	}

	public void set(int idx, long value) {
		set(idx, Long.valueOf(value));
	}

	public void set(int idx, Object obj) {
		_param.add(idx, obj);
	}

	public void set(int idx, byte[] value) {
		set(idx, (Object) value);
	}

	public void setSQL(String newSql) throws SQLException {
		close();
		_sql = newSql;
	}

	@Override
	public String toString() {
		return "SQL : " + getSQL();
	}

	public String getQueryString() {
		Object value = null;
		int qMarkCount = 0;
		StringTokenizer token = new StringTokenizer(getSQL(), "?");
		StringBuilder buf = new StringBuilder();
		while (token.hasMoreTokens()) {
			String oneChunk = token.nextToken();
			buf.append(oneChunk);
			try {
				if (_param.size() > qMarkCount) {
					value = _param.get(qMarkCount++);
					if (value == null || "".equals(value)) {
						value = "NULL";
					} else if (value instanceof CharSequence) {
						value = "'" + value + "'";
					} else if (value instanceof java.util.Date) {
						java.util.Date d = (java.util.Date) value;
						value = "'" + new java.sql.Timestamp(d.getTime()) + "'";
					}
				} else {
					if (token.hasMoreTokens()) {
						value = null;
					} else {
						value = "";
					}
				}
				buf.append("" + value);
			} catch (Throwable e) {
				buf.append("ERROR WHEN PRODUCING QUERY STRING FOR LOG." + e.toString());
			}
		}
		return buf.toString().trim();
	}
}