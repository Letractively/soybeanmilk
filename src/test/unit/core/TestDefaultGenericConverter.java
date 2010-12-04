package unit.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
		String src = "1254324.3823823";
		
		BigDecimal dest = (BigDecimal)converter.convert(src, BigDecimal.class);
		
		Assert.assertEquals(new BigDecimal(src), dest);
	}
	
	@Test
	public void stringArrayToBigDecimalArray()
	{
		String[] src = new String[]{"1254324.3823823","2342.23879102348"};
		
		BigDecimal[] dest = (BigDecimal[])converter.convert(src, BigDecimal[].class);
		
		Assert.assertEquals(new BigDecimal(src[0]), dest[0]);
		Assert.assertEquals(new BigDecimal(src[1]), dest[1]);
	}
	
	@Test
	public void stringToBigInteger()
	{
		String src = "12349787293841930481029348234242134";
		
		BigInteger dest = (BigInteger)converter.convert(src, BigInteger.class);
		
		Assert.assertEquals(new BigInteger(src), dest);
	}
	
	@Test
	public void stringArrayToBigIntegerArray()
	{
		String[] src = new String[]{"12349787293841930481029348234242134","3898417890183910927834234"};
		
		BigInteger[] dest = (BigInteger[])converter.convert(src, BigInteger[].class);
		
		Assert.assertEquals(new BigInteger(src[0]), dest[0]);
		Assert.assertEquals(new BigInteger(src[1]), dest[1]);
	}
	
	@Test
	public void stringToBoolean()
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
	}
	
	@Test
	public void stringArrayToBooleanArray()
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
	
	@Test
	public void stringToByte()
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
	public void stringArrayToByteArray()
	{
		{
			String[] src=new String[]{"5","3","-1"};
			Byte[] dest = (Byte[])converter.convert(src, Byte[].class);
			
			Assert.assertEquals(new Byte((byte)5), dest[0]);
			Assert.assertEquals(new Byte((byte)3), dest[1]);
			Assert.assertEquals(new Byte((byte)-1), dest[2]);
		}
		
		{
			String[] src=new String[]{"5","3","-1"};
			byte[] dest = (byte[])converter.convert(src, byte[].class);
			
			Assert.assertEquals((byte)5, dest[0]);
			Assert.assertEquals((byte)3, dest[1]);
			Assert.assertEquals((byte)-1, dest[2]);
		}
	}
	
	@Test
	public void stringToCharacter()
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
	public void stringToDate()
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
	public void stringToDouble()
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
	public void stringToFloat()
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
	public void stringToInteger()
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
	public void stringToLong()
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
	public void stringArrayToLongArray()
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
	public void stringToShort()
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
	public void stringToSqlDate()
	{
		{
			String src = "2010-10-12";
			java.sql.Date dest = (java.sql.Date)converter.convert(src, java.sql.Date.class);
			
			Assert.assertEquals(src, new SimpleDateFormat("yyyy-MM-dd").format(dest.getTime()));
		}
	}
	
	@Test
	public void stringToSqlTime()
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
	public void stringToSqlTimestamp()
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
	public void stringToString()
	{
		String src = "string_abc";
		
		String dest = (String)converter.convert(src, String.class);
		
		Assert.assertEquals(src, dest);
	}
	
	@Test(expected = ConvertException.class)
	public void testNullToPrimitiveException()
	{
		Object src = null;
		
		converter.convert(src, int.class);
	}
}
