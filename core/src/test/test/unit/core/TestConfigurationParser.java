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
import org.soybeanMilk.core.config.InterceptorInfo;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.exe.resolver.ResolverFactory;

public class TestConfigurationParser
{
	private Configuration config;
	
	@Before
	public void setUp() throws Exception{}
	
	@After
	public void tearDown() throws Exception{}
	
	@Test
	public void testParseResult()
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-0.xml");
		
		ResolverFactory rf=config.getResolverFactory();
		Assert.assertEquals(TestResolver.class, rf.getResolver("tr").getClass());
		Assert.assertEquals(TestResolver.class, rf.getResolver("tr1").getClass());
		Assert.assertEquals(TestResolver.class, rf.getResolver("tr2").getClass());
		
		GenericConverter gc=config.getGenericConverter();
		Assert.assertEquals(TestConverter.class, gc.getConverter(String.class, int.class).getClass());
		Assert.assertEquals(TestConverter.class, gc.getConverter(String.class, float.class).getClass());
		
		InterceptorInfo ii=config.getInterceptorInfo();
		Assert.assertEquals("global_before", ii.getBeforeHandler().getName());
		Assert.assertEquals("global_after", ii.getAfterHandler().getName());
		Assert.assertEquals("m1_exception", ii.getExceptionHandler().getName());
		Assert.assertEquals("executionKey", ii.getExecutionKey());
		
		{
			Invoke exe=(Invoke)config.getExecutable("global_before");
			Assert.assertEquals( rf.getResolver("tr"), exe.getResolverProvider().getResolver());
			Assert.assertEquals(Invoke.findMethodThrow(TestResolver.class, "test0", 0), exe.getMethod());
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
			Assert.assertEquals(Invoke.findMethodThrow(TestResolver.class, "test0", 0), ivk0.getMethod());
			Assert.assertEquals( rf.getResolver("tr"), ivk0.getResolverProvider().getResolver());
			Assert.assertNull(ivk0.getArgs());
			Assert.assertNull(ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
		}
		
		{
			Action exe=(Action)config.getExecutable("global_exe2");
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertEquals("test2", ivk0.getName());
			Assert.assertEquals(Invoke.findMethodThrow(TestResolver.class, "test2", 2), ivk0.getMethod());
			Assert.assertEquals( rf.getResolver("tr"), ivk0.getResolverProvider().getResolver());
			Arg[] args=ivk0.getArgs();
			Assert.assertEquals(2, args.length);
			Assert.assertEquals("arg0", args[0].getKey());
			Assert.assertEquals(String.class, args[0].getType());
			Assert.assertEquals("arg1", args[1].getKey());
			Assert.assertEquals(Date.class, args[1].getType());
			Assert.assertEquals("result", ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
		}
		
		{
			Action exe=(Action)config.getExecutable("m1_exe0");
			
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertEquals("staticTest", ivk0.getName());
			Assert.assertEquals(Invoke.findMethodThrow(TestResolver.class, "staticTest", 2), ivk0.getMethod());
			Assert.assertNull( ivk0.getResolverProvider());
			Arg[] args=ivk0.getArgs();
			Assert.assertEquals(2, args.length);
			Assert.assertEquals("arg0", args[0].getKey());
			Assert.assertEquals(String.class, args[0].getType());
			Assert.assertEquals("arg1", args[1].getKey());
			Assert.assertEquals(Date.class, args[1].getType());
			Assert.assertEquals("result", ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
		}
		
		{
			Action exe=(Action)config.getExecutable("m2_exe0");
			
			List<Executable> children=exe.getExecutables();
			
			Invoke ivk0=(Invoke)children.get(0);
			Assert.assertNull(ivk0.getName());
			Assert.assertEquals(Invoke.findMethodThrow(TestResolver.class, "staticTest", 2), ivk0.getMethod());
			Assert.assertNull( ivk0.getResolverProvider());
			Arg[] args=ivk0.getArgs();
			Assert.assertEquals(2, args.length);
			Assert.assertEquals("arg0", args[0].getKey());
			Assert.assertEquals(String.class, args[0].getType());
			Assert.assertEquals("arg1", args[1].getKey());
			Assert.assertEquals(Date.class, args[1].getType());
			Assert.assertEquals("result", ivk0.getResultKey());
			
			Assert.assertTrue( children.get(1) == config.getExecutable("m1_exe0") );
		}
		
		{
			Invoke exe=(Invoke)config.getExecutable("m2_exe1");
			
			Assert.assertEquals(Invoke.findMethodThrow(TestResolver.class, "test0", 0), exe.getMethod());
			Assert.assertEquals( rf.getResolver("tr2"), exe.getResolverProvider().getResolver());
			Arg[] args=exe.getArgs();
			Assert.assertNull(args);
		}
	}
	
	public static class TestConverter implements Converter
	{
		public Object convert(Object sourceObj, Type targetType)
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
