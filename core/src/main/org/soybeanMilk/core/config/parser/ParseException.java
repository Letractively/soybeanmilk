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

package org.soybeanMilk.core.config.parser;

/**
 * 解析异常。
 * @author earthAngry@gmail.com
 * @date 2010-10-1
 */
public class ParseException extends RuntimeException
{
	private static final long serialVersionUID = 8940703272817276135L;

	public ParseException()
	{
		super();
	}

	public ParseException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public ParseException(String arg0)
	{
		super(arg0);
	}

	public ParseException(Throwable arg0)
	{
		super(arg0);
	}
	
}
