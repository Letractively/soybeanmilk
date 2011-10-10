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
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.GenericType;
import org.soybeanMilk.web.bean.FilterAwareMap;
import org.soybeanMilk.web.bean.ParamConvertException;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.os.WebObjectSource.ParamFilterAwareMap;

import test.unit.core.MockParameterizedType;

public class TestWebGenericConverter
{
	private WebGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter=new WebGenericConverter();
	}
	
	@Test
	public void convertSimple_singleElementArrayToNoArrayObject()
	{
		Integer dest=(Integer)converter.convert(new String[]{"12345"}, int.class);
		Assert.assertEquals(12345, dest.intValue());
	}
	
	@Test
	public void convertSimple_singleElementArrayToNoArrayObject_1()
	{
		Integer dest=(Integer)converter.convert(new String[]{""}, Integer.class);
		Assert.assertNull(dest);
	}
	
	@Test
	public void convertMap_toMap()
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Object dest=converter.convert(src, Map.class);
		
		Assert.assertTrue(src == dest);
	}
	
	@Test
	public void convertMap_toJavaBean_srcNull() throws Exception
	{
		Object dest = converter.convert(null, JavaBean.class);
		Assert.assertNull(dest);
	}
	
	@Test
	public void convertMap_toJavaBean_srcEmpty() throws Exception
	{
		//源为空
		Map<String,Object> src=new HashMap<String, Object>();
		Object dest = converter.convert(src, JavaBean.class);
		
		Assert.assertNull(dest);
	}
	
	@Test
	public void convertMap_toJavaBean_srcHasNoPropertyContain() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		src.put("abc", 356);
		
		Object dest = converter.convert(src, JavaBean.class);
		
		Assert.assertNull(dest);
	}
	
	@Test(expected = GenericConvertException.class)
	public void convertMap_toJavaBean_srcHasInexistentSubPropertyContain() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		src.put("javaBean.def", 356);
		
		Object dest = converter.convert(src, JavaBean2.class);
		
		Assert.assertNull(dest);
	}
	
	@Test(expected = GenericConvertException.class)
	public void convertMap_toJavaBean_srcHasInexistentPropertyContainInSubArrayProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		
		String[] cmplexCollectionProperty_id=new String[]{"2","5","7"};
		String[] cmplexCollectionProperty_name=new String[]{"aaa","bbb","ccc"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("javaBean2Array.id", cmplexCollectionProperty_id);
		src.put("javaBean2Array.name", cmplexCollectionProperty_name);
		src.put("javaBean2Array.notExistsProperty", cmplexCollectionProperty_name);
		
		Object dest = converter.convert(src, ComplexJavaBean.class);
		
		Assert.assertNull(dest);
	}
	
	@Test(expected = GenericConvertException.class)
	public void convertMap_toJavaBean_srcHasInexistentPropertyContainInSubListProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		
		String[] cmplexCollectionProperty_id=new String[]{"2","5","7"};
		String[] cmplexCollectionProperty_name=new String[]{"aaa","bbb","ccc"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("javaBean2List.id", cmplexCollectionProperty_id);
		src.put("javaBean2List.name", cmplexCollectionProperty_name);
		src.put("javaBean2List.notExistsProperty", cmplexCollectionProperty_name);
		
		Object dest = converter.convert(src, ComplexJavaBean.class);
		
		Assert.assertNull(dest);
	}
	
	@Test(expected = GenericConvertException.class)
	public void convertMap_toJavaBean_srcHasInexistentPropertyContainInSubSetProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		
		String[] cmplexCollectionProperty_id=new String[]{"2","5","7"};
		String[] cmplexCollectionProperty_name=new String[]{"aaa","bbb","ccc"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("javaBean2Set.id", cmplexCollectionProperty_id);
		src.put("javaBean2Set.name", cmplexCollectionProperty_name);
		src.put("javaBean2Set.notExistsProperty", cmplexCollectionProperty_name);
		
		Object dest = converter.convert(src, ComplexJavaBean.class);
		
		Assert.assertNull(dest);
	}
	
	@Test
	public void convertFilterAwareMap_toSimple_srcIsInvalidValue() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		String[] id=new String[]{"invalidValue"};
		String[] name=new String[]{"jack"};
		
		src.put("id", id);
		src.put("name", name);
		
		FilterAwareMap<String, ?> fm=new ParamFilterAwareMap<String, Object>(src, "id", true);
		
		try
		{
			converter.convert(fm, int.class);
		}
		catch(ParamConvertException e)
		{
			Assert.assertEquals("id", e.getParamName());
			Assert.assertEquals("invalidValue", e.getSourceObject());
			Assert.assertEquals(int.class, e.getTargetType());
		}
	}
	
	@Test
	public void convertFilterAwareMap_toJavaBean_srcArrayPropertyContainInvalidValue() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","invalidValue","9"};
		
		src.put("filter.id", id);
		src.put("filter.name", name);
		
		src.put("filter.simpleArray", simpleCollectionProperty);
		
		FilterAwareMap<String, ?> fm=new ParamFilterAwareMap<String, Object>(src, "filter.", false);
		try
		{
			converter.convert(fm, ComplexJavaBean.class);
		}
		catch(ParamConvertException e)
		{
			Assert.assertEquals("filter.simpleArray", e.getParamName());
			Assert.assertEquals("invalidValue", e.getSourceObject());
			Assert.assertEquals(Integer.class, e.getTargetType());
		}
	}
	
	@Test
	public void convertFilterAwareMap_toJavaBean_srcComplexPropertyContainInvalidValue() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","3","9"};
		
		String[] cmplexCollectionProperty_id=new String[]{"2","invalidValue","7"};
		String[] cmplexCollectionProperty_name=new String[]{"aaa","bbb","ccc"};
		
		src.put("filter.id", id);
		src.put("filter.name", name);
		
		src.put("filter.simpleArray", simpleCollectionProperty);
		src.put("filter.simpleList", simpleCollectionProperty);
		src.put("filter.simpleSet", simpleCollectionProperty);
		
		src.put("filter.javaBean2List.id", cmplexCollectionProperty_id);
		src.put("filter.javaBean2List.name", cmplexCollectionProperty_name);
		
		
		
		FilterAwareMap<String, ?> fm=new ParamFilterAwareMap<String, Object>(src, "filter.", false);
		
		try
		{
			converter.convert(fm, ComplexJavaBean.class);
		}
		catch(ParamConvertException e)
		{
			Assert.assertEquals("filter.javaBean2List.id", e.getParamName());
			Assert.assertEquals("invalidValue", e.getSourceObject());
			Assert.assertEquals(int.class, e.getTargetType());
		}
	}
	
	@Test
	public void convertMap_toJavaBean_srcElementString() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		src.put("_no_property_defined", birth);
		
		JavaBean dest=(JavaBean)converter.convert(src, JavaBean.class);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convertMap_toJavaBean_srcElementSingleStringArray() throws Exception
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
	
	@Test
	public void convertMap_toJavaBean_srcElementMultiStringArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name=new String[]{"jack","tom"};
		String[] age=new String[]{"15","18"};
		String[] birth=new String[]{"1900-10-21","1900-10-22"};
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		Exception re=null;
		try
		{
			JavaBean dest=(JavaBean)converter.convert(src, JavaBean.class);
			dest.toString();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convertMap_toJavaBean_collectionProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","3","9"};
		
		String[] cmplexCollectionProperty_id=new String[]{"2","5","7"};
		String[] cmplexCollectionProperty_name=new String[]{"aaa","bbb","ccc"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("simpleArray", simpleCollectionProperty);
		src.put("simpleList", simpleCollectionProperty);
		src.put("simpleSet", simpleCollectionProperty);
		
		src.put("javaBean2Array.id", cmplexCollectionProperty_id);
		src.put("javaBean2Array.name", cmplexCollectionProperty_name);
		
		src.put("javaBean2List.id", cmplexCollectionProperty_id);
		src.put("javaBean2List.name", cmplexCollectionProperty_name);
		
		src.put("javaBean2Set.id", cmplexCollectionProperty_id);
		src.put("javaBean2Set.name", cmplexCollectionProperty_name);
		
		ComplexJavaBean dest=(ComplexJavaBean)converter.convert(src, ComplexJavaBean.class);
		
		Assert.assertEquals(Integer.parseInt(id[0]), dest.getId());
		Assert.assertEquals(name[0], dest.getName());
		{
			Integer[] p=dest.getSimpleArray();
			for(int i=0;i<p.length;i++)
				Assert.assertEquals(new Integer(simpleCollectionProperty[i]), p[i]);
		}
		{
			List<Integer> p=dest.getSimpleList();
			for(int i=0;i<p.size();i++)
				Assert.assertEquals(new Integer(simpleCollectionProperty[i]), p.get(i));
		}
		
		{
			Set<Integer> p=dest.getSimpleSet();
			for(Integer it : p)
			{
				int idx=-1;
				for(int i=0;i<simpleCollectionProperty.length;i++)
					if(new Integer(simpleCollectionProperty[i]).equals(it))
						idx=i;
				
				Assert.assertTrue( idx> -1 );
			}
		}
		
		{
			JavaBean2[] p=dest.getJavaBean2Array();
			
			for(int i=0;i<p.length;i++)
			{
				Assert.assertEquals(Integer.parseInt(cmplexCollectionProperty_id[i]), p[i].getId());
				Assert.assertEquals(cmplexCollectionProperty_name[i], p[i].getName());
			}
		}
		
		{
			List<JavaBean2> p=dest.getJavaBean2List();
			
			for(int i=0;i<p.size();i++)
			{
				Assert.assertEquals(Integer.parseInt(cmplexCollectionProperty_id[i]), p.get(i).getId());
				Assert.assertEquals(cmplexCollectionProperty_name[i], p.get(i).getName());
			}
		}
		
		{
			Set<JavaBean2> p=dest.getJavaBean2Set();
			for(JavaBean2 jb : p)
			{
				int idx=-1;
				for(int i=0;i<cmplexCollectionProperty_id.length;i++)
				{
					if(Integer.parseInt(cmplexCollectionProperty_id[i]) ==jb.getId()
							&& cmplexCollectionProperty_name[i].equals(jb.getName()))
							idx=i;
				}
				
				Assert.assertTrue( idx> -1 );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertMap_toNotGenericCollection_List() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		Exception re=null;
		try
		{
			List<JavaBean> dest=(List<JavaBean>)converter.convert(src, List.class);
			dest.size();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().endsWith("it has no javaBean property") );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertMap_toNotGenericCollection_Set() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		Exception re=null;
		try
		{
			Set<JavaBean> dest=(Set<JavaBean>)converter.convert(src, Set.class);
			dest.size();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().endsWith("it has no javaBean property") );
	}
	
	@Test
	public void convertMap_toUnSupportedParameterizedType()
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		Exception re=null;
		try
		{
			Type type=GenericType.getGenericType(new MockParameterizedType(JavaBean.class, JavaBean.class), null);
			converter.convert(src, type);
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("converting 'Map<String,?>' to") );
	}
	
	@Test
	public void convertMap_toJavaBeanList_List() throws Exception
	{
		convertMap_toJavaBeanList(List.class);
	}
	
	@Test
	public void convertMap_toJavaBeanList_ArrayList() throws Exception
	{
		convertMap_toJavaBeanList(ArrayList.class);
	}
	
	@Test
	public void convertMap_toJavaBeanList_LinkedList() throws Exception
	{
		convertMap_toJavaBeanList(LinkedList.class);
	}
	
	@Test
	public void convertMap_toJavaBeanList_Vector() throws Exception
	{
		convertMap_toJavaBeanList(Vector.class);
	}
	
	@Test
	public void convertMap_toJavaBeanList_Stack() throws Exception
	{
		convertMap_toJavaBeanList(Stack.class);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void convertMap_toJavaBeanList(Class<? extends List> listClass) throws Exception
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
			
			GenericType genericType=GenericType.getGenericType(new MockParameterizedType(listClass, new Type[]{JavaBean.class}), null);
			List<JavaBean> dest=(List<JavaBean>)converter.convert(src, genericType);
			
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
	public void convertMap_toJavaBeanSet_Set() throws Exception
	{
		convertMap_toJavaBeanSet(Set.class);
	}
	
	@Test
	public void convertMap_toJavaBeanSet_HashSet() throws Exception
	{
		convertMap_toJavaBeanSet(HashSet.class);
	}
	
	@Test
	public void convertMap_toJavaBeanSet_TreeSet() throws Exception
	{
		convertMap_toJavaBeanSet(TreeSet.class);
	}
	
	@Test
	public void convertMap_toJavaBeanSet_LinkedHashSet() throws Exception
	{
		convertMap_toJavaBeanSet(LinkedHashSet.class);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void convertMap_toJavaBeanSet(Class<? extends Set> setClass) throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		GenericType genericType=GenericType.getGenericType(new MockParameterizedType(setClass, new Type[]{JavaBean.class}), null);
		
		Set<JavaBean> dest=(Set<JavaBean>)converter.convert(src, genericType);
		
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
	
	@Test
	public void convertMap_toJavaBeanArray()
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
	
	@Test
	public void convertMap_toGenericJavaBean()
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id={"11"};
		String[] property={"12", "13"};
		
		src.put("id", id);
		src.put("property", property);
		
		GenericJavaBeanSub dest=(GenericJavaBeanSub)converter.convert(src, GenericJavaBeanSub.class);
		
		Assert.assertEquals(new Integer(id[0]), dest.getId());
		
		for(int i=0; i<property.length; i++)
		{
			Assert.assertEquals(new Double(property[i]), dest.getProperty().get(i));
			Assert.assertEquals(new Double(property[i]), dest.getProperty().get(i));
		}
	}
	
	public static class JavaBean implements Comparable<JavaBean>
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
		
		//@Override
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
		
		//@Override
		public String toString() {
			return "JavaBean [name=" + name + ", age=" + age + ", birth="
					+ birth + "]";
		}
	}
	
	public static class JavaBean2 implements Comparable<JavaBean2>
	{
		private int id;
		private String name;
		private JavaBean javaBean;
		
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
		
		public JavaBean getJavaBean()
		{
			return javaBean;
		}
		public void setJavaBean(JavaBean javaBean)
		{
			this.javaBean = javaBean;
		}
		//@Override
		public int compareTo(JavaBean2 o)
		{
			if(o == null)
				return 1;
			
			int re= this.id == o.id ? 0 : (this.id>o.id ? 1 : -1);
			if(re == 0)
				re=this.name.compareTo(o.name);
			
			return re;
		}
		
		//@Override
		public String toString() {
			return "JavaBean2 [id=" + id + ", name=" + name + "]";
		}
	}
	
	public static class ComplexJavaBean implements Comparable<ComplexJavaBean>
	{
		private int id;
		private String name;
		
		private Integer[] simpleArray;
		
		private List<Integer> simpleList;
		
		private Set<Integer> simpleSet;
		
		private JavaBean2[] javaBean2Array;
		
		private List<JavaBean2> javaBean2List;
		
		private Set<JavaBean2> javaBean2Set;
		
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

		public Integer[] getSimpleArray() {
			return simpleArray;
		}

		public void setSimpleArray(Integer[] simpleArray) {
			this.simpleArray = simpleArray;
		}

		public JavaBean2[] getJavaBean2Array() {
			return javaBean2Array;
		}

		public void setJavaBean2Array(JavaBean2[] javaBean2Array) {
			this.javaBean2Array = javaBean2Array;
		}

		public List<JavaBean2> getJavaBean2List() {
			return javaBean2List;
		}

		public void setJavaBean2List(List<JavaBean2> javaBean2List) {
			this.javaBean2List = javaBean2List;
		}

		public Set<JavaBean2> getJavaBean2Set() {
			return javaBean2Set;
		}

		public void setJavaBean2Set(Set<JavaBean2> javaBean2Set) {
			this.javaBean2Set = javaBean2Set;
		}

		public List<Integer> getSimpleList() {
			return simpleList;
		}

		public void setSimpleList(List<Integer> simpleList) {
			this.simpleList = simpleList;
		}

		public Set<Integer> getSimpleSet() {
			return simpleSet;
		}

		public void setSimpleSet(Set<Integer> simpleSet) {
			this.simpleSet = simpleSet;
		}

		//@Override
		public int compareTo(ComplexJavaBean o)
		{
			if(o == null)
				return 1;
			
			int re= this.id == o.id ? 0 : (this.id>o.id ? 1 : -1);
			if(re == 0)
				re=this.name.compareTo(o.name);
			
			return re;
		}
	}
	
	public static class GenericJavaBean<T>
	{
		private Integer id;
		private List<T> property;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public List<T> getProperty() {
			return property;
		}
		public void setProperty(List<T> property) {
			this.property = property;
		}
	}
	
	public static class GenericJavaBeanSub extends GenericJavaBean<Double>{}
}
