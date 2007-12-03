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
package org.eclipse.swordfish.core.interceptor.correlation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.correlation.CorrelationProcessor;
import org.eclipse.swordfish.core.management.messages.CorrelationMessage;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * The Class CorrelationProcessorBean.
 */
public class CorrelationProcessorBean extends AbstractProcessingComponent implements CorrelationProcessor {

    /** The operations. */
    private Operations operations;

    /**
     * Instantiates a new correlation processor bean.
     */
    public CorrelationProcessorBean() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(java.util.Collection)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        return true;
    }

    /**
     * Gets the content action.
     * 
     * @return the content action
     * 
     * @see org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent#getContentAction()
     */
    @Override
    public ContentAction getContentAction() {
        return ContentAction.READ;
    }

    /**
     * Gets the supported sources.
     * 
     * @return the supported sources
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#getSupportedSources()
     */
    @Override
    public Class[] getSupportedSources() {
        return new Class[] {DOMSource.class};
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
        this.handleMessage(context, assertions, nmMessage, role, Scope.REQUEST);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
        this.handleMessage(context, assertions, nmMessage, role, Scope.RESPONSE);
    }

    /**
     * Sets the operations.
     * 
     * @param ops
     *        the new operations
     */
    public void setOperations(final Operations ops) {
        this.operations = ops;
    }

    /**
     * Adds a SOAP header to the message exchange which exposes the businessID.
     * 
     * @param message
     *        the message
     * @param correlationName
     *        the correlation name
     * @param finalId
     *        the final id
     */
    private void addheader(final NormalizedMessage message, final String correlationName, final String finalId) {
        Map headers = (Map) message.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (null == headers) {
            headers = new HashMap();
        }
        DocumentFragment headerFragment = (DocumentFragment) headers.get(Constants.QNAME_STRING);
        if (null == headerFragment) {
            headerFragment = this.createHeaderFragment();
        }
        Document doc = headerFragment.getOwnerDocument();
        Element correlation = doc.createElementNS(Constants.NS_URI, "Correlation");
        Element root = (Element) headerFragment.getFirstChild();
        root.appendChild(correlation);
        correlation.setAttribute("name", correlationName);
        correlation.setAttribute("value", finalId);
        headers.put(Constants.QNAME_STRING, headerFragment);
        message.setProperty(HeaderUtil.HEADER_PROPERTY, headers);
    }

    /**
     * Creates the header fragment.
     * 
     * @return the document fragment
     */
    private DocumentFragment createHeaderFragment() {
        StringBuffer headerStr =
                new StringBuffer("<").append(Constants.HEADER_QNAME).append(" xmlns:cor=\"").append(Constants.NS_URI).append(
                        "\" xmlns:soap=\"").append(HeaderUtil.SOAP_NS).append("\" xmlns=\"").append(Constants.NS_URI).append(
                        "\" soap:mustUnderstand=\"0\" ").append("/>");
        Document header = TransformerUtil.docFromString(headerStr.toString());
        DocumentFragment headerFragment = header.createDocumentFragment();
        headerFragment.appendChild(header.getDocumentElement());
        return headerFragment;
    }

    /**
     * Evaluate.
     * 
     * @param context
     *        the context
     * @param xpathString
     *        the xpath string
     * 
     * @return the string
     */
    private String evaluate(final JXPathContext context, final String xpathString) {
        String res = null;
        try {
            Object val = context.getValue(xpathString);
            if (null != val) {
                res = val.toString();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return res;
    }

    /**
     * Handle message.
     * 
     * @param exchange
     *        the exchange
     * @param assertions
     *        the assertions
     * @param nmMessage
     *        the nm message
     * @param role
     *        the role
     * @param scope
     *        the scope
     */
    private void handleMessage(final MessageExchange exchange, final Collection/* <Assertion> */assertions,
            final NormalizedMessage nmMessage, final Role role, final Scope scope) {
        Source source = nmMessage.getContent();
        JXPathContext messageContext = null;
        Document doc = TransformerUtil.docFromSource(source);
        messageContext = JXPathContext.newContext(doc);
        CallContextExtension ctx = HeaderUtil.getCallContextExtension(exchange);
        Iterator it = assertions.iterator();
        while (it.hasNext()) {
            PrimitiveAssertion correlation = (PrimitiveAssertion) it.next();
            String message = this.getAttribute(correlation, "message", "request");
            String location = this.getAttribute(correlation, "location", "sender");
            if ((message.equals(scope.toString())) && (location.equals(role.toString()))) {
                this.processCorrelation(correlation, messageContext, ctx, nmMessage);
            }
        }
    }

    /**
     * processes one correlation statement.
     * 
     * @param correlation
     *        the correlation statement specifying the business ID
     * @param message
     *        the message
     * @param messageContext
     *        the message context
     * @param ctx
     *        the ctx
     */
    private void processCorrelation(final PrimitiveAssertion correlation, final JXPathContext messageContext,
            final CallContextExtension ctx, final NormalizedMessage message) {
        String correlationName = this.getAttribute(correlation, "name", null);
        boolean expose = new Boolean(this.getAttribute(correlation, "expose", "false")).booleanValue();
        StringBuffer businessID = new StringBuffer();
        String separator = "";
        Iterator it = correlation.getTerms().iterator();
        while (it.hasNext()) {
            PrimitiveAssertion term = (PrimitiveAssertion) it.next();
            if ("Namespace".equals(term.getName().getLocalPart())) {
                String uri = this.getAttribute(term, "uri", null);
                String prefix = this.getAttribute(term, "prefix", null);
                if ((null != uri) && (null != prefix)) {
                    messageContext.registerNamespace(prefix, uri);
                }
            }
            if ("Part".equals(term.getName().getLocalPart())) {
                String partName = this.getAttribute(term, "name", null);
                String xpathString = this.getAttribute(term, "xpath", null);
                if ((null != partName) && (null != xpathString)) {
                    String val = this.evaluate(messageContext, xpathString);
                    businessID.append(separator).append(partName).append("=").append(val);
                    separator = ",";
                }
            }
        }
        String finalId = businessID.toString();
        Object[] params = new Object[] {correlationName, ctx.getMessageID(), ctx.getCorrelationID(), finalId};
        this.operations.notify(CorrelationMessage.CORRELATION, params);
        if (expose) {
            this.addheader(message, correlationName, finalId);
        }
    }

}
