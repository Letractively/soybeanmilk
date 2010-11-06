package example;

import java.io.Serializable;

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

import example.resolver.HelloResolver;

public class ExampleCode
{
	public static void main(String[] args) throws Exception
	{
		Serializable resolverId="helloResolver";
		
		String actionName="helloAction";
		
		Serializable resultKey_getMe="resultMe";
		Serializable resultKey_hello="helloResult";
		Serializable argKey_hello_to="helloTo";
		
		//定义解决对象工厂
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.addResolver(resolverId, new HelloResolver());
		
		//创建配置
		Configuration cfg=new Configuration(rf);
		
		//创建动作
		Action action=new Action(actionName);
		//为动作添加调用"setMe"，并设置调用方法的参数值为"earth"
		{
			Invoke invoke=new Invoke(
					null,
					"setMe",
					new Arg[]{ new Arg((Object)"earth") },
					null,
					new FactoryResolverProvider(cfg.getResolverFactory(), resolverId));
			action.addExecutable(invoke);
		}
		//为动作添加调用"getMe"，并设置调用方法的返回结果以resultKey_getMe关键字保存到对象源中
		{
			Invoke invoke=new Invoke(
					null,
					"getMe",
					null,
					resultKey_getMe,
					new FactoryResolverProvider(cfg.getResolverFactory(), resolverId));
			
			action.addExecutable(invoke);
		}
		//为动作添加调用"hello"，并设置调用方法的第一个参数值以resultKey_getMe关键字从对象源中取得、
		//第二个参数值以argKey_hello_to关键字从对象源中取得，设置方法返回结果以resultKey_hello关键字保存到对象源
		{
			Invoke invoke=new Invoke(
					null,
					"hello",
					new Arg[]{new Arg(resultKey_getMe), new Arg(argKey_hello_to)},
					resultKey_hello,
					HelloResolver.class);
			
			action.addExecutable(invoke);
		}
		
		cfg.addExecutable(action);
		
		
		//创建执行器
		Executor executor=new DefaultExecutor(cfg);
		
		ConvertableObjectSource os = new HashMapObjectSource(new DefaultGenericConverter());
		
		//准备动作的调用"hello"的第二个参数值
		os.set(argKey_hello_to, "mars");
		
		//执行
		executor.execute(actionName, os);
		
		//取得执行结果
		Object helloResult=os.get(resultKey_hello, null);
		
		System.out.println("helloAction :"+helloResult);
	}
}
