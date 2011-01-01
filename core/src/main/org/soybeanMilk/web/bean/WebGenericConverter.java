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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SbmUtils;
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
	public Object convert(Object sourceObj, Type targetType)
	{
		if(SbmUtils.isInstanceOf(sourceObj, Map.class))
			return convertMap((Map<String, Object>)sourceObj, targetType);
		else
			return convertWithSupportConverter(sourceObj, targetType);
	}
	
	@Override
	protected Object convertWithSupportConverter(Object sourceObj, Type targetType) throws ConvertException
	{
		if(log.isDebugEnabled())
			log.debug("start converting '"+getStringDesc(sourceObj)+"' of type '"
					+(sourceObj == null ? null : sourceObj.getClass().getName())+"' to type '"
					+(targetType==null ? null : targetType)+"'");
		
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
		
		if(SbmUtils.isInstanceOf(sourceObj, targetType))
			return sourceObj;
		
		Converter c = getConverter(sourceObj.getClass(), targetType);
		
		//如果源对象是数组并且长度为1，而标类型不是，则使用数组的第一个元素转换
		if(c==null && sourceObj.getClass().isArray() && Array.getLength(sourceObj)==1 && !SbmUtils.isArray(targetType))
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
			
			if(SbmUtils.isInstanceOf(sourceObj, targetType))
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
			throw new ConvertException("no Converter defined for converting '"+sourceObj.getClass().getName()+"' to '"+targetType+"'");
		
		try
		{
			return c.convert(sourceObj, targetType);
		}
		catch(Exception e)
		{
			Object dv = getDefaultValueWhenException(sourceObj, targetType, c, e);
			
			if(log.isDebugEnabled())
				log.debug("default value '"+dv+"' is used while converting '"+sourceObj+"' to '"+targetType+"' because the following exception :", e);
			
			return dv;
		}
	}
	
	/**
	 * 将映射表转换成<code>targetType</code>类型的对象
	 * @param originalValueMap 值映射表，它也可能包含与<code>targetType</code>类属性无关的关键字
	 * @param targetType
	 * @return
	 */
	protected Object convertMap(Map<String, Object> originalValueMap, Type targetType)
	{
		if(originalValueMap==null || targetType == null)
			return originalValueMap;
		
		Object result = null;
		
		if(SbmUtils.isInstanceOf(targetType, ParameterizedType.class))
		{
			Class<?>[] genericClass=getSupportGenericType((ParameterizedType)targetType);
			if(SbmUtils.isAncestorClass(List.class, genericClass[0]))
				result=convertMapToList(originalValueMap, genericClass);
			else if(SbmUtils.isAncestorClass(Set.class, genericClass[0]))
				result=convertMapToSet(originalValueMap, genericClass);
			else
				throw new ConvertException("converting 'Map<String, Object>' to parameterized type '"+targetType+"' is not supported");
		}
		else if(SbmUtils.isInstanceOf(targetType, Class.class))
		{
			Class<?> targetClass=SbmUtils.narrowToClassType(targetType);
			
			if(SbmUtils.isAncestorClass(Map.class, targetClass))
				result=originalValueMap;
			if(targetClass.isArray())
				result=convertMapToArray(originalValueMap, targetClass.getComponentType());
			else
			{
				PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(targetClass);
				
				//TODO 添加属性为数组、List、Set的转换支持
				
				Set<String> keys=originalValueMap.keySet();
				for(String k : keys)
				{
					String[] propertyExpression=splitPropertyExpression(k);
					
					//剔除无关属性
					if(beanInfo.getSubPropertyInfo(propertyExpression[0]) != null)
					{
						//延迟初始化
						if(result == null)
							result = instance(beanInfo.getType(), -1);
						
						setProperty(result, beanInfo, propertyExpression, 0, originalValueMap.get(k));
					}
				}
			}
		}
		else
			throw new ConvertException("converting 'Map<String, Object>' to '"+targetType+"' is not supported");
		
		return result;
	}
	
	/**
	 * 由映射表转换为<code>java.util.Set</code>。
	 * @param valueMap
	 * @param setGeneric
	 * @return
	 * @date 2010-12-31
	 */
	@SuppressWarnings("unchecked")
	protected Set<?> convertMapToSet(Map<String, Object> valueMap, Class<?>[] setGeneric)
	{
		Set re=null;
		
		Object[] ary=convertMapToArray(valueMap, setGeneric[1]);
		if(ary != null)
		{
			re=(Set)instance(Set.class, -1);
			for(Object o : ary)
				re.add(o);
		}
		
		return re;
	}
	
	/**
	 * 由映射表转换为<code>java.util.List</code>。
	 * @param valueMap
	 * @param listGeneric
	 * @return
	 * @date 2010-12-31
	 */
	@SuppressWarnings("unchecked")
	protected List<?> convertMapToList(Map<String, Object> valueMap, Class<?>[] listGeneric)
	{
		List re=null;
		
		Object[] ary=convertMapToArray(valueMap, listGeneric[1]);
		if(ary != null)
		{
			re=(List)instance(listGeneric[0], -1);
			for(Object o : ary)
				re.add(o);
		}
		
		return re;
	}
	
	/**
	 * 由映射表转换为数组，<code>valueMap</code>中值为<code>null</code>和关键字不是<code>elementClass</code>类属性的元素将被忽略，
	 * 其他元素必须是数组并且长度一致。<br>
	 * 此方法不支持嵌套数组和集合（即<code>elementClass</code>不能包含数组和集合类属性）。
	 * @param valueMap
	 * @param elementClass
	 * @return 元素为<code>javaBeanClass</code>类型且长度为<code>valueMap</code>值元素长度的数组
	 * @date 2010-12-31
	 */
	protected Object[] convertMapToArray(Map<String, Object> valueMap, Class<?> elementClass)
	{
		if(valueMap==null || valueMap.size()==0)
			return null;
		
		Object[] re=null;
		int len=-1;
		
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(elementClass);
		
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
					re=(Object[])instance(elementClass, len);
					for(int i=0;i<len;i++)
						re[i]=instance(elementClass, -1);
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
	protected Object getDefaultValueWhenException(Object sourceObj, Type targetType, Converter converter, Exception e)
	{
		return getDefaultValue(targetType);
	}
	
	/**
	 * 取得参数化类型的原始类型和参数类型
	 * @param type
	 * @return
	 */
	protected Class<?>[] getSupportGenericType(ParameterizedType type)
	{
		Type[] ats=type.getActualTypeArguments();
		if(ats==null || ats.length!=1 || !SbmUtils.isClassType(ats[0]))
			throw new ConvertException("'"+type+"' is not valid, only 1 and only Class type of its actual type argument is supported");
		
		Type rt=type.getRawType();
		if(!SbmUtils.isClassType(rt))
			throw new ConvertException("'"+type+"' is not valid, only Class type of its raw type is supported");
		
		Class<?>[] re=new Class<?>[2];
		re[0]=SbmUtils.narrowToClassType(rt);
		re[1]=SbmUtils.narrowToClassType(ats[0]);
		
		return re;
	}
	
	/**
	 * 取得类型的默认值
	 * @param type
	 * @return
	 */
	public static Object getDefaultValue(Type type)
	{
		if(boolean.class.equals(type))
			return false;
		else if(byte.class.equals(type))
			return (byte)0;
		else if(char.class.equals(type))
			return (char)0;
		else if(double.class.equals(type))
			return (double)0;
		else if(float.class.equals(type))
			return (float)0;
		else if(int.class.equals(type))
			return 0;
		else if(long.class.equals(type))
			return (long)0;
		else if(short.class.equals(type))
			return (short)0;
		else
			return null;
	}
}