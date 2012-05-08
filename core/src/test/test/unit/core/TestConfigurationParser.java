package test.unit.core;


import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.Interceptors;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.exe.support.KeyArg;
import org.soybeanMilk.core.exe.support.ResolverObjectFactory;

public class TestConfigurationParser
{
	private Configuration config;
	
	@Before
	public void setUp() throws Exception{}
	
	@After
	public void tearDown() throws Exception{}
	
	@Test
	public void testParseResult() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-0.xml");
		
		ResolverObjectFactory rf=config.getResolverObjectFactory();
		Assert.assertEquals(TestResolver.class, rf.getResolverObject("tr").getClass());
		Assert.assertEquals(TestResolver.class, rf.getResolverObject("tr1").getClass());
		Assert.assertEquals(TestResolver.class, rf.getResolverObject("tr2").getClass());
		
		GenericConverter gc=config.getGenericConverter();
		Assert.assertEquals(TestConverter.class, gc.getConverter(String.class, int.class).getClass());
		Assert.assertEquals(TestConverter.class, gc.getConverter(String.class, float.class).getClass());
		
		Interceptors ii=config.getInterceptorInfo();
		Assert.assertEquals("global_before", ii.getBefore().getName());
		Assert.assertEquals("global_after", ii.getAfter().getName());
		Assert.assertEquals("m1_exception", ii.getException().getName());
		Assert.assertEquals("executionKey", ii.getExecutionKey());
		
		{
			Invoke exe=(Invoke)config.getExecutable("global_before");
			Assert.assertEquals( rf.getResolverObject("tr"), exe.getResolverProvider().getResolver(null).getResolverObject());
			Assert.assertEquals("test0", exe.getMethodName());
			Assert.assertNull(exe.getArgs());
		}
		
		{
			Action exe=(Action)config.getExecutable("global_after");
			Assert.assertNull(exe.getExecutables());
		}

		{
			Action exe=(Action)config.getExecutable("global_");
			Assert.assertNotNull(exe);
		}
		
		{
			Action exe=(Action)config.getExecutable("global_exe0");
			Assert.assertNotNull(exe);
		}
		
		{
			Action exe=(Action)config.getExecutable("global_exe1");
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertNull(ivk0.getName());
			Assert.assertEquals("test0", ivk0.getMethodName());
			Assert.assertEquals( rf.getResolverObject("tr"), ivk0.getResolverProvider().getResolver(null).getResolverObject());
			Assert.assertNull(ivk0.getArgs());
			Assert.assertNull(ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
		}
		
		{
			Action exe=(Action)config.getExecutable("global_exe2");
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertEquals("test2", ivk0.getMethodName());
			Assert.assertEquals( rf.getResolverObject("tr"), ivk0.getResolverProvider().getResolver(null).getResolverObject());
			Arg[] args=ivk0.getArgs();
			Assert.assertEquals(2, args.length);
			Assert.assertEquals(KeyArg.class, args[0].getClass());
			Assert.assertEquals("arg0", ((KeyArg)args[0]).getKey());
			Assert.assertEquals(KeyArg.class, args[1].getClass());
			Assert.assertEquals("arg1", ((KeyArg)args[1]).getKey());
			Assert.assertEquals("result", ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
		}
		
		{
			Invoke invoke=(Invoke)config.getExecutable("global_testLiterals");
			Arg[] args=invoke.getArgs();
			
			Assert.assertEquals(new Byte((byte)10), args[0].getValue(null, null, null, null));
			Assert.assertEquals(new Byte((byte)10), args[1].getValue(null, null, null, null));
			
			Assert.assertEquals(new Short((short)10), args[2].getValue(null, null, null, null));
			Assert.assertEquals(new Short((short)10), args[3].getValue(null, null, null, null));
			
			Assert.assertEquals(new Integer(10), args[4].getValue(null, null, null, null));
			Assert.assertEquals(new Integer(10), args[5].getValue(null, null, null, null));
			
			Assert.assertEquals(new Long(10), args[6].getValue(null, null, null, null));
			Assert.assertEquals(new Long(10), args[7].getValue(null, null, null, null));
			
			Assert.assertEquals(new Float(10f), args[8].getValue(null, null, null, null));
			Assert.assertEquals(new Float(10f), args[9].getValue(null, null, null, null));
			
			Assert.assertEquals(new Double(10d), args[10].getValue(null, null, null, null));
			Assert.assertEquals(new Double(10d), args[11].getValue(null, null, null, null));
			
			Assert.assertEquals(new Integer(1234), args[12].getValue(null, null, null, null));
			Assert.assertEquals(new Double(1.234), args[13].getValue(null, null, null, null));
			
			Assert.assertEquals("string", args[14].getValue(null, null, null, null));
			Assert.assertEquals('c', args[15].getValue(null, null, null, null));
			
			Assert.assertEquals(Boolean.TRUE, args[16].getValue(null, null, null, null));
			Assert.assertEquals(Boolean.FALSE, args[17].getValue(null, null, null, null));
			Assert.assertNull(args[18].getValue(null, null, null, null));
			
			Assert.assertEquals(KeyArg.class, args[19].getClass());
			Assert.assertEquals("someKey", ((KeyArg)args[19]).getKey());
		}
		
		{
			Action exe=(Action)config.getExecutable("m1_exe0");
			
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertEquals("staticTest", ivk0.getMethodName());
			Assert.assertNotNull(ivk0.getResolverProvider());
			Arg[] args=ivk0.getArgs();
			Assert.assertEquals(2, args.length);
			Assert.assertEquals(KeyArg.class, args[0].getClass());
			Assert.assertEquals("arg0", ((KeyArg)args[0]).getKey());
			Assert.assertEquals(KeyArg.class, args[1].getClass());
			Assert.assertEquals("arg1", ((KeyArg)args[1]).getKey());
			Assert.assertEquals("result", ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
		}
		
		{
			Action exe=(Action)config.getExecutable("m2_exe0");
			
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertNull(ivk0.getName());
			Assert.assertEquals("staticTest",  ivk0.getMethodName());
			Assert.assertNotNull( ivk0.getResolverProvider());
			Arg[] args=ivk0.getArgs();
			Assert.assertEquals(2, args.length);
			Assert.assertEquals(KeyArg.class, args[0].getClass());
			Assert.assertEquals("arg0", ((KeyArg)args[0]).getKey());
			Assert.assertEquals(KeyArg.class, args[1].getClass());
			Assert.assertEquals("arg1", ((KeyArg)args[1]).getKey());
			Assert.assertEquals("result", ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("m1_exe0") );
		}
		
		{
			Invoke exe=(Invoke)config.getExecutable("m2_exe1");
			
			Assert.assertEquals("breakerKey", exe.getBreaker());
			Assert.assertEquals("test0", exe.getMethodName());
			Assert.assertEquals( rf.getResolverObject("tr2"), exe.getResolverProvider().getResolver(null).getResolverObject());
			Arg[] args=exe.getArgs();
			Assert.assertNull(args);
		}
	}
	
	public static class TestConverter implements Converter
	{
		public <T> T convert(Object sourceObj, Type targetType)
		{
			return null;
		}
	}
	
	public static class TestResolver
	{
		public static void staticTest(String a0, Date a1){}
		
		public void before(){}
		
		public void after(){}
		
		public void exception(){}
		
		public void test0(){}
		
		public void test1(){}
		
		public void test2(String a0, Date a1){}
	}
}
