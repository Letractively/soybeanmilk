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

package org.soybeanMilk.web.bean;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericType;
import org.soybeanMilk.core.bean.PropertyInfo;

/**
 * WEB通用转换器，除了继承的转换支持，它还支持将请求参数映射表（{@link Map Map&lt;String, String[]&gt;}）对象转换为JavaBean对象、JavaBean数组以及JavaBean集合（List、Set）。<br>
 * 比如可以将下面的请求参数映射表：
 * <pre>
 * "id"                 -&gt;  "1" 或者 ["1"] 或者 ["1", "2"]（第一个元素"1"将被使用）
 * "name"               -&gt;  "jack" 或者 ["jack"] 或者 ["jack", "lily"]（第一个元素"jack"将被使用）
 * "listChildren.id"    -&gt;  ["11", "12"]
 * "listChildren.name"  -&gt;  ["tom", "mary"]
 * "setChildren.id"     -&gt;  ["11", "12"]
 * "setChildren.name"   -&gt;  ["tom", "mary"]
 * "arrayChildren.id"   -&gt;  ["11", "12"]
 * "arrayChildren.name" -&gt;  ["tom", "mary"]
 * "ignored"            -&gt;  "this value will be ignored"
 * </pre>
 * 转换为：
 * <pre>
 * class User{
 * 	private Integer id;
 * 	private String name;
 * 	private List&lt;User&gt; listChildren;
 * 	private Set&lt;User&gt; setChildren;
 * 	private User[] arrayChildren;
 * 	...
 * }
 * </pre>
 * 类型的对象。<br>
 * 或者将：
 * <pre>
 * "id"                 -&gt;  ["1","2","3"]
 * "name"               -&gt;  ["jack","tom","cherry"]
 * </pre>
 * 转换为：
 * <pre>
 * List&lt;User&gt;
 * Set&lt;User&gt;
 * User[]
 * </pre>
 * 类型的对象。
 * @author earthAngry@gmail.com
 * @date 2010-10-8
 */
public class WebGenericConverter extends DefaultGenericConverter
{
	private static Log log = LogFactory.getLog(WebGenericConverter.class);
	
	public WebGenericConverter()
	{
		super();
	}
	
	public WebGenericConverter(boolean initDefaultSupportConverter)
	{
		super(initDefaultSupportConverter);
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	protected Object convertWhenNoSupportConverter(Object sourceObj, Type targetType)
	{
		Object result=null;
		
		//如果源对象是数组而目标类型不是，则使用数组的第一个元素转换，与request.getParameter(...)规则相同
		if(SoybeanMilkUtils.isArray(sourceObj.getClass()) && SoybeanMilkUtils.isClassType(targetType)
				&& !SoybeanMilkUtils.isArray((Class<?>)targetType))
		{
			if(log.isDebugEnabled())
				log.debug("'"+sourceObj+"' is an array while the target not, so it's first element will be used for converting");
			
			sourceObj=Array.get(sourceObj, 0);
			
			result=convert(sourceObj, targetType);
		}
		else if(SoybeanMilkUtils.isInstanceOf(sourceObj, ParamValue.class))
		{
			ParamValue pv=(ParamValue)sourceObj;
			
			try
			{
				result=convert(pv.getValue(), targetType);
			}
			catch(ConvertException e)
			{
				handleParamValueConvertException(pv, e);
			}
		}
		else if(SoybeanMilkUtils.isInstanceOf(sourceObj, ParamPropertyMap.class))
		{
			result=convertParamPropertyMap((ParamPropertyMap)sourceObj, targetType);
		}
		else if(SoybeanMilkUtils.isInstanceOf(sourceObj, Map.class))
		{
			ParamPropertyMap ppm=new ParamPropertyMap();
			ppm.filter((Map<String, Object>)sourceObj);
			
			result=convertParamPropertyMap(ppm, targetType);
		}
		else
		{
			result=super.convertWhenNoSupportConverter(sourceObj, targetType);
		}
		
		return result;
	}
	
	/**
	 * 将映射表转换成目标类型的对象
	 * @param paramPropMap 源映射表
	 * @param targetType
	 * @return
	 */
	protected Object convertParamPropertyMap(ParamPropertyMap paramPropMap, Type targetType)
	{
		if(log.isDebugEnabled())
			log.debug("start converting Map '"+paramPropMap+"' to type '"+targetType+"'");
		
		Object result = null;
		
		//空的映射表作为null处理
		if(paramPropMap==null || paramPropMap.isEmpty())
		{
			result=convert(null, targetType);
		}
		else
		{
			if(targetType instanceof Class<?>)
			{
				result=convertPropertyMapToClass(paramPropMap, (Class<?>)targetType);
			}
			else if(targetType instanceof GenericType)
			{
				result=convertPropertyMapToGenericType(paramPropMap, (GenericType)targetType);
			}
			else if(targetType instanceof ParameterizedType
					|| targetType instanceof GenericArrayType
					|| targetType instanceof TypeVariable<?>
					|| targetType instanceof WildcardType)
			{
				result=convertPropertyMapToGenericType(paramPropMap, GenericType.getGenericType(targetType, null));
			}
			else
				throw new GenericConvertException("converting '"+paramPropMap+"' to '"+targetType+"' is not supported");
		}
		
		return result;
	}
	
	/**
	 * 将属性映射表转换为目标类型为<code>Class&lt?&gt;</code>的对象，目标类型只可能为JavaBean或者JavaBean数组
	 * @param propertyMap 属性映射表
	 * @param targetClass
	 * @return
	 * @date 2011-10-12
	 */
	protected Object convertPropertyMapToClass(ParamPropertyMap propertyMap, Class<?> targetClass)
	{
		Object result=null;
		
		//数组
		if(SoybeanMilkUtils.isArray(targetClass))
		{
			Class<?> eleClass=targetClass.getComponentType();
			
			result=convertPropertyMapToList(propertyMap, List.class, eleClass);
			result=listToArray((List<?>)result, eleClass);
		}
		//JavaBean
		else
		{
			PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(targetClass);
			
			if(!beanInfo.hasSubPropertyInfo())
				throw new GenericConvertException("the target javaBean Class '"+targetClass+"' is not valid, it has no javaBean property");
			
			Set<String> propertyKeys=propertyMap.keySet();
			for(String property : propertyKeys)
			{
				if(beanInfo.getSubPropertyInfo(property)==null)
				{
					if(!propertyMap.isRoot())
						throw new GenericConvertException("can not find property '"+property+"' in class '"+beanInfo.getType().getName()+"'");
					else
						continue;
				}
				
				//延迟初始化
				if(result == null)
					result = instance(beanInfo.getType(), -1);
				
				try
				{
					setProperty(result, property, propertyMap.get(property));
				}
				catch(ConvertException e)
				{
					handleParamPropertyMapConvertException(propertyMap, property, e);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性映射表转换为泛型类型的对象
	 * @param sourceMap
	 * @param genericType
	 * @return
	 * @date 2011-10-12
	 */
	protected Object convertPropertyMapToGenericType(ParamPropertyMap sourceMap, GenericType genericType)
	{
		Object result=null;
		
		boolean canConvert=true;
		
		if(genericType.isParameterizedType())
		{
			Class<?> actualClass=genericType.getActualClass();
			Class<?>[] argClasses=genericType.getParamClasses();
			
			//List<T>
			if(SoybeanMilkUtils.isAncestorClass(List.class, actualClass))
			{
				result=convertPropertyMapToList(sourceMap, actualClass, argClasses[0]);
			}
			//Set<T>
			else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualClass))
			{
				result=convertPropertyMapToList(sourceMap, List.class, argClasses[0]);
				result=listToSet((List<?>)result, actualClass);
			}
			else
				canConvert=false;
		}
		//T[]
		else if(genericType.isGenericArrayType())
		{
			Class<?> componentClass=genericType.getComponentClass();
			
			result=convertPropertyMapToList(sourceMap, List.class, componentClass);
			result=listToArray((List<?>)result, componentClass);
		}
		//T
		else if(genericType.isTypeVariable())
		{
			Class<?> actualClass=genericType.getActualClass();
			result=convert(sourceMap, actualClass);
		}
		//? extends SomeType
		else if(genericType.isWildcardType())
		{
			Class<?> actualClass=genericType.getActualClass();
			result=convert(sourceMap, actualClass);
		}
		else
			canConvert=false;
		
		if(!canConvert)
			throw new GenericConvertException("converting 'Map<String,?>' to '"+genericType.getType()+"' is not supported");
		
		return result;
	}
	
	/**
	 * 将属性映射表转换为列表对象，属性映射表的字符串关键字可以是两种内容：<br>
	 * <ol>
	 * 	<li>
	 * 		数字，比如：“0”、“1”，表示属性值在列表中的索引
	 * 	</li>
	 * 	<li>
	 * 		字符，比如："property1"、“property2”，表示列表元素对象对应的属性名
	 * 	</li>
	 * </ol>
	 * @param propertyMap
	 * @param listClass
	 * @param elementClass
	 * @return
	 * @date 2012-2-19
	 */
	protected List<Object> convertPropertyMapToList(ParamPropertyMap propertyMap, Class<?> listClass, Class<?> elementClass)
	{
		if(propertyMap==null || propertyMap.isEmpty())
			return null;
		
		@SuppressWarnings("unchecked")
		List<Object> result=(List<Object>)instance(listClass, -1);
		
		int arrayValueLen=-1;
		Set<String> propertyKeyes=propertyMap.keySet();
		for(String property : propertyKeyes)
		{
			Object value=propertyMap.get(property);
			
			//优先处理索引属性
			if(isIndexOfProperty(property))
			{
				int idx=-1;
				try
				{
					idx=Integer.parseInt(property);
				}
				catch(Exception e)
				{
					throw new GenericConvertException("illegal index value '"+property+"' in property expression '"+propertyMap.getFullParamName(property)+"'", e);
				}
				
				Object element=null;
				try
				{
					element=convert(value, elementClass);
				}
				catch(ConvertException e)
				{
					handleParamPropertyMapConvertException(propertyMap, property, e);
				}
				
				result.set(idx, element);
			}
			else
			{
				if(value == null)
					continue;
				else if(SoybeanMilkUtils.isArray(value.getClass()))
				{
					int len=Array.getLength(value);
					if(arrayValueLen == -1)
						arrayValueLen=len;
					else if(len != arrayValueLen)
						throw new GenericConvertException("all the array elements in the source Map must be the same length for converting them to JavaBean List");
					
					while(result.size() < len)
						result.add(null);
					
					for(int i=0; i<arrayValueLen; i++)
					{
						Object element=result.get(i);
						if(element ==null)
						{
							element=instance(elementClass, -1);
							result.set(i, element);
						}
						
						try
						{
							setProperty(element, property, Array.get(value, i));
						}
						catch(ConvertException e)
						{
							handleParamPropertyMapConvertException(propertyMap, property, e);
						}
					}
				}
				else
				{
					while(result.size() < 1)
						result.add(null);
					
					Object element=result.get(0);
					if(element ==null)
					{
						element=instance(elementClass, -1);
						result.set(0, element);
					}
					
					try
					{
						setProperty(element, property, value);
					}
					catch(ConvertException e)
					{
						handleParamPropertyMapConvertException(propertyMap, property, e);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 给定的字符串是否是索引值而非属性名（全部由数字组成）
	 * @param str
	 * @return
	 * @date 2012-2-19
	 */
	protected boolean isIndexOfProperty(String str)
	{
		if(str==null || str.length()==0)
			return false;
		
		boolean digit=true;
		
		for(int i=0; i<str.length(); i++)
		{
			char c=str.charAt(i);
			if(c<'0' || c>'9')
			{
				digit=false;
				break;
			}
		}
		
		return digit;
	}
	
	/**
	 * 处理参数值转换异常
	 * @param pv
	 * @param e
	 * @date 2012-2-22
	 */
	protected void handleParamValueConvertException(ParamValue pv, ConvertException e)
	{
		if(e instanceof ParamConvertException)
			throw e;
		else
			throw new ParamConvertException(pv.getParamName(), e.getSourceObject(), e.getTargetType(), e.getCause());
	}
	
	/**
	 * 处理参数属性映射表异常
	 * @param paramPropertyMap
	 * @param key
	 * @param e
	 * @date 2012-2-22
	 */
	protected void handleParamPropertyMapConvertException(ParamPropertyMap paramPropertyMap, String key, ConvertException e)
	{
		if(e instanceof ParamConvertException)
			throw e;
		else
			throw new ParamConvertException(paramPropertyMap.getFullParamName(key), e.getSourceObject(), e.getTargetType(), e.getCause());
	}
}