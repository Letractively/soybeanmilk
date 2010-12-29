package test.unit.core;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.core.bean.PropertyInfo;


public class TestPropertyInfo
{
	@Before
	public void setUp(){}
	
	@Test
	public void getPropertyInfo()
	{
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(String.class);
			Assert.assertNotNull(pi);
			Assert.assertNull( pi.getSubPropertyInfos() );
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(java.util.Date.class);
			Assert.assertNotNull(pi.getSubPropertyInfo("date").getReadMethod());
			Assert.assertNotNull(pi.getSubPropertyInfo("hours").getReadMethod());
			Assert.assertNotNull(pi.getSubPropertyInfo("minutes").getReadMethod());
			Assert.assertNotNull(pi.getSubPropertyInfo("month").getReadMethod());
			Assert.assertNotNull(pi.getSubPropertyInfo("seconds").getReadMethod());
			Assert.assertNotNull(pi.getSubPropertyInfo("time").getReadMethod());
			Assert.assertNotNull(pi.getSubPropertyInfo("year").getReadMethod());
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(int.class);
			Assert.assertNotNull(pi);
			Assert.assertNull( pi.getSubPropertyInfos() );
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(MyBean.class);
			Assert.assertNotNull(pi);
			Assert.assertNotNull(pi.getSubPropertyInfo("integer").getWriteMethod());
			
			PropertyInfo p0=pi.getSubPropertyInfo("myBean");
			Assert.assertNotNull(p0.getReadMethod());
			Assert.assertNotNull(p0.getSubPropertyInfo("myBean").getReadMethod());
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(MyBean2.class);
			Assert.assertNotNull(pi);
			Assert.assertNotNull(pi.getSubPropertyInfo("list").getReadMethod());
			
			PropertyInfo p0=pi.getSubPropertyInfo("myBean");
			Assert.assertNotNull(p0.getReadMethod());
			Assert.assertNotNull(p0.getSubPropertyInfo("myBean").getReadMethod());
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(int[].class);
			Assert.assertNotNull(pi);
			Assert.assertNull( pi.getSubPropertyInfos() );
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(MyBean[].class);
			Assert.assertNotNull(pi);
			Assert.assertNull( pi.getSubPropertyInfos() );
		}
		
		{
			PropertyInfo pi=PropertyInfo.getPropertyInfo(MyBean2[].class);
			Assert.assertNotNull(pi);
			Assert.assertNull( pi.getSubPropertyInfos() );
		}
	}
	
	protected static class MyBean
	{
		private MyBean myBean;
		private String string;
		private Integer integer;
		private int _int;
		private MyBean2 myBean2;
		
		public MyBean getMyBean() {
			return myBean;
		}
		public void setMyBean(MyBean myBean) {
			this.myBean = myBean;
		}
		public String getString() {
			return string;
		}
		public void setString(String string) {
			this.string = string;
		}
		public Integer getInteger() {
			return integer;
		}
		public void setInteger(Integer integer) {
			this.integer = integer;
		}
		public int get_int() {
			return _int;
		}
		public void set_int(int int1) {
			_int = int1;
		}
		public MyBean2 getMyBean2() {
			return myBean2;
		}
		public void setMyBean2(MyBean2 myBean2) {
			this.myBean2 = myBean2;
		}
	}
	
	protected static class MyBean2
	{
		private MyBean myBean;
		private Integer integer;
		private List<String> list;
		
		public MyBean getMyBean() {
			return myBean;
		}
		public void setMyBean(MyBean myBean) {
			this.myBean = myBean;
		}
		public Integer getInteger() {
			return integer;
		}
		public void setInteger(Integer integer) {
			this.integer = integer;
		}
		public List<String> getList() {
			return list;
		}
		public void setList(List<String> list) {
			this.list = list;
		}
	}
}
