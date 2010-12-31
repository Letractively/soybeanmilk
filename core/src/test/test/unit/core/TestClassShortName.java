package test.unit.core;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.config.parser.ClassShortName;

public class TestClassShortName
{
	@Before
	public void setUp(){}
	
	@Test
	public void getShortName()
	{
		{
			Class<?> c=ClassShortName.get("boolean");
			Assert.assertEquals(boolean.class, c);
		}
		{

			Class<?> c=ClassShortName.get("boolean[]");
			Assert.assertEquals(boolean[].class, c);
		}
		{
			Class<?> c=ClassShortName.get("Boolean");
			Assert.assertEquals(Boolean.class, c);
		}
		{

			Class<?> c=ClassShortName.get("Boolean[]");
			Assert.assertEquals(Boolean[].class, c);
		}
		{
			Class<?> c=ClassShortName.get("String[]");
			Assert.assertEquals(String[].class, c);
		}
		{
			Class<?> c=ClassShortName.get("java.sql.Date");
			Assert.assertEquals(java.sql.Date.class, c);
		}
		{
			Class<?> c=ClassShortName.get("java.sql.Date[]");
			Assert.assertEquals(java.sql.Date[].class, c);
		}
		{
			Class<?> c=ClassShortName.get("java.sql.Time");
			Assert.assertEquals(java.sql.Time.class, c);
		}
		{
			Class<?> c=ClassShortName.get("java.sql.Time[]");
			Assert.assertEquals(java.sql.Time[].class, c);
		}
		{
			Class<?> c=ClassShortName.get("java.sql.Timestamp");
			Assert.assertEquals(java.sql.Timestamp.class, c);
		}
		{
			Class<?> c=ClassShortName.get("java.sql.Timestamp[]");
			Assert.assertEquals(java.sql.Timestamp[].class, c);
		}
	}
}
