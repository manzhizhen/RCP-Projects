package cn.sunline.suncard.sde.baseplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "sunline.suncard.sde.dm"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
//	Dictionary<String, String> props = new Hashtable<String, String>();
//	SessionFactory sessionFactory = null;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//Class.forName("cn.sunline.suncard.sde.util.HibernateUtil"); 
		
//		props.put("uid", "Hibernate:SessionFactory");
//		Configuration configuration = new Configuration().configure(new File(
//				"./etc/org.opengoss.database.hibernate/hibernate.cfg.xml"));
//				Class[] domainClasses = getDomainClasses();
//				for (Class domainClass : domainClasses) {
//				 
//				configuration.addClass(domainClass);
//				 
//				}
//				 
//				sessionFactory = configuration.buildSessionFactory();
//				props.put("scope", "APPLICATION");
//				 
//				 
//				ServiceRegistration registration = context.registerService(SessionFactory.class.getName(), sessionFactory, props);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	
//	private Class[] getDomainClasses() throws Exception {
//		List domainClasses = new ArrayList();
//		IExtensionRegistry registry;
//		IExtensionPoint point = registry .getExtensionPoint("hibernate_point_id");
//		 
//		IExtension[] extensions = point.getExtensions();
//		 
//		for (IExtension extension : extensions) {
//		IConfigurationElement[] elements = extension .getConfigurationElements();
//		 
//		 
//		for (IConfigurationElement configurationElement : elements) {
//		 
//		Bundle bundle = context.getBundleBySymbolId(extension .getNamespaceIdentifier());
//		 
//		Class domainClass =  bundle.loadClass(configurationElement .getAttribute("class"));
//		 
//		domainClasses.add(domainClass);
//		 
//		}
//		 
//		}
//		return (Class[]) domainClasses.toArray(new Class[domainClasses.size()]);
//		}

}
