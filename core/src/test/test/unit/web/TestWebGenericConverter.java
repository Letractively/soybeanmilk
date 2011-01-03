package test.unit.web;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
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
		
		//其他
		{
			Integer[] src=new Integer[]{null};
			String dest=(String)converter.convert(src, String.class);
			Assert.assertNull(dest);
		}
		{
			Object dest=converter.convert(null, Object.class);
			Assert.assertNull(dest);
		}
		{
			String dest=(String)converter.convert(new Integer(12345), String.class);
			Assert.assertEquals("12345", dest);
		}
		{
			Integer src=new Integer(12345);
			
			Integer dest=(Integer)converter.convert(src, null);
			Assert.assertTrue( src==dest );
		}
	}
	
	/**
	 * 目标类型为父类型
	 */
	@Test
	public void convertMapToMap()
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Object dest=converter.convert(src, Map.class);
		
		Assert.assertTrue(src == dest);
	}
	
	@Test
	public void convertMapToClassType() throws Exception
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
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			String[] name=new String[]{"jack"};
			String[] age=new String[]{"15"};
			String[] birth=new String[]{"1900-10-21"};
			
			src.put("name", name);
			src.put("age", age);
			src.put("birth", birth);
			
			JavaBean dest=(JavaBean)converter.convert(src, JavaBean.class);
			
			Assert.assertEquals(name[0], dest.getName());
			Assert.assertEquals(new Integer(age[0]), dest.getAge());
			Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth[0]), dest.getBirth());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertMapToNormalCollection() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		//非泛型类，不知道元素类型，所以是null
		{
			try
			{
				List<JavaBean> dest=(List<JavaBean>)converter.convert(src, List.class);
				dest.size();
			}
			catch(Exception e)
			{
				Assert.assertEquals("only generic List converting is supported", e.getMessage());
			}
		}
		{
			try
			{
				Set<JavaBean> dest=(Set<JavaBean>)converter.convert(src, Set.class);
				dest.size();
			}
			catch(Exception e)
			{
				Assert.assertEquals("only generic Set converting is supported", e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertMapToUnSupportedGenericCollection()
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		{
			Type type=new MockParameterizedType(new MockParameterizedType(null, null), new Type[]{JavaBean.class});
			try
			{
				List<JavaBean> dest=(List<JavaBean>)converter.convert(src, type);
				dest.size();
			}
			catch(Exception e)
			{
				Assert.assertEquals("'"+type+"' is not valid, only Class type of its raw type is supported", e.getMessage());
			}
		}
	}
	
	@Test
	public void convertMapToGenericList() throws Exception
	{
		//使用List接口
		{
			convertMapToGenericList(List.class);
		}
		
		//使用List子类
		{
			convertMapToGenericList(ArrayList.class);
		}
		{
			convertMapToGenericList(LinkedList.class);
		}
		{
			convertMapToGenericList(Vector.class);
		}
		{
			convertMapToGenericList(Stack.class);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void convertMapToGenericList(Class<? extends List> listClass) throws Exception
	{
		//使用List接口
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			String[] names=new String[]{"aa", "bb", "cc"};
			String[] ages=new String[]{"11", "22", "33"};
			String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
			
			src.put("name", names);
			src.put("age", ages);
			src.put("birth", births);
			
			List<JavaBean> dest=(List<JavaBean>)converter.convert(src, new MockParameterizedType(listClass, new Type[]{JavaBean.class}));
			
			Assert.assertTrue( dest.size() == names.length);
			
			//默认创建ArrayList
			if(listClass.equals(List.class))
				Assert.assertEquals(ArrayList.class, dest.getClass());
			else
				Assert.assertEquals(listClass, dest.getClass());
			
			for(int i=0;i<dest.size();i++)
			{
				JavaBean jb=dest.get(i);
				
				Assert.assertEquals(names[i], jb.getName());
				Assert.assertEquals(ages[i], jb.getAge().toString());
				Assert.assertEquals(births[i], new SimpleDateFormat("yyyy-MM-dd").format(jb.getBirth()));
			}
		}
	}

	@Test
	public void convertMapToGenericSet() throws Exception
	{
		//使用Set接口
		{
			convertMapToGenericSet(Set.class);
		}
		
		//使用Set子类
		{
			convertMapToGenericSet(HashSet.class);
		}
		{
			convertMapToGenericSet(TreeSet.class);
		}
		{
			convertMapToGenericSet(LinkedHashSet.class);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void convertMapToGenericSet(Class<? extends Set> setClass) throws Exception
	{
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			String[] names=new String[]{"aa", "bb", "cc"};
			String[] ages=new String[]{"11", "22", "33"};
			String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
			
			src.put("name", names);
			src.put("age", ages);
			src.put("birth", births);
			
			Set<JavaBean> dest=(Set<JavaBean>)converter.convert(src, new MockParameterizedType(setClass, new Type[]{JavaBean.class}));
			
			Assert.assertTrue( dest.size() == names.length);
			
			//默认创建ArrayList
			if(setClass.equals(Set.class))
				Assert.assertEquals(HashSet.class, dest.getClass());
			else
				Assert.assertEquals(setClass, dest.getClass());
			
			for(JavaBean jb : dest)
			{
				String name=jb.getName();
				
				int idx=-1;
				if(name.equals(names[0]))
					idx=0;
				else if(name.equals(names[1]))
					idx=1;
				else if(name.equals(names[2]))
					idx=2;
				
				Assert.assertEquals(ages[idx], jb.getAge().toString());
				Assert.assertEquals(births[idx], new SimpleDateFormat("yyyy-MM-dd").format(jb.getBirth()));
			}
		}
	}
	
	@Test
	public void convertMapToArray()
	{
		//复杂类型
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			String[] names=new String[]{"aa", "bb", "cc"};
			String[] ages=new String[]{"11", "22", "33"};
			String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
			
			src.put("name", names);
			src.put("age", ages);
			src.put("birth", births);
			
			JavaBean[] dest=(JavaBean[])converter.convert(src, JavaBean[].class);
			
			Assert.assertTrue( dest.length == names.length);
			
			for(int i=0;i<dest.length;i++)
			{
				JavaBean jb=dest[i];
				
				Assert.assertEquals(names[i], jb.getName());
				Assert.assertEquals(ages[i], jb.getAge().toString());
				Assert.assertEquals(births[i], new SimpleDateFormat("yyyy-MM-dd").format(jb.getBirth()));
			}
		}
	}
	
	public static class JavaBean implements Comparable<JavaBean>
	{
		private String name;
		private Integer age;
		private Date birth;
		
		private List<JavaBean2> list;
		
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
		public List<JavaBean2> getList() {
			return list;
		}
		public void setList(List<JavaBean2> list) {
			this.list = list;
		}
		
		@Override
		public int compareTo(JavaBean o)
		{
			if(o == null)
				return 1;
			
			int re=this.name.compareTo(o.getName());
			if(re == 0)
				re=this.age.compareTo(o.getAge());
			if(re == 0)
				re=this.birth.compareTo(o.getBirth());
			
			return re;
		}
		
		@Override
		public String toString() {
			return "JavaBean [name=" + name + ", age=" + age + ", birth="
					+ birth + "]";
		}
	}
	
	public static class JavaBean2 implements Comparable<JavaBean2>
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
		
		@Override
		public int compareTo(JavaBean2 o)
		{
			if(o == null)
				return 1;
			
			int re= this.id == o.id ? 0 : (this.id>o.id ? 1 : -1);
			if(re == 0)
				re=this.name.compareTo(o.name);
			
			return re;
		}
		
		@Override
		public String toString() {
			return "JavaBean2 [id=" + id + ", name=" + name + "]";
		}
	}
}
