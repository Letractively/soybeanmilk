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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.SbmUtils;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestClassShortName
{
	@Before
	public void setUp() throws Exception{}
	
	@Test
	public void getShortName_boolean()throws Exception
	{
		Type c=SbmUtils.nameToType("boolean");
		Assert.assertEquals(boolean.class, c);
	}
	
	@Test
	public void getShortName_booleanArray()throws Exception
	{
		Type c=SbmUtils.nameToType("boolean[]");
		Assert.assertEquals(boolean[].class, c);
	}
	
	@Test
	public void getShortName_Boolean() throws Exception
	{
		Type c=SbmUtils.nameToType("Boolean");
		Assert.assertEquals(Boolean.class, c);
	}
	
	@Test
	public void getShortName_BooleanArray() throws Exception
	{
		Type c=SbmUtils.nameToType("Boolean[]");
		Assert.assertEquals(Boolean[].class, c);
	}
	
	@Test
	public void getShortName_stringArray() throws Exception
	{
		Type c=SbmUtils.nameToType("String[]");
		Assert.assertEquals(String[].class, c);
	}
	
	@Test
	public void getShortName_sqlDate() throws Exception
	{
		Type c=SbmUtils.nameToType("java.sql.Date");
		Assert.assertEquals(java.sql.Date.class, c);
	}
	
	@Test
	public void getShortName_sqlDateArray() throws Exception
	{
		Type c=SbmUtils.nameToType("java.sql.Date[]");
		Assert.assertEquals(java.sql.Date[].class, c);
	}
	
	@Test
	public void getShortName_sqlTime() throws Exception
	{
		Type c=SbmUtils.nameToType("java.sql.Time");
		Assert.assertEquals(java.sql.Time.class, c);
	}
	
	@Test
	public void getShortName_sqlTimeArray() throws Exception
	{
		Type c=SbmUtils.nameToType("java.sql.Time[]");
		Assert.assertEquals(java.sql.Time[].class, c);
	}
	
	@Test
	public void getShortName_sqlTimestamp() throws Exception
	{
		Type c=SbmUtils.nameToType("java.sql.Timestamp");
		Assert.assertEquals(java.sql.Timestamp.class, c);
	}
	
	@Test
	public void getShortName_sqlTimestampArray() throws Exception
	{
		Type c=SbmUtils.nameToType("java.sql.Timestamp[]");
		Assert.assertEquals(java.sql.Timestamp[].class, c);
	}
}
