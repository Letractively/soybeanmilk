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
import java.util.HashMap;
import java.util.Map;

/**
 * 解决对象工厂的默认实现，它本身是一个解决对象容器，另外还支持添加<i>外部解决对象工厂</i>，用于支持其他的IOC容器，比如spring和Guice。
 * 如果设置了外部解决对象工厂，它将被优先考虑。
 * @author earthAngry@gmail.com
 * @date 2010-10-1
 */
public class DefaultResolverFactory implements ResolverFactory
{
	private Map<Serializable, Object> resolvers;
	/**外部解决对象工厂*/
	private ResolverFactory externalResolverFactory;
	
	public DefaultResolverFactory()
	{
		super();
	}
	
	public Map<Serializable, Object> getResolvers() {
		return resolvers;
	}
	public void setResolvers(Map<Serializable, Object> resolvers) {
		this.resolvers = resolvers;
	}

	public ResolverFactory getExternalResolverFactory() {
		return externalResolverFactory;
	}
	
	/**
	 * 设置<i>外部解决对象工厂</i>，解决对象将优先从它取得
	 * @param externalResolverFactory
	 */
	public void setExternalResolverFactory(ResolverFactory externalResolverFactory) {
		this.externalResolverFactory = externalResolverFactory;
	}
	
	@Override
	public Object getResolver(Serializable resolverBeanId)
	{
		Object re= externalResolverFactory==null ? null : externalResolverFactory.getResolver(resolverBeanId);
		if(re == null)
			re= resolvers==null ? null : resolvers.get(resolverBeanId);
		
		return re;
	}
	
	/**
	 * 添加一个解决对象，不能与已添加的<code>id</code>重复，但是可以与<i>外部解决对象工厂</i>中的重复
	 * @param id 解决对象标识
	 * @param resolver 解决对象
	 */
	public void addResolver(Serializable id, Object resolver)
	{
		if(resolvers == null)
			resolvers = new HashMap<Serializable, Object>();
		
		if(resolvers.get(id) != null)
			throw new IllegalArgumentException("duplicate resolver id '"+id+"'");
		
		resolvers.put(id, resolver);
	}
}