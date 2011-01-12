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
	public Executable execute(String executableName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException
	{
		Executable exe=findExecutable(executableName, objSource);
		if(exe == null)
			throw new ExecutableNotFoundException(executableName);
		
		return execute(exe, objSource);
	}
	
	@Override
	public Executable execute(Executable executable, ObjectSource objSource)
			throws ExecuteException
	{
		if(objSource instanceof ConvertableObjectSource)
		{
			ConvertableObjectSource cvtObjSource=(ConvertableObjectSource)objSource;
			
			if(cvtObjSource.getGenericConverter() == null)
				cvtObjSource.setGenericConverter(getConfiguration().getGenericConverter());
		}
		
		InterceptorInfo itptInfo = getConfiguration().getInterceptorInfo();
		
		//保存执行语境信息
		Execution context=null;
		if(itptInfo!=null && itptInfo.getExecutionKey()!=null)
		{
			context=new Execution(executable, objSource);
			objSource.set(itptInfo.getExecutionKey(), context);
		}
		
		try
		{
			//before
			if(itptInfo!=null && itptInfo.getBeforeHandler()!=null)
				executeInterceptor(itptInfo.getBeforeHandler(), objSource);
			
			executeTargetExecutable(executable, objSource);
			
			//after
			if(itptInfo!=null && itptInfo.getAfterHandler()!=null)
				executeInterceptor(itptInfo.getAfterHandler(), objSource);
			
			return executable;
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
	 * @param executableName
	 * @param objSource
	 * @return
	 * @throws ExecuteException
	 * @date 2011-1-7
	 */
	protected Executable findExecutable(String executableName, ObjectSource objSource)
			throws ExecuteException
	{
		return getConfiguration().getExecutable(executableName);
	}
	
	/**
	 * 执行目标可执行对象。
	 * @param executable
	 * @param objSource
	 * @throws ExecuteException
	 * @date 2011-1-11
	 */
	protected void executeTargetExecutable(Executable executable, ObjectSource objSource) throws ExecuteException
	{
		executable.execute(objSource);
	}
	
	/**
	 * 执行拦截器
	 * @param exeInterceptor
	 * @param objSource
	 */
	protected void executeInterceptor(Executable exeInterceptor, ObjectSource objSource) throws ExecuteException
	{
		exeInterceptor.execute(objSource);
	}
}