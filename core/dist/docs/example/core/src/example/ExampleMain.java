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

package example;

import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.os.HashMapObjectSource;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class ExampleMain
{
	public static void main(String[] args) throws Exception
	{
		Configuration cfg=new ConfigurationParser().parse(
				"example/"+Constants.DEFAULT_CONFIG_FILE);
		
		Executor executor=new DefaultExecutor(cfg);
		
		ObjectSource os = new HashMapObjectSource();
		
		os.set("helloTo", "friend");
		os.set("helloRepeat", 3);
		
		os.set("genericStringInput", "11");
		os.set("genericStringArrayInput", new String[]{"11", "22", "33"});
		
		printDiv();
		executor.execute("helloXml", os);
		
		printDiv();
		executor.execute("helloStatement", os);
		
		printDiv();
		executor.execute("javaSyntax", os);
		
		printDiv();
		os.set("dynamicResolver", new DynamicResolver());
		executor.execute("dynamicResolver", os);

		printDiv();
		executor.execute("reference", os);
		
		printDiv();
		executor.execute("interceptorBeforeAfter", os);
		
		printDiv();
		executor.execute("interceptorException", os);
		
		printDiv();
		executor.execute("genericSimple", os);
		
		printDiv();
		executor.execute("genericList", os);
		
		printDiv();
		executor.execute("genericSet", os);
		
		printDiv();
		executor.execute("genericArray", os);
		
		System.in.read();
	}
	
	static void printDiv()
	{
		System.out.println("\n------------------------------------------------\n");
	}
	
	public static class DynamicResolver
	{
		public void dynamicMethod()
		{
			System.out.println();
			System.out.println("I am a dynamic method of object set into the ObjectSource");
			System.out.println();
		}
	}
}
