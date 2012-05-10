package org.soybeanMilk.core.exe.support;

import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.exe.Invoke.Resolver;
import org.soybeanMilk.core.exe.Invoke.ResolverProvider;

/**
 * 动态调用目标提供者，它依次从{@linkplain FactoryResolverProvider 工厂调用目标提供者}
 * 和{@linkplain ObjectSourceResolverProvider 对象源调用目标提供者}查找调用目标
 * @author earthangry@gmail.com
 * @date 2012-5-8
 */
public class DynamicResolverProvider implements ResolverProvider
{
	private FactoryResolverProvider factoryResolverProvider;
	
	private ObjectSourceResolverProvider objectSourceResolverProvider;
	
	public DynamicResolverProvider(){}
	
	public DynamicResolverProvider(FactoryResolverProvider factoryResolverProvider,
			ObjectSourceResolverProvider objectSourceResolverProvider)
	{
		super();
		this.factoryResolverProvider = factoryResolverProvider;
		this.objectSourceResolverProvider = objectSourceResolverProvider;
	}

	public FactoryResolverProvider getFactoryResolverProvider() {
		return factoryResolverProvider;
	}

	public void setFactoryResolverProvider(
			FactoryResolverProvider factoryResolverProvider) {
		this.factoryResolverProvider = factoryResolverProvider;
	}

	public ObjectSourceResolverProvider getObjectSourceResolverProvider() {
		return objectSourceResolverProvider;
	}

	public void setObjectSourceResolverProvider(
			ObjectSourceResolverProvider objectSourceResolverProvider) {
		this.objectSourceResolverProvider = objectSourceResolverProvider;
	}

	public Resolver getResolver(ObjectSource objectSource) throws Exception
	{
		Resolver resolver=null;
		
		if(this.factoryResolverProvider != null)
			resolver=this.factoryResolverProvider.getResolver(objectSource);
		
		if(resolver == null)
			resolver=this.objectSourceResolverProvider.getResolver(objectSource);
		
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
