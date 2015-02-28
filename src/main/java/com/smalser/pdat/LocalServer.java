package com.smalser.pdat;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

public class LocalServer
{
    public static void main(String[] args) throws Exception
    {
//        Server server = new Server();
//
//        ServerConnector connector = new ServerConnector(server);
//        connector.setPort(9999);
//
//        // Setup JMX
//        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
//        server.addBean(mbContainer);
//
//        server.setConnectors(new Connector[]{connector});
//
//        WebAppContext context = new WebAppContext();
//        context.setServer(server);
//        context.setContextPath("/");
//
//        ProtectionDomain protectionDomain = LocalServer.class.getProtectionDomain();
//        URL location = protectionDomain.getCodeSource().getLocation();
//        context.setWar(location.toExternalForm());
//
//        // This webapp will use jsps and jstl. We need to enable the AnnotationConfiguration in order to correctly
//        // set up the jsp container
//        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
//        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
//
//        // Set the ContainerIncludeJarPattern so that jetty examines these container-path jars for tlds, web-fragments etc.
//        // If you omit the jar that contains the jstl .tlds, the jsp engine will scan for them instead.
//        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
//                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
//
//
//        // A WebAppContext is a ContextHandler as well so it needs to be set to the server so it is aware of where to
//        // send the appropriate requests.
//        server.setHandler(context);
//
//        // Configure a LoginService
//        // Since this example is for our test webapp, we need to setup a LoginService so this shows how to create a
//        // very simple hashmap based one. The name of the LoginService needs to correspond to what is configured in
//        // the webapp's web.xml and since it has a lifecycle of its own we register it as a bean with the Jetty
//        // server object so it can be started and stopped according to the lifecycle of the server itself.
//        HashLoginService loginService = new HashLoginService();
//        loginService.setName("Test Realm");
//        loginService.setConfig("src/test/resources/realm.properties");
//        server.addBean(loginService);
//
//        // Start things up! By using the server.join() the server thread will join with the current thread.
//        // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
//        server.start();
//        server.join();

        Server server = new Server(8080);

        URL resource = LocalServer.class.getClassLoader().getResource(".");
        String rootPath = resource == null ? "" : resource.toString();
        WebAppContext webapp = new WebAppContext(rootPath + "../../src/main/webapp", "");
        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
