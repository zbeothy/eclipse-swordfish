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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;

public class SwordfishLaunchableAdapterDelegate extends LaunchableAdapterDelegate {

	public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact)
			throws CoreException {

		Logger.info("SworfishLaunchableAdapterDelegate.getLaunchable(IServer: " + server + ", IModuleArtifact: " + moduleArtifact + ")");
		
		if (server == null || moduleArtifact == null) {
			return null;
		}
		
		if (server.getAdapter(SwordfishServerDelegate.class) == null) {
			Logger.info("SworfishLaunchableAdapterDelegate.getLaunchable(...) - server.getAdapter(SwordfishServerDelegate.class) == null");
			return null;
		}

		return new String("DUMMY");
	}

}
