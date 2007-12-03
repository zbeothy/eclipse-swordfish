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
package org.eclipse.swordfish.core.interceptor.sticker.impl;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * The Class KeyObject.
 */
public class KeyObject {

    /** The ident. */
    private InternalParticipantIdentity ident;

    /** The service name. */
    private QName serviceName;

    /**
     * The Constructor.
     * 
     * @param ident
     *        the ident
     * @param name
     *        the name
     */
    public KeyObject(final InternalParticipantIdentity ident, final QName name) {
        super();
        this.ident = ident;
        this.serviceName = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof KeyObject)) return false;
        KeyObject ko = (KeyObject) obj;

        return (((this.ident == null) & (ko.ident == null)) | ((this.ident != null) & this.ident.equals(ko)))
                & (((this.serviceName == null) & (ko.serviceName == null)) | ((this.serviceName != null) & this.serviceName
                    .equals(ko)));
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.ident.hashCode() + 3 * this.serviceName.hashCode();
    }

}
