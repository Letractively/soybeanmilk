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
 * 调用语句解析器，它解析诸如"myReulst = myResolver.method(argKey0, argKey1)"之类字符串中的调用元素
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
		
		//结果关键字位置
		int resultKeyEndIdx=indexOf('=', methodLeftBracketIdx);
		
		//方法名之前的'.'访问符位置
		int methodDotIdx=lastIndexOf('.', methodLeftBracketIdx);
		if(methodDotIdx < 0)
			throw new ParseException("no key character  '.' found in statement \""+this.statement+"\"");
		
		setCurrentIdx(0);
		
		//解析方法的结果关键字
		
		if(resultKeyEndIdx < 0)
			invoke.setResultKey(null);
		else
		{
			ignoreFormatChars();
			
			String resultKey=parseUtil(KEY_CHAR_EQUAL, true, true, FORMAT_WITH_SPACE);
			if(resultKey==null || resultKey.length()==0)
				throw new ParseException("no result key segment found in statement \""+this.statement+"\"");
			
			invoke.setResultKey(resultKey);
			
			//移到'='之后
			setCurrentIdx(getCurrentIdx()+1);
		}
		
		//解析解决对象
		
		ignoreFormatChars();
		setEndIdx(methodDotIdx);
		
		Class<?> resolverClass=null;
		
		String resolver=parseUtil(null, true, true, FORMAT_WITH_SPACE);
		if(resolver==null || resolver.length()==0)
			throw new ParseException("no resolver id segment found in statement \""+this.statement+"\"");
		
		Object resolverBean=resolverFactory.getResolver(resolver);
		
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
		setCurrentIdx(getCurrentIdx()+1);
		
		//解析方法名
		
		ignoreFormatChars();
		setEndIdx(methodLeftBracketIdx);
		
		String methodName=parseUtil(null, true, true, FORMAT_WITH_SPACE);
		if(methodName==null || methodName.length()==0)
			throw new ParseException("no method name segment found in statement \""+this.statement+"\"");
		
		//移到'('之后
		setCurrentIdx(getCurrentIdx()+1);
		
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
				String key=null;
				Object value=null;
				
				Class<?> wrapClass=DefaultGenericConverter.toWrapperClass(argTypes[i]);
				String argStr=argStrList.get(i);
				
				//首字符为数字，则认为是数值
				if(argStr.length()>0 && isNumberChar(argStr.charAt(0)))
				{
					if(Byte.class.equals(wrapClass))
						value=new Byte(argStr);
					else if(Double.class.equals(wrapClass))
						value=new Double(argStr);
					else if(Integer.class.equals(wrapClass))
						value=new Integer(argStr);
					else if(Float.class.equals(wrapClass))
						value=new Float(argStr);
					else if(Integer.class.equals(wrapClass))
						value=new Integer(argStr);
					else if(Long.class.equals(wrapClass))
						value=new Long(argStr);
					else if(Short.class.equals(wrapClass))
						value=new Short(argStr);
					else
						throw new ParseException("can not create Number instance of class '"+argTypes[i]+"' with value \""+argStr+"\"");
				}
				else if("true".equals(argStr))
				{
					value=Boolean.TRUE;
				}
				else if("false".equals(argStr))
				{
					value=Boolean.FALSE;
				}
				else if(argStr.startsWith("\"") && argStr.endsWith("\"")
						&& argStr.length()>1)
				{
					value=argStr.substring(1, argStr.length()-1);
				}
				else if(argStr.startsWith("'") && argStr.endsWith("'")
						&& argStr.length()>1)
				{
					value=argStr.charAt(1);
				}
				else
					key=argStr;
				
				arg.setKey(key);
				arg.setValue(value);
				arg.setType(argTypes[i]);
				
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
	
	public int getEndIdx() {
		return endIdx;
	}
	
	/**
	 * 设置解析结束位置
	 * @param endIdx
	 */
	public void setEndIdx(int endIdx) {
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
		boolean isEscChar=false;
		
		for(; currentIdx<endIdx; currentIdx++)
		{
			char c=getChar(currentIdx);
			
			if(c == '\\' && !isEscChar)
			{
				isEscChar=true;
				continue;
			}
			
			if(isEscChar)
			{
				c=getEscChar(c);
				
				if(record)
					cache.append(c);
				
				isEscChar=false;
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
	
	protected boolean isNumberChar(char c)
	{
		return c>='0' && c<='9';
	}
	
	protected char getChar(int idx)
	{
		return this.statement.charAt(idx);
	}
	
	protected char getEscChar(char c)
	{
		if(c == 'n')
			return '\n';
		else if(c == 't')
			return '\t';
		else if(c == 'b')
			return '\b';
		else if(c == 'r')
			return '\r';
		else if(c == 'f')
			return '\f';
		else if(c == '\'')
			return c;
		else if(c =='"')
			return '"';
		else if(c == '\\')
			return c;
		else
			throw new ParseException("unknown ESC character '\\'"+c);
	}
}
