package org.soybeanMilk.web.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.web.WebConstants;

/**
 * 参数属性映射表，它的关键字是属性名而非属性表达式，即只可能是“propertyA”、“propertyB”，而非“propertyA.propertyB”
 * @author earthAngry@gmail.com
 * @date 2012-2-19
 *
 * @param <Object>
 */
public class ParamPropertyMap implements Map<String, Object>
{
	private ParamPropertyMap parent;
	
	private String propertyName;
	
	private HashMap<String, Object> internalMap;
	
	public ParamPropertyMap()
	{
		this(null, null);
	}
	
	public ParamPropertyMap(String propertyName)
	{
		this(null, propertyName);
	}
	
	public ParamPropertyMap(ParamPropertyMap parent, String propertyName)
	{
		super();
		this.parent = parent;
		this.propertyName=propertyName;
		
		this.internalMap=new HashMap<String, Object>();
	}
	
	/**
	 * 是否根属性映射表
	 * @return
	 * @date 2012-2-20
	 */
	public boolean isRootPropertyMap()
	{
		return (this.propertyName == null);
	}
	
	public String getParamFullKey(String key)
	{
		String result=null;
		
		if(this.parent != null)
			result=this.parent.getParamFullKey(this.propertyName);
		else
			result=this.propertyName;
		
		if(result==null || result.length()==0)
			return key;
		else
			return result+WebConstants.ACCESSOR+key;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public ParamPropertyMap getParent() {
		return parent;
	}

	public void setParent(ParamPropertyMap parent) {
		this.parent = parent;
	}
	
	public void filterWithProperty(Map<String, Object> source)
	{
		String filter= (this.propertyName==null || this.propertyName.length()==0 ? null : this.propertyName+WebConstants.ACCESSOR);
		
		Set<String> keys=source.keySet();
		boolean doAssemble=false;
		
		for(String key : keys)
		{
			String propExp=key;
			
			if(filter == null)
				doAssemble=true;
			else
			{
				int fl=filter.length();
				
				if(key.length() <= fl)
					doAssemble=false;
				else if(key.startsWith(filter))
				{
					doAssemble=true;
					propExp=key.substring(fl);
				}
				else
					doAssemble=false;
			}
			
			if(doAssemble)
			{
				String[] propKeys=SoybeanMilkUtils.splitPropertyExpression(propExp);
				
				ParamPropertyMap parent=this;
				
				for(int i=0; i<propKeys.length; i++)
				{
					if(i == propKeys.length-1)
					{
						parent.put(propKeys[i], source.get(key));
					}
					else
					{
						ParamPropertyMap tmp=(ParamPropertyMap)parent.get(propKeys[i]);
						if(tmp == null)
						{
							tmp=new ParamPropertyMap(parent, propKeys[i]);
							parent.put(propKeys[i], tmp);
						}
						
						parent=tmp;
					}
				}
			}
		}
	}

	public void clear()
	{
		this.internalMap.clear();
	}

	public boolean containsKey(Object arg0)
	{
		return this.internalMap.containsKey(arg0);
	}

	public boolean containsValue(Object arg0)
	{
		return this.internalMap.containsValue(arg0);
	}

	public Set<Entry<String, Object>> entrySet()
	{
		return this.internalMap.entrySet();
	}

	public Object get(Object arg0)
	{
		return this.internalMap.get(arg0);
	}

	public boolean isEmpty()
	{
		return this.internalMap.isEmpty();
	}

	public Set<String> keySet()
	{
		return this.internalMap.keySet();
	}

	public Object put(String arg0, Object arg1)
	{
		return this.internalMap.put(arg0, arg1);
	}

	public void putAll(Map<? extends String, ? extends Object> arg0)
	{
		this.internalMap.putAll(arg0);
	}

	public Object remove(Object arg0)
	{
		return this.internalMap.remove(arg0);
	}

	public int size()
	{
		return this.internalMap.size();
	}
	
	public Collection<Object> values()
	{
		return this.internalMap.values();
	}
}