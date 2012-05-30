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

import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;

/**
 * 字符串转换器，它可以将字符串转换为其他类型的对象
 * @author earthangry@gmail.com
 * @date 2012-5-23
 */
public abstract class AbstractStringTypeConverter implements Converter
{
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
	{
		if(targetType == null)
			return (T)sourceObj;
		
		String sourceStr=(String)sourceObj;
		Class<?> tc=SbmUtils.narrowToClass(targetType);
		
		//空字符串到其他类型认为是null
		if(sourceStr==null || "".equals(sourceStr))
		{
			if(tc.isPrimitive())
				throw new ConvertException(sourceObj, targetType, "can not convert "+SbmUtils.toString(sourceObj)+" to primitive type "+SbmUtils.toString(targetType));
			else
				return null;
		}
		else
		{
			try
			{
				return (T)convertStringToType(sourceStr, tc);
			}
			catch(Exception e)
			{
				if(e instanceof ConvertException)
					throw (ConvertException)e;
				else
					throw new ConvertException(sourceObj, targetType, e);
			}
		}
	}
	
	/**
	 * 将字符串转换为某个类型的对象
	 * @param str 源字符串，它不会为<code>null</code>和<code>""</code>
	 * @param type 目标类型，它不会为<code>null</code>
	 * @return
	 * @date 2012-5-23
	 * @throws Exception
	 */
	protected abstract Object convertStringToType(String str, Class<?> type) throws Exception;
	
	/**
	 * 转换不支持的辅助抛出异常方法
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @throws ConvertException
	 * @date 2012-5-23
	 */
	protected Object convertNotSupportedThrow(Object sourceObj, Type targetType) throws ConvertException
	{
		throw new ConvertException(sourceObj, targetType, "convert "+SbmUtils.toString(sourceObj)+" to "+SbmUtils.toString(targetType)+" is not supported");
	}
}