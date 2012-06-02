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

package org.soybeanMilk.test.unit.core;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.Execution;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.InvocationExecuteException;
import org.soybeanMilk.core.os.HashMapObjectSource;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
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
			Configuration cfg=new ConfigurationParser().parse("org/soybeanMilk/test/unit/core/TestDefaultExecutor.cfg.xml");
			executor=new DefaultExecutor(cfg);
		}
		catch(Exception e)
		{
			log.error("",e);
		}
	}
	
	@Test
	public void execute_inexistentExecutableName() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		ExecutableNotFoundException re=null;
		
		try
		{
			executor.execute("inexistentExe", os);
		}
		catch(ExecutableNotFoundException e)
		{
			re=e;
		}
		
		Assert.assertEquals("inexistentExe", re.getExecutableName());
	}
	
	@Test
	public void execute_setGenericConverterToObjectSource_autoIfConvertableObjectSource() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		
		Assert.assertTrue((executor.getConfiguration().getGenericConverter() == os.getGenericConverter()));
	}
	
	@Test
	public void execute_executionIsCreated() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		executor.execute(KEY_EXE_HELLO, os);
		
		Execution re=os.get(KEY_EXECUTION);
		
		Assert.assertEquals(os, re.getObjectSource());
		Assert.assertEquals(KEY_EXE_HELLO, re.getExecutable().getName());
		Assert.assertNull(re.getExecuteException());
	}
	
	@Test
	public void execute_executionIsCreatedEachExecute() throws Exception
	{
		Execution re0=null;
		Execution re1=null;
		
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		re0=os.get(KEY_EXECUTION);
		
		executor.execute(KEY_EXE_HELLO, os);
		re1=os.get(KEY_EXECUTION);
		
		Assert.assertTrue( re0!=null && re1!=null && re0!=re1 );
	}
	
	@Test
	public void execute_executionNotCreatedIfExecutionKeyIsNull() throws Exception
	{
		executor.getConfiguration().getInterceptor().setExecutionKey(null);
		
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		
		Assert.assertNull(os.get(KEY_EXECUTION));
	}
	
	@Test
	public void execute_beforeInterceptorNotNull() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		
		Assert.assertEquals(ResolverForTest.beforeResultVal, os.get(ResolverForTest.beforeResultKey));
	}
	
	@Test
	public void execute_beforeInterceptorIsNull() throws Exception
	{
		executor.getConfiguration().getInterceptor().setBefore(null);
		
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		
		Assert.assertNull(os.get(ResolverForTest.beforeResultKey));
	}
	
	@Test
	public void execute_afterInterceptorNotNull() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		
		Assert.assertEquals(ResolverForTest.afterResultVal, os.get(ResolverForTest.afterResultKey));
	}
	
	@Test
	public void execute_afterInterceptorIsNull() throws Exception
	{
		executor.getConfiguration().getInterceptor().setAfter(null);
		
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute(KEY_EXE_HELLO, os);
		
		Assert.assertNull(os.get(ResolverForTest.afterResultKey));
	}
	
	@Test
	public void execute_exceptionInterceptorNotNull() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute("helloThrow", os);
		
		Assert.assertEquals(ResolverForTest.exceptionResultVal, os.get(ResolverForTest.exceptionResultKey));
	}
	
	@Test
	public void execute_exceptionInterceptorIsNull() throws Exception
	{
		executor.getConfiguration().getInterceptor().setException(null);
		
		HashMapObjectSource os=new HashMapObjectSource();
		
		try
		{
			executor.execute("helloThrow", os);
		}
		catch(InvocationExecuteException e){}
		
		Assert.assertNull(os.get(ResolverForTest.exceptionResultKey));
	}
	
	@Test
	public void execute_exceptionIsThrownIfNoExceptionInterceptor() throws Exception
	{
		executor.getConfiguration().getInterceptor().setException(null);
		
		HashMapObjectSource os=new HashMapObjectSource();
		
		InvocationExecuteException re=null;
		try
		{
			executor.execute("helloThrow", os);
		}
		catch(InvocationExecuteException e)
		{
			re=e;
		}
		
		Assert.assertTrue( (re.getCause() instanceof UnsupportedOperationException) );
	}
	
	@Test
	public void execute_exceptionIsCaughtIfExceptionInterceptorNotNull() throws Exception
	{
		HashMapObjectSource os=new HashMapObjectSource();
		
		executor.execute("helloThrow", os);
		
		Execution re=os.get(KEY_EXECUTION);
		
		Assert.assertTrue( (re.getExecuteException().getCause() instanceof UnsupportedOperationException) );
	}
	
	public static class ResolverForTest
	{
		public static final String beforeResultKey="before";
		public static final String beforeResultVal="beforeRe";
		
		public static final String afterResultKey="after";
		public static final String afterResultVal="afterRe";
		
		public static final String exceptionResultKey="exception";
		public static final String exceptionResultVal="exceptionRe";
		
		public String before()
		{
			return beforeResultVal;
		}
		
		public String after()
		{
			return afterResultVal;
		}
		
		public String exception()
		{
			return exceptionResultVal;
		}
		
		public void hello(){}
		
		public void helloThrow()
		{
			throw new UnsupportedOperationException();
		}
	}
}
