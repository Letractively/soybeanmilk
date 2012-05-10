/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package test.unit.core;


import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.ExecuteException;
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
import org.soybeanMilk.core.exe.support.DefaultResolverObjectFactory;
import org.soybeanMilk.core.exe.support.DynamicResolverProvider;
import org.soybeanMilk.core.exe.support.FactoryResolverProvider;
import org.soybeanMilk.core.exe.support.KeyArg;
import org.soybeanMilk.core.exe.support.ObjectResolverProvider;
import org.soybeanMilk.core.exe.support.ObjectSourceResolverProvider;
import org.soybeanMilk.core.exe.support.ResolverObjectFactory;
import org.soybeanMilk.core.exe.support.ValueArg;
import org.soybeanMilk.core.os.HashMapObjectSource;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
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
	public void execute_dynamic_objectSourceResolverProvider() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverProvider rp=new ObjectSourceResolverProvider("objectSourceResolver");
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("objectSourceResolver", new TestResolver());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY, null));
	}
	
	@Test
	public void execute_dynamic_factoryResolverProvider() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		rof.addResolverObject("factoryResolver", new TestResolver());
		
		ResolverProvider rp=new FactoryResolverProvider(rof, "factoryResolver");
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY, null));
	}

	@Test
	public void execute_dynamic_dynamicResolverProvider_objectSource() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		rof.addResolverObject("dynamicResolver", new TestResolver());
		
		ResolverProvider rp=new DynamicResolverProvider(null, new ObjectSourceResolverProvider("dynamicResolver"));
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("dynamicResolver", new TestResolver());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY, null));
	}
	
	@Test
	public void execute_dynamic_dynamicResolverProvider_factory() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		rof.addResolverObject("dynamicResolver", new TestResolver());
		
		ResolverProvider rp=new DynamicResolverProvider(new FactoryResolverProvider(rof, "dynamicResolver"), null);
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY, null));
	}
	
	@Test
	public void execute_dynamic_dynamicResolverProvider_none() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		
		ResolverProvider rp=new DynamicResolverProvider(new FactoryResolverProvider(rof, "dynamicResolver"), new ObjectSourceResolverProvider("dynamicResolver"));
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		ExecuteException re=null;
		
		try
		{
			invoke.execute(os);
		}
		catch(ExecuteException e)
		{
			re=e;
		}
		
		Assert.assertNotNull(re);
		Assert.assertTrue( (re.getMessage().startsWith("got null resolver from ResolverProvider")) );
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
		ObjectSource objSource=new HashMapObjectSource();
		executor.execute("testBreaker_2", objSource);
		
		Assert.assertNull(objSource.get(RESULT_KEY, null));
	}

	@Test
	public void execute_breakerIsLiteralFalse() throws Exception
	{
		ObjectSource objSource=new HashMapObjectSource();
		executor.execute("testBreaker_3", objSource);
		
		Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY, null));
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
