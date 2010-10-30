package test.example.resolver;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestResolver
{
	private static Log log=LogFactory.getLog(TestResolver.class);
	
	private int base=10;
	
	public void hello(String user)
	{
		String re="hello, "+user+" !";
		
		log.info(re);
	}
	
	public int multiply(int mul)
	{
		int re=base*mul;
		
		log.info(re);
		
		return re;
	}
	
	public Date getDate()
	{
		return new Date();
	}
	
	public static String formatDate(Date date, String format)
	{
		String re=new SimpleDateFormat(format).format(date);
		
		log.info(re);
		
		return re;
	}
}
