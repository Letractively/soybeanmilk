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

package org.soybeanMilk.web.exe.th;

import java.io.IOException;

import javax.servlet.ServletException;

import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;

/**
 * {@linkplain WebAction Web动作}的{@linkplain Target 目标}处理器。
 * @author earthAngry@gmail.com
 * @date 2011-4-19
 *
 */
public interface TargetHandler
{
	/**
	 * 处理{@linkplain WebAction Web动作}的目标。
	 * @param webAction {@linkplain WebAction Web动作}对象，它也可能没有定义{@linkplain Target 目标}属性。
	 * @param webObjectSource 此{@linkplain WebAction Web动作}使用的{@linkplain WebObjectSource Web对象源}。
	 * @throws ServletException
	 * @throws IOException
	 * @date 2011-4-19
	 */
	void handleTarget(WebAction webAction, WebObjectSource webObjectSource)
			throws ServletException, IOException;
}