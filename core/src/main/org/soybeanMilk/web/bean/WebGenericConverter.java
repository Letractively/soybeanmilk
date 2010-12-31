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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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
			Object dv = getDefaultValueWhenException(sourceObj, targetType, c, e);
			
			if(log.isDebugEnabled())
				log.debug("default value '"+dv+"' is used while converting '"+sourceObj+"' to '"+targetType.getName()+"' because the following exception :", e);
			
			return dv;
		}
	}
	
	/**
	 * 将映射表转换成<code>targetType</code>类型的对象
	 * @param originalValueMap 值映射表，它也可能包含与<code>targetType</code>类属性无关的关键字
	 * @param targetType
	 * @return
	 */
	protected Object convertMap(Map<String, Object> originalValueMap, Class<?> targetType)
	{
		if(targetType == null)
			return originalValueMap;
		if(originalValueMap==null || targetType.isAssignableFrom(originalValueMap.getClass()))
			return originalValueMap;
		
		if(targetType.isArray())
			return convertMapToJavaBeanArray(originalValueMap, targetType.getComponentType());
		
		Object bean = null;
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(targetType);
		
		//TODO 添加属性为数组、List、Set的转换支持
		
		Set<String> keys=originalValueMap.keySet();
		for(String k : keys)
		{
			String[] propertyExpression=splitPropertyExpression(k);
			
			//剔除无关属性
			if(beanInfo.getSubPropertyInfo(propertyExpression[0]) != null)
			{
				//延迟初始化
				if(bean == null)
					bean = instance(beanInfo.getPropertyType(), -1);
				
				setProperty(bean, beanInfo, propertyExpression, 0, originalValueMap.get(k));
			}
		}
		
		return bean;
	}
	
	/**
	 * 由映射表转换为泛型<code>java.util.Set</code>。
	 * @param valueMap
	 * @param setClass
	 * @return
	 * @date 2010-12-31
	 */
	@SuppressWarnings("unchecked")
	protected Set<?> convertMapToJavaBeanSet(Map<String, Object> valueMap, Class<?> setClass)
	{
		Set re=null;
		
		//TODO 取得明确类型
		Class<?> elementClass=null;
		
		Object[] ary=convertMapToJavaBeanArray(valueMap, elementClass);
		if(ary != null)
		{
			re=(Set)instance(setClass, -1);
			for(Object o : ary)
				re.add(o);
		}
		
		return re;
	}
	
	/**
	 * 由映射表转换为泛型<code>java.util.List</code>。
	 * @param valueMap
	 * @param listClass
	 * @return
	 * @date 2010-12-31
	 */
	@SuppressWarnings("unchecked")
	protected List<?> convertMapToJavaBeanList(Map<String, Object> valueMap, Class<?> listClass)
	{
		List re=null;
		
		//TODO 取得明确类型
		Class<?> elementClass=null;
		
		Object[] ary=convertMapToJavaBeanArray(valueMap, elementClass);
		if(ary != null)
		{
			re=(List)instance(listClass, -1);
			for(Object o : ary)
				re.add(o);
		}
		
		return re;
	}
	
	/**
	 * 由映射表转换为JavaBean数组，<code>valueMap</code>中值为<code>null</code>和关键字不是<code>javaBeanClass</code>类属性的元素将被忽略，
	 * 其他元素必须是数组并且长度一致。<br>
	 * 此方法不支持嵌套数组和集合（即<code>javaBeanClass</code>不能包含数组和集合类属性）。
	 * @param valueMap
	 * @param javaBeanClass
	 * @return 元素为<code>javaBeanClass</code>类型且长度为<code>valueMap</code>值元素长度的数组
	 * @date 2010-12-31
	 */
	protected Object[] convertMapToJavaBeanArray(Map<String, Object> valueMap, Class<?> javaBeanClass)
	{
		if(valueMap==null || valueMap.size()==0)
			return null;
		
		Object[] re=null;
		int len=-1;
		
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(javaBeanClass);
		
		Set<String> keys=valueMap.keySet();
		for(String key : keys)
		{
			Object value=valueMap.get(key);
			if(value == null)
				continue;
			
			if(!value.getClass().isArray())
				throw new ConvertException("the element in the source map must be an array");
			
			int l=Array.getLength(value);
			if(len == -1)
				len=l;
			else
				if(l != len)
					throw new ConvertException("the element array in the source map must be the same length");
			
			String[] propertyExp=splitPropertyExpression(key);
			if(beanInfo.getSubPropertyInfo(propertyExp[0]) != null)
			{
				//延迟初始化
				if(re == null)
				{
					re=(Object[])instance(javaBeanClass, len);
					for(int i=0;i<len;i++)
						re[i]=instance(javaBeanClass, -1);
				}
				
				for(int i=0;i<len;i++)
					setProperty(re[i], beanInfo, propertyExp, 0, Array.get(value, i));
			}
		}
		
		return re;
	}
	
	/**
	 * 当出现转换异常时，使用此方法返回的值作为转换结果。
	 * @param sourceObj
	 * @param targetType
	 * @param converter
	 * @param e
	 * @return
	 * @date 2010-12-31
	 */
	protected Object getDefaultValueWhenException(Object sourceObj, Class<?> targetType, Converter converter, Exception e)
	{
		return getDefaultValue(targetType);
	}
	
	/**
	 * 取得JavaBean某个参数化属性的实际参数类型
	 * @param beanInfo
	 * @param property
	 * @return
	 * @date 2010-12-31
	 */
	protected Class<?> getPropertyFirstParamType(PropertyInfo beanInfo, String property)
	{
		PropertyInfo pi=beanInfo.getSubPropertyInfo(property);
		if(pi == null)
			throw new ConvertException("no property '"+property+"' found in class '"+beanInfo.getClass()+"'");
		
		Type type=pi.getWriteMethod().getGenericParameterTypes()[0];
		if(!(type instanceof ParameterizedType))
			throw new ConvertException("'"+type+"' is not parameterized type");
		
		Type pa=((ParameterizedType)type).getActualTypeArguments()[0];
		if(!(pa instanceof Class<?>))
			throw new ConvertException("invalide parameterized type '"+pa+"'");
		
		return (Class<?>)pa;
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