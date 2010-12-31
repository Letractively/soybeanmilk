package test.unit.web;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.PropertyInfo;
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
		//基本类型
		{
			Boolean dest=(Boolean)converter.convert(null, boolean.class);
			Assert.assertFalse(dest);
		}
		{
			Byte dest=(Byte)converter.convert(null, byte.class);
			Assert.assertEquals(0, dest.byteValue());
		}
		{
			Character dest=(Character)converter.convert(null, char.class);
			Assert.assertEquals(0, dest.charValue());
		}
		{
			Double dest=(Double)converter.convert(null, double.class);
			Assert.assertEquals(0d, dest.doubleValue());
		}
		{
			Float dest=(Float)converter.convert(null, float.class);
			Assert.assertEquals(0f, dest.floatValue());
		}
		{
			Integer dest=(Integer)converter.convert(null, int.class);
			Assert.assertEquals(0, dest.intValue());
		}
		{
			Long dest=(Long)converter.convert(null, long.class);
			Assert.assertEquals(0l, dest.longValue());
		}
		{
			Short dest=(Short)converter.convert(null, short.class);
			Assert.assertEquals((short)0, dest.shortValue());
		}
		{
			String dest=(String)converter.convert(null, String.class);
			Assert.assertNull(dest);
		}
		
		//数组
		{
			String dest=(String)converter.convert(new int[]{12345}, String.class);
			Assert.assertEquals("12345", dest);
		}
		{
			int[] src=new int[]{12345,2342809};
			String dest=(String)converter.convert(src, String.class);
			Assert.assertEquals(src.toString(), dest);
		}
		{
			String dest=(String)converter.convert(new boolean[]{false}, String.class);
			Assert.assertEquals("false", dest);
		}
		{
			boolean[] dest=(boolean[])converter.convert(new String[]{"false", "true"}, boolean[].class);
			Assert.assertFalse(dest[0]);
			Assert.assertTrue(dest[1]);
		}
		{
			int[] dest=(int[])converter.convert(new String[]{"222", "3333"}, int[].class);
			Assert.assertEquals(222,dest[0]);
			Assert.assertEquals(3333, dest[1]);
		}
		{
			int[] dest=(int[])converter.convert(new String[]{"222", "3333"}, int[].class);
			Assert.assertEquals(222,dest[0]);
			Assert.assertEquals(3333, dest[1]);
		}
		{
			Integer[] src=new Integer[]{null};
			String dest=(String)converter.convert(src, String.class);
			Assert.assertNull(dest);
		}
		
		//其他
		{
			Object dest=converter.convert(null, Object.class);
			Assert.assertNull(dest);
		}
		{
			String dest=(String)converter.convert(new Integer(12345), String.class);
			Assert.assertEquals("12345", dest);
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
	public void convertMapToOther() throws Exception
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
	
	@Test
	public void convertMapToCollectionProperty2() throws Exception
	{
		PropertyInfo pi=PropertyInfo.getPropertyInfo(JavaBean.class);
		
		ParameterizedType pt = (ParameterizedType)(pi.getSubPropertyInfo("list").getWriteMethod().getGenericParameterTypes()[0]);  
		
		System.out.println(pt.getActualTypeArguments().length);  
		System.out.println(((Class<?>)pt.getActualTypeArguments()[0]).getName());  
	}
	
	public static class JavaBean
	{
		private String name;
		private Integer age;
		private Date birth;
		
		private List<Bean2> list;
		
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
		public List<Bean2> getList() {
			return list;
		}
		public void setList(List<Bean2> list) {
			this.list = list;
		}
	}
	
	public static class Bean2
	{
		private int id;
		private String name;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
