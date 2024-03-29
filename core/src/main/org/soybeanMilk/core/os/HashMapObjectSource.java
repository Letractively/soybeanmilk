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
import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.core.ObjectSourceException;
import org.soybeanMilk.core.bean.ConvertException;
import org.soybeanMilk.core.bean.GenericConverter;


/**
 * 基于哈希表的一个简单对象源实现。
 * @author earthangry@gmail.com
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
	@SuppressWarnings("unchecked")
	public <T> T get(Serializable key) throws ObjectSourceException
	{
		return (T)getObject(key, null);
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Serializable key, Type expectType) throws ObjectSourceException
	{
		return (T)getObject(key, expectType);
	}
	
	//@Override
	public void set(Serializable key, Object obj) throws ObjectSourceException
	{
		source.put(key, obj);
		
		if(log.isDebugEnabled())
			log.debug("set object "+SbmUtils.toString(obj)+" to "+SbmUtils.toString(this)+" with key "+SbmUtils.toString(key));
	}
	
	/**
	 * 获取对象
	 * @param key
	 * @param expectType
	 * @return
	 * @throws ObjectSourceException
	 * @date 2012-5-16
	 */
	protected Object getObject(Serializable key, Type expectType) throws ObjectSourceException
	{
		Object re = source.get(key);
		
		re=convertGotObject(re, expectType);
		
		if(log.isDebugEnabled())
			log.debug("got object "+SbmUtils.toString(re)+" from "+SbmUtils.toString(this)+" with key "+SbmUtils.toString(key));
		
		return re;
	}
	
	/**
	 * 将从对象源中获取的对象转换到目标类型的对象
	 * @param sourceObj
	 * @param targetType
	 * @return
	 * @throws ObjectSourceException
	 * @date 2012-3-27
	 */
	protected Object convertGotObject(Object sourceObj, Type targetType) throws ObjectSourceException
	{
		if(targetType==null)
			return sourceObj;
		
		GenericConverter converter=getGenericConverter();
		
		if(converter == null)
			return sourceObj;
		else
		{
			try
			{
				return getGenericConverter().convert(sourceObj, targetType);
			}
			catch(ConvertException e)
			{
				throw new ObjectSourceException(e);
			}
		}
	}
}