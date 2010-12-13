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

package org.soybeanMilk.core;

import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.InterceptorInfo;
import org.soybeanMilk.core.os.ConvertableObjectSource;

/**
 * 执行器的一个简单实现，它仅是从{@linkplain Configuration 配置}中查找对应的{@linkplain Executable 可执行对象}，
 * 然后调用{@link Executor#execute(String, ObjectSource)}方法
 * @author earthAngry@gmail.com
 * @date 2010-9-30
 */
public class DefaultExecutor implements Executor
{
	private Configuration configuration;
	
	public DefaultExecutor(Configuration configuration)
	{
		super();
		this.configuration = configuration;
	}
	
	@Override
	public Configuration getConfiguration()
	{
		return this.configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Executable execute(String exeName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException
	{
		return execute(findExecutable(exeName), objSource);
	}
	
	/**
	 * 执行，并返回最终执行的那个可执行对象。如果正常执行，则返回参数给定的；如果出现异常，则返回作为异常处理器的那个。
	 * @param exe
	 * @param objSource
	 * @return
	 * @throws ExecuteException
	 */
	protected Executable execute(Executable exe, ObjectSource objSource) throws ExecuteException
	{
		if(objSource instanceof ConvertableObjectSource)
			((ConvertableObjectSource)objSource).setGenericConverter(getConfiguration().getGenericConverter());
		
		InterceptorInfo itptInfo = configuration.getInterceptorInfo();
		
		//保存执行语境信息
		Execution context=null;
		if(itptInfo!=null && itptInfo.getExecutionKey()!=null)
		{
			context=new Execution(exe, objSource);
			objSource.set(itptInfo.getExecutionKey(), context);
		}
		
		//before
		if(itptInfo!=null && itptInfo.getBeforeHandler()!=null)
			executeInterceptor(itptInfo.getBeforeHandler(), objSource);
		
		try
		{
			exe.execute(objSource);
			
			//after
			if(itptInfo!=null && itptInfo.getAfterHandler()!=null)
				executeInterceptor(itptInfo.getAfterHandler(), objSource);
			
			return exe;
		}
		catch(ExecuteException e)
		{
			if(context != null)
				context.setExecuteException(e);
			
			Executable expExe= itptInfo == null ? null : itptInfo.getExceptionHandler();
			if(expExe == null)
				throw e;
			
			//exception
			executeInterceptor(expExe, objSource);
			
			return expExe;
		}
	}
	
	/**
	 * 根据名称查找可执行对象
	 * @param name
	 * @return
	 * @throws ExecutableNotFoundException
	 */
	protected Executable findExecutable(String name) throws ExecutableNotFoundException
	{
		Executable exe = getConfiguration().getExecutable(name);
		if(exe == null)
			throw new ExecutableNotFoundException(name);
		
		return exe;
	}
	
	/**
	 * 执行拦截器，它不会抛出执行异常
	 * @param exeInterceptor
	 * @param objSource
	 */
	protected void executeInterceptor(Executable exeInterceptor, ObjectSource objSource)
	{
		try
		{
			exeInterceptor.execute(objSource);
		}
		catch(ExecuteException e)
		{
			throw new InterceptorException(e);
		}
	}
	
	/**
	 * 拦截器异常
	 * @author earthAngry@gmail.com
	 * @date 2010-11-30
	 *
	 */
	public static class InterceptorException extends RuntimeException
	{
		private static final long serialVersionUID = -1443863310934331790L;

		public InterceptorException() {
			super();
		}

		public InterceptorException(String message, Throwable cause) {
			super(message, cause);
		}

		public InterceptorException(String message) {
			super(message);
		}

		public InterceptorException(Throwable cause){
			super(cause);
		}
	}
}