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

import java.lang.reflect.Array;

import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;

/**
 * 数组转换器，它支持将一个类型的数组转换为另一个类型的数组
 * @author earthAngry@gmail.com
 * @date 2010-10-29
 *
 */
public class ArrayConverter extends AbstractConverter
{
	private Converter elementConverter;
	
	public ArrayConverter(Converter elementConverter)
	{
		super();
		this.elementConverter = elementConverter;
	}
	
	@Override
	public Object convert(Object sourceObj, Class<?> targetClass)
	{
		if(!targetClass.isArray())
			throw new ConvertException("[targetClass] must be an array");
		
		if(sourceObj == null)
			return null;
		
		if(!sourceObj.getClass().isArray())
			throw new ConvertException("[sourceObj] must be an array");
		
		int len=Array.getLength(sourceObj);
		Class<?> componentClass=targetClass.getComponentType();
		
		Object targetObj=Array.newInstance(componentClass, len);
		
		for(int i=0;i<len;i++)
			Array.set(targetObj, i, elementConverter.convert(Array.get(sourceObj, i), componentClass));
		
		return targetObj;
	}
}
