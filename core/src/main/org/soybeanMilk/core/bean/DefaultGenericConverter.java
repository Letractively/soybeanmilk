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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.bean.converters.ArrayConverter;
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
import org.soybeanMilk.web.WebConstants;

/**
 * 通用转换器的默认实现。<br>
 * 它默认支持的类型转换如下所示：<br>
 * <table border="1" cellspacing="1" cellpadding="3">
 *   <tr><td>源类型</td><td>目标类型</td></tr>
 *   <tr>
 *   	<td>String</td>
 *   	<td>boolean, Boolean; byte, Byte; char, Character; double, Double; float, Float;<br>
 *   		int, Integer; long, Long; short, Short;<br>
 *   		java.math.BigDecimal; java.math.BigInteger; java.util.Date; java.sql.Date; java.sql.Time; java.sql.Timestamp
 *   	</td>
 *   </tr>
 *   
 *   
 *   <tr>
 *   	<td>String[]</td>
 *   	<td>boolean[], Boolean[]; byte[], Byte[]; char[], Character[]; double[], Double[]; float[], Float[];<br>
 *   		int[], Integer[]; long[], Long[]; short[], Short[];<br>
 *   		java.math.BigDecimal[]; java.math.BigInteger[]; java.util.Date[]; java.sql.Date[]; java.sql.Time[];<br>
 *   		java.sql.Timestamp[]
 *   </td></tr>
 * </table>
 * <br>
 * 另外，如果目标类型为<code>String</code>，而你没有添加此对象到<code>String</code>类型的辅助转换器，那么它将返回此对象的<code>toString()</code>结果<br>
 * 你也可以通过{@link #addConverter(Class, Class, Converter)}为它添加更多辅助转换器，使其支持更多的类型转换。
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 */
public class DefaultGenericConverter implements GenericConverter
{
	private static Log log = LogFactory.getLog(DefaultGenericConverter.class);
	
	protected static final Object[] EMPTY_ARGS={};
	
	protected static final String ACCESSOR_REGEX="\\"+WebConstants.ACCESSOR;
	
	protected static final String SEPRATOR="->";
	
	private Map<String,Converter> converters;
	
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
		{
			addStringSourceConverters();
			addStringArraySourceConverters();
		}
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
	
	@Override
	public Object convert(Object sourceObj, Class<?> targetType)
	{
		return convertWithSupportConverter(sourceObj, targetType);
	}
	
	@Override
	public Object getProperty(Object srcObj, String propertyExpression, Class<?> targetType)
	{
		//TODO 实现
		return null;
	}
	
	@Override
	public void setProperty(Object srcObj, String propertyExpression, Object value)
	{
		//TODO 实现
	}
	
	@Override
	public void addConverter(Class<?> sourceType,Class<?> targetType,Converter converter)
	{
		if(getConverters() == null)
			setConverters(new HashMap<String, Converter>());
		
		getConverters().put(generateConverterKey(sourceType, targetType), converter);
		
		if(log.isDebugEnabled())
			log.debug("add a support Converter '"+converter.getClass().getName()+"' for converting '"+sourceType.getName()+"' to '"+targetType.getName()+"'");
	}
	
	@Override
	public Converter getConverter(Class<?> sourceType, Class<?> targetType)
	{
		return getConverters() == null ? null : getConverters().get(generateConverterKey(sourceType, targetType));
	}
	
	/**
	 * 使用辅助转换器转换类型
	 * @param sourceObj 源对象
	 * @param targetType 目标类型
	 * @return
	 * @throws ConvertException
	 * @date 2010-12-28
	 */
	protected Object convertWithSupportConverter(Object sourceObj, Class<?> targetType) throws ConvertException
	{
		if(log.isDebugEnabled())
			log.debug("start converting '"+getStringDesc(sourceObj)+"' of type '"
					+(sourceObj == null ? null : sourceObj.getClass().getName())+"' to type '"
					+(targetType==null ? null : targetType.getName())+"'");
		
		if(targetType == null)
		{
			if(log.isDebugEnabled())
				log.debug("the target type is null, so the source object will be returned directly");
			
			return sourceObj;
		}
		
		if(sourceObj == null)
		{
			if(targetType.isPrimitive())
				throw new ConvertException("can not convert null to primitive type");
			else
				return null;
		}
		
		if(toWrapperClass(targetType).isAssignableFrom(sourceObj.getClass()))
			return sourceObj;
		
		Converter c = getConverter(sourceObj.getClass(), targetType);
		if(c == null)
		{
			if(targetType.equals(String.class))
				return sourceObj.toString();
			else
				throw new ConvertException("can not find Converter for converting '"+sourceObj.getClass().getName()+"' to '"+targetType.getName()+"'");
		}
		
		try
		{
			return c.convert(sourceObj, targetType);
		}
		catch(Exception e)
		{
			throw new ConvertException(e);
		}
	}
	
	/**
	 * 产生转换器关键字，用于在映射表中标识转换器
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	protected String generateConverterKey(Class<?> sourceClass,Class<?> targetClass)
	{
		return sourceClass.getName()+SEPRATOR+targetClass.getName();
	}
	
	/**
	 * 创建给定类型的实例。<br>
	 * 如果此类是集合类接口<code>List</code>、<code>Map</code>、<code>Set</code>，它将创建默认实例，<code>arraySize</code>将被忽略；<br>
	 * 否则，此类必须提供一个无参的构造方法，如果<code>arrayLength</code>不小于0，它将创建这个类型的数组实例，否则，仅创建此类的实例。
	 * @param objectType 类型
	 * @param arrayLength 要创建数组的长度
	 * @return
	 * @throws ConvertException
	 * @date 2010-12-29
	 */
	@SuppressWarnings("unchecked")
	protected Object instance(Class<?> objectType, int arrayLength) throws ConvertException
	{
		if(java.util.List.class.equals(objectType))
			return new ArrayList();
		else if(java.util.Map.class.equals(objectType))
			return new HashMap();
		else if(java.util.Set.class.equals(objectType))
			return new HashSet();
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
				throw new ConvertException("exception occur while creating instance for class '"+objectType+"' ",e);
			}
			
		}
	}

	protected Map<String, Converter> getConverters() {
		return converters;
	}

	protected void setConverters(Map<String, Converter> converters) {
		this.converters = converters;
	}
	
	/**
	 * 添加可以将字符串转换到原子类型的辅助转换器
	 */
	protected void addStringSourceConverters()
	{
		//基本类型
		addConverter(String.class, boolean.class, new BooleanConverter());
		addConverter(String.class, byte.class, new ByteConverter());
		addConverter(String.class, char.class, new CharacterConverter());
		addConverter(String.class, double.class, new DoubleConverter());
		addConverter(String.class, float.class, new FloatConverter());
		addConverter(String.class, int.class, new IntegerConverter());
		addConverter(String.class, long.class, new LongConverter());
		addConverter(String.class, short.class, new ShortConverter());
		
		//包装类型
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
	 * 添加可以将字符串数组转换到原子类型数组的辅助转换器
	 */
	protected void addStringArraySourceConverters()
	{
		//基本类型
		addConverter(String[].class, boolean[].class, new ArrayConverter(new BooleanConverter()));
		addConverter(String[].class, byte[].class, new ArrayConverter(new ByteConverter()));
		addConverter(String[].class, char[].class, new ArrayConverter(new CharacterConverter()));
		addConverter(String[].class, double[].class, new ArrayConverter(new DoubleConverter()));
		addConverter(String[].class, float[].class, new ArrayConverter(new FloatConverter()));
		addConverter(String[].class, int[].class, new ArrayConverter(new IntegerConverter()));
		addConverter(String[].class, long[].class, new ArrayConverter(new LongConverter()));
		addConverter(String[].class, short[].class, new ArrayConverter(new ShortConverter()));
		
		//包装类型
		addConverter(String[].class, Boolean[].class, new ArrayConverter(new BooleanConverter()));
		addConverter(String[].class, Byte[].class, new ArrayConverter(new ByteConverter()));
		addConverter(String[].class, Character[].class, new ArrayConverter(new CharacterConverter()));
		addConverter(String[].class, Double[].class, new ArrayConverter(new DoubleConverter()));
		addConverter(String[].class, Float[].class, new ArrayConverter(new FloatConverter()));
		addConverter(String[].class, Integer[].class, new ArrayConverter(new IntegerConverter()));
		addConverter(String[].class, Long[].class, new ArrayConverter(new LongConverter()));
		addConverter(String[].class, Short[].class, new ArrayConverter(new ShortConverter()));
		
		//其他
		addConverter(String[].class, java.math.BigDecimal[].class, new ArrayConverter(new BigDecimalConverter()));
		addConverter(String[].class, java.math.BigInteger[].class, new ArrayConverter(new BigIntegerConverter()));
		addConverter(String[].class, java.util.Date[].class, new ArrayConverter(new DateConverter()));
		addConverter(String[].class, java.sql.Date[].class, new ArrayConverter(new SqlDateConverter()));
		addConverter(String[].class, java.sql.Time[].class, new ArrayConverter(new SqlTimeConverter()));
		addConverter(String[].class, java.sql.Timestamp[].class, new ArrayConverter(new SqlTimestampConverter()));
	}
	
	/**
	 * 返回基本类型的包装类型，如果不是基本类型，它将直接被返回
	 * @param type
	 * @return
	 */
	public static Class<?> toWrapperClass(Class<?> type)
	{
		if (type == null || !type.isPrimitive())
            return type;
		
		if (type == Integer.TYPE)
        	return Integer.class;
        else if (type == Double.TYPE)
            return Double.class;
        else if (type == Long.TYPE)
            return Long.class;
        else if (type == Boolean.TYPE)
            return Boolean.class;
        else if (type == Float.TYPE)
            return Float.class;
        else if (type == Short.TYPE)
            return Short.class;
        else if (type == Byte.TYPE)
            return Byte.class;
        else if (type == Character.TYPE)
            return Character.class;
        else
            return type;
	}
	
	/**
	 * 取得对象的字符串描述
	 * @param o
	 * @return
	 */
	protected String getStringDesc(Object o)
	{
		if(o == null)
			return "null";
		else if(o.getClass().isArray())
		{
			StringBuffer cache = new StringBuffer();
			cache.append('[');
			
			for(int i=0,len=Array.getLength(o); i < len; i++)
			{
				Object e = Array.get(o, i);
				cache.append(getStringDesc(e));
				
				if(i < len-1)
					cache.append(',');
			}
			
			cache.append(']');
			
			return cache.toString();
		}
		else
			return o.toString();
	}
}
