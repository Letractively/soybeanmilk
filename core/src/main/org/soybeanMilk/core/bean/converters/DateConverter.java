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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串到{@linkplain java.util.Date Date}类型转换器，它可以将如下格式的字符串转换为{@linkplain java.util.Date Date}对象：
 * <ul>
 *   <li>yyyy</li>
 *   <li>yyyy-MM</li>
 *   <li>yyyy-MM-dd</li>
 *   <li>yyyy-MM-dd HH</li>
 *   <li>yyyy-MM-dd HH:mm</li>
 *   <li>yyyy-MM-dd HH:mm:ss</li>
 *   <li>yyyy-MM-dd HH:mm:ss.SSS</li>
 * </ul>
 * @author earthangry@gmail.com
 * @date 2010-1-29
 */
public class DateConverter extends AbstractStringTypeConverter
{
	private static String[] PATTERNS = new String[]{
		"yyyy", "yyyy-MM", "yyyy-MM-dd",
		"yyyy-MM-dd HH", "yyyy-MM-dd HH:mm",
		"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"};
	
	//@Override
	protected Object convertStringToType(String str, Class<?> type) throws Exception
	{
		int len=str.length();
		
		switch(len)
		{
			case 4 :
				return parseDate(str, PATTERNS[0]);
			case 7 :
				return parseDate(str, PATTERNS[1]);
			case 10 :
				return parseDate(str, PATTERNS[2]);
			case 13 :
				return parseDate(str, PATTERNS[3]);
			case 16 :
				return parseDate(str, PATTERNS[4]);
			case 19 :
				return parseDate(str, PATTERNS[5]);
			case 23 :
				return parseDate(str, PATTERNS[6]);
			default :
				return convertNotSupportedThrow(str, type);
		}
	}
	
	/**
	 * 将字符串以给定格式转换为日期类型
	 * @param str
	 * @param format
	 * @return
	 * @throws Exception
	 * @date 2012-5-23
	 */
	protected Date parseDate(String str, String format) throws Exception
	{
		return new SimpleDateFormat(format).parse(str);
	}
}