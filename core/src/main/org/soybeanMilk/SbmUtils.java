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

package org.soybeanMilk;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.bean.CustomGenericArrayType;
import org.soybeanMilk.core.bean.CustomParameterizedType;
import org.soybeanMilk.core.config.parser.ParseException;

/**
 * 框架内部常用类。
 * @author earthangry@gmail.com
 * @date 2010-12-31
 */
public class SbmUtils
{
	/**
	 * 是否是<code>Class</code>类型
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isClassType(Type type)
	{
		return (type instanceof Class<?>);
	}
	
	/**
	 * 是否是数组类型
	 * @param type
	 * @return
	 * @date 2011-1-3
	 */
	public static boolean isArray(Class<?> type)
	{
		return type.isArray();
	}
	
	/**
	 * 将类型强制转换为<code>Class</code>
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static Class<?> narrowToClassType(Type type)
	{
		return (Class<?>)type;
	}
	
	/**
	 * 是否为基本类型
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isPrimitive(Type type)
	{
		return type!=null && isClassType(type) && narrowToClassType(type).isPrimitive();
	}
	
	/**
	 * 是否是枚举类型
	 * @param type
	 * @return
	 * @date 2011-1-9
	 */
	public static boolean isEnum(Type type)
	{
		return type!=null && isClassType(type) && narrowToClassType(type).isEnum();
	}
	
	/**
	 * 对象是否<code>instanceof</code>给定的类型
	 * @param obj
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isInstanceOf(Object obj, Type type)
	{
		if(obj==null || type==null)
			return false;
		else if(isClassType(type))
			return narrowToClassType(type).isInstance(obj);
		else
			return type.getClass().isInstance(obj);
	}
	
	/**
	 * 是否是超类
	 * @param ancestor
	 * @param descendant
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isAncestorClass(Class<?> ancestor, Class<?> descendant)
	{
		return ancestor!=null && ancestor.isAssignableFrom(narrowToClassType(descendant));
	}
	
	/**
	 * 是否是{@linkplain java.lang.Class Class}类型对象的数组类型
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static boolean isClassTypeArray(Type type)
	{
		if(type == null)
			return false;
		else if(isClassType(type))
			return narrowToClassType(type).isArray();
		else
			return false;
	}
	
	/**
	 * 返回基本类型的包装类型，如果不是基本类型，它将直接被返回
	 * @param type
	 * @return
	 * @date 2010-12-31
	 */
	public static Type toWrapperType(Type type)
	{
		if (!isPrimitive(type))
            return type;
		
		if (Byte.TYPE.equals(type))
            return Byte.class;
		else if (Short.TYPE.equals(type))
            return Short.class;
		else if (Integer.TYPE.equals(type))
        	return Integer.class;
		else if (Long.TYPE.equals(type))
            return Long.class;
		else if (Float.TYPE.equals(type))
            return Float.class;
        else if (Double.TYPE.equals(type))
            return Double.class;
        else if (Boolean.TYPE.equals(type))
            return Boolean.class;
        else if (Character.TYPE.equals(type))
            return Character.class;
        else
            return type;
	}
	
	public static Class<?> getRawClassType(Type type)
	{
		if(isClassType(type))
			return (Class<?>)type;
		else
		{
			String fqn=getFullQualifiedClassName(type);
			
			try
			{
				return Class.forName(fqn);
			}
			catch(ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 获取类型的完整名称，这个完整名称可以通过{@linkplain Class#forName(String)}来加载类
	 * @param type
	 * @return
	 * @date 2012-5-14
	 */
	public static String getFullQualifiedClassName(Type type)
	{
		String re=null;
		
		if(isClassType(type))
		{
			re=((Class<?>)type).getName();
		}
		else if(type instanceof ParameterizedType)
		{
			re=getFullQualifiedClassName(((ParameterizedType)type).getRawType());
		}
		else if(type instanceof GenericArrayType)
		{
			re=getFullQualifiedClassName(((GenericArrayType)type).getGenericComponentType());
			
			if(re.startsWith("[L"))
				re="["+re;
			else
				re="[L"+re+";";
		}
		else if(type instanceof TypeVariable<?>)
		{
			re=getFullQualifiedClassName(toConcreteType(type, (Class<?>)null));
		}
		else if(type instanceof WildcardType)
		{
			re=getFullQualifiedClassName(toConcreteType(type, (Class<?>)null));
		}
		else
			throw new IllegalArgumentException("unknown type '"+type+"'");
		
		return re;
	}
	
	/**
	 * 将<code>type</code>类型转换为具体化类型，它包含的所有{@linkplain TypeVariable}和{@linkplain WildcardType}类型都将被<code>ownerClass</code>的具体类型替代，
	 * 如果<code>type</code>中不包含这两种类型，它将直接被返回；否则，一个新的类型将被创建并返回。
	 * @param type
	 * @param ownerClass
	 * @return
	 * @date 2012-5-14
	 */
	public static Type toConcreteType(Type type, Class<?> ownerClass)
	{
		if(isClassType(type))
			return type;
		
		if(ownerClass == null)
		{
			return toConcreteTypeInner(type, null);
		}
		else
		{
			Map<TypeVariable<?>, Type> variableTypesMap=new HashMap<TypeVariable<?>, Type>();
			extractTypeVariablesInType(ownerClass, variableTypesMap);
			
			return toConcreteTypeInner(type, variableTypesMap);
		}
	}
	
	/**
	 * 具体化类型
	 * @param type
	 * @param variableTypesMap
	 * @return
	 * @date 2012-5-14
	 */
	private static Type toConcreteTypeInner(Type type, Map<TypeVariable<?>, Type> variableTypesMap)
	{
		Type result=null;
		
		if(type instanceof Class<?>)
		{
			result=type;
		}
		else if(type instanceof ParameterizedType)
		{
			ParameterizedType pt=(ParameterizedType)type;
			
			Type[] at=pt.getActualTypeArguments();
			Type[] cat=new Type[at.length];
			
			//如果pt的所有的参数类型都已具体化，则直接返回pt；否则，创建具体化的自定义参数类型
			boolean concrete=true;
			for(int i=0; i<at.length; i++)
			{
				cat[i]=toConcreteTypeInner(at[i], variableTypesMap);
				
				if(cat[i] != at[i])
					concrete=false;
			}
			
			if(concrete)
				result=pt;
			else
				result=new CustomParameterizedType(pt.getRawType(), pt.getOwnerType(), cat);
		}
		else if(type instanceof GenericArrayType)
		{
			GenericArrayType gap=(GenericArrayType)type;
			
			Type ct=gap.getGenericComponentType();
			Type cct=toConcreteTypeInner(ct, variableTypesMap);
			
			if(cct == ct)
				result=gap;
			else
				result=new CustomGenericArrayType(cct);
		}
		else if(type instanceof TypeVariable<?>)
		{
			TypeVariable<?> tv=(TypeVariable<?>)type;
			
			if(variableTypesMap != null)
				result=variableTypesMap.get(tv);
			
			if(result == null)
			{
				Type[] bounds=tv.getBounds();
				
				if(bounds==null || bounds.length==0)
					result=Object.class;
				else
					result=bounds[0];
			}
			
			result=toConcreteTypeInner(result, variableTypesMap);
		}
		else if(type instanceof WildcardType)
		{
			WildcardType wt=(WildcardType)type;
			
			Type[] ub=wt.getUpperBounds();
			Type[] lb=wt.getLowerBounds();
			
			Type cwt=(ub!=null && ub.length>0 ? ub[0] : null);
			if(cwt == null)
				cwt=(lb!=null && lb.length>0 ? lb[0] : null);
			if(cwt == null)
				cwt=Object.class;
			
			result=toConcreteTypeInner(cwt, variableTypesMap);
		}
		else
			result=type;
		
		return result;
	}
	
	/**
	 * 查找给定类型中包含的所有变量类型对应的具体类型，比如对于：
	 * <pre>
	 * class A&lt;T&gt;{}
	 * class B extends A&lt;Integer&gt;{}
	 * </pre>
	 * 执行<code>extractTypeVariablesInType(B.class, map)</code>方法，在<code>map</code>中将有：
	 * <pre>
	 * T    -------&gt;    Integer
	 * </pre>
	 * @param source
	 * @param container
	 * @date 2012-5-14
	 */
	private static void extractTypeVariablesInType(Type source, Map<TypeVariable<?>, Type> variableTypesMap)
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
					extractTypeVariablesInType(t, variableTypesMap);
			}
			
			//父类
			Type genericSuperType=clazz.getGenericSuperclass();
			Class<?> superClass = clazz.getSuperclass();
			while(superClass != null && !Object.class.equals(superClass))
			{
				extractTypeVariablesInType(genericSuperType, variableTypesMap);
				
				genericSuperType = superClass.getGenericSuperclass();
				superClass = superClass.getSuperclass();
			}
			
			//外部类
			Class<?> outerClass=clazz;
			while(outerClass.isMemberClass())
			{
				Type genericOuterType=outerClass.getGenericSuperclass();
				extractTypeVariablesInType(genericOuterType, variableTypesMap);
				
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
					
					variableTypesMap.put(tv, tvType);
				}
			}
		}
	}
	
	/**
	 * 获取对象的字符串表述
	 * @param obj
	 * @return
	 * @date 2012-2-28
	 */
	public static String toString(Object obj)
	{
		if(obj == null)
			return null;
		
		if(obj instanceof Class<?>)
		{
			return "'"+((Class<?>)obj).getName()+"'";
		}
		else if(obj instanceof String)
		{
			return "\""+obj+"\"";
		}
		else if(obj.getClass().isArray())
		{
			int len=Array.getLength(obj);
			
			StringBuilder sb=new StringBuilder();
			sb.append('[');
			
			for(int i=0; i<len; i++)
			{
				sb.append(toString(Array.get(obj, i)));
				
				if(i != len-1)
					sb.append(", ");
			}
			
			sb.append(']');
			
			return sb.toString();
		}
		else
			return obj.toString();
	}
	
	/**
	 * 反转义Java字符串
	 * @param s
	 * @return
	 */
	public static String unEscape(String s)
	{
		if(s==null || s.length()==0)
			return s;
		
		StringBuffer sb=new StringBuffer();
		
		int i=0;
		int len=s.length();
		while(i < len)
		{
			char c=s.charAt(i);
			
			if(c == '\\')
			{
				if(i == len-1)
					throw new ParseException("\""+s+"\" must not be end with '\\' ");
				
				i+=1;
				
				char next=s.charAt(i);
				if(next == 'u')
				{
					i+=1;
					int end=i+4;
					
					if(end > len)
						throw new ParseException("illegal \\uxxxx encoding in \""+s+"\"");
					
					int v=0;
					for (;i<end;i++)
					{
						next = s.charAt(i);
				        switch (next)
				        {
				        	case '0': case '1': case '2': case '3': case '4':
				        	case '5': case '6': case '7': case '8': case '9':
				        		v = (v << 4) + next - '0';
				        		break;
				        	case 'a': case 'b': case 'c':
				        	case 'd': case 'e': case 'f':
				        		v = (v << 4) + 10 + next - 'a';
				        		break;
				        	case 'A': case 'B': case 'C':
				        	case 'D': case 'E': case 'F':
				        		v = (v << 4) + 10 + next - 'A';
				        		break;
				        	default:
				        		throw new ParseException("illegal \\uxxxx encoding in \""+s+"\"");
				        }
					}
					
					sb.append((char)v);
				}
				else
				{
					if(next == 't') sb.append('\t');
					else if(next == 'r') sb.append('\r');
					else if(next == 'n') sb.append('\n');
					else if(next == '\'') sb.append('\'');
					else if(next == '\\') sb.append('\\');
					else if(next == '"') sb.append('"');
					else
						throw new ParseException("unknown escape character '\\"+next+"' ");
					
					i++;
				}
			}
			else
			{
				sb.append(c);
				i++;
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 将字符串从第一个{@linkplain Constants#ACCESSOR 访问符}位置拆分为两部分，如果不包含{@linkplain Constants#ACCESSOR 访问符}，则返回仅包含原字符串的长度为1的数组，
	 * 否则返回长度为2的且元素为拆分后的字符串的数组。
	 * @param str
	 * @return
	 * @date 2010-12-30
	 */
	public static String[] splitByFirstAccessor(String str)
	{
		String[] re=null;
		
		int idx=str.indexOf(Constants.ACCESSOR);
		
		if(idx < 0)
			re=new String[]{str};
		else if(idx == 0)
			re=new String[]{"", str};
		else if(idx == str.length()-1)
			re=new String[]{str, ""};
		else
			re=new String[]{str.substring(0,idx), str.substring(idx+1)};
		
		return re;
	}
	
	/**
	 * 拆分访问符表达式（以{@linkplain Constants#ACCESSOR 访问符}分隔的字符串）
	 * @param accessExpression
	 * @return
	 * @date 2012-2-21
	 */
	public static String[] splitAccessExpression(String accessExpression)
	{
		if(accessExpression == null)
			return null;
		
		String[] propertyArray=split(accessExpression, Constants.ACCESSOR);
		
		if(propertyArray==null || propertyArray.length==0)
			propertyArray=new String[]{accessExpression};
		
		return propertyArray;
	}
	
	/**
	 * 拆分字符串，连续的分隔符将按一个分隔符处理。
	 * @param str
	 * @param separatorChar 分隔符
	 * @return
	 * @date 2011-4-20
	 */
	public static String[] split(String str, char separatorChar)
	{
		boolean preserveAllTokens=false;
		
		//以下内容修改自org.apache.commons.lang.StringUtils.splitWorker(String, char, boolean)
		
		if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return null;//return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<String>();//List list = new ArrayList();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        
        return list.toArray(new String[list.size()]);//return (String[]) list.toArray(new String[list.size()]);
	}
}
