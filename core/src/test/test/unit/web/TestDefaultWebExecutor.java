package test.unit.web;


import java.io.IOException;

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
	public void testExecute() throws Exception
	{
		{
			WebObjectSource os=createWebObjectSource();
			webExecutor.execute("exe0", os);
			
			Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
		}
		
		{
			WebObjectSource os=createWebObjectSource();
			webExecutor.execute("exe1", os);
			
			Assert.assertEquals("url.jsp", ((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
		}
		
		{
			WebObjectSource os=createWebObjectSource();
			webExecutor.execute("exe2", os);
			
			Assert.assertEquals(JsonTargetHandler.jsonHeader, ((MockHttpServletResponse)os.getResponse()).getContentType());
			
			Assert.assertNull(((MockHttpServletResponse)os.getResponse()).getRedirectedUrl());
			Assert.assertNull(((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
		}
		
		{
			WebObjectSource os=createWebObjectSource();
			webExecutor.execute("test/aaa/1/bbb", os);
			
			Assert.assertEquals("test/aaa/1/bbb.jsp", ((MockHttpServletResponse)os.getResponse()).getForwardedUrl());
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
		public static String jsonResult="{}";
		
		public String jsonTarget;
		
		@Override
		public void handleTarget(WebAction webAction,
				WebObjectSource webObjectSource) throws ServletException,
				IOException
		{
			webObjectSource.getResponse().setContentType(jsonHeader);
			
			jsonTarget="{}";
		}
	}
}
