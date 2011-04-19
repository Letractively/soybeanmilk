package example;

import java.io.Serializable;

import org.soybeanMilk.core.Executor;
import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.exe.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.exe.resolver.FactoryResolverProvider;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.core.os.HashMapObjectSource;

import example.resolver.HelloResolver;

public class ExampleCode
{
	public static void main(String[] args) throws Exception
	{
		Serializable resolverId="helloResolver";
		
		String actionName="helloAction";
		
		Serializable resultKey_hello="helloResult";
		Serializable argKey_hello_to="helloTo";
		Serializable argKey_hello_repeat="helloRepeat";
		
		//定义解决对象工厂
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.addResolver(resolverId, new HelloResolver());
		
		//创建配置
		Configuration cfg=new Configuration(rf);
		
		//创建动作
		Action action=new Action(actionName);
		
		{
			Invoke invoke=new Invoke(
					null,
					"hello",
					new Arg[]{new Arg(argKey_hello_to), new Arg(argKey_hello_repeat)},
					resultKey_hello,
					new FactoryResolverProvider(rf, resolverId));
			
			action.addExecutable(invoke);
		}
		{
			Invoke invoke=new Invoke(
					null,
					"printObject",
					new Arg[]{new Arg(resultKey_hello)},
					null,
					HelloResolver.class);
			
			action.addExecutable(invoke);
		}
		
		cfg.addExecutable(action);
		
		//创建执行器
		Executor executor=new DefaultExecutor(cfg);
		
		ConvertableObjectSource os = new HashMapObjectSource(new DefaultGenericConverter());
		
		//设置参数
		os.set(argKey_hello_to, "mars");
		os.set(argKey_hello_repeat, 3);
		
		//执行
		executor.execute(actionName, os);
	}
}
