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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.web.WebConstants;

/**
 * WEB通用转换器，除了继承的转换支持，它还支持将WEB请求参数映射表{@link Map Map&lt;String,String[]&gt;}转换为JavaBean对象
 * @author earthAngry@gmail.com
 * @date 2010-10-8
 */
public class WebGenericConverter extends DefaultGenericConverter
{
	private static Log log = LogFactory.getLog(WebGenericConverter.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	private static final Object[] EMPTY_ARGS={};
	
	private static final String ACCESSOR_REGEX="\\"+WebConstants.ACCESSOR;
	
	private static ConcurrentHashMap<Class<?>,BeanInfo> beanInfoCache = new ConcurrentHashMap<Class<?>, BeanInfo>();;
	
	public WebGenericConverter()
	{
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object convert(Object sourceObj, Class<?> targetClass)
	{
		if(targetClass == null)
			return sourceObj;
		else
		{
			if(sourceObj instanceof Map)
			{
				if(targetClass.isAssignableFrom(sourceObj.getClass()))
					return sourceObj;
				else
				{
					//框架使用者可以自定义转换器
					Converter customizedConverter=getConverter(Map.class, targetClass);
					if(customizedConverter != null)
						return customizedConverter.convert(sourceObj, targetClass);
					else
						return convertMapToJavaBean((Map<String, Object>)sourceObj, targetClass);
				}
			}
			else
				return safeConvert(sourceObj, targetClass);
		}
	}
	
	/**
	 * 安全转换。<br>
	 * 如果<code>sourceObj</code>是数组，而<code>targetClass</code>不是，
	 * 并且没有<code>sourceObj</code>到<code>targetClass</code>的转换器，那么这个方法将尝试使用<code>sourceObj</code>的第一个元素（如果有的话）来转换；<br>
	 * 如果<code>sourceObj</code>为<code>null</code>或者出现转换异常，它将返回目标类型的默认值（参考{@link #getDefaultValue(Class)}）
	 * @param sourceObj
	 * @param targetClass
	 * @return
	 */
	protected Object safeConvert(Object sourceObj, Class<?> targetClass)
	{
		if(_logDebugEnabled)
			log.debug("start converting '"+getStringDesc(sourceObj)+"' of type '"+(sourceObj == null ? null : sourceObj.getClass().getName())+"' to type '"+targetClass.getName()+"'");
		
		if(sourceObj == null)
		{
			Object dv = getDefaultValue(targetClass);
			
			if(_logDebugEnabled)
				log.debug("the source Object is null, so the default value '"+dv+"' of type '"+targetClass+"' will be used");
			
			return dv;
		}
		
		Class<?> sourceClass=sourceObj.getClass();
		if(toWrapperClass(targetClass).isAssignableFrom(sourceClass))
			return sourceObj;
		
		Converter c = getConverter(sourceClass, targetClass);
		
		if(c == null)
		{
			//如果源对象是数组而目标类型不是，则使用数组的第一个元素转换
			if(sourceClass.isArray() && !targetClass.isArray())
			{
				if(_logDebugEnabled)
					log.debug("the source '"+getStringDesc(sourceObj)+"' is an array while the target Class is not, so it's first element will be used for converting");
				
				sourceClass=sourceClass.getComponentType();
				sourceObj=Array.getLength(sourceObj) ==0 ? null : Array.get(sourceObj, 0);
				
				if(toWrapperClass(targetClass).isAssignableFrom(sourceClass))
					return sourceObj;
				
				c = getConverter(sourceClass, targetClass);
			}
		}
		
		if(c == null)
			throw new ConvertException("no Converter defined for converting '"+sourceObj.getClass().getName()+"' to '"+targetClass.getName()+"'");
		
		if(_logDebugEnabled)
				log.debug("find Converter '"+c.getClass().getName()+"' for converting '"+sourceObj.getClass().getName()+"' to '"+targetClass.getName()+"'");
		
		try
		{
			return c.convert(sourceObj, targetClass);
		}
		catch(Exception e)
		{
			Object dv = getDefaultValue(targetClass);
			
			if(_logDebugEnabled)
				log.debug("default value '"+dv+"' is used while converting '"+sourceObj+"' to '"+targetClass.getName()+"' because the following exception :", e);
			
			return dv;
		}
	}
	
	/**
	 * 将映射表转换成<code>javaBeanClass</code>类的对象
	 * @param valueMap 值映射表，它也可能包含不是<code>javaBeanClass</code>类属性的关键字
	 * @param javaBeanClass
	 * @return
	 */
	protected Object convertMapToJavaBean(Map<String, Object> valueMap, Class<?> javaBeanClass)
	{
		if(valueMap==null || valueMap.size()==0)
			return null;
		
		Object bean = null;
		BeanInfo beanInfo=getBeanInfo(javaBeanClass);
		
		Set<String> keys=valueMap.keySet();
		for(String k : keys)
		{
			String[] propertyExpression=k.split(ACCESSOR_REGEX);
			if(propertyExpression==null || propertyExpression.length==0)
				propertyExpression=new String[]{k};
			
			if(beanInfo.getPropertyBeanInfo(propertyExpression[0]) != null)
			{
				//延迟初始化
				if(bean == null)
					bean = instance(beanInfo);
				
				setPropertyValue(bean, beanInfo, propertyExpression, 0, valueMap.get(k));
			}
		}
		
		return bean;
	}
	
	/**
	 * 取得类型的默认值
	 * @param type
	 * @return
	 */
	protected Object getDefaultValue(Class<?> type)
	{
		if(boolean.class == type)
			return false;
		else if(byte.class == type)
			return 0;
		else if(char.class == type)
			return 0;
		else if(double.class == type)
			return 0;
		else if(float.class == type)
			return 0;
		else if(int.class == type)
			return 0;
		else if(long.class == type)
			return 0;
		else if(short.class == type)
			return 0;
		else
			return null;
	}
	
	/**
	 * 设置JavaBean某个属性的值
	 * @param bean JavaBean对象
	 * @param beanInfo 该对象的Bean信息
	 * @param propertyExpression 属性层级数组，比如["address","home"]表示该Bean的address属性的home属性
	 * @param index 当前正在处理的属性层级数组的索引
	 * @param srcValue 属性对应的值
	 */
	private void setPropertyValue(Object bean, BeanInfo beanInfo, String[] propertyExpression, int index, Object srcValue)
	{
		if(index >= propertyExpression.length)
			return;
		
		BeanInfo propertyInfo=beanInfo.getPropertyBeanInfo(propertyExpression[index]);
		if(propertyInfo == null)
			throw new ConvertException("can not find property '"+propertyExpression[index]+"' in class '"+beanInfo.getBeanClass().getName()+"'");
		
		//自上而下递归，到达末尾时初始化和写入
		if(index == propertyExpression.length-1)
		{
			Object destValue=safeConvert(srcValue, propertyInfo.getBeanClass());
			try
			{
				propertyInfo.getBeanWriteMethod().invoke(bean, new Object[]{destValue});
			}
			catch(Exception e)
			{
				throw new ConvertException("exception occur while calling write method '"+propertyInfo.getBeanWriteMethod()+"'",e);
			}
		}
		else
		{
			Object propertyInstance=null;
			boolean toWrite=false;
			
			//查看对象是否已经初始化
			try
			{
				propertyInstance=propertyInfo.getBeanReadMethod().invoke(bean, EMPTY_ARGS);
			}
			catch(Exception e)
			{
				throw new ConvertException("exception occur while calling read method '"+propertyInfo.getBeanReadMethod().getName()+"'",e);
			}
			
			//初始化
			if(propertyInstance == null)
			{
				propertyInstance=instance(propertyInfo);
				toWrite=true;
			}
			
			//先将对象构建完成，再写入
			setPropertyValue(propertyInstance, propertyInfo, propertyExpression, index+1, srcValue);
			
			//如果之前已经写入了该对象，则不需要再写一次
			if(toWrite)
			{
				try
				{
					propertyInfo.getBeanWriteMethod().invoke(bean, new Object[]{propertyInstance});
				}
				catch(Exception e)
				{
					throw new ConvertException("exception occur while calling write method '"+propertyInfo.getBeanWriteMethod().getName()+"'",e);
				}
			}
		}
	}
	
	/**
	 * 获取bean类的BeanInfo
	 * @param beanClass
	 * @return
	 */
	private BeanInfo getBeanInfo(Class<?> beanClass)
	{
		BeanInfo beanInfo=null;
		
		beanInfo=beanInfoCache.get(beanClass);
		if(beanInfo == null)
		{
			beanInfo=anatomize(beanClass);
			beanInfoCache.putIfAbsent(beanClass, beanInfo);
		}
		
		return beanInfo;
	}
	
	/**
	 * 分解类
	 * @param beanClass
	 * @return
	 */
	private BeanInfo anatomize(Class<?> beanClass)
	{
		BeanInfo bf=new BeanInfo(beanClass);
		
		if(_logDebugEnabled)
			log.debug("");
		
		anatomizeRecursion(bf,0);
		
		if(_logDebugEnabled)
			log.debug("");
		
		return bf;
	}
	
	/**
	 * 递归分解
	 * @param beanInfo
	 * @param depth
	 */
	private void anatomizeRecursion(BeanInfo beanInfo,int depth)
	{
		if(_logDebugEnabled)
		{
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<depth;i++)
				sb.append("    ");
			
			log.debug(sb.toString()+beanInfo.toString());
		}
		
		Class<?> beanClass=beanInfo.getBeanClass();
		
		Class<?> sourceClass = null;
		if(beanClass.isArray())
			sourceClass = String[].class;
		else
			sourceClass = String.class;
		
		//找到对应的转换器，则不再继续分解，并且数组必须有其对应的转换器
		if(beanClass.isAssignableFrom(sourceClass) || getConverter(sourceClass, beanClass)!=null)
			return;
		else if(beanClass.isArray())
			throw new ConvertException("no Converter defined for converting '"+sourceClass.getName()+"' to '"+beanClass.getName()+"'");
		
		PropertyDescriptor[] pds=null;
		
		try
		{
			pds=Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
		}
		catch(IntrospectionException e)
		{
			throw new ConvertException(e);
		}
		
		for(PropertyDescriptor pd : pds)
		{
			Method wm=pd.getWriteMethod();
			Method rm=pd.getReadMethod();
			
			//非法
			if(wm==null || rm==null
					|| !Modifier.isPublic(wm.getModifiers())
					|| !Modifier.isPublic(rm.getModifiers()))
				continue;
			
			Class<?> propertyClass=pd.getPropertyType();
			
			BeanInfo bf=new BeanInfo(propertyClass, pd.getName(), beanInfo, pd.getReadMethod(), pd.getWriteMethod());
			
			anatomizeRecursion(bf,depth+1);
		}
	}
	
	/**
	 * 创建Bean实例
	 * @param beanInfo
	 * @return
	 */
	private Object instance(BeanInfo beanInfo) throws ConvertException
	{
		try
		{
			return beanInfo.getBeanClass().newInstance();
		}
		catch(Exception e)
		{
			throw new ConvertException("exception occur while creating instance for class '"+beanInfo.getBeanClass()+"' ",e);
		}
	}
	
	/**
	 * 取得对象的字符串描述
	 * @param o
	 * @return
	 */
	private static String getStringDesc(Object o)
	{
		if(o == null)
			return "null";
		else if(o.getClass().isArray())
		{
			StringBuffer cache = new StringBuffer();
			cache.append('[');
			
			for(int i=0,len=Array.getLength(o); i < len; i++)
			{
				Object e = Array.get(o, i);
				cache.append(getStringDesc(e));
				
				if(i < len-1)
					cache.append(',');
			}
			
			cache.append(']');
			
			return cache.toString();
		}
		else
			return o.toString();
	}
	
	/**
	 * javaBean类信息
	 * @author earthAngry@gmail.com
	 * @date 2010-4-4
	 */
	private static class BeanInfo
	{
		private static char ACCESSOR = WebConstants.ACCESSOR;
		
		private Class<?> beanClass;
		/**如果是某个类的属性类信息，则这是它的属性名，比如“age”*/
		private String beanName;
		/**如果是某个类的属性类信息，则这是它的属性层级名，比如“address.home”*/
		private String beanFullName;
		
		/**属性类信息集，以属性名作为关键字*/
		private Map<String,BeanInfo> propertyBeanInfos;
		
		/**如果是某个类的属性类信息，则这是它的读方法*/
		private Method beanReadMethod;
		/**如果是某个类的属性类信息，则这是它的写方法*/
		private Method beanWriteMethod;
		
		/**
		 * 创建指定类的类信息
		 * @param beanClass
		 */
		public BeanInfo(Class<?> beanClass)
		{
			this(beanClass, null, null, null, null);
		}
		
		/**
		 * 创建指定类的类信息，并且它是某个类的属性类信息
		 * @param beanClass
		 * @param beanName
		 * @param parent
		 * @param beanReadMethod
		 * @param beanWriteMethod
		 */
		public BeanInfo(Class<?> beanClass, String beanName, BeanInfo parent,
				Method beanReadMethod, Method beanWriteMethod)
		{
			this.beanClass = beanClass;
			this.beanName = beanName;
			
			if(parent != null)
				parent.addPropertyBeanInfo(this);
			
			this.beanReadMethod=beanReadMethod;
			this.beanWriteMethod=beanWriteMethod;
		}
		
		public Class<?> getBeanClass() {
			return beanClass;
		}
		public String getBeanName() {
			return beanName;
		}
		public void setBeanFullName(String beanFullName) {
			this.beanFullName = beanFullName;
		}
		public Method getBeanReadMethod() {
			return beanReadMethod;
		}
		public Method getBeanWriteMethod() {
			return beanWriteMethod;
		}
		
		/**
		 * 添加一个属性类信息
		 * @param beanInfo
		 */
		public void addPropertyBeanInfo(BeanInfo beanInfo)
		{
			if(propertyBeanInfos == null)
				propertyBeanInfos=new HashMap<String, BeanInfo>();
			
			if(beanInfo.getBeanName() == null)
				throw new IllegalArgumentException(BeanInfo.class.getSimpleName()+".getName() must not be null.");
			
			propertyBeanInfos.put(beanInfo.getBeanName(), beanInfo);
			
			if(this.beanFullName != null)
				beanInfo.setBeanFullName(this.beanFullName+ACCESSOR+beanInfo.getBeanName());
			else
				beanInfo.setBeanFullName(beanInfo.getBeanName());
		}
		
		/**
		 * 取得属性类信息
		 * @param name 属性名
		 * @return
		 */
		public BeanInfo getPropertyBeanInfo(String name)
		{
			return propertyBeanInfos == null ? null : propertyBeanInfos.get(name);
		}
		
		@Override
		public String toString()
		{
			return getClass().getSimpleName()+" [beanClass="
					+ beanClass.getName() + ", fullName=" + beanFullName + ", name=" + beanName
					+ ", readMethod=" + beanReadMethod
					+ ", writeMethod=" + beanWriteMethod + "]";
		}
	}
}