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

/**
 * 执行异常。{@linkplain Invoke 调用}在从{@linkplain ObjectSource 对象源}获取方法参数值时出现类型转换异常，这通常是由于输入非法导致的。
 * 它的源异常是{@linkplain ConvertException}。
 * @author earthAngry@gmail.com
 * @date 2011-1-11
 *
 */
public class ConvertExecuteException extends ExecuteException
{
	private static final long serialVersionUID = -5063626619646972164L;
	
	public ConvertExecuteException(ConvertException cause)
	{
		super(cause);
	}
	
	/**
	 * 获取源转换异常。
	 */
	public ConvertException getCause()
	{
		return (ConvertException)super.getCause();
	}
}
