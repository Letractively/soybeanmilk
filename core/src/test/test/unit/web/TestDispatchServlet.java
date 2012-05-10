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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.exe.support.DefaultResolverObjectFactory;
import org.soybeanMilk.core.exe.support.ResolverObjectFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.exe.th.AbstractTargetHandler;
import org.soybeanMilk.web.os.DefaultWebObjectSource;
import org.soybeanMilk.web.servlet.WebObjectSourceFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestDispatchServlet
{
	private static ResolverObjectFactory myExternalResolverObjectFactory=new MyResolverObjectFactory();
	
	private static String myExternalResolverKey="myExternalResolver";
	private static String myEncoding="GBK";
	private static String myExecutorKey="myExecutorKey";
	private static String mySoybeanMilkFile="test/unit/web/TestDispatchServlet.xml";
	private static String myWebObjectSourceFactoryClass="test.unit.web.TestDispatchServlet$MyWebObjectSourceFactory";
	
	private static String CONTEXT_PATH="/testContext";
	
	private MockServletContext servletContext;
	private Map<String, String> servletInitParameters;
	
	@Before
	public void setUp()
	{
		servletContext=new MockServletContext();
		servletInitParameters=new HashMap<String, String>();
		servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
	}
	
	@Test
	public void initEncoding_userSet()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, myEncoding);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(myEncoding, servlet.getEncoding());
		}
	}
	
	@Test
	public void initEncoding_userSetEmpty()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		}
	}
	
	@Test
	public void initEncoding_userNotSet()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		}
	}
	
	@Test
	public void initAppExecutorKey_userSet()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, myExecutorKey);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(myExecutorKey, servlet.getAppExecutorKey());
		}
	}
	
	@Test
	public void initAppExecutorKey_userSetEmpty()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(servlet.getAppExecutorKey());
		}
	}
	
	@Test
	public void initAppExecutorKey_userNotSet()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(servlet.getAppExecutorKey());
		}
	}
	
	@Test
	public void initWebObjectSourceFactory_userSet()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, myWebObjectSourceFactoryClass);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(MyWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initWebObjectSourceFactory_userSetEmpty()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(DefaultWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initWebObjectSourceFactory_userNotSet()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(DefaultWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initExternalResolverFactory_userSet()
	{
		{
			servletContext.setAttribute(myExternalResolverKey, myExternalResolverObjectFactory);
			servletInitParameters.put(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_OBJECT_FACTORY, myExternalResolverKey);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertTrue( ((DefaultResolverObjectFactory)servlet.getWebExecutor().getConfiguration().getResolverObjectFactory()).getExternalResolverObjectFactory() == myExternalResolverObjectFactory );
		}
	}
	
	@Test
	public void initExternalResolverFactory_userSetNull()
	{
		{
			servletContext.setAttribute(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_OBJECT_FACTORY, null);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(((DefaultResolverObjectFactory)servlet.getWebExecutor().getConfiguration().getResolverObjectFactory()).getExternalResolverObjectFactory());
		}
	}
	
	@Test
	public void initExternalResolverFactory_userNotSet()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(((DefaultResolverObjectFactory)servlet.getWebExecutor().getConfiguration().getResolverObjectFactory()).getExternalResolverObjectFactory());
		}
	}
	
	@Test
	public void getRequestExecutableName_userRequest_urlPath() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		
		request.setMethod("POST");
		request.setContextPath(CONTEXT_PATH);
		
		request.setPathInfo("/test/test");
		request.setServletPath("");
		
		servlet.service(request, response);
		
		Assert.assertEquals(TestResolver.RESULT, (String)request.getAttribute("result"));
	}
	
	@Test
	public void getRequestExecutableName_userRequest_urlSuffix() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		
		request.setMethod("POST");
		request.setContextPath(CONTEXT_PATH);
		
		request.setPathInfo("/test/test.do");
		request.setServletPath("");
		
		servlet.service(request, response);
		
		Assert.assertEquals(TestResolver.RESULT, (String)request.getAttribute("result"));
	}
	
	@Test
	public void getRequestExecutableName_include_urlPath_noServletPath() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		
		request.setMethod("POST");
		request.setContextPath(CONTEXT_PATH);
		request.setAttribute(AbstractTargetHandler.INCLUDE_PATH_INFO_ATTRIBUTE,"/test/test");
		
		servlet.service(request, response);
		
		Assert.assertEquals(TestResolver.RESULT, (String)request.getAttribute("result"));
	}
	
	@Test
	public void getRequestExecutableName_include_urlPath_withServletPath() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		
		request.setMethod("POST");
		request.setContextPath(CONTEXT_PATH);
		request.setAttribute(AbstractTargetHandler.INCLUDE_PATH_INFO_ATTRIBUTE,"/test");
		request.setAttribute(AbstractTargetHandler.INCLUDE_SERVLET_PATH_ATTRIBUTE,"/test");
		
		servlet.service(request, response);
		
		Assert.assertEquals(TestResolver.RESULT, (String)request.getAttribute("result"));
	}
	
	@Test
	public void getRequestExecutableName_endBackslash() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		MockHttpServletRequest request=new MockHttpServletRequest();
		MockHttpServletResponse response=new MockHttpServletResponse();
		
		request.setMethod("POST");
		request.setContextPath(CONTEXT_PATH);
		request.setAttribute(AbstractTargetHandler.INCLUDE_PATH_INFO_ATTRIBUTE,"/test/test/");
		
		servlet.service(request, response);
		
		Assert.assertEquals(TestResolver.RESULT, (String)request.getAttribute("result"));
	}
	
	@Test
	public void getRequestExecutableName_include_urlSuffix() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			request.setAttribute(AbstractTargetHandler.INCLUDE_SERVLET_PATH_ATTRIBUTE,"/test/test.do");
			
			servlet.service(request, response);
			
			Assert.assertEquals(TestResolver.RESULT, (String)request.getAttribute("result"));
		}
	}
	
	protected void initServlet(HttpServlet servlet)
	{
		try
		{
			servlet.init();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static class MyResolverObjectFactory implements ResolverObjectFactory
	{
		//@Override
		public Object getResolverObject(Serializable resolverId)
		{
			return null;
		}
		
		public void addResolverObject(Serializable resolverObjectId, Object resolverObject)
		{
			
		}
	}
	
	public static class MyWebObjectSourceFactory implements WebObjectSourceFactory
	{
		//@Override
		public WebObjectSource create(HttpServletRequest request,
				HttpServletResponse response, ServletContext application) 
		{
			return new MyWebObjectSource(request, response, application);
		}
	}
	
	protected static class MyWebObjectSource extends DefaultWebObjectSource
	{
		public MyWebObjectSource(HttpServletRequest request,
				HttpServletResponse response, ServletContext application)
		{
			super(request, response, application);
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
