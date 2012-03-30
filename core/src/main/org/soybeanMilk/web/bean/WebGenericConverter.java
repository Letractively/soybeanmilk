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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericType;
import org.soybeanMilk.core.bean.PropertyInfo;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.os.ParamFilterValue;

/**
 * WEB通用转换器，除了继承的转换支持，它还支持将参数映射表（{@link Map Map&lt;String, Object&gt;}）对象转换为JavaBean对象、
 * JavaBean数组、JavaBean集合（List、Set）、JavaBean映射表。<br>
 * 参数映射表的关键字必须是如下格式：<br>
 *         [属性名].[属性名]......<br>
 * 其中，“[属性名]”可以是下面这些语义：<br>
 * <ul>
 * 	<li>
 *  	JavaBean对象属性名
 *  </li>
 *  <li>
 *  	List、Set、数组对象的下标值（必须是数值）
 *  </li>
 *  <li>
 *  	Map对象的关键字
 *  </li>
 * </ul>
 * 比如，它可以将下面的参数映射表：
 * <pre>
 * "id"                    -&gt;  "1" 或者 ["1"] 或者 ["1", "2"]（第一个元素"1"将被使用）
 * "name"                  -&gt;  "jack" 或者 ["jack"] 或者 ["jack", "lily"]（第一个元素"jack"将被使用）
 * "listChildren.id"       -&gt;  ["11", "12"]
 * "listChildren.name"     -&gt;  ["tom", "mary"]
 * "setChildren.id"        -&gt;  ["11", "12"]
 * "setChildren.name"      -&gt;  ["tom", "mary"]
 * "arrayChildren.id"      -&gt;  ["11", "12"]
 * "arrayChildren.name"    -&gt;  ["tom", "mary"]
 * "mapChildren.key0.id"   -&gt;  "11" 或者 ["11"]
 * "mapChildren.key0.name" -&gt;  "tom" 或者 ["tom"]
 * "mapChildren.key1.id"   -&gt;  "22" 或者 ["22"]
 * "mapChildren.key1.name" -&gt;  "mary" 或者 ["mary"]
 * "ignored"               -&gt;  "this value will be ignored"
 * </pre>
 * 转换为：
 * <pre>
 * class User{
 * 	private Integer id;
 * 	private String name;
 * 	private List&lt;User&gt; listChildren;
 * 	private Set&lt;User&gt; setChildren;
 * 	private User[] arrayChildren;
 * 	private Map&lt;String, User&gt; mapChildren;
 * 	...
 * }
 * </pre>
 * 类型的对象，或者将：
 * <pre>
 * "id"                             -&gt;  ["1","2","3"]
 * "name"                           -&gt;  ["jack","tom","cherry"]
 * "0.listChildren.id"              -&gt;  ["10","11"]
 * "0.listChildren.name"            -&gt;  ["jack10","jack11"]
 * "1.setChildren.0.id"             -&gt;  "20"
 * "1.setChildren.0.name"           -&gt;  "tom20"
 * "1.setChildren.1.id"             -&gt;  "21"
 * "1.setChildren.1.name"           -&gt;  "tom21"
 * "2.arrayChildren.0.id"           -&gt;  "30"
 * "2.arrayChildren.0.name"         -&gt;  "cherry30"
 * "2.arrayChildren.1.id"           -&gt;  "31"
 * "2.arrayChildren.1.name"         -&gt;  "cherry31"
 * "2.mapChildren.key0.id"          -&gt;  "30"
 * "2.mapChildren.key0.name"        -&gt;  "cherry30"
 * "2.mapChildren.key1.id"          -&gt;  "31"
 * "2.mapChildren.key1.name"        -&gt;  "cherry31"
 * </pre>
 * 转换为：
 * <pre>
 * List&lt;User&gt;
 * Set&lt;User&gt;
 * User[]
 * </pre>
 * 类型的对象。
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
	
	public WebGenericConverter(boolean initDefaultSupportConverter)
	{
		super(initDefaultSupportConverter);
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	protected Object convertWhenNoSupportConverter(Object sourceObj, Type targetType) throws ConvertException
	{
		Object result=null;
		
		//如果源对象是数组而目标类型不是，则使用数组的第一个元素转换，与request.getParameter(...)规则相同
		if(SoybeanMilkUtils.isArray(sourceObj.getClass())
				&& SoybeanMilkUtils.isClassType(targetType) && !SoybeanMilkUtils.isArray((Class<?>)targetType))
		{
			if(log.isDebugEnabled())
				log.debug("'"+SoybeanMilkUtils.toString(sourceObj)+"' is an array while the target not, so it's first element will be used for converting");
			
			sourceObj=(Array.getLength(sourceObj) == 0 ? null : Array.get(sourceObj, 0));
			result=convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof ParamFilterValue)
		{
			result=convertParamFilterValue((ParamFilterValue)sourceObj, targetType);
		}
		else if(sourceObj instanceof Map<?, ?>)
		{
			result=convertMap((Map<String, ?>)sourceObj, targetType);
		}
		else if(sourceObj instanceof HttpServletRequest)
		{
			result=getConverterNotNull(HttpServletRequest.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof HttpSession)
		{
			result=getConverterNotNull(HttpSession.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof ServletContext)
		{
			result=getConverterNotNull(ServletContext.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof HttpServletResponse)
		{
			result=getConverterNotNull(HttpServletResponse.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof WebObjectSource)
		{
			result=getConverterNotNull(WebObjectSource.class, targetType).convert(sourceObj, targetType);
		}
		else
		{
			result=super.convertWhenNoSupportConverter(sourceObj, targetType);
		}
		
		return result;
	}
	
	/**
	 * 转换请求参数过滤值
	 * @param pfv
	 * @param targetType
	 * @return
	 * @throws ConvertException
	 * @date 2012-3-28
	 */
	@SuppressWarnings("unchecked")
	protected Object convertParamFilterValue(ParamFilterValue pfv, Type targetType) throws ConvertException
	{
		Object result=null;
		
		String filter=pfv.getFilter();
		Object value=pfv.getValue();
		
		if(SoybeanMilkUtils.isInstanceOf(value, SoybeanMilkUtils.toWrapperType(targetType)))
			return value;
		else if(value instanceof Map<?, ?>)
		{
			//过滤后的参数映射表必须是清洁的
			value=new PropertyValueMap((Map<String, ?>)value, (filter!=null && filter.length()!=0));
			result=convert(value, targetType);
		}
		else
			result=convert(value, targetType);
		
		return result;
	}
	
	/**
	 * 将射表转换成目标类型的对象
	 * @param sourceMap 源映射表
	 * @param targetType
	 * @return
	 */
	protected Object convertMap(Map<String, ?> sourceMap, Type targetType) throws ConvertException
	{
		Object result = null;
		
		//空的映射表作为null处理
		if(sourceMap==null || sourceMap.isEmpty())
		{
			result=convert(null, targetType);
		}
		else
		{
			PropertyValueMap pvm=null;
			
			if(sourceMap instanceof PropertyValueMap)
				pvm=(PropertyValueMap)sourceMap;
			else
				pvm=new PropertyValueMap(sourceMap);
			
			if(targetType instanceof Class<?>)
			{
				result=convertPropertyValueMapToClass(pvm, (Class<?>)targetType);
			}
			else if(targetType instanceof GenericType)
			{
				result=convertPropertyValueMapToGenericType(pvm, (GenericType)targetType);
			}
			else if(targetType instanceof ParameterizedType
					|| targetType instanceof GenericArrayType
					|| targetType instanceof TypeVariable<?>
					|| targetType instanceof WildcardType)
			{
				result=convertPropertyValueMapToGenericType(pvm, GenericType.getGenericType(targetType, null));
			}
			else
				throw new GenericConvertException("converting '"+SoybeanMilkUtils.toString(sourceMap)+"' to type '"+SoybeanMilkUtils.toString(targetType)+"' is not supported");
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为目标类型为<code>Class&lt?&gt;</code>的对象，目标类型只可能为JavaBean或者JavaBean数组
	 * @param sourceMap 属性值映射表
	 * @param targetClass
	 * @return
	 */
	protected Object convertPropertyValueMapToClass(PropertyValueMap sourceMap, Class<?> targetClass) throws ConvertException
	{
		Object result=null;
		
		//数组
		if(SoybeanMilkUtils.isArray(targetClass))
		{
			Class<?> eleClass=targetClass.getComponentType();
			
			List<?> tmpRe=convertPropertyValueMapToList(sourceMap, List.class, eleClass);
			
			result=listToArray(tmpRe, eleClass);
		}
		//JavaBean
		else
		{
			PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(targetClass);
			
			if(!beanInfo.hasSubPropertyInfo())
				throw new GenericConvertException("the target javaBean Class '"+SoybeanMilkUtils.toString(targetClass)+"' is not valid, it has no javaBean property");
			
			Set<String> propertyKeys=sourceMap.keySet();
			for(String property : propertyKeys)
			{
				PropertyInfo propInfo=null;
				if(!sourceMap.isCleaned())
				{
					propInfo=beanInfo.getSubPropertyInfo(property);
					
					//忽略无关属性
					if(propInfo == null)
						continue;
				}
				else
					propInfo=getSubPropertyInfoNotNull(beanInfo, property);
				
				//延迟初始化
				if(result == null)
					result = instance(beanInfo.getPropType(), -1);
				
				try
				{
					setProperty(result, propInfo, sourceMap.get(property));
				}
				catch(ConvertException e)
				{
					handlePropertyValueMapConvertException(sourceMap, property, e);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为泛型类型的对象
	 * @param sourceMap
	 * @param genericType
	 * @return
	 */
	protected Object convertPropertyValueMapToGenericType(PropertyValueMap sourceMap, GenericType genericType) throws ConvertException
	{
		Object result=null;
		
		boolean canConvert=true;
		
		if(genericType.isParameterizedType())
		{
			Class<?> actualClass=genericType.getActualClass();
			Class<?>[] argClasses=genericType.getParamClasses();
			
			//List<T>
			if(SoybeanMilkUtils.isAncestorClass(List.class, actualClass))
			{
				result=convertPropertyValueMapToList(sourceMap, actualClass, argClasses[0]);
			}
			//Set<T>
			else if(SoybeanMilkUtils.isAncestorClass(Set.class, actualClass))
			{
				List<?> tmpRe=convertPropertyValueMapToList(sourceMap, List.class, argClasses[0]);
				result=listToSet(tmpRe, actualClass);
			}
			//Map<K, V>
			else if(SoybeanMilkUtils.isAncestorClass(Map.class, actualClass))
			{
				result=convertPropertyValueMapToMap(sourceMap, actualClass, argClasses[0], argClasses[1]);
			}
			else
				canConvert=false;
		}
		//T[]
		else if(genericType.isGenericArrayType())
		{
			Class<?> componentClass=genericType.getComponentClass();
			
			result=convertPropertyValueMapToList(sourceMap, List.class, componentClass);
			result=listToArray((List<?>)result, componentClass);
		}
		//T
		else if(genericType.isTypeVariable())
		{
			Class<?> actualClass=genericType.getActualClass();
			result=convert(sourceMap, actualClass);
		}
		//? extends SomeType
		else if(genericType.isWildcardType())
		{
			Class<?> actualClass=genericType.getActualClass();
			result=convert(sourceMap, actualClass);
		}
		else
			canConvert=false;
		
		if(!canConvert)
			throw new GenericConvertException("converting '"+SoybeanMilkUtils.toString(sourceMap)+"' to type '"+SoybeanMilkUtils.toString(genericType)+"' is not supported");
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为列表对象，属性值映射表的关键字可以是两种内容：<br>
	 * <ol>
	 * 	<li>
	 * 		数字，比如：“0”、“1”，表示属性值在列表中的索引
	 * 	</li>
	 * 	<li>
	 * 		字符，比如：“property1”、“property2”，表示列表元素对象对应的属性名
	 * 	</li>
	 * </ol>
	 * @param sourceMap
	 * @param listClass
	 * @param elementClass
	 * @return
	 */
	protected List<?> convertPropertyValueMapToList(PropertyValueMap sourceMap, Class<?> listClass, Class<?> elementClass) throws ConvertException
	{
		if(sourceMap == null)
			return null;
		
		@SuppressWarnings("unchecked")
		List<Object> result=(List<Object>)instance(listClass, -1);
		
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(elementClass);
		
		Set<String> propertyKeyes=sourceMap.keySet();
		for(String property : propertyKeyes)
		{
			Object value=sourceMap.get(property);
			
			//需要优先处理索引属性
			if(isIndexOfProperty(property))
			{
				int idx=-1;
				try
				{
					idx=Integer.parseInt(property);
				}
				catch(Exception e)
				{
					throw new GenericConvertException("illegal index value '"+property+"' in property expression '"+sourceMap.getPropertyNamePath(property)+"'", e);
				}
				
				while(result.size() < idx+1)
					result.add(null);
				
				Object element=result.get(idx);
				
				if(element!=null && (value instanceof PropertyValueMap))
				{
					PropertyValueMap subPropMap=(PropertyValueMap)value;
					
					Set<String> subPropKeys=subPropMap.keySet();
					for(String subProp : subPropKeys)
					{
						PropertyInfo propInfo=getSubPropertyInfoNotNull(beanInfo, subProp);
						
						try
						{
							setProperty(element, propInfo, subPropMap.get(subProp));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(sourceMap, property, e);
						}
					}
				}
				else
				{
					try
					{
						element=convert(value, elementClass);
					}
					catch(ConvertException e)
					{
						handlePropertyValueMapConvertException(sourceMap, property, e);
					}
					
					result.set(idx, element);
				}
			}
			else
			{
				if(value == null)
					continue;
				
				PropertyInfo propInfo=null;
				if(!sourceMap.isCleaned())
				{
					propInfo=beanInfo.getSubPropertyInfo(property);
					
					//忽略无关属性
					if(propInfo == null)
						continue;
				}
				else
					propInfo=getSubPropertyInfoNotNull(beanInfo, property);
				
				//当前属性值是数组
				if(SoybeanMilkUtils.isArray(value.getClass()))
				{
					int len=Array.getLength(value);
					
					while(result.size() < len)
						result.add(null);
					
					for(int i=0; i<len; i++)
					{
						Object element=result.get(i);
						if(element ==null)
						{
							element=instance(elementClass, -1);
							result.set(i, element);
						}
						
						try
						{
							setProperty(element, propInfo, Array.get(value, i));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(sourceMap, property, e);
						}
					}
				}
				//当前属性值是属性映射表，则要将当前属性值转换为集合，并依次赋值
				else if(value instanceof PropertyValueMap)
				{
					PropertyValueMap pppm=(PropertyValueMap)value;
					
					List<?> propList=convertPropertyValueMapToList(pppm, List.class, propInfo.getPropType());
					
					if(propList != null)
					{
						while(result.size() < propList.size())
							result.add(null);
						
						for(int i=0; i<propList.size(); i++)
						{
							Object element=result.get(i);
							if(element ==null)
							{
								element=instance(elementClass, -1);
								result.set(i, element);
							}
							
							try
							{
								setProperty(element, propInfo, propList.get(i));
							}
							catch(ConvertException e)
							{
								handlePropertyValueMapConvertException(sourceMap, property, e);
							}
						}
					}
				}
				//属性值是其他对象
				else
				{
					while(result.size() < 1)
						result.add(null);
					
					Object element=result.get(0);
					if(element ==null)
					{
						element=instance(elementClass, -1);
						result.set(0, element);
					}
					
					try
					{
						setProperty(element, propInfo, value);
					}
					catch(ConvertException e)
					{
						handlePropertyValueMapConvertException(sourceMap, property, e);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为目标映射表, <code>sourceMap</code>的关键字将被转换目标映射表的关键字，值将被转换为此关键字对应的值
	 * @param sourceMap
	 * @param mapClass
	 * @param keyClass
	 * @param valueClass
	 * @return
	 */
	protected Map<?, ?> convertPropertyValueMapToMap(PropertyValueMap sourceMap, Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) throws ConvertException
	{
		if(sourceMap == null)
			return null;
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> result=(Map<Object, Object>)instance(mapClass, -1);
		
		Set<String> keys=sourceMap.keySet();
		for(String key : keys)
		{
			Object tk=null;
			Object tv=null;
			
			try
			{
				tk=convert(key, keyClass);
			}
			catch(ConvertException e)
			{
				throw new GenericConvertException("convert '"+key+"' in key '"+sourceMap.getPropertyNamePath(key)+"' to Map key of type '"+SoybeanMilkUtils.toString(keyClass)+"' failed", e);
			}
			
			try
			{
				tv=convert(sourceMap.get(key), valueClass);
			}
			catch(ConvertException e)
			{
				handlePropertyValueMapConvertException(sourceMap, key, e);
			}
			
			result.put(tk, tv);
		}
		
		return result;
	}
	
	/**
	 * 给定的字符串是否是索引值而非属性名（全部由数字组成）
	 * @param str
	 * @return
	 */
	protected boolean isIndexOfProperty(String str)
	{
		if(str==null || str.length()==0)
			return false;
		
		boolean digit=true;
		
		for(int i=0; i<str.length(); i++)
		{
			char c=str.charAt(i);
			if(c<'0' || c>'9')
			{
				digit=false;
				break;
			}
		}
		
		return digit;
	}
	
	/**
	 * 获取辅助{@linkplain Converter 转换器}，结果不会为<code>null</code>
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	protected Converter getConverterNotNull(Type sourceType, Type targetType)
	{
		Converter cvt=getConverter(sourceType, targetType);
		
		if(cvt == null)
			throw new GenericConvertException("can not find Converter for converting '"+SoybeanMilkUtils.toString(sourceType)+"' to '"+SoybeanMilkUtils.toString(targetType)+"'");
		
		return cvt;
	}
	
	/**
	 * 处理属性值映射表转换异常
	 * @param paramPropertyMap
	 * @param key
	 * @param e
	 */
	protected void handlePropertyValueMapConvertException(PropertyValueMap paramPropertyMap, String key, ConvertException e) throws ConvertException
	{
		if(e instanceof MapConvertException)
			throw e;
		else
			throw new MapConvertException(paramPropertyMap.getPropertyNamePath(key), e.getSourceObject(), e.getTargetType(), e.getCause());
	}
	
	/**
	 * 属性值映射表，它是关键字为<i>访问符表达式</i>映射表的分解结果。<br>
	 * 它的关键字表示某对象的某个属性名，而关键字对应的值则是这个对象该属性的值（或者是可以转换为该属性值的某个对象）。
	 * 它有一个特殊用途的{@linkplain #clean}属性，用以标识属性值映射表是否是清洁的，清洁的属性值映射表在转换为某对象时，
	 * 如果它的某个关键字找不到对应的对象属性名，转换将被终止；而如果属性值映射表不是清洁的，找不到对应对象属性名的关键字将被忽略。
	 * 
	 * @author earthAngry@gmail.com
	 * @date 2012-3-27
	 */
	protected static class PropertyValueMap extends HashMap<String, Object>
	{
		private static final long serialVersionUID = 1L;
		
		/**此属性值映射表的属性名*/
		private String propertyName;
		
		/**父属性值映射表*/
		private PropertyValueMap parent;
		
		/**是否是清洁的*/
		private boolean cleaned;
		
		/**
		 * 由源映射表创建属性值映射表
		 * @param map 源映射表，它是关键字具有<i>访问符表达式</i>语义
		 */
		public PropertyValueMap(Map<String, ?> map)
		{
			this(map, false);
		}
		
		/**
		 * 由源映射表创建属性值映射表
		 * @param map 源映射表，它是关键字具有<i>访问符表达式</i>语义
		 * @param cleaned 源映射表是否是清洁的
		 */
		public PropertyValueMap(Map<String, ?> map, boolean cleaned)
		{
			super();
			
			this.cleaned=cleaned;
			this.resolve(map);
		}
		
		/**
		 * 内部使用的构造器
		 */
		private PropertyValueMap(String propertyName, PropertyValueMap parent)
		{
			super();
			this.propertyName=propertyName;
			this.parent = parent;
			this.cleaned=true;
		}
		
		/**
		 * 获取某个属性的属性名路径。
		 * @param propertyName
		 * @return
		 */
		public String getPropertyNamePath(String propertyName)
		{
			String result=null;
			
			if(this.parent != null)
				result=this.parent.getPropertyNamePath(this.propertyName);
			else
				result=this.propertyName;
			
			if(result==null || result.length()==0)
				return propertyName;
			else
				return result+WebConstants.ACCESSOR+propertyName;
		}
		
		public boolean isCleaned() {
			return cleaned;
		}

		public void setCleaned(boolean cleaned) {
			this.cleaned = cleaned;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		public PropertyValueMap getParent() {
			return parent;
		}

		public void setParent(PropertyValueMap parent) {
			this.parent = parent;
		}
		
		/**
		 * 分解给定的关键字为<i>访问符表达式</i>的映射表。
		 * @param map 映射表，它的关键字具有<i>访问符表达式</i>语义
		 */
		protected void resolve(Map<String, ?> map)
		{
			Set<String> keys=map.keySet();
			
			for(String key : keys)
			{
				String[] propKeys=SoybeanMilkUtils.splitAccessExpression(key);
				
				PropertyValueMap parent=this;
				
				for(int i=0; i<propKeys.length; i++)
				{
					if(i == propKeys.length-1)
					{
						parent.put(propKeys[i], map.get(key));
					}
					else
					{
						PropertyValueMap tmp=(PropertyValueMap)parent.get(propKeys[i]);
						if(tmp == null)
						{
							tmp=new PropertyValueMap(propKeys[i], parent);
							parent.put(propKeys[i], tmp);
						}
						
						parent=tmp;
					}
				}
			}
		}
		
		@Override
		public String toString()
		{
			return getClass().getSimpleName()+" [propertyName=" + getPropertyName()
					+ ", cleaned="+cleaned+", " + super.toString() + "]";
		}
	}
}