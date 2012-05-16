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

package org.soybeanMilk.web.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.os.ParamFilterValue;

/**
 * Web通用转换器，除了继承自{@linkplain DefaultGenericConverter 默认通用转换器的}的转换支持，
 * 它还添加了一些适用于Web的功能，比如，如果源对象是数组，而目标类型不是，它将会使用数组的第一个元素执行转换。
 * 
 * @author earthangry@gmail.com
 * @date 2010-10-8
 */
public class WebGenericConverter extends DefaultGenericConverter
{
	private static Log log = LogFactory.getLog(WebGenericConverter.class);
	
	public WebGenericConverter()
	{
		super();
	}
	
	public WebGenericConverter(boolean initDefaultSupportConverter)
	{
		super(initDefaultSupportConverter);
	}
	
	//@Override
	protected Object convertWhenNoSupportConverter(Object sourceObj, Type targetType) throws ConvertException
	{
		Object result=null;
		
		//如果源对象是数组而目标类型不是，则使用数组的第一个元素转换，与request.getParameter(...)规则相同
		if(sourceObj.getClass().isArray() && SbmUtils.isClassType(targetType)
				&& !SbmUtils.narrowToClassType(targetType).isArray())
		{
			if(log.isDebugEnabled())
				log.debug(SbmUtils.toString(sourceObj)+" is an array while the target not, so it's first element will be used for converting");
			
			sourceObj=(Array.getLength(sourceObj) == 0 ? null : Array.get(sourceObj, 0));
			
			result=convertObjectToType(sourceObj, targetType);
		}
		else if(sourceObj instanceof ParamFilterValue)
		{
			result=convertParamFilterValue((ParamFilterValue)sourceObj, targetType);
		}
		else if(sourceObj instanceof HttpServletRequest)
		{
			result=getConverterNotNull(HttpServletRequest.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof HttpSession)
		{
			result=getConverterNotNull(HttpSession.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof ServletContext)
		{
			result=getConverterNotNull(ServletContext.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof HttpServletResponse)
		{
			result=getConverterNotNull(HttpServletResponse.class, targetType).convert(sourceObj, targetType);
		}
		else if(sourceObj instanceof WebObjectSource)
		{
			result=getConverterNotNull(WebObjectSource.class, targetType).convert(sourceObj, targetType);
		}
		else
		{
			result=super.convertWhenNoSupportConverter(sourceObj, targetType);
		}
		
		return result;
	}
	
	/**
	 * 转换请求参数过滤值
	 * @param pfv
	 * @param targetType
	 * @return
	 * @throws ConvertException
	 * @date 2012-3-28
	 */
	@SuppressWarnings("unchecked")
	protected Object convertParamFilterValue(ParamFilterValue pfv, Type targetType) throws ConvertException
	{
		Object result=null;
		
		String filter=pfv.getFilter();
		Object value=pfv.getValue();
		
		if(SbmUtils.isInstanceOf(value, SbmUtils.toWrapperType(targetType)))
			return value;
		else if(value instanceof Map<?, ?>)
		{
			//过滤后的参数映射表必须是清洁的
			value=new PropertyValueMap((Map<String, ?>)value, (filter!=null && filter.length()!=0));
			result=convertObjectToType(value, targetType);
		}
		else
			result=convertObjectToType(value, targetType);
		
		return result;
	}
}