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

package org.soybeanMilk.core.os;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.GenericConverter;


/**
 * 基于哈希表的一个简单对象源实现。
 * @author earthAngry@gmail.com
 * @date 2010-10-1
 */
public class HashMapObjectSource extends ConvertableObjectSource
{
	private static Log log=LogFactory.getLog(HashMapObjectSource.class);
	
	private HashMap<Serializable,Object> source;
	
	public HashMapObjectSource()
	{
		this(null);
	}
	
	public HashMapObjectSource(GenericConverter genericConverter)
	{
		this.source = new HashMap<Serializable, Object>();
		super.setGenericConverter(genericConverter);
	}

	//@Override
	public Object get(Serializable key, Type expectType) throws ObjectSourceException
	{
		Object re = source.get(key);
		
		if(expectType != null)
		{
			GenericConverter cvt = getGenericConverter();
			if(cvt == null)
			{
				if(re==null && SoybeanMilkUtils.isPrimitive(expectType))
					throw new ObjectSourceException("the object of key '"+key+"' is null, but primitive type needed");
				else
					return re;
			}
			else
				re = cvt.convert(re, expectType);
		}
		if(log.isDebugEnabled())
			log.debug("get '"+re+"' with key '"+key+"' from "+this);
		
		return re;
	}
	
	//@Override
	public void set(Serializable key, Object obj) throws ObjectSourceException
	{
		source.put(key, obj);
		
		if(log.isDebugEnabled())
			log.debug("save '"+obj+"' with key '"+key+"' into "+this);
	}
}