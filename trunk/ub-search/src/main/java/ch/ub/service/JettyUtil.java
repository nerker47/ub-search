package ch.ub.service;

import java.util.Map;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.common.collect.ImmutableMap;

public class JettyUtil {
	  public static void start() throws Exception {
	    Server server = new Server();
	    SelectChannelConnector connector = new SelectChannelConnector();
	    connector.setPort(8999);
	    server.addConnector(connector);

	    HandlerList handlers = new HandlerList();
	    handlers.setHandlers(new Handler[] { createJerseyServlet(), new DefaultHandler() });
	    server.setHandler(handlers);

	    server.start();
	    server.join();  
	  }

	  private static Handler createJerseyServlet() {
	    ServletContextHandler handler = new ServletContextHandler();
	    handler.setContextPath("/game");
	    SessionHandler sessionHandler = new SessionHandler();
	    handler.setSessionHandler(sessionHandler);
	    ServletHolder holder = handler.addServlet("com.sun.jersey.spi.container.servlet.ServletContainer", "/*");
	    Map<String,String> initParams = ImmutableMap.of(
	             "com.sun.jersey.config.property.packages", "com.test.todo.rest",
	             "com.sun.jersey.api.json.POJOMappingFeature", "true");
	    holder.setInitParameters(initParams);
	    return handler;
	  }
	 }