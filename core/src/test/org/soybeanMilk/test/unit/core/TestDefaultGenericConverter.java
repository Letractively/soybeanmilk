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

package org.soybeanMilk.test.unit.core;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.MapConvertException;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestDefaultGenericConverter
{
	public static final String KEY_CLASS=DefaultGenericConverter.KEY_CUSTOM_CLASS;
	public static final String KEY_CLASSES=DefaultGenericConverter.KEY_CUSTOM_ELEMENT_CLASSES;
	
	private DefaultGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter = new DefaultGenericConverter();
	}
	
	@Test
	public void convert_targetTypeIsNull() throws Exception
	{
		Integer src=2356;
		
		Integer result=converter.convert(src, null);
		
		Assert.assertTrue( (src == result) );
	}
	
	@Test
	public void convert_sourceIsIntanceOfTargetType() throws Exception
	{
		Map<String, Object> src=new HashMap<String, Object>();
		
		Map<String, Object> result=converter.convert(src, Map.class);
		
		Assert.assertTrue( (src == result) );
	}
	
	@Test
	public void convert_nullToPrimitive() throws Exception
	{
		String src=null;
		
		GenericConvertException re=null;
		
		try
		{
			converter.convert(src, int.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().endsWith(" to primitive type "+SbmUtils.toString(int.class))) );
	}
	
	@Test
	public void convert_primitiveValueToPrimitive() throws Exception
	{
		int src=3;
		
		Integer re=converter.convert(src, int.class);
		
		Assert.assertEquals(src, re.intValue());
	}
	
	@Test
	public void convert_nullToObject() throws Exception
	{
		String src=null;
		
		Integer re=converter.convert(src, Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void convert_emptyStringToPrimitive() throws Exception
	{
		String src="";
		
		ConvertException re=null;
		
		try
		{
			converter.convert(src, int.class);
		}
		catch(ConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getMessage().endsWith(" to primitive type "+SbmUtils.toString(int.class))) );
	}
	
	@Test
	public void convert_emptyStringToObject() throws Exception
	{
		String src="";
		
		Integer re=converter.convert(src, Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test
	public void convert_stringToBigDecimal() throws Exception
	{
		String src = "1254324.3823823";
		
		BigDecimal dest = converter.convert(src, BigDecimal.class);
		
		Assert.assertEquals(new BigDecimal(src), dest);
	}
	
	@Test
	public void convert_stringToBigInteger() throws Exception
	{
		String src = "12349787293841930481029348234242134";
		
		BigInteger dest = converter.convert(src, BigInteger.class);
		
		Assert.assertEquals(new BigInteger(src), dest);
	}
	
	@Test
	public void convert_stringToBoolean() throws Exception
	{
		{
			String src = "true";
			Boolean dest = converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "1";
			Boolean dest = converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "false";
			Boolean dest = converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
		
		{
			String src = "0";
			Boolean dest = converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
		
		{
			String src = "true";
			Boolean dest = converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "1";
			Boolean dest = converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "false";
			Boolean dest = converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
		
		{
			String src = "0";
			Boolean dest = converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
	}
	
	@Test
	public void convert_stringToByte() throws Exception
	{
		{
			String src = "5";
			Byte dest = converter.convert(src, byte.class);
			
			Assert.assertEquals(new Byte(src), dest);
		}
		
		{
			String src = "5";
			Byte dest = converter.convert(src, Byte.class);
			
			Assert.assertEquals(new Byte(src), dest);
		}
	}
	
	@Test
	public void convert_stringToCharacter() throws Exception
	{
		{
			String src = "2";
			
			Character dest = converter.convert(src, Character.class);
			Assert.assertEquals(new Character('2'), dest);
		}
		
		{
			String src = "2";
			
			Character dest = converter.convert(src, char.class);
			Assert.assertEquals('2', dest.charValue());
		}
	}
	
	@Test
	public void convert_stringToDate() throws Exception
	{
		{
			String src = "2010";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy").format(dest.getTime()));
		}
		
		{
			String src = "2010-10";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12 13";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12 13:00";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12 13:00:00";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dest.getTime()));
		}

		{
			String src = "2010-10-12 13:00:00.555";
			Date dest = converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dest.getTime()));
		}
	}
	
	@Test
	public void convert_stringToDouble() throws Exception
	{
		{
			String src = "1";
			
			Double dest = converter.convert(src, Double.class);
			
			Assert.assertEquals(new Double(src),dest);
		}
		
		{
			String src = "1.34";
			
			Double dest = converter.convert(src, Double.class);
			
			Assert.assertEquals(Double.parseDouble(src), dest.doubleValue());
		}
		
		{
			String src = "1.34d";
			
			Double dest = converter.convert(src, Double.class);
			
			Assert.assertEquals(new Double(src), dest.doubleValue());
		}
		
		{
			String src = "1.34d";
			
			Double dest = converter.convert(src, double.class);
			
			Assert.assertEquals(Double.parseDouble(src), dest.doubleValue());
		}
	}
	
	@Test
	public void convert_stringToEnum() throws Exception
	{
		{
			String src="ENUM_1";
			TestEnum te=converter.convert(src, TestEnum.class);
			
			Assert.assertEquals(src, te.toString());
		}
		{
			String src="ENUM_2";
			TestEnum te=converter.convert(src, TestEnum.class);
			
			Assert.assertEquals(src, te.toString());
		}
	}
	
	@Test
	public void convert_stringToFloat() throws Exception
	{
		{
			String src = "1";
			
			Float dest = converter.convert(src, Float.class);
			
			Assert.assertEquals(new Float(src), dest.floatValue());
		}
		
		{
			String src = "1.34f";
			
			Float dest = converter.convert(src, Float.class);
			
			Assert.assertEquals(new Float(src),dest.floatValue());
		}
		
		{
			String src = "1.34f";
			
			Float dest = converter.convert(src, float.class);
			
			Assert.assertEquals(Float.parseFloat(src),dest.floatValue());
		}
	}
	
	@Test
	public void convert_stringToInteger() throws Exception
	{
		String src = "1";
		
		{
			Integer dest = converter.convert(src, int.class);
			Assert.assertEquals(Integer.parseInt(src), dest.intValue());
		}
		
		{
			Integer dest = converter.convert(src, Integer.class);
			Assert.assertEquals(new Integer(src), dest);
		}
	}
	
	@Test
	public void convert_stringToLong() throws Exception
	{
		String src = "13424235532342";
		
		{
			Long dest = converter.convert(src, long.class);
			Assert.assertEquals(Long.parseLong(src), dest.longValue());
		}
		
		{
			Long dest = converter.convert(src, Long.class);
			Assert.assertEquals(new Long(src), dest);
		}
	}
	
	
	
	@Test
	public void convert_stringToShort() throws Exception
	{
		String src = "1342";
		
		{
			Short dest = converter.convert(src, short.class);
			Assert.assertEquals(Short.parseShort(src), dest.shortValue());
		}
		
		{
			Short dest = converter.convert(src, Short.class);
			Assert.assertEquals(new Short(src), dest);
		}
	}
	
	@Test
	public void convert_stringToSqlDate() throws Exception
	{
		{
			String src = "2010-10-12";
			java.sql.Date dest = converter.convert(src, java.sql.Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd").format(dest.getTime()));
		}
	}
	
	@Test
	public void convert_stringToSqlTime() throws Exception
	{
		{
			String src = "15:30:20";
			java.sql.Time dest = converter.convert(src, java.sql.Time.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("HH:mm:ss").format(dest.getTime()));
		}
		
		{
			String src = "15:30:20.333";
			java.sql.Time dest = converter.convert(src, java.sql.Time.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("HH:mm:ss.SSS").format(dest.getTime()));
		}
	}
	
	@Test
	public void convert_stringToSqlTimestamp() throws Exception
	{
		{
			String src = "2010-10-12 13:00:00";
			java.sql.Timestamp dest = converter.convert(src, java.sql.Timestamp.class);
			
			String destStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dest.getTime());
			Assert.assertEquals(src, destStr);
		}
		{
			String src = "2010-10-12 13:00:00.555";
			java.sql.Timestamp dest = converter.convert(src, java.sql.Timestamp.class);
			
			String destStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dest.getTime());
			Assert.assertEquals(src, destStr);
		}
	}
	
	@Test
	public void convert_noSupportConverter_targetTypeIsString() throws Exception
	{
		Integer src=123456;
		
		String re=converter.convert(src, String.class);
		
		Assert.assertEquals(src.toString(), re);
	}
	
	@Test
	public void convert_noSupportConverter_targetTypeIsEnum() throws Exception
	{
		String src="ENUM_1";
		
		TestEnum re=converter.convert(src, TestEnum.class);
		
		Assert.assertEquals(TestEnum.ENUM_1, re);
	}

	@Test
	public void convert_noSupportConverter_stringArrayToStringArray() throws Exception
	{
		String[] src = new String[]{"1254324.3823823","2342.23879102348"};
		
		String[] dest = (String[])converter.convert(src, String[].class);
		
		Assert.assertEquals(src[0], dest[0]);
		Assert.assertEquals(src[1], dest[1]);
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToBooleanArray() throws Exception
	{
		{
			String[] src = new String[]{"true","1","false","0"};
			Boolean[] dest = converter.convert(src, Boolean[].class);
			
			Assert.assertEquals(Boolean.TRUE, dest[0]);
			Assert.assertEquals(Boolean.TRUE, dest[1]);
			Assert.assertEquals(Boolean.FALSE, dest[2]);
			Assert.assertEquals(Boolean.FALSE, dest[3]);
		}
		
		{
			String[] src = new String[]{"true","1","false","0"};
			boolean[] dest = (boolean[])converter.convert(src, boolean[].class);
			
			Assert.assertEquals(true, dest[0]);
			Assert.assertEquals(true, dest[1]);
			Assert.assertEquals(false, dest[2]);
			Assert.assertEquals(false, dest[3]);
		}
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_TypeVariable() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		@SuppressWarnings("rawtypes")
		Type type=new MockTypeVariable("T", new Type[]{Integer[].class});
		
		Integer[] dest=converter.convert(src, type);
		
		for(int i=0; i<src.length; i++)
			Assert.assertEquals(new Integer(src[i]), dest[i]);
	}
	
	@Test
	public void convert_noSupportConverter_stringArray_toLongArray() throws Exception
	{
		String[] src=new String[]{"2342353413241234", "1342413542348779"};
		{
			long[] dest = converter.convert(src, long[].class);
			
			Assert.assertEquals(Long.parseLong(src[0]), dest[0]);
			Assert.assertEquals(Long.parseLong(src[1]), dest[1]);
		}
		
		{
			Long[] dest = converter.convert(src, Long[].class);
			
			Assert.assertEquals(new Long(src[0]), dest[0]);
			Assert.assertEquals(new Long(src[1]), dest[1]);
		}
	}
	
	@Test
	public void convert_noSupportConverter_stringToGeneric_TypeVariable() throws Exception
	{
		String src="33";
		
		@SuppressWarnings("rawtypes")
		Type type=new MockTypeVariable("T", new Type[]{Integer.class});
		
		Integer re=converter.convert(src, type);
		
		Assert.assertEquals(new Integer(src), re);
	}
	
	@Test
	public void convert_noSupportConverter_stringToGeneric_ParameterizedType() throws Exception
	{
		String src="33";
		
		Type type=new MockParameterizedType(Integer.class, new Type[]{Double.class});
		
		Exception re=null;
		
		try
		{
			converter.convert(src, type);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_noSupportConverter_stringToGeneric_GenericArrayType() throws Exception
	{
		String src="33";
		
		Type type=new MockGenericArrayType(Integer.class);
		
		Exception re=null;
		
		try
		{
			converter.convert(src, type);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_noSupportConverter_stringToGeneric_WildCardType() throws Exception
	{
		String src="33";
		
		MockWildcardType type=new MockWildcardType();
		type.setUpperBounds(new Type[]{Integer.class});
		
		Integer re=converter.convert(src, type);
		
		Assert.assertEquals(new Integer(src), re);
	}
	
	@Test
	public void convert_noSupportConverter_stringToGeneric_GenericType() throws Exception
	{
		String src="33";
		
		MockWildcardType type=new MockWildcardType();
		type.setUpperBounds(new Type[]{Integer.class});
		
		Integer re=converter.convert(src, type);
		
		Assert.assertEquals(new Integer(src), re);
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_ParameterizedType_List() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Type targetType=new MockParameterizedType(List.class, Integer.class);
		List<Integer> dest=converter.convert(src, targetType);
		
		for(int i=0;i<src.length;i++)
		{
			Assert.assertEquals(new Integer(src[i]), dest.get(i));
		}
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_ParameterizedType_Set() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Type targetType=new MockParameterizedType(Set.class, Integer.class);
		Set<Integer> dest=converter.convert(src, targetType);
		
		Assert.assertTrue( dest.size() == src.length );
	}

	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_ParameterizedType_notSupported() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Type type=new MockParameterizedType(Integer.class, new Type[]{Double.class});
		
		Exception re=null;
		
		try
		{
			converter.convert(src, type);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_GenericArrayType() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Type targetType=new MockGenericArrayType(Integer.class);
		Integer[] dest=converter.convert(src, targetType);
		
		for(int i=0; i<src.length; i++)
			Assert.assertEquals(new Integer(src[i]), dest[i]);
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_WildCardType() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		MockWildcardType type=new MockWildcardType();
		type.setUpperBounds(new Type[]{Integer[].class});
		
		Integer[] dest=converter.convert(src, type);
		
		for(int i=0; i<src.length; i++)
			Assert.assertEquals(new Integer(src[i]), dest[i]);
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToGeneric_GenericType() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Type targetType=new MockGenericArrayType(Integer.class);
		Integer[] dest=converter.convert(src, targetType);
		
		for(int i=0; i<src.length; i++)
			Assert.assertEquals(new Integer(src[i]), dest[i]);
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToNormalList() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Exception re=null;
		try
		{
			List<Integer> dest=converter.convert(src, List.class);
			dest.size();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_noSupportConverter_stringArrayToNormalSet() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Exception re=null;
		try
		{
			Set<Integer> dest=converter.convert(src, Set.class);
			dest.size();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcIsNull() throws Exception
	{
		Object dest = converter.convert(null, JavaBean.class);
		Assert.assertNull(dest);
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcIsEmpty() throws Exception
	{
		//源为空
		Map<String,Object> src=new HashMap<String, Object>();
		Object dest = converter.convert(src, JavaBean.class);
		
		Assert.assertNull(dest);
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcContainInexistentJavaBeanProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		src.put("abc", 356);
		
		GenericConvertException re=null;
		
		try
		{
			converter.convert(src, JavaBean.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue(( re.getMessage().startsWith("can not find property \"abc\"") ));
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcContainInexistentSubJavaBeanProperty() throws Exception
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
		
		Assert.assertTrue(( re.getMessage().startsWith("can not find property \"def\"") ));
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcContainInexistentSubJavaBeanProperty_inSubArrayProperty() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		String id="1";
		String name="jack";
		
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
	public void convert_noSupportConverter_mapToJavaBean_srcHasInexistentPropertyContainInSubListProperty() throws Exception
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
		
		Assert.assertTrue((re.getMessage().startsWith("can not find property \"notExistsProperty\"")));
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcHasInexistentPropertyContainInSubSetProperty() throws Exception
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
		
		Assert.assertTrue((re.getMessage().startsWith("can not find property \"notExistsProperty\"")));
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcElementString() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		JavaBean dest=converter.convert(src, JavaBean.class);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}

	@Test
	public void convert_noSupportConverter_mapToJavaBean_srcElementSingleStringArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="jack";
		String age="15";
		String birth="1900-10-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		JavaBean dest=converter.convert(src, JavaBean.class);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}

	@Test
	public void convert_noSupportConverter_mapToJavaBeanArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		JavaBean[] dest=converter.convert(src, JavaBean[].class);
		
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
	public void convert_noSupportConverter_mapToJavaBean_collectionProperty_simpleKeyArrayValueMap() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String id="1";
		String name="jack";
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
		
		Assert.assertEquals(Integer.parseInt(id), dest.getId());
		Assert.assertEquals(name, dest.getName());
		
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
		
		String id="1";
		String name="jack";
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
	public void convert_noSupportConverter_mapToJavaBean_srcComplexPropertyContainIllegalValue() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] id=new String[]{"1"};
		String[] name=new String[]{"jack"};
		String[] simpleCollectionProperty=new String[]{"1","3","9"};
		
		String[] cmplexCollectionProperty_id=new String[]{"2","illegalValue","7"};
		String[] cmplexCollectionProperty_name=new String[]{"aaa","bbb","ccc"};
		
		src.put("id", id);
		src.put("name", name);
		
		src.put("simpleArray", simpleCollectionProperty);
		src.put("simpleList", simpleCollectionProperty);
		src.put("simpleSet", simpleCollectionProperty);
		
		src.put("javaBean2List.id", cmplexCollectionProperty_id);
		src.put("javaBean2List.name", cmplexCollectionProperty_name);
		
		MapConvertException re=null;
		try
		{
			converter.convert(src, ComplexJavaBean.class);
		}
		catch(MapConvertException e)
		{
			re=e;
		}
		
		Assert.assertEquals("javaBean2List.id", re.getKey());
		Assert.assertEquals("illegalValue", re.getSourceObject());
		Assert.assertEquals(int.class, re.getTargetType());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_TypeVariable() throws Exception
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
		
		JavaBean dest=converter.convert(src, type);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_ParameterizedType() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanList(List.class);
	}

	@Test
	public void convert_noSupportConverter_mapToGeneric_ParameterizedType_notSupported() throws Exception
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
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_GenericArrayType() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		Type type=new MockGenericArrayType(JavaBean.class);
		JavaBean[] dest=converter.convert(src, type);
		
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
	public void convert_noSupportConverter_mapToGeneric_WildcardType() throws Exception
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
		
		JavaBean dest=converter.convert(src, type);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_GenericType() throws Exception
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
		
		JavaBean dest=converter.convert(src, type);
		
		Assert.assertEquals(name, dest.getName());
		Assert.assertEquals(new Integer(age), dest.getAge());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(birth), dest.getBirth());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanCollection_keyComplexSemantics_illegalValue() throws Exception
	{
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleArray.0", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.simpleArray.0", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(Integer.class, re.getTargetType());
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleList.0", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.simpleList.0", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(Integer.class, re.getTargetType());
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleSet.0", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.simpleSet.0", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(Integer.class, re.getTargetType());
		}

		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.simpleMap.key", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.simpleMap.key", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(Integer.class, re.getTargetType());
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2Array.0.id", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.javaBean2Array.0.id", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(int.class, re.getTargetType());
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2List.0.id", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.javaBean2List.0.id", re.getKey());
			Assert.assertEquals("illegalValue",re.getSourceObject());
			Assert.assertEquals(int.class, re.getTargetType());
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2Set.0.id", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.javaBean2Set.0.id", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(int.class, re.getTargetType());
		}
		
		{
			Map<String,Object> src=new HashMap<String, Object>();
			
			src.put("0.javaBean2Map.0.id", "illegalValue");
			
			MapConvertException re=null;
			try
			{
				converter.convert(src, ComplexJavaBean[].class);
			}
			catch(MapConvertException e)
			{
				re=e;
			}
			
			Assert.assertEquals("0.javaBean2Map.0.id", re.getKey());
			Assert.assertEquals("illegalValue", re.getSourceObject());
			Assert.assertEquals(int.class, re.getTargetType());
		}
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanCollection_keyComplexSemantics_illegalKey() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		src.put("0.javaBean2Map.illegalKey.id", "1");
		
		GenericConvertException re=null;
		try
		{
			converter.convert(src, ComplexJavaBean[].class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue(( re.getMessage().startsWith("convert \"illegalKey\" in key \"0.javaBean2Map.illegalKey\" to Map key") ));
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanArray_keyComplexSemantics() throws Exception
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
		
		ComplexJavaBean[] re=converter.convert(src, ComplexJavaBean[].class);
		
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
	public void convert_noSupportConverter_mapToGeneric_JavaBeanList_keyComplexSemantics() throws Exception
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
		
		List<ComplexJavaBean> re=converter.convert(src, listType);
		
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
	public void convert_noSupportConverter_mapToGeneric_JavaBeanSet_keyComplexSemantics() throws Exception
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

		Set<ComplexJavaBean> re=converter.convert(src, setType);
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
	public void convert_noSupportConverter_mapToGeneric_JavaBeanList() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanList(List.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanArrayList() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanList(ArrayList.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanLinkedList() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanList(LinkedList.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanVector() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanList(Vector.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanStack() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanList(Stack.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanSet() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanSet(Set.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBean_HashSet() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanSet(HashSet.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanTreeSet() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanSet(TreeSet.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanLinkedHashSet() throws Exception
	{
		convert_noSupportConverter_mapToJavaBeanSet(LinkedHashSet.class);
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanMap() throws Exception
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
		
		Map<String, JavaBean> dest=converter.convert(src, mapType);
		
		for(int i=0; i<keys.length; i++)
		{
			JavaBean jb=dest.get(keys[i]);
			
			Assert.assertEquals(names[i], jb.getName());
			Assert.assertEquals(new Integer(ages[i]), jb.getAge());
		}
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_JavaBeanIsGeneric() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String id="11";
		String[] property={"12", "13"};
		
		src.put("id", id);
		src.put("list", property);
		src.put("array", property);
		src.put("obj", property[0]);
		
		GenericJavaBeanSub dest=converter.convert(src, GenericJavaBeanSub.class);
		
		Assert.assertEquals(new Integer(id), dest.getId());
		Assert.assertEquals(new Double(property[0]), dest.getObj());
		for(int i=0; i<property.length; i++)
		{
			Assert.assertEquals(new Double(property[i]), dest.getList().get(i));
			Assert.assertEquals(new Double(property[i]), dest.getArray()[i]);
			
		}
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanArrayList() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("0.name", name_0);
		src.put("0.age", age_0);
		
		src.put("1.0.name", name_1[0]);
		src.put("1.0.age", age_1[0]);
		src.put("1.1.name", name_1[1]);
		src.put("1.1.age", age_1[1]);
		src.put("1.2.name", name_1[2]);
		src.put("1.2.age", age_1[2]);
		
		src.put("2.name", name_2);
		src.put("2.age", age_2);
		
		Type type=new MockParameterizedType(List.class, JavaBean[].class);
		List<JavaBean[]> dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.size());
		
		JavaBean[] jb0=dest.get(0);
		Assert.assertEquals(3, jb0.length);
		Assert.assertEquals(name_0[0], jb0[0].getName());
		Assert.assertEquals(age_0[0], jb0[0].getAge().toString());
		Assert.assertEquals(name_0[1], jb0[1].getName());
		Assert.assertEquals(age_0[1], jb0[1].getAge().toString());
		Assert.assertEquals(name_0[2], jb0[2].getName());
		Assert.assertEquals(age_0[2], jb0[2].getAge().toString());
		
		JavaBean[] jb1=dest.get(1);
		Assert.assertEquals(3, jb1.length);
		Assert.assertEquals(name_1[0], jb1[0].getName());
		Assert.assertEquals(age_1[0], jb1[0].getAge().toString());
		Assert.assertEquals(name_1[1], jb1[1].getName());
		Assert.assertEquals(age_1[1], jb1[1].getAge().toString());
		Assert.assertEquals(name_1[2], jb1[2].getName());
		Assert.assertEquals(age_1[2], jb1[2].getAge().toString());
		
		JavaBean[] jb2=dest.get(2);
		Assert.assertEquals(3, jb2.length);
		Assert.assertEquals(name_2[0], jb2[0].getName());
		Assert.assertEquals(age_2[0], jb2[0].getAge().toString());
		Assert.assertEquals(name_2[1], jb2[1].getName());
		Assert.assertEquals(age_2[1], jb2[1].getAge().toString());
		Assert.assertEquals(name_2[2], jb2[2].getName());
		Assert.assertEquals(age_2[2], jb2[2].getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanListList() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("0.name", name_0);
		src.put("0.age", age_0);
		
		src.put("1.0.name", name_1[0]);
		src.put("1.0.age", age_1[0]);
		src.put("1.1.name", name_1[1]);
		src.put("1.1.age", age_1[1]);
		src.put("1.2.name", name_1[2]);
		src.put("1.2.age", age_1[2]);
		
		src.put("2.name", name_2);
		src.put("2.age", age_2);
		
		Type type=new MockParameterizedType(List.class, new MockParameterizedType(List.class, JavaBean.class));
		List<List<JavaBean>> dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.size());
		
		List<JavaBean> jb0=dest.get(0);
		Assert.assertEquals(3, jb0.size());
		Assert.assertEquals(name_0[0], jb0.get(0).getName());
		Assert.assertEquals(age_0[0], jb0.get(0).getAge().toString());
		Assert.assertEquals(name_0[1], jb0.get(1).getName());
		Assert.assertEquals(age_0[1], jb0.get(1).getAge().toString());
		Assert.assertEquals(name_0[2], jb0.get(2).getName());
		Assert.assertEquals(age_0[2], jb0.get(2).getAge().toString());
		
		List<JavaBean> jb1=dest.get(1);
		Assert.assertEquals(3, jb1.size());
		Assert.assertEquals(name_1[0], jb1.get(0).getName());
		Assert.assertEquals(age_1[0], jb1.get(0).getAge().toString());
		Assert.assertEquals(name_1[1], jb1.get(1).getName());
		Assert.assertEquals(age_1[1], jb1.get(1).getAge().toString());
		Assert.assertEquals(name_1[2], jb1.get(2).getName());
		Assert.assertEquals(age_1[2], jb1.get(2).getAge().toString());
		
		List<JavaBean> jb2=dest.get(2);
		Assert.assertEquals(3, jb2.size());
		Assert.assertEquals(name_2[0], jb2.get(0).getName());
		Assert.assertEquals(age_2[0], jb2.get(0).getAge().toString());
		Assert.assertEquals(name_2[1], jb2.get(1).getName());
		Assert.assertEquals(age_2[1], jb2.get(1).getAge().toString());
		Assert.assertEquals(name_2[2], jb2.get(2).getName());
		Assert.assertEquals(age_2[2], jb2.get(2).getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanMapList() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("0.key00.name", name_0[0]);
		src.put("0.key00.age", age_0[0]);
		src.put("0.key01.name", name_0[1]);
		src.put("0.key01.age", age_0[1]);
		src.put("0.key02.name", name_0[2]);
		src.put("0.key02.age", age_0[2]);
		
		src.put("1.key10.name", name_1[0]);
		src.put("1.key10.age", age_1[0]);
		src.put("1.key11.name", name_1[1]);
		src.put("1.key11.age", age_1[1]);
		src.put("1.key12.name", name_1[2]);
		src.put("1.key12.age", age_1[2]);
		
		src.put("2.key20.name", name_2[0]);
		src.put("2.key20.age", age_2[0]);
		src.put("2.key21.name", name_2[1]);
		src.put("2.key21.age", age_2[1]);
		src.put("2.key22.name", name_2[2]);
		src.put("2.key22.age", age_2[2]);
		
		Type type=new MockParameterizedType(List.class, new MockParameterizedType(Map.class, String.class, JavaBean.class));
		List<Map<String, JavaBean>> dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.size());
		
		Map<String, JavaBean> jb0=dest.get(0);
		Assert.assertEquals(3, jb0.size());
		Assert.assertEquals(name_0[0], jb0.get("key00").getName());
		Assert.assertEquals(age_0[0], jb0.get("key00").getAge().toString());
		Assert.assertEquals(name_0[1], jb0.get("key01").getName());
		Assert.assertEquals(age_0[1], jb0.get("key01").getAge().toString());
		Assert.assertEquals(name_0[2], jb0.get("key02").getName());
		Assert.assertEquals(age_0[2], jb0.get("key02").getAge().toString());
		
		Map<String, JavaBean> jb1=dest.get(1);
		Assert.assertEquals(3, jb1.size());
		Assert.assertEquals(name_1[0], jb1.get("key10").getName());
		Assert.assertEquals(age_1[0], jb1.get("key10").getAge().toString());
		Assert.assertEquals(name_1[1], jb1.get("key11").getName());
		Assert.assertEquals(age_1[1], jb1.get("key11").getAge().toString());
		Assert.assertEquals(name_1[2], jb1.get("key12").getName());
		Assert.assertEquals(age_1[2], jb1.get("key12").getAge().toString());
		
		Map<String, JavaBean> jb2=dest.get(2);
		Assert.assertEquals(3, jb2.size());
		Assert.assertEquals(name_2[0], jb2.get("key20").getName());
		Assert.assertEquals(age_2[0], jb2.get("key20").getAge().toString());
		Assert.assertEquals(name_2[1], jb2.get("key21").getName());
		Assert.assertEquals(age_2[1], jb2.get("key21").getAge().toString());
		Assert.assertEquals(name_2[2], jb2.get("key22").getName());
		Assert.assertEquals(age_2[2], jb2.get("key22").getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanArrayArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("0.name", name_0);
		src.put("0.age", age_0);
		
		src.put("1.0.name", name_1[0]);
		src.put("1.0.age", age_1[0]);
		src.put("1.1.name", name_1[1]);
		src.put("1.1.age", age_1[1]);
		src.put("1.2.name", name_1[2]);
		src.put("1.2.age", age_1[2]);
		
		src.put("2.name", name_2);
		src.put("2.age", age_2);
		
		JavaBean[][] dest=converter.convert(src, JavaBean[][].class);
		
		Assert.assertEquals(3, dest.length);
		
		JavaBean[] jb0=dest[0];
		Assert.assertEquals(3, jb0.length);
		Assert.assertEquals(name_0[0], jb0[0].getName());
		Assert.assertEquals(age_0[0], jb0[0].getAge().toString());
		Assert.assertEquals(name_0[1], jb0[1].getName());
		Assert.assertEquals(age_0[1], jb0[1].getAge().toString());
		Assert.assertEquals(name_0[2], jb0[2].getName());
		Assert.assertEquals(age_0[2], jb0[2].getAge().toString());
		
		JavaBean[] jb1=dest[1];
		Assert.assertEquals(3, jb1.length);
		Assert.assertEquals(name_1[0], jb1[0].getName());
		Assert.assertEquals(age_1[0], jb1[0].getAge().toString());
		Assert.assertEquals(name_1[1], jb1[1].getName());
		Assert.assertEquals(age_1[1], jb1[1].getAge().toString());
		Assert.assertEquals(name_1[2], jb1[2].getName());
		Assert.assertEquals(age_1[2], jb1[2].getAge().toString());
		
		JavaBean[] jb2=dest[2];
		Assert.assertEquals(3, jb2.length);
		Assert.assertEquals(name_2[0], jb2[0].getName());
		Assert.assertEquals(age_2[0], jb2[0].getAge().toString());
		Assert.assertEquals(name_2[1], jb2[1].getName());
		Assert.assertEquals(age_2[1], jb2[1].getAge().toString());
		Assert.assertEquals(name_2[2], jb2[2].getName());
		Assert.assertEquals(age_2[2], jb2[2].getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanListArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("0.name", name_0);
		src.put("0.age", age_0);
		
		src.put("1.0.name", name_1[0]);
		src.put("1.0.age", age_1[0]);
		src.put("1.1.name", name_1[1]);
		src.put("1.1.age", age_1[1]);
		src.put("1.2.name", name_1[2]);
		src.put("1.2.age", age_1[2]);
		
		src.put("2.name", name_2);
		src.put("2.age", age_2);
		
		Type type=new MockGenericArrayType(new MockParameterizedType(List.class, JavaBean.class));
		List<JavaBean>[] dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.length);
		
		List<JavaBean> jb0=dest[0];
		Assert.assertEquals(3, jb0.size());
		Assert.assertEquals(name_0[0], jb0.get(0).getName());
		Assert.assertEquals(age_0[0], jb0.get(0).getAge().toString());
		Assert.assertEquals(name_0[1], jb0.get(1).getName());
		Assert.assertEquals(age_0[1], jb0.get(1).getAge().toString());
		Assert.assertEquals(name_0[2], jb0.get(2).getName());
		Assert.assertEquals(age_0[2], jb0.get(2).getAge().toString());
		
		List<JavaBean> jb1=dest[1];
		Assert.assertEquals(3, jb1.size());
		Assert.assertEquals(name_1[0], jb1.get(0).getName());
		Assert.assertEquals(age_1[0], jb1.get(0).getAge().toString());
		Assert.assertEquals(name_1[1], jb1.get(1).getName());
		Assert.assertEquals(age_1[1], jb1.get(1).getAge().toString());
		Assert.assertEquals(name_1[2], jb1.get(2).getName());
		Assert.assertEquals(age_1[2], jb1.get(2).getAge().toString());
		
		List<JavaBean> jb2=dest[2];
		Assert.assertEquals(3, jb2.size());
		Assert.assertEquals(name_2[0], jb2.get(0).getName());
		Assert.assertEquals(age_2[0], jb2.get(0).getAge().toString());
		Assert.assertEquals(name_2[1], jb2.get(1).getName());
		Assert.assertEquals(age_2[1], jb2.get(1).getAge().toString());
		Assert.assertEquals(name_2[2], jb2.get(2).getName());
		Assert.assertEquals(age_2[2], jb2.get(2).getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanMapArray() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("0.key00.name", name_0[0]);
		src.put("0.key00.age", age_0[0]);
		src.put("0.key01.name", name_0[1]);
		src.put("0.key01.age", age_0[1]);
		src.put("0.key02.name", name_0[2]);
		src.put("0.key02.age", age_0[2]);
		
		src.put("1.key10.name", name_1[0]);
		src.put("1.key10.age", age_1[0]);
		src.put("1.key11.name", name_1[1]);
		src.put("1.key11.age", age_1[1]);
		src.put("1.key12.name", name_1[2]);
		src.put("1.key12.age", age_1[2]);
		
		src.put("2.key20.name", name_2[0]);
		src.put("2.key20.age", age_2[0]);
		src.put("2.key21.name", name_2[1]);
		src.put("2.key21.age", age_2[1]);
		src.put("2.key22.name", name_2[2]);
		src.put("2.key22.age", age_2[2]);
		
		Type type=new MockGenericArrayType(new MockParameterizedType(Map.class, String.class, JavaBean.class));
		Map<String, JavaBean>[] dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.length);
		
		Map<String, JavaBean> jb0=dest[0];
		Assert.assertEquals(3, jb0.size());
		Assert.assertEquals(name_0[0], jb0.get("key00").getName());
		Assert.assertEquals(age_0[0], jb0.get("key00").getAge().toString());
		Assert.assertEquals(name_0[1], jb0.get("key01").getName());
		Assert.assertEquals(age_0[1], jb0.get("key01").getAge().toString());
		Assert.assertEquals(name_0[2], jb0.get("key02").getName());
		Assert.assertEquals(age_0[2], jb0.get("key02").getAge().toString());
		
		Map<String, JavaBean> jb1=dest[1];
		Assert.assertEquals(3, jb1.size());
		Assert.assertEquals(name_1[0], jb1.get("key10").getName());
		Assert.assertEquals(age_1[0], jb1.get("key10").getAge().toString());
		Assert.assertEquals(name_1[1], jb1.get("key11").getName());
		Assert.assertEquals(age_1[1], jb1.get("key11").getAge().toString());
		Assert.assertEquals(name_1[2], jb1.get("key12").getName());
		Assert.assertEquals(age_1[2], jb1.get("key12").getAge().toString());
		
		Map<String, JavaBean> jb2=dest[2];
		Assert.assertEquals(3, jb2.size());
		Assert.assertEquals(name_2[0], jb2.get("key20").getName());
		Assert.assertEquals(age_2[0], jb2.get("key20").getAge().toString());
		Assert.assertEquals(name_2[1], jb2.get("key21").getName());
		Assert.assertEquals(age_2[1], jb2.get("key21").getAge().toString());
		Assert.assertEquals(name_2[2], jb2.get("key22").getName());
		Assert.assertEquals(age_2[2], jb2.get("key22").getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanArrayMap() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("key0.name", name_0);
		src.put("key0.age", age_0);
		
		src.put("key1.0.name", name_1[0]);
		src.put("key1.0.age", age_1[0]);
		src.put("key1.1.name", name_1[1]);
		src.put("key1.1.age", age_1[1]);
		src.put("key1.2.name", name_1[2]);
		src.put("key1.2.age", age_1[2]);
		
		src.put("key2.name", name_2);
		src.put("key2.age", age_2);
		
		Type type=new MockParameterizedType(Map.class, String.class, JavaBean[].class);
		Map<String, JavaBean[]> dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.size());
		
		JavaBean[] jb0=dest.get("key0");
		Assert.assertEquals(3, jb0.length);
		Assert.assertEquals(name_0[0], jb0[0].getName());
		Assert.assertEquals(age_0[0], jb0[0].getAge().toString());
		Assert.assertEquals(name_0[1], jb0[1].getName());
		Assert.assertEquals(age_0[1], jb0[1].getAge().toString());
		Assert.assertEquals(name_0[2], jb0[2].getName());
		Assert.assertEquals(age_0[2], jb0[2].getAge().toString());
		
		JavaBean[] jb1=dest.get("key1");
		Assert.assertEquals(3, jb1.length);
		Assert.assertEquals(name_1[0], jb1[0].getName());
		Assert.assertEquals(age_1[0], jb1[0].getAge().toString());
		Assert.assertEquals(name_1[1], jb1[1].getName());
		Assert.assertEquals(age_1[1], jb1[1].getAge().toString());
		Assert.assertEquals(name_1[2], jb1[2].getName());
		Assert.assertEquals(age_1[2], jb1[2].getAge().toString());
		
		JavaBean[] jb2=dest.get("key2");
		Assert.assertEquals(3, jb2.length);
		Assert.assertEquals(name_2[0], jb2[0].getName());
		Assert.assertEquals(age_2[0], jb2[0].getAge().toString());
		Assert.assertEquals(name_2[1], jb2[1].getName());
		Assert.assertEquals(age_2[1], jb2[1].getAge().toString());
		Assert.assertEquals(name_2[2], jb2[2].getName());
		Assert.assertEquals(age_2[2], jb2[2].getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanListMap() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("key0.name", name_0);
		src.put("key0.age", age_0);
		
		src.put("key1.0.name", name_1[0]);
		src.put("key1.0.age", age_1[0]);
		src.put("key1.1.name", name_1[1]);
		src.put("key1.1.age", age_1[1]);
		src.put("key1.2.name", name_1[2]);
		src.put("key1.2.age", age_1[2]);
		
		src.put("key2.name", name_2);
		src.put("key2.age", age_2);
		
		Type type=new MockParameterizedType(Map.class, String.class, new MockParameterizedType(List.class, JavaBean.class));
		Map<String, List<JavaBean>> dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.size());
		
		List<JavaBean> jb0=dest.get("key0");
		Assert.assertEquals(3, jb0.size());
		Assert.assertEquals(name_0[0], jb0.get(0).getName());
		Assert.assertEquals(age_0[0], jb0.get(0).getAge().toString());
		Assert.assertEquals(name_0[1], jb0.get(1).getName());
		Assert.assertEquals(age_0[1], jb0.get(1).getAge().toString());
		Assert.assertEquals(name_0[2], jb0.get(2).getName());
		Assert.assertEquals(age_0[2], jb0.get(2).getAge().toString());
		
		List<JavaBean> jb1=dest.get("key1");
		Assert.assertEquals(3, jb1.size());
		Assert.assertEquals(name_1[0], jb1.get(0).getName());
		Assert.assertEquals(age_1[0], jb1.get(0).getAge().toString());
		Assert.assertEquals(name_1[1], jb1.get(1).getName());
		Assert.assertEquals(age_1[1], jb1.get(1).getAge().toString());
		Assert.assertEquals(name_1[2], jb1.get(2).getName());
		Assert.assertEquals(age_1[2], jb1.get(2).getAge().toString());
		
		List<JavaBean> jb2=dest.get("key2");
		Assert.assertEquals(3, jb2.size());
		Assert.assertEquals(name_2[0], jb2.get(0).getName());
		Assert.assertEquals(age_2[0], jb2.get(0).getAge().toString());
		Assert.assertEquals(name_2[1], jb2.get(1).getName());
		Assert.assertEquals(age_2[1], jb2.get(1).getAge().toString());
		Assert.assertEquals(name_2[2], jb2.get(2).getName());
		Assert.assertEquals(age_2[2], jb2.get(2).getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToGeneric_javaBeanMapMap() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] name_0={"a", "b", "c"};
		String[] age_0={"2", "3", "4"};
		
		String[] name_1={"aa", "bb", "cc"};
		String[] age_1={"12", "13", "14"};
		
		String[] name_2={"aaa", "bbb", "ccc"};
		String[] age_2={"112", "113", "114"};
		
		src.put("key0.key00.name", name_0[0]);
		src.put("key0.key00.age", age_0[0]);
		src.put("key0.key01.name", name_0[1]);
		src.put("key0.key01.age", age_0[1]);
		src.put("key0.key02.name", name_0[2]);
		src.put("key0.key02.age", age_0[2]);
		
		src.put("key1.key10.name", name_1[0]);
		src.put("key1.key10.age", age_1[0]);
		src.put("key1.key11.name", name_1[1]);
		src.put("key1.key11.age", age_1[1]);
		src.put("key1.key12.name", name_1[2]);
		src.put("key1.key12.age", age_1[2]);
		
		src.put("key2.key20.name", name_2[0]);
		src.put("key2.key20.age", age_2[0]);
		src.put("key2.key21.name", name_2[1]);
		src.put("key2.key21.age", age_2[1]);
		src.put("key2.key22.name", name_2[2]);
		src.put("key2.key22.age", age_2[2]);
		
		Type type=new MockParameterizedType(Map.class, String.class, new MockParameterizedType(Map.class, String.class, JavaBean.class));
		Map<String, Map<String, JavaBean>> dest=converter.convert(src, type);
		
		Assert.assertEquals(3, dest.size());
		
		Map<String, JavaBean> jb0=dest.get("key0");
		Assert.assertEquals(3, jb0.size());
		Assert.assertEquals(name_0[0], jb0.get("key00").getName());
		Assert.assertEquals(age_0[0], jb0.get("key00").getAge().toString());
		Assert.assertEquals(name_0[1], jb0.get("key01").getName());
		Assert.assertEquals(age_0[1], jb0.get("key01").getAge().toString());
		Assert.assertEquals(name_0[2], jb0.get("key02").getName());
		Assert.assertEquals(age_0[2], jb0.get("key02").getAge().toString());
		
		Map<String, JavaBean> jb1=dest.get("key1");
		Assert.assertEquals(3, jb1.size());
		Assert.assertEquals(name_1[0], jb1.get("key10").getName());
		Assert.assertEquals(age_1[0], jb1.get("key10").getAge().toString());
		Assert.assertEquals(name_1[1], jb1.get("key11").getName());
		Assert.assertEquals(age_1[1], jb1.get("key11").getAge().toString());
		Assert.assertEquals(name_1[2], jb1.get("key12").getName());
		Assert.assertEquals(age_1[2], jb1.get("key12").getAge().toString());
		
		Map<String, JavaBean> jb2=dest.get("key2");
		Assert.assertEquals(3, jb2.size());
		Assert.assertEquals(name_2[0], jb2.get("key20").getName());
		Assert.assertEquals(age_2[0], jb2.get("key20").getAge().toString());
		Assert.assertEquals(name_2[1], jb2.get("key21").getName());
		Assert.assertEquals(age_2[1], jb2.get("key21").getAge().toString());
		Assert.assertEquals(name_2[2], jb2.get("key22").getName());
		Assert.assertEquals(age_2[2], jb2.get("key22").getAge().toString());
	}
	
	@Test
	public void convert_noSupportConverter_mapToRawMap_targetIsRawMap() throws Exception
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Object dest=converter.convert(src, Map.class);
		
		Assert.assertTrue(src == dest);
	}
	
	@Test
	public void convert_noSupportConverter_mapToRawMap_targetIsWildcardParameterMap() throws Exception
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Type type=new MockParameterizedType(Map.class, new MockWildcardType(), new MockWildcardType());
		
		Object dest=converter.convert(src, type);
		
		Assert.assertTrue(src == dest);
	}
	
	@Test
	public void convert_noSupportConverter_mapToRawMap_targetIsObjectParameterMap() throws Exception
	{
		HashMap<String,Integer> src=new HashMap<String, Integer>();
		
		Type type=new MockParameterizedType(Map.class, Object.class, Object.class);
		
		Object dest=converter.convert(src, type);
		
		Assert.assertTrue(src == dest);
	}
	
	@Test
	public void convert_noSupportConverter_mapToRawList() throws Exception
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
			List<JavaBean> dest=converter.convert(src, List.class);
			dest.size();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().endsWith("its element type null is not JavaBean class") );
	}

	@Test
	public void convert_noSupportConverter_mapToRawSet() throws Exception
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
			Set<JavaBean> dest=converter.convert(src, Set.class);
			dest.size();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().endsWith("its element type null is not JavaBean class") );
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_customType_string() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="aa";
		String age="11";
		String birth="1900-07-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		src.put("class", JavaBean.class.getName());
		
		JavaBean dest=converter.convert(src, null);
		
		Assert.assertEquals(name, dest.getName());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBean_customType_class() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String name="aa";
		String age="11";
		String birth="1900-07-21";
		
		src.put("name", name);
		src.put("age", age);
		src.put("birth", birth);
		
		src.put("class", JavaBean.class);
		
		JavaBean dest=converter.convert(src, null);
		
		Assert.assertEquals(name, dest.getName());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanArray_customType_string_targetTypeInSrc() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		src.put("2.friend", friend);
		
		src.put(KEY_CLASS, JavaBean[].class.getName());
		src.put(KEY_CLASSES, new String[]{ JavaBeanChild.class.getName() });
		src.put("2."+KEY_CLASS, JavaBeanChild.class.getName());
		
		JavaBean[] dest=converter.convert(src, null);
		
		Assert.assertEquals(JavaBeanChild.class, dest[0].getClass());
		Assert.assertEquals(JavaBean.class, dest[1].getClass());
		Assert.assertEquals(JavaBeanChild.class, dest[2].getClass());
		
		Assert.assertNull( ((JavaBeanChild)dest[0]).getFriend() );
		Assert.assertEquals(friend, ((JavaBeanChild)dest[2]).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanArray_customType_string_targetTypeNotNull() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		src.put("2.friend", friend);
		
		src.put(KEY_CLASS, JavaBean[].class.getName());
		src.put(KEY_CLASSES, new String[]{ JavaBeanChild.class.getName() });
		src.put("2."+KEY_CLASS, JavaBeanChild.class.getName());
		
		JavaBean[] dest=converter.convert(src, JavaBean[].class);
		
		Assert.assertEquals(JavaBeanChild.class, dest[0].getClass());
		Assert.assertEquals(JavaBean.class, dest[1].getClass());
		Assert.assertEquals(JavaBeanChild.class, dest[2].getClass());
		
		Assert.assertNull( ((JavaBeanChild)dest[0]).getFriend() );
		Assert.assertEquals(friend, ((JavaBeanChild)dest[2]).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanList_customType_string_targetTypeInSrc() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		src.put("2.friend", friend);
		
		src.put(KEY_CLASS, List.class.getName());
		src.put(KEY_CLASSES, new String[]{ JavaBeanChild.class.getName() });
		src.put("2."+KEY_CLASS, JavaBeanChild.class.getName());
		
		List<JavaBean> dest=converter.convert(src, null);
		
		Assert.assertEquals(JavaBeanChild.class, dest.get(0).getClass());
		Assert.assertEquals(JavaBeanChild.class, dest.get(1).getClass());
		Assert.assertEquals(JavaBeanChild.class, dest.get(2).getClass());
		
		Assert.assertNull( ((JavaBeanChild)dest.get(0)).getFriend() );
		Assert.assertEquals(friend, ((JavaBeanChild)dest.get(2)).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanList_customType_string_targetTypeNotNull() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		src.put("2.friend", friend);
		
		src.put(KEY_CLASSES, new String[]{ JavaBeanChild.class.getName() });
		src.put("2."+KEY_CLASS, JavaBeanChild.class.getName());
		
		Type listType=new MockParameterizedType(List.class, JavaBean.class);
		
		List<JavaBean> dest=converter.convert(src, listType);
		
		Assert.assertEquals(JavaBeanChild.class, dest.get(0).getClass());
		Assert.assertEquals(JavaBean.class, dest.get(1).getClass());
		Assert.assertEquals(JavaBeanChild.class, dest.get(2).getClass());
		
		Assert.assertNull( ((JavaBeanChild)dest.get(0)).getFriend() );
		Assert.assertEquals(friend, ((JavaBeanChild)dest.get(2)).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanSet_customType_string_targetTypeInSrc() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		src.put("2.friend", friend);
		
		src.put(KEY_CLASS, TreeSet.class.getName());
		src.put(KEY_CLASSES, new String[]{ JavaBeanChild.class.getName() });
		src.put("2."+KEY_CLASS, JavaBeanChild.class.getName());
		
		Set<JavaBean> dest=converter.convert(src, null);
		
		JavaBean[] destArray=dest.toArray(new JavaBean[dest.size()]);
		
		Assert.assertEquals(JavaBeanChild.class, destArray[0].getClass());
		Assert.assertEquals(JavaBeanChild.class, destArray[1].getClass());
		Assert.assertEquals(JavaBeanChild.class, destArray[2].getClass());
		
		Assert.assertNull( ((JavaBeanChild)destArray[0]).getFriend() );
		Assert.assertEquals(friend, ((JavaBeanChild)destArray[2]).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanSet_customType_string_targetTypeNotNull() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		src.put("2.friend", friend);
		
		src.put(KEY_CLASSES, new String[]{ JavaBeanChild.class.getName() });
		src.put("2."+KEY_CLASS, JavaBeanChild.class.getName());
		
		Type setType=new MockParameterizedType(TreeSet.class, JavaBean.class);
		
		Set<JavaBean> dest=converter.convert(src, setType);
		
		JavaBean[] destArray=dest.toArray(new JavaBean[dest.size()]);
		
		Assert.assertEquals(JavaBeanChild.class, destArray[0].getClass());
		Assert.assertEquals(JavaBean.class, destArray[1].getClass());
		Assert.assertEquals(JavaBeanChild.class, destArray[2].getClass());
		
		Assert.assertNull( ((JavaBeanChild)destArray[0]).getFriend() );
		Assert.assertEquals(friend, ((JavaBeanChild)destArray[2]).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanMap_customType_string_targetTypeInSrc() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		String[] key=new String[]{ "a", "b", "c" };
		
		for(int i=0; i<key.length; i++)
		{
			src.put(key[i]+".name", names[i]);
			src.put(key[i]+".age", ages[i]);
			src.put(key[i]+".birth", births[i]);
		}
		
		src.put("class", Map.class.getName());
		src.put("c.friend", friend);
		src.put("c.class", JavaBeanChild.class.getName());
		
		Map<String, JavaBean> dest=converter.convert(src, null);
		
		Assert.assertTrue( dest.get("a") instanceof Map );
		Assert.assertTrue( dest.get("b") instanceof Map );
		
		Assert.assertEquals(JavaBeanChild.class, dest.get("c").getClass());
		Assert.assertEquals(friend, ((JavaBeanChild)dest.get("c")).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_mapToJavaBeanMap_customType_string_targetTypeNotNull() throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		String friend="jack";
		
		String[] key=new String[]{ "a", "b", "c" };
		
		for(int i=0; i<key.length; i++)
		{
			src.put(key[i]+".name", names[i]);
			src.put(key[i]+".age", ages[i]);
			src.put(key[i]+".birth", births[i]);
		}
		
		src.put("c.friend", friend);
		src.put("c.class", JavaBeanChild.class.getName());
		
		Type mapType=new MockParameterizedType(Map.class, String.class, JavaBean.class);
		
		Map<String, JavaBean> dest=converter.convert(src, mapType);
		
		Assert.assertEquals(JavaBean.class, dest.get("a").getClass());
		Assert.assertEquals(JavaBean.class, dest.get("b").getClass());
		Assert.assertEquals(JavaBeanChild.class, dest.get("c").getClass());
		
		Assert.assertEquals(friend, ((JavaBeanChild)dest.get("c")).getFriend());
	}
	
	@Test
	public void convert_noSupportConverter_collectionObjectToRawCollection() throws Exception
	{
		List<JavaBean> src=new ArrayList<JavaBean>();
		
		List<JavaBean> dest=converter.convert(src, List.class);
		
		Assert.assertTrue( (dest==src) );
	}
	
	@Test
	public void convert_noSupportConverter_collectionObjectToGenericCollection() throws Exception
	{
		List<JavaBean> src=new ArrayList<JavaBean>();
		
		Type type=new MockParameterizedType(List.class, JavaBean.class);
		List<JavaBean> dest=converter.convert(src, type);
		
		Assert.assertTrue( (dest==src) );
	}
	
	@Test
	public void convert_noSupportConverter_notSupported() throws Exception
	{
		int src=3355;
		
		Exception re=null;
		try
		{
			Byte dest=converter.convert(src, byte.class);
			dest.byteValue();
		}
		catch(Exception e)
		{
			re=e;
		}
		
		Assert.assertTrue( re.getMessage().startsWith("can not find Converter for converting") );
	}
	
	@Test
	public void convert_convertException() throws Exception
	{
		String src="sdf";
		
		ConvertException re=null;
		
		try
		{
			converter.convert(src, Integer.class);
		}
		catch(ConvertException e)
		{
			re=e;
		}
		
		Assert.assertEquals(src, re.getSourceObject());
		Assert.assertEquals(Integer.class, re.getTargetType());
	}
	
	@SuppressWarnings("rawtypes")
	private void convert_noSupportConverter_mapToJavaBeanList(Class<? extends List> listClass) throws Exception
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
			List<JavaBean> dest=converter.convert(src, type);
			
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

	@SuppressWarnings("rawtypes")
	private void convert_noSupportConverter_mapToJavaBeanSet(Class<? extends Set> setClass) throws Exception
	{
		Map<String,Object> src=new HashMap<String, Object>();
		
		String[] names=new String[]{"aa", "bb", "cc"};
		String[] ages=new String[]{"11", "22", "33"};
		String[] births=new String[]{"1900-07-21", "1900-07-22", "1900-07-23"};
		
		src.put("name", names);
		src.put("age", ages);
		src.put("birth", births);
		
		Type type=new MockParameterizedType(setClass, new Type[]{JavaBean.class});
		
		Set<JavaBean> dest=converter.convert(src, type);
		
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
	
	protected ComplexJavaBean createComplexJavaBeanTestInstance(int flag)
	{
		ComplexJavaBean cb=new ComplexJavaBean();
		cb.setId(flag);
		cb.setName("name-"+flag);
		
		Integer[] simpleArray=new Integer[]{11, 22, 33};
		
		List<Integer> simpleList=new ArrayList<Integer>();
		simpleList.add(111);
		simpleList.add(222);
		
		Set<Integer> simpleSet=new TreeSet<Integer>();
		simpleSet.add(111);
		simpleSet.add(222);
		
		Map<String, Integer> simpleMap=new HashMap<String, Integer>();
		simpleMap.put("0", 1111);
		simpleMap.put("1", 2222);
		
		JavaBean2[] javaBean2Array=new JavaBean2[]{
				new JavaBean2(flag*100+1, "ComplexJavaBean-"+flag+"-javaBean2Array-0"),
				new JavaBean2(flag*100+2, "ComplexJavaBean-"+flag+"-javaBean2Array-1"),
			};
		
		List<JavaBean2> javaBean2List=new ArrayList<JavaBean2>();
		javaBean2List.add(new JavaBean2(flag*100+3, "ComplexJavaBean-"+flag+"-javaBean2List-1"));
		javaBean2List.add(new JavaBean2(flag*100+4, "ComplexJavaBean-"+flag+"-javaBean2List-2"));
		
		Set<JavaBean2> javaBean2Set=new TreeSet<JavaBean2>();
		javaBean2Set.add(new JavaBean2(flag*100+5, "ComplexJavaBean-"+flag+"-javaBean2Set-1"));
		javaBean2Set.add(new JavaBean2(flag*100+6, "ComplexJavaBean-"+flag+"-javaBean2Set-2"));
		
		Map<Integer, JavaBean2> javaBean2Map=new HashMap<Integer, JavaBean2>();
		javaBean2Map.put(0, new JavaBean2(flag*100+7, "ComplexJavaBean-"+flag+"-javaBean2Map-1"));
		javaBean2Map.put(1, new JavaBean2(flag*100+8, "ComplexJavaBean-"+flag+"-javaBean2Map-2"));
		
		cb.setSimpleArray(simpleArray);
		cb.setSimpleList(simpleList);
		cb.setSimpleSet(simpleSet);
		cb.setSimpleMap(simpleMap);
		cb.setJavaBean2Array(javaBean2Array);
		cb.setJavaBean2List(javaBean2List);
		cb.setJavaBean2Set(javaBean2Set);
		cb.setJavaBean2Map(javaBean2Map);
		
		return cb;
	}
	
	private Integer[] stringArrayToIntArray(String[] strs)
	{
		Integer[] re=new Integer[strs.length];
		
		for(int i=0; i<re.length; i++)
		{
			re[i]=Integer.parseInt(strs[i]);
		}
		
		return re;
	}
	
	public static class MyBean implements Comparable<MyBean>
	{
		private String id;
		private Integer size;
		private MyBean2 myBean2;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Integer getSize() {
			return size;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		public MyBean2 getMyBean2() {
			return myBean2;
		}
		public void setMyBean2(MyBean2 myBean2) {
			this.myBean2 = myBean2;
		}
		
		public int compareTo(MyBean o)
		{
			return this.id.compareTo(o.getId());
		}
	}
	public static class MyBean2
	{
		private String id;
		private Integer size;
		private MyBean myBean;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Integer getSize() {
			return size;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		public MyBean getMyBean() {
			return myBean;
		}
		public void setMyBean(MyBean myBean) {
			this.myBean = myBean;
		}
	}
	
	public static enum TestEnum
	{
		ENUM_1,
		ENUM_2
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
	
	public static class JavaBeanChild extends JavaBean
	{
		private String friend;

		public String getFriend() {
			return friend;
		}

		public void setFriend(String friend) {
			this.friend = friend;
		}
	}
	
	public static class JavaBean2 implements Comparable<JavaBean2>
	{
		private int id;
		private String name;
		private JavaBean javaBean;
		
		public JavaBean2()
		{
			super();
		}
		
		public JavaBean2(int id, String name)
		{
			super();
			this.id = id;
			this.name = name;
		}

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
