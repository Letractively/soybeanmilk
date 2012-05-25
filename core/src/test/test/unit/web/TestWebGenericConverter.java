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

package test.unit.web;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.MapConvertException;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.os.DefaultWebObjectSource;
import org.soybeanMilk.web.os.ParamFilterMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestWebGenericConverter
{
	private WebGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter=new WebGenericConverter();
	}
	
	@Test
	public void convert_arrayToNotArrayObject() throws Exception
	{
		String[] src=new String[]{"12345", "56789"};
		
		Integer dest=converter.convert(src, int.class);
		Assert.assertEquals(src[0], dest.toString());
	}
	
	@Test
	public void convert_arrayToNotArrayObject_srcArrayEmpty() throws Exception
	{
		String[] src=new String[0];
		
		Integer dest=converter.convert(src, Integer.class);
		Assert.assertNull(dest);
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcElementSingleStringArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name=new String[]{"jack"};
		String[] age=new String[]{"15"};
		String[] birth=new String[]{"1900-10-21"};
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		JavaBean dest=converter.convert(src, JavaBean.class);
		
		Assert.assertEquals(name[0], dest.getName());
		Assert.assertEquals(new Integer(age[0]), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth[0]), dest.getBirth());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcElementMultiStringArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name=new String[]{"jack","tom"};
		String[] age=new String[]{"15","18"};
		String[] birth=new String[]{"1900-10-21","1900-10-22"};
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		JavaBean dest=converter.convert(src, JavaBean.class);
		
		Assert.assertEquals("jack", dest.getName());
		Assert.assertEquals(age[0], dest.getAge().toString());
		Assert.assertEquals(birth[0], new SimpleDateFormat("yyyy-MM-dd").format(dest.getBirth()));
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_collectionProperty_simpleKeyArrayValueMap() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","3","9"};
		
		String[] propsJavaBean2Collection_id=new String[]{"2","5","7"};
		String[] propsJavaBean2Collection_name=new String[]{"aaa","bbb","ccc"};
		
		String[] propsJavaBean2Collection_propsJavaBean_name=new String[]{"1-1", "1-2", "1-3"};
		String[] propsJavaBean2Collection_propsJavaBean_age=new String[]{"11", "12", "13"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("simpleArray", simpleCollectionProperty);
		src.put("simpleList", simpleCollectionProperty);
		src.put("simpleSet", simpleCollectionProperty);
		
		src.put("javaBean2Array.id", propsJavaBean2Collection_id);
		src.put("javaBean2Array.name", propsJavaBean2Collection_name);
		src.put("javaBean2Array.javaBean.name", propsJavaBean2Collection_propsJavaBean_name);
		src.put("javaBean2Array.javaBean.age", propsJavaBean2Collection_propsJavaBean_age);
		
		src.put("javaBean2List.id", propsJavaBean2Collection_id);
		src.put("javaBean2List.name", propsJavaBean2Collection_name);
		src.put("javaBean2List.javaBean.name", propsJavaBean2Collection_propsJavaBean_name);
		src.put("javaBean2List.javaBean.age", propsJavaBean2Collection_propsJavaBean_age);
		
		src.put("javaBean2Set.id", propsJavaBean2Collection_id);
		src.put("javaBean2Set.name", propsJavaBean2Collection_name);
		src.put("javaBean2Set.javaBean.name", propsJavaBean2Collection_propsJavaBean_name);
		src.put("javaBean2Set.javaBean.age", propsJavaBean2Collection_propsJavaBean_age);
		
		ComplexJavaBean dest=converter.convert(src, ComplexJavaBean.class);
		
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
				Assert.assertEquals(Integer.parseInt(propsJavaBean2Collection_id[i]), p[i].getId());
				Assert.assertEquals(propsJavaBean2Collection_name[i], p[i].getName());
				Assert.assertEquals(propsJavaBean2Collection_propsJavaBean_name[i], p[i].getJavaBean().getName());
				Assert.assertEquals(propsJavaBean2Collection_propsJavaBean_age[i], p[i].getJavaBean().getAge().toString());
			}
		}
		
		{
			List<JavaBean2> p=dest.getJavaBean2List();
			
			for(int i=0;i<p.size();i++)
			{
				Assert.assertEquals(Integer.parseInt(propsJavaBean2Collection_id[i]), p.get(i).getId());
				Assert.assertEquals(propsJavaBean2Collection_name[i], p.get(i).getName());
				Assert.assertEquals(propsJavaBean2Collection_propsJavaBean_name[i], p.get(i).getJavaBean().getName());
				Assert.assertEquals(propsJavaBean2Collection_propsJavaBean_age[i], p.get(i).getJavaBean().getAge().toString());
			}
		}
		
		{
			Set<JavaBean2> p=dest.getJavaBean2Set();
			for(JavaBean2 jb : p)
			{
				int idx=-1;
				for(int i=0;i<propsJavaBean2Collection_id.length;i++)
				{
					if(Integer.parseInt(propsJavaBean2Collection_id[i]) ==jb.getId()
							&& propsJavaBean2Collection_name[i].equals(jb.getName())
							&& propsJavaBean2Collection_propsJavaBean_name[i].equals(jb.getJavaBean().getName())
							&& propsJavaBean2Collection_propsJavaBean_age[i].equals(jb.getJavaBean().getAge().toString()))
							idx=i;
				}
				
				Assert.assertTrue( idx> -1 );
			}
		}
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcArrayPropertyContainIllegalValue() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","illegalValue","9"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("simpleArray", simpleCollectionProperty);
		
		MapConvertException re=null;
		try
		{
			converter.convert(src, ComplexJavaBean.class);
		}
		catch(MapConvertException e)
		{
			re=e;
		}
		
		Assert.assertEquals("simpleArray", re.getKey());
		Assert.assertEquals("illegalValue", re.getSourceObject());
		Assert.assertEquals(Integer.class, re.getTargetType());
	}

	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcContainInexistentJavaBeanProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","3","9"};
		
		src.put("id", id);
		src.put("name", name);
		src.put("simpleArray", simpleCollectionProperty);
		src.put("notAnyProperty", "aaaajskjewr");
		
		ComplexJavaBean dest=converter.convert(src, ComplexJavaBean.class);
		
		Assert.assertEquals(id[0], dest.getId()+"");
		Assert.assertEquals(name[0], dest.getName());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcContainInexistentSubJavaBeanProperty_inSubArrayProperty() throws Exception
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
		
		GenericConvertException re=null;
		try
		{
			converter.convert(src, ComplexJavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find property \"notExistsProperty\"")) );
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_paramFilterMap() throws Exception
	{
		Map<String,Object> src=new ParamFilterMap<Object>();
		
		String[] name=new String[]{"jack"};
		String[] age=new String[]{"15"};
		String[] birth=new String[]{"1900-10-21"};
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		src.put("illegalProperty", "1");
		
		GenericConvertException re=null;
		
		try
		{
			converter.convert(src, JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find property \"illegalProperty\"") );
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanIsGeneric() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id={"11"};
		String[] property={"12", "13"};
		
		src.put("id", id);
		src.put("list", property);
		src.put("array", property);
		src.put("obj", property[0]);
		
		GenericJavaBeanSub dest=converter.convert(src, GenericJavaBeanSub.class);
		
		Assert.assertEquals(new Integer(id[0]), dest.getId());
		Assert.assertEquals(new Double(property[0]), dest.getObj());
		for(int i=0; i<property.length; i++)
		{
			Assert.assertEquals(new Double(property[i]), dest.getList().get(i));
			Assert.assertEquals(new Double(property[i]), dest.getArray()[i]);
			
		}
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_customType_stringArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="aa";
		String age="11";
		String birth="1900-07-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		src.put("class", new String[]{JavaBean.class.getName(), JavaBean2.class.getName()});
		
		JavaBean dest=converter.convert(src, null);
		
		Assert.assertEquals(name, dest.getName());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_customType_classArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="aa";
		String age="11";
		String birth="1900-07-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		src.put("class", new Type[]{JavaBean.class, JavaBean2.class});
		
		JavaBean dest=converter.convert(src, null);
		
		Assert.assertEquals(name, dest.getName());
	}
	
	@Test
	public void convert_requestToTarget_noConverter() throws Exception
	{
		GenericConvertException re=null;
		
		try
		{
			converter.convert(new MockHttpServletRequest(), JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find Converter for converting")) );
	}
	
	@Test
	public void convert_requestToTarget_hasConverter() throws Exception
	{
		final JavaBean bean=new JavaBean();
		
		converter.addConverter(HttpServletRequest.class, JavaBean.class, new Converter()
		{
			@SuppressWarnings("unchecked")
			public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
			{
				return (T)bean;
			}
		});
		
		JavaBean re=converter.convert(new MockHttpServletRequest(), JavaBean.class);
		
		Assert.assertTrue( (re == bean) );
	}
	
	@Test
	public void convert_sessionToTarget_noConverter() throws Exception
	{
		GenericConvertException re=null;
		
		try
		{
			converter.convert(new MockHttpServletRequest().getSession(), JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find Converter for converting")) );
	}
	
	@Test
	public void convert_sessionToTarget_hasConverter() throws Exception
	{
		final JavaBean bean=new JavaBean();
		
		converter.addConverter(HttpSession.class, JavaBean.class, new Converter()
		{
			@SuppressWarnings("unchecked")
			public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
			{
				return (T)bean;
			}
		});
		
		JavaBean re=converter.convert(new MockHttpServletRequest().getSession(), JavaBean.class);
		
		Assert.assertTrue( (re == bean) );
	}
	
	@Test
	public void convert_servletContextToTarget_noConverter() throws Exception
	{
		GenericConvertException re=null;
		
		try
		{
			converter.convert(new MockServletContext(), JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find Converter for converting")) );
	}
	
	@Test
	public void convert_servletContextToTarget_hasConverter() throws Exception
	{
		final JavaBean bean=new JavaBean();
		
		converter.addConverter(ServletContext.class, JavaBean.class, new Converter()
		{
			@SuppressWarnings("unchecked")
			public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
			{
				return (T)bean;
			}
		});
		
		JavaBean re=converter.convert(new MockServletContext(), JavaBean.class);
		
		Assert.assertTrue( (re == bean) );
	}
	
	@Test
	public void convert_responseToTarget_noConverter() throws Exception
	{
		GenericConvertException re=null;
		
		try
		{
			converter.convert(new MockHttpServletResponse(), JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find Converter for converting")) );
	}
	
	@Test
	public void convert_responseToTarget_hasConverter() throws Exception
	{
		final JavaBean bean=new JavaBean();
		
		converter.addConverter(HttpServletResponse.class, JavaBean.class, new Converter()
		{
			@SuppressWarnings("unchecked")
			public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
			{
				return (T)bean;
			}
		});
		
		JavaBean re=converter.convert(new MockHttpServletResponse(), JavaBean.class);
		
		Assert.assertTrue( (re == bean) );
	}
	
	@Test
	public void convert_objectSourceToTarget_noConverter() throws Exception
	{
		GenericConvertException re=null;
		
		try
		{
			converter.convert(new DefaultWebObjectSource(), JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find Converter for converting")) );
	}
	
	@Test
	public void convert_objectSourceToTarget_hasConverter() throws Exception
	{
		final JavaBean bean=new JavaBean();
		
		converter.addConverter(WebObjectSource.class, JavaBean.class, new Converter()
		{
			@SuppressWarnings("unchecked")
			public <T> T convert(Object sourceObj, Type targetType) throws ConvertException
			{
				return (T)bean;
			}
		});
		
		JavaBean re=converter.convert(new DefaultWebObjectSource(), JavaBean.class);
		
		Assert.assertTrue( (re == bean) );
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
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JavaBean2 other = (JavaBean2) obj;
			if (id != other.id)
				return false;
			if (javaBean == null) {
				if (other.javaBean != null)
					return false;
			} else if (!javaBean.equals(other.javaBean))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		//@Override
		public String toString()
		{
			return "JavaBean2 [id=" + id + ", name=" + name + ", javaBean="
					+ javaBean + "]";
		}
	}
	
	public static class ComplexJavaBean implements Comparable<ComplexJavaBean>
	{
		private int id;
		private String name;
		
		private Integer[] simpleArray;
		
		private List<Integer> simpleList;
		
		private Set<Integer> simpleSet;
		
		private Map<String, Integer> simpleMap;
		
		private JavaBean2[] javaBean2Array;
		
		private List<JavaBean2> javaBean2List;
		
		private Set<JavaBean2> javaBean2Set;
		
		private Map<Integer, JavaBean2> javaBean2Map;
		
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

		public Map<String, Integer> getSimpleMap() {
			return simpleMap;
		}

		public void setSimpleMap(Map<String, Integer> simpleMap) {
			this.simpleMap = simpleMap;
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

		public Map<Integer, JavaBean2> getJavaBean2Map() {
			return javaBean2Map;
		}

		public void setJavaBean2Map(Map<Integer, JavaBean2> javaBean2Map) {
			this.javaBean2Map = javaBean2Map;
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
		private List<T> list;
		private T[] array;
		private T obj;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public List<T> getList() {
			return list;
		}
		public void setList(List<T> list) {
			this.list = list;
		}
		public T[] getArray() {
			return array;
		}
		public void setArray(T[] array) {
			this.array = array;
		}
		public T getObj() {
			return obj;
		}
		public void setObj(T obj) {
			this.obj = obj;
		}
	}
	
	public static class GenericJavaBeanSub extends GenericJavaBean<Double>{}
}
