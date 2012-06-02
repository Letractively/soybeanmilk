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


import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.th.AbstractTargetHandler;
import org.soybeanMilk.web.os.DefaultWebObjectSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestAbstractTargetHandler
{
	private WebConfiguration webConfiguration;
	AbstractTargetHandler abstractTargetHandler;
	
	@Before
	public void setUp() throws Exception
	{
		WebConfigurationParser parser=new WebConfigurationParser(null);
		parser.parse("org/soybeanMilk/test/unit/web/TestAbstractTargetHandler.xml");
		
		webConfiguration= parser.getWebConfiguration();
		
		abstractTargetHandler=new MockAbstractTargetHandler();
	}

	@After
	public void tearDown() throws Exception
	{
		abstractTargetHandler=null;
		webConfiguration=null;
	}
	
	@Test
	public void getAllInvokeResultKey() throws Exception
	{
		String[] expected={"result0", "request.result1", "session.result2", "application.result3", "request.result5", "request.result4"};
		WebAction webAction=(WebAction)webConfiguration.getExecutable("exe1");
		
		String[] re=abstractTargetHandler.getAllResultKeys(webAction);
		
		for(int i=0;i<expected.length;i++)
		{
			Assert.assertEquals(expected[i], re[i]);
		}
	}
	
	@Test
	public void getActualTargetUrl() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		WebAction webAction=(WebAction)webConfiguration.getExecutable("exe3");
		
		os.set("request.v0", "v0");
		os.set("request.v1", "v1");
		
		Assert.assertEquals("v0/v1/aaa/null/bbb.jsp", abstractTargetHandler.getActualTargetUrl(webAction, os));
		
		((MockHttpServletRequest)os.getRequest()).setParameter("v2", "v2");
		
		Assert.assertEquals("v0/v1/aaa/v2/bbb.jsp", abstractTargetHandler.getActualTargetUrl(webAction, os));
	}
	
	private WebObjectSource createWebObjectSource()
	{
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		MockServletContext application=new MockServletContext();
		
		WebObjectSource os=new DefaultWebObjectSource(request, response, application, new WebGenericConverter());
		
		return os;
	}
	
	protected static class MockAbstractTargetHandler extends AbstractTargetHandler
	{
		@Override
		public void handleTarget(WebAction webAction,
				WebObjectSource webObjectSource) throws ServletException,
				IOException
		{
			return;
		}
	}
	
	public static class TestResolver
	{
		public static final String RESULT="success";
		
		public String test()
		{
			return RESULT;
		}
	}
}
