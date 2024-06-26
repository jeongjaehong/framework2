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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * Excel 출력을 위해 이용할 수 있는 유틸리티 클래스이다.
 */
public class ExcelUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private ExcelUtil() {
	}

	//////////////////////////////////////////////////////////////////////////////////////////파싱
	/**
	 * 확장자에 의해서 엑셀파일을 파싱한다.
	 * @param fileItem 파일아이템
	 * @return 데이터의 리스트
	 * @throws Exception Exception
	 */
	public static List<Map<String, String>> parse(FileItem fileItem) throws Exception {
		return parse(fileItem, 0);
	}
	
	/**
	 *  확장자에 의해서 엑셀파일을 파싱한다.
	 * @param fileItem 파일 아이템.
	 * @param startRow 파싱할 파일의 헤더가 있는 행. 컬럼갯수를 측정할 위치. 0부터 시작.
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
			throw new Exception("지원하지 않는 파일포맷입니다.");
		}
	}

	/**
	 * 암호화된 엑셀파일을 파싱한다.
	 * @param fileItem 파일아이템
	 * @param password 비밀번호
	 * @return 데이터의 리스트
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
			throw new Exception("지원하지 않는 파일포맷입니다.");
		}
	}

	/**
	 * 확장자에 의해서 엑셀파일을 파싱한다.
	 * @param file 파일
	 * @return 데이터의 리스트
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
				throw new Exception("지원하지 않는 파일포맷입니다.");
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	/**
	 * 암호화된 엑셀파일을 파싱한다.
	 * @param file 파일
	 * @param password 패스워드
	 * @return 데이터의 리스트
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
				throw new Exception("지원하지 않는 파일포맷입니다.");
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setRecordSetXLS과 동일
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLS(response, rs, fileName, null);
	}

	/**
	 * RecordSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setRecordSetXLS과 동일
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, RecordSet rs, String fileName, String[] header) throws ColumnNotFoundException, IOException {
		return setRecordSetXLS(response, rs, fileName, header);
	}

	/**
	 * RecordSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetXLS(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLS(response, rs, fileName, null);
	}

	/**
	 * RecordSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
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
	 * RecordSet을 엑셀2003 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int writeXLS(File file, RecordSet rs) throws ColumnNotFoundException, IOException {
		return writeXLS(file, rs, null);
	}

	/**
	 * RecordSet을 엑셀2003 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @param header 헤더
	 * @return 처리건수
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
	 * RecordSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setRecordSetXLSX과 동일
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLSX(response, rs, fileName, null);
	}

	/**
	 * RecordSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setRecordSetXLSX과 동일
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, RecordSet rs, String fileName, String[] header) throws ColumnNotFoundException, IOException {
		return setRecordSetXLSX(response, rs, fileName, header);
	}

	/**
	 * RecordSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetXLSX(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetXLSX(response, rs, fileName, null);
	}

	/**
	 * RecordSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
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
	 * RecordSet을 엑셀2007 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int writeXLSX(File file, RecordSet rs) throws ColumnNotFoundException, IOException {
		return writeXLSX(file, rs, null);
	}

	/**
	 * RecordSet을 엑셀2007 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @param header 헤더
	 * @return 처리건수
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
	 * RecordSet을 CSV 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setRecordSetCSV과 동일
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderCSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetCSV(response, rs, fileName);
	}

	/**
	 * RecordSet을 CSV 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetCSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetSep(response, rs, fileName, ",");
	}

	/**
	 * RecordSet을 CSV 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static int writeCSV(File file, RecordSet rs) throws IOException, ColumnNotFoundException {
		return writeSep(file, rs, ",");
	}

	/**
	 * RecordSet을 TSV 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setRecordSetTSV과 동일
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderTSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetTSV(response, rs, fileName);
	}

	/**
	 * RecordSet을 TSV 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs RecordSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSetTSV(HttpServletResponse response, RecordSet rs, String fileName) throws ColumnNotFoundException, IOException {
		return setRecordSetSep(response, rs, fileName, "\t");
	}

	/**
	 * RecordSet을 TSV 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static int writeTSV(File file, RecordSet rs) throws IOException, ColumnNotFoundException {
		return writeSep(file, rs, "\t");
	}

	/**
	 * RecordSet을 구분자(CSV, TSV 등)파일 형식으로 출력한다. ExcelUtil.setRecordSetSep과 동일
	 * <br>
	 * ex) response로 rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 출력하는 경우: ExcelUtil.renderSep(response, rs, ",")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 구분자(CSV, TSV 등)파일 형식으로 변환할 RecordSet 객체
	 * @param fileName 파일명
	 * @param sep 열 구분자로 쓰일 문자열
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int renderSep(HttpServletResponse response, RecordSet rs, String fileName, String sep) throws ColumnNotFoundException, IOException {
		return setRecordSetSep(response, rs, fileName, sep);
	}

	/**
	 * RecordSet을 구분자(CSV, TSV 등)파일 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 출력하는 경우: ExcelUtil.setRecordSetSep(response, rs, ",")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 구분자(CSV, TSV 등)파일 형식으로 변환할 RecordSet 객체
	 * @param fileName 파일명
	 * @param sep 열 구분자로 쓰일 문자열
	 * @return 처리건수
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
	 * RecordSet을 구분자(CSV, TSV 등)파일 형식으로 파일로 저장한다.
	 * @param file 파일
	 * @param rs RecordSet
	 * @param sep 구분자
	 * @return 처리건수
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
	 * RecordSet을 구분자(CSV, TSV 등)파일 형식으로 변환한다. ExcelUtil.formatSep과 동일
	 * <br>
	 * ex) rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.renderSep(rs, ",")
	 *
	 * @param rs 변환할 RecordSet 객체
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String renderSep(RecordSet rs, String sep) throws ColumnNotFoundException {
		return formatSep(rs, sep);
	}

	/**
	 * RecordSet을 구분자(CSV, TSV 등)파일 형식으로 변환한다.
	 * <br>
	 * ex) rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.formatSep(rs, ",")
	 *
	 * @param rs 변환할 RecordSet 객체
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
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

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setResultSetXLS과 동일
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLS(response, rs, fileName, null);
	}

	/**
	 * ResultSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setResultSetXLS과 동일
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLS(HttpServletResponse response, ResultSet rs, String fileName, String[] header) throws SQLException, IOException {
		return setResultSetXLS(response, rs, fileName, header);
	}

	/**
	 * ResultSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetXLS(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLS(response, rs, fileName, null);
	}

	/**
	 * ResultSet을 엑셀2003 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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
	 * ResultSet을 엑셀2003 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int writeXLS(File file, ResultSet rs) throws SQLException, IOException {
		return writeXLS(file, rs, null);
	}

	/**
	 * ResultSet을 엑셀2003 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @param header 헤더
	 * @return 처리건수
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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
	 * ResultSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setResultSetXLSX과 동일
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLSX(response, rs, fileName, null);
	}

	/**
	 * ResultSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setResultSetXLSX과 동일
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderXLSX(HttpServletResponse response, ResultSet rs, String fileName, String[] header) throws SQLException, IOException {
		return setResultSetXLSX(response, rs, fileName, header);
	}

	/**
	 * ResultSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetXLSX(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetXLSX(response, rs, fileName, null);
	}

	/**
	 * ResultSet을 엑셀2007 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @param header 헤더
	 * @return 처리건수
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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
	 * ResultSet을 엑셀2007 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int writeXLSX(File file, ResultSet rs) throws SQLException, IOException {
		return writeXLSX(file, rs, null);
	}

	/**
	 * ResultSet을 엑셀2007 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @param header 헤더
	 * @return 처리건수
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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
	 * ResultSet을 CSV 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setResultSetCSV과 동일
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderCSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetCSV(response, rs, fileName);
	}

	/**
	 * ResultSet을 CSV 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetCSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetSep(response, rs, fileName, ",");
	}

	/**
	 * ResultSet을 CSV 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int writeCSV(File file, ResultSet rs) throws IOException, SQLException {
		return writeSep(file, rs, ",");
	}

	/**
	 * ResultSet을 TSV 형식으로 변환하여 응답객체로 전송한다. ExcelUtil.setResultSetTSV과 동일
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderTSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetTSV(response, rs, fileName);
	}

	/**
	 * ResultSet을 TSV 형식으로 변환하여 응답객체로 전송한다.
	 * @param response 응답객체
	 * @param rs ResultSet
	 * @param fileName 파일명
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSetTSV(HttpServletResponse response, ResultSet rs, String fileName) throws SQLException, IOException {
		return setResultSetSep(response, rs, fileName, "\t");
	}

	/**
	 * ResultSet을 TSV 형식으로 변환하여 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int writeTSV(File file, ResultSet rs) throws IOException, SQLException {
		return writeSep(file, rs, "\t");
	}

	/**
	 * ResultSet을 구분자(CSV, TSV 등)파일 형식으로 출력한다. ExcelUtil.setResultSetSep과 동일
	 * <br>
	 * ex) response로 rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 출력하는 경우: ExcelUtil.renderSep(response, rs, ",")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 구분자(CSV, TSV 등)파일 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param fileName 파일명
	 * @param sep 열 구분자로 쓰일 문자열
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int renderSep(HttpServletResponse response, ResultSet rs, String fileName, String sep) throws SQLException, IOException {
		return setResultSetSep(response, rs, fileName, sep);
	}

	/**
	 * ResultSet을 구분자(CSV, TSV 등)파일 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 출력하는 경우: ExcelUtil.setResultSetSep(response, rs, ",")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 구분자(CSV, TSV 등)파일 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param fileName 파일명
	 * @param sep 열 구분자로 쓰일 문자열
	 * @return 처리건수
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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
	 * ResultSet을 구분자(CSV, TSV 등)파일 형식으로 파일로 저장한다.
	 * @param file 파일
	 * @param rs ResultSet
	 * @param sep 구분자
	 * @return 처리건수
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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
	 * ResultSet을 구분자(CSV, TSV 등)파일 형식으로 변환한다. ExcelUtil.formatSep과 동일
	 * <br>
	 * ex) rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.renderSep(rs, ",")
	 *
	 * @param rs 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
	 * @throws SQLException SQLException
	 */
	public static String renderSep(ResultSet rs, String sep) throws SQLException {
		return formatSep(rs, sep);
	}

	/**
	 * ResultSet을 구분자(CSV, TSV 등)파일 형식으로 변환한다.
	 * <br>
	 * ex) rs를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.formatSep(rs, ",")
	 *
	 * @param rs 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
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
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
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

	////////////////////////////////////////////////////////////////////////////////////////// 기타 Collection 이용

	/**
	 * Map객체를 구분자(CSV, TSV 등)파일 형식으로 변환한다. ExcelUtil.formatSep과 동일
	 * <br>
	 * ex) map을 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.renderSep(map, ",")
	 *
	 * @param map 변환할 Map객체
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
	 */
	public static String renderSep(Map<String, Object> map, String sep) {
		return formatSep(map, sep);
	}

	/**
	 * Map객체를 구분자(CSV, TSV 등)파일 형식으로 변환한다.
	 * <br>
	 * ex) map을 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.formatSep(map, ",")
	 *
	 * @param map 변환할 Map객체
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
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
	 * List객체를 구분자(CSV, TSV 등)파일 형식으로 변환한다. ExcelUtil.formatSep과 동일
	 * <br>
	 * ex1) mapList를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.renderSep(mapList, ",")
	 *
	 * @param mapList 변환할 List객체
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
	 */
	public static String renderSep(List<Map<String, Object>> mapList, String sep) {
		return formatSep(mapList, sep);
	}

	/**
	 * List객체를 구분자(CSV, TSV 등)파일 형식으로 변환한다.
	 * <br>
	 * ex1) mapList를 열구분자 콤마(,) 인 구분자(CSV, TSV 등)파일 형식으로 변환하는 경우: String csv = ExcelUtil.formatSep(mapList, ",")
	 *
	 * @param mapList 변환할 List객체
	 * @param sep 열 구분자로 쓰일 문자열
	 *
	 * @return 구분자(CSV, TSV 등)파일 형식으로 변환된 문자열
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

	////////////////////////////////////////////////////////////////////////////////////////// 유틸리티

	/**
	 * 구분자로 쓰이는 문자열 또는 개행문자가 값에 포함되어 있을 경우 값을 쌍따옴표로 둘러싸도록 변환한다.
	 *
	 * @param str 변환할 문자열
	 * @param sep 열 구분자로 쓰일 문자열
	 * @return escape 된 문자열
	 */
	public static String escapeSep(String str, String sep) {
		if (str == null) {
			return "";
		}
		return (str.contains(sep) || str.contains("\n")) ? "\"" + str + "\"" : str;
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 * 구분자(CSV, TSV 등)파일 생성용 Row 문자열 생성
	 * 데이타가 숫자가 아닐때에는 구분자로 쓰인 문자열 또는 개행문자를 escape 하기 위해 값을 쌍따옴표로 둘러싼다.
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
	 * 구분자(CSV, TSV 등)파일 생성용 Row 문자열 생성
	 * 데이타가 숫자가 아닐때에는 구분자로 쓰인 문자열 또는 개행문자를 escape 하기 위해 값을 쌍따옴표로 둘러싼다.
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
	 * 구분자(CSV, TSV 등)파일 생성용 Row 문자열 생성
	 * 데이타가 숫자가 아닐때에는 구분자로 쓰인 문자열 또는 개행문자를 escape 하기 위해 값을 쌍따옴표로 둘러싼다.
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
	 * 엑셀 시트의 데이터 파싱하여 맵의 리스트로 리턴
	 * @param sheet 엑셀 워크시트
	 * @return 데이터를 가지고 있는 맵의 리스트
	 * @throws Exception Exception
	 */
	private static List<Map<String, String>> parseSheet(Sheet sheet) throws Exception {
		return parseSheet(sheet, 0);
	}
	
	/**
	 * 엑셀 시트의 데이터 파싱하여 맵의 리스트로 리턴
	 * @param sheet
	 * @param startRow 0부터 시작하는 시작행의 인덱스값.
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
	 * 헤더 셀 스타일 리턴
	 */
	private static CellStyle headerStyle(Workbook workbook) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 11);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("돋움");
		font.setColor(HSSFColor.BLACK.index);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	/**
	 * 로우 셀 스타일 리턴
	 */
	private static CellStyle rowStyle(Workbook workbook) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 11);
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		font.setFontName("돋움");
		font.setColor(HSSFColor.BLACK.index);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		return cellStyle;
	}
}