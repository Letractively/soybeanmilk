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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class MockWildcardType implements WildcardType
{
	private Type[] lowerBounds;
	private Type[] upperBounds;
	
	public MockWildcardType()
	{
		super();
	}
	
	public Type[] getLowerBounds()
	{
		return lowerBounds;
	}

	public void setLowerBounds(Type[] lowerBounds) {
		this.lowerBounds = lowerBounds;
	}

	public Type[] getUpperBounds() {
		return upperBounds;
	}

	public void setUpperBounds(Type[] upperBounds) {
		this.upperBounds = upperBounds;
	}
}
