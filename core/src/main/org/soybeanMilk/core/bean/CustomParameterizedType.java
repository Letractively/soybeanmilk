package org.soybeanMilk.core.bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 自定义参数类型，它的{@linkplain #equals(Object)}方法被重写为可以和任意{@linkplain ParameterizedType}实现类对象比较，
 * 当两者的{@linkplain #getRawType()}、{@linkplain #getOwnerType()}和{@linkplain #getActualTypeArguments()}都相等时，则它们也相等。
 * @author earthangry@gmail.com
 * @date 2012-5-14
 */
public class CustomParameterizedType implements ParameterizedType
{
	private Type rawType;
	
	private Type ownerType;
	
	private Type[] actualTypeArguments;

	public CustomParameterizedType(){}

	public CustomParameterizedType(Type rawType, Type[] actualTypeArguments)
	{
		super();
		this.rawType = rawType;
		this.actualTypeArguments = actualTypeArguments;
	}

	public CustomParameterizedType(Type rawType, Type ownerType, Type[] actualTypeArguments)
	{
		super();
		this.rawType = rawType;
		this.ownerType = ownerType;
		this.actualTypeArguments = actualTypeArguments;
	}

	public Type getRawType()
	{
		return this.rawType;
	}
	
	public void setRawType(Type rawType) {
		this.rawType = rawType;
	}

	public Type getOwnerType()
	{
		return this.ownerType;
	}

	public void setOwnerType(Type ownerType) {
		this.ownerType = ownerType;
	}

	public Type[] getActualTypeArguments()
	{
		return this.actualTypeArguments;
	}

	public void setActualTypeArguments(Type[] actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(actualTypeArguments);
		result = prime * result
				+ ((ownerType == null) ? 0 : ownerType.hashCode());
		result = prime * result + ((rawType == null) ? 0 : rawType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParameterizedType))
			return false;
		ParameterizedType other = (ParameterizedType) obj;
		if (!Arrays.equals(actualTypeArguments, other.getActualTypeArguments()))
			return false;
		if (ownerType == null) {
			if (other.getOwnerType() != null)
				return false;
		} else if (!ownerType.equals(other.getOwnerType()))
			return false;
		if (rawType == null) {
			if (other.getRawType() != null)
				return false;
		} else if (!rawType.equals(other.getRawType()))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [rawType=" + rawType + ", ownerType="
				+ ownerType + ", actualTypeArguments="
				+ Arrays.toString(actualTypeArguments) + "]";
	}
}
