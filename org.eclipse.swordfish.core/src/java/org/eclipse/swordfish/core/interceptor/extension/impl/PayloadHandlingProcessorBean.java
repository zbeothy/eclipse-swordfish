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
package org.eclipse.swordfish.core.interceptor.extension.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.helpers.UUIDGenerator;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.interceptor.extension.PayloadHandlingProcessor;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The Class PayloadHandlingProcessorBean.
 */
public class PayloadHandlingProcessorBean extends AbstractExtensionProcessor implements PayloadHandlingProcessor {

    /** The uuid generator. */
    private UUIDGenerator uuidGenerator;

    // TODO make this nicer :-/ currently we will trigger on
    // <Extension name="payloadHandling" value="byAttachment" /> otherwise this
    // processor does nothing
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(java.util.Collection)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        Iterator it = assertions.iterator();
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            String name = this.getAttribute(assertion, "name", null);
            String value = this.getAttribute(assertion, "value", null);
            if ("PayloadHandling".equalsIgnoreCase(name) && "attached".equalsIgnoreCase(value)) return true;
        }
        return false;
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
        return ContentAction.READWRITE;
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
        return new Class[] {StreamSource.class};
    }

    /**
     * Gets the uuid generator.
     * 
     * @return the uuid generator
     */
    public UUIDGenerator getUuidGenerator() {
        return this.uuidGenerator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {
        // TODO what should be done here.
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {

        if (this.canHandle(assertions)) {
            this.handle(context, role, Scope.REQUEST);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {

        if (this.canHandle(assertions)) {
            this.handle(context, role, Scope.RESPONSE);
        }

    }

    /**
     * Init.
     */
    public void init() {

    }

    /**
     * Sets the uuid generator.
     * 
     * @param uuidGenerator
     *        the new uuid generator
     */
    public void setUuidGenerator(final UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    /**
     * Handle.
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param scope
     *        the scope
     * 
     * @throws InternalSBBException
     */
    private void handle(final MessageExchange context, final Role role, final Scope scope) throws InternalSBBException {
        if ((role.equals(Role.SENDER) && scope.equals(Scope.REQUEST))
                || (role.equals(Role.RECEIVER) && scope.equals(Scope.RESPONSE))) {
            // outbound
            // As sender we need to put the message content into an Attachment

            // prepare the new content source
            String uuid = this.uuidGenerator.getUUID("msg");
            String uuidContent = "<attachment name=\"" + uuid + "\"/>";
            DOMSource contentPart = new DOMSource();
            try {
                contentPart.setNode(org.eclipse.swordfish.core.utils.XMLUtil.docFromString(uuidContent));
            } catch (UnsupportedEncodingException e) {
                throw new InternalMessagingException(e);
            } catch (SAXException e) {
                throw new InternalMessagingException(e);
            }
            // prepare the oldcontent to be attached to the nm
            NormalizedMessage nm = this.getCurrentNormalizedMessage(context, scope);
            InputStream attachmentPart = ((StreamSource) nm.getContent()).getInputStream();
            int lastAttachmentIndex = nm.getAttachmentNames().size();
            PayloadDataSource ds = new PayloadDataSource(attachmentPart, lastAttachmentIndex + 1);

            // change the content
            try {
                nm.setContent(contentPart);
                nm.addAttachment(uuid, new DataHandler(ds));
            } catch (MessagingException e) {
                throw new InternalMessagingException("problem while adding the payload to the attachment part", e);
            }

        } else {
            // inbound
            // As receiver we need to repleace the normaized message with an
            // attachment
            NormalizedMessage nm = this.getCurrentNormalizedMessage(context, scope);
            Document dom;
            try {
                dom = XMLUtil.docFromInputStream(((StreamSource) nm.getContent()).getInputStream());
            } catch (SAXException e) {
                throw new InternalMessagingException(e);
            } catch (IOException e) {
                throw new InternalInfrastructureException(e);
            }
            Element elem = dom.getDocumentElement();
            if (!"attachment".equalsIgnoreCase(elem.getLocalName()))
                throw new InternalMessagingException("cannot identity the payload attachment, no name available");
            String uuid = elem.getAttribute("name");

            if (uuid == null) throw new InternalMessagingException("cannot identity the payload attachment, found 'null' as name");

            DataHandler handler = nm.getAttachment(uuid);
            if (handler == null)
                throw new InternalMessagingException("no payload found for " + uuid + ", returned data handler is null");

            try {
                InputStream is = handler.getInputStream();
                StreamSource newContent = new StreamSource(is);
                nm.setContent(newContent);
                nm.removeAttachment(uuid);
            } catch (IOException e) {
                throw new InternalInfrastructureException("cannot assign the message payload from the attachment");
            } catch (MessagingException e) {
                throw new InternalMessagingException("cannot assign the message payload from the attachment");
            }

        }
    }

}
