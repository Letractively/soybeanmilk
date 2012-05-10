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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.config.parser.ClassShortName;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestClassShortName
{
	@Before
	public void setUp(){}
	
	@Test
	public void getShortName_boolean()
	{
		{
			Class<?> c=ClassShortName.get("boolean");
			Assert.assertEquals(boolean.class, c);
		}
	}
	
	@Test
	public void getShortName_booleanArray()
	{
		{

			Class<?> c=ClassShortName.get("boolean[]");
			Assert.assertEquals(boolean[].class, c);
		}
	}
	
	@Test
	public void getShortName_Boolean()
	{
		{
			Class<?> c=ClassShortName.get("Boolean");
			Assert.assertEquals(Boolean.class, c);
		}
	}
	
	@Test
	public void getShortName_BooleanArray()
	{
		{
			Class<?> c=ClassShortName.get("Boolean[]");
			Assert.assertEquals(Boolean[].class, c);
		}
	}
	
	@Test
	public void getShortName_stringArray()
	{
		{
			Class<?> c=ClassShortName.get("String[]");
			Assert.assertEquals(String[].class, c);
		}
	}
	
	@Test
	public void getShortName_sqlDate()
	{
		{
			Class<?> c=ClassShortName.get("java.sql.Date");
			Assert.assertEquals(java.sql.Date.class, c);
		}
	}
	
	@Test
	public void getShortName_sqlDateArray()
	{
		{
			Class<?> c=ClassShortName.get("java.sql.Date[]");
			Assert.assertEquals(java.sql.Date[].class, c);
		}
	}
	
	@Test
	public void getShortName_sqlTime()
	{
		{
			Class<?> c=ClassShortName.get("java.sql.Time");
			Assert.assertEquals(java.sql.Time.class, c);
		}
	}
	
	@Test
	public void getShortName_sqlTimeArray()
	{
		{
			Class<?> c=ClassShortName.get("java.sql.Time[]");
			Assert.assertEquals(java.sql.Time[].class, c);
		}
	}
	
	@Test
	public void getShortName_sqlTimestamp()
	{
		{
			Class<?> c=ClassShortName.get("java.sql.Timestamp");
			Assert.assertEquals(java.sql.Timestamp.class, c);
		}
	}
	
	@Test
	public void getShortName_sqlTimestampArray()
	{
		{
			Class<?> c=ClassShortName.get("java.sql.Timestamp[]");
			Assert.assertEquals(java.sql.Timestamp[].class, c);
		}
	}
}
