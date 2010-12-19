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

package org.soybeanMilk.web.vp;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 变量路径匹配器
 * @author earthAngry@gmail.com
 * @date 2010-12-16
 *
 */
public class VariablePathMatcher
{
	private VariablePath[] variablePaths;
	
	public VariablePathMatcher(String[] variablePaths)
	{
		if(variablePaths!=null && variablePaths.length>0)
		{
			SortedSet<VariablePath> vpSet=new TreeSet<VariablePath>();
			for(String vpStr : variablePaths)
			{
				VariablePath vp=new VariablePath(vpStr);
				if(vp.isVariablePath())
					vpSet.add(vp);
			}
			
			VariablePath[] vpAry=(VariablePath[])vpSet.toArray();
			setVariablePaths(vpAry);
		}
	}
	
	public VariablePath[] getVariablePaths() {
		return variablePaths;
	}
	
	/**
	 * 取得与值路径匹配的变量路径
	 * @param valuePath 值路径，比如“a/b/c/d”
	 * @return
	 */
	public VariablePath getMatched(String valuePath)
	{
		return getMatched(VariablePath.splitPath(valuePath));
	}
	
	/**
	 * 取得与值路径节点数组匹配的变量路径
	 * @param valuePathNodes 值路径数组
	 * @return
	 */
	public VariablePath getMatched(String[] valuePathNodes)
	{
		if(this.variablePaths==null || this.variablePaths.length==0
				|| valuePathNodes==null || valuePathNodes.length==0)
			return null;
		
		int[] range=new int[]{0,this.variablePaths.length-1};
		for(int i=0;i<valuePathNodes.length;i++)
			range=getMatchRowRange(valuePathNodes[i], i, range);
		
		int idx=-1;
		
		if(range!=null && range.length!=0
				&& this.variablePaths[range[0]].getPathNodes().length==valuePathNodes.length)
			idx=range[0];
		
		return idx == -1 ? null : this.variablePaths[idx];
	}
	
	protected int[] getMatchRowRange(String v, int column, int[] rowRangeIn)
	{
		//TODO 实现匹配查找
		
		return null;
	}
	
	protected void setVariablePaths(VariablePath[] variablePaths)
	{
		this.variablePaths = variablePaths;
	}
}
