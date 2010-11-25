package example;

import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;

public class ExampleConfigFile
{
	public static void main(String[] args) throws Exception
	{
		Configuration cfg=new ConfigurationParser().parse(
				//"example/"+Constants.DEFAULT_CONFIG_FILE);
				"example/soybean-milk.config_1.xml");
		
		Executor executor=new DefaultExecutor(cfg);
		
		ConvertableObjectSource os = new HashMapObjectSource();
		
		os.set("helloTo", "mars");
		os.set("helloRepeat", 3);
		
		executor.execute("helloAction", os);
	}
}
