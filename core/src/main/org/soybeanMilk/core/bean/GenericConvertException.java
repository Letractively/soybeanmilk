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

package org.soybeanMilk.core.bean;

/**
 * 通用转换器异常。
 * @author earthangry@gmail.com
 * @date 2010-10-6
 */
public class GenericConvertException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public GenericConvertException() {
		super();
	}

	public GenericConvertException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenericConvertException(String message) {
		super(message);
	}

	public GenericConvertException(Throwable cause) {
		super(cause);
	}
}
