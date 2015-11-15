<h2><a href='http://code.google.com/p/soybeanmilk'>中文</a>    <a href='http://code.google.com/p/soybeanmilk/wiki/home_en'>English</a></h2>

SoybeanMilk是一个简易、友好、且零侵入的Java MVC实现框架：

  * 它几乎没有学习成本，你只需要熟悉jsp和servlet技术
  * 也不需要你遵从任何代码编写模式
  * 你的代码中几乎没有这个框架的踪迹

使用它，您仅需要编写少量的XML代码，就可以轻松地将任何Java方法发布为WEB应用。

另外，它还：

  * 支持RESTful风格
  * 内置强大且易扩展的对象转换器，可以自动将请求参数转换为复杂类型的对象
  * 可以很容易与Spring、Velocity等当前流行的框架整合

如果你是一个WEB开发者，并且已经有点厌烦现有的WEB开发框架所固有的开发方式（固定的代码编写模式、芝麻点的小功能也要写一堆接口）， 应该考虑了解下这个框架。

还有，这个框架并不是仅能应用于WEB程序，你也可以在桌面程序中使用它。


---

你可以直接点击 **[这里](http://code.google.com/p/soybeanmilk/downloads/list)** 下载最新的框架包，里面包含了完整的说明文档和示例，或者先往下看，稍作了解。

---


来看看使用SoybeanMilk时，你需要做些什么。

首先，你需要编写你的Java业务类，比如下面的示例：
```
package my;

public class User{
    private Integer id;
    private String name;
}

public class UserManager
{
    public void save(User user){ ... }
    
    public List<User> list(){ ... }
}
```
然后，定义“/WEB-INF/soybean-milk.cfg.xml”配置文件：
```
<?xml version="1.0" encoding="UTF-8"?> 
<!DOCTYPE soybean-milk PUBLIC "-//SoybeanMilk//DTD soybeanMilk web//EN" "http://soybeanmilk.googlecode.com/files/soybeanMilk-web-1.0.dtd">
<soybean-milk>
        <resolvers>
                <resolver id="userManager" class="my.UserManager" />
        </resolvers>

        <executables>
                <action name="/user/add.do">
                        <invoke> userManager.save(user) </invoke>
                        <target url="/user/list.do" type="redirect" />
                </action>
                
                <action name="/user/list.do">
                        <invoke> userList = userManager.list() </invoke>
                        <target url="/user/list.jsp" />
                </action>
        </executables>
</soybean-milk>
```

好了，业务“保存用户”和“查询所有用户列表”已经完成（JSP页面部分省略） ！

下面，你需要在你的web.xml中加入如下内容：
```
<servlet>
    <servlet-name>dispatchServlet</servlet-name>
    <servlet-class>org.soybeanMilk.web.servlet.DispatchServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>dispatchServlet</servlet-name>
    <url-pattern>*.do</url-pattern>
</servlet-mapping>
```

并将SoybeanMilk库和其依赖库
```
soybeanMilk-[version].jar
commons-logging-api-1.1.jar
```
放入/WEB-INF/lib目录下。

最后，启动服务器，在浏览器中输入“ http://[yourApp]/user/add.do?user.id=1&user.name=jack ”，用户“jack”将被保存，并显示在“ /user/list.jsp ”页面上。