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
package org.eclipse.swordfish.policytrader;

/**
 * Operation policy object as created by the {@link PolicyFactory} from raw policy data.
 */
public interface OperationPolicy {

    /** The Constant WSP_NAMESPACE_URI. */
    String WSP_NAMESPACE_URI = "http://www.w3.org/2006/07/ws-policy";

    /** The Constant WSP_POLICY_TAG. */
    String WSP_POLICY_TAG = "Policy";

}
