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
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.os.DefaultWebObjectSource;
import org.soybeanMilk.web.os.ParamIllegalException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
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
	public void get_paramScope_keyIsScope_targetTypeIsNull() throws Exception
	{
		request.setParameter("key", "value");
		
		Map<String, String[]> re=webObjectSource.get("ParaM");
		
		Assert.assertEquals(request.getParameterMap().size(), re.size());
		Assert.assertEquals(request.getParameterMap().get("key"), re.get("key"));
	}
	
	@Test
	public void get_paramScope_keyIsScope_targetIsRawMap() throws Exception
	{
		String[] value1={"value1"};
		String[] value2={"value2"};
		
		request.setParameter("paramName.aaa", value1);
		request.setParameter("paramName.bbb", value2);
		
		Map<String, ?> re=webObjectSource.get("param", Map.class);
		
		Assert.assertEquals(2, re.size());
		Assert.assertEquals(value1, re.get("paramName.aaa"));
		Assert.assertEquals(value2, re.get("paramName.bbb"));
	}
	
	@Test
	public void get_paramScope_keyIsScope_targetIsJavaBean() throws Exception
	{
		request.setParameter("id", new String[]{"1"});
		request.setParameter("name", new String[]{"jack"});
		request.setParameter("yourBean.id", new String[]{"2"});
		request.setParameter("yourBean.name", new String[]{"tom"});
		
		MyBean dest=webObjectSource.get("param", MyBean.class);
		
		Assert.assertEquals(1, dest.getId().intValue());
		Assert.assertEquals("jack", dest.getName());
		Assert.assertEquals(2, dest.getYourBean().getId().intValue());
		Assert.assertEquals("tom", dest.getYourBean().getName());
	}
	
	@Test
	public void get_paramScope_singleValue() throws Exception
	{
		request.setParameter("key", "3");
		
		Integer re=webObjectSource.get("param.key", Integer.class);
		
		Assert.assertEquals(3, re.intValue());
	}
	
	@Test
	public void get_paramScope_singleValue_valueIllegal() throws Exception
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
	public void get_paramScope_singleValue_valueIsNull_targetIsPrimitive() throws Exception
	{
		GenericConvertException re=null;
		
		try
		{
			webObjectSource.get("param.value", int.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not convert null to primitive type")) );
	}
	
	@Test
	public void get_paramScope_mapValue_targetIsRawMap() throws Exception
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
	public void get_paramScope_mapValue_targetIsJavaBean() throws Exception
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
	public void get_paramScope_mapValue_customType() throws Exception
	{
		request.setParameter("my.myBean.id", new String[]{"1"});
		request.setParameter("my.myBean.name", new String[]{"jack"});
		request.setParameter("my.myBean.yourBean.id", new String[]{"3"});
		request.setParameter("my.myBean.yourBean.name", new String[]{"tom"});
		
		request.setParameter("my.myBean.class", MyBean.class.getName());
		
		MyBean re=webObjectSource.get("param.my.myBean");
		
		Assert.assertEquals(1, re.getId().intValue());
		Assert.assertEquals("jack", re.getName());
		Assert.assertEquals(3, re.getYourBean().getId().intValue());
		Assert.assertEquals("tom", re.getYourBean().getName());
	}
	
	@Test
	public void get_paramScope_mapValue_containIllegalValue() throws Exception
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
	public void get_paramScope_valueIsNull() throws Exception
	{
		Object re=webObjectSource.get("param.key", Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void get_requestScope_keyIsScope() throws Exception
	{
		HttpServletRequest re=webObjectSource.get("rEqueSt", HttpServletRequest.class);
		
		Assert.assertTrue( (re == request) );
	}
	
	@Test
	public void get_requestScope_keyWithScope() throws Exception
	{
		String key="requestKey.aaa.bbb";
		Integer value=1235;
		
		request.setAttribute(key, value);
		
		Integer re=webObjectSource.get("request."+key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_sessionScope_keyIsScope() throws Exception
	{
		HttpSession re=webObjectSource.get("sEssIon", HttpSession.class);
		
		Assert.assertTrue( (re == request.getSession()) );
	}
	
	@Test
	public void get_sessionScope_keyWithScope() throws Exception
	{
		String key="sessionKey.aaa.bbb";
		Integer value=1235;
		
		request.getSession().setAttribute(key, value);
		
		Integer re=webObjectSource.get("session."+key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_applicationScope_keyIsScope() throws Exception
	{
		ServletContext re=webObjectSource.get("appLicaTion", ServletContext.class);
		
		Assert.assertTrue( (re == application) );
	}
	
	@Test
	public void get_applicationScope_keyWithScope() throws Exception
	{
		String key="requestKey.aaa.bbb";
		Integer value=1235;
		
		application.setAttribute(key, value);
		
		Integer re=webObjectSource.get("application."+key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_responseScope_keyIsScope() throws Exception
	{
		HttpServletResponse re=webObjectSource.get("ResPonse");
		
		Assert.assertTrue( (re == response) );
	}
	
	@Test
	public void get_responseScope_keyWithScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.get("response.obj");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"response.obj\" is illegal")) );
	}
	
	@Test
	public void get_objectSourceScope_keyIsScope() throws Exception
	{
		ObjectSource re=webObjectSource.get("objEctSource");
		
		Assert.assertTrue( (re == webObjectSource) );
	}
	
	@Test
	public void get_objectSourceScope_keyInScope() throws Exception
	{
		ObjectSourceException re=null;
		
		try
		{
			webObjectSource.get("objectSource.obj");
		}
		catch(ObjectSourceException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"objectSource.obj\" is illegal")) );
	}
	
	@Test
	public void get_unknownScope_keyValueInRequest() throws Exception
	{
		String key="key0.aaa.bbb";
		Integer value=12345;
		
		request.setAttribute(key, value);
		
		Integer re=webObjectSource.get(key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_unknownScope_keyValueInSession() throws Exception
	{
		String key="key.aaa.bbb";
		Integer value=12345;
		
		request.getSession().setAttribute(key, value);
		
		Integer re=webObjectSource.get(key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_unknownScope_keyValueInApplication() throws Exception
	{
		String key="key.aaa.bbb";
		Integer value=12345;
		
		application.setAttribute(key, value);
		
		Integer re=webObjectSource.get(key, Integer.class);
		
		Assert.assertEquals(value, re);
	}
	
	@Test
	public void get_unknownScope_keyValueInParam() throws Exception
	{
		String key="key.aaa.bbb";
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
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"request\" is illegal")) );
	}
	
	@Test
	public void set_requestScope_keyWithScope() throws Exception
	{
		String key="requestKey.aaa.bbb";
		Integer value=1235;
		
		webObjectSource.set("request."+key, value);
		
		Integer re=(Integer)request.getAttribute(key);
		
		Assert.assertEquals(value, re);
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
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"sessIon\" is illegal")) );
	}
	
	@Test
	public void set_sessionScope_keyWithScope() throws Exception
	{
		String key="sessionKey.aaa.bbb";
		Integer value=1235;
		
		webObjectSource.set("session."+key, value);
		
		Integer re=(Integer)request.getSession().getAttribute(key);
		
		Assert.assertEquals(value, re);
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
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"appLication\" is illegal")) );
	}
	
	@Test
	public void set_applicationScope_keyWithScope() throws Exception
	{
		String key="sessionKey.aaa.bbb";
		Integer value=1235;
		
		webObjectSource.set("application."+key, value);
		
		Integer re=(Integer)application.getAttribute(key);
		
		Assert.assertEquals(value, re);
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
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"paRam\" is illegal")) );
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
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"resPonse\" is illegal")) );
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
		
		Assert.assertTrue( (re.getMessage().startsWith("key \"objeCtSource\" is illegal")) );
	}
	
	@Test
	public void set_unknownScope() throws Exception
	{
		Integer src=123465;
		
		String key="key.aaa.bbb";
		
		webObjectSource.set(key, src);
		
		Integer re=(Integer)request.getAttribute(key);
		
		Assert.assertEquals(src, re);
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
