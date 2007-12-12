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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.launching.AbstractVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * VM runner fake implementation.
 * 
 * @author Alex Tselesh
 */
public class FakeVMRunner extends AbstractVMRunner {

	/**
	 * @see VMRunner#getPluginIdentifier()
	 */
	protected String getPluginIdentifier() {
		return DeployPlugin.PLUGIN_ID;
	}

	public void run(VMRunnerConfiguration configuration, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask("Launching Fake VM...", 1);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		subMonitor.subTask("Starting Swordfish server...");
		subMonitor.worked(1);

		subMonitor.done();
	}
}
