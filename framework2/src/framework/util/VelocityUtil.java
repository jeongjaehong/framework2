/*
 * @(#)VelocityUtil.java
 */
package framework.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ResourceBundle;

import jakarta.servlet.http.HttpServlet;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import framework.action.Box;

/**
 * Velocity�� �̿��� ���ø� ó�� ���̺귯��
 */
public class VelocityUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private VelocityUtil() {
	}

	/**
	 * action.properties ���Ͽ� ������ key�� ����� ���ø� ���Ͽ��� statement�� ���ǵ� COMMAND�� ���ڿ��� �Ķ���͸�
	 * ������ ���ڿ��� �����Ѵ�. VelocityUtil.evalutate�� ����
	 * <br>
	 * Sql ������� �� �̸��� �߼��� ���� ���ø� �����Ҷ� ������ �� �ִ�.
	 * @param servlet ������ ��ü
	 * @param key action.properties�� ����� ���ø��� Ű ���ڿ�
	 * @param statement ����ĺ� ���ڿ�
	 * @param param �Ķ���� Box ��ü
	 * @return ���ø��� ����� ���ڿ�
	 * @throws Exception Exception
	 */
	public static String render(HttpServlet servlet, String key, String statement, Box param) throws Exception {
		return evaluate(servlet, key, statement, param);
	}

	/**
	 * action.properties ���Ͽ� ������ key�� ����� ���ø� ���Ͽ��� statement�� ���ǵ� COMMAND�� ���ڿ��� �Ķ���͸�
	 * ������ ���ڿ��� �����Ѵ�.
	 * <br>
	 * Sql ������� �� �̸��� �߼��� ���� ���ø� �����Ҷ� ������ �� �ִ�.
	 * @param servlet ������ ��ü
	 * @param key action.properties�� ����� ���ø��� Ű ���ڿ�
	 * @param statement ����ĺ� ���ڿ�
	 * @param param �Ķ���� Box ��ü
	 * @return ���ø��� ����� ���ڿ�
	 * @throws Exception Exception
	 */
	public static String evaluate(HttpServlet servlet, String key, String statement, Box param) throws Exception {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		VelocityContext context = new VelocityContext();
		context.put("COMMAND", statement);
		context.put("PARAM", param);
		context.put("UTIL", StringUtil.class);
		ResourceBundle bundle = (ResourceBundle) servlet.getServletContext().getAttribute("action-mapping");
		String fileName = ((String) bundle.getObject(key)).trim();
		StringWriter writer = new StringWriter();
		String template = readTemplate(servlet, fileName);
		StringReader reader = new StringReader(template);
		ve.evaluate(context, writer, "framework.util.VelocityUtil", reader);
		return writer.toString();
	}

	/**
	 * ���ø������� �о���δ�.
	 * @throws IOException
	 */
	private static String readTemplate(HttpServlet servlet, String fileName) throws IOException {
		String pathFile = servlet.getServletContext().getRealPath(fileName);
		return read(pathFile);
	}

	/**
	 * ������ path�� ���� ���ϸ����� ���� ���� �о String���� �����Ѵ�
	 * @throws IOException
	 */
	private static String read(String pathFile) throws IOException {
		StringBuilder ta = new StringBuilder();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(pathFile);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				ta.append(line + "\n");
			}
		} catch (IOException e) {
			ta.append("Problems reading file" + e.getMessage());
			throw e;
		} finally {
			if (br != null)
				br.close();
			if (fr != null)
				fr.close();
		}
		return ta.toString();
	}
}
