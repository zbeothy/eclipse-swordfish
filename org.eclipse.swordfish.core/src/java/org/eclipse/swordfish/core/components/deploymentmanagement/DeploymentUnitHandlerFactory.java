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
package org.eclipse.swordfish.core.components.deploymentmanagement;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.components.deploymentmanagement.impl.ConfigurationDeploymentUnitHandler;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A factory for creating DeploymentUnitHandler objects.
 */
public class DeploymentUnitHandlerFactory {

    /** The Constant DEPLOYMENT_DESCRIPTOR. */
    private static final String DEPLOYMENT_DESCRIPTOR = "sbb.xml";

    /** The db. */
    private static DocumentBuilder db;

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(DeploymentUnitHandlerFactory.class);

    static {
        try {
            db = TransformerUtil.getDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage());
        } catch (FactoryConfigurationError e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Gets the single instance of DeploymentUnitHandlerFactory.
     * 
     * @param path
     *        the path
     * @param sbbInstallpath
     *        the sbb installpath
     * 
     * @return single instance of DeploymentUnitHandlerFactory
     * 
     * @throws InfrastructureRuntimeException
     */
    public static synchronized DeploymentUnitHandler getInstance(final String path, final String sbbInstallpath)
            throws InfrastructureRuntimeException {
        String finalPath = path + File.separator + "META-INF" + File.separator + DEPLOYMENT_DESCRIPTOR;
        File descriptor = new File(finalPath);
        Document deployDoc = null;
        try {
            deployDoc = db.parse(descriptor);
        } catch (SAXException e) {
            throw new InfrastructureRuntimeException("cannot parse the deployment descriptor file in " + path, e);
        } catch (IOException e) {
            throw new InfrastructureRuntimeException("cannot read the deployment descriptor file in " + path, e);
        }
        Element elem = deployDoc.getDocumentElement();

        if (!"deployment".equals(elem.getNodeName()))
            throw new InfrastructureRuntimeException("unrecognized deplyoment document in  " + path);
        String deploymentType = elem.getAttribute("type");
        if (deploymentType == null)
            throw new InfrastructureRuntimeException("No deployment type found in the deplyoment descriptor in " + path);
        DocumentFragment fragment = deployDoc.createDocumentFragment();
        NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            fragment.appendChild(nl.item(i));
        }

        // now we go through the stuff we have and pass the directory and the
        // rest of the doc
        if ("configuration".equalsIgnoreCase(deploymentType))
            return new ConfigurationDeploymentUnitHandler(path, sbbInstallpath + File.separator + "conf", fragment);

        // if we did nto returned yet than we have a unknown deployment type
        throw new InfrastructureRuntimeException("unknown deployment type '" + deploymentType + "' in  descriptor in " + path);
    }

    /**
     * Gets the single instance of DeploymentUnitHandlerFactory.
     * 
     * @param path
     *        the path
     * @param sbbInstallpath
     *        the sbb installpath
     * @param type
     *        the type
     * 
     * @return single instance of DeploymentUnitHandlerFactory
     */
    public static synchronized DeploymentUnitHandler getInstance(final String path, final String sbbInstallpath,
            final DeploymentUnitType type) {

        if (DeploymentUnitType.CONFIGURATION.equals(type))
            return new ConfigurationDeploymentUnitHandler(path, sbbInstallpath + File.separator + "conf", null);

        if (DeploymentUnitType.BOOTSTRAP_CONFIGURATION.equals(type))
            return new ConfigurationDeploymentUnitHandler(path, sbbInstallpath + File.separator + "conf", null);

        throw new InfrastructureRuntimeException("unknown deployment type '" + type);
    }
}
