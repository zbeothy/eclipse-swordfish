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

import java.util.ResourceBundle;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.core.management.operations.AbstractOperationalMessage;

/**
 * The Class ConfigReposOperationalMessage.
 * 
 */
public class ConfigReposOperationalMessage extends AbstractOperationalMessage {

    /** The Constant CONFIGREPOSPROXY_COMPSTARTUP. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_COMPSTARTUP =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSTARTUP);

    /** The Constant CONFIGREPOSPROXY_COMPSHUTDOWN. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_COMPSHUTDOWN =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSHUTDOWN);

    /** The Constant CONFIGREPOSPROXY_CONFIGRESOURCEFETCH. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_CONFIGRESOURCEFETCH =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPRESOURCEFETCH);

    /** The Constant CONFIGREPOSPROXY_CONFIGRESOURCEFAILOVER. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_CONFIGRESOURCEFAILOVER =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_FAILOVER);

    /** The Constant CONFIGREPOSPROXY_INTERNALEXCEPTION. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_INTERNALEXCEPTION =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION);

    /** The Constant CONFIGREPOSPROXY_LOCALFILEMISSING. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_LOCALFILEMISSING =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_LOCALFILEMISSING);

    /** The Constant CONFIGREPOSPROXY_ERRORREADINGLOCALFILE. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_ERRORREADINGLOCALFILE =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE);

    /** The Constant CONFIGREPOSPROXY_NOOVERRIDERESOLVABLE. */
    public static final ConfigReposOperationalMessage CONFIGREPOSPROXY_NOOVERRIDERESOLVABLE =
            new ConfigReposOperationalMessage(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE);

    /**
     * The Constructor.
     * 
     * @param aMsgID
     *        for a message id to be used
     */
    public ConfigReposOperationalMessage(final int aMsgID) {
        super(aMsgID);
    }

    /**
     * The Constructor.
     * 
     * @param aMsgID
     *        for a message id to be used
     * @param aBundle
     *        the resource bundle to be applied
     */
    public ConfigReposOperationalMessage(final int aMsgID, final ResourceBundle aBundle) {
        super(aMsgID, aBundle);
    }

    /**
     * The Constructor.
     * 
     * @param aMsgID
     *        for a message id to be used
     * @param aBundleName
     *        the resource bundle to be applied
     */
    public ConfigReposOperationalMessage(final int aMsgID, final String aBundleName) {
        super(aMsgID, aBundleName);
    }

}
