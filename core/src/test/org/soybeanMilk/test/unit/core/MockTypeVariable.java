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

package org.soybeanMilk.test.unit.core;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class MockTypeVariable<D extends GenericDeclaration> implements TypeVariable<D>
{
	private D genericDeclaration;
	private Type[] bounds;
	private String name;
	
	public MockTypeVariable()
	{
		super();
	}
	
	public MockTypeVariable(String name, Type[] bounds)
	{
		super();
		this.name = name;
		this.bounds = bounds;
	}
	
	public D getGenericDeclaration() {
		return genericDeclaration;
	}
	public void setGenericDeclaration(D genericDeclaration) {
		this.genericDeclaration = genericDeclaration;
	}
	public Type[] getBounds() {
		return bounds;
	}
	public void setBounds(Type[] bounds) {
		this.bounds = bounds;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
