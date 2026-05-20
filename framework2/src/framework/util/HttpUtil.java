package framework.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HTTP Ŭ���̾�Ʈ�� ����� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class HttpUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private HttpUtil() {
	}

	/**
	 * Result ��ü
	 */
	public static class Result {
		private int _statusCode;
		private String _content;

		public Result() {
			super();
		}

		public Result(int statusCode, String content) {
			super();
			this._statusCode = statusCode;
			this._content = content;
		}

		public int getStatusCode() {
			return _statusCode;
		}

		public String getContent() {
			return _content;
		}

		@Override
		public String toString() {
			return String.format("Result={ statusCode : %d, content : %s }", getStatusCode(), getContent());
		}
	}

	/**
	 * url �� Get ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url �ּ�
	 * @return Result ��ü
	 */
	public static Result get(String url) {
		return get(url, null);
	}

	/**
	 * url �� Get ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url �ּ�
	 * @param headerMap ����ʰ�ü
	 * @return Result ��ü
	 */
	public static Result get(String url, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				statusCode = response.getStatusLine().getStatusCode();
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					content = EntityUtils.toString(resEntity);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return new Result(statusCode, content);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url �ּ�
	 * @return Result ��ü
	 */
	public static Result post(String url) {
		return post(url, null, (Map<String, String>) null);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url �ּ�
	 * @param paramMap �Ķ���͸ʰ�ü
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap) {
		return post(url, paramMap, (Map<String, String>) null);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url �ּ�
	 * @param paramMap �Ķ���͸ʰ�ü
	 * @param headerMap ����ʰ�ü
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(ent);
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				statusCode = response.getStatusLine().getStatusCode();
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					content = EntityUtils.toString(resEntity);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return new Result(statusCode, content);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�. (÷������ ����)
	 * @param url �ּ�
	 * @param paramMap �Ķ���͸ʰ�ü
	 * @param fileList ���ϸ���Ʈ
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap, List<File> fileList) {
		return post(url, paramMap, fileList, null);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�. (÷������ ����)
	 * @param url �ּ�
	 * @param paramMap �Ķ���͸ʰ�ü
	 * @param fileList ���ϸ���Ʈ
	 * @param headerMap ����ʰ�ü
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap, List<File> fileList, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
				}
			}
			if (fileList != null) {
				for (File file : fileList) {
					builder.addPart("userfile", new FileBody(file));
				}
			}
			httpPost.setEntity(builder.build());
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				statusCode = response.getStatusLine().getStatusCode();
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					content = EntityUtils.toString(resEntity);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return new Result(statusCode, content);
	}
}