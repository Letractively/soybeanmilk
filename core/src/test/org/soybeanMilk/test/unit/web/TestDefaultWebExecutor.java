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
import java.util.List;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.DefaultWebExecutor;
import org.soybeanMilk.web.WebObjectSource;
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
public class TestDefaultWebExecutor
{
	private DefaultWebExecutor webExecutor;
	
	@Before
	public void setUp() throws Exception
	{
		WebConfigurationParser parser=new WebConfigurationParser(null);
		parser.parse("org/soybeanMilk/test/unit/web/TestDefaultWebExecutor.xml");
		
		WebConfiguration webConfiguration= parser.getWebConfiguration();
		
		webExecutor=new DefaultWebExecutor(webConfiguration);
	}

	@After
	public void tearDown() throws Exception
	{
		webExecutor=null;
	}

	@Test
	public void execute_forwardTarget() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe0", os);
		
		Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
	}
	
	@Test
	public void execute_redirectTarget() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe1", os);
		
		Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
	}

	@Test
	public void execute_customizedTarget() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe2", os);
		
		Assert.assertEquals(JsonTargetHandler.jsonHeader, ((MockHttpServletResponse)os.getResponse()).getContentType());
		Assert.assertNull(((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
		Assert.assertNull(((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
	}
	
	@Test
	public void execute_targetTypeIgnoreCase() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe3", os);
		
		Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
	}
	
	@Test
	public void execute_restful() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("test/aaa/1/bbb", os);
		
		Assert.assertEquals("aaa", os.getRequest().getAttribute("v0"));
		Assert.assertEquals("bbb", os.getRequest().getAttribute("v1"));
		Assert.assertEquals("test/aaa/1/bbb.jsp", ((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
	}
	
	@Test
	public void execute_resolverClass_typeVariable() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		MockHttpServletRequest request=(MockHttpServletRequest)os.getRequest();
		
		String id="my id";
		String name="my name";
		
		request.setParameter("typeVariable.id", id);
		request.setParameter("typeVariable.name", name);
		
		webExecutor.execute("typeVariableTest", os);
		
		TestBeanSub result=os.get("request.testResult");
		
		Assert.assertEquals(result.getId(), id);
		Assert.assertEquals(result.getName(), name);
	}
	
	@Test
	public void execute_resolverClass_genericArray() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		MockHttpServletRequest request=(MockHttpServletRequest)os.getRequest();
		
		String[] id={"id-0", "id-1","id-2"};
		String[] name={"name-0", "name-1", "name-2"};
		
		request.setParameter("genericArray.id", id);
		request.setParameter("genericArray.name", name);
		
		webExecutor.execute("genericArrayTest", os);
		
		TestBeanSub[] result=os.get("request.testResult");
		
		Assert.assertEquals(id.length, result.length);
		
		for(int i=0; i<result.length; i++)
		{
			TestBeanSub tbs=result[i];
			
			Assert.assertEquals(id[i], tbs.getId());
			Assert.assertEquals(name[i], tbs.getName());
		}
	}
	
	@Test
	public void execute_resolverClass_parameterizedType() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		MockHttpServletRequest request=(MockHttpServletRequest)os.getRequest();
		
		String[] id={"id-0", "id-1","id-2"};
		String[] name={"name-0", "name-1", "name-2"};
		
		request.setParameter("parameterizedType.id", id);
		request.setParameter("parameterizedType.name", name);
		
		webExecutor.execute("parameterizedTypeTest", os);
		
		List<TestBeanSub> result=os.get("request.testResult");
		
		Assert.assertEquals(id.length, result.size());
		
		for(int i=0; i<result.size(); i++)
		{
			TestBeanSub tbs=result.get(i);
			
			Assert.assertEquals(id[i], tbs.getId());
			Assert.assertEquals(name[i], tbs.getName());
		}
	}
	
	
	private WebObjectSource createWebObjectSource()
	{
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		MockServletContext application=new MockServletContext();
		
		WebObjectSource os=new DefaultWebObjectSource(request, response, application);
		
		return os;
	}
	
	public static class JsonTargetHandler extends AbstractTargetHandler
	{
		public static String jsonHeader="text/json";
		
		@Override
		public void handleTarget(WebAction webAction,
				WebObjectSource webObjectSource) throws ServletException,
				IOException
		{
			webObjectSource.getResponse().setContentType(jsonHeader);
		}
	}
	
	public static interface ResolverForTestInterface<T extends TestBean>
	{
		T typeVariableTest(T param);
		
		T[] genericArrayTest(T[] param);
		
		List<T> parameterizedTypeTest(List<T> param);
	}
	
	public static abstract class AbstractResolverForTest<T extends TestBean> implements ResolverForTestInterface<T>
	{
		public T typeVariableTest(T param)
		{
			return param;
		}

		public T[] genericArrayTest(T[] param)
		{
			return param;
		}

		public List<T> parameterizedTypeTest(List<T> param)
		{
			return param;
		}
	}
	
	public static class TestResolver extends AbstractResolverForTest<TestBeanSub>
	{
		public static final String RESULT="success";
		
		public String test()
		{
			return RESULT;
		}
	}
	
	public static class TestBean
	{
		private String id;
		private String name;
		
		public TestBean()
		{
			super();
		}
		
		public TestBean(String id, String name)
		{
			super();
			this.id = id;
			this.name = name;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class TestBeanSub extends TestBean{}
}
