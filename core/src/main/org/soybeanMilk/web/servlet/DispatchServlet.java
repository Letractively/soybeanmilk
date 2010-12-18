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
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.soybeanMilk.web.os.PathWebObjectSource;
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.restful.PathNode;
import org.soybeanMilk.web.restful.VariablePath;
import org.soybeanMilk.web.restful.VariablePathMatcher;


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
	
	/**执行器*/
	private Executor executor;
	
	/**WEB对象源工厂*/
	private WebObjectSourceFactory webObjectSourceFactory;
	
	/**编码*/
	private String encoding;
	
	/**WEB执行器存储关键字*/
	private String appExecutorKey;
	
	/**
	 * 用于查找RESTful风格的可执行对象名
	 */
	private VariablePathMatcher variablePathMatcher;
	
	public DispatchServlet()
	{
		super();
	}
	
	public Executor getExecutor() {
		return executor;
	}
	public void setExecutor(Executor executor) {
		this.executor = executor;
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
		
		this.executor = null;
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
		this.executor=getInitExecutor();
		
		//WEB对象源工厂
		WebObjectSourceFactory wsf=getInitWebObjectSourceFactory();
		if(wsf == null)
		{
			wsf=new WebObjectSourceFactory()
			{
				@Override
				public WebObjectSource create(HttpServletRequest request, HttpServletResponse response, ServletContext application)
				{
					return new PathWebObjectSource(request, response, application);
				}
			};
		}
		this.webObjectSourceFactory=wsf;
		
		//执行器存储关键字
		this.appExecutorKey=getInitAppExecutorKey();
		if(this.appExecutorKey==null || this.appExecutorKey.length()==0)
			this.appExecutorKey=null;
		if(appExecutorKey!=null)
			getServletContext().setAttribute(appExecutorKey, executor);
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
		
		Configuration cfg=this.executor.getConfiguration();
		WebObjectSource webObjSource=webObjectSourceFactory.create(request, response, getServletContext());
		
		String exeName=getRequestExecutableName(request, response);
		Executable exe=cfg.getExecutable(exeName);
		
		//按照变量路径方式匹配
		if(exe == null)
		{
			String[] valuePath=VariablePath.splitPath(exeName);
			VariablePath vp=getVariablePathMatcher().getMatched(valuePath);
			if(vp != null)
				exe=cfg.getExecutable(vp.getVariablePath());
			
			if(exe != null)
			{
				PathNode[] pathNodes=vp.getPathNodes();
				for(int i=0;i<pathNodes.length;i++)
				{
					if(pathNodes[i].isVariable())
						webObjSource.set(PathWebObjectSource.SCOPE_PATH+WebConstants.ACCESSOR+pathNodes[i].getNodeValue(), valuePath[i]);
				}
			}
		}
		
		if(exe == null)
		{
			handleExecutableNotFound(exeName, request, response);
			return;
		}
		
		try
		{
			exe=executor.execute(exe, webObjSource);
		}
		catch(ExecuteException e)
		{
			throw new ServletException(e);
		}
		
		processTarget(exe, webObjSource);
	}
	
	/**
	 * 取得用于处理请求的可执行对象名
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected String getRequestExecutableName(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		return request.getServletPath();
	}
	
	/**
	 * 没有找到能够处理请求的可执行对象
	 * @param executableName
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleExecutableNotFound(String executableName, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_NOT_FOUND, executableName);
	}
	
	/**
	 * 处理可执行对象的目标
	 * @param executable
	 * @param objSource
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processTarget(Executable executable, WebObjectSource objSource) throws ServletException, IOException
	{
		Target target=null;
		if(executable instanceof WebAction)
			target = ((WebAction)executable).getTarget();
		
		if(target == null)
		{
			if(_logDebugEnabled)
				log.debug("Executable named '"+executable.getName()+"' not dispatched,because no Target defined");
			
			return;
		}
		
		HttpServletRequest request = objSource.getRequest();
		HttpServletResponse response=objSource.getResponse();
		
		if(Target.REDIRECT.equalsIgnoreCase(target.getType()))
		{
			//在语境内
			if(target.getUrl().startsWith("/"))
				response.sendRedirect(request.getContextPath()+target.getUrl());
			else
				response.sendRedirect(target.getUrl());
			
			if(_logDebugEnabled)
				log.debug("redirect request to '"+target.getUrl()+"'");
		}
		else
		{
			request.getRequestDispatcher(target.getUrl()).forward(request, response);
			
			if(_logDebugEnabled)
				log.debug("forward request to '"+target.getUrl()+"'");
		}
	}
	
	protected VariablePathMatcher getVariablePathMatcher()
	{
		return this.variablePathMatcher;
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
	 * 取得初始化{@link Executor 执行器}对象
	 */
	protected Executor getInitExecutor() throws ServletException
	{
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.setExternalResolverFactory(getInitExternalResolverFactory());
		
		Configuration webConfiguration=new Configuration(rf);
		
		String configFileName=getInitParameter(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG);
		
		WebConfigurationParser parser=new WebConfigurationParser(webConfiguration,getServletContext());
		
		parser.parse(configFileName);
		
		return new DefaultExecutor(webConfiguration);
	}
	
	/**
	 * 取得初始化{@linkplain WebObjectSourceFactory WEB对象源工厂}
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