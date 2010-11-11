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

package org.soybeanMilk.web.os;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.web.WebConstants;

/**
 * 用于WEB应用的对象源，它的实例的生命周期与一次请求的生命周期相同。
 * @author earthAngry@gmail.com
 * @date 2010-7-19
 *
 */
public class WebObjectSource extends ConvertableObjectSource
{
	private static Log log = LogFactory.getLog(WebObjectSource.class);
	private static boolean _logDebugEnabled=log.isDebugEnabled();
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext application;
	
	public WebObjectSource(HttpServletRequest request,
			HttpServletResponse response, ServletContext application)
	{
		this(request, response, application, null);
	}
	
	public WebObjectSource(HttpServletRequest request,
			HttpServletResponse response, ServletContext application,
			GenericConverter genericConverter)
	{
		super();
		this.request = request;
		this.response = response;
		this.application = application;
		super.setGenericConverter(genericConverter);
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public ServletContext getApplication() {
		return application;
	}
	public void setApplication(ServletContext application) {
		this.application = application;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object get(Serializable key, Class<?> objectType)
	{
		Object data = null;
		if(objectType == HttpServletRequest.class)
			data = request;
		else if(objectType == HttpServletResponse.class)
			data = response;
		else if(objectType == ServletContext.class)
			data = application;
		else if(objectType == HttpSession.class)
			data = request.getSession();
		else
		{
			//WEB环境下只有字符串主键
			String strKey = (String)key;
			
			if(strKey==null || strKey.length()==0)
				throw new ObjectSourceException("[key] must not be empty.");
			
			int accessorIdx=strKey.indexOf(WebConstants.ACCESSOR);
			boolean accessor = accessorIdx>0 && accessorIdx<strKey.length()-1;
			String scope = accessor ? strKey.substring(0, accessorIdx) : strKey;
			String subKey = accessor ? strKey.substring(accessorIdx+1) : null;
			
			if(scope.equals(WebConstants.Scope.PARAM))
			{
				if(objectType == null)
					throw new ObjectSourceException("[objectType] must not be null while getting object from '"+scope+"'");
				
				data = convertParamMap(request.getParameterMap(), subKey, objectType);
			}
			else if(scope.equals(WebConstants.Scope.REQUEST))
				data = accessor ? request.getAttribute(subKey) : convertServletObject(request, objectType);
			else if(scope.equals(WebConstants.Scope.SESSION))
				data = accessor ? request.getSession().getAttribute(subKey) : convertServletObject(request.getSession(), objectType);
			else if(scope.equals(WebConstants.Scope.APPLICATION))
				data = accessor ? application.getAttribute(subKey) : convertServletObject(application, objectType);
			else if(scope.equals(WebConstants.Scope.RESPONSE) && !accessor)
				data = convertServletObject(response, objectType);
			else
			{
				//只要包含访问符，那么就必须以框架允许的作用域开头
				//否则，从默认作用域取
				if(accessor)
					throw new ObjectSourceException("the scope in key '"+key+"' is invalid, it must be one of '"+WebConstants.Scope.PARAM+"', '"+WebConstants.Scope.REQUEST+"', '"+WebConstants.Scope.SESSION+"', '"+WebConstants.Scope.APPLICATION+"', '"+WebConstants.Scope.RESPONSE+"'");
				else
				{
					data = convertParamMap(request.getParameterMap(), strKey, objectType);
					
					if(_logDebugEnabled)
					{
						scope=WebConstants.Scope.PARAM;
						subKey=strKey;
					}
				}
			}
			
			if(_logDebugEnabled)
				log.debug("get '"+data+"' from '"+scope+"' with key '"+subKey+"'");
		}
		
		return data;
	}
	
	@Override
	public void set(Serializable key, Object obj)
	{
		String strKey = (String)key;
		
		//主键不能为空
		if(strKey==null || strKey.length()==0)
			throw new ObjectSourceException("[key] must not be empty.");
		
		int accessorIdx=strKey.indexOf(WebConstants.ACCESSOR);
		boolean accessor = accessorIdx>0 && accessorIdx<strKey.length()-1;
		String scope = accessor ? strKey.substring(0, accessorIdx) : strKey;
		String subKey = accessor ? strKey.substring(accessorIdx+1) : null;
		
		if(scope.equals(WebConstants.Scope.PARAM))
			throw new ObjectSourceException("'"+key+"' is invalid, can not save object into '"+WebConstants.Scope.PARAM+"'");
		else if(scope.equals(WebConstants.Scope.REQUEST))
			request.setAttribute(subKey, obj);
		else if(scope.equals(WebConstants.Scope.SESSION))
			request.getSession().setAttribute(subKey, obj);
		else if(scope.equals(WebConstants.Scope.APPLICATION))
			application.setAttribute(subKey, obj);
		else if(scope.equals(WebConstants.Scope.RESPONSE))
			throw new ObjectSourceException("'"+key+"' is invalid, can not save object into '"+WebConstants.Scope.RESPONSE+"'");
		else
		{
			//只要包含访问符，那么就必须以框架允许的作用域开头
			//否则，保存到默认作用域
			if(accessor)
				throw new ObjectSourceException("the scope in key '"+key+"' is invalid, it must be one of '"+WebConstants.Scope.PARAM+"', '"+WebConstants.Scope.REQUEST+"', '"+WebConstants.Scope.SESSION+"', '"+WebConstants.Scope.APPLICATION+"', '"+WebConstants.Scope.RESPONSE+"'");
			else
			{
				request.setAttribute(strKey, obj);
				
				if(_logDebugEnabled)
				{
					scope=WebConstants.Scope.REQUEST;
					subKey=strKey;
				}
			}
		}
		
		if(_logDebugEnabled)
			log.debug("save '"+obj+"' into '"+scope+"' with key '"+subKey+"'");
	}
	
	/**
	 * 将请求参数映射表转换为目标对象。<br>
	 * 如果<code>keyFilter</code>是一个明确的关键字（映射表中有该关键字的值），它将直接根据该关键字的值来转换；<br>
	 * 如果<code>keyFilter</code>是<code>null</code>，那么它将使用原始的请求参数映射表来进行转换；<br>
	 * 否则，它会根据<code>keyFilter</code>来对参数映射表进行过滤，产生一个新的映射表（它的关键字将会被替换为原始关键字的“<code>[keyFilter]</code>.”之后的部分，比如由“<code>beanName.propertyName</code>”变为“<code>propertyName</code>”），
	 * 然后使用它进行转换。
	 * 
	 * @param rawRequestParams 原始的请求参数映射表，直接由<code>request.getParameterMap()</code>取得
	 * @param keyFilter 主键筛选器，只有以此筛选器开头的Map关键字才会被转换，如果为null，则表明不做筛选
	 * @param targetType 目标类型
	 * 
	 * @return
	 */
	protected Object convertParamMap(Map<String,String[]> rawRequestParams, String keyFilter, Class<?> targetType)
	{
		GenericConverter genericConverter=getGenericConverter();
		
		if(keyFilter == null)
			return genericConverter.convert(rawRequestParams, targetType);
		
		//明确的KEY，直接根据值转换
		Object explicit = rawRequestParams.get(keyFilter);
		if(explicit != null)
			return genericConverter.convert(explicit, targetType);
		else
		{
			String keyPrefix = keyFilter+WebConstants.ACCESSOR;
			
			Map<String,Object> filtered = new HashMap<String, Object>();
			Set<String> keys=rawRequestParams.keySet();
			for(String k : keys)
			{
				if(k.startsWith(keyPrefix))
					filtered.put(k.substring(keyPrefix.length()), rawRequestParams.get(k));
			}
			
			return genericConverter.convert(filtered, targetType);
		}
	}
	
	/**
	 * 转换servlet对象到目标类型的对象
	 * @param obj servlet对象，包括：HttpServletRequest、HttpSession、HttpServletResponse、ServletContext
	 * @param targetType
	 * @return
	 */
	protected Object convertServletObject(Object obj, Class<?> targetType)
	{
		if(targetType == null || targetType.isAssignableFrom(obj.getClass()))
			return obj;
		
		GenericConverter genericConverter=getGenericConverter();
		
		Class<?> sourceClass=null;
		Converter converter=null;
		
		if(obj instanceof HttpServletRequest)
			sourceClass=HttpServletRequest.class;
		else if(obj instanceof HttpSession)
			sourceClass=HttpSession.class;
		else if(obj instanceof HttpServletResponse)
			sourceClass=HttpServletResponse.class;
		else if(obj instanceof ServletContext)
			sourceClass=ServletContext.class;
		else
			throw new ObjectSourceException("unknown servlet object '"+obj.getClass().getName()+"'");
		
		converter=genericConverter.getConverter(sourceClass, targetType);
		
		if(converter == null)
			throw new ObjectSourceException("no Converter defined for converting '"+sourceClass.getName()+"' to '"+targetType.getName()+"'");
		
		return converter.convert(obj, targetType);
	}
}