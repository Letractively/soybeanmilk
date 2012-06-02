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

package org.soybeanMilk.example.resolver;

import java.util.List;
import java.util.Set;

/**
 * SoybeanMilk示例
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class GenericResolver
{
	public static abstract class BaseResolver<T>
	{
		public void simple(T t)
		{
			System.out.println();
			System.out.println("T value is :"+t.toString()+", type is :"+t.getClass().getName());
			System.out.println();
		}
		
		public void list(List<T> list)
		{
			System.out.println();
			if(list != null)
			{
				for(int i=0; i<list.size(); i++)
				{
					T t=list.get(i);
					System.out.println("List<T> "+i+" value is :"+t.toString()+", type is :"+t.getClass().getName());
				}
			}
			System.out.println();
		}
		
		public void set(Set<T> set)
		{
			System.out.println();
			if(set != null)
			{
				int i=0;
				for(T t : set)
				{
					System.out.println("Set<T> "+i+" value is :"+t.toString()+", type is :"+t.getClass().getName());
					
					i++;
				}
			}
			System.out.println();
		}
		
		public void array(T[] array)
		{
			System.out.println();
			if(array != null)
			{
				int i=0;
				for(T t : array)
				{
					System.out.println("T[] "+i+" value is :"+t.toString()+", type is :"+t.getClass().getName());
					
					i++;
				}
			}
			System.out.println();
		}
	}
	
	public static class MyResolver extends BaseResolver<Integer>{}
}
