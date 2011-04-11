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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@linkplain java.lang.Class Class}类型的属性信息封装类
 * @author earthAngry@gmail.com
 * @date 2010-4-4
 */
public class PropertyInfo
{
	private static Log log = LogFactory.getLog(PropertyInfo.class);
	
	/**属性类型*/
	private Class<?> type;
	/**属性的泛型类型*/
	private Type genericType;
	
	/**此属性类型的属性信息集，以属性名作为关键字*/
	private Map<String,PropertyInfo> subPropertyInfos;
	
	/**此属性的读方法*/
	private Method readMethod;
	
	/**此属性的写方法*/
	private Method writeMethod;

	/**此属性名*/
	private String name;
	
	protected PropertyInfo(Class<?> propertyType)
	{
		this(propertyType, null, null, null);
	}
	
	protected PropertyInfo(Class<?> type, String name, Method readMethod, Method writeMethod)
	{
		super();
		this.type = type;
		this.name=name;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		
		if(writeMethod != null)
			this.genericType=writeMethod.getGenericParameterTypes()[0];
		else
			this.genericType=type;
		
	}
	
	/**
	 * 获取属性的{@linkplain Class}类型。
	 * @return
	 * @date 2010-12-28
	 */
	public Class<?> getType() {
		return type;
	}

	protected void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * 获取属性的类型。它可能包含更多的信息，比如参数化类型。
	 * @return
	 * @date 2010-12-28
	 */
	public Type getGenericType() {
		return genericType;
	}

	protected void setGenericType(Type genericType) {
		this.genericType = genericType;
	}

	/**
	 * 获取此属性的类信息，以属性名作为关键字。如果没有类信息，则返回<code>null</code>（比如<code>int</code>类型）。
	 * @return
	 * @date 2010-12-28
	 */
	public Map<String, PropertyInfo> getSubPropertyInfos() {
		return subPropertyInfos;
	}

	protected void setSubPropertyInfos(Map<String, PropertyInfo> subPropertyInfos) {
		this.subPropertyInfos = subPropertyInfos;
	}

	/**
	 * 获取属性的读方法。
	 * @return
	 * @date 2010-12-28
	 */
	public Method getReadMethod() {
		return readMethod;
	}

	protected void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	/**
	 * 获取属性的写方法。
	 * @return
	 * @date 2010-12-28
	 */
	public Method getWriteMethod() {
		return writeMethod;
	}

	protected void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}
	
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 添加此属性所属类型的子属性信息
	 * @param propertyInfo
	 * @date 2010-12-28
	 */
	public void addSubPropertyInfo(PropertyInfo propertyInfo)
	{
		if(subPropertyInfos == null)
			subPropertyInfos=new HashMap<String, PropertyInfo>();
		
		if(propertyInfo.getName() == null)
			throw new IllegalArgumentException("the name of this PropertyInfo must not be null.");
		
		subPropertyInfos.put(propertyInfo.getName(), propertyInfo);
	}
	
	/**
	 * 取得此类型的某个属性信息
	 * @param name 属性名
	 * @return
	 */
	public PropertyInfo getSubPropertyInfo(String name)
	{
		return subPropertyInfos == null ? null : subPropertyInfos.get(name);
	}
	
	/**
	 * 是否有子属性信息
	 * @return
	 * @date 2011-1-2
	 */
	public boolean hasSubPropertyInfo()
	{
		return this.subPropertyInfos!=null && !this.subPropertyInfos.isEmpty();
	}
	
	//@Override
	public String toString() {
		return "PropertyInfo [name=" + name + ", type=" + type
				+ ", genericType=" + genericType + "]";
	}
	
	/**
	 * 缓存
	 */
	private static ConcurrentHashMap<Class<?>,PropertyInfo> propertyInfoCache = new ConcurrentHashMap<Class<?>, PropertyInfo>();
	
	/**
	 * 获取类的属性信息，一个仅包含<code>propertyType</code>属性（值为参数<code>beanClass</code>）的<code>PropertyInfo</code>对象将被返回，用作顶层对象。
	 * @param beanClass
	 * @return
	 * @date 2010-12-28
	 */
	public static PropertyInfo getPropertyInfo(Class<?> beanClass)
	{
		if(beanClass == null)
			return null;
		
		PropertyInfo beanInfo=null;
		
		beanInfo=propertyInfoCache.get(beanClass);
		if(beanInfo == null)
		{
			Map<Class<?>, PropertyInfo> localExists=new HashMap<Class<?>, PropertyInfo>();
			beanInfo=getPropertyInfoAnatomized(beanClass, localExists, 0);
		}
		else
		{
			if(log.isDebugEnabled())
				log.debug("get '"+beanClass.getName()+"' property information from cache");
		}
		
		return beanInfo;
	}
	
	private static PropertyInfo getPropertyInfoAnatomized(Class<?> beanClass, Map<Class<?>, PropertyInfo> localExists, int depth)
	{
		PropertyInfo cached=propertyInfoCache.get(beanClass);
		if(cached != null)
		{
			if(log.isDebugEnabled())
				log.debug(getSpace(depth)+"get '"+beanClass.getName()+"' property information from cache");
			
			return cached;
		}
		
		if(log.isDebugEnabled())
			log.debug(getSpace(depth)+"start  anatomizing '"+beanClass.getName()+"' property information");
		
		PropertyInfo beanInfo=new PropertyInfo(beanClass);
		beanInfo.setGenericType(beanClass);
		
		localExists.put(beanInfo.getType(), beanInfo);
		
		PropertyDescriptor[] pds=null;
		
		try
		{
			pds=Introspector.getBeanInfo(beanInfo.getType()).getPropertyDescriptors();
		}
		catch(IntrospectionException e)
		{
			throw new RuntimeException(e);
		}
		
		if(pds==null || pds.length==0)
			;
		else
		{
			for(PropertyDescriptor pd : pds)
			{
				String name=pd.getName();
				Method wm=pd.getWriteMethod();
				Method rm=pd.getReadMethod();
				Class<?> propertyClazz=pd.getPropertyType();
				
				//非法
				if(wm==null || rm==null
						|| !Modifier.isPublic(wm.getModifiers())
						|| !Modifier.isPublic(rm.getModifiers()))
					continue;
				
				//localExists保存了递归调用过程中的已有PropertyInfo对象，防止出现死循环
				PropertyInfo exist= localExists.get(propertyClazz);
				if(exist == null)
					exist=getPropertyInfoAnatomized(propertyClazz, localExists, depth+1);
				
				//拷贝已缓存的并设置特有的读写方法和名称
				PropertyInfo copied=new PropertyInfo(propertyClazz, name, rm, wm);
				copied.setSubPropertyInfos(exist.getSubPropertyInfos());
				
				beanInfo.addSubPropertyInfo(copied);
				
				if(log.isDebugEnabled())
					log.debug(getSpace(depth)+"add '"+copied+"'");
			}
		}
		
		if(log.isDebugEnabled())
			log.debug(getSpace(depth)+"finish anatomizing '"+beanClass.getName()+"' property information");
		
		propertyInfoCache.putIfAbsent(beanClass, beanInfo);
		
		return beanInfo;
	}
	
	private static String getSpace(int len)
	{
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<len;i++)
			sb.append("    ");
		
		return sb.toString();
	}
}
