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

import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitHandler;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitHandlerFactory;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitState;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * The Class DeploymentUnitImpl.
 */
public class DeploymentUnitImpl implements DeploymentUnit {

    /** The name. */
    private String name;

    /** The type. */
    private DeploymentUnitType type;

    /** The state. */
    private DeploymentUnitState state;

    /** The deployment path. */
    private String deploymentPath;

    /** The unit handler. */
    private DeploymentUnitHandler unitHandler;

    /**
     * Instantiates a new deployment unit impl.
     * 
     * @param elem
     *        the elem
     * @param sbbInstallPath
     *        the sbb install path
     */
    public DeploymentUnitImpl(final Element elem, final String sbbInstallPath) {
        NodeList nl;
        nl = elem.getElementsByTagName("name");
        if (nl.getLength() > 0) {
            String sName = nl.item(0).getFirstChild().getNodeValue();
            this.name = sName.trim();
        }
        nl = elem.getElementsByTagName("path");
        if (nl.getLength() > 0) {
            String path = nl.item(0).getFirstChild().getNodeValue();
            this.deploymentPath = path.trim();
        }
        nl = elem.getElementsByTagName("type");
        if (nl.getLength() > 0) {
            String sType = nl.item(0).getFirstChild().getNodeValue();
            this.type = DeploymentUnitType.fromString(sType.trim());
        }
        nl = elem.getElementsByTagName("state");
        if (nl.getLength() > 0) {
            String sState = nl.item(0).getFirstChild().getNodeValue();
            this.state = DeploymentUnitState.fromString(sState.trim());
        }
        this.unitHandler = DeploymentUnitHandlerFactory.getInstance(this.deploymentPath, sbbInstallPath, this.type);
    }

    /**
     * Instantiates a new deployment unit impl.
     * 
     * @param name
     *        the name
     * @param deploymentPath
     *        the deployment path
     * @param sbbInstallPath
     *        the sbb install path
     */
    public DeploymentUnitImpl(final String name, final String deploymentPath, final String sbbInstallPath) {
        this.name = name;
        this.deploymentPath = deploymentPath;
        this.state = DeploymentUnitState.UNKNOWN;
        this.unitHandler = DeploymentUnitHandlerFactory.getInstance(this.deploymentPath, sbbInstallPath);
        this.type = this.unitHandler.getHandlingType();
    }

    /**
     * Fill into element.
     * 
     * @param elem
     *        the elem
     */
    public void fillIntoElement(final Element elem) {
        Document doc = elem.getOwnerDocument();
        Text text = null;

        Element nameElement = doc.createElement("name");
        text = doc.createTextNode(this.getName());
        nameElement.appendChild(text);
        elem.appendChild(nameElement);

        Element typeElement = doc.createElement("type");
        text = doc.createTextNode(this.getType().toString());
        typeElement.appendChild(text);
        elem.appendChild(typeElement);

        Element pathElement = doc.createElement("path");
        text = doc.createTextNode(this.getDeploymentPath());
        pathElement.appendChild(text);
        elem.appendChild(pathElement);

        Element stateElement = doc.createElement("state");
        text = doc.createTextNode(this.getState().toString());
        stateElement.appendChild(text);
        elem.appendChild(stateElement);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit#getDeploymentPath()
     */
    public String getDeploymentPath() {
        return this.deploymentPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit#getHandler()
     */
    public DeploymentUnitHandler getHandler() {
        return this.unitHandler;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit#getState()
     */
    public DeploymentUnitState getState() {
        return this.state;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit#getType()
     */
    public DeploymentUnitType getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit#setState(org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitState)
     */
    public void setState(final DeploymentUnitState state) {
        this.state = state;
    }
}
