package com.corejsf;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.faces.component.UIComponent;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.api.InitialPage;
import org.jboss.jsfunit.api.JSFUnitResource;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@InitialPage("/faces/index.xhtml")
public class HelloJSFUnitTest {
	@JSFUnitResource
	private JSFClientSession client;
	@JSFUnitResource
	private JSFServerSession server;
	
	@Deployment
	public static WebArchive createDeployment() {
		
		WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
			.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
			.addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
			.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
			.addPackage(Package.getPackage("com.corejsf"))
			.addAsWebResource(new File("src/main/webapp", "index.xhtml"))
			.addAsWebResource(new File("src/main/webapp", "next.xhtml"));
		
		return war;
	}
	
	@Test
	public void testFirst() throws Exception {
		assertThat(server.getCurrentViewID(), is("/index.xhtml"));
		
		client.setValue("firstName", "Hideo");
		client.setValue("lastName", "Sashida");
		client.click("submit_button");
		
		assertThat(server.getCurrentViewID(), is("/next.xhtml"));
		
		UIComponent greeting = server.findComponent("greeting");

		assertThat(greeting.isRendered(), is(true));

		assertThat(client.getPageAsText(), is(containsString("Hello, Hideo Sashida!")));
		assertThat((String)server.getComponentValue("greeting"), is("Hello, Hideo Sashida!"));
		
		assertThat((String)server.getManagedBeanValue("#{helloBean.firstName}"), 
				is("Hideo"));
		assertThat((String)server.getManagedBeanValue("#{helloBean.lastName}"), 
				is("Sashida"));
		assertThat((String)server.getManagedBeanValue("#{helloBean.greeting}"), 
				is("Hello, Hideo Sashida!"));
	}
}
