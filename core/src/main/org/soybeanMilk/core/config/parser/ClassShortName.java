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

package org.soybeanMilk.core.config.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名简写工具类，它可以由简写获取类型
 * @author earthAngry@gmail.com
 * @date 2010-12-31
 *
 */
public class ClassShortName
{
	private static Class<?>[] simpleNameClasses=new Class<?>[]{
			boolean.class, 		boolean[].class, 	Boolean.class, 		Boolean[].class,
			byte.class, 		byte[].class, 		Byte.class, 		Byte[].class, 
			char.class, 		char[].class, 		Character.class, 	Character[].class,
			double.class, 		double[].class, 	Double.class, 		Double[].class,
			float.class, 		float[].class, 		Float.class, 		Float[].class,
			int.class, 			int[].class, 		Integer.class, 		Integer[].class,
			long.class, 		long[].class, 		Long.class, 		Long[].class,
			short.class, 		short[].class, 		Short.class, 		Short[].class,
			String.class, 		String[].class,
			BigDecimal.class, 	BigDecimal[].class,
			BigInteger.class, 	BigInteger[].class,
			Date.class, 		Date[].class
	};
	
	private static Class<?>[] canonicalNameClasses=new Class<?>[]{
			java.sql.Date.class,		java.sql.Date[].class,
			java.sql.Time.class,		java.sql.Time[].class,
			java.sql.Timestamp.class,	java.sql.Timestamp[].class
	};
	
	private static Map<String, Class<?>> nameMaps=new HashMap<String, Class<?>>();
	static
	{
		for(Class<?> c : simpleNameClasses)
			nameMaps.put(c.getSimpleName(), c);
		
		for(Class<?> c : canonicalNameClasses)
			nameMaps.put(c.getCanonicalName(), c);
	}
	
	/**
	 * 由简称获取类型
	 * @param shortName
	 * @return
	 */
	public static Class<?> get(String shortName)
	{
		if(shortName==null || shortName.length()==0)
			return null;
		
		return nameMaps.get(shortName);
	}
}
