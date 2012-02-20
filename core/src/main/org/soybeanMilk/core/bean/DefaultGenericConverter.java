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
 * 通用转换器的默认实现。<br>
 * 它默认支持的类型转换如下所示：<br>
 * <table border="1" cellspacing="1" cellpadding="3">
 *   <tr><td>源类型</td><td>目标类型</td></tr>
 *   <tr>
 *   	<td>String</td>
 *   	<td>
 *   		boolean, Boolean; byte, Byte; char, Character; double, Double; float, Float;<br>
 *   		int, Integer; long, Long; short, Short;<br>
 *   		java.math.BigDecimal; java.math.BigInteger; java.util.Date; java.sql.Date; java.sql.Time; java.sql.Timestamp
 *   	</td>
 *   </tr>
 *   
 *   
 *   <tr>
 *   	<td>String[]</td>
 *   	<td>
 *   		上述各类型的数组、java.util.List、java.util.Set。<br>
 *   		比如“int[]”、“boolean[]”、“Short[]”、List&lt;Integer&gt;、List&lt;Date&gt;、Set&lt;Integer&gt;、Set&lt;Date&gt; <br>
 *   </td></tr>
 * </table>
 * <br>
 * 另外，如果目标类型为<code>String</code>，而你没有添加对象到<code>String</code>类型的辅助转换器，那么它将返回此对象的<code>toString()</code>结果。<br>
 * 你也可以通过{@link #addConverter(Type, Type, Converter)}为它添加更多辅助转换器，使其支持更多的类型转换。
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
	public Object convert(Object sourceObj, Type targetType)
	{
		if(log.isDebugEnabled())
			log.debug("start converting '"+sourceObj+"' of type '"+(sourceObj==null ? null : sourceObj.getClass())+"' to type '"+targetType+"'");
		
		if(targetType == null)
			return sourceObj;
		else if(SoybeanMilkUtils.isInstanceOf(sourceObj, SoybeanMilkUtils.toWrapperType(targetType)))
			return sourceObj;
		else if(sourceObj==null
				|| (sourceObj instanceof String && ((String)sourceObj).length()==0))
		{
			if(SoybeanMilkUtils.isPrimitive(targetType))
				throw new GenericConvertException("can not convert '"+sourceObj+"' to primitive type '"+targetType+"'");
			else
				return null;
		}
		else
		{
			Converter converter = getConverter(sourceObj.getClass(), targetType);
			
			if(converter == null)
				return convertWhenNoSupportConverter(sourceObj, targetType);
			else
				return doConvert(converter, sourceObj, targetType);
		}
	}
	
	//@Override
	public Object getProperty(Object srcObj, String propertyExpression, Type expectType)
	{
		if(srcObj == null)
			return null;
		
		if(propertyExpression==null || propertyExpression.length()==0)
			throw new IllegalArgumentException("[propertyExpression] must not be empty");
		
		if(log.isDebugEnabled())
			log.debug("start getting  property '"+propertyExpression+"' from '"+srcObj+"'");
		
		Object result=null;
		
		Object parent=srcObj;
		PropertyInfo parentBeanInfo=PropertyInfo.getPropertyInfo(srcObj.getClass());
		String[] properties=splitPropertyExpression(propertyExpression);
		
		for(int i=0, len=properties.length; i<len; i++)
		{
			if(parent == null)
				break;
			
			PropertyInfo propInfo=parentBeanInfo.getSubPropertyInfo(properties[i]);
			if(propInfo == null)
				throw new GenericConvertException("can not find property '"+properties[i]+"' in class '"+parentBeanInfo.getType().getName()+"'");
			
			parent=getProperty(parent, propInfo, null);
			parentBeanInfo=propInfo;
		}
		
		result=(expectType == null ? parent : convert(parent, expectType));
		
		return result;
	}
	
	//@Override
	public void setProperty(Object srcObj, String propertyExpression, Object value)
	{
		if(srcObj == null)
			throw new IllegalArgumentException("[srcObj] must not be null");
		if(propertyExpression==null || propertyExpression.length()==0)
			throw new IllegalArgumentException("[propertyExpression] must not be empty");
		
		if(log.isDebugEnabled())
			log.debug("start setting '"+value+"' to '"+srcObj+"' property '"+propertyExpression+"'");
		
		Object parent=srcObj;
		PropertyInfo parentBeanInfo=PropertyInfo.getPropertyInfo(srcObj.getClass());
		String[] properties=splitPropertyExpression(propertyExpression);
		
		for(int i=0, len=properties.length; i<len; i++)
		{
			PropertyInfo propInfo=parentBeanInfo.getSubPropertyInfo(properties[i]);
			
			if(i == len-1)
			{
				if(propInfo == null)
					throw new GenericConvertException("can not find property '"+properties[i]+"' in class '"+parentBeanInfo.getType().getName()+"'");
				
				setProperty(parent, propInfo, value);
			}
			else
			{
				Object tmp=getProperty(parent, propInfo, null);
				if(tmp == null)
				{
					tmp=instance(propInfo.getType(), -1);
					setProperty(parent, propInfo, tmp);
				}
				
				parent=tmp;
				parentBeanInfo=propInfo;
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
			log.debug("add a support Converter '"+converter.getClass().getName()+"' for converting '"+sourceType+"' to '"+targetType+"'");
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertWhenNoSupportConverter(Object sourceObj, Type targetType)
	{
		Object re=null;
		
		boolean canConvert=true;
		
		if(String.class.equals(targetType))
			re=sourceObj.toString();
		else if(SoybeanMilkUtils.isEnum(targetType))
		{
			Class enumClass=SoybeanMilkUtils.narrowToClassType(targetType);
			re= (sourceObj instanceof String) ?
					Enum.valueOf(enumClass, (String)sourceObj) : Enum.valueOf(enumClass, sourceObj.toString());
		}
		else if(targetType instanceof Class<?>)
		{
			Class<?> targetClass=(Class<?>)targetType;
			
			if(SoybeanMilkUtils.isArray(sourceObj.getClass()) && SoybeanMilkUtils.isArray(targetClass))
			{
				re=convertArrayToArray(sourceObj, targetClass.getComponentType());
			}
			else
				canConvert=false;
		}
		else if(targetType instanceof GenericType)
		{
			re=convertToGenericType(sourceObj, (GenericType)targetType);
		}
		else if(targetType instanceof ParameterizedType
				|| targetType instanceof GenericArrayType
				|| targetType instanceof TypeVariable<?>
				|| targetType instanceof WildcardType)
		{
			re=convertToGenericType(sourceObj, GenericType.getGenericType(targetType, null));
		}
		else
			canConvert=false;
		
		if(!canConvert)
			throw new GenericConvertException("can not find Converter for converting '"+sourceObj.getClass().getName()+"' to '"+targetType+"'");
		
		return re;
	}
	
	/**
	 * 使用转换器转换对象
	 * @param converter
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @date 2011-4-10
	 */
	protected Object doConvert(Converter converter, Object sourceObj, Type targetType)
	{
		try
		{
			return converter.convert(sourceObj, targetType);
		}
		catch(Exception e)
		{
			throw new ConvertException(sourceObj, targetType, e);
		}
	}
	
	/**
	 * 将对象转换为泛型类型
	 * @param sourceObj
	 * @param genericType
	 * @return
	 * @date 2011-10-12
	 */
	protected Object convertToGenericType(Object sourceObj, GenericType genericType)
	{
		Object re=null;
		
		boolean isSrcArray=SoybeanMilkUtils.isArray(sourceObj.getClass());
		boolean canConvert=true;
		
		if(genericType.isParameterizedType())
		{
			Class<?> actualClass=genericType.getActualClass();
			Class<?>[] argClasses=genericType.getParamClasses();
			
			if(isSrcArray)
			{
				//List<T>
				if(SoybeanMilkUtils.isAncestorClass(List.class, actualClass))
				{
					re=convertArrayToList(sourceObj, actualClass, argClasses[0]);
				}
				//Set<T>
				else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualClass))
				{
					List<Object> list=convertArrayToList(sourceObj, List.class, argClasses[0]);
					re=listToSet(list, actualClass);
				}
				else
					canConvert=false;
			}
			else
				canConvert=false;
		}
		//T[]
		else if(isSrcArray && genericType.isGenericArrayType())
		{
			Class<?> componentClass=genericType.getComponentClass();
			re=convertArrayToArray(sourceObj, componentClass);
		}
		//T
		else if(genericType.isTypeVariable())
		{
			Class<?> actualClass=genericType.getActualClass();
			re=convert(sourceObj, actualClass);
		}
		//? extends SomeType
		else if(genericType.isWildcardType())
		{
			Class<?> actualClass=genericType.getActualClass();
			re=convert(sourceObj, actualClass);
		}
		else
			canConvert=false;
		
		if(!canConvert)
			throw new GenericConvertException("can not find Converter for converting '"+sourceObj.getClass().getName()+"' to '"+genericType.getType()+"'");
		
		return re;
	}
	
	/**
	 * 由数组转换为{@linkplain java.util.List List}对象，它不会对数组元素执行类型转换。
	 * @param array
	 * @param listClass
	 * @return
	 * @date 2011-1-5
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> convertArrayToList(Object array, Class<?> listClass, Class<?> elementClass)
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
	protected Object convertArrayToArray(Object array, Class<?> elementClass)
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
	protected Set<Object> listToSet(List<?> list, Class<?> setClass)
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
	 * @param targetElementType 目标数组的元素类型
	 * @return
	 * @date 2012-2-19
	 */
	protected Object[] listToArray(List<?> list)
	{
		Object[] result=null;
		
		if(list!=null)
		{
			result=new Object[list.size()];
			list.toArray(result);
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
	protected void setProperty(Object obj, PropertyInfo propertyInfo, Object value)
	{
		Type targetType=propertyInfo.getGenericType();
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
	protected Object getProperty(Object obj, PropertyInfo propertyInfo, Type targetType)
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
				throw new GenericConvertException("exception occur while creating instance of class '"+objectType+"' ",e);
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
	 * 拆分属性表达式
	 * @param propertyExpression
	 * @return
	 * @date 2010-12-30
	 */
	protected String[] splitPropertyExpression(String propertyExpression)
	{
		String[] propertyArray=SoybeanMilkUtils.split(propertyExpression, Constants.ACCESSOR);
		if(propertyArray==null || propertyArray.length==0)
			propertyArray=new String[]{propertyExpression};
		
		return propertyArray;
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
			super();
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
}
