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

/**
 * 执行器，它可以通过名称来执行{@linkplain Executable 可执行对象}
 * @author earthAngry@gmail.com
 * @date 2010-9-30
 *
 */
public interface Executor
{
	/**
	 * 获取执行器使用的{@linkplain Configuration 配置}对象
	 * @return
	 */
	Configuration getConfiguration();
	
	/**
	 * 执行，并返回对应的可执行对象
	 * @param exeName 可执行对象名称
	 * @param objSource 对象源
	 * @throws ExecuteException
	 * @throws ExecutableNotFoundException
	 */
	Executable execute(String exeName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException;
}