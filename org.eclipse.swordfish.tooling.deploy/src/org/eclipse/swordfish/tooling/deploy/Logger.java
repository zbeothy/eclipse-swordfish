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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Logger.
 * 
 * @author Alex Tselesh
 */
public class Logger {

	private Logger() {
	}

	public static void log(IStatus status) {
		DeployPlugin.getDefault().getLog().log(status);
	}

	public static void error(String message) {
		log(new Status(IStatus.ERROR, DeployPlugin.PLUGIN_ID, IStatus.ERROR, message, null));
	}

	public static void error(Throwable e) {
		log(new Status(IStatus.ERROR, DeployPlugin.PLUGIN_ID, IStatus.ERROR, getMessage(e), e));
	}

	public static void error(String message, Throwable e) {
		log(new Status(IStatus.ERROR, DeployPlugin.PLUGIN_ID, IStatus.ERROR, message, e));
	}

	public static void warn(String message) {
		log(new Status(IStatus.WARNING, DeployPlugin.PLUGIN_ID, IStatus.WARNING, message, null));
	}

	public static void info(String message) {
		log(new Status(IStatus.INFO, DeployPlugin.PLUGIN_ID, IStatus.INFO, message, null));
	}

	public static String getMessage(Throwable e) {
		return (e.getMessage() != null) ? e.getMessage() : e.getClass().getName();
	}

}
