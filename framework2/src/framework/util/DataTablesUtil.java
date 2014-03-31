/*
 * @(#)DataTablesUtil.java
 */
package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * DataTables �� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class DataTablesUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private DataTablesUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet �̿�

	/**
	 * RecordSet�� DataTables �������� ����Ѵ�. DataTablesUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs);
	}

	/**
	 * RecordSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.setRecordSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(dataTablesRowStr(rs, colNms));
		}
		pw.print("]");
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� DataTables �������� ����Ѵ�. DataTablesUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.render(response, rs, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String[] colNames) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, colNames);
	}

	/**
	 * RecordSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.setRecordSet(response, rs, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, String[] colNames) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(dataTablesRowStr(rs, colNames));
		}
		pw.print("]");
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� DataTables �������� ��ȯ�Ѵ�. DataTablesUtil.format�� ����
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.render(rs)
	 * 
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs) throws ColumnNotFoundException {
		return format(rs);
	}

	/**
	 * RecordSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.format(rs)
	 * 
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(dataTablesRowStr(rs, colNms));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * RecordSet�� DataTables �������� ��ȯ�Ѵ�. DataTablesUtil.format�� ����
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.render(rs, new String[] { "col1", "col2" })
	 * 
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs, String[] colNames) throws ColumnNotFoundException {
		return format(rs, colNames);
	}

	/**
	 * RecordSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.format(rs, new String[] { "col1", "col2" })
	 * 
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs, String[] colNames) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(dataTablesRowStr(rs, colNames));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet �̿�

	/**
	 * ResultSet�� DataTables �������� ����Ѵ�. DataTablesUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int render(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
		return setResultSet(response, rs);
	}

	/**
	 * ResultSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.setResultSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			pw.print("{");
			int rowCount = 0;
			pw.print("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print(dataTablesRowStr(rs, colNms));
			}
			pw.print("]");
			pw.print("}");
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet�� DataTables �������� ����Ѵ�. DataTablesUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.render(response, rs, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String[] colNames) throws SQLException, IOException {
		return setResultSet(response, rs, colNames);
	}

	/**
	 * ResultSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� => DataTablesUtil.setResultSet(response, rs, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, String[] colNames) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			pw.print("{");
			int rowCount = 0;
			pw.print("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print(dataTablesRowStr(rs, colNames));
			}
			pw.print("]");
			pw.print("}");
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet�� DataTables �������� ��ȯ�Ѵ�. DataTablesUtil.format�� ����
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.render(rs)
	 * 
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs) throws SQLException {
		return format(rs);
	}

	/**
	 * ResultSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.format(rs)
	 * 
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			int rowCount = 0;
			buffer.append("{");
			buffer.append("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append(dataTablesRowStr(rs, colNms));
			}
			buffer.append("]");
			buffer.append("}");
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return buffer.toString();
	}

	/**
	 * ResultSet�� DataTables �������� ��ȯ�Ѵ�. DataTablesUtil.format�� ����
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.render(rs, new String[] { "col1", "col2" })
	 * 
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs, String[] colNames) throws SQLException {
		return format(rs, colNames);
	}

	/**
	 * ResultSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.format(rs, new String[] { "col1", "col2" })
	 * 
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs, String[] colNames) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			int rowCount = 0;
			buffer.append("{");
			buffer.append("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append(dataTablesRowStr(rs, colNames));
			}
			buffer.append("]");
			buffer.append("}");
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ��Ÿ Collection �̿�

	/**
	 * List��ü�� DataTables �������� ��ȯ�Ѵ�. DataTablesUtil.format�� ����
	 * <br>
	 * ex1) mapList�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.render(mapList)
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		return format(mapList);
	}

	/**
	 * List��ü�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) mapList�� DataTables �������� ��ȯ�ϴ� ��� => String json = DataTablesUtil.format(mapList)
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 */
	public static String format(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		buffer.append("\"aaData\":");
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				buffer.append(dataTablesRowStr(map));
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		buffer.append("}");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ��ƿ��Ƽ

	/**
	 * �ڹٽ�ũ��Ʈ�� Ư���ϰ� �νĵǴ� ���ڵ��� JSON� ����ϱ� ���� ��ȯ�Ͽ��ش�.
	 * 
	 * @param str ��ȯ�� ���ڿ�
	 */
	public static String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * DataTables �� Row ���ڿ� ����
	 */
	private static String dataTablesRowStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buffer.append("[");
			for (Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	/**
	 * DataTables �� Row ���ڿ� ����
	 * @throws ColumnNotFoundException 
	 */
	private static String dataTablesRowStr(RecordSet rs, String[] colNms) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c].toUpperCase());
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	private static String dataTablesRowStr(ResultSet rs, String[] colNms) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.getObject(colNms[c].toUpperCase());
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}
}
