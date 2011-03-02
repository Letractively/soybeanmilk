package test.unit.web;

import java.util.Map;

import javax.servlet.ServletContext;

import org.soybeanMilk.web.servlet.DispatchServlet;
import org.springframework.mock.web.MockServletContext;

public class MockDispathServlet extends DispatchServlet
{
	private static final long serialVersionUID = 1L;
	
	private MockServletContext servletContext;
	private Map<String, String> servletInitParameters;
	
	public MockDispathServlet(MockServletContext servletContext, Map<String, String> servletInitParameters)
	{
		super();
		
		this.servletContext=servletContext;
		this.servletInitParameters=servletInitParameters;
	}
	
	//@Override
	public String getInitParameter(String name)
	{
		return servletInitParameters.get(name);
	}

	//@Override
	public ServletContext getServletContext()
	{
		return servletContext;
	}
}