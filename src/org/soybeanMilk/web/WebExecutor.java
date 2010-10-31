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

package org.soybeanMilk.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.web.config.ExceptionHandlerInfo;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.soybeanMilk.web.os.WebObjectSource;


/**
 * WEB执行器，它将根据WEB请求执行对应的{@linkplain Executable 可执行对象}
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 */
public class WebExecutor implements Executor
{
	private static Log log=LogFactory.getLog(WebExecutor.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	private WebConfiguration webConfiguration;
	
	public WebExecutor(WebConfiguration configuration)
	{
		this.webConfiguration=configuration;
	}
	
	@Override
	public Configuration getConfiguration()
	{
		return webConfiguration;
	}
	
	public WebConfiguration getWebConfiguration() {
		return webConfiguration;
	}
	public void setWebConfiguration(WebConfiguration webConfiguration) {
		this.webConfiguration = webConfiguration;
	}
	
	@Override
	public void execute(String exeName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException
	{
		throw new UnsupportedOperationException("this method is not support here");
	}
	
	/**
	 * 执行，它根据请求查找可执行对象并执行对应的动作；如果配置了异常处理器，它还会处理可能出现的异常
	 * @param objSource WEB对象源，你不需要要设置它的{@linkplain GenericConverter 通用转换器}属性，这个方法会自动设置它
	 * @throws ServletException
	 * @throws IOException
	 * @throws ExecutableNotFoundException
	 */
	public void execute(WebObjectSource objSource)
			throws ServletException, IOException, ExecutableNotFoundException
	{
		if(objSource.getGenericConverter() == null)
			objSource.setGenericConverter(webConfiguration.getGenericConverter());
		
		HttpServletRequest request = objSource.getRequest();
		HttpServletResponse response = objSource.getResponse();
		
		if(_logDebugEnabled)
			log.debug("processing request '"+request.getServletPath()+"'");
		
		Executable exe = findRequestExecutable(request, response);
		
		try
		{
			exe.execute(objSource);
		}
		catch(ExecuteException e)
		{
			//处理异常
			ExceptionHandlerInfo hi = webConfiguration.getExceptionHandlerInfo();
			if(hi==null || hi.getExceptionHandler()==null)
				throw new ServletException(e);
			
			//存入异常对象到对象源
			objSource.set(hi.getExceptionArgKey(), e);
			
			exe = hi.getExceptionHandler();
			
			try
			{
				exe.execute(objSource);
			}
			catch(ExecuteException e1)
			{
				throw new ServletException(e1);
			}
		}
		
		processTarget(exe, request, response);
	}
	
	/**
	 * 查找处理请求的{@linkplain Executable 可执行对象}，{@link #execute(WebObjectSource)}使用这个方法来确定哪个可执行对象来处理该请求
	 * @param request
	 * @param response
	 * @return
	 * @throws ExecutableNotFoundException
	 */
	protected Executable findRequestExecutable(HttpServletRequest request, HttpServletResponse response)
			throws ExecutableNotFoundException
	{
		String servletPath=request.getServletPath();
		
		Executable exe = webConfiguration.getExecutable(servletPath);
		if(exe == null)
			throw new ExecutableNotFoundException(servletPath);
		
		return exe;
	}
	
	/**
	 * 处理可执行对象的目标
	 * @param executable
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processTarget(Executable executable,HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException
	{
		Target target = getTarget(executable);
		
		if(target == null)
		{
			if(_logDebugEnabled)
				log.debug("Executable named '"+executable.getName()+"' not dispatched,because no Target defined");
			
			return;
		}
		
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
	
	/**
	 * 返回可执行对象的{@linkplain Target 目标}，如果没有，则可以返回null（比如{@link org.soybeanMilk.core.exe.Invoke 调用}类并没有定义<i>目标</i>属性）
	 * @param exe
	 * @return
	 */
	protected Target getTarget(Executable exe)
	{
		if(exe instanceof WebAction)
			return ((WebAction)exe).getTarget();
		else
			return null;
	}
}