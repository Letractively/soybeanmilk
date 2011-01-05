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
 * {@linkplain java.sql.Timestamp}类型转换器，它可以将“yyyy-MM-dd HH:mm:ss”或“yyyy-MM-dd HH:mm:ss.SSS”格式的字符串转换为{@linkplain java.sql.Timestamp}类型的对象。
 * @author earthAngry@gmail.com
 * @date 2010-10-3
 * @see org.apache.commons.beanutils.converters.SqlTimestampConverter
 */
public class SqlTimestampConverter extends ClassTypeConverter
{
	private static String[] PATTERNS=new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"};
	
	private org.apache.commons.beanutils.converters.SqlTimestampConverter c;
	
	public SqlTimestampConverter()
	{
		super();
		
		c = new org.apache.commons.beanutils.converters.SqlTimestampConverter();
		c.setPatterns(PATTERNS);
	}
	
	@Override
	protected Object convertToClass(Object sourceObj, Class<?> targetType)
	{
		return c.convert(targetType, sourceObj);
	}
}
