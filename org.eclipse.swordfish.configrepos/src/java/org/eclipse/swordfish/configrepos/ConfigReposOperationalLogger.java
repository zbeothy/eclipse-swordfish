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
package org.eclipse.swordfish.configrepos;

/**
 * Interface of the operational logging adapter for the configuration manager.
 * 
 */
public interface ConfigReposOperationalLogger {

    /** Startup message. */
    int CONFIGREPOSPROXY_MSGID_COMPSTARTUP = 1;

    /** Shutdown message. */
    int CONFIGREPOSPROXY_MSGID_COMPSHUTDOWN = 2;

    /** Configuration fetch message. */
    int CONFIGREPOSPROXY_MSGID_COMPRESOURCEFETCH = 3;

    /** Failover encountered. */
    int CONFIGREPOSPROXY_MSGID_FAILOVER = 4;

    /** Internal exception message. */
    int CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION = 5;

    /** Local file missing exception message. */
    int CONFIGREPOSPROXY_MSGID_LOCALFILEMISSING = 6;

    /** Error reading local file message. */
    int CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE = 7;

    /** Error finding overrideable configuration message. */
    int CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE = 8;

    /** Error while synchronizing configuratio/resource data to local disk. */
    int REPOSITORYDATA_SYNC_ERROR = 9;

    /** Error finding overrideable configuration message. */
    int CONFIGREPOSPROXY_MSGID_REMOTECALLEXCEPTION = 10;

    /**
     * Issue an operational log event.
     * 
     * @param aMessagID
     *        to identify the message to be issued
     * @param aParameters
     *        which should configure the message
     */
    void issueOperationalLog(final int aMessagID, final Object[] aParameters);
}
