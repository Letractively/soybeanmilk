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
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.PropertyInfo;

/**
 * WEB通用转换器，除了继承的转换支持，它还支持将{@link Map Map&lt;String, Object&gt;}转换为JavaBean对象。<br>
 * 如果源对象是数组并且长度为<code>1</code>，而目标类型不是数组，并且你没有定义对应的转换器，那么源数组对象的第一个元素将被用于转换。
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
	
	@Override
	@SuppressWarnings("unchecked")
	public Object convert(Object sourceObj, Class<?> targetType)
	{
		if(sourceObj instanceof Map)
			return convertMap((Map<String, Object>)sourceObj, targetType);
		else
			return convertWithSupportConverter(sourceObj, targetType);
	}
	
	@Override
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
			Object dv = getDefaultValue(targetType);
			if(log.isDebugEnabled())
				log.debug("the source object is null, so the default value '"+dv+"' of type '"+targetType+"' will be used");
			
			return dv;
		}
		
		if(toWrapperClass(targetType).isAssignableFrom(sourceObj.getClass()))
			return sourceObj;
		
		Converter c = getConverter(sourceObj.getClass(), targetType);
		
		//如果源对象是数组并且长度为1，而标类型不是，则使用数组的第一个元素转换
		if(c==null && sourceObj.getClass().isArray() && Array.getLength(sourceObj)==1 && !targetType.isArray())
		{
			if(log.isDebugEnabled())
				log.debug("the source '"+getStringDesc(sourceObj)+"' is an array an array and the length is 1, while the target Class is not, so it's first element will be used for converting");
			
			sourceObj=Array.getLength(sourceObj) ==0 ? null : Array.get(sourceObj, 0);
			
			if(sourceObj == null)
			{
				Object dv = getDefaultValue(targetType);
				if(log.isDebugEnabled())
					log.debug("the source object is null, so the default value '"+dv+"' of type '"+targetType+"' will be used");
				
				return dv;
			}
			
			if(toWrapperClass(targetType).isAssignableFrom(sourceObj.getClass()))
				return sourceObj;
			
			c = getConverter(sourceObj.getClass(), targetType);
		}
		
		//如果目标是字符串，并且没有对应的转换器，则调用toString()
		if(c==null && String.class.equals(targetType))
		{
			if(log.isDebugEnabled())
				log.debug("'toString()' method will be used for converting because the expected type is 'String' but not Converter found");
			
			return sourceObj.toString();
		}
		
		if(c == null)
			throw new ConvertException("no Converter defined for converting '"+sourceObj.getClass().getName()+"' to '"+targetType.getName()+"'");
		
		try
		{
			return c.convert(sourceObj, targetType);
		}
		catch(Exception e)
		{
			Object dv = getDefaultValue(targetType);
			if(log.isDebugEnabled())
				log.debug("default value '"+dv+"' is used while converting '"+sourceObj+"' to '"+targetType.getName()+"' because the following exception :", e);
			
			return dv;
		}
	}
	
	/**
	 * 将映射表转换成<code>targetType</code>类型的对象
	 * @param valueMap 值映射表，它也可能包含与<code>targetType</code>类属性无关的关键字
	 * @param targetType
	 * @return
	 */
	protected Object convertMap(Map<String, Object> valueMap, Class<?> targetType)
	{
		if(valueMap==null || targetType.isAssignableFrom(valueMap.getClass()))
			return valueMap;
		
		Object bean = null;
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(targetType);
		
		Set<String> keys=valueMap.keySet();
		for(String k : keys)
		{
			String[] propertyExpression=k.split(ACCESSOR_REGEX);
			if(propertyExpression==null || propertyExpression.length==0)
				propertyExpression=new String[]{k};
			
			//剔除无关属性
			if(beanInfo.getSubPropertyInfo(propertyExpression[0]) != null)
			{
				//延迟初始化
				if(bean == null)
					bean = instance(beanInfo.getPropertyType(), -1);
				
				setPropertyValue(bean, beanInfo, propertyExpression, 0, valueMap.get(k));
			}
		}
		
		return bean;
	}
	
	/**
	 * 设置JavaBean某个属性的值
	 * @param bean JavaBean对象
	 * @param beanInfo 该对象的Bean信息
	 * @param propertyExpression 属性层级数组，比如["address","home"]表示该Bean的address属性的home属性
	 * @param index 当前正在处理的属性层级数组的索引
	 * @param srcValue 属性对应的值
	 */
	private void setPropertyValue(Object bean, PropertyInfo beanInfo, String[] propertyExpression, int index, Object srcValue)
	{
		if(index >= propertyExpression.length)
			return;
		
		PropertyInfo propertyInfo=beanInfo.getSubPropertyInfo(propertyExpression[index]);
		if(propertyInfo == null)
			throw new ConvertException("can not find property '"+propertyExpression[index]+"' in class '"+beanInfo.getPropertyType().getName()+"'");
		
		//自上而下递归，到达末尾时初始化和写入
		if(index == propertyExpression.length-1)
		{
			Object destValue=convertWithSupportConverter(srcValue, propertyInfo.getPropertyType());
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
			setPropertyValue(propertyInstance, propertyInfo, propertyExpression, index+1, srcValue);
			
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
	 * 取得类型的默认值
	 * @param type
	 * @return
	 */
	public static Object getDefaultValue(Class<?> type)
	{
		if(boolean.class == type)
			return false;
		else if(byte.class == type)
			return (byte)0;
		else if(char.class == type)
			return (char)0;
		else if(double.class == type)
			return (double)0;
		else if(float.class == type)
			return (float)0;
		else if(int.class == type)
			return 0;
		else if(long.class == type)
			return (long)0;
		else if(short.class == type)
			return (short)0;
		else
			return null;
	}
}