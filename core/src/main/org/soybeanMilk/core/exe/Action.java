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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;


/**
 * 动作，它可以包含多个{@linkplain Executable 可执行对象}（{@linkplain Action 动作}或者{@linkplain Invoke 调用}），
 * 并且它们会按照添加时的顺序被执行。如果某个可执行对象仅会被添加到动作中，那么你不需要为其设置唯一名称，因为动作不需要标识各子可执行对象。
 * 
 * @author earthangry@gmail.com
 * @date 2010-5-9
 */
public class Action extends AbstractExecutable
{
	private static final long serialVersionUID = 1L;

	private static Log log=LogFactory.getLog(Action.class);
	
	/**动作包含的可执行对象集*/
	private List<Executable> executables;
	
	public Action(){}
	
	public Action(String name)
	{
		this(name, null);
	}
	
	public Action(String name, List<Executable> executables)
	{
		super();
		super.setName(name);
		this.executables = executables;
	}
	
	//@Override
	public void execute(ObjectSource dataStore) throws ExecuteException
	{
		if(log.isDebugEnabled())
			log.debug("start  execute "+SbmUtils.toString(this));
		
		List<Executable> executables = getExecutables();
		
		if(executables != null)
		{
			for(Executable exe : executables)
				exe.execute(dataStore);
		}
		
		if(log.isDebugEnabled())
			log.debug("finish execute "+SbmUtils.toString(this));
	}

	/**
	 * 获取此动作包含的所有{@linkplain Executable 可执行对象}，它可能包含{@linkplain Action 动作}和{@linkplain Invoke 调用}。
	 * @return
	 * @date 2011-1-13
	 */
	public List<Executable> getExecutables() {
		return executables;
	}
	public void setExecutables(List<Executable> executables) {
		this.executables = executables;
	}
	
	/**
	 * 添加一个可执行对象到此动作中，这个可执行对象并不必须设置它的名称属性，
	 * 因为动作内不需要识别任何可执行对象，它只是顺序地执行它们。
	 * @param exe
	 */
	public void addExecutable(Executable exe)
	{
		List<Executable> exes=getExecutables();
		
		if(exes == null)
		{
			exes = new ArrayList<Executable>();
			setExecutables(exes);
		}
		
		exes.add(exe);
		
		if(log.isDebugEnabled())
			log.debug("add an Executable "+SbmUtils.toString(exe));
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [name=" + getName() + "]";
	}
}