package example.resolver;

public class HelloResolver
{
	private String me;
	
	public HelloResolver()
	{
		this.me="soybeanMilk";
	}
	
	public String hello(String to, int repeat)
	{
		String re="";
		
		for(int i=0;i<repeat;i++)
		{
			re+="Hello "+to+", I am "+this.me+"! ";
		}
		
		return re;
	}
	
	public static void printObject(Object object)
	{
		System.out.println();
		System.out.println("print :"+object);
		System.out.println();
	}
}
