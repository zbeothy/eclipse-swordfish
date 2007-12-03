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
package org.eclipse.swordfish.core.components.configrepos.impl;

import java.io.InputStream;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerExternalizablelImpl;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationRepositoryException;
import org.eclipse.swordfish.papi.internal.extension.configrepos.InternalConfigurationRepositoryManagerProxy;
import org.eclipse.swordfish.papi.internal.extension.configrepos.event.InternalConfigurationRepositoryEventListener;
import org.springframework.beans.factory.DisposableBean;

/**
 * The Class ConfigurationRepositoryManagerProxyBean.
 * 
 */
public class ConfigurationRepositoryManagerProxyBean implements InternalConfigurationRepositoryManagerProxy, DisposableBean {

    /** The delegate which is being used to forward the calls. */
    private ConfigurationRepositoryManagerInternal delegate = null;

    /**
     * Constructor with the internal manager which calls are being delegated to.
     * 
     * @param aDelegate
     *        to forward the calls to
     */
    public ConfigurationRepositoryManagerProxyBean(final ConfigurationRepositoryManagerExternalizablelImpl aDelegate) {
        super();
        this.delegate = aDelegate;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aEventListener
     *        the a event listener
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#addConfigurationRepositoryEventListner(org.eclipse.swordfish.papi.extension.configrepos.events.InternalConfigurationRepositoryEventListener)
     */
    public void addConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aEventListener) {
        // ConfigurationRepositoryEventListenerInternal delegate = new
        // ConfigurationRepositoryEventListenerInternal() {
        //
        // public void notify(
        // final ConfigurationRepositoryEventInternal
        // configurationrepositoryevent) {
        // aEventListener.notify(new InternalConfigurationRepositoryEvent() {
        //
        // public String getScopePath() {
        // return configurationrepositoryevent.getScopePath();
        // }
        //
        // public String getTreeQualifier() {
        // return configurationrepositoryevent.getTreeQualifier();
        // }
        // });
        // }
        // };
        // // FIXME 'addConfigurationRepositoryEventListner' not implemented yet
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aEventListener
     *        the a event listener
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#addConfigurationRepositoryEventListner(java.lang.String,
     *      org.eclipse.swordfish.papi.extension.configrepos.events.InternalConfigurationRepositoryEventListener)
     */
    public void addConfigurationRepositoryEventListner(final String aTreeQualifier,
            final InternalConfigurationRepositoryEventListener aEventListener) {
        // ConfigurationRepositoryEventListenerInternal delegate = new
        // ConfigurationRepositoryEventListenerInternal() {
        //
        // public void notify(
        // final ConfigurationRepositoryEventInternal
        // configurationrepositoryevent) {
        // aEventListener.notify(new InternalConfigurationRepositoryEvent() {
        //
        // public String getScopePath() {
        // return configurationrepositoryevent.getScopePath();
        // }
        //
        // public String getTreeQualifier() {
        // return configurationrepositoryevent.getTreeQualifier();
        // }
        // });
        // }
        // };
        // FIXME 'addConfigurationRepositoryEventListner' not implemented yet
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        this.delegate.close();
        this.delegate = null;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePathString
     *        the a scope path string
     * 
     * @return the configuration
     * 
     * @throws InternalConfigurationRepositoryException
     *         on error.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#getConfiguration(java.lang.String,
     *      java.lang.String)
     */
    public String getConfiguration(final String aTreeQualifier, final String aScopePathString)
            throws InternalConfigurationRepositoryException {
        try {
            return ((XMLConfiguration) this.delegate.getConfiguration(aTreeQualifier, new ScopePathUtil(
                    ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR).composeScopePath(aScopePathString))).toString();
        } catch (ConfigurationRepositoryConfigException crce) {
            throw new InternalConfigurationRepositoryException(crce);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aLocation
     *        the a location
     * @param aPartId
     *        the a part id
     * 
     * @return the configuration
     * 
     * @throws InternalConfigurationRepositoryException
     *         on error.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#getConfiguration(java.lang.String,
     *      java.lang.String, org.eclipse.swordfish.papi.InternalParticipantIdentity)
     */
    public String getConfiguration(final String aTreeQualifier, final String aLocation, final InternalParticipantIdentity aPartId)
            throws InternalConfigurationRepositoryException {
        try {
            String appId = null;
            String instId = null;
            if (aPartId != null) {
                appId = aPartId.getApplicationID();
                instId = aPartId.getInstanceID();
            }
            return ((XMLConfiguration) this.delegate.getConfiguration(aTreeQualifier, new ScopePathUtil(
                    ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR).composeScopePath(aLocation, appId, instId)))
                .toString();
        } catch (ConfigurationRepositoryConfigException crce) {
            throw new InternalConfigurationRepositoryException(crce);
        } catch (ScopePathException spe) {
            throw new InternalConfigurationRepositoryException(spe);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the local resource base
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#getLocalResourceBase()
     */
    public String getLocalResourceBase() {
        return this.delegate.getLocalResourceBase();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aLocation
     *        the a location
     * @param aPartId
     *        the a part id
     * @param aComponent
     *        the a component
     * @param aResourceName
     *        the a resource name
     * 
     * @return the resource
     * 
     * @throws InternalConfigurationRepositoryException
     *         on error.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#getResource(java.lang.String,
     *      java.lang.String, org.eclipse.swordfish.papi.InternalParticipantIdentity,
     *      java.lang.String, java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final String aLocation, final InternalParticipantIdentity aPartId,
            final String aComponent, final String aResourceName) throws InternalConfigurationRepositoryException {
        try {
            String appId = null;
            String instId = null;
            if (aPartId != null) {
                appId = aPartId.getApplicationID();
                instId = aPartId.getInstanceID();
            }
            return this.delegate.getResource(aTreeQualifier, new ScopePathUtil(
                    ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR).composeScopePath(aLocation, appId, instId),
                    aComponent, aResourceName);
        } catch (ConfigurationRepositoryResourceException crre) {
            throw new InternalConfigurationRepositoryException(crre);
        } catch (ScopePathException spe) {
            throw new InternalConfigurationRepositoryException(spe);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePathString
     *        the a scope path string
     * @param aComponent
     *        the a component
     * @param aResourceName
     *        the a resource name
     * 
     * @return the resource
     * 
     * @throws InternalConfigurationRepositoryException
     *         on error.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#getResource(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final String aScopePathString, final String aComponent,
            final String aResourceName) throws InternalConfigurationRepositoryException {
        try {
            return this.delegate.getResource(aTreeQualifier, new ScopePathUtil(
                    ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR).composeScopePath(aScopePathString), aComponent,
                    aResourceName);
        } catch (ConfigurationRepositoryResourceException crre) {
            throw new InternalConfigurationRepositoryException(crre);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return true, if is skip remote repository calls
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#isSkipRemoteRepositoryCalls()
     */
    public boolean isSkipRemoteRepositoryCalls() {
        return this.delegate.isSkipRemoteRepositoryCalls();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aEventListener
     *        the a event listener
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#removeConfigurationRepositoryEventListner(org.eclipse.swordfish.papi.extension.configrepos.events.InternalConfigurationRepositoryEventListener)
     */
    public void removeConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aEventListener) {
        // FIXME 'removeConfigurationRepositoryEventListner' not implemented yet
    }

    /**
     * (non-Javadoc).
     * 
     * @param aPath
     *        the a path
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#setLocalResourceBase(java.lang.String)
     */
    public void setLocalResourceBase(final String aPath) {
        this.delegate.setLocalResourceBase(aPath);
    }

    /**
     * (non-Javadoc).
     * 
     * @param aSkip
     *        the a skip
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManagerProxy#setSkipRemoteRepositoryCalls(boolean)
     */
    public void setSkipRemoteRepositoryCalls(final boolean aSkip) {
        this.delegate.setSkipRemoteRepositoryCalls(aSkip);
    }
}
