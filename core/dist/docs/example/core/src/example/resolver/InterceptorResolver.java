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
	
	public void before(Execution execution)
	{
		HelloResolver.printObject("before execute: "+execution.getExecutable().getName());
	}
	
	public void after(Execution execution)
	{
		HelloResolver.printObject("after  execute: "+execution.getExecutable().getName());
	}
	
	public void exception(Execution execution)
	{
		HelloResolver.printObject("exception handler: "+execution.getExecuteException());
	}
}
