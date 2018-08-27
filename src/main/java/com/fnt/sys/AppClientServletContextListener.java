package com.fnt.sys;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;

import com.vaadin.server.VaadinServlet;

@WebListener
public class AppClientServletContextListener implements ServletContextListener {

	// SSE
	// private Client client;
	// private WebTarget webTarget;
	// private SseEventSource eventSource;
	// SSE

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

		
		// SSE init
		/*
		if (client == null) {
			client = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
			webTarget = client.target(REST_EVENT_END_POINT);

			try {
				eventSource = SseEventSource.target(webTarget).build();
				eventSource.register((e) -> {
					Broadcaster.broadcast(e.readData());
				});
				eventSource.open();
			} catch (Throwable t) {
				System.out.println(t.toString());
			}
		}
		*/
		// SSE

	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
/*
		if (client != null) {
			webTarget.request().delete();
			// eventSource.close();
			client.close();
			webTarget = null;
			client = null;
		}
*/

	}

}
