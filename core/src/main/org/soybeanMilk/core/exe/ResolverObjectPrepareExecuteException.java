package org.soybeanMilk.core.exe;

import org.soybeanMilk.core.ExecuteException;

/**
 * 调用目标对象准备异常
 * @author earthangry@gmail.com
 * @date 2012-5-18
 */
public class ResolverObjectPrepareExecuteException extends ExecuteException
{
	private static final long serialVersionUID = 1L;
	
	/**产生此异常的调用*/
	private Invoke invoke;

	public ResolverObjectPrepareExecuteException()
	{
		super();
	}

	public ResolverObjectPrepareExecuteException(Invoke invoke, String message, Throwable cause)
	{
		super(message, cause);
		this.invoke=invoke;
	}

	public ResolverObjectPrepareExecuteException(Invoke invoke, String message)
	{
		super(message);
		this.invoke=invoke;
	}

	public ResolverObjectPrepareExecuteException(Invoke invoke, Throwable cause)
	{
		super(cause);
		this.invoke=invoke;
	}
	
	/**
	 * 获取产生此异常的{@link Invoke 调用}对象
	 * @return
	 * @date 2012-5-18
	 */
	public Invoke getInvoke() {
		return invoke;
	}
	
	/**
	 * 设置产生此异常的{@link Invoke 调用}对象
	 * @param invoke
	 * @date 2012-5-18
	 */
	public void setInvoke(Invoke invoke) {
		this.invoke = invoke;
	}
}
