package framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * �׺���̼� ���� ����¡ ���� ���� ���̺귯��
 */
public class PagingUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private PagingUtil() {
	}

	/**
	 * ����¡�� ���� �ʿ��� ������ �����Ѵ�.
	 * @param totcnt ��ü ���ڵ� �Ǽ�
	 * @param pagenum ���� ������ ��ȣ 
	 * @param pagesize ���������� ������ ������
	 * @param displaysize �׺���̼� ����¡ ������
	 * @return totcnt(��ü ���ڵ� �Ǽ�), pagesize(���������� ������ ������), totalpage(��ü��������), pagenum(����������), startpage(����������), endpage(��������), beforepage(����������), afterpage(����������) ������ ��� �ִ� �� ��ü
	 */
	public static Map<String, Integer> getPagingMap(int totcnt, int pagenum, int pagesize, int displaysize) {
		int beforepage = 0;
		int afterpage = 0;
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		int totalpage = totcnt / pagesize;
		if (totcnt % pagesize != 0) {
			totalpage += 1;
		}
		int startpage = (((pagenum - 1) / displaysize) * displaysize) + 1;
		int endpage = (((pagenum - 1) + displaysize) / displaysize) * displaysize;
		if (totalpage <= endpage) {
			endpage = totalpage;
		}
		if ((startpage - displaysize) > 0) {
			beforepage = ((((pagenum - displaysize) - 1) / displaysize) * displaysize) + 1;
		}
		if ((startpage + displaysize) <= totalpage) {
			afterpage = ((((pagenum + displaysize) - 1) / displaysize) * displaysize) + 1;
		}
		resultMap.put("totcnt", Integer.valueOf(totcnt));
		resultMap.put("totalpage", Integer.valueOf(totalpage));
		resultMap.put("pagenum", Integer.valueOf(pagenum));
		resultMap.put("startpage", Integer.valueOf(startpage));
		resultMap.put("endpage", Integer.valueOf(endpage));
		resultMap.put("pagesize", Integer.valueOf(pagesize));
		resultMap.put("displaysize", Integer.valueOf(displaysize));
		resultMap.put("beforepage", Integer.valueOf(beforepage));
		resultMap.put("afterpage", Integer.valueOf(afterpage));
		return resultMap;
	}
}