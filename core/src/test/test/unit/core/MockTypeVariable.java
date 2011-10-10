package test.unit.core;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class MockTypeVariable<D extends GenericDeclaration> implements TypeVariable<D>
{
	private D genericDeclaration;
	private Type[] bounds;
	private String name;
	
	public MockTypeVariable()
	{
		super();
	}
	
	public MockTypeVariable(String name, Type[] bounds)
	{
		super();
		this.name = name;
		this.bounds = bounds;
	}
	
	public D getGenericDeclaration() {
		return genericDeclaration;
	}
	public void setGenericDeclaration(D genericDeclaration) {
		this.genericDeclaration = genericDeclaration;
	}
	public Type[] getBounds() {
		return bounds;
	}
	public void setBounds(Type[] bounds) {
		this.bounds = bounds;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
