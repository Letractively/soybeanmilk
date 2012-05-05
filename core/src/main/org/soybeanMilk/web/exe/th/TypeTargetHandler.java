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

import org.soybeanMilk.web.exe.WebAction.Target;

/**
 * 类型目标处理器，它可以根据{@linkplain Target 目标}类型来处理目标。
 * @author earthangry@gmail.com
 * @date 2011-4-19
 *
 */
public interface TypeTargetHandler extends TargetHandler
{
	/**
	 * 添加一个{@linkplain TargetHandler 目标处理器}，用于处理<code>type</code>类型的目标。
	 * @param type 可处理的{@linkplain Target 目标}类型。
	 * @param targetHandler 目标处理器
	 * @date 2011-4-19
	 */
	void addTargetHandler(String type, TargetHandler targetHandler);
	
	/**
	 * 获取能处理给定目标类型的处理器。
	 * @param type
	 * @return
	 * @date 2011-4-19
	 */
	TargetHandler getTargetHandler(String type);
}
