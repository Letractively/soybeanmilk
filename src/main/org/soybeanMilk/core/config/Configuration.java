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

package org.soybeanMilk.core.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.resolver.ResolverFactory;

/**
 * 配置，它包含执行所需的环境信息，比如{@linkplain ResolverFactory 解决对象工厂}、{@linkplain GenericConverter 通用转换器}，
 * 以及{@linkplain Executable 可执行对象}集
 * @author earthAngry@gmail.com
 * @date 2010-10-1
 *
 */
public class Configuration
{
	private static Log log=LogFactory.getLog(Configuration.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	/**解决对象工厂*/
	private ResolverFactory resolverFactory;
	
	/**通用转换器，它负责将配置文件中定义的字符串参数值转换为实际的参数对象*/
	private GenericConverter genericConverter;
	
	/**可执行对象集*/
	private Map<String, Executable> executables;
	
	public Configuration()
	{
		this(null);
	}
	
	public Configuration(ResolverFactory resolverFactory)
	{
		this.resolverFactory = resolverFactory;
	}
	
	public ResolverFactory getResolverFactory() {
		return resolverFactory;
	}
	public void setResolverFactory(ResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}
	public GenericConverter getGenericConverter() {
		return genericConverter;
	}
	public void setGenericConverter(GenericConverter genericConverter) {
		this.genericConverter = genericConverter;
	}
	public Map<String, Executable> getExecutables() {
		return executables;
	}
	public void setExecutables(Map<String, Executable> executables) {
		this.executables = executables;
	}
	
	/**
	 * 根据名称查找可执行对象
	 * @param name
	 * @return
	 */
	public Executable getExecutable(String name)
	{
		return executables == null ? null : executables.get(name);
	}
	
	/**
	 * 添加一个可执行对象
	 * @param exe
	 */
	public void addExecutable(Executable exe)
	{
		if(executables == null)
			executables=new HashMap<String,Executable>();
		
		if(exe.getName() == null)
			throw new IllegalArgumentException("Executable.getName() must not be null.");
		
		if(executables.get(exe.getName()) != null)
			throw new IllegalArgumentException("duplicate Executable name '"+exe.getName()+"'");
		
		executables.put(exe.getName(), exe);
		
		if(_logDebugEnabled)
			log.debug("add '"+exe+"' to '"+this+"'");
	}
}