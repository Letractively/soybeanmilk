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
		if(srcObj == null)
			throw new IllegalArgumentException("[srcObj] must not be null");
		if(propertyExpression==null || propertyExpression.length()==0)
			throw new IllegalArgumentException("[propertyExpression] must not be empty");
		
		return getProperty(srcObj, PropertyInfo.getPropertyInfo(srcObj.getClass()), splitPropertyExpression(propertyExpression), targetType);
	}
	
	@Override
	public void setProperty(Object srcObj, String propertyExpression, Object value)
	{
		if(srcObj == null)
			throw new IllegalArgumentException("[srcObj] must not be null");
		if(propertyExpression==null || propertyExpression.length()==0)
			throw new IllegalArgumentException("[propertyExpression] must not be empty");
		
		setProperty(srcObj, PropertyInfo.getPropertyInfo(srcObj.getClass()), splitPropertyExpression(propertyExpression), 0, value);
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
	 * @param targetType 目标类型，为<code>null</code>则表示不需转换
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
	 * 设置JavaBean某个属性的值，如果中间属性对象不存在，这个方法将会尝试创建它，如果<code>value</code>与对象属性不一致，它将尝试执行类型转换。<br>
	 * 目前不支持中间属性是数组或集合类。
	 * @param bean JavaBean对象
	 * @param beanInfo 此对象的属性信息
	 * @param propertyExpression 属性层级数组，比如["address","home"]表示此对象的address属性的home属性
	 * @param index 当前正在处理的属性层级数组的索引
	 * @param value 属性对应的值
	 */
	protected void setProperty(Object bean, PropertyInfo beanInfo, String[] propertyExpression, int index, Object value)
	{
		PropertyInfo propertyInfo=beanInfo.getSubPropertyInfo(propertyExpression[index]);
		if(propertyInfo == null)
			throw new ConvertException("can not find property '"+propertyExpression[index]+"' in class '"+beanInfo.getPropertyType().getName()+"'");
		
		//自上而下递归，到达末尾时初始化和写入
		if(index == propertyExpression.length-1)
		{
			Object destValue=convertWithSupportConverter(value, propertyInfo.getPropertyType());
			try
			{
				propertyInfo.getWriteMethod().invoke(bean, new Object[]{destValue});
			}
			catch(Exception e)
			{
				throw new ConvertException("exception occur while calling write method '"+propertyInfo.getWriteMethod()+"'",e);
			}
		}
		else
		{
			Object propertyInstance=null;
			boolean toWrite=false;
			
			//查看对象是否已经初始化
			try
			{
				propertyInstance=propertyInfo.getReadMethod().invoke(bean, EMPTY_ARGS);
			}
			catch(Exception e)
			{
				throw new ConvertException("exception occur while calling read method '"+propertyInfo.getReadMethod().getName()+"'",e);
			}
			
			//初始化
			if(propertyInstance == null)
			{
				propertyInstance=instance(propertyInfo.getPropertyType(), -1);
				toWrite=true;
			}
			
			//先将对象构建完成，再写入
			setProperty(propertyInstance, propertyInfo, propertyExpression, index+1, value);
			
			//如果之前已经写入了该对象，则不需要再写一次
			if(toWrite)
			{
				try
				{
					propertyInfo.getWriteMethod().invoke(bean, new Object[]{propertyInstance});
				}
				catch(Exception e)
				{
					throw new ConvertException("exception occur while calling write method '"+propertyInfo.getWriteMethod().getName()+"'",e);
				}
			}
		}
	}
	
	/**
	 * 获取属性值，中间属性不能为null。<br>
	 * 目前不支持中间属性是数组或集合类。
	 * @param bean JavaBean对象
	 * @param beanInfo 此对象的属性信息
	 * @param propertyExpression 属性层级数组，比如["address","home"]表示此对象的address属性的home属性
	 * @param targetType 期望转换的目标类型，为<code>null</code>则表示不转换
	 * @return
	 * @date 2010-12-30
	 */
	protected Object getProperty(Object bean, PropertyInfo beanInfo, String[] propertyExpression, Class<?> targetType)
	{
		Object property=bean;
		PropertyInfo propertyInfo=beanInfo;
		
		for(int i=0;i<propertyExpression.length;i++)
		{
			propertyInfo = propertyInfo.getSubPropertyInfo(propertyExpression[i]);
			
			if(propertyInfo == null)
				throw new ConvertException("can not find property '"+propertyExpression[i]+"' in class '"+beanInfo.getPropertyType().getName()+"'");
			if(property == null)
				throw new ConvertException("can not get property '"+propertyExpression[i]+"' from null object");
			
			try
			{
				property=propertyInfo.getReadMethod().invoke(property, EMPTY_ARGS);
			}
			catch(Exception e)
			{
				throw new ConvertException("exception occur while calling read method '"+propertyInfo.getReadMethod().getName()+"'",e);
			}
		}
		
		return convertWithSupportConverter(property, targetType);
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
	 * 如果此类是集合类接口<code>List</code>、<code>Set</code>，它将创建默认实例，<code>arraySize</code>将被忽略；<br>
	 * 否则，此类必须提供一个无参的构造方法，如果<code>arrayLength</code>不小于0，它将创建此类的数组实例，否则，仅创建此类的实例。
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
	 * 拆分属性表达式
	 * @param propertyExpression
	 * @return
	 * @date 2010-12-30
	 */
	public static String[] splitPropertyExpression(String propertyExpression)
	{
		String[] propertyArray=propertyExpression.split(ACCESSOR_REGEX);
		if(propertyArray==null || propertyArray.length==0)
			propertyArray=new String[]{propertyExpression};
		
		return propertyArray;
	}
}
