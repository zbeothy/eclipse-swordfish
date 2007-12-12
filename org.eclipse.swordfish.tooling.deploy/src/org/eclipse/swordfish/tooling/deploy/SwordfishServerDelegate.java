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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jst.server.core.FacetUtil;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * Swordfish server implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishServerDelegate extends ServerDelegate {

    public SwordfishServerDelegate() {
    	super();
    	Logger.info("SwordfishServerDelegate()");
    }

	protected void initialize() {
		super.initialize();
		Logger.info("SwordfishServerDelegate(): server state - "
				+ OSGiProjectUtilities.resolveModuleState(getServer().getServerState()));
	}

	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		
		StringBuffer sb = new StringBuffer("SwordfishServerDelegate.canModifyModules(IModule[] add, IModule[] remove)");
		sb.append("\t IModule[] add - " + OSGiProjectUtilities.printModulesInfo(add));
		sb.append("\t IModule[] remove - " + OSGiProjectUtilities.printModulesInfo(remove));
		Logger.info(sb.toString());
		
		if (add != null) { // remove is OK.
			int size = add.length;
			for (int i = 0; i < size; i++) {
				IModule module = add[i];

				if (module.getProject() != null) {
					IStatus status = FacetUtil.verifyFacets(module.getProject(), getServer());
					Logger.info("SwordfishServerDelegate.canModifyModules(...): module facet status - " + status);
					if (status != null && !status.isOK())
						return status;
				}
			}
		}
		
		return Status.OK_STATUS;
	}
	
    public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
    	
    }

	public IModule[] getChildModules(IModule[] module) {
		return new IModule[0];
	}

    public IModule[] getRootModules(IModule module) throws CoreException {
     	
    	Logger.info("SwordfishServerDelegate.getRootModules(" + OSGiProjectUtilities.printModuleInfo(module) + ")");

		IStatus status = canModifyModules(new IModule[] { module }, null);
        if (status != null && !status.isOK()) {
        	Logger.info("SwordfishServerDelegate.getRootModules() - status: " + status);
            throw new CoreException(status);
        }

  		return new IModule[] { module };
    }

}
