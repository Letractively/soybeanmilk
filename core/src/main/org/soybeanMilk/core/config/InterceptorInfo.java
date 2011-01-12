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

import java.io.Serializable;

import org.soybeanMilk.core.Executable;

/**
 * 框架执行拦截器信息，你可以定义执行前切点处理器、后切点处理器，以及异常处理器
 * @author earthAngry@gmail.com
 * @date 2010-11-27
 */
public class InterceptorInfo implements Serializable
{
	private static final long serialVersionUID = -9129725997833969256L;
	
	/**前切点处理器*/
	private Executable beforeHandler;
	/**后切点处理器*/
	private Executable afterHandler;
	/**执行异常处理器*/
	private Executable exceptionHandler;
	/**执行语境对象保存关键字*/
	private Serializable executionKey;
	
	public InterceptorInfo()
	{
		super();
	}
	
	public InterceptorInfo(Executable beforeHandler, Executable afterHandler,
			Executable exceptionHandler, Serializable executionKey)
	{
		super();
		this.beforeHandler = beforeHandler;
		this.afterHandler = afterHandler;
		this.exceptionHandler = exceptionHandler;
		this.executionKey = executionKey;
	}
	
	public Executable getBeforeHandler() {
		return beforeHandler;
	}
	public void setBeforeHandler(Executable beforeHandler) {
		this.beforeHandler = beforeHandler;
	}
	public Executable getAfterHandler() {
		return afterHandler;
	}
	public void setAfterHandler(Executable afterHandler) {
		this.afterHandler = afterHandler;
	}
	public Executable getExceptionHandler() {
		return exceptionHandler;
	}
	public void setExceptionHandler(Executable exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	public Serializable getExecutionKey() {
		return executionKey;
	}
	public void setExecutionKey(Serializable executionKey) {
		this.executionKey = executionKey;
	}

	@Override
	public String toString()
	{
		return "InterceptorInfo [beforeHandler=" + beforeHandler
				+ ", afterHandler=" + afterHandler + ", exceptionHandler="
				+ exceptionHandler + ", executionKey=" + executionKey
				+ "]";
	}
}
