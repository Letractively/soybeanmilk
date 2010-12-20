package test.unit.web;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.vp.VariablePath;

public class TestVariablePath
{
	@Before
	public void setUp(){}
	
	@Test
	public void create()
	{
		{
			VariablePath vp=new VariablePath("/abc/def/ghi");
			Assert.assertTrue( !vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("abc/{def}/ghi");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("abc/{def}/ghi/");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("{abc}");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(1, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("/{abc}");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(1, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("/abc/{def}/ghi");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
		
		{
			VariablePath vp=new VariablePath("/abc/{def}/ghi/");
			Assert.assertTrue( vp.isVariable() );
			Assert.assertEquals(3, vp.getPathNodeLength());
		}
	}
	
	@Test
	public void compareTo()
	{
		{
			VariablePath vp0=new VariablePath("/abc/def/ghi");
			VariablePath vp1=new VariablePath("/abc/def/ghi");
			
			Assert.assertTrue( vp0.compareTo(vp1)==0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/def/ghi");
			VariablePath vp1=new VariablePath("/abc/def/{ghi}");
			
			Assert.assertTrue( vp0.compareTo(vp1)>0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/def");
			VariablePath vp1=new VariablePath("/abc/def/{ghi}");
			
			Assert.assertTrue( vp0.compareTo(vp1)<0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/{def}/{ghi}");
			VariablePath vp1=new VariablePath("/abc/def/ghi");
			
			Assert.assertTrue( vp0.compareTo(vp1)<0 );
		}
		
		{
			VariablePath vp0=new VariablePath("{ghi}/abc/def");
			VariablePath vp1=new VariablePath("/abc/def/{ghi}");
			
			Assert.assertTrue( vp0.compareTo(vp1)<0 );
		}
		
		{
			VariablePath vp0=new VariablePath("/abc/{def}/ghi");
			VariablePath vp1=new VariablePath("/abc/{ghi}/ghi");
			
			Assert.assertTrue( vp0.compareTo(vp1)==0 );
		}
	}
}
