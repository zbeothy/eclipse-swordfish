/*******************************************************************************
 * Copyright (c) 2007 SOPERA GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.tooling.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Swordfish OSGi Framework implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishOSGiFramework {

	public final static String MANIFEST_BUNDLE_SYMBOLICNAME = Constants.BUNDLE_SYMBOLICNAME;
	public final static String MANIFEST_BUNDLE_VERSION = Constants.BUNDLE_VERSION;
	public final static String MANIFEST_BUNDLE_NAME = Constants.BUNDLE_NAME;
	
	private Set deployedSwordfishBundles = new HashSet();
	
	private BundleContext context;
	
	private PackageAdmin packageAdmin;
	
	private static SwordfishOSGiFramework instance;

	private SwordfishServerModuleListener listener;

	private SwordfishOSGiFramework() {
		context = DeployPlugin.getDefault().getBundleContext();
		
		ServiceReference packageAdminRef = context.getServiceReference(PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) context.getService(packageAdminRef); 
		
		StringBuffer sb = new StringBuffer("The OSGi Framework information:");
		sb.append("\t version    : " + context.getProperty(Constants.FRAMEWORK_VERSION));
		sb.append("\t vendor     : " + context.getProperty(Constants.FRAMEWORK_VENDOR));
		sb.append("\t language   : " + context.getProperty(Constants.FRAMEWORK_LANGUAGE));
		sb.append("\t OS name    : " + context.getProperty(Constants.FRAMEWORK_OS_NAME));
		sb.append("\t OS version : " + context.getProperty(Constants.FRAMEWORK_OS_VERSION));
		sb.append("\t processor  : " + context.getProperty(Constants.FRAMEWORK_PROCESSOR));
		Logger.info(sb.toString());
	}
	
	static SwordfishOSGiFramework getInstance() {
		if (null == instance) {
			instance = new SwordfishOSGiFramework();
		}
		return instance;
	}
	
	public Properties init(Properties p, SwordfishServerModuleListener listener) {

		this.listener = listener;
		
		Properties deployedBundles = new Properties();
		
		if (null != p) {
			Set keys = p.keySet();
			Iterator it = keys.iterator();
			while (it.hasNext()) {
				String bundleId = (String) it.next();
				String location = p.getProperty(bundleId);
				
				if (isBundlePresented(location)) {
					deployedBundles.put(bundleId, location);
				}
			}
		}
		
		Logger.info("SwordfishOSGiFramework.init(...) - previously installed bundles: " + p.toString());

		deployedSwordfishBundles = new HashSet(deployedBundles.values());
		context.addBundleListener(new SwordfishBundleListener());
		
		return deployedBundles;
	}
	
	public boolean isInternalFramework() {
		return true;
	}
	
	public void deployBundle(String bundleLocation)
			throws SwordfishOSGiFrameworkException {
		
		Logger.info("SwordfishOSGiFramework.deployBundle(location: " + bundleLocation + ")");

		// printAllBundles();
		
		Bundle bundle = null;
		try {
			bundle = getBundleByLocation(bundleLocation);
		} catch (SwordfishOSGiFrameworkException e) {
			// bundle is not presented in OSGi runtime
			e = null;
		}
		boolean bFirstInstall = (null == bundle);
		Logger.info("SwordfishOSGiFramework.deployBundle(...):"
						+ " bundle " + ((bFirstInstall) ? "does not present" : "already presents")
						+ " in OSGi container - will be " + ((bFirstInstall) ? "installed" : "updated") + "...");
		
		try {
			if (bFirstInstall) {
				bundle = context.installBundle(bundleLocation);
			} else {
				bundle.uninstall();
				packageAdmin.refreshPackages(new Bundle[] {bundle}); 		

				bundle = context.installBundle(bundleLocation);

//				bundle.update();
				
			}
			deployedSwordfishBundles.add(bundleLocation);
		}	
		catch (BundleException e) {
			throw new SwordfishOSGiFrameworkException("cannon " + ((bFirstInstall) ? "unstall" : "update") + " bundle - " + stringBundleInfo(bundle) + ": " + e.getMessage(), e);
		}

		Logger.info("SwordfishOSGiFramework.deployBundle(...): bundle " + ((bFirstInstall) ? "unstalled" : "updated") + " successfully - " + stringBundleInfo(bundle));
	}
	
	public void uninstallBundle(String bundleLocation)
			throws SwordfishOSGiFrameworkException {

		Logger.info("SwordfishOSGiFramework.uninstallBundle(bundleLocation: " + bundleLocation + ")");

		Bundle bundle = getBundleByLocation(bundleLocation);

		try {
			bundle.uninstall();
			packageAdmin.refreshPackages(new Bundle[] {bundle}); 		
			deployedSwordfishBundles.remove(bundleLocation);
		} catch (BundleException e) {
			throw new SwordfishOSGiFrameworkException("cannon unistall bundle "
						+ stringBundleInfo(bundle) + ": " + e.getMessage(), e);
		}
	}
	
	public int getBundleState(String bundleLocation)
			throws SwordfishOSGiFrameworkException {
		
		Bundle bundle = getBundleByLocation(bundleLocation);
		
		return bundle.getState();
	}

	public void startBundle(String bundleLocation)
			throws SwordfishOSGiFrameworkException {

		Logger.info("SwordfishOSGiFramework.startBundle(location: " + bundleLocation + ")");

		Bundle bundle = getBundleByLocation(bundleLocation);
		
		if (bundle.getState() == Bundle.INSTALLED) {
			Logger.info("SwordfishOSGiFramework.startBundle(...): bundle just installed - trying to resolve...");
			if (!packageAdmin.resolveBundles(new Bundle[] {bundle})) {
				String reason = ((AbstractBundle) bundle).getResolutionFailureMessage();
				Logger.info("SwordfishOSGiFramework.startBundle(...): bundle is not resolved - " + reason);
				throw new SwordfishOSGiFrameworkException("cannon resolve bundle "
						+ stringBundleInfo(bundle) + ": " + reason);
			}
			Logger.info("SwordfishOSGiFramework.startBundle(...): bundle after resolving - " + stringBundleInfo(bundle));
		}
		
//		if (!isStateActive(bundle)) {
			try {
				bundle.start();
			} catch (BundleException e) {
				throw new SwordfishOSGiFrameworkException("cannon start bundle "
							+ stringBundleInfo(bundle) + ": " + e.getMessage(), e);
			}
//		}
	}
	
	private boolean isStateActive(Bundle bundle) {
		int state = bundle.getState();
		return state == Bundle.ACTIVE || state == Bundle.STARTING;
	}
	
	public void stopBundle(String bundleLocation)
			throws SwordfishOSGiFrameworkException {

		Logger.info("SwordfishOSGiFramework.stopBundle(bundleId: " + bundleLocation + ")");

		Bundle bundle = getBundleByLocation(bundleLocation);
		
//		if (isStateActive(bundle)) {
			try {
				bundle.stop();
			} catch (BundleException e) {
				throw new SwordfishOSGiFrameworkException("cannon stop bundle "
							+ stringBundleInfo(bundle) + ": " + e.getMessage(), e);
			}
//		}
	}

	public boolean isBundlePresented(String bundleLocation) {
		try {
			getBundleByLocation(bundleLocation);
			return true;
		} catch (SwordfishOSGiFrameworkException e) {
			// bundle with specified location does not present in OSGi context
			e = null;
		}
		return false;
	}
	
	/**
	 * Locate an installed bundle with a given identity.
	 * 
	 * @param location
	 *            string for the bundle
	 * @return Bundle object for bundle with the specified location or null if
	 *         no bundle is installed with the specified location.
	 * @throws SwordfishOSGiFrameworkException 
	 */
	private Bundle getBundleByLocation(String location)
			throws SwordfishOSGiFrameworkException {

		Logger.info("SwordfishOSGiFramework.getBundleByLocation(location: " + location + ")");

		if (null == location || 0 == location.length()) {
			throw new IllegalArgumentException("nullable OSGi location");
		}

		Bundle[] bundles = context.getBundles();
		for (int i = 0, count = bundles.length; i < count; i++) {
			Bundle bundle = bundles[i];
			if (location.equals(bundle.getLocation())) {
				// TODO: Bundle.getLocation requires AdminPermission (metadata)
				// java.lang.SecurityException
				// If the caller does not have the appropriate AdminPermission[this,METADATA],
				// and the Java Runtime Environment supports permissions.
				Logger.info("SwordfishOSGiFramework.getBundleByLocation(...) - find bundle: " + stringBundleInfo(bundle));
				return bundle;
			}
		}
		Logger.info("SwordfishOSGiFramework.getBundleByLocation(...) - cannot find bundle by location: " + location);
		throw new SwordfishOSGiFrameworkException("cannot find bundle by location: " + location);
	}
	
//	private Bundle[] getBundlesBySymbolicName(String symbolicName) {
//		
//		List result = new ArrayList();
//		
//		Bundle[] bundles = context.getBundles();
//		for (int i = 0, count = bundles.length; i < count; i++) {
//			Bundle bundle = bundles[i];
//			if (null == symbolicName) {
//				if (null == bundle.getSymbolicName()) {
//					result.add(bundle);
//				}
//			} else if(symbolicName.equals(bundle.getSymbolicName())) {
//				result.add(bundle);
//			}
//		}
//		
//		return (Bundle[]) result.toArray(new Bundle[result.size()]);
//	}
	
//	private Bundle getBundleBySymbolicNameAndVersion(String symbolicName, String versionString) {
//		
//		Bundle[] bundlesBySymbolicName = getBundlesBySymbolicName(symbolicName);
//		
//		int count = bundlesBySymbolicName.length;
//		if (count > 0) {
//			Version version = new Version(versionString);
//			for (int i = 0; i < count; i++) {
//				AbstractBundle abstractBundle = (AbstractBundle) bundlesBySymbolicName[i];
//				
//				if (version.equals(abstractBundle.getVersion())) {
//					return (Bundle) abstractBundle;
//				}
//			}
//		}
//		return (Bundle) null;
//	}

	public static Map parseManifest(InputStream isManifest) throws IOException, BundleException {
		return ManifestElement.parseBundleManifest(isManifest, null);
	}
	
//	private void printAllBundles() {
//		StringBuffer sb = new StringBuffer();
//		Bundle[] bundles = context.getBundles();
//		sb.append("\t already installed bundles [" + bundles.length + "]:");
//		for (int i = 0, count = bundles.length; i < count; i++) {
//			Bundle bundle = bundles[i];
//			sb.append(
//					"\t\t {id: " + bundle.getBundleId()
//						+ ",\t state: " + stringBundleState(bundle)
//						+ ",\t name: " + bundle.getSymbolicName()
//						+ ",\t changed: " + new Date(bundle.getLastModified())
//						+ ",\t location: " + bundle.getLocation()
//					+ "}");
//		}
//		Logger.info(sb.toString());
//	}

	private static String stringBundleInfo(Bundle bundle) {
		if (null == bundle) return "null";
		return
			"{id: " + bundle.getBundleId() 
				+ ", state: " + stringBundleState(bundle)
				+ ", name: " + bundle.getSymbolicName()
				+ ", changed: " + new Date(bundle.getLastModified())
				+ ", location: " + bundle.getLocation()
				+ ", hashcode: " + bundle.hashCode()
			+ "}";
	}
	
	private static String stringBundleState(Bundle bundle) {
		
		int state = bundle.getState();
		if(state == Bundle.ACTIVE)      return "ACTIVE";
		if(state == Bundle.STOPPING)    return "STOPPING";
		if(state == Bundle.STARTING)    return "STARTING";
		if(state == Bundle.RESOLVED)    return "RESOLVED";
		if(state == Bundle.INSTALLED)   return "INSTALLED";
		if(state == Bundle.UNINSTALLED) return "UNINSTALLED";
		
		throw new RuntimeException("unknown bundle state");
	}
	
	class SwordfishBundleListener implements BundleListener {
		public void bundleChanged(BundleEvent event) {
			String location = event.getBundle().getLocation();
			if (deployedSwordfishBundles.contains(location)) {
				listener.moduleChanged(new SwordfishOSGiModuleEvent(event));
			}
		}
	}
}
