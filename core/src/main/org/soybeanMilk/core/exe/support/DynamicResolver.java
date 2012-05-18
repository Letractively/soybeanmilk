package org.soybeanMilk.core.exe.support;

import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.exe.Invoke.Resolver;

/**
 * 动态调用目标，它依次从{@linkplain FactoryResolver 工厂调用目标}
 * 和{@linkplain ObjectSourceResolver 对象源调用目标}查找调用目标对象
 * @author earthangry@gmail.com
 * @date 2012-5-8
 */
public class DynamicResolver implements Resolver
{
	private FactoryResolver factoryResolverProvider;
	
	private ObjectSourceResolver objectSourceResolverProvider;
	
	public DynamicResolver(){}
	
	public DynamicResolver(FactoryResolver factoryResolverProvider,
			ObjectSourceResolver objectSourceResolverProvider)
	{
		super();
		this.factoryResolverProvider = factoryResolverProvider;
		this.objectSourceResolverProvider = objectSourceResolverProvider;
	}
	
	public FactoryResolver getFactoryResolverProvider() {
		return factoryResolverProvider;
	}

	public void setFactoryResolverProvider(
			FactoryResolver factoryResolverProvider) {
		this.factoryResolverProvider = factoryResolverProvider;
	}
	
	public ObjectSourceResolver getObjectSourceResolverProvider() {
		return objectSourceResolverProvider;
	}

	public void setObjectSourceResolverProvider(
			ObjectSourceResolver objectSourceResolverProvider) {
		this.objectSourceResolverProvider = objectSourceResolverProvider;
	}
	
	//@Override
	public Object getResolverObject(ObjectSource objectSource) throws Exception
	{
		Object ro=null;
		
		if(this.factoryResolverProvider != null)
			ro=this.factoryResolverProvider.getResolverObject(objectSource);
		
		if(ro==null && this.objectSourceResolverProvider!=null)
			ro=this.objectSourceResolverProvider.getResolverObject(objectSource);
		
		return ro;
	}
	
	//@Override
	public Class<?> getResolverClass(ObjectSource objectSource) throws Exception
	{
		Class<?> re=null;
		
		if(this.factoryResolverProvider != null)
			re=this.factoryResolverProvider.getResolverClass(objectSource);
		
		if(re==null && this.objectSourceResolverProvider!=null)
			re=this.objectSourceResolverProvider.getResolverClass(objectSource);
		
		return re;
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [factoryResolverProvider="
				+ factoryResolverProvider + ", objectSourceResolverProvider="
				+ objectSourceResolverProvider + "]";
	}
}
