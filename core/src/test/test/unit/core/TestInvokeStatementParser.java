package test.unit.core;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.Test;
import org.soybeanMilk.core.config.parser.InvokeStatementParser;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;


public class TestInvokeStatementParser
{
	private DefaultResolverFactory resolverFactory= new DefaultResolverFactory();
	
	@Test
	public void parse() throws Exception
	{
		resolverFactory.addResolver("testResolver", new TestResolver());
		
		{
			Invoke invoke =new Invoke();
			String stmt=" testResolver . method_a( ); ";
			new InvokeStatementParser(invoke, stmt, resolverFactory).parse();
			
			Method m=TestResolver.class.getMethod("method_a", new Class[]{});
			
			Assert.assertNull(invoke.getResultKey());
			Assert.assertNull(invoke.getArgs());
			Assert.assertTrue(resolverFactory.getResolver("testResolver") == invoke.getResolverProvider().getResolver());
			Assert.assertTrue(m.getName().equals( invoke.getMethod().getName()));
		}
		
		{
			Invoke invoke =new Invoke();
			String stmt=" testResult =  testResolver . method_b  ( my.argKey, 10); ";
			new InvokeStatementParser(invoke, stmt, resolverFactory).parse();
			
			Method m=TestResolver.class.getMethod("method_b", new Class[]{TestResolver.class, int.class});
			
			Assert.assertEquals("testResult", invoke.getResultKey());
			Assert.assertEquals("my.argKey", invoke.getArgs()[0].getKey());
			Assert.assertNull(invoke.getArgs()[0].getValue());
			Assert.assertNull(invoke.getArgs()[1].getKey());
			Assert.assertEquals(new Integer(10), invoke.getArgs()[1].getValue());
			Assert.assertTrue(resolverFactory.getResolver("testResolver") == invoke.getResolverProvider().getResolver());
			Assert.assertTrue(m.getName().equals( invoke.getMethod().getName()));
		}
	}
	
	@Test
	public void stringToArgProperty()
	{
		//关键字
		{
			String stmt="resultKey";
			Arg arg=new Arg();
			arg.setType(Integer.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(stmt, arg.getKey());
			Assert.assertNull(arg.getValue());
		}
		
		//关键字
		{
			String stmt="my.resultKey";
			Arg arg=new Arg();
			arg.setType(Integer.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(stmt, arg.getKey());
			Assert.assertNull(arg.getValue());
		}
		
		//byte
		{
			String stmt="124";
			Arg arg=new Arg();
			arg.setType(byte.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Byte(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//byte
		{
			String stmt="124";
			Arg arg=new Arg();
			arg.setType(Byte.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Byte(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//double
		{
			String stmt="3.53244d";
			Arg arg=new Arg();
			arg.setType(double.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Double(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//double
		{
			String stmt="3.5342344";
			Arg arg=new Arg();
			arg.setType(Double.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Double(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//float
		{
			String stmt="3.5f";
			Arg arg=new Arg();
			arg.setType(float.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Float(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//float
		{
			String stmt="3.534";
			Arg arg=new Arg();
			arg.setType(Float.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Float(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//int
		{
			String stmt="2344134";
			Arg arg=new Arg();
			arg.setType(int.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Integer(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//int
		{
			String stmt="34241324";
			Arg arg=new Arg();
			arg.setType(Integer.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Integer(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//long
		{
			String stmt="234413424134234";
			Arg arg=new Arg();
			arg.setType(long.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Long(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//long
		{
			String stmt="34241324";
			Arg arg=new Arg();
			arg.setType(Long.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Long(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}

		//short
		{
			String stmt="2343";
			Arg arg=new Arg();
			arg.setType(short.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Short(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//short
		{
			String stmt="231";
			Arg arg=new Arg();
			arg.setType(Short.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Short(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//boolean
		{
			String stmt="true";
			Arg arg=new Arg();
			arg.setType(boolean.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Boolean(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//boolean
		{
			String stmt="false";
			Arg arg=new Arg();
			arg.setType(Boolean.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Boolean(stmt), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//null
		{
			String stmt="null";
			Arg arg=new Arg();
			arg.setType(Boolean.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertNull(arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//String
		{
			String stmt="\" abcde \\\" fg \\t \\n \r \\\\ \\' ' g \"";
			Arg arg=new Arg();
			arg.setType(String.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(" abcde \" fg \t \n \r \\ \' ' g ", arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//char
		{
			String stmt="'\\t'";
			Arg arg=new Arg();
			arg.setType(char.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Character('\t'), arg.getValue());
			Assert.assertNull(arg.getKey());
		}
		
		//char
		{
			String stmt="'\\u00FF'";
			Arg arg=new Arg();
			arg.setType(Character.class);
			InvokeStatementParser.stringToArgProperty(arg, stmt);
			
			Assert.assertEquals(new Character('\u00FF'), arg.getValue());
			Assert.assertNull(arg.getKey());
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
