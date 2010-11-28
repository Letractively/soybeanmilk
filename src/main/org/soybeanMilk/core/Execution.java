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

import java.io.Serializable;

import org.soybeanMilk.core.config.InterceptorInfo;

/**
 * 执行语境信息。<br>
 * 如果你为框架添加了执行拦截器（参考{@linkplain InterceptorInfo 执行拦截器信息}类），
 * 你或许需要这些执行语境信息。
 * @author earthAngry@gmail.com
 * @date 2010-11-28
 */
public class Execution implements Serializable
{
	private static final long serialVersionUID = -1593312435424212387L;
	
	private transient Executable executable;
	private transient ObjectSource objectSource;
	private transient ExecuteException executeException;
	
	public Execution()
	{
		super();
	}
	
	public Execution(Executable executable, ObjectSource objectSource)
	{
		this(executable, objectSource, null);
	}

	public Execution(Executable executable, ObjectSource objectSource,
			ExecuteException executeException) {
		super();
		this.executable = executable;
		this.objectSource = objectSource;
		this.executeException = executeException;
	}

	public Executable getExecutable() {
		return executable;
	}
	public void setExecutable(Executable executable) {
		this.executable = executable;
	}
	public ObjectSource getObjectSource() {
		return objectSource;
	}
	public void setObjectSource(ObjectSource objectSource) {
		this.objectSource = objectSource;
	}
	public ExecuteException getExecuteException() {
		return executeException;
	}
	public void setExecuteException(ExecuteException executeException) {
		this.executeException = executeException;
	}

	@Override
	public String toString()
	{
		return "Execution [executable=" + executable + ", objectSource="
				+ objectSource + ", executeException=" + executeException + "]";
	}
}
