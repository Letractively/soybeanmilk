package test.unit.web;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.soybeanMilk.web.bean.ParamConvertException;
import org.soybeanMilk.web.bean.ParamPropertyMap;
import org.soybeanMilk.web.bean.ParamValue;
import org.soybeanMilk.web.bean.WebGenericConverter;

import test.unit.core.MockGenericArrayType;
import test.unit.core.MockParameterizedType;
import test.unit.core.MockTypeVariable;
import test.unit.core.MockWildcardType;

public class TestWebGenericConverter
{
	private WebGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter=new WebGenericConverter();
	}
	
	@Test
	public void convertSimple_arrayToNoArrayObject()
	{
		Integer dest=(Integer)converter.convert(new String[]{"12345", "56789"}, int.class);
		Assert.assertEquals(12345, dest.intValue());
	}
	
	@Test
	public void convertSimple_arrayToNoArrayObject_1()
	{
		Integer dest=(Integer)converter.convert(new String[]{""}, Integer.class);
		Assert.assertNull(dest);
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
	
	@Test
	public void convertMap_toJavaBean_srcHasInexistentSubPropertyContain() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		src.put("javaBean.def", 356);
		
		GenericConvertException re=null;
		
		try
		{
			converter.convert(src, JavaBean2.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue(( re.getMessage().startsWith("can not find property 'def'") ));
	}
	
	@Test
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
		
		GenericConvertException re=null;
		try
		{
			converter.convert(src, ComplexJavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().startsWith("can not find property 'notExistsProperty'")) );
	}
	
	@Test
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
		
		GenericConvertException re=null;
		
		try
		{
			converter.convert(src, ComplexJavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue((re.getMessage().startsWith("can not find property 'notExistsProperty'")));
	}
	
	@Test
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
		
		GenericConvertException re=null;
		
		try
		{
			converter.convert(src, ComplexJavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue((re.getMessage().startsWith("can not find property 'notExistsProperty'")));
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
		
		JavaBean dest=(JavaBean)converter.convert(src, JavaBean.class);
		
		Assert.assertEquals("jack", dest.getName());
		Assert.assertEquals(age[0], dest.getAge().toString());
		Assert.assertEquals(birth[0], new SimpleDateFormat("yyyy-MM-dd").format(dest.getBirth()));
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
	public void convertMap_toJavaBean_collectionProperty_simpleKeyArrayValueMap() throws Exception
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
	public void convertParamValue_toAtomic() throws Exception
	{
		ParamValue src=new ParamValue("id", "3");
		
		Integer re=(Integer)converter.convert(src, Integer.class);
		
		Assert.assertEquals(src.getValue(), re.toString());
	}

	@Test
	public void convertParamValue_srcIsInvalidValue() throws Exception
	{
		ParamValue pv=new ParamValue("id", "invalidValue");
		
		ParamConvertException re=null;
		try
		{
			converter.convert(pv, int.class);
		}
		catch(ParamConvertException e)
		{
			re=e;
		}
		
		Assert.assertEquals("id", re.getParamName());
		Assert.assertEquals("invalidValue", re.getSourceObject());
		Assert.assertEquals(int.class, re.getTargetType());
	}
	
	@Test
	public void convertMap_toJavaBean_srcArrayPropertyContainInvalidValue() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","invalidValue","9"};
		
		src.put("filter.id", id);
		src.put("filter.name", name);
		
		src.put("filter.simpleArray", simpleCollectionProperty);
		
		ParamPropertyMap ppm=new ParamPropertyMap("filter");
		ppm.filter(src);
		
		ParamConvertException re=null;
		try
		{
			converter.convert(ppm, ComplexJavaBean.class);
		}
		catch(ParamConvertException e)
		{
			re=e;
		}
		
		Assert.assertEquals("filter.simpleArray", re.getParamName());
		Assert.assertEquals("invalidValue", re.getSourceObject());
		Assert.assertEquals(Integer.class, re.getTargetType());
	}
	
	@Test
	public void convertMap_toJavaBean_srcComplexPropertyContainInvalidValue() throws Exception
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
		
		ParamPropertyMap ppm=new ParamPropertyMap("filter");
		ppm.filter(src);
		
		ParamConvertException re=null;
		try
		{
			converter.convert(ppm, ComplexJavaBean.class);
		}
		catch(ParamConvertException e)
		{
			re=e;
		}
		
		Assert.assertEquals("filter.javaBean2List.id", re.getParamName());
		Assert.assertEquals("invalidValue", re.getSourceObject());
		Assert.assertEquals(int.class, re.getTargetType());
	}
	
	@Test
	public void convertMap_toGeneric_TypeVariable() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		@SuppressWarnings("rawtypes")
		Type type=new MockTypeVariable("T", new Type[]{JavaBean.class});
		
		JavaBean dest=(JavaBean)converter.convert(src, type);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convertMap_toGeneric_ParameterizedType() throws Exception
	{
		convertMap_toJavaBeanList(List.class);
	}

	@Test
	public void convertMap_toGeneric_ParameterizedType_notSupported() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		Exception re=null;
		
		try
		{
			Type type=new MockParameterizedType(JavaBean.class, JavaBean.class);
			converter.convert(src, type);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().endsWith("is not supported") );
	}
	
	@Test
	public void convertMap_toGeneric_GenericArrayType() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		Type type=new MockGenericArrayType(JavaBean.class);
		JavaBean[] dest=(JavaBean[])converter.convert(src, type);
		
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
	public void convertMap_toGeneric_WildcardType() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		MockWildcardType type=new MockWildcardType();
		type.setUpperBounds(new Type[]{JavaBean.class});
		
		JavaBean dest=(JavaBean)converter.convert(src, type);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convertMap_toGeneric_GenericType() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		MockWildcardType tp=new MockWildcardType();
		tp.setUpperBounds(new Type[]{JavaBean.class});
		
		Type type=GenericType.getGenericType(tp, null);
		
		JavaBean dest=(JavaBean)converter.convert(src, type);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanCollection_keyComplexSemantics_invalidValue() throws Exception
	{
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleArray.0", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.simpleArray.0");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), Integer.class);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleList.0", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.simpleList.0");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), Integer.class);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleSet.0", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.simpleSet.0");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), Integer.class);
		}

		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleMap.key", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.simpleMap.key");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), Integer.class);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2Array.0.id", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.javaBean2Array.0.id");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), int.class);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2List.0.id", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.javaBean2List.0.id");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), int.class);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2Set.0.id", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.javaBean2Set.0.id");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), int.class);
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2Map.0.id", "invalidValue");
			
			ParamConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(ParamConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals(re.getParamName(), "0.javaBean2Map.0.id");
			Assert.assertEquals(re.getSourceObject(), "invalidValue");
			Assert.assertEquals(re.getTargetType(), int.class);
		}
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanCollection_keyComplexSemantics_invalidKey() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		src.put("0.javaBean2Map.invalidKey.id", "1");
		
		GenericConvertException re=null;
		try
		{
			converter.convert(src, ComplexJavaBean[].class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue(( re.getMessage().startsWith("convert 'invalidKey' in param name '0.javaBean2Map.invalidKey' to Map key of type") ));
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanArray_keyComplexSemantics() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] ids={"1", "2", "3"};
		String[] names={"name_1", "name_2", "name_3"};
		String[][] propsSimpleCollection=new String[][]
		            {
						{"11", "12", "13"},
						{"21", "22", "23", "24"},
						{"31", "32", "33"}
		            };
		String[][] propsSimpleKeys=new String[][]
					{
						{"key_1_1", "key_1_2"},
						{"key_2_1", "key_2_2", "key_2_3", "key_2_4"},
						{"key_3_1"},
					};
		String[][] propsSimpleValues=new String[][]
					{
						{"11", "12"},
						{"21", "22", "23", "24"},
						{"31"},
					};
		String[][] propsJavaBeanCollection_id=new String[][]
   		            {
   						{"11", "12"},
   						{"21", "22", "23", "24"},
   						{"31"}
   		            };
		String[][] propsJavaBeanCollection_name=new String[][]
   		            {
   						{"1_1", "1_2"},
   						{"2_1", "2_2", "2_3", "2_4"},
   						{"3_1"}
   		            };
		String[][] propsJavaBeanMapKeys=new String[][]
				{
					{"11", "12"},
					{"21", "22", "23", "24"},
					{"31"},
				};
		String[][] propsJavaBeanMapValuesId=new String[][]
				{
					{"11", "12"},
					{"21", "22", "23", "24"},
					{"31"},
				};
		String[][] propsJavaBeanMapValuesName=new String[][]
				{
					{"1_1", "1_2"},
					{"2_1", "2_2", "2_3", "2_4"},
					{"3_1"},
				};
		
		
		src.put("id", ids);
		src.put("name", names);
		
		//简单集合属性
		{
			src.put("0.simpleArray", propsSimpleCollection[0]);
			src.put("0.simpleList", propsSimpleCollection[0]);
			src.put("0.simpleSet", propsSimpleCollection[0]);
			
			src.put("1.simpleArray", propsSimpleCollection[1]);
			src.put("1.simpleList", propsSimpleCollection[1]);
			src.put("1.simpleSet", propsSimpleCollection[1]);
			
			for(int i=0; i<propsSimpleCollection[2].length; i++)
			{
				src.put("2.simpleArray."+i, propsSimpleCollection[2][i]);
				src.put("2.simpleList."+i, propsSimpleCollection[2][i]);
				src.put("2.simpleSet."+i, propsSimpleCollection[2][i]);
			}
			
			for(int i=0; i<propsSimpleKeys.length; i++)
			{
				for(int j=0; j<propsSimpleKeys[i].length; j++)
				{
					src.put(i+".simpleMap."+propsSimpleKeys[i][j], propsSimpleValues[i][j]);
				}
			}
		}
		
		//JavaBean集合属性
		{
			for(int i=0; i<propsJavaBeanCollection_name[0].length; i++)
			{
				src.put("0.javaBean2Array."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2List."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2Set."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2Array."+i+".name", propsJavaBeanCollection_name[0][i]);
				src.put("0.javaBean2List."+i+".name", propsJavaBeanCollection_name[0][i]);
				src.put("0.javaBean2Set."+i+".name", propsJavaBeanCollection_name[0][i]);
			}
			
			src.put("1.javaBean2Array.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2List.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2Set.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2Array.name", propsJavaBeanCollection_name[1]);
			src.put("1.javaBean2List.name", propsJavaBeanCollection_name[1]);
			src.put("1.javaBean2Set.name", propsJavaBeanCollection_name[1]);
			
			src.put("2.javaBean2Array.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2List.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2Set.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2Array.name", propsJavaBeanCollection_name[2]);
			src.put("2.javaBean2List.name", propsJavaBeanCollection_name[2]);
			src.put("2.javaBean2Set.name", propsJavaBeanCollection_name[2]);
			
			for(int i=0; i<propsJavaBeanMapKeys.length; i++)
			{
				for(int j=0; j<propsJavaBeanMapKeys[i].length; j++)
				{
					src.put(i+".javaBean2Map."+propsJavaBeanMapKeys[i][j]+".id", propsJavaBeanMapValuesId[i][j]);
					src.put(i+".javaBean2Map."+propsJavaBeanMapKeys[i][j]+".name", propsJavaBeanMapValuesName[i][j]);
				}
			}
		}
		
		ComplexJavaBean[] re=(ComplexJavaBean[])converter.convert(src, ComplexJavaBean[].class);
		
		Assert.assertEquals(ids.length, re.length);
		
		for(int i=0; i<ids.length; i++)
		{
			ComplexJavaBean rei=re[i];
			
			Assert.assertEquals(ids[i], rei.getId()+"");
			Assert.assertEquals(names[i], rei.getName());
			
			//简单集合属性
			{
				Integer[] simplePropsArray=stringArrayToIntArray(propsSimpleCollection[i]);
				Set<Integer> simplePropsSet=new HashSet<Integer>();
				for(Integer it : simplePropsArray)
					simplePropsSet.add(it);
				
				Integer[] simplePropArrayi=rei.getSimpleArray();
				Integer[] simplePropListi=new Integer[simplePropsArray.length];
				rei.getSimpleList().toArray(simplePropListi);
				Set<Integer> simplePropSeti=rei.getSimpleSet();
				Map<String, Integer> simplePropsMapi=rei.getSimpleMap();
				
				Assert.assertTrue(Arrays.equals(simplePropsArray, simplePropArrayi));
				Assert.assertTrue(Arrays.equals(simplePropsArray, simplePropListi));
				Assert.assertEquals(new TreeSet<Integer>(simplePropsSet), new TreeSet<Integer>(simplePropSeti));
				for(int j=0; j<propsSimpleKeys[i].length; j++)
				{
					Integer v=Integer.parseInt(propsSimpleValues[i][j]);
					Assert.assertEquals(v, simplePropsMapi.get(propsSimpleKeys[i][j]));
				}
			}
			
			//JavaBean集合属性
			{
				JavaBean2[] javaBeanPropsArray=new JavaBean2[propsJavaBeanCollection_id[i].length];
				for(int j=0; j<javaBeanPropsArray.length; j++)
				{
					javaBeanPropsArray[j]=new JavaBean2();
					javaBeanPropsArray[j].setId(Integer.parseInt(propsJavaBeanCollection_id[i][j]));
					javaBeanPropsArray[j].setName(propsJavaBeanCollection_name[i][j]);
				}
				Set<JavaBean2> javaBeanPropsSet=new HashSet<JavaBean2>();
				for(JavaBean2 jb : javaBeanPropsArray)
				{
					javaBeanPropsSet.add(jb);
				}
				
				JavaBean2[] javaBeanPropsArrayi=rei.getJavaBean2Array();
				JavaBean2[] javaBeanPropsListi=new JavaBean2[javaBeanPropsArray.length];
				rei.getJavaBean2List().toArray(javaBeanPropsListi);
				Set<JavaBean2> javaBeanPropsSeti=rei.getJavaBean2Set();
				Map<Integer, JavaBean2> javaBeanPropsMapi=rei.getJavaBean2Map();
				
				Assert.assertTrue(Arrays.equals(javaBeanPropsArray, javaBeanPropsArrayi));
				Assert.assertTrue(Arrays.equals(javaBeanPropsArray, javaBeanPropsListi));
				Assert.assertEquals(new TreeSet<JavaBean2>(javaBeanPropsSet), new TreeSet<JavaBean2>(javaBeanPropsSeti));
				for(int j=0; j<propsJavaBeanMapKeys[i].length; j++)
				{
					Integer key=Integer.parseInt(propsJavaBeanMapKeys[i][j]);
					JavaBean2 jb2=javaBeanPropsMapi.get(key);
					
					Assert.assertEquals(new Integer(propsJavaBeanMapValuesId[i][j]), Integer.valueOf(jb2.getId()));
					Assert.assertEquals(propsJavaBeanMapValuesName[i][j], jb2.getName());
				}
			}
		}
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanList_keyComplexSemantics() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] ids={"1", "2", "3"};
		String[] names={"name_1", "name_2", "name_3"};
		String[][] propsSimpleCollection=new String[][]
		            {
						{"11", "12", "13"},
						{"21", "22", "23", "24"},
						{"31", "32", "33"}
		            };
		String[][] propsSimpleKeys=new String[][]
					{
						{"key_1_1", "key_1_2"},
						{"key_2_1", "key_2_2", "key_2_3", "key_2_4"},
						{"key_3_1"},
					};
		String[][] propsSimpleValues=new String[][]
					{
						{"11", "12"},
						{"21", "22", "23", "24"},
						{"31"},
					};
		String[][] propsJavaBeanCollection_id=new String[][]
   		            {
   						{"11", "12"},
   						{"21", "22", "23", "24"},
   						{"31"}
   		            };
		String[][] propsJavaBeanCollection_name=new String[][]
   		            {
   						{"1_1", "1_2"},
   						{"2_1", "2_2", "2_3", "2_4"},
   						{"3_1"}
   		            };
		String[][] propsJavaBeanMapKeys=new String[][]
				{
					{"11", "12"},
					{"21", "22", "23", "24"},
					{"31"},
				};
		String[][] propsJavaBeanMapValuesId=new String[][]
				{
					{"11", "12"},
					{"21", "22", "23", "24"},
					{"31"},
				};
		String[][] propsJavaBeanMapValuesName=new String[][]
				{
					{"1_1", "1_2"},
					{"2_1", "2_2", "2_3", "2_4"},
					{"3_1"},
				};
		
		
		src.put("id", ids);
		src.put("name", names);
		
		//简单集合属性
		{
			src.put("0.simpleArray", propsSimpleCollection[0]);
			src.put("0.simpleList", propsSimpleCollection[0]);
			src.put("0.simpleSet", propsSimpleCollection[0]);
			
			src.put("1.simpleArray", propsSimpleCollection[1]);
			src.put("1.simpleList", propsSimpleCollection[1]);
			src.put("1.simpleSet", propsSimpleCollection[1]);
			
			for(int i=0; i<propsSimpleCollection[2].length; i++)
			{
				src.put("2.simpleArray."+i, propsSimpleCollection[2][i]);
				src.put("2.simpleList."+i, propsSimpleCollection[2][i]);
				src.put("2.simpleSet."+i, propsSimpleCollection[2][i]);
			}
			
			for(int i=0; i<propsSimpleKeys.length; i++)
			{
				for(int j=0; j<propsSimpleKeys[i].length; j++)
				{
					src.put(i+".simpleMap."+propsSimpleKeys[i][j], propsSimpleValues[i][j]);
				}
			}
		}
		
		//JavaBean集合属性
		{
			for(int i=0; i<propsJavaBeanCollection_name[0].length; i++)
			{
				src.put("0.javaBean2Array."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2List."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2Set."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2Array."+i+".name", propsJavaBeanCollection_name[0][i]);
				src.put("0.javaBean2List."+i+".name", propsJavaBeanCollection_name[0][i]);
				src.put("0.javaBean2Set."+i+".name", propsJavaBeanCollection_name[0][i]);
			}
			
			src.put("1.javaBean2Array.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2List.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2Set.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2Array.name", propsJavaBeanCollection_name[1]);
			src.put("1.javaBean2List.name", propsJavaBeanCollection_name[1]);
			src.put("1.javaBean2Set.name", propsJavaBeanCollection_name[1]);
			
			src.put("2.javaBean2Array.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2List.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2Set.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2Array.name", propsJavaBeanCollection_name[2]);
			src.put("2.javaBean2List.name", propsJavaBeanCollection_name[2]);
			src.put("2.javaBean2Set.name", propsJavaBeanCollection_name[2]);
			
			for(int i=0; i<propsJavaBeanMapKeys.length; i++)
			{
				for(int j=0; j<propsJavaBeanMapKeys[i].length; j++)
				{
					src.put(i+".javaBean2Map."+propsJavaBeanMapKeys[i][j]+".id", propsJavaBeanMapValuesId[i][j]);
					src.put(i+".javaBean2Map."+propsJavaBeanMapKeys[i][j]+".name", propsJavaBeanMapValuesName[i][j]);
				}
			}
		}
		
		Type listType=new MockParameterizedType(List.class, ComplexJavaBean.class);
		@SuppressWarnings("unchecked")
		List<ComplexJavaBean> re=(List<ComplexJavaBean>)converter.convert(src, listType);
		
		Assert.assertEquals(ids.length, re.size());
		
		for(int i=0; i<ids.length; i++)
		{
			ComplexJavaBean rei=re.get(i);
			
			Assert.assertEquals(ids[i], rei.getId()+"");
			Assert.assertEquals(names[i], rei.getName());
			
			//简单集合属性
			{
				Integer[] simplePropsArray=stringArrayToIntArray(propsSimpleCollection[i]);
				Set<Integer> simplePropsSet=new HashSet<Integer>();
				for(Integer it : simplePropsArray)
					simplePropsSet.add(it);
				
				Integer[] simplePropArrayi=rei.getSimpleArray();
				Integer[] simplePropListi=new Integer[simplePropsArray.length];
				rei.getSimpleList().toArray(simplePropListi);
				Set<Integer> simplePropSeti=rei.getSimpleSet();
				Map<String, Integer> simplePropsMapi=rei.getSimpleMap();
				
				Assert.assertTrue(Arrays.equals(simplePropsArray, simplePropArrayi));
				Assert.assertTrue(Arrays.equals(simplePropsArray, simplePropListi));
				Assert.assertEquals(new TreeSet<Integer>(simplePropsSet), new TreeSet<Integer>(simplePropSeti));
				for(int j=0; j<propsSimpleKeys[i].length; j++)
				{
					Integer v=Integer.parseInt(propsSimpleValues[i][j]);
					Assert.assertEquals(v, simplePropsMapi.get(propsSimpleKeys[i][j]));
				}
			}
			
			//JavaBean集合属性
			{
				JavaBean2[] javaBeanPropsArray=new JavaBean2[propsJavaBeanCollection_id[i].length];
				for(int j=0; j<javaBeanPropsArray.length; j++)
				{
					javaBeanPropsArray[j]=new JavaBean2();
					javaBeanPropsArray[j].setId(Integer.parseInt(propsJavaBeanCollection_id[i][j]));
					javaBeanPropsArray[j].setName(propsJavaBeanCollection_name[i][j]);
				}
				Set<JavaBean2> javaBeanPropsSet=new HashSet<JavaBean2>();
				for(JavaBean2 jb : javaBeanPropsArray)
				{
					javaBeanPropsSet.add(jb);
				}
				
				JavaBean2[] javaBeanPropsArrayi=rei.getJavaBean2Array();
				JavaBean2[] javaBeanPropsListi=new JavaBean2[javaBeanPropsArray.length];
				rei.getJavaBean2List().toArray(javaBeanPropsListi);
				Set<JavaBean2> javaBeanPropsSeti=rei.getJavaBean2Set();
				Map<Integer, JavaBean2> javaBeanPropsMapi=rei.getJavaBean2Map();
				
				Assert.assertTrue(Arrays.equals(javaBeanPropsArray, javaBeanPropsArrayi));
				Assert.assertTrue(Arrays.equals(javaBeanPropsArray, javaBeanPropsListi));
				Assert.assertEquals(new TreeSet<JavaBean2>(javaBeanPropsSet), new TreeSet<JavaBean2>(javaBeanPropsSeti));
				for(int j=0; j<propsJavaBeanMapKeys[i].length; j++)
				{
					Integer key=Integer.parseInt(propsJavaBeanMapKeys[i][j]);
					JavaBean2 jb2=javaBeanPropsMapi.get(key);
					
					Assert.assertEquals(new Integer(propsJavaBeanMapValuesId[i][j]), Integer.valueOf(jb2.getId()));
					Assert.assertEquals(propsJavaBeanMapValuesName[i][j], jb2.getName());
				}
			}
		}
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanSet_keyComplexSemantics() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] ids={"1", "2", "3"};
		String[] names={"name_1", "name_2", "name_3"};
		String[][] propsSimpleCollection=new String[][]
		            {
						{"11", "12", "13"},
						{"21", "22", "23", "24"},
						{"31", "32", "33"}
		            };
		String[][] propsSimpleKeys=new String[][]
					{
						{"key_1_1", "key_1_2"},
						{"key_2_1", "key_2_2", "key_2_3", "key_2_4"},
						{"key_3_1"},
					};
		String[][] propsSimpleValues=new String[][]
					{
						{"11", "12"},
						{"21", "22", "23", "24"},
						{"31"},
					};
		String[][] propsJavaBeanCollection_id=new String[][]
   		            {
   						{"11", "12"},
   						{"21", "22", "23", "24"},
   						{"31"}
   		            };
		String[][] propsJavaBeanCollection_name=new String[][]
   		            {
   						{"1_1", "1_2"},
   						{"2_1", "2_2", "2_3", "2_4"},
   						{"3_1"}
   		            };
		String[][] propsJavaBeanMapKeys=new String[][]
				{
					{"11", "12"},
					{"21", "22", "23", "24"},
					{"31"},
				};
		String[][] propsJavaBeanMapValuesId=new String[][]
				{
					{"11", "12"},
					{"21", "22", "23", "24"},
					{"31"},
				};
		String[][] propsJavaBeanMapValuesName=new String[][]
				{
					{"1_1", "1_2"},
					{"2_1", "2_2", "2_3", "2_4"},
					{"3_1"},
				};
		
		
		src.put("id", ids);
		src.put("name", names);
		
		//简单集合属性
		{
			src.put("0.simpleArray", propsSimpleCollection[0]);
			src.put("0.simpleList", propsSimpleCollection[0]);
			src.put("0.simpleSet", propsSimpleCollection[0]);
			
			src.put("1.simpleArray", propsSimpleCollection[1]);
			src.put("1.simpleList", propsSimpleCollection[1]);
			src.put("1.simpleSet", propsSimpleCollection[1]);
			
			for(int i=0; i<propsSimpleCollection[2].length; i++)
			{
				src.put("2.simpleArray."+i, propsSimpleCollection[2][i]);
				src.put("2.simpleList."+i, propsSimpleCollection[2][i]);
				src.put("2.simpleSet."+i, propsSimpleCollection[2][i]);
			}
			
			for(int i=0; i<propsSimpleKeys.length; i++)
			{
				for(int j=0; j<propsSimpleKeys[i].length; j++)
				{
					src.put(i+".simpleMap."+propsSimpleKeys[i][j], propsSimpleValues[i][j]);
				}
			}
		}
		
		//JavaBean集合属性
		{
			for(int i=0; i<propsJavaBeanCollection_name[0].length; i++)
			{
				src.put("0.javaBean2Array."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2List."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2Set."+i+".id", propsJavaBeanCollection_id[0][i]);
				src.put("0.javaBean2Array."+i+".name", propsJavaBeanCollection_name[0][i]);
				src.put("0.javaBean2List."+i+".name", propsJavaBeanCollection_name[0][i]);
				src.put("0.javaBean2Set."+i+".name", propsJavaBeanCollection_name[0][i]);
			}
			
			src.put("1.javaBean2Array.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2List.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2Set.id", propsJavaBeanCollection_id[1]);
			src.put("1.javaBean2Array.name", propsJavaBeanCollection_name[1]);
			src.put("1.javaBean2List.name", propsJavaBeanCollection_name[1]);
			src.put("1.javaBean2Set.name", propsJavaBeanCollection_name[1]);
			
			src.put("2.javaBean2Array.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2List.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2Set.id", propsJavaBeanCollection_id[2]);
			src.put("2.javaBean2Array.name", propsJavaBeanCollection_name[2]);
			src.put("2.javaBean2List.name", propsJavaBeanCollection_name[2]);
			src.put("2.javaBean2Set.name", propsJavaBeanCollection_name[2]);
			
			for(int i=0; i<propsJavaBeanMapKeys.length; i++)
			{
				for(int j=0; j<propsJavaBeanMapKeys[i].length; j++)
				{
					src.put(i+".javaBean2Map."+propsJavaBeanMapKeys[i][j]+".id", propsJavaBeanMapValuesId[i][j]);
					src.put(i+".javaBean2Map."+propsJavaBeanMapKeys[i][j]+".name", propsJavaBeanMapValuesName[i][j]);
				}
			}
		}
		
		Type setType=new MockParameterizedType(Set.class, ComplexJavaBean.class);
		@SuppressWarnings("unchecked")
		Set<ComplexJavaBean> re=(Set<ComplexJavaBean>)converter.convert(src, setType);
		re=new TreeSet<ComplexJavaBean>(re);
		
		Assert.assertEquals(ids.length, re.size());
		
		int i=0;
		for(ComplexJavaBean rei : re)
		{
			Assert.assertEquals(ids[i], rei.getId()+"");
			Assert.assertEquals(names[i], rei.getName());
			
			//简单集合属性
			{
				Integer[] simplePropsArray=stringArrayToIntArray(propsSimpleCollection[i]);
				Set<Integer> simplePropsSet=new HashSet<Integer>();
				for(Integer it : simplePropsArray)
					simplePropsSet.add(it);
				
				Integer[] simplePropArrayi=rei.getSimpleArray();
				Integer[] simplePropListi=new Integer[simplePropsArray.length];
				rei.getSimpleList().toArray(simplePropListi);
				Set<Integer> simplePropSeti=rei.getSimpleSet();
				Map<String, Integer> simplePropsMapi=rei.getSimpleMap();
				
				Assert.assertTrue(Arrays.equals(simplePropsArray, simplePropArrayi));
				Assert.assertTrue(Arrays.equals(simplePropsArray, simplePropListi));
				Assert.assertEquals(new TreeSet<Integer>(simplePropsSet), new TreeSet<Integer>(simplePropSeti));
				for(int j=0; j<propsSimpleKeys[i].length; j++)
				{
					Integer v=Integer.parseInt(propsSimpleValues[i][j]);
					Assert.assertEquals(v, simplePropsMapi.get(propsSimpleKeys[i][j]));
				}
			}
			
			//JavaBean集合属性
			{
				JavaBean2[] javaBeanPropsArray=new JavaBean2[propsJavaBeanCollection_id[i].length];
				for(int j=0; j<javaBeanPropsArray.length; j++)
				{
					javaBeanPropsArray[j]=new JavaBean2();
					javaBeanPropsArray[j].setId(Integer.parseInt(propsJavaBeanCollection_id[i][j]));
					javaBeanPropsArray[j].setName(propsJavaBeanCollection_name[i][j]);
				}
				Set<JavaBean2> javaBeanPropsSet=new HashSet<JavaBean2>();
				for(JavaBean2 jb : javaBeanPropsArray)
				{
					javaBeanPropsSet.add(jb);
				}
				
				JavaBean2[] javaBeanPropsArrayi=rei.getJavaBean2Array();
				JavaBean2[] javaBeanPropsListi=new JavaBean2[javaBeanPropsArray.length];
				rei.getJavaBean2List().toArray(javaBeanPropsListi);
				Set<JavaBean2> javaBeanPropsSeti=rei.getJavaBean2Set();
				Map<Integer, JavaBean2> javaBeanPropsMapi=rei.getJavaBean2Map();
				
				Assert.assertTrue(Arrays.equals(javaBeanPropsArray, javaBeanPropsArrayi));
				Assert.assertTrue(Arrays.equals(javaBeanPropsArray, javaBeanPropsListi));
				Assert.assertEquals(new TreeSet<JavaBean2>(javaBeanPropsSet), new TreeSet<JavaBean2>(javaBeanPropsSeti));
				for(int j=0; j<propsJavaBeanMapKeys[i].length; j++)
				{
					Integer key=Integer.parseInt(propsJavaBeanMapKeys[i][j]);
					JavaBean2 jb2=javaBeanPropsMapi.get(key);
					
					Assert.assertEquals(new Integer(propsJavaBeanMapValuesId[i][j]), Integer.valueOf(jb2.getId()));
					Assert.assertEquals(propsJavaBeanMapValuesName[i][j], jb2.getName());
				}
			}
			
			i++;
		}
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanList() throws Exception
	{
		convertMap_toJavaBeanList(List.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanArrayList() throws Exception
	{
		convertMap_toJavaBeanList(ArrayList.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanLinkedList() throws Exception
	{
		convertMap_toJavaBeanList(LinkedList.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanVector() throws Exception
	{
		convertMap_toJavaBeanList(Vector.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanStack() throws Exception
	{
		convertMap_toJavaBeanList(Stack.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanSet() throws Exception
	{
		convertMap_toJavaBeanSet(Set.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBean_HashSet() throws Exception
	{
		convertMap_toJavaBeanSet(HashSet.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanTreeSet() throws Exception
	{
		convertMap_toJavaBeanSet(TreeSet.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanLinkedHashSet() throws Exception
	{
		convertMap_toJavaBeanSet(LinkedHashSet.class);
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanMap() throws Exception
	{
		Map<String, String> src=new HashMap<String, String>();
		
		String[] keys=new String[]
				{
					"key_1", "key_2", "key_3"
				};
		String[] names=new String[]
				{
					"name_1", "name_2", "name_3"
				};
		String[] ages=new String[]
				{
					"11", "12", "13"
				};
		for(int i=0; i<keys.length; i++)
		{
			src.put(keys[i]+".name", names[i]);
			src.put(keys[i]+".age", ages[i]);
		}
		
		Type mapType=new MockParameterizedType(Map.class, String.class, JavaBean.class);
		@SuppressWarnings("unchecked")
		Map<String, JavaBean> dest=(Map<String, JavaBean>)converter.convert(src, mapType);
		
		for(int i=0; i<keys.length; i++)
		{
			JavaBean jb=dest.get(keys[i]);
			
			Assert.assertEquals(names[i], jb.getName());
			Assert.assertEquals(new Integer(ages[i]), jb.getAge());
		}
	}
	
	@Test
	public void convertMap_toGeneric_JavaBeanIsGeneric()
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id={"11"};
		String[] property={"12", "13"};
		
		src.put("id", id);
		src.put("list", property);
		src.put("array", property);
		src.put("obj", property[0]);
		
		GenericJavaBeanSub dest=(GenericJavaBeanSub)converter.convert(src, GenericJavaBeanSub.class);
		
		Assert.assertEquals(new Integer(id[0]), dest.getId());
		Assert.assertEquals(new Double(property[0]), dest.getObj());
		for(int i=0; i<property.length; i++)
		{
			Assert.assertEquals(new Double(property[i]), dest.getList().get(i));
			Assert.assertEquals(new Double(property[i]), dest.getArray()[i]);
			
		}
	}
	
	@Test
	public void convertMap_toRawMap()
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Object dest=converter.convert(src, Map.class);
		
		Assert.assertTrue(src == dest);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertMap_toRawList() throws Exception
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
	public void convertMap_toRawSet() throws Exception
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
			
			Type type=new MockParameterizedType(listClass, new Type[]{JavaBean.class});
			List<JavaBean> dest=(List<JavaBean>)converter.convert(src, type);
			
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
		
		Type type=new MockParameterizedType(setClass, new Type[]{JavaBean.class});
		
		Set<JavaBean> dest=(Set<JavaBean>)converter.convert(src, type);
		
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
	
	protected Integer[] stringArrayToIntArray(String[] strs)
	{
		Integer[] re=new Integer[strs.length];
		
		for(int i=0; i<re.length; i++)
		{
			re[i]=Integer.parseInt(strs[i]);
		}
		
		return re;
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
