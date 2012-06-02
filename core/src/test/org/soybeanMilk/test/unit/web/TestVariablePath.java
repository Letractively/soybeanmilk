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

package org.soybeanMilk.test.unit.web;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.vp.VariablePath;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestVariablePath
{
	@Before
	public void setUp(){}
	
	@Test
	public void create()
	{
		{
			VariablePath vp=new VariablePath("/abc/def/ghi");
			Assert.assertTrue( !vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("abc/{def}/ghi");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("abc/{def}/ghi/");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("{abc}");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(1, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("/{abc}");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(1, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("/abc/{def}/ghi");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("/abc/{def}/ghi/");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("///abc/{def}/////ghi/");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
	}
	
	@Test
	public void compareTo()
	{
		{
			VariablePath vp0=new VariablePath("/abc/def/ghi");
			VariablePath vp1=new VariablePath("/abc/def/ghi");
			
			Assert.assertTrue( vp0.compareTo(vp1)==0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/def/ghi");
			VariablePath vp1=new VariablePath("/abc/def/{ghi}");
			
			Assert.assertTrue( vp0.compareTo(vp1)>0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/def");
			VariablePath vp1=new VariablePath("/abc/def/{ghi}");
			
			Assert.assertTrue( vp0.compareTo(vp1)<0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/{def}/{ghi}");
			VariablePath vp1=new VariablePath("/abc/def/ghi");
			
			Assert.assertTrue( vp0.compareTo(vp1)<0 );
		}
		
		{
			VariablePath vp0=new VariablePath("{ghi}/abc/def");
			VariablePath vp1=new VariablePath("/abc/def/{ghi}");
			
			Assert.assertTrue( vp0.compareTo(vp1)<0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/{def}/ghi");
			VariablePath vp1=new VariablePath("/abc/{ghi}/ghi");
			
			Assert.assertTrue( vp0.compareTo(vp1)==0 );
		}
	}
}
