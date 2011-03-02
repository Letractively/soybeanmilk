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

package org.soybeanMilk.web.exe;

import java.io.Serializable;
import java.util.List;

import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.exe.Action;


/**
 * WEB动作，多一个{@link Target Target}属性。
 * @author earthAngry@gmail.com
 * @date 2010-10-4
 */
public class WebAction extends Action
{
	private static final long serialVersionUID = -1806187193060075225L;
	
	private Target target;
	
	public WebAction()
	{
		super();
	}
	
	public WebAction(String name)
	{
		this(name, null, null);
	}
	
	public WebAction(String name, List<Executable> executables)
	{
		this(name, executables, null);
	}
	
	public WebAction(String name, List<Executable> executables, Target target)
	{
		super(name, executables);
		this.target=target;
	}

	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}
	
	/**
	 * 动作的目标，包括目标URL和类型（转向、重定向）
	 * @author earthAngry@gmail.com
	 * @date 2010-10-4
	 */
	public static class Target implements Serializable
	{
		private static final long serialVersionUID = -5061315375843462989L;
		
		/**目标类型：转向*/
		public static final String FORWARD="forward";
		/**目标类型：重定向*/
		public static final String REDIRECT="redirect";
		
		/**目标URL*/
		private String url;
		/**目标类型*/
		private String type;
		
		public Target()
		{
			super();
		}

		public Target(String url, String type)
		{
			super();
			this.url = url;
			this.type = type;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		//@Override
		public String toString()
		{
			return getClass().getSimpleName()+" [url=" + url + ", type=" + type + "]";
		}
	}
}