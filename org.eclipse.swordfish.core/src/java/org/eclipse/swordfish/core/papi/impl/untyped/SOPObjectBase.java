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
package org.eclipse.swordfish.core.papi.impl.untyped;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * This class is intended to have all common functionalities that are offered by all InternalSBB
 * related objects.
 */
public abstract class SOPObjectBase {

    /** the set of the handlers for this object. */
    private Set handlerSet;

    /**
     * delegate constructor for the subclasses.
     */
    protected SOPObjectBase() {
        this.handlerSet = new HashSet();
    }

    /**
     * Adds the authentication handler.
     * 
     * @param handler
     *        the handler
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(org.eclipse.swordfish.papi.authentication.InternalAuthenticationHandler)
     */
    public void addAuthenticationHandler(final InternalAuthenticationHandler handler) throws InternalIllegalInputException {
        if (handler == null) throw new IllegalArgumentException("provided authentication handler must not be null.");

        if (this.authHandlerClassAlreadyExists(handler))
            throw new InternalIllegalInputException("A Handler of type " + handler.getClass().getName()
                    + " has already been registered.");
        this.handlerSet.add(handler);

    }

    /**
     * Gets the authentication handlers.
     * 
     * @return -- an array of registered authentication handlers TODO make this public
     */
    public InternalAuthenticationHandler[] getAuthenticationHandlers() {
        InternalAuthenticationHandler[] array = new InternalAuthenticationHandler[this.handlerSet.size()];
        return (InternalAuthenticationHandler[]) this.handlerSet.toArray(array);
    }

    /**
     * Gets the participant identity as string.
     * 
     * @return the participant identity as string
     */
    public abstract String getParticipantIdentityAsString();

    /**
     * Removes the authentication handler.
     * 
     * @param handler
     *        the handler
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#removeAuthenticationHandler(org.eclipse.swordfish.papi.authentication.InternalAuthenticationHandler)
     */
    public void removeAuthenticationHandler(final InternalAuthenticationHandler handler) {
        if (this.handlerSet.contains(handler)) {
            this.handlerSet.remove(handler);
        }
    }

    /**
     * determines if the given handler is already included in the list of handlers. Comparision is
     * done using the class information.
     * 
     * @param handler
     *        the handler to check for its duplicity
     * 
     * @return true if the class is already included, false otherwise
     */
    private boolean authHandlerClassAlreadyExists(final InternalAuthenticationHandler handler) {
        Iterator iter = this.handlerSet.iterator();
        String name = handler.getClass().getName();
        while (iter.hasNext()) {
            if (name.equals(iter.next().getClass().getName())) return true;
        }
        return false;
    }
}
