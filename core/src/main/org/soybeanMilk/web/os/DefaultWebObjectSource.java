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
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.bean.MapConvertException;
import org.soybeanMilk.core.os.ConvertableObjectSource;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.bean.WebGenericConverter;

/**
 * 默认Web对象源，它是{@linkplain WebObjectSource Web对象源}的一个默认实现，
 * 使用Servlet环境的{@linkplain HttpServletRequest}、{@linkplain HttpSession}、{@linkplain ServletContext}作为底层对象源。
 * <p>
 * 当从默认Web对象源获取某个关键字对应的对象时，它会依次从这三个底层对象源中查找对象，如果在任意一个中找到匹配，那个匹配对象将被返回，否则，它会把这个关键字理解为请求
 * 参数过滤器，然后从请求参数中过滤和获取对象并返回。
 * </p>
 * <p>
 * 在从请求参数获取对象时，它会做一些特殊的处理：如果这个关键字直接对应某个请求参数，那么那个请求参数的值将被返回；否则，它会以“[关键字].”作为过滤器（加一个'.'字符），
 * 筛选请求参数映射表中仅以这个过滤器开头的项，生成一个新的映射表并将它返回，并且这个新映射表的关键字将只是原请求参数映射表关键字的这个过滤器之后的部分。
 * 比如有一个参数名为“somePrefix.someParam.someValue”的请求参数，那么在以“somePrefix.someParam”为关键字从默认Web对象源获取之后，新的映射表关键字
 * 将变为“someValue”。
 * </p>
 * <p>
 * 另外，你也可以使用“someObj.someProperty”形式的关键字，来获取已存在于底层对象源的“someObj”对象的“someProperty”属性值，而如果“someObj”对象不存在，
 * “someObj.someProperty”将被认为仅仅是一个普通形式的关键字而重新查找。
 * </p>
 * <p>
 * 如果默认Web对象源从底层对象源中获取的对象与期望的对象类型不匹配，它将会使用{@linkplain WebGenericConverter Web通用转换器}将这个对象转换为期望类型的对象。
 * </p>
 * <p>
 * 当将对象保存到默认Web对象源时，对象将会被直接保存到{@linkplain HttpServletRequest}中，而如果是要将对象保存到已存在于底层对象源的某个对象的某个属性中，
 * 比如以关键字“someObj.someProperty”，那么默认Web对象源会从三个底层对象源查找“someObj”对象，并设置它的“someProperty”属性的值，而如果“someObj”对象不存在，
 * “someObj.someProperty”将被认为仅仅是一个普通形式的关键字，而保存到{@linkplain HttpServletRequest}中。
 * </p>
 * <p>
 * 默认Web对象源还提供了一些用于标识作用域的特殊关键字，包括“param”、“request”、“session”、“application”、“response”、“objectSource”：
 * <ul>
 *     <li>
 *         param<br>
 *         表示整个请求参数映射表，使用它可以从默认Web对象源获取整个请求参数映射表，也可以使用“param.someKey”的形式，指明是从请求参数中过滤和获取对象。
 *     </li>
 *     <li>
 *         request<br>
 *         表示{@linkplain HttpServletRequest}对象，使用它可以从默认Web对象源获取当前请求对象，
 *         也可以使用“request.someKey”的形式，指明是从当前请求对象属性中获取对象或者将对象保存到请求对象属性中。
 *     </li>
 *     <li>
 *         session<br>
 *         表示{@linkplain HttpSession}对象，使用它可以从默认Web对象源获取当前会话对象，
 *         也可以使用“session.someKey”的形式，指明是当前从会话对象属性中获取对象或者将对象保存到会话对象属性中。
 *     </li>
 *     <li>
 *         application<br>
 *         表示{@linkplain ServletContext}对象，使用它可以从默认Web对象源获取当前应用对象，
 *         也可以使用“application.someKey”的形式，指明是从当前应用对象属性中获取对象或者将对象保存到应用对象属性中。
 *     </li>
 *     <li>
 *         response<br>
 *         表示{@linkplain HttpServletResponse}对象，使用它可以从默认Web对象源获取当前响应对象。
 *     </li>
 *     <li>
 *         objectSource<br>
 *         表示{@linkplain WebObjectSource Web对象源}本身，使用它可以获取当前的Web对象源引用。
 *     </li>
 * </ul>
 * 注意，如果你是要从默认Web对象源获取“request”、“session”、“application”、“response”、“objectSource”这些对象，而期望类型与与它们的类型不同，
 * 那么你需要为默认Web对象源使用的{@linkplain WebGenericConverter Web通用转换器}添加这些对象表示的类型到你所期望类型的辅助转换器。
 * </p>
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