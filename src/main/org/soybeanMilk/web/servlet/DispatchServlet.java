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
 * 框架整合servlet，它可以将WEB请求转给WEB执行器
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
	
	/**WEB执行器存储关键字*/
	private String appExecutorKey;
	
	public DispatchServlet()
	{
		super();
	}
	
	public WebExecutor getWebExecutor() {
		return webExecutor;
	}
	public String getEncoding() {
		return encoding;
	}
	public String getAppExecutorKey() {
		return appExecutorKey;
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
		if(appExecutorKey != null)
			getServletContext().removeAttribute(appExecutorKey);
		
		this.webExecutor = null;
		super.destroy();
	}
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		initEncoding();
		initWebExecutor();
		
		if(appExecutorKey != null)
			getServletContext().setAttribute(appExecutorKey, webExecutor);
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
	 * 初始化框架编码
	 * 如果你配置了{@link WebConstants.ServletInitParams#ENCODING}参数，框架将使用这个编码，
	 * 否则，将使用{@link WebConstants#DEFAULT_ENCODING}定义的编码
	 */
	protected void initEncoding()
	{
		encoding=getInitParameter(WebConstants.ServletInitParams.ENCODING);
		if(encoding==null || encoding.length()==0)
			encoding=WebConstants.DEFAULT_ENCODING;
	}
	
	/**
	 * 初始化执行器在应用中的存储关键字
	 */
	protected void initAppExecutorKey()
	{
		appExecutorKey=getInitParameter(WebConstants.ServletInitParams.APPLICATION_EXECUTOR_KEY);
	}
	
	/**
	 * 初始化{@link WebExecutor WEB执行器}对象。
	 * 如果你配置了{@link WebConstants.ServletInitParams#SOYBEAN_MILK_CONFIG}参数，框架将使用它初始化，
	 * 否则，将使用{@link WebConstants#DEFAULT_CONFIG_FILE}。
	 * 它还会尝试从<code>application</code>作用域中查找标识为{@link WebConstants.ServletInitParams#EXTERNAL_RESOLVER_FACTORY}的外部{@linkplain ResolverFactory 解决对象工厂}并加入框架。
	 */
	protected void initWebExecutor()
	{
		DefaultResolverFactory rf=new DefaultResolverFactory();
		rf.setExternalResolverFactory(findExternalResolverFactory());
		
		WebConfiguration configuration=new WebConfiguration(rf);
		
		String configFileName=getInitParameter(WebConstants.ServletInitParams.SOYBEAN_MILK_CONFIG);
		
		WebConfigurationParser parser=new WebConfigurationParser(configuration,getServletContext());
		
		parser.parse(configFileName);
		
		this.webExecutor=new WebExecutor(configuration);
	}
	
	/**
	 * 查找应用的外部解决对象工厂
	 * @return
	 */
	protected ResolverFactory findExternalResolverFactory()
	{
		String erfKey=getInitParameter(WebConstants.ServletInitParams.EXTERNAL_RESOLVER_FACTORY);
		
		ResolverFactory erf=null;
		
		if(erfKey != null)
		{
			erf=(ResolverFactory)getServletContext().getAttribute(erfKey);
			if(erf == null)
				throw new NullPointerException("can not find external ResolverFactory in application with key '"+erfKey+"'");
			
			if(_logDebugEnabled)
				log.debug("find external resolver factory '"+erf.getClass().getName()+"' in 'application' scope");
		}
		
		return erf;
	}
}