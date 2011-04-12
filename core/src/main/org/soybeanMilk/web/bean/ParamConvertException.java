package org.soybeanMilk.web.bean;

import java.lang.reflect.Type;

import org.soybeanMilk.core.bean.ConvertException;

/**
 * 转换请求参数时出现转换异常，
 * 它可以记录导致异常的请求参数名（参考{@linkplain #getParamName()}方法）。<br>
 * 注意：<br>
 * {@linkplain #getSourceObject()}方法获取的源对象不一定是参数值本身，
 * 因为转换器可能会将参数值数组拆分后转换，通常，源对象只是参数值数组中的某一个元素。
 * @author earthAngry@gmail.com
 * @date 2011-4-12
 *
 */
public class ParamConvertException extends ConvertException
{
	private static final long serialVersionUID = 1L;
	
	private String paramName;
	
	/**
	 * 创建参数转换异常对象。
	 * @param paramName 参数名
	 * @param sourceObject 转换异常时的源对象
	 * @param targetType 转换目标类型
	 * @param cause
	 */
	public ParamConvertException(String paramName, Object sourceObject, Type targetType, Throwable cause)
	{
		super(sourceObject, targetType, cause);
		this.paramName=paramName;
	}
	
	/**
	 * 获取出现转换异常时的请求参数名，转换异常即是由于此参数的值不合法导致的。
	 * @return
	 * @date 2011-4-12
	 */
	public String getParamName()
	{
		return paramName;
	}
	
	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}

	@Override
	public String toString()
	{
		return "ParamConvertException [paramName=" + paramName
				+ ", sourceObject=" + getSourceObject()
				+ ", targetType=" + getTargetType() + "]";
	}
}
