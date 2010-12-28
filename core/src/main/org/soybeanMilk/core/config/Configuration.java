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

import java.util.Collection;
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
	
	
	/**解决对象工厂*/
	private ResolverFactory resolverFactory;
	
	/**通用转换器，它负责将配置文件中定义的字符串参数值转换为实际的参数对象*/
	private GenericConverter genericConverter;
	
	/**拦截器信息*/
	private InterceptorInfo interceptorInfo;
	
	/**可执行对象集*/
	private Map<String, Executable> executablesMap;
	
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
	public InterceptorInfo getInterceptorInfo() {
		return interceptorInfo;
	}
	public void setInterceptorInfo(InterceptorInfo interceptorInfo) {
		this.interceptorInfo = interceptorInfo;
	}
	
	/**
	 * 获取此配置包含的所有可执行对象集合
	 * @return
	 */
	public Collection<Executable> getExecutables()
	{
		return getExecutablesMap() == null ? null : getExecutablesMap().values();
	}
	
	/**
	 * 获取它包含的所有可执行对象名集合
	 * @return
	 */
	public Collection<String> getExecutableNames()
	{
		return getExecutablesMap()==null ? null : getExecutablesMap().keySet();
	}
	
	/**
	 * 添加集合中的所有可执行对象
	 * @param executables
	 */
	public void addExecutables(Collection<Executable> executables)
	{
		for(Executable e : executables)
			this.addExecutable(e);
	}
	
	/**
	 * 根据名称查找可执行对象
	 * @param executableName
	 * @return
	 */
	public Executable getExecutable(String executableName)
	{
		if(executableName == null)
			return null;
		
		return getExecutablesMap() == null ? null : getExecutablesMap().get(executableName);
	}
	
	/**
	 * 添加一个可执行对象
	 * @param executable
	 */
	public void addExecutable(Executable executable)
	{
		Map<String, Executable> exeMap=getExecutablesMap();
		
		if(exeMap == null)
		{
			exeMap=new HashMap<String,Executable>();
			setExecutablesMap(exeMap);
		}
		
		checkNameNotNull(executable);
		
		if(exeMap.get(executable.getName()) != null)
			throw new IllegalArgumentException("duplicate Executable name '"+executable.getName()+"'");
		
		exeMap.put(executable.getName(), executable);
		
		if(log.isDebugEnabled())
			log.debug("add '"+executable+"' to '"+this+"'");
	}
	
	public Map<String, Executable> getExecutablesMap() {
		return executablesMap;
	}
	public void setExecutablesMap(Map<String, Executable> executablesMap) {
		this.executablesMap = executablesMap;
	}

	/**
	 * 校验名称，添加到此对象中的可执行对象的名称不能为null
	 * @param exe
	 */
	protected void checkNameNotNull(Executable exe)
	{
		if(exe.getName() == null)
			throw new IllegalArgumentException("Executable.getName() must not be null.");
	}
}