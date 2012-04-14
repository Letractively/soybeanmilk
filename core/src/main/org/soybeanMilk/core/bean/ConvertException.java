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

package org.soybeanMilk.core.bean;

import java.lang.reflect.Type;

/**
 * 转换异常。{@linkplain Converter 转换器}在执行对象转换时出现异常。
 * @author earthangry@gmail.com
 * @date 2011-1-12
 *
 */
public class ConvertException extends Exception
{
	private static final long serialVersionUID = 5534640330364525246L;
	
	private transient Object sourceObject;
	private Type targetType;
	
	public ConvertException(Object sourceObject, Type targetType)
	{
		super();
	}
	
	public ConvertException(Object sourceObject, Type targetType, Throwable cause)
	{
		super(cause);
		
		this.sourceObject=sourceObject;
		this.targetType=targetType;
	}

	/**
	 * 获取发生此异常时要转换的对象。
	 * @return
	 * @date 2011-1-13
	 */
	public Object getSourceObject() {
		return sourceObject;
	}

	public void setSourceObject(Object sourceObject) {
		this.sourceObject = sourceObject;
	}

	/**
	 * 获取发生此异常时要转换的目标类型。
	 * @return
	 * @date 2011-1-13
	 */
	public Type getTargetType() {
		return targetType;
	}

	public void setTargetType(Type targetType) {
		this.targetType = targetType;
	}

	@Override
	public String toString()
	{
		return "ConvertException [sourceObject=" + sourceObject + ", targetType=" + targetType + "]";
	}
}
