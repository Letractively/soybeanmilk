package test.unit.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.vp.PathNode;


public class TestPathNode
{
	@Before
	public void setUp(){}
	
	@Test
	public void createNotVariable()
	{
		{
			String s="abc";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( !pn.isVariable() );
			Assert.assertEquals(s, pn.getNodeValue());
		}
		
		{
			String s="{abc";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( !pn.isVariable() );
			Assert.assertEquals(s, pn.getNodeValue());
		}
		
		{
			String s="abc}";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( !pn.isVariable() );
			Assert.assertEquals(s, pn.getNodeValue());
		}
	}
	
	@Test
	public void createIsVariable()
	{
		{
			String s="{abc}";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( pn.isVariable() );
			Assert.assertEquals("abc", pn.getNodeValue());
		}
		
		{
			String s="{}";
			PathNode pn=new PathNode(s);
			
			Assert.assertTrue( pn.isVariable() );
			Assert.assertEquals("", pn.getNodeValue());
		}
	}
	
	@Test
	public void compareTo()
	{
		{
			PathNode pn0=new PathNode("abc");
			PathNode pn1=new PathNode("{def}");
			
			Assert.assertTrue( pn0.compareTo(pn1)>0 );
		}
		
		{
			PathNode pn0=new PathNode("{abc}");
			PathNode pn1=new PathNode("{def}");
			
			Assert.assertTrue( pn0.compareTo(pn1)==0 );
		}
		
		{
			PathNode pn0=new PathNode("{abc}");
			PathNode pn1=new PathNode("{}");
			
			Assert.assertTrue( pn0.compareTo(pn1)==0 );
		}
		
		{
			PathNode pn0=new PathNode("{abc}");
			PathNode pn1=new PathNode("def");
			
			Assert.assertTrue( pn0.compareTo(pn1)<0 );
		}
	}
}
