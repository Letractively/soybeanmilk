package test.unit.web;


import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.soybeanMilk.web.exe.th.AbstractTargetHandler;
import org.soybeanMilk.web.exe.th.DefaultTypeTargetHandler;
import org.soybeanMilk.web.exe.th.ForwardTargetHandler;
import org.soybeanMilk.web.exe.th.RedirectTargetHandler;
import org.soybeanMilk.web.exe.th.TargetHandler;
import org.soybeanMilk.web.os.WebObjectSource;

public class TestWebConfigurationParser
{
	@Before
	public void setUp() throws Exception{}

	@After
	public void tearDown() throws Exception{}
	
	@Test
	public void testParseTargetHandler() throws Exception
	{
		{
			WebConfigurationParser parser=new WebConfigurationParser(null);
			parser.parse("test/unit/web/TestWebConfigurationParser-0.xml");
			
			WebConfiguration webConfiguration= parser.getWebConfiguration();
			
			Assert.assertEquals(DefaultTypeTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getClass());
			
			Assert.assertEquals(JsonTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getTargetHandler("json").getClass());
			
			Assert.assertEquals(JsonTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getTargetHandler("forward").getClass());
			
			Assert.assertEquals(RedirectTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getTargetHandler("redirect").getClass());
			
			
			TargetHandler multi= webConfiguration.getTypeTargetHandler().getTargetHandler("pdf");
			Assert.assertEquals(JsonTargetHandler.class, multi.getClass());
			Assert.assertTrue( multi ==  webConfiguration.getTypeTargetHandler().getTargetHandler("mp3"));
			Assert.assertTrue( multi ==  webConfiguration.getTypeTargetHandler().getTargetHandler("JPEG"));
		}
		
		{
			WebConfigurationParser parser=new WebConfigurationParser(null);
			parser.parse("test/unit/web/TestWebConfigurationParser-1.xml");
			
			WebConfiguration webConfiguration= parser.getWebConfiguration();
			
			Assert.assertEquals(MyTypeTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getClass());
			
			Assert.assertEquals(JsonTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getTargetHandler("json").getClass());
			
			Assert.assertEquals(ForwardTargetHandler.class,
					webConfiguration.getTypeTargetHandler().getTargetHandler("forward").getClass());
			
		}
	}
	
	@Test
	public void testParseTarget() throws Exception
	{
		WebConfigurationParser parser=new WebConfigurationParser(null);
		parser.parse("test/unit/web/TestWebConfigurationParser-0.xml");
		
		WebConfiguration webConfiguration= parser.getWebConfiguration();
		
		{
			WebAction ac=(WebAction)webConfiguration.getExecutable("exe0");
			Assert.assertNull(ac.getTarget().getUrl());
			Assert.assertEquals(Target.FORWARD, ac.getTarget().getType());
		}
		{
			WebAction ac=(WebAction)webConfiguration.getExecutable("exe1");
			Assert.assertEquals("url", ac.getTarget().getUrl());
			Assert.assertEquals(Target.FORWARD, ac.getTarget().getType());
		}
		{
			WebAction ac=(WebAction)webConfiguration.getExecutable("exe2");
			Assert.assertNull(ac.getTarget().getUrl());
			Assert.assertEquals("json", ac.getTarget().getType());
		}
		{
			WebAction ac=(WebAction)webConfiguration.getExecutable("exe3");
			Assert.assertNull(ac.getTarget().getUrl());
			Assert.assertEquals("JSON", ac.getTarget().getType());
		}
	}
	
	public static class MyTypeTargetHandler extends DefaultTypeTargetHandler{}
	
	public static class JsonTargetHandler extends AbstractTargetHandler
	{
		@Override
		public void handleTarget(WebAction webAction,
				WebObjectSource webObjectSource) throws ServletException,
				IOException
		{
		}
	}
}
