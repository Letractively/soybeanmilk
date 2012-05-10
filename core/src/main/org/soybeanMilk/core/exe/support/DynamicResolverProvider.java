package org.soybeanMilk.core.exe.support;

import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.exe.Invoke.Resolver;
import org.soybeanMilk.core.exe.Invoke.ResolverProvider;

/**
 * 动态调用目标提供者，它依次从{@linkplain ObjectSourceResolverProvider 对象源调用目标提供者}
 * 和{@linkplain FactoryResolverProvider 工厂调用目标提供者}查找调用目标
 * @author earthangry@gmail.com
 * @date 2012-5-8
 */
public class DynamicResolverProvider implements ResolverProvider
{
	private ObjectSourceResolverProvider objectSourceResolverProvider;
	
	private FactoryResolverProvider factoryResolverProvider;
	
	public DynamicResolverProvider(){}
	
	public DynamicResolverProvider(ObjectSourceResolverProvider objectSourceResolverProvider,
			FactoryResolverProvider factoryResolverProvider)
	{
		super();
		this.objectSourceResolverProvider = objectSourceResolverProvider;
		this.factoryResolverProvider = factoryResolverProvider;
	}

	public ObjectSourceResolverProvider getObjectSourceResolverProvider() {
		return objectSourceResolverProvider;
	}

	public void setObjectSourceResolverProvider(
			ObjectSourceResolverProvider objectSourceResolverProvider) {
		this.objectSourceResolverProvider = objectSourceResolverProvider;
	}
	
	public FactoryResolverProvider getFactoryResolverProvider() {
		return factoryResolverProvider;
	}

	public void setFactoryResolverProvider(
			FactoryResolverProvider factoryResolverProvider) {
		this.factoryResolverProvider = factoryResolverProvider;
	}
	
	public Resolver getResolver(ObjectSource objectSource) throws Exception
	{
		Resolver resolver=null;
		
		if(this.objectSourceResolverProvider != null)
			resolver=this.objectSourceResolverProvider.getResolver(objectSource);
		
		if(resolver==null && this.factoryResolverProvider!=null)
			resolver=this.factoryResolverProvider.getResolver(objectSource);
		
		return resolver;
	}
	
	//@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [factoryResolverProvider="
				+ factoryResolverProvider + ", objectSourceResolverProvider="
				+ objectSourceResolverProvider + "]";
	}
}
