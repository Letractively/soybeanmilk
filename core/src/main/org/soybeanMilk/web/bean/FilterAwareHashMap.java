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
 * 过滤器哈希表，它由某个原始映射表（关键字必须是字符串类型）中特定的关键字前缀过滤而得，并可以记住过滤关键字。
 * @author earthAngry@gmail.com
 * @date 2012-2-17
 * @param <V>
 */
public class FilterAwareHashMap<V> implements Map<String, V>
{
	private static final String EXPLICIT_KEY="org.soybeanMilk.web.bean.FilterAwareHashMap_EXPLICIT_KEY";
	
	/**父映射表*/
	private Map<String, V> parentMap;
	
	private Map<String, V> internalMap;
	
	/**当前过滤器*/
	private String filter;
	
	/**根源过滤器*/
	private String filterFromRoot;
	
	/**过滤器是否为明确关键字*/
	private boolean explictFilter;
	
	/**
	 * 创建一个过滤器哈希表对象
	 * @param parentMap 原始映射表
	 * @param filter 过滤器
	 */
	public FilterAwareHashMap(Map<String, V> parentMap, String filter)
	{
		this(parentMap, filter, false);
	}
	
	/**
	 * 创建一个过滤器哈希表对象
	 * @param parentMap 原始映射表
	 * @param filter 过滤器，如果为<code>null</code>，则仅是对原是映射表的包装
	 * @param explictFilter 是否是明确过滤器，即过滤器是否直接对应唯一的一个值
	 */
	public FilterAwareHashMap(Map<String, V> parentMap, String filter, boolean explictFilter)
	{
		this.parentMap=parentMap;
		this.filter=filter;
		this.explictFilter=explictFilter;
		
		String parentFilterFromRoot=null;
		
		if(parentMap instanceof FilterAwareHashMap<?>)
			parentFilterFromRoot=((FilterAwareHashMap<?>)parentMap).getFilterFromRoot();
		
		if(filter==null || filter.length()==0)
		{
			this.internalMap=this.parentMap;
			
			setFilterFromRoot(parentFilterFromRoot);
		}
		else
		{
			initInternalMap();
			
			if(parentFilterFromRoot == null)
				setFilterFromRoot(filter);
			else
				setFilterFromRoot(parentFilterFromRoot+filter);
		}
	}
	
	/**
	 * 执行过滤
	 * @date 2012-2-16
	 */
	public void filter()
	{
		if(this.parentMap==null
				|| filter==null || filter.length()==0)
			return;
		else
		{
			if(this.explictFilter)
			{
				V value=(V)this.parentMap.get(this.filter);
				put(EXPLICIT_KEY, value);
			}
			else
			{
				Set<String> keys=this.parentMap.keySet();
				for(String k : keys)
				{
					if(k.startsWith(filter))
						put(k.substring(filter.length()), (V)this.parentMap.get(k));
				}
			}
		}
	}
	
	/**
	 * 此映射表是否是已被过滤的
	 * @return
	 * @date 2011-4-11
	 */
	public boolean isFiltered()
	{
		return this.filterFromRoot!=null && this.filterFromRoot.length()!=0;
	}
	
	/**
	 * 获取父映射表
	 * @return
	 * @date 2012-2-16
	 */
	public Map<String, V> getParentMap() {
		return parentMap;
	}
	protected void setParentMap(Map<String, V> parentMap) {
		this.parentMap = parentMap;
	}
	
	/**
	 * 获取过滤器
	 * @return
	 * @date 2012-2-16
	 */
	public String getFilter() {
		return filter;
	}
	protected void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * 获取根源过滤器
	 * @return
	 * @date 2012-2-16
	 */
	public String getFilterFromRoot() {
		return filterFromRoot;
	}
	protected void setFilterFromRoot(String filterFromRoot) {
		this.filterFromRoot = filterFromRoot;
	}
	
	/**
	 * 是否是明确过滤器，如果是，那么你应该通过{@linkplain #getExplictValue()}来取值
	 * @return
	 * @date 2012-2-16
	 */
	public boolean isExplictFilter() {
		return explictFilter;
	}
	protected void setExplictFilter(boolean explictFilter) {
		this.explictFilter = explictFilter;
	}
	
	/**
	 * 取得根源关键字
	 * @param key 此过滤映射表中的一个关键字
	 * @return
	 * @date 2011-4-13
	 */
	public String getKeyInRoot(String key)
	{
		if(this.explictFilter)
			return this.getFilter();
		else
			return this.filterFromRoot==null ? key : this.filterFromRoot+key;
	}
	
	/**
	 * 获取明确过滤器的值
	 * @return
	 * @date 2012-2-16
	 */
	public V getExplictValue()
	{
		if(!this.explictFilter)
			throw new IllegalStateException("filter is not explict");
		
		return get(EXPLICIT_KEY);
	}

	public void clear()
	{
		this.internalMap.clear();
	}

	public boolean containsKey(Object key)
	{
		return this.internalMap.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return this.internalMap.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, V>> entrySet()
	{
		return this.internalMap.entrySet();
	}

	public V get(Object key)
	{
		return this.internalMap.get(key);
	}
	
	public boolean isEmpty()
	{
		return this.internalMap.isEmpty();
	}

	public Set<String> keySet()
	{
		return this.internalMap.keySet();
	}
	
	public V put(String key, V value)
	{
		return this.internalMap.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends V> t)
	{
		this.internalMap.putAll(t);
	}

	public V remove(Object key)
	{
		return this.internalMap.remove(key);
	}

	public int size()
	{
		return this.internalMap.size();
	}

	public Collection<V> values()
	{
		return this.internalMap.values();
	}
	
	@Override
	public String toString() 
	{
		return this.internalMap.toString();
	}

	protected void initInternalMap()
	{
		this.internalMap=new HashMap<String, V>();
	}
	
	/**
	 * 包装
	 * @param <T>
	 * @param originalMap
	 * @return
	 * @date 2012-2-17
	 */
	public static <T> FilterAwareHashMap<T> wrap(Map<String, T> originalMap)
	{
		if(originalMap instanceof FilterAwareHashMap<?>)
			return (FilterAwareHashMap<T>) originalMap;
		else
			return new FilterAwareHashMap<T>(originalMap, null);
	}
}
