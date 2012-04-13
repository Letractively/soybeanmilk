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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.bean.converters.BigDecimalConverter;
import org.soybeanMilk.core.bean.converters.BigIntegerConverter;
import org.soybeanMilk.core.bean.converters.BooleanConverter;
import org.soybeanMilk.core.bean.converters.ByteConverter;
import org.soybeanMilk.core.bean.converters.CharacterConverter;
import org.soybeanMilk.core.bean.converters.DateConverter;
import org.soybeanMilk.core.bean.converters.DoubleConverter;
import org.soybeanMilk.core.bean.converters.FloatConverter;
import org.soybeanMilk.core.bean.converters.IntegerConverter;
import org.soybeanMilk.core.bean.converters.LongConverter;
import org.soybeanMilk.core.bean.converters.ShortConverter;
import org.soybeanMilk.core.bean.converters.SqlDateConverter;
import org.soybeanMilk.core.bean.converters.SqlTimeConverter;
import org.soybeanMilk.core.bean.converters.SqlTimestampConverter;

/**
 * 默认通用转换器的。
 * <p>
 * 它支持的类型转换如下所示：<br>
 * <table border="1" cellspacing="1" cellpadding="3">
 *   <tr><td>源类型</td><td>目标类型</td></tr>
 *   <tr>
 *   	<td>String</td>
 *   	<td>
 *   		boolean, Boolean; byte, Byte; char, Character; double, Double; float, Float;<br>
 *   		int, Integer; long, Long; short, Short;<br>
 *   		java.math.BigDecimal; java.math.BigInteger; java.util.Date; java.sql.Date; java.sql.Time; java.sql.Timestamp;<br>
 *   		enum
 *   	</td>
 *   </tr><tr>
 *   	<td>String[]</td>
 *   	<td>
 *   		上述各类型的数组、java.util.List、java.util.Set。<br>
 *   		比如“int[]”、“boolean[]”、“Short[]”、List&lt;Integer&gt;、List&lt;Date&gt;、Set&lt;Integer&gt;、Set&lt;Date&gt; <br>
 *   </td></tr>
 *   <tr>
 *   	<td>
 *   		Map&lt;String, ?&gt;
 *   	</td><td>
 *   		JavaBean; List&lt;JavaBean&gt;; Set&lt;JavaBean&gt;; Map&lt;SomeKeyType, JavaBean&gt;
 *   	</td>
 *   </tr>
 * </table>
 * </p>
 * <p>
 * 对于Map&lt;String, ?&gt;类型的源对象，它的关键字必须符合<i>访问符表达式</i>语义：
 * </p>
 * <p>
 * 		<i>name</i>[.<i>name</i> ...]
 * </p>
 * <p>
 * 这里，<i>name</i>可以是下面这些语义和字面值：
 * </p>
 * <ul>
 * 	<li>
 *  	JavaBean对象属性名
 *  </li>
 *  <li>
 *  	List、Set、数组对象的下标值（必须是数值，Set时仅作为标识）
 *  </li>
 *  <li>
 *  	Map对象关键字
 *  </li>
 *  <li>
 *  	“class”字面值，用以指定自定义转换JavaBean类型，它的值必须是JavaBean的全类名
 *  </li>
 * </ul>
 * <p>
 * 比如，它可以将下面的映射表：
 * <pre>
 * "id"                    -&gt;  "1"
 * "name"                  -&gt;  "jack"
 * "listChildren.id"       -&gt;  ["11", "12"]
 * "listChildren.name"     -&gt;  ["tom", "mary"]
 * "setChildren.id"        -&gt;  ["11", "12"]
 * "setChildren.name"      -&gt;  ["tom", "mary"]
 * "arrayChildren.id"      -&gt;  ["11", "12"]
 * "arrayChildren.name"    -&gt;  ["tom", "mary"]
 * "mapChildren.key0.id"   -&gt;  "11"
 * "mapChildren.key0.name" -&gt;  "tom"
 * "mapChildren.key1.id"   -&gt;  "22"
 * "mapChildren.key1.name" -&gt;  "mary"
 * </pre>
 * 转换为：
 * <pre>
 * class User{
 * 	private Integer id;
 * 	private String name;
 * 	private List&lt;User&gt; listChildren;
 * 	private Set&lt;User&gt; setChildren;
 * 	private User[] arrayChildren;
 * 	private Map&lt;String, User&gt; mapChildren;
 * 	...
 * }
 * </pre>
 * 类型的对象，或者将：
 * <pre>
 * "id"                             -&gt;  ["1","2","3"]
 * "name"                           -&gt;  ["jack","tom","cherry"]
 * "0.listChildren.id"              -&gt;  ["10","11"]
 * "0.listChildren.name"            -&gt;  ["jack10","jack11"]
 * "1.setChildren.0.id"             -&gt;  "20"
 * "1.setChildren.0.name"           -&gt;  "tom20"
 * "1.setChildren.1.id"             -&gt;  "21"
 * "1.setChildren.1.name"           -&gt;  "tom21"
 * "2.arrayChildren.0.id"           -&gt;  "30"
 * "2.arrayChildren.0.name"         -&gt;  "cherry30"
 * "2.arrayChildren.1.id"           -&gt;  "31"
 * "2.arrayChildren.1.name"         -&gt;  "cherry31"
 * "2.mapChildren.key0.id"          -&gt;  "30"
 * "2.mapChildren.key0.name"        -&gt;  "cherry30"
 * "2.mapChildren.key1.id"          -&gt;  "31"
 * "2.mapChildren.key1.name"        -&gt;  "cherry31"
 * </pre>
 * 转换为：
 * <pre>
 * List&lt;User&gt;
 * Set&lt;User&gt;
 * User[]
 * </pre>
 * 类型的对象。
 * </p>
 * <p>
 * 你也可以通过{@link #addConverter(Type, Type, Converter)}方法为它添加更多辅助转换器，使其支持更多的类型转换。<br>
 * 另外，如果目标类型为<code>String</code>，而你没有添加某类型对象到<code>String</code>类型的辅助转换器，那么它将返回此对象的<code>toString()</code>结果。
 * </p>
 * 
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 */
public class DefaultGenericConverter implements GenericConverter
{
	private static Log log = LogFactory.getLog(DefaultGenericConverter.class);
	
	protected static final Object[] EMPTY_ARGS={};
	
	private Map<ConverterKey,Converter> converters;
	
	/**
	 * 创建通用转换器，默认的辅助转换器将被添加
	 */
	public DefaultGenericConverter()
	{
		this(true);
	}
	
	/**
	 * 创建通用转换器
	 * @param initDefaultSupportConverter 是否添加默认的辅助转换器
	 */
	public DefaultGenericConverter(boolean initDefaultSupportConverter)
	{
		if(initDefaultSupportConverter)
			addStringSourceConverters();
	}
	
	/**
	 * 获取所有辅助转换器
	 * @return
	 * @date 2010-12-29
	 */
	public Collection<Converter> getSupportConverters()
	{
		return converters==null ? null : converters.values();
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
	{
		if(log.isDebugEnabled())
			log.debug("start converting '"+SoybeanMilkUtils.toString(sourceObj)+"' to type '"+SoybeanMilkUtils.toString(targetType)+"'");
		
		Object result=null;
		
		if(targetType==null || SoybeanMilkUtils.isInstanceOf(sourceObj, SoybeanMilkUtils.toWrapperType(targetType)))
		{
			result=sourceObj;
		}
		else if(sourceObj==null || (sourceObj instanceof String && ((String)sourceObj).length()==0))
		{
			if(SoybeanMilkUtils.isPrimitive(targetType))
				throw new GenericConvertException("can not convert '"+SoybeanMilkUtils.toString(sourceObj)+"' to primitive type '"+SoybeanMilkUtils.toString(targetType)+"'");
			else
				result=null;
		}
		else
		{
			Converter converter = getConverter(sourceObj.getClass(), targetType);
			
			if(converter != null)
				result=doConvert(converter, sourceObj, targetType);
			else
				result=convertWhenNoSupportConverter(sourceObj, targetType);
		}
		
		return (T)result;
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T getProperty(Object srcObj, String property, Type expectType) throws ConvertException
	{
		if(srcObj == null)
			return null;
		
		if(property==null || property.length()==0)
			throw new IllegalArgumentException("[property] must not be empty");
		
		if(log.isDebugEnabled())
			log.debug("start getting property '"+property+"' from '"+SoybeanMilkUtils.toString(srcObj)+"'");
		
		Object result=null;
		
		Object obj=srcObj;
		PropertyInfo objBeanInfo=PropertyInfo.getPropertyInfo(obj.getClass());
		String[] properties=SoybeanMilkUtils.splitAccessExpression(property);
		
		String prop=null;
		for(int i=0; i<properties.length; i++)
		{
			if(obj == null)
				break;
			
			prop=properties[i];
			
			if(obj instanceof Map<?, ?>)
			{
				obj=getMapValueByStringEqualKey(((Map<?, ?>)obj), prop);
				
				if(obj == null)
					break;
				else
				{
					//最后一个不再需要准备属性信息
					if(i < properties.length-1)
						objBeanInfo=PropertyInfo.getPropertyInfo(obj.getClass());
				}
			}
			else if(isIndexAccessor(prop))
			{
				obj=getIndexedObjValueByStringIndex(obj, prop);
				
				if(obj == null)
					break;
				else
				{
					//同上
					if(i < properties.length-1)
						objBeanInfo=PropertyInfo.getPropertyInfo(obj.getClass());
				}
			}
			else
			{
				objBeanInfo=getSubPropertyInfoNotNull(objBeanInfo, prop);
				obj=getJavaBeanProperty(obj, objBeanInfo, null);
			}
		}
		
		result=(expectType == null ? obj : convert(obj, expectType));
		
		return (T)result;
	}
	
	//@Override
	public void setProperty(Object srcObj, String property, Object value) throws ConvertException
	{
		if(srcObj == null)
			throw new IllegalArgumentException("[srcObj] must not be null");
		if(property==null || property.length()==0)
			throw new IllegalArgumentException("[property] must not be empty");
		
		if(log.isDebugEnabled())
			log.debug("start setting '"+SoybeanMilkUtils.toString(value)+"' to '"+SoybeanMilkUtils.toString(srcObj)+"' property '"+property+"'");
		
		Object obj=srcObj;
		PropertyInfo objBeanInfo=PropertyInfo.getPropertyInfo(srcObj.getClass());
		String[] properties=SoybeanMilkUtils.splitAccessExpression(property);
		
		for(int i=0; i<properties.length; i++)
		{
			String prop=properties[i];
			
			objBeanInfo=getSubPropertyInfoNotNull(objBeanInfo, prop);
			
			if(i == properties.length-1)
				setJavaBeanProperty(obj, objBeanInfo, value);
			else
			{
				Object tmp=getJavaBeanProperty(obj, objBeanInfo, null);
				if(tmp == null)
				{
					tmp=instance(objBeanInfo.getPropType(), -1);
					setJavaBeanProperty(obj, objBeanInfo, tmp);
				}
				
				obj=tmp;
			}
		}
	}
	
	//@Override
	public void addConverter(Type sourceType,Type targetType,Converter converter)
	{
		if(getConverters() == null)
			setConverters(new HashMap<ConverterKey, Converter>());
		
		getConverters().put(generateConverterKey(sourceType, targetType), converter);
		
		if(log.isDebugEnabled())
			log.debug("add a support Converter '"+SoybeanMilkUtils.toString(converter.getClass())+"' for converting '"+SoybeanMilkUtils.toString(sourceType)+"' to '"+SoybeanMilkUtils.toString(targetType)+"'");
	}
	
	//@Override
	public Converter getConverter(Type sourceType, Type targetType)
	{
		Converter re=null;
		Map<ConverterKey,Converter> converters=getConverters();
		
		if(converters != null)
		{
			re=converters.get(generateConverterKey(sourceType, targetType));
			if(re==null && SoybeanMilkUtils.isPrimitive(targetType))
				re=converters.get(generateConverterKey(sourceType, SoybeanMilkUtils.toWrapperType(targetType)));
		}
		
		return re;
	}
	
	/**
	 * 当找不到对应的辅助转换器时，此方法将被调用。
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @date 2011-1-5
	 */
	protected Object convertWhenNoSupportConverter(Object sourceObj, Type targetType) throws ConvertException
	{
		Object result=null;
		
		if(targetType instanceof Class<?>)
		{
			result=convertObjectToClassType(sourceObj, (Class<?>)targetType);
		}
		else if(targetType instanceof GenericType)
		{
			result=convertObjectToGenericType(sourceObj, (GenericType)targetType);
		}
		else if(targetType instanceof ParameterizedType || targetType instanceof GenericArrayType
				|| targetType instanceof TypeVariable<?> || targetType instanceof WildcardType)
		{
			result=convertObjectToGenericType(sourceObj, GenericType.getGenericType(targetType, null));
		}
		else
			result=converterNotFoundThrow(sourceObj.getClass(), targetType);
		
		return result;
	}
	
	/**
	 * 将对象转换为目标类型为<code>Class</code>类型的对象
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @throws ConvertException
	 * @date 2012-4-1
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertObjectToClassType(Object sourceObj, Class targetType) throws ConvertException
	{
		Object result=null;
		
		if(targetType.equals(String.class))
		{
			result=sourceObj.toString();
		}
		else if(targetType.isEnum())
		{
			result= ((sourceObj instanceof String) ? Enum.valueOf(targetType, (String)sourceObj) : Enum.valueOf(targetType, sourceObj.toString()));
		}
		else if(sourceObj instanceof Map<?, ?>)
		{
			result=convertPropertyValueMapToClass(toPropertyValueMap((Map<String, ?>)sourceObj), targetType);
		}
		else if(SoybeanMilkUtils.isArray(sourceObj.getClass()) && SoybeanMilkUtils.isArray(targetType))
		{
			result=convertArrayToArray(sourceObj, targetType.getComponentType());
		}
		else
			result=converterNotFoundThrow(sourceObj.getClass(), targetType);
		
		return result;
	}
	
	/**
	 * 将对象转换为泛型类型
	 * @param sourceObj
	 * @param genericType
	 * @return
	 * @date 2011-10-12
	 */
	@SuppressWarnings("unchecked")
	protected Object convertObjectToGenericType(Object sourceObj, GenericType genericType) throws ConvertException
	{
		Object result=null;
		
		if(sourceObj instanceof Map<?, ?>)
		{
			result=convertPropertyValueMapToGenericType(toPropertyValueMap((Map<String, ?>)sourceObj), genericType);
		}
		else
		{
			boolean isSrcArray=SoybeanMilkUtils.isArray(sourceObj.getClass());
			
			if(isSrcArray && genericType.isParameterizedType())
			{
				Class<?> actualClass=genericType.getActualClass();
				Class<?>[] argClasses=genericType.getParamClasses();
				
				//List<T>
				if(SoybeanMilkUtils.isAncestorClass(List.class, actualClass))
				{
					result=convertArrayToList(sourceObj, actualClass, argClasses[0]);
				}
				//Set<T>
				else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualClass))
				{
					List<?> list=convertArrayToList(sourceObj, List.class, argClasses[0]);
					result=listToSet(list, actualClass);
				}
				else
					result=converterNotFoundThrow(sourceObj.getClass(), genericType);
			}
			//T[]
			else if(isSrcArray && genericType.isGenericArrayType())
			{
				Class<?> componentClass=genericType.getComponentClass();
				result=convertArrayToArray(sourceObj, componentClass);
			}
			//T
			else if(genericType.isTypeVariable())
			{
				Class<?> actualClass=genericType.getActualClass();
				result=convert(sourceObj, actualClass);
			}
			//? extends SomeType
			else if(genericType.isWildcardType())
			{
				Class<?> actualClass=genericType.getActualClass();
				result=convert(sourceObj, actualClass);
			}
			else
				result=converterNotFoundThrow(sourceObj.getClass(), genericType);
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为目标类型为<code>Class&lt?&gt;</code>的对象，目标类型只可能为JavaBean或者JavaBean数组
	 * @param sourceMap 属性值映射表
	 * @param targetClass
	 * @return
	 */
	protected Object convertPropertyValueMapToClass(PropertyValueMap sourceMap, Class<?> targetClass) throws ConvertException
	{
		Object result=null;
		
		if(sourceMap==null || sourceMap.isEmpty())
		{
			result=convert(null, targetClass);
		}
		//数组
		if(SoybeanMilkUtils.isArray(targetClass))
		{
			Class<?> eleClass=targetClass.getComponentType();
			
			List<?> tmpRe=convertPropertyValueMapToList(sourceMap, List.class, eleClass);
			
			result=listToArray(tmpRe, eleClass);
		}
		//JavaBean
		else
		{
			targetClass=getPropertyValueMapTargetType(sourceMap, targetClass, -1);
			
			PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(targetClass);
			
			if(!beanInfo.hasSubPropertyInfo())
				throw new GenericConvertException("the target javaBean Class '"+SoybeanMilkUtils.toString(targetClass)+"' is not valid, it has no javaBean property");
			
			Set<String> propertyKeys=sourceMap.keySet();
			for(String property : propertyKeys)
			{
				PropertyInfo propInfo=null;
				if(!sourceMap.isCleaned())
				{
					propInfo=beanInfo.getSubPropertyInfo(property);
					
					//忽略无关属性
					if(propInfo == null)
						continue;
				}
				else
					propInfo=getSubPropertyInfoNotNull(beanInfo, property);
				
				//延迟初始化
				if(result == null)
					result = instance(beanInfo.getPropType(), -1);
				
				try
				{
					setJavaBeanProperty(result, propInfo, sourceMap.get(property));
				}
				catch(ConvertException e)
				{
					handlePropertyValueMapConvertException(sourceMap, property, e);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为泛型类型的对象
	 * @param sourceMap
	 * @param genericType
	 * @return
	 */
	protected Object convertPropertyValueMapToGenericType(PropertyValueMap sourceMap, GenericType genericType) throws ConvertException
	{
		Object result=null;
		
		if(genericType.isParameterizedType())
		{
			Class<?> actualClass=genericType.getActualClass();
			Class<?>[] argClasses=genericType.getParamClasses();
			
			//List<T>
			if(SoybeanMilkUtils.isAncestorClass(List.class, actualClass))
			{
				result=convertPropertyValueMapToList(sourceMap, actualClass, argClasses[0]);
			}
			//Set<T>
			else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualClass))
			{
				List<?> tmpRe=convertPropertyValueMapToList(sourceMap, List.class, argClasses[0]);
				result=listToSet(tmpRe, actualClass);
			}
			//Map<K, V>
			else if(SoybeanMilkUtils.isAncestorClass(Map.class, actualClass))
			{
				result=convertPropertyValueMapToMap(sourceMap, actualClass, argClasses[0], argClasses[1]);
			}
			else
				result=converterNotFoundThrow(sourceMap.getClass(), genericType);
		}
		//T[]
		else if(genericType.isGenericArrayType())
		{
			Class<?> componentClass=genericType.getComponentClass();
			
			result=convertPropertyValueMapToList(sourceMap, List.class, componentClass);
			result=listToArray((List<?>)result, componentClass);
		}
		//T
		else if(genericType.isTypeVariable())
		{
			Class<?> actualClass=genericType.getActualClass();
			result=convertPropertyValueMapToClass(sourceMap, actualClass);
		}
		//? extends SomeType
		else if(genericType.isWildcardType())
		{
			Class<?> actualClass=genericType.getActualClass();
			result=convertPropertyValueMapToClass(sourceMap, actualClass);
		}
		else
			result=converterNotFoundThrow(sourceMap.getClass(), genericType);
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为列表对象
	 * @param sourceMap
	 * @param listClass
	 * @param elementClass
	 * @return
	 */
	protected List<?> convertPropertyValueMapToList(PropertyValueMap sourceMap, Class<?> listClass, Class<?> elementClass) throws ConvertException
	{
		if(sourceMap==null || sourceMap.isEmpty())
			return null;
		
		@SuppressWarnings("unchecked")
		List<Object> result=(List<Object>)instance(listClass, -1);
		
		PropertyInfo eleBeanInfo=PropertyInfo.getPropertyInfo(elementClass);
		
		Set<String> propertyKeyes=sourceMap.keySet();
		for(String property : propertyKeyes)
		{
			Object value=sourceMap.get(property);
			
			//明确指定了索引位置
			if(isIndexAccessor(property))
			{
				int idx=-1;
				try
				{
					idx=Integer.parseInt(property);
				}
				catch(Exception e)
				{
					throw new GenericConvertException("illegal index value '"+property+"' of property '"+sourceMap.getPropertyNamePath(property)+"'", e);
				}
				
				while(result.size() < idx+1)
					result.add(null);
				
				Object element=result.get(idx);
				
				//元素存在并且值是属性值映射表，则依次设置这些属性
				if(element!=null && (value instanceof PropertyValueMap))
				{
					PropertyInfo subBeanInfo=PropertyInfo.getPropertyInfo(element.getClass());
					PropertyValueMap subPropMap=(PropertyValueMap)value;
					
					Set<String> subPropKeys=subPropMap.keySet();
					for(String subProp : subPropKeys)
					{
						PropertyInfo subPropInfo=getSubPropertyInfoNotNull(subBeanInfo, subProp);
						
						try
						{
							setJavaBeanProperty(element, subPropInfo, subPropMap.get(subProp));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(sourceMap, property, e);
						}
					}
				}
				else
				{
					try
					{
						element=convert(value, getPropertyValueMapTargetType(sourceMap, elementClass, idx));
					}
					catch(ConvertException e)
					{
						handlePropertyValueMapConvertException(sourceMap, property, e);
					}
					
					result.set(idx, element);
				}
			}
			else
			{
				if(value == null)
					continue;
				
				PropertyInfo propInfo=null;
				//忽略无关属性
				if(!sourceMap.isCleaned())
					propInfo=eleBeanInfo.getSubPropertyInfo(property);
				else
					propInfo=getSubPropertyInfoNotNull(eleBeanInfo, property);
				
				if(propInfo == null)
					continue;
				
				//当前属性值是数组
				if(SoybeanMilkUtils.isArray(value.getClass()))
				{
					int len=Array.getLength(value);
					
					while(result.size() < len)
						result.add(null);
					
					for(int i=0; i<len; i++)
					{
						Object element=result.get(i);
						if(element == null)
						{
							element=instance(getPropertyValueMapTargetType(sourceMap, elementClass, i), -1);
							result.set(i, element);
						}
						
						try
						{
							setJavaBeanProperty(element, propInfo, Array.get(value, i));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(sourceMap, property, e);
						}
					}
				}
				//当前值是属性值映射表，则要将当前属性值转换为集合，并依次赋值
				else if(value instanceof PropertyValueMap)
				{
					PropertyValueMap pppm=(PropertyValueMap)value;
					
					List<?> propList=convertPropertyValueMapToList(pppm, List.class, propInfo.getPropType());
					
					while(result.size() < propList.size())
						result.add(null);
					
					for(int i=0; i<propList.size(); i++)
					{
						Object element=result.get(i);
						if(element ==null)
						{
							element=instance(getPropertyValueMapTargetType(sourceMap, elementClass, i), -1);
							result.set(i, element);
						}
						
						try
						{
							setJavaBeanProperty(element, propInfo, propList.get(i));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(sourceMap, property, e);
						}
					}
				}
				//属性值是其他对象
				else
				{
					while(result.size() < 1)
						result.add(null);
					
					Object element=result.get(0);
					if(element ==null)
					{
						element=instance(getPropertyValueMapTargetType(sourceMap, elementClass, 0), -1);
						result.set(0, element);
					}
					
					try
					{
						setJavaBeanProperty(element, propInfo, value);
					}
					catch(ConvertException e)
					{
						handlePropertyValueMapConvertException(sourceMap, property, e);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为目标映射表, <code>sourceMap</code>的关键字将被转换目标映射表的关键字，值将被转换为此关键字对应的值
	 * @param sourceMap
	 * @param mapClass
	 * @param keyClass
	 * @param valueClass
	 * @return
	 */
	protected Map<?, ?> convertPropertyValueMapToMap(PropertyValueMap sourceMap, Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) throws ConvertException
	{
		if(sourceMap == null)
			return null;
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> result=(Map<Object, Object>)instance(mapClass, -1);
		
		Set<String> keys=sourceMap.keySet();
		for(String key : keys)
		{
			Object tk=null;
			Object tv=null;
			
			try
			{
				tk=convert(key, keyClass);
			}
			catch(ConvertException e)
			{
				throw new GenericConvertException("convert '"+key+"' in key '"+sourceMap.getPropertyNamePath(key)+"' to Map key of type '"+SoybeanMilkUtils.toString(keyClass)+"' failed", e);
			}
			
			try
			{
				Object value=sourceMap.get(key);
				
				Class<?> cvc=valueClass;
				if(value instanceof PropertyValueMap)
					cvc=getPropertyValueMapTargetType((PropertyValueMap)value, valueClass, -1);
				
				tv=convert(value, cvc);
			}
			catch(ConvertException e)
			{
				handlePropertyValueMapConvertException(sourceMap, key, e);
			}
			
			result.put(tk, tv);
		}
		
		return result;
	}
	
	/**
	 * 给定的字符串是否是索引值而非属性名（全部由数字组成）
	 * @param str
	 * @return
	 */
	protected boolean isIndexAccessor(String str)
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
	 * 获取辅助{@linkplain Converter 转换器}，结果不会为<code>null</code>
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	protected Converter getConverterNotNull(Type sourceType, Type targetType)
	{
		Converter cvt=getConverter(sourceType, targetType);
		
		if(cvt == null)
			converterNotFoundThrow(sourceType, targetType);
		
		return cvt;
	}
	
	/**
	 * 处理属性值映射表转换异常
	 * @param paramPropertyMap
	 * @param key
	 * @param e
	 */
	protected void handlePropertyValueMapConvertException(PropertyValueMap paramPropertyMap, String key, ConvertException e) throws ConvertException
	{
		if(e instanceof MapConvertException)
			throw e;
		else
			throw new MapConvertException(paramPropertyMap.getPropertyNamePath(key), e.getSourceObject(), e.getTargetType(), e.getCause());
	}
	
	/**
	 * 由数组转换为{@linkplain java.util.List List}对象。
	 * @param array
	 * @param listClass
	 * @return
	 * @date 2011-1-5
	 */
	@SuppressWarnings("unchecked")
	protected List<?> convertArrayToList(Object array, Class<?> listClass, Class<?> elementClass) throws ConvertException
	{
		List<Object> result=null;
		
		if(array != null)
		{
			result=(List<Object>)instance(listClass, -1);
			
			for(int i=0,len=Array.getLength(array); i<len; i++)
				result.add(convert(Array.get(array, i), elementClass));
		}
		
		return result;
	}
	
	/**
	 * 由一个数组对象转换为另一数组对象
	 * @param array
	 * @param elementClass
	 * @return
	 * @date 2012-2-20
	 */
	protected Object convertArrayToArray(Object array, Class<?> elementClass) throws ConvertException
	{
		Object result=null;
		
		if(array != null)
		{
			int len=Array.getLength(array);
			
			result=instance(elementClass, len);
			
			for(int i=0; i<len; i++)
			{
				Object v=convert(Array.get(array, i), elementClass);
				Array.set(result, i, v);
			}
		}
		
		return result;
	}
	
	/**
	 * 由列表对象转换为{@linkplain java.util.Set Set}对象，它不会对列表对象的元素执行类型转换
	 * @param list
	 * @param setClass
	 * @return
	 * @date 2012-2-19
	 */
	@SuppressWarnings("unchecked")
	protected Set<?> listToSet(List<?> list, Class<?> setClass)
	{
		Set<Object> result=null;
		
		if(list != null)
		{
			result=(Set<Object>)instance(setClass, -1);
			
			for(int i=0,len=list.size(); i<len; i++)
				result.add(list.get(i));
		}
		
		return result;
	}
	
	/**
	 * 由列表对象转换为数组对象，它不会对列表对象的元素执行类型转换
	 * @param list 列表对象
	 * @param elementClass 目标数组的元素类型
	 * @return
	 * @date 2012-2-19
	 */
	protected Object listToArray(List<?> list, Class<?> elementClass)
	{
		Object result=null;
		
		if(list!=null)
		{
			int size=list.size();
			
			result=Array.newInstance(elementClass, size);
			
			for(int i=0; i<size; i++)
				Array.set(result, i, list.get(i));
		}
		
		return result;
	}
	
	/**
	 * 设置对象的某个属性值，如果值类型与属性类型不匹配，值对象将被转换
	 * @param obj 要设置属性的对象
	 * @param propertyInfo 要设置的属性信息
	 * @param value 属性值
	 * @date 2012-2-20
	 */
	protected void setJavaBeanProperty(Object obj, PropertyInfo propertyInfo, Object value) throws ConvertException
	{
		Type targetType=propertyInfo.getPropGenericType();
		if(!SoybeanMilkUtils.isClassType(targetType))
			targetType=GenericType.getGenericType(targetType, propertyInfo.getOwnerClass());
		
		Object destValue=convert(value, targetType);
		try
		{
			propertyInfo.getWriteMethod().invoke(obj, new Object[]{destValue});
		}
		catch(Exception e)
		{
			throw new GenericConvertException("exception occur while calling write method '"+propertyInfo.getWriteMethod()+"'",e);
		}
	}
	
	/**
	 * 获取对象属性值，如果属性值类型与期望的目标类型不相符，属性值对象将被转换
	 * @param obj
	 * @param propertyInfo
	 * @param targetType
	 * @return
	 * @date 2012-2-20
	 */
	protected Object getJavaBeanProperty(Object obj, PropertyInfo propertyInfo, Type targetType)
	{
		Object result=null;
		
		try
		{
			result=propertyInfo.getReadMethod().invoke(obj, EMPTY_ARGS);
			
			if(targetType != null)
				result=convert(result, targetType);
		}
		catch(Exception e)
		{
			throw new GenericConvertException("exception occur while calling read method '"+propertyInfo.getReadMethod().getName()+"'",e);
		}
		
		return result;
	}
	
	/**
	 * 获取子属性信息
	 * @param parent
	 * @param property
	 * @return
	 * @date 2012-2-26
	 */
	protected PropertyInfo getSubPropertyInfoNotNull(PropertyInfo parent, String property)
	{
		PropertyInfo re=parent.getSubPropertyInfo(property);
		if(re == null)
			throw new GenericConvertException("can not find property '"+property+"' in class '"+SoybeanMilkUtils.toString(parent.getPropType())+"'");
		
		return re;
	}
	
	/**
	 * 获取属性值映射表转换的目标类型
	 * @param pvm
	 * @param defaultType
	 * @param idx 索引，当属性值映射表对应集合类型时，可能会有多个目标类型
	 * @return
	 * @date 2012-4-12
	 */
	protected Class<?> getPropertyValueMapTargetType(PropertyValueMap pvm, Class<?> defaultType, int idx)
	{
		Class<?> re=null;
		
		Class<?>[] cs=pvm.getPropertyType();
		
		if(idx < 0)
		{
			re=(cs!=null && cs.length>0 ? cs[0] : defaultType);
		}
		else if(cs!=null && idx<cs.length)
		{
				re=cs[idx];
		}
		else
		{
			Object sub=pvm.get(String.valueOf(idx));
			
			if(sub instanceof PropertyValueMap)
				re=getPropertyValueMapTargetType((PropertyValueMap)sub, defaultType, -1);
			else
				re=defaultType;
		}
		
		return re;
	}
	
	/**
	 * 获取给定字符串关键字的值
	 * @param map
	 * @param str
	 * @return
	 * @date 2012-4-2
	 */
	protected Object getMapValueByStringEqualKey(Map<?, ?> map, String str)
	{
		Object result=map.get(str);
		
		//不能获取值，则映射表的关键字可能不是字符串形式，需要遍历查找
		if(result == null)
		{
			Set<?> mkeys=map.keySet();
			
			for(Object mk : mkeys)
			{
				if(mk != null)
				{
					String strMk=(mk instanceof String ? (String)mk : mk.toString());
					
					if(strMk.equals(str))
					{
						result=map.get(mk);
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 获取可以使用索引访问的对象的某个索引的值
	 * @param obj
	 * @param strIdx
	 * @return
	 * @date 2012-4-2
	 */
	protected Object getIndexedObjValueByStringIndex(Object obj, String strIdx)
	{
		Object result=null;
		
		int idx=-1;
		try
		{
			idx=Integer.parseInt(strIdx);
		}
		catch(Exception e)
		{
			throw new GenericConvertException("illegal index value '"+strIdx+"' for getting value from '"+obj+"'", e);
		}
		
		if(obj.getClass().isArray())
		{
			result=Array.get(obj, idx);
		}
		else if(obj instanceof List<?>)
		{
			result=((List<?>)obj).get(idx);
		}
		else if(obj instanceof Set<?>)
		{
			int si=0;
			Set<?> set=(Set<?>)obj;
			
			if(idx >= set.size())
				throw new GenericConvertException("you are getting the "+idx+"-th value of Set '"+set+"', but its size is only "+set.size());
			
			for(Object o : set)
			{
				if(si == idx)
				{
					result=o;
					break;
				}
				
				si++;
			}
		}
		else
			throw new GenericConvertException("get the "+idx+"-th value from '"+obj+"' is not supported");
		
		return result;
	}
	
	/**
	 * 使用转换器转换对象
	 * @param converter
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @date 2011-4-10
	 */
	protected Object doConvert(Converter converter, Object sourceObj, Type targetType) throws ConvertException
	{
		return converter.convert(sourceObj, targetType);
	}
	
	/**
	 * 专门用于抛出找不到转换器的异常方法
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @date 2012-4-1
	 */
	protected Object converterNotFoundThrow(Type sourceType, Type targetType)
	{
		throw new GenericConvertException("can not find Converter for converting '"+SoybeanMilkUtils.toString(sourceType)+"' to '"+SoybeanMilkUtils.toString(targetType)+"'");
	}
	
	/**
	 * 将给定映射表转换为属性值映射表
	 * @param map
	 * @return
	 * @date 2012-4-1
	 */
	protected PropertyValueMap toPropertyValueMap(Map<String, ?> map)
	{
		return ((map instanceof PropertyValueMap) ? (PropertyValueMap)map : new PropertyValueMap(map));
	}
	
	/**
	 * 产生转换器关键字，用于在映射表中标识转换器
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	protected ConverterKey generateConverterKey(Type sourceType, Type targetType)
	{
		return new ConverterKey(sourceType, targetType);
	}
	
	/**
	 * 创建给定类型的实例。<br>
	 * 如果此类是集合类接口<code>List</code>、<code>Set</code>、<code>Map</code>，它将创建默认实例，<code>arraySize</code>将被忽略；<br>
	 * 否则，此类必须提供一个无参的构造方法，如果<code>arrayLength</code>不小于0，它将创建此类的数组实例，否则，仅创建此类的实例。
	 * @param objectType 类型
	 * @param arrayLength 要创建数组的长度
	 * @return
	 * @date 2010-12-29
	 */
	@SuppressWarnings("rawtypes")
	protected Object instance(Class<?> objectType, int arrayLength)
	{
		if(java.util.List.class.equals(objectType))
			return new ArrayList();
		else if(java.util.Set.class.equals(objectType))
			return new HashSet();
		else if(java.util.Map.class.equals(objectType))
			return new HashMap();
		else
		{
			try
			{
				if(arrayLength < 0)
					return objectType.newInstance();
				else
					return Array.newInstance(objectType, arrayLength);
			}
			catch(Exception e)
			{
				throw new GenericConvertException("exception occur while creating instance of type '"+SoybeanMilkUtils.toString(objectType)+"' ",e);
			}
		}
	}

	protected Map<ConverterKey, Converter> getConverters() {
		return converters;
	}

	protected void setConverters(Map<ConverterKey, Converter> converters) {
		this.converters = converters;
	}
	
	/**
	 * 添加可以将字符串转换到原子类型的辅助转换器
	 */
	protected void addStringSourceConverters()
	{
		//基本类型及其包装类型
		addConverter(String.class, Boolean.class, new BooleanConverter());
		addConverter(String.class, Byte.class, new ByteConverter());
		addConverter(String.class, Character.class, new CharacterConverter());
		addConverter(String.class, Double.class, new DoubleConverter());
		addConverter(String.class, Float.class, new FloatConverter());
		addConverter(String.class, Integer.class, new IntegerConverter());
		addConverter(String.class, Long.class, new LongConverter());
		addConverter(String.class, Short.class, new ShortConverter());
		
		//其他
		addConverter(String.class, java.math.BigDecimal.class, new BigDecimalConverter());
		addConverter(String.class, java.math.BigInteger.class, new BigIntegerConverter());
		addConverter(String.class, java.util.Date.class, new DateConverter());
		addConverter(String.class, java.sql.Date.class, new SqlDateConverter());
		addConverter(String.class, java.sql.Time.class, new SqlTimeConverter());
		addConverter(String.class, java.sql.Timestamp.class, new SqlTimestampConverter());
	}
	
	/**
	 * 用于转换器映射表中主键的类
	 * @author earthAngry@gmail.com
	 * @date 2011-10-8
	 */
	protected static class ConverterKey
	{
		private Type sourceType;
		private Type targetType;
		
		public ConverterKey(Type sourceType, Type targetType)
		{
			this.sourceType = sourceType;
			this.targetType = targetType;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((sourceType == null) ? 0 : sourceType.hashCode());
			result = prime * result
					+ ((targetType == null) ? 0 : targetType.hashCode());
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
			ConverterKey other = (ConverterKey) obj;
			if (sourceType == null) {
				if (other.sourceType != null)
					return false;
			} else if (!sourceType.equals(other.sourceType))
				return false;
			if (targetType == null) {
				if (other.targetType != null)
					return false;
			} else if (!targetType.equals(other.targetType))
				return false;
			return true;
		}
	}
	
	/**
	 * 属性值映射表，它是关键字为<i>访问符表达式</i>映射表的分解结果。<br>
	 * 它的关键字表示某对象的某个属性名，而关键字对应的值则是这个对象该属性的值（或者是可以转换为该属性值的某个对象）。
	 * 它有一个特殊用途的{@linkplain #clean}属性，用以标识属性值映射表是否是清洁的，清洁的属性值映射表在转换为某对象时，
	 * 如果它的某个关键字找不到对应的对象属性名，转换将被终止；而如果属性值映射表不是清洁的，找不到对应对象属性名的关键字将被忽略。
	 * 
	 * @author earthAngry@gmail.com
	 * @date 2012-3-27
	 */
	protected static class PropertyValueMap extends HashMap<String, Object>
	{
		private static final long serialVersionUID = 1L;
		
		/**保留关键字-表明源映射表中自定义转换的目标类型*/
		public static final String RESERVE_KEY_CUSTOM_CLASS="class";
		
		/**此属性值映射表的属性名*/
		private String propertyName;
		
		/**父属性值映射表*/
		private PropertyValueMap parent;
		
		/**是否是清洁的*/
		private boolean cleaned;
		
		/**该属性值映射表对应的类型*/
		private Class<?>[] propertyType;
		
		/**
		 * 由源映射表创建属性值映射表
		 * @param map 源映射表，它的关键字具有<i>访问符表达式</i>语义
		 */
		public PropertyValueMap(Map<String, ?> map)
		{
			this(map, true);
		}
		
		/**
		 * 由源映射表创建属性值映射表
		 * @param map 源映射表，它的关键字具有<i>访问符表达式</i>语义
		 * @param cleaned 源映射表是否是清洁的
		 */
		public PropertyValueMap(Map<String, ?> map, boolean cleaned)
		{
			super();
			
			this.cleaned=cleaned;
			this.resolve(map);
		}
		
		/**
		 * 内部使用的构造器
		 */
		private PropertyValueMap(String propertyName, PropertyValueMap parent)
		{
			super();
			this.propertyName=propertyName;
			this.parent = parent;
			this.cleaned=true;
		}
		
		/**
		 * 获取某个属性的属性名路径。
		 * @param propertyName
		 * @return
		 */
		public String getPropertyNamePath(String propertyName)
		{
			String result=null;
			
			if(this.parent != null)
				result=this.parent.getPropertyNamePath(this.propertyName);
			else
				result=this.propertyName;
			
			if(result==null || result.length()==0)
				return propertyName;
			else
				return result+Constants.ACCESSOR+propertyName;
		}
		
		public boolean isCleaned() {
			return cleaned;
		}

		public void setCleaned(boolean cleaned) {
			this.cleaned = cleaned;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		public PropertyValueMap getParent() {
			return parent;
		}

		public void setParent(PropertyValueMap parent) {
			this.parent = parent;
		}
		
		public Class<?>[] getPropertyType() {
			return propertyType;
		}

		public void setPropertyType(Class<?>[] propertyType) {
			this.propertyType = propertyType;
		}

		/**
		 * 分解给定的关键字为<i>访问符表达式</i>的映射表。
		 * @param map 映射表，它的关键字具有<i>访问符表达式</i>语义
		 */
		protected void resolve(Map<String, ?> map)
		{
			Set<String> keys=map.keySet();
			
			for(String key : keys)
			{
				String[] propKeys=SoybeanMilkUtils.splitAccessExpression(key);
				
				PropertyValueMap parent=this;
				
				for(int i=0; i<propKeys.length; i++)
				{
					if(i == propKeys.length-1)
					{
						//当前属性值映射表的自定义对应类型
						if(RESERVE_KEY_CUSTOM_CLASS.equals(propKeys[i]))
						{
							parent.setPropertyType(toPropertyType(map.get(key)));
						}
						else
							parent.put(propKeys[i], map.get(key));
					}
					else
					{
						PropertyValueMap tmp=(PropertyValueMap)parent.get(propKeys[i]);
						if(tmp == null)
						{
							tmp=new PropertyValueMap(propKeys[i], parent);
							parent.put(propKeys[i], tmp);
						}
						
						parent=tmp;
					}
				}
			}
		}
		
		/**
		 * 将对象转换为类型数组
		 * @param clazz 可以是Class、String，以及他们的数组
		 * @return
		 * @date 2012-4-12
		 */
		protected Class<?>[] toPropertyType(Object clazz)
		{
			Class<?>[] re=null;
			
			if(clazz == null)
				return null;
			
			if(clazz instanceof String)
			{
				re=new Class<?>[1];
				
				try
				{
					re[0]=Class.forName((String)clazz);
				}
				catch(ClassNotFoundException e)
				{
					throw new GenericConvertException("custom target type '"+clazz+"' not found", e);
				}
			}
			else if(clazz instanceof String[])
			{
				String[] clazzs=(String[])clazz;
				re=new Class<?>[clazzs.length];
				
				for(int i=0; i<clazzs.length; i++)
				{
					try
					{
						re[i]=Class.forName(clazzs[i]);
					}
					catch(ClassNotFoundException e)
					{
						throw new GenericConvertException("custom target type '"+clazzs[i]+"' not found", e);
					}
				}
			}
			else if(clazz instanceof Class<?>)
			{
				re=new Class<?>[]{ (Class<?>)clazz };
			}
			else if(clazz instanceof Class<?>[])
			{
				re=(Class<?>[])clazz;
			}
			else
				throw new GenericConvertException("custom convert target type '"+clazz+"' is unknown");
			
			return re;
		}
		
		@Override
		public String toString()
		{
			return getClass().getSimpleName()+" [propertyName=" + getPropertyName()
					+ ", cleaned="+cleaned+", " + super.toString() + "]";
		}
	}
}
