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

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * Swordfish launch configuration delegate implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String launchMode, final ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		
		// INFO LOGGING
		StringBuffer sb = new StringBuffer();
		sb.append("ILaunchConfiguration configuration: " + configuration.getName())
			.append("\t file: " + configuration.getFile())
			.append("\t location: " + configuration.getLocation());
			
		Map attributes = configuration.getAttributes();
		sb.append("\t attributes {" + attributes.size() + "}:");
		Iterator it = attributes.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Object value = attributes.get(key);
			sb.append("\t\t " + key + ": " + value);
		}
		Logger.info(sb.toString());
		//INFO LOGGING
		
		
		
		IServer server = ServerUtil.getServer(configuration);
		Logger.info("\t server: " + server);
		if (server == null) {
			IStatus status = new Status(IStatus.ERROR, DeployPlugin.PLUGIN_ID, 0, "Launch configuration could not find server", null);
			Logger.log(status);
			throw new CoreException(status);
		}
		
		SwordfishServerBehaviourDelegate swordfishServer = (SwordfishServerBehaviourDelegate) server.loadAdapter(SwordfishServerBehaviourDelegate.class, null);
		Logger.info("swordfish server: " + swordfishServer);

		
		swordfishServer.setupLaunch(launch, launchMode, monitor);
		
		IVMRunner runner = new FakeVMRunner();
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
				"FakeSwordfishStartClass", new String[] {"fake.class.path"});

		//Launch the configuration
		try {
			runner.run(runConfig, launch, monitor);
		} catch (CoreException e) {
			Logger.error("error launching Swordfish server", e);
			swordfishServer.terminate();
			throw e;
		}
		
		swordfishServer.setServerStarted();
	}
}

