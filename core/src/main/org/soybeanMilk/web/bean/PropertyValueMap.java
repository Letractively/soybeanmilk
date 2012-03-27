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
 * 属性值映射表，它的关键字表示某对象的某个属性名，而关键字对应的值则是这个对象该属性的值。
 * 另外，它本身的也具有一个<i>属性名</i>属性，表明它是某对象的某个属性所对应的属性值映射表
 * 
 * @author earthAngry@gmail.com
 * @date 2012-3-27
 *
 */
public class PropertyValueMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;
	
	/**父属性值映射表*/
	private PropertyValueMap parent;
	
	/**此属性值映射表的属性名*/
	private String propertyName;
	
	/**
	 * 创建默认属性值映射表
	 */
	public PropertyValueMap()
	{
		this(null, null);
	}
	
	/**
	 * 创建属性值映射表，并设置它的属性名
	 * @param propertyName 属性名
	 */
	public PropertyValueMap(String propertyName)
	{
		this(propertyName, null);
	}
	
	/**
	 * 创建属性值映射表，设置其父属性值映射表和它的属性名
	 * @param propertyName 初始属性名，用于追溯完整参数名
	 * @param parent 父属性值映射表
	 */
	public PropertyValueMap(String propertyName, PropertyValueMap parent)
	{
		super();
		this.propertyName=propertyName;
		this.parent = parent;
	}
	
	/**
	 * 是否是根属性值映射表
	 * @return
	 */
	public boolean isRoot()
	{
		return (this.propertyName == null);
	}
	
	/**
	 * 获取某个属性的属性名路径
	 * @param propertyName
	 * @return
	 */
	public String getPropertyNamePath(String propertyName)
	{
		String result=null;
		
		if(this.parent != null)
			result=this.parent.getPropertyNamePath(this.propertyName);
		else
			result=this.propertyName;
		
		if(result==null || result.length()==0)
			return propertyName;
		else
			return result+WebConstants.ACCESSOR+propertyName;
	}
	
	/**
	 * 分解给定的关键字为<i>访问符表达式</i>的映射表
	 * @param accessExpKeyMap 映射表，它的关键字具有<i>访问符表达式</i>语义
	 */
	public void resolve(Map<String, ?> accessExpKeyMap)
	{
		String filter= (this.propertyName==null || this.propertyName.length()==0 ? null : this.propertyName+WebConstants.ACCESSOR);
		
		Set<String> keys=accessExpKeyMap.keySet();
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
				
				PropertyValueMap parent=this;
				
				for(int i=0; i<propKeys.length; i++)
				{
					if(i == propKeys.length-1)
					{
						parent.put(propKeys[i], accessExpKeyMap.get(key));
					}
					else
					{
						PropertyValueMap tmp=(PropertyValueMap)parent.get(propKeys[i]);
						if(tmp == null)
						{
							tmp=new PropertyValueMap(propKeys[i], parent);
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

	public PropertyValueMap getParent() {
		return parent;
	}

	public void setParent(PropertyValueMap parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [propertyName=" + getPropertyName()
				+ ", " + super.toString() + "]";
	}
}