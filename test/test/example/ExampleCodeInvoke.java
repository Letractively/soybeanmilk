package test.example;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;
import org.soybeanMilk.core.resolver.ObjectResolverProvider;

import test.example.resolver.TestResolver;

public class ExampleCodeInvoke
{
	private static Log log=LogFactory.getLog(ExampleCodeInvoke.class);
	
	public static void main(String[] args)
	{
		Serializable arg_0_key="user";
		
		Invoke invoke=new Invoke(null, "hello",
				new Arg[]{new Arg(arg_0_key)}, null, new ObjectResolverProvider(new TestResolver()));
		
		ConvertableObjectSource os = new HashMapObjectSource(new DefaultGenericConverter());
		os.set(arg_0_key, "world");
		
		try
		{
			invoke.execute(os);
		}
		catch(ExecuteException e)
		{
			log.error("",e);
		}
	}
}
