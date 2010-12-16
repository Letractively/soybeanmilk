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

package org.soybeanMilk.web.restful;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author earthAngry@gmail.com
 * @date 2010-12-16
 *
 */
public class VariablePathManager
{
	protected SortedSet<VariablePath> variableNameExecutables;
	
	public VariablePathManager()
	{
		this.variableNameExecutables=new TreeSet<VariablePath>();
	}

	public SortedSet<VariablePath> getVariableNameExecutables(){
		return variableNameExecutables;
	}
	public void setVariableNameExecutables(SortedSet<VariablePath> variableNameExecutables){
		this.variableNameExecutables = variableNameExecutables;
	}
	
	/**
	 * 添加一个变量路径对象
	 * @param variablePath
	 */
	public void addVariablePath(VariablePath variablePath)
	{
		if(!this.variableNameExecutables.add(variablePath))
			throw new IllegalArgumentException("duplicate variable path '"+variablePath.getVariablePath()+"'");
	}
	
	/**
	 * 取得与值路径匹配的变量路径
	 * @param valuePath 值路径，比如“a/b/c/d”
	 * @return
	 */
	public VariablePath getMatched(String valuePath)
	{
		//TODO 实现匹配查找
		return null;
	}
}
