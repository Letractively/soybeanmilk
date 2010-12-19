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

import org.soybeanMilk.core.exe.Invoke.ResolverProvider;

/**
 * 解决对象提供者，它从解决对象工厂中即时取得调用所需的解决对象
 * @author earthAngry@gmail.com
 * @date 2010-10-19
 *
 */
public class FactoryResolverProvider implements ResolverProvider
{
	private ResolverFactory resolverFactory;
	private Serializable resolverId;
	
	public FactoryResolverProvider()
	{
		this(null, null);
	}

	public FactoryResolverProvider(ResolverFactory resolverFactory,
			Serializable resolverId)
	{
		super();
		this.resolverFactory = resolverFactory;
		this.resolverId = resolverId;
	}

	public ResolverFactory getResolverFactory() {
		return resolverFactory;
	}
	public void setResolverFactory(ResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}

	public Serializable getResolverId() {
		return resolverId;
	}
	public void setResolverId(Serializable resolverId) {
		this.resolverId = resolverId;
	}

	@Override
	public Object getResolver()
	{
		return getResolverFactory().getResolver(getResolverId());
	}
}