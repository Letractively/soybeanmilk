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
 * 泛型类型元信息。<br>
 * 它封装泛型类型所属的类信息，使用者仅通过此类即可解析任何泛型对象。
 * @author earthAngry@gmail.com
 * @date 2011-9-29
 *
 */
public class GenericType implements Type
{
	/**泛型类型*/
	private Type type;
	
	/**此泛型类型所属的类*/
	private Class<?> ownerClass;
	
	/**
	 * 创建泛型类型元信息对象
	 * @param type 泛型类型
	 * @param ownerClass 此泛型类型的拥有类
	 */
	public GenericType(Type type, Class<?> ownerClass)
	{
		super();
		this.type = type;
		this.ownerClass = ownerClass;
	}
	
	/**
	 * 获取泛型类型
	 * @return
	 * @date 2011-10-1
	 */
	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	/**
	 * 获取泛型类型的拥有类
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?> getOwnerClass()
	{
		return ownerClass;
	}
	
	public void setOwnerClass(Class<?> ownerClass)
	{
		this.ownerClass = ownerClass;
	}
	
	/**
	 * 获取泛型类型的实际类
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?> getActualClass()
	{
		return null;
	}
	
	/**
	 * 获取泛型类型的参数类，如果没有，则返回<code>null</code>
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?>[] getActualParamClass()
	{
		return null;
	}
	
	/**
	 * 如果此泛型类型是数组，则返回元素类型；如果不是数组，则返回<code>null</code>
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?> getActualComponentClass()
	{
		return null;
	}
	
	/**
	 * 此泛型类型是否为数组
	 * @return
	 * @date 2011-10-1
	 */
	public boolean isArrayType()
	{
		return false;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ownerClass == null) ? 0 : ownerClass.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericType other = (GenericType) obj;
		if (ownerClass == null) {
			if (other.ownerClass != null)
				return false;
		} else if (!ownerClass.equals(other.ownerClass))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
