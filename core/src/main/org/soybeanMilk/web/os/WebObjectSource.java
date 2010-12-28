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
 * <br>
 * 传递给它的关键字会被理解为由两个部分组成：[scope].[yourKey]，其中
 * “[scope]”表示作用域，“[yourKey]”则是真正的该作用域下的关键字。
 * <br>
 * 它目前所支持的关键字格式及其说明如下：
 * <ul>
 * 	<li>
 *   set
 *   <ul>
 *  	<li>
 *  		<span class="tagValue">yourKey</span> <br/>
 *  		结果将以“<span class="var">yourKey</span>”关键字被保存到“<span class="var">request</span>”作用域中
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request.yourKey</span> <br/>
 *  		同上
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session.yourKey</span> <br/>
 *  		结果将以“<span class="var">yourKey</span>”关键字被保存到“<span class="var">session</span>”作用域中
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application.yourKey</span> <br/>
 *  		结果将以“<span class="var">yourKey</span>”关键字被保存到“<span class="var">application</span>”作用域中
 *  	</li>
 *   </ul>
 *  </li>
 *  <li>
 *  	get
 *  	<ul>
 *  	<li>
 *  		<span class="tagValue">param</span> <br/>
 *  		整个请求参数映射表。如果目标类型是<span class="var">java.util.Map</span>，
 *  		那么它不会做任何处理而直接返回整个参数映射表；如果是其他类型，它会首先将此映射表转换为这个类型的对象，然后返回此对象。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">yourKey</span> <br/>
 *  		请求参数映射表中以“<span class="var">yourKey</span>”开头的请求参数。
 *  		如果这个参数有明确的值，它将对这个值进行类型转换（需要的话），然后返回转换后的对象；否则，就根据“<span class="var">yourKey</span>”来对参数映射表进行过滤，
 *  		产生一个新的映射表（它的主键是原始关键字“<span class="var">yourKey.</span>”之后的部分，比如由“<span class="var">beanName.propertyName</span>”变为“<span class="var">propertyName</span>”），
 *  		然后，与上面提到的一样，根据目标类型直接返回这个新映射表或者返回转换后的对象。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">param.yourKey</span> <br/>
 *  		同上。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request</span> <br/>
 *  		请求HttpServletRequest对象。框架本身并没有提供它的转换器，如果目标类型不是“<span class="var">HttpServletRequest</span>”，
 *  		那么你需要为它的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletRequest</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request.yourKey</span> <br/>
 *  		请求属性中的“<span class="var">yourKey</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session</span> <br/>
 *  		会话HttpSession对象。框架本身并没有提供它的转换器，如果目标类型不是“<span class="var">HttpSession</span>”，
 *  		那么你需要为它的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpSession</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session.yourKey</span> <br/>
 *  		会话属性中的“<span class="var">yourKey</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application</span> <br/>
 *  		应用ServletContext对象。如果目标类型不是“<span class="var">ServletContext</span>”，
 *  		那么你需要为它的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.ServletContext</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application.yourKey</span> <br/>
 *  		应用属性中的“<span class="var">yourKey</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">response</span> <br/>
 *  		回应HttpServletResponse对象。如果目标类型不是“<span class="var">HttpServletResponse</span>”，
 *  		那么你需要为它的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletResponse</span>”到目标类型辅助{@linkplain Converter 转换器}。
 *  	</li>
 *   </ul>
 *  </li>
 * </ul>
 * <br>
 * 实际上，你在配置文件中定义的&lt;arg&gt;关键字的格式就是由它决定的。
 * @author earthAngry@gmail.com
 * @date 2010-7-19
 */
public class WebObjectSource extends ConvertableObjectSource
{
	private static Log log = LogFactory.getLog(WebObjectSource.class);
	
	
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
			data = getRequest();
		else if(objectType == HttpServletResponse.class)
			data = getResponse();
		else if(objectType == ServletContext.class)
			data = getApplication();
		else if(objectType == HttpSession.class)
			data = getRequest().getSession();
		else
		{
			//WEB环境下只有字符串主键
			String strKey = (String)key;
			
			if(strKey==null || strKey.length()==0)
				throw new ObjectSourceException("[key] must not be empty.");
			
			int accessorIdx=strKey.indexOf(WebConstants.ACCESSOR);
			if(accessorIdx==0 || accessorIdx >= strKey.length()-1)
				throw new ObjectSourceException("invalid key '"+strKey+"'");
			
			String scope = accessorIdx > 0 ? strKey.substring(0, accessorIdx) : null;
			String subKey = accessorIdx > 0 ? strKey.substring(accessorIdx+1) : strKey;
			
			if(scope == null)
			{
				if(WebConstants.Scope.PARAM.equals(subKey))
					data=convertParamMap(getRequest().getParameterMap(), null, objectType);
				else if(WebConstants.Scope.REQUEST.equals(subKey))
					data=convertServletObject(getRequest(), objectType);
				else if(WebConstants.Scope.SESSION.equals(subKey))
					data=convertServletObject(getRequest().getSession(), objectType);
				else if(WebConstants.Scope.APPLICATION.equals(subKey))
					data=convertServletObject(getApplication(), objectType);
				else if(WebConstants.Scope.RESPONSE.equals(subKey))
					data=convertServletObject(getResponse(), objectType);
				else
					data=getWithUnknownScope(scope, subKey, objectType);
			}
			else
			{
				if(WebConstants.Scope.PARAM.equals(scope))
					data=convertParamMap(getRequest().getParameterMap(), subKey, objectType);
				else if(WebConstants.Scope.REQUEST.equals(scope))
					data=getGenericConverter().convert(getRequest().getAttribute(subKey), objectType);
				else if(WebConstants.Scope.SESSION.equals(scope))
					data=getGenericConverter().convert(getRequest().getSession().getAttribute(subKey), objectType);
				else if(WebConstants.Scope.APPLICATION.equals(scope))
					data=getGenericConverter().convert(getApplication().getAttribute(subKey), objectType);
				else if(WebConstants.Scope.RESPONSE.equals(scope))
				{
					if(subKey != null)
						throw new ObjectSourceException("key '"+key+"' is invalid, you can not get data from '"+WebConstants.Scope.RESPONSE+"' scope");
					
					data=convertServletObject(getResponse(), objectType);
				}
				else
					data=getWithUnknownScope(scope, subKey, objectType);
			}
			
			if(log.isDebugEnabled())
				log.debug("get '"+data+"' from scope '"+scope+"' with key '"+subKey+"'");
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
		if(accessorIdx==0 || accessorIdx >= strKey.length()-1)
			throw new ObjectSourceException("invalid key '"+strKey+"'");
		
		String scope = accessorIdx > 0 ? strKey.substring(0, accessorIdx) : null;
		String subKey = accessorIdx > 0 ? strKey.substring(accessorIdx+1) : strKey;
		
		if(WebConstants.Scope.PARAM.equals(scope))
			throw new ObjectSourceException("'"+key+"' is invalid, you can not save object into '"+WebConstants.Scope.PARAM+"'");
		else if(WebConstants.Scope.REQUEST.equals(scope))
			getRequest().setAttribute(subKey, obj);
		else if(WebConstants.Scope.SESSION.equals(scope))
			getRequest().getSession().setAttribute(subKey, obj);
		else if(WebConstants.Scope.APPLICATION.equals(scope))
			getApplication().setAttribute(subKey, obj);
		else if(WebConstants.Scope.RESPONSE.equals(scope))
			throw new ObjectSourceException("'"+key+"' is invalid, you can not save object into '"+WebConstants.Scope.RESPONSE+"'");
		else
			setWithUnknownScope(scope, subKey, obj);
		
		if(log.isDebugEnabled())
			log.debug("save '"+obj+"' into '"+scope+"' with key '"+subKey+"'");
	}
	
	/**
	 * 从默认无法识别的作用域取得对象（作用域为null或者未知）
	 * @param scope 作用域
	 * @param keyInScope 该作用域下的关键字，它不会为null
	 * @param objectType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object getWithUnknownScope(String scope, String keyInScope, Class<?> objectType)
	{
		if(scope != null)
			throw new ObjectSourceException("scope '"+scope+"' in key '"+(scope+WebConstants.ACCESSOR+keyInScope)+"' is invalid, it must be one of '"+WebConstants.Scope.PARAM+"', '"+WebConstants.Scope.REQUEST+"', '"+WebConstants.Scope.SESSION+"', '"+WebConstants.Scope.APPLICATION+"', '"+WebConstants.Scope.RESPONSE+"'");
		
		return convertParamMap(getRequest().getParameterMap(), keyInScope, objectType);
	}
	
	/**
	 * 将对象存储到默认无法识别的作用域中（作用域为null或者未知）
	 * @param scope 作用域
	 * @param keyInScope 该作用域下的关键字，它不会为null
	 * @param obj
	 */
	protected void setWithUnknownScope(String scope, String keyInScope, Object obj)
	{
		if(scope != null)
			throw new ObjectSourceException("scope '"+scope+"' in key '"+(scope+WebConstants.ACCESSOR+keyInScope)+"' is invalid, it must be one of '"+WebConstants.Scope.PARAM+"', '"+WebConstants.Scope.REQUEST+"', '"+WebConstants.Scope.SESSION+"', '"+WebConstants.Scope.APPLICATION+"', '"+WebConstants.Scope.RESPONSE+"'");
		
		getRequest().setAttribute(keyInScope, obj);
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