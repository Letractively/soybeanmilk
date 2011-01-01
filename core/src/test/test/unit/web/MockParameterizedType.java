package test.unit.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class MockParameterizedType implements ParameterizedType
{
	private Type[] actualTypeArguments;
	private Type rawType;
	
	public MockParameterizedType(Type rawType, Type[] actualTypeArguments)
	{
		super();
		this.rawType = rawType;
		this.actualTypeArguments = actualTypeArguments;
	}
	
	@Override
	public Type[] getActualTypeArguments()
	{
		return this.actualTypeArguments;
	}

	@Override
	public Type getOwnerType()
	{
		return null;
	}
	
	@Override
	public Type getRawType()
	{
		return this.rawType;
	}

	@Override
	public String toString() {
		return "MockParameterizedType [rawType=" + rawType
				+ ", actualTypeArguments="
				+ Arrays.toString(actualTypeArguments) + "]";
	}
}
