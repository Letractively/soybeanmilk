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
	public void execute(String exeName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException
	{
		Configuration cfg=getConfiguration();
		
		Executable exe = cfg.getExecutable(exeName);
		if(exe == null)
			throw new ExecutableNotFoundException(exeName);
		
		if(objSource instanceof ConvertableObjectSource)
			((ConvertableObjectSource)objSource).setGenericConverter(cfg.getGenericConverter());
		
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
			itptInfo.getBeforeHandler().execute(objSource);
		
		try
		{
			exe.execute(objSource);
			
			//after
			if(itptInfo!=null && itptInfo.getAfterHandler()!=null)
				itptInfo.getAfterHandler().execute(objSource);
		}
		catch(ExecuteException e)
		{
			if(itptInfo==null || itptInfo.getExceptionHandler()==null)
				throw e;
			
			if(context != null)
				context.setExecuteException(e);
			
			//exception
			itptInfo.getExceptionHandler().execute(objSource);
		}
	}
}