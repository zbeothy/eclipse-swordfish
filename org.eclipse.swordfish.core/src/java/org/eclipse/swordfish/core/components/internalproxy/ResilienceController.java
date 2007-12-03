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
package org.eclipse.swordfish.core.components.internalproxy;

import java.util.Collection;
import java.util.List;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;

/**
 * The Interface ResilienceController.
 * 
 */
public interface ResilienceController {

    /**
     * Creates the in only exchange.
     * 
     * @param serviceOperation
     *        which should be called
     * @param messageExchangeBag
     *        the message exchange bag
     * 
     * @return the message exchange which should be used for the next call. Return null in case no
     *         subsequent retries should be executed
     * 
     * @throws Exception
     *         of any type
     */
    MessageExchange createInOnlyExchange(Collection messageExchangeBag, QName serviceOperation) throws Exception;

    /**
     * Creates the in out exchange.
     * 
     * @param serviceDescriptionList
     *        which should be called
     * @param serviceOperation
     *        which should be called
     * 
     * @return the message exchange which should be used for the next call. Return null in case no
     *         subsequent retries should be executed
     * 
     * @throws Exception
     *         of any type
     */
    MessageExchange createInOutExchange(final Collection serviceDescriptionList, final QName serviceOperation) throws Exception;

    /**
     * Allow to close the exchange gracefully.
     * 
     * @param aExchange
     *        which has gone through its lifetime
     * 
     * @throws Exception
     *         an exception
     */
    void finalizeExchange(final MessageExchange aExchange) throws Exception;

    /**
     * Return the timeout the proxy should apply when calling a remote provider.
     * 
     * @return Time in seconds, or 0 for no timeout setting
     */
    long getCallTimeout();

    /**
     * Handle an error in the current exchange.
     * 
     * @param aExchange
     *        which caused and error
     * 
     * @throws Exception
     *         of any type
     */
    void handleError(final MessageExchange aExchange) throws Exception;

    /**
     * Handle exception.
     * 
     * @param e
     *        is the exception to process
     * 
     * @return a exception if this shall be rethrown, null in case the exception should be ignored
     */
    Exception handleException(final Exception e);

    /**
     * Handle failure.
     * 
     * @param aExceptionList
     *        which has been compiled so far
     * 
     * @return object which should be handed over to the client code as result of the call
     * 
     * @throws Exception
     *         of any type
     */
    Object handleFailure(final List aExceptionList) throws Exception;

    /**
     * Postprocess exchange.
     * 
     * @param aExchange
     *        is the exchange which should be post-processed
     * 
     * @throws Exception
     *         of any type
     */
    void postprocessExchange(final MessageExchange aExchange) throws Exception;

    /**
     * Post-process the inbound message.
     * 
     * @param aOutMessage
     *        comming out of the message exchange
     * @param aFault
     *        which was extracted from the exchange
     * 
     * @return a result which should be passed as the result of the call
     * 
     * @throws Exception
     *         of any type
     */
    Object postprocessMessage(final NormalizedMessage aOutMessage, final Fault aFault) throws Exception;

    /**
     * Preprocess exchange.
     * 
     * @param aExchange
     *        which should be preprocessed before creating a message based on it
     * 
     * @throws Exception
     *         of any type
     */
    void preprocessExchange(final MessageExchange aExchange) throws Exception;

    /**
     * Preprocess message.
     * 
     * @param aInMessage
     *        which should be preprocessed
     * 
     * @throws Exception
     *         of any type
     */
    void preprocessMessage(final NormalizedMessage aInMessage) throws Exception;

}
