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

package org.soybeanMilk.web.config;

import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.restful.VariablePath;
import org.soybeanMilk.web.restful.VariablePathManager;

/**
 * Web配置信息。<br>
 * 它提供web环境特有的功能支持，比如查找名称定义为RESTful风格的{@linkplain Executable 可执行对象}
 * @author earthAngry@gmail.com
 * @date 2010-12-16
 *
 */
public class WebConfiguration extends Configuration
{
	protected VariablePathManager variablePathManager;
	
	public WebConfiguration()
	{
		this(null);
	}
	
	public WebConfiguration(ResolverFactory resolverFactory)
	{
		super(resolverFactory);
		this.variablePathManager=new VariablePathManager();
	}
	
	public VariablePathManager getVariablePathManager() {
		return variablePathManager;
	}

	@Override
	public void addExecutable(Executable exe)
	{
		super.addExecutable(exe);
		
		addVariablePath(exe.getName());
	}
	
	@Override
	public Executable getExecutable(String executableName)
	{
		Executable re = super.getExecutable(executableName);
		if(re == null)
		{
			VariablePath vp=getVariablePathMatched(executableName);
			if(vp != null)
				re=super.getExecutable(vp.getVariablePath());
		}
		
		return re;
	}
	
	/**
	 * 查找与给定可执行对象名称匹配的变量路径
	 * @param executbaleName
	 * @return
	 */
	public VariablePath getVariablePathMatched(String executbaleName)
	{
		return variablePathManager==null ? null : variablePathManager.getMatched(executbaleName);
	}
	
	protected boolean addVariablePath(String variablePath)
	{
		VariablePath vp=new VariablePath(variablePath);
		
		if(!vp.isVariablePath())
			return false;
		
		this.variablePathManager.addVariablePath(vp);
		
		return true;
	}
}
