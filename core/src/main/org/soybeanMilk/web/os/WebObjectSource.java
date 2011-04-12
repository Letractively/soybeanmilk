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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

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
import org.soybeanMilk.web.bean.FilterAwareMap;

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
 *  		那么你需要为此对象源的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletRequest</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">request.keyInScope</span> <br/>
 *  		请求属性中的“<span class="var">keyInScope</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session</span> <br/>
 *  		会话HttpSession对象。框架本身并没有提供它的转换器，如果目标类型不是“<span class="var">HttpSession</span>”，
 *  		那么你需要为此对象源的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpSession</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">session.keyInScope</span> <br/>
 *  		会话属性中的“<span class="var">keyInScope</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application</span> <br/>
 *  		应用ServletContext对象。如果目标类型不是“<span class="var">ServletContext</span>”，
 *  		那么你需要为此对象源的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.ServletContext</span>”到目标类型的辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">application.keyInScope</span> <br/>
 *  		应用属性中的“<span class="var">keyInScope</span>”关键字对应的对象。如果目标类型与此对象不一致，框架将尝试执行类型转换。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">response</span> <br/>
 *  		回应HttpServletResponse对象。如果目标类型不是“<span class="var">HttpServletResponse</span>”，
 *  		那么你需要为此对象源的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletResponse</span>”到目标类型辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">objectSource</span> <br/>
 *  		当前的{@linkplain WebObjectSource}对象，你可以使用它来获取所有servlet对象。如果目标类型不是“<span class="var">WebObjectSource</span>”，
 *  		那么你需要为此对象源的{@linkplain GenericConverter 通用转换器}添加“<span class="var">org.soybeanMilk.web.os.WebObjectSource</span>”到目标类型辅助{@linkplain Converter 转换器}。
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
	
	/**
	 * 获得当前请求对象。
	 * @return
	 * @date 2010-7-19
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	/**
	 * 获得当前回应对象。
	 * @return
	 * @date 2010-7-19
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * 获得当前应用对象。
	 * @return
	 * @date 2010-7-19
	 */
	public ServletContext getApplication() {
		return application;
	}
	public void setApplication(ServletContext application) {
		this.application = application;
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public Object get(Serializable key, Type expectType)
	{
		Object data = null;
		
		//WEB环境下只有字符串主键
		String strKey = (String)key;
		
		if(strKey==null)
			throw new ObjectSourceException("[key] must not be null.");
		
		String[] scopedKey=splitByFirstAccessor(strKey);
		
		String scope = scopedKey[0];
		String subKey = scopedKey.length==1 ? null : scopedKey[1];
		
		if(WebConstants.WebObjectSourceScope.PARAM.equalsIgnoreCase(scope))
		{
			data=convertParameterMap(getRequest().getParameterMap(), subKey, expectType);
		}
		else if(WebConstants.WebObjectSourceScope.REQUEST.equalsIgnoreCase(scope))
		{
			data=convertServletObjectAttribute(getRequest(), subKey, expectType);
		}
		else if(WebConstants.WebObjectSourceScope.SESSION.equalsIgnoreCase(scope))
		{
			data=convertServletObjectAttribute(getRequest().getSession(), subKey, expectType);
		}
		else if(WebConstants.WebObjectSourceScope.APPLICATION.equalsIgnoreCase(scope))
		{
			data=convertServletObjectAttribute(getApplication(), subKey, expectType);
		}
		else if(WebConstants.WebObjectSourceScope.RESPONSE.equalsIgnoreCase(scope))
		{
			data=convertServletObjectAttribute(getResponse(), subKey, expectType);
		}
		else if(WebConstants.WebObjectSourceScope.OBJECT_SOURCE.equalsIgnoreCase(scope))
		{
			if(subKey != null)
				throw new ObjectSourceException("invalide key '"+strKey+"', get object from '"+WebConstants.WebObjectSourceScope.OBJECT_SOURCE+"' scope is not supported");
			
			if(expectType==null || SoybeanMilkUtils.isInstanceOf(this, expectType))
				data=this;
			else
			{
				Converter cvt=getGenericConverter().getConverter(WebObjectSource.class, expectType);
				if(cvt == null)
					throw new ObjectSourceException("no Converter defined for converting '"+WebObjectSource.class+"' to '"+expectType+"'");
				
				data=cvt.convert(this, expectType);
			}
		}
		else
			data=getObjectForUnknownKey(strKey, expectType);
		
		if(log.isDebugEnabled())
			log.debug("get '"+data+"' from scope '"+scope+"' with key '"+subKey+"'");
		
		return data;
	}
	
	//@Override
	public void set(Serializable key, Object obj)
	{
		String strKey = (String)key;
		
		//主键不能为空
		if(strKey == null)
			throw new IllegalArgumentException("[key] must not be null");
		
		String[] scopeSplit=splitByFirstAccessor(strKey);
		
		String scope = scopeSplit[0];
		String subKey = scopeSplit.length == 1 ? null : scopeSplit[1];
		
		if(WebConstants.WebObjectSourceScope.REQUEST.equalsIgnoreCase(scope))
		{
			setAttributeByKeyExpression(getRequest(), subKey, obj);
		}
		else if(WebConstants.WebObjectSourceScope.SESSION.equalsIgnoreCase(scope))
		{
			setAttributeByKeyExpression(getRequest().getSession(), subKey, obj);
		}
		else if(WebConstants.WebObjectSourceScope.APPLICATION.equalsIgnoreCase(scope))
		{
			setAttributeByKeyExpression(getApplication(), subKey, obj);
		}
		else if(WebConstants.WebObjectSourceScope.PARAM.equalsIgnoreCase(scope))
		{
			throw new ObjectSourceException("'"+key+"' is invalid, you can not save object into '"+WebConstants.WebObjectSourceScope.PARAM+"' scope");
		}
		else if(WebConstants.WebObjectSourceScope.RESPONSE.equalsIgnoreCase(scope))
		{
			throw new ObjectSourceException("'"+key+"' is not valid, you can not save object into '"+WebConstants.WebObjectSourceScope.RESPONSE+"' scope");
		}
		else
			setObjectForUnknownKey(strKey, obj);
		
		if(log.isDebugEnabled())
			log.debug("save '"+obj+"' into scope '"+scope+"' with key '"+subKey+"'");
	}
	
	/**
	 * 取得默认无法识别的关键字所对应的对象。
	 * @param key
	 * @param expectType
	 * @return
	 * @date 2011-2-22
	 */
	@SuppressWarnings("unchecked")
	protected Object getObjectForUnknownKey(String key, Type expectType)
	{
		if(!containAccessor(key))
			return convertParameterMap(getRequest().getParameterMap(), key, expectType);
		else
			throw new ObjectSourceException("key '"+key+"' is invalid, the 'get' method can not recognize it");
	}
	
	/**
	 * 保存默认无法识别关键字所对应的对象。
	 * @param key 关键字
	 * @param obj
	 */
	protected void setObjectForUnknownKey(String key, Object obj)
	{
		if(!containAccessor(key))
			setAttributeByKeyExpression(getRequest(), key, obj);
		else
			throw new ObjectSourceException("key '"+key+"' is invalid, the 'set' method can not recognize it");
	}
	
	/**
	 * 转换Servlet对象的属性对象，如果<code>keyExpression</code>为空，Servlet对象本身将被用于转换。
	 * @param servletObj
	 * @param keyExpression 关键字表达式，比如“myobj”、“myobj.myProperty”
	 * @param targetType
	 * @return
	 * @date 2011-2-22
	 */
	protected Object convertServletObjectAttribute(Object servletObj, String keyExpression, Type targetType)
	{
		Object re=null;
		
		if(keyExpression==null || keyExpression.length()==0)
		{
			if(targetType==null || SoybeanMilkUtils.isInstanceOf(servletObj, targetType))
				re=servletObj;
			else
			{
				Type srcType=getServletObjectType(servletObj);
				
				Converter cvt=getGenericConverter().getConverter(srcType, targetType);
				if(cvt == null)
					throw new ObjectSourceException("no Converter defined for converting '"+srcType+"' to '"+targetType+"'");
				
				re=cvt.convert(servletObj, targetType);
			}
		}
		else
		{
			String[] keyWithProperty=splitByFirstAccessor(keyExpression);
			re=getServletObjAttribute(servletObj, keyWithProperty[0]);
			
			//关键字表达式不包访问符
			if(keyWithProperty.length == 1)
				re=getGenericConverter().convert(re, targetType);
			else
			{
				//包含访问符，并且对象存在时，才按照属性表达式方式，否则直接按照关键字方式
				if(re == null)
					re=getGenericConverter().convert(getServletObjAttribute(servletObj, keyExpression), targetType);
				else
					re=getGenericConverter().getProperty(re, keyWithProperty[1], targetType);
			}
		}
		
		return re;
	}
	
	/**
	 * 将对象保存到servlet对象作用域内。
	 * @param servletObj
	 * @param keyExpression 关键字表达式，比如“yourBean”、“yourBean.property”
	 * @param obj
	 * @date 2010-12-30
	 */
	protected void setAttributeByKeyExpression(Object servletObj, String keyExpression, Object obj)
	{
		String[] objKeyWithProperty=splitByFirstAccessor(keyExpression);
		
		//只有包含了访问符，并且对象存在时，才按照属性表达式方式，否则直接按照关键字方式
		if(objKeyWithProperty.length == 2)
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
			throw new ObjectSourceException("can not set attribute to servlet object '"+servletObj+"'");
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
			throw new ObjectSourceException("can not get attribute from servlet object '"+servletObj+"'");
	}
	
	/**
	 * 转换请求参数映射表。<br>
	 * 如果<code>keyFilter</code>是一个明确的关键字（映射表中有该关键字的值），它将直接根据该关键字的值来转换；<br>
	 * 如果<code>keyFilter</code>是<code>null</code>，那么它将使用原始的请求参数映射表来进行转换；<br>
	 * 否则，它会根据<code>keyFilter</code>来对参数映射表进行过滤，产生一个新的映射表（它的关键字将会被替换为原始关键字的“<code>[keyFilter]</code>.”之后的部分，比如由“<code>beanName.propertyName</code>”变为“<code>propertyName</code>”），
	 * 然后使用它进行转换。
	 * 
	 * @param paramMap 请求参数映射表
	 * @param keyFilter 筛选器，只有以此筛选器开头的Map关键字才会被转换，如果为null，则表明不做筛选
	 * @param targetType 目标类型
	 * 
	 * @return
	 */
	protected Object convertParameterMap(Map<String, ?> paramMap, String keyFilter, Type targetType)
	{
		FilterAwareMap<String, ?> src=null;
		
		//没有过滤器
		if(keyFilter==null || keyFilter.length()==0)
			src=FilterAwareMap.wrap(paramMap);
		else
		{
			boolean explicitValue;
			//有确切的值或者仅对应一个参数
			if(paramMap.get(keyFilter)!=null || isSingleParameterKey(targetType))
				explicitValue=true;
			else
			{
				explicitValue=false;
				keyFilter=keyFilter+ACCESSOR;
			}
			
			src=FilterAwareMap.filter(paramMap, keyFilter, explicitValue);
		}
		
		//设置为null，减少转换开销
		if(src.isEmpty())
			src=null;
		
		return getGenericConverter().convert(src, targetType);
	}
	
	/**
	 * 取得Servlet对象的标准类型
	 * @param servletObject
	 * @return
	 * @date 2011-2-22
	 */
	protected Class<?> getServletObjectType(Object servletObject)
	{
		Class<?> type=null;
		
		if(servletObject instanceof HttpServletRequest)
			type=HttpServletRequest.class;
		else if(servletObject instanceof HttpSession)
			type=HttpSession.class;
		else if(servletObject instanceof HttpServletResponse)
			type=HttpServletResponse.class;
		else if(servletObject instanceof ServletContext)
			type=ServletContext.class;
		else
			throw new ObjectSourceException("unknown servlet object '"+servletObject.getClass().getName()+"'");
		
		return type;
	}
	
	/**
	 * 类型在请求参数映射表中是否只会有一个关键字与其对应。
	 * @param type
	 * @return
	 * @date 2011-4-11
	 */
	protected boolean isSingleParameterKey(Type type)
	{
		if(type==null)
			return false;
		else if(!SoybeanMilkUtils.isClassType(type))
			return false;
		else
		{
			if(SoybeanMilkUtils.narrowToClassType(type).isPrimitive())
				return true;
			else
			{
				if (Integer.class.equals(type)
						|| Double.class.equals(type)
						|| Long.class.equals(type)
						|| Boolean.class.equals(type)
						|| Float.class.equals(type)
						|| Short.class.equals(type)
						|| Byte.class.equals(type)
						|| Character.class.equals(type)
						
						|| String.class.equals(type)
						
						|| BigInteger.class.equals(type)
						|| BigDecimal.class.equals(type))
					return true;
				else
					return false;
			}
		}
	}
	
	/**
	 * 将字符串从第一个'.'位置拆分为两部分，如果不包含'.'，则返回仅包含原字符串的长度为1的数组，否则返回长度为2的且元素为拆分后的字符串的数组。
	 * @param str
	 * @return
	 * @date 2010-12-30
	 */
	protected String[] splitByFirstAccessor(String str)
	{
		String[] re=null;
		
		int idx=str.indexOf(ACCESSOR);
		
		if(idx<=0 || idx==str.length()-1)
			re=new String[]{str};
		else
		{
			re=new String[2];
			re[0]=str.substring(0,idx);
			re[1]=str.substring(idx+1);
		}
		
		return re;
	}
	
	/**
	 * 字符串是否包含访问符'.'
	 * @param str
	 * @return
	 * @date 2011-2-22
	 */
	protected boolean containAccessor(String str)
	{
		return str!=null && str.indexOf(ACCESSOR)>=0;
	}
}