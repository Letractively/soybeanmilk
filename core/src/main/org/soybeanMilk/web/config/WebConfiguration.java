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

import java.util.Collection;
import java.util.SortedSet;

import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.config.restful.VariablePath;
import org.soybeanMilk.web.config.restful.VariablePathManager;

/**
 * Web配置信息。<br>
 * 它提供web环境特有的功能支持，比如RESTful风格的{@linkplain Executable 可执行对象}查找
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
		checkNameNotNull(exe);
		
		if(!addVariableNameExecutable(exe))
			super.addExecutable(exe);
	}
	
	@Override
	public void addExecutables(Collection<Executable> executables)
	{
		super.addExecutables(executables);
	}
	
	@Override
	public Collection<Executable> getExecutables()
	{
		return super.getExecutables();
	}
	
	@Override
	public Executable getExecutable(String name)
	{
		Executable re = super.getExecutable(name);
		if(re == null)
			re = (VariableNameExecutable)this.variablePathManager.getMatched(name);
		
		return re;
	}
	
	protected boolean addVariableNameExecutable(Executable executable)
	{
		VariableNameExecutable vne=new VariableNameExecutable(executable);
		if(!vne.isVariablePath())
			return false;
		
		this.variablePathManager.addVariablePath(vne);
		
		return true;
	}
	
	protected static class VariableNameExecutable extends VariablePath implements Executable
	{
		private static final long serialVersionUID = -6535341086644875191L;
		
		private Executable executable;
		
		public VariableNameExecutable(Executable executable)
		{
			super(executable.getName());
		}
		
		public Executable getExecutable() {
			return executable;
		}

		@Override
		public void execute(ObjectSource objectSource) throws ExecuteException
		{
			this.executable.execute(objectSource);
		}

		@Override
		public String getName()
		{
			return this.executable.getName();
		}

		@Override
		public String toString()
		{
			return this.executable.toString();
		}
	}
}
