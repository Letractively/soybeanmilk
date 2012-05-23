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

import org.soybeanMilk.SbmUtils;

/**
 * 字符串到字符类型转换器，它返回转义后长度为1的字符串的第一个字符。
 * @author earthangry@gmail.com
 * @date 2010-10-3
 */
public class CharacterConverter extends AbstractStringTypeConverter
{
	//@Override
	protected Object convertStringToType(String str, Class<?> type) throws Exception
	{
		if(str.length() == 1)
			return str.charAt(0);
		else
		{
			String unEscaped=SbmUtils.unEscape(str);
			
			if(unEscaped.length() == 1)
				return unEscaped.charAt(0);
			else
				return convertNotSupportedThrow(str, type);
		}
	}
}