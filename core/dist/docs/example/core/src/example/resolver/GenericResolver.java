package example.resolver;

import java.util.List;
import java.util.Set;

/**
 * 泛型支持示例
 */
public class GenericResolver
{
	public static abstract class BaseResolver<T>
	{
		public void simple(T t)
		{
			System.out.println("T value is :"+t.toString()+", type is :"+t.getClass().getName());
		}
		
		public void list(List<T> list)
		{
			if(list != null)
			{
				for(int i=0; i<list.size(); i++)
				{
					T t=list.get(i);
					System.out.println("List<T> "+i+" value is :"+t.toString()+", type is :"+t.getClass().getName());
				}
			}
		}
		
		public void set(Set<T> set)
		{
			if(set != null)
			{
				int i=0;
				for(T t : set)
				{
					System.out.println("Set<T> "+i+" value is :"+t.toString()+", type is :"+t.getClass().getName());
					
					i++;
				}
			}
		}
		
		public void array(T[] array)
		{
			if(array != null)
			{
				int i=0;
				for(T t : array)
				{
					System.out.println("T[] "+i+" value is :"+t.toString()+", type is :"+t.getClass().getName());
					
					i++;
				}
			}
		}
	}
	
	public static class MyResolver extends BaseResolver<Integer>{}
}
