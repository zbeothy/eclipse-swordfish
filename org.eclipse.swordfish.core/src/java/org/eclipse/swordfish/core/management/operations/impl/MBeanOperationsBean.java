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
package org.eclipse.swordfish.core.management.operations.impl;

import java.util.HashMap;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.management.operations.ParametrizedOperationalMessageType;
import org.eclipse.swordfish.core.utils.jmx.NamingStrategyFactory;

/**
 * The Class MBeanOperationsBean.
 */
public class MBeanOperationsBean implements MBeanOperationsBeanMBean {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(MBeanOperationsBean.class);

    /** The message types. */
    private Map messageTypes = new HashMap();

    /** The operations. */
    private Operations operations;

    /** The m bean server. */
    private MBeanServer mBeanServer;

    /** The object name. */
    private ObjectName objectName;

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isInfoEnabled()) {
            LOG.info("destroying");
        }
        if (null != this.mBeanServer) {
            try {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Unregistering self from mbs. ObjectName: " + this.objectName.toString());
                }
                this.mBeanServer.unregisterMBean(this.objectName);
            } catch (Exception e) {
                LOG.warn("Unexpected exception unregistering self from mbs", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.MBeanOperationsBeanMBean#publish(java.lang.String,
     *      java.lang.String[])
     */
    public void publish(final String msgType, final String[] params) {
        ParametrizedOperationalMessageType type = (ParametrizedOperationalMessageType) this.messageTypes.get(msgType);
        if (null == type) throw new IllegalArgumentException("Unknown message type " + msgType);
        this.operations.notify(type, params);
        if (LOG.isTraceEnabled()) {
            LOG.trace("published operational log message of type " + type.getQualifiedName());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.operations.impl.MBeanOperationsBeanMBean#registerMessageType(java.lang.String,
     *      java.lang.String[])
     */
    public void registerMessageType(final String msgType, final String[] attributes) {
        ParametrizedOperationalMessageType type = new ParametrizedOperationalMessageType(attributes);
        this.messageTypes.put(msgType, type);
        if (LOG.isDebugEnabled()) {
            LOG.trace("registered message type " + msgType);
        }
    }

    /**
     * Sets the mbean server.
     * 
     * @param mbs
     *        the new mbean server
     * 
     * @throws MalformedObjectNameException
     * @throws InstanceAlreadyExistsException
     * @throws MBeanRegistrationException
     * @throws NotCompliantMBeanException
     */
    public void setMbeanServer(final MBeanServer mbs) throws MalformedObjectNameException, InstanceAlreadyExistsException,
            MBeanRegistrationException, NotCompliantMBeanException {
        this.mBeanServer = mbs;
        this.objectName =
                NamingStrategyFactory.getNamingStrategy()
                    .createObjectName("sbb/internal:type=OperationsBean,id=" + this.hashCode());
        mbs.registerMBean(this, this.objectName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("registered self at mbs with objectName " + this.objectName.toString());
        }
    }

    /**
     * Sets the operations.
     * 
     * @param ops
     *        the new operations
     */
    public void setOperations(final Operations ops) {
        this.operations = ops;
        if (LOG.isDebugEnabled()) {
            LOG.debug("registered operations");
        }
    }

}
