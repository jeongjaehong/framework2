/* 
 * @(#)JsonUtil.java
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

import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * JSON(JavaScript Object Notation)�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class JsonUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private JsonUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet �̿�

	/**
	 * RecordSet�� JSON �������� ����Ѵ�. JsonUtil.setRecordSet�� ����
	 * <br>
	 * ex) response�� rs�� JSON �������� ����ϴ� ��� => JsonUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs JSON �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs);
	}

	/**
	 * RecordSet�� JSON �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� JSON �������� ����ϴ� ��� => JsonUtil.setRecordSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs JSON �������� ��ȯ�� RecordSet ��ü
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
		pw.print("[");
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(jsonRowStr(rs, colNms));
		}
		pw.print("]");
		return rowCount;
	}

	/**
	 * RecordSet�� Json �迭 ���·� ��ȯ�Ѵ�. JsonUtil.format�� ����
	 * <br>
	 * ex) rs�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.render(rs)
	 * 
	 * @param rs JSON �������� ��ȯ�� RecordSet ��ü
	 * 
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String render(RecordSet rs) throws ColumnNotFoundException {
		return format(rs);
	}

	/**
	 * RecordSet�� Json �迭 ���·� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.format(rs)
	 * 
	 * @param rs JSON �������� ��ȯ�� RecordSet ��ü
	 * 
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("[");
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(jsonRowStr(rs, colNms));
		}
		buffer.append("]");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet �̿�

	/**
	 * ResultSet�� JSON �������� ����Ѵ�. JsonUtil.setResultSet�� ����
	 * <br>
	 * ex) response�� rs�� JSON �������� ����ϴ� ��� => JsonUtil.render(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs JSON �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ�
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
		return setResultSet(response, rs);
	}

	/**
	 * ResultSet�� JSON �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� JSON �������� ����ϴ� ��� => JsonUtil.setResultSet(response, rs)
	 * 
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs JSON �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
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
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			pw.print("[");
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print(jsonRowStr(rs, colNms));
			}
			pw.print("]");
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
	 * ResultSet�� Json �迭 ���·� ��ȯ�Ѵ�. JsonUtil.format�� ����
	 * <br>
	 * ex) rs�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.render(rs)
	 * 
	 * @param rs JSON �������� ��ȯ�� ResultSet ��ü
	 * 
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 * @throws SQLException 
	 */
	public static String render(ResultSet rs) throws SQLException {
		return format(rs);
	}

	/**
	 * ResultSet�� Json �迭 ���·� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.format(rs)
	 * 
	 * @param rs JSON �������� ��ȯ�� ResultSet ��ü
	 * 
	 * @return JSON �������� ��ȯ�� ���ڿ�
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
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			buffer.append("[");
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append(jsonRowStr(rs, colNms));
			}
			buffer.append("]");
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
	 * Map��ü�� JSON �������� ��ȯ�Ѵ�. JsonUtil.format�� ����
	 * <br>
	 * ex) map�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.render(map)
	 *
	 * @param map ��ȯ�� Map��ü
	 *
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map) {
		return format(map);
	}

	/**
	 * Map��ü�� JSON �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) map�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.format(map)
	 *
	 * @param map ��ȯ�� Map��ü
	 *
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String format(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(jsonRowStr(map));
		return buffer.toString();
	}

	/**
	 * List��ü�� JSON �������� ��ȯ�Ѵ�. JsonUtil.format�� ����
	 * <br>
	 * ex1) mapList�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.render(mapList)
	 *
	 * @param mapList ��ȯ�� List��ü
	 *
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		return format(mapList);
	}

	/**
	 * List��ü�� JSON �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) mapList�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.format(mapList)
	 *
	 * @param mapList ��ȯ�� List��ü
	 *
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String format(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				buffer.append(jsonRowStr(map));
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
	 * ��ü�� JSON �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) obj�� JSON �������� ��ȯ�ϴ� ��� => String json = JsonUtil.stringify(obj)
	 *
	 * @param obj ��ȯ�� ��ü
	 *
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String stringify(Object obj) {
		JSONWriter writer = new JSONWriter();
		return writer.write(obj);
	}

	/**
	 * JSON ���ڿ��� Object �� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) json�� Object �������� ��ȯ�ϴ� ��� => Object obj = JsonUtil.parse(json)
	 *
	 * @param json ��ȯ�� JSON ���ڿ�
	 *
	 * @return Object �������� ��ȯ�� ��ü
	 */
	public static Object parse(String json) {
		JSONReader reader = new JSONReader();
		return reader.read(json);
	}

	/**
	 * JSON ���ڿ��� ���ڰ� �鿩���⸦ �����Ͽ� �����Ѵ�.
	 * 
	 * @param json json ��ȯ�� JSON ���ڿ�
	 * @return Object �������� ��ȯ�� ��ü
	 */
	public static String pretty(String json) {
		return pretty(json, "    ");
	}

	/**
	 * JSON ���ڿ��� ���ڰ� �鿩���⸦ �����Ͽ� �����Ѵ�.
	 * 
	 * @param json json json ��ȯ�� JSON ���ڿ�
	 * @param indent �鿩���⿡ ����� ���ڿ�
	 * @return Object �������� ��ȯ�� ��ü
	 */
	public static String pretty(String json, String indent) {
		StringBuilder buffer = new StringBuilder();
		int level = 0;
		String target = null;
		for (int i = 0; i < json.length(); i++) {
			target = json.substring(i, i + 1);
			if (target.equals("{") || target.equals("[")) {
				buffer.append(target).append("\n");
				level++;
				for (int j = 0; j < level; j++) {
					buffer.append(indent);
				}
			} else if (target.equals("}") || target.equals("]")) {
				buffer.append("\n");
				level--;
				for (int j = 0; j < level; j++) {
					buffer.append(indent);
				}
				buffer.append(target);
			} else if (target.equals(",")) {
				buffer.append(target);
				buffer.append("\n");
				for (int j = 0; j < level; j++) {
					buffer.append(indent);
				}
			} else {
				buffer.append(target);
			}

		}
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
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * JSON �� Row ���ڿ� ����
	 */
	@SuppressWarnings("unchecked")
	private static String jsonRowStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buffer.append("{");
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = "\"" + escapeJS(entry.getKey().toLowerCase()) + "\"";
				Object value = entry.getValue();
				if (value == null) {
					buffer.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number) {
						buffer.append(key + ":" + value.toString());
					} else if (value instanceof Map) {
						buffer.append(key + ":" + format((Map<String, Object>) value));
					} else if (value instanceof List) {
						buffer.append(key + ":" + format((List<Map<String, Object>>) value));
					} else {
						buffer.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("}");
		} else {
			buffer.append("{}");
		}
		return buffer.toString();
	}

	/**
	 * JSON �� Row ���ڿ� ����
	 * @throws ColumnNotFoundException 
	 */
	private static String jsonRowStr(RecordSet rs, String[] colNms) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("{");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c]);
				String key = "\"" + escapeJS(colNms[c].toLowerCase()) + "\"";

				if (value == null) {
					buffer.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number) {
						buffer.append(key + ":" + value.toString());
					} else {
						buffer.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("}");
		} else {
			buffer.append("{}");
		}
		return buffer.toString();
	}

	private static String jsonRowStr(ResultSet rs, String[] colNms) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("{");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.getObject(colNms[c]);
				String key = "\"" + escapeJS(colNms[c].toLowerCase()) + "\"";

				if (value == null) {
					buffer.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number) {
						buffer.append(key + ":" + value.toString());
					} else {
						buffer.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("}");
		} else {
			buffer.append("{}");
		}
		return buffer.toString();
	}
}