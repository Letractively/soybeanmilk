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

import org.soybeanMilk.core.bean.ConvertException;

/**
 * 布尔类型转换器
 * @author earthAngry@gmail.com
 * @date 2010-10-3
 */
public class BooleanConverter extends ClassTypeConverter
{
	private org.apache.commons.beanutils.converters.BooleanConverter c;
	
	public BooleanConverter()
	{
		super();
		c=new org.apache.commons.beanutils.converters.BooleanConverter();
	}
	
	@Override
	protected Object convertToClass(Object sourceObj, Class<?> targetType) throws ConvertException
	{
		try
		{
			return c.convert(targetType, sourceObj);
		}
		catch(Exception e)
		{
			throw new ConvertException(sourceObj, targetType, e);
		}
	}
}
