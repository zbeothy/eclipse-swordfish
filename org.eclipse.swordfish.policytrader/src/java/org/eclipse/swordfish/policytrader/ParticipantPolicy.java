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

import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.impl.XMLStreamToDOMWriter;
import org.w3c.dom.Document;

/**
 * Participant policy object mapping policy identities to their respective service operations.
 */
public interface ParticipantPolicy {

    /** PolicyIdentity for "unused" operations, i.e. operations not supported by a ParticipantPolicy. */
    OperationPolicyIdentity VOID_POLICY_ID = C.createVoidPolicyId();

    /** Empty WS Policy object for the trading result of "unused operation". */
    Policy EMPTY_POLICY = C.createEmptyPolicy();

    /** Void agreed policy for failed agreements. */
    AgreedPolicy FAILED_AGREEMENT_POLICY = C.createFailedAgreementPolicy();

    /** Key name for "unused" operations. */
    String UNUSED_OPERATION = "@unused@";

    /**
     * Key for the default policy identity. It may be passed as operation name to get the default
     * policy identity.
     */
    String ANY_OPERATION = "@AnyOperation@";

    /**
     * Get the default policy identifier. If the operation is marked as "unused", then
     * {@link VOID_POLICY_ID} is returned.
     * 
     * @return default policy identifier or <code>null</code> if none is defined
     */
    OperationPolicyIdentity getDefaultPolicyIdentity();

    /**
     * Gets the id.
     * 
     * @return the id
     */
    String getId();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Get the policy identifier defined for an opertion. If the operation name is unknown or no
     * policy identity is defined for the operation, <code>null</code> is returned. If the
     * operation is marked as "unused", then {@link VOID_POLICY_ID} is returned.
     * 
     * @param operationName
     *        operation name, a non-empty String
     * 
     * @return the policy identifier or <code>null</code> if none is defined
     */
    OperationPolicyIdentity getPolicyIdentityForOperation(String operationName);

    /**
     * Get the policy identifier defined for an opertion. If the operation name is unknown or no
     * policy identity is defined for the operation, the default policy identifier is returned (or
     * <code>null</code> if none is defined). If the operation is marked as "unused", then
     * {@link VOID_POLICY_ID} is returned.
     * 
     * @param operationName
     *        operation name, a non-empty String
     * 
     * @return the policy identifier, if none is defined, the default policy identifier
     */
    OperationPolicyIdentity getPolicyIdentityForOperationOrDefault(String operationName);

    /**
     * Get a Map of the operation policy identifiers mapped to the operation names. The default
     * policy identity is mapped to {@link ANY_OPERATION}.
     * 
     * @return an umodifiable Map
     */
    Map getPolicyIdentityMap();

    /**
     * Gets the provider.
     * 
     * @return the provider
     */
    String getProvider();

    /**
     * Gets the role.
     * 
     * @return the role
     */
    String getRole();

    /**
     * Gets the service.
     * 
     * @return the service
     */
    String getService();

    /**
     * Gets the service location.
     * 
     * @return the service location
     */
    String getServiceLocation();

    /**
     * Gets the version.
     * 
     * @return the version
     */
    String getVersion();

    /**
     * Internal creator class for constant objects.
     */
    final class C {

        /** The N o_ ID. */
        private static ParticipantPolicyIdentity noId = new ParticipantPolicyIdentity() {

            public String getKeyName() {
                return "@invalid@";
            }

            public String getLocation() {
                return null;
            }

            public void setLocation(String location) {
                // do nothing
            }
        };

        /**
         * Create the object for the EMPTY_POLICY constant.
         * 
         * @return an empty policy
         */
        private static Policy createEmptyPolicy() {
            Policy p = new Policy();
            p.addTerm(new ExactlyOne());
            return (Policy) p.normalize();
        }

        /**
         * Creates the failed agreement policy.
         * 
         * @return the agreed policy
         */
        private static AgreedPolicy createFailedAgreementPolicy() {
            return new AgreedPolicy() {

                public ParticipantPolicyIdentity getConsumerPolicyIdentity() {
                    return noId;
                }

                public Policy getDefaultOperationPolicy() {
                    return null;
                }

                public Policy getOperationPolicy(final String operation) {
                    return null;
                }

                /*
                 * public void writeClassicTo(final OutputStream output) { XMLStreamWriter writer =
                 * null; try { writer = XMLOutputFactory.newInstance()
                 * .createXMLStreamWriter(output); writeClassicTo(writer); writer.flush(); } catch
                 * (XMLStreamException ex) { throw new RuntimeException(ex); } }
                 * 
                 * public void writeClassicTo(final XMLStreamWriter writer) throws
                 * XMLStreamException { // writer.writeStartDocument(); writer.writeStartElement("",
                 * AGREED_POLICY_TAG, AGREED_POLICY_CLASSIC_NAMESPACE); writer
                 * .writeDefaultNamespace(AGREED_POLICY_CLASSIC_NAMESPACE);
                 * writer.setDefaultNamespace(AGREED_POLICY_CLASSIC_NAMESPACE);
                 * writer.writeEndElement(); // writer.writeEndDocument(); }
                 * 
                 * public void writeClassicTo(final Document document) { final XMLStreamToDOMWriter
                 * writer = new XMLStreamToDOMWriter(document); try { writeClassicTo(writer);
                 * writer.flush(); } catch (XMLStreamException ex) { throw new RuntimeException(ex); } }
                 */

                public String getProvider() {
                    return "@noProvider@";
                }

                public ParticipantPolicyIdentity getProviderPolicyIdentity() {
                    return noId;
                }

                public AgreedPolicy getReducedAgreedPolicy(final String operation) {
                    return this;
                }

                public String getService() {
                    return "@noService@";
                }

                public void setProvider(final String value) {
                    throw new IllegalStateException("Can't set ServiceProvider for failed agreed policy");
                }

                public void setValid(final Date since, final Date through) {
                    // do nothing
                }

                public Date validSince() {
                    return new Date(Long.MIN_VALUE);
                }

                public Date validThrough() {
                    return new Date(Long.MAX_VALUE);
                }

                public void writeTo(final Document document) {
                    final XMLStreamToDOMWriter writer = new XMLStreamToDOMWriter(document);
                    try {
                        writeTo(writer);
                        writer.flush();
                    } catch (XMLStreamException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                public void writeTo(final OutputStream output) {
                    XMLStreamWriter writer = null;
                    try {
                        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
                        writeTo(writer);
                        writer.flush();
                    } catch (XMLStreamException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
                    // writer.writeStartDocument();
                    writer.writeStartElement("", AGREED_POLICY_TAG, AGREED_POLICY_NAMESPACE);
                    writer.writeDefaultNamespace(AGREED_POLICY_NAMESPACE);
                    writer.setDefaultNamespace(AGREED_POLICY_NAMESPACE);
                    writer.writeEndElement();
                    // writer.writeEndDocument();
                }
            };
        }

        /**
         * Create the OperationPolicyIdentity constant for "unused" operations.
         * 
         * @return the identity object
         */
        private static OperationPolicyIdentity createVoidPolicyId() {
            return new OperationPolicyIdentity() {

                public String getKeyName() {
                    return UNUSED_OPERATION;
                }

                public String getLocation() {
                    return null;
                }

                public void setLocation(final String location) {
                    // do nothing
                }
            };
        }

        /**
         * Hidden constructor.
         */
        private C() {
            super();
        }
    }
}
