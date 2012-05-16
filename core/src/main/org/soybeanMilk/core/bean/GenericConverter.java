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

package org.soybeanMilk.core.bean;

import java.lang.reflect.Type;

/**
 * 通用转换器，通过添加辅助{@linkplain 转换器}，它可以将任何类型的对象转换到目标类型对象。
 * @author earthangry@gmail.com
 * @date 2010-10-10
 */
public interface GenericConverter extends Converter
{
	/**
	 * 添加一个辅助{@linkplain Converter 转换器}，后添加的转换器应该替换旧的相同功能的转换器
	 * @param sourceType 源类型
	 * @param targetType 目标类型
	 * @param converter 辅助转换器对象
	 */
	void addConverter(Type sourceType,Type targetType,Converter converter);
	
	/**
	 * 取得能够将源类型转换到目标类型的辅助{@linkplain Converter 转换器}
	 * @param sourceType 源类型
	 * @param targetType 目标类型
	 * @return 转换器对象
	 */
	Converter getConverter(Type sourceType,Type targetType);
}
