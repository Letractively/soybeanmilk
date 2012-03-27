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

package org.soybeanMilk.core.exe;

import org.soybeanMilk.core.ExecuteException;

/**
 * 执行异常，{@linkplain Invoke 调用}方法在执行时内部抛出异常。
 * 它的源异常是由{@linkplain Invoke 调用}方法的编写者定义的方法异常或者方法引起的运行时异常。
 * 
 * @author earthAngry@gmail.com
 * @date 2010-12-19
 */
public class InvocationExecuteException extends ExecuteException
{
	private static final long serialVersionUID = 4055198175289241031L;
	
	private transient Invoke invoke;
	
	public InvocationExecuteException(Invoke invoke, Throwable cause)
	{
		super(cause);
		this.invoke=invoke;
	}
	
	/**
	 * 获取产生此异常的{@linkplain Invoke 调用}。
	 * @return
	 * @date 2011-1-13
	 */
	public Invoke getInvoke() {
		return invoke;
	}

	public void setInvoke(Invoke invoke) {
		this.invoke = invoke;
	}

	/**
	 * 获取源异常
	 */
	public Throwable getCause()
	{
		return super.getCause();
	}
}
