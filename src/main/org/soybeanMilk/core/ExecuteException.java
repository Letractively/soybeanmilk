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

/**
 * 执行异常
 * @author earthAngry@gmail.com
 * @date 2010-9-30
 */
public class ExecuteException extends Exception
{
	private static final long serialVersionUID = 3370475438251845697L;
	
	private transient Executable source;
	private int exceptionType;
	
	/**
	 * 创建执行异常对象
	 * @param source 出现异常的可执行对象
	 * @param cause 源异常
	 * @param exceptionType 源异常类型
	 */
	public ExecuteException(Executable source, Throwable cause, int exceptionType)
	{
		super(cause);
		this.source=source;
		this.exceptionType=exceptionType;
	}
	
	public Executable getSource() {
		return source;
	}
	public void setSource(Executable source) {
		this.source = source;
	}

	public int getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(int exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	@Override
	public String toString()
	{
		return "ExecuteException [source=" + source + ", exceptionType="
				+ exceptionType + "]";
	}
	
	/**
	 * 导致执行异常的异常源类型
	 * @author earthAngry@gmail.com
	 * @date 2010-9-30
	 */
	public static class ExceptionType
	{
		/**
		 * 执行异常类型-IllegalAccessException
		 */
		public static final int ACCESS=1;
		/**
		 * 执行异常类型-IllegalArgumentException
		 */
		public static final int ARGUMENT=2;
		/**
		 * 执行异常类型-InvocationTargetException
		 */
		public static final int INVOCATION=3;
	}
}