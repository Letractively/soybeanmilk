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

import java.util.HashMap;

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

/**
 * 通用转换器的默认实现。<br>
 * 它默认支持的类型转换如下所示：<br>
 * <table border="1" cellspacing="1" cellpadding="3">
 *   <tr><td>源类型</td><td>目标类型</td></tr>
 *   <tr><td rowspan="14">String</td><td>boolean, Boolean</td></tr>
 *   <tr><td>byte, Byte</td></tr>
 *   <tr><td>char, Character</td></tr>
 *   <tr><td>double, Double</td></tr>
 *   <tr><td>float, Float</td></tr>
 *   <tr><td>int, Integer</td></tr>
 *   <tr><td>long, Long</td></tr>
 *   <tr><td>short, Short</td></tr>
 *   
 *   <tr><td>java.math.BigDecimal</td></tr>
 *   <tr><td>java.math.BigInteger</td></tr>
 *   <tr><td>java.util.Date</td></tr>
 *   <tr><td>java.sql.Date</td></tr>
 *   <tr><td>java.sql.Time</td></tr>
 *   <tr><td>java.sql.Timestamp</td></tr>
 *   
 *   
 *   <tr><td rowspan="14">String[]</td><td>boolean[], Boolean[]</td></tr>
 *   <tr><td>byte[], Byte[]</td></tr>
 *   <tr><td>char[], Character[]</td></tr>
 *   <tr><td>double[], Double[]</td></tr>
 *   <tr><td>float[], Float[]</td></tr>
 *   <tr><td>int[], Integer[]</td></tr>
 *   <tr><td>long[], Long[]</td></tr>
 *   <tr><td>short[], Short[]</td></tr>
 *   
 *   <tr><td>java.math.BigDecimal[]</td></tr>
 *   <tr><td>java.math.BigInteger[]</td></tr>
 *   <tr><td>java.util.Date[]</td></tr>
 *   <tr><td>java.sql.Date[]</td></tr>
 *   <tr><td>java.sql.Time[]</td></tr>
 *   <tr><td>java.sql.Timestamp[]</td></tr>
 * </table>
 * <br>
 * 你也可以通过{@link #addConverter(Class, Class, Converter)}为它添加其他转换器，使其支持更多的类型转换。
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 */
public class DefaultGenericConverter implements GenericConverter
{
	private static Log log = LogFactory.getLog(DefaultGenericConverter.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
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
	
	@Override
	public void addConverter(Class<?> sourceClass,Class<?> targetClass,Converter converter)
	{
		if(getConverters() == null)
			setConverters(new HashMap<String, Converter>());
		
		getConverters().put(generateConverterKey(sourceClass, targetClass), converter);
		
		if(_logDebugEnabled)
			log.debug("add a support Converter '"+converter.getClass().getName()+"' for converting '"+sourceClass.getName()+"' to '"+targetClass.getName()+"'");
	}
	
	@Override
	public Converter getConverter(Class<?> sourceClass, Class<?> targetClass)
	{
		return getConverters() == null ? null : getConverters().get(generateConverterKey(sourceClass, targetClass));
	}
	
	@Override
	public Object convert(Object sourceObj, Class<?> targetClass)
	{
		if(targetClass == null)
			return sourceObj;
		
		if(sourceObj == null)
		{
			if(targetClass.isPrimitive())
				throw new ConvertException("can not convert null to primitive type");
			else
				return null;
		}
		
		if(toWrapperClass(targetClass).isAssignableFrom(sourceObj.getClass()))
			return sourceObj;
		
		Converter c = getConverter(sourceObj.getClass(), targetClass);
		if(c == null)
			throw new ConvertException("can not find Converter for converting '"+sourceObj.getClass().getName()+"' to '"+targetClass.getName()+"'");
		
		try
		{
			return c.convert(sourceObj, targetClass);
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
	
	public Map<String, Converter> getConverters() {
		return converters;
	}
	public void setConverters(Map<String, Converter> converters) {
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
}