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

package org.soybeanMilk.web.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.web.WebConstants;

/**
 * 参数属性映射表，它将参数映射表的关键字分解为属性名
 * @author earthAngry@gmail.com
 * @date 2012-2-19
 *
 * @param <Object>
 */
public class ParamPropertyMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;
	
	/**父参数属性映射表*/
	private ParamPropertyMap parent;
	
	/**此参数属性映射表的属性名*/
	private String propertyName;
	
	/**
	 * 创建默认参数属性映射表
	 */
	public ParamPropertyMap()
	{
		this(null, null);
	}
	
	/**
	 * 创建参数属性映射表，并设置一个初始属性名
	 * @param propertyName 初始属性名，用于追溯完整参数名
	 */
	public ParamPropertyMap(String propertyName)
	{
		this(null, propertyName);
	}
	
	/**
	 * 创建参数属性映射表，设置其父参数属性映射表和初始属性名
	 * @param parent 父参数属性映射表
	 * @param propertyName 初始属性名，用于追溯完整参数名
	 */
	public ParamPropertyMap(ParamPropertyMap parent, String propertyName)
	{
		super();
		this.parent = parent;
		this.propertyName=propertyName;
	}
	
	/**
	 * 是否根参数属性映射表
	 * @return
	 * @date 2012-2-20
	 */
	public boolean isRoot()
	{
		return (this.propertyName == null);
	}
	
	/**
	 * 获取某个属性的完成参数名
	 * @param propertyName
	 * @return
	 * @date 2012-2-22
	 */
	public String getFullParamName(String propertyName)
	{
		String result=null;
		
		if(this.parent != null)
			result=this.parent.getFullParamName(this.propertyName);
		else
			result=this.propertyName;
		
		if(result==null || result.length()==0)
			return propertyName;
		else
			return result+WebConstants.ACCESSOR+propertyName;
	}
	
	/**
	 * 过滤源参数映射表，将所有以此参数属性映射表的初始属性名开头的条目都存入此参数属性映射表
	 * @param paramMap
	 * @date 2012-2-22
	 */
	public void filter(Map<String, ?> paramMap)
	{
		String filter= (this.propertyName==null || this.propertyName.length()==0 ? null : this.propertyName+WebConstants.ACCESSOR);
		
		Set<String> keys=paramMap.keySet();
		boolean doAssemble=false;
		
		for(String key : keys)
		{
			String propExp=key;
			
			if(filter == null)
				doAssemble=true;
			else
			{
				int fl=filter.length();
				
				if(key.length() <= fl)
					doAssemble=false;
				else if(key.startsWith(filter))
				{
					doAssemble=true;
					propExp=key.substring(fl);
				}
				else
					doAssemble=false;
			}
			
			if(doAssemble)
			{
				String[] propKeys=SoybeanMilkUtils.splitAccessExpression(propExp);
				
				ParamPropertyMap parent=this;
				
				for(int i=0; i<propKeys.length; i++)
				{
					if(i == propKeys.length-1)
					{
						parent.put(propKeys[i], paramMap.get(key));
					}
					else
					{
						ParamPropertyMap tmp=(ParamPropertyMap)parent.get(propKeys[i]);
						if(tmp == null)
						{
							tmp=new ParamPropertyMap(parent, propKeys[i]);
							parent.put(propKeys[i], tmp);
						}
						
						parent=tmp;
					}
				}
			}
		}
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public ParamPropertyMap getParent() {
		return parent;
	}

	public void setParent(ParamPropertyMap parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString()
	{
		return "ParamPropertyMap [paramName=" + getFullParamName("")
				+ ", " + super.toString() + "]";
	}
}