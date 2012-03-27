package test.unit.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericConvertException;
import org.soybeanMilk.core.os.HashMapObjectSource;



public class TestHashMapObjectSource
{
	private HashMapObjectSource objSource;
	
	@Before
	public void setUp()
	{
		objSource=new HashMapObjectSource(new DefaultGenericConverter());
	}
	
	@Test
	public void get() throws Exception
	{
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
	
	@Test
	public void getPrimitiveForNull() throws Exception
	{
		GenericConvertException re=null;
		try
		{
			objSource.get("key", int.class);
		}
		catch(GenericConvertException e)
		{
			re=e;
		}
		
		Assert.assertTrue((re.getMessage().startsWith("can not convert 'null' to primitive")));
	}
}
