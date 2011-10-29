package example;

import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;

public class ExampleMain
{
	public static void main(String[] args) throws Exception
	{
		Configuration cfg=new ConfigurationParser().parse(
				"example/"+Constants.DEFAULT_CONFIG_FILE);
		
		Executor executor=new DefaultExecutor(cfg);
		
		ConvertableObjectSource os = new HashMapObjectSource();
		
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
}
