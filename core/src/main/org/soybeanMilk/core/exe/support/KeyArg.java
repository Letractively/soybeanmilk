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
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.core.ObjectSource;

/**
 * 关键字调用参数，参数的值是{@linkplain Invoke 调用}当前执行{@linkplain ObjectSource 对象源}中某个关键字的值
 * @author earthangry@gmail.com
 * @date 2012-5-6
 */
public class KeyArg extends AbstractArg
{
	/**参数值在对象源中的关键字*/
	private Serializable key;
	
	public KeyArg()
	{
		this(null);
	}
	
	public KeyArg(Serializable key)
	{
		super();
		this.key = key;
	}
	
	public KeyArg(Serializable key, Type type)
	{
		super();
		this.key = key;
		setType(type);
	}
	
	public Serializable getKey()
	{
		return key;
	}
	
	public void setKey(Serializable key)
	{
		this.key = key;
	}
	
	//@Override
	public Object getValue(ObjectSource objectSource, Type argType, Method method, Class<?> methodClass) throws Exception
	{
		Type targetType=getType();
		
		if(targetType == null)
			targetType=argType;
		
		if(!SbmUtils.isClassType(targetType))
			targetType=toConcreteArgType(argType, methodClass);
		
		return objectSource.get(this.key, targetType);
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [key=" + key + ", type= " + getType() + "]";
	}
	
	/**
	 * 将参数类型具体化
	 * @param argType
	 * @param ownerClass
	 * @return
	 * @date 2012-5-16
	 */
	protected Type toConcreteArgType(Type argType, Class<?> ownerClass)
	{
		return SbmUtils.toConcreteType(argType, ownerClass);
	}
}