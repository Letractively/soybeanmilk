package example.resolver;

import org.soybeanMilk.core.Execution;

public class InterceptorResolver
{
	public void invokeNormal()
	{
		HelloResolver.printObject("invoke normal");
	}
	
	public void invokeThrow()
	{
		throw new NullPointerException("invoke throw");
	}
	
	public void before()
	{
		HelloResolver.printObject("before handler");
	}
	
	public void after()
	{
		HelloResolver.printObject("after handler");
	}
	
	public void exception(Execution execution)
	{
		HelloResolver.printObject("exception handler: "+execution);
	}
}
