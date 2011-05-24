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

package org.soybeanMilk.web.exe.th;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.soybeanMilk.SoybeanMilkUtils;
import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.web.WebConstants;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;
import org.soybeanMilk.web.os.WebObjectSource;

/**
 * {@linkplain TargetHandler 目标处理器}的抽象类，提供一些常用的辅助方法。
 * @author earthAngry@gmail.com
 * @date 2011-4-19
 *
 */
public abstract class AbstractTargetHandler implements TargetHandler
{
	/** servlet规范"include"属性-request_uri */
	public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
	/** servlet规范"include"属性-path_info */
	public static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";
	/** servlet规范"include"属性-servlet_path */
	public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
	
	/** servlet规范"forward"属性-path_info */
	public static final String FORWARD_PATH_INFO_ATTRIBUTE = "javax.servlet.forward.path_info";
	/** servlet规范"forward"属性-servlet_path */
	public static final String FORWARD_SERVLET_PATH_ATTRIBUTE = "javax.servlet.forward.servlet_path";
	
	public AbstractTargetHandler(){}
	
	public abstract void handleTarget(WebAction webAction, WebObjectSource webObjectSource)
			throws ServletException, IOException;
	
	/**
	 * 获取动作<code>webAction</code>保存到<code>scope</code>作用域内的所有对象。
	 * @param webAction
	 * @param scope 作用域，参考{@linkplain WebConstants.Scope}类
	 * @param webObjectSource
	 * @return 对象映射表，它关键字的<code>scope</code>作用域前缀已被去除。
	 * @date 2011-5-8
	 */
	public Map<String, Object> getResultInScope(WebAction webAction, String scope, WebObjectSource webObjectSource)
	{
		Map<String, Object> re=new HashMap<String, Object>();
		
		String[] all=getAllInvokeResultKey(webAction, webObjectSource);
		if(all!=null && all.length>0)
		{
			for(String s : all)
			{
				String[] scopedKey=SoybeanMilkUtils.splitByFirstAccessor(s);
				
				if(scopedKey[0].equalsIgnoreCase(scope))
				{
					Object obj=webObjectSource.get(s, null);
					re.put(scopedKey[1], obj);
				}
			}
		}
		
		return re;
	}
	
	/**
	 * 获取{@linkplain WebAction Web动作}包含的所有{@linkplain Invoke 调用}的结果关键字，
	 * 在{@linkplain WebObjectSource Web对象源}中保存着这些关键字对应的对象。
	 * @param webAction
	 * @param webObjectSource
	 * @return 结果关键字，每个关键字都会包含作用域前缀。
	 * @date 2011-4-19
	 */
	public String[] getAllInvokeResultKey(WebAction webAction, WebObjectSource webObjectSource)
	{
		List<String> reList=new ArrayList<String>();
		findAllResultKey(reList, webAction);
		
		return reList.toArray(new String[reList.size()]);
	}
	
	private void findAllResultKey(List<String> re, WebAction webAction)
	{
		List<Executable> exes=webAction.getExecutables();
		if(exes==null)
			return;
		
		for(Executable exe : exes)
		{
			if(exe instanceof Invoke)
			{
				String resultKey=(String)((Invoke)exe).getResultKey();
				if(resultKey != null)
				{
					if(SoybeanMilkUtils.splitByFirstAccessor(resultKey).length == 1)
						resultKey=WebConstants.Scope.REQUEST+Constants.ACCESSOR+resultKey;
					
					re.add(resultKey);
				}
			}
			else if(exe instanceof WebAction)
			{
				findAllResultKey(re, (WebAction)exe);
			}
			else
				throw new UnsupportedOperationException("unknown Executable object of type "+exe.getClass().getName());
		}
	}
	
	/**
	 * 获取目标的实际URL（URL中的变量已被取值），
	 * 通常在{@linkplain #handleTarget(WebAction, WebObjectSource)}中你需要先调用此方法来获取实际的目标URL。
	 * @param webAction
	 * @param objectSource
	 * @return 目标URL，没有则返回<code>null</code>
	 * @date 2011-4-19
	 */
	public String getActualTargetUrl(WebAction webAction, ObjectSource objectSource)
	{
		String re=null;
		
		Target target=webAction.getTarget();
		if(target != null)
			re=evaluateVariableUrl(target.getUrl(), objectSource);
		
		return re;
	}
	
	/**
	 * 求变量URL的值
	 * @param variableUrl URL，它可能包含"{...}"格式的变量
	 * @param objectSource
	 * @return
	 */
	public static String evaluateVariableUrl(String variableUrl, ObjectSource objectSource)
	{
		if(variableUrl == null)
			return null;
		
		StringBuffer result=new StringBuffer();
		
		int i=0, len=variableUrl.length();
		for(;i<len;i++)
		{
			char c=variableUrl.charAt(i);
			
			if(c == WebConstants.VARIABLE_QUOTE_LEFT)
			{
				int j=i+1;
				int start=j;
				for(;j<len && (c=variableUrl.charAt(j))!=WebConstants.VARIABLE_QUOTE_RIGHT;)
					j++;
				
				String var=variableUrl.substring(start, j);
				if(c == WebConstants.VARIABLE_QUOTE_RIGHT)
				{
					String value=(String)objectSource.get(var, String.class);
					result.append(value==null ? "null" : value);
				}
				else
				{
					result.append(WebConstants.VARIABLE_QUOTE_LEFT);
					result.append(var);
				}
				
				i=j;
			}
			else
				result.append(c);
		}
		
		return result.toString();
	}
	
	/**
	 * 是否是JSP动态"include"请求
	 * @param request
	 * @return
	 */
	public static boolean isJspIncludeRequest(ServletRequest request)
	{
		return (request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE) != null);
	}
}
