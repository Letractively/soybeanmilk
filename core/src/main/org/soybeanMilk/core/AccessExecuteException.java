package org.soybeanMilk.core;

import org.soybeanMilk.core.exe.Invoke;

/**
 * 执行异常。{@linkplain Invoke 调用}方法在执行时访问非法，它的源异常是{@linkplain IllegalAccessException}
 * @author earthAngry@gmail.com
 * @date 2010-12-19
 *
 */
public class AccessExecuteException extends ExecuteException
{
	private static final long serialVersionUID = -8990168470353582122L;
	
	public AccessExecuteException(IllegalAccessException cause) {
		super(cause);
	}
	
	/**
	 * 获取源异常
	 */
	public IllegalAccessException getCause()
	{
		return (IllegalAccessException)super.getCause();
	}
}
