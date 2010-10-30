package test.example;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.FactoryResolverProvider;

import test.example.resolver.TestResolver;

public class ExampleCodeAction
{
	private static Log log=LogFactory.getLog(ExampleCodeAction.class);
	
	public static void main(String[] args)
	{
		Serializable resolverId="testResolver";
		
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.addResolver(resolverId, new TestResolver());
		
		Action action=new Action();
		
		Serializable result_0_key="multiplyResult";
		Serializable result_1_key="formatResult";
		
		Serializable 	multiply_arg_0_key="mul";
		Serializable 	formatDate_arg_0_key="date";
		Object 			formatDate_arg_1_value="yyyy-MM-dd HH:mm:ss";
		
		{
			Invoke invoke=new Invoke(null, "multiply",
					new Arg[]{new Arg(multiply_arg_0_key)}, result_0_key, new FactoryResolverProvider(rf, resolverId));
			
			action.addExecutable(invoke);
		}
		
		{
			Invoke invoke=new Invoke(null, "formatDate",
					new Arg[]{new Arg(formatDate_arg_0_key), new Arg(formatDate_arg_1_value)}, result_1_key,
					new FactoryResolverProvider(rf, resolverId));
			
			action.addExecutable(invoke);
		}
		
		ConvertableObjectSource os = new HashMapObjectSource(new DefaultGenericConverter());
		
		os.set(multiply_arg_0_key, 5);
		os.set(formatDate_arg_0_key, new Date());
		
		try
		{
			action.execute(os);
			
			Integer re_0=(Integer)os.get(result_0_key, null);
			String re_1=(String)os.get(result_1_key, null);
			
			log.info(re_0);
			log.info(re_1);
		}
		catch(ExecuteException e)
		{
			log.error("",e);
		}
	}
}
