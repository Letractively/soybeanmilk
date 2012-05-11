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

import org.junit.Test;
import org.soybeanMilk.core.config.parser.InvokeStatementParser;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestInvokeStatementParser
{
	@Test
	public void parse_noArgNoResult() throws Exception
	{
		String stmt=" testResolver . method_a( ); ";
		InvokeStatementParser isp=new InvokeStatementParser(stmt);
		isp.parse();
		
		Assert.assertNull(isp.getResultKey());
		Assert.assertEquals("testResolver", isp.getResolver());
		Assert.assertEquals("method_a", isp.getMethodName());
		Assert.assertNull(isp.getArgs());
		Assert.assertNull(isp.getArgTypes());
	}
	
	@Test
	public void parse_hasArghasResult() throws Exception
	{
		String stmt=" testResult =  testResolver . method_b  ( my.argKey, 10, agrEnd); ";
		InvokeStatementParser isp=new InvokeStatementParser(stmt);
		isp.parse();
		
		Assert.assertEquals("testResult", isp.getResultKey());
		Assert.assertEquals("testResolver", isp.getResolver());
		Assert.assertEquals("method_b", isp.getMethodName());
		Assert.assertEquals("my.argKey", isp.getArgs()[0]);
		Assert.assertEquals("10", isp.getArgs()[1]);
		Assert.assertEquals("agrEnd", isp.getArgs()[2]);
		
		Assert.assertEquals(isp.getArgs().length, isp.getArgTypes().length);
	}
	
	@Test
	public void parse_hasArgType() throws Exception
	{
		String stmt=" testResult =  testResolver . method_b  ( my.argKey(int), \"aaa\", 10(somepkg.some.someClass), 345(double), \"aaa\"(String), 'a'(char), endArg()); ";
		InvokeStatementParser isp=new InvokeStatementParser(stmt);
		isp.parse();
		
		Assert.assertEquals("testResult", isp.getResultKey());
		Assert.assertEquals("testResolver", isp.getResolver());
		Assert.assertEquals("method_b", isp.getMethodName());
		
		String[] args=isp.getArgs();
		String[] argTypes=isp.getArgTypes();
		
		Assert.assertEquals(args.length, argTypes.length);
		
		Assert.assertEquals("my.argKey", args[0]);
		Assert.assertEquals("int", argTypes[0]);
		
		Assert.assertEquals("\"aaa\"", args[1]);
		Assert.assertNull(argTypes[1]);
		
		Assert.assertEquals("10", args[2]);
		Assert.assertEquals("somepkg.some.someClass", argTypes[2]);
		
		Assert.assertEquals("345", args[3]);
		Assert.assertEquals("double", argTypes[3]);
		
		Assert.assertEquals("\"aaa\"", args[4]);
		Assert.assertEquals("String", argTypes[4]);
		
		Assert.assertEquals("'a'", args[5]);
		Assert.assertEquals("char", argTypes[5]);
		
		Assert.assertEquals("endArg", args[6]);
		Assert.assertEquals("", argTypes[6]);
	}
}
