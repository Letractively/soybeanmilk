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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.web.WebConstants;

/**
 * 用于WEB应用的对象源，它的实例的生命周期与一次请求的生命周期相同。
 * <br>
 * 传递给它的关键字会被理解为由两个部分组成：“[scope].[keyInScope]”，其中
 * “[scope]”表示作用域，“[keyInScope]”则是真正的该作用域下的关键字。
 * <br>
 * 它目前所支持的关键字格式及其说明如下：
 * <ul>
 * 	<li>
 *   set
 *   <ul>
 *  	<li>
 *  		<span class="tagValue">keyInScope</span> <br/>
 *  		结果将以“<span class="var">keyInScope</span>”关键字被保存到“<span class="var">request</span>”作用域中
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request.keyInScope</span> <br/>
 *  		同上
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session.keyInScope</span> <br/>
 *  		结果将以“<span class="var">keyInScope</span>”关键字被保存到“<span class="var">session</span>”作用域中
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application.keyInScope</span> <br/>
 *  		结果将以“<span class="var">keyInScope</span>”关键字被保存到“<span class="var">application</span>”作用域中
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
 *  		<span class="tagValue">keyInScope</span> <br/>
 *  		请求参数映射表中以“<span class="var">keyInScope</span>”开头的请求参数。
 *  		如果这个参数有明确的值，它将对这个值进行类型转换（需要的话），然后返回转换后的对象；否则，就根据“<span class="var">keyInScope</span>”来对参数映射表进行过滤，
 *  		产生一个新的映射表（它的主键是原始关键字“<span class="var">keyInScope.</span>”之后的部分，比如由“<span class="var">beanName.propertyName</span>”变为“<span class="var">propertyName</span>”），
 *  		然后，与上面提到的一样，根据目标类型直接返回这个新映射表或者返回转换后的对象。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">param.keyInScope</span> <br/>
 *  		同上。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request</span> <br/>
 *  		请求HttpServletRequest对象。框架本身并没有提供它的转换器，如果目标类型不是“<span class="var">HttpServletRequest</span>”，
 *  		那么你需要为此类的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletRequest</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request.keyInScope</span> <br/>
 *  		请求属性中的“<span class="var">keyInScope</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session</span> <br/>
 *  		会话HttpSession对象。框架本身并没有提供它的转换器，如果目标类型不是“<span class="var">HttpSession</span>”，
 *  		那么你需要为此类的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpSession</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session.keyInScope</span> <br/>
 *  		会话属性中的“<span class="var">keyInScope</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application</span> <br/>
 *  		应用ServletContext对象。如果目标类型不是“<span class="var">ServletContext</span>”，
 *  		那么你需要为此类的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.ServletContext</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application.keyInScope</span> <br/>
 *  		应用属性中的“<span class="var">keyInScope</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">response</span> <br/>
 *  		回应HttpServletResponse对象。如果目标类型不是“<span class="var">HttpServletResponse</span>”，
 *  		那么你需要为此类的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletResponse</span>”到目标类型辅助{@linkplain Converter 转换器}。
 *  	</li>
 *   </ul>
 *  </li>
 * </ul>
 * <br>
 * 另外，如果“request”、“session”、“application”作用域的“[keyInScope]”中包含访问符“.”，比如“request.yourBean.property”，
 * 它会认为你是想要取得或设置“request”作用域内“yourBean”对象的“property”属性，并按此处理（如果“yourBean”对象存在的话）。
 * <br>
 * 实际上，你在配置文件中定义的&lt;arg&gt;关键字的格式就是由这个类决定的。
 * @author earthAngry@gmail.com
 * @date 2010-7-19
 */
public class WebObjectSource extends ConvertableObjectSource
{
	private static Log log = LogFactory.getLog(WebObjectSource.class);
	
	private static final char ACCESSOR=WebConstants.ACCESSOR;
	
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
	public Object get(Serializable key, Type expectType)
	{
		Object data = null;
		if(HttpServletRequest.class.equals(expectType))
			data = getRequest();
		else if(HttpServletResponse.class.equals(expectType))
			data = getResponse();
		else if(ServletContext.class.equals(expectType))
			data = getApplication();
		else if(HttpSession.class.equals(expectType))
			data = getRequest().getSession();
		else
		{
			//WEB环境下只有字符串主键
			String strKey = (String)key;
			
			if(strKey==null)
				throw new ObjectSourceException("[key] must not be null.");
			
			String[] scopeSplit=splitByFirstAccessor(strKey);
			
			String scope = scopeSplit[0];
			String keyInScope = scopeSplit[1];
			
			if(scope == null)
			{
				if(WebConstants.Scope.PARAM.equalsIgnoreCase(keyInScope))
					data=convertFromMap(getRequest().getParameterMap(), null, expectType);
				else if(WebConstants.Scope.REQUEST.equalsIgnoreCase(keyInScope))
					data=convertServletObject(getRequest(), expectType);
				else if(WebConstants.Scope.SESSION.equalsIgnoreCase(keyInScope))
					data=convertServletObject(getRequest().getSession(), expectType);
				else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(keyInScope))
					data=convertServletObject(getApplication(), expectType);
				else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(keyInScope))
					data=convertServletObject(getResponse(), expectType);
				else
					data=getWithUnknownScope(scope, keyInScope, expectType);
			}
			else
			{
				if(WebConstants.Scope.PARAM.equalsIgnoreCase(scope))
					data=convertFromMap(getRequest().getParameterMap(), keyInScope, expectType);
				else if(WebConstants.Scope.REQUEST.equalsIgnoreCase(scope))
					data=getAttributeByKeyExpression(getRequest(), keyInScope, expectType);
				else if(WebConstants.Scope.SESSION.equalsIgnoreCase(scope))
					data=getAttributeByKeyExpression(getRequest().getSession(), keyInScope, expectType);
				else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(scope))
					data=getAttributeByKeyExpression(getApplication(), keyInScope, expectType);
				else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(scope))
					throw new ObjectSourceException("key '"+key+"' is not valid, you can not get data from '"+WebConstants.Scope.RESPONSE+"' scope");
				else
					data=getWithUnknownScope(scope, keyInScope, expectType);
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
		
		String[] scopeSplit=splitByFirstAccessor(strKey);
		
		String scope = scopeSplit[0];
		String keyInScope = scopeSplit[1];
		
		if(WebConstants.Scope.PARAM.equalsIgnoreCase(scope))
			throw new ObjectSourceException("'"+key+"' is invalid, you can not save object into '"+WebConstants.Scope.PARAM+"'");
		else if(WebConstants.Scope.REQUEST.equalsIgnoreCase(scope))
			setAttributeByKeyExpression(getRequest(), keyInScope, obj);
		else if(WebConstants.Scope.SESSION.equalsIgnoreCase(scope))
			setAttributeByKeyExpression(getRequest().getSession(), keyInScope, obj);
		else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(scope))
			setAttributeByKeyExpression(getApplication(), keyInScope, obj);
		else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(scope))
			throw new ObjectSourceException("'"+key+"' is not valid, you can not save object into '"+WebConstants.Scope.RESPONSE+"'");
		else
			setWithUnknownScope(scope, keyInScope, obj);
		
		if(log.isDebugEnabled())
			log.debug("save '"+obj+"' into '"+scope+"' with key '"+keyInScope+"'");
	}
	
	/**
	 * 从无法识别的作用域取得对象
	 * @param scope 作用域，可能为<code>null</code>
	 * @param keyInScope 该作用域下的关键字
	 * @param objectType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object getWithUnknownScope(String scope, String keyInScope, Type objectType)
	{
		//作用域无法识别，则认为它是param作用域里关键字的一部分
		if(scope != null)
			keyInScope=scope+ACCESSOR+keyInScope;
		
		return convertFromMap(getRequest().getParameterMap(), keyInScope, objectType);
	}
	
	/**
	 * 将对象存储到无法识别的作用域中
	 * @param scope 作用域，可能为<code>null</code>
	 * @param keyInScope 该作用域下的关键字
	 * @param obj
	 */
	protected void setWithUnknownScope(String scope, String keyInScope, Object obj)
	{
		//作用域无法识别，则认为它是request作用域里关键字的一部分
		if(scope != null)
			keyInScope=scope+ACCESSOR+keyInScope;
		
		setAttributeByKeyExpression(getRequest(), keyInScope, obj);
	}
	
	/**
	 * 将对象保存到servlet对象作用域内，它支持设置作用域内对象的属性。
	 * @param servletObj
	 * @param keyExpression 关键字表达式，比如“yourBean”、“yourBean.property”
	 * @param obj
	 * @date 2010-12-30
	 */
	protected void setAttributeByKeyExpression(Object servletObj, String keyExpression, Object obj)
	{
		String[] objKeyWithProperty=splitByFirstAccessor(keyExpression);
		
		//只有包含了'.'字符，并且对象存在时，才按照属性表达式方式，否则直接按照关键字方式
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
	 * 从servlet对象作用域内取得对象，它支持取得作用域内对象的属性。
	 * @param servletObj
	 * @param keyExpression 关键字表达式，比如“yourBean”、“yourBean.property”
	 * @param objectType
	 * @return
	 * @date 2010-12-30
	 */
	protected Object getAttributeByKeyExpression(Object servletObj, String keyExpression, Type objectType)
	{
		String[] objKeyWithProperty=splitByFirstAccessor(keyExpression);
		
		//只有包含了'.'字符，并且对象存在时，才按照属性表达式方式，否则直接按照关键字方式
		if(objKeyWithProperty[0] != null)
		{
			Object data=getServletObjAttribute(servletObj, objKeyWithProperty[0]);
			if(data != null)
				return getGenericConverter().getProperty(data, objKeyWithProperty[1], objectType);
			else
				return getGenericConverter().convert(getServletObjAttribute(servletObj, keyExpression), objectType);
		}
		else
			return getGenericConverter().convert(getServletObjAttribute(servletObj, keyExpression), objectType);
	}
	
	/**
	 * 将对象保存到servlet对象作用域内。
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
			throw new ObjectSourceException("unknown servlet object '"+servletObj+"'");
	}
	
	/**
	 * 从servlet对象作用域内取得对象。
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
			throw new ObjectSourceException("unknown servlet object '"+servletObj+"'");
	}
	
	/**
	 * 从映射表取得对象。<br>
	 * 如果<code>keyFilter</code>是一个明确的关键字（映射表中有该关键字的值），它将直接根据该关键字的值来转换；<br>
	 * 如果<code>keyFilter</code>是<code>null</code>，那么它将使用原始的请求参数映射表来进行转换；<br>
	 * 否则，它会根据<code>keyFilter</code>来对参数映射表进行过滤，产生一个新的映射表（它的关键字将会被替换为原始关键字的“<code>[keyFilter]</code>.”之后的部分，比如由“<code>beanName.propertyName</code>”变为“<code>propertyName</code>”），
	 * 然后使用它进行转换。
	 * 
	 * @param valueMap 原始映射表
	 * @param keyFilter 主键筛选器，只有以此筛选器开头的Map关键字才会被转换，如果为null，则表明不做筛选
	 * @param targetType 目标类型
	 * 
	 * @return
	 */
	protected Object convertFromMap(Map<String,Object> valueMap, String keyFilter, Type targetType)
	{
		Object src=null;
		
		//没有过滤器
		if(keyFilter == null)
			src=valueMap;
		else
		{
			//有确切的值
			Object explicit = valueMap.get(keyFilter);
			if(explicit != null)
				src=explicit;
			else
			{
				String keyPrefix = keyFilter+ACCESSOR;
				
				Map<String,Object> filtered = new HashMap<String, Object>();
				Set<String> keys=valueMap.keySet();
				for(String k : keys)
				{
					if(k.startsWith(keyPrefix))
						filtered.put(k.substring(keyPrefix.length()), valueMap.get(k));
				}
				
				src=filtered;
			}
		}
		
		return getGenericConverter().convert(src, targetType);
	}
	
	/**
	 * 转换servlet对象到目标类型的对象
	 * @param obj servlet对象，包括：HttpServletRequest、HttpSession、HttpServletResponse、ServletContext
	 * @param targetType
	 * @return
	 */
	protected Object convertServletObject(Object obj, Type targetType)
	{
		if(targetType == null || SoybeanMilkUtils.isInstanceOf(obj, targetType))
			return obj;
		
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
		
		converter=getGenericConverter().getConverter(sourceClass, targetType);
		
		if(converter == null)
			throw new ObjectSourceException("no Converter defined for converting '"+sourceClass.getName()+"' to '"+targetType+"'");
		
		return converter.convert(obj, targetType);
	}
	
	/**
	 * 将字符串从第一个'.'位置拆分为两部分，如果不包含'.'，则第一个元素为<code>null</code>，第二个元素为原字符串。
	 * @param str
	 * @return
	 * @date 2010-12-30
	 */
	protected String[] splitByFirstAccessor(String str)
	{
		String[] re=new String[2];
		int idx=str.indexOf(ACCESSOR);
		
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