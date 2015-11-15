<h2><a href='http://code.google.com/p/soybeanmilk'>中文</a>    <a href='http://code.google.com/p/soybeanmilk/wiki/home_en'>English</a></h2>

SoybeanMilk is a simple, friendly, and zero-invasive Java MVC framework:

  * It has few learning costs, you only need to know JSP and Servlet technology.
  * It does not make you complying with any coding pattern.
  * It can have zero appearance in your codes.

Using it, you can make any Java method to be a Web application by writing a few XML codes.

And also it:

  * supports RESTful.
  * supplies a powerful and extensible built-in converter which can automatically convert request parameters to complex JavaBean object.
  * can be easily integrated with Spring or Velocity or some other frameworks.

It may be a good choice if you are a web developer and feeling a little hate with the develop mode of some framework.

And more, it can not only be used in web development but also in desktop development.


---

You can click **[Here](http://code.google.com/p/soybeanmilk/downloads/list)** to download the latest framework, which contains tutorial and examples, or just have a look here.

---


Now, let us have a look how you need to do when using SoybeanMilk.

First, you must write a Java class, just like the following example:
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
Next, add the following configuration file named "soybean-milk.cfg.xml" to "/WEB-INF" folder:
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

OK, the work "Save user" and "Find all users" is finish (The JSP pages are omitted).

Now, add the following XML codes into your "web.xml":
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

Put SoybeanMilk and it's dependent library
```
soybeanMilk-[version].jar
commons-logging-api-1.1.jar
```
into "/WEB-INF/lib" folder.

That's OK!
Now, start the web server, and type "http://[yourApp]/user/add.do?user.id=1&user.name=jack" in your internet explorer, user "jack" will be saved and displayed in "/user/list.jsp" page.