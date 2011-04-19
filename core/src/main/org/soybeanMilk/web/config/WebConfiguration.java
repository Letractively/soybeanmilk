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

package org.soybeanMilk.web.config;

import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.exe.resolver.ResolverFactory;
import org.soybeanMilk.web.exe.th.TypeTargetHandler;

public class WebConfiguration extends Configuration
{
	/**Web动作的目标处理器*/
	private TypeTargetHandler typeTargetHandler;
	
	public WebConfiguration()
	{
		super();
	}

	public WebConfiguration(ResolverFactory resolverFactory)
	{
		super(resolverFactory);
	}
	
	/**
	 * 获取{@linkplain TypeTargetHandler 目标处理器}配置。
	 * @return
	 * @date 2011-4-19
	 */
	public TypeTargetHandler getTypeTargetHandler()
	{
		return typeTargetHandler;
	}

	public void setTypeTargetHandler(TypeTargetHandler typeTargetHandler)
	{
		this.typeTargetHandler = typeTargetHandler;
	}
}
