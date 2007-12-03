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
package org.eclipse.swordfish.core.components.instancemanager;

import org.eclipse.swordfish.core.exception.ComponentException;

/**
 * The Class AssociationException.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class AssociationException extends ComponentException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6622353587420064715L;

    /**
     * public constructore.
     */
    public AssociationException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     */
    public AssociationException(final String resourceKey) {
        super(resourceKey);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param parameter1
     *        parameter1
     */
    public AssociationException(final String resourceKey, final String parameter1) {
        super(resourceKey, parameter1);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param parameter1
     *        parameter1
     * @param parameter2
     *        parameter2 parameter2
     */
    public AssociationException(final String resourceKey, final String parameter1, final String parameter2) {
        super(resourceKey, parameter1, parameter2);

    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param parameter1
     *        parameter1
     * @param parameter2
     *        parameter2
     * @param parameter3
     *        parameter2
     */
    public AssociationException(final String resourceKey, final String parameter1, final String parameter2, final String parameter3) {
        super(resourceKey, parameter1, parameter2, parameter3);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param parameter1
     *        parameter1
     * @param parameter2
     *        parameter2
     * @param parameter3
     *        parameter2
     * @param cause
     *        cause
     */
    public AssociationException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3, final Throwable cause) {
        super(resourceKey, parameter1, parameter2, parameter3, cause);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param parameter1
     *        parameter1
     * @param parameter2
     *        parameter2
     * @param cause
     *        cause cause
     */
    public AssociationException(final String resourceKey, final String parameter1, final String parameter2, final Throwable cause) {
        super(resourceKey, parameter1, parameter2, cause);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param parameter1
     *        parameter1
     * @param cause
     *        cause
     */
    public AssociationException(final String resourceKey, final String parameter1, final Throwable cause) {
        super(resourceKey, parameter1, cause);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param messageParameters
     *        messageParameters
     */
    public AssociationException(final String resourceKey, final String[] messageParameters) {
        super(resourceKey, messageParameters);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param messageParameters
     *        messageParameters
     * @param cause
     *        cause
     */
    public AssociationException(final String resourceKey, final String[] messageParameters, final Throwable cause) {
        super(resourceKey, messageParameters, cause);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        resourceKey
     * @param cause
     *        cause
     */
    public AssociationException(final String resourceKey, final Throwable cause) {
        super(resourceKey, cause);
    }
}
