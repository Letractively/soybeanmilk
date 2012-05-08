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
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.exe.Invoke.Arg;

/**
 * 参数准备执行异常，{@linkplain Invoke 调用}在准备方法参数时出现异常。
 * 这通常是由于{@linkplain Invoke 调用}从{@linkplain ObjectSource 对象源}获取方法参数值时出现类型转换异常导致的。
 * 
 * @author earthangry@gmail.com
 * @date 2011-1-11
 */
public class ArgPrepareExecuteException extends ExecuteException
{
	private static final long serialVersionUID = -5063626619646972164L;
	
	/**产生此异常的调用对象*/
	private Invoke invoke;
	
	/**产生此异常的调用参数位置*/
	private int argIndex;
	
	public ArgPrepareExecuteException()
	{
		this(null, -1, null, null);
	}
	
	public ArgPrepareExecuteException(Invoke invoke, int argIndex, String message)
	{
		this(invoke, argIndex, message, null);
	}
	
	public ArgPrepareExecuteException(Invoke invoke, int argIndex, Throwable cause)
	{
		this(invoke, argIndex, null, cause);
	}
	
	public ArgPrepareExecuteException(Invoke invoke, int argIndex, String message, Throwable cause)
	{
		super(message, cause);
		
		this.invoke=invoke;
		this.argIndex=argIndex;
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
	 * 获取产生此异常的{@linkplain Invoke 调用}方法参数位置。
	 * @return
	 * @date 2011-1-13
	 */
	public int getArgIndex() {
		return argIndex;
	}
	
	public void setArgIndex(int argIndex) {
		this.argIndex = argIndex;
	}
	
	/**
	 * 获取产生此异常的{@linkplain Arg 调用参数}
	 * @return
	 * @date 2011-1-13
	 */
	public Arg getArg()
	{
		return invoke.getArgs()[this.argIndex];
	}
}
