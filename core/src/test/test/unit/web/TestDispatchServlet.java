package test.unit.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.servlet.DispatchServlet;
import org.soybeanMilk.web.servlet.WebObjectSourceFactory;
import org.springframework.mock.web.MockServletContext;


public class TestDispatchServlet
{
	private static Log log=LogFactory.getLog(TestDispatchServlet.class);
	
	private static ResolverFactory myExternalResolverFactory=new MyResolverFactory();
	
	private static String myExternalResolverKey="myExternalResolver";
	private static String myEncoding="GBK";
	private static String myExecutorKey="myExecutorKey";
	private static String mySoybeanMilkFile="test/unit/web/soybean-milk.config.xml";
	private static String myWebObjectSourceFactoryClass="test.unit.web.TestDispatchServlet$MyWebObjectSourceFactory";
	
	@Test
	public void defaultInitParameter()
	{
		MockDispathServlet servlet=new MockDispathServlet(false);
		
		try
		{
			servlet.init();
		}
		catch(Exception e)
		{
			log.error("",e);
		}
		
		Assert.assertNull(((DefaultResolverFactory)servlet.getWebExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory());
		Assert.assertEquals(WebConstants.DEFAULT_ENCODING, servlet.getEncoding());
		Assert.assertEquals(WebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
	}
	
	@Test
	public void customizedInitParameter()
	{
		MockDispathServlet servlet=new MockDispathServlet(true);
		
		try
		{
			servlet.init();
		}
		catch(Exception e)
		{
			log.error("",e);
		}
		
		Assert.assertTrue( ((DefaultResolverFactory)servlet.getWebExecutor().getConfiguration().getResolverFactory()).getExternalResolverFactory() == myExternalResolverFactory );
		Assert.assertEquals(myEncoding, servlet.getEncoding());
		Assert.assertTrue( servlet.getServletContext().getAttribute(myExecutorKey) == servlet.getWebExecutor() );
		Assert.assertEquals(MyWebObjectSource.class, servlet.getWebObjectSourceFactory().create(null, null, null).getClass());
	}
	
	public static class MockDispathServlet extends DispatchServlet
	{
		private Map<String, String> servletInitParameters=new HashMap<String, String>();
		private MockServletContext servletContext=new MockServletContext();
		public MockDispathServlet(boolean customized)
		{
			super();
			
			if(customized)
			{
				servletContext.setAttribute(myExternalResolverKey, myExternalResolverFactory);
				
				servletInitParameters.put(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY, myExecutorKey);
				servletInitParameters.put(WebConstants.ServletInitParams.ENCODING, myEncoding);
				servletInitParameters.put(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY_KEY, myExternalResolverKey);
				servletInitParameters.put(WebConstants.ServletInitParams.WEB_OBJECT_SOURCE_FACTORY_CLASS, myWebObjectSourceFactoryClass);
			}
			
			servletInitParameters.put(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG, mySoybeanMilkFile);
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
