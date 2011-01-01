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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * 框架内部常用类
 * @author earthAngry@gmail.com
 * @date 2010-12-31
 *
 */
public class SbmUtils
{
	/**
	 * 是否是<code>Class</code>类型
	 * @param type
	 * @return
	 */
	public static boolean isClassType(Type type)
	{
		return (type instanceof Class<?>);
	}
	
	/**
	 * 将类型收缩为<code>Class</code>
	 * @param type
	 * @return
	 */
	public static Class<?> narrowToClassType(Type type)
	{
		return (Class<?>)type;
	}
	
	/**
	 * 是否为基本类型
	 * @param type
	 * @return
	 */
	public static boolean isPrimitive(Type type)
	{
		return type!=null && (type instanceof Class<?>) && ((Class<?>)type).isPrimitive();
	}
	
	/**
	 * 对象是否<code>instanceof</code>给定的类型
	 * @param obj
	 * @param type
	 * @return
	 */
	public static boolean isInstanceOf(Object obj, Type type)
	{
		if(obj==null || type==null)
			return false;
		else if(obj instanceof Type)
			return type.getClass().isInstance(obj);
		else if(isClassType(type))
			return narrowToClassType(type).isInstance(obj);
		else
			return false;
	}
	
	/**
	 * 是否是超类
	 * @param ancestor
	 * @param descendant
	 * @return
	 */
	public static boolean isAncestorClass(Class<?> ancestor, Class<?> descendant)
	{
		return ancestor!=null && ancestor.isAssignableFrom(descendant);
	}
	
	/**
	 * 是否是数组类型
	 * @param type
	 * @return
	 */
	public static boolean isArray(Type type)
	{
		if(type == null)
			return false;
		else if(isClassType(type))
			return narrowToClassType(type).isArray();
		else if(type instanceof GenericArrayType)
			return true;
		else
			return false;
	}
	
	/**
	 * 返回基本类型的包装类型，如果不是基本类型，它将直接被返回
	 * @param type
	 * @return
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
}
