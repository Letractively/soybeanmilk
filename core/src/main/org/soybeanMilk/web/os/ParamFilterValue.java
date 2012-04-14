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

package org.soybeanMilk.web.os;

/**
 * 参数过滤值，它保存参数过滤器和过滤结果值。
 * 
 * @author earthangry@gmail.com
 * @date 2012-3-27
 */
public class ParamFilterValue
{
	/**过滤器*/
	private String filter;
	
	/**过滤值*/
	private Object value;
	
	/**
	 * 创建一个空过滤值对象
	 */
	public ParamFilterValue(){}

	/**
	 * 创建一个参数过滤值对象
	 * @param filter 过滤器
	 * @param value 过滤值
	 */
	public ParamFilterValue(String filter, Object value)
	{
		this.filter = filter;
		this.value = value;
	}

	/**
	 * 设置过滤器
	 * @param filter
	 */
	public void setFilter(String filter)
	{
		this.filter = filter;
	}
	
	/**
	 * 获取过滤器
	 * @return
	 */
	public String getFilter()
	{
		return this.filter;
	}
	
	/**
	 * 设置过滤值
	 * @param value
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	/**
	 * 获取过滤值
	 * @return
	 */
	public Object getValue()
	{
		return this.value;
	}
}