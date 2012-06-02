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

package org.soybeanMilk.example.resolver;

import org.soybeanMilk.core.Execution;

/**
 * SoybeanMilk示例
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class InterceptorResolver
{
	public void invokeNormal()
	{
		System.out.println();
		System.out.println("invoke normal");
		System.out.println();
	}
	
	public void invokeThrow()
	{
		throw new NullPointerException("invoke throw");
	}
	
	public void before(Execution execution)
	{
		System.out.println();
		System.out.println("before execute: "+execution.getExecutable().getName());
		System.out.println();
	}
	
	public void after(Execution execution)
	{
		System.out.println();
		System.out.println("after  execute: "+execution.getExecutable().getName());
		System.out.println();
	}
	
	public void exception(Execution execution)
	{
		System.out.println();
		System.out.println("exception handler: "+execution.getExecuteException());
		System.out.println();
	}
}
