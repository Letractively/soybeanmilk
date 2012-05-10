package test.unit.web;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.os.DefaultWebObjectSource;
import org.soybeanMilk.web.os.ParamIllegalException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

public class TestDefaultWebObjectSource
{
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockServletContext application;
	private DefaultWebObjectSource webObjectSource;
	
	@Before
	public void setUp()
	{
		request=new MockHttpServletRequest();
		response=new MockHttpServletResponse();
		application=new MockServletContext();
		
		webObjectSource=new DefaultWebObjectSource(request, response, application, new WebGenericConverter());
	}
	
	@Test
	public void get_paramScope_keyIsScope_expectTypeIsNull() throws Exception
	{
		request.setParameter("key", "value");
		
		Map<String, String[]> re=webObjectSource.get("ParaM", null);
		
		Assert.assertEquals(request.getParameterMap().size(), re.size());
		Assert.assertEquals(request.getParameterMap().get("key"), re.get("key"));
	}
	
	@Test
	public void get_paramScope_keyIsScope_expectTypeIsRawMap() throws Exception
	{
		String[] value1={"value1"};
		String[] value2={"value2"};
		
		request.setParameter("paramName.aaa", value1);
		request.setParameter("paramName.bbb", value2);
		
		Map<String, ?> re=webObjectSource.get("param.paramName", Map.class);
		
		Assert.assertEquals(2, re.size());
		Assert.assertEquals(value1, re.get("aaa"));
		Assert.assertEquals(value2, re.get("bbb"));
	}
	
	@Test
	public void get_paramScope_keyHasExplicitValue() throws Exception
	{
		request.setParameter("key", "value");
		
		String re=webObjectSource.get("param.key", String.class);
		
		Assert.assertEquals("value", re);
	}
	
	@Test
	public void get_paramScope_keyHasMultiValue_targetIsRawMap() throws Exception
	{
		request.setParameter("key.k0", "v0");
		request.setParameter("key.k1.k11", "v1");
		request.setParameter("ignored", "111");
		
		Map<String, String[]> re=webObjectSource.get("param.key", Map.class);
		
		Assert.assertEquals("2", re.size()+"");
		Assert.assertEquals("v0", re.get("k0")[0]);
		Assert.assertEquals("v1", re.get("k1.k11")[0]);
	}
	
	@Test
	public void get_paramScope_keyHasMultiValue_targetTypeIsJavaBean() throws Exception
	{
		request.setParameter("my.myBean.id", new String[]{"1"});
		request.setParameter("my.myBean.name", new String[]{"jack"});
		request.setParameter("my.myBean.yourBean.id", new String[]{"2"});
		request.setParameter("my.myBean.yourBean.name", new String[]{"tom"});
		
		MyBean dest=webObjectSource.get("param.my.myBean", MyBean.class);
		
		Assert.assertEquals(1, dest.getId().intValue());
		Assert.assertEquals("jack", dest.getName());
		Assert.assertEquals(2, dest.getYourBean().getId().intValue());
		Assert.assertEquals("tom", dest.getYourBean().getName());
	}
	
	@Test
	public void get_paramScope_keyHasNoValue() throws Exception
	{
		Object re=webObjectSource.get("param.key", Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void get_paramScope_paramValueIllegal_explicitParam() throws Exception
	{
		String name="paramName";
		String value="illegalValue";
		
		request.setParameter(name, value);
		
		ParamIllegalException re=null;
		try
		{
			webObjectSource.get("param."+name, int.class);
		}
		catch(ParamIllegalException e)
		{
			re=e;
		}
		
		Assert.assertEquals(name, re.getParamName());
		Assert.assertEquals(value, re.getParamValue());
		Assert.assertEquals(int.class, re.getTargetType());
	}
	
	@Test
	public void get_paramScope_paramValueIllegal_mapParam() throws Exception
	{
		String value="illegalValue";
		
		request.setParameter("my.myBean.id", new String[]{"1"});
		request.setParameter("my.myBean.name", new String[]{"jack"});
		request.setParameter("my.myBean.yourBean.id", new String[]{value});
		request.setParameter("my.myBean.yourBean.name", new String[]{"tom"});
		
		ParamIllegalException re=null;
		try
		{
			webObjectSource.get("param.my.myBean", MyBean.class);
		}
		catch(ParamIllegalException e)
		{
			re=e;
		}
		
		Assert.assertEquals("my.myBean.yourBean.id", re.getParamName());
		Assert.assertEquals(value, re.getParamValue());
		Assert.assertEquals(Integer.class, re.getTargetType());
	}
	
	@Test
	public void get_requestScope_keyIsScope() throws Exception
	{
		HttpServletRequest re=webObjectSource.get("rEqueSt", HttpServletRequest.class);
		
		Assert.assertTrue( (re == request) );
	}
	
	@Test
	public void get_requestScope_keyIsNotAccessorExpression() throws Exception
	{
		String key="requestKey";
		Integer value=1235;
		
		request.setAttribute(key, value);
		
		Integer re=webObjectSource.get("request."+key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_requestScope_keyIsAccessorExpression_noAttributeObject() throws Exception
	{
		String key="key0.key1.key2";
		Integer value=1235;
		
		request.setAttribute(key, value);
		
		Integer re=webObjectSource.get("request."+key, Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void get_requestScope_keyIsAccessorExpression_hasAttributeObject() throws Exception
	{
		Integer id=1235;
		YourBean yb=new YourBean();
		yb.setId(id);
		
		MyBean src=new MyBean();
		src.setId(id);
		src.setYourBean(yb);
		
		request.setAttribute("myBean", src);
		
		Integer re0=webObjectSource.get("request.myBean.id", Integer.class);
		Integer re1=webObjectSource.get("request.myBean.yourBean.id", Integer.class);
		
		Assert.assertEquals(id, re0);
		Assert.assertEquals(id, re1);
	}
	
	@Test
	public void get_sessionScope_keyIsScope() throws Exception
	{
		HttpSession re=webObjectSource.get("sEssIon", HttpSession.class);
		
		Assert.assertTrue( (re == request.getSession()) );
	}
	
	@Test
	public void get_sessionScope_keyIsNotAccessorExpression() throws Exception
	{
		String key="sessionKey";
		Integer value=1235;
		
		request.getSession().setAttribute(key, value);
		
		Integer re=webObjectSource.get("session."+key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_sessionScope_keyIsAccessorExpression_noAttributeObject() throws Exception
	{
		String key="key0.key1.key2";
		Integer value=1235;
		
		request.getSession().setAttribute(key, value);
		
		Integer re=webObjectSource.get("session."+key, Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void get_sessionScope_keyIsAccessorExpression_hasAttributeObject() throws Exception
	{
		Integer id=1235;
		YourBean yb=new YourBean();
		yb.setId(id);
		
		MyBean src=new MyBean();
		src.setId(id);
		src.setYourBean(yb);
		
		request.getSession().setAttribute("myBean", src);
		
		Integer re0=webObjectSource.get("session.myBean.id", Integer.class);
		Integer re1=webObjectSource.get("session.myBean.yourBean.id", Integer.class);
		
		Assert.assertEquals(id, re0);
		Assert.assertEquals(id, re1);
	}
	
	@Test
	public void get_applicationScope_keyIsScope() throws Exception
	{
		ServletContext re=webObjectSource.get("appLicaTion", ServletContext.class);
		
		Assert.assertTrue( (re == application) );
	}
	
	@Test
	public void get_applicationScope_keyIsNotAccessorExpression() throws Exception
	{
		String key="requestKey";
		Integer value=1235;
		
		application.setAttribute(key, value);
		
		Integer re=webObjectSource.get("application."+key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_applicationScope_keyIsAccessorExpression_noAttributeObject() throws Exception
	{
		String key="key0.key1.key2";
		Integer value=1235;
		
		application.setAttribute(key, value);
		
		Integer re=webObjectSource.get("application."+key, Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void get_applicationScope_keyIsAccessorExpression_hasAttributeObject() throws Exception
	{
		Integer id=1235;
		YourBean yb=new YourBean();
		yb.setId(id);
		
		MyBean src=new MyBean();
		src.setId(id);
		src.setYourBean(yb);
		
		application.setAttribute("myBean", src);
		
		Integer re0=webObjectSource.get("application.myBean.id", Integer.class);
		Integer re1=webObjectSource.get("application.myBean.yourBean.id", Integer.class);
		
		Assert.assertEquals(id, re0);
		Assert.assertEquals(id, re1);
	}
	
	@Test
	public void get_responseScope_keyIsScope() throws Exception
	{
		HttpServletResponse re=webObjectSource.get("ResPonse", null);
		
		Assert.assertTrue( (re == response) );
	}
	
	@Test
	public void get_responseScope_keyInScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.get("response.obj", null);
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'response.obj' is illegal")) );
	}
	
	@Test
	public void get_objectSourceScope_keyIsScope() throws Exception
	{
		ObjectSource re=webObjectSource.get("objEctSource", null);
		
		Assert.assertTrue( (re == webObjectSource) );
	}
	
	@Test
	public void get_objectSourceScope_keyInScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.get("objectSource.obj", null);
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'objectSource.obj' is illegal")) );
	}
	
	@Test
	public void get_unknownScope_keyValueInRequest() throws Exception
	{
		String key="key0";
		Integer value=12345;
		
		request.setAttribute(key, value);
		
		Integer re=webObjectSource.get(key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_unknownScope_keyValueInSession() throws Exception
	{
		String key="key";
		Integer value=12345;
		
		request.getSession().setAttribute(key, value);
		
		Integer re=webObjectSource.get(key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_unknownScope_keyValueInApplication() throws Exception
	{
		String key="key";
		Integer value=12345;
		
		application.setAttribute(key, value);
		
		Integer re=webObjectSource.get(key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_unknownScope_keyValueInParam() throws Exception
	{
		String key="key";
		String value="12345";
		
		request.setParameter(key, value);
		
		String re=webObjectSource.get(key, String.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void set_requestScope_keyIsScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("request", "v");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'request' is illegal")) );
	}
	
	@Test
	public void set_requestScope_keyIsNotAccessorExpression() throws Exception
	{
		String key="requestKey";
		Integer value=1235;
		
		webObjectSource.set("request."+key, value);
		
		Integer re=(Integer)request.getAttribute(key);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void set_requestScope_keyIsAccessorExpression_noAttributeObject() throws Exception
	{
		String key="key0.key1.key2";
		Integer value=1235;
		
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("request."+key, value);
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertNotNull(re);
		Assert.assertTrue( re.getMessage().startsWith("no 'key0' attribute object found") );
	}
	
	@Test
	public void set_requestScope_keyIsAccessorExpression_hasAttributeObject() throws Exception
	{
		String id="1235";
		
		MyBean bean=new MyBean();
		
		request.setAttribute("myBean", bean);
		
		webObjectSource.set("request.myBean.id", id);
		webObjectSource.set("request.myBean.yourBean.id", id);
		
		MyBean re=(MyBean)request.getAttribute("myBean");
		
		Assert.assertEquals(id, re.getId().toString());
		Assert.assertEquals(id, re.getYourBean().getId().toString());
	}
	
	@Test
	public void set_sessionScope_keyIsScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("sessIon", "v");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'sessIon' is illegal")) );
	}
	
	@Test
	public void set_sessionScope_keyIsNotAccessorExpression() throws Exception
	{
		String key="sessionKey";
		Integer value=1235;
		
		webObjectSource.set("session."+key, value);
		
		Integer re=(Integer)request.getSession().getAttribute(key);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void set_sessionScope_keyIsAccessorExpression_noAttributeObject() throws Exception
	{
		String key="key0.key1.key2";
		Integer value=1235;
		
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("session."+key, value);
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertNotNull(re);
		Assert.assertTrue( re.getMessage().startsWith("no 'key0' attribute object found") );
	}
	
	@Test
	public void set_sessionScope_keyIsAccessorExpression_hasAttributeObject() throws Exception
	{
		String id="1235";
		
		MyBean bean=new MyBean();
		
		request.getSession().setAttribute("myBean", bean);
		
		webObjectSource.set("session.myBean.id", id);
		webObjectSource.set("session.myBean.yourBean.id", id);
		
		MyBean re=(MyBean)request.getSession().getAttribute("myBean");
		
		Assert.assertEquals(id, re.getId().toString());
		Assert.assertEquals(id, re.getYourBean().getId().toString());
	}
	
	@Test
	public void set_applicationScope_keyIsScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("appLication", "v");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'appLication' is illegal")) );
	}
	
	@Test
	public void set_applicationScope_keyIsNotAccessorExpression() throws Exception
	{
		String key="sessionKey";
		Integer value=1235;
		
		webObjectSource.set("application."+key, value);
		
		Integer re=(Integer)application.getAttribute(key);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void set_applicationScope_keyIsAccessorExpression_noAttributeObject() throws Exception
	{
		String key="key0.key1.key2";
		Integer value=1235;
		
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("application."+key, value);
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertNotNull(re);
		Assert.assertTrue( re.getMessage().startsWith("no 'key0' attribute object found") );
	}
	
	@Test
	public void set_applicationScope_keyIsAccessorExpression_hasAttributeObject() throws Exception
	{
		String id="1235";
		
		MyBean bean=new MyBean();
		
		application.setAttribute("myBean", bean);
		
		webObjectSource.set("application.myBean.id", id);
		webObjectSource.set("application.myBean.yourBean.id", id);
		
		MyBean re=(MyBean)application.getAttribute("myBean");
		
		Assert.assertEquals(id, re.getId().toString());
		Assert.assertEquals(id, re.getYourBean().getId().toString());
	}
	
	@Test
	public void set_paramScope_keyIsScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("paRam", "v");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'paRam' is illegal")) );
	}
	
	@Test
	public void set_responseScope_keyIsScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("resPonse", "v");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'resPonse' is illegal")) );
	}
	
	@Test
	public void set_objectSourceScope_keyIsScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("objeCtSource", "v");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key 'objeCtSource' is illegal")) );
	}
	
	@Test
	public void set_unknownScope_keyIsNotAccessExpresion() throws Exception
	{
		Integer src=123465;
		
		webObjectSource.set("key", src);
		
		Integer re=(Integer)request.getAttribute("key");
		
		Assert.assertEquals(src, re);
	}
	
	@Test
	public void set_unknownScope_keyIsAccessExpresion_noAttributeObjectInAnyScope() throws Exception
	{
		Integer src=123465;
		
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.set("key0.key1", src);
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertNotNull(re);
		Assert.assertTrue( re.getMessage().startsWith("no 'key0' attribute object found") );
	}
	
	@Test
	public void set_unknownScope_keyIsAccessorExpression_hasRequestAttributeObject() throws Exception
	{
		String id="1235";
		
		MyBean bean=new MyBean();
		
		request.setAttribute("myBean", bean);
		
		webObjectSource.set("myBean.id", id);
		webObjectSource.set("myBean.yourBean.id", id);
		
		MyBean re=(MyBean)request.getAttribute("myBean");
		
		Assert.assertEquals(id, re.getId().toString());
		Assert.assertEquals(id, re.getYourBean().getId().toString());
	}
	
	@Test
	public void set_unknownScope_keyIsAccessorExpression_hasSessionAttributeObject() throws Exception
	{
		String id="1235";
		
		MyBean bean=new MyBean();
		
		request.getSession().setAttribute("myBean", bean);
		
		webObjectSource.set("myBean.id", id);
		webObjectSource.set("myBean.yourBean.id", id);
		
		MyBean re=(MyBean)request.getSession().getAttribute("myBean");
		
		Assert.assertEquals(id, re.getId().toString());
		Assert.assertEquals(id, re.getYourBean().getId().toString());
	}
	
	@Test
	public void set_unknownScope_keyIsAccessorExpression_hasApplicationAttributeObject() throws Exception
	{
		String id="1235";
		
		MyBean bean=new MyBean();
		
		application.setAttribute("myBean", bean);
		
		webObjectSource.set("myBean.id", id);
		webObjectSource.set("myBean.yourBean.id", id);
		
		MyBean re=(MyBean)application.getAttribute("myBean");
		
		Assert.assertEquals(id, re.getId().toString());
		Assert.assertEquals(id, re.getYourBean().getId().toString());
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
