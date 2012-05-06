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

package refactor.org.soybeanMilk.core.exe;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
 * 调用
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
	private Arg[] args;
	
	/**调用结果存放到对象源中的关键字*/
	private Serializable resultKey;
	
	/**调用解决对象*/
	private ResolverProvider resolverProvider;
	
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
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Arg[] getArgs() {
		return args;
	}

	public void setArgs(Arg[] args) {
		this.args = args;
	}

	public Serializable getResultKey() {
		return resultKey;
	}

	public void setResultKey(Serializable resultKey) {
		this.resultKey = resultKey;
	}

	public ResolverProvider getResolverProvider() {
		return resolverProvider;
	}

	public void setResolverProvider(ResolverProvider resolverProvider) {
		this.resolverProvider = resolverProvider;
	}

	public Serializable getBreaker() {
		return breaker;
	}

	public void setBreaker(Serializable breaker) {
		this.breaker = breaker;
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}

	public void setMethodInfo(MethodInfo methodInfo) {
		this.methodInfo = methodInfo;
	}

	@Override
	public void execute(ObjectSource objectSource) throws ExecuteException
	{
		if(log.isDebugEnabled())
			log.debug("start  execute '"+this+"'");
		
		boolean breaked=isBreaked(objectSource);
		
		if(!breaked)
			executeMethod(objectSource);
		else
		{
			if(log.isDebugEnabled())
				log.debug("Invoke method not executed, it is breaked in current object source");
		}
		
		if(log.isDebugEnabled())
			log.debug("finish execute '"+this+"'");
	}
	
	/**
	 * 执行调用方法。
	 * @param objectSource
	 * @throws ExecuteException
	 * @date 2011-1-12
	 */
	protected void executeMethod(ObjectSource objectSource) throws ExecuteException
	{
		Object resolver=getResolver(objectSource);
		Serializable resultKey=getResultKey();
		MethodInfo methodInfo=getMethodInfo(resolver);
		
		Object methodResult=null;
		
		Object[] argValues=prepareMethodArgValues(methodInfo, objectSource);
		
		try
		{
			methodResult=methodInfo.getMethod().invoke(resolver, argValues);
		}
		catch(InvocationTargetException e)
		{
			//TODO 把null替换为this
			throw new InvocationExecuteException(null, e.getCause());
		}
		catch(IllegalArgumentException e)
		{
			throw new ExecuteException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new ExecuteException(e);
		}
		
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
	protected Object getResolver(ObjectSource objectSource) throws ExecuteException
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
		
		Arg[] args = getArgs();
		if(args!=null && args.length>0)
		{
			values=new Object[args.length];
			
			for(int i=0;i<args.length;i++)
				values[i]=args[i].getValue(objectSource, methodInfo, i);
		}
		
		if(log.isDebugEnabled())
			log.debug("got method arguments: "+SoybeanMilkUtils.toString(values));
		
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
		Boolean breaked=null;
		
		if(this.breaker != null)
		{
			if(this.breaker instanceof String)
			{
				String brkString=(String)this.breaker;
				
				if(Boolean.toString(true).equalsIgnoreCase(brkString))
					breaked=true;
				else if(Boolean.toString(false).equalsIgnoreCase(brkString))
					breaked=false;
			}
			
			if(breaked == null)
			{
				Object brkObj=null;
				
				try
				{
					brkObj=objectSource.get(this.breaker, null);
				}
				catch(Exception e)
				{
					throw new ExecuteException(e);
				}
				
				if(brkObj!=null && Boolean.TRUE.equals(brkObj))
					breaked=true;
			}
		}
		
		return breaked == null ? false : breaked;
	}
	
	protected MethodInfo getMethodInfo(Object declareObject)
	{
		if(this.methodInfo == null)
			this.methodInfo=new MethodInfo(declareObject.getClass(),
					this.methodName, this.args==null ? 0 : this.args.length);
		
		return methodInfo;
	}
	
	/**
	 * 方法信息
	 * @author earthangry@gmail.com
	 * @date 2012-5-6
	 */
	public static class MethodInfo
	{
		private Method method;
		
		private Type[] argTypes;
		
		private Class<?> methodClass;
		
		public MethodInfo(Class<?> methodClass, String methodName, int argNums)
		{
			this.methodClass=methodClass;
			
			this.method=SoybeanMilkUtils.findMethodThrow(this.methodClass, methodName, argNums);
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
	 * 调用方法参数
	 * @author earthangry@gmail.com
	 * @date 2010-10-3
	 */
	public static interface Arg
	{
		Object getValue(ObjectSource objectSource, MethodInfo methodInfo, int argIdx) throws ArgPrepareExecuteException;
	}
	
	/**
	 * 解决对象（任意Java对象）提供者，调用所需的解决对象将由它提供
	 * @author earthangry@gmail.com
	 * @date 2010-10-19
	 */
	public static interface ResolverProvider
	{
		Object getResolver(ObjectSource objectSource) throws Exception;
	}
}
