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
package org.eclipse.swordfish.tooling.deploy.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swordfish.tooling.deploy.Logger;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Swordfish OSGi facet install action delegate implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishOSGiFacetInstallDelegate implements IDelegate
{
    public void execute( final IProject pj,
                         final IProjectFacetVersion fv,
                         final Object config,
                         final IProgressMonitor monitor )

        throws CoreException

    {
        monitor.beginTask( "", 1 );

        try
        {
        	Logger.info("SwordfishOSGiFacetInstallDelegate.execute(IProject: " + pj
        					+ ", IProjectFacetVersion: " + fv
        					+ ", Object config: " + config
        					+ ", IProgressMonitor: " + monitor + ")");       	

            monitor.worked( 1 );
        }
        finally
        {
            monitor.done();
        }
    }
}
