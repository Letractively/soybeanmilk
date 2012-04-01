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
 * {@linkplain Map 映射表}转换异常，它保存导致转换异常的{@linkplain Map 映射表}关键字。
 * @author earthAngry@gmail.com
 * @date 2012-3-27
 *
 */
public class MapConvertException extends ConvertException
{
	private static final long serialVersionUID = 1L;
	
	/**映射表关键字*/
	private String key;
	
	/**
	 * 创建参数转换异常对象
	 * @param key 映射表关键字
	 * @param sourceObject 转换异常时的源对象
	 * @param targetType 转换目标类型
	 * @param cause
	 */
	public MapConvertException(String key, Object sourceObject, Type targetType, Throwable cause)
	{
		super(sourceObject, targetType, cause);
		this.key=key;
	}
	
	/**
	 * 获取导致转换异常的映射表关键字
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置导致转换异常的映射表关键字
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [key=" + getKey()
				+ ", sourceObject=" + getSourceObject()
				+ ", targetType=" + getTargetType() + "]";
	}
}
