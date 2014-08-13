sshw
====

SSH client in a browser, via a JEE webapp

**What is it for?**

A regular SSH connection is done over TCP port 22. However in most corporate environments, only ports 80 and 443 are open to the internet (for HTTP and HTTPS respectively). This means that is it not possible to use a tool like putty to open an SSH session to a server on the internet when inside a corporate environment. In this case, port 80 or 443 could be repurposed for SSH assuming the corporate firewall allows this.

However, if this server is also hosting JEE webapps, then it is very difficult (and could be impossible) to get an interactive SSH session to such a server from a corporate environment. This is where SSHW steps in. It is designed to offer interactive SSH sessions from a JEE container alongside any other webapps that may be deployed on the server.

**How do I install it?**

Simply take the latest WAR file from the releases page and deploy it to your Tomcat instance on your server. It may also work with other JEE containers, however I have only performed testing with Tomcat v7 and v8. 

**How do I use it?**

It will install itself as the root context *sshw*. So if your tomcat instance is on your local machine and running on the default port 8080, the URL will be [http://localhost:8080/sshw](http://localhost:8080/sshw).

**What does it look like?**

![](https://raw.githubusercontent.com/the-james-burton/sshw/master/doc/sshw-screenshot.png)

**Is it secure?**

It uses Spring Security, so the password transfer mechanism *should* be secure. However, I recommend you setup your JEE container to use HTTPS and access it through the resulting HTTPS link.

**Built upon...**

JQuery Terminal: http://terminal.jcubic.pl/

SSHJ: https://github.com/shikhar/sshj

Spring WebSockets: http://assets.spring.io/wp/WebSocketBlogPost.html

Spring Security: http://docs.spring.io/spring-security/site/docs/3.2.x/guides/hellomvc.html

**With help from...**

Elixir Web Shell: https://github.com/glejeune/ews

Spring WebSocket examples: https://github.com/rstoyanchev/spring-websocket-test

Spring REST examples: http://codetutr.com/2013/04/09/spring-mvc-easy-rest-based-json-services-with-responsebody/

Spring Social example: http://www.petrikainulainen.net/programming/spring-framework/adding-social-sign-in-to-a-spring-mvc-web-application-configuration/
