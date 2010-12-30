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
			
			if(strKey==null)
				throw new ObjectSourceException("[key] must not be null.");
			
			String[] scopeSplit=splitByFirstDot(strKey);
			
			String scope = scopeSplit[0];
			String keyInScope = scopeSplit[1];
			
			if(scope == null)
			{
				if(WebConstants.Scope.PARAM.equals(keyInScope))
					data=getFromMap(getRequest().getParameterMap(), null, objectType);
				else if(WebConstants.Scope.REQUEST.equals(keyInScope))
					data=convertServletObject(getRequest(), objectType);
				else if(WebConstants.Scope.SESSION.equals(keyInScope))
					data=convertServletObject(getRequest().getSession(), objectType);
				else if(WebConstants.Scope.APPLICATION.equals(keyInScope))
					data=convertServletObject(getApplication(), objectType);
				else if(WebConstants.Scope.RESPONSE.equals(keyInScope))
					data=convertServletObject(getResponse(), objectType);
				else
					data=getWithUnknownScope(scope, keyInScope, objectType);
			}
			else
			{
				if(WebConstants.Scope.PARAM.equals(scope))
					data=getFromMap(getRequest().getParameterMap(), keyInScope, objectType);
				else if(WebConstants.Scope.REQUEST.equals(scope))
					data=getWithKeyExpression(getRequest(), keyInScope, objectType);
				else if(WebConstants.Scope.SESSION.equals(scope))
					data=getWithKeyExpression(getRequest().getSession(), keyInScope, objectType);
				else if(WebConstants.Scope.APPLICATION.equals(scope))
					data=getWithKeyExpression(getApplication(), keyInScope, objectType);
				else if(WebConstants.Scope.RESPONSE.equals(scope))
				{
					if(keyInScope != null)
						throw new ObjectSourceException("key '"+key+"' is invalid, you can not get data from '"+WebConstants.Scope.RESPONSE+"' scope");
				}
				else
					data=getWithUnknownScope(scope, keyInScope, objectType);
			}
			
			if(log.isDebugEnabled())
				log.debug("get '"+data+"' from scope '"+scope+"' with key '"+keyInScope+"'");
		}
		
		return data;
	}
	
	@Override
	public void set(Serializable key, Object obj)
	{
		String strKey = (String)key;
		
		//主键不能为空
		if(strKey == null)
			throw new IllegalArgumentException("[key] must not be null");
		
		String[] scopeSplit=splitByFirstDot(strKey);
		
		String scope = scopeSplit[0];
		String keyInScope = scopeSplit[1];
		
		if(WebConstants.Scope.PARAM.equals(scope))
			throw new ObjectSourceException("'"+key+"' is invalid, you can not save object into '"+WebConstants.Scope.PARAM+"'");
		else if(WebConstants.Scope.REQUEST.equals(scope))
			setWithKeyExpression(getRequest(), keyInScope, obj);
		else if(WebConstants.Scope.SESSION.equals(scope))
			setWithKeyExpression(getRequest().getSession(), keyInScope, obj);
		else if(WebConstants.Scope.APPLICATION.equals(scope))
			setWithKeyExpression(getApplication(), keyInScope, obj);
		else if(WebConstants.Scope.RESPONSE.equals(scope))
			throw new ObjectSourceException("'"+key+"' is invalid, you can not save object into '"+WebConstants.Scope.RESPONSE+"'");
		else
			setWithUnknownScope(scope, keyInScope, obj);
		
		if(log.isDebugEnabled())
			log.debug("save '"+obj+"' into '"+scope+"' with key '"+keyInScope+"'");
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
		
		return getFromMap(getRequest().getParameterMap(), keyInScope, objectType);
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
		
		setWithKeyExpression(getRequest(), keyInScope, obj);
	}
	
	/**
	 * 设置属性值到servlet对象
	 * @param servletObj
	 * @param keyExpression
	 * @param obj
	 * @date 2010-12-30
	 */
	protected void setWithKeyExpression(Object servletObj, String keyExpression, Object obj)
	{
		String[] objKeyWithProperty=splitByFirstDot(keyExpression);
		
		//只有包含了'.'字符，并且对象存在时，才按照属性表达式方式，否则直接按照关键字方式，下面两个都是相同的逻辑
		if(objKeyWithProperty[0] != null)
		{
			Object data=getServletObjAttribute(servletObj, objKeyWithProperty[0]);
			if(data != null)
				getGenericConverter().setProperty(data, objKeyWithProperty[1], obj);
			else
				setServletObjAttribute(servletObj, keyExpression, obj);
		}
		else
			setServletObjAttribute(servletObj, keyExpression, obj);
	}
	
	/**
	 * 从servlet对象取得属性值。
	 * @param servletObj
	 * @param keyExpression
	 * @param objectType
	 * @return
	 * @date 2010-12-30
	 */
	protected Object getWithKeyExpression(Object servletObj, String keyExpression, Class<?> objectType)
	{
		Object data=getServletObjAttribute(servletObj, keyExpression);
		if(data != null)
			data=getGenericConverter().convert(data, objectType);
		else
		{
			String[] objKeyWithProperty=splitByFirstDot(keyExpression);
			if(objKeyWithProperty[0]!=null && objKeyWithProperty[1]!=null)
			{
				data=getServletObjAttribute(servletObj, objKeyWithProperty[0]);
				if(data != null)
					data=getGenericConverter().getProperty(data, objKeyWithProperty[1], objectType);
			}
		}
		
		return data;
	}
	
	/**
	 * 设置属性值到servlet对象
	 * @param servletObj
	 * @param key
	 * @param value
	 * @date 2010-12-30
	 */
	protected void setServletObjAttribute(Object servletObj, String key, Object value)
	{
		if(servletObj instanceof HttpServletRequest)
			((HttpServletRequest)servletObj).setAttribute(key, value);
		else if(servletObj instanceof HttpSession)
			((HttpSession)servletObj).setAttribute(key, value);
		else if(servletObj instanceof ServletContext)
			((ServletContext)servletObj).setAttribute(key, value);
		else
			throw new IllegalArgumentException("unknown servlet object '"+servletObj+"'");
	}
	
	/**
	 * 从servlet对象取得属性值
	 * @param servletObj
	 * @param key
	 * @param value
	 * @date 2010-12-30
	 */
	protected Object getServletObjAttribute(Object servletObj, String key)
	{
		if(servletObj instanceof HttpServletRequest)
			return ((HttpServletRequest)servletObj).getAttribute(key);
		else if(servletObj instanceof HttpSession)
			return ((HttpSession)servletObj).getAttribute(key);
		else if(servletObj instanceof ServletContext)
			return ((ServletContext)servletObj).getAttribute(key);
		else
			throw new IllegalArgumentException("unknown servlet object '"+servletObj+"'");
	}
	
	/**
	 * 将从映射表取得对象。<br>
	 * 如果<code>keyFilter</code>是一个明确的关键字（映射表中有该关键字的值），它将直接根据该关键字的值来转换；<br>
	 * 如果<code>keyFilter</code>是<code>null</code>，那么它将使用原始的请求参数映射表来进行转换；<br>
	 * 否则，它会根据<code>keyFilter</code>来对参数映射表进行过滤，产生一个新的映射表（它的关键字将会被替换为原始关键字的“<code>[keyFilter]</code>.”之后的部分，比如由“<code>beanName.propertyName</code>”变为“<code>propertyName</code>”），
	 * 然后使用它进行转换。
	 * 
	 * @param rawValueMap 原始映射表
	 * @param keyFilter 主键筛选器，只有以此筛选器开头的Map关键字才会被转换，如果为null，则表明不做筛选
	 * @param targetType 目标类型
	 * 
	 * @return
	 */
	protected Object getFromMap(Map<String,Object> rawValueMap, String keyFilter, Class<?> targetType)
	{
		GenericConverter genericConverter=getGenericConverter();
		
		if(keyFilter == null)
			return genericConverter.convert(rawValueMap, targetType);
		
		//明确的KEY，直接根据值转换
		Object explicit = rawValueMap.get(keyFilter);
		if(explicit != null)
			return genericConverter.convert(explicit, targetType);
		else
		{
			String keyPrefix = keyFilter+WebConstants.ACCESSOR;
			
			Map<String,Object> filtered = new HashMap<String, Object>();
			Set<String> keys=rawValueMap.keySet();
			for(String k : keys)
			{
				if(k.startsWith(keyPrefix))
					filtered.put(k.substring(keyPrefix.length()), rawValueMap.get(k));
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
	
	/**
	 * 将字符串从第一个'.'位置拆分为两部分，如果不包含'.'，则第一个元素为<code>null</code>，第二个元素为原字符串。
	 * @param str
	 * @return
	 * @date 2010-12-30
	 */
	private String[] splitByFirstDot(String str)
	{
		String[] re=new String[2];
		int idx=str.indexOf(WebConstants.ACCESSOR);
		
		if(idx<=0 || idx==str.length()-1)
			re[1]=str;
		else
		{
			re[0]=str.substring(0,idx);
			re[1]=str.substring(idx+1);
		}
		
		return re;
	}
}