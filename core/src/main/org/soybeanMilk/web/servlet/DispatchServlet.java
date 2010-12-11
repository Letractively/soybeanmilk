/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.soybeanMilk.web.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebExecutor;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.os.WebObjectSource;


/**
 * 框架整合servlet，它可以将WEB请求转给WEB执行器
 * @author earthAngry@gmail.com
 *
 */
public class DispatchServlet extends HttpServlet
{
	private static final long serialVersionUID = -1647302324862162094L;
	
	private static Log log=LogFactory.getLog(DispatchServlet.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	/**WEB执行器*/
	protected WebExecutor webExecutor;
	
	/**WEB对象源工厂*/
	protected WebObjectSourceFactory webObjectSourceFactory;
	
	/**编码*/
	protected String encoding;
	
	/**WEB执行器存储关键字*/
	protected String appExecutorKey;
	
	public DispatchServlet()
	{
		super();
	}
	
	public WebExecutor getWebExecutor() {
		return webExecutor;
	}
	public void setWebExecutor(WebExecutor webExecutor) {
		this.webExecutor = webExecutor;
	}
	public WebObjectSourceFactory getWebObjectSourceFactory() {
		return webObjectSourceFactory;
	}
	public void setWebObjectSourceFactory(
			WebObjectSourceFactory webObjectSourceFactory) {
		this.webObjectSourceFactory = webObjectSourceFactory;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getAppExecutorKey() {
		return appExecutorKey;
	}
	public void setAppExecutorKey(String appExecutorKey) {
		this.appExecutorKey = appExecutorKey;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doProcess(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doProcess(req,resp);
	}
	
	@Override
	public void destroy()
	{
		if(appExecutorKey != null)
			getServletContext().removeAttribute(appExecutorKey);
		
		this.webExecutor = null;
		super.destroy();
	}
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		//编码
		String ec=getInitEncoding();
		if(ec==null || ec.length()==0)
			ec=WebConstants.DEFAULT_ENCODING;
		this.encoding=ec;
		
		//执行器
		this.webExecutor=getInitWebExecutor();
		
		//WEB对象源工厂
		WebObjectSourceFactory wsf=getInitWebObjectSourceFactory();
		if(wsf == null)
		{
			wsf=new WebObjectSourceFactory()
			{
				@Override
				public WebObjectSource create(HttpServletRequest request, HttpServletResponse response, ServletContext application)
				{
					return new WebObjectSource(request, response, application);
				}
			};
		}
		this.webObjectSourceFactory=wsf;
		
		//执行器存储关键字
		this.appExecutorKey=getInitAppExecutorKey();
		if(appExecutorKey != null)
			getServletContext().setAttribute(appExecutorKey, webExecutor);
	}
	
	/**
	 * 处理WEB请求
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if(request.getCharacterEncoding() == null)
			request.setCharacterEncoding(encoding);
		
		try
		{
			webExecutor.execute(webObjectSourceFactory.create(request, response, getServletContext()));
		}
		catch(ExecutableNotFoundException e)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getExecutableName());
		}
	}
	
	/**
	 * 取得初始化编码
	 */
	protected String getInitEncoding() throws ServletException
	{
		return getInitParameter(WebConstants.ServletInitParams.ENCODING);
	}
	
	/**
	 * 取得执行器在应用中的存储关键字
	 */
	protected String getInitAppExecutorKey() throws ServletException
	{
		return getInitParameter(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY);
	}
	
	/**
	 * 取得初始化{@link WebExecutor WEB执行器}对象
	 */
	protected WebExecutor getInitWebExecutor() throws ServletException
	{
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.setExternalResolverFactory(getInitExternalResolverFactory());
		
		Configuration configuration=new Configuration(rf);
		
		String configFileName=getInitParameter(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG);
		
		WebConfigurationParser parser=new WebConfigurationParser(configuration,getServletContext());
		
		parser.parse(configFileName);
		
		return new WebExecutor(configuration);
	}
	
	/**
	 * 取得初始化{@linkplain WebObjectSourceFactory WEB对象源工厂}
	 */
	protected WebObjectSourceFactory getInitWebObjectSourceFactory() throws ServletException
	{
		WebObjectSourceFactory wsf=null;
		
		String clazz=getInitParameter(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY);
		if(clazz!=null && clazz.length()!=0)
		{
			try
			{
				wsf=(WebObjectSourceFactory)Class.forName(clazz).newInstance();
			}
			catch(Exception e)
			{
				throw new ServletException(e);
			}
		}
		
		return wsf;
	}
	
	/**
	 * 取得初始化外部解决对象工厂，它将被整合到框架中
	 * @return
	 */
	protected ResolverFactory getInitExternalResolverFactory() throws ServletException
	{
		String erfKey=getInitParameter(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY_KEY);
		
		ResolverFactory erf=null;
		
		if(erfKey!=null && erfKey.length()!=0)
		{
			erf=(ResolverFactory)getServletContext().getAttribute(erfKey);
			if(erf == null)
				throw new ServletException("can not find external ResolverFactory in application with key '"+erfKey+"'");
			
			if(_logDebugEnabled)
				log.debug("find external resolver factory '"+erf.getClass().getName()+"' in 'application' scope");
		}
		
		return erf;
	}
}