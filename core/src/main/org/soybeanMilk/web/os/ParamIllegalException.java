package org.soybeanMilk.web.os;

import java.lang.reflect.Type;

import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.web.WebObjectSource;

/**
 * 请求参数非法异常，{@linkplain WebObjectSource Web对象源}在将请求参数转换为某类型的对象时由于参数值非法而出现异常。
 * 
 * @author earthAngry@gmail.com
 * @date 2012-3-27
 */
public class ParamIllegalException extends ObjectSourceException
{
	private static final long serialVersionUID = 1L;
	
	/**参数名*/
	private String paramName;
	
	/**参数值*/
	private Object paramValue;
	
	/**参数值转换目标类型*/
	private Type targetType;

	public ParamIllegalException()
	{
		this(null, null, null, null, null);
	}

	public ParamIllegalException(String paramName, Object paramValue, Type targetType, String message)
	{
		this(paramName, paramValue, targetType, message, null);
	}

	public ParamIllegalException(String paramName, Object paramValue, Type targetType, Throwable cause)
	{
		this(paramName, paramValue, targetType, null, cause);
	}

	public ParamIllegalException(String paramName, Object paramValue, Type targetType, String message, Throwable cause)
	{
		super(message, cause);
		
		this.paramName=paramName;
		this.paramValue=paramValue;
		this.targetType=targetType;
	}

	/**
	 * 获取参数名。
	 * @return
	 * @date 2012-3-27
	 */
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * 获取参数值，此异常即是由于这个参数值无法转换为目标类型而导致的。
	 * @return
	 * @date 2012-3-27
	 */
	public Object getParamValue() {
		return paramValue;
	}

	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}

	/**
	 * 获取参数目标类型，此异常即是由于参数值无法转换为这个类型的对象而导致的。
	 * @return
	 * @date 2012-3-27
	 */
	public Type getTargetType() {
		return targetType;
	}

	public void setTargetType(Type targetType) {
		this.targetType = targetType;
	}
}
