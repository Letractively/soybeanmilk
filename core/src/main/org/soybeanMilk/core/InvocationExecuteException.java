package org.soybeanMilk.core;

import org.soybeanMilk.core.exe.Invoke;

/**
 * 执行异常。{@linkplain Invoke 调用}方法在执行时内部抛出异常，它的源异常是方法异常。
 * @author earthAngry@gmail.com
 * @date 2010-12-19
 *
 */
public class InvocationExecuteException extends ExecuteException
{
	private static final long serialVersionUID = 4055198175289241031L;

	public InvocationExecuteException(Throwable cause) {
		super(cause);
	}

	/**
	 * 获取源异常
	 */
	public Throwable getCause()
	{
		return super.getCause();
	}
}
