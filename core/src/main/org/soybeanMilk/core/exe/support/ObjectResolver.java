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

/**
 * 对象调用目标，它直接持有调用目标对象
 * @author earthangry@gmail.com
 * @date 2010-10-19
 */
public class ObjectResolver implements Resolver
{
	private Object resolverObject;
	private Class<?> resolverClass;
	
	public ObjectResolver(){}

	public ObjectResolver(Object resolverObject)
	{
		this.resolverObject=resolverObject;
	}
	
	public ObjectResolver(Object resolverObject, Class<?> resolverClass)
	{
		this.resolverObject=resolverObject;
		this.resolverClass=resolverClass;
	}
	
	//@Override
	public Object getResolverObject(ObjectSource objectSource) throws Exception
	{
		return this.resolverObject;
	}
	
	//@Override
	public Class<?> getResolverClass(ObjectSource objectSource) throws Exception
	{
		return this.resolverClass;
	}

	public Object getResolverObject() {
		return resolverObject;
	}

	public void setResolverObject(Object resolverObject) {
		this.resolverObject = resolverObject;
	}

	public Class<?> getResolverClass() {
		return resolverClass;
	}

	public void setResolverClass(Class<?> resolverClass) {
		this.resolverClass = resolverClass;
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [resolverObject=" + resolverObject
				+ ", resolverClass=" + resolverClass + "]";
	}
}