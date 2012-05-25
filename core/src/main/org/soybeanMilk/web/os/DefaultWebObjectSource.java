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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SbmUtils;
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
 * 当从默认Web对象源获取某个关键字对应的对象时（通过{@linkplain #get(Serializable, Type)}方法），它会依次从这三个底层对象源中查找对象，如果在任意一个中找到匹配，那个匹配对象将被返回，否则，
 * 它会把这个关键字理解为请求参数过滤器，然后从请求参数中过滤和获取对象并返回。
 * </p>
 * <p>
 * 在从请求参数获取对象时，它会做一些特殊的处理：如果这个关键字直接对应某个请求参数，那么那个请求参数的值将被返回；否则，它会以“[关键字].”作为过滤器（加一个'.'字符），
 * 筛选请求参数映射表中仅以这个过滤器开头的项，生成一个新的映射表并将它返回，并且这个新映射表的关键字将只是原请求参数映射表关键字的这个过滤器之后的部分。
 * 比如有参数名为“somePrefix.someBean.propertyA”和“somePrefix.someBean.propertyB”的请求参数值，
 * 那么在以“somePrefix.someBean”为关键字从默认Web对象源获取之后，新的映射表关键字将变为“propertyA”和“propertyB”。
 * </p>
 * <p>
 * 如果默认Web对象源从底层对象源中获取的对象与期望的对象类型不匹配，它将会使用{@linkplain WebGenericConverter Web通用转换器}将这个对象转换为期望类型的对象。
 * </p>
 * <p>
 * 当将对象保存到默认Web对象源时（通过{@linkplain #set(Serializable, Object)}方法），对象将会被直接保存到{@linkplain HttpServletRequest}中。
 * </p>
 * <p>
 * 默认Web对象源还提供了一些用于标识作用域的特殊关键字，包括“param”、“request”、“session”、“application”、“response”、“objectSource”：
 * <ul>
 *     <li>
 *         param<br>
 *         表示整个请求参数映射表，使用它可以从默认Web对象源获取整个请求参数映射表，也可以使用“param.someKey”形式的关键字，指明是从请求参数中过滤和获取对象。
 *     </li>
 *     <li>
 *         request<br>
 *         表示{@linkplain HttpServletRequest}对象，使用它可以从默认Web对象源获取当前请求对象，
 *         也可以使用“request.someKey”形式的关键字，指明是从当前请求对象属性中获取对象或者将对象保存到请求对象属性中。
 *     </li>
 *     <li>
 *         session<br>
 *         表示{@linkplain HttpSession}对象，使用它可以从默认Web对象源获取当前会话对象，
 *         也可以使用“session.someKey”形式的关键字，指明是当前从会话对象属性中获取对象或者将对象保存到会话对象属性中。
 *     </li>
 *     <li>
 *         application<br>
 *         表示{@linkplain ServletContext}对象，使用它可以从默认Web对象源获取当前应用对象，
 *         也可以使用“application.someKey”形式的关键字，指明是从当前应用对象属性中获取对象或者将对象保存到应用对象属性中。
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
 * @author earthangry@gmail.com
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
	public <T> T get(Serializable key) throws ObjectSourceException
	{
		return (T)getObject(key, null);
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Serializable key, Type expectType) throws ObjectSourceException
	{
		return (T)getObject(key, expectType);
	}
	
	//@Override
	public void set(Serializable key, Object obj)  throws ObjectSourceException
	{
		if(key == null)
			throw new IllegalArgumentException("[key] must not be null");
		
		String strKey=(key instanceof String ? (String)key : key.toString());
		String[] scopedKeys=SbmUtils.splitByFirstAccessor(strKey);
		
		if(WebConstants.Scope.REQUEST.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				getRequest().setAttribute(scopedKeys[1], obj);
			else
				throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, you can not replace "
						+SbmUtils.toString(WebConstants.Scope.REQUEST)+" scope object");
		}
		else if(WebConstants.Scope.SESSION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				getRequest().getSession().setAttribute(scopedKeys[1], obj);
			else
				throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, you can not replace "
						+SbmUtils.toString(WebConstants.Scope.SESSION)+" scope object");
		}
		else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				getApplication().setAttribute(scopedKeys[1], obj);
			else
				throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, you can not replace "
						+SbmUtils.toString(WebConstants.Scope.APPLICATION)+" scope object");
		}
		else if(WebConstants.Scope.PARAM.equalsIgnoreCase(scopedKeys[0]))
		{
			throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, set object to "
					+SbmUtils.toString(WebConstants.Scope.PARAM)+" scope is not supported");
		}
		else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(scopedKeys[0]))
		{
			throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, set object to "
					+SbmUtils.toString(WebConstants.Scope.RESPONSE)+" scope is not supported");
		}
		else if(WebConstants.Scope.OBJECT_SOURCE.equalsIgnoreCase(scopedKeys[0]))
		{
			throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, set object to "
					+SbmUtils.toString(WebConstants.Scope.OBJECT_SOURCE)+" scope is not supported");
		}
		else
			setObjectWithScopeUnknownKey(strKey, obj);
		
		if(log.isDebugEnabled())
			log.debug("set object "+SbmUtils.toString(obj)+" to "+SbmUtils.toString(this)+" with key "+SbmUtils.toString(strKey));
	}
	
	/**
	 * 获取对象
	 * @param key
	 * @param expectType
	 * @return
	 * @throws ObjectSourceException
	 * @date 2012-5-16
	 */
	@SuppressWarnings("unchecked")
	protected Object getObject(Serializable key, Type expectType) throws ObjectSourceException
	{
		if(key == null)
			throw new ObjectSourceException("[key] must not be null");
		
		Object result = null;
		
		String strKey=(key instanceof String ? (String)key : key.toString());
		String[] scopedKeys=SbmUtils.splitByFirstAccessor(strKey);
		
		if(WebConstants.Scope.PARAM.equalsIgnoreCase(scopedKeys[0]))
		{
			result=getParamFilterValue(getRequest().getParameterMap(), (scopedKeys.length > 1 ? scopedKeys[1] : null), expectType);
		}
		else if(WebConstants.Scope.REQUEST.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				result=getRequest().getAttribute(scopedKeys[1]);
			else
				result=getRequest();
		}
		else if(WebConstants.Scope.SESSION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				result=getRequest().getSession().getAttribute(scopedKeys[1]);
			else
				result=getRequest().getSession();
		}
		else if(WebConstants.Scope.APPLICATION.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				result=getApplication().getAttribute(scopedKeys[1]);
			else
				result=getApplication();
		}
		else if(WebConstants.Scope.RESPONSE.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, get object from "
						+HttpServletResponse.class.getSimpleName()+" is not supported");
			else
				result=getResponse();
		}
		else if(WebConstants.Scope.OBJECT_SOURCE.equalsIgnoreCase(scopedKeys[0]))
		{
			if(scopedKeys.length > 1)
				throw new ObjectSourceException("key "+SbmUtils.toString(key)+" is illegal, get object from "
						+WebObjectSource.class.getSimpleName()+" is not supported");
			else
				result=this;
		}
		else
		{
			result=getObjectWithScopeUnknownKey(strKey, expectType);
		}
		
		result=convertGotObject(result, expectType);
		
		if(log.isDebugEnabled())
			log.debug("got object "+SbmUtils.toString(result)+" from "+SbmUtils.toString(this)+" with key "+SbmUtils.toString(strKey));
		
		return result;
	}
	
	/**
	 * 从此对象源获取无法识别作用域的关键字对应的对象，这个方法不必执行类型转换。
	 * @param key 关键字，此关键字的作用域无法被识别
	 * @param expectType 期望类型
	 * @return
	 * @date 2012-3-24
	 */
	@SuppressWarnings("unchecked")
	protected Object getObjectWithScopeUnknownKey(String key, Type expectType) throws ObjectSourceException
	{
		Object result=getRequest().getAttribute(key);
		
		if(result == null)
		{
			result=getRequest().getSession().getAttribute(key);
			
			if(result == null)
			{
				result=getApplication().getAttribute(key);
				
				if(result == null)
					result=getParamFilterValue(getRequest().getParameterMap(), key, expectType);
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
		getRequest().setAttribute(key, value);
	}
	
	/**
	 * 获取请求参数过滤值对象，这个方法不必执行类型转换。
	 * @param paramMap 参数映射表
	 * @param paramNameFilter 参数名筛选器，只有以此筛选器开头的参数关键字才会被保留，如果为<code>null</code>或空，则表明不做筛选
	 * @param expectType 期望类型
	 * @return
	 */
	protected ParamFilterValue getParamFilterValue(Map<String, ?> paramMap, String paramNameFilter, Type expectType)
	{
		ParamFilterValue result=null;
		
		if(paramNameFilter==null || paramNameFilter.length()==0)
		{
			result=new ParamFilterValue(paramNameFilter, paramMap);
		}
		else
		{
			Object explictValue=paramMap.get(paramNameFilter);
			
			if(explictValue!=null || isSingleParamValue(expectType))
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
	 * 给定的类型是否只对应一个参数值
	 * @param type
	 * @return
	 * @date 2012-5-25
	 */
	protected boolean isSingleParamValue(Type type)
	{
		if(!SbmUtils.isClassType(type))
			return false;
		else
		{
			Class<?> clazz=SbmUtils.narrowToClassType(type);
			
			if(clazz.isPrimitive())
				return true;
			else if (Boolean.class.equals(type) || Integer.class.equals(type) || Long.class.equals(type)
					|| Float.class.equals(type) || Double.class.equals(type)
					|| BigInteger.class.equals(type) || BigDecimal.class.equals(type)
					|| Character.class.equals(type) || Byte.class.equals(type) || Short.class.equals(type))
	            return true;
	        else
	        	return false;
		}
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
}