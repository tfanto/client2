package com.fnt.sys;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.fnt.ui.SSEClient;
import com.vaadin.server.VaadinServlet;

@WebListener
public class AppClientServletContextListener implements ServletContextListener {
	
	private static SSEClient sseClient;


	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		String REST_EVENT_END_POINT = "";
		try {
			Properties props = new Properties();
			InputStream is = servletContextEvent.getServletContext().getResourceAsStream("WEB-INF/clientsettings.properties");
			props.load(is);

			ServletContext ctx = servletContextEvent.getServletContext();
			props.forEach((key, value) -> {
				String keyStr = String.valueOf(key);
				String valStr = String.valueOf(value);
				ctx.setAttribute(keyStr, valStr);
			});
			
			REST_EVENT_END_POINT = props.getProperty("REST_EVENT_END_POINT");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sseClient = new SSEClient(REST_EVENT_END_POINT);
		sseClient.open();

		
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

}
