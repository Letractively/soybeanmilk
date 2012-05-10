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

import org.soybeanMilk.core.ObjectSource;

import org.soybeanMilk.core.exe.Invoke.Resolver;
import org.soybeanMilk.core.exe.Invoke.ResolverProvider;

/**
 * 对象调用目标提供者，它直接持有{@linkplain Resolver 调用目标}
 * @author earthangry@gmail.com
 * @date 2010-10-19
 */
public class ObjectResolverProvider implements ResolverProvider
{
	private Resolver resolver;
	
	public ObjectResolverProvider(){}

	public ObjectResolverProvider(Object resolverObject)
	{
		this.resolver=new Resolver(resolverObject, resolverObject.getClass());
	}
	
	public ObjectResolverProvider(Object resolverObject, Class<?> resolverClass)
	{
		this.resolver=new Resolver(resolverObject, resolverClass);
	}
	
	//@Override
	public Resolver getResolver(ObjectSource objectSource) throws Exception
	{
		return this.resolver;
	}

	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [resolver=" + resolver + "]";
	}
}