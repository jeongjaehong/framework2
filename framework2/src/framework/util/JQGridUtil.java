/* 
 * @(#)JQGridUtil.java
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
 * jqGrid �� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class JQGridUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private JQGridUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet �̿�

	/**
	 * RecordSet�� jqGrid �������� ����Ѵ�. JQGridUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, totalCount, currentPage, rowsPerPage);
	}

	/**
	 * RecordSet�� jqGrid �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.setRecordSet(response, rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print("{");
			pw.print("\"id\":" + rowCount + ",");
			pw.print("\"cell\":" + jqGridRowStr(rs, colNms));
			pw.print("}");
		}
		pw.print("],");
		pw.print("\"total\":" + totalPage + ",");
		pw.print("\"page\":" + currentPage + ",");
		pw.print("\"records\":" + totalCount);
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� jqGrid �������� ����Ѵ�. JQGridUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, totalCount, currentPage, rowsPerPage, colNames);
	}

	/**
	 * RecordSet�� jqGrid �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.setRecordSet(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		PrintWriter pw = response.getWriter();
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print("{");
			pw.print("\"id\":" + rowCount + ",");
			pw.print("\"cell\":" + jqGridRowStr(rs, colNames));
			pw.print("}");
		}
		pw.print("],");
		pw.print("\"total\":" + totalPage + ",");
		pw.print("\"page\":" + currentPage + ",");
		pw.print("\"records\":" + totalCount);
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� jqGrid �������� ��ȯ�Ѵ�. JQGridUtil.format�� ����
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs, int totalCount, int currentPage, int rowsPerPage) throws ColumnNotFoundException {
		return format(rs, totalCount, currentPage, rowsPerPage);
	}

	/**
	 * RecordSet�� jqGrid �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.format(rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs, int totalCount, int currentPage, int rowsPerPage) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append("\"id\":" + rowCount + ",");
			buffer.append("\"cell\":" + jqGridRowStr(rs, colNms));
			buffer.append("}");
		}
		buffer.append("],");
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * RecordSet�� jqGrid �������� ��ȯ�Ѵ�. JQGridUtil.format�� ����
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws ColumnNotFoundException {
		return format(rs, totalCount, currentPage, rowsPerPage, colNames);
	}

	/**
	 * RecordSet�� jqGrid �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.format(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append("\"id\":" + rowCount + ",");
			buffer.append("\"cell\":" + jqGridRowStr(rs, colNames));
			buffer.append("}");
		}
		buffer.append("],");
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet �̿�

	/**
	 * ResultSet�� jqGrid �������� ����Ѵ�. JQGridUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int render(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage) throws SQLException, IOException {
		return setResultSet(response, rs, totalCount, currentPage, rowsPerPage);
	}

	/**
	 * ResultSet�� jqGrid �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.setResultSet(response, rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		PrintWriter pw = response.getWriter();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			pw.print("{");
			int rowCount = 0;
			pw.print("\"rows\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print("{");
				pw.print("\"id\":" + rowCount + ",");
				pw.print("\"cell\":" + jqGridRowStr(rs, colNms));
				pw.print("}");
			}
			pw.print("],");
			pw.print("\"total\":" + totalPage + ",");
			pw.print("\"page\":" + currentPage + ",");
			pw.print("\"records\":" + totalCount);
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
	 * ResultSet�� jqGrid �������� ����Ѵ�. JQGridUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws SQLException
	 * @throws IOException
	 */
	public static int render(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws SQLException, IOException {
		return setResultSet(response, rs, totalCount, currentPage, rowsPerPage, colNames);
	}

	/**
	 * ResultSet�� jqGrid �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� => JQGridUtil.setResultSet(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		PrintWriter pw = response.getWriter();
		try {
			pw.print("{");
			int rowCount = 0;
			pw.print("\"rows\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print("{");
				pw.print("\"id\":" + rowCount + ",");
				pw.print("\"cell\":" + jqGridRowStr(rs, colNames));
				pw.print("}");
			}
			pw.print("],");
			pw.print("\"total\":" + totalPage + ",");
			pw.print("\"page\":" + currentPage + ",");
			pw.print("\"records\":" + totalCount);
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
	 * ResultSet�� jqGrid �������� ��ȯ�Ѵ�. JQGridUtil.format�� ����
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs, int totalCount, int currentPage, int rowsPerPage) throws SQLException {
		return format(rs, totalCount, currentPage, rowsPerPage);
	}

	/**
	 * ResultSet�� jqGrid �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.format(rs, totalCount, currentPage, rowsPerPage)
	 * 
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs, int totalCount, int currentPage, int rowsPerPage) throws SQLException {
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			int rowCount = 0;
			buffer.append("{");
			buffer.append("\"rows\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append("{");
				buffer.append("\"id\":" + rowCount + ",");
				buffer.append("\"cell\":" + jqGridRowStr(rs, colNms));
				buffer.append("}");
			}
			buffer.append("],");
			buffer.append("\"total\":" + totalPage + ",");
			buffer.append("\"page\":" + currentPage + ",");
			buffer.append("\"records\":" + totalCount);
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
	 * ResultSet�� jqGrid �������� ��ȯ�Ѵ�. JQGridUtil.format�� ����
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws SQLException {
		return format(rs, totalCount, currentPage, rowsPerPage, colNames);
	}

	/**
	 * ResultSet�� jqGrid �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.format(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * 
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) throws SQLException {
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		try {
			int rowCount = 0;
			buffer.append("{");
			buffer.append("\"rows\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append("{");
				buffer.append("\"id\":" + rowCount + ",");
				buffer.append("\"cell\":" + jqGridRowStr(rs, colNames));
				buffer.append("}");
			}
			buffer.append("],");
			buffer.append("\"total\":" + totalPage + ",");
			buffer.append("\"page\":" + currentPage + ",");
			buffer.append("\"records\":" + totalCount);
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
	 * List��ü�� jqGrid �������� ��ȯ�Ѵ�. JQGridUtil.format�� ����
	 * <br>
	 * ex1) mapList�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.render(mapList, totalCount, currentPage, rowsPerPage)
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList, int totalCount, int currentPage, int rowsPerPage) {
		return format(mapList, totalCount, currentPage, rowsPerPage);
	}

	/**
	 * List��ü�� jqGrid �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) mapList�� jqGrid �������� ��ȯ�ϴ� ��� => String json = JQGridUtil.format(mapList, totalCount, currentPage, rowsPerPage)
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String format(List<Map<String, Object>> mapList, int totalCount, int currentPage, int rowsPerPage) {
		if (mapList == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		buffer.append("{");
		buffer.append("\"rows\":");
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				rowCount++;
				buffer.append("{");
				buffer.append("\"id\":" + rowCount + ",");
				buffer.append("\"cell\":" + jqGridRowStr(map));
				buffer.append("}");
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("],");
		} else {
			buffer.append("[],");
		}
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
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
	 * jqGrid �� Row ���ڿ� ����
	 */
	private static String jqGridRowStr(Map<String, Object> map) {
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
	 * jqGrid �� Row ���ڿ� ����
	 * @throws ColumnNotFoundException 
	 */
	private static String jqGridRowStr(RecordSet rs, String[] colNms) throws ColumnNotFoundException {
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

	private static String jqGridRowStr(ResultSet rs, String[] colNms) throws SQLException {
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
