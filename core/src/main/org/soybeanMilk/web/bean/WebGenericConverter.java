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
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.PropertyInfo;

/**
 * WEB通用转换器，除了继承的转换支持，它还支持将{@link Map Map&lt;String, Object&gt;}转换为JavaBean对象、JavaBean数组以及JavaBean集合（List、Set）。<br>
 * 比如可以将下面的映射表：
 * <pre>
 * "id"                 -&gt;  "1" 或 ["1"]
 * "name"               -&gt;  "jack" 或 ["jack"]
 * "listChildren.id"    -&gt;  ["11", "12"]
 * "listChildren.name"  -&gt;  ["tom", "mary"]
 * "setChildren.id"     -&gt;  ["11", "12"]
 * "setChildren.name"   -&gt;  ["tom", "mary"]
 * "arrayChildren.id"   -&gt;  ["11", "12"]
 * "arrayChildren.name" -&gt;  ["tom", "mary"]
 * "ignored"            -&gt;  "this value will be ignored"
 * </pre>
 * 转换为：
 * <pre>
 * class User{
 * 	private Integer id;
 * 	private String name;
 * 	private List&lt;User&gt; listChildren;
 * 	private Set&lt;User&gt; setChildren;
 * 	private User[] arrayChildren;
 * 	...
 * }
 * 
 * 类型的对象。
 * </pre>
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
	
	@SuppressWarnings("unchecked")
	//@Override
	protected Object convertWhenNoSupportConverter(Object sourceObj, Type targetType)
	{
		//如果源对象是数组并且长度为1，而目标类型不是，则使用数组的第一个元素转换
		if(sourceObj.getClass().isArray()
				&& Array.getLength(sourceObj)==1
				&& !SoybeanMilkUtils.isClassTypeArray(targetType))
		{
			if(log.isDebugEnabled())
				log.debug("the src '"+getStringDesc(sourceObj)+"' is array with length 1, while the target type not, so it's first element will be used for converting");
			
			sourceObj=Array.get(sourceObj, 0);
			
			return convert(sourceObj, targetType);
		}
		else if(SoybeanMilkUtils.isInstanceOf(sourceObj, Map.class))
		{
			return convertMap(FilterAwareMap.wrap((Map<String, Object>)sourceObj), targetType);
		}
		else
		{
			return super.convertWhenNoSupportConverter(sourceObj, targetType);
		}
	}
	
	/**
	 * 将映射表转换成<code>targetType</code>类型的对象
	 * @param sourceMap 值映射表，它也可能包含与<code>targetType</code>类属性无关的关键字
	 * @param targetType
	 * @return
	 */
	protected Object convertMap(FilterAwareMap<String, Object> sourceMap, Type targetType)
	{
		if(log.isDebugEnabled())
			log.debug("start converting 'Map<String, Object>' object to type '"+targetType+"'");
		
		Object result = null;
		
		if(sourceMap==null || sourceMap.isEmpty())//空元素的映射表作为null处理
		{
			result=convert(null, targetType);
		}
		else if(sourceMap.isExplicitValue())
		{
			result=convert(sourceMap.get(FilterAwareMap.EXPLICIT_KEY), targetType);
		}
		else
		{
			Class<?>[] actualTypes=SoybeanMilkUtils.getActualClassTypeInfo(targetType);
			
			if(SoybeanMilkUtils.isArray(actualTypes[0]))//array
			{
				result=convertMapToJavaBeanArray(sourceMap, actualTypes[0].getComponentType());
			}
			else if(SoybeanMilkUtils.isAncestorClass(List.class, actualTypes[0]))//List
			{
				if(actualTypes.length != 2)
					throw new GenericConvertException("'"+targetType+"' is invalid, only generic List converting is supported");
				
				result=convertArrayToList(convertMapToJavaBeanArray(sourceMap, actualTypes[1]), actualTypes[0]);
			}
			else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualTypes[0]))//Set
			{
				if(actualTypes.length != 2)
					throw new GenericConvertException("'"+targetType+"' is invalid, only generic Set converting is supported");
				
				result=convertArrayToSet(convertMapToJavaBeanArray(sourceMap, actualTypes[1]), actualTypes[0]);
			}
			else if(SoybeanMilkUtils.isAncestorClass(Collection.class, actualTypes[0]))//不支持的集合类
			{
				throw new GenericConvertException("converting 'Map<String,Object>' to '"+targetType+"' is not supported");
			}
			else//JavaBean
			{
				PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(actualTypes[0]);
				
				if(!beanInfo.hasSubPropertyInfo())
					throw new GenericConvertException("the target javaBean Class '"+actualTypes[0]+"' is not valid, it has no javaBean property");
				
				Map<String, Boolean> collectionPropertyProcessed=new HashMap<String, Boolean>();
				
				Set<String> keys=sourceMap.keySet();
				for(String propExp : keys)
				{
					String[] propExpressionArray=splitPropertyExpression(propExp);
					
					//忽略无关属性
					if(beanInfo.getSubPropertyInfo(propExpressionArray[0])==null)
					{
						//如果被过滤过，则属性必须存在
						if(sourceMap.isFiltered())
							throw new GenericConvertException("can not find property '"+propExpressionArray[0]+"' in class '"+beanInfo.getType().getName()+"'");
					}
					else
					{
						//延迟初始化
						if(result == null)
							result = instance(beanInfo.getType(), -1);
						
						String collectionPropertyExp=detectCollectionProperty(beanInfo, propExpressionArray);
						
						if(collectionPropertyExp == null)
							setProperty(result, beanInfo, propExpressionArray, 0, sourceMap.get(propExp));
						//集合属性需要特殊处理
						else
						{
							if(collectionPropertyProcessed.get(collectionPropertyExp) == null)
							{
								FilterAwareMap<String, Object> collectionPropertyValueMap=FilterAwareMap.filter(
										sourceMap, collectionPropertyExp+ACCESSOR, false);
								
								setProperty(result, collectionPropertyExp, collectionPropertyValueMap);
								
								collectionPropertyProcessed.put(collectionPropertyExp, true);
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 查询<code>propExpressionArray</code>属性表达式中是否包含集合类属性。<br>
	 * 如果表达式没有包含集合类属性或者末尾节点是集合类，则返回<code>null</code>；否则，返回原表达式中到集合类属性为止（包括）的字符串。
	 * @param beanInfo
	 * @param propExpressionArray
	 * @return 集合类属性的类型
	 * @date 2011-1-4
	 */
	protected String detectCollectionProperty(PropertyInfo beanInfo, String[] propExpressionArray)
	{
		String re=null;
		
		int i=0;
		PropertyInfo tmpPropInfo=null;
		for(;i<propExpressionArray.length;i++)
		{
			tmpPropInfo=beanInfo.getSubPropertyInfo(propExpressionArray[i]);
			if(tmpPropInfo == null)
				throw new GenericConvertException("can not find property '"+propExpressionArray[i]+"' in class '"+beanInfo.getType().getName()+"'");
			else
				beanInfo=tmpPropInfo;
			
			if(isArrayOrCollection(beanInfo.getType()))
				break;
		}
		
		if(i < propExpressionArray.length-1)
			re=assemblePropertyExpression(propExpressionArray, 0, i+1);
		
		return re;
	}
	
	/**
	 * 由映射表转换为JavaBean数组，<code>valueMap</code>中值为<code>null</code>和关键字不是<code>javaBeanClass</code>类属性的元素将被忽略，
	 * 其他元素必须是数组并且长度一致。<br>
	 * 此方法不支持嵌套数组和集合（即<code>elementClass</code>不能包含数组和集合类属性）。
	 * @param sourceMap
	 * @param javaBeanClass
	 * @return 元素为<code>javaBeanClass</code>类型且长度为<code>valueMap</code>值元素长度的数组
	 * @date 2010-12-31
	 */
	protected Object[] convertMapToJavaBeanArray(FilterAwareMap<String, Object> sourceMap, Class<?> javaBeanClass)
	{
		if(sourceMap==null || sourceMap.size()==0)
			return null;
		
		Object[] re=null;
		int len=-1;
		
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(javaBeanClass);
		if(!beanInfo.hasSubPropertyInfo())
			throw new GenericConvertException("the target javaBean Class '"+javaBeanClass+"' is not valid, it has no javaBean property");
		
		Set<String> keys=sourceMap.keySet();
		for(String key : keys)
		{
			Object value=sourceMap.get(key);
			if(value == null)
				continue;
			
			if(!value.getClass().isArray())
				throw new GenericConvertException("the element in the source map must be array");
			
			int l=Array.getLength(value);
			if(len == -1)
				len=l;
			else
				if(l != len)
					throw new GenericConvertException("the array element in the source map must be the same length");
			
			String[] propertyExp=splitPropertyExpression(key);
			
			//忽略无关属性
			if(beanInfo.getSubPropertyInfo(propertyExp[0])==null)
			{
				//如果被过滤过，则属性必须存在
				if(sourceMap.isFiltered())
					throw new GenericConvertException("can not find property '"+propertyExp[0]+"' in class '"+beanInfo.getType().getName()+"'");
			}
			else
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
		else
			return false;
	}
}