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
import java.lang.reflect.Array;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.soybeanMilk.web.os.PathWebObjectSource;
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.restful.PathNode;
import org.soybeanMilk.web.restful.VariablePath;


/**
 * WEB执行器，它将根据WEB请求执行对应的{@linkplain Executable 可执行对象}
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 */
public class WebExecutor extends DefaultExecutor
{
	private static Log log=LogFactory.getLog(WebExecutor.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	public WebExecutor(Configuration configuration)
	{
		super(configuration);
	}

	@Override
	public Executable execute(String exeName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException
	{
		return super.execute(exeName, objSource);
	}
	
	/**
	 * 执行。它的核心处理逻辑与{@linkplain #execute(String, ObjectSource)}一样，
	 * 只不过它会把{@linkplain HttpServletRequest#getServletPath()}返回的字符串作为可执行对象名，
	 * 并且它还会处理可能的{@linkplain Target 目标}分发。
	 * @param objSource WEB对象源，你不需要要设置它的{@linkplain GenericConverter 通用转换器}属性，这个方法会自动设置它
	 * @throws ServletException
	 * @throws IOException
	 * @throws ExecutableNotFoundException
	 */
	public void execute(WebObjectSource objSource)
			throws ServletException, IOException, ExecutableNotFoundException
	{
		HttpServletRequest request = objSource.getRequest();
		String servletPath=request.getServletPath();
		
		Executable exe=findExecutable(servletPath, objSource);
		if(exe == null)
			throw new ExecutableNotFoundException(servletPath);
		
		try
		{
			Executable finalExe=execute(exe, objSource);
			
			processTarget(finalExe, objSource);
		}
		catch(ExecuteException e)
		{
			throw new ServletException(e);
		}
	}
	
	/**
	 * 处理可执行对象的目标
	 * @param executable
	 * @param objSource
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void processTarget(Executable executable, WebObjectSource objSource) throws ServletException, IOException
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
}