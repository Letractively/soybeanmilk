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


import java.util.HashMap;
import java.util.Map;

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
import org.soybeanMilk.core.exe.Invoke.Resolver;
import org.soybeanMilk.core.exe.support.DefaultResolverObjectFactory;
import org.soybeanMilk.core.exe.support.DynamicResolver;
import org.soybeanMilk.core.exe.support.FactoryResolver;
import org.soybeanMilk.core.exe.support.KeyArg;
import org.soybeanMilk.core.exe.support.ObjectResolver;
import org.soybeanMilk.core.exe.support.ObjectSourceResolver;
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
	public void init_byResolver() throws Exception
	{
		Arg[] args=new Arg[]{
				new ValueArg("aaa"),
				new ValueArg("bbb"),
		};
		Resolver rp=new ObjectResolver(new TestResolver());
		
		Invoke re=new Invoke("test", rp,  "test1", args, RESULT_KEY);
		
		Assert.assertEquals(re.getName(), "test");
		Assert.assertEquals(re.getResultKey(), RESULT_KEY);
		Assert.assertEquals(re.getResolver(), rp);
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
		
		Resolver rp=new ObjectResolver(TestResolver.class);
		
		Invoke re=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		Assert.assertEquals(re.getName(), "test");
		Assert.assertEquals(re.getResultKey(), RESULT_KEY);
		Assert.assertEquals(re.getResolver(), rp);
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
		Resolver rp=new ObjectResolver(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY));
	}
	
	@Test
	public void execute_dynamic_objectSourceResolver() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		Resolver rp=new ObjectSourceResolver("objectSourceResolver");
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("objectSourceResolver", new TestResolver());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY));
	}
	
	@Test
	public void execute_dynamic_factoryResolver() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		rof.addResolverObject("factoryResolver", new TestResolver());
		
		Resolver rp=new FactoryResolver(rof, "factoryResolver");
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY));
	}

	@Test
	public void execute_dynamic_dynamicResolver_objectSource() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		rof.addResolverObject("dynamicResolver", new TestResolver());
		
		Resolver rp=new DynamicResolver(null, new ObjectSourceResolver("dynamicResolver"));
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("dynamicResolver", new TestResolver());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY));
	}
	
	@Test
	public void execute_dynamic_dynamicResolver_factory() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		rof.addResolverObject("dynamicResolver", new TestResolver());
		
		Resolver rp=new DynamicResolver(new FactoryResolver(rof, "dynamicResolver"), null);
		
		Invoke invoke=new Invoke("test", rp, "test1", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", "arg0");
		os.set("arg1", "1111");
		
		invoke.execute(os);
		
		Assert.assertEquals(TestResolver.RESULT, os.get(RESULT_KEY));
	}
	
	@Test
	public void execute_dynamic_dynamicResolver_none() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		
		ResolverObjectFactory rof=new DefaultResolverObjectFactory();
		
		Resolver rp=new DynamicResolver(new FactoryResolver(rof, "dynamicResolver"), new ObjectSourceResolver("dynamicResolver"));
		
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
		Assert.assertTrue( (re.getMessage().startsWith("got null resolver class from")) );
	}
	
	@Test
	public void execute_breakerNoValue() throws Exception
	{
		//无breaker
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_0", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY));
		}
	}
	
	@Test
	public void execute_breakerValueIsNull() throws Exception
	{
		//breaker关键字的值为null
		{
			ObjectSource objSource=new HashMapObjectSource();
			executor.execute("testBreaker_1", objSource);
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY));
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
			
			Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY));
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
			
			Assert.assertNull(objSource.get(RESULT_KEY));
		}
	}
	
	@Test
	public void execute_breakerIsLiteralTrue() throws Exception
	{
		ObjectSource objSource=new HashMapObjectSource();
		executor.execute("testBreaker_2", objSource);
		
		Assert.assertNull(objSource.get(RESULT_KEY));
	}

	@Test
	public void execute_breakerIsLiteralFalse() throws Exception
	{
		ObjectSource objSource=new HashMapObjectSource();
		executor.execute("testBreaker_3", objSource);
		
		Assert.assertEquals(TestResolver.RESULT, objSource.get(RESULT_KEY));
	}
	
	@Test
	public void execute_similarMethod_keyArg_typeSet() throws Exception
	{
		{
			Arg[] args=new Arg[]{
					new KeyArg("arg", Double.class),
				};
			
			Resolver rp=new ObjectResolver(new TestResolver());
			
			Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
			
			ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
			os.set("arg", "33");
			
			invoke.execute(os);
			
			Assert.assertEquals("Double", os.get(RESULT_KEY));
		}
		
		{
			Arg[] args=new Arg[]{
					new KeyArg("arg", Integer.class),
				};
			
			Resolver rp=new ObjectResolver(new TestResolver());
			
			Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
			
			ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
			os.set("arg", "33");
			
			invoke.execute(os);
			
			Assert.assertEquals("Integer", os.get(RESULT_KEY));
		}
	}
	
	@Test
	public void execute_similarMethod_keyArg_typeNotSet() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg"),
			};
		
		Resolver rp=new ObjectResolver(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg", "33");
		
		invoke.execute(os);
		
		Assert.assertNotNull(os.get(RESULT_KEY));
	}
	
	@Test
	public void execute_similarMethod_valueArg_wrapperType() throws Exception
	{
		{
			Arg[] args=new Arg[]{
					new ValueArg(33D),
				};
			
			Resolver rp=new ObjectResolver(new TestResolver());
			
			Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
			
			ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
			
			invoke.execute(os);
			
			Assert.assertEquals("Double", os.get(RESULT_KEY));
		}
		
		{
			Arg[] args=new Arg[]{
					new ValueArg(33),
				};
			
			Resolver rp=new ObjectResolver(new TestResolver());
			
			Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
			
			ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
			os.set("arg", "33");
			
			invoke.execute(os);
			
			Assert.assertEquals("Integer", os.get(RESULT_KEY));
		}
	}
	
	@Test
	public void execute_similarMethod_valueArg_primitiveType() throws Exception
	{
		Arg[] args=new Arg[]{
				new ValueArg(33, int.class),
			};
		
		Resolver rp=new ObjectResolver(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg", "33");
		
		invoke.execute(os);
		
		Assert.assertEquals("int", os.get(RESULT_KEY));
	}
	
	@Test
	public void execute_similarMethod_valueArgNull() throws Exception
	{
		Arg[] args=new Arg[]{
				new ValueArg(null, Integer.class),
			};
		
		Resolver rp=new ObjectResolver(new TestResolver());
		
		Invoke invoke=new Invoke("test", rp, "sameMethod", args, RESULT_KEY);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		
		invoke.execute(os);
		
		Assert.assertNotNull(os.get(RESULT_KEY));
	}

	@Test
	public void execute_genericMethod_resolveBase() throws Exception
	{
		Arg[] args=new Arg[1];
		args[0]=new KeyArg("arg0");
		Invoke invoke=new Invoke(null, new ObjectResolver(new SubSubSubGenericResolverImpl()), "resolveBase", args, "result");
		
		Map<String, Object> src=new HashMap<String, Object>();
		src.put("id", 1);
		src.put("name", "generic");
		src.put("age", 5);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", src);
		
		invoke.execute(os);
		
		JavaBeanSub re=os.get("result");
		
		Assert.assertEquals(1, re.getId().intValue());
		Assert.assertEquals("generic", re.getName());
		Assert.assertEquals(5, re.getAge().intValue());
	}
	
	@Test
	public void execute_genericMethod_resolveSub() throws Exception
	{
		Arg[] args=new Arg[1];
		args[0]=new KeyArg("arg0");
		Invoke invoke=new Invoke(null, new ObjectResolver(new SubSubSubGenericResolverImpl()), "resolveSub", args, "result");
		
		Map<String, Object> src=new HashMap<String, Object>();
		src.put("id", 1);
		src.put("name", "generic");
		src.put("age", 5);
		
		ObjectSource os=new HashMapObjectSource(new DefaultGenericConverter());
		os.set("arg0", src);
		
		invoke.execute(os);
		
		JavaBeanSub re=os.get("result");
		
		Assert.assertEquals(1, re.getId().intValue());
		Assert.assertEquals("generic", re.getName());
		Assert.assertEquals(5, re.getAge().intValue());
	}
	
	@Test
	public void execute_exception_ArgPrepareExecuteException() throws Exception
	{
		Arg[] args=new Arg[]{
				new KeyArg("arg0"),
				new KeyArg("arg1"),
		};
		Resolver rp=new ObjectResolver(new TestResolver());
		
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
		Resolver rp=new ObjectResolver(new TestResolver());
		
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
		
		public String sameMethod(Integer arg)
		{
			return "Integer";
		}
		
		public String sameMethod(int arg)
		{
			return "int";
		}
		
		public String sameMethod(Double arg)
		{
			return "Double";
		}
		
		public String sameMethod(Object o)
		{
			return "object";
		}
	}
	
	public static interface BaseGenericResolver<T extends Object>
	{
		T resolveBase(T t);
	}
	
	public static interface SubGenericResolver<T extends Object> extends BaseGenericResolver<T>
	{
		T resolveSub(T t);
	}
	
	public static class SubGenericResolverImpl<T extends Object> implements SubGenericResolver<T>
	{
		public T resolveBase(T t)
		{
			return t;
		}
		
		public T resolveSub(T t)
		{
			return t;
		}
	}
	
	public static interface SubSubGenericResolver<T extends JavaBean> extends SubGenericResolver<T>{}
	
	public static interface SubSubSubGenericResolver extends SubSubGenericResolver<JavaBeanSub>{}
	
	public static class SubSubSubGenericResolverImpl extends SubGenericResolverImpl<JavaBeanSub> implements SubSubSubGenericResolver{}
	
	public static class JavaBean
	{
		private Integer id;
		private String name;

		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class JavaBeanSub extends JavaBean
	{
		private Integer age;

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
	}
}
