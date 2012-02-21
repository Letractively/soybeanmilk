package org.soybeanMilk.web.bean;

/**
 * 参数值，它知道自己的参数名
 * 
 * @author earthAngry@gmail.com
 * @date 2012-2-21
 */
public class ParamValue
{
	/**参数名*/
	private String paramName;
	
	/**要被转换的对象*/
	private Object value;
	
	public ParamValue()
	{
		this(null, null);
	}

	public ParamValue(String paramName, Object value)
	{
		super();
		this.paramName = paramName;
		this.value = value;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
