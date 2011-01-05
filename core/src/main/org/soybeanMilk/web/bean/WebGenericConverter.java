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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.PropertyInfo;

/**
 * WEB通用转换器，除了继承的转换支持，它还支持将{@link Map Map&lt;String, Object&gt;}转换为JavaBean对象。
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
		if(SoybeanMilkUtils.isInstanceOf(sourceObj, Map.class))
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
			Object dv = getDefaultValue(sourceObj, targetType);
			if(log.isDebugEnabled())
				log.debug("the source object is null, so the default value '"+dv+"' of type '"+targetType+"' will be used");
			
			return dv;
		}
		
		if(SoybeanMilkUtils.isInstanceOf(sourceObj, targetType))
			return sourceObj;
		
		Converter c = getConverter(sourceObj.getClass(), targetType);
		
		//如果源对象是数组并且长度为1，而标类型不是，则使用数组的第一个元素转换
		if(c==null && sourceObj.getClass().isArray() && Array.getLength(sourceObj)==1 && !SoybeanMilkUtils.isClassTypeArray(targetType))
		{
			if(log.isDebugEnabled())
				log.debug("the source '"+getStringDesc(sourceObj)+"' is an array and the length is 1, while the target Class is not, so it's first element will be used for converting");
			
			sourceObj=Array.getLength(sourceObj) ==0 ? null : Array.get(sourceObj, 0);
			
			if(sourceObj == null)
			{
				Object dv = getDefaultValue(sourceObj, targetType);
				if(log.isDebugEnabled())
					log.debug("the source object is null, so the default value '"+dv+"' of type '"+targetType+"' will be used");
				
				return dv;
			}
			
			if(SoybeanMilkUtils.isInstanceOf(sourceObj, targetType))
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
			Object dv = getDefaultValue(sourceObj, targetType);
			
			if(log.isDebugEnabled())
				log.debug("default value '"+dv+"' is used while converting '"+sourceObj+"' to '"+targetType+"' because the following exception :", e);
			
			return dv;
		}
	}
	
	/**
	 * 将映射表转换成<code>targetType</code>类型的对象
	 * @param valueMap 值映射表，它也可能包含与<code>targetType</code>类属性无关的关键字
	 * @param targetType
	 * @return
	 */
	protected Object convertMap(Map<String, Object> valueMap, Type targetType)
	{
		if(log.isDebugEnabled())
			log.debug("start converting 'Map<String, Object>' object to '"+targetType+"'");
		
		if(valueMap==null || targetType == null)
			return valueMap;
		
		Object result = null;
		
		Class<?>[] actualTypes=SoybeanMilkUtils.getActualClassTypeInfo(targetType);
		
		if(actualTypes==null || actualTypes.length==0)
			throw new ConvertException("converting 'Map<String,Object>' to '"+targetType+"' is not supported");
		else if(isArrayOrCollection(actualTypes[0]))
		{
			//List
			if(SoybeanMilkUtils.isAncestorClass(List.class, actualTypes[0]))
			{
				if(actualTypes.length != 2)
					throw new ConvertException("'"+targetType+"' is invalid, only generic List converting is supported");
				
				result=convertMapToJavaBeanList(valueMap, actualTypes);
			}
			//Set
			else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualTypes[0]))
			{
				if(actualTypes.length != 2)
					throw new ConvertException("'"+targetType+"' is invalid, only generic Set converting is supported");
				
				result=convertMapToJavaBeanSet(valueMap, actualTypes);
			}
			//Map
			else if(SoybeanMilkUtils.isAncestorClass(Map.class, actualTypes[0]))
			{
				result=valueMap;
			}
			//数组
			else if(SoybeanMilkUtils.isArray(actualTypes[0]))
				result=convertMapToJavaBeanArray(valueMap, actualTypes[0].getComponentType());
			else
				throw new ConvertException("converting to '"+targetType+"' is not supported");
		}
		//JavaBean
		else
		{
			PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(actualTypes[0]);
			
			if(!beanInfo.hasSubPropertyInfo())
				throw new ConvertException("the target javaBean Class '"+actualTypes[0]+"' is not valid, it has no javaBean property");
			else
			{
				//预存储的集合类属性映射表
				Map<String, Map<String, Object>> collectionProperties=new HashMap<String, Map<String,Object>>();
				String[] collectionPropSplits=new String[2];
				
				Set<String> keys=valueMap.keySet();
				for(String propExp : keys)
				{
					String[] propExpAry=splitPropertyExpression(propExp);
					
					//剔除无关属性
					if(beanInfo.getSubPropertyInfo(propExpAry[0]) != null)
					{
						//延迟初始化
						if(result == null)
							result = instance(beanInfo.getType(), -1);
						
						splitCollectionProperty(beanInfo, propExpAry, collectionPropSplits);
						
						if(collectionPropSplits[0] == null)
							setProperty(result, beanInfo, propExpAry, 0, valueMap.get(propExp));
						else
						{
							Map<String, Object> clsnValueMap=collectionProperties.get(collectionPropSplits[0]);
							if(clsnValueMap == null)
							{
								clsnValueMap=new HashMap<String, Object>();
								collectionProperties.put(collectionPropSplits[0], clsnValueMap);
							}
							
							clsnValueMap.put(collectionPropSplits[1], valueMap.get(propExp));
						}
					}
				}
				
				//设置集合类型属性的值
				if(collectionProperties!=null && !collectionProperties.isEmpty())
				{
					Set<String> props=collectionProperties.keySet();
					
					for(String propExp : props)
						setProperty(result, propExp, collectionProperties.get(propExp));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 拆分javaBean的集合类属性表达式。<br>
	 * 如果表达式没有包含集合类属性或者末尾节点是集合类，则<code>splits</code>将被清空；
	 * 否则，<code>splits[0]</code>是原表达式中到集合类属性为止的子属性，<code>splits[1]</code>则是之后的部分。
	 * @param beanInfo
	 * @param propExpressionArray
	 * @param splits
	 * @return 集合类属性的类型
	 * @date 2011-1-4
	 */
	protected void splitCollectionProperty(PropertyInfo beanInfo, String[] propExpressionArray, String[] splits)
	{
		int i=0;
		PropertyInfo tmpPropInfo=null;
		for(;i<propExpressionArray.length;i++)
		{
			tmpPropInfo=beanInfo.getSubPropertyInfo(propExpressionArray[i]);
			if(tmpPropInfo == null)
				throw new ConvertException("can not find property '"+propExpressionArray[i]+"' in class '"+beanInfo.getType().getName()+"'");
			else
				beanInfo=tmpPropInfo;
			
			if(isArrayOrCollection(beanInfo.getType()))
				break;
		}
		
		if(i < propExpressionArray.length-1)
		{
			splits[0]=assemblePropertyExpression(propExpressionArray, 0, i+1);
			splits[1]=assemblePropertyExpression(propExpressionArray, i+1, propExpressionArray.length);
		}
		else
		{
			splits[0]=null;
			splits[1]=null;
		}
	}
	
	/**
	 * 由映射表转换为JavaBean <code>java.util.Set</code>。
	 * @param valueMap
	 * @param setGeneric
	 * @return
	 * @date 2010-12-31
	 */
	@SuppressWarnings("unchecked")
	protected Set<?> convertMapToJavaBeanSet(Map<String, Object> valueMap, Class<?>[] setGeneric)
	{
		Set re=null;
		
		Object[] ary=convertMapToJavaBeanArray(valueMap, setGeneric[1]);
		if(ary != null)
		{
			re=(Set)instance(setGeneric[0], -1);
			for(Object o : ary)
				re.add(o);
		}
		
		return re;
	}
	
	/**
	 * 由映射表转换为JavaBean <code>java.util.List</code>。
	 * @param valueMap
	 * @param listGeneric
	 * @return
	 * @date 2010-12-31
	 */
	@SuppressWarnings("unchecked")
	protected List<?> convertMapToJavaBeanList(Map<String, Object> valueMap, Class<?>[] listGeneric)
	{
		List re=null;
		
		Object[] ary=convertMapToJavaBeanArray(valueMap, listGeneric[1]);
		if(ary != null)
		{
			re=(List)instance(listGeneric[0], -1);
			for(Object o : ary)
				re.add(o);
		}
		
		return re;
	}
	
	/**
	 * 由映射表转换为JavaBean数组，<code>valueMap</code>中值为<code>null</code>和关键字不是<code>javaBeanClass</code>类属性的元素将被忽略，
	 * 其他元素必须是数组并且长度一致。<br>
	 * 此方法不支持嵌套数组和集合（即<code>elementClass</code>不能包含数组和集合类属性）。
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
		if(!beanInfo.hasSubPropertyInfo())
			throw new ConvertException("the target javaBean Class '"+javaBeanClass+"' is not valid, it has no javaBean property");
		
		Set<String> keys=valueMap.keySet();
		for(String key : keys)
		{
			Object value=valueMap.get(key);
			if(value == null)
				continue;
			
			if(!value.getClass().isArray())
				throw new ConvertException("the element in the source map must be array");
			
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
	 * 是否是数组或者集合类
	 * @param clazz
	 * @return
	 * @date 2011-1-3
	 */
	protected boolean isArrayOrCollection(Class<?> clazz)
	{
		if(clazz.isArray())
			return true;
		else if(SoybeanMilkUtils.isAncestorClass(Collection.class, clazz))
			return true;
		else if(SoybeanMilkUtils.isAncestorClass(Map.class, clazz))
			return true;
		else
			return false;
	}
	
	/**
	 * 获取转换默认值。当转换不能正常执行时（比如源对象为<code>null</code>而目标类型为基本类型，或者源对象无法转换到目标类型），此方法的结果将被用做转换结果。
	 * @param srcObject
	 * @param targetType
	 * @return
	 * @date 2011-1-4
	 */
	protected Object getDefaultValue(Object srcObject, Type targetType)
	{
		if(boolean.class.equals(targetType))
			return false;
		else if(byte.class.equals(targetType))
			return (byte)0;
		else if(char.class.equals(targetType))
			return (char)0;
		else if(double.class.equals(targetType))
			return (double)0;
		else if(float.class.equals(targetType))
			return (float)0;
		else if(int.class.equals(targetType))
			return 0;
		else if(long.class.equals(targetType))
			return (long)0;
		else if(short.class.equals(targetType))
			return (short)0;
		else
			return null;
	}
}