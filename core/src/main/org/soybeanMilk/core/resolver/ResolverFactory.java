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

package org.soybeanMilk.core.resolver;

import java.io.Serializable;

/**
 * 解决对象工厂
 * @author earthAngry@gmail.com
 * @date 2010-10-19
 *
 */
public interface ResolverFactory
{
	/**
	 * 根据ID查找解决对象
	 * @param resolverId 解决对象ID
	 * @return
	 */
	Object getResolver(Serializable resolverId);
}