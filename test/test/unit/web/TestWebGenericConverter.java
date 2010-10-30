package test.unit.web;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.bean.WebGenericConverter;


public class TestWebGenericConverter
{
	private WebGenericConverter converter;
	
	@Before
	public void setUp()
	{
		converter=new WebGenericConverter();
	}
	
	@Test
	public void convertNull()
	{
		{
			Object re=converter.convert(null, Object.class);
			
			Assert.assertNull(re);
		}
		
		{
			Object re=converter.convert(null, int.class);
			
			Assert.assertEquals(new Integer(0), re);
		}
		
		{
			
		}
	}
}
