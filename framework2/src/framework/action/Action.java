/** 
 * @(#)Action.java
 */
package framework.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.config.Configuration;
import framework.db.ConnectionManager;

/** 
 * �����Ͻ� ������ ó���ϴ� Ŭ������ ��ӹ޾ƾ� �� �߻�Ŭ�����̴�.
 * ��������(jsp ������)�� ����Ǳ� ���� Ŭ���̾�Ʈ���� ������ ���۵� �����͸� ���ϰ� ���������� �ݿ��ϱ� 
 * ���� ��ó��(Pre-processing)����̴�. �ϳ��� ���񽺿� ���� �������� ���������� ������Ʈ ���·� �����Ͽ� ����� �� �ִ�. 
 * �ۼ��� Actioin�� action.properties�� ��ϵȴ�.
 */
public abstract class Action {
	private final Map<String, ConnectionManager> _connMgrMap = new HashMap<String, ConnectionManager>();
	private static final String _FLASH_SCOPE_OBJECT_KEY = "___FLASH_SCOPE_OBJECT___";
	private HttpServlet _servlet = null;
	private Box _input = null;
	private Box _cookies = null;
	private MultipartBox _multipartInput = null;
	private PrintWriter _out = null;
	private HttpServletRequest _request = null;
	private HttpServletResponse _response = null;
	private Map<String, Object> flash = null;
	private static Log _logger = LogFactory.getLog(framework.action.Action.class);

	/** 
	 * Ŭ���̾�Ʈ���� ���񽺸� ȣ���� �� ��û�Ķ���� action�� ������ ���� �����Ͽ� �ش� �޼ҵ带 �����Ѵ�.
	 * �޼ҵ���� process �� ���ξ�� �Ͽ� action���� �߰��� ��Ī�̴�.
	 * ���ǵ��� ���� �޼ҵ带 ȣ���� ��� �α׿� �����޽����� ��ϵǸ� �޼ҵ� ������ ��ģ �� ����Ÿ���̽� ������ �ڵ����� �ݾ��ش�.
	 * <br>
	 * ex) action�� search �϶� => processSearch() �޼ҵ尡 ȣ��ȴ�.
	 * 
	 * @param servlet ���� ��ü
	 * @param request Ŭ���̾�Ʈ���� ��û�� Request��ü
	 * @param response Ŭ���̾�Ʈ�� ������ Response��ü
	 * @throws Exception 
	 */
	public void execute(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setServlet(servlet);
		setRequest(request);
		setResponse(response);
		this.flash = new HashMap<String, Object>();
		flashRestore();
		Method method = getMethod(request.getParameter("action"));
		if (method == null) {
			throw new PageNotFoundExeption("action");
		}
		try {
			method.invoke(this, (Object[]) null);
		} finally {
			flashSave();
			destroy();
		}
	}

	/**
	 * ��û�� JSP�������� ������(Forward) �Ѵ�.
	 * �ۼ��� JSP��������  action.properties�� ��ϵȴ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� => route("search-jsp")
	 * 
	 * @param key action.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void route(String key) {
		try {
			ActionRouter router = new ActionRouter(key);
			router.route(getServlet(), getRequest(), getResponse());
		} catch (Exception e) {
			getLogger().error("Router Error!", e);
		}
	}

	/** 
	 * ��û�� JSP�������� ������(Forward) �Ǵ� ������(Redirect) �Ѵ�.
	 * �ۼ��� JSP��������  action.properties�� ��ϵȴ�.
	 * <br>
	 * ex1) Ű�� search-jsp �� JSP�������� ������ �� ��� => route("search-jsp", true)
	 * <br>
	 * ex2) Ű�� search-jsp �� JSP�������� ������ �� ��� => route("search-jsp", false)
	 * 
	 * @param key action.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 * @param isForward true�̸� ������(Forward), false �̸� ������(Redirect)
	 */
	protected void route(String key, boolean isForward) {
		try {
			ActionRouter router = new ActionRouter(key, isForward);
			router.route(getServlet(), getRequest(), getResponse());
		} catch (Exception e) {
			getLogger().error("Router Error!", e);
		}
	}

	/** 
	 * ��û�� JSP�������� ������(Redirect) �Ѵ�.
	 * �ۼ��� JSP��������  action.properties�� ��ϵȴ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� => redirect("search-jsp")
	 * 
	 * @param key action.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void redirect(String key) {
		route(key, false);
	}

	/** 
	 * ����Ÿ���̽� ���������(���ؼ� �Ŵ���) ��ü�� �����Ѵ�.
	 * <br>
	 * config.properties�� datasource�� ��ϵǾ� ������ JNDI�� ��ϵǾ��ִ� ����Ÿ�ҽ����� ���ؼ��� �����Ѵ�.
	 * datasource�� ��ϵǾ� ���� �ʴ� ��� ���������� �������� jdbc ���ؼ��� �����Ѵ�.
	 * �������� default�� �ش��ϴ� �������� ������ �̿��Ͽ� ���ؼ��� �����Ѵ�.
	 * ������ ���ؼ��� autoCommit �Ӽ��� false �� ���õȴ�.
	 *
	 * @return ���������(���ؼ� �Ŵ���) ��ü
	 */
	protected ConnectionManager getConnectionManager() {
		return getConnectionManager("default");
	}

	/** 
	 * ����Ÿ���̽� ���������(���ؼ� �Ŵ���) ��ü�� �����Ѵ�.
	 * <br>
	 * config.properties�� datasource�� ��ϵǾ� ������ JNDI�� ��ϵǾ��ִ� ����Ÿ�ҽ����� ���ؼ��� �����Ѵ�.
	 * datasource�� ��ϵǾ� ���� �ʴ� ��� ���������� �������� jdbc ���ؼ��� �����Ѵ�.
	 * �Ķ���ͷ� �Ѱ��� ������ �ش��ϴ� �������� ������ �̿��Ͽ� ���ؼ��� �����Ѵ�.
	 * ������ ���ؼ��� autoCommit �Ӽ��� false �� ���õȴ�.
	 *
	 * @param serviceName ���񽺸�(������)
	 * @return ���������(���ؼ� �Ŵ���) ��ü
	 */
	protected ConnectionManager getConnectionManager(String serviceName) {
		if (!this._connMgrMap.containsKey(serviceName)) {
			String dsName = null;
			String jdbcDriver = null;
			String jdbcUrl = null;
			String jdbcUid = null;
			String jdbcPw = null;
			try {
				dsName = getConfig().getString("jdbc." + serviceName + ".datasource");
			} catch (Exception e) {
				// �������Ͽ� ����Ÿ�ҽ��� ���ǵǾ����� ������ ����
				jdbcDriver = getConfig().getString("jdbc." + serviceName + ".driver");
				jdbcUrl = getConfig().getString("jdbc." + serviceName + ".url");
				jdbcUid = getConfig().getString("jdbc." + serviceName + ".uid");
				jdbcPw = getConfig().getString("jdbc." + serviceName + ".pwd");
			}
			try {
				ConnectionManager connMgr = new ConnectionManager(dsName, this);
				if (dsName != null) {
					connMgr.connect();
				} else {
					connMgr.connect(jdbcDriver, jdbcUrl, jdbcUid, jdbcPw);
				}
				connMgr.setAutoCommit(false);
				this._connMgrMap.put(serviceName, connMgr);
			} catch (Exception e) {
				getLogger().error("DB Connection Error!", e);
			}
		}
		return this._connMgrMap.get(serviceName);
	}

	/** 
	 * ���������� ������ �ִ� ��ü�� �����Ͽ� �����Ѵ�.
	 *
	 * @return config.properties�� ���������� ������ �ִ� ��ü
	 */
	protected Configuration getConfig() {
		return Configuration.getInstance();
	}

	/** 
	 * Action��ü�� ȣ���� ���� ��ü�� �����Ѵ�.
	 *
	 * @return Action��ü�� ȣ���� ���� ��ü
	 */
	protected HttpServlet getServlet() {
		return this._servlet;
	}

	/** 
	 * HTTP Ŭ���̾�Ʈ ��û ��ü�� �����Ѵ�.
	 *
	 * @return HTTP Ŭ���̾�Ʈ ��û��ü
	 */
	protected HttpServletRequest getRequest() {
		return this._request;
	}

	/** 
	 * HTTP Ŭ���̾�Ʈ ���� ��ü�� �����Ѵ�.
	 *
	 * @return HTTP Ŭ���̾�Ʈ ���䰴ü
	 */
	protected HttpServletResponse getResponse() {
		return this._response;
	}

	/** 
	 * Ŭ���̾�Ʈ�� ���� ��ü�� �����Ѵ�.
	 * �̹� ������ �����Ǿ� �ִ°��� ���� ������ �����ϸ� ������ ���°��� ���� �����Ͽ� �����Ѵ�. 
	 *
	 * @return Ŭ���̾�Ʈ�� ���� ��ü
	 */
	protected HttpSession getSession() {
		return getRequest().getSession();
	}

	/** 
	 * Ŭ���̾�Ʈ�� ���� ��ü�� �����Ѵ�.
	 * ������ ���°��� �Ķ���� ���� true�̸� ������ ���� �����ϸ� false �̸� �������� �ʴ´�. 
	 *
	 * @param create ������ ������� true�̸� ���� ����, false�̸� �������� ����
	 * @return Ŭ���̾�Ʈ�� ���� ��ü
	 */
	protected HttpSession getSession(boolean create) {
		return getRequest().getSession(create);
	}

	/** 
	 * ���ǰ�ü���� �ش� Ű�� �ش��ϴ� ������Ʈ�� �����Ѵ�.
	 * <br>
	 * ex) ���ǿ��� result��� Ű�� ������Ʈ�� ���Ϲ޴� ��� => Object obj = getSessionAttribute("result")
	 *
	 * @param key ���ǰ�ü�� ��ȸŰ
	 * @return ���ǰ�ü���� ���� ������Ʈ
	 */
	protected Object getSessionAttribute(String key) {
		return getSession().getAttribute(key);
	}

	/** 
	 * ��û�Ķ������ ���� ��� �ִ� �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex1) [ name=ȫ�浿 ]�� ��û�Ķ���͸� �޾ƿ��� ��� => String name = getInput().getString("name")
	 * <br>
	 * ex2) [ age=20 ]�� ��û�Ķ���͸� �޾ƿ��� ��� => Integer age = getInput().getInteger("age")
	 *
	 * @return ��û�Ķ������ ���� ��� �ؽ����̺�
	 */
	protected Box getInput() {
		if (this._input == null) {
			this._input = Box.getBox(getRequest());
		}
		return this._input;
	}

	/** 
	 * Multipart ��û�Ķ������ ���� ��� �ִ� �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex1) [ name=ȫ�浿 ]�� ��û�Ķ���͸� �޾ƿ��� ��� => String name = getMultipartInput().getString("name")
	 * <br>
	 * ex2) [ age=20 ]�� ��û�Ķ���͸� �޾ƿ��� ��� => Integer age = getMultipartInput().getInteger("age")
	 * <br>
	 * ex3) ���۵� ���ϸ� �޾ƿ��� ��� => List<FileItem> files = getMultipartInput().getFileItems()
	 * 
	 * @return ��û�Ķ������ ���� ��� �ؽ����̺�
	 */
	protected MultipartBox getMultipartInput() {
		if (this._multipartInput == null) {
			this._multipartInput = MultipartBox.getMultipartBox(getRequest());
		}
		return this._multipartInput;
	}

	/** 
	 * ��Ű���� ��� �ִ� �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex1) [ name=ȫ�浿 ]�� ��Ű�� �޾ƿ��� ��� => String name = getCookies().getString("name")
	 * <br>
	 * ex2) [ age=20 ]�� ��Ű�� �޾ƿ��� ��� => Integer age = getCookies().getInteger("age")
	 *
	 * @return ��Ű���� ��� �ؽ����̺�
	 */
	protected Box getCookies() {
		if (this._cookies == null) {
			this._cookies = Box.getBoxFromCookie(getRequest());
		}
		return this._cookies;
	}

	/** 
	 * ���䰴ü�� PrintWriter ��ü�� �����Ѵ�.
	 * <br>
	 * ex) ���信 Hello World �� ���� ��� => getOut().println("Hello World!")
	 *
	 * @return ���䰴ü�� PrintWriter ��ü
	 */
	protected PrintWriter getOut() {
		if (this._out == null) {
			try {
				this._out = getResponse().getWriter();
			} catch (IOException e) {
			}
		}
		return this._out;
	}

	/** 
	 * Action�� �ΰŰ�ü�� �����Ѵ�.
	 * ��� �α״� �ش� �ΰŸ� �̿��ؼ� ����Ͽ��� �Ѵ�.
	 * <br>
	 * ex1) ���� ������ ����� ��� => getLogger().error("...�����޽�������")
	 * <br>
	 * ex2) ����� ������ ����� ��� => getLogger().debug("...����׸޽�������")
	 *
	 * @return Action�� �ΰŰ�ü
	 */
	protected Log getLogger() {
		return Action._logger;
	}

	/**
	 * ���䰴ü�� Ŭ���̾�Ʈ���� �����ϱ� ���� ������Ÿ���� �����Ѵ�. 
	 * <br>
	 * ex1) xml������ ���� �ϴ� ��� => setContentType("text/xml; charset=utf-8")
	 * <br>
	 * ex2) �ؽ�Ʈ ������ �����ϴ� ��� => setContentType("text/plain; charset=euc-kr")
	 *
	 * @param contentType ���䰴ü�� ������ ������ Ÿ��
	 */
	protected void setContentType(String contentType) {
		getResponse().setContentType(contentType);
	}

	/** 
	 * ��û��ü�� Ű,�� �Ӽ��� �����Ѵ�.
	 * Action���� ó���� ����� �� �� �ѱ涧 ��û��ü�� �Ӽ��� �����Ͽ� ������Ѵ�.
	 * <br>
	 * ex) rs��� RecordSet ��ü�� result ��� Ű�� ��û��ü�� �����ϴ� ��� => setAttribute("result", re) 
	 *
	 * @param key �Ӽ��� Ű ���ڿ�
	 * @param value �Ӽ��� �� ��ü
	 */
	protected void setAttribute(String key, Object value) {
		getRequest().setAttribute(key, value);
	}

	/** 
	 * ���ǰ�ü�� Ű,�� �Ӽ��� �����Ѵ�.
	 * Action���� ó���� ����� ���ǿ� �����Ѵ�.
	 * <br>
	 * ex) userinfo ��� �����������ü�� userinfo ��� Ű�� ���ǰ�ü�� �����ϴ� ��� => setSessionAttribute("userinfo", userinfo)
	 *
	 * @param key �Ӽ��� Ű ���ڿ�
	 * @param value �Ӽ��� �� ��ü
	 */
	protected void setSessionAttribute(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	/** 
	 * �÷��ð�ü�� Ű,�� �Ӽ��� �����Ѵ�.
	 * Controller���� ó���� ����� ���� ��û�� ��û��ü�� �����Ѵ�.
	 * <br>
	 * ex) message ��� ��ü�� message ��� Ű�� �÷��ð�ü�� �����ϴ� ��� : setFlashAttribute("message", message)
	 * @param key �Ӽ��� Ű ���ڿ�
	 * @param value �Ӽ��� �� ��ü
	 */
	protected void setFlashAttribute(String key, Object value) {
		flash.put(key, value);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�
	private void setServlet(HttpServlet servlet) {
		this._servlet = servlet;
	}

	private void setRequest(HttpServletRequest req) {
		this._request = req;
	}

	private void setResponse(HttpServletResponse res) {
		this._response = res;
	}

	private void destroy() {
		ConnectionManager connMgr = null;
		for (String key : this._connMgrMap.keySet()) {
			connMgr = this._connMgrMap.get(key);
			if (connMgr != null) {
				connMgr.release();
				connMgr = null;
			}
		}
		this._connMgrMap.clear();
		this._input = null;
		this._out = null;
	}

	private Method getMethod(String methodName) {
		if (methodName == null || methodName.trim().equals("")) {
			methodName = "init";
		}
		StringBuilder sb = new StringBuilder(methodName);
		sb.setCharAt(0, Character.toUpperCase(methodName.charAt(0)));
		String name = "process" + sb.toString().trim();
		Method m = getMethod(this.getClass(), name);
		return m;
	}

	private static Method getMethod(Class<?> actionClass, String methodName) {
		Method method[] = actionClass.getMethods();
		for (int i = 0; i < method.length; i++) {
			if (method[i].getName().equals(methodName)) {
				return method[i];
			}
		}
		return null;
	}

	/*
	 * �÷��ð�ü�� ���ǿ� ����
	 */
	private void flashSave() {
		if (!flash.isEmpty()) {
			try {
				setSessionAttribute(_FLASH_SCOPE_OBJECT_KEY, flash);
			} catch (IllegalStateException e) {
				getLogger().error("Session State Error!");
			}
		}
	}

	/*
	 * ���ǿ��� �÷��ð�ü�� ����
	 */
	@SuppressWarnings("unchecked")
	private void flashRestore() {
		Map<String, Object> flashMap = (Map<String, Object>) getSessionAttribute(_FLASH_SCOPE_OBJECT_KEY);
		if (flashMap != null) {
			for (Entry<String, Object> entry : flashMap.entrySet()) {
				setAttribute(entry.getKey(), entry.getValue());
			}
			getSession().removeAttribute(_FLASH_SCOPE_OBJECT_KEY);
		}
	}
}