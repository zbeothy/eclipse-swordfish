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
package org.eclipse.swordfish.papi.internal;

/**
 * <p>
 * <b>Interface encapsulating instance identity</b>
 * </p>
 * <p>
 * Every participant application needs to provide a class implementing this interface.
 * <code>InternalParticipantIdentity</code> objects are used by the InternalSBB to access the
 * information required to distinguish InternalSBB instances from each other.
 * </p>
 * 
 * 
 */
public interface InternalParticipantIdentity {

    /**
     * This method returns an <code>application ID</code>.
     * 
     * An applicationID identifies one specific piece of software connected via InternalSBB to an
     * SOP instance. It identifies a particiant.
     * 
     * <p>
     * An applicationID:
     * <ul>
     * <li>must be a sequence of slash ('/') separated XML name tokens (xsd:NMTOKEN) so it must
     * conform to the grammar production:<br>
     * applicationID ::= Nmtoken ( '/' Nmtoken )*<br>
     * while Nmtoken is defined in "Extensible Markup Language (XML) 1.0 (Third Edition) , W3C,
     * 2004, http://www.w3.org/TR/REC-xml" </li>
     * <li> is case sensitive </li>
     * <li>must be unique in the scope of a SOP instance </li>
     * </ul>
     * <p>
     * 
     * @return The <code>application ID</code>.
     */
    String getApplicationID();

    /**
     * This method returns an instance ID for an InternalSBB instance. An instanceID identifies one
     * running instance of a SOP connected piece of software identified by an application.
     * 
     * <p>
     * InstanceID is used inside InternalSBB to ensure correct delivery of responses to non-blocking
     * requests to scheduled requests. It is possible to start different processes with the same
     * instanceID but different tasks, for example, one for sending requests and one for receiving
     * responses.
     * </p>
     * <p>
     * It is possible to use <code>null</code> as the value for instance ID; this signifies that
     * instances are interchangeable.
     * </p>
     * <p>
     * An instanceID together with an applicationID identifies a participant instance.
     * </p>
     * <p>
     * An instanceID:
     * <ul>
     * <li> must be a sequence of slash ('/') separated XML name tokens (xsd:NMTOKEN) so it must
     * conform to the grammar production:<br>
     * instanceID ::= Nmtoken ( '/' Nmtoken )* <br>
     * while Nmtoken is defined in "Extensible Markup Language (XML) 1.0 (Third Edition) , W3C,
     * 2004, http://www.w3.org/TR/REC-xml" </li>
     * <li>is case sensitive</li>
     * <li>must be unique in the scope of an application</li>
     * </ul>
     * <p>
     * Recommendation: If running many processes of the same participant (with the same
     * applicationID) defining explicit instanceID values, instanceID values should build a
     * hierarchy based on the included slash as separator.
     * </p>
     * <p>
     * Usage: An instanceID is defined during development and deployment of a participant by an SOP
     * administrator.<br>
     * Within PAPI an instanceID can also be provided by the participant application to identify a
     * specific instance of this kind of participant to SOP.<br>
     * Within configuration, the instance specific configurations can be stored in the scope
     * identified by instanceID as subscope of an application scope.
     * </p>
     * 
     * @return The <code>instance ID</code> of this application instance.
     */
    String getInstanceID();
}
