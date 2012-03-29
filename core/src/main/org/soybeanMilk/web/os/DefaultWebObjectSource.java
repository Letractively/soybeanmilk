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
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.bean.MapConvertException;

/**
 * 默认的{@linkplain WebObjectSource WEB对象源}实现。
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
 *  		响应HttpServletResponse对象。如果目标类型不是“<span class="var">HttpServletResponse</span>”，
 *  		那么你需要为此对象源的{@linkplain GenericConverter 通用转换器}添加“<span class="var">javax.servlet.http.HttpServletResponse</span>”到目标类型辅助{@linkplain Converter 转换器}。
 *  	</li>
 *  	<li>
 *  		<span class="tagValue">objectSource</span> <br/>
 *  		当前的{@linkplain DefaultWebObjectSource}对象，你可以使用它来获取所有servlet对象。如果目标类型不是“<span class="var">WebObjectSource</span>”，
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
public class DefaultWebObjectSource extends ConvertableObjectSource implements WebObjectSource
{
	private static Log log = LogFactory.getLog(DefaultWebObjectSource.class);
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext application;
	
	public DefaultWebObjectSource()
	{
		super();
	}

	public DefaultWebObjectSource(HttpServletRequest request,
			HttpServletResponse response, ServletContext application)
	{
		this(request, response, application, null);
	}
	
	public DefaultWebObjectSource(HttpServletRequest request,
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

	/**
	 * 设置当前{@linkplain HttpServletRequest 请求}对象
	 * @param request
	 * @date 2011-12-11
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * 设置当前{@linkplain HttpServletResponse 响应}对象
	 * @param response
	 * @date 2011-12-11
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public ServletContext getApplication() {
		return application;
	}
	
	/**
	 * 设置{@linkplain ServletContext Servlet语境}对象
	 * @param application
	 * @date 2011-12-11
	 */
	public void setApplication(ServletContext application) {
		this.application = application;
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Serializable key, Type expectType) throws ObjectSourceException
	{
		if(key == null)
			throw new ObjectSourceException("[key] must not be null");
		
		Object result = null;
		
		String strKey=(key instanceof String ? (String)key : key.toString());
		String[] scopedKeys=SoybeanMilkUtils.splitByFirstAccessor(strKey);
		
		if(WebConstants.Scope.PARAM.equalsIgnoreCase(scopedKeys[0]))
		{
			result=getParamFilterValue(getRequest().getParameterMap(), (scopedKeys.length > 1 ? scopedKeys[1] : null));
		}
		else if(WebConstants.Scope.REQUEST.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				result=getServletObjAttrExpression(getRequest(), scopedKeys[1]);
			else
				result=getRequest();
		}
		else if(WebConstants.Scope.SESSION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				result=getServletObjAttrExpression(getRequest().getSession(), scopedKeys[1]);
			else
				result=getRequest().getSession();
		}
		else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				result=getServletObjAttrExpression(getApplication(), scopedKeys[1]);
			else
				result=getApplication();
		}
		else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				throw new ObjectSourceException("key '"+key+"' is illegal, get object from "
						+HttpServletResponse.class.getSimpleName()+" is not supported");
			else
				result=getResponse();
		}
		else if(WebConstants.Scope.OBJECT_SOURCE.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				throw new ObjectSourceException("key '"+key+"' is illegal, get object from "
						+WebObjectSource.class.getSimpleName()+" is not supported");
			else
				result=this;
		}
		else
		{
			result=getObjectWithScopeUnknownKey(strKey);
		}
		
		result=convertGotObject(result, expectType);
		
		if(log.isDebugEnabled())
			log.debug("get object '"+SoybeanMilkUtils.toString(result)+"' from '"+SoybeanMilkUtils.toString(this)+"' with key '"+SoybeanMilkUtils.toString(strKey)+"'");
		
		return (T)result;
	}
	
	//@Override
	public void set(Serializable key, Object obj)  throws ObjectSourceException
	{
		if(key == null)
			throw new IllegalArgumentException("[key] must not be null");
		
		String strKey=(key instanceof String ? (String)key : key.toString());
		String[] scopedKeys=SoybeanMilkUtils.splitByFirstAccessor(strKey);
		
		if(WebConstants.Scope.REQUEST.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				setServletObjAttrExpression(getRequest(), scopedKeys[1], obj, true);
			else
				throw new ObjectSourceException("key '"+key+"' is illegal, you can not replace '"
						+WebConstants.Scope.REQUEST+"' scope object");
		}
		else if(WebConstants.Scope.SESSION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				setServletObjAttrExpression(getRequest().getSession(), scopedKeys[1], obj, true);
			else
				throw new ObjectSourceException("key '"+key+"' is illegal, you can not replace '"
						+WebConstants.Scope.SESSION+"' scope object");
		}
		else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				setServletObjAttrExpression(getApplication(), scopedKeys[1], obj, true);
			else
				throw new ObjectSourceException("key '"+key+"' is illegal, you can not replace '"
						+WebConstants.Scope.APPLICATION+"' scope object");
		}
		else if(WebConstants.Scope.PARAM.equalsIgnoreCase(scopedKeys[0]))
		{
			throw new ObjectSourceException("key '"+key+"' is illegal, set object to '"
					+WebConstants.Scope.PARAM+"' scope is not supported");
		}
		else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(scopedKeys[0]))
		{
			throw new ObjectSourceException("key '"+key+"' is illegal, set object to '"
					+WebConstants.Scope.RESPONSE+"' scope is not supported");
		}
		else if(WebConstants.Scope.OBJECT_SOURCE.equalsIgnoreCase(scopedKeys[0]))
		{
			throw new ObjectSourceException("key '"+key+"' is illegal, set object to '"
					+WebConstants.Scope.OBJECT_SOURCE+"' scope is not supported");
		}
		else
			setObjectWithScopeUnknownKey(strKey, obj);
		
		if(log.isDebugEnabled())
			log.debug("set object '"+SoybeanMilkUtils.toString(obj)+"' to '"+SoybeanMilkUtils.toString(this)+"' with key '"+SoybeanMilkUtils.toString(strKey)+"'");
	}
	
	/**
	 * 从此对象源获取无法识别作用域的关键字对应的对象。
	 * @param key 关键字，此关键字的作用域无法被识别
	 * @return
	 * @date 2012-3-24
	 */
	@SuppressWarnings("unchecked")
	protected Object getObjectWithScopeUnknownKey(String key) throws ObjectSourceException
	{
		Object result=getServletObjAttrExpression(getRequest(), key);
		
		if(result == null)
		{
			result=getServletObjAttrExpression(getRequest().getSession(), key);
			
			if(result == null)
			{
				result=getServletObjAttrExpression(getApplication(), key);
				
				if(result == null)
					result=getParamFilterValue(getRequest().getParameterMap(), key);
			}
		}
		return result;
	}
	
	/**
	 * 将对象以作用域无法识别的关键字保存到此对象源中。
	 * @param key 关键字，此关键字的作用域无法被识别
	 * @param value
	 * @date 2012-3-24
	 */
	protected void setObjectWithScopeUnknownKey(String key, Object value) throws ObjectSourceException
	{
		boolean success=setServletObjAttrExpression(getRequest(), key, value, false);
		
		if(!success)
		{
			success=setServletObjAttrExpression(getRequest().getSession(), key, value, false);
			
			if(!success)
			{
				success=setServletObjAttrExpression(getApplication(), key, value, false);
				
				if(!success)
					setServletObjAttr(getRequest(), key, value);
			}
		}
	}
	
	/**
	 * 获取给定属性表达式在Servlet对象中的值
	 * @param servletObj
	 * @param attrExpression
	 * @return
	 */
	protected Object getServletObjAttrExpression(Object servletObj, String attrExpression) throws ObjectSourceException
	{
		if(attrExpression == null)
			return null;
		
		Object result=null;
		
		String[] propKeys=SoybeanMilkUtils.splitAccessExpression(attrExpression);
		
		//不包含访问符，则直接取值
		if(propKeys.length == 1)
			result=getServletObjAttr(servletObj, attrExpression);
		else
		{
			//尝试确定Servlet作用域属性名并获取属性值
			StringBuilder nameCache=new StringBuilder();
			int i=0;
			for(; i<propKeys.length; i++)
			{
				if(i != 0)
					nameCache.append(WebConstants.ACCESSOR);
				
				nameCache.append(propKeys[i]);
				
				result=getServletObjAttr(servletObj, nameCache.toString());
				
				if(result != null)
					break;
			}
			
			i=i+1;
			
			//作用域属性名之后的即是对象的属性名
			if(result!=null && i<propKeys.length)
			{
				nameCache.delete(0, nameCache.length());
				for(int j=i; j<propKeys.length; j++)
				{
					if(j != 0)
						nameCache.append(WebConstants.ACCESSOR);
					
					nameCache.append(propKeys[j]);
				}
				
				result=getProperty(result, nameCache.toString());
			}
		}
		
		return result;
	}
	
	/**
	 * 将值以给定属性表达式保存到Servlet对象中
	 * @param servletObj
	 * @param attrExpression
	 * @param value
	 * @param force 强制保存，返回结果将始终为<code>true</code>
	 * @return 是否成功保存
	 * @date 2012-3-25
	 */
	protected boolean setServletObjAttrExpression(Object servletObj, String attrExpression, Object value, boolean force) throws ObjectSourceException
	{
		if(attrExpression == null)
			return force;
		
		boolean result=false;
		
		String[] propKeys=SoybeanMilkUtils.splitAccessExpression(attrExpression);
		
		//没有包含访问符，则直接保存到此作用域
		if(propKeys.length ==1)
		{
			setServletObjAttr(servletObj, attrExpression, value);
			result=true;
		}
		else
		{
			//尝试确定Servlet作用域属性名并获取属性值
			Object attrObj=null;
			StringBuilder nameCache=new StringBuilder();
			int i=0;
			for(; i<propKeys.length; i++)
			{
				if(i != 0)
					nameCache.append(WebConstants.ACCESSOR);
				
				nameCache.append(propKeys[i]);
				
				attrObj=getServletObjAttr(servletObj, nameCache.toString());
				
				if(attrObj != null)
					break;
			}
			
			i=i+1;
			
			//作用域属性名之后的即是对象属性名
			if(attrObj!=null && i<propKeys.length)
			{
				nameCache.delete(0, nameCache.length());
				for(int j=i; j<propKeys.length; j++)
				{
					if(j != 0)
						nameCache.append(WebConstants.ACCESSOR);
					
					nameCache.append(propKeys[j]);
				}
				
				setProperty(attrObj, nameCache.toString(), value);
				result=true;
			}
			else if(force)
			{
				setServletObjAttr(servletObj, attrExpression, value);
				result=true;
			}
		}
		
		return result;
	}
	
	/**
	 * 获取请求参数过滤值对象。
	 * @param paramMap 参数映射表
	 * @param paramNameFilter 参数名筛选器，只有以此筛选器开头的参数关键字才会被保留，如果为<code>null</code>或空，则表明不做筛选
	 * @return
	 */
	protected ParamFilterValue getParamFilterValue(Map<String, ?> paramMap, String paramNameFilter)
	{
		ParamFilterValue result=null;
		
		if(paramNameFilter==null || paramNameFilter.length()==0)
		{
			result=new ParamFilterValue(paramNameFilter, paramMap);
		}
		else
		{
			Object explictValue=paramMap.get(paramNameFilter);
			if(explictValue != null)
			{
				result=new ParamFilterValue(paramNameFilter, explictValue);
			}
			else
			{
				//按照访问符表达式过滤
				paramNameFilter=paramNameFilter+WebConstants.ACCESSOR;
				Map<String, Object> fm=new HashMap<String, Object>();
				int fl=paramNameFilter.length();
				
				Set<String> keys=paramMap.keySet();
				
				for(String key : keys)
				{
					if(key!=null && key.length()>fl && key.startsWith(paramNameFilter))
						fm.put(key.substring(fl), paramMap.get(key));
				}
				
				result=new ParamFilterValue(paramNameFilter, (fm.size() == 0 ? null : fm));
			}
		}
		
		return result;
	}
	
	/**
	 * 从Servlet对象作用域内获取给定属性名的对象
	 * @param servletObj
	 * @param attr 属性名
	 * @date 2010-12-30
	 */
	protected Object getServletObjAttr(Object servletObj, String attr) throws ObjectSourceException
	{
		if(servletObj instanceof HttpServletRequest)
			return ((HttpServletRequest)servletObj).getAttribute(attr);
		else if(servletObj instanceof HttpSession)
			return ((HttpSession)servletObj).getAttribute(attr);
		else if(servletObj instanceof ServletContext)
			return ((ServletContext)servletObj).getAttribute(attr);
		else
			throw new ObjectSourceException("can not get attribute from servlet object '"+servletObj+"'");
	}
	
	/**
	 * 将对象保存到servlet对象作用域内
	 * @param servletObj
	 * @param attr 属性名
	 * @param value
	 * @date 2010-12-30
	 */
	protected void setServletObjAttr(Object servletObj, String attr, Object value) throws ObjectSourceException
	{
		if(servletObj instanceof HttpServletRequest)
			((HttpServletRequest)servletObj).setAttribute(attr, value);
		else if(servletObj instanceof HttpSession)
			((HttpSession)servletObj).setAttribute(attr, value);
		else if(servletObj instanceof ServletContext)
			((ServletContext)servletObj).setAttribute(attr, value);
		else
			throw new ObjectSourceException("can not set attribute to servlet object '"+servletObj+"'");
	}
	
	/**
	 * 将从对象源中获取的对象转换为目标类型的对象
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @date 2012-3-27
	 */
	protected Object convertGotObject(Object sourceObj, Type targetType) throws ObjectSourceException
	{
		Object result=null;
		
		if(targetType == null)
		{
			if(sourceObj instanceof ParamFilterValue)
				result=((ParamFilterValue)sourceObj).getValue();
			else
				result=sourceObj;
		}
		else
		{
			try
			{
				result=getGenericConverter().convert(sourceObj, targetType);
			}
			catch(ConvertException e)
			{
				if(sourceObj instanceof ParamFilterValue)
				{
					ParamFilterValue pfv=(ParamFilterValue)sourceObj;
					
					String paramName=pfv.getFilter();
					
					if(e instanceof MapConvertException)
					{
						String key=((MapConvertException)e).getKey();
						
						if(paramName == null)
							paramName=key;
						else if(key != null)
							paramName+=key;
					}
					
					throw new ParamIllegalException(paramName, e.getSourceObject(), e.getTargetType(), e);
				}
				else
					throw new ObjectSourceException(e);
			}
		}
		
		return result;
	}
	
	/**
	 * 获取对象的给定属性表达式的值
	 * @param obj
	 * @param propExpression
	 * @return
	 * @date 2012-3-25
	 */
	protected Object getProperty(Object obj, String propExpression) throws ObjectSourceException
	{
		try
		{
			return getGenericConverter().getProperty(obj, propExpression, null);
		}
		catch(ConvertException e)
		{
			throw new ObjectSourceException(e);
		}
	}
	
	/**
	 * 设置对象的给定属性表达式的值
	 * @param obj
	 * @param propExpression
	 * @param value
	 * @date 2012-3-25
	 */
	protected void setProperty(Object obj, String propExpression, Object value) throws ObjectSourceException
	{
		try
		{
			getGenericConverter().setProperty(obj, propExpression, value);
		}
		catch(ConvertException e)
		{
			throw new ObjectSourceException(e);
		}
	}
}