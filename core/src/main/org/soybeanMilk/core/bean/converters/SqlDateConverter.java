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
 * 字符串到{@linkplain java.sql.Date}类型转换器，它可以将“yyyy-MM-dd”格式的字符串转换为{@linkplain java.sql.Date}类型的对象。
 * @author earthangry@gmail.com
 * @date 2010-10-3
 */
public class SqlDateConverter extends DateConverter
{
	private static String[] PATTERNS=new String[]{"yyyy-MM-dd"};
	
	//@Override
	protected Object convertStringToType(String str, Class<?> type) throws Exception
	{
		if(str.length() == 10)
		{
			return new java.sql.Date(parseDate(str, PATTERNS[0]).getTime());
		}
		else
			return convertNotSupportedThrow(str, type);
	}
}