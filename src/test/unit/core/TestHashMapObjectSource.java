package unit.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.os.HashMapObjectSource;



public class TestHashMapObjectSource
{
	private HashMapObjectSource objSource;
	
	@Before
	public void setUp()
	{
		objSource=new HashMapObjectSource();
	}
	
	@Test
	public void getNoConverter()
	{
		{
			Object dest=objSource.get(null, null);
			Assert.assertNull(dest);
		}
		{
			Object dest=objSource.get("key", null);
			Assert.assertNull(dest);
		}
		{
			Object dest=objSource.get("key", String.class);
			Assert.assertNull(dest);
		}
		{
			String src="hi";
			objSource.set("key", src);
			
			String dest=(String)objSource.get("key", String.class);
			Assert.assertEquals(src, dest);
		}
		{
			Integer src=1;
			
			objSource.set("key", src);
			Integer dest=(Integer)objSource.get("key", Integer.class);
			Assert.assertEquals(src, dest);
		}
		
		{
			Integer src=1;
			
			objSource.set(1, src);
			Integer dest=(Integer)objSource.get(1, int.class);
			Assert.assertEquals(src, dest);
		}
	}
	
	@Test
	public void getWithConverter()
	{
		addGenericConverter();
		
		{
			String src="1";
			
			objSource.set("key", src);
			Integer dest=(Integer)objSource.get("key", int.class);
			Assert.assertEquals(Integer.parseInt(src), dest.intValue());
		}
		
		{
			String[] src=new String[]{"1","2"};
			objSource.set("key", src);
			
			int[] dest=(int[])objSource.get("key", int[].class);
			
			Assert.assertEquals(Integer.parseInt(src[0]), dest[0]);
			Assert.assertEquals(Integer.parseInt(src[1]), dest[1]);
		}
	}
	
	@Test(expected=ObjectSourceException.class)
	public void getPrimitiveForNull()
	{
		objSource.get("key", int.class);
	}
	
	protected void addGenericConverter()
	{
		objSource.setGenericConverter(new DefaultGenericConverter());
	}
}
