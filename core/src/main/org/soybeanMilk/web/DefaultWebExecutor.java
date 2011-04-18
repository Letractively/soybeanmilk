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
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
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
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.vp.PathNode;
import org.soybeanMilk.web.vp.VariablePath;
import org.soybeanMilk.web.vp.VariablePathMatcher;

/**
 * {@linkplain WebExecutor Web执行器}的默认实现。
 * @author earthAngry@gmail.com
 * @date 2011-4-18
 *
 */
public class DefaultWebExecutor extends DefaultExecutor implements WebExecutor
{
	private static Log log=LogFactory.getLog(DefaultWebExecutor.class);
	
	/** servlet规范"include"属性-request_uri */
	public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
	/** servlet规范"include"属性-path_info */
	public static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";
	/** servlet规范"include"属性-servlet_path */
	public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
	
	/** servlet规范"forward"属性-path_info */
	public static final String FORWARD_PATH_INFO_ATTRIBUTE = "javax.servlet.forward.path_info";
	/** servlet规范"forward"属性-servlet_path */
	public static final String FORWARD_SERVLET_PATH_ATTRIBUTE = "javax.servlet.forward.servlet_path";
	
	private VariablePathMatcher variablePathMatcher;
	
	public DefaultWebExecutor()
	{
		this(null);
	}

	public DefaultWebExecutor(Configuration configuration)
	{
		super(configuration);
		
		if(isEnableVariablePath() && getConfiguration()!=null)
		{
			//初始化变量路径匹配器并且设为非空以便使用
			Collection<String> exeNames=getConfiguration().getExecutableNames();
			VariablePathMatcher vpm=new VariablePathMatcher(exeNames);
			setVariablePathMatcher(vpm);
		}
	}

	/**
	 * 获取此执行器使用的{@linkplain VariablePathMatcher 变量路径匹配器}。
	 * @return
	 * @date 2011-4-18
	 */
	public VariablePathMatcher getVariablePathMatcher()
	{
		return variablePathMatcher;
	}

	protected void setVariablePathMatcher(VariablePathMatcher variablePathMatcher)
	{
		this.variablePathMatcher = variablePathMatcher;
	}
	
	public void execute(String executableName, WebObjectSource webObjSource)
			throws ExecuteException, ExecutableNotFoundException, ServletException, IOException
	{
		Executable re=super.execute(executableName, webObjSource);
		
		handleTarget(re, webObjSource);
	}
	
	/**
	 * 处理{@linkplain Executable 可执行对象}的目标属性。目前只有{@linkplain WebAction}定义了目标属性。
	 * @param executable
	 * @param webObjectSource
	 * @throws ServletException
	 * @throws IOException
	 * @date 2011-4-18
	 */
	protected void handleTarget(Executable executable, WebObjectSource webObjectSource)
			throws ServletException, IOException
	{
		Target target=null;
		if(executable instanceof WebAction)
			target = ((WebAction)executable).getTarget();
		
		if(target == null)
		{
			if(log.isDebugEnabled())
				log.debug("Executable named '"+executable.getName()+"' not dispatched, because no Target defined");
			
			return;
		}
		
		HttpServletRequest request = webObjectSource.getRequest();
		HttpServletResponse response=webObjectSource.getResponse();
		
		String url=evaluateVariableUrl(target.getUrl(), webObjectSource);
		if(url == null)
			throw new ServletException("the target url of '"+executable+"' must be defined.");
		
		if(Target.REDIRECT.equalsIgnoreCase(target.getType()))
		{
			//在语境内
			if(url.startsWith("/"))
				response.sendRedirect(request.getContextPath()+url);
			else
				response.sendRedirect(url);
			
			if(log.isDebugEnabled())
				log.debug("redirect '"+url+"' for request");
		}
		else
		{
			if(isIncludeRequest(request))
			{
				request.getRequestDispatcher(url).include(request, response);
				
				if(log.isDebugEnabled())
					log.debug("include '"+url+"' for request");
			}
			else
			{
				request.getRequestDispatcher(url).forward(request, response);
				if(log.isDebugEnabled())
					log.debug("forward '"+url+"' for request");
			}
		}
	}

	@Override
	protected Executable findExecutable(String executableName, ObjectSource objSource)
			throws ExecuteException
	{
		Executable re=super.findExecutable(executableName, objSource);
		if(re==null && isEnableVariablePath())
		{
			VariablePath valuePath=new VariablePath(executableName);
			VariablePath targetPath=getVariablePathMatcher().getMatched(valuePath);
			if(targetPath != null)
				re=getConfiguration().getExecutable(targetPath.getVariablePath());
			
			if(re != null)
			{
				PathNode[] pathNodes=targetPath.getPathNodes();
				for(int i=0;i<pathNodes.length;i++)
				{
					if(pathNodes[i].isVariable())
						objSource.set(pathNodes[i].getNodeValue(), valuePath.getPathNode(i).getNodeValue());
				}
			}
		}
		
		return re;
	}
	
	/**
	 * 求变量URL的值
	 * @param variableUrl 目标URL，它可能包含"{...}"格式的变量
	 * @param objectSource
	 * @return
	 */
	protected String evaluateVariableUrl(String variableUrl, WebObjectSource objectSource)
	{
		if(variableUrl == null)
			return null;
		
		StringBuffer result=new StringBuffer();
		
		int i=0, len=variableUrl.length();
		for(;i<len;i++)
		{
			char c=variableUrl.charAt(i);
			
			if(c == WebConstants.VARIABLE_QUOTE_LEFT)
			{
				int j=i+1;
				int start=j;
				for(;j<len && (c=variableUrl.charAt(j))!=WebConstants.VARIABLE_QUOTE_RIGHT;)
					j++;
				
				String var=variableUrl.substring(start, j);
				if(c == WebConstants.VARIABLE_QUOTE_RIGHT)
				{
					String value=(String)objectSource.get(var, String.class);
					result.append(value==null ? "null" : value);
				}
				else
				{
					result.append(WebConstants.VARIABLE_QUOTE_LEFT);
					result.append(var);
				}
				
				i=j;
			}
			else
				result.append(c);
		}
		
		return result.toString();
	}
	
	/**
	 * 是否是"include"请求
	 * @param request
	 * @return
	 */
	protected boolean isIncludeRequest(ServletRequest request) {
		return (request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE) != null);
	}

	/**
	 * 是否开启变量路径功能
	 * @return
	 */
	protected boolean isEnableVariablePath()
	{
		return true;
	}
}
