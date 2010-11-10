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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.resolver.ResolverFactory;

/**
 * WEB配置，除了包含父类的信息外，它还可以配置{@linkplain Invoke 调用}对象是否允许被外部访问，以及{@linkplain ExceptionHandlerInfo 异常处理器信息}
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 *
 */
public class WebConfiguration extends Configuration
{
	private static final long serialVersionUID = -6252432801625398696L;
	
	private static Log log=LogFactory.getLog(WebConfiguration.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	/**
	 * 异常处理器信息
	 */
	private ExceptionHandlerInfo exceptionHandlerInfo;
	
	public WebConfiguration()
	{
		this(null);
	}
	
	public WebConfiguration(ResolverFactory resolverFactory)
	{
		super(resolverFactory);
	}
	
	public ExceptionHandlerInfo getExceptionHandlerInfo() {
		return exceptionHandlerInfo;
	}
	public void setExceptionHandlerInfo(ExceptionHandlerInfo exceptionHandlerInfo)
	{
		this.exceptionHandlerInfo = exceptionHandlerInfo;
		
		if(_logDebugEnabled)
			log.debug("set exception handler to '"+exceptionHandlerInfo+"'");
	}
}