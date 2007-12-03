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
package org.eclipse.swordfish.policytrader;

import java.io.InputStream;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.w3c.dom.Element;

/**
 * Factory for all policy-related objects.
 */
public interface PolicyFactory {

    /**
     * Creates a new Policy object.
     * 
     * @param agreedPolicyRootElement
     *        the agreed policy root element
     * 
     * @return the agreed policy
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    AgreedPolicy createAgreedPolicy(Element agreedPolicyRootElement) throws UnreadableSourceException, CorruptedSourceException;

    /**
     * Creates a new Policy object.
     * 
     * @param agreedPolicyData
     *        the agreed policy data
     * 
     * @return the agreed policy
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    AgreedPolicy createAgreedPolicy(InputStream agreedPolicyData) throws UnreadableSourceException, CorruptedSourceException;

    /**
     * Create an operation policy object from a DOM Element.
     * 
     * @param policyRootElement
     *        DOM Element
     * 
     * @return the policy object
     * 
     * @throws UnreadableSourceException
     *         if the stream cannot be read
     * @throws CorruptedSourceException
     *         if the stream does not contain a valid WS-Policy
     */
    OperationPolicy createOperationPolicy(Element policyRootElement) throws UnreadableSourceException, CorruptedSourceException;

    /**
     * Create an operation policy object from a stream of WS-Policy XML data.
     * 
     * @param policyData
     *        XML data
     * 
     * @return the policy object
     * 
     * @throws UnreadableSourceException
     *         if the stream cannot be read
     * @throws CorruptedSourceException
     *         if the stream does not contain a valid WS-Policy
     */
    OperationPolicy createOperationPolicy(InputStream policyData) throws UnreadableSourceException, CorruptedSourceException;

    /**
     * Create an operation policy identity object from an identifier String.
     * 
     * @param identifier
     *        identifier key as non-empty String
     * 
     * @return policy identity object
     */
    OperationPolicyIdentity createOperationPolicyIdentity(String identifier);

    /**
     * Create an operation policy identity object from an identifier String.
     * 
     * @param identifier
     *        identifier key as non-empty String
     * @param location
     *        an optional hint which may be used by the client when resolving the policy (may be
     *        null or empty)
     * 
     * @return policy identity object
     */
    OperationPolicyIdentity createOperationPolicyIdentity(String identifier, String location);

    /**
     * Create a participant policy object from a DOM Element.
     * 
     * @param policyRootElement
     *        DOM Element
     * 
     * @return the participant policy object
     * 
     * @throws UnreadableSourceException
     *         if the stream cannot be read
     * @throws CorruptedSourceException
     *         if the stream does not contain a valid participant policy
     */
    ParticipantPolicy createParticipantPolicy(Element policyRootElement) throws UnreadableSourceException, CorruptedSourceException;

    /**
     * Create a participant policy object from a stream of WS-Policy XML data.
     * 
     * @param policyData
     *        XML data
     * 
     * @return the participant policy object
     * 
     * @throws UnreadableSourceException
     *         if the stream cannot be read
     * @throws CorruptedSourceException
     *         if the stream does not contain a valid participant policy
     */
    ParticipantPolicy createParticipantPolicy(InputStream policyData) throws UnreadableSourceException, CorruptedSourceException;

    /**
     * Create a participant policy identity object from an identifier String.
     * 
     * @param identifier
     *        identifier key as non-empty String
     * 
     * @return policy identity object
     */
    ParticipantPolicyIdentity createParticipantPolicyIdentity(String identifier);

    /**
     * Create a participant policy identity object from an identifier String.
     * 
     * @param identifier
     *        identifier key as non-empty String
     * @param location
     *        an optional hint which may be used by the client when resolving the policy (may be
     *        null or empty)
     * 
     * @return policy identity object
     */
    ParticipantPolicyIdentity createParticipantPolicyIdentity(String identifier, String location);

    /**
     * Create a service descriptor object from a stream of service description XML data.
     * 
     * @param serviceDescriptionData
     *        XML data
     * 
     * @return the service descriptor object
     * 
     * @throws UnreadableSourceException
     *         if the stream cannot be read
     * @throws CorruptedSourceException
     *         if the stream does not contain a valid participant policy
     */
    ServiceDescriptor createServiceDescriptor(InputStream serviceDescriptionData) throws UnreadableSourceException,
            CorruptedSourceException;
}
