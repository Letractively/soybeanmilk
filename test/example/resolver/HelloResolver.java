package example.resolver;

public class HelloResolver
{
	private String me;
	
	public String getMe() {
		return me;
	}
	
	public void setMe(String me) {
		this.me = me;
	}
	
	public static String hello(String from, String to)
	{
		return "hello "+to+", I am "+from;
	}
}
