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
package org.eclipse.swordfish.core.components.processing;

import javax.xml.namespace.QName;

/**
 * This class contains processing constants.
 * 
 */
public final class ProcessingConstants {

    /** This is the URI for all sbb properties in context. */
    public static final String AGREED_POLICY_URI = "http://types.sopware.org/qos/AgreedPolicy/1.0";

    /** This is the QName of the agreed policy property name. */
    public static final QName AGREED_POLICY = new QName(AGREED_POLICY_URI, "Agreed");

    /**
     * Prevents creation of this class.
     */
    private ProcessingConstants() {
    }

}
