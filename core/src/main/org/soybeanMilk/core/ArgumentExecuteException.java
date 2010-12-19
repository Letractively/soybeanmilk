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

import org.soybeanMilk.core.exe.Invoke;

/**
 * 执行异常。{@linkplain Invoke 调用}方法在执行时参数非法，它的源异常是{@linkplain IllegalArgumentException}
 * @author earthAngry@gmail.com
 * @date 2010-12-19
 *
 */
public class ArgumentExecuteException extends ExecuteException
{
	private static final long serialVersionUID = 3587718359040102481L;

	public ArgumentExecuteException(IllegalArgumentException cause) {
		super(cause);
	}
	
	/**
	 * 获取源异常
	 */
	public IllegalArgumentException getCause()
	{
		return (IllegalArgumentException)getCause();
	}
}
