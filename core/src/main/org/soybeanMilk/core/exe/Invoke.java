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

package org.soybeanMilk.core.exe;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.exe.AbstractExecutable;
import org.soybeanMilk.core.exe.ArgPrepareExecuteException;
import org.soybeanMilk.core.exe.InvocationExecuteException;

/**
 * 调用，它用于执行对象方法
 * @author earthangry@gmail.com
 * @date 2010-9-30
 */
public class Invoke extends AbstractExecutable
{
	private static final long serialVersionUID = 1L;
	
	private static Log log=LogFactory.getLog(Invoke.class);
	
	/**调用方法名*/
	private String methodName;
	
	/**调用方法参数*/
	private transient Arg[] args;
	
	/**调用结果存放到对象源中的关键字*/
	private Serializable resultKey;
	
	/**调用解决对象*/
	private transient ResolverProvider resolverProvider;
	
	/**此调用的打断器在对象源中的关键字，打断器可以控制调用方法是否执行*/
	private Serializable breaker;
	
	private transient volatile MethodInfo methodInfo;
	
	public Invoke()
	{
		super();
	}
	
	public Invoke(String name, ResolverProvider resolverProvider, String methodName, Arg[] args, Serializable resultKey)
	{
		super.setName(name);
		this.methodName=methodName;
		this.args=args;
		this.resolverProvider=resolverProvider;
		this.resultKey=resultKey;
	}
	
	/**
	 * 获取调用方法名
	 * @return
	 * @date 2012-5-9
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * 设置调用方法名
	 * @return
	 * @date 2012-5-9
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * 获取调用参数
	 * @return
	 * @date 2012-5-9
	 */
	public Arg[] getArgs() {
		return args;
	}

	/**
	 * 设置调用参数
	 * @return
	 * @date 2012-5-9
	 */
	public void setArgs(Arg[] args) {
		this.args = args;
	}
	
	/**
	 * 获取调用结果存储关键字
	 * @return
	 * @date 2012-5-9
	 */
	public Serializable getResultKey() {
		return resultKey;
	}
	
	/**
	 * 设置调用结果存储关键字
	 * @return
	 * @date 2012-5-9
	 */
	public void setResultKey(Serializable resultKey) {
		this.resultKey = resultKey;
	}
	
	/**
	 * 获取调用目标提供者
	 * @return
	 * @date 2012-5-9
	 */
	public ResolverProvider getResolverProvider() {
		return resolverProvider;
	}
	
	/**
	 * 设置调用目标提供者
	 * @return
	 * @date 2012-5-9
	 */
	public void setResolverProvider(ResolverProvider resolverProvider) {
		this.resolverProvider = resolverProvider;
	}
	
	/**
	 * 获取调用打断器
	 * @return
	 * @date 2012-5-9
	 */
	public Serializable getBreaker() {
		return breaker;
	}
	
	/**
	 * 设置调用打断器
	 * @param breaker
	 * @date 2012-5-9
	 */
	public void setBreaker(Serializable breaker) {
		this.breaker = breaker;
	}
	
	//@Override
	public void execute(ObjectSource objectSource) throws ExecuteException
	{
		if(log.isDebugEnabled())
			log.debug("start  execute '"+this+"'");
		
		boolean breaked=isBreaked(objectSource);
		
		if(!breaked)
		{
			Object result=executeMethod(objectSource);
			saveMethodResult(result, objectSource);
		}
		else
		{
			if(log.isDebugEnabled())
				log.debug("Invoke method not executed, it is breaked in current object source");
		}
		
		if(log.isDebugEnabled())
			log.debug("finish execute '"+this+"'");
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [name="+getName()+", resultKey=" + resultKey + ", resolverProvider="
				+ resolverProvider + ", methodName=" + methodName + ", args="
				+ Arrays.toString(args) + ", breaker=" + breaker + "]";
	}

	/**
	 * 执行调用方法
	 * @param objectSource
	 * @throws ExecuteException
	 * @date 2011-1-12
	 */
	protected Object executeMethod(ObjectSource objectSource) throws ExecuteException
	{
		Resolver resolver=getResolver(objectSource);
		if(resolver == null)
			throw new ExecuteException("got null resolver from ResolverProvider '"+SoybeanMilkUtils.toString(this.getResolverProvider())+"'");
		
		Class<?> resolverClass=resolver.getResolverClass();
		if(resolverClass == null)
			throw new ExecuteException("the resolver class must not be null in Resolver '"+resolver+"'");
		
		MethodInfo methodInfo=getMethodInfo(resolverClass, this.methodName, this.args);
		if(methodInfo == null)
			throw new ExecuteException("no method named '"+this.methodName+"' with "+SoybeanMilkUtils.toString(this.args)
					+" arguments can be found in resolver class '"+SoybeanMilkUtils.toString(resolver.getResolverClass())+"'");
		
		Object[] argValues=prepareMethodArgValues(methodInfo, objectSource);
		
		try
		{
			return methodInfo.getMethod().invoke(resolver.getResolverObject(), argValues);
		}
		catch(InvocationTargetException e)
		{
			throw new InvocationExecuteException(this, e.getCause());
		}
		catch(IllegalArgumentException e)
		{
			throw new ExecuteException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new ExecuteException(e);
		}
	}
	
	/**
	 * 将调用方法结果保存到对象源
	 * @param methodResult
	 * @param objectSource
	 * @throws ExecuteException
	 * @date 2012-5-7
	 */
	protected void saveMethodResult(Object methodResult, ObjectSource objectSource) throws ExecuteException
	{
		Serializable resultKey=getResultKey();
		
		if(resultKey != null)
		{
			try
			{
				objectSource.set(resultKey, methodResult);
			}
			catch(ObjectSourceException e)
			{
				throw new ExecuteException(e);
			}
		}
	}
	
	/**
	 * 获取当前调用解决对象
	 * @param objectSource
	 * @return
	 * @throws ExecuteException
	 * @date 2012-5-6
	 */
	protected Resolver getResolver(ObjectSource objectSource) throws ExecuteException
	{
		try
		{
			return getResolverProvider().getResolver(objectSource);
		}
		catch(Exception e)
		{
			throw new ExecuteException(e);
		}
	}
	
	/**
	 * 获取方法的参数值数组
	 * @param methodInfo
	 * @param objectSource
	 * @return
	 * @throws ExecuteException
	 * @date 2012-5-6
	 */
	protected Object[] prepareMethodArgValues(MethodInfo methodInfo,ObjectSource objectSource) throws ExecuteException
	{
		Object[] values=null;
		
		Arg[] args=this.args;
		
		if(args != null)
		{
			values=new Object[args.length];
			
			for(int i=0;i<args.length;i++)
			{
				try
				{
					values[i]=args[i].getValue(objectSource, methodInfo.getArgType(i), methodInfo.getMethod(), methodInfo.getMethodClass());
				}
				catch(Exception e)
				{
					throw new ArgPrepareExecuteException(this, i, e);
				}
			}
		}
		
		if(log.isDebugEnabled())
			log.debug("prepared method arguments: "+SoybeanMilkUtils.toString(values));
		
		return values;
	}
	
	/**
	 * 此调用是否会在给定对象源上执行时被打断
	 * @param objectSource
	 * @return
	 * @date 2011-10-28
	 */
	protected boolean isBreaked(ObjectSource objectSource) throws ExecuteException
	{
		if(this.breaker == null)
			return false;
		
		Boolean breaked=null;
		
		if(this.breaker instanceof Boolean)
			breaked=(Boolean)this.breaker;
		else
		{
			Object breakerObj=null;
			
			try
			{
				breakerObj=objectSource.get(this.breaker, null);
			}
			catch(Exception e)
			{
				throw new ExecuteException(e);
			}
			
			if(breakerObj instanceof Boolean)
				breaked=(Boolean)breakerObj;
			else
				breaked=(breakerObj != null);
		}
		
		return breaked == null ? false : breaked;
	}
	
	/**
	 * 获取给定解决对象的{@linkplain MethodInfo 方法信息}对象
	 * @param methodClass
	 * @param methodName
	 * @param argNums
	 * @return
	 * @date 2012-5-7
	 */
	protected MethodInfo getMethodInfo(Class<?> methodClass, String methodName, Arg[] args)
	{
		MethodInfo methodInfo=getMethodInfo();
		
		if(methodInfo == null)
		{
			methodInfo=findMethodInfo(methodClass, methodName, args, true);
			if(methodInfo == null)
				methodInfo=findMethodInfo(methodClass, methodName, args, false);
			
			if(methodInfo != null)
				setMethodInfo(methodInfo);
		}
		
		return methodInfo;
	}
	
	protected MethodInfo getMethodInfo() {
		return methodInfo;
	}

	protected void setMethodInfo(MethodInfo methodInfo) {
		this.methodInfo = methodInfo;
	}
	
	/**
	 * 获取调用参数个数
	 * @return
	 * @date 2012-5-7
	 */
	protected int getArgNums()
	{
		return (this.args==null ? 0 : this.args.length);
	}
	
	/**
	 * 根据方法名和参数查找方法
	 * @param clazz
	 * @param methodName
	 * @param args
	 * @param exactMatch 是否精确匹配类型
	 * @return
	 * @throws ExecuteException
	 * @date 2012-5-11
	 */
	protected MethodInfo findMethodInfo(Class<?> clazz, String methodName, Arg[] args, boolean exactMatch)
	{
		MethodInfo result=null;
		
		//动态代理类会丢失泛型信息，所以如果是动态代理类，则需要在其实现的接口中查找方法，以获取泛型信息
		if(SoybeanMilkUtils.isAncestorClass(Proxy.class, clazz))
		{
			 Class<?>[] interfaces=clazz.getInterfaces();
			 
			 if(interfaces!=null && interfaces.length>0)
			 {
				 for(Class<?> si : interfaces)
				 {
					 result=findMethodInfo(si, methodName, args, exactMatch);
					 
					 if(result != null)
						 break;
				 }
			 }
		}
		else
		{
			Method method=null;
			Class<?> methodClass=clazz;
			
			int al=(args == null ? 0 : args.length);
			Type[] at=new Type[al];
			for(int i=0; i<al; i++)
				at[i]=args[i].getType();
			
			Method[] ms=clazz.getMethods();
			
			for(Method m : ms)
			{
				//忽略小三方法
				if(m.isSynthetic())
					continue;
				
				if(m.getName().equals(methodName) && Modifier.isPublic(m.getModifiers()))
				{
					Class<?>[] types=m.getParameterTypes();
					int mal=(types == null ? 0 : types.length);
					
					if(mal == al)
					{
						boolean match=true;
						
						for(int i=0; i<mal; i++)
						{
							//null认为是匹配
							if(at[i] == null)
								continue;
							
							//精确匹配
							if(exactMatch)
							{
								if(!at[i].equals(types[i]))
								{
									match=false;
									break;
								}
							}
							//父子类型匹配
							else
							{
								Type wat=SoybeanMilkUtils.toWrapperType(at[i]);
								Type wt=SoybeanMilkUtils.toWrapperType(types[i]);
								
								if(SoybeanMilkUtils.isClassType(wat) && SoybeanMilkUtils.isClassType(wt)
										&&!SoybeanMilkUtils.isAncestorClass((Class<?>)wt, (Class<?>)wat))
								{
									match=false;
									break;
								}
							}
						}
						
						if(match)
						{
							method=m;
							break;
						}
					}
				}
			}
			
			if(method != null)
				result=new MethodInfo(method, methodClass);
		}
		
		return result;
	}
	
	/**
	 * 方法信息
	 * @author earthangry@gmail.com
	 * @date 2012-5-6
	 */
	protected static class MethodInfo
	{
		private Method method;
		
		private Type[] argTypes;
		
		private Class<?> methodClass;
		
		public MethodInfo(Method method, Class<?> methodClass)
		{
			this.method=method;
			this.methodClass=methodClass;
			this.argTypes=this.method.getGenericParameterTypes();
		}
		
		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Type[] getArgTypes() {
			return argTypes;
		}

		public void setArgTypes(Type[] argTypes) {
			this.argTypes = argTypes;
		}

		public Class<?> getMethodClass() {
			return methodClass;
		}
		
		public void setMethodClass(Class<?> methodClass) {
			this.methodClass = methodClass;
		}
		
		public Type getArgType(int argIdx)
		{
			return this.argTypes[argIdx];
		}
	}
	
	/**
	 * 调用目标，{@linkplain Invoke 调用}执行调用方法时依赖的目标对象，从{@linkplain ResolverProvider 调用目标提供者}获取
	 * @author earthangry@gmail.com
	 * @date 2012-5-7
	 */
	public static class Resolver
	{
		/**目标对象，当调用方法是静态方法时，它可以为null*/
		private Object resolverObject;
		
		/**目标类型，它不会为null*/
		private Class<?> resolverClass;
		
		public Resolver(){}
		
		public Resolver(Object resolverObject)
		{
			super();
			
			if(resolverObject == null)
				throw new NullPointerException("the resolverObject must not be null");
			
			this.resolverObject = resolverObject;
			this.resolverClass = resolverObject.getClass();
		}
		
		public Resolver(Object resolverObject, Class<?> resolverClass)
		{
			super();
			this.resolverObject = resolverObject;
			this.resolverClass = resolverClass;
		}

		/**
		 * 获取调用目标对象
		 * @return
		 * @date 2012-5-9
		 */
		public Object getResolverObject() {
			return resolverObject;
		}
		
		/**
		 * 设置调用目标对象
		 * @param resolverObject
		 * @date 2012-5-9
		 */
		public void setResolverObject(Object resolverObject) {
			this.resolverObject = resolverObject;
		}
		
		/**
		 * 获取调用目标类型
		 * @return
		 * @date 2012-5-9
		 */
		public Class<?> getResolverClass() {
			return resolverClass;
		}
		
		/**
		 * 设置调用目标类型
		 * @param resolverClass
		 * @date 2012-5-9
		 */
		public void setResolverClass(Class<?> resolverClass) {
			this.resolverClass = resolverClass;
		}
	}
	
	/**
	 * 调用参数
	 * @author earthangry@gmail.com
	 * @date 2010-10-3
	 */
	public static interface Arg
	{
		/**
		 * 获取参数值
		 * @param objectSource
		 * @param argType
		 * @param method
		 * @param methodClass
		 * @return
		 * @throws Exception
		 * @date 2012-5-7
		 */
		Object getValue(ObjectSource objectSource, Type argType, Method method, Class<?> methodClass) throws Exception;
		
		/**
		 * 获取参数类型，它可以让{@linkplain Invoke 调用}更准确地找到调用方法，也可以返回<code>null</code>，由{@linkplain Invoke 调用}自己查找
		 * @return
		 * @date 2012-5-11
		 */
		Type getType();
	}
	
	/**
	 * 调用目标提供者，它为{@linkplain Invoke 调用}执行时提供{@linkplain Resolver 调用目标}对象
	 * @author earthangry@gmail.com
	 * @date 2010-10-19
	 */
	public static interface ResolverProvider
	{
		/**
		 * 获取调用目标
		 * @param objectSource
		 * @return
		 * @throws Exception
		 * @date 2012-5-7
		 */
		Resolver getResolver(ObjectSource objectSource) throws Exception;
	}
}
