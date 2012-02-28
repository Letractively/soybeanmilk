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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛型类型元信息。<br>
 * 它同时封装泛型类型的持有类信息，并提供了获取泛型类型的具体类型的方法。<br>
 * {@linkplain DefaultGenericConverter 默认通用转换器}即是靠它来支持泛型类型转换的。
 * @author earthAngry@gmail.com
 * @date 2011-9-29
 *
 */
public class GenericType implements Type
{
	private static final int TYPE_TypeVariable=1;
	private static final int TYPE_ParameterizedType=2;
	private static final int TYPE_GenericArrayType=3;
	private static final int TYPE_WildcardType=4;
	
	/**泛型类型*/
	private Type type;
	
	/**此泛型类型的持有类*/
	private Class<?> ownerClass;
	
	/**类型标识*/
	private int typeFlag;
	
	/**实际类型*/
	private Class<?> actualClass;
	
	/**参数类型*/
	private Class<?>[] paramClasses;
	
	/**数组元素类型*/
	private Class<?> componentClass;
	
	private Map<TypeVariable<?>, Type> ownerClassTypeVariables;
	
	/**
	 * 创建泛型类型元信息对象
	 * @param type 泛型类型
	 * @param ownerClass 此泛型类型的持有类
	 */
	private GenericType(Type type, Class<?> ownerClass)
	{
		super();
		this.type = type;
		this.ownerClass = ownerClass;
		
		this.ownerClassTypeVariables=new HashMap<TypeVariable<?>, Type>();
		extractTypeVariablesInType(this.ownerClass, this.ownerClassTypeVariables);
		
		if(this.type instanceof TypeVariable<?>)
		{
			this.typeFlag=TYPE_TypeVariable;
			
			this.actualClass=getTypeVariableActualClass((TypeVariable<?>)this.type);
		}
		else if(this.type instanceof ParameterizedType)
		{
			this.typeFlag=TYPE_ParameterizedType;
			
			ParameterizedType pt=(ParameterizedType)this.type;
			
			this.actualClass=getParameterizedTypeActualRawClass(pt);
			this.paramClasses=getParameterizedTypeActualParamClasses(pt);
		}
		else if(this.type instanceof GenericArrayType)
		{
			this.typeFlag=TYPE_GenericArrayType;
			
			this.actualClass=evaluateGenericTypeAcualClass(this.type);
			this.componentClass=getGenericArrayTypeActualComponentClass((GenericArrayType)this.type);
		}
		else if(this.type instanceof WildcardType)
		{
			this.typeFlag=TYPE_WildcardType;
			
			this.actualClass=getWildcardTypeActualClass((WildcardType)this.type);
		}
		else
			throw new IllegalArgumentException("unknown type '"+type+"'");
	}
	
	/**
	 * 获取此泛型类型
	 * @return
	 * @date 2011-10-1
	 */
	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	/**
	 * 获取泛型类型的持有类
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?> getOwnerClass()
	{
		return ownerClass;
	}
	
	public void setOwnerClass(Class<?> ownerClass)
	{
		this.ownerClass = ownerClass;
	}
	
	/**
	 * 获取泛型类型的实际类
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?> getActualClass()
	{
		return this.actualClass;
	}
	
	/**
	 * 如果此泛型类型是{@linkplain ParameterizedType}，则获取它的参数类型；否则，返回<code>null</code>
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?>[] getParamClasses()
	{
		return this.paramClasses;
	}
	
	/**
	 * 如果此泛型类型是泛型数组，则返回它的元素类型；否则，则返回<code>null</code>
	 * @return
	 * @date 2011-10-1
	 */
	public Class<?> getComponentClass()
	{
		return this.componentClass;
	}
	
	/**
	 * 是否为{@linkplain ParameterizedType}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isParameterizedType()
	{
		return TYPE_ParameterizedType == this.typeFlag;
	}
	
	/**
	 * 是否为{@linkplain TypeVariable}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isTypeVariable()
	{
		return TYPE_TypeVariable == this.typeFlag;
	}
	
	/**
	 * 是否为{@linkplain GenericArrayType}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isGenericArrayType()
	{
		return TYPE_GenericArrayType == this.typeFlag;
	}
	
	/**
	 * 是否为{@linkplain WildcardType}类型
	 * @return
	 * @date 2011-10-4
	 */
	public boolean isWildcardType()
	{
		return TYPE_WildcardType == this.typeFlag;
	}
	
	protected Class<?> getTypeVariableActualClass(TypeVariable<?> typeVariable)
	{
		Class<?> re=null;
		
		Type tp=ownerClassTypeVariables.get(typeVariable);
		
		//找不到，则使用上限类型再次解析
		if(tp == null)
		{
			Type[] upperBounds=typeVariable.getBounds();
			if(upperBounds!=null && upperBounds.length>=1)
				tp=upperBounds[0];
		}
		
		re=evaluateGenericTypeAcualClass(tp);
		
		return re;
	}
	
	protected Class<?> getGenericArrayTypeActualComponentClass(GenericArrayType gat)
	{
		Class<?> re=null;
		
		Type tp=gat.getGenericComponentType();
		re=evaluateGenericTypeAcualClass(tp);
		
		return re;
	}
	
	protected Class<?>[] getParameterizedTypeActualParamClasses(ParameterizedType pt)
	{
		Class<?>[] re=null;
		
		Type[] tps=pt.getActualTypeArguments();
		if(tps!=null)
		{
			re=new Class<?>[tps.length];
			for(int i=0; i<tps.length; i++)
				re[i]=evaluateGenericTypeAcualClass(tps[i]);
		}
		
		return re;
	}
	
	protected Class<?> getParameterizedTypeActualRawClass(ParameterizedType pt)
	{
		Type tp=pt.getRawType();
		
		return evaluateGenericTypeAcualClass(tp);
	}
	
	protected Class<?> getWildcardTypeActualClass(WildcardType pt)
	{
		Class<?> re=null;
		
		//通配符只能使用上限类型
		Type tp=null;
		Type[] upperBounds=pt.getUpperBounds();
		if(upperBounds!=null && upperBounds.length>=1)
			tp=upperBounds[0];
		
		re=evaluateGenericTypeAcualClass(tp);
		
		return re;
	}
	
	protected Class<?> evaluateGenericTypeAcualClass(Type type)
	{
		Class<?> re=null;
		
		//找不到对应类型，设置为Object.class
		if(type == null)
		{
			re=Object.class;
		}
		else if(type instanceof Class<?>)
		{
			re=(Class<?>)type;
		}
		else if(type instanceof WildcardType)
		{
			re=getWildcardTypeActualClass((WildcardType)type);
		}
		else if(type instanceof TypeVariable<?>)
		{
			re=getTypeVariableActualClass((TypeVariable<?>)type);
		}
		else if(type instanceof ParameterizedType)
		{
			re=getParameterizedTypeActualRawClass((ParameterizedType)type);
		}
		else if(type instanceof GenericArrayType)
		{
			re=getGenericArrayTypeActualComponentClass((GenericArrayType)type);
			re=Array.newInstance(re, 0).getClass();
		}
		else
			throw new IllegalArgumentException("unknown generic type '"+type+"'");
		
		return re;
	}
	
	protected void extractTypeVariablesInType(Type source, Map<TypeVariable<?>, Type> container)
	{
		if(source == null)
			return;
		else if(source instanceof Class<?>)
		{
			Class<?> clazz=(Class<?>)source;
			
			//实现的接口
			Type[] genericInterfaces=clazz.getGenericInterfaces();
			if(genericInterfaces != null)
			{
				for(Type t : genericInterfaces)
					extractTypeVariablesInType(t, container);
			}
			
			//父类
			Type genericSuperType=clazz.getGenericSuperclass();
			Class<?> superClass = clazz.getSuperclass();
			while(superClass != null && !Object.class.equals(superClass))
			{
				extractTypeVariablesInType(genericSuperType, container);
				
				genericSuperType = superClass.getGenericSuperclass();
				superClass = superClass.getSuperclass();
			}
			
			//外部类
			Class<?> outerClass=clazz;
			while(outerClass.isMemberClass())
			{
				Type genericOuterType=outerClass.getGenericSuperclass();
				extractTypeVariablesInType(genericOuterType, container);
				
				outerClass=outerClass.getEnclosingClass();
			}
		}
		else if(source instanceof ParameterizedType)
		{
			ParameterizedType pt=(ParameterizedType)source;
			
			if(pt.getRawType() instanceof Class<?>)
			{
				Type[] actualArgTypes=pt.getActualTypeArguments();
				TypeVariable<?>[] typeVariables=((Class<?>)pt.getRawType()).getTypeParameters();
				
				for(int i=0; i<actualArgTypes.length;i++)
				{
					TypeVariable<?> tv=typeVariables[i];
					Type tvType=actualArgTypes[i];
					
					container.put(tv, tvType);
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		String re="UnknownType";
		
		if(this.isTypeVariable() || this.isWildcardType())
		{
			return this.actualClass.getName();
		}
		else if(this.isGenericArrayType())
		{
			return this.componentClass.getName()+"[]";
		}
		else if(this.isParameterizedType())
		{
			StringBuilder sb=new StringBuilder();
			sb.append(this.actualClass.getName());
			sb.append('<');
			for(int i=0; i<this.paramClasses.length; i++)
			{
				sb.append(this.paramClasses[i].getName());
				
				if(i != this.paramClasses.length-1)
					sb.append(", ");
			}
			sb.append('>');
			
			re=sb.toString();
		}
		
		return re;
	}

	private static ConcurrentHashMap<GenericTypeKey, GenericType> genericTypeCache=new ConcurrentHashMap<GenericTypeKey, GenericType>();
	
	/**
	 * 获取泛型类型对象
	 * @param type 类型
	 * @param ownerClass <code>type</code>类型的持有类，如果为<code>null</code>，<code>type</code>中的类型变量将被解析为其边界类。
	 * @return
	 * @date 2011-10-2
	 */
	public static GenericType getGenericType(Type type, Class<?> ownerClass)
	{
		if(type instanceof Class<?>)
			throw new IllegalArgumentException("'"+type.toString()+"' is Class type, no generic type info");
		
		GenericType re=null;
		
		GenericTypeKey key=new GenericTypeKey(type, ownerClass);
		
		re=genericTypeCache.get(key);
		if(re == null)
		{
			re=new GenericType(type, ownerClass);
			genericTypeCache.putIfAbsent(key, re);
		}
		
		return re;
	}
	
	/**
	 * 用于泛型类型映射表中主键的类
	 * @author earthAngry@gmail.com
	 * @date 2011-10-8
	 */
	protected static class GenericTypeKey
	{
		private Type type;
		private Class<?> ownerClass;
		
		public GenericTypeKey(Type type, Class<?> ownerClass)
		{
			super();
			this.type = type;
			this.ownerClass = ownerClass;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((ownerClass == null) ? 0 : ownerClass.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
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
			GenericTypeKey other = (GenericTypeKey) obj;
			if (ownerClass == null) {
				if (other.ownerClass != null)
					return false;
			} else if (!ownerClass.equals(other.ownerClass))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
}
