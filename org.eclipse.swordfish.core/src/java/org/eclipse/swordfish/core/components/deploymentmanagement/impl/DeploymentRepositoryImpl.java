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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentRepository;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitState;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.DOM2Writer;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class DeploymentRepositoryImpl.
 */
public class DeploymentRepositoryImpl extends Observable implements DeploymentRepository {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(DeploymentRepository.class);

    /** The Constant deploymentReposFileName. */
    private final static String DEPLOYMENT_REPOS_FILE_NAME = "deployments.xml";

    /** The deployments. */
    private Map deployments;

    /** pointer to the engine install directory. */
    private String deploymentReposFileLocation;

    /**
     * Instantiates a new deployment repository impl.
     * 
     * @param deploymentReposFileLocation
     *        the deployment repos file location
     */
    public DeploymentRepositoryImpl(final String deploymentReposFileLocation) {
        this.deploymentReposFileLocation = deploymentReposFileLocation;
        this.deployments = new HashMap();
        this.load();
    }

    /**
     * Adds the deployment unit.
     * 
     * @param unit
     *        the unit
     */
    public void addDeploymentUnit(final DeploymentUnit unit) {
        this.deployments.put(unit.getName(), unit);
        this.save();
    }

    /**
     * Gets the deployment repository location.
     * 
     * @return the deployment repository location
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentRepository#getDeploymentRepositoryLocation()
     */
    public String getDeploymentRepositoryLocation() {
        return this.deploymentReposFileLocation;
    }

    /**
     * Gets the deployment unit.
     * 
     * @param name
     *        the name
     * 
     * @return the deployment unitt
     */
    public DeploymentUnit getDeploymentUnit(final String name) {
        return (DeploymentUnit) this.deployments.get(name);
    }

    /**
     * Gets the deployment unit names.
     * 
     * @return the deployment unit names
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentRepository#getDeploymentUnitNames()
     */
    public Collection getDeploymentUnitNames() {
        return this.deployments.keySet();
    }

    /**
     * Gets the deployment units in state.
     * 
     * @param state
     *        the state
     * 
     * @return the deployment units in statee
     */
    public Collection getDeploymentUnitsInState(final DeploymentUnitState state) {
        Iterator iter = this.deployments.keySet().iterator();
        Collection result = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (this.getDeploymentUnit(key).getState().equals(state)) {
                result.add(this.getDeploymentUnit(key));
            }
        }
        return result;
    }

    /**
     * Gets the out of sync deplyoments.
     * 
     * @return the out of sync deplyomentss
     */
    public Collection getOutOfSyncDeplyoments() {
        Iterator iter = this.deployments.keySet().iterator();
        Collection result = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            File path = new File(this.getDeploymentUnit(key).getDeploymentPath());
            if (!path.exists()) {
                result.add(this.getDeploymentUnit(key));
            }
        }
        return result;
    }

    /**
     * Removes the deployment unit.
     * 
     * @param name
     *        the name
     */
    public void removeDeploymentUnit(final String name) {
        this.deployments.remove(name);
        this.save();
    }

    /**
     * Sets the deployment unit state.
     * 
     * @param name
     *        the name
     * @param state
     *        the state
     */
    public void setDeploymentUnitState(final String name, final DeploymentUnitState state) {
        DeploymentUnit unit = this.getDeploymentUnit(name);
        unit.setState(state);
        this.save();
    }

    /**
     * Load.
     */
    private void load() {
        File file = new File(this.deploymentReposFileLocation + File.separator + DEPLOYMENT_REPOS_FILE_NAME);
        Document doc = null;
        DocumentBuilder db;
        if (file.exists()) {
            try {
                db = TransformerUtil.getDocumentBuilder();
                doc = db.parse(file);
            } catch (ParserConfigurationException e) {
                LOG.error(e.toString());
                // e.printStackTrace();
            } catch (FactoryConfigurationError e) {
                LOG.error(e.toString());
                // e.printStackTrace();
            } catch (SAXException e) {
                LOG.error(e.toString());
                // e.printStackTrace();
            } catch (IOException e) {
                LOG.error(e.toString());
                // e.printStackTrace();
            }
        } else {
            LOG.info(DEPLOYMENT_REPOS_FILE_NAME + " could not be restored, assuming fresh installation");
            return;
        }

        NodeList nl = doc.getDocumentElement().getElementsByTagName("deployment");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // FIXME ... use a factory rather
                DeploymentUnit unit = new DeploymentUnitImpl((Element) node, this.deploymentReposFileLocation);
                this.deployments.put(unit.getName(), unit);
            }
        }
    }

    /**
     * Save.
     */
    private synchronized void save() {
        File file = new File(this.deploymentReposFileLocation + File.separator + DEPLOYMENT_REPOS_FILE_NAME);
        Document doc = null;
        DocumentBuilder db;
        try {
            db = TransformerUtil.getDocumentBuilder();
            doc = db.newDocument();
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage());
        } catch (FactoryConfigurationError e) {
            LOG.error(e.getMessage());
        }
        Element elem = doc.createElement("deployments");
        Iterator iter = this.deployments.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            DeploymentUnitImpl unit = (DeploymentUnitImpl) this.getDeploymentUnit(key);
            Element unitElem = doc.createElement("deployment");
            unit.fillIntoElement(unitElem);
            elem.appendChild(unitElem);
        }
        doc.appendChild(elem);
        try {
            DOM2Writer.serializeAsXML(doc, new FileWriter(file), true, true);
        } catch (IOException e) {
            LOG.error("cannot write the " + DEPLOYMENT_REPOS_FILE_NAME + "caused by " + e.getMessage());
        }
    }
}
