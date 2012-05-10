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

package test.unit.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class MockParameterizedType implements ParameterizedType
{
	private Type[] actualTypeArguments;
	private Type rawType;
	
	public MockParameterizedType(Type rawType, Type... actualTypeArguments)
	{
		super();
		this.rawType = rawType;
		this.actualTypeArguments = actualTypeArguments;
	}
	
	//@Override
	public Type[] getActualTypeArguments()
	{
		return this.actualTypeArguments;
	}

	//@Override
	public Type getOwnerType()
	{
		return null;
	}
	
	//@Override
	public Type getRawType()
	{
		return this.rawType;
	}

	//@Override
	public String toString() {
		return "MockParameterizedType [rawType=" + rawType
				+ ", actualTypeArguments="
				+ Arrays.toString(actualTypeArguments) + "]";
	}
}
