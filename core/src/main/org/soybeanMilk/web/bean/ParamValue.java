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

/**
 * 参数值，它知道自己的参数名
 * 
 * @author earthAngry@gmail.com
 * @date 2012-2-21
 */
public class ParamValue
{
	/**参数名*/
	private String paramName;
	
	/**要被转换的对象*/
	private Object value;
	
	public ParamValue()
	{
		this(null, null);
	}

	public ParamValue(String paramName, Object value)
	{
		super();
		this.paramName = paramName;
		this.value = value;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "ParamValue [paramName=" + paramName + ", value=" + value + "]";
	}
}
