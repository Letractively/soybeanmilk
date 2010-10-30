package test.unit.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericConverter;


public class TestDefaultGenericConverter
{
	private GenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter = new DefaultGenericConverter();
	}
	
	@Test
	public void stringToBigDecimal()
	{
		String sourceObj = "1254324.3823823";
		
		BigDecimal bd = (BigDecimal)converter.convert(sourceObj, BigDecimal.class);
		
		Assert.assertEquals(new BigDecimal(sourceObj), bd);
	}
	
	@Test
	public void stringArrayToBigDecimalArray()
	{
		BigDecimal[] actual=new BigDecimal[]{new BigDecimal("1254324.3823823"), new BigDecimal("2342.23879102348")};
		
		String[] sourceObj = new String[]{"1254324.3823823","2342.23879102348"};
		
		BigDecimal[] bd = (BigDecimal[])converter.convert(sourceObj, BigDecimal[].class);
		
		Assert.assertEquals(bd[0], actual[0]);
		Assert.assertEquals(bd[1], actual[1]);
	}
	
	@Test
	public void stringToBigInteger()
	{
		String sourceObj = "12349787293841930481029348234242134";
		
		BigInteger bd = (BigInteger)converter.convert(sourceObj, BigInteger.class);
		
		Assert.assertEquals(new BigInteger(sourceObj), bd);
	}
	
	@Test
	public void stringToBoolean()
	{
		{
			String sourceObj = "true";
			Boolean re = (Boolean)converter.convert(sourceObj, Boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, re);
		}
		
		{
			String sourceObj = "1";
			Boolean re = (Boolean)converter.convert(sourceObj, Boolean.class);
			
			Assert.assertEquals(Boolean.TRUE, re);
		}
		
		{
			String sourceObj = "false";
			Boolean re = (Boolean)converter.convert(sourceObj, Boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, re);
		}
		
		{
			String sourceObj = "0";
			Boolean re = (Boolean)converter.convert(sourceObj, Boolean.class);
			
			Assert.assertEquals(Boolean.FALSE, re);
		}
	}
	
	@Test
	public void stringToByte()
	{
		{
			String s = "5";
			Byte b = (Byte)converter.convert(s, byte.class);
			
			Assert.assertEquals(new Byte(s), b);
		}
		
		{
			String s = "5";
			Byte b = (Byte)converter.convert(s, Byte.class);
			
			Assert.assertEquals(new Byte(s), b);
		}
	}
	
	@Test
	public void stringToCalendar()
	{
		{
			String s = "2010";
			Calendar re = (Calendar)converter.convert(s, Calendar.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy").format(re.getTime()));
		}
		
		{
			String s = "2010-10";
			Calendar re = (Calendar)converter.convert(s, Calendar.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12";
			Calendar re = (Calendar)converter.convert(s, Calendar.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13";
			Calendar re = (Calendar)converter.convert(s, Calendar.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00";
			Calendar re = (Calendar)converter.convert(s, Calendar.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00:00";
			Calendar re = (Calendar)converter.convert(s, Calendar.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(re.getTime()));
		}
	}
	
	@Test
	public void stringToCharacter()
	{
		{
			String s = "2";
			
			Character re = (Character)converter.convert(s, Character.class);
			Assert.assertEquals('2', re.charValue());
		}
		
		{
			String s = "2";
			
			Character re = (Character)converter.convert(s, char.class);
			Assert.assertEquals('2', re.charValue());
		}
	}
	
	@Test
	public void stringToDate()
	{
		{
			String s = "2010";
			Date re = (Date)converter.convert(s, Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy").format(re.getTime()));
		}
		
		{
			String s = "2010-10";
			Date re = (Date)converter.convert(s, Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12";
			Date re = (Date)converter.convert(s, Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13";
			Date re = (Date)converter.convert(s, Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00";
			Date re = (Date)converter.convert(s, Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00:00";
			Date re = (Date)converter.convert(s, Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(re.getTime()));
		}
	}
	
	@Test
	public void stringToDouble()
	{
		{
			String s = "1";
			
			Double re = (Double)converter.convert(s, Double.class);
			
			Assert.assertEquals(1d,re.doubleValue());
		}
		
		{
			String s = "1.34";
			
			Double re = (Double)converter.convert(s, Double.class);
			
			Assert.assertEquals(1.34,re.doubleValue());
		}
		
		{
			String s = "1.34d";
			
			Double re = (Double)converter.convert(s, Double.class);
			
			Assert.assertEquals(1.34,re.doubleValue());
		}
		
		{
			String s = "1.34d";
			
			Double re = (Double)converter.convert(s, double.class);
			
			Assert.assertEquals(1.34,re.doubleValue());
		}
	}
	
	@Test
	public void stringToFloat()
	{
		{
			String s = "1";
			
			Float re = (Float)converter.convert(s, Float.class);
			
			Assert.assertEquals(1f,re.floatValue());
		}
		
		{
			String s = "1.34f";
			
			Float re = (Float)converter.convert(s, Float.class);
			
			Assert.assertEquals(1.34f,re.floatValue());
		}
		
		{
			String s = "1.34f";
			
			Float re = (Float)converter.convert(s, float.class);
			
			Assert.assertEquals(1.34f,re.floatValue());
		}
	}
	
	@Test
	public void stringToInteger()
	{
		String s = "1";
		
		{
			Integer i = (Integer)converter.convert(s, int.class);
			Assert.assertEquals(i.intValue(), 1);
		}
		
		{
			Integer i = (Integer)converter.convert(s, Integer.class);
			Assert.assertEquals(i.intValue(), 1);
		}
	}
	
	@Test
	public void stringToLong()
	{
		String s = "13424235532342";
		
		{
			Long i = (Long)converter.convert(s, long.class);
			Assert.assertEquals(13424235532342l, i.longValue());
		}
		
		{
			Long i = (Long)converter.convert(s, Long.class);
			Assert.assertEquals(13424235532342l, i.longValue());
		}
	}
	
	@Test
	public void stringArrayToLongArray()
	{
		String[] src=new String[]{"2342353413241234", "1342413542348779"};
		{
			long[] expected=new long[]{Long.parseLong(src[0]), Long.parseLong(src[1])};
			
			long[] actual = (long[])converter.convert(src, long[].class);
			
			Assert.assertEquals(expected[0], actual[0]);
			Assert.assertEquals(expected[1], actual[1]);
		}
		
		{
			Long[] expected=new Long[]{Long.parseLong(src[0]), Long.parseLong(src[1])};
			
			Long[] actual = (Long[])converter.convert(src, Long[].class);
			
			Assert.assertEquals(expected[0], actual[0]);
			Assert.assertEquals(expected[1], actual[1]);
		}
	}
	
	@Test
	public void stringToShort()
	{
		String s = "1342";
		
		{
			Short i = (Short)converter.convert(s, short.class);
			Assert.assertEquals(1342, i.shortValue());
		}
		
		{
			Short i = (Short)converter.convert(s, Short.class);
			Assert.assertEquals(1342, i.shortValue());
		}
		
		{
			Short i = (Short)converter.convert(s, Short.class);
			Assert.assertEquals(1342, i.shortValue());
		}
	}
	
	@Test
	public void stringToSqlDate()
	{
		{
			String s = "2010";
			java.sql.Date re = (java.sql.Date)converter.convert(s, java.sql.Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy").format(re.getTime()));
		}
		
		{
			String s = "2010-10";
			java.sql.Date re = (java.sql.Date)converter.convert(s, java.sql.Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12";
			java.sql.Date re = (java.sql.Date)converter.convert(s, java.sql.Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13";
			java.sql.Date re = (java.sql.Date)converter.convert(s, java.sql.Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00";
			java.sql.Date re = (java.sql.Date)converter.convert(s, java.sql.Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00:00";
			java.sql.Date re = (java.sql.Date)converter.convert(s, java.sql.Date.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(re.getTime()));
		}
	}
	
	@Test
	public void stringToSqlTime()
	{
		{
			String s = "2010";
			java.sql.Time re = (java.sql.Time)converter.convert(s, java.sql.Time.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy").format(re.getTime()));
		}
		
		{
			String s = "2010-10";
			java.sql.Time re = (java.sql.Time)converter.convert(s, java.sql.Time.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12";
			java.sql.Time re = (java.sql.Time)converter.convert(s, java.sql.Time.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13";
			java.sql.Time re = (java.sql.Time)converter.convert(s, java.sql.Time.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00";
			java.sql.Time re = (java.sql.Time)converter.convert(s, java.sql.Time.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00:00";
			java.sql.Time re = (java.sql.Time)converter.convert(s, java.sql.Time.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(re.getTime()));
		}
	}
	
	@Test
	public void stringToSqlTimestamp()
	{
		{
			String s = "2010";
			java.sql.Timestamp re = (java.sql.Timestamp)converter.convert(s, java.sql.Timestamp.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy").format(re.getTime()));
		}
		
		{
			String s = "2010-10";
			java.sql.Timestamp re = (java.sql.Timestamp)converter.convert(s, java.sql.Timestamp.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12";
			java.sql.Timestamp re = (java.sql.Timestamp)converter.convert(s, java.sql.Timestamp.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13";
			java.sql.Timestamp re = (java.sql.Timestamp)converter.convert(s, java.sql.Timestamp.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00";
			java.sql.Timestamp re = (java.sql.Timestamp)converter.convert(s, java.sql.Timestamp.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(re.getTime()));
		}
		
		{
			String s = "2010-10-12 13:00:00";
			java.sql.Timestamp re = (java.sql.Timestamp)converter.convert(s, java.sql.Timestamp.class);
			
			Assert.assertEquals(s, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(re.getTime()));
		}
	}
	
	@Test
	public void stringToString()
	{
		String s = "string_abc";
		
		String re = (String)converter.convert(s, String.class);
		
		Assert.assertEquals(s, re);
	}
	
	@Test(expected = ConvertException.class)
	public void testNullToPrimitiveException()
	{
		Object o = null;
		
		converter.convert(o, int.class);
	}
}
