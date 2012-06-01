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

package org.soybeanMilk.core.exe.support;

import java.io.Serializable;

import org.soybeanMilk.core.exe.Invoke;

/**
 * 调用目标对象工厂，它为{@linkplain Invoke 调用}提供执行调用方法时所依赖的方法对象。
 * @author earthangry@gmail.com
 * @date 2010-10-19
 */
public interface ResolverObjectFactory
{
	/**
	 * 根据ID查找调用目标对象。如果没有找到，它应该返回<code>null</code>而不是抛出异常
	 * @param resolverObjectId 调用目标对象ID
	 * @return
	 */
	Object getResolverObject(Serializable resolverObjectId);
	
	/**
	 * 添加一个调用目标对象
	 * @param resolverObjectId
	 * @param resolverObject
	 * @date 2012-5-9
	 */
	void addResolverObject(Serializable resolverObjectId, Object resolverObject);
}