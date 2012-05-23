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

package org.soybeanMilk.core.bean.converters;

/**
 * 字符串到{@linkplain Boolean}类型转换器，它可以将<code>"true", "1", "on", "false", "0", "off"</code>转换为{@linkplain Boolean}类型对象
 * @author earthangry@gmail.com
 * @date 2010-10-3
 */
public class BooleanConverter extends AbstractStringTypeConverter
{
	//@Override
	protected Object convertStringToType(String str, Class<?> type) throws Exception
	{
		if("true".equals(str) || "1".equals(str) || "on".equals(str))
			return Boolean.TRUE;
		else if("false".equals(str) || "0".equals(str) || "off".equals(str))
			return Boolean.FALSE;
		else
			return convertNotSupportedThrow(str, type);
	}
}