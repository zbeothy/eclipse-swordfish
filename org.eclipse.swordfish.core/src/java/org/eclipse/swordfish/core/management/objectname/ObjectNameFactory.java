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
package org.eclipse.swordfish.core.management.objectname;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.components.iapi.impl.KernelBean;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.jmx.NamingStrategyFactory;

/**
 * Utility class to create <code>ObjectName</code>s that
 * <ul>
 * <li>correctly identify the ParticipantID for the participant who owns the bean</li>
 * <li>are always unique</li>
 * <li>can be extended with optional domain and name/value parts</li>
 * </ul>.
 * 
 */
public class ObjectNameFactory implements PropertyChangeListener {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ObjectNameFactory.class);

    /** The Constant DEFAULT_PARTICIPANT_ID. */
    private final static String DEFAULT_PARTICIPANT_ID = "sbb";

    /** The Constant empty. */
    private final static List EMPTY = new Vector(0);

    /** The participant id. */
    private String participantId = DEFAULT_PARTICIPANT_ID;

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
    }

    /**
     * Builds an basic ObjectName.
     * 
     * @param bean
     *        to build ObjectName for
     * 
     * @return the object name
     */
    public ObjectName getObjectName(final Object bean) {
        Properties nameProperties = this.createDefaultNameProperties(bean);
        return this.getObjectName(bean, EMPTY, nameProperties);
    }

    /**
     * Builds an ObjectName with custom domain elements.
     * 
     * @param bean
     *        to build ObjectName for
     * @param domainParts
     *        <code>java.util.List</code> of <code>String</code> elements representing domain
     *        parts. '/' will be inserted as separator
     * 
     * @return the object name
     */
    public ObjectName getObjectName(final Object bean, final List domainParts) {
        Properties nameProperties = this.createDefaultNameProperties(bean);
        return this.getObjectName(bean, domainParts, nameProperties);
    }

    /**
     * Gets the object name.
     * 
     * @param bean
     *        to build ObjectName for
     * @param domainParts
     *        <code>java.util.List</code> of <code>String</code> elements representing domain
     *        parts. '/' will be inserted as separator
     * @param nameProperties
     *        key/value pairs for the properties part of the ObjectName
     * 
     * @return the object name
     */
    public ObjectName getObjectName(final Object bean, final List domainParts, final Properties nameProperties) {
        String onString = this.getObjectNameAsString(bean, domainParts, nameProperties);
        ObjectName on = null;
        try {
            on = NamingStrategyFactory.getNamingStrategy().createObjectName(onString);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created ObjectName " + onString);
            }
        } catch (MalformedObjectNameException e) {
            LOG.warn("Could not create ObjectName for " + onString + ". Exception " + e.getMessage());
        }
        return on;
    }

    /**
     * Builds an ObjectName with custom name properties.
     * 
     * @param bean
     *        to build ObjectName for
     * @param nameProperties
     *        key/value pairs for the properties part of the ObjectName
     * 
     * @return the object name
     */
    public ObjectName getObjectName(final Object bean, final Properties nameProperties) {
        return this.getObjectName(bean, EMPTY, nameProperties);
    }

    /**
     * Builds an basic ObjectName.
     * 
     * @param bean
     *        to build ObjectName for
     * 
     * @return the object name as string
     */
    public String getObjectNameAsString(final Object bean) {
        Properties nameProperties = this.createDefaultNameProperties(bean);
        return this.getObjectNameAsString(bean, EMPTY, nameProperties);
    }

    /**
     * Builds an ObjectName with custom domain elements.
     * 
     * @param bean
     *        to build ObjectName for
     * @param domainParts
     *        <code>java.util.List</code> of <code>String</code> elements representing domain
     *        parts. '/' will be inserted as separator
     * 
     * @return the object name as string
     */
    public String getObjectNameAsString(final Object bean, final List domainParts) {
        Properties nameProperties = this.createDefaultNameProperties(bean);
        return this.getObjectNameAsString(bean, domainParts, nameProperties);
    }

    /**
     * Gets the object name as string.
     * 
     * @param bean
     *        to build ObjectName for
     * @param domainParts
     *        <code>java.util.List</code> of <code>String</code> elements representing domain
     *        parts. '/' will be inserted as separator
     * @param nameProperties
     *        key/value pairs for the properties part of the ObjectName
     * 
     * @return the object name as string
     */
    public String getObjectNameAsString(final Object bean, final List domainParts, final Properties nameProperties) {
        StringBuffer domain = new StringBuffer("sbb");
        for (Iterator iter = domainParts.iterator(); iter.hasNext();) {
            String part = (String) iter.next();
            if (null != part) {
                domain.append("/").append(part);
            }
        }
        // make sure that participant id is put first
        // no guarantee as to order in final ObjectName, but seems to work for
        // now (MX4J)
        domain.append(":pid=");
        if (nameProperties.keySet().contains("pid")) {
            domain.append(nameProperties.get("pid"));
            nameProperties.remove("pid");
        } else {
            domain.append(this.participantId.replace(',', ':'));
        }
        // make sure that id string is part of object name to ensure
        if (!nameProperties.keySet().contains("id")) {
            nameProperties.put("id", String.valueOf(bean.hashCode()));
        }
        return this.getPlainObjectNameString(domain, nameProperties);
    }

    /**
     * Builds an ObjectName with custom name properties.
     * 
     * @param bean
     *        to build ObjectName for
     * @param nameProperties
     *        key/value pairs for the properties part of the ObjectName
     * 
     * @return the object name as string
     */
    public String getObjectNameAsString(final Object bean, final Properties nameProperties) {
        return this.getObjectNameAsString(bean, EMPTY, nameProperties);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("participant".equals(evt.getPropertyName())) {
            this.participantId = String.valueOf(evt.getNewValue());
        }
    }

    /**
     * Sets the kernel.
     * 
     * @param aKernel
     *        the new kernel
     */
    public void setKernel(final KernelBean aKernel) {
        aKernel.addPropertyChangeListener("participant", this);
        this.participantId = String.valueOf(aKernel.getParticipant());
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
    protected String getPlainObjectNameString(final String domain, final Properties nameProperties) {
        StringBuffer onString = new StringBuffer(domain);
        return this.getPlainObjectNameString(onString, nameProperties);
    }

    /**
     * Creates a new ObjectName object.
     * 
     * @param bean
     *        the bean
     * 
     * @return the properties
     */
    private Properties createDefaultNameProperties(final Object bean) {
        Properties nameProperties = new Properties();
        nameProperties.put("class", bean.getClass().getName());
        nameProperties.put("id", new Integer(bean.hashCode()).toString());
        return nameProperties;
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
        return new String(key.replace(',', '_').replace(':', '_') + "=" + value.replace(',', '_').replace(':', '_'));
    }

    /**
     * Gets the plain object name string.
     * 
     * @param domain
     *        the domain
     * @param nameProperties
     *        the name properties
     * 
     * @return the plain object name string
     */
    private String getPlainObjectNameString(final StringBuffer domain, final Properties nameProperties) {
        // ensure that id property is set last in name string
        String id = nameProperties.getProperty("id");
        nameProperties.remove("id");
        Iterator iter = nameProperties.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            domain.append("," + this.getPair(nameProperties, name));
        }
        if (null != id) {
            domain.append(",id=").append(id);
        }
        return new String(domain);
    }

}
