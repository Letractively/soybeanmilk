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

package org.soybeanMilk.core.bean.converters;

import java.math.BigInteger;

/**
 * 字符串到{@linkplain java.math.BigInteger BigInteger}类型转换器，它调用{@link BigInteger#BigInteger(String)}实现转换。
 * @author earthangry@gmail.com
 * @date 2010-10-3
 */
public class BigIntegerConverter extends AbstractStringTypeConverter
{
	//@Override
	protected Object convertStringToType(String str, Class<?> type) throws Exception
	{
		return new BigInteger(str);
	}
}