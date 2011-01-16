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

package org.soybeanMilk;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 框架内部常用类。
 * @author earthAngry@gmail.com
 * @date 2010-12-31
 */
public class SoybeanMilkUtils
{
	/**
	 * 是否是<code>Class</code>类型
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isClassType(Type type)
	{
		return (type instanceof Class<?>);
	}
	
	/**
	 * 是否是数组类型
	 * @param type
	 * @return
	 * @date 2011-1-3
	 */
	public static boolean isArray(Class<?> type)
	{
		return type.isArray();
	}
	
	/**
	 * 将类型收缩为<code>Class</code>
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static Class<?> narrowToClassType(Type type)
	{
		return (Class<?>)type;
	}
	
	/**
	 * 是否为基本类型
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isPrimitive(Type type)
	{
		return type!=null && isClassType(type) && narrowToClassType(type).isPrimitive();
	}
	
	/**
	 * 是否是枚举类型
	 * @param type
	 * @return
	 * @date 2011-1-9
	 */
	public static boolean isEnum(Type type)
	{
		return type!=null && isClassType(type) && narrowToClassType(type).isEnum();
	}
	
	/**
	 * 对象是否<code>instanceof</code>给定的类型
	 * @param obj
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isInstanceOf(Object obj, Type type)
	{
		if(obj==null || type==null)
			return false;
		else if(isClassType(type))
			return narrowToClassType(type).isInstance(obj);
		else
			return type.getClass().isInstance(obj);
	}
	
	/**
	 * 是否是超类
	 * @param ancestor
	 * @param descendant
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isAncestorClass(Class<?> ancestor, Class<?> descendant)
	{
		return ancestor!=null && ancestor.isAssignableFrom(descendant);
	}
	
	/**
	 * 是否是{@linkplain java.lang.Class Class}类型对象的数组类型
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isClassTypeArray(Type type)
	{
		if(type == null)
			return false;
		else if(isClassType(type))
			return narrowToClassType(type).isArray();
		else
			return false;
	}
	
	/**
	 * 返回基本类型的包装类型，如果不是基本类型，它将直接被返回
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static Type toWrapperType(Type type)
	{
		if (!isPrimitive(type))
            return type;
		
		if (Integer.TYPE.equals(type))
        	return Integer.class;
        else if (Double.TYPE.equals(type))
            return Double.class;
        else if (Long.TYPE.equals(type))
            return Long.class;
        else if (Boolean.TYPE.equals(type))
            return Boolean.class;
        else if (Float.TYPE.equals(type))
            return Float.class;
        else if (Short.TYPE.equals(type))
            return Short.class;
        else if (Byte.TYPE.equals(type))
            return Byte.class;
        else if (Character.TYPE.equals(type))
            return Character.class;
        else
            return type;
	}
	
	/**
	 * 获取类型实际的{@linkplain java.lang.Class Class}类型。
	 * 如果<code>type</code>是{@linkplain java.lang.Class Class}类型，则结果是包含仅包含它一个元素的数组；
	 * 如果是{@linkplain java.lang.reflect.ParameterizedType ParameterizedType}类型，
	 * 则返回数组的第一个元素是它的原始类型，而后续的元素则是参数类型；
	 * 如果是无法识别的类型，则会抛出异常。
	 * @param type
	 * @return
	 * @date 2011-1-3
	 */
	public static Class<?>[] getActualClassTypeInfo(Type type)
	{
		Class<?>[] re=null;
		
		if(isInstanceOf(type, Class.class))
			re=new Class<?>[]{ narrowToClassType(type) };
		else if(isInstanceOf(type, ParameterizedType.class))
		{
			ParameterizedType paramType=(ParameterizedType)type;
			Type[] ats=paramType.getActualTypeArguments();
			
			if(!SoybeanMilkUtils.isClassType(paramType.getRawType()))
				throw new IllegalArgumentException("'"+type+"' is not valid, its raw type must be Class type");
			
			re=new Class<?>[1+ats.length];
			re[0]=SoybeanMilkUtils.narrowToClassType(paramType.getRawType());
			
			for(int i=0;i<ats.length;i++)
			{
				if(!isClassType(ats[i]))
					throw new IllegalArgumentException("'"+type+"' is not valid, its actual type must be Class type");
				
				re[i+1]=narrowToClassType(ats[i]);
			}
		}
		else
			throw new IllegalArgumentException("'"+type+"' is not supported type");
		
		return re;
	}
	
}
