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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 调用目标对象工厂的默认实现，它本身是一个对象容器，另外还支持添加<i>外部调用目标对象工厂</i>，用于支持其他的IOC容器，比如spring和Guice。
 * 如果设置了外部调用目标对象工厂，它将被优先考虑。
 * @author earthangry@gmail.com
 * @date 2010-10-1
 */
public class DefaultResolverObjectFactory implements ResolverObjectFactory
{
	private static Log log=LogFactory.getLog(DefaultResolverObjectFactory.class);
	
	private Map<Serializable, Object> resolverObjects;
	
	/**外部调用目标对象工厂*/
	private ResolverObjectFactory externalResolverObjectFactory;
	
	public DefaultResolverObjectFactory(){}
	
	public Map<Serializable, Object> getResolverObjects() {
		return resolverObjects;
	}

	public void setResolverObjects(Map<Serializable, Object> resolverObjects) {
		this.resolverObjects = resolverObjects;
	}

	public ResolverObjectFactory getExternalResolverObjectFactory() {
		return externalResolverObjectFactory;
	}
	
	/**
	 * 设置<i>外部调用目标对象工厂</i>
	 * @param externalResolverFactory
	 */
	public void setExternalResolverObjectFactory(ResolverObjectFactory externalResolverObjectFactory) {
		this.externalResolverObjectFactory = externalResolverObjectFactory;
	}
	
	//@Override
	public Object getResolverObject(Serializable resolverObjectId)
	{
		Object re= this.externalResolverObjectFactory==null ? null : this.externalResolverObjectFactory.getResolverObject(resolverObjectId);
		if(re == null)
			re= this.resolverObjects==null ? null : this.resolverObjects.get(resolverObjectId);
		
		return re;
	}
	
	/**
	 * 添加一个调用目标对象
	 * @param id 解决对象标识
	 * @param resolver 解决对象
	 */
	public void addResolverObject(Serializable resolverObjectId, Object resolverObject)
	{
		if(this.resolverObjects == null)
			this.resolverObjects = new HashMap<Serializable, Object>();
		
		//允许重复添加，使得功能可以被替换
		if(this.resolverObjects.get(resolverObjectId) != null)
			log.warn("duplicate resolver object id '"+resolverObjectId+"'");
		
		this.resolverObjects.put(resolverObjectId, resolverObject);
		
		if(log.isDebugEnabled())
			log.debug("add a resolver object '"+resolverObject+"' with id '"+resolverObjectId+"'");
	}
}