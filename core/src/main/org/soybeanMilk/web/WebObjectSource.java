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

package org.soybeanMilk.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.soybeanMilk.core.ObjectSource;

/**
 * Web对象源，它实例的生命周期应该与一次请求的生命周期相同
 * @author earthangry@gmail.com
 * @date 2011-12-11
 */
public interface WebObjectSource extends ObjectSource
{
	/**
	 * 获取当前{@linkplain HttpServletRequest 请求}对象
	 * @return
	 * @date 2011-12-11
	 */
	HttpServletRequest getRequest();
	
	/**
	 * 获取当前{@linkplain HttpServletResponse 响应}对象
	 * @return
	 * @date 2011-12-11
	 */
	HttpServletResponse getResponse();
	
	/**
	 * 获取{@linkplain ServletContext Servlet语境}对象
	 * @return
	 * @date 2011-12-11
	 */
	ServletContext getApplication();
}