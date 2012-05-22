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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SbmUtils;
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
 * 默认通用转换器的。
 * <p>
 * 它支持的类型转换如下所示：<br>
 * <table border="1" cellspacing="1" cellpadding="3">
 *   <tr><td>源类型</td><td>目标类型</td></tr>
 *   <tr>
 *   	<td>String</td>
 *   	<td>
 *   		boolean, Boolean; byte, Byte; char, Character; double, Double; float, Float;<br>
 *   		int, Integer; long, Long; short, Short;<br>
 *   		java.math.BigDecimal; java.math.BigInteger; java.util.Date; java.sql.Date; java.sql.Time; java.sql.Timestamp;<br>
 *   		enum
 *   	</td>
 *   </tr><tr>
 *   	<td>String[]</td>
 *   	<td>
 *   		上述各类型的数组、java.util.List、java.util.Set。<br>
 *   		比如“int[]”、“boolean[]”、“Short[]”、List&lt;Integer&gt;、List&lt;Date&gt;、Set&lt;Integer&gt;、Set&lt;Date&gt; <br>
 *   </td></tr>
 *   <tr>
 *   	<td>
 *   		Map&lt;String, ?&gt;
 *   	</td><td>
 *   		JavaBean; List&lt;JavaBean&gt;; Set&lt;JavaBean&gt;; Map&lt;SomeKeyType, JavaBean&gt;
 *   	</td>
 *   </tr>
 * </table>
 * </p>
 * <p>
 * 对于Map&lt;String, ?&gt;类型的源对象，它的关键字必须符合下面的<i>访问符表达式</i>格式：
 * </p>
 * <p>
 * 		<i>name</i>[.<i>name</i> ...]
 * </p>
 * <p>
 * 这里，<i>name</i>可以是下面这些语义和字面值：
 * </p>
 * <ul>
 * 	<li>
 *  	JavaBean对象属性名<br>
 *  	表明映射表的值属于JavaBean的<i>name</i>属性值
 *  </li>
 *  <li>
 *  	List、Set、数组对象的下标值<br>
 *  	表明映射表的值属于集合中的<i>name</i>元素
 *  </li>
 *  <li>
 *  	Map对象关键字<br>
 *  	表明映射表的值属于另一个映射表<i>name</i>关键字的值
 *  </li>
 *  <li>
 *  	"class"字面值<br>
 *  	表明自定义转换目标类型，它的值必须是全类名，且必须位于<i>访问符表达式</i>的末尾位置。<br>
 *  	它用以提供集合类元素的多态转换支持，比如：<br>
 *  	"2.class" -&gt; "org.somePkg.UserSub"<br>
 *  	&nbsp;&nbsp;&nbsp;&nbsp;指定目标集合的第三个元素类型为“org.somePkg.UserSub”<br>
 *  	"someKey.class" -&gt; "org.somePkg.UserSub"<br>
 *  	&nbsp;&nbsp;&nbsp;&nbsp;指定目标映射表的"someKey"元素类型为“org.somePkg.UserSub”<br>
 *  	"class" -&gt; ["org.somePkg.UserSub0", "org.somePkg.UserSub1"]<br>
 *  	&nbsp;&nbsp;&nbsp;&nbsp;指定目标集合的第一个元素类型为"org.somePkg.UserSub0"，第二个元素类型为"org.somePkg.UserSub1"
 *  </li>
 * </ul>
 * <p>
 * 比如，它可以将下面的映射表：
 * <pre>
 * "id"                    -&gt;  "1"
 * "name"                  -&gt;  "jack"
 * "listChildren.id"       -&gt;  ["11", "12"]
 * "listChildren.name"     -&gt;  ["tom", "mary"]
 * "setChildren.id"        -&gt;  ["11", "12"]
 * "setChildren.name"      -&gt;  ["tom", "mary"]
 * "arrayChildren.id"      -&gt;  ["11", "12"]
 * "arrayChildren.name"    -&gt;  ["tom", "mary"]
 * "mapChildren.key0.id"   -&gt;  "11"
 * "mapChildren.key0.name" -&gt;  "tom"
 * "mapChildren.key1.id"   -&gt;  "22"
 * "mapChildren.key1.name" -&gt;  "mary"
 * </pre>
 * 转换为：
 * <pre>
 * package org.somePkg;
 * 
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
 * </p>
 * <p>
 * 你也可以通过{@link #addConverter(Type, Type, Converter)}方法为它添加更多辅助转换器，使其支持更多的类型转换。<br>
 * 另外，如果目标类型为<code>String</code>，而你没有添加某类型对象到<code>String</code>类型的辅助转换器，那么它将返回此对象的<code>toString()</code>结果。
 * </p>
 * 
 * @author earthangry@gmail.com
 * @date 2010-10-6
 */
public class DefaultGenericConverter implements GenericConverter
{
	private static Log log = LogFactory.getLog(DefaultGenericConverter.class);
	
	/**映射表转换的自定义目标类型关键字*/
	public static final String KEY_CUSTOM_CLASS="class";
	
	/**映射表转换的自定义目标列表元素类型集合*/
	public static final String KEY_CUSTOM_ELEMENT_CLASSES="classes";
	
	private Map<ConverterKey,Converter> converters;
	
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
	public void addConverter(Type sourceType,Type targetType,Converter converter)
	{
		if(getConverters() == null)
			setConverters(new HashMap<ConverterKey, Converter>());
		
		getConverters().put(generateConverterKey(sourceType, targetType), converter);
		
		if(log.isDebugEnabled())
			log.debug("add a support Converter "+SbmUtils.toString(converter.getClass())+" for converting "+SbmUtils.toString(sourceType)+" to "+SbmUtils.toString(targetType));
	}
	
	//@Override
	public Converter getConverter(Type sourceType, Type targetType)
	{
		Converter re=null;
		Map<ConverterKey,Converter> converters=getConverters();
		
		if(converters != null)
		{
			re=converters.get(generateConverterKey(sourceType, targetType));
			if(re==null && SbmUtils.isPrimitive(targetType))
				re=converters.get(generateConverterKey(sourceType, SbmUtils.toWrapperType(targetType)));
		}
		
		return re;
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
	{
		return (T)convertObjectToType(sourceObj, targetType);
	}
	
	/**
	 * 将对象转换为给定类型的对象
	 * @param obj
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-12
	 */
	protected Object convertObjectToType(Object obj, Type type) throws ConvertException
	{
		if(log.isDebugEnabled())
			log.debug("start converting "+SbmUtils.toString(obj)+" to type "+SbmUtils.toString(type));
		
		Object result=null;
		
		if(obj == null)
		{
			if(SbmUtils.isPrimitive(type))
				throw new GenericConvertException("can not convert "+SbmUtils.toString(obj)+" to primitive type "+SbmUtils.toString(type));
			else
				result=null;
		}
		else if(type==null || SbmUtils.isInstanceOf(obj, type))
		{
			//Map需要特殊处理，因为它可能包含自定义目标类型
			if(obj instanceof Map<?, ?>)
			{
				result=convertMapToType((Map<?, ?>)obj, type);
			}
			else
			{
				result=obj;
			}
		}
		else
		{
			Converter converter = getConverter(obj.getClass(), type);
			
			if(converter != null)
				result=doConvert(converter, obj, type);
			else
				result=convertWhenNoSupportConverter(obj, type);
		}
		
		if(log.isDebugEnabled())
			log.debug("finish converting "+SbmUtils.toString(obj)+" to type "+SbmUtils.toString(type));
		
		return result;
	}
	
	/**
	 * 当找不到对应的辅助转换器时，此方法将被调用。
	 * @param obj
	 * @param type
	 * @return
	 * @date 2011-1-5
	 */
	protected Object convertWhenNoSupportConverter(Object obj, Type type) throws ConvertException
	{
		if(obj == null)
			return null;
		
		Object result=null;
		
		if(obj instanceof String)
		{
			result=convertStringToType((String)obj, type);
		}
		else if(obj.getClass().isArray())
		{
			result=convertArrayToType(obj, type);
		}
		else if(obj instanceof Map<?, ?>)
		{
			result=convertMapToType((Map<?, ?>)obj, type);
		}
		else if(String.class.equals(type))
		{
			result=obj.toString();
		}
		else
			result=converterNotFoundThrow(obj.getClass(), type);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected Object convertStringToType(String str, Type type) throws ConvertException
	{
		Object result=null;
		
		if(str==null || str.length()==0)
		{
			if(SbmUtils.isPrimitive(type))
				throw new GenericConvertException("can not convert "+SbmUtils.toString(str)+" to primitive type "+SbmUtils.toString(type));
			else
				return null;
		}
		else if(SbmUtils.isClassType(type))
		{
			@SuppressWarnings("rawtypes")
			Class clazz=SbmUtils.narrowToClassType(type);
			
			if(clazz.isEnum())
				result= Enum.valueOf(clazz, str);
			else
				result=converterNotFoundThrow(str.getClass(), type);
		}
		else if(type instanceof TypeVariable<?>)
		{
			result=convertObjectToType(str, toConcreteType(type));
		}
		else if(type instanceof WildcardType)
		{
			result=convertObjectToType(str, toConcreteType(type));
		}
		else if(type instanceof ParameterizedType)
		{
			result=converterNotFoundThrow(str.getClass(), type);
		}
		else if(type instanceof GenericArrayType)
		{
			result=converterNotFoundThrow(str.getClass(), type);
		}
		else
			result=converterNotFoundThrow(str.getClass(), type);
		
		return result;
	}
	
	/**
	 * 将数组对象转换为目标类型的对象
	 * @param array
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertArrayToType(Object array, Type type) throws ConvertException
	{
		if(array == null)
			return null;
		
		Object result=null;
		
		if(type==null || SbmUtils.isInstanceOf(array, type))
		{
			result=array;
		}
		else if(SbmUtils.isClassType(type))
		{
			result=convertArrayToClass(array, SbmUtils.narrowToClassType(type));
		}
		else if(type instanceof ParameterizedType)
		{
			result=convertArrayToParameterrizedType(array, (ParameterizedType)type);
		}
		else if(type instanceof GenericArrayType)
		{
			result=convertArrayToGenericArrayType(array, (GenericArrayType)type);
		}
		else if(type instanceof TypeVariable<?>)
		{
			result=convertObjectToType(array, toConcreteType(type));
		}
		else if(type instanceof WildcardType)
		{
			result=convertObjectToType(array, toConcreteType(type));
		}
		else
			result=converterNotFoundThrow(array.getClass(), type);
		
		return result;
	}
	
	/**
	 * 将数组对象转换为{@linkplain Class}类型的对象
	 * @param array
	 * @param clazz
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertArrayToClass(Object array, Class<?> clazz) throws ConvertException
	{
		Object result=null;
		
		if(clazz.isArray())
		{
			result=convertArrayToArray(array, clazz.getComponentType());
		}
		else
			result=converterNotFoundThrow(array.getClass(), clazz);
		
		return result;
	}
	
	/**
	 * 将数组对象转换为{@linkplain ParameterizedType}类型的对象
	 * @param array
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertArrayToParameterrizedType(Object array, ParameterizedType type) throws ConvertException
	{
		Object result=null;
		
		Type rt=type.getRawType();
		Type[] atas=type.getActualTypeArguments();
		
		if(SbmUtils.isClassType(rt))
		{
			Class<?> actualType=SbmUtils.narrowToClassType(rt);
			
			//List<T>
			if(SbmUtils.isAncestorType(List.class, actualType))
			{
				result=convertArrayToList(array, actualType, atas[0]);
			}
			//Set<T>
			else if(SbmUtils.isAncestorType(Set.class, actualType))
			{
				List<?> list=convertArrayToList(array, List.class, atas[0]);
				result=listToSet(list, actualType);
			}
			else
				result=converterNotFoundThrow(array.getClass(), type);
		}
		else
			result=converterNotFoundThrow(array.getClass(), type);
		
		return result;
	}
	
	/**
	 * 将数组对象转换为{@linkplain GenericArrayType}类型的对象
	 * @param array
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertArrayToGenericArrayType(Object array, GenericArrayType type) throws ConvertException
	{
		Object result=null;
		
		Type ct=type.getGenericComponentType();
		
		result=convertArrayToArray(array, ct);
		
		return result;
	}
	
	/**
	 * 由一个数组对象转换为另一数组对象
	 * @param array
	 * @param elementType
	 * @return
	 * @date 2012-2-20
	 */
	protected Object convertArrayToArray(Object array, Type elementType) throws ConvertException
	{
		Object result=null;
		
		int len=Array.getLength(array);
		
		result=instance(elementType, len);
		
		for(int i=0; i<len; i++)
		{
			Object v=convertObjectToType(Array.get(array, i), elementType);
			Array.set(result, i, v);
		}
		
		return result;
	}
	
	/**
	 * 由数组转换为{@linkplain java.util.List List}对象。
	 * @param array
	 * @param listClass
	 * @param elementType
	 * @return
	 * @date 2011-1-5
	 */
	@SuppressWarnings("unchecked")
	protected List<?> convertArrayToList(Object array, Class<?> listClass, Type elementType) throws ConvertException
	{
		List<Object> result=null;
		
		result=(List<Object>)instance(listClass, -1);
		
		for(int i=0,len=Array.getLength(array); i<len; i++)
			result.add(convertObjectToType(Array.get(array, i), elementType));
		
		return result;
	}
	
	/**
	 * 将映射表转换为目标类型的对象
	 * @param map
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	@SuppressWarnings("unchecked")
	protected Object convertMapToType(Map<?, ?> map, Type type) throws ConvertException
	{
		Object result=null;
		
		//优先自定义类型
		Type customType=getMapCustomTargetType(map, null);
		if(customType != null)
			type=customType;
		
		if(type == null)
		{
			result=map;
		}
		else if(SbmUtils.isClassType(type))
		{
			Class<?> clazz=SbmUtils.narrowToClassType(type);
			
			//如果没有自定义类型且目标类型是原生Map则不做转换
			if(customType==null && SbmUtils.isAncestorType(Map.class, clazz))
			{
				result=map;
			}
			else
			{
				result=convertPropertyValueMapToClass(toPropertyValueMap((Map<String, ?>)map), clazz);
			}
		}
		else if(type instanceof ParameterizedType)
		{
			boolean convert=true;
			
			ParameterizedType pt=(ParameterizedType)type;
			Type rt=pt.getRawType();
			
			//目标类型是Map<?, ?>或者Map<Object, Object>也不做转换
			if(SbmUtils.isAncestorType(rt, map.getClass()))
			{
				Type[] at=pt.getActualTypeArguments();
				if(at!=null && at.length==2
						&& ((Object.class.equals(at[0])&&Object.class.equals(at[1]))
								|| (isSimpleWildcardType(at[0]) && isSimpleWildcardType(at[1]))))
				{
					convert=false;
				}
			}
			
			if(convert)
				result=convertPropertyValueMapToParameterrizedType(toPropertyValueMap((Map<String, ?>)map), pt);
			else
				result=map;
		}
		else if(type instanceof GenericArrayType)
		{
			result=convertPropertyValueMapToGenericArrayType(toPropertyValueMap((Map<String, ?>)map), (GenericArrayType)type);
		}
		else if(type instanceof TypeVariable<?>)
		{
			result=convertObjectToType(map, toConcreteType(type));
		}
		else if(type instanceof WildcardType)
		{
			result=convertObjectToType(map, toConcreteType(type));
		}
		else
			result=converterNotFoundThrow(map.getClass(), type);
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为目标类型为<code>Class&lt?&gt;</code>的对象，
	 * 目标类型可能为：JavaBean、数组、List、Set、Map
	 * @param pvm 属性值映射表
	 * @param type
	 * @return
	 */
	protected Object convertPropertyValueMapToClass(PropertyValueMap pvm, Class<?> type) throws ConvertException
	{
		Object result=null;
		
		//数组
		if(type.isArray())
		{
			Class<?> eleClass=type.getComponentType();
			
			List<?> tmpRe=convertPropertyValueMapToList(pvm, List.class, eleClass);
			
			result=listToArray(tmpRe, eleClass);
		}
		//List
		else if(SbmUtils.isAncestorType(List.class, type))
		{
			result=convertPropertyValueMapToList(pvm, type, null);
		}
		//Set
		else if(SbmUtils.isAncestorType(Set.class, type))
		{
			List<?> tmpRe=convertPropertyValueMapToList(pvm, List.class, null);
			result=listToSet(tmpRe, type);
		}
		//Map
		else if(SbmUtils.isAncestorType(Map.class, type))
		{
			result=convertPropertyValueMapToMap(pvm, type, null, null);
		}
		//JavaBean
		else
		{
			result=convertPropertyValueMapToJavaBeanClass(pvm, type);
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为JavaBean对象
	 * @param pvm
	 * @param javaBeanClass
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertPropertyValueMapToJavaBeanClass(PropertyValueMap pvm, Class<?> javaBeanClass) throws ConvertException
	{
		Object result=null;
		
		PropertyInfo beanInfo=PropertyInfo.getPropertyInfo(javaBeanClass);
		
		if(!beanInfo.hasSubPropertyInfo())
			throw new GenericConvertException("the target javaBean Class "+SbmUtils.toString(javaBeanClass)+" is not valid, it has no javaBean property");
		
		Set<String> propertyKeys=pvm.keySet();
		for(String property : propertyKeys)
		{
			//忽略自定义类型关键字
			if(KEY_CUSTOM_CLASS.equals(property))
				continue;
			
			PropertyInfo propInfo=null;
			if(!pvm.isCleaned())
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
				setJavaBeanProperty(result, propInfo, pvm.get(property));
			}
			catch(ConvertException e)
			{
				handlePropertyValueMapConvertException(pvm, property, e);
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为{@linkplain ParameterizedType}类型的对象
	 * @param pvm
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertPropertyValueMapToParameterrizedType(PropertyValueMap pvm, ParameterizedType type) throws ConvertException
	{
		Object result=null;
		
		Type rt=type.getRawType();
		Type[] atas=type.getActualTypeArguments();
		
		if(SbmUtils.isClassType(rt))
		{
			Class<?> actualType=SbmUtils.narrowToClassType(rt);
			
			//List<T>
			if(SbmUtils.isAncestorType(List.class, actualType))
			{
				result=convertPropertyValueMapToList(pvm, actualType, atas[0]);
			}
			//Set<T>
			else if(SbmUtils.isAncestorType(Set.class, actualType))
			{
				List<?> tmpRe=convertPropertyValueMapToList(pvm, List.class, atas[0]);
				result=listToSet(tmpRe, actualType);
			}
			//Map<K, V>
			else if(SbmUtils.isAncestorType(Map.class, actualType))
			{
				result=convertPropertyValueMapToMap(pvm, actualType, atas[0], atas[1]);
			}
			else
				result=converterNotFoundThrow(pvm.getClass(), type);
		}
		else
			result=converterNotFoundThrow(pvm.getClass(), type);
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为{@linkplain GenericArrayType}类型的对象
	 * @param pvm
	 * @param type
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-14
	 */
	protected Object convertPropertyValueMapToGenericArrayType(PropertyValueMap pvm, GenericArrayType type) throws ConvertException
	{
		Object result=null;
		
		Type ct=type.getGenericComponentType();
		
		result=convertPropertyValueMapToList(pvm, List.class, ct);
		result=listToArray((List<?>)result, ct);
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为JavaBean列表
	 * @param pvm
	 * @param listClass
	 * @param elementType
	 * @return
	 */
	protected List<?> convertPropertyValueMapToList(PropertyValueMap pvm, Class<?> listClass, Type elementType) throws ConvertException
	{
		if(pvm==null || pvm.isEmpty())
			return null;
		
		@SuppressWarnings("unchecked")
		List<Object> result=(List<Object>)instance(listClass, -1);
		
		Type[] customTypes=getMapTargetListElementCustomTypes(pvm);
		
		//当关键字不是索引位置而是元素的属性名时，它即是所有元素都共有的属性信息
		PropertyInfo commonEleBeanInfo=null;
		Type commonEleType=null;
		if(elementType != null)
			commonEleType=toConcreteType(elementType);
		else
		{
			Type cet=getMapTargetListElementCustomType(pvm, 0, customTypes, elementType);
			
			//使用第一个元素的类型作为共有属性信息
			if(cet != null)
				commonEleType=toConcreteType(cet);
		}
		
		if(commonEleType!=null && SbmUtils.isClassType(commonEleType))
			commonEleBeanInfo=PropertyInfo.getPropertyInfo(SbmUtils.narrowToClassType(commonEleType));
		
		Set<String> propertyKeyes=pvm.keySet();
		for(String property : propertyKeyes)
		{
			//忽略自定义类型关键字
			if(KEY_CUSTOM_ELEMENT_CLASSES.equals(property) || KEY_CUSTOM_CLASS.equals(property))
				continue;
			
			Object value=pvm.get(property);
			
			//明确指定了索引位置
			if(isIndexAccessor(property))
			{
				int idx=-1;
				try
				{
					idx=Integer.parseInt(property);
				}
				catch(Exception e)
				{
					throw new GenericConvertException("illegal index value "+SbmUtils.toString(property)+" of property "+SbmUtils.toString(pvm.getPropertyNamePath(property)), e);
				}
				
				while(result.size() < idx+1)
					result.add(null);
				
				Object element=result.get(idx);
				
				//元素存在并且值是属性值映射表，则依次设置这些属性
				if(element!=null && (value instanceof PropertyValueMap))
				{
					PropertyValueMap subPropMap=(PropertyValueMap)value;
					
					PropertyInfo subBeanInfo=PropertyInfo.getPropertyInfo(element.getClass());
					Set<String> subPropKeys=subPropMap.keySet();
					for(String subProp : subPropKeys)
					{
						//忽略自定义类型
						if(KEY_CUSTOM_CLASS.equals(subProp))
							continue;
						
						PropertyInfo subPropInfo=getSubPropertyInfoNotNull(subBeanInfo, subProp);
						
						try
						{
							setJavaBeanProperty(element, subPropInfo, subPropMap.get(subProp));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(pvm, property, e);
						}
					}
				}
				//否则，创建这个元素对象
				else
				{
					try
					{
						element=convertObjectToType(value, getMapTargetListElementCustomType(pvm, idx, customTypes, elementType));
					}
					catch(ConvertException e)
					{
						handlePropertyValueMapConvertException(pvm, property, e);
					}
					
					result.set(idx, element);
				}
			}
			//没有明确指定索引位置，而直接是属性名，那么元素必定是JavaBean，且他们都具有commonEleBeanInfo属性信息
			else
			{
				if(value == null)
					continue;
				
				PropertyInfo propInfo=null;
				//忽略无关属性
				if(!pvm.isCleaned() && commonEleBeanInfo!=null)
					propInfo=commonEleBeanInfo.getSubPropertyInfo(property);
				else
				{
					if(commonEleBeanInfo == null)
						throw new GenericConvertException("illegal key "+SbmUtils.toString(pvm.getPropertyNamePath(property))
								+" in Map "+SbmUtils.toString(pvm)+", you must specify the target collection index but not JavaBean property because its element type "+SbmUtils.toString(commonEleType)+" is not JavaBean class");
					
					propInfo=getSubPropertyInfoNotNull(commonEleBeanInfo, property);
				}
				
				if(propInfo == null)
					continue;
				
				//当前属性值是数组
				if(value.getClass().isArray())
				{
					int len=Array.getLength(value);
					
					while(result.size() < len)
						result.add(null);
					
					for(int i=0; i<len; i++)
					{
						Object element=result.get(i);
						if(element == null)
						{
							element=instance(getMapTargetListElementCustomType(pvm, i, customTypes, elementType), -1);
							result.set(i, element);
						}
						
						try
						{
							setJavaBeanProperty(element, propInfo, Array.get(value, i));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(pvm, property, e);
						}
					}
				}
				//当前值是属性值映射表，则要将当前属性值转换为集合，并依次赋值
				else if(value instanceof PropertyValueMap)
				{
					PropertyValueMap pppm=(PropertyValueMap)value;
					
					List<?> propList=convertPropertyValueMapToList(pppm, List.class, propInfo.getPropType());
					
					while(result.size() < propList.size())
						result.add(null);
					
					for(int i=0; i<propList.size(); i++)
					{
						Object element=result.get(i);
						if(element ==null)
						{
							element=instance(getMapTargetListElementCustomType(pvm, i, customTypes, elementType), -1);
							result.set(i, element);
						}
						
						try
						{
							setJavaBeanProperty(element, propInfo, propList.get(i));
						}
						catch(ConvertException e)
						{
							handlePropertyValueMapConvertException(pvm, property, e);
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
						element=instance(getMapTargetListElementCustomType(pvm, 0, customTypes, elementType), -1);
						result.set(0, element);
					}
					
					try
					{
						setJavaBeanProperty(element, propInfo, value);
					}
					catch(ConvertException e)
					{
						handlePropertyValueMapConvertException(pvm, property, e);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 将属性值映射表转换为目标映射表, <code>sourceMap</code>的关键字将被转换目标映射表的关键字，值将被转换为此关键字对应的值
	 * @param pvm
	 * @param mapClass
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	protected Map<?, ?> convertPropertyValueMapToMap(PropertyValueMap pvm, Class<?> mapClass, Type keyType, Type valueType) throws ConvertException
	{
		if(pvm == null)
			return null;
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> result=(Map<Object, Object>)instance(mapClass, -1);
		
		Set<String> keys=pvm.keySet();
		for(String key : keys)
		{
			Object tk=null;
			Object tv=null;
			
			try
			{
				tk=convertObjectToType(key, keyType);
			}
			catch(ConvertException e)
			{
				throw new GenericConvertException("convert "+SbmUtils.toString(key)+" in key "+SbmUtils.toString(pvm.getPropertyNamePath(key))+" to Map key of type "+SbmUtils.toString(keyType)+" failed", e);
			}
			
			try
			{
				Object value=pvm.get(key);
				tv=convertObjectToType(value, valueType);
			}
			catch(ConvertException e)
			{
				handlePropertyValueMapConvertException(pvm, key, e);
			}
			
			result.put(tk, tv);
		}
		
		return result;
	}
	
	/**
	 * 获取映射表中的自定义转换类型，映射表中的“class”关键字的字符串值或者{@link Class}值将被认为是自定义类型
	 * @param map
	 * @return
	 * @date 2012-5-20
	 */
	protected Type getMapCustomTargetType(Map<?, ?> map, Type defaultType)
	{
		if(map.isEmpty())
			return defaultType;
		
		Object typeObj=map.get(KEY_CUSTOM_CLASS);
		
		if(typeObj == null)
			return defaultType;
		else if(typeObj instanceof Type)
			return (Type)typeObj;
		else if(typeObj instanceof String)
		{
			Type re=stringToType((String)typeObj);
			return (re == null ? defaultType : re);
		}
		else
			throw new GenericConvertException("illegal custom target type "+SbmUtils.toString(typeObj)+" with key "+SbmUtils.toString(KEY_CUSTOM_ELEMENT_CLASSES)+" in Map "+SbmUtils.toString(map));
	}
	
	/**
	 * 获取映射表转换列表目标的元素自定义类型数组
	 * @param map
	 * @return
	 * @date 2012-5-21
	 */
	protected Type[] getMapTargetListElementCustomTypes(Map<?, ?> map)
	{
		if(map.isEmpty())
			return null;
		
		Object typeObj=map.get(KEY_CUSTOM_ELEMENT_CLASSES);
		
		if(typeObj == null)
			return null;
		else
		{
			Type[] re=null;
			
			if(typeObj instanceof String[])
			{
				String[] strTypes=(String[])typeObj;
				re=new Type[strTypes.length];
				
				for(int i=0; i<strTypes.length; i++)
					re[i]=stringToType(strTypes[i]);
			}
			else if(typeObj instanceof Type[])
			{
				re=(Type[])typeObj;
			}
			else
				throw new GenericConvertException("illegal custom target type "+SbmUtils.toString(typeObj)+" with key "+SbmUtils.toString(KEY_CUSTOM_ELEMENT_CLASSES)+" in Map "+SbmUtils.toString(map));
			
			return re;
		}
	}

	/**
	 * 获取属性值映射表转换的目标类型
	 * @param map
	 * @param idx 索引，当属性值映射表对应集合类型时，可能会有多个目标类型
	 * @param customListEleTypes
	 * @param defaultType
	 * @return
	 * @date 2012-5-21
	 */
	protected Type getMapTargetListElementCustomType(Map<?, ?> map, int idx, Type[] customListEleTypes, Type defaultType)
	{
		Type re=null;
		
		Object cv=map.get(String.valueOf(idx));
		
		//优先查找类似"0.class"的自定义类型
		if(cv!=null && (cv instanceof Map<?, ?>))
		{
			re=getMapCustomTargetType((Map<?, ?>)cv, null);
		}
		
		if(re==null && customListEleTypes!=null && customListEleTypes.length>0)
		{
			if(idx >= customListEleTypes.length)
			{
				//没有默认类型，则沿用自定义类型
				if(defaultType == null)
					re=customListEleTypes[customListEleTypes.length-1];
			}
			else
				re=customListEleTypes[idx];
		}
		
		return (re==null ? defaultType : re);
	}
	
	/**
	 * 是否是没有定义边界的WildcardType，即“?”
	 * @param type
	 * @return
	 * @date 2012-5-20
	 */
	protected boolean isSimpleWildcardType(Type type)
	{
		if(!(type instanceof WildcardType))
			return false;
		
		WildcardType wt=(WildcardType)type;
		
		Type[] lb=wt.getLowerBounds();
		Type[] ub=wt.getUpperBounds();
		
		if((lb==null ||lb.length==0) && (ub==null || ub.length==0))
			return true;
		else
			return false;
	}
	
	/**
	 * 给定的字符串是否是索引值而非属性名（全部由数字组成）
	 * @param str
	 * @return
	 */
	protected boolean isIndexAccessor(String str)
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
			converterNotFoundThrow(sourceType, targetType);
		
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
	 * 由列表对象转换为{@linkplain java.util.Set Set}对象，它不会对列表对象的元素执行类型转换
	 * @param list
	 * @param setClass
	 * @return
	 * @date 2012-2-19
	 */
	@SuppressWarnings("unchecked")
	protected Set<?> listToSet(List<?> list, Class<?> setClass)
	{
		Set<Object> result=null;
		
		if(list != null)
		{
			result=(Set<Object>)instance(setClass, -1);
			
			for(int i=0,len=list.size(); i<len; i++)
				result.add(list.get(i));
		}
		
		return result;
	}
	
	/**
	 * 由列表对象转换为数组对象，它不会对列表对象的元素执行类型转换
	 * @param list 列表对象
	 * @param elementType 目标数组的元素类型
	 * @return
	 * @date 2012-2-19
	 */
	protected Object listToArray(List<?> list, Type elementType)
	{
		Object result=null;
		
		if(list!=null)
		{
			int size=list.size();
			
			result=instance(elementType, size);
			
			for(int i=0; i<size; i++)
				Array.set(result, i, list.get(i));
		}
		
		return result;
	}
	
	/**
	 * 设置对象的某个属性值，如果值类型与属性类型不匹配，值对象将被转换
	 * @param obj 要设置属性的对象
	 * @param propertyInfo 要设置的属性信息
	 * @param value 属性值
	 * @date 2012-2-20
	 */
	protected void setJavaBeanProperty(Object obj, PropertyInfo propertyInfo, Object value) throws ConvertException
	{
		Type targetType=propertyInfo.getPropGenericType();
		if(!SbmUtils.isClassType(targetType))
			targetType=toConcreteType(targetType, propertyInfo.getOwnerClass());
		
		Object destValue=convertObjectToType(value, targetType);
		try
		{
			propertyInfo.getWriteMethod().invoke(obj, new Object[]{destValue});
		}
		catch(Exception e)
		{
			throw new GenericConvertException("exception occur while calling write method "+SbmUtils.toString(propertyInfo.getWriteMethod()),e);
		}
	}
	
	/**
	 * 获取子属性信息
	 * @param parent
	 * @param property
	 * @return
	 * @date 2012-2-26
	 */
	protected PropertyInfo getSubPropertyInfoNotNull(PropertyInfo parent, String property)
	{
		PropertyInfo re=parent.getSubPropertyInfo(property);
		if(re == null)
			throw new GenericConvertException("can not find property "+SbmUtils.toString(property)+" in class "+SbmUtils.toString(parent.getPropType()));
		
		return re;
	}
	
	/**
	 * 使用转换器转换对象
	 * @param converter
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @date 2011-4-10
	 */
	protected Object doConvert(Converter converter, Object sourceObj, Type targetType) throws ConvertException
	{
		return converter.convert(sourceObj, targetType);
	}
	
	/**
	 * 专门用于抛出找不到转换器的异常方法
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @date 2012-4-1
	 */
	protected Object converterNotFoundThrow(Type sourceType, Type targetType)
	{
		throw new GenericConvertException("can not find Converter for converting "+SbmUtils.toString(sourceType)+" to "+SbmUtils.toString(targetType));
	}
	
	/**
	 * 将给定映射表转换为属性值映射表
	 * @param map
	 * @return
	 * @date 2012-4-1
	 */
	protected PropertyValueMap toPropertyValueMap(Map<String, ?> map)
	{
		return ((map instanceof PropertyValueMap) ? (PropertyValueMap)map : new PropertyValueMap(map));
	}
	
	/**
	 * 产生转换器关键字，用于在映射表中标识转换器
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	protected ConverterKey generateConverterKey(Type sourceType, Type targetType)
	{
		return new ConverterKey(sourceType, targetType);
	}
	
	/**
	 * 创建给定类型的实例。<br>
	 * 如果<code>type</code>是{@linkplain List}、{@linkplain Set}、{@linkplain Map}接口，它将自动选择实现类。
	 * <code>arrayLength</code>指定是否是创建<code>type</code>类型的数组，如果小于0，则不是，大于或等于0表明是要创建数组。
	 * @param type 类型
	 * @param arrayLength 要创建数组的长度
	 * @return
	 * @date 2010-12-29
	 */
	protected Object instance(Type type, int arrayLength)
	{
		Class<?> clazz=null;
		
		if(SbmUtils.isClassType(type))
		{
			if(java.util.List.class.equals(type))
				clazz=ArrayList.class;
			else if(java.util.Set.class.equals(type))
				clazz=HashSet.class;
			else if(java.util.Map.class.equals(type))
				clazz=HashMap.class;
			else
				clazz=SbmUtils.narrowToClassType(type);
		}
		else
			clazz=null;
		
		try
		{
			if(clazz == null)
			{
				String fqn=SbmUtils.getFullQualifiedClassName(type);
				clazz=Class.forName(fqn);
			}
			
			if(arrayLength < 0)
				return clazz.newInstance();
			else
				return Array.newInstance(clazz, arrayLength);
		}
		catch(Exception e)
		{
			throw new GenericConvertException("exception occur while creating instance of type "+SbmUtils.toString(type),e);
		}
	}
	
	/**
	 * 将字符串转换为类型对象
	 * @param typeObj
	 * @return
	 * @date 2012-5-21
	 */
	protected Type stringToType(String str)
	{
		try
		{
			return Class.forName(str);
		}
		catch(ClassNotFoundException e)
		{
			throw new GenericConvertException("type named "+SbmUtils.toString(str)+" not found", e);
		}
	}
	
	/**
	 * 将给定类型具体化，它包含的所有{@linkplain TypeVariable}和{@linkplain WildcardType}都将被它们的边界类型替代
	 * @param type
	 * @return
	 * @date 2012-5-15
	 */
	protected Type toConcreteType(Type type)
	{
		return SbmUtils.toConcreteType(type, null);
	}
	
	/**
	 * 将给定类型具体化，它包含的所有{@linkplain TypeVariable}和{@linkplain WildcardType}都将被被<code>ownerClass</code>中具体化的类型所替代
	 * @param type
	 * @param ownerClass
	 * @return
	 * @date 2012-5-15
	 */
	protected Type toConcreteType(Type type, Class<?> ownerClass)
	{
		return SbmUtils.toConcreteType(type, ownerClass);
	}
	
	protected Map<ConverterKey, Converter> getConverters() {
		return converters;
	}

	protected void setConverters(Map<ConverterKey, Converter> converters) {
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
	 * 用于转换器映射表中主键的类
	 * @author earthangry@gmail.com
	 * @date 2011-10-8
	 */
	protected static class ConverterKey
	{
		private Type sourceType;
		private Type targetType;
		
		public ConverterKey(Type sourceType, Type targetType)
		{
			this.sourceType = sourceType;
			this.targetType = targetType;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((sourceType == null) ? 0 : sourceType.hashCode());
			result = prime * result
					+ ((targetType == null) ? 0 : targetType.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConverterKey other = (ConverterKey) obj;
			if (sourceType == null) {
				if (other.sourceType != null)
					return false;
			} else if (!sourceType.equals(other.sourceType))
				return false;
			if (targetType == null) {
				if (other.targetType != null)
					return false;
			} else if (!targetType.equals(other.targetType))
				return false;
			return true;
		}
	}
	
	/**
	 * 属性值映射表，它是关键字为<i>访问符表达式</i>映射表的分解结果。<br>
	 * 它的关键字表示某对象的某个属性名，而关键字对应的值则是这个对象该属性的值（或者是可以转换为该属性值的某个对象）。
	 * 它有一个特殊用途的{@linkplain #clean}属性，用以标识属性值映射表是否是清洁的，清洁的属性值映射表在转换为某对象时，
	 * 如果它的某个关键字找不到对应的对象属性名，转换将被终止；而如果属性值映射表不是清洁的，找不到对应对象属性名的关键字将被忽略。
	 * 
	 * @author earthangry@gmail.com
	 * @date 2012-3-27
	 */
	protected static class PropertyValueMap extends HashMap<String, Object>
	{
		private static final long serialVersionUID = 1L;
		
		/**保留关键字-表明源映射表中自定义转换的目标类型*/
		public static final String RESERVE_KEY_CUSTOM_CLASS="class";
		
		/**此属性值映射表的属性名*/
		private String propertyName;
		
		/**父属性值映射表*/
		private PropertyValueMap parent;
		
		/**是否是清洁的*/
		private boolean cleaned;
		
		/**
		 * 由源映射表创建属性值映射表
		 * @param map 源映射表，它的关键字具有<i>访问符表达式</i>语义
		 */
		public PropertyValueMap(Map<String, ?> map)
		{
			this(map, true);
		}
		
		/**
		 * 由源映射表创建属性值映射表
		 * @param map 源映射表，它的关键字具有<i>访问符表达式</i>语义
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
				return result+Constants.ACCESSOR+propertyName;
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
				String[] propKeys=SbmUtils.splitAccessExpression(key);
				
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
