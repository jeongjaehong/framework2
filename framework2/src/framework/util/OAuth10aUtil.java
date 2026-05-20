/** 
 * @(#)OAuth10aUtil.java
 */
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

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

/**
 * OAuth 1.0a ������ ����ϱ� ���� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class OAuth10aUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private OAuth10aUtil() {
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

		public String toString() {
			return String.format("Result={ statusCode : %d, content : %s }", getStatusCode(), getContent());
		}
	}

	/**
	 * Consumer ��ü
	 */
	public static class Consumer extends CommonsHttpOAuthConsumer {
		private static final long serialVersionUID = 3312085951191371927L;

		public Consumer(String consumerKey, String consumerSecret) {
			super(consumerKey, consumerSecret);
		}

		public Consumer(String consumerKey, String consumerSecret, String token, String tokenSecret) {
			this(consumerKey, consumerSecret);
			this.setTokenWithSecret(token, tokenSecret);
		}
	}

	/**
	 * Provider ��ü
	 */
	public static class Provider extends CommonsHttpOAuthProvider {
		private static final long serialVersionUID = -4670920617701598709L;

		public Provider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
			super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
			this.setOAuth10a(true);
		}
	}

	/**
	 * RequestToken ��û �ܰ迡 �ʿ��� Consumer�� �����Ѵ�.
	 * @param consumerKey ������Ű
	 * @param consumerSecret �����ӽ�ũ��
	 * @return Consumer ��ü
	 */
	public static Consumer makeConsumer(String consumerKey, String consumerSecret) {
		return new Consumer(consumerKey, consumerSecret);
	}

	/**
	 * Protected Resource ��û �ܰ迡 �ʿ��� Consumer�� �����Ѵ�.
	 * @param consumerKey ������Ű
	 * @param consumerSecret �����ӽ�ũ��
	 * @param token �׼�����ū
	 * @param tokenSecret �׼�����ū��ũ��
	 * @return Consumer ��ü
	 */
	public static Consumer makeConsumer(String consumerKey, String consumerSecret, String token, String tokenSecret) {
		return new Consumer(consumerKey, consumerSecret, token, tokenSecret);
	}

	/**
	 * �Է��� ������ Provider�� �����Ѵ�.
	 * @param requestTokenEndpointUrl ��û ��ū �ּ�
	 * @param accessTokenEndpointUrl ������ ��ū �ּ�
	 * @param authorizationWebsiteUrl ���� �ּ�
	 * @return Provider ��ü
	 */
	public static Provider makeProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
		return new Provider(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
	}

	/**
	 * Provider�� RequestToken�� ��û�Ͽ�, RequestToken�� RequestTokenSecret�� �޾ƿ´�. 
	 * @param consumer ������ ��ü
	 * @param provider ���ι��̴� ��ü
	 * @param callbackUrl �ݹ��ּ�
	 * @return authorize URL
	 */
	public static String getRequestToken(Consumer consumer, Provider provider, String callbackUrl) {
		try {
			String authorizeUrl = provider.retrieveRequestToken(consumer, callbackUrl);
			return authorizeUrl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Provider�� AccessToken�� ��û�Ͽ�, AccessToken�� AccessTokenSecret�� �޾ƿ´�.
	 * @param consumer ������ ��ü
	 * @param provider ���ι��̴� ��ü
	 * @param verifier ������ �Ǵ� ���ڵ�
	 */
	public static void getAccessToken(Consumer consumer, Provider provider, String verifier) {
		try {
			provider.retrieveAccessToken(consumer, verifier);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Protected Resource �� GET ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @return Result ��ü
	 */
	public static Result get(Consumer consumer, String url) {
		return get(consumer, url, null);
	}

	/**
	 * Protected Resource �� GET ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @param headerMap ���
	 * @return Result ��ü
	 */
	public static Result get(Consumer consumer, String url, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpGet get = new HttpGet(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					get.addHeader(entry.getKey(), entry.getValue());
				}
			}
			consumer.sign(get);
			try (CloseableHttpResponse responseGet = client.execute(get)) {
				statusCode = responseGet.getStatusLine().getStatusCode();
				HttpEntity resEntityGet = responseGet.getEntity();
				if (resEntityGet != null) {
					content = EntityUtils.toString(resEntityGet);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Result(statusCode, content);
	}

	/**
	 * Protected Resource �� POST ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @return Result ��ü
	 */
	public static Result post(Consumer consumer, String url) {
		return post(consumer, url, null, (Map<String, String>) null);
	}

	/**
	 * Protected Resource �� POST ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @param paramMap �Ķ����
	 * @return Result ��ü
	 */
	public static Result post(Consumer consumer, String url, Map<String, String> paramMap) {
		return post(consumer, url, paramMap, (Map<String, String>) null);
	}

	/**
	 * Protected Resource �� POST ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @param paramMap �Ķ����
	 * @param headerMap ���
	 * @return Result ��ü
	 */
	public static Result post(Consumer consumer, String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(ent);
			consumer.sign(post);
			try (CloseableHttpResponse responsePOST = client.execute(post)) {
				statusCode = responsePOST.getStatusLine().getStatusCode();
				HttpEntity resEntity = responsePOST.getEntity();
				if (resEntity != null) {
					content = EntityUtils.toString(resEntity);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Result(statusCode, content);
	}

	/**
	 * Protected Resource �� POST ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @param paramMap �Ķ����
	 * @param fileList ����
	 * @return Result ��ü
	 */
	public static Result post(Consumer consumer, String url, Map<String, String> paramMap, List<File> fileList) {
		return post(consumer, url, paramMap, fileList, null);
	}

	/**
	 * Protected Resource �� POST ������� ��û�Ѵ�.
	 * @param consumer ������ ��ü
	 * @param url API URL
	 * @param paramMap �Ķ����
	 * @param fileList ����
	 * @param headerMap ���
	 * @return Result ��ü
	 */
	public static Result post(Consumer consumer, String url, Map<String, String> paramMap, List<File> fileList, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			consumer.sign(post);
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
			post.setEntity(builder.build());
			try (CloseableHttpResponse response = client.execute(post)) {
				statusCode = response.getStatusLine().getStatusCode();
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					content = EntityUtils.toString(resEntity);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Result(statusCode, content);
	}
}
