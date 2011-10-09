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
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.ConvertExecuteException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.InvocationExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.GenericType;

/**
 * 调用，它包含执行方法（{@linkplain Method}对象）、方法的{@linkplain Arg 参数信息}、{@linkplain ResolverProvider 解决对象提供者}
 * @author earthAngry@gmail.com
 * @date 2010-9-30
 * 
 * @see Arg
 * @see ResolverProvider
 */
public class Invoke extends AbstractExecutable
{
	private static final long serialVersionUID = -6517860148774345653L;
	
	private static Log log=LogFactory.getLog(Invoke.class);
	
	/**解决对象提供者，方法被调用时的对象参数将由它提供*/
	private transient ResolverProvider resolverProvider;
	
	/**调用方法，如果为静态方法，则调用源可以为null*/
	private transient Method method;
	
	/**此调用方法所属的类*/
	private Class<?> resolverClass;
	
	/**调用方法的参数*/
	private Arg[] args;
	
	/**调用结果存放到对象源中的关键字*/
	private Serializable resultKey;
	
	/**
	 * 创建空的调用对象
	 */
	public Invoke(){}
	
	/**
	 * 创建调用对象，它将自动查找方法对象
	 * @param name
	 * @param methodName
	 * @param args
	 * @param resultKey
	 * @param resolverProvider
	 * @see #Invoke(String, Method, Arg[], Serializable, ResolverProvider)
	 */
	public Invoke(String name, String methodName, Arg[] args, Serializable resultKey,
			ResolverProvider resolverProvider)
	{
		Object resObj=resolverProvider.getResolver();
		if(resObj == null)
			throw new IllegalArgumentException("resolver object must not be null");
		
		int argNums= args==null ? 0 : args.length;
		
		init(name, findMethodThrow(resolverProvider.getResolver().getClass(), methodName, argNums), args, resultKey, resolverProvider);
	}
	
	/**
	 * 创建调用对象，它将自动查找方法对象
	 * @param name
	 * @param methodName 方法名称
	 * @param args
	 * @param resultKey
	 * @param resolverClass 解决对象类
	 * @see #Invoke(String, Method, Arg[], Serializable, ResolverProvider)
	 */
	public Invoke(String name, String methodName, Arg[] args, Serializable resultKey, Class<?> resolverClass)
	{
		int argNums= args==null ? 0 : args.length;
		
		init(name, findMethodThrow(resolverClass, methodName, argNums), args, resultKey, null);
	}
	
	/**
	 * 创建调用对象，它的方法是静态方法，因此不需要解决对象
	 * @see #Invoke(String, Method, Arg[], Serializable, ResolverProvider)
	 */
	public Invoke(String name, Method method, Arg[] args, Serializable resultKey)
	{
		init(name, method, args, resultKey, null);
	}
	
	/**
	 * 创建调用对象
	 * @param name 调用名称
	 * @param method 调用将执行的方法
	 * @param args 参数信息，如果执行方法没有参数，可以将它设置为<code>null</code>，你只需要设置参数信息中的<code>key</code>或者<code>value</code>属性，这个构造方法会自动设置参数类型属性
	 * @param resultKey 方法执行结果的存储关键字，如果设置为<code>null</code>，无论执行方法是否有返回结果都不会被存储；相反，则都会存储（无返回结果的按<code>null</code>存储）
	 * @param resolverProvider 解决对象提供者，如果方法是静态的，可以将它设置为<code>null</code>
	 */
	public Invoke(String name, Method method, Arg[] args, Serializable resultKey, ResolverProvider resolverProvider)
	{
		init(name, method, args, resultKey, resolverProvider);
	}
	
	/**
	 * 初始化
	 * @param name
	 * @param method
	 * @param args
	 * @param resultKey
	 * @param resolverProvider
	 */
	private void init(String name, Method method, Arg[] args, Serializable resultKey, ResolverProvider resolverProvider)
	{
		super.setName(name);
		setMethod(method);
		setResolverProvider(resolverProvider);
		setResolverClass((this.resolverProvider==null ? method.getDeclaringClass() : this.resolverProvider.getResolver().getClass()));
		setResultKey(resultKey);
		setArgs(args);
	}
	
	public ResolverProvider getResolverProvider() {
		return resolverProvider;
	}
	public void setResolverProvider(ResolverProvider resolverProvider) {
		this.resolverProvider = resolverProvider;
	}

	/**
	 * 获取此调用的{@linkplain Method 方法}。
	 * @return
	 * @date 2011-1-13
	 */
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	
	/**
	 * 获取此调用方法{@linkplain Arg 参数}数组。
	 * @return
	 * @date 2011-1-13
	 */
	public Arg[] getArgs() {
		return args;
	}
	
	/**
	 * 设置调用方法{@linkplain Arg 参数}数组，它会自动查找并设置参数类型。
	 * @param args
	 * @date 2011-10-1
	 */
	public void setArgs(Arg[] args)
	{
		this.args = args;
		
		Type[] mtdArgs=method.getGenericParameterTypes();
		if(mtdArgs!=null && mtdArgs.length!=0)
		{
			if(args==null || args.length!=mtdArgs.length)
				throw new IllegalArgumentException("[args] length is not match with the [method] arguments length");
			
			for(int i=0;i<mtdArgs.length;i++)
				args[i].setType(mtdArgs[i]);
		}
	}

	/**
	 * 获取此调用的方法结果保存关键字。
	 * @return
	 * @date 2011-1-13
	 */
	public Serializable getResultKey() {
		return resultKey;
	}
	public void setResultKey(Serializable resultKey) {
		this.resultKey = resultKey;
	}
	
	/**
	 * 获取此调用方法所属的类
	 * @return
	 * @date 2011-9-29
	 */
	public Class<?> getResolverClass() {
		return resolverClass;
	}

	public void setResolverClass(Class<?> resolverClass) {
		this.resolverClass = resolverClass;
	}

	/**
	 * 获取调用方法某个位置的{@linkplain Arg 参数}。
	 * @param index
	 * @return
	 * @date 2011-1-13
	 */
	public Arg getArg(int index)
	{
		return this.args[index];
	}
	
	//@Override
	public void execute(ObjectSource objectSource) throws ExecuteException
	{
		if(log.isDebugEnabled())
			log.debug("start  execute '"+this+"'");
		
		executeMethod(objectSource);
		
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
		Serializable resultKey=getResultKey();
		
		try
		{
			Object methodResult=getMethod().invoke(getResolver(objectSource), makeMethodArguments(objectSource));
			
			if(resultKey != null)
				objectSource.set(resultKey, methodResult);
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
	 * 从对象源中取得方法的参数值数组
	 * @param objectSource
	 * @return
	 * @throws ExecuteException
	 * @date 2011-1-11
	 */
	protected Object[] makeMethodArguments(ObjectSource objectSource) throws ExecuteException
	{
		Object[] values=null;
		
		Arg[] args = getArgs();
		if(args!=null && args.length>0)
		{
			values=new Object[args.length];
			
			for(int i=0;i<args.length;i++)
			{
				//优先取值
				if(args[i].getValue()!=null || args[i].getKey()==null)
					values[i]=args[i].getValue();
				else
					values[i]=getArgValueFromObjectSource(args, i, objectSource);
			}
		}
		
		if(log.isDebugEnabled())
			log.debug("construct method arguments '"+Arrays.toString(values)+"'");
		
		return values;
	}
	
	/**
	 * 从对象源中取得第<code>argIdx</code>个参数的值。
	 * @param args
	 * @param argIdx
	 * @param objectSource
	 * @return
	 * @throws ExecuteException
	 * @date 2011-4-11
	 */
	protected Object getArgValueFromObjectSource(Arg[] args, int argIdx, ObjectSource objectSource) throws ExecuteException
	{
		Object re=null;
		
		//如果参数类型不是Class类，则需要转换为GenericType类，以使转换器能正确识别类型
		Type argType=args[argIdx].getType();
		if(!SoybeanMilkUtils.isClassType(argType))
			argType=GenericType.getGenericType(argType, this.resolverClass);
		
		try
		{
			re=objectSource.get(args[argIdx].getKey(), argType);
		}
		catch(ConvertException e)
		{
			throw new ConvertExecuteException(this, argIdx, e);
		}
		
		return re;
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [name=" + getName() + ", method=" + method
				+ ", resultKey=" + resultKey + ", resolverProvider="
				+ resolverProvider + ", args=" + Arrays.toString(args) + "]";
	}
	
	/**
	 * 取得本调用所依赖的解决对象
	 * @param objectSource 本次调用的对象源，默认实现是从调用的属性取得解决对象的，所以在这个实现中没有用到
	 * @return
	 */
	protected Object getResolver(ObjectSource objectSource)
	{
		return resolverProvider==null ? null : resolverProvider.getResolver();
	}
	
	/**
	 * 根据方法名称及方法参数数目查找类的公开方法，找不到则会抛出异常
	 * @param clazz 查找目标类
	 * @param methodName 方法名
	 * @param argNums 参数数目
	 * @return
	 */
	public static Method findMethodThrow(Class<?> clazz,String methodName,int argNums)
	{
		Method result=null;
		
		Method[] ms=clazz.getMethods();
		for(Method m : ms)
		{
			if(m.getName().equals(methodName)
					&& Modifier.isPublic(m.getModifiers()))
			{
				Class<?>[] types=m.getParameterTypes();
				int mParamNums= types == null ? 0 : types.length;
				
				if(mParamNums == argNums)
				{
					result=m;
					break;
				}
			}
		}
		
		if(result == null)
			throw new NullPointerException("can not find Method named '"+methodName+"' with "+argNums+" arguments in Class '"+clazz.getName()+"'");
		
		return result;
	}
	
	/**
	 * 方法的参数信息。它提供从对象源中取得方法参数值所需的信息，或者可以直接提供参数值（如果参数值被设置，那么它将优先被使用）。
	 * @author earthAngry@gmail.com
	 * @date 2010-10-3
	 */
	public static class Arg implements Serializable
	{
		private static final long serialVersionUID = -1460025906014956461L;
		
		/**从对象源中取得参数值的关键字*/
		private Serializable key;
		/**参数类型*/
		private transient Type type;
		/**参数值*/
		private transient Object value;
		
		public Arg(){}
		
		/**
		 * 创建参数信息对象
		 * @see Arg#Arg(Serializable, Class)
		 */
		public Arg(Serializable key)
		{
			super();
			this.key = key;
		}
		
		/**
		 * 创建参数信息对象
		 * @param value 参数的固定值
		 */
		public Arg(Object value)
		{
			super();
			this.value = value;
		}
		
		/**
		 * 创建参数信息对象
		 * @param key 从{@linkplain ObjectSource 对象源}中取得参数值的关键字
		 * @param type 参数值类型
		 */
		public Arg(Serializable key, Type type)
		{
			super();
			this.key = key;
			this.type = type;
		}
		
		/**
		 * 获取此参数的值在{@linkplain ObjectSource 对象源}中的关键字
		 * @return
		 * @date 2010-10-3
		 */
		public Serializable getKey() {
			return key;
		}
		public void setKey(Serializable key) {
			this.key = key;
		}
		
		/**
		 * 获取此参数的类型
		 * @return
		 * @date 2010-10-3
		 */
		public Type getType() {
			return type;
		}
		public void setType(Type type) {
			this.type = type;
		}
		
		/**
		 * 获取此参数的预设值，没有则返回<code>null</code>
		 * @return
		 * @date 2010-10-3
		 */
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}

		//@Override
		public String toString()
		{
			return getClass().getSimpleName()+" [key=" + key + ", type=" + type + ", value=" + value + (value==null ? "" : "("+value.getClass().getName()+")")
					+ "]";
		}
	}
	
	/**
	 * 解决对象（任意Java对象）的提供者，调用所需的解决对象将由它提供
	 * @author earthAngry@gmail.com
	 * @date 2010-10-19
	 *
	 */
	public static interface ResolverProvider
	{
		/**
		 * 取得解决对象
		 * @return
		 */
		Object getResolver();
	}
}