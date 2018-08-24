package com.fnt.authentication;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fnt.sys.Fnc;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

public class AppLoginRepository {

	private static final String REST_LOGIN_END_POINT = String.valueOf(VaadinServlet.getCurrent().getServletContext().getAttribute("REST_LOGIN_END_POINT"));

	private static final String LOGIN = "login";
	private static final String JWE = "jwe";

	public Client createClient() {
		return Fnc.createClient();
	}

	public static boolean authenticate(String login, String pwd) {

		Client client = null;
		try {
			client = Fnc.createClient();
			Response response = client.target(REST_LOGIN_END_POINT).path(login).path(pwd).request(MediaType.APPLICATION_JSON).get(Response.class);
			if (response.getStatus() == 200) {
				String jwe = response.getHeaderString("Authorization");
				if (jwe != null) {
					VaadinSession.getCurrent().setAttribute(LOGIN, login);
					VaadinSession.getCurrent().setAttribute(JWE, jwe);
					Page.getCurrent().reload();
					return true;
				}
			}
			VaadinSession.getCurrent().setAttribute(LOGIN, null);
			VaadinSession.getCurrent().setAttribute(JWE, null);
			return false;
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public static boolean isAuthenticated() {
		return VaadinSession.getCurrent().getAttribute(LOGIN) != null;
	}

	public static void logout() {
		VaadinService.getCurrentRequest().getWrappedSession().invalidate();
		Page.getCurrent().setLocation("");
	}

}
