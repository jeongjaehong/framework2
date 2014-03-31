/* 
 * @(#)RDUtil.java
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
 * RD(Report Designer)�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class RDUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private RDUtil() {
	}

	/**
	 * ����Ʈ �� ������
	 */
	public static final String DEFAULT_COLSEP = "##";

	/**
	 * ����Ʈ �� ������
	 */
	public static final String DEFAULT_LINESEP = "\n";

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet �̿�

	/**
	 * RecordSet�� RD ���� �������� ����Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. RDUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs);
	}

	/**
	 * RecordSet�� RD ���� �������� ����Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.setRecordSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet�� RD ���� �������� ����Ѵ�. RDUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� RecordSet ��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String colSep, String lineSep) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, colSep, lineSep);
	}

	/**
	 * RecordSet�� RD ���� �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.setRecordSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� RecordSet ��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, String colSep, String lineSep) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(lineSep);
			}
			pw.print(rdRowStr(rs, colNms, colSep));
		}
		return rowCount;
	}

	/**
	 * RecordSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex) rs�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(rs)
	 * 
	 * @param rs ��ȯ�� RecordSet ��ü
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs) throws ColumnNotFoundException {
		return format(rs);
	}

	/**
	 * RecordSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) rs�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(rs)
	 * 
	 * @param rs ��ȯ�� RecordSet ��ü
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		return format(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet�� RD ���� �������� ��ȯ�Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex) rs�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(rs, "##", "!!")
	 * 
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs, String colSep, String lineSep) throws ColumnNotFoundException {
		return format(rs, colSep, lineSep);
	}

	/**
	 * RecordSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(rs, "##", "!!")
	 * 
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs, String colSep, String lineSep) throws ColumnNotFoundException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(lineSep);
			}
			buffer.append(rdRowStr(rs, colNms, colSep));
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet �̿�

	/**
	 * ResultSet�� RD ���� �������� ����Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. RDUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ� 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
		return setResultSet(response, rs);
	}

	/**
	 * ResultSet�� RD ���� �������� ����Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.setResultSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ� 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
		return setResultSet(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet�� RD ���� �������� ����Ѵ�. RDUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String colSep, String lineSep) throws SQLException, IOException {
		return setResultSet(response, rs, colSep, lineSep);
	}

	/**
	 * ResultSet�� RD ���� �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� => RDUtil.setResultSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, String colSep, String lineSep) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(lineSep);
				}
				pw.print(rdRowStr(rs, colNms, colSep));
			}
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
	 * ResultSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex) rs�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(rs)
	 * 
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs) throws SQLException {
		return format(rs);
	}

	/**
	 * ResultSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) rs�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(rs)
	 * 
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs) throws SQLException {
		return format(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet�� RD ���� �������� ��ȯ�Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex) rs�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(rs, "##", "!!")
	 * 
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs, String colSep, String lineSep) throws SQLException {
		return format(rs, colSep, lineSep);
	}

	/**
	 * ResultSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(rs, "##", "!!")
	 * 
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * 
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs, String colSep, String lineSep) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(lineSep);
				}
				buffer.append(rdRowStr(rs, colNms, colSep));
			}
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
	 * Map��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex) map�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(map)
	 *
	 * @param map ��ȯ�� Map��ü
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map) {
		return format(map);
	}

	/**
	 * Map��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) map�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(map)
	 *
	 * @param map ��ȯ�� Map��ü
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String format(Map<String, Object> map) {
		return format(map, DEFAULT_COLSEP);
	}

	/**
	 * Map��ü�� RD ���� �������� ��ȯ�Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex) map�� �������� ## �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(map, "##")
	 *
	 * @param map ��ȯ�� Map��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map, String colSep) {
		return format(map, colSep);
	}

	/**
	 * Map��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) map�� �������� ## �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(map, "##")
	 *
	 * @param map ��ȯ�� Map��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String format(Map<String, Object> map, String colSep) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(rdRowStr(map, colSep));
		return buffer.toString();
	}

	/**
	 * List��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex1) mapList�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(mapList)
	 *
	 * @param mapList ��ȯ�� List��ü
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		return format(mapList);
	}

	/**
	 * List��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex1) mapList�� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(mapList)
	 *
	 * @param mapList ��ȯ�� List��ü
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String format(List<Map<String, Object>> mapList) {
		return format(mapList, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * List��ü�� RD ���� �������� ��ȯ�Ѵ�. RDUtil.format�� ����
	 * <br>
	 * ex1) mapList�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.render(mapList, "##", "!!")
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList, String colSep, String lineSep) {
		return format(mapList, colSep, lineSep);
	}

	/**
	 * List��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) mapList�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� => String rd = RDUtil.format(mapList, "##", "!!")
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String format(List<Map<String, Object>> mapList, String colSep, String lineSep) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			for (Map<String, Object> map : mapList) {
				buffer.append(rdRowStr(map, colSep));
				buffer.append(lineSep);
			}
			buffer.delete(buffer.length() - lineSep.length(), buffer.length());
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ��ƿ��Ƽ

	/**
	 * ĳ��������, �����ǵ� ���ڵ��� ��ȯ�Ͽ��ش�.
	 * 
	 * @param str ��ȯ�� ���ڿ�
	 */
	private static String escapeRD(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�
	/**
	 * RD(����Ʈ�����̳�) �� Row ���ڿ� ����
	 */
	private static String rdRowStr(Map<String, Object> map, String colSep) {
		StringBuilder buffer = new StringBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				buffer.append(escapeRD(value.toString()));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	/**
	 * RD(����Ʈ�����̳�) �� Row ���ڿ� ����
	 * @throws ColumnNotFoundException 
	 */
	private static String rdRowStr(RecordSet rs, String[] colNms, String colSep) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		for (int c = 0; c < colNms.length; c++) {
			if (rs.get(colNms[c]) != null) {
				buffer.append(escapeRD(rs.getString(colNms[c])));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	private static String rdRowStr(ResultSet rs, String[] colNms, String colSep) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		for (int c = 0; c < colNms.length; c++) {
			if (rs.getObject(colNms[c]) != null) {
				buffer.append(escapeRD(rs.getString(colNms[c])));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}
}
