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
 * 路径的节点，该节点可能是变量节点，也可能是值节点。
 * @author earthAngry@gmail.com
 * @date 2010-12-16
 *
 */
public class PathNode implements Comparable<PathNode>,Serializable
{
	private static final long serialVersionUID = 2874050175900495860L;
	
	private String nodeValue;
	private boolean isVariable;
	
	public PathNode(String nodeValue)
	{
		if(isVariableString(nodeValue))
		{
			this.isVariable=true;
			this.nodeValue=nodeValue.substring(1, nodeValue.length()-1);
		}
		else
			this.nodeValue=nodeValue;
	}
	
	public String getNodeValue() {
		return nodeValue;
	}
	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}
	public boolean isVariable() {
		return isVariable;
	}
	public void setVariable(boolean isVariable) {
		this.isVariable = isVariable;
	}

	@Override
	public int compareTo(PathNode o)
	{
		if(o == null)
			return 1;
		
		int re=0;
		
		if(this.isVariable() && o.isVariable())
			re=0;
		else if(this.isVariable())
			re=-1;
		else if(o.isVariable())
			re=1;
		else
			re=this.getNodeValue().compareTo(o.getNodeValue());
		
		return re;
	}
	
	private boolean isVariableString(String s)
	{
		return s.length()>2 && s.charAt(0)=='{' && s.charAt(s.length()-1)=='}';
	}
}
