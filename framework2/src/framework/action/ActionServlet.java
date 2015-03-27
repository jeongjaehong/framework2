/** 
 * @(#)ActionServlet.java
 */
package framework.action;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.cache.Cache;
import framework.util.StringUtil;

/** 
 * ��Ʈ�ѷ� ������ �ϴ� �������� ��� Ŭ���̾�Ʈ�� ��û�� �޾� �ش� �׼��� �����Ѵ�.
 * Ȯ���ڰ� (.do)�� ����Ǵ� ��� ��û�� �� ������ ó���ϱ� ���Ͽ� web.xml ���Ͽ��� ������ �����Ͽ��� �ϸ�
 * ���� ���ý� �Ѱ��� ��ü�� ������ ���´�.  
 * ��û���� ������ �׼�Ű�� action.properties���� ActionŬ������ ã�� ��ü�� �����Ͽ� �����Ͻ� ���μ����� �����Ѵ�. 
 */
public class ActionServlet extends HttpServlet {
	private static final long serialVersionUID = -6478697606075642071L;
	private final Log _logger = LogFactory.getLog(framework.action.ActionServlet.class);

	/**
	 * ���� ��ü�� �ʱ�ȭ �Ѵ�.
	 * web.xml�� �ʱ�ȭ �Ķ���ͷ� ��ϵǾ� �ִ� action-mapping ���� ã�� ���ҽ� ������ �����ϴ� ������ �Ѵ�.
	 * 
	 * @param config ServletConfig ��ü
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ResourceBundle bundle = null;
		String[] _DEFAULT_SERVLET_NAMES = new String[] { "default", "WorkerServlet", "ResourceServlet", "FileServlet", "resin-file", "SimpleFileServlet", "_ah_default" };
		try {
			bundle = ResourceBundle.getBundle(config.getInitParameter("action-mapping"));
			String defaultServletName = StringUtil.nullToBlankString(config.getInitParameter("default-servlet-name"));
			if ("".equals(defaultServletName)) {
				for (String servletName : _DEFAULT_SERVLET_NAMES) {
					if (getServletContext().getNamedDispatcher(servletName) != null) {
						defaultServletName = servletName;
						break;
					}
				}
			}
			RequestDispatcher dispatcher = getServletContext().getNamedDispatcher(defaultServletName);
			if (dispatcher == null) {
				getLogger().info("Default Servlet�� ã�� �� �����ϴ�.");
			} else {
				getServletContext().setAttribute("default-servlet-dispatcher", dispatcher);
				getLogger().info("Default Servlet�� ã�ҽ��ϴ�. (" + defaultServletName + ")");
			}
		} catch (MissingResourceException e) {
			throw new ServletException(e);
		}
		getServletContext().setAttribute("action-mapping", bundle);
		// Cache
		Cache.init();
	}

	/**
	 * Ŭ���̾�Ʈ�� Get ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * 
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * 
	 * @exception java.io.IOException ActionServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		processRequest(request, response);
	}

	/**
	 * Ŭ���̾�Ʈ�� Post ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * 
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * 
	 * @exception java.io.IOException ActionServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		processRequest(request, response);
	}

	/**
	 * Ŭ���̾�Ʈ�� Put ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * 
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * 
	 * @exception java.io.IOException ActionServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Ŭ���̾�Ʈ�� Delete ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * 
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * 
	 * @exception java.io.IOException ActionServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String actionKey = getActionKey(request);
			String actionClassName = getActionClass(actionKey);
			Action action = null;
			if (actionClassName == null) {
				throw new PageNotFoundExeption("controller");
			} else {
				Class<?> actionClass = Class.forName(actionClassName);
				action = (Action) actionClass.newInstance();
				long currTime = 0;
				if (getLogger().isDebugEnabled()) {
					currTime = System.currentTimeMillis();
					getLogger().debug("�ڡڡ� " + request.getRemoteAddr() + " �� ���� \"" + request.getMethod() + " " + request.getRequestURI() + "\" ��û�� ���۵Ǿ����ϴ�");
					getLogger().debug("ContentLength : " + request.getContentLength() + "bytes");
				}
				action.execute(this, request, response);
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("�١١� " + request.getRemoteAddr() + " �� ���� \"" + request.getMethod() + " " + request.getRequestURI() + "\" ��û�� ����Ǿ����ϴ� | duration : " + (System.currentTimeMillis() - currTime) + "ms\n");
				}
			}
		} catch (PageNotFoundExeption e) {
			RequestDispatcher dispatcher = (RequestDispatcher) getServletContext().getAttribute("default-servlet-dispatcher");
			if (dispatcher != null) {
				dispatcher.forward(request, response);
			}
			return;
		} catch (Exception e) {
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}

	private String getActionClass(String actionKey) {
		ResourceBundle bundle = (ResourceBundle) getServletContext().getAttribute("action-mapping");
		try {
			return ((String) bundle.getObject(actionKey)).trim();
		} catch (MissingResourceException e) {
			getLogger().error("error actionkey is " + actionKey);
			getLogger().error(e.getMessage());
			return null;
		}
	}

	private String getActionKey(HttpServletRequest request) {
		String path = request.getServletPath();
		int slash = path.lastIndexOf("/");
		int period = path.lastIndexOf(".");
		if (period > 0 && period > slash) {
			path = path.substring(0, period);
			return path;
		}
		return null;
	}

	private Log getLogger() {
		return this._logger;
	}
}