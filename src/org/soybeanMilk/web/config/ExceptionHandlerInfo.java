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

import java.io.Serializable;

import org.soybeanMilk.core.Executable;

/**
 * 异常处理器信息，保存在配置对象中
 * @author earthAngry@gmail.com
 * @date 2010-7-19
 *
 */
public class ExceptionHandlerInfo implements Serializable
{
	private static final long serialVersionUID = -6053471765972861217L;
	
	private Executable exceptionHandler;
	private String exceptionArgKey;
	
	public ExceptionHandlerInfo(){}

	public ExceptionHandlerInfo(Executable exceptionHandler, String exceptionArgKey)
	{
		super();
		this.exceptionHandler = exceptionHandler;
		this.exceptionArgKey = exceptionArgKey;
	}

	public Executable getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(Executable exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public String getExceptionArgKey() {
		return exceptionArgKey;
	}

	public void setExceptionArgKey(String exceptionArgKey) {
		this.exceptionArgKey = exceptionArgKey;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [exceptionArgKey=" + exceptionArgKey
				+ ", exceptionHandler=" + exceptionHandler + "]";
	}
}