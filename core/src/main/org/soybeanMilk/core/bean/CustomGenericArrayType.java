package org.soybeanMilk.core.bean;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * 自定义泛型数组类型，它的{{@link #equals(Object)}方法被重写为可以和任意{@linkplain GenericArrayType}实现类对象比较，
 * 当两者的{@link #getGenericComponentType()}相等时，则它们也相等。
 * @author earthangry@gmail.com
 * @date 2012-5-14
 */
public class CustomGenericArrayType implements GenericArrayType
{
	private Type genericComponentType;
	
	public CustomGenericArrayType() {}

	public CustomGenericArrayType(Type genericComponentType)
	{
		super();
		this.genericComponentType = genericComponentType;
	}

	public Type getGenericComponentType()
	{
		return this.genericComponentType;
	}

	public void setGenericComponentType(Type genericComponentType)
	{
		this.genericComponentType = genericComponentType;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((genericComponentType == null) ? 0 : genericComponentType
						.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GenericArrayType))
			return false;
		GenericArrayType other = (GenericArrayType) obj;
		if (genericComponentType == null) {
			if (other.getGenericComponentType() != null)
				return false;
		} else if (!genericComponentType.equals(other.getGenericComponentType()))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [genericComponentType="
				+ genericComponentType + "]";
	}
}
