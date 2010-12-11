package unit.web;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.os.WebObjectSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import unit.web.TestWebGenericConverter.JavaBean;


public class TestWebObjectSource
{
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockServletContext application;
	private WebObjectSource webObjectSource;
	
	@Before
	public void setUp()
	{
		request=new MockHttpServletRequest();
		response=new MockHttpServletResponse();
		application=new MockServletContext();
		
		webObjectSource=new WebObjectSource(request, response, application, new WebGenericConverter());
	}
	
	@Test
	public void getServletObject()
	{
		//request
		{
			Object dest=webObjectSource.get(null, HttpServletRequest.class);
			Assert.assertTrue(request == dest);
		}
		{
			Object dest=webObjectSource.get("request", HttpServletRequest.class);
			Assert.assertTrue(request == dest);
		}
		
		//response
		{
			Object dest=webObjectSource.get(null, HttpServletResponse.class);
			Assert.assertTrue(response == dest);
		}
		{
			Object dest=webObjectSource.get("response", HttpServletResponse.class);
			Assert.assertTrue(response == dest);
		}
		
		//session
		{
			Object dest=webObjectSource.get(null, HttpSession.class);
			Assert.assertTrue(request.getSession() == dest);
		}
		{
			Object dest=webObjectSource.get("session", HttpSession.class);
			Assert.assertTrue(request.getSession() == dest);
		}
		
		//application
		{
			Object dest=webObjectSource.get(null, ServletContext.class);
			Assert.assertTrue(application == dest);
		}
		{
			Object dest=webObjectSource.get("application", ServletContext.class);
			Assert.assertTrue(application == dest);
		}
	}
	
	@Test
	public void convertServletObject()
	{
		final JavaBean staticJavaBean=new JavaBean();
		
		GenericConverter genericConverter=webObjectSource.getGenericConverter();
		
		Converter converter=new Converter()
		{
			@Override
			public Object convert(Object sourceObj, Class<?> targetClass) 
			{
				return staticJavaBean;
			}
		};
		
		{
			genericConverter.addConverter(HttpServletRequest.class, JavaBean.class, converter);
			Object dest=webObjectSource.get("request", JavaBean.class);
			Assert.assertTrue( dest == staticJavaBean);
		}
		
		{
			genericConverter.addConverter(HttpSession.class, JavaBean.class, converter);
			Object dest=webObjectSource.get("session", JavaBean.class);
			Assert.assertTrue( dest == staticJavaBean);
		}
		
		{
			genericConverter.addConverter(HttpServletResponse.class, JavaBean.class, converter);
			Object dest=webObjectSource.get("response", JavaBean.class);
			Assert.assertTrue( dest == staticJavaBean);
		}
		
		{
			genericConverter.addConverter(ServletContext.class, JavaBean.class, converter);
			Object dest=webObjectSource.get("application", JavaBean.class);
			Assert.assertTrue( dest == staticJavaBean);
		}
	}
	
	@Test
	public void convertServletObjectThrow()
	{
		String exceptionPrefix="no Converter defined for converting";
		
		{
			try
			{
				webObjectSource.get("request", JavaBean.class);
			}
			catch(Exception e)
			{
				Assert.assertTrue( e.getMessage().startsWith(exceptionPrefix) );
			}
		}
		
		{
			try
			{
				webObjectSource.get("session", JavaBean.class);
			}
			catch(Exception e)
			{
				Assert.assertTrue( e.getMessage().startsWith(exceptionPrefix) );
			}
		}
		
		{
			try
			{
				webObjectSource.get("response", JavaBean.class);
			}
			catch(Exception e)
			{
				Assert.assertTrue( e.getMessage().startsWith(exceptionPrefix) );
			}
		}
		
		{
			try
			{
				webObjectSource.get("application", JavaBean.class);
			}
			catch(Exception e)
			{
				Assert.assertTrue( e.getMessage().startsWith(exceptionPrefix) );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getRawRequestParameterMap()
	{
		String value="12345";
		request.setParameter("value", value);
		
		Map dest=(Map)webObjectSource.get("param", Map.class);
		
		Assert.assertEquals(request.getParameterMap().get("value"), dest.get("value"));
	}
	
	@Test
	public void setAndGetFromParam()
	{
		String value="12345";
		
		request.setParameter("value", value);
		
		//默认从param中取
		Object dest0=webObjectSource.get("value", String.class);
		Assert.assertEquals(value, dest0);
		
		Object dest1=webObjectSource.get("param.value", String.class);
		Assert.assertEquals(value, dest1);
	}
	
	@Test
	public void setAndGetFromRequest()
	{
		String value="12345";
		
		{
			//默认设置到request中
			webObjectSource.set("value", value);
			Assert.assertEquals(request.getAttribute("value"), value);
			
			Object dest=webObjectSource.get("request.value", String.class);
			Assert.assertEquals(value, dest);
		}
		
		{
			webObjectSource.set("request.value", value);
			Assert.assertEquals(request.getAttribute("value"), value);
			
			Object dest=webObjectSource.get("request.value", String.class);
			Assert.assertEquals(value, dest);
		}
	}
	
	@Test
	public void setAndGetFromSession()
	{
		String value="12345";
		
		webObjectSource.set("session.value", value);
		Assert.assertEquals(request.getSession().getAttribute("value"), value);
		
		Object dest=webObjectSource.get("session.value", String.class);
		Assert.assertEquals(value, dest);
	}
	
	@Test
	public void setAndGetFromApplication()
	{
		String value="12345";
		
		webObjectSource.set("application.value", value);
		Assert.assertEquals(application.getAttribute("value"), value);
		
		Object dest=webObjectSource.get("application.value", String.class);
		Assert.assertEquals(value, dest);
	}
}
