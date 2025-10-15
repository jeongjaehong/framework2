package framework.filter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AccessLogFilter implements Filter {
	private static Log _logger = LogFactory.getLog(AccessLogFilter.class);

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("★★★ " + httpReq.getRemoteAddr() + " 로 부터 \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" 요청이 시작되었습니다");
			this.getLogger().debug(this.getParamString(httpReq));
			this.getLogger().debug("ContentLength : " + httpReq.getContentLength() + "bytes");
		}

		chain.doFilter(req, res);
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("★★★ " + httpReq.getRemoteAddr() + " 로 부터 \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" 요청이 종료되었습니다\n");
		}

	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	private String getParamString(HttpServletRequest req) {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0L;

		String key;
		String value;
		for (Iterator var6 = req.getParameterMap().keySet().iterator(); var6.hasNext(); buf.append(key + "=" + value)) {
			Object obj = var6.next();
			key = (String) obj;
			value = null;
			Object o = req.getParameterValues(key);
			if (o == null) {
				value = "";
			} else {
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

					for (int j = 0; j < length; ++j) {
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
			}

			if (currentRow++ > 0L) {
				buf.append(", ");
			}
		}

		buf.append(" }");
		return "Box[requestbox]=" + buf.toString();
	}

	private Log getLogger() {
		return _logger;
	}
}