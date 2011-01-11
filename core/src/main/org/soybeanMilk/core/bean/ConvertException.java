package org.soybeanMilk.core.bean;

import java.lang.reflect.Type;

/**
 * 转换异常。
 * @author earthAngry@gmail.com
 * @date 2011-1-11
 *
 */
public class ConvertException extends Exception
{
	private static final long serialVersionUID = 5534640330364525246L;
	
	/**
	 * 转换的源对象
	 */
	private transient Object source;
	
	/**
	 * 转换的目标类型
	 */
	private Type targetType;
	
	public ConvertException(Object source, Type targetType)
	{
		this(source, targetType, null);
	}
	
	public ConvertException(Object source, Type targetType, Throwable cause)
	{
		super(cause);
		this.source = source;
		this.targetType = targetType;
	}

	/**
	 * 获取转换源对象
	 * @return
	 * @date 2011-1-11
	 */
	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	/**
	 * 获取转换目标类型
	 * @return
	 * @date 2011-1-11
	 */
	public Type getTargetType() {
		return targetType;
	}

	public void setTargetType(Type targetType) {
		this.targetType = targetType;
	}
}
