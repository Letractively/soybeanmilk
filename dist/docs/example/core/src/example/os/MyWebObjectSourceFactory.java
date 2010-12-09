package example.os;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.web.os.WebObjectSource;
import org.soybeanMilk.web.servlet.WebObjectSourceFactory;

public class MyWebObjectSourceFactory implements WebObjectSourceFactory
{
	@Override
	public WebObjectSource create(HttpServletRequest request, HttpServletResponse response,
			ServletContext application)
	{
		MyWebObjectSource os=new MyWebObjectSource(request, response, application);
		Context vc=new VelocityContext();
		os.setVelocityContext(vc);
		
		request.setAttribute("myVelocityContext", vc);
		
		return os;
	}
	
	protected static class MyWebObjectSource extends WebObjectSource
	{
		private Context velocityContext;
		public Context getVelocityContext() {
			return velocityContext;
		}
		public void setVelocityContext(Context velocityContext) {
			this.velocityContext = velocityContext;
		}
		@Override
		protected Object getWithUnknownScope(String scope, String keyInScope, Class<?> objectType)
		{
			if("vm".equals(scope))
				return velocityContext.get(keyInScope);
			else
				return super.getWithUnknownScope(scope, keyInScope, objectType);
		}
		@Override
		protected void setWithUnknownScope(String scope, String keyInScope, Object obj)
		{
			if("vm".equals(scope))
				velocityContext.put(keyInScope, obj);
			else
				super.setWithUnknownScope(scope, keyInScope, obj);
		}
		
		public MyWebObjectSource(HttpServletRequest request,
				HttpServletResponse response, ServletContext application,
				GenericConverter genericConverter)
		{
			super(request, response, application, genericConverter);
		}
	
		public MyWebObjectSource(HttpServletRequest request,
				HttpServletResponse response, ServletContext application)
		{
			super(request, response, application);
		}
	}
}
