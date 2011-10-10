package test.unit.core;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class MockWildcardType implements WildcardType
{
	private Type[] lowerBounds;
	private Type[] upperBounds;
	
	public MockWildcardType()
	{
		super();
	}
	
	public Type[] getLowerBounds()
	{
		return lowerBounds;
	}

	public void setLowerBounds(Type[] lowerBounds) {
		this.lowerBounds = lowerBounds;
	}

	public Type[] getUpperBounds() {
		return upperBounds;
	}

	public void setUpperBounds(Type[] upperBounds) {
		this.upperBounds = upperBounds;
	}
}
