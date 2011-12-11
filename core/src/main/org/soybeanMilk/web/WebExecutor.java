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

package org.soybeanMilk.web;

import java.io.IOException;

import javax.servlet.ServletException;

import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;

/**
 * Web执行器。
 * @author earthAngry@gmail.com
 * @date 2011-4-18
 *
 */
public interface WebExecutor extends Executor
{
	/**
	 * 获取{@linkplain Web配置}信息。
	 * @return
	 * @date 2011-4-19
	 */
	WebConfiguration getWebConfiguration();
	
	/**
	 * 执行，如果要执行的{@linkplain Executable 可执行对象}是{@linkplain WebAction Web动作}，此方法还要处理其{@linkplain Target 目标}。
	 * @param executableName {@linkplain Executable 可执行对象}名，通常是请求URL。
	 * @param webObjSource {@linkplain WebObjectSource Web对象源}
	 * @throws ExecuteException
	 * @throws ExecutableNotFoundException
	 * @throws ServletException
	 * @throws IOException
	 * @date 2011-4-18
	 */
	void execute(String executableName, WebObjectSource webObjSource)
			throws ExecuteException, ExecutableNotFoundException, ServletException, IOException;
}