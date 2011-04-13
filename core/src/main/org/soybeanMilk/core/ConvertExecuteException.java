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

package org.soybeanMilk.core;

import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;

/**
 * 执行异常，{@linkplain Invoke 调用}在从{@linkplain ObjectSource 对象源}获取方法参数值时出现类型转换异常，这通常是由于输入非法导致的。
 * 它的源异常是{@linkplain ConvertException}。
 * @author earthAngry@gmail.com
 * @date 2011-1-11
 *
 */
public class ConvertExecuteException extends ExecuteException
{
	private static final long serialVersionUID = -5063626619646972164L;
	
	private transient Invoke invoke;
	private int argIndex;
	
	public ConvertExecuteException(Invoke invoke, int argIndex, ConvertException cause)
	{
		super(cause);
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
	 * 获取产生此异常的{@linkplain Invoke 调用}方法{@linkplain Arg 参数}。
	 * @return
	 * @date 2011-1-13
	 */
	public Arg getArg()
	{
		return invoke.getArg(this.argIndex);
	}

	/**
	 * 获取源转换异常。
	 */
	public ConvertException getCause()
	{
		return (ConvertException)super.getCause();
	}
}
