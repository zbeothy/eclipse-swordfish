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
package org.eclipse.swordfish.papi.internal.extension.operations;

/**
 * Description of the static part of operational messages. Instances of this interface act as
 * parameter to the {@link InternalOperations#notify} methods.
 * 
 * Each operational message has the attributes
 * <ul>
 * <li>msgID - a number acting as unique ID for this message</li>
 * <li>severity - the severity of the triggered event.</li>
 * <li>category - some String indicating the area of the event. SBB will prepend the participantID
 * to the given category to clearly separate participant notifications from sbb notifications</li>
 * <li>rawMessage - the raw message which can be formatted by a MessageFormat this message will
 * include markers for inclkusion of the parameters describing the specific environment of the
 * event.</li>
 * <li>paramCount - expected number of parameters specifying the environment for this message</li>
 * </ul>
 * Please note that the instances of this interface are not meant to hold the actual parameters.
 * There should be only one instance for each message type (not implementation type!) and the actual
 * parameters for each message to be reported should be provided with the call to the appropriate
 * <code>InternalOperations.notify(...)</code> method. See the javadoc in
 * <code>InternalAbstractOperationalMessage</code> for details.
 * 
 * @see org.eclipse.swordfish.papi.extension.operations.InternalAbstractOperationalMessage
 * 
 */
public interface InternalOperationalMessage {

    /**
     * Gets the category.
     * 
     * @return the category
     */
    String getCategory();

    /**
     * Gets the message ID.
     * 
     * @return the message ID
     */
    int getMsgID();

    /**
     * Gets the expected number of parameters.
     * 
     * @return the expected number of parameters
     */
    int getParamCount();

    /**
     * Gets the raw message.
     * 
     * @return the raw message
     */
    String getRawMessage();

    /**
     * Gets the severity.
     * 
     * @return the severity
     */
    InternalSeverity getSeverity();

}
