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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.exe.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.exe.resolver.ResolverFactory;
import org.soybeanMilk.web.DefaultWebExecutor;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebExecutor;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.exe.th.AbstractTargetHandler;
import org.soybeanMilk.web.os.DefaultWebObjectSource;

/**
 * 框架整合servlet，它可以将WEB请求转给{@linkplain WebExecutor Web执行器}。
 * @author earthangry@gmail.com
 * @date 2010-12-28
 */
public class DispatchServlet extends HttpServlet
{
	private static final long serialVersionUID = -1647302324862162094L;
	
	private static Log log=LogFactory.getLog(DispatchServlet.class);
	
	/**Web执行器*/
	private WebExecutor webExecutor;
	
	/**WEB对象源工厂*/
	private WebObjectSourceFactory webObjectSourceFactory;
	
	/**编码*/
	private String encoding;
	
	/**WEB执行器存储关键字*/
	private String appExecutorKey;
	
	public DispatchServlet()
	{
		super();
	}
	
	public WebExecutor getWebExecutor()
	{
		return webExecutor;
	}
	public void setWebExecutor(WebExecutor webExecutor)
	{
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
	
	//@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doProcess(req,resp);
	}
	
	//@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doProcess(req,resp);
	}
	
	//@Override
	public void destroy()
	{
		String aek=getAppExecutorKey();
		if(aek != null)
			getServletContext().removeAttribute(aek);
		
		setWebExecutor(null);
		super.destroy();
	}
	
	//@Override
	public void init() throws ServletException
	{
		if(log.isInfoEnabled())
			log.info("start  initializing SoybeanMilk web context");
		
		super.init();
		
		//编码
		String ec=getInitEncoding();
		if(ec==null || ec.length()==0)
			ec=WebConstants.DEFAULT_ENCODING;
		setEncoding(ec);
		
		//执行器
		setWebExecutor(getInitWebExecutor());
		
		if(log.isInfoEnabled())
		{
			int totalExecutables= getWebExecutor().getWebConfiguration().getExecutables() == null ? 0 : getWebExecutor().getWebConfiguration().getExecutables().size();
			log.info(totalExecutables+" executables has been initialized");
		}
		
		//WEB对象源工厂
		WebObjectSourceFactory wsf=getInitWebObjectSourceFactory();
		if(wsf == null)
		{
			wsf=new WebObjectSourceFactory()
			{
				//@Override
				public WebObjectSource create(HttpServletRequest request, HttpServletResponse response, ServletContext application)
				{
					return new DefaultWebObjectSource(request, response, application);
				}
			};
		}
		setWebObjectSourceFactory(wsf);
		
		//执行器存储关键字
		setAppExecutorKey(getInitAppExecutorKey());
		String aek=getAppExecutorKey();
		if(aek==null || aek.length()==0)
			setAppExecutorKey(null);
		if(aek != null)
			getServletContext().setAttribute(aek, getWebExecutor());
		
		if(log.isInfoEnabled())
			log.info("finish initializing SoybeanMilk web context");
	}
	
	/**
	 * 处理WEB请求。
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if(request.getCharacterEncoding() == null)
			request.setCharacterEncoding(getEncoding());
		
		String exeName=getRequestExecutableName(request, response);
		
		if(log.isDebugEnabled())
			log.debug("processing request '"+exeName+"'");
		
		WebObjectSource webObjSource=getWebObjectSourceFactory().create(request, response, getServletContext());
		
		try
		{
			getWebExecutor().execute(exeName, webObjSource);
		}
		catch(ExecutableNotFoundException e)
		{
			handleExecutableNotFound(exeName, webObjSource);
		}
		catch(ExecuteException e)
		{
			handleExecuteException(e, exeName, webObjSource);
		}
	}
	
	/**
	 * 取得用于处理请求的可执行对象名。
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected String getRequestExecutableName(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String result=null;
		
		String pathInfo=null;
		String servletPath=null;
		
		//include请求
		pathInfo=(String)request.getAttribute(AbstractTargetHandler.INCLUDE_PATH_INFO_ATTRIBUTE);
		servletPath=(String)request.getAttribute(AbstractTargetHandler.INCLUDE_SERVLET_PATH_ATTRIBUTE);
		if(pathInfo==null && servletPath==null)
		{
			pathInfo=request.getPathInfo();
			servletPath=request.getServletPath();
		}
		
		if(servletPath == null)
			servletPath="";
		
		int period=servletPath.lastIndexOf(".");
		
		if(period>=0 && period>servletPath.lastIndexOf("/"))
			result=servletPath;
		else
		{
			if(pathInfo != null)
				result=servletPath+pathInfo;
			else
				result=servletPath;
		}
		
		return result;
	}
	
	/**
	 * 没有找到能够处理请求的可执行对象。
	 * @param requestExeName
	 * @param objSource
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleExecutableNotFound(String requestExeName, WebObjectSource objSource)
			throws ServletException, IOException
	{
		//servlet规范规定这里要抛出FileNotFoundException异常
		if(AbstractTargetHandler.isJspIncludeRequest(objSource.getRequest()))
			throw new FileNotFoundException(requestExeName);
		
		objSource.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, requestExeName);
	}
	
	/**
	 * 处理执行异常。
	 * @param e
	 * @param requestExeName
	 * @param webObjSource
	 * @throws ServletException
	 * @throws IOException
	 * @date 2011-1-12
	 */
	protected void handleExecuteException(ExecuteException e, String requestExeName, WebObjectSource webObjSource)
			throws ServletException, IOException
	{
		throw new ServletException(e);
	}
	
	/**
	 * 取得初始化编码。
	 */
	protected String getInitEncoding() throws ServletException
	{
		return getInitParameter(WebConstants.ServletInitParams.ENCODING);
	}
	
	/**
	 * 取得执行器在应用中的存储关键字。
	 */
	protected String getInitAppExecutorKey() throws ServletException
	{
		return getInitParameter(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY);
	}
	
	/**
	 * 取得初始化{@link WebExecutor Web执行器}对象。
	 */
	protected WebExecutor getInitWebExecutor() throws ServletException
	{
		DefaultWebExecutor we=null;
		
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.setExternalResolverFactory(getInitExternalResolverFactory());
		
		WebConfiguration webConfiguration=new WebConfiguration(rf);
		
		String configFileName=getInitParameter(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG);
		
		WebConfigurationParser parser=new WebConfigurationParser(webConfiguration,getServletContext());
		
		parser.parse(configFileName);
		
		we=new DefaultWebExecutor(webConfiguration);
		
		return we;
	}
	
	/**
	 * 取得初始化{@linkplain WebObjectSourceFactory WEB对象源工厂}。
	 */
	protected WebObjectSourceFactory getInitWebObjectSourceFactory() throws ServletException
	{
		WebObjectSourceFactory wsf=null;
		
		String clazz=getInitParameter(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS);
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
	 * 取得初始化外部解决对象工厂，它将被整合到框架中。
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
			
			if(log.isDebugEnabled())
				log.debug("find external resolver factory '"+erf.getClass().getName()+"' in 'application' scope");
		}
		
		return erf;
	}
}