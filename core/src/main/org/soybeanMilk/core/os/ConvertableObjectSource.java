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

package org.soybeanMilk.core.os;

import java.io.Serializable;
import java.lang.reflect.Type;

import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.bean.GenericConverter;


/**
 * 可以添加对象转换支持的对象源，当对象源中的对象与期望的结果对象类型不符时，对象源将尝试进行类型转换
 * @author earthAngry@gmail.com
 * @date 2010-10-14
 *
 */
public abstract class ConvertableObjectSource implements ObjectSource
{
	private GenericConverter genericConverter;
	
	public GenericConverter getGenericConverter() {
		return genericConverter;
	}
	
	/**
	 * 设置通用转换器
	 * @param genericConverter
	 */
	public void setGenericConverter(GenericConverter genericConverter) {
		this.genericConverter = genericConverter;
	}
	
	@Override
	public abstract Object get(Serializable key, Type type);
	
	@Override
	public abstract void set(Serializable key, Object obj);
}