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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 类属性信息封装类
 * @author earthAngry@gmail.com
 * @date 2010-4-4
 */
public class PropertyInfo
{
	private static Log log = LogFactory.getLog(PropertyInfo.class);
	
	/**属性类型*/
	private Class<?> propertyType;
	
	/**此属性类型的属性信息集，以属性名作为关键字*/
	private Map<String,PropertyInfo> subPropertyInfos;
	
	/**此属性的读方法*/
	private Method readMethod;
	
	/**此属性的写方法*/
	private Method writeMethod;
	
	protected PropertyInfo(Class<?> propertyType)
	{
		this(propertyType, null, null);
	}
	
	protected PropertyInfo(Class<?> propertyType, Method readMethod, Method writeMethod)
	{
		super();
		this.propertyType = propertyType;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
	}
	
	public Class<?> getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Class<?> propertyType) {
		this.propertyType = propertyType;
	}

	public Map<String, PropertyInfo> getSubPropertyInfos() {
		return subPropertyInfos;
	}

	public void setSubPropertyInfos(Map<String, PropertyInfo> subPropertyInfos) {
		this.subPropertyInfos = subPropertyInfos;
	}

	public Method getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	/**
	 * 是否是数组
	 * @return
	 * @date 2010-12-28
	 */
	public boolean isArray()
	{
		return propertyType.isArray();
	}
	
	/**
	 * 是否是<code>java.util.List</code>或其子类
	 * @return
	 * @date 2010-12-28
	 */
	public boolean isList()
	{
		return java.util.List.class.isAssignableFrom(propertyType);
	}
	
	/**
	 * 是否是<code>java.util.Map</code>或其子类
	 * @return
	 * @date 2010-12-28
	 */
	public boolean isMap()
	{
		return java.util.Map.class.isAssignableFrom(propertyType);
	}
	
	/**
	 * 是否是<code>java.util.Set</code>或其子类
	 * @return
	 * @date 2010-12-28
	 */
	public boolean isSet()
	{
		return java.util.Set.class.isAssignableFrom(propertyType);
	}
	
	/**
	 * 是否是Java接口
	 * @return
	 * @date 2010-12-28
	 */
	public boolean isInterface()
	{
		return propertyType.isInterface();
	}
	
	/**
	 * 添加子属性类信息
	 * @param name
	 * @param propertyInfo
	 * @date 2010-12-28
	 */
	public void addSubPropertyInfo(String name, PropertyInfo propertyInfo)
	{
		if(subPropertyInfos == null)
			subPropertyInfos=new HashMap<String, PropertyInfo>();
		
		if(name == null)
			throw new IllegalArgumentException("[name] must not be null.");
		
		subPropertyInfos.put(name, propertyInfo);
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
	
	@Override
	public String toString() {
		return "PropertyInfo [propertyType=" + propertyType + ", readMethod="
				+ readMethod + ", writeMethod=" + writeMethod + "]";
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
			log.debug(getSpace(depth)+"start  anatomized '"+beanClass.getName()+"' property information");
		
		PropertyInfo beanInfo=new PropertyInfo(beanClass);
		localExists.put(beanInfo.getPropertyType(), beanInfo);
		
		PropertyDescriptor[] pds=null;
		
		try
		{
			pds=Introspector.getBeanInfo(beanInfo.getPropertyType()).getPropertyDescriptors();
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
				
				//拷贝已缓存的并设置特有的读写方法
				PropertyInfo copied=new PropertyInfo(propertyClazz, rm, wm);
				copied.setSubPropertyInfos(exist.getSubPropertyInfos());
				
				beanInfo.addSubPropertyInfo(name, copied);
				
				if(log.isDebugEnabled())
					log.debug(getSpace(depth)+"add '"+name+"' named '"+copied+"'");
			}
		}
		
		if(log.isDebugEnabled())
			log.debug(getSpace(depth)+"finish anatomized '"+beanClass.getName()+"' property information");
		
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
