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
package org.eclipse.swordfish.core.management.instrumentation.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;
import org.eclipse.swordfish.core.components.iapi.impl.KernelBean;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.AlreadyRegisteredException;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;

/**
 * Responsible for the registration / deregistration of instrumenation objects.
 * 
 */
public class InstrumentationManagerBean implements InstrumentationManager, PropertyChangeListener {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(InstrumentationManagerBean.class);

    /** The commons.modeler registry that is used */
    private Registry registry;

    /** MBeanServer to use. */
    private MBeanServer mBeanServer;

    /** Comment for <code>objectNameFactory</code>. */
    private ObjectNameFactory objectNameFactory;

    /** specifier is added after "sbb" to domain name. */
    private String specifier = "component";

    /** default domain elements for ObjectNames if none are specified. */
    private List domainParts;

    /**
     * Shared by all InstrumenationManager instances, set by container key object registered as
     * managed component value <code>ObjectName</code> of corresponding MBean.
     */
    private HashMap managedObjects;

    private KernelBean kernel;

    /** The participant. */
    private String participant;

    /** flag indicating if this bean is still active. Set to false when shutting down */
    private boolean active = true;

    /**
     * Constructor.
     */
    public InstrumentationManagerBean() {
        this.registry = Registry.getRegistry(this, null);
        Registry.setUseContextClassLoader(true);
        this.domainParts = new Vector(1);
        this.domainParts.add(this.specifier);
    }

    /**
     * Destroy.
     */
    public synchronized void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        this.active = false;
        String[] beanNames = this.registry.findManagedBeans();
        for (int i = 0; i < beanNames.length; i++) {
            ManagedBean bean = this.registry.findManagedBean(beanNames[i]);
            if (null != bean) {
                this.registry.removeManagedBean(bean);
            }
        }
        this.registry.setMBeanServer(null);
        this.registry.stop();
        this.registry = null;
        this.domainParts.clear();
        this.objectNameFactory = null;
        // this.managedObjects.clear();
        // this.managedObjects = new HashMap();
        this.mBeanServer = null;
    }

    /**
     * Gets the managed objects.
     * 
     * @return the managed objects
     */
    public HashMap getManagedObjects() {
        return this.managedObjects;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager#getObjectName(java.lang.Object)
     */
    public ObjectName getObjectName(final Object instrumentation) {
        ManagedObjectInfo info = (ManagedObjectInfo) this.managedObjects.get(instrumentation);
        ObjectName ret = null;
        if (null != info) {
            ret = info.getOn();
        }
        return ret;
    }

    /**
     * Gets the participant.
     * 
     * @return the participant
     */
    public String getParticipant() {
        return this.participant;
    }

    /**
     * Gets the specifier.
     * 
     * @return the specifier
     */
    public String getSpecifier() {
        return this.specifier;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public synchronized void propertyChange(final PropertyChangeEvent evt) {
        if ("participant".equals(evt.getPropertyName())) {
            String oldParticipant = String.valueOf(evt.getOldValue());
            String newParticipant = String.valueOf(evt.getNewValue());
            this.reregisterManagedObjects(oldParticipant, newParticipant);
            this.participant = newParticipant;
        }
    }

    /**
     * Register instrumentation.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param description
     *        the description
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,
     *      java.io.InputStream)
     */
    public void registerInstrumentation(final Object instrumentation, final InputStream description)
            throws InternalInfrastructureException, AlreadyRegisteredException {
        String typeName = instrumentation.getClass().getName();
        this.registerInstrumentation(instrumentation, description, typeName);
    }

    /**
     * Register instrumentation for components who /really/ know what they want as ObjectName.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param description
     *        the description
     * @param customDomainParts
     *        the custom domain parts
     * @param nameProperties
     *        the name properties
     * @param typeName
     *        the type name
     * 
     * @throws InternalAlreadyRegisteredException
     * @throws ParticipantHandlingException
     */
    public void registerInstrumentation(final Object instrumentation, final InputStream description, final List customDomainParts,
            final Properties nameProperties, final String typeName) throws AlreadyRegisteredException,
            InternalInfrastructureException {
        ObjectName on = this.objectNameFactory.getObjectName(instrumentation, customDomainParts, nameProperties);
        if (null != on) {
            this.register(instrumentation, description, typeName, on);
        } else {
            String onString = this.objectNameFactory.getObjectNameAsString(instrumentation, this.domainParts, nameProperties);
            String msg = "Could not create ObjectName for " + onString;
            throw new InternalInfrastructureException(msg);
        }
    }

    /**
     * Register instrumentation.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param description
     *        the description
     * @param nameProperties
     *        the name properties
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,
     *      java.io.InputStream, java.util.Properties)
     */
    public void registerInstrumentation(final Object instrumentation, final InputStream description, final Properties nameProperties)
            throws InternalInfrastructureException, AlreadyRegisteredException {
        String typeName = instrumentation.getClass().getName();
        this.registerInstrumentation(instrumentation, description, nameProperties, typeName);
    }

    /**
     * Register instrumentation.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param description
     *        the description
     * @param nameProperties
     *        the name properties
     * @param typeName
     *        the type name
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,
     *      java.io.InputStream, java.util.Properties, java.lang.String)
     */
    public void registerInstrumentation(final Object instrumentation, final InputStream description,
            final Properties nameProperties, final String typeName) throws InternalInfrastructureException,
            AlreadyRegisteredException {
        ObjectName on = this.objectNameFactory.getObjectName(instrumentation, this.domainParts, nameProperties);
        if (null != on) {
            this.register(instrumentation, description, typeName, on);
        } else {
            String onString = this.objectNameFactory.getObjectNameAsString(instrumentation, this.domainParts, nameProperties);
            String msg = "Could not create ObjectName for " + onString;
            throw new InternalInfrastructureException(msg);
        }
    }

    /**
     * Register instrumentation.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param description
     *        the description
     * @param typeName
     *        the type name
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,
     *      java.io.InputStream, java.lang.String)
     */
    public void registerInstrumentation(final Object instrumentation, final InputStream description, final String typeName)
            throws InternalInfrastructureException, AlreadyRegisteredException {
        ObjectName on = this.objectNameFactory.getObjectName(instrumentation, this.domainParts);
        if (null != on) {
            this.register(instrumentation, description, typeName, on);
        } else {
            String onString = this.objectNameFactory.getObjectNameAsString(instrumentation, this.domainParts);
            String msg = "Could not create ObjectName for " + onString;
            throw new InternalInfrastructureException(msg);
        }
    }

    public void setKernel(final KernelBean aKernel) {
        this.kernel = aKernel;
        this.participant = String.valueOf(aKernel.getParticipant());
        aKernel.addPropertyChangeListener("participant", this);
    }

    /**
     * Sets the managed objects.
     * 
     * @param managedObjects
     *        the new managed objects
     */
    public void setManagedObjects(final HashMap managedObjects) {
        this.managedObjects = managedObjects;
    }

    /**
     * creates a unique object name string for the instrumentation object.
     * 
     * @param nameProperties
     *        the name properties
     * @param instrumentation
     *        the instrumentation
     * 
     * @return the object name string
     */
    /*
     * private String getObjectNameString(final Object instrumentation, final Properties
     * nameProperties) { nameProperties.put("class", instrumentation.getClass().getName());
     * nameProperties.put("id", new Integer(instrumentation.hashCode()) .toString()); return
     * getPlainObjectNameString(nameProperties, "sbb/components:"); }
     */

    /**
     * Sets the mbean server.
     * 
     * @param mbs
     *        the new mbean server
     */
    public void setMbeanServer(final MBeanServer mbs) {
        this.mBeanServer = mbs;
        if (null != this.registry) {
            this.registry.setMBeanServer(mbs);
        }
    }

    /**
     * Sets the object name factory.
     * 
     * @param objectNameFactory
     *        the new object name factory
     */
    public void setObjectNameFactory(final ObjectNameFactory objectNameFactory) {
        this.objectNameFactory = objectNameFactory;
    }

    /**
     * Sets the specifier.
     * 
     * @param specifier
     *        the new specifier
     */
    public void setSpecifier(final String specifier) {
        this.specifier = specifier;
        this.domainParts.clear();
        this.domainParts.add(specifier);
    }

    /**
     * (non-Javadoc).
     * 
     * @param instrumentation
     *        the instrumentation
     * 
     * @return true, if unregister instrumentation
     * 
     * @throws ParticipantHandlingException
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#unregisterInstrumentation(java.lang.Object)
     */
    public synchronized boolean unregisterInstrumentation(final Object instrumentation) throws InternalInfrastructureException {
        if (!this.active) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Tried to unregister instrumentation " + instrumentation + " after shutdown of InstrumenationManager");
            }
            return false;
        }
        boolean ret = false;
        try {
            ObjectName on;
            synchronized (this.managedObjects) {
                ManagedObjectInfo info = (ManagedObjectInfo) this.managedObjects.get(instrumentation);
                on = null;
                if (null != info) {
                    on = info.getOn();
                    this.managedObjects.remove(instrumentation);
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Unknown instrumentation object - ignoring " + instrumentation);
                    }
                }
            }
            if ((null != on) && (null != this.mBeanServer)) {
                Set names = this.mBeanServer.queryNames(on, null);
                for (Iterator iter = names.iterator(); iter.hasNext();) {
                    ObjectName name = (ObjectName) iter.next();
                    this.mBeanServer.unregisterMBean(name);
                    ret = true;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unregistered instrumenation object " + instrumentation + " with ObjectName " + on);
            }

        } catch (Exception e) {
            String msg = "Could not unregister instrumentation object. Reason:\n" + e.getClass().getName() + ":\n" + e.getMessage();
            throw new InternalInfrastructureException(msg);
        }
        return ret;
    }

    /**
     * creates a plain object name string for the instrumenation object It is the caller's
     * responsiblity to make the object name unique by appropriate name properties.
     * 
     * @param nameProperties
     *        the name properties
     * @param domain
     *        the domain
     * 
     * @return the plain object name string
     */
    protected String getPlainObjectNameString(final Properties nameProperties, final String domain) {
        StringBuffer onString = new StringBuffer(domain);
        Iterator iter = nameProperties.keySet().iterator();
        if (iter.hasNext()) {
            String name = (String) iter.next();
            onString.append(this.getPair(nameProperties, name));
            while (iter.hasNext()) {
                name = (String) iter.next();
                onString.append("," + this.getPair(nameProperties, name));
            }
        }
        return new String(onString);
    }

    /**
     * registration method for components that already have their description registered.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param on
     *        the on
     * 
     * @throws Exception
     */
    protected void registerInstrumentation(final Object instrumentation, final ObjectName on) throws Exception {
        String typeName = instrumentation.getClass().getName();
        this.registry.registerComponent(instrumentation, on, typeName);
        ManagedObjectInfo info = new ManagedObjectInfo(on, typeName);
        synchronized (this.managedObjects) {
            this.managedObjects.put(instrumentation, info);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registered instrumentation object " + instrumentation + " with ObjectName " + on);
        }
    }

    /**
     * Registers an MBean description.
     * 
     * @param is
     *        the is
     * 
     * @throws Exception
     * 
     * @see org.apache.commons.modeler.Registry#loadMetadata(java.lang.Object)
     */
    protected void registerInstrumentationDescription(final InputStream is) throws Exception {
        this.registry.loadMetadata(is);
    }

    /**
     * Gets the pair.
     * 
     * @param nameProperties
     *        the name properties
     * @param key
     *        the key
     * 
     * @return the pair
     */
    private String getPair(final Properties nameProperties, final String key) {
        String value = (String) nameProperties.get(key);
        return new String(key + "=" + value);
    }

    /**
     * Register.
     * 
     * @param instrumentation
     *        the instrumentation
     * @param description
     *        the description
     * @param typeName
     *        the type name
     * @param on
     *        the on
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     */
    private void register(final Object instrumentation, final InputStream description, final String typeName, final ObjectName on)
            throws InternalInfrastructureException, AlreadyRegisteredException {
        synchronized (this.managedObjects) {
            if (this.managedObjects.keySet().contains(instrumentation))
                throw new AlreadyRegisteredException(instrumentation + " is already registered as instrumentation object.",
                        instrumentation);
            ClassLoader oldCl = null;
            try {
                oldCl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                this.registry.loadMetadata(description);
                this.registry.registerComponent(instrumentation, on, typeName);
                ManagedObjectInfo info = new ManagedObjectInfo(on, typeName);
                this.managedObjects.put(instrumentation, info);
            } catch (Exception e) {
                String msg =
                        "Could not read instrumentation description. Reason:\n" + e.getClass().getName() + ":\n" + e.getMessage();
                throw new InternalInfrastructureException(msg);
            } finally {
                if (null != oldCl) {
                    Thread.currentThread().setContextClassLoader(oldCl);
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registered instrumentation object " + instrumentation + " with ObjectName " + on);
        }
    }

    /**
     * Reregister managed objects.
     * 
     * @param oldParticipant
     *        the old participant
     * @param newParticipant
     *        the new participant
     */
    private void reregisterManagedObjects(final String oldParticipant, final String newParticipant) {
        synchronized (this.managedObjects) {
            ArrayList candidates = new ArrayList(this.managedObjects.size());
            for (Iterator iter = this.managedObjects.keySet().iterator(); iter.hasNext();) {
                Object instrumentation = iter.next();
                ManagedObjectInfo info = (ManagedObjectInfo) this.managedObjects.get(instrumentation);
                String pid = info.getOn().getKeyProperty("pid");
                if (oldParticipant.equals(pid)) {
                    candidates.add(instrumentation);
                }
            }
            for (Iterator iter = candidates.iterator(); iter.hasNext();) {
                Object instrumentation = iter.next();
                ManagedObjectInfo info = (ManagedObjectInfo) this.managedObjects.get(instrumentation);
                Properties props = new Properties();
                props.putAll(info.getOn().getKeyPropertyList());
                props.put("pid", newParticipant);
                ArrayList colDomainParts = new ArrayList(1);
                String oldDomain = info.getOn().getDomain();
                colDomainParts.add(oldDomain.replaceAll("^sbb/", ""));
                try {
                    this.unregisterInstrumentation(instrumentation);
                    ObjectName newOn = this.objectNameFactory.getObjectName(instrumentation, colDomainParts, props);
                    String typeName = info.getTypeId();
                    this.registry.registerComponent(instrumentation, newOn, typeName);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Reregistered instrumentation object " + instrumentation + " old " + info.getOn() + " new "
                                + newOn);
                    }
                    ManagedObjectInfo newInfo = new ManagedObjectInfo(newOn, typeName);
                    this.managedObjects.put(instrumentation, newInfo);
                } catch (Exception e) {
                    LOG.error("Unexpected exception in InstrumentationManagerBean.reregisterManagedObjects."
                            + "Please report to SOPsolutions support with the following information:\n" + e.getMessage());
                }
            }
        }
    }

    /**
     * The Class ManagedObjectInfo.
     */
    private class ManagedObjectInfo {

        /** The on. */
        private ObjectName on;

        /** The type id. */
        private String typeId;

        /**
         * Instantiates a new managed object info.
         * 
         * @param on
         *        the on
         * @param typeId
         *        the type id
         */
        public ManagedObjectInfo(final ObjectName on, final String typeId) {
            this.on = on;
            this.typeId = typeId;
        }

        /**
         * Gets the on.
         * 
         * @return the on
         */
        public ObjectName getOn() {
            return this.on;
        }

        /**
         * Gets the type id.
         * 
         * @return the type id
         */
        public String getTypeId() {
            return this.typeId;
        }

    }

}
