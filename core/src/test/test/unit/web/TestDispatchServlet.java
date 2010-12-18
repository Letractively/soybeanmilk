package test.unit.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.os.PathWebObjectSource;
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.servlet.DispatchServlet;
import org.soybeanMilk.web.servlet.WebObjectSourceFactory;
import org.springframework.mock.web.MockServletContext;


public class TestDispatchServlet
{
	private static ResolverFactory myExternalResolverFactory=new MyResolverFactory();
	
	private static String myExternalResolverKey="myExternalResolver";
	private static String myEncoding="GBK";
	private static String myExecutorKey="myExecutorKey";
	private static String mySoybeanMilkFile="test/unit/web/soybean-milk.config.xml";
	private static String myWebObjectSourceFactoryClass="test.unit.web.TestDispatchServlet$MyWebObjectSourceFactory";
	
	@Test
	public void initEncoding()
	{
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, myEncoding);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(myEncoding, servlet.getEncoding());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		}
	}
	
	@Test
	public void initAppExecutorKey()
	{
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, myExecutorKey);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(myExecutorKey, servlet.getAppExecutorKey());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(servlet.getAppExecutorKey());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(servlet.getAppExecutorKey());
		}
	}
	

	@Test
	public void initWebObjectSourceFactory()
	{
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, myWebObjectSourceFactoryClass);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(MyWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, "");
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(PathWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertEquals(PathWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
		}
	}
	
	@Test
	public void initExternalResolverFactory()
	{
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletContext.setAttribute(myExternalResolverKey, myExternalResolverFactory);
			servletInitParameters.put(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY_KEY, myExternalResolverKey);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertTrue( ((DefaultResolverFactory)servlet.getExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory() == myExternalResolverFactory );
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			servletContext.setAttribute(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY_KEY, null);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(((DefaultResolverFactory)servlet.getExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory());
		}
		
		{
			MockServletContext servletContext=new MockServletContext();
			Map<String, String> servletInitParameters=new HashMap<String, String>();
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
			
			MockDispathServlet servlet=new MockDispathServlet(servletContext, servletInitParameters);
			initServlet(servlet);
			
			Assert.assertNull(((DefaultResolverFactory)servlet.getExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory());
		}
	}
	
	public static class MockDispathServlet extends DispatchServlet
	{
		private static final long serialVersionUID = 1L;
		
		private MockServletContext servletContext;
		private Map<String, String> servletInitParameters;
		
		public MockDispathServlet(MockServletContext servletContext, Map<String, String> servletInitParameters)
		{
			super();
			
			this.servletContext=servletContext;
			this.servletInitParameters=servletInitParameters;
		}
		
		@Override
		public String getInitParameter(String name)
		{
			return servletInitParameters.get(name);
		}

		@Override
		public ServletContext getServletContext()
		{
			return servletContext;
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
		}}
}
