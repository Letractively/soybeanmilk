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

import org.soybeanMilk.core.Constants;

/**
 * WEB常量类。
 * @author earthAngry@gmail.com
 * @date 2010-10-6
 */
public class WebConstants extends Constants
{
	/**默认WEB配置文件位置*/
	public static final String DEFAULT_CONFIG_FILE="/WEB-INF/"+Constants.DEFAULT_CONFIG_FILE;
	
	/**字符串变量左括弧*/
	public static final char VARIABLE_QUOTE_LEFT='{';
	/**字符串变量右括弧*/
	public static final char VARIABLE_QUOTE_RIGHT='}';
	
	/**默认的编码*/
	public static final String DEFAULT_ENCODING="UTF-8";
	
	/**
	 * servlet允许的初始化配置参数
	 * @author earthAngry@gmail.com
	 * @date 2010-10-6
	 */
	public static final class ServletInitParams
	{
		/**
		 * 编码。如果不指定，则框架会使用{@link WebConstants#DEFAULT_ENCODING}编码
		 */
		public static final String ENCODING="encoding";
		
		/**
		 * 配置文件位置。如果不指定，则框架会使用{@link WebConstants#DEFAULT_CONFIG_FILE}配置
		 */
		public static final String SOYBEAN_MILK_CONFIG="soybean-milk-config";
		
		/**
		 * 外部解决对象工厂在应用中的关键字。如果指定，框架会搜索并整合它
		 */
		public static final String EXTERNAL_RESOLVER_FACTORY_KEY="external-resolver-factory-key";
		
		/**
		 * WEB对象源工厂的类名。框架将使用它来为请求创建WEB对象源
		 */
		public static final String WEB_OBJECT_SOURCE_FACTORY_CLASS="webObjectSoruce-factory-class";
		
		/**
		 * WEB执行器对象在应用中的存储关键字。如果指定，框架会将创建的WEB执行器存储到应用中；如果不指定，则不会存储
		 */
		public static final String APPLICATION_EXECUTOR_KEY="application-executor-key";
	}
	
	/**
	 * 框架的Web对象源支持的对象源作用域
	 * @author earthAngry@gmail.com
	 * @date 2010-10-7
	 */
	public static class Scope
	{
		/**
		 * 请求参数映射表，它是一个Map对象
		 */
		public static final String PARAM="param";
		
		/**
		 * 请求，它表示HttpServletRequest对象
		 */
		public static final String REQUEST="request";
		
		/**
		 * 会话，它表示HttpSession对象
		 */
		public static final String SESSION="session";
		
		/**
		 * 应用，它表示ServletContext对象
		 */
		public static final String APPLICATION="application";
		
		/**
		 * 回应，它表示HttpServletResponse对象
		 */
		public static final String RESPONSE="response";
		
		/**
		 * WEB对象源本身
		 */
		public static final String OBJECT_SOURCE="objectSource";
	}
}