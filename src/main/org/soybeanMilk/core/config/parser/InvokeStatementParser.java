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

package org.soybeanMilk.core.config.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.resolver.FactoryResolverProvider;
import org.soybeanMilk.core.resolver.ResolverFactory;

/**
 * 调用语句解析器，它解析诸如"myReulst = myResolver.method(argKey0, argKey1, "string")"之类字符串中的与{@link Invoke 调用}对应的属性
 * @author earthAngry@gmail.com
 * @date 2010-11-25
 */
public class InvokeStatementParser
{
	private static final char[] FORMAT_WITH_SPACE={'\n', '\t', ' '};
	private static final char[] KEY_CHAR_EQUAL={'='};
	private static final char[] METHOD_ARG_END={'\n', '\t', ' ', ',', ')'};
	private static final char[] SINGLE_QUOTE={'\''};
	private static final char[] DOUBLE_QUOTE={'"'};
	
	/**输入语句*/
	private String statement;
	/**调用*/
	private Invoke invoke;
	/**解决对象工厂，解析器需要使用它查找解决对象的方法和参数类型*/
	private ResolverFactory resolverFactory;
	
	/**内部缓存，它临时存储解析字符*/
	private StringBuffer cache;
	/**当前解析位置*/
	private int currentIdx;
	/**解析的结束位置*/
	private int endIdx;
	/**总长度*/
	private int length;
	
	public InvokeStatementParser(Invoke invoke, String statement, ResolverFactory resolverFactory)
	{
		if(statement==null || statement.length()==0)
			throw new ParseException("[statement] must not be empty");
		
		this.invoke=invoke;
		this.statement=statement;
		this.resolverFactory=resolverFactory;
		
		this.cache=new StringBuffer();
		this.currentIdx=0;
		this.length=this.statement.length();
		this.endIdx=this.length;
	}
	
	public void parse()
	{
		//方法的左括弧
		int methodLeftBracketIdx=indexOf('(');
		if(methodLeftBracketIdx < 0)
			throw new ParseException("no key character '(' found in statement \""+this.statement+"\"");
		
		//等号位置
		int equalCharIdx=indexOf('=', methodLeftBracketIdx);
		
		//方法名之前的'.'访问符位置
		int methodLeftDotIdx=lastIndexOf('.', methodLeftBracketIdx);
		if(methodLeftDotIdx < 0)
			throw new ParseException("no key character  '.' found in statement \""+this.statement+"\"");
		
		setCurrentIdx(0);
		
		//解析方法的结果关键字
		
		if(equalCharIdx < 0)
			invoke.setResultKey(null);
		else
		{
			ignoreFormatChars();
			
			String resultKey=parseUtil(KEY_CHAR_EQUAL, true, true, FORMAT_WITH_SPACE);
			if(resultKey==null || resultKey.length()==0)
				throw new ParseException("no result key segment found in statement \""+this.statement+"\"");
			
			invoke.setResultKey(resultKey);
			
			//移到'='之后
			setCurrentIdx(equalCharIdx+1);
		}
		
		//解析解决对象
		
		ignoreFormatChars();
		setEndIdx(methodLeftDotIdx);
		
		Class<?> resolverClass=null;
		
		String resolver=parseUtil(null, true, true, FORMAT_WITH_SPACE);
		if(resolver==null || resolver.length()==0)
			throw new ParseException("no resolver id segment found in statement \""+this.statement+"\"");
		
		Object resolverBean= resolverFactory == null ? null : resolverFactory.getResolver(resolver);
		
		if(resolverBean != null)
		{
			invoke.setResolverProvider(new FactoryResolverProvider(resolverFactory, resolver));
			resolverClass=resolverBean.getClass();
		}
		else
		{
			try
			{
				resolverClass=Class.forName(resolver);
			}
			catch(ClassNotFoundException e)
			{
				throw new ParseException("can not find resolver class '"+resolver+"'");
			}
		}
		
		//移到'.'
		setCurrentIdx(methodLeftDotIdx+1);
		
		//解析方法名
		
		ignoreFormatChars();
		setEndIdx(methodLeftBracketIdx);
		
		String methodName=parseUtil(null, true, true, FORMAT_WITH_SPACE);
		if(methodName==null || methodName.length()==0)
			throw new ParseException("no method name segment found in statement \""+this.statement+"\"");
		
		//移到'('之后
		setCurrentIdx(methodLeftBracketIdx+1);
		
		//解析方法参数
		setEndIdx(this.length);
		List<String> argStrList=new ArrayList<String>();
		while(getCurrentIdx() < this.length)
		{
			ignoreFormatChars();
			
			char c=getCurrentChar();
			
			//遇到方法的右括弧
			if(c == ')')
				break;
			//参数分隔符
			else if(c == ',')
			{
				setCurrentIdx(getCurrentIdx()+1);
			}
			//字符串或者字符
			else if(c == '"')
			{
				this.cache.append('"');
				setCurrentIdx(getCurrentIdx()+1);
				
				String str=parseUtil(DOUBLE_QUOTE, true, true, null);
				argStrList.add(str+"\"");
				
				setCurrentIdx(getCurrentIdx()+1);
			}
			else if(c == '\'')
			{
				this.cache.append('\'');
				setCurrentIdx(getCurrentIdx()+1);
				
				String str=parseUtil(SINGLE_QUOTE, true, true, null);
				argStrList.add(str+"'");
				
				setCurrentIdx(getCurrentIdx()+1);
			}
			else
			{
				String str=parseUtil(METHOD_ARG_END, true, true, null);
				argStrList.add(str);
			}
		}
		
		Method method=Invoke.findMethodThrow(resolverClass, methodName, argStrList.size());
		invoke.setMethod(method);
		
		if(argStrList.size() > 0)
		{
			Class<?>[] argTypes=method.getParameterTypes();
			Arg[] args=new Arg[argTypes.length];
			
			for(int i=0;i<argTypes.length;i++)
			{
				Arg arg=new Arg();
				arg.setType(argTypes[i]);
				
				stringToArgProperty(arg, argStrList.get(i));
				
				args[i]=arg;
			}
			
			invoke.setArgs(args);
		}
	}
	
	protected int getCurrentIdx()
	{
		return this.currentIdx;
	}
	
	/**
	 * 设置解析当前位置
	 * @param currentIdx
	 */
	protected void setCurrentIdx(int currentIdx) {
		this.currentIdx = currentIdx;
	}
	
	protected int getEndIdx() {
		return endIdx;
	}
	
	/**
	 * 设置解析结束位置
	 * @param endIdx
	 */
	protected void setEndIdx(int endIdx) {
		this.endIdx = endIdx;
	}
	
	/**
	 * 忽略所有格式字符
	 */
	protected void ignoreFormatChars()
	{
		parseUtil(FORMAT_WITH_SPACE, false, false, null);
	}
	
	/**
	 * 从当前位置（{@link #getCurrentIdx()}）解析，直到遇到特殊字符或者不是特殊字符
	 * @param specialChars 特殊字符集合
	 * @param inSpecial 是否当遇到特殊字符时停止，否则，当不是特殊字符时停止
	 * @param record 是否保存遇到的字符
	 * @param ignoreChars 设置不保存的字符集合
	 * @return
	 */
	protected String parseUtil(char[] specialChars, boolean inSpecial, boolean record, char[] ignoreChars)
	{
		//前一个字符是否是转义标记'\'
		boolean preEscMark=false;
		
		for(; currentIdx<endIdx; currentIdx++)
		{
			char c=getChar(currentIdx);
			
			if(c == '\\' && !preEscMark)
			{
				preEscMark=true;
				
				if(record)
					cache.append(c);
			}
			else
			{
				//如果前一个字符是'\'，则不对此字符做任何特殊过滤
				if(preEscMark)
				{
					if(record)
						cache.append(c);
					
					preEscMark=false;
				}
				else
				{
					if(!inSpecial && !contain(specialChars, c))
						break;
					else if(inSpecial && contain(specialChars, c))
						break;
					
					if(record && !contain(ignoreChars, c))
						cache.append(c);
				}
			}
		}
		
		String re=null;
		if(record)
			re=cache.toString();
		else
			re=null;
		
		cache.delete(0, cache.length());
		
		return re;
	}
	
	/**
	 * 取得当前位置的字符
	 * @return
	 */
	protected char getCurrentChar()
	{
		return getChar(currentIdx);
	}
	
	protected int indexOf(char c)
	{
		return indexOf(c, this.length);
	}
	
	protected int indexOf(char c, int endIdx)
	{
		int i=0;
		for(; i<endIdx; i++)
		{
			if(getChar(i) == c)
				break;
		}
		
		return i == endIdx ? -1 : i;
	}
	
	protected int lastIndexOf(char c, int endIdx)
	{
		int i=endIdx-1;
		
		for(; i>=0; i--)
		{
			if(getChar(i) == c)
				break;
		}
		
		return i;
	}
	
	protected boolean contain(char[] chars, char c)
	{
		if(chars==null || chars.length==0)
			return false;
		
		for(char ch : chars)
			if(ch == c)
				return true;
		
		return false;
	}
	
	protected char getChar(int idx)
	{
		return this.statement.charAt(idx);
	}
	
	/**
	 * 设置{@link Arg}的关键字属性或者值属性，它根据字符串的语法格式（与Java语法一样）来确定应该设置哪个属性。<br>
	 * 比如，["abc"]是字符串值、[myresult_key]是关键字、['a']是字符值、[3.5f]是数值
	 * @param arg
	 * @param stmt 符合Java语法的字符串，可以包含转义字符和'\\uxxxx'格式字符
	 */
	public static void stringToArgProperty(Arg arg, String stmt)
	{
		if(stmt==null || stmt.length()==0)
			return;
		
		if(arg.getType() == null)
			throw new ParseException("the [type] property of this Arg must not be null");
		
		Class<?> wrapClass=DefaultGenericConverter.toWrapperClass(arg.getType());
		
		String key=null;
		Object value=null;
		
		//首字符为数字，则认为是数值
		if(stmt.length()>0 && isNumberChar(stmt.charAt(0)))
		{
			if(Byte.class.equals(wrapClass))
				value=new Byte(stmt);
			else if(Double.class.equals(wrapClass))
				value=new Double(stmt);
			else if(Float.class.equals(wrapClass))
				value=new Float(stmt);
			else if(Integer.class.equals(wrapClass))
				value=new Integer(stmt);
			else if(Long.class.equals(wrapClass))
				value=new Long(stmt);
			else if(Short.class.equals(wrapClass))
				value=new Short(stmt);
			else
				throw new ParseException("can not create Number instance of class '"+arg.getType()+"' with value \""+stmt+"\"");
		}
		else if("true".equals(stmt))
		{
			value=Boolean.TRUE;
		}
		else if("false".equals(stmt))
		{
			value=Boolean.FALSE;
		}
		else if("null".equals(stmt))
		{
			if(arg.getType().isPrimitive())
				throw new ParseException("can not set null to primitive type");
			
			value=null;
		}
		else if(stmt.startsWith("\"") && stmt.endsWith("\"")
				&& stmt.length()>1)
		{
			stmt=unEscape(stmt);
			
			if(stmt.length()<2)
				throw new ParseException("invalid String definition "+stmt+"");
			
			value=stmt.substring(1, stmt.length()-1);
		}
		else if(stmt.startsWith("'") && stmt.endsWith("'"))
		{
			stmt=unEscape(stmt);
			
			if(stmt.length() != 3)
				throw new ParseException("invalid char definition "+stmt+"");
			
			value=stmt.charAt(1);
		}
		else
			key=stmt;
		
		arg.setKey(key);
		arg.setValue(value);
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
						throw new ParseException("invalid \\uxxxx encoding in \""+s+"\"");
					
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
				        		throw new ParseException("invalid \\uxxxx encoding in \""+s+"\"");
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
	 * 判断字符是否是数字[0-9]
	 * @param c
	 * @return
	 */
	public static boolean isNumberChar(char c)
	{
		return c>='0' && c<='9';
	}
}
