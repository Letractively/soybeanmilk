package test.unit.core;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class MockGenericArrayType implements GenericArrayType
{
	private Type componentType;
	
	public MockGenericArrayType(Type componentType)
	{
		super();
		this.componentType = componentType;
	}

	public Type getGenericComponentType()
	{
		return componentType;
	}

}
