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
package org.eclipse.swordfish.core.management.operations;

import org.eclipse.swordfish.papi.internal.extension.operations.InternalOperations;

/**
 * Description of the static part of operational messages. Instances of this interface act as
 * parameter to the {@link InternalOperations#notify} methods.
 * 
 * Each operational message has the attributes
 * <ul>
 * <li>msgID - a number acting as unique ID for this message <i>deprecated</i></li>
 * <li>qualifiedName - a string that can act as unique ID for this message type Should be
 * hierarchical, with a package name indicating the general type and a meaningful shortname for the
 * particular message.</li>
 * <li>severity - the severity of the triggered event.</li>
 * <li>category - some String indicating the area of the event. InternalSBB will prepend the
 * participantID to the given category to clearly separate participant notifications from sbb
 * notifications</li>
 * <li>rawMessage - the raw message which can be formatted by a MessageFormat this message will
 * include markers for inclkusion of the parameters describing the specific environment of the
 * event.</li>
 * <li>paramCount - expected number of parameters specifying the environment for this message</li>
 * </ul>
 * 
 */
public interface OperationalMessage {

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
     * Gets the qualified name.
     * 
     * @return the qualified name
     */
    String getQualifiedName();

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
    Severity getSeverity();

}
