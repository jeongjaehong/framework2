package framework.action;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.servlet5.JakartaServletFileUpload;

import framework.config.Configuration;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Multipart 요청객체, 쿠키객체의 값을 담는 해시테이블 객체이다. Multipart 요청객체의 파라미터를 추상화 하여
 * MultipartBox 를 생성해 놓고 파라미터이름을 키로 해당 값을 원하는 데이타 타입으로 반환받는다.
 */
public class MultipartBox extends Box {
	private static final long serialVersionUID = -8810823011616521004L;
	private List<FileItem> _fileItems = null;

	/***
	 * MultipartBox 생성자
	 * 
	 * @param name
	 *            MultipartBox 객체의 이름
	 */
	public MultipartBox(String name) {
		super(name);
		this._fileItems = new ArrayList<>();
	}

	/**
	 * Multipart 요청객체의 파라미터 이름과 값을 저장한 해시테이블을 생성한다. <br>
	 * ex) Multipart Request Box 객체를 얻는 경우: MultipartBox multipartBox =
	 * MultipartBox.getMultipartBox(request)
	 *
	 * @param request
	 *            HTTP 클라이언트 요청객체
	 *
	 * @return 요청 MultipartBox 객체
	 */
	public static MultipartBox getMultipartBox(HttpServletRequest request) {
		MultipartBox multipartBox = new MultipartBox("multipartbox");

		// 파라미터 맵 설정
		request.getParameterMap().forEach((key, values) -> multipartBox.put(key, values));

		if (request != null && request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/")) {
			try {

				// Jakarta 버전 빌더 사용
				DiskFileItemFactory factory = DiskFileItemFactory.builder().get();

				JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
				upload.setHeaderCharset(StandardCharsets.UTF_8);

				// 최대 파일 크기 설정
				try {
					upload.setSizeMax(getConfig().getInt("fileupload.sizeMax")); // 업로드 최대 크기 설정
				} catch (IllegalArgumentException e) {
					// 설정 오류 무시
				}

				// 업로드 요청 파싱
				List<FileItem> items = upload.parseRequest(request);
				for (FileItem item : items) {
					if (item.isFormField()) {
						String fieldName = item.getFieldName();
						String fieldValue = item.getString();
						String[] oldValue = multipartBox.getArray(fieldName);

						if (oldValue == null) {
							multipartBox.put(fieldName, new String[] { fieldValue });
						} else {
							int size = oldValue.length;
							String[] newValue = new String[size + 1];
							System.arraycopy(oldValue, 0, newValue, 0, size);
							newValue[size] = fieldValue;
							multipartBox.put(fieldName, newValue);
						}
					} else {
						multipartBox.addFileItem(item);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return multipartBox;
	}

	/**
	 * 파일아이템(FileItem)의 리스트 객체를 리턴한다.
	 *
	 * @return 파일아이템 리스트 객체
	 */
	public List<FileItem> getFileItems() {
		return _fileItems;
	}

	//////////////////////////////////////////////////////////////////////////////
	// Private 메소드

	/**
	 * Multipart 파일업로드시 파일 아이템을 리스트에 추가한다.
	 *
	 * @param item
	 *            파일을 담고 있는 객체
	 * @return 성공여부
	 */
	private boolean addFileItem(FileItem item) {
		return _fileItems.add(item);
	}

	/**
	 * 설정정보를 가지고 있는 객체를 생성하여 리턴한다.
	 *
	 * @return config.properties의 설정정보 객체
	 */
	private static Configuration getConfig() {
		return Configuration.getInstance();
	}
}