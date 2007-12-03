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

import java.util.Collection;
import javax.jbi.messaging.MessageExchange;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * This is the base interface for all processing components. IMPORTANT: Each processing component
 * must define our interface which extends this one and in this interface must be some like this:
 * <code>String ROLE = SomeProcessingComponent.class.getName();</code>
 * 
 */
public interface ProcessingComponent {

    /**
     * This method responds to the query whether the implementing component can handle the set of
     * assertions passed.
     * 
     * @param assertions
     *        the assertions
     * 
     * @return boolean result
     * 
     * @throws InternalSBBException
     *         in case of error
     */
    boolean canHandle(Collection/* <Assertion> */assertions) throws InternalSBBException;

    /**
     * Contains all javax.transform.Source interfaces can handle.
     * 
     * @return the interfaces that this ProcessingComponent supports
     */
    Class[] getSupportedSources();

    /**
     * This method processes faults on the base of the context information.
     * 
     * @param context
     *        the processing context.
     * @param role
     *        the caller role - sender/receiver. Where as the operation level of role is passed
     *        here. This means a consumer has always the role of a sender and a provider has always
     *        the role of a receiver
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    void handleFault(MessageExchange context, Role role, Collection/* <Assertion> */assertions) throws InternalSBBException,
            PolicyViolatedException;

    /**
     * This method processes requests on the base of the context information.
     * 
     * @param context
     *        the processing context.
     * @param role
     *        the caller role - sender/receiver. Where as the operation level of role is passed
     *        here. This means a consumer has always the role of a sender and a provider has always
     *        the role of a receiver
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    void handleRequest(MessageExchange context, Role role, Collection/* <Assertion> */assertions) throws InternalSBBException,
            PolicyViolatedException;

    /**
     * This method processes responses on the base of the context information.
     * 
     * @param context
     *        the processing context.
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     *         if some error occurs
     * @throws PolicyViolatedException
     */
    void handleResponse(MessageExchange context, Role role, Collection/* <Assertion> */assertions) throws InternalSBBException,
            PolicyViolatedException;

    /**
     * Indicates if this component can handle type of message source.
     * 
     * @param src
     *        the src
     * 
     * @return true if the component assures to be able to handle this source type
     */
    boolean supportSource(Class src);
}
