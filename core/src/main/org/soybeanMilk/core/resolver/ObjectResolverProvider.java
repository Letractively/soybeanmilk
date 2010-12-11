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

import org.soybeanMilk.core.exe.Invoke.ResolverProvider;

/**
 * 解决对象提供者，它直接持有解决对象
 * @author earthAngry@gmail.com
 * @date 2010-10-19
 *
 */
public class ObjectResolverProvider implements ResolverProvider
{
	private Object resolver;
	
	public ObjectResolverProvider()
	{
		this(null);
	}

	public ObjectResolverProvider(Object resolver)
	{
		super();
		this.resolver = resolver;
	}

	@Override
	public Object getResolver()
	{
		return resolver;
	}

	public void setResolver(Object resolver) {
		this.resolver = resolver;
	}
}