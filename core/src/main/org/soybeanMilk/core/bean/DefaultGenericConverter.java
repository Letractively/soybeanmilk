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
import java.lang.reflect.Type;
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
			log.debug("start converting '"+getStringDesc(sourceObj)
					+"' of type '"+(sourceObj==null ? null : sourceObj.getClass())+"' to type '"+targetType+"'");
		
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
			log.debug("start getting '"+srcObj+"' property '"+propertyExpression+"'");
		
		return getProperty(srcObj, PropertyInfo.getPropertyInfo(srcObj.getClass()), splitPropertyExpression(propertyExpression), expectType);
	}
	
	//@Override
	public void setProperty(Object srcObj, String propertyExpression, Object value)
	{
		if(srcObj == null)
			throw new IllegalArgumentException("[srcObj] must not be null");
		if(propertyExpression==null || propertyExpression.length()==0)
			throw new IllegalArgumentException("[propertyExpression] must not be empty");
		
		if(log.isDebugEnabled())
			log.debug("start setting '"+srcObj+"' property '"+propertyExpression+"' to value '"+value+"'");
		
		setProperty(srcObj, PropertyInfo.getPropertyInfo(srcObj.getClass()), splitPropertyExpression(propertyExpression), 0, value);
	}
	
	//@Override
	public void addConverter(Type sourceType,Type targetType,Converter converter)
	{
		if(getConverters() == null)
			setConverters(new HashMap<String, Converter>());
		
		getConverters().put(generateConverterKey(sourceType, targetType), converter);
		
		if(log.isDebugEnabled())
			log.debug("add a support Converter '"+converter.getClass().getName()+"' for converting '"+sourceType+"' to '"+targetType+"'");
	}
	
	//@Override
	public Converter getConverter(Type sourceType, Type targetType)
	{
		Converter re=null;
		Map<String,Converter> converters=getConverters();
		
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
		
		if(targetType.equals(String.class))
			re=sourceObj.toString();
		else if(SoybeanMilkUtils.isEnum(targetType))
		{
			Class enumClass=SoybeanMilkUtils.narrowToClassType(targetType);
			re= (sourceObj instanceof String) ?
					Enum.valueOf(enumClass, (String)sourceObj) : Enum.valueOf(enumClass, sourceObj.toString());
		}
		else if(sourceObj.getClass().isArray())
		{
			Class<?>[] actualTypes=SoybeanMilkUtils.getActualClassTypeInfo(targetType);
			
			if(SoybeanMilkUtils.isArray(actualTypes[0]))
			{
				re=convertArrayToArray(sourceObj, actualTypes[0].getComponentType());
			}
			else if(SoybeanMilkUtils.isAncestorClass(List.class, actualTypes[0]))
			{
				if(actualTypes.length != 2)
					throw new GenericConvertException("'"+targetType+"' is invalid, only generic List converting is supported");
				
				re=convertArrayToList(convertArrayToArray(sourceObj, actualTypes[1]), actualTypes[0]);
			}
			else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualTypes[0]))
			{
				if(actualTypes.length != 2)
					throw new GenericConvertException("'"+targetType+"' is invalid, only generic Set converting is supported");
				
				re=convertArrayToSet(convertArrayToArray(sourceObj, actualTypes[1]), actualTypes[0]);
			}
			else
				canConvert=false;
		}
		else
			canConvert=false;
		
		if(canConvert)
			return re;
		else
			throw new GenericConvertException("can not find Converter for converting '"+sourceObj.getClass().getName()+"' to '"+targetType+"'");
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
	 * 由数组转换为{@linkplain java.util.List List}，它不会对数组元素执行类型转换。
	 * @param array
	 * @param listClass
	 * @return
	 * @date 2011-1-5
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertArrayToList(Object array, Class<?> listClass)
	{
		List re=(List)instance(listClass, -1);
		
		for(int i=0,len=Array.getLength(array);i<len;i++)
			re.add(Array.get(array, i));
		
		return re;
	}
	
	/**
	 * 由数组转换为{@linkplain java.util.Set Set}，它不会对数组元素执行类型转换。
	 * @param array
	 * @param setClass
	 * @return
	 * @date 2011-1-5
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertArrayToSet(Object array, Class<?> setClass)
	{
		Set re=(Set)instance(setClass, -1);
		
		for(int i=0,len=Array.getLength(array);i<len;i++)
			re.add(Array.get(array, i));
		
		return re;
	}
	
	/**
	 * 由一种数组转换到为另一个对象数组。
	 * @param sourceObj 源对象
	 * @param targetElementType 目标数组的元素类型
	 * @return
	 * @date 2011-1-5
	 */
	protected Object convertArrayToArray(Object sourceObj, Class<?> targetElementType)
	{
		if(!sourceObj.getClass().isArray())
			throw new GenericConvertException("the source object must be an array");
		
		int len=Array.getLength(sourceObj);
		Object re=Array.newInstance(targetElementType, len);
		
		for(int i=0;i<len;i++)
			Array.set(re, i, convert(Array.get(sourceObj, i), targetElementType));
		
		return re;
	}
	
	/**
	 * 设置JavaBean某个属性的值，如果中间属性对象不存在，这个方法将会尝试创建它，如果<code>value</code>与对象属性不一致，它将尝试执行类型转换。<br>
	 * 目前不支持中间属性是数组或集合类。
	 * @param bean JavaBean对象
	 * @param beanInfo 此对象的属性信息
	 * @param propertyExpressionArray 属性层级数组，比如["address","home"]表示此对象的address属性的home属性
	 * @param index 当前正在处理的属性层级数组的索引
	 * @param value 属性对应的值
	 */
	protected void setProperty(Object bean, PropertyInfo beanInfo, String[] propertyExpressionArray, int index, Object value)
	{
		PropertyInfo propertyInfo=beanInfo.getSubPropertyInfo(propertyExpressionArray[index]);
		if(propertyInfo == null)
			throw new GenericConvertException("can not find property '"+propertyExpressionArray[index]+"' in class '"+beanInfo.getType().getName()+"'");
		
		//自上而下递归，到达末尾时初始化和写入
		if(index == propertyExpressionArray.length-1)
		{
			Object destValue=convert(value, propertyInfo.getGenericType());
			try
			{
				propertyInfo.getWriteMethod().invoke(bean, new Object[]{destValue});
			}
			catch(Exception e)
			{
				throw new GenericConvertException("exception occur while calling write method '"+propertyInfo.getWriteMethod()+"'",e);
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
				throw new GenericConvertException("exception occur while calling read method '"+propertyInfo.getReadMethod().getName()+"'",e);
			}
			
			//初始化
			if(propertyInstance == null)
			{
				propertyInstance=instance(propertyInfo.getType(), -1);
				toWrite=true;
			}
			
			//先将对象构建完成，再写入
			setProperty(propertyInstance, propertyInfo, propertyExpressionArray, index+1, value);
			
			//如果之前已经写入了该对象，则不需要再写一次
			if(toWrite)
			{
				try
				{
					propertyInfo.getWriteMethod().invoke(bean, new Object[]{propertyInstance});
				}
				catch(Exception e)
				{
					throw new GenericConvertException("exception occur while calling write method '"+propertyInfo.getWriteMethod().getName()+"'",e);
				}
			}
		}
	}
	
	/**
	 * 获取属性值，如果中间属性为<code>null</code>，将直接返回<code>null</code>。<br>
	 * 目前不支持中间属性是数组或集合类。
	 * @param bean JavaBean对象
	 * @param beanInfo 此对象的属性信息
	 * @param propertyExpression 属性层级数组，比如["address","home"]表示此对象的address属性的home属性
	 * @param targetType 期望转换的目标类型，为<code>null</code>则表示不转换
	 * @return
	 * @date 2010-12-30
	 */
	protected Object getProperty(Object bean, PropertyInfo beanInfo, String[] propertyExpression, Type targetType)
	{
		PropertyInfo tmpPropInfo=null;
		for(int i=0;i<propertyExpression.length;i++)
		{
			if(bean == null)
				break;
			
			tmpPropInfo = beanInfo.getSubPropertyInfo(propertyExpression[i]);
			if(tmpPropInfo == null)
				throw new GenericConvertException("can not find property '"+propertyExpression[i]+"' in class '"+beanInfo.getType().getName()+"'");
			else
				beanInfo=tmpPropInfo;
			
			try
			{
				bean=beanInfo.getReadMethod().invoke(bean, EMPTY_ARGS);
			}
			catch(Exception e)
			{
				throw new GenericConvertException("exception occur while calling read method '"+tmpPropInfo.getReadMethod().getName()+"'",e);
			}
		}
		
		return convert(bean, targetType);
	}
	
	/**
	 * 产生转换器关键字，用于在映射表中标识转换器
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	protected String generateConverterKey(Type sourceType, Type targetType)
	{
		return sourceType.toString()+SEPRATOR+targetType.toString();
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
	 * 组装属性表达式数组为字符串
	 * @param propExpressionAry
	 * @param start
	 * @param end
	 * @return
	 * @date 2011-1-4
	 */
	protected String assemblePropertyExpression(String[] propExpressionAry, int start, int end)
	{
		StringBuffer re=new StringBuffer();
		
		for(int i=start;i<end;i++)
		{
			re.append(propExpressionAry[i]);
			
			if(i<end-1)
				re.append(Constants.ACCESSOR);
		}
		
		return re.toString();
	}
	
	/**
	 * 查找转换器，返回结果不会为<code>null</code>
	 * @param genericConverter
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @date 2011-4-14
	 */
	public static Converter getConverterNotNull(GenericConverter genericConverter, Type sourceType, Type targetType)
	{
		Converter cvt=genericConverter.getConverter(sourceType, targetType);
		if(cvt == null)
			throw new GenericConvertException("no Converter defined for converting '"+sourceType+"' to '"+targetType+"'");
		
		return cvt;
	}
}
