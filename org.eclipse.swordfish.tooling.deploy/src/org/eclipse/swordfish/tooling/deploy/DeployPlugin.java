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

import java.net.URL;

import org.eclipse.core.runtime.Plugin;

import org.eclipse.jface.resource.ImageDescriptor;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DeployPlugin extends Plugin {

	private static URL ICON_BASE_URL;

	protected static DeployPlugin singleton;

	private BundleContext bundleContext;

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.swordfish.tooling.deploy";

	/**
	 * The constructor
	 */
	public DeployPlugin() {
		super();
		singleton = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		singleton = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static DeployPlugin getDefault() {
		return singleton;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * 
	 * @return org.eclipse.swordfish.tooling.deploy.DeployPlugin
	 */
	public static DeployPlugin getInstance() {
		return singleton;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Return the deploy location preference.
	 * 
	 * @param id
	 *            a runtime type id
	 * @return the deploy location
	 */
	public static String getPreference(String id) {
		return getInstance().getPluginPreferences().getString(id);
	}

	/**
	 * Set the deploy location preference.
	 * 
	 * @param id
	 *            the runtimt type id
	 * @param value
	 *            the location
	 */
	public static void setPreference(String id, String value) {
		getInstance().getPluginPreferences().setValue(id, value);
		getInstance().savePluginPreferences();
	}

	public static ImageDescriptor getImageDescriptor(String imgName) {
		if (ICON_BASE_URL == null) {
			String pathSuffix = "icons/";
			ICON_BASE_URL = singleton.getBundle().getEntry(pathSuffix);
		}

		try {
			return ImageDescriptor
					.createFromURL(new URL(ICON_BASE_URL, imgName));
		} catch (Exception e) {
			Logger.error("Error registering image", e);
		}
		return null;
	}
}
