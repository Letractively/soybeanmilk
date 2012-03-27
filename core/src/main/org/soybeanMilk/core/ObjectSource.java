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
 * 对象源，用于获取和存储对象。
 * @author earthAngry@gmail.com
 * @date 2010-9-30
 */
public interface ObjectSource
{
	/**
	 * 取得对象
	 * @param key 对象关键字
	 * @param expectType 期望的对象类型，如果为<code>null</code>，则表明不关心类型
	 * @return
	 * @throws ObjectSourceException
	 */
	Object get(Serializable key, Type expectType) throws ObjectSourceException;
	
	/**
	 * 保存对象
	 * @param key 对象关键字
	 * @param obj 要保存的对象
	 * @throws ObjectSourceException
	 */
	void set(Serializable key, Object obj) throws ObjectSourceException;
}