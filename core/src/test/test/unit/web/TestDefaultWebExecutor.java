package test.unit.web;


import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.DefaultWebExecutor;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.th.AbstractTargetHandler;
import org.soybeanMilk.web.os.WebObjectSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

public class TestDefaultWebExecutor
{
	private DefaultWebExecutor webExecutor;
	
	@Before
	public void setUp() throws Exception
	{
		WebConfigurationParser parser=new WebConfigurationParser(null);
		parser.parse("test/unit/web/TestDefaultWebExecutor.xml");
		
		WebConfiguration webConfiguration= parser.getWebConfiguration();
		
		webExecutor=new DefaultWebExecutor(webConfiguration);
	}

	@After
	public void tearDown() throws Exception
	{
		webExecutor=null;
	}

	@Test
	public void testExecute_forwardTarget() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe0", os);
		
		Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
	}
	
	@Test
	public void testExecute_redirectTarget() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe1", os);
		
		Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
	}

	@Test
	public void testExecute_customizedTarget() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe2", os);
		
		Assert.assertEquals(JsonTargetHandler.jsonHeader, ((MockHttpServletResponse)os.getResponse()).getContentType());
		Assert.assertNull(((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
		Assert.assertNull(((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
	}
	
	@Test
	public void testExecute_targetTypeIgnoreCase() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("exe3", os);
		
		Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
	}
	
	@Test
	public void testExecute_restful() throws Exception
	{
		WebObjectSource os=createWebObjectSource();
		webExecutor.execute("test/aaa/1/bbb", os);
		
		Assert.assertEquals("aaa", os.getRequest().getAttribute("v0"));
		Assert.assertEquals("bbb", os.getRequest().getAttribute("v1"));
		Assert.assertEquals("test/aaa/1/bbb.jsp", ((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
	}
	
	@Test
	public void testExecute_typeVariable() throws Exception
	{
		
		Class<TestResolver> tc=TestResolver.class;
		Type[] GenericInterfaces=tc.getGenericInterfaces();
		Type GenericSuperclass=tc.getGenericSuperclass();
		
		WebObjectSource os=createWebObjectSource();
		MockHttpServletRequest request=(MockHttpServletRequest)os.getRequest();
		
		request.setParameter("typeVariable.id", "id");
		request.setParameter("typeVariable.name", "name");
		
		try
		{
			webExecutor.execute("typeVariableTest", os);
			//TestBean result=(TestBean)os.get("request.testResult", null);
		}
		catch(Exception e)
		{
			Assert.assertTrue( e.getMessage().endsWith("is not supported type") );
		}
	}
	
	protected WebObjectSource createWebObjectSource()
	{
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		MockServletContext application=new MockServletContext();
		
		WebObjectSource os=new WebObjectSource(request, response, application);
		
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
	}
	
	public static abstract class AbstractResolverForTest<T extends TestBean> implements ResolverForTestInterface<T>
	{
		public T typeVariableTest(T param)
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
