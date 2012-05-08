package test.unit.core;

import junit.framework.Assert;

import org.junit.Test;
import org.soybeanMilk.core.config.parser.InvokeStatementParser;

public class TestInvokeStatementParser
{
	@Test
	public void parse() throws Exception
	{
		{
			String stmt=" testResolver . method_a( ); ";
			InvokeStatementParser isp=new InvokeStatementParser(stmt);
			isp.parse();
			
			Assert.assertNull(isp.getResultKey());
			Assert.assertEquals("testResolver", isp.getResolver());
			Assert.assertEquals("method_a", isp.getMethodName());
			Assert.assertNull(isp.getArgs());
		}
		
		{
			String stmt=" testResult =  testResolver . method_b  ( my.argKey, 10); ";
			InvokeStatementParser isp=new InvokeStatementParser(stmt);
			isp.parse();
			
			Assert.assertEquals("testResult", isp.getResultKey());
			Assert.assertEquals("testResolver", isp.getResolver());
			Assert.assertEquals("method_b", isp.getMethodName());
			Assert.assertEquals("my.argKey", isp.getArgs()[0]);
			Assert.assertEquals("10", isp.getArgs()[1]);
		}
	}
	
	protected static class TestResolver
	{
		public void method_a(){}
		
		public TestResolver method_b(TestResolver arg0, int arg1)
		{
			return null;
		}
	}
}
