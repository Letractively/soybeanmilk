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
import org.soybeanMilk.core.exe.Invoke.ResolverProvider;

/**
 * 对象源调用目标提供者，它从当前{@linkplain ObjectSource 对象源}中动态获取{@linkplain Resolver 调用目标}
 * @author earthangry@gmail.com
 * @date 2012-5-6
 */
public class ObjectSourceResolverProvider implements ResolverProvider
{
	private Serializable resolverKey;
	
	public ObjectSourceResolverProvider()
	{
		this(null);
	}
	
	public ObjectSourceResolverProvider(Serializable resolverKey)
	{
		this.resolverKey = resolverKey;
	}
	
	public Serializable getResolverKey() {
		return resolverKey;
	}
	
	public void setResolverKey(Serializable resolverKey) {
		this.resolverKey = resolverKey;
	}
	
	//@Override
	public Resolver getResolver(ObjectSource objectSource) throws Exception
	{
		Object ro=objectSource.get(this.resolverKey, null);
		
		return (ro == null ? null : new Resolver(ro));
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [resolverKey=" + resolverKey + "]";
	}
}
