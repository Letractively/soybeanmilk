package org.soybeanMilk.core;

import java.io.Serializable;
import java.lang.reflect.Type;

import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.exe.Invoke;

/**
 * 执行异常。当{@linkplain Invoke 调用}从对象源取得其方法参数的对象时出现转换异常，它的源异常是{@linkplain ConvertException}。
 * @author earthAngry@gmail.com
 * @date 2011-1-11
 *
 */
public class ConvertExecuteException extends ExecuteException
{
	private static final long serialVersionUID = -5063626619646972164L;
	
	private Serializable argKey;
	private Type argType;
	
	public ConvertExecuteException(Serializable argKey, Type argType, ConvertException cause)
	{
		super(cause);
		this.argKey = argKey;
		this.argType = argType;
	}
	
	/**
	 * 获取{@linkplain Invoke 调用}方法的参数关键字。
	 * @return
	 * @date 2011-1-11
	 */
	public Serializable getArgKey() {
		return argKey;
	}

	public void setArgKey(Serializable argKey) {
		this.argKey = argKey;
	}

	/**
	 * 获取{@linkplain Invoke 调用}方法的参数的类型。
	 * @return
	 * @date 2011-1-11
	 */
	public Type getArgType() {
		return argType;
	}

	public void setArgType(Type argType) {
		this.argType = argType;
	}

	/**
	 * 获取源转换异常。
	 */
	public ConvertException getCause()
	{
		return (ConvertException)super.getCause();
	}
}
