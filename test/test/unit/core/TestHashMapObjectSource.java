package test.unit.core;

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
			Object o=objSource.get(null, null);
			Assert.assertNull(o);
		}
		{
			Object o=objSource.get("key", null);
			Assert.assertNull(o);
		}
		{
			Object o=objSource.get("key", String.class);
			Assert.assertNull(o);
		}
		{
			objSource.set("key", "hi");
			String o=(String)objSource.get("key", String.class);
			Assert.assertEquals(o, "hi");
		}
		{
			objSource.set(1, 1);
			Integer o=(Integer)objSource.get(1, Integer.class);
			Assert.assertEquals(o, new Integer(1));
		}
		
		{
			objSource.set(1, 1);
			Integer o=(Integer)objSource.get(1, int.class);
			Assert.assertEquals(o, new Integer(1));
		}
	}
	
	@Test
	public void getWithConverter()
	{
		addGenericConverter();
		
		{
			objSource.set("key", "1");
			Integer o=(Integer)objSource.get("key", int.class);
			Assert.assertEquals(o, new Integer(1));
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
