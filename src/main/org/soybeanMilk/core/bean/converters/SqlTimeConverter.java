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
 * SQL时间转换器，它可以将“HH:mm:ss”或“HH:mm:ss.SSS”格式的字符串转换为java.sql.Time类型的对象。
 * @author earthAngry@gmail.com
 * @date 2010-10-3
 */
public class SqlTimeConverter extends AbstractConverter
{
	private static String[] PATTERNS=new String[]{"HH:mm:ss", "HH:mm:ss.SSS"};
	
	private org.apache.commons.beanutils.converters.SqlTimeConverter c;
	
	public SqlTimeConverter()
	{
		super();
		
		c = new org.apache.commons.beanutils.converters.SqlTimeConverter();
		c.setPatterns(PATTERNS);
	}
	
	@Override
	public Object convert(Object sourceObj, Class<?> targetClass)
	{
		return c.convert(targetClass, sourceObj);
	}
}
