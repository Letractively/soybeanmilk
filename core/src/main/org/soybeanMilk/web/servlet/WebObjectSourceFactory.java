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

package org.soybeanMilk.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.soybeanMilk.web.os.WebObjectSource;

/**
 * {@linkplain WebObjectSource WEB对象源}工厂，{@linkplain DispatchServlet}使用它来为请求创建WEB对象源。
 * @author earthAngry@gmail.com
 * @date 2010-12-9
 */
public interface WebObjectSourceFactory
{
	/**
	 * 为请求创建WEB对象源
	 * @param request
	 * @param response
	 * @param application
	 * @return
	 */
	WebObjectSource create(HttpServletRequest request, HttpServletResponse response, ServletContext application);
}