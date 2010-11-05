package test.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;

public class ExampleConfigFile
{
	private static Log log=LogFactory.getLog(ExampleConfigFile.class);
	
	public static void main(String[] args)
	{
		Configuration cfg=new ConfigurationParser().parse("test/example/"+Constants.DEFAULT_CONFIG_FILE);
		
		Executor executor=new DefaultExecutor(cfg);
		
		{
			ConvertableObjectSource os = new HashMapObjectSource();
			os.set("user", "world");
			try
			{
				executor.execute("hello", os);
				
				String re=(String)os.get("helloResult", null);
				
				log.info(re);
			}
			catch(ExecuteException e)
			{
				log.error("",e);
			}
		}
		
		log.info("----------------------------------------------");
		
		{
			ConvertableObjectSource os = new HashMapObjectSource();
			os.set("mul", 5);
			try
			{
				executor.execute("multiply", os);
				
				Integer re=(Integer)os.get("multiplyResult", null);
				
				log.info(re);
			}
			catch(ExecuteException e)
			{
				log.error("",e);
			}
		}
		
		log.info("----------------------------------------------");
		
		{
			ConvertableObjectSource os = new HashMapObjectSource();
			try
			{
				executor.execute("testAction", os);
				
				String re=(String)os.get("formatResult", null);
				
				log.info(re);
			}
			catch(ExecuteException e)
			{
				log.error("",e);
			}
		}
		
		log.info("----------------------------------------------");
		
		{
			ConvertableObjectSource os = new HashMapObjectSource();
			try
			{
				executor.execute("m0.testAction", os);
				
				String re=(String)os.get("formatResult", null);
				
				log.info(re);
			}
			catch(ExecuteException e)
			{
				log.error("",e);
			}
		}
		
		log.info("----------------------------------------------");
		
		{
			ConvertableObjectSource os = new HashMapObjectSource();
			try
			{
				executor.execute("m1.testAction", os);
				
				String re=(String)os.get("formatResult", null);
				
				log.info(re);
			}
			catch(ExecuteException e)
			{
				log.error("",e);
			}
		}
	}
}
