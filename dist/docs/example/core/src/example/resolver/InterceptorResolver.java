package example.resolver;

import org.soybeanMilk.core.ExecuteException;

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
	
	public void exception(ExecuteException e)
	{
		HelloResolver.printObject("exception handler: "+e.getMessage());
	}
}
