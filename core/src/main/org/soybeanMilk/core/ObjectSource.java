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

package org.soybeanMilk.core;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * 对象源，用于获取和存储对象。<br>
 * {@linkplain Executable 可执行对象}从它获取执行所需的参数对象并用它存储执行结果对象。
 * @author earthangry@gmail.com
 * @date 2010-9-30
 */
public interface ObjectSource
{
	/**
	 * 从这个对象源中获取对象
	 * @param key 对象关键字
	 * @return
	 * @throws ObjectSourceException
	 * @date 2012-5-16
	 */
	<T> T get(Serializable key) throws ObjectSourceException;
	
	/**
	 * 从这个对象源中获取对象，如果对象类型与期望类型不匹配，对象会被转换为目标类型的对象
	 * @param key 对象关键字
	 * @param expectType 期望类型
	 * @return
	 * @throws ObjectSourceException
	 */
	<T> T get(Serializable key, Type expectType) throws ObjectSourceException;
	
	/**
	 * 将对象保存到这个对象源中
	 * @param key 对象关键字
	 * @param obj 要保存的对象
	 * @throws ObjectSourceException
	 */
	void set(Serializable key, Object obj) throws ObjectSourceException;
}