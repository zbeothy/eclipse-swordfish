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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
import org.osgi.framework.BundleException;

/**
 * Swordfish WST Server Tools server and OSGi Framework mediator.
 * 
 * @author Alex Tselesh
 */
public class SwordfishServerOSGiFrameworkMediator implements SwordfishServerModuleListener {

	private Map mapBundleLocationToModule = new HashMap(); // bundle location => module
	
	private Properties deployedBundles = new Properties(); // module id => bundle location
	
	private SwordfishServerBehaviourDelegate serverBehaviourDelegate;
	
	private SwordfishOSGiFramework osgiFramework;
	
	private IPath bundlesLocation;
	
	public SwordfishServerOSGiFrameworkMediator(SwordfishServerBehaviourDelegate serverBehaviourDelegate) {
		this.serverBehaviourDelegate = serverBehaviourDelegate;
		osgiFramework = SwordfishOSGiFramework.getInstance();
	}
	
	public Properties init(Properties p) {
		bundlesLocation = serverBehaviourDelegate.getServer().getRuntime().getLocation();
		if (!bundlesLocation.toFile().exists()) {
			bundlesLocation.toFile().mkdirs();
		}
		
		IPath zzz = bundlesLocation.append("test.jar");
		try {
			Logger.info("### test IPath.toOSString - " + zzz.toOSString());
			Logger.info("### test IPath.toFile().toURL().toExternalForm() - " + zzz.toFile().toURL().toExternalForm());
		} catch (Exception e) {
			e = null;
		}

		return osgiFramework.init(p, this);
	}
	
	public void install(IModule module)
			throws SwordfishOSGiFrameworkException {
		
		Logger.info("SwordfishServerOSGiFrameworkMediator - " + this.toString());
		Logger.info("SwordfishServerOSGiFrameworkMediator.install(ISwordfishModule: " + module + ")");

		String location = getBundleLocationString(module);
		osgiFramework.deployBundle(location);
		mapBundleLocationToModule.put(location, module);
	}
	
	public void uninstall(IModule module)
			throws SwordfishOSGiFrameworkException {
	
		Logger.info("SwordfishServerOSGiFrameworkMediator - " + this.toString());
		Logger.info("SwordfishServerOSGiFrameworkMediator.uninstall(ISwordfishModule: " + module + ")");

		String location = getBundleLocationString(module);
		osgiFramework.uninstallBundle(location);
		mapBundleLocationToModule.remove(location);
	}
	
	public void start(IModule module)
			throws SwordfishOSGiFrameworkException {
		
		Logger.info("SwordfishServerOSGiFrameworkMediator - " + this.toString());
		Logger.info("SwordfishServerOSGiFrameworkMediator.start(ISwordfishModule: " + module + ")");

		String location = getBundleLocationString(module);
		osgiFramework.startBundle(location);
	}
	
	public void stop(IModule module)
			throws SwordfishOSGiFrameworkException {
		
		Logger.info("SwordfishServerOSGiFrameworkMediator - " + this.toString());
		Logger.info("SwordfishServerOSGiFrameworkMediator.stop(ISwordfishModule: " + module + ")");

		String location = getBundleLocationString(module);
		osgiFramework.stopBundle(location);
	}

	public int getModuleState(IModule module)
			throws SwordfishOSGiFrameworkException {
		
		String location = getBundleLocationString(module);
		
		int bundleState = osgiFramework.getBundleState(location);
		
		return SwordfishOSGiModuleEvent.mapOSGiBundleStateToModuleState(bundleState);
	}
	
	public String getInstalledBundleLocation(String moduleId) {

		Logger.info("SwordfishServerOSGiFrameworkMediator.getInstalledBundleLocation(moduleId: " + moduleId + ")");

		return (String) deployedBundles.get(moduleId);
	}
	
	public void moduleChanged(SwordfishOSGiModuleEvent event) {
		String location = event.getBundleLocation();
		int newModuleState = event.getModuleEventState();
		
		IModule module = (IModule) mapBundleLocationToModule.get(location);
		
		serverBehaviourDelegate.setChangedModuleState(new IModule[] {module}, newModuleState);
	}

	public String toString() {
		return "SwordfishServerOSGiFrameworkMediator@" + this.hashCode() + " {installed bundles - " + deployedBundles + "}";
	}

	public String getBundleLocationString(IModule module)
			throws SwordfishOSGiFrameworkException {

		IPath moduleLocation = getBundleLocation(module);
		
		try {
			return moduleLocation.toFile().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			throw new SwordfishOSGiFrameworkException("illegal path to bundle: " + moduleLocation);
		}
		
	}
	
	public IPath getBundleLocation(IModule module) {

		return bundlesLocation.append(getBundleFileName(module) + ".jar");
	}

	private String getBundleFileName(IModule module) {
		
		IFolder metainfFolder = module.getProject().getFolder("META-INF");
		IFile metainfFile = metainfFolder.getFile("MANIFEST.MF");
		
		String bundleFileName = module.getProject().getName();
		try {
			Map manifestElements = SwordfishOSGiFramework.parseManifest(metainfFile.getContents());
			
			String symbolicName = (String) manifestElements.get(SwordfishOSGiFramework.MANIFEST_BUNDLE_SYMBOLICNAME);
			String version = (String) manifestElements.get(SwordfishOSGiFramework.MANIFEST_BUNDLE_VERSION);

			if (null != symbolicName && 0 < symbolicName.length()) {
				bundleFileName = symbolicName;
				if (null != version && 0 < version.length()) {
					bundleFileName += "-" + version;
				}
			}
		} catch (CoreException e) {
			Logger.error("cannot get manifest file content", e);
		} catch (IOException e) {
			Logger.error("error occurs while reading the manifest", e);
		} catch (BundleException e) {
			Logger.error("manifest has an invalid syntax", e);
		}
		
		return bundleFileName;
	}
	
}
