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
 * 执行器找不到给定名称的可执行对象时，将抛出该异常
 * @author earthAngry@gmail.com
 * @date 2010-10-28
 */
public class ExecutableNotFoundException extends Exception
{
	private static final long serialVersionUID = -1301466907843994358L;
	
	private String executableName;

	public ExecutableNotFoundException(String executableName)
	{
		super();
		this.executableName = executableName;
	}

	public String getExecutableName() {
		return executableName;
	}

	public void setExecutableName(String executableName) {
		this.executableName = executableName;
	}
}
