package org.soybeanMilk.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.soybeanMilk.web.os.WebObjectSource;

/**
 * {@linkplain WebObjectSource WEB对象源}工厂，{@linkplain DispatchServlet}使用它来为请求创建WEB对象源。
 * @author zangzf
 * @date 2010-12-9
 *
 */
public interface WebObjectSourceFactory
{
	/**
	 * 为请求创建WEB对象源
	 * @param request
	 * @param response
	 * @param application
	 * @return
	 */
	WebObjectSource create(HttpServletRequest request, HttpServletResponse response, ServletContext application);
}