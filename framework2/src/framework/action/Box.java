/**
 * @(#)Box.java
 */
package framework.action;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import framework.util.StringUtil;

/**
 * ��û��ü, ��Ű��ü�� ���� ��� �ؽ����̺� ��ü�̴�.
 * ��û��ü�� �Ķ���͸� �߻�ȭ �Ͽ� Box �� ������ ���� �Ķ�����̸��� Ű�� �ش� ���� ���ϴ� ����Ÿ Ÿ������ ��ȯ�޴´�.
 */
public class Box extends HashMap<String, String[]> {
	private static final long serialVersionUID = 7143941735208780214L;
	private String _name = null;

	/***
	 * Box ������
	 * @param name Box ��ü�� �̸�
	 */
	public Box(String name) {
		super();
		this._name = name;
	}

	/**
	 * ��û��ü�� �Ķ���� �̸��� ���� ������ �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex) request Box ��ü�� ��� ���: Box box = Box.getBox(request)
	 *
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 *
	 * @return ��ûBox ��ü
	 */
	public static Box getBox(HttpServletRequest request) {
		Box box = new Box("requestbox");
		for (Object obj : request.getParameterMap().keySet()) {
			String key = (String) obj;
			box.put(key, request.getParameterValues(key));
		}
		return box;
	}

	/**
	 * ��û��ü�� ��Ű �̸��� ���� ������ �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex) cookie Box ��ü�� ��� ���: Box box = Box.getBoxFromCookie(request)
	 *
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 *
	 * @return ��ŰBox ��ü
	 */
	public static Box getBoxFromCookie(HttpServletRequest request) {
		Box cookiebox = new Box("cookiebox");
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return cookiebox;
		}
		for (Cookie cookie : cookies) {
			cookiebox.put(cookie.getName(), new String[] { cookie.getValue() == null ? "" : cookie.getValue() });
		}
		return cookiebox;
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� ���ڿ� �迭�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public String[] getArray(String key) {
		return getArray(key, new String[] {});
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� ���ڿ� �迭�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public String[] getArray(String key, String[] defaultValue) {
		String[] value = super.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Boolean ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Boolean getBoolean(String key) {
		return getBoolean(key, Boolean.FALSE);
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Boolean ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String value = getRawString(key).trim();
		if (value.isEmpty()) {
			return defaultValue;
		}
		return Boolean.valueOf(value);
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Double ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Double getDouble(String key) {
		return getDouble(key, Double.valueOf(0));
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Double ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Double getDouble(String key, Double defaultValue) {
		try {
			String value = getRawString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� BigDecimal ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public BigDecimal getBigDecimal(String key) {
		return getBigDecimal(key, BigDecimal.ZERO);
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� BigDecimal ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		try {
			String value = getRawString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Float ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Float getFloat(String key) {
		return getFloat(key, Float.valueOf(0));
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Float ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Float getFloat(String key, Float defaultValue) {
		try {
			String value = getRawString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Float.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Integer ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Integer getInteger(String key) {
		return getInteger(key, Integer.valueOf(0));
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Integer ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		try {
			String value = getRawString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Long ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Long getLong(String key) {
		return getLong(key, Long.valueOf(0));
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Long ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Long getLong(String key, Long defaultValue) {
		try {
			String value = getRawString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� �����Ѵ�.
	 * ũ�ν�����Ʈ ��ũ���� ���� ������ ���� &lt;, &gt; ġȯ�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	@Deprecated
	public String get(String key) {
		return getString(key);
	}

	/**
	 * ���� getlong()���� ���ǵǾ� �ִ� �ҽ����� ȣȯ�� ����.
	 * @param key
	 * @return
	 */
	@Deprecated
	public Long getlong(String key) {
		return getLong(key, Long.valueOf(0));
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� �����Ѵ�.
	 * ũ�ν�����Ʈ ��ũ���� ���� ������ ���� &lt;, &gt; ġȯ�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public String getString(String key) {
		return getString(key, "");
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	@Deprecated
	public String get(String key, String defaultValue) {
		return getString(key, defaultValue);
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public String getString(String key, String defaultValue) {
		return StringUtil.escapeHtmlSpecialChars(getRawString(key, defaultValue));
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� ��ȯ���� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public String getRawString(String key) {
		return getRawString(key, "");
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� ��ȯ���� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public String getRawString(String key, String defaultValue) {
		String[] value = super.get(key);
		if (value == null || value.length == 0 || StringUtil.isEmpty(value[0])) {
			return defaultValue;
		}
		return value[0];
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Date ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�(�⺻����: yyyy-MM-dd)
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Date getDate(String key) {
		return getDate(key, (Date) null);
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Date ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�(�⺻����: yyyy-MM-dd)
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Date getDate(String key, Date defaultValue) {
		String value = getRawString(key).trim().replaceAll("[^\\d]", "");
		if (value.isEmpty()) {
			return defaultValue;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setLenient(false);
		try {
			return sdf.parse(value);
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Date ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param format ��¥ ����(��, yyyy-MM-dd HH:mm:ss)
	 * @return key�� ���εǾ� �ִ� ��
	 */
	public Date getDate(String key, String format) {
		return getDate(key, format, (Date) null);
	}

	/**
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Date ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param format ��¥ ����(��, yyyy-MM-dd HH:mm:ss)
	 * @param defaultValue ���� ���� �� ������ �⺻ ��
	 * @return key�� ���εǾ� �ִ� �� �Ǵ� �⺻ ��
	 */
	public Date getDate(String key, String format, Date defaultValue) {
		String value = getRawString(key).trim();
		if (value.isEmpty()) {
			return defaultValue;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		try {
			return sdf.parse(value);
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	/**
	 * Ű(key)�� ���εǴ� ��Ʈ���� �����Ѵ�.
	 *
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param value Ű�� ���εǴ� ���ڿ�
	 * @return ���� key�� ���εǾ� �ִ� ��Ʈ�� �迭
	 */
	public String[] put(String key, String value) {
		return put(key, new String[] { value });
	}

	/**
	 * Box ��ü�� ������ �ִ� ������ ȭ�� ����� ���� ���ڿ��� ��ȯ�Ѵ�.
	 *
	 * @return ȭ�鿡 ����ϱ� ���� ��ȯ�� ���ڿ�
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (String key : this.keySet()) {
			String value = null;
			Object o = this.get(key);
			if (o == null) {
				value = "";
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						value = "";
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null) {
							value = "";
						} else {
							value = item.toString();
						}
					} else {
						StringBuilder valueBuf = new StringBuilder();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null) {
								valueBuf.append(item.toString());
							}
							if (j < length - 1) {
								valueBuf.append(",");
							}
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else {
					value = o.toString();
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append(key + "=" + value);
		}
		buf.append(" }");
		return "Box[" + _name + "]=" + buf.toString();
	}

	/**
	 * Box ��ü�� ������ �ִ� ������ ���� ��Ʈ������ ��ȯ�Ѵ�.
	 *
	 * @return ���� ��Ʈ������ ��ȯ�� ���ڿ�
	 */
	public String toQueryString() {
		StringBuilder buf = new StringBuilder();
		long currentRow = 0;
		for (String key : this.keySet()) {
			Object o = this.get(key);
			if (currentRow++ > 0) {
				buf.append("&");
			}
			if (o == null) {
				buf.append(key + "=" + "");
			} else {
				if (o.getClass().isArray()) {
					StringBuilder valueBuf = new StringBuilder();
					for (int j = 0, length = Array.getLength(o); j < length; j++) {
						Object item = Array.get(o, j);
						if (item != null) {
							valueBuf.append(key + "=" + item.toString());
						}
						if (j < length - 1) {
							valueBuf.append("&");
						}
					}
					buf.append(valueBuf.toString());
				} else {
					buf.append(key + "=" + o.toString());
				}
			}
		}
		return buf.toString();
	}

	/**
	 * Box ��ü�� ������ �ִ� ������ Xml�� ��ȯ�Ѵ�.
	 *
	 * @return Xml�� ��ȯ�� ���ڿ�
	 */
	public String toXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("<items>");
		buf.append("<item>");
		for (String key : this.keySet()) {
			Object o = this.get(key);
			if (o == null || "".equals(o)) {
				buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null || "".equals(item)) {
							buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
						} else {
							buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + item.toString() + "]]>" + "</" + key.toLowerCase() + ">");
						}
					} else {
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item == null || "".equals(item)) {
								buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
							} else {
								buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + item.toString() + "]]>" + "</" + key.toLowerCase() + ">");
							}
						}
					}
				} else {
					buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + o.toString() + "]]>" + "</" + key.toLowerCase() + ">");
				}
			}
		}
		buf.append("</item>");
		buf.append("</items>");
		return buf.toString();
	}

	/**
	 * Box ��ü�� ������ �ִ� ������ Json ǥ������� ��ȯ�Ѵ�.
	 *
	 * @return Json ǥ������� ��ȯ�� ���ڿ�
	 */
	public String toJson() {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (String key : this.keySet()) {
			String value = null;
			Object o = this.get(key);
			if (o == null) {
				value = "\"\"";
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						value = "\"\"";
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null) {
							value = "\"\"";
						} else {
							value = "\"" + escapeJS(item.toString()) + "\"";
						}
					} else {
						StringBuilder valueBuf = new StringBuilder();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null) {
								valueBuf.append("\"" + escapeJS(item.toString()) + "\"");
							}
							if (j < length - 1) {
								valueBuf.append(",");
							}
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else {
					value = "\"" + escapeJS(o.toString()) + "\"";
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append("\"" + escapeJS(key) + "\"" + ":" + value);
		}
		buf.append(" }");
		return buf.toString();
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�
	/**
	 * �ڹٽ�ũ��Ʈ�� Ư���ϰ� �νĵǴ� ���ڵ��� JSON� ����ϱ� ���� ��ȯ�Ͽ��ش�.
	 *
	 * @param str ��ȯ�� ���ڿ�
	 */
	private String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}
}