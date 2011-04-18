package org.soybeanMilk.web;

import java.util.Collection;

import org.soybeanMilk.core.DefaultExecutor;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecutableNotFoundException;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.web.vp.PathNode;
import org.soybeanMilk.web.vp.VariablePath;
import org.soybeanMilk.web.vp.VariablePathMatcher;

public class WebExecutor extends DefaultExecutor
{
	private VariablePathMatcher variablePathMatcher;
	
	public WebExecutor()
	{
		this(null);
	}

	public WebExecutor(Configuration configuration)
	{
		super(configuration);
		
		if(isEnableVariablePath() && getConfiguration()!=null)
		{
			//初始化变量路径匹配器并且设为非空以便使用
			Collection<String> exeNames=getConfiguration().getExecutableNames();
			VariablePathMatcher vpm=new VariablePathMatcher(exeNames);
			setVariablePathMatcher(vpm);
		}
	}

	public VariablePathMatcher getVariablePathMatcher()
	{
		return variablePathMatcher;
	}

	protected void setVariablePathMatcher(VariablePathMatcher variablePathMatcher)
	{
		this.variablePathMatcher = variablePathMatcher;
	}
	
	@Override
	protected Executable findExecutable(String executableName, ObjectSource objSource)
			throws ExecuteException, ExecutableNotFoundException
	{
		Executable re=super.findExecutable(executableName, objSource);
		if(re==null && isEnableVariablePath())
		{
			VariablePath valuePath=new VariablePath(executableName);
			VariablePath targetPath=getVariablePathMatcher().getMatched(valuePath);
			if(targetPath != null)
				re=getConfiguration().getExecutable(targetPath.getVariablePath());
			
			if(re != null)
			{
				PathNode[] pathNodes=targetPath.getPathNodes();
				for(int i=0;i<pathNodes.length;i++)
				{
					if(pathNodes[i].isVariable())
						objSource.set(pathNodes[i].getNodeValue(), valuePath.getPathNode(i).getNodeValue());
				}
			}
		}
		
		return re;
	}

	/**
	 * 是否开启变量路径功能
	 * @return
	 */
	protected boolean isEnableVariablePath()
	{
		return true;
	}
}
