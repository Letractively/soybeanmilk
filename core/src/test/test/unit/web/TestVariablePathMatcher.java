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

package test.unit.web;


import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.vp.VariablePath;
import org.soybeanMilk.web.vp.VariablePathMatcher;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestVariablePathMatcher
{
	private VariablePathMatcher matcher;
	
	@Before
	public void setUp()
	{
		Collection<String> src=new ArrayList<String>();
		src.add("/abc/def/ghi");
		src.add("/{abc}/def/ghi");
		src.add("/{abc}/def/{ghi}");
		src.add("/{abc}/{def}/ghi");
		src.add("/abc");
		
		matcher=new VariablePathMatcher(src);
	}
	
	@Test
	public void create()
	{
		{
			VariablePath[] vps=matcher.getVariablePaths();
			
			int idx=0;
			Assert.assertEquals("/abc", vps[idx++].toString());
			Assert.assertEquals("/{abc}/{def}/ghi", vps[idx++].toString());
			Assert.assertEquals("/{abc}/def/{ghi}", vps[idx++].toString());
			Assert.assertEquals("/{abc}/def/ghi", vps[idx++].toString());
			Assert.assertEquals("/abc/def/ghi", vps[idx++].toString());
		}
	}
	
	@Test
	public void getMatched()
	{
		//-----------------------
		// null
		{
			VariablePath matched=matcher.getMatched((String)null);
			Assert.assertNull(matched);
		}
		{
			VariablePath matched=matcher.getMatched((VariablePath)null);
			Assert.assertNull(matched);
		}
		{
			VariablePath matched=matcher.getMatched("/abc/def");
			Assert.assertNull(matched);
		}
		{
			VariablePath matched=matcher.getMatched("/abc/{def}");
			Assert.assertNull(matched);
		}
		{
			VariablePath matched=matcher.getMatched("/abc/def/ghi/sdf");
			Assert.assertNull(matched);
		}
		
		//-----------------------
		// "/abc"
		{
			VariablePath matched=matcher.getMatched("/abc");
			Assert.assertEquals("/abc",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/{abc}");
			Assert.assertNull(matched);
		}
		
		//-----------------------
		// "/{abc}/{def}/ghi"
		{
			VariablePath matched=matcher.getMatched("/{abc}/{def}/ghi");
			Assert.assertEquals("/{abc}/{def}/ghi",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/{3333}/{5555}/ghi");
			Assert.assertEquals("/{abc}/{def}/ghi",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/view/001/ghi");
			Assert.assertEquals("/{abc}/{def}/ghi",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/edit/001/ghi");
			Assert.assertEquals("/{abc}/{def}/ghi",matched.toString());
		}
		
		//-----------------------
		// "/{abc}/def/{ghi}"
		{
			VariablePath matched=matcher.getMatched("/{abc}/def/{ghi}");
			Assert.assertEquals("/{abc}/def/{ghi}",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/{ab342bc}/def/{gbsdhi}");
			Assert.assertEquals("/{abc}/def/{ghi}",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/query/def/0035");
			Assert.assertEquals("/{abc}/def/{ghi}",matched.toString());
		}
		
		//-----------------------
		// "/{abc}/def/ghi"
		{
			VariablePath matched=matcher.getMatched("/{abc}/def/ghi");
			Assert.assertEquals("/{abc}/def/ghi",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/{abdfewc}/def/ghi");
			Assert.assertEquals("/{abc}/def/ghi",matched.toString());
		}
		{
			VariablePath matched=matcher.getMatched("/hahaha003/def/ghi");
			Assert.assertEquals("/{abc}/def/ghi",matched.toString());
		}
		
		//-----------------------
		// "/abc/def/ghi"
		{
			VariablePath matched=matcher.getMatched("/abc/def/ghi");
			Assert.assertEquals("/abc/def/ghi",matched.toString());
		}
	}
}
