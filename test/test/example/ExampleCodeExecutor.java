package test.example;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.FactoryResolverProvider;

import test.example.resolver.TestResolver;


public class ExampleCodeExecutor
{
	private static Log log=LogFactory.getLog(ExampleCodeExecutor.class);
	
	public static void main(String[] args)
	{
		Serializable resolverId="testResolver";
		
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.addResolver(resolverId, new TestResolver());
		
		Configuration cfg=new Configuration(rf);
		
		String actionName="testAction";
		
		Action action=new Action(actionName);
		
		Serializable result_0_key="dateResult";
		Serializable result_1_key="formatResult";
		
		Serializable 	formatDate_arg_0_key=result_0_key;
		Object 			formatDate_arg_1_value="yyyy-MM-dd HH:mm:ss";
		
		{
			Invoke invoke=new Invoke(null, "getDate", null, result_0_key, new FactoryResolverProvider(cfg.getResolverFactory(), resolverId));
			
			action.addExecutable(invoke);
		}
		
		{
			Invoke invoke=new Invoke(null, "formatDate",
					new Arg[]{new Arg(formatDate_arg_0_key), new Arg(formatDate_arg_1_value)}, result_1_key,
					new FactoryResolverProvider(cfg.getResolverFactory(), resolverId));
			
			action.addExecutable(invoke);
		}
		
		cfg.addExecutable(action);
		
		Executor executor=new DefaultExecutor(cfg);
		
		ConvertableObjectSource os = new HashMapObjectSource(new DefaultGenericConverter());
		
		try
		{
			executor.execute(actionName, os);
			
			String re_1=(String)os.get(result_1_key, null);
			
			log.info(re_1);
		}
		catch(ExecuteException e)
		{
			log.error("",e);
		}
	}
}
