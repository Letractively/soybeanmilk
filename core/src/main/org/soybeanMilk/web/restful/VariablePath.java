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

import java.io.Serializable;

/**
 * 变量路径封装类。<br>
 * 如果一个路径字符串的某个节点是“{...}”格式，则认为它是变量路径。
 * 比如“{id}/edit”就是一个变量路径。
 * @author earthAngry@gmail.com
 * @date 2010-12-16
 *
 */
public class VariablePath implements Comparable<VariablePath>,Serializable
{
	private static final long serialVersionUID = 4221252496072385107L;
	
	private String variablePath;
	
	/**
	 * 路径节点数组，以'/'拆分的
	 */
	private PathNode[] pathNodes;
	
	public VariablePath(String variablePath)
	{
		this.variablePath=variablePath;
		
		if(variablePath==null || variablePath.length()==0)
			return;
		
		this.pathNodes=split(this.variablePath);
	}
	
	public String getVariablePath() {
		return variablePath;
	}
	public void setVariablePath(String variablePath) {
		this.variablePath = variablePath;
	}
	public PathNode[] getPathNodes() {
		return pathNodes;
	}
	public void setPathNodes(PathNode[] pathNodes) {
		this.pathNodes = pathNodes;
	}

	/**
	 * 是否是变量路径
	 * @return
	 */
	public boolean isVariablePath()
	{
		return this.pathNodes != null;
	}
	
	/**
	 * 比较变量路径。<br>
	 * 比较规则如下：
	 * <ol>
	 * 	<li>比较变量路径节点长度</li>
	 * 	<li>顺序比较每个相同位置的路径节点（变量节点小于非变量节点，变量节点等于变量节点），如果此位置相等，则继续下一位置路径节点的比较</li>
	 * </ol>
	 */
	@Override
	public int compareTo(VariablePath o)
	{
		PathNode[] targetNodes=o.getPathNodes();
		
		if(this.pathNodes==null && targetNodes==null)
			return 0;
		else if(this.pathNodes == null)
			return -1;
		else if(targetNodes == null)
			return 1;
		else
		{
			if(this.pathNodes.length < targetNodes.length)
				return -1;
			else if(this.pathNodes.length > targetNodes.length)
				return 1;
			else
			{
				int re=0;
				
				for(int i=0;i<targetNodes.length;i++)
				{
					PathNode te=targetNodes[i];
					PathNode le=this.pathNodes[i];
					
					re=le.compareTo(te);
					
					if(re == 0)
						continue;
				}
				
				return re;
			}
		}
	}
	
	@Override
	public String toString()
	{
		return this.variablePath;
	}
	
	/**
	 * 拆分变量路径，如果它不是一个变量路径，此方法将返回null
	 * @param variabalePath 变量路径，格式为“{variable_0}/bbb/{variable_1}/ddd”，其中"{}"表示该节点是变量
	 * @return 拆分结果数组
	 */
	private PathNode[] split(String variabalePath)
	{
		PathNode re[]=null;
		
		String[] names=null;
		if(variabalePath!=null && variabalePath.length()>0)
			names=variabalePath.split("/");
		
		boolean variable=false;
		
		if(names!=null && names.length>0)
		{
			re=new PathNode[names.length];
			
			for(int i=0;i<names.length;i++)
			{
				re[i]=new PathNode(names[i]);
				
				if(!variable && re[i].isVariable())
					variable=true;
			}
		}
		
		if(!variable)
			re=null;
		
		return re;
	}
}