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
package org.eclipse.swordfish.core.interceptor.security.xkms;

import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * The Class XKMSResponseException.
 * 
 */
public class XKMSResponseException extends InternalSBBException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8049493883257272181L;

    /**
     * empty const.
     */
    public XKMSResponseException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        key
     */
    public XKMSResponseException(final String resourceKey) {
        super(resourceKey);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        cause
     * @param resourceKey
     *        key
     */
    public XKMSResponseException(final Throwable cause, final String resourceKey) {
        super(resourceKey, cause);
    }
}
