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

import java.lang.reflect.Type;

import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;

/**
 * 目标类型是{@linkplain java.lang.Class Class}的转换器。
 * @author earthangry@gmail.com
 * @date 2010-10-3
 */
public abstract class ClassTypeConverter implements Converter
{
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
	{
		Object result=null;
		
		if(targetType == null)
			result=sourceObj;
		
		try
		{
			result=convertToClass(sourceObj, SoybeanMilkUtils.narrowToClassType(targetType));
		}
		catch(Exception e)
		{
			throw new ConvertException(sourceObj, targetType, e);
		}
		
		return (T)result;
	}
	
	/**
	 * 将源对象转换为目标类型的对象
	 * @param sourceObj
	 * @param targetType
	 * @return
	 */
	protected abstract Object convertToClass(Object sourceObj, Class<?> targetType);
}
