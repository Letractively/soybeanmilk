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
 * 执行拦截器集，它包含执行前切点拦截器、后切点拦截器，以及异常拦截器
 * @author earthangry@gmail.com
 * @date 2010-11-27
 */
public class Interceptor implements Serializable
{
	private static final long serialVersionUID = -9129725997833969256L;
	
	/**前切点处理器*/
	private Executable before;
	
	/**后切点处理器*/
	private Executable after;
	
	/**执行异常处理器*/
	private Executable exception;
	
	/**执行语境对象保存关键字*/
	private Serializable executionKey;
	
	public Interceptor()
	{
		super();
	}
	
	public Interceptor(Executable before, Executable after,
			Executable exception, Serializable executionKey)
	{
		super();
		this.before = before;
		this.after = after;
		this.exception = exception;
		this.executionKey = executionKey;
	}
	
	public Executable getBefore() {
		return before;
	}
	public void setBefore(Executable before) {
		this.before = before;
	}
	public Executable getAfter() {
		return after;
	}
	public void setAfter(Executable after) {
		this.after = after;
	}
	public Executable getException() {
		return exception;
	}
	public void setException(Executable exception) {
		this.exception = exception;
	}
	public Serializable getExecutionKey() {
		return executionKey;
	}
	public void setExecutionKey(Serializable executionKey) {
		this.executionKey = executionKey;
	}

	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [before=" + before
				+ ", after=" + after + ", exception="
				+ exception + ", executionKey=" + executionKey
				+ "]";
	}
}
