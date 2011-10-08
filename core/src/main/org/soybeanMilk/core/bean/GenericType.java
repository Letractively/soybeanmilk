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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛型类型元信息。<br>
 * 它封装泛型类型的持有类信息，使用者仅通过此类即可解析任何泛型对象。
 * @author earthAngry@gmail.com
 * @date 2011-9-29
 *
 */
public class GenericType implements Type
{
	private static final int TYPE_TypeVariable=1;
	private static final int TYPE_ParameterizedType=2;
	private static final int TYPE_GenericArrayType=3;
	private static final int TYPE_WildcardType=4;
	
	/**泛型类型*/
	private Type type;
	
	/**此泛型类型的持有类*/
	private Class<?> ownerClass;
	
	/**类型标识*/
	private int typeFlag;
	
	/**实际类型*/
	private Class<?> actualClass;
	
	/**参数类型*/
	private Type[] paramTypes;
	
	/**数组元素类型*/
	private Type componentType;
	
	/**
	 * 创建泛型类型元信息对象
	 * @param type 泛型类型
	 * @param ownerClass 此泛型类型的持有类
	 */
	private GenericType(Type type, Class<?> ownerClass)
	{
		super();
		this.type = type;
		this.ownerClass = ownerClass;
		
		if(this.type instanceof TypeVariable<?>)
		{
			this.typeFlag=TYPE_TypeVariable;
			
			TypeVariable<?> tv=(TypeVariable<?>)this.type;
		}
		else if(this.type instanceof ParameterizedType)
		{
			this.typeFlag=TYPE_ParameterizedType;
			
			ParameterizedType pt=(ParameterizedType)this.type;
		}
		else if(this.type instanceof GenericArrayType)
		{
			this.typeFlag=TYPE_GenericArrayType;
			
			GenericArrayType ga=(GenericArrayType)this.type;
		}
		else if(this.type instanceof WildcardType)
		{
			this.typeFlag=TYPE_WildcardType;
			
			WildcardType wt=(WildcardType)this.type;
		}
		else
			throw new IllegalArgumentException("unknown type '"+type+"'");
	}
	
	/**
	 * 获取此泛型类型
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
	 * 获取泛型类型的持有类
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
	 * 如果此泛型类型是{@linkplain ParameterizedType}，则获取它的参数类型；否则，返回<code>null</code>
	 * @return
	 * @date 2011-10-1
	 */
	public Type[] getParamTypes()
	{
		return null;
	}
	
	/**
	 * 如果此泛型类型是数组，则返回它的元素类型；否则，则返回<code>null</code>
	 * @return
	 * @date 2011-10-1
	 */
	public Type getActualComponentType()
	{
		return null;
	}
	
	/**
	 * 此泛型类型是否为数组
	 * @return
	 * @date 2011-10-1
	 */
	public boolean isArray()
	{
		return false;
	}
	
	/**
	 * 是否为{@linkplain ParameterizedType}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isParameterizedType()
	{
		return TYPE_ParameterizedType == this.typeFlag;
	}
	
	/**
	 * 是否为{@linkplain TypeVariable}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isTypeVariable()
	{
		return TYPE_TypeVariable == this.typeFlag;
	}
	
	/**
	 * 是否为{@linkplain GenericArrayType}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isGenericArrayType()
	{
		return TYPE_GenericArrayType == this.typeFlag;
	}
	
	/**
	 * 是否为{@linkplain WildcardType}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isWildcardType()
	{
		return TYPE_WildcardType == this.typeFlag;
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
	
	protected Map<TypeVariable<?>, Type> extractTypeVariableInClass(Class<?> clazz)
	{
		Map<TypeVariable<?>, Type> re=new HashMap<TypeVariable<?>, Type>();
		
		
		
		return re;
	}
	
	private static ConcurrentHashMap<String, GenericType> genericTypeCache=new ConcurrentHashMap<String, GenericType>();
	
	/**
	 * 获取泛型类型对象
	 * @param type 类型
	 * @param ownerClass 持有类
	 * @return
	 * @date 2011-10-2
	 */
	public static GenericType getGenerictType(Type type, Class<?> ownerClass)
	{
		if(type instanceof Class<?>)
			throw new IllegalArgumentException("'"+type.toString()+"' is Class type, no generic type info");
		
		GenericType re=null;
		
		String key=type.toString()+"->"+ownerClass.toString();
		
		re=genericTypeCache.get(key);
		if(re == null)
		{
			re=new GenericType(type, ownerClass);
			genericTypeCache.putIfAbsent(key, re);
		}
		
		return re;
	}
}
