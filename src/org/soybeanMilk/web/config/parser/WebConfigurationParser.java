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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.config.ExceptionHandlerInfo;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.w3c.dom.Element;

/**
 * @author earthAngry@gmail.com
 *
 */
public class WebConfigurationParser extends ConfigurationParser
{
	private static Log log=LogFactory.getLog(WebConfigurationParser.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	public static final String TAG_GLOBAL_CONFIG_TAG_EXECUTABLE_NAME_SUFFIX="exe-name-suffix";
	
	public static final String TAG_GLOBAL_CONFIG_TAG_INVOKE_ACCESSABLE="invoke-accessable";
	
	public static final String TAG_EXCEPTION_HANDLER="exception-handler";
	public static final String TAG_EXCEPTION_HANDLER_ATTR_EXECUTABLE="executable-name";
	public static final String TAG_EXCEPTION_HANDLER_ATTR_ARG_KEY="exception-arg-key";
	
	public static final String TAG_TARGET="target";
	public static final String TAG_TARGET_ATTR_URL="url";
	public static final String TAG_TARGET_ATTR_TYPE="type";
	
	/**
	 * 可执行对象名的后缀
	 */
	private String executableNameSuffix;
	
	public WebConfigurationParser()
	{
		super();
	}
	
	public WebConfigurationParser(String configFile)
	{
		super(configFile);
	}
	
	public WebConfigurationParser(InputStream configFileStream)
	{
		super(configFileStream);
	}
	
	@Override
	protected void parseGlobalConfigs()
	{
		super.parseGlobalConfigs();
		
		Element parent=getSingleElementByTagName(root, TAG_GLOBAL_CONFIG);
		
		parseExecutableNameSuffix(parent);
		parseExceptionHandlerInfo(parent);
	}
	
	/**
	 * 解析并保存父元素下的可执行对象后缀配置。
	 * @param parent
	 */
	protected void parseExecutableNameSuffix(Element parent)
	{
		Element element=getSingleElementByTagName(parent,TAG_GLOBAL_CONFIG_TAG_EXECUTABLE_NAME_SUFFIX);
		if(element != null)
			this.executableNameSuffix = element.getTextContent();
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
	
	@Override
	protected void processExecutableRefProxys()
	{
		processExecutableRefProxysExceptionHanderInfo();
		super.processExecutableRefProxys();
	}
	
	/**
	 * 替换异常处理器的代理为真实的可执行对象
	 */
	protected void processExecutableRefProxysExceptionHanderInfo()
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
	protected String customizeExecutableName(String rawName)
	{
		if(executableNameSuffix==null || executableNameSuffix.length()==0)
			return rawName;
		return rawName+executableNameSuffix;
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
		return (WebConfiguration)configuration;
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