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

package test.unit.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.bean.GenericType;

import test.unit.core.TestGenericType.C.CInner;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
public class TestGenericType
{
	@Before
	public void setUp() throws Exception{}

	@After
	public void tearDown() throws Exception{}
	
	@Test
	public void getGenericType_typeNoBound()
	{
		@SuppressWarnings("rawtypes")
		Class<A> clazz=A.class;
		
		//TypeVariable
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "typeVariable", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isTypeVariable());
			Assert.assertEquals(Object.class, re.getActualClass());
		}
		
		//ParameterizedType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "paramerized", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isParameterizedType());
			Assert.assertEquals(List.class, re.getActualClass());
			Assert.assertEquals(Object.class, re.getParamClasses()[0]);
		}
		
		//GenericArrayType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "array", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isGenericArrayType());
			Assert.assertEquals(Object.class, re.getComponentClass());
		}
		
		//WildcardType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "wildcard", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isParameterizedType());
			Assert.assertEquals(List.class, re.getActualClass());
			Assert.assertEquals(Object.class, re.getParamClasses()[0]);
		}
	}
	
	@Test
	public void getGenericType_typeWithBound()
	{
		@SuppressWarnings("rawtypes")
		Class<B> clazz=B.class;
		
		//TypeVariable
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "typeVariable", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isTypeVariable());
			Assert.assertEquals(Bean.class, re.getActualClass());
		}
		
		//ParameterizedType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "paramerized", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isParameterizedType());
			Assert.assertEquals(List.class, re.getActualClass());
			Assert.assertEquals(Bean.class, re.getParamClasses()[0]);
		}
		
		//GenericArrayType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "array", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isGenericArrayType());
			Assert.assertEquals(Bean.class, re.getComponentClass());
		}
		
		//WildcardType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "wildcard", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isParameterizedType());
			Assert.assertEquals(List.class, re.getActualClass());
			Assert.assertEquals(Bean.class, re.getParamClasses()[0]);
		}
	}
	
	@Test
	public void getGenericType_typeLocalInMethod()
	{
		@SuppressWarnings("rawtypes")
		Class<B> clazz=B.class;
		
		Method m=SoybeanMilkUtils.findMethodThrow(clazz, "local", 1);
		Type t=m.getGenericParameterTypes()[0];
		
		GenericType re=GenericType.getGenericType(t, clazz);
		
		Assert.assertTrue(re.isTypeVariable());
		Assert.assertEquals(Object.class, re.getActualClass());
	}
	
	@Test
	public void getGenericType_typeInitializedInSubClass()
	{
		Class<C> clazz=C.class;
		
		//TypeVariable
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "typeVariable", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isTypeVariable());
			Assert.assertEquals(Bean1.class, re.getActualClass());
		}
		
		//ParameterizedType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "paramerized", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isParameterizedType());
			Assert.assertEquals(List.class, re.getActualClass());
			Assert.assertEquals(Bean1.class, re.getParamClasses()[0]);
		}
		
		//GenericArrayType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "array", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isGenericArrayType());
			Assert.assertEquals(Bean1.class, re.getComponentClass());
		}
		
		//WildcardType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "wildcard", 1);
			Type t=m.getGenericParameterTypes()[0];
			
			GenericType re=GenericType.getGenericType(t, clazz);
			
			Assert.assertTrue(re.isParameterizedType());
			Assert.assertEquals(List.class, re.getActualClass());
			Assert.assertEquals(Bean1.class, re.getParamClasses()[0]);
		}
	}
	
	@Test
	public void getGenericType_innerClass()
	{
		Class<CInner> clazz=CInner.class;
		
		//TypeVariable
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "typeVariable", 2);
			Type t0=m.getGenericParameterTypes()[0];
			Type t1=m.getGenericParameterTypes()[1];
			
			GenericType re0=GenericType.getGenericType(t0, clazz);
			GenericType re1=GenericType.getGenericType(t1, clazz);
			
			Assert.assertTrue(re0.isTypeVariable());
			Assert.assertEquals(Bean1.class, re0.getActualClass());
			
			Assert.assertTrue(re1.isTypeVariable());
			Assert.assertEquals(Bean2.class, re1.getActualClass());
		}
		
		//ParameterizedType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "paramerized", 2);
			Type t0=m.getGenericParameterTypes()[0];
			Type t1=m.getGenericParameterTypes()[1];
			
			GenericType re0=GenericType.getGenericType(t0, clazz);
			GenericType re1=GenericType.getGenericType(t1, clazz);
			
			Assert.assertTrue(re0.isParameterizedType());
			Assert.assertEquals(List.class, re0.getActualClass());
			Assert.assertEquals(Bean1.class, re0.getParamClasses()[0]);
			
			Assert.assertTrue(re1.isParameterizedType());
			Assert.assertEquals(List.class, re1.getActualClass());
			Assert.assertEquals(Bean2.class, re1.getParamClasses()[0]);
		}
		
		//GenericArrayType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "array", 2);
			Type t0=m.getGenericParameterTypes()[0];
			Type t1=m.getGenericParameterTypes()[1];
			
			GenericType re0=GenericType.getGenericType(t0, clazz);
			GenericType re1=GenericType.getGenericType(t1, clazz);
			
			Assert.assertTrue(re0.isGenericArrayType());
			Assert.assertEquals(Bean1.class, re0.getComponentClass());
			
			Assert.assertTrue(re1.isGenericArrayType());
			Assert.assertEquals(Bean2.class, re1.getComponentClass());
		}
		
		//WildcardType
		{
			Method m=SoybeanMilkUtils.findMethodThrow(clazz, "wildcard", 2);
			Type t0=m.getGenericParameterTypes()[0];
			Type t1=m.getGenericParameterTypes()[1];
			
			GenericType re0=GenericType.getGenericType(t0, clazz);
			GenericType re1=GenericType.getGenericType(t1, clazz);
			
			Assert.assertTrue(re0.isParameterizedType());
			Assert.assertEquals(List.class, re0.getActualClass());
			Assert.assertEquals(Bean1.class, re0.getParamClasses()[0]);
			
			Assert.assertTrue(re1.isParameterizedType());
			Assert.assertEquals(List.class, re1.getActualClass());
			Assert.assertEquals(Bean2.class, re1.getParamClasses()[0]);
		}
	}
	
	public static interface A<T>
	{
		void typeVariable(T t);
		
		void paramerized(List<T> param);
		
		void array(T[] param);
		
		void wildcard(List<? extends T> param);
	}
	
	public static class B<T extends Bean> implements A<T>
	{
		public void typeVariable(T t){}

		public void paramerized(List<T> param){}

		public void array(T[] param){}

		public void wildcard(List<? extends T> param){}
		
		public <D> void local(D b){}

		public class Inner<G>
		{
			public void typeVariable(T t, G g){}

			public void paramerized(List<T> param, List<G> param1){}
			
			public void array(T[] param, G[] param1){}
			
			public void wildcard(List<? extends T> param, List<? extends G> param1){}
		}
	}
	
	public static class C extends B<Bean1>
	{
		public class CInner extends test.unit.core.TestGenericType.B<Bean1>.Inner<Bean2>{}
	}
	
	public static class Bean{}
	
	public static class Bean1 extends Bean
	{
		private Integer id;
		private String name;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class Bean2
	{
		private Integer id;
		private String name;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
