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

/**
 * 转换器，它可以将某个类型的对象转换为另一个类型的对象，比如将字符串转换成整型
 * @author earthAngry@gmail.com
 * @date 2010-10-5
 */
public interface Converter
{
	/**
	 * 将源对象转换为目标类型的对象
	 * @param sourceObj 源对象
	 * @param targetClass 目标类型
	 * @return <code>targetClass</code>类型的对象
	 */
	Object convert(Object sourceObj, Class<?> targetClass);
}
