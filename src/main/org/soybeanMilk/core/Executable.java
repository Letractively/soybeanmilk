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

/**
 * 可执行对象
 * @author earthAngry@gmail.com
 * @date 2010-9-30
 */
public interface Executable
{
	/**
	 * 取得名称，该名称应该可以唯一标识这个可执行对象
	 * @return
	 */
	String getName();
	
	/**
	 * 执行
	 * @param objectSource {@linkplain ObjectSource 对象源}，执行依赖的所有参数值都从它获取、结果也都保存到它里面
	 * @throws ExecuteException
	 */
	void execute(ObjectSource objectSource) throws ExecuteException;
}