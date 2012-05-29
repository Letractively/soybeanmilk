/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package test.unit.web;

import java.util.Map;

import javax.servlet.ServletContext;

import org.soybeanMilk.web.servlet.DispatchServlet;
import org.springframework.mock.web.MockServletContext;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
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
		String result=null;
		
		try
		{
			result=super.getInitParameter(name);
		}
		catch(Exception e){}
		
		if(result == null)
			result=servletInitParameters.get(name);
		
		return result;
	}

	//@Override
	public ServletContext getServletContext()
	{
		return servletContext;
	}
}