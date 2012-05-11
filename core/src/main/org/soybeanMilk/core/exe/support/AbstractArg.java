package org.soybeanMilk.core.exe.support;

import java.lang.reflect.Type;

import org.soybeanMilk.core.exe.Invoke.Arg;

/**
 * 参数抽象类，它封装参数类型属性
 * @author earthangry@gmail.com
 * @date 2012-5-11
 */
public abstract class AbstractArg implements Arg
{
	/**参数类型*/
	private Type type;

	//@Override
	public Type getType(){
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
}
