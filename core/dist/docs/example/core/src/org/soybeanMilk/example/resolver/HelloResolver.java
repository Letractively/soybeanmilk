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

package org.soybeanMilk.example.resolver;

/**
 * SoybeanMilk示例
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class HelloResolver
{
	private String me;
	
	public HelloResolver()
	{
		this.me="soybeanMilk";
	}
	
	public String hello(String to, int repeat)
	{
		String re="";
		
		for(int i=0;i<repeat;i++)
		{
			re+="Hello "+to+", I am "+this.me+"! ";
		}
		
		return re;
	}
	
	public static void printObject(Object obj)
	{
		System.out.println();
		System.out.println(obj);
		System.out.println();
	}
	
	public static void literals(byte b, short s, int d, long e, float f, double g,
			char a, boolean c, String h, Object _null)
	{
		System.out.println();
		System.out.println("byte 	:"+b);
		System.out.println("short 	:"+s);
		System.out.println("int 	:"+d);
		System.out.println("long 	:"+e);
		System.out.println("float 	:"+f);
		System.out.println("double 	:"+g);
		System.out.println("char 	:"+a);
		System.out.println("boolean :"+c);
		System.out.println("String 	:"+h);
		System.out.println("null 	:"+ _null);
		System.out.println();
	}
}
