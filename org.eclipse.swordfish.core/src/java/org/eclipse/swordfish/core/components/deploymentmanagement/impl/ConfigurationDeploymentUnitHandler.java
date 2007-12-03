/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.components.deploymentmanagement.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitHandler;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitType;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.w3c.dom.DocumentFragment;

/**
 * The Class ConfigurationDeploymentUnitHandler.
 */
public class ConfigurationDeploymentUnitHandler extends AbstractDeploymentUnitHandler implements DeploymentUnitHandler {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(ConfigurationDeploymentUnitHandler.class);

    /** The deployment path. */
    private String deploymentPath;

    /** The installation path. */
    private String installationPath;

    // private DocumentFragment instructions;

    /**
     * The Constructor.
     * 
     * @param fragment
     *        the remaining set of the instructions for this deplyoment
     * @param deploymentPath
     *        the deployment path
     * @param installationPath
     *        the installation path
     */
    public ConfigurationDeploymentUnitHandler(final String deploymentPath, final String installationPath,
            final DocumentFragment fragment) {
        super();
        this.deploymentPath = deploymentPath;
        // this.instructions = fragment;
        this.installationPath = installationPath;
    }

    /**
     * Deploy.
     * 
     * @throws InternalInfrastructureException
     */
    public void deploy() throws InternalInfrastructureException {
        File source = new File(this.deploymentPath);
        File destination = new File(this.installationPath);
        this.shuffelFiles(source, destination, 0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitHandler#getHandlingType()
     */
    public DeploymentUnitType getHandlingType() {
        return DeploymentUnitType.CONFIGURATION;
    }

    /**
     * Undeploy.
     * 
     * @throws InternalInfrastructureException
     */
    public void undeploy() throws InternalInfrastructureException {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Copy.
     * 
     * @param src
     *        the src
     * @param dest
     *        the dest
     */
    private void copy(final FileReader src, final FileWriter dest) {
        char[] buf = new char[2048];
        try {
            int readed = 0;
            do {
                readed = src.read(buf);
                if (readed > 0) {
                    dest.write(buf, 0, readed);
                }
            } while (readed > 0);

            dest.flush();
            dest.close();
            src.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Shuffel files.
     * 
     * @param src
     *        the src
     * @param dest
     *        the dest
     * @param level
     *        the level
     * 
     * @throws InternalInfrastructureException
     */
    private void shuffelFiles(final File src, final File dest, final int level) throws InternalInfrastructureException {

        if (src.isDirectory()) {
            // shuffel directory structures
            if (!dest.exists()) {
                // System.out.println("creating " + dest.getAbsolutePath());
                dest.mkdirs();
            }
            File[] flist = src.listFiles();
            for (int i = 0; i < flist.length; i++) {
                File destfile = new File(dest.getAbsolutePath() + File.separator + flist[i].getName());
                if (!(destfile.getName().endsWith("META-INF") && (level == 0))) {
                    this.shuffelFiles(flist[i], destfile, level + 1);
                }
            }
        } else {
            // copy the files iff the deployment directory has newer
            // versions than the destination directory
            if (!dest.exists() || (dest.lastModified() < src.lastModified())) {
                try {
                    FileReader reader = new FileReader(src);
                    FileWriter writer = new FileWriter(dest);
                    this.copy(reader, writer);

                } catch (FileNotFoundException e) {
                    LOG.error("cannot copy deployment related files", e);
                } catch (IOException e) {
                    LOG.error("cannot copy deployment related files", e);
                }
            }
        }
    }
}
