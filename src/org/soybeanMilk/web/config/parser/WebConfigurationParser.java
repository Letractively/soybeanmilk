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
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.config.ExceptionHandlerInfo;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author earthAngry@gmail.com
 *
 */
public class WebConfigurationParser extends ConfigurationParser
{
	private static Log log=LogFactory.getLog(WebConfigurationParser.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	public static final String TAG_EXCEPTION_HANDLER="exception-handler";
	public static final String TAG_EXCEPTION_HANDLER_ATTR_EXECUTABLE="executable-name";
	public static final String TAG_EXCEPTION_HANDLER_ATTR_ARG_KEY="exception-arg-key";
	
	public static final String TAG_TARGET="target";
	public static final String TAG_TARGET_ATTR_URL="url";
	public static final String TAG_TARGET_ATTR_TYPE="type";
	
	private ServletContext servletContext;
	
	/**
	 * @param document
	 * @see ConfigurationParser#ConfigurationParser(Document)
	 */
	public WebConfigurationParser(Document document)
	{
		super(document);
	}

	/**
	 * @param inputStream
	 * @see ConfigurationParser#ConfigurationParser(InputStream)
	 */
	public WebConfigurationParser(InputStream inputStream)
	{
		super(inputStream);
	}
	
	/**
	 * WEB下从默认配置文件解析，配置文件{@link WebConstants#DEFAULT_CONFIG_FILE}应该存在
	 * @param servletContext
	 */
	public WebConfigurationParser(ServletContext servletContext)
	{
		this(WebConstants.DEFAULT_CONFIG_FILE, servletContext);
	}
	
	/**
	 * WEB下的配置文件解析
	 * @param configFile 配置文件名，可以是类路径资源文件，也可以是“/WEB-INF/”下的文件
	 * @param servletContext 应用语境，用以确定文件的绝对路径
	 */
	public WebConfigurationParser(String configFile, ServletContext servletContext)
	{
		super((String)null);
		
		if(configFile == null)
			return;
		
		setDocument(parseDocument(configFile));
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	@Override
	public void parseGlobalConfigs()
	{
		super.parseGlobalConfigs();
		
		Element parent=getSingleElementByTagName(getCurrentDocumentRoot(), TAG_GLOBAL_CONFIG);
		
		parseExceptionHandlerInfo(parent);
	}
	
	@Override
	public void parseRefs()
	{
		processExceptionHanderInfoRefs();
		super.parseRefs();
	}
	
	/**
	 * 解析父元素下的异常处理器信息对象。
	 * @param parent
	 */
	protected void parseExceptionHandlerInfo(Element parent)
	{
		Element element=getSingleElementByTagName(parent, TAG_EXCEPTION_HANDLER);
		if(element == null)
			return;
		
		String exeName=getAttribute(element, TAG_EXCEPTION_HANDLER_ATTR_EXECUTABLE);
		assertNotEmpty(exeName, "<"+TAG_EXCEPTION_HANDLER+"> attribute ["+TAG_EXCEPTION_HANDLER_ATTR_EXECUTABLE+"] must not be null");
		String argKey=getAttribute(element, TAG_EXCEPTION_HANDLER_ATTR_ARG_KEY);
		assertNotEmpty(exeName, "<"+TAG_EXCEPTION_HANDLER+"> attribute ["+TAG_EXCEPTION_HANDLER_ATTR_ARG_KEY+"] must not be null");
		
		ExceptionHandlerInfo info=createExceptionHandlerInfoInstane();
		info.setExceptionHandler(new ExecutableRefProxy(customizeExecutableName(exeName)));
		info.setExceptionArgKey(argKey);
		
		if(_logDebugEnabled)
			log.debug("parsed '"+info+"'");
		
		getWebConfiguration().setExceptionHandlerInfo(info);
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
		
		if(_logDebugEnabled)
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
		String url=getAttribute(element, TAG_TARGET_ATTR_URL);
		assertNotEmpty(url, "<"+TAG_TARGET+"> attribute ["+TAG_TARGET_ATTR_URL+"] must not be null");
		String type=getAttribute(element, TAG_TARGET_ATTR_TYPE);
		
		targetInfo.setUrl(url);
		targetInfo.setType(type);
	}
	
	/**
	 * 替换异常处理器的代理为真实的可执行对象
	 */
	protected void processExceptionHanderInfoRefs()
	{
		ExceptionHandlerInfo hi=getWebConfiguration().getExceptionHandlerInfo();
		if(hi == null)
			return;
		
		Executable he=hi.getExceptionHandler();
		
		if(he instanceof ExecutableRefProxy)
		{
			Executable handler = getWebConfiguration().getExecutable(((ExecutableRefProxy)he).getRefName());
			hi.setExceptionHandler(handler);
		}
	}
	
	@Override
	protected Document parseDocument(String fileName)
	{
		if(fileName.startsWith("/WEB-INF/"))
			fileName = getServletContext().getRealPath("").replace(File.separatorChar, '/')+fileName;
		else if(fileName.startsWith("WEB-INF/"))
			fileName = getServletContext().getRealPath("").replace(File.separatorChar, '/')+"/"+fileName;
		
		return super.parseDocument(fileName);
	}

	@Override
	protected Configuration createConfigurationInstance()
	{
		return new WebConfiguration();
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
	
	protected WebConfiguration getWebConfiguration()
	{
		return (WebConfiguration)getConfiguration();
	}
	
	protected ExceptionHandlerInfo createExceptionHandlerInfoInstane()
	{
		return new ExceptionHandlerInfo();
	}
	
	protected Target createTargetInstance()
	{
		return new Target();
	}
}