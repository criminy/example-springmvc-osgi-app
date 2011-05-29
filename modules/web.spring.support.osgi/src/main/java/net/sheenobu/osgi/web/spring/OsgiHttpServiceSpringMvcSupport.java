package net.sheenobu.osgi.web.spring;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 
 * Abstract support class which bootstraps the Spring MVC system with the standard
 * OSGI HttpService.
 * 
 */
public class OsgiHttpServiceSpringMvcSupport implements
		BundleContextAware, InitializingBean 
		
{
	private BundleContext bundleContext;
	private String contextConfigLocation;
	private String pathSpec;
	
	public String getContextConfigLocation() {
		return contextConfigLocation;
	}
	
	public String getPathSpec() {
		return pathSpec;
	}
	
	public void setPathSpec(String pathSpec) {
		this.pathSpec = pathSpec;
	}
	public void setContextConfigLocation(String contextConfigLocation) {
		this.contextConfigLocation = contextConfigLocation;
	}
	
	



	public void afterPropertiesSet() throws Exception {
		try {
			init(bundleContext);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	/**
	 * Extension of DispatcherServlet which inserts the BundleContext through
	 * the ServletContext at the right moment; used to get
	 * Spring MVC to play nicely with OSGIs Bundle Context.
	 * 
	 * @author artripa
	 * 
	 */
	final class D extends DispatcherServlet {

		private static final long serialVersionUID = 1L;
		private BundleContext ctx;
		private OsgiHttpServiceSpringMvcSupport parent;

		public D(OsgiHttpServiceSpringMvcSupport parent, BundleContext c) {
			this.parent = parent;
			this.ctx = c;
		}

		@Override
		protected WebApplicationContext initWebApplicationContext() {
			ServletContext context = getServletContext();
			context.setAttribute(
					OsgiBundleXmlWebApplicationContext.BUNDLE_CONTEXT_ATTRIBUTE,
					ctx);
			this.setContextConfigLocation(parent.getContextConfigLocation());
			return super.initWebApplicationContext();
		}
	}

	/**
	 * Starts up the SpringMVC system and attaches it to the external OSGI HTTP
	 * service.
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	final public void init(BundleContext arg0) throws Exception {

		ServiceReference sRef = arg0.getServiceReference(HttpService.class
				.getName());
		if (sRef != null) {
			D dispatcherServlet = new D(this, arg0);
			dispatcherServlet
					.setContextClass(Class
							.forName("org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext"));

			Dictionary dict = new Hashtable();
			dict.put(
					"contextClass",
					"org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext");

			HttpService service = (HttpService) arg0.getService(sRef);

			service.registerServlet(this.getPathSpec(), dispatcherServlet,
					dict, null);
		}

	}

	final public void destroy() throws Exception {

	}

	final public void setBundleContext(BundleContext arg0) {
		this.bundleContext = arg0;
	}

}