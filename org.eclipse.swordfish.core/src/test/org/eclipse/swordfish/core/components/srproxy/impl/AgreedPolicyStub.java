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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.io.OutputStream;
import java.util.Date;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.w3c.dom.Document;

/**
 * The Class AgreedPolicyStub.
 */
public class AgreedPolicyStub implements AgreedPolicy {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getConsumerPolicyIdentity()
     */
    public ParticipantPolicyIdentity getConsumerPolicyIdentity() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getDefaultOperationPolicy()
     */
    public Policy getDefaultOperationPolicy() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getOperationPolicy(java.lang.String)
     */
    public Policy getOperationPolicy(final String s) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getProvider()
     */
    public String getProvider() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getProviderPolicyIdentity()
     */
    public ParticipantPolicyIdentity getProviderPolicyIdentity() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getReducedAgreedPolicy(java.lang.String)
     */
    public AgreedPolicy getReducedAgreedPolicy(final String s) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#getService()
     */
    public String getService() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#setProvider(java.lang.String)
     */
    public void setProvider(final String s) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#setValid(java.util.Date, java.util.Date)
     */
    public void setValid(final Date date, final Date date1) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#validSince()
     */
    public Date validSince() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#validThrough()
     */
    public Date validThrough() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeTo(org.w3c.dom.Document)
     */
    public void writeTo(final Document document) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeTo(java.io.OutputStream)
     */
    public void writeTo(final OutputStream outputstream) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.AgreedPolicy#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    public void writeTo(final XMLStreamWriter xmlstreamwriter) throws XMLStreamException {

    }

}
