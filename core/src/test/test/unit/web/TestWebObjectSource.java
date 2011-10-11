package test.unit.web;

import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.web.bean.ParamConvertException;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.os.WebObjectSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import test.unit.web.TestWebGenericConverter.JavaBean;

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
			Object dest=webObjectSource.get("request", HttpServletRequest.class);
			Assert.assertTrue(request == dest);
		}
		{
			Object dest=webObjectSource.get("request", null);
			Assert.assertTrue(request == dest);
		}
		
		//response
		{
			Object dest=webObjectSource.get("response", HttpServletResponse.class);
			Assert.assertTrue(response == dest);
		}
		{
			Object dest=webObjectSource.get("response", null);
			Assert.assertTrue(response == dest);
		}
		
		//session
		{
			Object dest=webObjectSource.get("session", HttpSession.class);
			Assert.assertTrue(request.getSession() == dest);
		}
		{
			Object dest=webObjectSource.get("session", null);
			Assert.assertTrue(request.getSession() == dest);
		}
		
		//application
		{
			Object dest=webObjectSource.get("application", ServletContext.class);
			Assert.assertTrue(application == dest);
		}
		{
			Object dest=webObjectSource.get("application", null);
			Assert.assertTrue(application == dest);
		}
		
		//objectSource
		{
			Object dest=webObjectSource.get("objectSource", WebObjectSource.class);
			Assert.assertTrue(webObjectSource == dest);
		}
		{
			Object dest=webObjectSource.get("objectSource", null);
			Assert.assertTrue(webObjectSource == dest);
		}
	}
	
	@Test
	public void convertServletObject()
	{
		final JavaBean staticJavaBean=new JavaBean();
		
		GenericConverter genericConverter=webObjectSource.getGenericConverter();
		
		Converter converter=new Converter()
		{
			//@Override
			public Object convert(Object sourceObj, Type targetClass) 
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
			Exception re=null;
			
			try
			{
				webObjectSource.get("request", JavaBean.class);
			}
			catch(Exception e)
			{
				re=e;
			}
			
			Assert.assertTrue( re.getMessage().startsWith(exceptionPrefix) );
		}
		{
			Exception re=null;
			
			try
			{
				webObjectSource.get("session", JavaBean.class);
			}
			catch(Exception e)
			{
				re=e;
			}
			
			Assert.assertTrue( re.getMessage().startsWith(exceptionPrefix) );
		}
		{
			Exception re=null;
			
			try
			{
				webObjectSource.get("response", JavaBean.class);
			}
			catch(Exception e)
			{
				re=e;
			}
			
			Assert.assertTrue( re.getMessage().startsWith(exceptionPrefix) );
		}
		{
			Exception re=null;
			
			try
			{
				webObjectSource.get("application", JavaBean.class);
			}
			catch(Exception e)
			{
				re=e;
			}
			
			Assert.assertTrue( re.getMessage().startsWith(exceptionPrefix) );
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void getRawRequestParameterMap()
	{
		String value="12345";
		request.setParameter("value", value);
		
		Map src=request.getParameterMap();
		Map dest=(Map)webObjectSource.get("param", Map.class);
		
		Assert.assertEquals(src.get(value), dest.get(value));
		Assert.assertEquals(src, dest);
	}
	
	@Test
	public void setAndGetFromParam()
	{
		String value="12345";
		
		{
			request.setParameter("value", value);
			Assert.assertEquals(value, webObjectSource.get("param.value", String.class));
		}
		{
			request.setParameter("my.set.value", value);
			Assert.assertEquals(value, webObjectSource.get("param.my.set.value", String.class));
		}
		{
			request.setParameter("id", new String[]{"1"});
			request.setParameter("name", new String[]{"jack"});
			request.setParameter("yourBean.id", new String[]{"2"});
			request.setParameter("yourBean.name", new String[]{"tom"});
			
			MyBean dest=(MyBean)webObjectSource.get("param", MyBean.class);
			
			Assert.assertEquals(1, dest.getId().intValue());
			Assert.assertEquals("jack", dest.getName());
			Assert.assertEquals(2, dest.getYourBean().getId().intValue());
			Assert.assertEquals("tom", dest.getYourBean().getName());
		}
		
		{
			request.setParameter("my.myBean.id", new String[]{"1"});
			request.setParameter("my.myBean.name", new String[]{"jack"});
			request.setParameter("my.myBean.yourBean.id", new String[]{"2"});
			request.setParameter("my.myBean.yourBean.name", new String[]{"tom"});
			
			MyBean dest=(MyBean)webObjectSource.get("param.my.myBean", MyBean.class);
			
			Assert.assertEquals(1, dest.getId().intValue());
			Assert.assertEquals("jack", dest.getName());
			Assert.assertEquals(2, dest.getYourBean().getId().intValue());
			Assert.assertEquals("tom", dest.getYourBean().getName());
		}

		{
			request.setParameter("my.myBean.id", new String[]{"1"});
			request.setParameter("my.myBean.name", new String[]{"jack"});
			request.setParameter("my.myBean.yourBean.id", new String[]{"2"});
			request.setParameter("my.myBean.yourBean.name", new String[]{"tom"});
			
			MyBean dest=(MyBean)webObjectSource.get("param.my.myBean", MyBean.class);
			
			Assert.assertEquals(1, dest.getId().intValue());
			Assert.assertEquals("jack", dest.getName());
			Assert.assertEquals(2, dest.getYourBean().getId().intValue());
			Assert.assertEquals("tom", dest.getYourBean().getName());
		}
	}
	
	@Test
	public void getFromParamWithInvalidParamValue() throws Exception
	{
		String value="invalidValue";
		
		{
			request.setParameter("value", value);
			
			ParamConvertException re=null;
			try
			{
				webObjectSource.get("param.value", int.class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("value", re.getParamName());
			Assert.assertEquals(value, re.getSourceObject());
			Assert.assertEquals(int.class, re.getTargetType());
		}
		
		{
			request.setParameter("my.set.value", value);
			
			ParamConvertException re=null;
			try
			{
				webObjectSource.get("param.my.set.value", Boolean.class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("my.set.value", re.getParamName());
			Assert.assertEquals(value, re.getSourceObject());
			Assert.assertEquals(Boolean.class, re.getTargetType());
		}
		
		{
			request.setParameter("id", new String[]{"1"});
			request.setParameter("name", new String[]{"jack"});
			request.setParameter("yourBean.id", new String[]{value});
			request.setParameter("yourBean.name", new String[]{"tom"});
			
			ParamConvertException re=null;
			try
			{
				webObjectSource.get("param", MyBean.class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			Assert.assertEquals("yourBean.id", re.getParamName());
			Assert.assertEquals(value, re.getSourceObject());
			Assert.assertEquals(Integer.class, re.getTargetType());
		}
		
		{
			request.setParameter("my.myBean.id", new String[]{"1"});
			request.setParameter("my.myBean.name", new String[]{"jack"});
			request.setParameter("my.myBean.yourBean.id", new String[]{value});
			request.setParameter("my.myBean.yourBean.name", new String[]{"tom"});
			
			ParamConvertException re=null;
			try
			{
				webObjectSource.get("param.my.myBean", MyBean.class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("my.myBean.yourBean.id", re.getParamName());
			Assert.assertEquals(value, re.getSourceObject());
			Assert.assertEquals(Integer.class, re.getTargetType());
		}
	}
	
	@Test
	public void getFromParamWithNotExistParam()
	{
		webObjectSource.get("param.noValue", Integer.class);
	}
	
	@Test
	public void setAndGetFromRequest()
	{
		String value="12345";
		
		{
			webObjectSource.set("value", value);
			Assert.assertEquals(request.getAttribute("value"), value);
			
			Object dest=webObjectSource.get("request.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			webObjectSource.set("request", value);
			Assert.assertEquals(request.getAttribute("request"), value);
			
			Object dest=webObjectSource.get("request.request", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			webObjectSource.set("request.value", value);
			Assert.assertEquals(request.getAttribute("value"), value);
			
			Object dest=webObjectSource.get("request.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			webObjectSource.set("request.my.value", value);
			Assert.assertEquals(request.getAttribute("my.value"), value);
			
			Object dest=webObjectSource.get("request.my.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			MyBean myBean=new MyBean();
			
			webObjectSource.set("request.myBean",myBean);
			Assert.assertTrue( request.getAttribute("myBean")==myBean );
			Assert.assertTrue( webObjectSource.get("request.myBean", null)==myBean );
			
			webObjectSource.set("request.myBean.id", 7);
			Assert.assertEquals(7, myBean.getId().intValue());
			Assert.assertEquals(7, webObjectSource.get("request.myBean.id", null));
			
			webObjectSource.set("request.myBean.name", "name");
			Assert.assertEquals("name", myBean.getName());
			Assert.assertEquals("name", webObjectSource.get("request.myBean.name", null));
			
			webObjectSource.set("request.myBean.yourBean.id", 8);
			Assert.assertEquals(8, myBean.getYourBean().getId().intValue());
			Assert.assertEquals(8, webObjectSource.get("request.myBean.yourBean.id", null));
		}
	}
	
	@Test
	public void setAndGetFromSession()
	{
		String value="12345";
		
		{
			webObjectSource.set("session.value", value);
			Assert.assertEquals(request.getSession().getAttribute("value"), value);
			
			Object dest=webObjectSource.get("session.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			webObjectSource.set("session.my.value", value);
			Assert.assertEquals(request.getSession().getAttribute("my.value"), value);
			
			Object dest=webObjectSource.get("session.my.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			MyBean myBean=new MyBean();
			
			webObjectSource.set("session.myBean",myBean);
			Assert.assertTrue( request.getSession().getAttribute("myBean")==myBean );
			Assert.assertTrue( webObjectSource.get("session.myBean", null)==myBean );
			
			webObjectSource.set("session.myBean.id", 7);
			Assert.assertEquals(7, myBean.getId().intValue());
			Assert.assertEquals(7, webObjectSource.get("session.myBean.id", null));
			
			webObjectSource.set("session.myBean.name", "name");
			Assert.assertEquals("name", myBean.getName());
			Assert.assertEquals("name", webObjectSource.get("session.myBean.name", null));
			
			webObjectSource.set("session.myBean.yourBean.id", 8);
			Assert.assertEquals(8, myBean.getYourBean().getId().intValue());
			Assert.assertEquals(8, webObjectSource.get("session.myBean.yourBean.id", null));
		}
	}
	
	@Test
	public void setAndGetFromApplication()
	{
		String value="12345";
		
		{
			webObjectSource.set("application.value", value);
			Assert.assertEquals(application.getAttribute("value"), value);
			
			Object dest=webObjectSource.get("application.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			webObjectSource.set("application.my.value", value);
			Assert.assertEquals(application.getAttribute("my.value"), value);
			
			Object dest=webObjectSource.get("application.my.value", String.class);
			Assert.assertEquals(value, dest);
		}
		{
			MyBean myBean=new MyBean();
			
			webObjectSource.set("application.myBean",myBean);
			Assert.assertTrue( application.getAttribute("myBean")==myBean );
			Assert.assertTrue( webObjectSource.get("application.myBean", null)==myBean );
			
			webObjectSource.set("application.myBean.id", 7);
			Assert.assertEquals(7, myBean.getId().intValue());
			Assert.assertEquals(7, webObjectSource.get("application.myBean.id", null));
			
			webObjectSource.set("application.myBean.name", "name");
			Assert.assertEquals("name", myBean.getName());
			Assert.assertEquals("name", webObjectSource.get("application.myBean.name", null));
			
			webObjectSource.set("application.myBean.yourBean.id", 8);
			Assert.assertEquals(8, myBean.getYourBean().getId().intValue());
			Assert.assertEquals(8, webObjectSource.get("application.myBean.yourBean.id", null));
		}
	}
	
	@Test
	public void setAndGetWithNotScopedKey()
	{
		String value="12345";
		
		//默认应该保存到request中
		{
			webObjectSource.set("value", value);
			Assert.assertEquals(value, request.getAttribute("value"));
			
			Object dest=webObjectSource.get("request.value", String.class);
			Assert.assertEquals(value, dest);
		}
		
		//默认应该从param取
		{
			request.setParameter("value", value);
			
			Object dest0=webObjectSource.get("value", String.class);
			Assert.assertEquals(value, dest0);
		}
	}
	
	@Test
	public void setAndGetForUnknownScope()
	{
		String value="12345";
		
		{
			Exception re=null;
			
			try
			{
				webObjectSource.get("unknown.value", null);
			}
			catch(ObjectSourceException e)
			{
				re=e;
			}
			
			Assert.assertEquals("key 'unknown.value' is invalid, get object from scope 'unknown' is not supported", re.getMessage());
		}
		
		{
			Exception re=null;
			
			try
			{
				webObjectSource.set("unknown.value", value);
			}
			catch(ObjectSourceException e)
			{
				re=e;
			}
			
			Assert.assertEquals("key 'unknown.value' is invalid, set object into scope 'unknown' is not supported", re.getMessage());
		}
	}
	
	public static class MyBean
	{
		private Integer id;
		private String name;
		private YourBean yourBean;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public YourBean getYourBean() {
			return yourBean;
		}
		public void setYourBean(YourBean yourBean) {
			this.yourBean = yourBean;
		}
	}
	
	public static class YourBean
	{
		private Integer id;
		private String name;
		private MyBean myBean;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public MyBean getMyBean() {
			return myBean;
		}
		public void setMyBean(MyBean myBean) {
			this.myBean = myBean;
		}
	}
}
