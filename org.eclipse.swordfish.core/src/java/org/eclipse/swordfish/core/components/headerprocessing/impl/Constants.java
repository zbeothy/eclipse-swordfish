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
package org.eclipse.swordfish.core.components.headerprocessing.impl;

import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Class Constants.
 */
public class Constants {

    /** The Constant WSA_NS. */
    static final public String WSA_NS = "http://www.w3.org/2005/08/addressing";

    /** The Constant WSA_NS_DOTNET. */
    static final public String WSA_NS_DOTNET = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

    /** The Constant WSAW_NS. */
    static final public String WSAW_NS = "http://www.w3.org/2005/03/addressing/wsdl";

    /** The Constant AGREED_POLICY. */
    static final String AGREED_POLICY = "{" + AgreedPolicy.AGREED_POLICY_NAMESPACE + "}Agreed";

    /** The Constant ACTION. */
    static final String ACTION = "{" + Constants.WSA_NS + "}Action";

    /** The Constant ACTION_DOTNET. */
    static final String ACTION_DOTNET = "{" + Constants.WSA_NS_DOTNET + "}Action";

    /** The Constant MESSAGE_ID. */
    static final String MESSAGE_ID = "{" + Constants.WSA_NS + "}MessageID";

    /** The Constant MESSAGE_ID_DOTNET. */
    static final String MESSAGE_ID_DOTNET = "{" + Constants.WSA_NS_DOTNET + "}MessageID";

    /** The Constant RELATES_TO. */
    static final String RELATES_TO = "{" + Constants.WSA_NS + "}RelatesTo";

    /** The Constant REPLY_TO. */
    static final String REPLY_TO = "{" + Constants.WSA_NS + "}ReplyTo";

    /** The Constant FAULT_TO. */
    static final String FAULT_TO = "{" + Constants.WSA_NS + "}FaultTo";

    /** The Constant REPLY_TO_DOTNET. */
    static final String REPLY_TO_DOTNET = "{" + Constants.WSA_NS_DOTNET + "}ReplyTo";

    /** The Constant FAULT_TO_DOTNET. */
    static final String FAULT_TO_DOTNET = "{" + Constants.WSA_NS_DOTNET + "}FaultTo";

    /** The Constant APPLICATION_ID. */
    static final String APPLICATION_ID = "{" + HeaderUtil.SBB_NS + "}ApplicationId";

    /** The Constant CORRELATION_ID. */
    static final String CORRELATION_ID = "{" + HeaderUtil.SBB_NS + "}CorrelationId";

    /** The Constant CONSCALL_ID. */
    static final String CONSCALL_ID = "{" + HeaderUtil.SBB_NS + "}ConsumerCallId";

    /** The Constant INSTANCE_ID. */
    static final String INSTANCE_ID = "{" + HeaderUtil.SBB_NS + "}InstanceId";

    /** The Constant CALL_RELATIONS. */
    static final String CALL_RELATIONS = "{" + HeaderUtil.SBB_NS + "}CallRelations";

    /** The Constant WSA_ANONYMOUS. */
    static final String WSA_ANONYMOUS = "http://www.w3.org/2005/08/addressing/anonymous";

    /** The Constant MSG_TRACKING. */
    static final String MSG_TRACKING = "{" + HeaderUtil.SBB_NS + "}MessageTracking";

    /** The Constant RESPONSE_RELATION. */
    static final String RESPONSE_RELATION = "http://www.w3.org/2005/08/addressing/reply";
}
