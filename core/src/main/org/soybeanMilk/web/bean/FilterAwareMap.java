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
 * 过滤映射表，它由其他映射表过滤而得，并可以记住过滤关键字。
 * @author earthAngry@gmail.com
 * @date 2011-4-10
 *
 * @param <K>
 * @param <V>
 */
public class FilterAwareMap<K, V> implements Map<String, V>
{
	/**如果过滤映射表是明确关键字的，你需要使用这个关键字来取得它的值*/
	public static final String EXPLICIT_KEY="";
	
	/**当前过滤器*/
	private String filter;
	
	/**根源过滤器*/
	private String filterFromRoot;
	
	/**存储过滤结果的映射表*/
	private Map<String, V> internalMap;
	
	/**过滤器是否为明确关键字*/
	private boolean explicitKey;
	
	/**
	 * 使用过滤器（<code>filter</code>）过滤原始映射表，创建过滤映射表。
	 * @param originalMap 原始映射表
	 * @param filter 过滤器，此过滤映射表只会保留<code>originalMap</code>中关键字以此过滤器开头的对象。
	 * @param explicitKey 指定<code>filter</code>是否作为明确关键字
	 * @return
	 * @date 2011-4-10
	 */
	@SuppressWarnings("unchecked")
	public FilterAwareMap(Map<String, ?> originalMap, String filter, boolean explicitKey)
	{
		if(filter==null || filter.length()==0)
		{
			setInternalMap((Map<String, V>)originalMap);
			setFilterFromRoot(null);
			setFilter(null);
			setExplicitKey(false);
		}
		else
		{
			initInternalMap();
			setFilter(filter);
			setExplicitKey(explicitKey);
			
			if(originalMap instanceof FilterAwareMap)
			{
				String pf=((FilterAwareMap<?, ?>)originalMap).getFilterFromRoot();
				if(pf != null)
					setFilterFromRoot(pf+this.filter);
				else
					setFilterFromRoot(this.filter);
			}
			else
				setFilterFromRoot(this.filter);
			
			if(this.explicitKey)
			{
				V value=(V)originalMap.get(this.filter);
				if(value != null)
					put(EXPLICIT_KEY, value);
			}
			else
			{
				Set<String> keys=originalMap.keySet();
				for(String k : keys)
				{
					if(k.startsWith(filter))
						put(k.substring(filter.length()), (V)originalMap.get(k));
				}
			}
		}
	}
	
	/**
	 * 获取根源过滤器
	 * @return
	 * @date 2011-4-13
	 */
	public String getFilterFromRoot()
	{
		return filterFromRoot;
	}
	
	/**
	 * 获取当前过滤器
	 * @return
	 * @date 2011-4-13
	 */
	public String getFilter()
	{
		return filter;
	}
	
	/**
	 * 此映射表是否是已被过滤，如果{@linkplain #getFilterFromRoot()}不为空，
	 * 则返回<code>true</code>，否则为<code>false</code>。
	 * @return
	 * @date 2011-4-11
	 */
	public boolean isFiltered()
	{
		return this.filterFromRoot!=null && this.filterFromRoot.length()!=0;
	}
	
	/**
	 * 过滤器是否作为明确关键字，如果是，那么你需要使用{@linkplain #EXPLICIT_KEY}来取值。
	 * @return
	 * @date 2011-4-11
	 */
	public boolean isExplicitKey()
	{
		return explicitKey;
	}
	
	/**
	 * 取得根源关键字
	 * @param key 此过滤映射表中的一个关键字
	 * @return
	 * @date 2011-4-13
	 */
	public String getKeyInRoot(String key)
	{
		return this.filterFromRoot==null ? key : this.filterFromRoot+key;
	}
	
	//@Override
	public void clear()
	{
		this.internalMap.clear();
	}
	
	//@Override
	public boolean containsKey(Object key)
	{
		return this.internalMap.containsKey(key);
	}
	
	//@Override
	public boolean containsValue(Object value)
	{
		return this.internalMap.containsValue(value);
	}
	
	//@Override
	public Set<java.util.Map.Entry<String, V>> entrySet()
	{
		return this.internalMap.entrySet();
	}
	
	//@Override
	public V get(Object key)
	{
		return this.internalMap.get(key);
	}
	
	//@Override
	public boolean isEmpty()
	{
		return this.internalMap.isEmpty();
	}
	
	//@Override
	public Set<String> keySet()
	{
		return this.internalMap.keySet();
	}
	
	//@Override
	public V put(String key, V value)
	{
		return this.internalMap.put(key, value);
	}
	
	public void putAll(Map<? extends String, ? extends V> m)
	{
		this.internalMap.putAll(m);
	}

	//@Override
	public V remove(Object key)
	{
		return this.internalMap.remove(key);
	}
	
	//@Override
	public int size()
	{
		return this.internalMap.size();
	}
	
	//@Override
	public Collection<V> values()
	{
		return this.internalMap.values();
	}
	
	@Override
	public String toString()
	{
		return "FilterAwareMap [" + internalMap + "]";
	}
	
	protected void initInternalMap()
	{
		this.internalMap=new HashMap<String, V>();
	}

	protected Map<String, V> getInternalMap()
	{
		return internalMap;
	}

	protected void setInternalMap(Map<String, V> internalMap)
	{
		this.internalMap = internalMap;
	}
	
	protected void setFilter(String filter)
	{
		this.filter = filter;
	}

	protected void setExplicitKey(boolean explicitKey)
	{
		this.explicitKey = explicitKey;
	}
	
	protected void setFilterFromRoot(String filterFromRoot)
	{
		this.filterFromRoot = filterFromRoot;
	}
	
	/**
	 * 包装
	 * @param <T>
	 * @param originalMap
	 * @return
	 * @date 2011-4-11
	 */
	public static <T> FilterAwareMap<String, T> wrap(Map<String, T> originalMap)
	{
		if(originalMap instanceof FilterAwareMap<?, ?>)
			return (FilterAwareMap<String, T>) originalMap;
		return new FilterAwareMap<String, T>(originalMap, null, false);
	}
}
