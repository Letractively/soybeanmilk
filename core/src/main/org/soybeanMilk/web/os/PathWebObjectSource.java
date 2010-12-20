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

package org.soybeanMilk.web.os;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.soybeanMilk.core.bean.GenericConverter;

/**
 * 添加了"path"作用域支持的web对象源，使你可以使用“path.[subKey]”来获取和保存对象。
 * @author earthAngry@gmail.com
 * @date 2010-12-17
 *
 */
public class PathWebObjectSource extends WebObjectSource
{
	/**
	 * 作用域-“path”
	 */
	public static final String SCOPE_PATH="path";
	
	private PathScope pathScope;
	
	public PathWebObjectSource(HttpServletRequest request,
			HttpServletResponse response, ServletContext application,
			GenericConverter genericConverter)
	{
		super(request, response, application, genericConverter);
		this.pathScope=new PathScope();
	}

	public PathWebObjectSource(HttpServletRequest request,
			HttpServletResponse response, ServletContext application)
	{
		super(request, response, application);
		this.pathScope=new PathScope();
	}
	
	@Override
	protected Object getWithUnknownScope(String scope, String keyInScope,
			Class<?> objectType)
	{
		if(SCOPE_PATH.equals(scope))
			return getGenericConverter().convert(getPathScope().get(keyInScope), objectType);
		else
			return super.getWithUnknownScope(scope, keyInScope, objectType);
	}

	@Override
	protected void setWithUnknownScope(String scope, String keyInScope,
			Object obj)
	{
		if(SCOPE_PATH.equals(scope))
			getPathScope().put(keyInScope, obj);
		else
			super.setWithUnknownScope(scope, keyInScope, obj);
	}

	protected PathScope getPathScope()
	{
		return this.pathScope;
	}
	
	/**
	 * 路径作用域
	 * @author earthAngry@gmail.com
	 * @date 2010-12-17
	 *
	 */
	protected static class PathScope
	{
		private Map<String, Object> path;
		
		public Object get(String key)
		{
			return path == null ? null : path.get(key);
		}
		
		public void put(String key, Object value)
		{
			if(this.path == null)
				this.path=new HashMap<String, Object>();
			
			this.path.put(key, value);
		}
	}
}
