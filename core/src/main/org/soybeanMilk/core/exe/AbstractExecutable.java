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

import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;

/**
 * 可执行对象的顶层抽象类，它实现名称定义。
 * @author earthAngry@gmail.com
 * @date 2010-11-21
 */
public abstract class AbstractExecutable implements Executable
{
	private static final long serialVersionUID = 8600625732310639588L;
	
	/**可执行对象名称*/
	private String name;
	
	@Override
	public abstract void execute(ObjectSource objectSource) throws ExecuteException;
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name=name;
	}
}
