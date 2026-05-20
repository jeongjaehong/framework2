package framework.action;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

import framework.config.Configuration;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Multipart ��û��ü, ��Ű��ü�� ���� ��� �ؽ����̺� ��ü�̴�. Multipart ��û��ü�� �Ķ���͸� �߻�ȭ �Ͽ�
 * MultipartBox �� ������ ���� �Ķ�����̸��� Ű�� �ش� ���� ���ϴ� ����Ÿ Ÿ������ ��ȯ�޴´�.
 */
public class MultipartBox extends Box {
	private static final long serialVersionUID = -8810823011616521004L;
	private List<FileItem> _fileItems = null;

	/***
	 * MultipartBox ������
	 * 
	 * @param name
	 *            MultipartBox ��ü�� �̸�
	 */
	public MultipartBox(String name) {
		super(name);
		this._fileItems = new ArrayList<>();
	}

	/**
	 * Multipart ��û��ü�� �Ķ���� �̸��� ���� ������ �ؽ����̺��� �����Ѵ�. <br>
	 * ex) Multipart Request Box ��ü�� ��� ���: MultipartBox multipartBox =
	 * MultipartBox.getMultipartBox(request)
	 *
	 * @param request
	 *            HTTP Ŭ���̾�Ʈ ��û��ü
	 *
	 * @return ��û MultipartBox ��ü
	 */
	public static MultipartBox getMultipartBox(HttpServletRequest request) {
		MultipartBox multipartBox = new MultipartBox("multipartbox");

		// �Ķ���� �� ����
		request.getParameterMap().forEach((key, values) -> multipartBox.put(key, values));

		if (request != null && request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/")) {
			try {

				// Jakarta ���� ���� ���
				DiskFileItemFactory factory = DiskFileItemFactory.builder().get();

				JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
				upload.setHeaderCharset(StandardCharsets.UTF_8);

				// �ִ� ���� ũ�� ����
				try {
					upload.setSizeMax(getConfig().getInt("fileupload.sizeMax")); // ���ε� �ִ� ũ�� ����
				} catch (IllegalArgumentException e) {
					// ���� ���� ����
				}

				// ���ε� ��û �Ľ�
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
	 * ���Ͼ�����(FileItem)�� ����Ʈ ��ü�� �����Ѵ�.
	 *
	 * @return ���Ͼ����� ����Ʈ ��ü
	 */
	public List<FileItem> getFileItems() {
		return _fileItems;
	}

	//////////////////////////////////////////////////////////////////////////////
	// Private �޼ҵ�

	/**
	 * Multipart ���Ͼ��ε�� ���� �������� ����Ʈ�� �߰��Ѵ�.
	 *
	 * @param item
	 *            ������ ��� �ִ� ��ü
	 * @return ��������
	 */
	private boolean addFileItem(FileItem item) {
		return _fileItems.add(item);
	}

	/**
	 * ���������� ������ �ִ� ��ü�� �����Ͽ� �����Ѵ�.
	 *
	 * @return config.properties�� �������� ��ü
	 */
	private static Configuration getConfig() {
		return Configuration.getInstance();
	}
}