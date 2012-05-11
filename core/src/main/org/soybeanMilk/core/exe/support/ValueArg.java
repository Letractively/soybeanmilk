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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.soybeanMilk.core.ObjectSource;

import org.soybeanMilk.core.exe.Invoke.Arg;

/**
 * 值调用参数，它直接持有调用参数的值
 * @author earthangry@gmail.com
 * @date 2012-5-6
 */
public class ValueArg implements Arg
{
	/**参数值*/
	private Object value;
	
	public ValueArg()
	{
		this(null);
	}

	public ValueArg(Object value)
	{
		super();
		this.value = value;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	//@Override
	public Object getValue(ObjectSource objectSource, Type argType, Method method, Class<?> methodClass) throws Exception
	{
		return value;
	}
	
	//@Override
	public Type getArgType()
	{
		return (this.value == null ? null : this.value.getClass());
	}

	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [value=" + value + "]";
	}
}
