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

package refactor.org.soybeanMilk.core.exe.support;

import java.io.Serializable;
import java.lang.reflect.Type;

import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.GenericType;
import org.soybeanMilk.core.exe.ArgPrepareExecuteException;

import refactor.org.soybeanMilk.core.exe.Invoke.Arg;
import refactor.org.soybeanMilk.core.exe.Invoke.MethodInfo;

/**
 * 关键字调用参数，参数的值是{@linkplain Invoke 调用}执行的当前{@linkplain ObjectSource 对象源}某个关键字的值
 * @author earthangry@gmail.com
 * @date 2012-5-6
 */
public class KeyArg implements Arg
{
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
	
	public Serializable getKey()
	{
		return key;
	}
	
	public void setKey(Serializable key)
	{
		this.key = key;
	}
	
	public Object getValue(ObjectSource objectSource, MethodInfo methodInfo, int argIdx) throws ArgPrepareExecuteException
	{
		Type argType=methodInfo.getArgType(argIdx);
		if(!SoybeanMilkUtils.isClassType(argType))
			argType=GenericType.getGenericType(argType, methodInfo.getMethodClass());
		
		try
		{
			return objectSource.get(this.key, argType);
		}
		catch(ObjectSourceException e)
		{
			//TODO 把null替换为this
			throw new ArgPrepareExecuteException(null, argIdx, e);
		}
	}
}