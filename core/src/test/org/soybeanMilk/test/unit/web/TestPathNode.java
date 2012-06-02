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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.vp.PathNode;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestPathNode
{
	@Before
	public void setUp(){}
	
	@Test
	public void createNotVariable()
	{
		{
			String s="abc";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( !pn.isVariable() );
			Assert.assertEquals(s, pn.getNodeValue());
		}
		
		{
			String s="{abc";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( !pn.isVariable() );
			Assert.assertEquals(s, pn.getNodeValue());
		}
		
		{
			String s="abc}";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( !pn.isVariable() );
			Assert.assertEquals(s, pn.getNodeValue());
		}
	}
	
	@Test
	public void createIsVariable()
	{
		{
			String s="{abc}";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( pn.isVariable() );
			Assert.assertEquals("abc", pn.getNodeValue());
		}
		
		{
			String s="{}";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( pn.isVariable() );
			Assert.assertEquals("", pn.getNodeValue());
		}
	}
	
	@Test
	public void compareTo()
	{
		{
			PathNode pn0=new PathNode("abc");
			PathNode pn1=new PathNode("{def}");
			
			Assert.assertTrue( pn0.compareTo(pn1)>0 );
		}
		
		{
			PathNode pn0=new PathNode("{abc}");
			PathNode pn1=new PathNode("{def}");
			
			Assert.assertTrue( pn0.compareTo(pn1)==0 );
		}
		
		{
			PathNode pn0=new PathNode("{abc}");
			PathNode pn1=new PathNode("{}");
			
			Assert.assertTrue( pn0.compareTo(pn1)==0 );
		}
		
		{
			PathNode pn0=new PathNode("{abc}");
			PathNode pn1=new PathNode("def");
			
			Assert.assertTrue( pn0.compareTo(pn1)<0 );
		}
	}
}
