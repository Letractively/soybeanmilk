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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.soybeanMilk.core.Constants;
import org.soybeanMilk.core.Executable;
import org.soybeanMilk.core.ExecuteException;
import org.soybeanMilk.core.ObjectSource;
import org.soybeanMilk.core.bean.Converter;
import org.soybeanMilk.core.bean.DefaultGenericConverter;
import org.soybeanMilk.core.bean.GenericConverter;
import org.soybeanMilk.core.config.Configuration;
import org.soybeanMilk.core.config.InterceptorInfo;
import org.soybeanMilk.core.exe.Action;
import org.soybeanMilk.core.exe.Invoke;
import org.soybeanMilk.core.exe.Invoke.Arg;
import org.soybeanMilk.core.resolver.DefaultResolverFactory;
import org.soybeanMilk.core.resolver.FactoryResolverProvider;
import org.soybeanMilk.core.resolver.ResolverFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 配置解析器
 * @author earthAngry@gmail.com
 * @date 2010-10-1
 */
public class ConfigurationParser
{
	private static Log log=LogFactory.getLog(ConfigurationParser.class);
	
	protected static final String TAG_ROOT="soybean-milk";
	
	protected static final String TAG_GLOBAL_CONFIG="global-config";
	protected static final String TAG_GENERIC_CONVERTER="generic-converter";
	protected static final String TAG_GENERIC_CONVERTER_ATTR_CLASS="class";
	protected static final String TAG_CONVERTER="converter";
	protected static final String TAG_CONVERTER_ATTR_SRC="src";
	protected static final String TAG_CONVERTER_ATTR_TARGET="target";
	protected static final String TAG_CONVERTER_ATTR_CLASS=TAG_GENERIC_CONVERTER_ATTR_CLASS;
	
	protected static final String TAG_INTERCEPROT="interceptor";
	protected static final String TAG_INTERCEPROT_ATTR_BEFORE="before";
	protected static final String TAG_INTERCEPROT_ATTR_AFTER="after";
	protected static final String TAG_INTERCEPROT_ATTR_EXCEPTION="exception";
	protected static final String TAG_INTERCEPROT_ATTR_EXECUTION_KEY="execution-key";
	
	protected static final String TAG_INCLUDES="includes";
	protected static final String TAG_FILE="file";
	
	protected static final String TAG_RESOLVERS="resolvers";
	protected static final String TAG_RESOLVER="resolver";
	protected static final String TAG_RESOLVER_ATTR_ID="id";
	protected static final String TAG_RESOLVER_ATTR_CLASS="class";
	
	protected static final String TAG_EXECUTABLES="executables";
	
	protected static final String TAG_ACTION="action";
	protected static final String TAG_ACTION_ATTR_NAME="name";
	
	protected static final String TAG_INVOKE="invoke";
	protected static final String TAG_INVOKE_ATTR_NAME=TAG_ACTION_ATTR_NAME;
	protected static final String TAG_INVOKE_ATTR_METHOD="method";
	protected static final String TAG_INVOKE_ATTR_RESOLVER_OBJECT="resolver";
	protected static final String TAG_INVOKE_ATTR_RESOLVER_CLASS="resolver-class";
	protected static final String TAG_INVOKE_ATTR_RESULT_KEY="result-key";
	
	protected static final String TAG_ARG="arg";
	
	protected static final String TAG_REF="ref";
	protected static final String TAG_REF_ATTR_NAME="name";
	
	
	private Document document;
	private Configuration configuration;
	
	private Document currentDocument;
	private List<Document> modules;
	
	/**
	 * 创建解析器，不预设存储配置对象
	 */
	public ConfigurationParser()
	{
		this(null);
	}
	
	/**
	 * 创建解析器，并预设存储配置对象，所有的解析结果都将保存到这个配置中
	 * @param configuration 预设配置对象
	 */
	public ConfigurationParser(Configuration configuration)
	{
		setConfiguration(configuration);
	}
	
	/**
	 * 取得解析结果
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * 设置解析配置对象，所有的解析结果将保存到该配置中，它应该在解析前调用
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * 取得解析文档对象
	 * @return
	 */
	public Document getDocument()
	{
		return this.document;
	}
	
	/**
	 * 设置解析文档对象
	 * @param document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	/**
	 * 取得模块文档
	 * @return
	 */
	public List<Document> getModules() {
		return modules;
	}
	
	/**
	 * 设置模块文档
	 * @param modules
	 */
	public void setModules(List<Document> modules) {
		this.modules = modules;
	}
	
	/**
	 * 从默认配置文件解析
	 * @return
	 */
	public Configuration parse()
	{
		return parse((String)null);
	}
	
	/**
	 * 从给定配置文件解析
	 * @param configFile 配置文件，可以类路径资源文件，也可以是文件系统文件
	 * @return
	 */
	public Configuration parse(String configFile)
	{
		if(configFile==null || configFile.length()==0)
			configFile=getDefaultConfigFile();
		
		setDocument(parseDocument(configFile));
		
		parseAll();
		return getConfiguration();
	}
	
	/**
	 * 从输入流解析
	 * @param in
	 * @return
	 */
	public Configuration parse(InputStream in)
	{
		setDocument(parseDocument(in));
		
		parseAll();
		return getConfiguration();
	}
	
	/**
	 * 从文档对象解析
	 * @param document
	 * @return
	 */
	public Configuration parse(Document document)
	{
		setDocument(document);
		
		parseAll();
		return getConfiguration();
	}
	
	/**
	 * 解析，如果你没有预设配置对象，这个方法将自动创建
	 * @return 解析结果
	 */
	protected void parseAll()
	{
		if(getConfiguration() == null)
			setConfiguration(createConfigurationInstance());
		
		setCurrentDocument(document);
		
		parseGlobalConfigs();
		
		parseIncludes();
		
		parseResolvers();
		if(modules !=null)
		{
			for(Document doc : modules)
			{
				setCurrentDocument(doc);
				parseResolvers();
			}
		}
		
		setCurrentDocument(document);
		
		parseExecutables();
		if(modules !=null)
		{
			for(Document doc : modules)
			{
				setCurrentDocument(doc);
				parseExecutables();
			}
		}
		
		setCurrentDocument(document);
		
		parseRefs();
	}
	
	/**
	 * 解析全局配置
	 */
	protected void parseGlobalConfigs()
	{
		Element parent=getSingleElementByTagName(getCurrentDocumentRoot(), TAG_GLOBAL_CONFIG);
		
		parseGenericConverter(parent);
		
		parseInterceptor(parent);
	}
	
	/**
	 * 解析包含的模块配置
	 */
	protected void parseIncludes()
	{
		List<Element> files=getChildrenByTagName(getSingleElementByTagName(getCurrentDocumentRoot(), TAG_INCLUDES), TAG_FILE);
		
		if(files == null || files.isEmpty())
			return;
		
		this.modules = new ArrayList<Document>();
		
		for(Element el : files)
		{
			String fileName=getTextContent(el);
			assertNotEmpty(fileName, "<"+TAG_FILE+">'s content must not be null");
			
			Document[] docs=parseDocuments(fileName);
			if(docs != null)
			{
				for(Document d : docs)
					this.modules.add(d);
			}
		}
	}
	
	/**
	 * 解析并构建解决对象
	 */
	protected void parseResolvers()
	{
		List<Element> children=getChildrenByTagName(getSingleElementByTagName(getCurrentDocumentRoot(), TAG_RESOLVERS), TAG_RESOLVER);
		
		if(children!=null && !children.isEmpty())
		{
			ResolverFactory rf = configuration.getResolverFactory();
			if(rf == null)
			{
				rf= createResolverFactoryInstance();
				configuration.setResolverFactory(rf);
			}
			
			if(!(rf instanceof DefaultResolverFactory))
				throw new ParseException("the resolver factory you set must be instance of '"+DefaultResolverFactory.class.getName()+"'");
			
			DefaultResolverFactory drf=(DefaultResolverFactory)rf;
			
			for(Element e : children)
			{
				String id=getAttributeIngoreEmpty(e,TAG_RESOLVER_ATTR_ID);
				assertNotEmpty(id,"<"+TAG_RESOLVER+"> attribute ["+TAG_RESOLVER_ATTR_ID+"] must not be null");
				String clazz=getAttributeIngoreEmpty(e,TAG_RESOLVER_ATTR_CLASS);
				assertNotEmpty(clazz,"<"+TAG_RESOLVER+"> of id '"+id+"' attribute ["+TAG_RESOLVER_ATTR_CLASS+"] must not be null");
				
				Object resolver=createClassInstance(clazz);
				
				if(log.isDebugEnabled())
					log.debug("parsed resolver instance of Class '"+resolver.getClass().getName()+"' with id '"+id+"'");
				
				drf.addResolver(id,resolver);
			}
		}
	}
	
	/**
	 * 解析并构建可执行对象
	 */
	protected void parseExecutables()
	{
		Element executables=getSingleElementByTagName(getCurrentDocumentRoot(),TAG_EXECUTABLES);
		if(executables != null)
		{
			List<Element> children=getChildrenByTagName(executables, null);
			
			if(children != null)
			{
				for(Element e : children)
				{
					Executable executable=createExecutableInstance(e.getTagName());
					
					setExecutableProperties(executable,e);
					
					if(log.isDebugEnabled())
						log.debug("parsed '"+executable+"'");
					
					configuration.addExecutable(executable);
				}
			}
		}
	}
	
	/**
	 * 处理引用
	 */
	protected void parseRefs()
	{
		processExecutableRefs();
		processInterceptorInfoRefs();
	}
	
	/**
	 * 解析通用转换器
	 * @param parent
	 */
	protected void parseGenericConverter(Element parent)
	{
		Element cvtEl = getSingleElementByTagName(parent, TAG_GENERIC_CONVERTER);
		
		String clazz = cvtEl==null ? null : getAttributeIngoreEmpty(cvtEl, TAG_GENERIC_CONVERTER_ATTR_CLASS);
		
		GenericConverter genericConverter = configuration.getGenericConverter();
		if(genericConverter == null)
		{
			if(clazz==null || clazz.length()==0)
				genericConverter = createGenericConverterInstance();
			else
				genericConverter = (GenericConverter)createClassInstance(clazz);
			
			configuration.setGenericConverter(genericConverter);
		}
		
		parseSupportConverters(genericConverter, cvtEl);
	}
	
	/**
	 * 解析父元素下的支持转换器并加入给定的通用转换器中
	 * @param genericConverter
	 * @param parent
	 */
	protected void parseSupportConverters(GenericConverter genericConverter, Element parent)
	{
		List<Element> children=getChildrenByTagName(parent, TAG_CONVERTER);
		if(children==null || children.isEmpty())
			return;
		
		for(Element e : children)
		{
			String src = getAttributeIngoreEmpty(e, TAG_CONVERTER_ATTR_SRC);
			String target = getAttributeIngoreEmpty(e, TAG_CONVERTER_ATTR_TARGET);
			String clazz = getAttributeIngoreEmpty(e, TAG_CONVERTER_ATTR_CLASS);
			
			assertNotEmpty(src, "<"+TAG_CONVERTER+"> attribute ["+TAG_CONVERTER_ATTR_SRC+"] must not be empty");
			assertNotEmpty(target, "<"+TAG_CONVERTER+"> attribute ["+TAG_CONVERTER_ATTR_TARGET+"] must not be empty");
			assertNotEmpty(clazz, "<"+TAG_CONVERTER+"> attribute ["+TAG_CONVERTER_ATTR_CLASS+"] must not be empty");
			
			genericConverter.addConverter(converterClassAttrToClass(src), converterClassAttrToClass(target), (Converter)createClassInstance(clazz));
		}
	}
	
	/**
	 * 解析拦截器信息
	 * @param element 父元素
	 */
	protected void parseInterceptor(Element parent)
	{
		Element el=getSingleElementByTagName(parent, TAG_INTERCEPROT);
		if(el == null)
			return;
		
		String before=getAttributeIngoreEmpty(el, TAG_INTERCEPROT_ATTR_BEFORE);
		String after=getAttributeIngoreEmpty(el, TAG_INTERCEPROT_ATTR_AFTER);
		String exception=getAttributeIngoreEmpty(el, TAG_INTERCEPROT_ATTR_EXCEPTION);
		String executionKey=getAttributeIngoreEmpty(el, TAG_INTERCEPROT_ATTR_EXECUTION_KEY);
		
		if(before==null && after==null && exception==null && executionKey==null)
			return;
		
		InterceptorInfo ii=createInterceptorInfoInstance();
		
		ii.setBeforeHandler(new ExecutableRefProxy(before));
		ii.setAfterHandler(new ExecutableRefProxy(after));
		ii.setExceptionHandler(new ExecutableRefProxy(exception));
		ii.setExecutionKey(executionKey);
		
		if(log.isDebugEnabled())
			log.debug("parsed '"+ii+"'");
		
		getConfiguration().setInterceptorInfo(ii);
	}
	
	/**
	 * 从可执行对象对应的元素中解析并设置对象的属性。
	 * @param executable 可执行对象，可能是Action也可能是Invoke
	 * @param element
	 */
	protected void setExecutableProperties(Executable executable,Element element)
	{
		if(executable instanceof Action)
			setActionProperties((Action)executable,element);
		else
			setInvokeProperties((Invoke)executable,element);
	}
	
	/**
	 * 从元素中解析并设置动作的属性。
	 * @param action
	 * @param element
	 */
	protected void setActionProperties(Action action,Element element)
	{
		//动作和调用的名称可以为空字符串""，因为在servlet规范中会有空字符串名的serlvet路径
		
		String name=getAttribute(element,TAG_ACTION_ATTR_NAME);
		assertNotNull(name, "<"+TAG_ACTION+"> attribute ["+TAG_ACTION_ATTR_NAME+"] must not be null");
		
		action.setName(customizeExecutableName(name));
		
		List<Element> children=getChildrenByTagName(element, null);
		for(Element e : children)
		{
			String tagName=e.getTagName();
			if(TAG_REF.equals(tagName))
			{
				String refExecutableName=getAttribute(e,TAG_REF_ATTR_NAME);
				assertNotNull(refExecutableName, "<"+TAG_REF+"> attribute ["+TAG_REF_ATTR_NAME+"] in <"+TAG_ACTION+"> named '"+action.getName()+"' must not be null");
				
				action.addExecutable(new ExecutableRefProxy(customizeExecutableName(refExecutableName)));
			}
			else if(TAG_INVOKE.equals(tagName))
			{
				Invoke invoke=createInvokeIntance();
				setInvokeProperties(invoke,e);
				
				action.addExecutable(invoke);
			}
		}
	}
	
	/**
	 * 从元素中解析并设置调用的属性
	 * @param invoke
	 * @param element
	 */
	protected void setInvokeProperties(Invoke invoke, Element element)
	{
		String methodName=getAttributeIngoreEmpty(element, TAG_INVOKE_ATTR_METHOD);
		
		if(methodName == null)
			setInvokePropertiesStatement(invoke, element);
		else
			setInvokePropertiesXml(invoke, element);
	}
	
	/**
	 * 设置以表达式方式定义的调用属性
	 * @param invoke
	 * @param element
	 */
	protected void setInvokePropertiesStatement(Invoke invoke, Element element)
	{
		String statement=getTextContent(element);
		assertNotEmpty(statement, "<"+TAG_INVOKE+"> content must not be empty");
		
		String name=getAttribute(element,TAG_INVOKE_ATTR_NAME);
		
		invoke.setName(customizeExecutableName(name));
		
		new InvokeStatementParser(invoke, statement, configuration.getResolverFactory()).parse();
	}
	
	/**
	 * 设置以XML方式定义的调用属性
	 * @param invoke
	 * @param element
	 */
	protected void setInvokePropertiesXml(Invoke invoke,Element element)
	{
		String name=getAttribute(element,TAG_INVOKE_ATTR_NAME);
		String methodName=getAttributeIngoreEmpty(element, TAG_INVOKE_ATTR_METHOD);
		String resolverId=getAttributeIngoreEmpty(element,TAG_INVOKE_ATTR_RESOLVER_OBJECT);
		String resolverClazz=getAttributeIngoreEmpty(element, TAG_INVOKE_ATTR_RESOLVER_CLASS);
		String resultKey=getAttributeIngoreEmpty(element,TAG_INVOKE_ATTR_RESULT_KEY);
		
		if(methodName == null)
			throw new ParseException("<"+TAG_INVOKE+"> attribute ["+TAG_INVOKE_ATTR_METHOD+"] must not be null");
		
		if(resolverClazz==null && resolverId==null)
			throw new ParseException("<"+TAG_INVOKE+"> attribute ["+TAG_INVOKE_ATTR_RESOLVER_OBJECT+"] or ["+TAG_INVOKE_ATTR_RESOLVER_CLASS+"] must not be null");
		
		Class<?> resolverClass=null;
		List<Element> argInfos=getChildrenByTagName(element, TAG_ARG);
		int argNums= argInfos == null ? 0 : argInfos.size();
		
		if(resolverClazz != null)
		{
			resolverClass=toClass(resolverClazz);
		}
		else if(resolverId != null)
		{
			Object resolverBean=configuration.getResolverFactory().getResolver(resolverId);
			assertNotEmpty(resolverBean, "can not find resolver with id '"+resolverId+"' referenced in <"+TAG_INVOKE+"> named '"+name+"'");
			
			resolverClass=resolverBean.getClass();
			
			invoke.setResolverProvider(new FactoryResolverProvider(configuration.getResolverFactory(), resolverId));
		}
		
		Method method=Invoke.findMethodThrow(resolverClass, methodName, argNums);
		
		invoke.setName(customizeExecutableName(name));
		invoke.setMethod(method);
		invoke.setResultKey(resultKey);
		
		parseArgs(element,invoke);
	}
	
	/**
	 * 解析并构建parent元素下的所有参数信息对象，写入调用对象中
	 * @param parent
	 * @param invoke
	 */
	protected void parseArgs(Element parent,Invoke invoke)
	{
		Type[] paramTypes=invoke.getMethod().getGenericParameterTypes();
		//如果调用对应的方法没有参数，则没有必要再解析
		if(paramTypes==null || paramTypes.length==0)
			return;
		
		Arg[] args = new Arg[paramTypes.length];
		
		List<Element> elements=getChildrenByTagName(parent, TAG_ARG);
		if(elements==null || elements.size()!=paramTypes.length)
			throw new ParseException("the number of <"+TAG_ARG+"> does not match the number of the actual method parameters named '"+invoke.getMethod()+"'");
		
		for(int i=0;i<elements.size();i++)
		{
			Element e=elements.get(i);
			
			Arg a=createArgInfoInstance();
			a.setType(paramTypes[i]);
			setArgProperties(a,e);
			
			if(log.isDebugEnabled())
				log.debug("parsed '"+a+"'");
			
			args[i]=a;
		}
		
		invoke.setArgs(args);
	}
	
	/**
	 * 从元素中解析并设置参数对象的属性
	 * @param arg
	 * @param element
	 */
	protected void setArgProperties(Arg arg,Element element)
	{
		String content=getTextContent(element);
		
		if(content == null)
			throw new ParseException("<"+TAG_ARG+"> must have text content");
		
		InvokeStatementParser.stringToArgProperty(arg, content);
	}
	
	/**
	 * 处理可执行对象引用代理，将它们替换为真正的引用可执行对象，
	 * 比如动作中的可执行对象引用代理。
	 */
	protected void processExecutableRefs()
	{
		Collection<Executable> executables=configuration.getExecutables();
		if(executables == null)
			return;
		
		for(Executable exe : executables)
		{
			if(exe instanceof Action)
			{
				Action action=(Action)exe;
				List<Executable> actionExes=action.getExecutables();
				if(actionExes==null)
					continue;
				
				for(int i=0,len=actionExes.size();i<len;i++)
				{
					Executable e=actionExes.get(i);
					
					if(e instanceof ExecutableRefProxy)
					{
						String refName=((ExecutableRefProxy)e).getRefName();
						Executable refExe=configuration.getExecutable(refName);
						
						if(refExe == null)
							throw new ParseException("can not find Executable named '"+refName+"' referenced in Action '"+action.getName()+"'");
						
						actionExes.set(i, refExe);
					}
				}
			}
		}
	}
	

	/**
	 * 替换拦截器的代理为真实的可执行对象
	 */
	protected void processInterceptorInfoRefs()
	{
		InterceptorInfo ii=getConfiguration().getInterceptorInfo();
		if(ii == null)
			return;
		{	
			Executable before=ii.getBeforeHandler();
			if(before instanceof ExecutableRefProxy)
			{
				Executable real = getConfiguration().getExecutable(((ExecutableRefProxy)before).getRefName());
				if(real == null)
					throw new ParseException("can not find before interceptor named '"+((ExecutableRefProxy)before).getRefName()+"'");
				
				ii.setBeforeHandler(real);
			}
		}
		{
			Executable after=ii.getAfterHandler();
			if(after instanceof ExecutableRefProxy)
			{
				Executable real = getConfiguration().getExecutable(((ExecutableRefProxy)after).getRefName());
				if(real == null)
					throw new ParseException("can not find after interceptor named '"+((ExecutableRefProxy)after).getRefName()+"'");
				
				ii.setAfterHandler(real);
			}
		}
		{
			Executable exception=ii.getExceptionHandler();
			if(exception instanceof ExecutableRefProxy)
			{
				Executable real = getConfiguration().getExecutable(((ExecutableRefProxy)exception).getRefName());
				if(real == null)
					throw new ParseException("can not find exception interceptor named '"+((ExecutableRefProxy)exception).getRefName()+"'");
				
				ii.setExceptionHandler(real);
			}
		}
	}
	
	/**
	 * 从输入流解析xml文档对象
	 * @param in
	 * @return
	 * @throws ParseException
	 */
	protected Document parseDocument(InputStream in)
	{
		try
		{
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setNamespaceAware(false);
			
			DocumentBuilder db=dbf.newDocumentBuilder();
			
			return db.parse(in);
		}
		catch(Exception e)
		{
			throw new ParseException("",e);
		}
	}
	
	/**
	 * 解析名称中带有匹配符的文档
	 * @param fileName
	 * @return
	 */
	protected Document[] parseDocuments(String fileName)
	{
		Document[] docs=null;
		
		if(! fileName.endsWith("/*"))
		{
			docs=new Document[1];
			docs[0]=parseDocument(fileName);
		}
		else
		{
			fileName=formatIncludeFileName(fileName);
			fileName=fileName.substring(0, fileName.length()-2);
			
			File folder=new File(fileName);
			if(!folder.exists() || !folder.isDirectory())
				throw new ParseException("can not find directory '"+fileName+"'");
			
			File[] files=folder.listFiles(new FileFilter()
			{
				@Override
				public boolean accept(File pathname)
				{
					String name=pathname.getName().toLowerCase();
					if(name.endsWith(".xml"))
						return true;
					else
						return false;
				}
			});
			
			if(files!=null && files.length>0)
			{
				docs=new Document[files.length];
				
				for(int i=0;i<files.length;i++)
				{
					InputStream in=null;
					try
					{
						in=new FileInputStream(files[i]);
					}
					catch(Exception e)
					{
						throw new ParseException("", e);
					}
					
					docs[i]=parseDocument(in);
					
					if(log.isDebugEnabled())
						log.debug("parsed Document object from '"+files[i].getAbsolutePath()+"'");
				}
			}
			else
			{
				if(log.isDebugEnabled())
					log.debug("no xml file found in directory '"+fileName+"'");
			}
		}
		
		return docs;
	}
	
	/**
	 * 根据名称取得文档对象
	 * @param fileName
	 * @return
	 */
	protected Document parseDocument(String fileName)
	{
		fileName=formatIncludeFileName(fileName);
		
		InputStream in = null;
		try
		{
			in = getClass().getClassLoader().getResourceAsStream(fileName);
		}
		catch(Exception e){}
		
		if(in == null)
		{
			try
			{
				in=new FileInputStream(fileName);
			}
			catch(Exception e1){}
		}
		
		if(in == null)
			throw new ParseException("can not find config file named '"+fileName+"'");
		
		Document doc=parseDocument(in);
		
		if(log.isDebugEnabled())
			log.debug("parsing Document object from '"+fileName+"'");
		
		return doc;
	}
	
	/**
	 * 格式化包含模块文件
	 * @param rawFileName
	 * @return
	 */
	protected String formatIncludeFileName(String rawFileName)
	{
		return rawFileName;
	}
	
	/**
	 * 将converter元素里面的src或者dest属性的值转换为它所代表的类型
	 * @param name
	 * @return
	 */
	protected Class<?> converterClassAttrToClass(String name)
	{
		Class<?> re=ClassShortName.get(name);
		if(re == null)
			re=toClass(name);
		
		return re;
	}
	
	/**
	 * 设置当前解析文档
	 * @param doc
	 */
	protected void setCurrentDocument(Document doc)
	{
		this.currentDocument=doc;
	}
	
	/**
	 * 取得当前解析的文档根元素
	 * @return
	 */
	protected Element getCurrentDocumentRoot()
	{
		return this.currentDocument.getDocumentElement();
	}
	
	/**
	 * 取得默认配置文件
	 * @return
	 */
	protected String getDefaultConfigFile()
	{
		return Constants.DEFAULT_CONFIG_FILE;
	}
	
	/**
	 * 取得父元素的直接子元素列表。如果没有，则返回null。
	 * 只有子元素的标签名与给定名称匹配的才会返回，如果名称为null，则返回所有。
	 * @param parent
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected List<Element> getChildrenByTagName(Element parent,String name)
	{
		if(parent == null)
			return null;
		
		boolean filter= name != null;
		
		NodeList nl=parent.getChildNodes();
		
		List<Element> elements=new ArrayList<Element>();
		for(int i=0;i<nl.getLength();i++)
		{
			Node n=nl.item(i);
			if(!(n instanceof Element))
				continue;
			
			Element e=(Element)nl.item(i);
			
			if(!filter)
				elements.add(e);
			else
			{
				if(name.equals(e.getTagName()))
					elements.add(e);
			}
		}
		
		return elements;
	}
	
	/**
	 * 取得父元素的单一子元素
	 * @param parent
	 * @param name
	 * @return
	 */
	protected Element getSingleElementByTagName(Element parent,String name)
	{
		if(parent == null)
			return null;
		
		NodeList nodes=parent.getElementsByTagName(name);
		
		if(nodes==null || nodes.getLength()==0)
			return null;
		
		return (Element)nodes.item(0);
	}
	
	/**
	 * 确保给定的对象不为null，如果是字符串，同时确保它不为空字符串。
	 * @param o 对象
	 * @param msg 失败时的异常消息
	 */
	protected void assertNotEmpty(Object o,String msg)
	{
		boolean toThrow=false;
		
		if(o == null)
			toThrow=true;
		else if(o instanceof String)
		{
			String s=(String)o;
			if(s.length() == 0)
				toThrow=true;
		}
		
		if(toThrow)
			throw new ParseException(msg);
	}
	
	/**
	 * 确保参数不为null
	 * @param o
	 * @param msg
	 */
	protected void assertNotNull(Object o,String msg)
	{
		if(o == null)
			throw new ParseException(msg);
	}
	
	/**
	 * 获取元素的属性值，如果属性未定义或者值为空字符串，将返回null
	 * @param element
	 * @param attrName
	 * @return
	 */
	protected String getAttributeIngoreEmpty(Element element,String attrName)
	{
		String v=element.getAttribute(attrName);
		return v==null || v.length()==0 ? null : v;
	}
	
	/**
	 * 获取元素的属性值，空字符串也将被如实返回
	 * @param element
	 * @param attrName
	 * @return
	 */
	protected String getAttribute(Element element, String attrName)
	{
		Attr attr=element.getAttributeNode(attrName);
		return attr == null ? null : attr.getValue();
	}
	
	/**
	 * 获取元素文本内容
	 * @param element
	 * @return
	 */
	protected String getTextContent(Element element)
	{
		String re=element.getTextContent();
		
		return re==null || re.length()==0 ? null : re;
	}
	
	/**
	 * 自定义可执行对象的名称，所有可执行对象的名称定义和引用处都将使用该自定义规则
	 * @param rawName
	 * @return
	 */
	protected String customizeExecutableName(String rawName)
	{
		return rawName;
	}
	
	/**
	 * 创建空的配置对象，用于从配置文件解析并设置其属性
	 * @return
	 */
	protected Configuration createConfigurationInstance()
	{
		return new Configuration();
	}
	
	protected ResolverFactory createResolverFactoryInstance()
	{
		return new DefaultResolverFactory();
	}
	
	/**
	 * 创建空的通用转换器对象，用于设置其属性
	 * @return
	 */
	protected GenericConverter createGenericConverterInstance()
	{
		return new DefaultGenericConverter();
	}
	
	/**
	 * 创建空的拦截器信息对象，用于设置其属性
	 * @return
	 */
	protected InterceptorInfo createInterceptorInfoInstance()
	{
		return new InterceptorInfo();
	}
	
	/**
	 * 创建空的可执行对象，用于从配置文件解析并设置其属性
	 * @return
	 */
	protected Executable createExecutableInstance(String type)
	{
		if(TAG_ACTION.equals(type))
			return createActionIntance();
		else if(TAG_INVOKE.equals(type))
			return createInvokeIntance();
		else
			throw new ParseException("invalid Executable type <"+type+">");
	}
	
	/**
	 * 创建空的动作对象，用于从配置文件解析并设置其属性
	 * @return
	 */
	protected Action createActionIntance()
	{
		return new Action();
	}
	
	/**
	 * 创建空的调用对象，用于从配置文件解析并设置其属性
	 * @return
	 */
	protected Invoke createInvokeIntance()
	{
		return new Invoke();
	}
	
	/**
	 * 创建参数信息对象，用于设置其属性
	 * @return
	 */
	protected Arg createArgInfoInstance()
	{
		return new Arg();
	}
	
	/**
	 * 创建对象
	 * @param clazz
	 * @return
	 */
	protected static Object createClassInstance(String clazz)
	{
		try
		{
			return Class.forName(clazz).newInstance();
		}
		catch(Exception e)
		{
			throw new ParseException(e);
		}
	}
	
	protected static Class<?> toClass(String clazz)
	{
		try
		{
			return Class.forName(clazz);
		}
		catch(Exception e)
		{
			throw new ParseException(e);
		}
	}
	
	/**
	 * 可执行对象代理，用于可执行对象引用的延迟初始化
	 * @author earthAngry@gmail.com
	 * @date 2010-10-28
	 *
	 */
	protected static class ExecutableRefProxy implements Executable
	{
		private static final long serialVersionUID = 1L;
		
		private String refName;

		public ExecutableRefProxy(String refName)
		{
			super();
			this.refName = refName;
		}

		public String getRefName() {
			return refName;
		}

		public void setRefName(String refName) {
			this.refName = refName;
		}

		@Override
		public void execute(ObjectSource objectSource) throws ExecuteException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public String getName()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString()
		{
			return "Executable [name=" + refName + "]";
		}
	}
}