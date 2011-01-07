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
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.os.WebObjectSourceFactory;
import org.soybeanMilk.web.servlet.DispatchServlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;


public class TestDispatchServlet
{
	private static ResolverFactory myExternalResolverFactory=new MyResolverFactory();
	
	private static String myExternalResolverKey="myExternalResolver";
	private static String myEncoding="GBK";
	private static String myExecutorKey="myExecutorKey";
	private static String mySoybeanMilkFile="test/unit/web/soybean-milk.config.xml";
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
	public void initEncoding1()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, myEncoding);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(myEncoding, servlet.getEncoding());
		}
	}
	
	@Test
	public void initEncoding2()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		}
	}
	
	@Test
	public void initEncoding3()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		}
	}
	
	@Test
	public void initAppExecutorKey1()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, myExecutorKey);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(myExecutorKey, servlet.getAppExecutorKey());
		}
	}
	
	@Test
	public void initAppExecutorKey2()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(servlet.getAppExecutorKey());
		}
	}
	
	@Test
	public void initAppExecutorKey3()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(servlet.getAppExecutorKey());
		}
	}
	
	@Test
	public void initWebObjectSourceFactory1()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, myWebObjectSourceFactoryClass);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(MyWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initWebObjectSourceFactory2()
	{
		{
			servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initWebObjectSourceFactory3()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initExternalResolverFactory1()
	{
		{
			servletContext.setAttribute(myExternalResolverKey, myExternalResolverFactory);
			servletInitParameters.put(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY_KEY, myExternalResolverKey);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertTrue( ((DefaultResolverFactory)servlet.getExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory() == myExternalResolverFactory );
		}
	}
	
	@Test
	public void initExternalResolverFactory2()
	{
		{
			servletContext.setAttribute(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY_KEY, null);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(((DefaultResolverFactory)servlet.getExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory());
		}
	}
	
	@Test
	public void initExternalResolverFactory3()
	{
		{
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(((DefaultResolverFactory)servlet.getExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory());
		}
	}
	
	@Test
	public void clientRequestExecute() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			
			request.setPathInfo("/user/edit.do");
			request.setServletPath("");
			
			request.setParameter("userId", "35");
			
			servlet.service(request, response);
			
			Assert.assertEquals("edit(35)", (String)request.getAttribute("result"));
			Assert.assertEquals(CONTEXT_PATH+"/user/view/35", response.getRedirectedUrl());
		}
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			
			request.setPathInfo("/user/edit/35");
			request.setServletPath("");
			
			servlet.service(request, response);
			
			Assert.assertEquals("edit(35)", (String)request.getAttribute("result"));
			Assert.assertEquals(CONTEXT_PATH+"/user/view/35", response.getRedirectedUrl());
		}
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			
			request.setPathInfo("/user/view/35");
			request.setServletPath("");
			
			servlet.service(request, response);
			
			Assert.assertEquals("view(35)", (String)request.getAttribute("result"));
			Assert.assertEquals("/jsp/user/35/view.jsp", response.getForwardedUrl());
		}
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			
			request.setPathInfo("/product/35/edit/233");
			request.setServletPath("");
			
			servlet.service(request, response);
			
			Assert.assertEquals("edit(35,233)", (String)request.getAttribute("result"));
			Assert.assertEquals("/jsp/product/35/233/edit.jsp", response.getForwardedUrl());
		}
	}
	
	@Test
	public void includeExecute() throws Exception
	{
		MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
		initServlet(servlet);
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			request.setAttribute(DispatchServlet.INCLUDE_PATH_INFO_ATTRIBUTE,"/product/35/edit/233");
			
			servlet.service(request, response);
			
			Assert.assertEquals("edit(35,233)", (String)request.getAttribute("result"));
			//Assert.assertEquals("/jsp/product/35/233/edit.jsp", response.getIncludedUrl());
		}
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			request.setAttribute(DispatchServlet.INCLUDE_PATH_INFO_ATTRIBUTE,"/35/edit/233");
			request.setAttribute(DispatchServlet.INCLUDE_SERVLET_PATH_ATTRIBUTE,"/product");
			
			servlet.service(request, response);
			
			Assert.assertEquals("edit(35,233)", (String)request.getAttribute("result"));
			//Assert.assertEquals("/jsp/product/35/233/edit.jsp", response.getIncludedUrl());
		}
		
		{
			MockHttpServletRequest request=new MockHttpServletRequest();
			MockHttpServletResponse response=new MockHttpServletResponse();
			
			request.setMethod("POST");
			request.setContextPath(CONTEXT_PATH);
			request.setAttribute(DispatchServlet.INCLUDE_SERVLET_PATH_ATTRIBUTE,"/product/edit.do");
			
			request.setParameter("userId", "35");
			request.setParameter("id", "233");
			
			servlet.service(request, response);
			
			Assert.assertEquals("edit(35,233)", (String)request.getAttribute("result"));
			//Assert.assertEquals("/jsp/product/35/233/edit.jsp", response.getIncludedUrl());
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
	
	public static class MyResolverFactory implements ResolverFactory
	{
		@Override
		public Object getResolver(Serializable resolverId)
		{
			return null;
		}
	}
	
	public static class MyWebObjectSourceFactory implements WebObjectSourceFactory
	{
		@Override
		public WebObjectSource create(HttpServletRequest request,
				HttpServletResponse response, ServletContext application) 
		{
			return new MyWebObjectSource(request, response, application);
		}
	}
	
	protected static class MyWebObjectSource extends WebObjectSource
	{
		public MyWebObjectSource(HttpServletRequest request,
				HttpServletResponse response, ServletContext application)
		{
			super(request, response, application);
		}
	}
	
	public static class TestResolver
	{
		public String view(int userId)
		{
			return "view("+userId+")";
		}
		
		public String edit(int userId)
		{
			return "edit("+userId+")";
		}
		
		public String edit(int userId, int productId)
		{
			return "edit("+userId+","+productId+")";
		}
	}
}
