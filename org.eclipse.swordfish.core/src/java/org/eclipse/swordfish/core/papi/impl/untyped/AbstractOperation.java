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
package org.eclipse.swordfish.core.papi.impl.untyped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.papi.impl.authentication.GenericCallbackHandler;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.MessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.OperationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The implementation of an abstract part of operations. This is the super class for all
 * operationsskeltons and proxies
 */
public abstract class AbstractOperation extends SOPObjectBase implements InternalOperation {

    /** this is the most important field in this object, describing what this operation is about. */
    private OperationDescription myDescription;

    /** the hosting InternalSBB instance. */
    private SBBExtension sbb;

    /** the service this operation belongs to. */
    private AbstractService parent;

    /** the JBI message exchange Factory that is used to construct message Exchange objects. */
    private MessageExchangeFactory myMessageExchangeFactory;

    /**
     * the agreement applying for this service lookup as an own agreed policy The agreed policy of
     * an operation is narrowed down to the operation level, still containing all roles and scopes
     * that need to be sent piggyback over the network.
     */
    private AgreedPolicy myAgreedPolicy;

    /** indicate whether this operation is marked as supported in the agreed policy. */
    private boolean iAmSupported;

    /** The partner operation names. */
    private Set partnerOperationNames;

    /** The is callback operation. */
    private boolean isCallbackOperation;

    /** The supported headers. */
    private QName[] supportedHeaders;

    /**
     * The Constructor.
     * 
     * @param desc
     *        decsriptor for this operation
     * @param sbb
     *        the managing InternalSBB instance
     * @param parent
     *        the service this operation belongs to
     * @param partnersWith
     *        the service to which's operations this operation
     * @param resolvePolicy
     *        determin weather we should determin the agreed policy or not might add his name
     */
    public AbstractOperation(final OperationDescription desc, final SBBExtension sbb, final AbstractService parent,
            final AbstractService partnersWith, final boolean resolvePolicy) {

        if (desc == null) throw new ComponentRuntimeException("cannot construct an operation with null descriptor");
        if (sbb == null)
            throw new ComponentRuntimeException("cannot construct an operation with null as managing InternalSBB instance");
        if (parent == null) throw new ComponentRuntimeException("cannot construct an operation in context of a null service");

        this.supportedHeaders = new QName[0];
        this.myDescription = desc;
        this.sbb = sbb;
        this.parent = parent;
        this.partnerOperationNames = new HashSet();
        this.isCallbackOperation = (partnersWith != null);

        // Do the wiring to the partner service operation if this is a partner
        // operation
        if (this.isCallbackOperation()) {
            String operationNameThatWillPointAtThis = desc.getPartnerOperationName();
            // if the name is null than the cdx does not point to any partner
            // operation which must not be illegal!
            if (operationNameThatWillPointAtThis != null) {
                try {
                    AbstractOperation operationThatWillPointAtThis =
                            (AbstractOperation) partnersWith.getOperation(operationNameThatWillPointAtThis);
                    operationThatWillPointAtThis.addPartnerOperationName(this.getName());
                } catch (InternalSBBException e) {
                    throw new RuntimeException("Unexpected operation name mismatch", e);
                }
            }
        }

        if (resolvePolicy && !this.isCallbackOperation()) {
            /*
             * resolve the policy only if we are asked for, BUT it does not apply for for partner
             * services as the agreed policy is always coming in dynamically
             */
            AgreedPolicy ap = this.myDescription.getServiceDescription().getAgreedPolicy();
            if (null == ap)
                throw new ComponentRuntimeException("missing the agreed policy for service "
                        + this.myDescription.getServiceDescription().getServiceQName());
            this.iAmSupported = (null != ap.getOperationPolicy(this.getName()));
            if (this.iAmSupported) {
                this.myAgreedPolicy = ap.getReducedAgreedPolicy(this.getName());
            }
        } else {
            this.myAgreedPolicy = null;
            this.iAmSupported = true;
            // operations which do not require policy resolution
            // and callbacks must be considered supported,
            // otherwise request-callback is broken.
        }
    }

    /**
     * This method checks for not supported must understand headers and throws an exception if such
     * headers are detected.
     * 
     * @param msg
     *        the message which headers need to be examined
     * 
     * @throws ServiceInvocationException
     *         the exception that flies
     */
    public void checkUnsupportedMustUnderstandHeaders(final MessageBase msg) throws InternalMessagingException {
        List lst = this.filterUnsupportedMustUnderstandHeaders(msg.getHeaderMap());
        if (!lst.isEmpty()) throw new InternalMessagingException("unsupported SOAP mustUnderstand headers " + lst.toString());
    }

    /**
     * Cleanup.
     * 
     * @param sbbInitiated
     *        whether this clean up was initiated by InternalSBB or not
     */
    public void cleanup(final boolean sbbInitiated) {
        this.sbb = null;
        this.myMessageExchangeFactory = null;
        this.parent = null;
        this.myAgreedPolicy = null;
        this.myDescription = null;
        this.partnerOperationNames = null;
        this.sbb = null;
    }

    /**
     * This method returns a list of all headers that are must understand headers, but which are not
     * supported by this operation.
     * 
     * @param map
     *        a Map of document fragments that represent the headers
     * 
     * @return a list of qnames declaring the tags which have a mustUnderstand attribute, but which
     *         are not indicated to be supported by this operation.
     */
    public List filterUnsupportedMustUnderstandHeaders(final Map map) {
        List lst = new ArrayList();
        if (map == null) // no SOAP headers in place at all ...nothing to filter
            return lst;
        Iterator iter = map.keySet().iterator();
        // Go through all Element nodes and examine their attributes
        while (iter.hasNext()) {
            DocumentFragment frag = (DocumentFragment) map.get(iter.next());
            NodeList nl = frag.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node nd = nl.item(i);
                if (nd.getNodeType() == Node.ELEMENT_NODE) {
                    if (this.hasMustUnderstandAttribute((Element) nd)) {
                        QName qname = this.getNodeQName((Element) nd);
                        if (!this.isSupportedMustunderstandTag(qname)) {
                            lst.add(qname);
                        }
                    }
                }
            }
        }
        return lst;
    }

    /**
     * Gets the callback operation names.
     * 
     * @return a collection of Strings containing the names of all partner operation names that has
     *         been added to this operation using addPartnerOperationName
     */
    public Collection getCallbackOperationNames() {
        return this.partnerOperationNames;
    }

    /**
     * Gets the communication style.
     * 
     * @return the communication style
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOperation#getCommunicationStyle()
     */
    public abstract InternalCommunicationStyle getCommunicationStyle();

    /**
     * Gets the environment.
     * 
     * @return the environment
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOperation#getEnvironment()
     */
    public InternalEnvironment getEnvironment() {
        return this.parent.getEnvironment();
    }

    /**
     * Gets the name.
     * 
     * @return the name
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOperation#getName()
     */
    public String getName() {
        return this.myDescription.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SOPObjectBase#getParticipantIdentityAsString()
     */
    @Override
    public String getParticipantIdentityAsString() {
        return this.getKernel().getParticipant().toString();
    }

    /**
     * Gets the service.
     * 
     * @return the service
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOperation#getService()
     */
    public InternalService getService() {
        return this.parent;
    }

    public QName[] getSupportedHeaders() {
        return this.supportedHeaders;
    }

    /**
     * Checks for callback operations.
     * 
     * @return true if there are any partner operation names associated with this operation
     */
    public boolean hasCallbackOperations() {
        return !this.partnerOperationNames.isEmpty();
    }

    /**
     * Checks if is callback operation.
     * 
     * @return true if the service this operation belongs to is not the root of a partner
     *         relationship
     */
    public boolean isCallbackOperation() {
        return this.isCallbackOperation;
    }

    /**
     * Checks if is supported operation.
     * 
     * @return -- true if this operation is supported through the agreed policy
     */
    public boolean isSupportedOperation() {
        return this.iAmSupported;
    }

    public void setCallbackOperation(final boolean bIsCallbackOperation) {
        this.isCallbackOperation = bIsCallbackOperation;
    }

    public void setSupportedHeaders(final QName[] supportedHeaders) {
        this.supportedHeaders = supportedHeaders;
    }

    /**
     * Sets the supported must understand headers.
     * 
     * @param headers
     *        the new supported must understand headers
     */
    public void setSupportedMustUnderstandHeaders(final QName[] headers) {
        this.supportedHeaders = headers;
    }

    /**
     * adds a partner operation name to this operation.
     * 
     * @param aName
     *        name of the operation to add
     */
    protected void addPartnerOperationName(final String aName) {
        this.partnerOperationNames.add(aName);
    }

    /**
     * Cleanup callbacks.
     * 
     * @param exchange
     *        message exchange
     */
    protected void cleanupCallbacks(final MessageExchange exchange) {
        exchange.setProperty(ExchangeProperties.MESSAGE_AUTH_CALLBACKS, null);
        exchange.setProperty(ExchangeProperties.OPERATION_AUTH_CALLBACKS, null);
        exchange.setProperty(ExchangeProperties.SERVICE_AUTH_CALLBACKS, null);
        exchange.setProperty(ExchangeProperties.SBB_AUTH_CALLBACKS, null);
    }

    /**
     * Creates the correlation id.
     * 
     * @return -- an String that is unique to be used as corelation Id. This Id will travel around
     *         with request and responses
     */
    protected String createCorrelationId() {
        return this.getKernel().generateUUID();
    }

    /**
     * used by subclasses to create message exchanges as the units of communication.
     * 
     * @param ctxe
     *        the ctxe
     * @param msg
     *        the msg
     * 
     * @return -- a message exchange that supports the interaction style of this operation
     * 
     * @throws InternalMessagingException
     *         if no MessageExchangeFactory could be constructed to create message exchanges or if
     *         the MessageExchangeFactory throws an exception while MEP creation
     */
    protected MessageExchange createMessageExchange(final CallContextExtension ctxe, final OutgoingMessageBase msg)
            throws MessagingException {
        return this.createMessageExchange(ctxe, null, msg);
    }

    /**
     * This method creates a message exchange for this operation.
     * 
     * @param ctxe
     *        the ctxe
     * @param epr
     *        the epr
     * @param msg
     *        the msg
     * 
     * @return -- a message Exchange that is created to address the indicated endpoint by the
     *         document fragment
     * 
     * @throws InternalMessagingException
     *         if the message exchange could not be constructed
     */
    protected MessageExchange createMessageExchange(final CallContextExtension ctxe, final WSAEndpointReference epr,
            final OutgoingMessageBase msg) throws MessagingException {
        MessageExchangeFactory exchangeFactory = null;
        if (epr == null) {
            if (this.myMessageExchangeFactory == null) {
                this.myMessageExchangeFactory = this.getKernel().createMessageExchangeFactory(this.myDescription);
            }
            exchangeFactory = this.myMessageExchangeFactory;
        } else {
            exchangeFactory = this.getKernel().createMessageExchangeFactory(epr);
        }

        // partner operations policy is inside the Call context
        if (!this.isCallbackOperation() && !this.isSupportedOperation())
            throw new OperationException("cannot create exchange for operation " + this.getName()
                    + " as it is not a supported operation as indicated by the agreed policy");

        MessageExchange exchange = null;
        if (this.myDescription.getExchangePattern().equals(MessageExchangePattern.IN_OUT_URI)) {
            exchange = exchangeFactory.createInOutExchange();
        } else if (this.myDescription.getExchangePattern().equals(MessageExchangePattern.IN_ONLY_URI)) {
            exchange = exchangeFactory.createInOnlyExchange();
        } else if (this.myDescription.getExchangePattern().equals(MessageExchangePattern.OUT_ONLY_URI)) {
            exchange = exchangeFactory.createInOnlyExchange();
        }

        if (exchange == null)
            throw new MessagingException("missing mapping for exchange pattern " + this.myDescription.getExchangePattern());
        // populate the exchange
        QName wsdlServiceName = this.myDescription.getServiceDescription().getServiceQName();
        exchange.setService(wsdlServiceName);
        exchange.setInterfaceName(this.myDescription.getServiceDescription().getPortTypeQName());
        exchange.setOperation(new QName(wsdlServiceName.getNamespaceURI(), this.getName()));

        // TODO populate the InternalCallContext .. are these values correct?
        ctxe.setServiceName(exchange.getInterfaceName());
        ctxe.setProviderID(exchange.getService());
        ctxe.setMessageExchangeId(exchange.getExchangeId());
        ctxe.setUnifiedParticipantIdentity(this.getKernel().getParticipant());
        ctxe.setCommunicationStyle(this.getCommunicationStyle());
        ctxe.setMessageID(this.createMessageId());
        ctxe.setCorrelationID(this.createCorrelationId());
        ctxe.setOperationName(this.getName());
        ctxe.setReplyTo(this.getOperationDescription().getWSAEndpointReference("ReplyTo"));
        ctxe.setSOAPAction(this.getOperationDescription().getSoapAction());

        HeaderUtil.setCallContextExtension(exchange, ctxe);
        this.setAuthCallbacks(exchange, msg);

        return exchange;
    }

    /**
     * Creates the message id.
     * 
     * @return -- an String that is unique to be used as message Id. This Id will travel only one
     *         direction
     */
    protected String createMessageId() {
        return this.getKernel().generateUUID();
    }

    /**
     * Gets the agreed policy.
     * 
     * @return -- the agreed policy out of the negotiation of this operation retrival, narrowed down
     *         to the scope of this operation
     */
    protected AgreedPolicy getAgreedPolicy() {
        return this.myAgreedPolicy;
    }

    /**
     * Gets the kernel.
     * 
     * @return -- the actual kernel object to be used by subclasses
     */
    protected Kernel getKernel() {
        return this.sbb.getKernel();
    }

    /**
     * for subclass usage for registration of provider Handlers.
     * 
     * @return -- the description for this particular operation
     */
    protected OperationDescription getOperationDescription() {
        return this.myDescription;
    }

    /**
     * Sets the auth callbacks.
     * 
     * @param exchange
     *        message exchange
     * @param msg
     *        the msg
     */
    protected void setAuthCallbacks(final MessageExchange exchange, final OutgoingMessageBase msg) {

        if (msg != null) {
            InternalAuthenticationHandler[] messageCallbackCol = msg.getAuthenticationHandlers();
            if ((messageCallbackCol != null) && (messageCallbackCol.length > 0)) {
                exchange.setProperty(ExchangeProperties.MESSAGE_AUTH_CALLBACKS, new GenericCallbackHandler(messageCallbackCol));
            }
        }

        InternalAuthenticationHandler[] operationCallbackCol = this.getAuthenticationHandlers();
        if ((operationCallbackCol != null) && (operationCallbackCol.length > 0)) {
            exchange.setProperty(ExchangeProperties.OPERATION_AUTH_CALLBACKS, new GenericCallbackHandler(operationCallbackCol));
        }

        InternalAuthenticationHandler[] serviceCallbackCol = this.parent.getAuthenticationHandlers();
        if ((serviceCallbackCol != null) && (serviceCallbackCol.length > 0)) {
            exchange.setProperty(ExchangeProperties.SERVICE_AUTH_CALLBACKS, new GenericCallbackHandler(serviceCallbackCol));
        }

        InternalAuthenticationHandler[] sbbCallbackCol = this.parent.getSBB().getAuthenticationHandlers();
        if ((sbbCallbackCol != null) && (sbbCallbackCol.length > 0)) {
            exchange.setProperty(ExchangeProperties.SBB_AUTH_CALLBACKS, new GenericCallbackHandler(sbbCallbackCol));
        }

    }

    /**
     * Gets the node Q name.
     * 
     * @param element
     *        the element
     * 
     * @return the node Q name
     */
    private QName getNodeQName(final Element element) {
        String local = element.getLocalName();
        String ns = element.getNamespaceURI();
        QName qname = new QName(ns, local);
        return qname;
    }

    /**
     * Checks for must understand attribute.
     * 
     * @param element
     *        the element
     * 
     * @return true, if successful
     */
    private boolean hasMustUnderstandAttribute(final Element element) {
        // add namespaces that need to be checked for future versions
        // to this array;
        String[] nameSpaces = new String[] {"http://schemas.xmlsoap.org/soap/envelope/"};

        for (int i = 0; i < nameSpaces.length; i++) {
            String trimedAttributeValue = element.getAttributeNS(nameSpaces[i], "mustUnderstand").trim();
            if ("1".equals(trimedAttributeValue)) return true;
        }
        return false;
    }

    /**
     * Checks if is supported mustunderstand tag.
     * 
     * @param qname
     *        the qname
     * 
     * @return true, if is supported mustunderstand tag
     */
    private boolean isSupportedMustunderstandTag(final QName qname) {
        for (int i = 0; i < this.supportedHeaders.length; i++) {
            if (this.supportedHeaders[i].equals(qname)) return true;
        }
        return false;
    }
}
