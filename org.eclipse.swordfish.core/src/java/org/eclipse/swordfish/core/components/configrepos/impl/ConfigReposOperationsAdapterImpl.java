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

import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.springframework.beans.factory.DisposableBean;

/**
 * The Class ConfigReposOperationsAdapterImpl.
 * 
 */
public class ConfigReposOperationsAdapterImpl implements ConfigReposOperationalLogger, DisposableBean {

    /** Operational logger. */
    private Operations opsLogger = null;

    /**
     * Instantiates a new config repos operations adapter impl.
     */
    public ConfigReposOperationsAdapterImpl() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        this.opsLogger = null;
    }

    /**
     * Gets the ops logger.
     * 
     * @return Returns the opsLogger.
     */
    public Operations getOpsLogger() {
        return this.opsLogger;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aMessagId
     *        the a messag id
     * @param aArgumentList
     *        the a argument list
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger#issueOperationalLog(int,
     *      java.lang.Object[])
     */
    public void issueOperationalLog(final int aMessagId, final Object[] aArgumentList) {
        switch (aMessagId) {
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPRESOURCEFETCH:
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPRESOURCEFETCH));
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSHUTDOWN:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSHUTDOWN
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSHUTDOWN), aArgumentList[0]);
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSTARTUP:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSTARTUP
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSTARTUP), aArgumentList[0]);
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE), aArgumentList[0]);
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_FAILOVER:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_FAILOVER
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_FAILOVER), aArgumentList[0]);
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION), aArgumentList[0]);
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_LOCALFILEMISSING:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_LOCALFILEMISSING
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_LOCALFILEMISSING), aArgumentList[0]);
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE:
                if ((null == aArgumentList) || (aArgumentList.length != 1))
                    throw new IllegalArgumentException("Wrong number of parameters in opslog call '"
                            + ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE
                            + "' for configuration repository proxy.");
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE));
                break;
            case ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_REMOTECALLEXCEPTION:
                this.opsLogger.notify(new ConfigReposOperationalMessage(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_REMOTECALLEXCEPTION));
                break;
            default:
                throw new IllegalArgumentException("Unknown error code issue to operation logger for configuration proxy.");
        }

    }

    /**
     * Sets the ops logger.
     * 
     * @param opLogger
     *        The opsLogger to set.
     */
    public void setOpsLogger(final Operations opLogger) {
        this.opsLogger = opLogger;
    }

}
