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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

/**
 * Swordfish OSGi module events.
 * 
 * @author Alex Tselesh
 */
public class SwordfishOSGiModuleEvent {

	private static Map mapOSGiBundleEventToModuleState = new HashMap(10);
	static {
		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.INSTALLED),       new Integer(IServer.STATE_STOPPED));
		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.UPDATED),         new Integer(IServer.STATE_STOPPED));
		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.LAZY_ACTIVATION), new Integer(IServer.STATE_STOPPED));
		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.RESOLVED),        new Integer(IServer.STATE_STOPPED));
		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.UNRESOLVED),      new Integer(IServer.STATE_STOPPED));
		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.STOPPED),         new Integer(IServer.STATE_STOPPED));

		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.STARTING),    new Integer(IServer.STATE_STARTING));

		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.STOPPING),    new Integer(IServer.STATE_STOPPING));

		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.STARTED),     new Integer(IServer.STATE_STARTED));

		mapOSGiBundleEventToModuleState.put(new Integer(BundleEvent.UNINSTALLED), new Integer(IServer.STATE_UNKNOWN));
	}

	private static Map mapOSGiBundleStateToModuleState = new HashMap();
	static {
		mapOSGiBundleStateToModuleState.put(new Integer(Bundle.UNINSTALLED), new Integer(IServer.STATE_UNKNOWN));

		mapOSGiBundleStateToModuleState.put(new Integer(Bundle.INSTALLED), new Integer(IServer.STATE_STOPPED));
		mapOSGiBundleStateToModuleState.put(new Integer(Bundle.RESOLVED),  new Integer(IServer.STATE_STOPPED));

		mapOSGiBundleStateToModuleState.put(new Integer(Bundle.STARTING), new Integer(IServer.STATE_STARTING));
		mapOSGiBundleStateToModuleState.put(new Integer(Bundle.STOPPING), new Integer(IServer.STATE_STOPPING));

		mapOSGiBundleStateToModuleState.put(new Integer(Bundle.ACTIVE),   new Integer(IServer.STATE_STARTED));
	}
	
	private final BundleEvent bundleEvent;

	public SwordfishOSGiModuleEvent(BundleEvent event) {
		bundleEvent = event;
	}

	public int getModuleEventState() {
		return mapOSGiBundleEventToModuleState(bundleEvent.getType());
	}
	 
	public String getBundleLocation() {
		return bundleEvent.getBundle().getLocation();
	}
	 
	public static int mapOSGiBundleEventToModuleState(int isgiBundleStatus) {
		Object moduleState = mapOSGiBundleEventToModuleState.get(new Integer(isgiBundleStatus));
		if (null != moduleState) {
			return ((Integer) moduleState).intValue();
		}
		return IServer.STATE_UNKNOWN;
	}

	public static int mapOSGiBundleStateToModuleState(int isgiBundleStatus) {
		Object moduleState = mapOSGiBundleStateToModuleState.get(new Integer(isgiBundleStatus));
		if (null != moduleState) {
			return ((Integer) moduleState).intValue();
		}
		return IServer.STATE_UNKNOWN;
	}
}
