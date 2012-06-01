package org.soybeanMilk.web.os;

import java.util.HashMap;
import java.util.Map;

import org.soybeanMilk.web.bean.WebGenericConverter;

/**
 * 参数过滤映射表，它继承自{@linkplain HashMap}且没有做任何扩展，主要作为一个标识类，使{@linkplain WebGenericConverter Web通用转换器}可以识别参数过滤映射表。
 * @author earthangry@gmail.com
 * @date 2012-5-25
 * @param <V>
 */
public class ParamFilterMap<V> extends HashMap<String, V>
{
	private static final long serialVersionUID = 1L;
	
	public ParamFilterMap()
	{
		super();
	}
	
	public ParamFilterMap(int arg0, float arg1)
	{
		super(arg0, arg1);
	}
	
	public ParamFilterMap(int arg0)
	{
		super(arg0);
	}
	
	public ParamFilterMap(Map<? extends String, ? extends V> arg0)
	{
		super(arg0);
	}
}
