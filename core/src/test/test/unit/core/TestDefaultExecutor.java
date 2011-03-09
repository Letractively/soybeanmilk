package test.unit.core;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.Execution;
import org.soybeanMilk.core.InvocationExecuteException;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.os.HashMapObjectSource;


public class TestDefaultExecutor
{
	private static Log log=LogFactory.getLog(TestDefaultExecutor.class);
	
	private DefaultExecutor executor=null;
	
	private static String KEY_EXECUTION="execution";
	private static String KEY_EXE_HELLO="hello";
	
	@Before
	public void setUp()
	{
		try
		{
			Configuration cfg=new ConfigurationParser().parse("test/unit/core/TestDefaultExecutor.cfg.xml");
			executor=new DefaultExecutor(cfg);
		}
		catch(Exception e)
		{
			log.error("",e);
		}
	}
	
	/**
	 * 测试拦截器
	 */
	@Test
	public void interceptorExecution()
	{
		{
			HashMapObjectSource os=new HashMapObjectSource();
			
			try
			{
				executor.execute(KEY_EXE_HELLO, os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			
			Assert.assertNotNull(os.get(KEY_EXECUTION, null));
		}
		
		{
			executor.getConfiguration().getInterceptorInfo().setExecutionKey(null);
			
			HashMapObjectSource os=new HashMapObjectSource();
			
			try
			{
				executor.execute(KEY_EXE_HELLO, os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			
			Assert.assertNull(os.get(KEY_EXECUTION, null));
		}
	}
	
	@Test
	public void interceptorBefore()
	{
		{
			HashMapObjectSource os=new HashMapObjectSource();
			
			try
			{
				executor.execute(KEY_EXE_HELLO, os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			Execution execution=(Execution)os.get(KEY_EXECUTION, null);
			Assert.assertEquals(execution.toString(), os.get("before", null));
		}
		
		{
			executor.getConfiguration().getInterceptorInfo().setBeforeHandler(null);
			
			HashMapObjectSource os=new HashMapObjectSource();
			try
			{
				executor.execute(KEY_EXE_HELLO, os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			Assert.assertNull(os.get("before", null));
		}
	}
	
	@Test
	public void interceptorAfter()
	{
		{
			HashMapObjectSource os=new HashMapObjectSource();
			
			try
			{
				executor.execute(KEY_EXE_HELLO, os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			Execution execution=(Execution)os.get(KEY_EXECUTION, null);
			Assert.assertEquals(execution.toString(), os.get("after", null));
		}
		
		{
			executor.getConfiguration().getInterceptorInfo().setAfterHandler(null);
			
			HashMapObjectSource os=new HashMapObjectSource();
			try
			{
				executor.execute(KEY_EXE_HELLO, os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			Assert.assertNull(os.get("after", null));
		}
	}
	
	@Test
	public void interceptorException()
	{
		{
			HashMapObjectSource os=new HashMapObjectSource();
			
			try
			{
				executor.execute("helloThrow", os);
			}
			catch(Exception e)
			{
				log.error("",e);
			}
			Execution execution=(Execution)os.get(KEY_EXECUTION, null);
			Assert.assertEquals(execution.toString(), os.get("exception", null));
		}
		
		{
			executor.getConfiguration().getInterceptorInfo().setExceptionHandler(null);
			
			HashMapObjectSource os=new HashMapObjectSource();
			try
			{
				executor.execute("helloThrow", os);
			}
			catch(Exception e){}
			
			Assert.assertNull(os.get("exception", null));
		}
	}
	
	@Test
	public void testInterceptorRuntimeException() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		try
		{
			executor.execute("helloThrowRuntime", os);
		}
		catch(ExecuteException e)
		{
			log.error("",e);
		}
		ExecuteException execution=(ExecuteException)((Execution)os.get(KEY_EXECUTION, null)).getExecuteException();
		Assert.assertTrue( execution instanceof InvocationExecuteException );
	}
	
	@Test
	public void execute() throws Exception
	{
		//参数化类型，默认不支持
		{
			HashMapObjectSource os=new HashMapObjectSource();
			os.set("list", new ArrayList<String>());
			
			Exception e=null;
			try
			{
				executor.execute("helloParameterized", os);
			}
			catch(Exception e1)
			{
				e=e1;
			}
			
			Assert.assertTrue( e.getMessage().startsWith("can not find Converter for converting '") );
		}
	}
	
	public static class ResolverForTest
	{
		public String before(Execution execution)
		{
			return execution==null ? null : execution.toString();
		}
		
		public String after(Execution execution)
		{
			return execution==null ? null : execution.toString();
		}
		
		public String exception(Execution execution)
		{
			return execution==null ? null : execution.toString();
		}
		
		public void hello(){}
		
		public void helloThrow()
		{
			throw new UnsupportedOperationException();
		}
		
		public void helloThrowRuntime()
		{
			throw new NullPointerException("helloThrowRuntime");
		}
		
		public void helloParameterized(List<String> list){}
	}
}
