package unit.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.web.bean.WebGenericConverter;


public class TestWebGenericConverter
{
	private WebGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter=new WebGenericConverter();
	}
	
	@Test
	public void convertSafe()
	{
		{
			Object dest=converter.convert(null, Object.class);
			
			Assert.assertNull(dest);
		}
		
		{
			Integer dest=(Integer)converter.convert(null, int.class);
			
			Assert.assertEquals(0, dest.intValue());
		}
	}
	
	/**
	 * 目标类型为父类型
	 */
	@Test
	public void convertMapToSuperMap()
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Object dest=converter.convert(src, Map.class);
		
		Assert.assertTrue(src == dest);
	}
	
	/**
	 * 内置Map转换
	 */
	@Test
	public void convertMapNest() throws Exception
	{
		//源为空
		{
			Map<String,Object> src=new HashMap<String, Object>();
			Object dest = converter.convert(src, JavaBean.class);
			
			Assert.assertNull(dest);
		}
		
		//源Map中没有包含目标bean的属性
		{
			Map<String,Object> src=new HashMap<String, Object>();
			src.put("abc", 356);
			
			Object dest = converter.convert(src, JavaBean.class);
			
			Assert.assertNull(dest);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			String name="jack";
			String age="15";
			String birth="1900-10-21";
			
			src.put("name", name);
			src.put("age", age);
			src.put("birth", birth);
			
			JavaBean dest=(JavaBean)converter.convert(src, JavaBean.class);
			
			Assert.assertEquals(name, dest.getName());
			Assert.assertEquals(new Integer(age), dest.getAge());
			Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
		}
	}
	
	public static class JavaBean
	{
		private String name;
		private Integer age;
		private Date birth;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
		public Date getBirth() {
			return birth;
		}
		public void setBirth(Date birth) {
			this.birth = birth;
		}
	}
}
