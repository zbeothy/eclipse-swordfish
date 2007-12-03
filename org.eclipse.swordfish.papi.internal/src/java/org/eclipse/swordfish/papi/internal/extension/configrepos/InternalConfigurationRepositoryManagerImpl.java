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
package org.eclipse.swordfish.papi.internal.extension.configrepos;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationRepositoryException;
import org.eclipse.swordfish.papi.internal.extension.configrepos.event.InternalConfigurationRepositoryEventListener;

/**
 * <p>
 * This interface provides the facade which is the entry point to the SOP Configuration Repository
 * feature of the SOP platform. It combines methods for retreival of configuration / resource data,
 * as well as integration into the notification mechanisms of the SOP Configuration Repository.
 * </p>
 * 
 */
public class InternalConfigurationRepositoryManagerImpl implements InternalConfigurationRepositoryManager {

    /**
     * The type of the bean which should be wrapped
     */
    private static final String CONFIGMANAGERPROXY_CLASSNAME =
            "org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerProxyBean";

    /**
     * Logger for this papi extension type
     */
    private Logger logger = Logger.getLogger(InternalConfigurationRepositoryManager.class.getName());

    /**
     * Factory required to build XML configurations
     */
    private DocumentBuilderFactory factory = null;

    /**
     * Delegate which is being loaded inside the SE
     */
    private InternalConfigurationRepositoryManagerProxy delegate = null;

    /**
     * Constructor for the configuration manager wrapper.
     * 
     * @param aTarget
     *        which should be wrapped
     * @param aSuperType
     *        constructor which should be available
     */
    public InternalConfigurationRepositoryManagerImpl(final Object aTarget, final Class aSuperType) {
        super();
        try {
            Class proxyClass = aTarget.getClass().getClassLoader().loadClass(CONFIGMANAGERPROXY_CLASSNAME);
            Constructor builder = proxyClass.getConstructor(new Class[] {aSuperType});
            this.delegate = (InternalConfigurationRepositoryManagerProxy) builder.newInstance(new Object[] {aTarget});
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Configuration repository manager could not be instantiated. Missing " + cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("Software installation error. Encountering missing method " + nsme);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("Configuration repository manager classes not reachble due to security reasons. "
                    + iae.getMessage());
        } catch (InstantiationException ie) {
            throw new IllegalArgumentException("Configuration repository manager could not be instantiated. " + ie.getMessage());
        } catch (InvocationTargetException ite) {
            throw new IllegalArgumentException("Configuration repository manager could not be instantiated. "
                    + ite.getCause().getMessage());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aListener
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#addConfigurationRepositoryEventListner(org.eclipse.swordfish.papi.extension.configrepos.events.InternalConfigurationRepositoryEventListener)
     */
    public void addConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aListener) {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(),
                    "addConfigurationRepositoryEventListner", new Object[] {aListener});
        }
        try {
            this.delegate.addConfigurationRepositoryEventListner(aListener);
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(),
                        "addConfigurationRepositoryEventListner");
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#addConfigurationRepositoryEventListner(java.lang.String,
     *      org.eclipse.swordfish.papi.extension.configrepos.events.InternalConfigurationRepositoryEventListener)
     * @param aTreeQualifier
     * @param aListener
     */
    public void addConfigurationRepositoryEventListner(final String aTreeQualifier,
            final InternalConfigurationRepositoryEventListener aListener) {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(),
                    "addConfigurationRepositoryEventListner", new Object[] {aListener});
        }
        try {
            this.delegate.addConfigurationRepositoryEventListner(aTreeQualifier, aListener);
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(),
                        "addConfigurationRepositoryEventListner");
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     * @param aScopePath
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getConfiguration(java.lang.String,
     *      java.lang.String)
     * @throws InternalConfigurationRepositoryException
     * @return Configuration
     */
    public InputStream getConfiguration(final String aTreeQualifier, final String aScopePath)
            throws InternalConfigurationRepositoryException {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(), "getConfiguration", new Object[] {
                    aTreeQualifier, aScopePath});
        }
        XMLConfiguration result = null;
        try {
            this.assureInit();

            ByteArrayInputStream ret =
                    new ByteArrayInputStream(this.delegate.getConfiguration(aTreeQualifier, aScopePath).getBytes());
            return ret;

            // // DocumentBuilder builder = factory.newDocumentBuilder();
            // result = new XMLConfiguration();
            // result.load(new ByteArrayInputStream(delegate.getConfiguration(
            // aTreeQualifier, aScopePath).getBytes()));
            // // result
            // // .initProperties(
            // // builder.parse(new ByteArrayInputStream(delegate
            // // .getConfiguration(aTreeQualifier, aScopePath)
            // // .getBytes())), true);
            // return result;
            // // } catch (ParserConfigurationException pce) {
            // // throw new InternalConfigurationRepositoryException(
            // // "could not fetch configuration", pce);
            // // } catch (IOException ioe) {
            // // throw new InternalConfigurationRepositoryException(
            // // "error reading configuration", ioe);
            // // } catch (SAXException saxe) {
            // // throw new InternalConfigurationRepositoryException(
            // // "error parsing configuration", saxe);
        } catch (Exception ex) {
            throw new InternalConfigurationRepositoryException("error processing configuration", ex);
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(), "getConfiguration", result);
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the aTreeQualifier
     * @param aLocation
     * @param aParticipantIdentity
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getConfiguration(java.lang.String,
     *      java.lang.String, org.eclipse.swordfish.papi.InternalParticipantIdentity)
     * @throws InternalConfigurationRepositoryException
     * @return Configuration the Configuration.
     */
    public InputStream getConfiguration(final String aTreeQualifier, final String aLocation,
            final InternalParticipantIdentity aParticipantIdentity) throws InternalConfigurationRepositoryException {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(), "getConfiguration", new Object[] {
                    aTreeQualifier, aLocation, aParticipantIdentity});
        }

        XMLConfiguration result = null;
        try {
            this.assureInit();

            ByteArrayInputStream ret =
                    new ByteArrayInputStream(this.delegate.getConfiguration(aTreeQualifier, aLocation, aParticipantIdentity)
                        .getBytes());

            return ret;

            // // DocumentBuilder builder = factory.newDocumentBuilder();
            // result = new XMLConfiguration();
            // // result.initProperties(
            // // builder.parse(new
            // ByteArrayInputStream(delegate.getConfiguration(
            // // aTreeQualifier, aLocation, aParticipantIdentity).getBytes())),
            // // true);
            // result
            // .load(new ByteArrayInputStream(delegate.getConfiguration(
            // aTreeQualifier, aLocation, aParticipantIdentity)
            // .getBytes()));
            // return result;
            // // } catch (ParserConfigurationException pce) {
            // // throw new InternalConfigurationRepositoryException("could not
            // fetch
            // // configuration", pce);
            // // } catch (IOException ioe) {
            // // throw new InternalConfigurationRepositoryException("error
            // reading
            // // configuration", ioe);
            // // } catch (SAXException saxe) {
            // // throw new InternalConfigurationRepositoryException("error
            // parsing
            // // configuration", saxe);
        } catch (Exception ex) {
            throw new InternalConfigurationRepositoryException("error processing configuration", ex);
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(), "getConfiguration", result);
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getLocalResourceBase()
     * @return String the String.
     */
    public String getLocalResourceBase() {
        return this.delegate.getLocalResourceBase();
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getResource(java.lang.String,
     *      java.lang.String, org.eclipse.swordfish.papi.InternalParticipantIdentity,
     *      java.lang.String, java.lang.String)
     * @param aTreeQualifier
     * @param aLocation
     * @param aParticipantIdentity
     * @param aComponent
     * @param aResourceIdentifier
     * @throws InternalConfigurationRepositoryException
     * @return InputStream
     */
    public InputStream getResource(final String aTreeQualifier, final String aLocation,
            final InternalParticipantIdentity aParticipantIdentity, final String aComponent, final String aResourceIdentifier)
            throws InternalConfigurationRepositoryException {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(), "getResource", new Object[] {
                    aTreeQualifier, aLocation, aParticipantIdentity, aComponent, aResourceIdentifier});
        }
        InputStream result = null;
        try {
            result = this.delegate.getResource(aTreeQualifier, aLocation, aParticipantIdentity, aComponent, aResourceIdentifier);
            return result;
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(), "getResource", result);
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     * @param aScopePath
     * @param aComponent
     * @param aResourceIdentifier
     * 
     * @throws InternalConfigurationRepositoryException
     * 
     * @return InputStream the InputStream.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getResource(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final String aScopePath, final String aComponent,
            final String aResourceIdentifier) throws InternalConfigurationRepositoryException {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(), "getResource", new Object[] {
                    aTreeQualifier, aScopePath, aComponent, aResourceIdentifier});
        }
        InputStream result = null;
        try {
            result = this.delegate.getResource(aTreeQualifier, aScopePath, aComponent, aResourceIdentifier);
            return result;
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(), "getResource", result);
            }
        }
    }

    /**
     * Check whether the configuration repository proxy instance skipping the usage of a remote
     * configuration repository or not.
     * 
     * @return if remote calls are configured to be skipped
     */
    public boolean isSkipRemoteRepositoryCalls() {
        return this.delegate.isSkipRemoteRepositoryCalls();
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#removeConfigurationRepositoryEventListner(org.eclipse.swordfish.papi.extension.configrepos.events.InternalConfigurationRepositoryEventListener)
     * @param aListener
     */
    public void removeConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aListener) {
        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(InternalConfigurationRepositoryManagerImpl.class.getName(),
                    "removeConfigurationRepositoryEventListner", new Object[] {aListener});
        }
        try {
            this.delegate.removeConfigurationRepositoryEventListner(aListener);
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(InternalConfigurationRepositoryManagerImpl.class.getName(),
                        "removeConfigurationRepositoryEventListner");
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#setLocalResourceBase(java.lang.String)
     * @param aFilePath
     */
    public void setLocalResourceBase(final String aFilePath) {
        this.delegate.setLocalResourceBase(aFilePath);
    }

    /**
     * This flag defines whether the configuration repository proxy instance shall skip remote calls
     * and service configurations and resources from the local sources. The respective configuration
     * defined in the SBB deployment will not be changed and will be available at the moment the
     * next manager is instantiated.
     * 
     * @param aSkip
     *        whether remote calls should be skipped
     */
    public void setSkipRemoteRepositoryCalls(final boolean aSkip) {
        this.delegate.setSkipRemoteRepositoryCalls(aSkip);
    }

    /**
     * Try to create a related document builder factory very late in the interaction section
     */
    private synchronized void assureInit() {
        if (null == this.factory) {
            this.factory = DocumentBuilderFactory.newInstance();
        }
    }
}
