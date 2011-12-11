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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
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
	
	private WebConfiguration webConfiguration;
	
	private VariablePathMatcher variablePathMatcher;
	
	public DefaultWebExecutor()
	{
		this(null);
	}

	public DefaultWebExecutor(WebConfiguration configuration)
	{
		setWebConfiguration(configuration);
		initVariablePathMatcher();
	}

	@Override
	public void setConfiguration(Configuration configuration)
	{
		setWebConfiguration((WebConfiguration)configuration);
	}
	
	public WebConfiguration getWebConfiguration()
	{
		return this.webConfiguration;
	}

	public void setWebConfiguration(WebConfiguration webConfiguration)
	{
		this.webConfiguration = webConfiguration;
		super.setConfiguration(webConfiguration);
		
		initVariablePathMatcher();
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
	 * 处理{@linkplain Executable 可执行对象}的{@linkplain Target 目标}属性，目前只有{@linkplain WebAction}定义了{@linkplain Target 目标}属性。
	 * @param executable
	 * @param webObjectSource
	 * @throws ServletException
	 * @throws IOException
	 * @date 2011-4-18
	 */
	protected void handleTarget(Executable executable, WebObjectSource webObjectSource)
			throws ServletException, IOException
	{
		if(!(executable instanceof WebAction))
			return;
		
		getWebConfiguration().getTypeTargetHandler().handleTarget((WebAction)executable, webObjectSource);
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
				if(log.isDebugEnabled())
					log.debug("find '"+re+"' that matches name '"+executableName+"'");
				
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
	 * 初始化变量路径匹配器
	 * @date 2011-4-19
	 */
	protected void initVariablePathMatcher()
	{
		if(isEnableVariablePath() && getConfiguration()!=null)
		{
			//初始化变量路径匹配器并且设为非空以便使用
			Collection<String> exeNames=getConfiguration().getExecutableNames();
			VariablePathMatcher vpm=new VariablePathMatcher(exeNames);
			setVariablePathMatcher(vpm);
		}
	}
	
	/**
	 * 是否开启变量路径功能。
	 * @return
	 */
	protected boolean isEnableVariablePath()
	{
		return true;
	}
}
