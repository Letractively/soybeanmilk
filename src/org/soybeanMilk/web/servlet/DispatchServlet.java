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

package org.soybeanMilk.web.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebExecutor;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.config.parser.WebConfigurationParser;
import org.soybeanMilk.web.os.WebObjectSource;


/**
 * @author earthAngry@gmail.com
 *
 */
public class DispatchServlet extends HttpServlet
{
	private static final long serialVersionUID = -1647302324862162094L;
	
	private static Log log=LogFactory.getLog(DispatchServlet.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	/**WEB执行器*/
	private WebExecutor webExecutor;
	
	/**编码*/
	private String encoding;
	
	public DispatchServlet()
	{
		super();
	}
	
	public WebExecutor getWebExecutor() {
		return webExecutor;
	}
	public void setWebExecutor(WebExecutor webExecutor) {
		this.webExecutor = webExecutor;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doProcess(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doProcess(req,resp);
	}

	@Override
	public void destroy()
	{
		this.webExecutor = null;
		super.destroy();
	}
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		initEncoding();
		initWebExecutor();
	}
	
	/**
	 * 处理WEB请求
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if(request.getCharacterEncoding() == null)
			request.setCharacterEncoding(encoding);
		
		try
		{
			webExecutor.execute(new WebObjectSource(request, response, getServletContext()));
		}
		catch(ExecutableNotFoundException e)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getExecutableName());
		}
	}
	
	/**
	 * 初始化框架编码。
	 * 如果你配置了{@link WebConstants.ServletInitParams#ENCODING}参数，框架将使用这个编码，
	 * 否则，将使用{@link WebConstants#DEFAULT_ENCODING}定义的编码
	 */
	protected void initEncoding()
	{
		encoding=getInitParameter(WebConstants.ServletInitParams.ENCODING);
		if(encoding == null)
			encoding=WebConstants.DEFAULT_ENCODING;
	}
	
	/**
	 * 初始化{@link WebExecutor WEB执行器}对象。
	 * 如果你配置了{@link WebConstants.ServletInitParams#SOYBEAN_MILK_CONFIG}参数，框架将使用它初始化，
	 * 否则，将使用{@link WebConstants#DEFAULT_CONFIG_FILE}。
	 * 它还会尝试从<code>application</code>作用域中查找标识为{@link WebConstants.ServletInitParams#EXTERNAL_RESOLVER_FACTORY}的外部{@linkplain ResolverFactory 解决对象工厂}并加入框架。
	 */
	protected void initWebExecutor()
	{
		String configFileName=getInitParameter(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG);
		if(configFileName == null)
			configFileName=WebConstants.DEFAULT_CONFIG_FILE;
		
		if(configFileName.startsWith("/WEB-INF/"))
			configFileName = getServletContext().getRealPath("").replace(File.separatorChar, '/')+configFileName;
		else if(configFileName.startsWith("WEB-INF/"))
			configFileName = getServletContext().getRealPath("/").replace(File.separatorChar, '/')+configFileName;
		
		WebConfiguration configuration=new WebConfiguration(new DefaultResolverFactory());
		
		String efKey=getInitParameter(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY);
		if(efKey != null)
		{
			ResolverFactory external=(ResolverFactory)getServletContext().getAttribute(efKey);
			if(external == null)
				throw new NullPointerException("can not find external ResolverFactory in application with key '"+efKey+"'");
			
			DefaultResolverFactory drf=(DefaultResolverFactory)configuration.getResolverFactory();
			drf.setExternalResolverFactory(external);
			
			if(_logDebugEnabled)
				log.debug("add '"+external+"' in 'application' scope to '"+configuration+"' as external resolver factory");
		}
		
		new WebConfigurationParser(configFileName).parse(configuration);
		
		this.webExecutor=new WebExecutor(configuration);
	}
}