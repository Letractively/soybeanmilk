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

package org.soybeanMilk.web.config.parser;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.w3c.dom.Element;

/**
 * WEB解析器，它可以解析类路径资源文件和应用“/WEB-INF”下的配置文件。
 * @author earthAngry@gmail.com
 *
 */
public class WebConfigurationParser extends ConfigurationParser
{
	private static Log log=LogFactory.getLog(WebConfigurationParser.class);
	
	
	public static final String TAG_TARGET="target";
	public static final String TAG_TARGET_ATTR_URL="url";
	public static final String TAG_TARGET_ATTR_TYPE="type";
	
	private ServletContext servletContext;
	
	/**
	 * 创建WEB解析器
	 * @param servletContext
	 */
	public WebConfigurationParser(ServletContext servletContext)
	{
		this(null, servletContext);
	}
	
	/**
	 * 创建WEB解析器并预设存储配置对象
	 * @param configuration
	 * @param servletContext
	 */
	public WebConfigurationParser(Configuration configuration, ServletContext servletContext)
	{
		super(configuration);
		this.servletContext=servletContext;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	protected void setActionProperties(Action action, Element element)
	{
		super.setActionProperties(action, element);
		parseTarget(element,(WebAction)action);
	}
	
	/**
	 * 解析父元素下的目标信息对象，写入对应的动作对象中。
	 * @param parent
	 * @param action
	 */
	protected void parseTarget(Element parent,WebAction action)
	{
		Element element=getSingleElementByTagName(parent, TAG_TARGET);
		if(element == null)
			return;
		
		Target targetInfo=createTargetInstance();
		setTargetInfoProperties(targetInfo,element);
		
		if(log.isDebugEnabled())
			log.debug("parsed '"+targetInfo+"'");
		
		action.setTarget(targetInfo);
	}
	
	/**
	 * 设置目标信息对象属性
	 * @param targetInfo
	 * @param element
	 */
	protected void setTargetInfoProperties(Target targetInfo,Element element)
	{
		//与动作和调用名称一样，url也应该可以为空字符串
		String url=getAttribute(element, TAG_TARGET_ATTR_URL);
		assertNotNull(url, "<"+TAG_TARGET+"> attribute ["+TAG_TARGET_ATTR_URL+"] must not be null");
		String type=getAttributeIngoreEmpty(element, TAG_TARGET_ATTR_TYPE);
		
		targetInfo.setUrl(url);
		targetInfo.setType(type);
	}
	
	@Override
	protected String formatIncludeFileName(String rawFileName)
	{
		if(rawFileName.startsWith("/WEB-INF/"))
			return getServletContext().getRealPath("").replace(File.separatorChar, '/')+rawFileName;
		else if(rawFileName.startsWith("WEB-INF/"))
			return getServletContext().getRealPath("").replace(File.separatorChar, '/')+"/"+rawFileName;
		else
			return rawFileName;
	}
	
	@Override
	protected Class<?> converterClassAttrToClass(String name)
	{
		if(WebConstants.Scope.REQUEST.equals(name))
			return HttpServletRequest.class;
		else if(WebConstants.Scope.SESSION.equals(name))
			return HttpSession.class;
		if(WebConstants.Scope.APPLICATION.equals(name))
			return ServletContext.class;
		if(WebConstants.Scope.RESPONSE.equals(name))
			return HttpServletResponse.class;
		else
			return super.converterClassAttrToClass(name);
	}
	
	@Override
	protected String getDefaultConfigFile()
	{
		return WebConstants.DEFAULT_CONFIG_FILE;
	}
	
	@Override
	protected GenericConverter createGenericConverterInstance()
	{
		return new WebGenericConverter();
	}
	
	@Override
	protected Action createActionIntance()
	{
		return new WebAction();
	}
	
	protected Target createTargetInstance()
	{
		return new Target();
	}
}