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
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.parser.ConfigurationParser;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.bean.WebGenericConverter;
import org.soybeanMilk.web.config.WebConfiguration;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.soybeanMilk.web.exe.th.DefaultTypeTargetHandler;
import org.soybeanMilk.web.exe.th.TargetHandler;
import org.soybeanMilk.web.exe.th.TypeTargetHandler;
import org.w3c.dom.Element;

/**
 * WEB解析器，它可以解析类路径资源文件和应用“/WEB-INF”下的配置文件。
 * @author earthangry@gmail.com
 *
 */
public class WebConfigurationParser extends ConfigurationParser
{
	protected static final String TAG_TARGET_HANDLER="type-target-handler";
	protected static final String TAG_TARGET_HANDLER_ATTR_CLASS="class";
	protected static final String TAG_HANDLER="target-handler";
	protected static final String TAG_HANDLER_ATTR_TARGET_TYPE="handle-type";
	protected static final String TAG_HANDLER_ATTR_CLASS=TAG_GENERIC_CONVERTER_ATTR_CLASS;
	
	protected static final String TAG_TARGET="target";
	protected static final String TAG_TARGET_ATTR_URL="url";
	protected static final String TAG_TARGET_ATTR_TYPE="type";
	
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
	 * @param webConfiguration
	 * @param servletContext
	 */
	public WebConfigurationParser(WebConfiguration webConfiguration, ServletContext servletContext)
	{
		super(webConfiguration);
		this.servletContext=servletContext;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	/**
	 * 获取解析结果
	 * @return
	 * @date 2011-4-19
	 */
	public WebConfiguration getWebConfiguration()
	{
		return (WebConfiguration)getConfiguration();
	}
	
	//@Override
	protected void parseGlobalConfigs(Element docRoot)
	{
		super.parseGlobalConfigs(docRoot);
		
		parseTypeTargetHandler(getSingleElementByTagName(docRoot, TAG_GLOBAL_CONFIG));
	}
	
	/**
	 * 解析{@linkplain TypeTargetHandler 类型目标处理器}
	 * @param parent
	 * @date 2011-4-19
	 */
	protected void parseTypeTargetHandler(Element parent)
	{
		Element hdlEl = getSingleElementByTagName(parent, TAG_TARGET_HANDLER);
		
		WebConfiguration cfg=getWebConfiguration();
		
		String clazz = hdlEl==null ? null : getAttributeValueIngoreEmpty(hdlEl, TAG_TARGET_HANDLER_ATTR_CLASS);
		TypeTargetHandler targetHandler=cfg.getTypeTargetHandler();
		if(targetHandler == null)
		{
			if(clazz==null || clazz.length()==0)
				targetHandler = createTypeTargetHandlerInstance();
			else
				targetHandler = (TypeTargetHandler)createClassInstance(clazz);
			
			cfg.setTypeTargetHandler(targetHandler);
		}
		
		parseTargetHandler(targetHandler, hdlEl);
	}
	
	/**
	 * 解析子类型处理器
	 * @param typeTargetHandler
	 * @param parent
	 * @date 2011-4-19
	 */
	protected void parseTargetHandler(TypeTargetHandler typeTargetHandler, Element parent)
	{
		List<Element> children=getChildrenByTagName(parent, TAG_HANDLER);
		if(children==null || children.isEmpty())
			return;
		
		for(Element e : children)
		{
			String targetTypes = getAttributeValueIngoreEmpty(e, TAG_HANDLER_ATTR_TARGET_TYPE);
			String clazz = getAttributeValueIngoreEmpty(e, TAG_HANDLER_ATTR_CLASS);
			
			assertNotEmpty(targetTypes, "<"+TAG_HANDLER+"> attribute ["+TAG_HANDLER_ATTR_TARGET_TYPE+"] must not be empty");
			assertNotEmpty(clazz, "<"+TAG_HANDLER+"> attribute ["+TAG_HANDLER_ATTR_CLASS+"] must not be empty");
			
			TargetHandler handler=(TargetHandler)createClassInstance(clazz);
			String[] ttps=SoybeanMilkUtils.split(targetTypes, ',');
			
			for(String tt : ttps)
				typeTargetHandler.addTargetHandler(tt.trim(), handler);
		}
	}
	
	//@Override
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
		String url=getAttributeValue(element, TAG_TARGET_ATTR_URL);
		String type=getAttributeValueIngoreEmpty(element, TAG_TARGET_ATTR_TYPE);
		
		if(type == null)
			type=Target.FORWARD;
		
		targetInfo.setUrl(url);
		targetInfo.setType(type);
	}
	
	//@Override
	protected String formatIncludeFileName(String rawFileName)
	{
		if(rawFileName.startsWith("/WEB-INF/"))
			return getServletContext().getRealPath("").replace(File.separatorChar, '/')+rawFileName;
		else if(rawFileName.startsWith("WEB-INF/"))
			return getServletContext().getRealPath("").replace(File.separatorChar, '/')+"/"+rawFileName;
		else
			return rawFileName;
	}
	
	//@Override
	protected Class<?> nameToClass(String name)
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
			return super.nameToClass(name);
	}
	
	//@Override
	protected String getDefaultConfigFile()
	{
		return WebConstants.DEFAULT_CONFIG_FILE;
	}
	
	@Override
	protected Configuration createConfigurationInstance()
	{
		return new WebConfiguration();
	}

	//@Override
	protected GenericConverter createGenericConverterInstance()
	{
		return new WebGenericConverter();
	}
	
	//@Override
	protected Action createActionIntance()
	{
		return new WebAction();
	}
	
	protected Target createTargetInstance()
	{
		return new Target();
	}
	
	protected TypeTargetHandler createTypeTargetHandlerInstance()
	{
		return new DefaultTypeTargetHandler();
	}
}