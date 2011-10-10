package test.unit.core;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public class MockGenericDeclaration implements GenericDeclaration
{
	private TypeVariable<?>[] typeParameters;
	
	public MockGenericDeclaration(TypeVariable<?>... typeParameters)
	{
		super();
		this.typeParameters = typeParameters;
	}

	public TypeVariable<?>[] getTypeParameters()
	{
		return typeParameters;
	}
}
