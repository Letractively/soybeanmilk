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

package org.soybeanMilk.core.bean;

/**
 * 转换异常。
 * @author earthAngry@gmail.com
 * @date 2011-1-11
 *
 */
public class ConvertException extends Exception
{
	private static final long serialVersionUID = 5534640330364525246L;

	public ConvertException()
	{
		super();
	}

	public ConvertException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public ConvertException(String arg0)
	{
		super(arg0);
	}

	public ConvertException(Throwable arg0)
	{
		super(arg0);
	}
}
