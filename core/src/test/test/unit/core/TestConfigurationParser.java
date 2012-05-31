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


import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.Interceptor;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.exe.Invoke.Resolver;
import org.soybeanMilk.core.exe.support.DynamicResolver;
import org.soybeanMilk.core.exe.support.KeyArg;
import org.soybeanMilk.core.exe.support.ObjectResolver;
import org.soybeanMilk.core.exe.support.ResolverObjectFactory;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestConfigurationParser
{
	private Configuration config;
	
	@Before
	public void setUp() throws Exception{}
	
	@After
	public void tearDown() throws Exception{}
	
	@Test
	public void parse_genericConverter_noClassAttr() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-genericConverter-noClassAttr.xml");
		
		GenericConverter gc=config.getGenericConverter();
		
		Assert.assertEquals(DefaultGenericConverter.class, gc.getClass());
		Assert.assertEquals(TestConverter.class, gc.getConverter(String.class, int.class).getClass());
		Assert.assertEquals(TestConverter.class, gc.getConverter(String.class, float.class).getClass());
	}
	
	@Test
	public void parse_genericConverter_hasClassAttr() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-genericConverter-hasClassAttr.xml");
		
		GenericConverter gc=config.getGenericConverter();
		
		Assert.assertEquals(MyGenericConverter.class, gc.getClass());
	}
	
	@Test
	public void parse_interceptor() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Interceptor ii=config.getInterceptor();
		
		Assert.assertTrue(config.getExecutable("global_before") == ii.getBefore());
		Assert.assertTrue(config.getExecutable("global_after") == ii.getAfter());
		Assert.assertTrue(config.getExecutable("m1_exception") == ii.getException());
		Assert.assertEquals("executionKey", ii.getExecutionKey());
	}
	
	@Test
	public void parse_includes() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		ResolverObjectFactory rf=config.getResolverObjectFactory();
		Assert.assertNotNull(rf.getResolverObject("tr"));
		Assert.assertNotNull(rf.getResolverObject("tr1"));
		Assert.assertNotNull(rf.getResolverObject("tr2"));
		
		Assert.assertNotNull(config.getExecutable("global_exe0"));
		Assert.assertNotNull(config.getExecutable("m1_exe0"));
		Assert.assertNotNull(config.getExecutable("m2_exe0"));
	}
	
	@Test
	public void parse_resolvers() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		ResolverObjectFactory rf=config.getResolverObjectFactory();
		Assert.assertEquals(TestResolver.class, rf.getResolverObject("tr").getClass());
		Assert.assertEquals(TestResolver.class, rf.getResolverObject("tr1").getClass());
		Assert.assertEquals(TestResolver.class, rf.getResolverObject("tr2").getClass());
	}
	
	@Test
	public void parse_executables_emptyName() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Action exe0=(Action)config.getExecutable("global_");
		Assert.assertNotNull(exe0);
		
		Action exe1=(Action)config.getExecutable("m1_");
		Assert.assertNotNull(exe1);
		
		Action exe2=(Action)config.getExecutable("m2_");
		Assert.assertNotNull(exe2);
	}

	@Test
	public void parse_executables_multiExecutables() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Action exe0=(Action)config.getExecutable("another_exe1");
		Assert.assertNotNull(exe0);
		
		List<Executable> inners=exe0.getExecutables();
		
		Assert.assertEquals(2, inners.size());
		
		Assert.assertTrue( inners.get(0) == config.getExecutable("another_innerRef") );
		Assert.assertTrue( inners.get(1) == config.getExecutable("global_exe1") );
	}
	
	@Test
	public void parse_executables_action() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		{
			Action exe=(Action)config.getExecutable("global_exe0");
			Assert.assertNull(exe.getExecutables());
		}
		
		{
			Action exe=(Action)config.getExecutable("global_exe1");
			
			Assert.assertNotNull(exe.getExecutables());
			List<Executable> children=exe.getExecutables();
			Assert.assertEquals(4, children.size());
			Assert.assertEquals(Invoke.class, children.get(0).getClass());
			Assert.assertTrue( children.get(1) == config.getExecutable("global_exe0") );
			Assert.assertTrue( children.get(2) == config.getExecutable("global_exe0") );
			Assert.assertTrue( children.get(3) == config.getExecutable("m1_exe0") );
		}
	}

	@Test
	public void parse_executables_invoke_noArgNoResult() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Invoke exe=(Invoke)config.getExecutable("global_before");
		
		Resolver rp=exe.getResolver();
		Assert.assertEquals(DynamicResolver.class, rp.getClass());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getFactoryResolverProvider().getResolverId());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getObjectSourceResolverProvider().getResolverKey());
		Assert.assertEquals("test0", exe.getMethodName());
		Assert.assertNull(exe.getArgs());
	}
	
	@Test
	public void parse_executables_invoke_hasArgHasResult() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Invoke exe=(Invoke)config.getExecutable("global_exe3");
		
		Assert.assertEquals("result", exe.getResultKey());
		Resolver rp=exe.getResolver();
		Assert.assertEquals(DynamicResolver.class, rp.getClass());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getFactoryResolverProvider().getResolverId());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getObjectSourceResolverProvider().getResolverKey());
		Assert.assertEquals("test2", exe.getMethodName());
		Assert.assertEquals(2, exe.getArgs().length);
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[0].getClass());
		Assert.assertEquals("arg0", ((KeyArg)exe.getArgs()[0]).getKey());
		Assert.assertEquals(KeyArg.class, exe.getArgs()[1].getClass());
		Assert.assertEquals("arg1", ((KeyArg)exe.getArgs()[1]).getKey());
	}
	
	@Test
	public void parse_executables_invoke_staticMethod() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Invoke exe=(Invoke)config.getExecutable("m1_exe1");
		
		Assert.assertEquals("result", exe.getResultKey());
		Resolver rp=exe.getResolver();
		Assert.assertEquals(ObjectResolver.class, rp.getClass());
		Assert.assertNull(((ObjectResolver)rp).getResolverObject(null));
		Assert.assertEquals(TestResolver.class, ((ObjectResolver)rp).getResolverClass(null));
		Assert.assertEquals("staticTest", exe.getMethodName());
		Assert.assertEquals(2, exe.getArgs().length);
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[0].getClass());
		Assert.assertEquals("arg0", ((KeyArg)exe.getArgs()[0]).getKey());
		Assert.assertEquals(KeyArg.class, exe.getArgs()[1].getClass());
		Assert.assertEquals("arg1", ((KeyArg)exe.getArgs()[1]).getKey());
	}
	
	@Test
	public void parse_executables_invoke_xml() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Invoke exe=(Invoke)config.getExecutable("global_exe4");
		
		Assert.assertEquals("result", exe.getResultKey());
		Resolver rp=exe.getResolver();
		Assert.assertEquals(DynamicResolver.class, rp.getClass());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getFactoryResolverProvider().getResolverId());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getObjectSourceResolverProvider().getResolverKey());
		Assert.assertEquals("test2", exe.getMethodName());
		Assert.assertEquals(2, exe.getArgs().length);
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[0].getClass());
		Assert.assertEquals("arg0", ((KeyArg)exe.getArgs()[0]).getKey());
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[1].getClass());
		Assert.assertEquals("arg1", ((KeyArg)exe.getArgs()[1]).getKey());
	}
	
	@Test
	public void parse_executables_invoke_xml_setArgType() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Invoke exe=(Invoke)config.getExecutable("global_exe5");
		
		Assert.assertEquals("result", exe.getResultKey());
		Resolver rp=exe.getResolver();
		Assert.assertEquals(DynamicResolver.class, rp.getClass());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getFactoryResolverProvider().getResolverId());
		Assert.assertEquals("tr", ((DynamicResolver)rp).getObjectSourceResolverProvider().getResolverKey());
		Assert.assertEquals("test2", exe.getMethodName());
		Assert.assertEquals(4, exe.getArgs().length);
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[0].getClass());
		Assert.assertEquals("arg0", ((KeyArg)exe.getArgs()[0]).getKey());
		Assert.assertEquals(String.class, exe.getArgs()[0].getType());
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[1].getClass());
		Assert.assertEquals("arg1", ((KeyArg)exe.getArgs()[1]).getKey());
		Assert.assertEquals(Date.class, exe.getArgs()[1].getType());
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[2].getClass());
		Assert.assertEquals("arg2", ((KeyArg)exe.getArgs()[2]).getKey());
		Assert.assertEquals(int.class, exe.getArgs()[2].getType());
		
		Assert.assertEquals(KeyArg.class, exe.getArgs()[3].getClass());
		Assert.assertEquals("arg3", ((KeyArg)exe.getArgs()[3]).getKey());
		Assert.assertEquals(Integer.class, exe.getArgs()[3].getType());
	}
	
	@Test
	public void parse_executables_invoke_breaker() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		{
			Invoke exe=(Invoke)config.getExecutable("m2_exe1");
			Assert.assertEquals("breakerKey", exe.getBreaker());
		}
		
		{
			Invoke exe=(Invoke)config.getExecutable("m2_exe2");
			Assert.assertEquals(Boolean.TRUE, exe.getBreaker());
		}
		
		{
			Invoke exe=(Invoke)config.getExecutable("m2_exe3");
			Assert.assertEquals(Boolean.FALSE, exe.getBreaker());
		}
	}
	
	@Test
	public void parse_executables_invoke_valueArg_withType() throws Exception
	{
		config=new ConfigurationParser().parse("test/unit/core/TestConfigurationParser-main.xml");
		
		Invoke invoke=(Invoke)config.getExecutable("global_valueArg");
		Arg[] args=invoke.getArgs();
		
		int idx=-1;
		
		idx++;
		Assert.assertEquals(new Byte((byte)10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(byte.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Byte((byte)10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Byte.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Short((short)10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(short.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Short((short)10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Short.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Integer(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(int.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Integer(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Integer.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Long(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(long.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Long(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Long.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Float(10f), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(float.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Float(10f), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Float.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Double(10d), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(double.class, args[10].getType());
		
		idx++;
		Assert.assertEquals(new Double(10d), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Double.class, args[idx].getType());

		idx++;
		Assert.assertEquals('c', args[idx].getValue(null, null, null, null));
		Assert.assertEquals(char.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals('c', args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Character.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals("string", args[idx].getValue(null, null, null, null));
		Assert.assertEquals(String.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(Boolean.TRUE, args[idx].getValue(null, null, null, null));
		Assert.assertEquals(boolean.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(Boolean.FALSE, args[idx].getValue(null, null, null, null));
		Assert.assertEquals(boolean.class, args[idx].getType());

		idx++;
		Assert.assertEquals(Boolean.TRUE, args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Boolean.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(Boolean.FALSE, args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Boolean.class, args[idx].getType());
		
		idx++;
		Assert.assertNull(args[idx].getValue(null, null, null, null));
		Assert.assertEquals(String.class, args[idx].getType());
		
		idx++;
		Assert.assertNull(args[idx].getValue(null, null, null, null));
		Assert.assertNull(args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Integer(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Integer.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Double(10.0), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Double.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Long(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(long.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Long(10), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Long.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Float(10f), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(float.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Float(10f), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Float.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Double(10d), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(double.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(new Double(10d), args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Double.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals('c', args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Character.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals("string", args[idx].getValue(null, null, null, null));
		Assert.assertEquals(String.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(Boolean.TRUE, args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Boolean.class, args[idx].getType());
		
		idx++;
		Assert.assertEquals(Boolean.FALSE, args[idx].getValue(null, null, null, null));
		Assert.assertEquals(Boolean.class, args[idx].getType());
	}
	
	public static class MyGenericConverter implements GenericConverter
	{
		public <T> T convert(Object sourceObj, Type targetType)
				throws ConvertException {
			return null;
		}

		public void addConverter(Type sourceType, Type targetType,
				Converter converter) {
			
		}

		public Converter getConverter(Type sourceType, Type targetType) {
			return null;
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
