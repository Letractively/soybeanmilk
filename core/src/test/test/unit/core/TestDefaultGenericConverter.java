package test.unit.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;

import test.unit.web.MockParameterizedType;


public class TestDefaultGenericConverter
{
	private DefaultGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter = new DefaultGenericConverter();
	}
	
	@Test
	public void convertString_toBigDecimal() throws Exception
	{
		String src = "1254324.3823823";
		
		BigDecimal dest = (BigDecimal)converter.convert(src, BigDecimal.class);
		
		Assert.assertEquals(new BigDecimal(src), dest);
	}
	
	@Test
	public void convertString_toBigInteger() throws Exception
	{
		String src = "12349787293841930481029348234242134";
		
		BigInteger dest = (BigInteger)converter.convert(src, BigInteger.class);
		
		Assert.assertEquals(new BigInteger(src), dest);
	}
	
	@Test
	public void convertString_toBoolean() throws Exception
	{
		{
			String src = "true";
			Boolean dest = (Boolean)converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "1";
			Boolean dest = (Boolean)converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "false";
			Boolean dest = (Boolean)converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
		
		{
			String src = "0";
			Boolean dest = (Boolean)converter.convert(src, Boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
		
		{
			String src = "true";
			Boolean dest = (Boolean)converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "1";
			Boolean dest = (Boolean)converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, dest);
		}
		
		{
			String src = "false";
			Boolean dest = (Boolean)converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
		
		{
			String src = "0";
			Boolean dest = (Boolean)converter.convert(src, boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, dest);
		}
	}
	
	@Test
	public void convertString_toByte() throws Exception
	{
		{
			String src = "5";
			Byte dest = (Byte)converter.convert(src, byte.class);
			
			Assert.assertEquals(new Byte(src), dest);
		}
		
		{
			String src = "5";
			Byte dest = (Byte)converter.convert(src, Byte.class);
			
			Assert.assertEquals(new Byte(src), dest);
		}
	}
	
	@Test
	public void convertString_toCharacter() throws Exception
	{
		{
			String src = "2";
			
			Character dest = (Character)converter.convert(src, Character.class);
			Assert.assertEquals(new Character('2'), dest);
		}
		
		{
			String src = "2";
			
			Character dest = (Character)converter.convert(src, char.class);
			Assert.assertEquals('2', dest.charValue());
		}
	}
	
	@Test
	public void convertString_toDate() throws Exception
	{
		{
			String src = "2010";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy").format(dest.getTime()));
		}
		
		{
			String src = "2010-10";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12 13";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12 13:00";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dest.getTime()));
		}
		
		{
			String src = "2010-10-12 13:00:00";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dest.getTime()));
		}

		{
			String src = "2010-10-12 13:00:00.555";
			Date dest = (Date)converter.convert(src, Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dest.getTime()));
		}
	}
	
	@Test
	public void convertString_toDouble() throws Exception
	{
		{
			String src = "1";
			
			Double dest = (Double)converter.convert(src, Double.class);
			
			Assert.assertEquals(new Double(src),dest);
		}
		
		{
			String src = "1.34";
			
			Double dest = (Double)converter.convert(src, Double.class);
			
			Assert.assertEquals(Double.parseDouble(src), dest.doubleValue());
		}
		
		{
			String src = "1.34d";
			
			Double dest = (Double)converter.convert(src, Double.class);
			
			Assert.assertEquals(new Double(src), dest.doubleValue());
		}
		
		{
			String src = "1.34d";
			
			Double dest = (Double)converter.convert(src, double.class);
			
			Assert.assertEquals(Double.parseDouble(src), dest.doubleValue());
		}
	}
	
	@Test
	public void convertString_toEnum() throws Exception
	{
		{
			String src="ENUM_1";
			TestEnum te=(TestEnum)converter.convert(src, TestEnum.class);
			
			Assert.assertEquals(src, te.toString());
		}
		{
			String src="ENUM_2";
			TestEnum te=(TestEnum)converter.convert(src, TestEnum.class);
			
			Assert.assertEquals(src, te.toString());
		}
	}
	
	@Test
	public void convertString_toFloat() throws Exception
	{
		{
			String src = "1";
			
			Float dest = (Float)converter.convert(src, Float.class);
			
			Assert.assertEquals(new Float(src), dest.floatValue());
		}
		
		{
			String src = "1.34f";
			
			Float dest = (Float)converter.convert(src, Float.class);
			
			Assert.assertEquals(new Float(src),dest.floatValue());
		}
		
		{
			String src = "1.34f";
			
			Float dest = (Float)converter.convert(src, float.class);
			
			Assert.assertEquals(Float.parseFloat(src),dest.floatValue());
		}
	}
	
	@Test
	public void convertString_toInteger() throws Exception
	{
		String src = "1";
		
		{
			Integer dest = (Integer)converter.convert(src, int.class);
			Assert.assertEquals(Integer.parseInt(src), dest.intValue());
		}
		
		{
			Integer dest = (Integer)converter.convert(src, Integer.class);
			Assert.assertEquals(new Integer(src), dest);
		}
	}
	
	@Test
	public void convertString_toLong() throws Exception
	{
		String src = "13424235532342";
		
		{
			Long dest = (Long)converter.convert(src, long.class);
			Assert.assertEquals(Long.parseLong(src), dest.longValue());
		}
		
		{
			Long dest = (Long)converter.convert(src, Long.class);
			Assert.assertEquals(new Long(src), dest);
		}
	}
	
	@Test
	public void convertStringArray_toLongArray() throws Exception
	{
		String[] src=new String[]{"2342353413241234", "1342413542348779"};
		{
			long[] dest = (long[])converter.convert(src, long[].class);
			
			Assert.assertEquals(Long.parseLong(src[0]), dest[0]);
			Assert.assertEquals(Long.parseLong(src[1]), dest[1]);
		}
		
		{
			Long[] dest = (Long[])converter.convert(src, Long[].class);
			
			Assert.assertEquals(new Long(src[0]), dest[0]);
			Assert.assertEquals(new Long(src[1]), dest[1]);
		}
	}
	
	@Test
	public void convertString_toShort() throws Exception
	{
		String src = "1342";
		
		{
			Short dest = (Short)converter.convert(src, short.class);
			Assert.assertEquals(Short.parseShort(src), dest.shortValue());
		}
		
		{
			Short dest = (Short)converter.convert(src, Short.class);
			Assert.assertEquals(new Short(src), dest);
		}
	}
	
	@Test
	public void convertString_toSqlDate() throws Exception
	{
		{
			String src = "2010-10-12";
			java.sql.Date dest = (java.sql.Date)converter.convert(src, java.sql.Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd").format(dest.getTime()));
		}
	}
	
	@Test
	public void convertString_toSqlTime() throws Exception
	{
		{
			String src = "15:30:20";
			java.sql.Time dest = (java.sql.Time)converter.convert(src, java.sql.Time.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("HH:mm:ss").format(dest.getTime()));
		}
		
		{
			String src = "15:30:20.333";
			java.sql.Time dest = (java.sql.Time)converter.convert(src, java.sql.Time.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("HH:mm:ss.SSS").format(dest.getTime()));
		}
	}
	
	@Test
	public void convertString_toSqlTimestamp() throws Exception
	{
		{
			String src = "2010-10-12 13:00:00";
			java.sql.Timestamp dest = (java.sql.Timestamp)converter.convert(src, java.sql.Timestamp.class);
			
			String destStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dest.getTime());
			Assert.assertEquals(src, destStr);
		}
		{
			String src = "2010-10-12 13:00:00.555";
			java.sql.Timestamp dest = (java.sql.Timestamp)converter.convert(src, java.sql.Timestamp.class);
			
			String destStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dest.getTime());
			Assert.assertEquals(src, destStr);
		}
	}
	
	@Test
	public void convertString_toString() throws Exception
	{
		String src = "string_abc";
		String dest = (String)converter.convert(src, String.class);
		
		Assert.assertEquals(src, dest);
	}
	
	@Test
	public void convertString_toAncestorType() throws Exception
	{
		String src = "string_abc";
		String dest = (String)converter.convert(src, Object.class);
		
		Assert.assertEquals(src, dest);
	}
	
	@Test
	public void convertString_emptyStringToObject() throws Exception
	{
		String src="";
		Integer re=(Integer)converter.convert(src, Integer.class);
		
		Assert.assertNull(re);
	}
	
	@Test(expected = ConvertException.class)
	public void convertString_emptyStringToPrimitive() throws Exception
	{
		String src="";
		Boolean re=(Boolean)converter.convert(src, boolean.class);
		
		Assert.assertNull(re);
	}
	
	@Test(expected = ConvertException.class)
	public void convertNull_toPrimitive() throws Exception
	{
		Object src = null;
		converter.convert(src, int.class);
	}

	@Test
	public void convertStringArray_toStringArray() throws Exception
	{
		String[] src = new String[]{"1254324.3823823","2342.23879102348"};
		
		String[] dest = (String[])converter.convert(src, String[].class);
		
		Assert.assertEquals(src[0], dest[0]);
		Assert.assertEquals(src[1], dest[1]);
	}
	
	@Test
	public void convertStringArray_toBooleanArray() throws Exception
	{
		{
			String[] src = new String[]{"true","1","false","0"};
			Boolean[] dest = (Boolean[])converter.convert(src, Boolean[].class);
			
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertStringArray_toGenericList() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		List<Integer> dest=(List<Integer>)converter.convert(src, new MockParameterizedType(List.class, Integer.class));
		
		for(int i=0;i<src.length;i++)
		{
			Assert.assertEquals(new Integer(src[i]), dest.get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertStringArray_toNormalList() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		try
		{
			List<Integer> dest=(List<Integer>)converter.convert(src, List.class);
			dest.size();
		}
		catch(Exception e)
		{
			Assert.assertTrue( e.getMessage().endsWith("only generic List converting is supported") );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertStringArray_toGenericSet() throws Exception
	{
		String[] src=new String[]{"123", "456", "789"};
		
		Set<Integer> dest=(Set<Integer>)converter.convert(src, new MockParameterizedType(Set.class, Integer.class));
		
		Assert.assertTrue( dest.size() == src.length );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertStringArray_toNormalSet()
	{
		String[] src=new String[]{"123", "456", "789"};
		
		try
		{
			Set<Integer> dest=(Set<Integer>)converter.convert(src, Set.class);
			dest.size();
		}
		catch(Exception e)
		{
			Assert.assertTrue( e.getMessage().endsWith("only generic Set converting is supported") );
		}
	}
	
	@Test
	public void convert_notSupported()
	{
		int src=3355;
		
		try
		{
			Byte dest=(Byte)converter.convert(src, byte.class);
			dest.byteValue();
		}
		catch(Exception e)
		{
			Assert.assertTrue( e.getMessage().startsWith("can not find Converter for converting") );
		}
	}
	
	@Test
	public void getProperty() throws Exception
	{
		MyBean bean=new MyBean();
		MyBean2 bean2=new MyBean2();
		
		bean.setId("111");
		bean.setSize(7);
		bean.setMyBean2(bean2);
		
		bean2.setId("222");
		bean2.setSize(8);
		bean2.setMyBean(bean);
		
		{
			String id=(String)converter.getProperty(bean, "id", null);
			Assert.assertEquals("111", id);
		}
		{
			Integer id=(Integer)converter.getProperty(bean, "id", int.class);
			Assert.assertEquals(111, id.intValue());
		}
		{
			Integer size=(Integer)converter.getProperty(bean, "size", null);
			Assert.assertEquals(7, size.intValue());
		}
		
		{
			String id=(String)converter.getProperty(bean, "myBean2.id", null);
			Assert.assertEquals("222", id);
		}
		{
			Integer id=(Integer)converter.getProperty(bean, "myBean2.id", int.class);
			Assert.assertEquals(222, id.intValue());
		}
		{
			Integer size=(Integer)converter.getProperty(bean, "myBean2.size", null);
			Assert.assertEquals(8, size.intValue());
		}
		
		{
			Integer size=(Integer)converter.getProperty(bean, "myBean2.myBean.myBean2.size", null);
			Assert.assertEquals(8, size.intValue());
		}
	}
	
	@Test(expected = GenericConvertException.class)
	public void getProperty_notExistProperty() throws Exception
	{
		MyBean bean=new MyBean();
		bean.setId("111");
		bean.setSize(7);
		
		converter.getProperty(bean, "age.size", null);
	}
	
	@Test
	public void setProperty() throws Exception
	{
		{
			MyBean bean=new MyBean();
			
			converter.setProperty(bean, "id", 111);
			converter.setProperty(bean, "size", "7");
			converter.setProperty(bean, "myBean2.myBean", bean);
			converter.setProperty(bean, "myBean2.id", "222");
			converter.setProperty(bean, "myBean2.size", 8);
			
			Assert.assertEquals("111", bean.getId());
			Assert.assertEquals(7, bean.getSize().intValue());
			Assert.assertTrue( bean == bean.getMyBean2().getMyBean() );
			Assert.assertEquals("222", bean.getMyBean2().getId());
			Assert.assertEquals(8, bean.getMyBean2().getSize().intValue());
		}
	}
	
	public static class MyBean
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
}
