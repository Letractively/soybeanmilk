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

package org.soybeanMilk.core.bean;

import java.lang.reflect.Type;

/**
 * 泛型类型元信息。<br>
 * 它封装泛型类型所属的类信息，使用者仅通过此类即可解析任何泛型对象。
 * @author earthAngry@gmail.com
 * @date 2011-9-29
 *
 */
public class GenericTypeMeta implements Type
{
	/**泛型类型*/
	private Type type;
	
	/**此泛型类型所属的类*/
	private Class<?> ownerClass;
	
	public GenericTypeMeta()
	{
		super();
	}

	public GenericTypeMeta(Type type, Class<?> ownerClass)
	{
		super();
		this.type = type;
		this.ownerClass = ownerClass;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public Class<?> getOwnerClass()
	{
		return ownerClass;
	}

	public void setOwnerClass(Class<?> ownerClass)
	{
		this.ownerClass = ownerClass;
	}
}
