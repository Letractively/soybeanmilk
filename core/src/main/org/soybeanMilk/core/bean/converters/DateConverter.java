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
 * 日期转换器，它可以将如下格式的字符串转换为java.util.Date对象：
 * <ul>
 *   <li>yyyy</li>
 *   <li>yyyy-MM</li>
 *   <li>yyyy-MM-dd</li>
 *   <li>yyyy-MM-dd HH</li>
 *   <li>yyyy-MM-dd HH:mm</li>
 *   <li>yyyy-MM-dd HH:mm:ss</li>
 *   <li>yyyy-MM-dd HH:mm:ss.SSS</li>
 * </ul>
 * @author earthAngry@gmail.com
 * @date 2010-1-29
 */
public class DateConverter extends AbstractConverter
{
	private static String[] PATTERNS = new String[]{"yyyy","yyyy-MM","yyyy-MM-dd","yyyy-MM-dd HH",
		"yyyy-MM-dd HH:mm","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS"};
	
	private org.apache.commons.beanutils.converters.DateConverter c;
	
	public DateConverter()
	{
		c=new org.apache.commons.beanutils.converters.DateConverter();
		c.setPatterns(PATTERNS);
	}

	@Override
	public Object convert(Object sourceObj, Class<?> targetClass)
	{
		return c.convert(targetClass, sourceObj);
	}
}