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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.os.HashMapObjectSource;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestHashMapObjectSource
{
	private HashMapObjectSource objSource;
	
	@Before
	public void setUp()
	{
		objSource=new HashMapObjectSource(new DefaultGenericConverter());
	}
	
	@Test
	public void get() throws Exception
	{
		{
			String src="1";
			
			objSource.set("key", src);
			Integer dest=objSource.get("key", int.class);
			Assert.assertEquals(Integer.parseInt(src), dest.intValue());
		}
		
		{
			String[] src=new String[]{"1","2"};
			objSource.set("key", src);
			
			int[] dest=objSource.get("key", int[].class);
			
			Assert.assertEquals(Integer.parseInt(src[0]), dest[0]);
			Assert.assertEquals(Integer.parseInt(src[1]), dest[1]);
		}
	}
	
	@Test
	public void getPrimitiveForNull() throws Exception
	{
		GenericConvertException re=null;
		try
		{
			objSource.get("key", int.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue((re.getMessage().startsWith("can not convert null to primitive")));
	}
}
