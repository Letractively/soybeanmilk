/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.soybeanMilk.web.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 可以记住过滤关键字的映射表
 * @author earthAngry@gmail.com
 * @date 2011-4-10
 *
 * @param <K>
 * @param <V>
 */
public class FilterAwareMap<K, V> implements Map<String, V>
{
	public static final String EXPLICIT_KEY="";
	
	/**过滤器*/
	private String filter;
	
	/**存储过滤结果的映射表*/
	private Map<String, V> map;
	
	/**过滤结果是否唯一*/
	private boolean explicit;
	
	protected FilterAwareMap()
	{
		this.map=new HashMap<String, V>();
	}
	
	/**
	 * 获取过滤关键字
	 * @return
	 * @date 2011-4-10
	 */
	public String getFilter()
	{
		return filter;
	}

	protected void setFilter(String filter)
	{
		this.filter = filter;
	}
	
	protected Map<String, V> getMap()
	{
		return map;
	}

	protected void setMap(Map<String, V> map)
	{
		this.map = map;
	}
	
	/**
	 * 此映射表是否是经过过滤的。
	 * @return
	 * @date 2011-4-11
	 */
	public boolean isFiltered()
	{
		return this.filter!=null && this.filter.length()!=0;
	}

	/**
	 * 过滤器在原始映射表中是否对应明确的值。
	 * @return
	 * @date 2011-4-11
	 */
	public boolean isExplicit()
	{
		return explicit;
	}

	protected void setExplicit(boolean explicit)
	{
		this.explicit = explicit;
	}

	//@Override
	public void clear()
	{
		this.map.clear();
	}
	
	//@Override
	public boolean containsKey(Object key)
	{
		return this.map.containsKey(key);
	}
	
	//@Override
	public boolean containsValue(Object value)
	{
		return this.map.containsValue(value);
	}
	
	//@Override
	public Set<java.util.Map.Entry<String, V>> entrySet()
	{
		return this.map.entrySet();
	}
	
	//@Override
	public V get(Object key)
	{
		return this.map.get(key);
	}
	
	//@Override
	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}
	
	//@Override
	public Set<String> keySet()
	{
		return this.map.keySet();
	}
	
	//@Override
	public V put(String key, V value)
	{
		return this.map.put(key, value);
	}
	
	public void putAll(Map<? extends String, ? extends V> m)
	{
		this.map.putAll(m);
	}

	//@Override
	public V remove(Object key)
	{
		return this.map.remove(key);
	}
	
	//@Override
	public int size()
	{
		return this.map.size();
	}
	
	//@Override
	public Collection<V> values()
	{
		return this.map.values();
	}
	
	/**
	 * 过滤映射表，如果原始映射表中没有包含过滤器的关键字，它将返回一个不包含任何元素的映射表对象。
	 * @param original
	 * @param filter
	 * @return
	 * @date 2011-4-10
	 */
	public static FilterAwareMap<String, Object> filter(Map<String, Object> original, String filter)
	{
		FilterAwareMap<String, Object> filtered=new FilterAwareMap<String, Object>();
		
		if(filter==null || filter.length()==0)
		{
			filtered.setMap(original);
			filtered.setFilter(null);
		}
		else
		{
			if(original instanceof FilterAwareMap<?, ?>)
			{
				String pf=((FilterAwareMap<?, ?>)original).getFilter();
				if(pf != null)
					filtered.setFilter(pf+filter);
				else
					filtered.setFilter(filter);
			}
			else
				filtered.setFilter(filter);
			
			Object explicit=original.get(filter);
			if(explicit != null)
			{
				filtered.put(EXPLICIT_KEY, explicit);
				filtered.setExplicit(true);
			}
			else
			{
				Set<String> keys=original.keySet();
				for(String k : keys)
				{
					if(k.startsWith(filter))
						filtered.put(k.substring(filter.length()), original.get(k));
				}
				
				filtered.setExplicit(false);
			}
		}
		
		return filtered;
	}
}
