package org.soybeanMilk.core.bean;

/**
 * 转换异常。{@linkplain Converter 转换器}在执行对象转换时出现异常。
 * @author earthAngry@gmail.com
 * @date 2011-1-12
 *
 */
public class ConvertException extends RuntimeException
{
	private static final long serialVersionUID = 5534640330364525246L;

	public ConvertException()
	{
		super();
	}

	public ConvertException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ConvertException(String message)
	{
		super(message);
	}

	public ConvertException(Throwable cause)
	{
		super(cause);
	}
}
