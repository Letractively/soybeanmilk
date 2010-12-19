package org.soybeanMilk.core;

import org.soybeanMilk.core.exe.Invoke;

/**
 * 执行异常。{@linkplain Invoke 调用}方法在执行时参数非法，它的源异常是{@linkplain IllegalArgumentException}
 * @author earthAngry@gmail.com
 * @date 2010-12-19
 *
 */
public class ArgumentExecuteException extends ExecuteException
{
	private static final long serialVersionUID = 3587718359040102481L;

	public ArgumentExecuteException(IllegalArgumentException cause) {
		super(cause);
	}
	
	/**
	 * 获取源异常
	 */
	public IllegalArgumentException getCause()
	{
		return (IllegalArgumentException)getCause();
	}
}
