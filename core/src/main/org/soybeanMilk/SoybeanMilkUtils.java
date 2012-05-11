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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.config.parser.ParseException;

/**
 * 框架内部常用类。
 * @author earthangry@gmail.com
 * @date 2010-12-31
 */
public class SoybeanMilkUtils
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
		return ancestor!=null && ancestor.isAssignableFrom(descendant);
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
			return ((Class<?>)obj).getName();
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
