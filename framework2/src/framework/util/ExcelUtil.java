/*
 * @(#)ExcelUtil.java
 */
package framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload2.core.FileItem;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Excel ����� ���� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class ExcelUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private ExcelUtil() {
	}

	//////////////////////////////////////////////////////////////////////////////////////////�Ľ�
	/**
	 * Ȯ���ڿ� ���ؼ� ���������� �Ľ��Ѵ�.
	 * @param fileItem ���Ͼ�����
	 * @return �������� ����Ʈ
	 * @throws Exception Exception
	 */
	public static List<Map<String, String>> parse(FileItem fileItem) throws Exception {
		return parse(fileItem, 0);
	}
	
	/**
	 *  Ȯ���ڿ� ���ؼ� ���������� �Ľ��Ѵ�.
	 * @param fileItem ���� ������.
	 * @param startRow �Ľ��� ������ ����� �ִ� ��. �÷������� ������ ��ġ. 0���� ����.
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, String>> parse(FileItem fileItem, int startRow) throws Exception {
		String ext = FileUtil.getFileExtension(fileItem.getName());
		InputStream is = fileItem.getInputStream();
		if ("csv".equalsIgnoreCase(ext)) {
			return parseCSV(is);
		} else if ("tsv".equalsIgnoreCase(ext)) {
			return parseTSV(is);
		} else if ("xls".equalsIgnoreCase(ext)) {
			return parseXLS(is, startRow);
		} else if ("xlsx".equalsIgnoreCase(ext)) {
			return parseXLSX(is);
		} else {
			throw new Exception("�������� �ʴ� ���������Դϴ�.");
		}
	}

	/**
	 * ��ȣȭ�� ���������� �Ľ��Ѵ�.
	 * @param fileItem ���Ͼ�����
	 * @param password ��й�ȣ
	 * @return �������� ����Ʈ
	 * @throws Exception Exception
	 */
	public static List<Map<String, String>> parse(FileItem fileItem, String password) throws Exception {
		String ext = FileUtil.getFileExtension(fileItem.getName());
		InputStream is = fileItem.getInputStream();
		if ("xls".equalsIgnoreCase(ext)) {
			return parseXLS(is, password);
		} else if ("xlsx".equalsIgnoreCase(ext)) {
			return parseXLSX(is, password);
		} else {
			throw new Exception("�������� �ʴ� ���������Դϴ�.");
		}
	}

	/**
	 * Ȯ���ڿ� ���ؼ� ���������� �Ľ��Ѵ�.
	 * @param file ����
	 * @return �������� ����Ʈ
	 * @throws Exception Exception
	 */
	public static List<Map<String, String>> parse(File file) throws Exception {
		FileInputStream fis = null;
		try {
			String ext = FileUtil.getFileExtension(file);
			fis = new FileInputStream(file);
			if ("csv".equalsIgnoreCase(ext)) {
				return parseCSV(fis);
			} else if ("tsv".equalsIgnoreCase(ext)) {
				return parseTSV(fis);
			} else if ("xls".equalsIgnoreCase(ext)) {
				return parseXLS(fis);
			} else if ("xlsx".equalsIgnoreCase(ext)) {
				return parseXLSX(fis);
			} else {
				throw new Exception("�������� �ʴ� ���������Դϴ�.");
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	/**
	 * ��ȣȭ�� ���������� �Ľ��Ѵ�.
	 * @param file ����
	 * @param password �н�����
	 * @return �������� ����Ʈ
	 * @throws Exception Exception
	 */
	public static List<Map<String, String>> parse(File file, String password) throws Exception {
		FileInputStream fis = null;
		try {
			String ext = FileUtil.getFileExtension(file);
			fis = new FileInputStream(file);
			if ("xls".equalsIgnoreCase(ext)) {
				return parseXLS(fis, password);
			} else if ("xlsx".equalsIgnoreCase(ext)) {
				return parseXLSX(fis, password);
			} else {
				throw new Exception("�������� �ʴ� ���������Դϴ�.");
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet �̿�

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setRecordSetXLS�� ����
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLS(response, rs, fileName, null);
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setRecordSetXLS�� ����
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, RecordSet rs, String fileName, String[] header) throws ColumnNotFoundException, IOException {
		return setRecordSetXLS(response, rs, fileName, header);
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetXLS(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLS(response, rs, fileName, null);
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetXLS(HttpServletResponse response, RecordSet rs, String fileName, String[] header) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		response.setContentType("application/octet-stream;");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		OutputStream os = response.getOutputStream();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		if (header != null) {
			Row row = sheet.createRow(rowCount);
			CellStyle cellStyle = headerStyle(workbook);
			appendHeader(row, header, cellStyle);
			rowCount++;
		}
		CellStyle cellStyle = rowStyle(workbook);
		while (rs.nextRow()) {
			Row row = sheet.createRow(rowCount);
			appendRow(row, rs, colNms, cellStyle);
			rowCount++;
		}
		for (int i = 0; i < colNms.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
		}
		workbook.write(os);
		return rowCount;
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int writeXLS(File file, RecordSet rs) throws ColumnNotFoundException, IOException {
		return writeXLS(file, rs, null);
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int writeXLS(File file, RecordSet rs, String[] header) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		FileOutputStream fos = new FileOutputStream(file);
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		if (header != null) {
			Row row = sheet.createRow(rowCount);
			CellStyle cellStyle = headerStyle(workbook);
			appendHeader(row, header, cellStyle);
			rowCount++;
		}
		CellStyle cellStyle = rowStyle(workbook);
		while (rs.nextRow()) {
			Row row = sheet.createRow(rowCount);
			appendRow(row, rs, colNms, cellStyle);
			rowCount++;
		}
		for (int i = 0; i < colNms.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
		}
		workbook.write(fos);
		fos.close();
		return rowCount;
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setRecordSetXLSX�� ����
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLSX(response, rs, fileName, null);
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setRecordSetXLSX�� ����
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, RecordSet rs, String fileName, String[] header) throws ColumnNotFoundException, IOException {
		return setRecordSetXLSX(response, rs, fileName, header);
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetXLSX(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLSX(response, rs, fileName, null);
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetXLSX(HttpServletResponse response, RecordSet rs, String fileName, String[] header) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		response.setContentType("application/octet-stream;");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		OutputStream os = response.getOutputStream();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		if (header != null) {
			Row row = sheet.createRow(rowCount);
			CellStyle cellStyle = headerStyle(workbook);
			appendHeader(row, header, cellStyle);
			rowCount++;
		}
		CellStyle cellStyle = rowStyle(workbook);
		while (rs.nextRow()) {
			Row row = sheet.createRow(rowCount);
			appendRow(row, rs, colNms, cellStyle);
			rowCount++;
		}
		for (int i = 0; i < colNms.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
		}
		workbook.write(os);
		return rowCount;
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int writeXLSX(File file, RecordSet rs) throws ColumnNotFoundException, IOException {
		return writeXLSX(file, rs, null);
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int writeXLSX(File file, RecordSet rs, String[] header) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		FileOutputStream fos = new FileOutputStream(file);
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		if (header != null) {
			Row row = sheet.createRow(rowCount);
			CellStyle cellStyle = headerStyle(workbook);
			appendHeader(row, header, cellStyle);
			rowCount++;
		}
		CellStyle cellStyle = rowStyle(workbook);
		while (rs.nextRow()) {
			Row row = sheet.createRow(rowCount);
			appendRow(row, rs, colNms, cellStyle);
			rowCount++;
		}
		for (int i = 0; i < colNms.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
		}
		workbook.write(fos);
		fos.close();
		return rowCount;
	}

	/**
	 * RecordSet�� CSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setRecordSetCSV�� ����
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderCSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetCSV(response, rs, fileName);
	}

	/**
	 * RecordSet�� CSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetCSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetSep(response, rs, fileName, ",");
	}

	/**
	 * RecordSet�� CSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @return ó���Ǽ�
	 * @throws IOException IOException
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static int writeCSV(File file, RecordSet rs) throws IOException, ColumnNotFoundException {
		return writeSep(file, rs, ",");
	}

	/**
	 * RecordSet�� TSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setRecordSetTSV�� ����
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderTSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetTSV(response, rs, fileName);
	}

	/**
	 * RecordSet�� TSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs RecordSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetTSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetSep(response, rs, fileName, "\t");
	}

	/**
	 * RecordSet�� TSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @return ó���Ǽ�
	 * @throws IOException IOException
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static int writeTSV(File file, RecordSet rs) throws IOException, ColumnNotFoundException {
		return writeSep(file, rs, "\t");
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ����Ѵ�. ExcelUtil.setRecordSetSep�� ����
	 * <br>
	 * ex) response�� rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ����ϴ� ���: ExcelUtil.renderSep(response, rs, ",")
	 *
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ������(CSV, TSV ��)���� �������� ��ȯ�� RecordSet ��ü
	 * @param fileName ���ϸ�
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderSep(HttpServletResponse response, RecordSet rs, String fileName, String sep) throws ColumnNotFoundException, IOException {
		return setRecordSetSep(response, rs, fileName, sep);
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ����ϴ� ���: ExcelUtil.setRecordSetSep(response, rs, ",")
	 *
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ������(CSV, TSV ��)���� �������� ��ȯ�� RecordSet ��ü
	 * @param fileName ���ϸ�
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetSep(HttpServletResponse response, RecordSet rs, String fileName, String sep) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		response.setContentType("application/octet-stream;");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print("\n");
			}
			pw.print(sepRowStr(rs, colNms, sep));
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs RecordSet
	 * @param sep ������
	 * @return ó���Ǽ�
	 * @throws IOException IOException
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static int writeSep(File file, RecordSet rs, String sep) throws IOException, ColumnNotFoundException {
		if (rs == null) {
			return 0;
		}
		FileWriter fw = new FileWriter(file);
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				fw.write("\n");
			}
			fw.write(sepRowStr(rs, colNms, sep));
		}
		fw.close();
		return rowCount;
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. ExcelUtil.formatSep�� ����
	 * <br>
	 * ex) rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.renderSep(rs, ",")
	 *
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String renderSep(RecordSet rs, String sep) throws ColumnNotFoundException {
		return formatSep(rs, sep);
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.formatSep(rs, ",")
	 *
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String formatSep(RecordSet rs, String sep) throws ColumnNotFoundException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append("\n");
			}
			buffer.append(sepRowStr(rs, colNms, sep));
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet �̿�

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setResultSetXLS�� ����
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLS(response, rs, fileName, null);
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setResultSetXLS�� ����
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, ResultSet rs, String fileName, String[] header) throws SQLException, IOException {
		return setResultSetXLS(response, rs, fileName, header);
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetXLS(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLS(response, rs, fileName, null);
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetXLS(HttpServletResponse response, ResultSet rs, String fileName, String[] header) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		response.setContentType("application/octet-stream;");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		OutputStream os = response.getOutputStream();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			int rowCount = 0;
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				CellStyle cellStyle = headerStyle(workbook);
				appendHeader(row, header, cellStyle);
				rowCount++;
			}
			CellStyle cellStyle = rowStyle(workbook);
			while (rs.next()) {
				Row row = sheet.createRow(rowCount);
				appendRow(row, rs, colNms, cellStyle);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
			}
			workbook.write(os);
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
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int writeXLS(File file, ResultSet rs) throws SQLException, IOException {
		return writeXLS(file, rs, null);
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int writeXLS(File file, ResultSet rs, String[] header) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		FileOutputStream fos = new FileOutputStream(file);
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			int rowCount = 0;
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				CellStyle cellStyle = headerStyle(workbook);
				appendHeader(row, header, cellStyle);
				rowCount++;
			}
			CellStyle cellStyle = rowStyle(workbook);
			while (rs.next()) {
				Row row = sheet.createRow(rowCount);
				appendRow(row, rs, colNms, cellStyle);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
			}
			workbook.write(fos);
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (fos != null)
				fos.close();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setResultSetXLSX�� ����
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLSX(response, rs, fileName, null);
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setResultSetXLSX�� ����
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, ResultSet rs, String fileName, String[] header) throws SQLException, IOException {
		return setResultSetXLSX(response, rs, fileName, header);
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetXLSX(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLSX(response, rs, fileName, null);
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetXLSX(HttpServletResponse response, ResultSet rs, String fileName, String[] header) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		response.setContentType("application/octet-stream;");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		OutputStream os = response.getOutputStream();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			int rowCount = 0;
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				CellStyle cellStyle = headerStyle(workbook);
				appendHeader(row, header, cellStyle);
				rowCount++;
			}
			CellStyle cellStyle = rowStyle(workbook);
			while (rs.next()) {
				Row row = sheet.createRow(rowCount);
				appendRow(row, rs, colNms, cellStyle);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
			}
			workbook.write(os);
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
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int writeXLSX(File file, ResultSet rs) throws SQLException, IOException {
		return writeXLSX(file, rs, null);
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @param header ���
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int writeXLSX(File file, ResultSet rs, String[] header) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		FileOutputStream fos = new FileOutputStream(file);
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			int rowCount = 0;
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				CellStyle cellStyle = headerStyle(workbook);
				appendHeader(row, header, cellStyle);
				rowCount++;
			}
			CellStyle cellStyle = rowStyle(workbook);
			while (rs.next()) {
				Row row = sheet.createRow(rowCount);
				appendRow(row, rs, colNms, cellStyle);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.2));
			}
			workbook.write(fos);
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (fos != null)
				fos.close();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet�� CSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setResultSetCSV�� ����
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderCSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetCSV(response, rs, fileName);
	}

	/**
	 * ResultSet�� CSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetCSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetSep(response, rs, fileName, ",");
	}

	/**
	 * ResultSet�� CSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @return ó���Ǽ�
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int writeCSV(File file, ResultSet rs) throws IOException, SQLException {
		return writeSep(file, rs, ",");
	}

	/**
	 * ResultSet�� TSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. ExcelUtil.setResultSetTSV�� ����
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderTSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetTSV(response, rs, fileName);
	}

	/**
	 * ResultSet�� TSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response ���䰴ü
	 * @param rs ResultSet
	 * @param fileName ���ϸ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetTSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetSep(response, rs, fileName, "\t");
	}

	/**
	 * ResultSet�� TSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @return ó���Ǽ�
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int writeTSV(File file, ResultSet rs) throws IOException, SQLException {
		return writeSep(file, rs, "\t");
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ����Ѵ�. ExcelUtil.setResultSetSep�� ����
	 * <br>
	 * ex) response�� rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ����ϴ� ���: ExcelUtil.renderSep(response, rs, ",")
	 *
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ������(CSV, TSV ��)���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param fileName ���ϸ�
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderSep(HttpServletResponse response, ResultSet rs, String fileName, String sep) throws SQLException, IOException {
		return setResultSetSep(response, rs, fileName, sep);
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ����ϴ� ���: ExcelUtil.setResultSetSep(response, rs, ",")
	 *
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ������(CSV, TSV ��)���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param fileName ���ϸ�
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetSep(HttpServletResponse response, ResultSet rs, String fileName, String sep) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		response.setContentType("application/octet-stream;");
		response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
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
					pw.print("\n");
				}
				pw.print(sepRowStr(rs, colNms, sep));
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
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ���Ϸ� �����Ѵ�.
	 * @param file ����
	 * @param rs ResultSet
	 * @param sep ������
	 * @return ó���Ǽ�
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int writeSep(File file, ResultSet rs, String sep) throws IOException, SQLException {
		if (rs == null) {
			return 0;
		}
		FileWriter fw = new FileWriter(file);
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
					fw.write("\n");
				}
				fw.write(sepRowStr(rs, colNms, sep));
			}
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (fw != null)
				fw.close();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. ExcelUtil.formatSep�� ����
	 * <br>
	 * ex) rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.renderSep(rs, ",")
	 *
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 * @throws SQLException SQLException
	 */
	public static String renderSep(ResultSet rs, String sep) throws SQLException {
		return formatSep(rs, sep);
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.formatSep(rs, ",")
	 *
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 * @throws SQLException SQLException
	 */
	public static String formatSep(ResultSet rs, String sep) throws SQLException {
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
					buffer.append("\n");
				}
				buffer.append(sepRowStr(rs, colNms, sep));
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
	 * Map��ü�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. ExcelUtil.formatSep�� ����
	 * <br>
	 * ex) map�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.renderSep(map, ",")
	 *
	 * @param map ��ȯ�� Map��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String renderSep(Map<String, Object> map, String sep) {
		return formatSep(map, sep);
	}

	/**
	 * Map��ü�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) map�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.formatSep(map, ",")
	 *
	 * @param map ��ȯ�� Map��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String formatSep(Map<String, Object> map, String sep) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(sepRowStr(map, sep));
		return buffer.toString();
	}

	/**
	 * List��ü�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. ExcelUtil.formatSep�� ����
	 * <br>
	 * ex1) mapList�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.renderSep(mapList, ",")
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String renderSep(List<Map<String, Object>> mapList, String sep) {
		return formatSep(mapList, sep);
	}

	/**
	 * List��ü�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) mapList�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ���: String csv = ExcelUtil.formatSep(mapList, ",")
	 *
	 * @param mapList ��ȯ�� List��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 *
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String formatSep(List<Map<String, Object>> mapList, String sep) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		for (Map<String, Object> map : mapList) {
			if (rowCount++ > 0) {
				buffer.append("\n");
			}
			buffer.append(sepRowStr(map, sep));
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ��ƿ��Ƽ

	/**
	 * �����ڷ� ���̴� ���ڿ� �Ǵ� ���๮�ڰ� ���� ���ԵǾ� ���� ��� ���� �ֵ���ǥ�� �ѷ��ε��� ��ȯ�Ѵ�.
	 *
	 * @param str ��ȯ�� ���ڿ�
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return escape �� ���ڿ�
	 */
	public static String escapeSep(String str, String sep) {
		if (str == null) {
			return "";
		}
		return (str.contains(sep) || str.contains("\n")) ? "\"" + str + "\"" : str;
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * ������(CSV, TSV ��)���� ������ Row ���ڿ� ����
	 * ����Ÿ�� ���ڰ� �ƴҶ����� �����ڷ� ���� ���ڿ� �Ǵ� ���๮�ڸ� escape �ϱ� ���� ���� �ֵ���ǥ�� �ѷ��Ѵ�.
	 */
	private static String sepRowStr(Map<String, Object> map, String sep) {
		StringBuilder buffer = new StringBuilder();
		Set<String> keys = map.keySet();
		int rowCount = 0;
		for (String key : keys) {
			Object value = map.get(key);
			if (rowCount++ > 0) {
				buffer.append(sep);
			}
			if (value == null) {
				buffer.append("");
			} else {
				if (value instanceof Number) {
					buffer.append(value.toString());
				} else {
					buffer.append(escapeSep(value.toString(), sep));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * ������(CSV, TSV ��)���� ������ Row ���ڿ� ����
	 * ����Ÿ�� ���ڰ� �ƴҶ����� �����ڷ� ���� ���ڿ� �Ǵ� ���๮�ڸ� escape �ϱ� ���� ���� �ֵ���ǥ�� �ѷ��Ѵ�.
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	private static String sepRowStr(RecordSet rs, String[] colNms, String sep) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (rowCount++ > 0) {
				buffer.append(sep);
			}
			if (value == null) {
				buffer.append("");
			} else {
				if (value instanceof Number) {
					buffer.append(value.toString());
				} else {
					buffer.append(escapeSep(value.toString(), sep));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * ������(CSV, TSV ��)���� ������ Row ���ڿ� ����
	 * ����Ÿ�� ���ڰ� �ƴҶ����� �����ڷ� ���� ���ڿ� �Ǵ� ���๮�ڸ� escape �ϱ� ���� ���� �ֵ���ǥ�� �ѷ��Ѵ�.
	 * @throws SQLException SQLException
	 */
	private static String sepRowStr(ResultSet rs, String[] colNms, String sep) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.getObject(colNms[c]);
			if (rowCount++ > 0) {
				buffer.append(sep);
			}
			if (value == null) {
				buffer.append("");
			} else {
				if (value instanceof Number) {
					buffer.append(value.toString());
				} else {
					buffer.append(escapeSep(value.toString(), sep));
				}
			}
		}
		return buffer.toString();
	}

	private static void appendHeader(Row row, String[] header, CellStyle cellStyle) {
		if (header == null)
			return;
		for (int c = 0; c < header.length; c++) {
			Cell cell = row.createCell(c);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(header[c]);
		}
	}

	private static void appendRow(Row row, RecordSet rs, String[] colNms, CellStyle cellStyle) throws ColumnNotFoundException {
		if (rs.getRowCount() == 0)
			return;
		for (int c = 0; c < colNms.length; c++) {
			Cell cell = row.createCell(c);
			Object value = rs.get(colNms[c]);
			if (value == null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue("");
			} else {
				if (value instanceof Number) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(Double.valueOf(value.toString()));
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(value.toString());
				}
			}
		}
	}

	private static void appendRow(Row row, ResultSet rs, String[] colNms, CellStyle cellStyle) throws SQLException {
		if (rs.getRow() == 0)
			return;
		for (int c = 0; c < colNms.length; c++) {
			Cell cell = row.createCell(c);
			Object value = rs.getObject(colNms[c]);
			if (value == null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue("");
			} else {
				if (value instanceof Number) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(Double.valueOf(value.toString()));
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(value.toString());
				}
			}
		}
	}

	private static List<Map<String, String>> parseXLS(InputStream is) throws Exception {
		return parseXLS(is, 0);
	}

	private static List<Map<String, String>> parseXLS(InputStream is, int startRow) throws Exception {
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(is);
		HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
		return parseSheet(workbook.getSheetAt(0), startRow);
	}

	private static List<Map<String, String>> parseXLS(InputStream is, String password) throws Exception {
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(is);
		Biff8EncryptionKey.setCurrentUserPassword(password);
		HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
		Biff8EncryptionKey.setCurrentUserPassword(null);
		return parseSheet(workbook.getSheetAt(0));
	}

	private static List<Map<String, String>> parseXLSX(InputStream is) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook(is);
		return parseSheet(workbook.getSheetAt(0));
	}

	private static List<Map<String, String>> parseXLSX(InputStream is, String password) throws Exception {
		POIFSFileSystem fs = new POIFSFileSystem(is);
		EncryptionInfo info = new EncryptionInfo(fs);
		Decryptor d = new Decryptor(info);
		d.verifyPassword(password);
		XSSFWorkbook workbook = new XSSFWorkbook(d.getDataStream(fs));
		return parseSheet(workbook.getSheetAt(0));
	}

	private static List<Map<String, String>> parseCSV(InputStream is) throws Exception {
		return parseSep(is, ",");
	}

	private static List<Map<String, String>> parseTSV(InputStream is) throws Exception {
		return parseSep(is, "\t");
	}

	private static List<Map<String, String>> parseSep(InputStream is, String sep) throws Exception {
		BufferedReader br = null;
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] items = line.split(sep);
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < items.length; i++) {
					map.put(String.valueOf(i), items[i]);
				}
				mapList.add(map);
			}
		} finally {
			if (br != null)
				br.close();
		}
		return mapList;
	}

	/**
	 * ���� ��Ʈ�� ������ �Ľ��Ͽ� ���� ����Ʈ�� ����
	 * @param sheet ���� ��ũ��Ʈ
	 * @return �����͸� ������ �ִ� ���� ����Ʈ
	 * @throws Exception Exception
	 */
	private static List<Map<String, String>> parseSheet(Sheet sheet) throws Exception {
		return parseSheet(sheet, 0);
	}
	
	/**
	 * ���� ��Ʈ�� ������ �Ľ��Ͽ� ���� ����Ʈ�� ����
	 * @param sheet
	 * @param startRow 0���� �����ϴ� �������� �ε�����.
	 * @return
	 * @throws Exception
	 */
	private static List<Map<String, String>> parseSheet(Sheet sheet, int startRow) throws Exception {
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(startRow).getPhysicalNumberOfCells();
		
		for (int i = 0; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				Map<String, String> map = new HashMap<String, String>();
				for (int j = 0; j < colCount; j++) {
					Cell cell = row.getCell(j);
					String item = "";
					if (cell != null) {
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_BOOLEAN:
						case Cell.CELL_TYPE_FORMULA:
						case Cell.CELL_TYPE_STRING:
							cell.setCellType(Cell.CELL_TYPE_STRING);
							item = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								item = dateFormat.format(date);
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								item = cell.getStringCellValue();
							}
							break;
						}
					}
					map.put(String.valueOf(j), item);
				}
				mapList.add(map);
			}
		}
		return mapList;
	}

	/**
	 * ��� �� ��Ÿ�� ����
	 */
	private static CellStyle headerStyle(Workbook workbook) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 11);
		font.setBold(true);
		font.setFontName("굴림");
		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	/**
	 * �ο� �� ��Ÿ�� ����
	 */
	private static CellStyle rowStyle(Workbook workbook) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 11);
		font.setBold(false);
		font.setFontName("굴림");
		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(true);
		return cellStyle;
	}
}