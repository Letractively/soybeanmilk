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
		System.out.println(object);
		System.out.println();
	}
	
	public static void testJavaSyntax(char a, byte b, boolean c, int d, long e, float f, double g, String h, String _null)
	{
		System.out.println();
		System.out.println("char 	:"+a);
		System.out.println("byte 	:"+b);
		System.out.println("boolean :"+c);
		System.out.println("int 	:"+d);
		System.out.println("long 	:"+e);
		System.out.println("float 	:"+f);
		System.out.println("double 	:"+g);
		System.out.println("String 	:"+h);
		System.out.println("null 	:"+ _null);
		System.out.println();
	}
}
