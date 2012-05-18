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

import org.soybeanMilk.core.ObjectSource;

import org.soybeanMilk.core.exe.Invoke.Resolver;

/**
 * 工厂调用目标，它从{@linkplain ResolverObjectFactory 调用目标对象工厂}中获取调用目标对象
 * @author earthangry@gmail.com
 * @date 2010-10-19
 */
public class FactoryResolver implements Resolver
{
	private Serializable resolverId;
	private ResolverObjectFactory resolverObjectFactory;
	
	public FactoryResolver()
	{
		this(null, null);
	}
	
	public FactoryResolver(ResolverObjectFactory resolverObjectFactory,
			Serializable resolverId)
	{
		super();
		this.resolverObjectFactory = resolverObjectFactory;
		this.resolverId = resolverId;
	}
	
	public ResolverObjectFactory getResolverObjectFactory() {
		return resolverObjectFactory;
	}

	public void setResolverObjectFactory(ResolverObjectFactory resolverObjectFactory) {
		this.resolverObjectFactory = resolverObjectFactory;
	}
	
	public Serializable getResolverId() {
		return resolverId;
	}
	
	public void setResolverId(Serializable resolverId) {
		this.resolverId = resolverId;
	}
	
	//@Override
	public Object getResolverObject(ObjectSource objectSource) throws Exception
	{
		Object ro=this.resolverObjectFactory.getResolverObject(this.resolverId);
		
		return ro;
	}
	
	//@Override
	public Class<?> getResolverClass(ObjectSource objectSource) throws Exception
	{
		return null;
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [resolverId=" + resolverId
				+ ", resolverObjectFactory=" + resolverObjectFactory + "]";
	}
}