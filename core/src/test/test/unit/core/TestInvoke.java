package test.unit.core;


import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.ArgPrepareExecuteException;
import org.soybeanMilk.core.exe.InvocationExecuteException;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.exe.Invoke.ResolverProvider;
import org.soybeanMilk.core.exe.support.KeyArg;
import org.soybeanMilk.core.exe.support.ObjectResolverProvider;
import org.soybeanMilk.core.exe.support.ValueArg;
import org.soybeanMilk.core.os.HashMapObjectSource;

public class TestInvoke
{
	private static Log log=LogFactory.getLog(TestDefaultExecutor.class);
	
	private DefaultExecutor executor=null;
	
	private static final String RESULT_KEY="testResult";
	
	@Before
	public void setUp() throws Exception
	{
		try
		{
			Configuration cfg=new ConfigurationParser().parse("test/unit/core/TestInvoke.cfg.xml");
			executor=new DefaultExecutor(cfg);
		}
		catch(Exception e)
		{
			log.error("",e);
		}
	}

	@After
	public void tearDown() throws Exception
	{
		executor=null;
	}
	
	@Test
	public void init_byResolverProvider() throws Exception
	{
		Arg[] args=new Arg[]{
				new ValueArg("aaa"),
				new ValueArg("bbb"),
		};
		ResolverProvider rp=new ObjectResolverProvider(new TestResolver());
		
		Invoke re=new Invoke("test", rp,  "test1", args, RESULT_KEY);
		
		Assert.assertEquals(re.getName(), "test");
		Assert.assertEquals(re.getResultKey(), RESULT_KEY);
		Assert.assertEquals(re.getResolverProvider(), rp);
		Assert.assertTrue( (re.getArgs()[0]==args[0]) );
		Assert.assertTrue( (re.getArgs()[1]==args[1]) );
	}
	
	@Test
	public void init_byResolverClass() throws Exception
	{
		Arg[] args=new Arg[]{
				new ValueArg("aaa"),
				new ValueArg("bbb"),
		};
		
		ResolverProvider rp=new ObjectResolverProvider(TestResolver.class);
		
		Invoke re=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		Assert.assertEquals(re.getName(), "test");
		Assert.assertEquals(re.getResultKey(), RESULT_KEY);
		Assert.assertEquals(re.getResolverProvider(), rp);
		Assert.assertTrue( (re.getArgs()[0]==args[0]) );
		Assert.assertTrue( (re.getArgs()[1]==args[1]) );
	}
	
	@Test
	public void execute() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		ResolverProvider rp=new ObjectResolverProvider(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY, null));
	}
	
	@Test
	public void execute_breakerNoValue() throws Exception
	{
		//无breaker
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_0", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
	}
	
	@Test
	public void execute_breakerValueIsNull() throws Exception
	{
		//breaker关键字的值为null
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
	}
	
	@Test
	public void execute_breakerValueIsFalse() throws Exception
	{
		//breaker关键字的值为false
		{
			ObjectSource objSource=new HashMapObjectSource();
			objSource.set("breakerKey", false);
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
	}
	
	@Test
	public void execute_breakerValueIsTrue() throws Exception
	{
		//breaker关键字的值为true
		{
			ObjectSource objSource=new HashMapObjectSource();
			objSource.set("breakerKey", true);
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertNull(objSource.get(RESULT_KEY, null));
		}
	}
	
	@Test
	public void execute_breakerIsLiteralTrue() throws Exception
	{
		//breaker为"true"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_2", objSource);
			
			Assert.assertNull(objSource.get(RESULT_KEY, null));
		}
		
		//breaker为"TRUE"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_4", objSource);
			
			Assert.assertNull(objSource.get(RESULT_KEY, null));
		}
	}

	@Test
	public void execute_breakerIsLiteralFalse() throws Exception
	{
		//breaker为"false"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_3", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
		
		//breaker为"FALSE"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_5", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
	}
	
	@Test
	public void execute_exception_ArgPrepareExecuteException() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		ResolverProvider rp=new ObjectResolverProvider(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "arg1");
		
		ArgPrepareExecuteException re=null;
		try
		{
			invoke.execute(os);
		}
		catch(ArgPrepareExecuteException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getInvoke() == invoke) );
		Assert.assertTrue( (re.getArg() == args[1]) );
		Assert.assertTrue( (re.getCause() instanceof ObjectSourceException) );
	}
	
	@Test
	public void execute_exception_InvocationExecuteException() throws Exception
	{
		ResolverProvider rp=new ObjectResolverProvider(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "testThrow", null, RESULT_KEY);
		
		InvocationExecuteException re=null;
		try
		{
			invoke.execute(new HashMapObjectSource());
		}
		catch(InvocationExecuteException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getCause() instanceof NullPointerException) );
	}
	
	public static class TestResolver
	{
		public static final String RESULT="result";
		
		public String test()
		{
			return RESULT;
		}
		
		public String test1(String arg0, Integer arg1)
		{
			return RESULT;
		}
		
		public String testThrow()
		{
			throw new NullPointerException();
		}
	}
}
