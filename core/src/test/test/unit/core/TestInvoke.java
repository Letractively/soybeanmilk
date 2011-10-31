package test.unit.core;


import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
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
	public void testBreaker() throws Exception
	{
		//无breaker
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_0", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
		
		//breaker关键字的值为null
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
		
		//breaker关键字的值为false
		{
			ObjectSource objSource=new HashMapObjectSource();
			objSource.set("breakerKey", false);
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}

		//breaker关键字的值为true
		{
			ObjectSource objSource=new HashMapObjectSource();
			objSource.set("breakerKey", true);
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertNull(objSource.get(RESULT_KEY, null));
		}
		
		//breaker为"true"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_2", objSource);
			
			Assert.assertNull(objSource.get(RESULT_KEY, null));
		}

		//breaker为"false"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_3", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}

		//breaker为"TRUE"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_4", objSource);
			
			Assert.assertNull(objSource.get(RESULT_KEY, null));
		}

		//breaker为"FALSE"
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_5", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
		}
	}

	public static class TestResolver
	{
		public static final String RESULT="result";
		
		public String test()
		{
			return RESULT;
		}
	}
}
