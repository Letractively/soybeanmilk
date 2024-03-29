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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.SbmUtils;
import org.soybeanMilk.web.WebObjectSource;
import org.soybeanMilk.web.exe.WebAction;
import org.soybeanMilk.web.exe.WebAction.Target;

/**
 * "redirect"类型目标处理器。
 * @author earthangry@gmail.com
 * @date 2011-4-19
 *
 */
public class RedirectTargetHandler extends AbstractTargetHandler
{
	private static Log log=LogFactory.getLog(RedirectTargetHandler.class);
	
	public RedirectTargetHandler()
	{
		super();
	}
	
	//@Override
	public void handleTarget(WebAction webAction, WebObjectSource webObjectSource)
			throws ServletException, IOException
	{
		String url=getActualTargetUrl(webAction, webObjectSource);
		
		if(url==null)
			throw new NullPointerException("the url must not be null in "+SbmUtils.toString(Target.REDIRECT)+" type target");
		
		HttpServletRequest request = webObjectSource.getRequest();
		HttpServletResponse response=webObjectSource.getResponse();
		
		//在语境内
		if(url.startsWith("/"))
			response.sendRedirect(request.getContextPath()+url);
		else
			response.sendRedirect(url);
		
		if(log.isDebugEnabled())
			log.debug("redirect "+SbmUtils.toString(url)+" for request");
	}
}
